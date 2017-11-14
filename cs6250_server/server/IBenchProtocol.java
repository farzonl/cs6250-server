/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package server;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface IBenchProtocol {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"BenchProtocol\",\"namespace\":\"cs6250.benchmarkingsuite.imageprocessing.server\",\"types\":[],\"messages\":{\"addGrayscaleEffect\":{\"request\":[],\"response\":\"null\"},\"addIdentityEffect\":{\"request\":[],\"response\":\"null\"},\"clearEffects\":{\"request\":[],\"response\":\"null\"},\"addCartoonEffect\":{\"request\":[],\"response\":\"null\"},\"addFaceDetectionEffect\":{\"request\":[],\"response\":\"null\"},\"addMaskEffect\":{\"request\":[],\"response\":\"null\"},\"addMotionDetectionEffect\":{\"request\":[],\"response\":\"null\"},\"addCheckerBoardDetectionEffect\":{\"request\":[],\"response\":\"null\"},\"addFrames\":{\"request\":[{\"name\":\"frames\",\"type\":{\"type\":\"array\",\"items\":\"bytes\"}}],\"response\":{\"type\":\"array\",\"items\":\"bytes\"}},\"addCompressedFrames\":{\"request\":[{\"name\":\"frames\",\"type\":{\"type\":\"array\",\"items\":\"bytes\"}},{\"name\":\"algo\",\"type\":\"string\"}],\"response\":{\"type\":\"array\",\"items\":\"bytes\"}}}}");
  /**
   */
  java.lang.Void addGrayscaleEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addIdentityEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void clearEffects() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addCartoonEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addFaceDetectionEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addMaskEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addMotionDetectionEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.lang.Void addCheckerBoardDetectionEffect() throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.util.List<java.nio.ByteBuffer> addFrames(java.util.List<java.nio.ByteBuffer> frames) throws org.apache.avro.AvroRemoteException;
  /**
   */
  java.util.List<java.nio.ByteBuffer> addCompressedFrames(java.util.List<java.nio.ByteBuffer> frames, java.lang.CharSequence algo) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends IBenchProtocol {
    public static final org.apache.avro.Protocol PROTOCOL = cs6250.benchmarkingsuite.imageprocessing.server.IBenchProtocol.PROTOCOL;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addGrayscaleEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addIdentityEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void clearEffects(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addCartoonEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addFaceDetectionEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addMaskEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addMotionDetectionEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addCheckerBoardDetectionEffect(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addFrames(java.util.List<java.nio.ByteBuffer> frames, org.apache.avro.ipc.Callback<java.util.List<java.nio.ByteBuffer>> callback) throws java.io.IOException;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void addCompressedFrames(java.util.List<java.nio.ByteBuffer> frames, java.lang.CharSequence algo, org.apache.avro.ipc.Callback<java.util.List<java.nio.ByteBuffer>> callback) throws java.io.IOException;
  }
}