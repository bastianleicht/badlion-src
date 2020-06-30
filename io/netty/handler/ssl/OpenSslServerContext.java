package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.OpenSslSessionStats;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;

public final class OpenSslServerContext extends SslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSslServerContext.class);
   private static final List DEFAULT_CIPHERS;
   private final long aprPool;
   private final List ciphers;
   private final List unmodifiableCiphers;
   private final long sessionCacheSize;
   private final long sessionTimeout;
   private final List nextProtocols;
   private final long ctx;
   private final OpenSslSessionStats stats;

   public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
      this(certChainFile, keyFile, (String)null);
   }

   public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
      this(certChainFile, keyFile, keyPassword, (Iterable)null, (Iterable)null, 0L, 0L);
   }

   public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable ciphers, Iterable nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
      this.ciphers = new ArrayList();
      this.unmodifiableCiphers = Collections.unmodifiableList(this.ciphers);
      OpenSsl.ensureAvailability();
      if(certChainFile == null) {
         throw new NullPointerException("certChainFile");
      } else if(!certChainFile.isFile()) {
         throw new IllegalArgumentException("certChainFile is not a file: " + certChainFile);
      } else if(keyFile == null) {
         throw new NullPointerException("keyPath");
      } else if(!keyFile.isFile()) {
         throw new IllegalArgumentException("keyPath is not a file: " + keyFile);
      } else {
         if(ciphers == null) {
            ciphers = DEFAULT_CIPHERS;
         }

         if(keyPassword == null) {
            keyPassword = "";
         }

         if(nextProtocols == null) {
            nextProtocols = Collections.emptyList();
         }

         for(String c : ciphers) {
            if(c == null) {
               break;
            }

            this.ciphers.add(c);
         }

         List<String> nextProtoList = new ArrayList();

         for(String p : nextProtocols) {
            if(p == null) {
               break;
            }

            nextProtoList.add(p);
         }

         this.nextProtocols = Collections.unmodifiableList(nextProtoList);
         this.aprPool = Pool.create(0L);
         boolean success = false;

         try {
            synchronized(OpenSslServerContext.class) {
               try {
                  this.ctx = SSLContext.make(this.aprPool, 6, 1);
               } catch (Exception var25) {
                  throw new SSLException("failed to create an SSL_CTX", var25);
               }

               SSLContext.setOptions(this.ctx, 4095);
               SSLContext.setOptions(this.ctx, 16777216);
               SSLContext.setOptions(this.ctx, 4194304);
               SSLContext.setOptions(this.ctx, 524288);
               SSLContext.setOptions(this.ctx, 1048576);
               SSLContext.setOptions(this.ctx, 65536);

               try {
                  StringBuilder cipherBuf = new StringBuilder();

                  for(String c : this.ciphers) {
                     cipherBuf.append(c);
                     cipherBuf.append(':');
                  }

                  cipherBuf.setLength(cipherBuf.length() - 1);
                  SSLContext.setCipherSuite(this.ctx, cipherBuf.toString());
               } catch (SSLException var28) {
                  throw var28;
               } catch (Exception var29) {
                  throw new SSLException("failed to set cipher suite: " + this.ciphers, var29);
               }

               SSLContext.setVerify(this.ctx, 0, 10);

               try {
                  if(!SSLContext.setCertificate(this.ctx, certChainFile.getPath(), keyFile.getPath(), keyPassword, 0)) {
                     throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile + " (" + SSL.getLastError() + ')');
                  }
               } catch (SSLException var26) {
                  throw var26;
               } catch (Exception var27) {
                  throw new SSLException("failed to set certificate: " + certChainFile + " and " + keyFile, var27);
               }

               if(!SSLContext.setCertificateChainFile(this.ctx, certChainFile.getPath(), true)) {
                  String error = SSL.getLastError();
                  if(!error.startsWith("error:00000000:")) {
                     throw new SSLException("failed to set certificate chain: " + certChainFile + " (" + SSL.getLastError() + ')');
                  }
               }

               if(!nextProtoList.isEmpty()) {
                  StringBuilder nextProtocolBuf = new StringBuilder();

                  for(String p : nextProtoList) {
                     nextProtocolBuf.append(p);
                     nextProtocolBuf.append(',');
                  }

                  nextProtocolBuf.setLength(nextProtocolBuf.length() - 1);
                  SSLContext.setNextProtos(this.ctx, nextProtocolBuf.toString());
               }

               if(sessionCacheSize > 0L) {
                  this.sessionCacheSize = sessionCacheSize;
                  SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
               } else {
                  this.sessionCacheSize = sessionCacheSize = SSLContext.setSessionCacheSize(this.ctx, 20480L);
                  SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
               }

               if(sessionTimeout > 0L) {
                  this.sessionTimeout = sessionTimeout;
                  SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
               } else {
                  this.sessionTimeout = sessionTimeout = SSLContext.setSessionCacheTimeout(this.ctx, 300L);
                  SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
               }
            }

            success = true;
         } finally {
            if(!success) {
               this.destroyPools();
            }

         }

         this.stats = new OpenSslSessionStats(this.ctx);
      }
   }

   public boolean isClient() {
      return false;
   }

   public List cipherSuites() {
      return this.unmodifiableCiphers;
   }

   public long sessionCacheSize() {
      return this.sessionCacheSize;
   }

   public long sessionTimeout() {
      return this.sessionTimeout;
   }

   public List nextProtocols() {
      return this.nextProtocols;
   }

   public long context() {
      return this.ctx;
   }

   public OpenSslSessionStats stats() {
      return this.stats;
   }

   public SSLEngine newEngine(ByteBufAllocator alloc) {
      return this.nextProtocols.isEmpty()?new OpenSslEngine(this.ctx, alloc, (String)null):new OpenSslEngine(this.ctx, alloc, (String)this.nextProtocols.get(this.nextProtocols.size() - 1));
   }

   public SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
      throw new UnsupportedOperationException();
   }

   public void setTicketKeys(byte[] keys) {
      if(keys == null) {
         throw new NullPointerException("keys");
      } else {
         SSLContext.setSessionTicketKeys(this.ctx, keys);
      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      synchronized(OpenSslServerContext.class) {
         if(this.ctx != 0L) {
            SSLContext.free(this.ctx);
         }
      }

      this.destroyPools();
   }

   private void destroyPools() {
      if(this.aprPool != 0L) {
         Pool.destroy(this.aprPool);
      }

   }

   static {
      List<String> ciphers = new ArrayList();
      Collections.addAll(ciphers, new String[]{"ECDHE-RSA-AES128-GCM-SHA256", "ECDHE-RSA-RC4-SHA", "ECDHE-RSA-AES128-SHA", "ECDHE-RSA-AES256-SHA", "AES128-GCM-SHA256", "RC4-SHA", "RC4-MD5", "AES128-SHA", "AES256-SHA", "DES-CBC3-SHA"});
      DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
      if(logger.isDebugEnabled()) {
         logger.debug("Default cipher suite (OpenSSL): " + ciphers);
      }

   }
}
