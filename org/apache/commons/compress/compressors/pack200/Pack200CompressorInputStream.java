package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.apache.commons.compress.compressors.pack200.StreamBridge;

public class Pack200CompressorInputStream extends CompressorInputStream {
   private final InputStream originalInput;
   private final StreamBridge streamBridge;
   private static final byte[] CAFE_DOOD = new byte[]{(byte)-54, (byte)-2, (byte)-48, (byte)13};
   private static final int SIG_LENGTH = CAFE_DOOD.length;

   public Pack200CompressorInputStream(InputStream in) throws IOException {
      this(in, Pack200Strategy.IN_MEMORY);
   }

   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode) throws IOException {
      this(in, (File)null, mode, (Map)null);
   }

   public Pack200CompressorInputStream(InputStream in, Map props) throws IOException {
      this(in, Pack200Strategy.IN_MEMORY, props);
   }

   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode, Map props) throws IOException {
      this(in, (File)null, mode, props);
   }

   public Pack200CompressorInputStream(File f) throws IOException {
      this(f, Pack200Strategy.IN_MEMORY);
   }

   public Pack200CompressorInputStream(File f, Pack200Strategy mode) throws IOException {
      this((InputStream)null, f, mode, (Map)null);
   }

   public Pack200CompressorInputStream(File f, Map props) throws IOException {
      this(f, Pack200Strategy.IN_MEMORY, props);
   }

   public Pack200CompressorInputStream(File f, Pack200Strategy mode, Map props) throws IOException {
      this((InputStream)null, f, mode, props);
   }

   private Pack200CompressorInputStream(final InputStream in, File f, Pack200Strategy mode, Map props) throws IOException {
      this.originalInput = in;
      this.streamBridge = mode.newStreamBridge();
      JarOutputStream jarOut = new JarOutputStream(this.streamBridge);
      Unpacker u = Pack200.newUnpacker();
      if(props != null) {
         u.properties().putAll(props);
      }

      if(f == null) {
         u.unpack(new FilterInputStream(in) {
            public void close() {
            }
         }, jarOut);
      } else {
         u.unpack(f, jarOut);
      }

      jarOut.close();
   }

   public int read() throws IOException {
      return this.streamBridge.getInput().read();
   }

   public int read(byte[] b) throws IOException {
      return this.streamBridge.getInput().read(b);
   }

   public int read(byte[] b, int off, int count) throws IOException {
      return this.streamBridge.getInput().read(b, off, count);
   }

   public int available() throws IOException {
      return this.streamBridge.getInput().available();
   }

   public boolean markSupported() {
      try {
         return this.streamBridge.getInput().markSupported();
      } catch (IOException var2) {
         return false;
      }
   }

   public void mark(int limit) {
      try {
         this.streamBridge.getInput().mark(limit);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void reset() throws IOException {
      this.streamBridge.getInput().reset();
   }

   public long skip(long count) throws IOException {
      return this.streamBridge.getInput().skip(count);
   }

   public void close() throws IOException {
      try {
         this.streamBridge.stop();
      } finally {
         if(this.originalInput != null) {
            this.originalInput.close();
         }

      }

   }

   public static boolean matches(byte[] signature, int length) {
      if(length < SIG_LENGTH) {
         return false;
      } else {
         for(int i = 0; i < SIG_LENGTH; ++i) {
            if(signature[i] != CAFE_DOOD[i]) {
               return false;
            }
         }

         return true;
      }
   }
}
