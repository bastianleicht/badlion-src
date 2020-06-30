package org.lwjgl.util;

import org.lwjgl.util.ReadablePoint;

public interface WritablePoint {
   void setLocation(int var1, int var2);

   void setLocation(ReadablePoint var1);

   void setX(int var1);

   void setY(int var1);
}
