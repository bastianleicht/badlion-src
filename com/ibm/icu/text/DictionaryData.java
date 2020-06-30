package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.BytesDictionaryMatcher;
import com.ibm.icu.text.CharsDictionaryMatcher;
import com.ibm.icu.text.DictionaryMatcher;
import com.ibm.icu.util.UResourceBundle;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class DictionaryData {
   public static final int TRIE_TYPE_BYTES = 0;
   public static final int TRIE_TYPE_UCHARS = 1;
   public static final int TRIE_TYPE_MASK = 7;
   public static final int TRIE_HAS_VALUES = 8;
   public static final int TRANSFORM_NONE = 0;
   public static final int TRANSFORM_TYPE_OFFSET = 16777216;
   public static final int TRANSFORM_TYPE_MASK = 2130706432;
   public static final int TRANSFORM_OFFSET_MASK = 2097151;
   public static final int IX_STRING_TRIE_OFFSET = 0;
   public static final int IX_RESERVED1_OFFSET = 1;
   public static final int IX_RESERVED2_OFFSET = 2;
   public static final int IX_TOTAL_SIZE = 3;
   public static final int IX_TRIE_TYPE = 4;
   public static final int IX_TRANSFORM = 5;
   public static final int IX_RESERVED6 = 6;
   public static final int IX_RESERVED7 = 7;
   public static final int IX_COUNT = 8;
   private static final byte[] DATA_FORMAT_ID = new byte[]{(byte)68, (byte)105, (byte)99, (byte)116};

   public static DictionaryMatcher loadDictionaryFor(String dictType) throws IOException {
      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/brkitr");
      String dictFileName = rb.getStringWithFallback("dictionaries/" + dictType);
      dictFileName = "data/icudt51b/brkitr/" + dictFileName;
      InputStream is = ICUData.getStream(dictFileName);
      ICUBinary.readHeader(is, DATA_FORMAT_ID, (ICUBinary.Authenticate)null);
      DataInputStream s = new DataInputStream(is);
      int[] indexes = new int[8];

      for(int i = 0; i < 8; ++i) {
         indexes[i] = s.readInt();
      }

      int offset = indexes[0];
      Assert.assrt(offset >= 32);
      if(offset > 32) {
         int diff = offset - 32;
         s.skipBytes(diff);
      }

      int trieType = indexes[4] & 7;
      int totalSize = indexes[3] - offset;
      DictionaryMatcher m = null;
      if(trieType == 0) {
         int transform = indexes[5];
         byte[] data = new byte[totalSize];

         int i;
         for(i = 0; i < data.length; ++i) {
            data[i] = s.readByte();
         }

         Assert.assrt(i == totalSize);
         m = new BytesDictionaryMatcher(data, transform);
      } else if(trieType == 1) {
         Assert.assrt(totalSize % 2 == 0);
         int num = totalSize / 2;
         char[] data = new char[totalSize / 2];

         for(int i = 0; i < num; ++i) {
            data[i] = s.readChar();
         }

         m = new CharsDictionaryMatcher(new String(data));
      } else {
         m = null;
      }

      s.close();
      is.close();
      return m;
   }
}
