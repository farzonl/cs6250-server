package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import cs6250.benchmarkingsuite.imageprocessing.effects.CartoonEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.CheckerBoardDetectionEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.FaceDetectionEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.GrayscaleEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.IdentityEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.MaskEffect;
import cs6250.benchmarkingsuite.imageprocessing.effects.MotionDetectionEffect;
import cs6250.benchmarkingsuite.imageprocessing.pipeline.EffectTask;
import cs6250.benchmarkingsuite.imageprocessing.pipeline.FrameProcessor;
import cs6250.benchmarkingsuite.imageprocessing.pipeline.LocalEffectTask;

public class BenchProtocolImpl implements IBenchProtocol {

	FrameProcessor frameProcessor;
	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;
	private ByteArrayOutputStream outputBuffer;
	private CompressorStreamFactory csf;

	void StartFrameProcessor() {
		frameProcessor.start();
	}

	//TODO create a RemoteEffectTask or rename
	public BenchProtocolImpl() {
		System.out.println("Starting server side frame processor ");
		csf = new CompressorStreamFactory();
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
	public List<ByteBuffer> addCompressedFrames(List<ByteBuffer> frames, CharSequence algo) throws AvroRemoteException {
		List<ByteBuffer> deompressedFrames = new ArrayList<>();
		List<ByteBuffer> effectedFrames;
		List<ByteBuffer> compressedFrames = new ArrayList<>();
		try {
			for (ByteBuffer bb : frames) {
				ByteBuffer b = decompress(bb, algo);
				deompressedFrames.add(b);
			}
		} catch (IOException ioe) {
			System.err.println("Error decompressing");
			return null;
		}
		effectedFrames = addFrames(deompressedFrames);
		try {
			for (ByteBuffer bb : effectedFrames) {
				ByteBuffer b = compress(bb, algo);
				compressedFrames.add(b);
			}
		} catch (IOException ioe) {
			System.err.println("Error decompressing");
			return null;
		}
		return compressedFrames;
	}

	private ByteBuffer compress(ByteBuffer uncompressedData, CharSequence c) throws IOException {
		ByteArrayOutputStream baos = getOutputBuffer(uncompressedData.capacity());
		OutputStream outputStream = null;
		try {
			outputStream = csf.createCompressorOutputStream(c.toString(), baos);
			outputStream.write(uncompressedData.array());
		} catch (IOException ioe) {
			System.err.println("CloudFrameProcessor, Error compressing " + ioe.toString());
		} catch (CompressorException ce) {
			System.err.println("CloudFrameProcessor, Unknown compressor" + ce.toString());
		} finally {
			outputStream.close();
		}

		return ByteBuffer.wrap(baos.toByteArray());
	}

	private ByteBuffer decompress(ByteBuffer compressedData, CharSequence c) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.array());
		InputStream inputStream = null;
		ByteBuffer toReturn;
		try {
            inputStream = csf.createCompressorInputStream(c.toString(), bais);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int readCount = -1;

			while ((readCount = inputStream.read(buffer, compressedData.position(), buffer.length)) > 0) {
				baos.write(buffer, 0, readCount);
			}
			toReturn = ByteBuffer.wrap(baos.toByteArray());
		} catch (IOException ioe) {
			toReturn = null;
			System.err.println("CloudFrameProcessor, Error decompressing" + ioe.toString());
		} catch (CompressorException ce) {
			toReturn = null;
			System.err.println("CloudFrameProcessor, Unknown decompressor" + ce.toString());
		} finally {
			inputStream.close();
		}
		return toReturn;
	}

	private ByteArrayOutputStream getOutputBuffer(int suggestedLength) {
		if (outputBuffer == null) {
			outputBuffer = new ByteArrayOutputStream(suggestedLength);
		}
		outputBuffer.reset();
		return outputBuffer;
	}
}
