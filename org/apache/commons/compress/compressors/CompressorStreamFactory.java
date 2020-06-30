package org.apache.commons.compress.compressors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZUtils;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class CompressorStreamFactory {
   public static final String BZIP2 = "bzip2";
   public static final String GZIP = "gz";
   public static final String PACK200 = "pack200";
   public static final String XZ = "xz";
   public static final String LZMA = "lzma";
   public static final String SNAPPY_FRAMED = "snappy-framed";
   public static final String SNAPPY_RAW = "snappy-raw";
   public static final String Z = "z";
   private boolean decompressConcatenated = false;

   public void setDecompressConcatenated(boolean decompressConcatenated) {
      this.decompressConcatenated = decompressConcatenated;
   }

   public CompressorInputStream createCompressorInputStream(InputStream in) throws CompressorException {
      if(in == null) {
         throw new IllegalArgumentException("Stream must not be null.");
      } else if(!in.markSupported()) {
         throw new IllegalArgumentException("Mark is not supported.");
      } else {
         byte[] signature = new byte[12];
         in.mark(signature.length);

         try {
            int signatureLength = IOUtils.readFully(in, signature);
            in.reset();
            if(BZip2CompressorInputStream.matches(signature, signatureLength)) {
               return new BZip2CompressorInputStream(in, this.decompressConcatenated);
            }

            if(GzipCompressorInputStream.matches(signature, signatureLength)) {
               return new GzipCompressorInputStream(in, this.decompressConcatenated);
            }

            if(XZUtils.isXZCompressionAvailable() && XZCompressorInputStream.matches(signature, signatureLength)) {
               return new XZCompressorInputStream(in, this.decompressConcatenated);
            }

            if(Pack200CompressorInputStream.matches(signature, signatureLength)) {
               return new Pack200CompressorInputStream(in);
            }

            if(FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
               return new FramedSnappyCompressorInputStream(in);
            }

            if(ZCompressorInputStream.matches(signature, signatureLength)) {
               return new ZCompressorInputStream(in);
            }
         } catch (IOException var4) {
            throw new CompressorException("Failed to detect Compressor from InputStream.", var4);
         }

         throw new CompressorException("No Compressor found for the stream signature.");
      }
   }

   public CompressorInputStream createCompressorInputStream(String name, InputStream in) throws CompressorException {
      if(name != null && in != null) {
         try {
            if("gz".equalsIgnoreCase(name)) {
               return new GzipCompressorInputStream(in, this.decompressConcatenated);
            }

            if("bzip2".equalsIgnoreCase(name)) {
               return new BZip2CompressorInputStream(in, this.decompressConcatenated);
            }

            if("xz".equalsIgnoreCase(name)) {
               return new XZCompressorInputStream(in, this.decompressConcatenated);
            }

            if("lzma".equalsIgnoreCase(name)) {
               return new LZMACompressorInputStream(in);
            }

            if("pack200".equalsIgnoreCase(name)) {
               return new Pack200CompressorInputStream(in);
            }

            if("snappy-raw".equalsIgnoreCase(name)) {
               return new SnappyCompressorInputStream(in);
            }

            if("snappy-framed".equalsIgnoreCase(name)) {
               return new FramedSnappyCompressorInputStream(in);
            }

            if("z".equalsIgnoreCase(name)) {
               return new ZCompressorInputStream(in);
            }
         } catch (IOException var4) {
            throw new CompressorException("Could not create CompressorInputStream.", var4);
         }

         throw new CompressorException("Compressor: " + name + " not found.");
      } else {
         throw new IllegalArgumentException("Compressor name and stream must not be null.");
      }
   }

   public CompressorOutputStream createCompressorOutputStream(String name, OutputStream out) throws CompressorException {
      if(name != null && out != null) {
         try {
            if("gz".equalsIgnoreCase(name)) {
               return new GzipCompressorOutputStream(out);
            }

            if("bzip2".equalsIgnoreCase(name)) {
               return new BZip2CompressorOutputStream(out);
            }

            if("xz".equalsIgnoreCase(name)) {
               return new XZCompressorOutputStream(out);
            }

            if("pack200".equalsIgnoreCase(name)) {
               return new Pack200CompressorOutputStream(out);
            }
         } catch (IOException var4) {
            throw new CompressorException("Could not create CompressorOutputStream", var4);
         }

         throw new CompressorException("Compressor: " + name + " not found.");
      } else {
         throw new IllegalArgumentException("Compressor name and stream must not be null.");
      }
   }
}
