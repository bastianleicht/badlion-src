package org.lwjgl.util.glu;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Util {
   private static IntBuffer scratch = BufferUtils.createIntBuffer(16);

   protected static int ceil(int a, int b) {
      return a % b == 0?a / b:a / b + 1;
   }

   protected static float[] normalize(float[] v) {
      float r = (float)Math.sqrt((double)(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
      if((double)r == 0.0D) {
         return v;
      } else {
         r = 1.0F / r;
         v[0] *= r;
         v[1] *= r;
         v[2] *= r;
         return v;
      }
   }

   protected static void cross(float[] v1, float[] v2, float[] result) {
      result[0] = v1[1] * v2[2] - v1[2] * v2[1];
      result[1] = v1[2] * v2[0] - v1[0] * v2[2];
      result[2] = v1[0] * v2[1] - v1[1] * v2[0];
   }

   protected static int compPerPix(int format) {
      switch(format) {
      case 6400:
      case 6401:
      case 6402:
      case 6403:
      case 6404:
      case 6405:
      case 6406:
      case 6409:
         return 1;
      case 6407:
      case 32992:
         return 3;
      case 6408:
      case 32993:
         return 4;
      case 6410:
         return 2;
      default:
         return -1;
      }
   }

   protected static int nearestPower(int value) {
      int i = 1;
      if(value == 0) {
         return -1;
      } else {
         while(value != 1) {
            if(value == 3) {
               return i << 2;
            }

            value >>= 1;
            i <<= 1;
         }

         return i;
      }
   }

   protected static int bytesPerPixel(int format, int type) {
      int n;
      switch(format) {
      case 6400:
      case 6401:
      case 6402:
      case 6403:
      case 6404:
      case 6405:
      case 6406:
      case 6409:
         n = 1;
         break;
      case 6407:
      case 32992:
         n = 3;
         break;
      case 6408:
      case 32993:
         n = 4;
         break;
      case 6410:
         n = 2;
         break;
      default:
         n = 0;
      }

      int m;
      switch(type) {
      case 5120:
         m = 1;
         break;
      case 5121:
         m = 1;
         break;
      case 5122:
         m = 2;
         break;
      case 5123:
         m = 2;
         break;
      case 5124:
         m = 4;
         break;
      case 5125:
         m = 4;
         break;
      case 5126:
         m = 4;
         break;
      case 6656:
         m = 1;
         break;
      default:
         m = 0;
      }

      return n * m;
   }

   protected static int glGetIntegerv(int what) {
      scratch.rewind();
      GL11.glGetInteger(what, scratch);
      return scratch.get();
   }
}
