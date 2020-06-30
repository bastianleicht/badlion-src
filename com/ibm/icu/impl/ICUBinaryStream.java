package com.ibm.icu.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class ICUBinaryStream extends DataInputStream {
   public ICUBinaryStream(InputStream stream, int size) {
      super(stream);
      this.mark(size);
   }

   public ICUBinaryStream(byte[] raw) {
      this(new ByteArrayInputStream(raw), raw.length);
   }

   public void seek(int offset) throws IOException {
      this.reset();
      int actual = this.skipBytes(offset);
      if(actual != offset) {
         throw new IllegalStateException("Skip(" + offset + ") only skipped " + actual + " bytes");
      }
   }
}
