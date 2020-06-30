package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
   private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
   private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);

   static ByteBuf[] readCertificates(File file) throws CertificateException {
      String content;
      try {
         content = readContent(file);
      } catch (IOException var7) {
         throw new CertificateException("failed to read a file: " + file, var7);
      }

      List<ByteBuf> certs = new ArrayList();
      Matcher m = CERT_PATTERN.matcher(content);

      for(int start = 0; m.find(start); start = m.end()) {
         ByteBuf base64 = Unpooled.copiedBuffer((CharSequence)m.group(1), CharsetUtil.US_ASCII);
         ByteBuf der = Base64.decode(base64);
         base64.release();
         certs.add(der);
      }

      if(certs.isEmpty()) {
         throw new CertificateException("found no certificates: " + file);
      } else {
         return (ByteBuf[])certs.toArray(new ByteBuf[certs.size()]);
      }
   }

   static ByteBuf readPrivateKey(File file) throws KeyException {
      String content;
      try {
         content = readContent(file);
      } catch (IOException var5) {
         throw new KeyException("failed to read a file: " + file, var5);
      }

      Matcher m = KEY_PATTERN.matcher(content);
      if(!m.find()) {
         throw new KeyException("found no private key: " + file);
      } else {
         ByteBuf base64 = Unpooled.copiedBuffer((CharSequence)m.group(1), CharsetUtil.US_ASCII);
         ByteBuf der = Base64.decode(base64);
         base64.release();
         return der;
      }
   }

   private static String readContent(File file) throws IOException {
      InputStream in = new FileInputStream(file);
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try {
         byte[] buf = new byte[8192];

         while(true) {
            int ret = in.read(buf);
            if(ret < 0) {
               String var8 = out.toString(CharsetUtil.US_ASCII.name());
               return var8;
            }

            out.write(buf, 0, ret);
         }
      } finally {
         safeClose(in);
         safeClose((OutputStream)out);
      }
   }

   private static void safeClose(InputStream in) {
      try {
         in.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", (Throwable)var2);
      }

   }

   private static void safeClose(OutputStream out) {
      try {
         out.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", (Throwable)var2);
      }

   }
}
