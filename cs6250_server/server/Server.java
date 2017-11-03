package server;
//import SendFileServer.SendFile;

import java.io.IOException;
import java.net.*;
import com.flipkart.phantom.runtime.impl.server.netty.*;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.DatagramServer;

import org.apache.avro.ipc.specific.SpecificResponder;

public class Server {
	//private static NettyServer server;
	//private static DatagramServer server;
	private static UDPNettyServer server;
	final static int port = 20001;
	
	public void InitServer() throws Exception {
		InetSocketAddress socketAddr = new InetSocketAddress(port);
		while (true) {
			//server = new NettyServer(new SpecificResponder(IBenchProtocol.class,
			//		new BenchProtocolImpl()), socketAddr);
			//server = new DatagramServer(new SpecificResponder(IBenchProtocol.class,
		    //		new BenchProtocolImpl()), socketAddr);
			server = new UDPNettyServer();
			server.socketAddress = socketAddr;
			
			try {
				//server.getPort();
				server.getPortNumber();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			break;
		}
		
		System.err.println("Server is ready to accept connections");
	}
	
	public void StartServer() throws Exception {
		//server.startServer(port);
		//server.start();
		server.createServerBootstrap();
		server.createChannel();
		System.out.println(server.toString());
		//server.join();
	}
	
	public static void main(String[] args) throws Exception {
		System.loadLibrary("opencv_java330");
		System.out.println("Cloud Server");
		
		Server myServer = new Server();
		try {
			myServer.InitServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
<<<<<<< Updated upstream

=======
		
		myServer.StartServer();
		//myServer.run();
>>>>>>> Stashed changes
	}

}
