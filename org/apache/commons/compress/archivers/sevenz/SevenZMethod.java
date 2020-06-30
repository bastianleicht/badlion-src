package org.apache.commons.compress.archivers.sevenz;

import java.util.Arrays;

public enum SevenZMethod {
   COPY(new byte[]{(byte)0}),
   LZMA(new byte[]{(byte)3, (byte)1, (byte)1}),
   LZMA2(new byte[]{(byte)33}),
   DEFLATE(new byte[]{(byte)4, (byte)1, (byte)8}),
   BZIP2(new byte[]{(byte)4, (byte)2, (byte)2}),
   AES256SHA256(new byte[]{(byte)6, (byte)-15, (byte)7, (byte)1}),
   BCJ_X86_FILTER(new byte[]{(byte)3, (byte)3, (byte)1, (byte)3}),
   BCJ_PPC_FILTER(new byte[]{(byte)3, (byte)3, (byte)2, (byte)5}),
   BCJ_IA64_FILTER(new byte[]{(byte)3, (byte)3, (byte)4, (byte)1}),
   BCJ_ARM_FILTER(new byte[]{(byte)3, (byte)3, (byte)5, (byte)1}),
   BCJ_ARM_THUMB_FILTER(new byte[]{(byte)3, (byte)3, (byte)7, (byte)1}),
   BCJ_SPARC_FILTER(new byte[]{(byte)3, (byte)3, (byte)8, (byte)5}),
   DELTA_FILTER(new byte[]{(byte)3});

   private final byte[] id;

   private SevenZMethod(byte[] id) {
      this.id = id;
   }

   byte[] getId() {
      byte[] copy = new byte[this.id.length];
      System.arraycopy(this.id, 0, copy, 0, this.id.length);
      return copy;
   }

   static SevenZMethod byId(byte[] id) {
      for(SevenZMethod m : (SevenZMethod[])SevenZMethod.class.getEnumConstants()) {
         if(Arrays.equals(m.id, id)) {
            return m;
         }
      }

      return null;
   }
}
