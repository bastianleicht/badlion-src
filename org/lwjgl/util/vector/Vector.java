package org.lwjgl.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;
import org.lwjgl.util.vector.ReadableVector;

public abstract class Vector implements Serializable, ReadableVector {
   public final float length() {
      return (float)Math.sqrt((double)this.lengthSquared());
   }

   public abstract float lengthSquared();

   public abstract Vector load(FloatBuffer var1);

   public abstract Vector negate();

   public final Vector normalise() {
      float len = this.length();
      if(len != 0.0F) {
         float l = 1.0F / len;
         return this.scale(l);
      } else {
         throw new IllegalStateException("Zero length vector");
      }
   }

   public abstract Vector store(FloatBuffer var1);

   public abstract Vector scale(float var1);
}
