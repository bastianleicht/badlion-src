package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class DemuxInputStream extends InputStream {
   private final InheritableThreadLocal m_streams = new InheritableThreadLocal();

   public InputStream bindStream(InputStream input) {
      InputStream oldValue = (InputStream)this.m_streams.get();
      this.m_streams.set(input);
      return oldValue;
   }

   public void close() throws IOException {
      InputStream input = (InputStream)this.m_streams.get();
      if(null != input) {
         input.close();
      }

   }

   public int read() throws IOException {
      InputStream input = (InputStream)this.m_streams.get();
      return null != input?input.read():-1;
   }
}
