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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;

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
	public List<ByteBuffer> addCompressedFrames(List<ByteBuffer> frames, Compress algo) throws AvroRemoteException {
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

	private ByteBuffer compress(ByteBuffer uncompressedData, Compress c) throws IOException {
		ByteArrayOutputStream baos = getOutputBuffer(uncompressedData.capacity());
		OutputStream outputStream = null;
		switch (c) {
			case BZIP2:
				outputStream = new BZip2CompressorOutputStream(baos);
				break;
			case LZ4:
				outputStream = new FramedLZ4CompressorOutputStream(baos);
				break;
			case GZIP:
				outputStream = new GzipCompressorOutputStream(baos);
				break;
			case SNAPPY:
				outputStream = new FramedSnappyCompressorOutputStream(baos);
				break;
			case DELFATE:
				outputStream = new DeflateCompressorOutputStream(baos);
				break;
			case PACK200:
				outputStream = new Pack200CompressorOutputStream(baos);
				break;
			case ZSTD:
				outputStream = new ZstdOutputStream(baos);
			case UNKNOWN:
			default:
				System.err.println("CloudFrameProcessor, Unknown compressor: " + c.toString());
				System.exit(-1);
		}

		try {
			outputStream.write(uncompressedData.array());
		} catch (IOException ioe) {
			System.err.println("CloudFrameProcessor, Error compressing " + ioe.toString());
		} finally {
			outputStream.close();
		}

		return ByteBuffer.wrap(baos.toByteArray());
	}

	private ByteBuffer decompress(ByteBuffer compressedData, Compress c) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.array());
		InputStream inputStream = null;
		switch (c) {
			case BZIP2:
				inputStream = new BZip2CompressorInputStream(bais);
				break;
			case LZ4:
				inputStream = new FramedLZ4CompressorInputStream(bais);
				break;
			case GZIP:
				inputStream = new GzipCompressorInputStream(bais);
				break;
			case SNAPPY:
				inputStream = new FramedSnappyCompressorInputStream(bais);
				break;
			case DELFATE:
				inputStream = new DeflateCompressorInputStream(bais);
				break;
			case PACK200:
				inputStream = new Pack200CompressorInputStream(bais);
				break;
			case ZSTD:
				inputStream = new ZstdInputStream(bais);
			case UNKNOWN:
			default:
				System.err.println("CloudFrameProcessor, Unknown decompressor: " + c.toString());
				System.exit(-1);
		}

		ByteBuffer toReturn;

		try {
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
