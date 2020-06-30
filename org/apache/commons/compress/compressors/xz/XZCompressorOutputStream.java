package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

public class XZCompressorOutputStream extends CompressorOutputStream {
   private final XZOutputStream out;

   public XZCompressorOutputStream(OutputStream outputStream) throws IOException {
      this.out = new XZOutputStream(outputStream, new LZMA2Options());
   }

   public XZCompressorOutputStream(OutputStream outputStream, int preset) throws IOException {
      this.out = new XZOutputStream(outputStream, new LZMA2Options(preset));
   }

   public void write(int b) throws IOException {
      this.out.write(b);
   }

   public void write(byte[] buf, int off, int len) throws IOException {
      this.out.write(buf, off, len);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void finish() throws IOException {
      this.out.finish();
   }

   public void close() throws IOException {
      this.out.close();
   }
}
