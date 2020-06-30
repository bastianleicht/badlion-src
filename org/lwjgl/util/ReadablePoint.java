package org.lwjgl.util;

import org.lwjgl.util.WritablePoint;

public interface ReadablePoint {
   int getX();

   int getY();

   void getLocation(WritablePoint var1);
}
