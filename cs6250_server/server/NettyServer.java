package server;

public interface NettyServer {
		/** The port this server runs on. */
		  int getPort();

		  /** Start this server. */
		  void start();

		  /** Stop this server. */
		  void close();

		  /** Wait for this server to exit. */
		  void join() throws InterruptedException;
}
