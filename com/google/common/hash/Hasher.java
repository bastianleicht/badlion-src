package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.PrimitiveSink;
import java.nio.charset.Charset;

@Beta
public interface Hasher extends PrimitiveSink {
   Hasher putByte(byte var1);

   Hasher putBytes(byte[] var1);

   Hasher putBytes(byte[] var1, int var2, int var3);

   Hasher putShort(short var1);

   Hasher putInt(int var1);

   Hasher putLong(long var1);

   Hasher putFloat(float var1);

   Hasher putDouble(double var1);

   Hasher putBoolean(boolean var1);

   Hasher putChar(char var1);

   Hasher putUnencodedChars(CharSequence var1);

   Hasher putString(CharSequence var1, Charset var2);

   Hasher putObject(Object var1, Funnel var2);

   HashCode hash();
}
