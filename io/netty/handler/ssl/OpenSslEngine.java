package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.security.cert.X509Certificate;
import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.SSL;

public final class OpenSslEngine extends SSLEngine {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSslEngine.class);
   private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];
   private static final X509Certificate[] EMPTY_X509_CERTIFICATES = new X509Certificate[0];
   private static final SSLException ENGINE_CLOSED = new SSLException("engine closed");
   private static final SSLException RENEGOTIATION_UNSUPPORTED = new SSLException("renegotiation unsupported");
   private static final SSLException ENCRYPTED_PACKET_OVERSIZED = new SSLException("encrypted packet oversized");
   private static final int MAX_PLAINTEXT_LENGTH = 16384;
   private static final int MAX_COMPRESSED_LENGTH = 17408;
   private static final int MAX_CIPHERTEXT_LENGTH = 18432;
   static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
   static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = 2329;
   private static final AtomicIntegerFieldUpdater DESTROYED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(OpenSslEngine.class, "destroyed");
   private long ssl;
   private long networkBIO;
   private int accepted;
   private boolean handshakeFinished;
   private boolean receivedShutdown;
   private volatile int destroyed;
   private String cipher;
   private volatile String applicationProtocol;
   private boolean isInboundDone;
   private boolean isOutboundDone;
   private boolean engineClosed;
   private int lastPrimingReadResult;
   private final ByteBufAllocator alloc;
   private final String fallbackApplicationProtocol;
   private SSLSession session;

   public OpenSslEngine(long sslCtx, ByteBufAllocator alloc, String fallbackApplicationProtocol) {
      OpenSsl.ensureAvailability();
      if(sslCtx == 0L) {
         throw new NullPointerException("sslContext");
      } else if(alloc == null) {
         throw new NullPointerException("alloc");
      } else {
         this.alloc = alloc;
         this.ssl = SSL.newSSL(sslCtx, true);
         this.networkBIO = SSL.makeNetworkBIO(this.ssl);
         this.fallbackApplicationProtocol = fallbackApplicationProtocol;
      }
   }

   public synchronized void shutdown() {
      if(DESTROYED_UPDATER.compareAndSet(this, 0, 1)) {
         SSL.freeSSL(this.ssl);
         SSL.freeBIO(this.networkBIO);
         this.ssl = this.networkBIO = 0L;
         this.isInboundDone = this.isOutboundDone = this.engineClosed = true;
      }

   }

   private int writePlaintextData(ByteBuffer src) {
      int pos = src.position();
      int limit = src.limit();
      int len = Math.min(limit - pos, 16384);
      int sslWrote;
      if(src.isDirect()) {
         long addr = Buffer.address(src) + (long)pos;
         sslWrote = SSL.writeToSSL(this.ssl, addr, len);
         if(sslWrote > 0) {
            src.position(pos + sslWrote);
            return sslWrote;
         } else {
            throw new IllegalStateException("SSL.writeToSSL() returned a non-positive value: " + sslWrote);
         }
      } else {
         ByteBuf buf = this.alloc.directBuffer(len);

         int var9;
         try {
            long addr;
            if(buf.hasMemoryAddress()) {
               addr = buf.memoryAddress();
            } else {
               addr = Buffer.address(buf.nioBuffer());
            }

            src.limit(pos + len);
            buf.setBytes(0, (ByteBuffer)src);
            src.limit(limit);
            sslWrote = SSL.writeToSSL(this.ssl, addr, len);
            if(sslWrote <= 0) {
               src.position(pos);
               throw new IllegalStateException("SSL.writeToSSL() returned a non-positive value: " + sslWrote);
            }

            src.position(pos + sslWrote);
            var9 = sslWrote;
         } finally {
            buf.release();
         }

         return var9;
      }
   }

   private int writeEncryptedData(ByteBuffer src) {
      int pos = src.position();
      int len = src.remaining();
      if(src.isDirect()) {
         long addr = Buffer.address(src) + (long)pos;
         int netWrote = SSL.writeToBIO(this.networkBIO, addr, len);
         if(netWrote >= 0) {
            src.position(pos + netWrote);
            this.lastPrimingReadResult = SSL.readFromSSL(this.ssl, addr, 0);
            return netWrote;
         } else {
            return 0;
         }
      } else {
         ByteBuf buf = this.alloc.directBuffer(len);

         int var8;
         try {
            long addr;
            if(buf.hasMemoryAddress()) {
               addr = buf.memoryAddress();
            } else {
               addr = Buffer.address(buf.nioBuffer());
            }

            buf.setBytes(0, (ByteBuffer)src);
            int netWrote = SSL.writeToBIO(this.networkBIO, addr, len);
            if(netWrote < 0) {
               src.position(pos);
               return 0;
            }

            src.position(pos + netWrote);
            this.lastPrimingReadResult = SSL.readFromSSL(this.ssl, addr, 0);
            var8 = netWrote;
         } finally {
            buf.release();
         }

         return var8;
      }
   }

   private int readPlaintextData(ByteBuffer dst) {
      if(dst.isDirect()) {
         int pos = dst.position();
         long addr = Buffer.address(dst) + (long)pos;
         int len = dst.limit() - pos;
         int sslRead = SSL.readFromSSL(this.ssl, addr, len);
         if(sslRead > 0) {
            dst.position(pos + sslRead);
            return sslRead;
         } else {
            return 0;
         }
      } else {
         int pos = dst.position();
         int limit = dst.limit();
         int len = Math.min(18713, limit - pos);
         ByteBuf buf = this.alloc.directBuffer(len);

         int var9;
         try {
            long addr;
            if(buf.hasMemoryAddress()) {
               addr = buf.memoryAddress();
            } else {
               addr = Buffer.address(buf.nioBuffer());
            }

            int sslRead = SSL.readFromSSL(this.ssl, addr, len);
            if(sslRead <= 0) {
               return 0;
            }

            dst.limit(pos + sslRead);
            buf.getBytes(0, (ByteBuffer)dst);
            dst.limit(limit);
            var9 = sslRead;
         } finally {
            buf.release();
         }

         return var9;
      }
   }

   private int readEncryptedData(ByteBuffer dst, int pending) {
      if(dst.isDirect() && dst.remaining() >= pending) {
         int pos = dst.position();
         long addr = Buffer.address(dst) + (long)pos;
         int bioRead = SSL.readFromBIO(this.networkBIO, addr, pending);
         if(bioRead > 0) {
            dst.position(pos + bioRead);
            return bioRead;
         } else {
            return 0;
         }
      } else {
         ByteBuf buf = this.alloc.directBuffer(pending);

         int var8;
         try {
            long addr;
            if(buf.hasMemoryAddress()) {
               addr = buf.memoryAddress();
            } else {
               addr = Buffer.address(buf.nioBuffer());
            }

            int bioRead = SSL.readFromBIO(this.networkBIO, addr, pending);
            if(bioRead <= 0) {
               return 0;
            }

            int oldLimit = dst.limit();
            dst.limit(dst.position() + bioRead);
            buf.getBytes(0, (ByteBuffer)dst);
            dst.limit(oldLimit);
            var8 = bioRead;
         } finally {
            buf.release();
         }

         return var8;
      }
   }

   public synchronized SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
      if(this.destroyed != 0) {
         return new SSLEngineResult(Status.CLOSED, HandshakeStatus.NOT_HANDSHAKING, 0, 0);
      } else if(srcs == null) {
         throw new NullPointerException("srcs");
      } else if(dst == null) {
         throw new NullPointerException("dst");
      } else if(offset < srcs.length && offset + length <= srcs.length) {
         if(dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
         } else {
            if(this.accepted == 0) {
               this.beginHandshakeImplicitly();
            }

            HandshakeStatus handshakeStatus = this.getHandshakeStatus();
            if((!this.handshakeFinished || this.engineClosed) && handshakeStatus == HandshakeStatus.NEED_UNWRAP) {
               return new SSLEngineResult(this.getEngineStatus(), HandshakeStatus.NEED_UNWRAP, 0, 0);
            } else {
               int bytesProduced = 0;
               int pendingNet = SSL.pendingWrittenBytesInBIO(this.networkBIO);
               if(pendingNet > 0) {
                  int capacity = dst.remaining();
                  if(capacity < pendingNet) {
                     return new SSLEngineResult(Status.BUFFER_OVERFLOW, handshakeStatus, 0, bytesProduced);
                  } else {
                     try {
                        bytesProduced = bytesProduced + this.readEncryptedData(dst, pendingNet);
                     } catch (Exception var13) {
                        throw new SSLException(var13);
                     }

                     if(this.isOutboundDone) {
                        this.shutdown();
                     }

                     return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), 0, bytesProduced);
                  }
               } else {
                  int bytesConsumed = 0;

                  for(int i = offset; i < length; ++i) {
                     ByteBuffer src = srcs[i];

                     while(src.hasRemaining()) {
                        try {
                           bytesConsumed += this.writePlaintextData(src);
                        } catch (Exception var15) {
                           throw new SSLException(var15);
                        }

                        pendingNet = SSL.pendingWrittenBytesInBIO(this.networkBIO);
                        if(pendingNet > 0) {
                           int capacity = dst.remaining();
                           if(capacity < pendingNet) {
                              return new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                           }

                           try {
                              bytesProduced = bytesProduced + this.readEncryptedData(dst, pendingNet);
                           } catch (Exception var14) {
                              throw new SSLException(var14);
                           }

                           return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                        }
                     }
                  }

                  return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
               }
            }
         }
      } else {
         throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
      }
   }

   public synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
      if(this.destroyed != 0) {
         return new SSLEngineResult(Status.CLOSED, HandshakeStatus.NOT_HANDSHAKING, 0, 0);
      } else if(src == null) {
         throw new NullPointerException("src");
      } else if(dsts == null) {
         throw new NullPointerException("dsts");
      } else if(offset < dsts.length && offset + length <= dsts.length) {
         int capacity = 0;
         int endOffset = offset + length;

         for(int i = offset; i < endOffset; ++i) {
            ByteBuffer dst = dsts[i];
            if(dst == null) {
               throw new IllegalArgumentException();
            }

            if(dst.isReadOnly()) {
               throw new ReadOnlyBufferException();
            }

            capacity += dst.remaining();
         }

         if(this.accepted == 0) {
            this.beginHandshakeImplicitly();
         }

         HandshakeStatus handshakeStatus = this.getHandshakeStatus();
         if((!this.handshakeFinished || this.engineClosed) && handshakeStatus == HandshakeStatus.NEED_WRAP) {
            return new SSLEngineResult(this.getEngineStatus(), HandshakeStatus.NEED_WRAP, 0, 0);
         } else if(src.remaining() > 18713) {
            this.isInboundDone = true;
            this.isOutboundDone = true;
            this.engineClosed = true;
            this.shutdown();
            throw ENCRYPTED_PACKET_OVERSIZED;
         } else {
            int bytesConsumed = 0;
            this.lastPrimingReadResult = 0;

            try {
               bytesConsumed = bytesConsumed + this.writeEncryptedData(src);
            } catch (Exception var17) {
               throw new SSLException(var17);
            }

            String error = SSL.getLastError();
            if(error != null && !error.startsWith("error:00000000:")) {
               if(logger.isInfoEnabled()) {
                  logger.info("SSL_read failed: primingReadResult: " + this.lastPrimingReadResult + "; OpenSSL error: \'" + error + '\'');
               }

               this.shutdown();
               throw new SSLException(error);
            } else {
               int pendingApp = SSL.isInInit(this.ssl) == 0?SSL.pendingReadableBytesInSSL(this.ssl):0;
               if(capacity < pendingApp) {
                  return new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, 0);
               } else {
                  int bytesProduced = 0;
                  int idx = offset;

                  while(idx < endOffset) {
                     ByteBuffer dst = dsts[idx];
                     if(!dst.hasRemaining()) {
                        ++idx;
                     } else {
                        if(pendingApp <= 0) {
                           break;
                        }

                        int bytesRead;
                        try {
                           bytesRead = this.readPlaintextData(dst);
                        } catch (Exception var16) {
                           throw new SSLException(var16);
                        }

                        if(bytesRead == 0) {
                           break;
                        }

                        bytesProduced += bytesRead;
                        pendingApp -= bytesRead;
                        if(!dst.hasRemaining()) {
                           ++idx;
                        }
                     }
                  }

                  if(!this.receivedShutdown && (SSL.getShutdown(this.ssl) & 2) == 2) {
                     this.receivedShutdown = true;
                     this.closeOutbound();
                     this.closeInbound();
                  }

                  return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
               }
            }
         }
      } else {
         throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
      }
   }

   public Runnable getDelegatedTask() {
      return null;
   }

   public synchronized void closeInbound() throws SSLException {
      if(!this.isInboundDone) {
         this.isInboundDone = true;
         this.engineClosed = true;
         if(this.accepted != 0) {
            if(!this.receivedShutdown) {
               this.shutdown();
               throw new SSLException("Inbound closed before receiving peer\'s close_notify: possible truncation attack?");
            }
         } else {
            this.shutdown();
         }

      }
   }

   public synchronized boolean isInboundDone() {
      return this.isInboundDone || this.engineClosed;
   }

   public synchronized void closeOutbound() {
      if(!this.isOutboundDone) {
         this.isOutboundDone = true;
         this.engineClosed = true;
         if(this.accepted != 0 && this.destroyed == 0) {
            int mode = SSL.getShutdown(this.ssl);
            if((mode & 1) != 1) {
               SSL.shutdownSSL(this.ssl);
            }
         } else {
            this.shutdown();
         }

      }
   }

   public synchronized boolean isOutboundDone() {
      return this.isOutboundDone;
   }

   public String[] getSupportedCipherSuites() {
      return EmptyArrays.EMPTY_STRINGS;
   }

   public String[] getEnabledCipherSuites() {
      return EmptyArrays.EMPTY_STRINGS;
   }

   public void setEnabledCipherSuites(String[] strings) {
      throw new UnsupportedOperationException();
   }

   public String[] getSupportedProtocols() {
      return EmptyArrays.EMPTY_STRINGS;
   }

   public String[] getEnabledProtocols() {
      return EmptyArrays.EMPTY_STRINGS;
   }

   public void setEnabledProtocols(String[] strings) {
      throw new UnsupportedOperationException();
   }

   public SSLSession getSession() {
      SSLSession session = this.session;
      if(session == null) {
         this.session = session = new SSLSession() {
            public byte[] getId() {
               return String.valueOf(OpenSslEngine.this.ssl).getBytes();
            }

            public SSLSessionContext getSessionContext() {
               return null;
            }

            public long getCreationTime() {
               return 0L;
            }

            public long getLastAccessedTime() {
               return 0L;
            }

            public void invalidate() {
            }

            public boolean isValid() {
               return false;
            }

            public void putValue(String s, Object o) {
            }

            public Object getValue(String s) {
               return null;
            }

            public void removeValue(String s) {
            }

            public String[] getValueNames() {
               return EmptyArrays.EMPTY_STRINGS;
            }

            public Certificate[] getPeerCertificates() {
               return OpenSslEngine.EMPTY_CERTIFICATES;
            }

            public Certificate[] getLocalCertificates() {
               return OpenSslEngine.EMPTY_CERTIFICATES;
            }

            public X509Certificate[] getPeerCertificateChain() {
               return OpenSslEngine.EMPTY_X509_CERTIFICATES;
            }

            public Principal getPeerPrincipal() {
               return null;
            }

            public Principal getLocalPrincipal() {
               return null;
            }

            public String getCipherSuite() {
               return OpenSslEngine.this.cipher;
            }

            public String getProtocol() {
               String applicationProtocol = OpenSslEngine.this.applicationProtocol;
               return applicationProtocol == null?"unknown":"unknown:" + applicationProtocol;
            }

            public String getPeerHost() {
               return null;
            }

            public int getPeerPort() {
               return 0;
            }

            public int getPacketBufferSize() {
               return 18713;
            }

            public int getApplicationBufferSize() {
               return 16384;
            }
         };
      }

      return session;
   }

   public synchronized void beginHandshake() throws SSLException {
      if(this.engineClosed) {
         throw ENGINE_CLOSED;
      } else {
         switch(this.accepted) {
         case 0:
            SSL.doHandshake(this.ssl);
            this.accepted = 2;
            break;
         case 1:
            this.accepted = 2;
            break;
         case 2:
            throw RENEGOTIATION_UNSUPPORTED;
         default:
            throw new Error();
         }

      }
   }

   private synchronized void beginHandshakeImplicitly() throws SSLException {
      if(this.engineClosed) {
         throw ENGINE_CLOSED;
      } else {
         if(this.accepted == 0) {
            SSL.doHandshake(this.ssl);
            this.accepted = 1;
         }

      }
   }

   private Status getEngineStatus() {
      return this.engineClosed?Status.CLOSED:Status.OK;
   }

   public synchronized HandshakeStatus getHandshakeStatus() {
      if(this.accepted != 0 && this.destroyed == 0) {
         if(!this.handshakeFinished) {
            if(SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0) {
               return HandshakeStatus.NEED_WRAP;
            } else if(SSL.isInInit(this.ssl) == 0) {
               this.handshakeFinished = true;
               this.cipher = SSL.getCipherForSSL(this.ssl);
               String applicationProtocol = SSL.getNextProtoNegotiated(this.ssl);
               if(applicationProtocol == null) {
                  applicationProtocol = this.fallbackApplicationProtocol;
               }

               if(applicationProtocol != null) {
                  this.applicationProtocol = applicationProtocol.replace(':', '_');
               } else {
                  this.applicationProtocol = null;
               }

               return HandshakeStatus.FINISHED;
            } else {
               return HandshakeStatus.NEED_UNWRAP;
            }
         } else {
            return this.engineClosed?(SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0?HandshakeStatus.NEED_WRAP:HandshakeStatus.NEED_UNWRAP):HandshakeStatus.NOT_HANDSHAKING;
         }
      } else {
         return HandshakeStatus.NOT_HANDSHAKING;
      }
   }

   public void setUseClientMode(boolean clientMode) {
      if(clientMode) {
         throw new UnsupportedOperationException();
      }
   }

   public boolean getUseClientMode() {
      return false;
   }

   public void setNeedClientAuth(boolean b) {
      if(b) {
         throw new UnsupportedOperationException();
      }
   }

   public boolean getNeedClientAuth() {
      return false;
   }

   public void setWantClientAuth(boolean b) {
      if(b) {
         throw new UnsupportedOperationException();
      }
   }

   public boolean getWantClientAuth() {
      return false;
   }

   public void setEnableSessionCreation(boolean b) {
      if(b) {
         throw new UnsupportedOperationException();
      }
   }

   public boolean getEnableSessionCreation() {
      return false;
   }

   static {
      ENGINE_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      RENEGOTIATION_UNSUPPORTED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      ENCRYPTED_PACKET_OVERSIZED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
   }
}
