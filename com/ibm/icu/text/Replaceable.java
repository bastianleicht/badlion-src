package com.ibm.icu.text;

public interface Replaceable {
   int length();

   char charAt(int var1);

   int char32At(int var1);

   void getChars(int var1, int var2, char[] var3, int var4);

   void replace(int var1, int var2, String var3);

   void replace(int var1, int var2, char[] var3, int var4, int var5);

   void copy(int var1, int var2, int var3);

   boolean hasMetaData();
}
