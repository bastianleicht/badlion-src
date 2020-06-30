package io.netty.handler.ssl.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.ssl.util.BouncyCastleSelfSignedCertGenerator;
import io.netty.handler.ssl.util.OpenJdkSelfSignedCertGenerator;
import io.netty.handler.ssl.util.ThreadLocalInsecureRandom;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class SelfSignedCertificate {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
   static final Date NOT_BEFORE = new Date(System.currentTimeMillis() - 31536000000L);
   static final Date NOT_AFTER = new Date(253402300799000L);
   private final File certificate;
   private final File privateKey;

   public SelfSignedCertificate() throws CertificateException {
      this("example.com");
   }

   public SelfSignedCertificate(String fqdn) throws CertificateException {
      this(fqdn, ThreadLocalInsecureRandom.current(), 1024);
   }

   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
      KeyPair keypair;
      try {
         KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
         keyGen.initialize(bits, random);
         keypair = keyGen.generateKeyPair();
      } catch (NoSuchAlgorithmException var10) {
         throw new Error(var10);
      }

      String[] paths;
      try {
         paths = OpenJdkSelfSignedCertGenerator.generate(fqdn, keypair, random);
      } catch (Throwable var9) {
         logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", var9);

         try {
            paths = BouncyCastleSelfSignedCertGenerator.generate(fqdn, keypair, random);
         } catch (Throwable var8) {
            logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", var8);
            throw new CertificateException("No provider succeeded to generate a self-signed certificate. See debug log for the root cause.");
         }
      }

      this.certificate = new File(paths[0]);
      this.privateKey = new File(paths[1]);
   }

   public File certificate() {
      return this.certificate;
   }

   public File privateKey() {
      return this.privateKey;
   }

   public void delete() {
      safeDelete(this.certificate);
      safeDelete(this.privateKey);
   }

   static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
      String keyText = "-----BEGIN PRIVATE KEY-----\n" + Base64.encode(Unpooled.wrappedBuffer(key.getEncoded()), true).toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
      File keyFile = File.createTempFile("keyutil_" + fqdn + '_', ".key");
      keyFile.deleteOnExit();
      OutputStream keyOut = new FileOutputStream(keyFile);

      try {
         keyOut.write(keyText.getBytes(CharsetUtil.US_ASCII));
         keyOut.close();
         keyOut = null;
      } finally {
         if(keyOut != null) {
            safeClose(keyFile, keyOut);
            safeDelete(keyFile);
         }

      }

      String certText = "-----BEGIN CERTIFICATE-----\n" + Base64.encode(Unpooled.wrappedBuffer(cert.getEncoded()), true).toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
      File certFile = File.createTempFile("keyutil_" + fqdn + '_', ".crt");
      certFile.deleteOnExit();
      OutputStream certOut = new FileOutputStream(certFile);

      try {
         certOut.write(certText.getBytes(CharsetUtil.US_ASCII));
         certOut.close();
         certOut = null;
      } finally {
         if(certOut != null) {
            safeClose(certFile, certOut);
            safeDelete(certFile);
            safeDelete(keyFile);
         }

      }

      return new String[]{certFile.getPath(), keyFile.getPath()};
   }

   private static void safeDelete(File certFile) {
      if(!certFile.delete()) {
         logger.warn("Failed to delete a file: " + certFile);
      }

   }

   private static void safeClose(File keyFile, OutputStream keyOut) {
      try {
         keyOut.close();
      } catch (IOException var3) {
         logger.warn("Failed to close a file: " + keyFile, (Throwable)var3);
      }

   }
}
