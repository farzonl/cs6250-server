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
		int numRemaining = 0;
		
		for (ByteBuffer buf: frames) {
			++numRemaining;
			MatOfByte m = new MatOfByte(buf.array());
			Mat frame = Imgcodecs.imdecode(m, 0);
			frameProcessor.addFrame(frame);
		}
		
		List<ByteBuffer> processedFrames = new ArrayList<ByteBuffer>();
		while (numRemaining > 0) {
			Mat newFrame = frameProcessor.getFrame();
			MatOfByte m = new MatOfByte();
			Imgcodecs.imencode(".png", newFrame, m);
			processedFrames.add(ByteBuffer.wrap(m.toArray()));
			--numRemaining;
		}
		
		return processedFrames;
	}

}
