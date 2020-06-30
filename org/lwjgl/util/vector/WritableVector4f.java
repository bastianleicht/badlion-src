package org.lwjgl.util.vector;

import org.lwjgl.util.vector.WritableVector3f;

public interface WritableVector4f extends WritableVector3f {
   void setW(float var1);

   void set(float var1, float var2, float var3, float var4);
}
