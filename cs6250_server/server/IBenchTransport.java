package server;

import org.apache.avro.Protocol;

@SuppressWarnings("all")
public interface IBenchTransport {

	 public static final org.apache.avro.Protocol PROTOCOL1 = org.apache.avro.Protocol.parse("{\"protocol\":\"BenchTransport\",\"namespace\":\"serverTransport\",\"types\":[],\"messages\":{\"chooseTCP\":{\"request\":[],\"response\":\"null\"},\"chooseUDP\":{\"request\":[],\"response\":\"null\"},\"addFrames\":{\"request\":[{\"name\":\"frames\",\"type\":{\"type\":\"array\",\"items\":\"bytes\"}}],\"response\":{\"type\":\"array\",\"items\":\"bytes\"}}}}");

	java.lang.Void selectTCP() throws org.apache.avro.AvroRemoteException;
	  java.lang.Void selectUDP() throws org.apache.avro.AvroRemoteException;
	  java.util.List<java.nio.ByteBuffer> chooseTrans(java.util.List<java.nio.ByteBuffer> Transport) throws org.apache.avro.AvroRemoteException;

	  @SuppressWarnings("all")
	  public interface Callback extends IBenchTransport {
	    public static final org.apache.avro.Protocol PROTOCOL1 = server.IBenchTransport.PROTOCOL1;
	    void selectTCP(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
	    void selectUDP(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
	    void chooseTrans(java.util.List<java.nio.ByteBuffer> Transport, org.apache.avro.ipc.Callback<java.util.List<java.nio.ByteBuffer>> callback) throws java.io.IOException;
	  }
}
