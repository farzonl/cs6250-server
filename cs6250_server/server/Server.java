package server;

import java.net.*;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import cs6250.benchmarkingsuite.imageprocessing.server.IBenchProtocol;
import cs6250.benchmarkingsuite.imageprocessing.static_files.*;

public class Server {
	private static NettyServer server;
	final static int port = 20001;
	
	public void InitServer() throws UnknownHostException {
		InetSocketAddress socketAddr = new InetSocketAddress(port);
		while (true) {
			server = new NettyServer(new SpecificResponder(IBenchProtocol.class,
					new BenchProtocolImpl()), socketAddr);
			
			try {
				server.getPort();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			break;
		}
		
		System.err.println("Server is ready to accept connections");		
	}
	
	public void StartServer() {
		server.start();
	}
	
	public static void main(String[] args) {
		System.loadLibrary("opencv_java330");
		System.out.println("Cloud Server");
		
		Resources.initServerResources();
		
		Server myServer = new Server();
		try {
			myServer.InitServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		myServer.StartServer();
	}

}
