package server;
//import SendFileServer.SendFile;

import java.io.IOException;
import java.net.*;

import org.apache.avro.ipc.DatagramServer;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;

public class Server {
	private static NettyServer server;
	//private static DatagramServer server;
	final static int port = 20001;
	
	public void InitServer() throws Exception {
		InetSocketAddress socketAddr = new InetSocketAddress(port);
		while (true) {
			server = new NettyServer(new SpecificResponder(IBenchProtocol.class,
					new BenchProtocolImpl()), socketAddr);
			//server = new DatagramServer(new SpecificResponder(IBenchProtocol.class,
				//	new BenchProtocolImpl()), socketAddr);
			
			try {
				server.getPort();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			break;
		}
		
		System.out.println("Server is ready to accept connections");		
	}
	
	public void StartServer() throws Exception {
		server.start();
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

	}

}