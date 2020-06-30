package org.apache.commons.compress.archivers.sevenz;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.archivers.sevenz.AES256SHA256Decoder;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;
import org.apache.commons.compress.archivers.sevenz.DeltaDecoder;
import org.apache.commons.compress.archivers.sevenz.LZMA2Decoder;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.tukaani.xz.ARMOptions;
import org.tukaani.xz.ARMThumbOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.FinishableOutputStream;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.IA64Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.PowerPCOptions;
import org.tukaani.xz.SPARCOptions;
import org.tukaani.xz.X86Options;

class Coders {
   private static final Map CODER_MAP = new HashMap() {
      private static final long serialVersionUID = 1664829131806520867L;

      {
         this.put(SevenZMethod.COPY, new Coders.CopyDecoder());
         this.put(SevenZMethod.LZMA, new Coders.LZMADecoder());
         this.put(SevenZMethod.LZMA2, new LZMA2Decoder());
         this.put(SevenZMethod.DEFLATE, new Coders.DeflateDecoder());
         this.put(SevenZMethod.BZIP2, new Coders.BZIP2Decoder());
         this.put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
         this.put(SevenZMethod.BCJ_X86_FILTER, new Coders.BCJDecoder(new X86Options()));
         this.put(SevenZMethod.BCJ_PPC_FILTER, new Coders.BCJDecoder(new PowerPCOptions()));
         this.put(SevenZMethod.BCJ_IA64_FILTER, new Coders.BCJDecoder(new IA64Options()));
         this.put(SevenZMethod.BCJ_ARM_FILTER, new Coders.BCJDecoder(new ARMOptions()));
         this.put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new Coders.BCJDecoder(new ARMThumbOptions()));
         this.put(SevenZMethod.BCJ_SPARC_FILTER, new Coders.BCJDecoder(new SPARCOptions()));
         this.put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
      }
   };

   static CoderBase findByMethod(SevenZMethod method) {
      return (CoderBase)CODER_MAP.get(method);
   }

   static InputStream addDecoder(InputStream is, Coder coder, byte[] password) throws IOException {
      CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
      if(cb == null) {
         throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId));
      } else {
         return cb.decode(is, coder, password);
      }
   }

   static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
      CoderBase cb = findByMethod(method);
      if(cb == null) {
         throw new IOException("Unsupported compression method " + method);
      } else {
         return cb.encode(out, options);
      }
   }

   static class BCJDecoder extends CoderBase {
      private final FilterOptions opts;

      BCJDecoder(FilterOptions opts) {
         super(new Class[0]);
         this.opts = opts;
      }

      InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
         try {
            return this.opts.getInputStream(in);
         } catch (AssertionError var6) {
            IOException ex = new IOException("BCJ filter needs XZ for Java > 1.4 - see http://commons.apache.org/proper/commons-compress/limitations.html#7Z");
            ex.initCause(var6);
            throw ex;
         }
      }

      OutputStream encode(OutputStream out, Object options) {
         final FinishableOutputStream fo = this.opts.getOutputStream(new FinishableWrapperOutputStream(out));
         return new FilterOutputStream(fo) {
            public void flush() {
            }
         };
      }
   }

   static class BZIP2Decoder extends CoderBase {
      BZIP2Decoder() {
         super(new Class[]{Number.class});
      }

      InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
         return new BZip2CompressorInputStream(in);
      }

      OutputStream encode(OutputStream out, Object options) throws IOException {
         int blockSize = numberOptionOrDefault(options, 9);
         return new BZip2CompressorOutputStream(out, blockSize);
      }
   }

   static class CopyDecoder extends CoderBase {
      CopyDecoder() {
         super(new Class[0]);
      }

      InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
         return in;
      }

      OutputStream encode(OutputStream out, Object options) {
         return out;
      }
   }

   static class DeflateDecoder extends CoderBase {
      DeflateDecoder() {
         super(new Class[]{Number.class});
      }

      InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
         return new InflaterInputStream(new Coders.DummyByteAddingInputStream(in), new Inflater(true));
      }

      OutputStream encode(OutputStream out, Object options) {
         int level = numberOptionOrDefault(options, 9);
         return new DeflaterOutputStream(out, new Deflater(level, true));
      }
   }

   private static class DummyByteAddingInputStream extends FilterInputStream {
      private boolean addDummyByte;

      private DummyByteAddingInputStream(InputStream in) {
         super(in);
         this.addDummyByte = true;
      }

      public int read() throws IOException {
         int result = super.read();
         if(result == -1 && this.addDummyByte) {
            this.addDummyByte = false;
            result = 0;
         }

         return result;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         int result = super.read(b, off, len);
         if(result == -1 && this.addDummyByte) {
            this.addDummyByte = false;
            b[off] = 0;
            return 1;
         } else {
            return result;
         }
      }
   }

   static class LZMADecoder extends CoderBase {
      LZMADecoder() {
         super(new Class[0]);
      }

      InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
         byte propsByte = coder.properties[0];
         long dictSize = (long)coder.properties[1];

         for(int i = 1; i < 4; ++i) {
            dictSize |= ((long)coder.properties[i + 1] & 255L) << 8 * i;
         }

         if(dictSize > 2147483632L) {
            throw new IOException("Dictionary larger than 4GiB maximum size");
         } else {
            return new LZMAInputStream(in, -1L, propsByte, (int)dictSize);
         }
      }
   }
}
