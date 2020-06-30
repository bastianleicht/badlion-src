package com.jcraft.jorbis;

import com.jcraft.jorbis.Lookup;

class Lsp {
   static final float M_PI = 3.1415927F;

   static void lsp_to_curve(float[] curve, int[] map, int n, int ln, float[] lsp, int m, float amp, float ampoffset) {
      float wdel = 3.1415927F / (float)ln;

      for(int i = 0; i < m; ++i) {
         lsp[i] = Lookup.coslook(lsp[i]);
      }

      int m2 = m / 2 * 2;

      float var24;
      int var10001;
      for(int var18 = 0; var18 < n; curve[var10001] *= var24) {
         int k = map[var18];
         float p = 0.70710677F;
         q = 0.70710677F;
         float w = Lookup.coslook(wdel * (float)k);

         for(int j = 0; j < m2; j += 2) {
            var24 *= lsp[j] - w;
            p *= lsp[j + 1] - w;
         }

         if((m & 1) != 0) {
            var24 = var24 * (lsp[m - 1] - w);
            var24 = var24 * var24;
            p = p * p * (1.0F - w * w);
         } else {
            var24 = var24 * var24 * (1.0F + w);
            p = p * p * (1.0F - w);
         }

         var24 = p + var24;
         int hx = Float.floatToIntBits(var24);
         int ix = Integer.MAX_VALUE & hx;
         int qexp = 0;
         if(ix < 2139095040 && ix != 0) {
            if(ix < 8388608) {
               var24 = (float)((double)var24 * 3.3554432E7D);
               hx = Float.floatToIntBits(var24);
               ix = Integer.MAX_VALUE & hx;
               qexp = -25;
            }

            qexp += (ix >>> 23) - 126;
            hx = hx & -2139095041 | 1056964608;
            var24 = Float.intBitsToFloat(hx);
         }

         var24 = Lookup.fromdBlook(amp * Lookup.invsqlook(var24) * Lookup.invsq2explook(qexp + m) - ampoffset);

         while(true) {
            var10001 = var18++;
            if(var18 >= n || map[var18] != k) {
               break;
            }
         }
      }

   }
}
