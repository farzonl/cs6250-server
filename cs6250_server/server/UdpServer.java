package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

public class UdpServer {

    int port=20001;
    private Channel serverChannel;

    public UdpServer( int port ) {
        super();
        this.port = port;
    }

    public int getport() {
    	return this.port;
    }
    public void start() {
        NioDatagramChannelFactory serverChannelFactory =
            new NioDatagramChannelFactory( Executors.newCachedThreadPool(), 1 );
        ConnectionlessBootstrap serverBootstrap =
            new ConnectionlessBootstrap( serverChannelFactory );
        serverBootstrap.setPipelineFactory( new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
                return Channels.pipeline( new MyChannelHandler() {
                    @Override
                    public void messageReceived( ChannelHandlerContext ctx,
                        MessageEvent e ) {
                        // TODO, handle message from client
                    	
                    }
                } );
            }
        } );
        serverBootstrap.setOption( "reusePort", Boolean.TRUE );
        final InetSocketAddress trafficAddress = new InetSocketAddress( port );
        serverChannel = serverBootstrap.bind( trafficAddress );
    }

    
    public void sendMessage( byte[] message, String clientIp )
        throws UnknownHostException {
        // TODO, how do I control the source port of this packet??
        SocketAddress address =
            new InetSocketAddress( InetAddress.getByName( clientIp ), port );
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer( message );
        serverChannel.write( buffer, address );
    }

}
