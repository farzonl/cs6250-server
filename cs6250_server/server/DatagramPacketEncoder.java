package server;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import org.jboss.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class DatagramPacketEncoder extends MessageToMessageEncoder<AddressedEnvelope<Object, InetSocketAddress>> implements ChannelUpstreamHandler{

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<Object, InetSocketAddress> msg, List<Object> out) throws Exception {
        if (msg.content() instanceof ByteBuf) {
            ByteBuf payload = (ByteBuf)msg.content();
            // Just wrap the message as DatagramPacket, need to make sure the message content is ByteBuf
            DatagramPacket dp = new DatagramPacket(payload.retain(), msg.recipient());
            out.add(dp);
        }
    }

	@Override
	public void handleUpstream(org.jboss.netty.channel.ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
