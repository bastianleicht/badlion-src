package com.ibm.icu.util;

import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.StringTrieBuilder;
import java.nio.CharBuffer;

public final class CharsTrieBuilder extends StringTrieBuilder {
   private final char[] intUnits = new char[3];
   private char[] chars;
   private int charsLength;

   public CharsTrieBuilder add(CharSequence s, int value) {
      this.addImpl(s, value);
      return this;
   }

   public CharsTrie build(StringTrieBuilder.Option buildOption) {
      return new CharsTrie(this.buildCharSequence(buildOption), 0);
   }

   public CharSequence buildCharSequence(StringTrieBuilder.Option buildOption) {
      this.buildChars(buildOption);
      return CharBuffer.wrap(this.chars, this.chars.length - this.charsLength, this.charsLength);
   }

   private void buildChars(StringTrieBuilder.Option buildOption) {
      if(this.chars == null) {
         this.chars = new char[1024];
      }

      this.buildImpl(buildOption);
   }

   public CharsTrieBuilder clear() {
      this.clearImpl();
      this.chars = null;
      this.charsLength = 0;
      return this;
   }

   /** @deprecated */
   protected boolean matchNodesCanHaveValues() {
      return true;
   }

   /** @deprecated */
   protected int getMaxBranchLinearSubNodeLength() {
      return 5;
   }

   /** @deprecated */
   protected int getMinLinearMatch() {
      return 48;
   }

   /** @deprecated */
   protected int getMaxLinearMatchLength() {
      return 16;
   }

   private void ensureCapacity(int length) {
      if(length > this.chars.length) {
         int newCapacity = this.chars.length;

         while(true) {
            newCapacity *= 2;
            if(newCapacity > length) {
               break;
            }
         }

         char[] newChars = new char[newCapacity];
         System.arraycopy(this.chars, this.chars.length - this.charsLength, newChars, newChars.length - this.charsLength, this.charsLength);
         this.chars = newChars;
      }

   }

   /** @deprecated */
   protected int write(int unit) {
      int newLength = this.charsLength + 1;
      this.ensureCapacity(newLength);
      this.charsLength = newLength;
      this.chars[this.chars.length - this.charsLength] = (char)unit;
      return this.charsLength;
   }

   /** @deprecated */
   protected int write(int offset, int length) {
      int newLength = this.charsLength + length;
      this.ensureCapacity(newLength);
      this.charsLength = newLength;

      for(int charsOffset = this.chars.length - this.charsLength; length > 0; --length) {
         this.chars[charsOffset++] = this.strings.charAt(offset++);
      }

      return this.charsLength;
   }

   private int write(char[] s, int length) {
      int newLength = this.charsLength + length;
      this.ensureCapacity(newLength);
      this.charsLength = newLength;
      System.arraycopy(s, 0, this.chars, this.chars.length - this.charsLength, length);
      return this.charsLength;
   }

   /** @deprecated */
   protected int writeValueAndFinal(int i, boolean isFinal) {
      if(0 <= i && i <= 16383) {
         return this.write(i | (isFinal?'耀':0));
      } else {
         int length;
         if(i >= 0 && i <= 1073676287) {
            this.intUnits[0] = (char)(16384 + (i >> 16));
            this.intUnits[1] = (char)i;
            length = 2;
         } else {
            this.intUnits[0] = 32767;
            this.intUnits[1] = (char)(i >> 16);
            this.intUnits[2] = (char)i;
            length = 3;
         }

         this.intUnits[0] = (char)(this.intUnits[0] | (isFinal?'耀':0));
         return this.write(this.intUnits, length);
      }
   }

   /** @deprecated */
   protected int writeValueAndType(boolean hasValue, int value, int node) {
      if(!hasValue) {
         return this.write(node);
      } else {
         int length;
         if(value >= 0 && value <= 16646143) {
            if(value <= 255) {
               this.intUnits[0] = (char)(value + 1 << 6);
               length = 1;
            } else {
               this.intUnits[0] = (char)(16448 + (value >> 10 & 32704));
               this.intUnits[1] = (char)value;
               length = 2;
            }
         } else {
            this.intUnits[0] = 32704;
            this.intUnits[1] = (char)(value >> 16);
            this.intUnits[2] = (char)value;
            length = 3;
         }

         this.intUnits[0] |= (char)node;
         return this.write(this.intUnits, length);
      }
   }

   /** @deprecated */
   protected int writeDeltaTo(int jumpTarget) {
      int i = this.charsLength - jumpTarget;

      assert i >= 0;

      if(i <= 'ﯿ') {
         return this.write(i);
      } else {
         int length;
         if(i <= 67043327) {
            this.intUnits[0] = (char)('ﰀ' + (i >> 16));
            length = 1;
         } else {
            this.intUnits[0] = '\uffff';
            this.intUnits[1] = (char)(i >> 16);
            length = 2;
         }

         this.intUnits[length++] = (char)i;
         return this.write(this.intUnits, length);
      }
   }
}
