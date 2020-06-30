package com.ibm.icu.impl.duration.impl;

interface RecordWriter {
   boolean open(String var1);

   boolean close();

   void bool(String var1, boolean var2);

   void boolArray(String var1, boolean[] var2);

   void character(String var1, char var2);

   void characterArray(String var1, char[] var2);

   void namedIndex(String var1, String[] var2, int var3);

   void namedIndexArray(String var1, String[] var2, byte[] var3);

   void string(String var1, String var2);

   void stringArray(String var1, String[] var2);

   void stringTable(String var1, String[][] var2);
}
