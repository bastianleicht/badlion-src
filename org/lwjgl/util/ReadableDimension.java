package org.lwjgl.util;

import org.lwjgl.util.WritableDimension;

public interface ReadableDimension {
   int getWidth();

   int getHeight();

   void getSize(WritableDimension var1);
}
