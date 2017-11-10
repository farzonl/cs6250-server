/*
 * Copyright 2012-2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import com.flipkart.phantom.runtime.impl.server.concurrent.NamedThreadFactory;
import com.flipkart.phantom.runtime.impl.server.netty.AbstractNettyNetworkServer;
import com.flipkart.phantom.runtime.spi.server.NetworkServer.TRANSMISSION_PROTOCOL;
import com.flipkart.phantom.runtime.spi.server.NetworkServer.TransmissionProtocol;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;


public class UDPNettyServer {

    
	private static final int portNumber = 20001;
private UDPResponder responder;

    /** Server Type */
    private String serverType = "UDP Netty Server";
ConnectionlessBootstrap b;

    public TransmissionProtocol getTransmissionProtocol() {
        return TRANSMISSION_PROTOCOL.UDP;
    }
InetSocketAddress socketAddress = new InetSocketAddress(portNumber);

    
    
    public String toString(){
        return "UDPNettyServer [socketAddress=" + socketAddress + ", portNumber=" + portNumber + "] " ;
    }

    protected void createServerBootstrap() throws RuntimeException {
    	
    		b = new ConnectionlessBootstrap(new NioDatagramChannelFactory());    		
    }

    
    protected Channel createChannel() throws RuntimeException {
        /*if (this.getServerBootstrap() == null) {
            throw new RuntimeException("Error creating Channel. Bootstrap instance cannot be null. See TCPNettyServer#createServerBootstrap()");
        }*/
        return (b.bind(socketAddress));
    }

    protected void createchpipeline() {
    	b.setPipelineFactory((ChannelPipelineFactory) new NioDatagramChannelFactory());
    }
    public String getServerType() {
        return  this.serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    /**
     * Abstract method implementation. Returns server endpoint as string.
     */
    public int getPortNumber() {
        return portNumber;
    }

}
