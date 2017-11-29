package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.avro.ipc.NettyTransportCodec.*;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServerNetty implements NettyServer {
	  private static final Logger LOG = LoggerFactory.getLogger(UDPServerNetty.class
	      .getName());

	  private final UDPResponder responder;

	  private final Channel serverChannel;
	  private final ChannelGroup allChannels = new DefaultChannelGroup(
	      "avro-netty-server");
	  private final ChannelFactory channelFactory;
	  private final CountDownLatch closed = new CountDownLatch(1);
	  private final ExecutionHandler executionHandler;

	public InetSocketAddress socketAddress;

	  public UDPServerNetty(UDPResponder responder, InetSocketAddress addr) {
	    this(responder, addr, new NioDatagramChannelFactory
	         (Executors .newCachedThreadPool()));
	  }

	  public UDPServerNetty(UDPResponder responder, InetSocketAddress addr,
	                     ChannelFactory channelFactory) {
	      this(responder, addr, channelFactory, null);
	  }

	  /**
	   * @param executionHandler if not null, will be inserted into the Netty
	   *                         pipeline. Use this when your responder does
	   *                         long, non-cpu bound processing (see Netty's
	   *                         ExecutionHandler javadoc).
	   * @param pipelineFactory  Avro-related handlers will be added on top of
	   *                         what this factory creates
	   */
	  public UDPServerNetty(UDPResponder responder, InetSocketAddress addr,
	                     ChannelFactory channelFactory,
	                     final ChannelPipelineFactory pipelineFactory,
	                     final ExecutionHandler executionHandler) {
	    this.responder = responder;
	    this.channelFactory = channelFactory;
	    this.executionHandler = executionHandler;
	    ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(channelFactory);
	    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	      @Override
	      public ChannelPipeline getPipeline() throws Exception {
	        ChannelPipeline p = pipelineFactory.getPipeline();
	        p.addLast("frameDecoder", new DatagramPacketDecoder());
	        //p.addLast("frameDecoder", new NettyFrameDecoder());
	        p.addLast("frameEncoder", new DatagramPacketEncoder());
	        //p.addLast("frameEncoder", new NettyFrameEncoder());
	        if (executionHandler != null) {
	          p.addLast("executionHandler", executionHandler);
	        }
	        p.addLast("handler", new NettyServerAvroHandler());
	        return p;
	      }
	    });
	    serverChannel = bootstrap.bind(addr);
	    allChannels.add(serverChannel);
	  }

	  /**
	   * @param executionHandler if not null, will be inserted into the Netty
	   *                         pipeline. Use this when your responder does
	   *                         long, non-cpu bound processing (see Netty's
	   *                         ExecutionHandler javadoc).
	   */
	  public UDPServerNetty(UDPResponder responder, InetSocketAddress addr,
	                     ChannelFactory channelFactory,
	                     final ExecutionHandler executionHandler) {
	    this(responder, addr, channelFactory, new ChannelPipelineFactory() {
	      @Override
	      public ChannelPipeline getPipeline() throws Exception {
	        return Channels.pipeline();
	      }
	    }, executionHandler);
	  }

	  @Override
	  public void start() {
	    // No-op.
	  }

	  @Override
	  public void close() {
	    ChannelGroupFuture future = allChannels.close();
	    future.awaitUninterruptibly();
	    channelFactory.releaseExternalResources();
	    closed.countDown();
	  }

	  @Override
	  public int getPort() {
	    return ((InetSocketAddress) serverChannel.getLocalAddress()).getPort();
	  }

	  @Override
	  public void join() throws InterruptedException {
	    closed.await();
	  }

	  /**
	   *
	   * @return The number of clients currently connected to this server.
	   */
	  public int getNumActiveConnections() {
	    //allChannels also contains the server channel, so exclude that from the
	    //count.
	    return allChannels.size() - 1;
	  }

	  /**
	   * Avro server handler for the Netty transport
	   */
	  class NettyServerAvroHandler extends SimpleChannelUpstreamHandler {

	    private NettyTransceiver connectionMetadata = new NettyTransceiver();

	    @Override
	    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
	        throws Exception {
	      if (e instanceof ChannelStateEvent) {
	        LOG.info(e.toString());
	      }
	      super.handleUpstream(ctx, e);
	    }

	    @Override
	    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
	        throws Exception {
	      allChannels.add(e.getChannel());
	      super.channelOpen(ctx, e);
	    }

	    @Override
	    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	      try {
	        NettyDataPack dataPack = (NettyDataPack) e.getMessage();
	        List<ByteBuffer> req = dataPack.getDatas();
	        List<ByteBuffer> res = responder.respond(req, connectionMetadata);
	        // response will be null for oneway messages.
	        if(res != null) {
	          dataPack.setDatas(res);
	          e.getChannel().write(dataPack);
	        }
	      } catch (IOException ex) {
	        LOG.warn("unexpect error");
	      }
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	      LOG.warn("Unexpected exception from downstream.", e.getCause());
	      e.getChannel().close();
	      allChannels.remove(e.getChannel());
	    }

	    @Override
	    public void channelClosed(
	            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	      LOG.info("Connection to {} disconnected.",
	              e.getChannel().getRemoteAddress());
	      super.channelClosed(ctx, e);
	      e.getChannel().close();
	      allChannels.remove(e.getChannel());
	    }

	  }
	}