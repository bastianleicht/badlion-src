package com.ibm.icu.text;

import java.text.CharacterIterator;

abstract class DictionaryMatcher {
   public abstract int matches(CharacterIterator var1, int var2, int[] var3, int[] var4, int var5, int[] var6);

   public int matches(CharacterIterator text, int maxLength, int[] lengths, int[] count, int limit) {
      return this.matches(text, maxLength, lengths, count, limit, (int[])null);
   }

   public abstract int getType();
}
