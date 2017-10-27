package server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRemoteException;
public class IBenchTransportImpl implements IBenchTransport{

	@Override
	public Void selectTCP() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void selectUDP() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ByteBuffer> chooseTrans(List<ByteBuffer> Transport) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
