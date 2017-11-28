package server;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.DatagramServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import cs6250.benchmarkingsuite.imageprocessing.server.IBenchProtocol;

public class UDPServer {

	private DatagramServer server;
	final static int port = 30001;
	public void InitServer() throws IOException {
		InetSocketAddress socketAddr = new InetSocketAddress(port);
		while (true) {
			server = new DatagramServer(new SpecificResponder(IBenchProtocol.class,
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
}