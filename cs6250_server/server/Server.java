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
	//private static UDPNettyServer server;
	//private static  UdpServer server;
	private static UDPServerNetty server;
	final static int port = 20001;
	UDPResponder res;
	public void InitServer() throws Exception {
		InetSocketAddress socketAddr = new InetSocketAddress(port);
		while (true) {
			//server = new NettyServer(new SpecificResponder(IBenchProtocol.class,
			//		new BenchProtocolImpl()), socketAddr);
			//server = new DatagramServer(new SpecificResponder(IBenchProtocol.class,
		    //		new BenchProtocolImpl()), socketAddr);
			//server = new UDPNettyServer();
			server = new UDPServerNetty(res, socketAddr);
			//server.socketAddress = socketAddr;
			
			//server = new UdpServer(port);
			
			try {
				//server.getPort();
				server.getPort();
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
		//server.afterPropertiesSet();
		//server.createServerBootstrap();
	//	server.createChannel();
		server.start();
		System.out.println(server.toString());
		/*String clientIp = "localhost";
		String msg = "Hello";
		byte [] message = msg.getBytes();
		server.sendMessage(message, clientIp);*/
		server.join();
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

		myServer.StartServer();
		//myServer.run();
	}

}

