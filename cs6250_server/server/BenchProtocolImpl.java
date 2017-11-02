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
import org.opencv.imgproc.Imgproc;

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
	public Void addCartoonEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new CartoonEffect()));
		return null;
	}
	
	@Override
	public Void addFaceDetectionEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new FaceDetectionEffect()));
		return null;
	}
	
	@Override
	public Void addMaskEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new MaskEffect()));
		return null;
	}
	
	@Override
	public Void addMotionDetectionEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new MotionDetectionEffect()));
		return null;
	}
	
	@Override
	public Void addCheckerBoardDetectionEffect() throws AvroRemoteException {
		frameProcessor.addEffect(new LocalEffectTask(new CheckerBoardDetectionEffect()));
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
			Mat frame = Imgcodecs.imdecode(encodedMat, Imgcodecs.CV_LOAD_IMAGE_COLOR);
			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGBA);
			frameProcessor.addFrame(frame);
			
			MatOfByte decodedMat = new MatOfByte();
			Mat newFrame = frameProcessor.getFrame();
			if (newFrame != null) {
				Imgcodecs.imencode(".png", newFrame, decodedMat);
				processedFrames.add(ByteBuffer.wrap(decodedMat.toArray()));
			}
		}
		
		return processedFrames;
	}

	@Override
	public Void addSnappyCompression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addGzipCompression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addDeflateCompression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addBzip2Compression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addPack200Compression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addLz4Compression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void addZstdCompression() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
