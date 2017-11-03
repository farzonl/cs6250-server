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

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;


public class UDPNettyServer extends AbstractNettyNetworkServer {

    /** The default counts (invalid one) for server and worker pool counts*/
    private static final int INVALID_POOL_SIZE = -1;

    /** The server and worker thread pool sizes*/
    private int workerPoolSize = INVALID_POOL_SIZE;
    private int executorQueueSize = Runtime.getRuntime().availableProcessors() * 12;

    /** The server and worker ExecutorService instances*/
  private ExecutorService workerExecutors;
private UDPResponder responder;

    /** Server Type */
    private String serverType = "UDP Netty Server";


    public TransmissionProtocol getTransmissionProtocol() {
        return TRANSMISSION_PROTOCOL.UDP;
    }
InetSocketAddress socketAddress = new InetSocketAddress(portNumber);

    public void afterPropertiesSet() throws Exception {
        
        if (this.getWorkerExecutors() == null) {  // no executors have been set for workers
            if (this.getWorkerPoolSize() != UDPNettyServer.INVALID_POOL_SIZE) { // thread pool size has not been set.
                this.setWorkerExecutors(new ThreadPoolExecutor(this.getWorkerPoolSize(),
                        this.getWorkerPoolSize(),
                        60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(this.getExecutorQueueSize()),
                        new NamedThreadFactory("UDPServer-Worker"),
                        new ThreadPoolExecutor.CallerRunsPolicy()));
            } else { // default behavior of creating and using a cached thread pool
                this.setWorkerExecutors(Executors.newCachedThreadPool(new NamedThreadFactory("UDPServer-Worker")));
            }
        }
        super.afterPropertiesSet();
    }
    
    
    public String toString(){
        return "UDPNettyServer [socketAddress=" + socketAddress + ", portNumber=" + portNumber + "] " + this.getPipelineFactory();
    }

    protected Bootstrap createServerBootstrap() throws RuntimeException {
    	if (this.getWorkerPoolSize() != UDPNettyServer.INVALID_POOL_SIZE) { // specify the worker count if it has been set, else use defaults (Netty uses 2 * no. of cores)
    		return new ServerBootstrap(new NioDatagramChannelFactory(this.getWorkerExecutors(), this.getWorkerPoolSize()));    		
    	} else {
   		return new ServerBootstrap(new NioDatagramChannelFactory(this.getWorkerExecutors()));
    	}
    }

    
    protected Channel createChannel() throws RuntimeException {
        if (this.getServerBootstrap() == null) {
            throw new RuntimeException("Error creating Channel. Bootstrap instance cannot be null. See TCPNettyServer#createServerBootstrap()");
        }
        return ((ServerBootstrap)this.serverBootstrap).bind(this.socketAddress);
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
    public String getServerEndpoint() {
        return ""+this.portNumber;
    }

    /** Start Getter/Setter methods */
    
    public int getWorkerPoolSize() {
        return this.workerPoolSize;
    }
    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }
    
    public ExecutorService getWorkerExecutors() {
        return this.workerExecutors;
    }
    public void setWorkerExecutors(ExecutorService workerExecutors) {
        this.workerExecutors = workerExecutors;
    }
    public int getExecutorQueueSize() {
        return executorQueueSize;
    }
    public void setExecutorQueueSize(int executorQueueSize) {
        this.executorQueueSize = executorQueueSize;
    }
    /** End Getter/Setter methods */
}