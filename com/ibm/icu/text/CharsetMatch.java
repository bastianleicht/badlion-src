package com.ibm.icu.text;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetRecognizer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CharsetMatch implements Comparable {
   private int fConfidence;
   private byte[] fRawInput = null;
   private int fRawLength;
   private InputStream fInputStream = null;
   private String fCharsetName;
   private String fLang;

   public Reader getReader() {
      InputStream inputStream = this.fInputStream;
      if(inputStream == null) {
         inputStream = new ByteArrayInputStream(this.fRawInput, 0, this.fRawLength);
      }

      try {
         inputStream.reset();
         return new InputStreamReader(inputStream, this.getName());
      } catch (IOException var3) {
         return null;
      }
   }

   public String getString() throws IOException {
      return this.getString(-1);
   }

   public String getString(int maxLength) throws IOException {
      String result = null;
      if(this.fInputStream == null) {
         String name = this.getName();
         int startSuffix = name.indexOf("_rtl") < 0?name.indexOf("_ltr"):name.indexOf("_rtl");
         if(startSuffix > 0) {
            name = name.substring(0, startSuffix);
         }

         result = new String(this.fRawInput, name);
         return result;
      } else {
         StringBuilder sb = new StringBuilder();
         char[] buffer = new char[1024];
         Reader reader = this.getReader();
         int max = maxLength < 0?Integer.MAX_VALUE:maxLength;

         int var11;
         for(bytesRead = 0; (var11 = reader.read(buffer, 0, Math.min(max, 1024))) >= 0; max -= var11) {
            sb.append(buffer, 0, var11);
         }

         reader.close();
         return sb.toString();
      }
   }

   public int getConfidence() {
      return this.fConfidence;
   }

   public String getName() {
      return this.fCharsetName;
   }

   public String getLanguage() {
      return this.fLang;
   }

   public int compareTo(CharsetMatch other) {
      int compareResult = 0;
      if(this.fConfidence > other.fConfidence) {
         compareResult = 1;
      } else if(this.fConfidence < other.fConfidence) {
         compareResult = -1;
      }

      return compareResult;
   }

   CharsetMatch(CharsetDetector det, CharsetRecognizer rec, int conf) {
      this.fConfidence = conf;
      if(det.fInputStream == null) {
         this.fRawInput = det.fRawInput;
         this.fRawLength = det.fRawLength;
      }

      this.fInputStream = det.fInputStream;
      this.fCharsetName = rec.getName();
      this.fLang = rec.getLanguage();
   }

   CharsetMatch(CharsetDetector det, CharsetRecognizer rec, int conf, String csName, String lang) {
      this.fConfidence = conf;
      if(det.fInputStream == null) {
         this.fRawInput = det.fRawInput;
         this.fRawLength = det.fRawLength;
      }

      this.fInputStream = det.fInputStream;
      this.fCharsetName = csName;
      this.fLang = lang;
   }
}
