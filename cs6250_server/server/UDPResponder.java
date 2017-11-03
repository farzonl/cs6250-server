package server;

import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.Protocol.Message;
import org.apache.avro.util.ByteBufferInputStream;
import org.apache.avro.util.ByteBufferOutputStream;
import org.apache.avro.util.Utf8;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.ipc.MD5;
import org.apache.avro.ipc.RPCContext;
import org.apache.avro.ipc.RPCPlugin;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import io.netty.handler.codec.*;

/** Base class for the server side of a protocol interaction. */
public abstract class UDPResponder {
  private static final Logger LOG = LoggerFactory.getLogger(UDPResponder.class);

  private static final Schema META =
    Schema.createMap(Schema.create(Schema.Type.BYTES));
  private static final GenericDatumReader<Map<String,ByteBuffer>>
    META_READER = new GenericDatumReader<Map<String,ByteBuffer>>(META);
  private static final GenericDatumWriter<Map<String,ByteBuffer>>
    META_WRITER = new GenericDatumWriter<Map<String,ByteBuffer>>(META);

  private static final ThreadLocal<Protocol> REMOTE =  new ThreadLocal<Protocol>();

  private final Map<MD5,Protocol> protocols = new ConcurrentHashMap<MD5,Protocol>();

  private final Protocol local;
  private final MD5 localHash;
  protected final List<RPCPlugin> rpcMetaPlugins;

  protected UDPResponder(Protocol local) {
    this.local = local;
    this.localHash = new MD5();
    localHash.bytes(local.getMD5());
    protocols.put(localHash, local);
    this.rpcMetaPlugins = new CopyOnWriteArrayList<RPCPlugin>();
  }
  /** Return the remote protocol.  Accesses a {@link ThreadLocal} that's set
   * around calls to {@link #respond(Protocol.Message, Object)}. */
  public static Protocol getRemote() { return REMOTE.get(); }

  /** Return the local protocol. */
  public Protocol getLocal() { return local; }
  
  public List<ByteBuffer> respond(List<ByteBuffer> buffers) throws IOException {
	    return respond(buffers, null);
	  }
  
  public List<ByteBuffer> respond(List<ByteBuffer> buffers,
          Transceiver connection) throws IOException {
Decoder in = DecoderFactory.get().binaryDecoder(
new ByteBufferInputStream(buffers), null);
ByteBufferOutputStream bbo = new ByteBufferOutputStream();
BinaryEncoder out = EncoderFactory.get().binaryEncoder(bbo, null);
Exception error = null;
RPCContext context = new RPCContext();
List<ByteBuffer> payload = null;
List<ByteBuffer> handshake = null;
boolean wasConnected = connection != null && connection.isConnected();
try {
Protocol remote = handshake(in, out, connection);
out.flush();
if (remote == null)                        // handshake failed
return bbo.getBufferList();
handshake = bbo.getBufferList();

// read request using remote protocol specification
context.setRequestCallMeta(META_READER.read(null, in));
String messageName = in.readString(null).toString();
if (messageName.equals(""))                 // a handshake ping
return handshake;
Message rm = remote.getMessages().get(messageName);
if (rm == null)
throw new AvroRuntimeException("No such remote message: "+messageName);
Message m = getLocal().getMessages().get(messageName);
if (m == null)
throw new AvroRuntimeException("No message named "+messageName
               +" in "+getLocal());

Object request = readRequest(rm.getRequest(), m.getRequest(), in);

context.setMessage(rm);
for (RPCPlugin plugin : rpcMetaPlugins) {
plugin.serverReceiveRequest(context);
}

// create response using local protocol specification
if ((m.isOneWay() != rm.isOneWay()) && wasConnected)
throw new AvroRuntimeException("Not both one-way: "+messageName);

Object response = null;

try {
REMOTE.set(remote);
response = respond(m, request);
context.setResponse(response);
} catch (Exception e) {
error = e;
context.setError(error);
LOG.warn("user error", e);
} finally {
REMOTE.set(null);
}

if (m.isOneWay() && wasConnected)           // no response data
return null;

out.writeBoolean(error != null);
if (error == null)
writeResponse(m.getResponse(), response, out);
else
try {
writeError(m.getErrors(), error, out);
} catch (UnresolvedUnionException e) {    // unexpected error
throw error;
}
} catch (Exception e) {                       // system error
LOG.warn("system error", e);
context.setError(e);
bbo = new ByteBufferOutputStream();
out = EncoderFactory.get().binaryEncoder(bbo, null);
out.writeBoolean(true);
writeError(Protocol.SYSTEM_ERRORS, new Utf8(e.toString()), out);
if (null == handshake) {
handshake = new ByteBufferOutputStream().getBufferList();
}
}
out.flush();
payload = bbo.getBufferList();

// Grab meta-data from plugins
context.setResponsePayload(payload);
for (RPCPlugin plugin : rpcMetaPlugins) {
plugin.serverSendResponse(context);
}
META_WRITER.write(context.responseCallMeta(), out);
out.flush();
// Prepend handshake and append payload
bbo.prepend(handshake);
bbo.append(payload);

return bbo.getBufferList();
}

private SpecificDatumWriter<HandshakeResponse> handshakeWriter =
new SpecificDatumWriter<HandshakeResponse>(HandshakeResponse.class);
private SpecificDatumReader<HandshakeRequest> handshakeReader =
new SpecificDatumReader<HandshakeRequest>(HandshakeRequest.class);

private Protocol handshake(Decoder in, Encoder out, Transceiver connection)
throws IOException {
if (connection != null && connection.isConnected())
return connection.getRemote();
HandshakeRequest request = (HandshakeRequest)handshakeReader.read(null, in);
Protocol remote = protocols.get(request.clientHash);
if (remote == null && request.clientProtocol != null) {
remote = Protocol.parse(request.clientProtocol.toString());
protocols.put(request.clientHash, remote);
}
HandshakeResponse response = new HandshakeResponse();
if (localHash.equals(request.serverHash)) {
response.match =
remote == null ? HandshakeMatch.NONE : HandshakeMatch.BOTH;
} else {
response.match =
remote == null ? HandshakeMatch.NONE : HandshakeMatch.CLIENT;
}
if (response.match != HandshakeMatch.BOTH) {
response.serverProtocol = local.toString();
response.serverHash = localHash;
}

RPCContext context = new RPCContext();
context.setHandshakeRequest(request);
context.setHandshakeResponse(response);
for (RPCPlugin plugin : rpcMetaPlugins) {
plugin.serverConnecting(context);
}
handshakeWriter.write(response, out);

if (connection != null && response.match != HandshakeMatch.NONE)
connection.setRemote(remote);

return remote;
}
 /** Computes the response for a message. */
  public abstract Object respond(Message message, Object request)
    throws Exception;

  /** Reads a request message. */
  public abstract Object readRequest(Schema actual, Schema expected, Decoder in)
    throws IOException;

  /** Writes a response message. */
  public abstract void writeResponse(Schema schema, Object response,
                                     Encoder out) throws IOException;

  /** Writes an error message. */
  public abstract void writeError(Schema schema, Object error,
                                  Encoder out) throws IOException;

}