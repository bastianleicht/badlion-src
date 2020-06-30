package org.lwjgl.util;

import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.WritableRectangle;

public interface ReadableRectangle extends ReadableDimension, ReadablePoint {
   void getBounds(WritableRectangle var1);
}
