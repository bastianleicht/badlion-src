package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class ColognePhonetic implements StringEncoder {
   private static final char[] AEIJOUY = new char[]{'A', 'E', 'I', 'J', 'O', 'U', 'Y'};
   private static final char[] SCZ = new char[]{'S', 'C', 'Z'};
   private static final char[] WFPV = new char[]{'W', 'F', 'P', 'V'};
   private static final char[] GKQ = new char[]{'G', 'K', 'Q'};
   private static final char[] CKQ = new char[]{'C', 'K', 'Q'};
   private static final char[] AHKLOQRUX = new char[]{'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X'};
   private static final char[] SZ = new char[]{'S', 'Z'};
   private static final char[] AHOUKQX = new char[]{'A', 'H', 'O', 'U', 'K', 'Q', 'X'};
   private static final char[] TDX = new char[]{'T', 'D', 'X'};
   private static final char[][] PREPROCESS_MAP = new char[][]{{'Ä', 'A'}, {'Ü', 'U'}, {'Ö', 'O'}, {'ß', 'S'}};

   private static boolean arrayContains(char[] arr, char key) {
      for(char element : arr) {
         if(element == key) {
            return true;
         }
      }

      return false;
   }

   public String colognePhonetic(String text) {
      if(text == null) {
         return null;
      } else {
         text = this.preprocess(text);
         ColognePhonetic.CologneOutputBuffer output = new ColognePhonetic.CologneOutputBuffer(text.length() * 2);
         ColognePhonetic.CologneInputBuffer input = new ColognePhonetic.CologneInputBuffer(text.toCharArray());
         char lastChar = 45;
         char lastCode = 47;
         int rightLength = input.length();

         while(true) {
            char code;
            char chr;
            while(true) {
               if(rightLength <= 0) {
                  return output.toString();
               }

               chr = input.removeNext();
               char nextChar;
               if((rightLength = input.length()) > 0) {
                  nextChar = input.getNextChar();
               } else {
                  nextChar = 45;
               }

               if(arrayContains(AEIJOUY, chr)) {
                  code = 48;
                  break;
               }

               if(chr != 72 && chr >= 65 && chr <= 90) {
                  if(chr != 66 && (chr != 80 || nextChar == 72)) {
                     if((chr == 68 || chr == 84) && !arrayContains(SCZ, nextChar)) {
                        code = 50;
                        break;
                     }

                     if(arrayContains(WFPV, chr)) {
                        code = 51;
                        break;
                     }

                     if(arrayContains(GKQ, chr)) {
                        code = 52;
                        break;
                     }

                     if(chr == 88 && !arrayContains(CKQ, lastChar)) {
                        code = 52;
                        input.addLeft('S');
                        ++rightLength;
                        break;
                     }

                     if(chr != 83 && chr != 90) {
                        if(chr == 67) {
                           if(lastCode == 47) {
                              if(arrayContains(AHKLOQRUX, nextChar)) {
                                 code = 52;
                              } else {
                                 code = 56;
                              }
                              break;
                           }

                           if(!arrayContains(SZ, lastChar) && arrayContains(AHOUKQX, nextChar)) {
                              code = 52;
                              break;
                           }

                           code = 56;
                           break;
                        }

                        if(arrayContains(TDX, chr)) {
                           code = 56;
                           break;
                        }

                        if(chr == 82) {
                           code = 55;
                           break;
                        }

                        if(chr == 76) {
                           code = 53;
                           break;
                        }

                        if(chr != 77 && chr != 78) {
                           code = chr;
                           break;
                        }

                        code = 54;
                        break;
                     }

                     code = 56;
                     break;
                  }

                  code = 49;
                  break;
               }

               if(lastCode != 47) {
                  code = 45;
                  break;
               }
            }

            if(code != 45 && (lastCode != code && (code != 48 || lastCode == 47) || code < 48 || code > 56)) {
               output.addRight(code);
            }

            lastChar = chr;
            lastCode = code;
         }
      }
   }

   public Object encode(Object object) throws EncoderException {
      if(!(object instanceof String)) {
         throw new EncoderException("This method\'s parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
      } else {
         return this.encode((String)object);
      }
   }

   public String encode(String text) {
      return this.colognePhonetic(text);
   }

   public boolean isEncodeEqual(String text1, String text2) {
      return this.colognePhonetic(text1).equals(this.colognePhonetic(text2));
   }

   private String preprocess(String text) {
      text = text.toUpperCase(Locale.GERMAN);
      char[] chrs = text.toCharArray();

      for(int index = 0; index < chrs.length; ++index) {
         if(chrs[index] > 90) {
            for(char[] element : PREPROCESS_MAP) {
               if(chrs[index] == element[0]) {
                  chrs[index] = element[1];
                  break;
               }
            }
         }
      }

      return new String(chrs);
   }

   private abstract class CologneBuffer {
      protected final char[] data;
      protected int length = 0;

      public CologneBuffer(char[] data) {
         this.data = data;
         this.length = data.length;
      }

      public CologneBuffer(int buffSize) {
         this.data = new char[buffSize];
         this.length = 0;
      }

      protected abstract char[] copyData(int var1, int var2);

      public int length() {
         return this.length;
      }

      public String toString() {
         return new String(this.copyData(0, this.length));
      }
   }

   private class CologneInputBuffer extends ColognePhonetic.CologneBuffer {
      public CologneInputBuffer(char[] data) {
         super(data);
      }

      public void addLeft(char ch) {
         ++this.length;
         this.data[this.getNextPos()] = ch;
      }

      protected char[] copyData(int start, int length) {
         char[] newData = new char[length];
         System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
         return newData;
      }

      public char getNextChar() {
         return this.data[this.getNextPos()];
      }

      protected int getNextPos() {
         return this.data.length - this.length;
      }

      public char removeNext() {
         char ch = this.getNextChar();
         --this.length;
         return ch;
      }
   }

   private class CologneOutputBuffer extends ColognePhonetic.CologneBuffer {
      public CologneOutputBuffer(int buffSize) {
         super(buffSize);
      }

      public void addRight(char chr) {
         this.data[this.length] = chr;
         ++this.length;
      }

      protected char[] copyData(int start, int length) {
         char[] newData = new char[length];
         System.arraycopy(this.data, start, newData, 0, length);
         return newData;
      }
   }
}
