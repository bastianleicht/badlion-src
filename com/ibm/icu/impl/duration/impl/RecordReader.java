package com.ibm.icu.impl.duration.impl;

interface RecordReader {
   boolean open(String var1);

   boolean close();

   boolean bool(String var1);

   boolean[] boolArray(String var1);

   char character(String var1);

   char[] characterArray(String var1);

   byte namedIndex(String var1, String[] var2);

   byte[] namedIndexArray(String var1, String[] var2);

   String string(String var1);

   String[] stringArray(String var1);

   String[][] stringTable(String var1);
}
