package org.lwjgl.util.vector;

import java.nio.FloatBuffer;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector4f;

public class Quaternion extends Vector implements ReadableVector4f {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;
   public float z;
   public float w;

   public Quaternion() {
      this.setIdentity();
   }

   public Quaternion(ReadableVector4f src) {
      this.set(src);
   }

   public Quaternion(float x, float y, float z, float w) {
      this.set(x, y, z, w);
   }

   public void set(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void set(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void set(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Quaternion set(ReadableVector4f src) {
      this.x = src.getX();
      this.y = src.getY();
      this.z = src.getZ();
      this.w = src.getW();
      return this;
   }

   public Quaternion setIdentity() {
      return setIdentity(this);
   }

   public static Quaternion setIdentity(Quaternion q) {
      q.x = 0.0F;
      q.y = 0.0F;
      q.z = 0.0F;
      q.w = 1.0F;
      return q;
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
   }

   public static Quaternion normalise(Quaternion src, Quaternion dest) {
      float inv_l = 1.0F / src.length();
      if(dest == null) {
         dest = new Quaternion();
      }

      dest.set(src.x * inv_l, src.y * inv_l, src.z * inv_l, src.w * inv_l);
      return dest;
   }

   public Quaternion normalise(Quaternion dest) {
      return normalise(this, dest);
   }

   public static float dot(Quaternion left, Quaternion right) {
      return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
   }

   public Quaternion negate(Quaternion dest) {
      return negate(this, dest);
   }

   public static Quaternion negate(Quaternion src, Quaternion dest) {
      if(dest == null) {
         dest = new Quaternion();
      }

      dest.x = -src.x;
      dest.y = -src.y;
      dest.z = -src.z;
      dest.w = src.w;
      return dest;
   }

   public Vector negate() {
      return negate(this, this);
   }

   public Vector load(FloatBuffer buf) {
      this.x = buf.get();
      this.y = buf.get();
      this.z = buf.get();
      this.w = buf.get();
      return this;
   }

   public Vector scale(float scale) {
      return scale(scale, this, this);
   }

   public static Quaternion scale(float scale, Quaternion src, Quaternion dest) {
      if(dest == null) {
         dest = new Quaternion();
      }

      dest.x = src.x * scale;
      dest.y = src.y * scale;
      dest.z = src.z * scale;
      dest.w = src.w * scale;
      return dest;
   }

   public Vector store(FloatBuffer buf) {
      buf.put(this.x);
      buf.put(this.y);
      buf.put(this.z);
      buf.put(this.w);
      return this;
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

   public void setZ(float z) {
      this.z = z;
   }

   public float getZ() {
      return this.z;
   }

   public void setW(float w) {
      this.w = w;
   }

   public float getW() {
      return this.w;
   }

   public String toString() {
      return "Quaternion: " + this.x + " " + this.y + " " + this.z + " " + this.w;
   }

   public static Quaternion mul(Quaternion left, Quaternion right, Quaternion dest) {
      if(dest == null) {
         dest = new Quaternion();
      }

      dest.set(left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y, left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z, left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x, left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z);
      return dest;
   }

   public static Quaternion mulInverse(Quaternion left, Quaternion right, Quaternion dest) {
      float n = right.lengthSquared();
      n = (double)n == 0.0D?n:1.0F / n;
      if(dest == null) {
         dest = new Quaternion();
      }

      dest.set((left.x * right.w - left.w * right.x - left.y * right.z + left.z * right.y) * n, (left.y * right.w - left.w * right.y - left.z * right.x + left.x * right.z) * n, (left.z * right.w - left.w * right.z - left.x * right.y + left.y * right.x) * n, (left.w * right.w + left.x * right.x + left.y * right.y + left.z * right.z) * n);
      return dest;
   }

   public final void setFromAxisAngle(Vector4f a1) {
      this.x = a1.x;
      this.y = a1.y;
      this.z = a1.z;
      float n = (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
      float s = (float)(Math.sin(0.5D * (double)a1.w) / (double)n);
      this.x *= s;
      this.y *= s;
      this.z *= s;
      this.w = (float)Math.cos(0.5D * (double)a1.w);
   }

   public final Quaternion setFromMatrix(Matrix4f m) {
      return setFromMatrix(m, this);
   }

   public static Quaternion setFromMatrix(Matrix4f m, Quaternion q) {
      return q.setFromMat(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12, m.m20, m.m21, m.m22);
   }

   public final Quaternion setFromMatrix(Matrix3f m) {
      return setFromMatrix(m, this);
   }

   public static Quaternion setFromMatrix(Matrix3f m, Quaternion q) {
      return q.setFromMat(m.m00, m.m01, m.m02, m.m10, m.m11, m.m12, m.m20, m.m21, m.m22);
   }

   private Quaternion setFromMat(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
      float tr = m00 + m11 + m22;
      if((double)tr >= 0.0D) {
         float s = (float)Math.sqrt((double)tr + 1.0D);
         this.w = s * 0.5F;
         s = 0.5F / s;
         this.x = (m21 - m12) * s;
         this.y = (m02 - m20) * s;
         this.z = (m10 - m01) * s;
      } else {
         float max = Math.max(Math.max(m00, m11), m22);
         if(max == m00) {
            float s = (float)Math.sqrt((double)(m00 - (m11 + m22)) + 1.0D);
            this.x = s * 0.5F;
            s = 0.5F / s;
            this.y = (m01 + m10) * s;
            this.z = (m20 + m02) * s;
            this.w = (m21 - m12) * s;
         } else if(max == m11) {
            float s = (float)Math.sqrt((double)(m11 - (m22 + m00)) + 1.0D);
            this.y = s * 0.5F;
            s = 0.5F / s;
            this.z = (m12 + m21) * s;
            this.x = (m01 + m10) * s;
            this.w = (m02 - m20) * s;
         } else {
            float s = (float)Math.sqrt((double)(m22 - (m00 + m11)) + 1.0D);
            this.z = s * 0.5F;
            s = 0.5F / s;
            this.x = (m20 + m02) * s;
            this.y = (m12 + m21) * s;
            this.w = (m10 - m01) * s;
         }
      }

      return this;
   }
}
