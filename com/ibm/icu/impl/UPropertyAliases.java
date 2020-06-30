package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.ibm.icu.util.BytesTrie;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;

public final class UPropertyAliases {
   private static final int IX_VALUE_MAPS_OFFSET = 0;
   private static final int IX_BYTE_TRIES_OFFSET = 1;
   private static final int IX_NAME_GROUPS_OFFSET = 2;
   private static final int IX_RESERVED3_OFFSET = 3;
   private int[] valueMaps;
   private byte[] bytesTries;
   private String nameGroups;
   private static final UPropertyAliases.IsAcceptable IS_ACCEPTABLE = new UPropertyAliases.IsAcceptable();
   private static final byte[] DATA_FORMAT = new byte[]{(byte)112, (byte)110, (byte)97, (byte)109};
   public static final UPropertyAliases INSTANCE;

   private void load(InputStream data) throws IOException {
      BufferedInputStream bis = new BufferedInputStream(data);
      ICUBinary.readHeader(bis, DATA_FORMAT, IS_ACCEPTABLE);
      DataInputStream ds = new DataInputStream(bis);
      int indexesLength = ds.readInt() / 4;
      if(indexesLength < 8) {
         throw new IOException("pnames.icu: not enough indexes");
      } else {
         int[] inIndexes = new int[indexesLength];
         inIndexes[0] = indexesLength * 4;

         for(int i = 1; i < indexesLength; ++i) {
            inIndexes[i] = ds.readInt();
         }

         int offset = inIndexes[0];
         int nextOffset = inIndexes[1];
         int numInts = (nextOffset - offset) / 4;
         this.valueMaps = new int[numInts];

         for(int i = 0; i < numInts; ++i) {
            this.valueMaps[i] = ds.readInt();
         }

         nextOffset = inIndexes[2];
         int numBytes = nextOffset - nextOffset;
         this.bytesTries = new byte[numBytes];
         ds.readFully(this.bytesTries);
         nextOffset = inIndexes[3];
         numBytes = nextOffset - nextOffset;
         StringBuilder sb = new StringBuilder(numBytes);

         for(int i = 0; i < numBytes; ++i) {
            sb.append((char)ds.readByte());
         }

         this.nameGroups = sb.toString();
         data.close();
      }
   }

   private UPropertyAliases() throws IOException {
      this.load(ICUData.getRequiredStream("data/icudt51b/pnames.icu"));
   }

   private int findProperty(int property) {
      int i = 1;

      for(int numRanges = this.valueMaps[0]; numRanges > 0; --numRanges) {
         int start = this.valueMaps[i];
         int limit = this.valueMaps[i + 1];
         i = i + 2;
         if(property < start) {
            break;
         }

         if(property < limit) {
            return i + (property - start) * 2;
         }

         i = i + (limit - start) * 2;
      }

      return 0;
   }

   private int findPropertyValueNameGroup(int valueMapIndex, int value) {
      if(valueMapIndex == 0) {
         return 0;
      } else {
         ++valueMapIndex;
         int numRanges = this.valueMaps[valueMapIndex++];
         if(numRanges < 16) {
            while(numRanges > 0) {
               int start = this.valueMaps[valueMapIndex];
               int limit = this.valueMaps[valueMapIndex + 1];
               valueMapIndex = valueMapIndex + 2;
               if(value < start) {
                  break;
               }

               if(value < limit) {
                  return this.valueMaps[valueMapIndex + value - start];
               }

               valueMapIndex = valueMapIndex + (limit - start);
               --numRanges;
            }
         } else {
            int valuesStart = valueMapIndex;
            int nameGroupOffsetsStart = valueMapIndex + numRanges - 16;

            while(true) {
               int v = this.valueMaps[valueMapIndex];
               if(value < v) {
                  break;
               }

               if(value == v) {
                  return this.valueMaps[nameGroupOffsetsStart + valueMapIndex - valuesStart];
               }

               ++valueMapIndex;
               if(valueMapIndex >= nameGroupOffsetsStart) {
                  break;
               }
            }
         }

         return 0;
      }
   }

