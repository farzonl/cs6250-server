package server;

import org.gradle.wrapper.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import sun.rmi.runtime.Log;

public class MyChannelHandler extends SimpleChannelUpstreamHandler {

	 private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class
		      .getName());
	 
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        // Log all channel state changes.
        if (e instanceof ChannelStateEvent) {
            LOG.info("Channel state changed: " + e);
        }
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        // Log all channel state changes.
        if (e instanceof MessageEvent) {
            LOG.info("Writing:: " + e);
        }
    }
    private NettyTransceiver connectionMetadata = new NettyTransceiver();


    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
        throws Exception {
      allChannels.add(e.getChannel());
      super.channelOpen(ctx, e);
    }

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
