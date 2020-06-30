package org.lwjgl.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.WritableVector2f;

public class Vector2f extends Vector implements Serializable, ReadableVector2f, WritableVector2f {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;

   public Vector2f() {
   }

   public Vector2f(ReadableVector2f src) {
      this.set(src);
   }

   public Vector2f(float x, float y) {
      this.set(x, y);
   }

   public void set(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public Vector2f set(ReadableVector2f src) {
      this.x = src.getX();
      this.y = src.getY();
      return this;
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public Vector2f translate(float x, float y) {
      this.x += x;
      this.y += y;
      return this;
   }

   public Vector negate() {
      this.x = -this.x;
      this.y = -this.y;
      return this;
   }

   public Vector2f negate(Vector2f dest) {
      if(dest == null) {
         dest = new Vector2f();
      }

      dest.x = -this.x;
      dest.y = -this.y;
      return dest;
   }

   public Vector2f normalise(Vector2f dest) {
      float l = this.length();
      if(dest == null) {
         dest = new Vector2f(this.x / l, this.y / l);
      } else {
         dest.set(this.x / l, this.y / l);
      }

      return dest;
   }

   public static float dot(Vector2f left, Vector2f right) {
      return left.x * right.x + left.y * right.y;
   }

   public static float angle(Vector2f a, Vector2f b) {
      float dls = dot(a, b) / (a.length() * b.length());
      if(dls < -1.0F) {
         dls = -1.0F;
      } else if(dls > 1.0F) {
         dls = 1.0F;
      }

      return (float)Math.acos((double)dls);
   }

   public static Vector2f add(Vector2f left, Vector2f right, Vector2f dest) {
      if(dest == null) {
         return new Vector2f(left.x + right.x, left.y + right.y);
      } else {
         dest.set(left.x + right.x, left.y + right.y);
         return dest;
      }
   }

   public static Vector2f sub(Vector2f left, Vector2f right, Vector2f dest) {
      if(dest == null) {
         return new Vector2f(left.x - right.x, left.y - right.y);
      } else {
         dest.set(left.x - right.x, left.y - right.y);
         return dest;
      }
   }

   public Vector store(FloatBuffer buf) {
      buf.put(this.x);
      buf.put(this.y);
      return this;
   }

   public Vector load(FloatBuffer buf) {
      this.x = buf.get();
      this.y = buf.get();
      return this;
   }

   public Vector scale(float scale) {
      this.x *= scale;
      this.y *= scale;
      return this;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(64);
      sb.append("Vector2f[");
      sb.append(this.x);
      sb.append(", ");
      sb.append(this.y);
      sb.append(']');
      return sb.toString();
   }

   public final float getX() {
      return this.x;
   }

   public final float getY() {
      return this.y;
   }

   public final void setX(float x) {
      this.x = x;
   }

   public final void setY(float y) {
      this.y = y;
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(obj == null) {
         return false;
      } else if(this.getClass() != obj.getClass()) {
         return false;
      } else {
         Vector2f other = (Vector2f)obj;
         return this.x == other.x && this.y == other.y;
      }
   }
}