   private String getName(int nameGroupsIndex, int nameIndex) {
      int numNames = this.nameGroups.charAt(nameGroupsIndex++);
      if(nameIndex >= 0 && numNames > nameIndex) {
         while(nameIndex > 0) {
            while(0 != this.nameGroups.charAt(nameGroupsIndex++)) {
               ;
            }

            --nameIndex;
         }

         int nameStart;
         for(nameStart = nameGroupsIndex; 0 != this.nameGroups.charAt(nameGroupsIndex); ++nameGroupsIndex) {
            ;
         }

         if(nameStart == nameGroupsIndex) {
            return null;
         } else {
            return this.nameGroups.substring(nameStart, nameGroupsIndex);
         }
      } else {
         throw new IllegalIcuArgumentException("Invalid property (value) name choice");
      }
   }

   private static int asciiToLowercase(int c) {
      return 65 <= c && c <= 90?c + 32:c;
   }

   private boolean containsName(BytesTrie trie, CharSequence name) {
      BytesTrie.Result result = BytesTrie.Result.NO_VALUE;

      for(int i = 0; i < name.length(); ++i) {
         int c = name.charAt(i);
         if(c != 45 && c != 95 && c != 32 && (9 > c || c > 13)) {
            if(!result.hasNext()) {
               return false;
            }

            c = asciiToLowercase(c);
            result = trie.next(c);
         }
      }

      return result.hasValue();
   }

   public String getPropertyName(int property, int nameChoice) {
      int valueMapIndex = this.findProperty(property);
      if(valueMapIndex == 0) {
         throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
      } else {
         return this.getName(this.valueMaps[valueMapIndex], nameChoice);
      }
   }

   public String getPropertyValueName(int property, int value, int nameChoice) {
      int valueMapIndex = this.findProperty(property);
      if(valueMapIndex == 0) {
         throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
      } else {
         int nameGroupOffset = this.findPropertyValueNameGroup(this.valueMaps[valueMapIndex + 1], value);
         if(nameGroupOffset == 0) {
            throw new IllegalArgumentException("Property " + property + " (0x" + Integer.toHexString(property) + ") does not have named values");
         } else {
            return this.getName(nameGroupOffset, nameChoice);
         }
      }
   }

   private int getPropertyOrValueEnum(int bytesTrieOffset, CharSequence alias) {
      BytesTrie trie = new BytesTrie(this.bytesTries, bytesTrieOffset);
      return this.containsName(trie, alias)?trie.getValue():-1;
   }

   public int getPropertyEnum(CharSequence alias) {
      return this.getPropertyOrValueEnum(0, alias);
   }

   public int getPropertyValueEnum(int property, CharSequence alias) {
      int valueMapIndex = this.findProperty(property);
      if(valueMapIndex == 0) {
         throw new IllegalArgumentException("Invalid property enum " + property + " (0x" + Integer.toHexString(property) + ")");
      } else {
         valueMapIndex = this.valueMaps[valueMapIndex + 1];
         if(valueMapIndex == 0) {
            throw new IllegalArgumentException("Property " + property + " (0x" + Integer.toHexString(property) + ") does not have named values");
         } else {
            return this.getPropertyOrValueEnum(this.valueMaps[valueMapIndex], alias);
         }
      }
   }

   public static int compare(String stra, String strb) {
      int istra = 0;
      int istrb = 0;
      int cstra = 0;
      int cstrb = 0;

      while(true) {
         if(istra < stra.length()) {
            cstra = stra.charAt(istra);
            switch(cstra) {
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 32:
            case 45:
            case 95:
               ++istra;
               continue;
            }
         }

         label117:
         for(; istrb < strb.length(); ++istrb) {
            cstrb = strb.charAt(istrb);
            switch(cstrb) {
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 32:
            case 45:
            case 95:
            default:
               break label117;
            }
         }

         boolean endstra = istra == stra.length();
         boolean endstrb = istrb == strb.length();
         if(endstra) {
            if(endstrb) {
               return 0;
            }

            cstra = 0;
         } else if(endstrb) {
            cstrb = 0;
         }

         int rc = asciiToLowercase(cstra) - asciiToLowercase(cstrb);
         if(rc != 0) {
            return rc;
         }

         ++istra;
         ++istrb;
      }
   }

   static {
      try {
         INSTANCE = new UPropertyAliases();
      } catch (IOException var2) {
         MissingResourceException mre = new MissingResourceException("Could not construct UPropertyAliases. Missing pnames.icu", "", "");
         mre.initCause(var2);
         throw mre;
      }
   }

   private static final class IsAcceptable implements ICUBinary.Authenticate {
      private IsAcceptable() {
      }

      public boolean isDataVersionAcceptable(byte[] version) {
         return version[0] == 2;
      }
   }
}
