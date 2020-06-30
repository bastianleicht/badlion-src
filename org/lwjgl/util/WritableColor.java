package org.lwjgl.util;

import java.nio.ByteBuffer;
import org.lwjgl.util.ReadableColor;

public interface WritableColor {
   void set(int var1, int var2, int var3, int var4);

   void set(byte var1, byte var2, byte var3, byte var4);

   void set(int var1, int var2, int var3);

   void set(byte var1, byte var2, byte var3);

   void setRed(int var1);

   void setGreen(int var1);

   void setBlue(int var1);

   void setAlpha(int var1);

   void setRed(byte var1);

   void setGreen(byte var1);

   void setBlue(byte var1);

   void setAlpha(byte var1);

   void readRGBA(ByteBuffer var1);

   void readRGB(ByteBuffer var1);

   void readARGB(ByteBuffer var1);

   void readBGRA(ByteBuffer var1);

   void readBGR(ByteBuffer var1);

   void readABGR(ByteBuffer var1);

   void setColor(ReadableColor var1);
}
