package com.ibm.icu.text;

import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetRecog_2022;
import com.ibm.icu.text.CharsetRecog_UTF8;
import com.ibm.icu.text.CharsetRecog_Unicode;
import com.ibm.icu.text.CharsetRecog_mbcs;
import com.ibm.icu.text.CharsetRecog_sbcs;
import com.ibm.icu.text.CharsetRecognizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CharsetDetector {
   private static final int kBufSize = 8000;
   byte[] fInputBytes = new byte[8000];
   int fInputLen;
   short[] fByteStats = new short[256];
   boolean fC1Bytes = false;
   String fDeclaredEncoding;
   byte[] fRawInput;
   int fRawLength;
   InputStream fInputStream;
   boolean fStripTags = false;
   private static ArrayList fCSRecognizers = createRecognizers();
   private static String[] fCharsetNames;

   public CharsetDetector setDeclaredEncoding(String encoding) {
      this.fDeclaredEncoding = encoding;
      return this;
   }

   public CharsetDetector setText(byte[] in) {
      this.fRawInput = in;
      this.fRawLength = in.length;
      return this;
   }

   public CharsetDetector setText(InputStream in) throws IOException {
      this.fInputStream = in;
      this.fInputStream.mark(8000);
      this.fRawInput = new byte[8000];
      this.fRawLength = 0;

      int bytesRead;
      for(int remainingLength = 8000; remainingLength > 0; remainingLength -= bytesRead) {
         bytesRead = this.fInputStream.read(this.fRawInput, this.fRawLength, remainingLength);
         if(bytesRead <= 0) {
            break;
         }

         this.fRawLength += bytesRead;
      }

      this.fInputStream.reset();
      return this;
   }

   public CharsetMatch detect() {
      CharsetMatch[] matches = this.detectAll();
      return matches != null && matches.length != 0?matches[0]:null;
   }

   public CharsetMatch[] detectAll() {
      ArrayList<CharsetMatch> matches = new ArrayList();
      this.MungeInput();

      for(CharsetRecognizer csr : fCSRecognizers) {
         CharsetMatch m = csr.match(this);
         if(m != null) {
            matches.add(m);
         }
      }

      Collections.sort(matches);
      Collections.reverse(matches);
      CharsetMatch[] resultArray = new CharsetMatch[matches.size()];
      resultArray = (CharsetMatch[])matches.toArray(resultArray);
      return resultArray;
   }

   public Reader getReader(InputStream in, String declaredEncoding) {
      this.fDeclaredEncoding = declaredEncoding;

      try {
         this.setText(in);
         CharsetMatch match = this.detect();
         return match == null?null:match.getReader();
      } catch (IOException var4) {
         return null;
      }
   }

   public String getString(byte[] in, String declaredEncoding) {
      this.fDeclaredEncoding = declaredEncoding;

      try {
         this.setText(in);
         CharsetMatch match = this.detect();
         return match == null?null:match.getString(-1);
      } catch (IOException var4) {
         return null;
      }
   }

   public static String[] getAllDetectableCharsets() {
      return fCharsetNames;
   }

   public boolean inputFilterEnabled() {
      return this.fStripTags;
   }

   public boolean enableInputFilter(boolean filter) {
      boolean previous = this.fStripTags;
      this.fStripTags = filter;
      return previous;
   }

   private void MungeInput() {
      int srci = 0;
      int dsti = 0;
      boolean inMarkup = false;
      int openTags = 0;
      int badTags = 0;
      if(this.fStripTags) {
         for(srci = 0; srci < this.fRawLength && dsti < this.fInputBytes.length; ++srci) {
            byte b = this.fRawInput[srci];
            if(b == 60) {
               if(inMarkup) {
                  ++badTags;
               }

               inMarkup = true;
               ++openTags;
            }

            if(!inMarkup) {
               this.fInputBytes[dsti++] = b;
            }

            if(b == 62) {
               inMarkup = false;
            }
         }

         this.fInputLen = dsti;
      }

      if(openTags < 5 || openTags / 5 < badTags || this.fInputLen < 100 && this.fRawLength > 600) {
         int limit = this.fRawLength;
         if(limit > 8000) {
            limit = 8000;
         }

         for(srci = 0; srci < limit; ++srci) {
            this.fInputBytes[srci] = this.fRawInput[srci];
         }

         this.fInputLen = srci;
      }

      Arrays.fill(this.fByteStats, (short)0);

      for(srci = 0; srci < this.fInputLen; ++srci) {
         int val = this.fInputBytes[srci] & 255;
         ++this.fByteStats[val];
      }

      this.fC1Bytes = false;

      for(int i = 128; i <= 159; ++i) {
         if(this.fByteStats[i] != 0) {
            this.fC1Bytes = true;
            break;
         }
      }

   }

   private static ArrayList createRecognizers() {
      ArrayList<CharsetRecognizer> recognizers = new ArrayList();
      recognizers.add(new CharsetRecog_UTF8());
      recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_BE());
      recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_LE());
      recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_BE());
      recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_LE());
      recognizers.add(new CharsetRecog_mbcs.CharsetRecog_sjis());
      recognizers.add(new CharsetRecog_2022.CharsetRecog_2022JP());
      recognizers.add(new CharsetRecog_2022.CharsetRecog_2022CN());
      recognizers.add(new CharsetRecog_2022.CharsetRecog_2022KR());
      recognizers.add(new CharsetRecog_mbcs.CharsetRecog_gb_18030());
      recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_jp());
      recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_kr());
      recognizers.add(new CharsetRecog_mbcs.CharsetRecog_big5());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_5_ru());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_6_ar());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_7_el());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_I_he());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_he());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1251());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1256());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_KOI8_R());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_9_tr());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_rtl());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM424_he_ltr());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_rtl());
      recognizers.add(new CharsetRecog_sbcs.CharsetRecog_IBM420_ar_ltr());
      String[] charsetNames = new String[recognizers.size()];
      int out = 0;

      for(int i = 0; i < recognizers.size(); ++i) {
         String name = ((CharsetRecognizer)recognizers.get(i)).getName();
         if(out == 0 || !name.equals(charsetNames[out - 1])) {
            charsetNames[out++] = name;
         }
      }

      fCharsetNames = new String[out];
      System.arraycopy(charsetNames, 0, fCharsetNames, 0, out);
      return recognizers;
   }
}
