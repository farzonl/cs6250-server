package server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import cs6250.benchmarkingsuite.imageprocessing.pipeline.*;
import cs6250.benchmarkingsuite.imageprocessing.effects.*;
import org.apache.avro.AvroRemoteException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class BenchProtocolImpl implements IBenchProtocol {

	FrameProcessor frameProcessor;
	
	void StartFrameProcessor() {
		frameProcessor.start();
	}
	
	//TODO create a RemoteEffectTask or rename 
	public BenchProtocolImpl() {
		System.out.println("Starting server side frame processor ");
		frameProcessor = new FrameProcessor(new EffectTask[]{new LocalEffectTask(new IdentityEffect())});
		StartFrameProcessor();
	}
	
	@Override
	public Void addGrayscaleEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new GrayscaleEffect()));
		return null;
	}

	@Override
	public Void addIdentityEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new IdentityEffect()));
		return null;
	}

	@Override
	public Void clearEffects() throws AvroRemoteException {
		System.out.println("Clearling all effects");
		frameProcessor.clearEffects();
		return null;
	}

	@Override
	public List<ByteBuffer> addFrames(List<ByteBuffer> frames)
			throws AvroRemoteException {

		List<ByteBuffer> processedFrames = new ArrayList<ByteBuffer>();
		for (ByteBuffer buf: frames) {
			MatOfByte encodedMat = new MatOfByte(buf.array());
			Mat frame = Imgcodecs.imdecode(encodedMat, 0);
			frameProcessor.addFrame(frame);
			
			MatOfByte decodedMat = new MatOfByte();
			Imgcodecs.imencode(".png", frame, decodedMat);
			processedFrames.add(ByteBuffer.wrap(decodedMat.toArray()));
		}
		
		return processedFrames;
	}

}
