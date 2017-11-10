package server;

import java.net.InetSocketAddress;
import java.util.List;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

@Sharable
public class DatagramPacketDecoder extends MessageToMessageDecoder<DatagramPacket> implements ChannelUpstreamHandler{

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        // decode the DatagramPackage to AddressedEnvelope
        DefaultAddressedEnvelope<Object, InetSocketAddress> addressEvelop = 
            new DefaultAddressedEnvelope<Object, InetSocketAddress>(msg.content().retain(), msg.recipient(), msg.sender());
        out.add(addressEvelop);
        
    }

	@Override
	public void handleUpstream(org.jboss.netty.channel.ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
