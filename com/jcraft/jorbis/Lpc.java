package com.jcraft.jorbis;

import com.jcraft.jorbis.Drft;

class Lpc {
   Drft fft = new Drft();
   int ln;
   int m;

   static float lpc_from_data(float[] data, float[] lpc, int n, int m) {
      float[] aut = new float[m + 1];

      float d;
      for(int j = m + 1; j-- != 0; aut[j] = d) {
         d = 0.0F;

         for(int i = j; i < n; ++i) {
            d += data[i] * data[i - j];
         }
      }

      float error = aut[0];

      for(int i = 0; i < m; ++i) {
         d = -aut[i + 1];
         if(error == 0.0F) {
            for(int k = 0; k < m; ++k) {
               lpc[k] = 0.0F;
            }

            return 0.0F;
         }

         for(int var11 = 0; var11 < i; ++var11) {
            d -= lpc[var11] * aut[i - var11];
         }

         d = d / error;
         lpc[i] = d;

         int var12;
         for(var12 = 0; var12 < i / 2; ++var12) {
            float tmp = lpc[var12];
            lpc[var12] += d * lpc[i - 1 - var12];
            lpc[i - 1 - var12] += d * tmp;
         }

         if(i % 2 != 0) {
            lpc[var12] += lpc[var12] * d;
         }

         error = (float)((double)error * (1.0D - (double)(d * d)));
      }

      return error;
   }

   float lpc_from_curve(float[] curve, float[] lpc) {
      int n = this.ln;
      float[] work = new float[n + n];
      float fscale = (float)(0.5D / (double)n);

      for(int i = 0; i < n; ++i) {
         work[i * 2] = curve[i] * fscale;
         work[i * 2 + 1] = 0.0F;
      }

      work[n * 2 - 1] = curve[n - 1] * fscale;
      n = n * 2;
      this.fft.backward(work);
      int var10 = 0;

      float temp;
      for(int j = n / 2; var10 < n / 2; work[j++] = temp) {
         temp = work[var10];
         work[var10++] = work[j];
      }

      return lpc_from_data(work, lpc, n, this.m);
   }

   void init(int mapped, int m) {
      this.ln = mapped;
      this.m = m;
      this.fft.init(mapped * 2);
   }

   void clear() {
      this.fft.clear();
   }

   static float FAST_HYPOT(float a, float b) {
      return (float)Math.sqrt((double)(a * a + b * b));
   }

   void lpc_to_curve(float[] curve, float[] lpc, float amp) {
      for(int i = 0; i < this.ln * 2; ++i) {
         curve[i] = 0.0F;
      }

      if(amp != 0.0F) {
         for(int i = 0; i < this.m; ++i) {
            curve[i * 2 + 1] = lpc[i] / (4.0F * amp);
            curve[i * 2 + 2] = -lpc[i] / (4.0F * amp);
         }

         this.fft.backward(curve);
         int l2 = this.ln * 2;
         float unit = (float)(1.0D / (double)amp);
         curve[0] = (float)(1.0D / (double)(curve[0] * 2.0F + unit));

         for(int i = 1; i < this.ln; ++i) {
            float real = curve[i] + curve[l2 - i];
            float imag = curve[i] - curve[l2 - i];
            float a = real + unit;
            curve[i] = (float)(1.0D / (double)FAST_HYPOT(a, imag));
         }

      }
   }
}
