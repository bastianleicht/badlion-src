package com.jcraft.jorbis;

class Mdct {
   int n;
   int log2n;
   float[] trig;
   int[] bitrev;
   float scale;
   float[] _x = new float[1024];
   float[] _w = new float[1024];

   void init(int n) {
      this.bitrev = new int[n / 4];
      this.trig = new float[n + n / 4];
      this.log2n = (int)Math.rint(Math.log((double)n) / Math.log(2.0D));
      this.n = n;
      int AE = 0;
      int AO = 1;
      int BE = AE + n / 2;
      int BO = BE + 1;
      int CE = BE + n / 2;
      int CO = CE + 1;

      for(int i = 0; i < n / 4; ++i) {
         this.trig[AE + i * 2] = (float)Math.cos(3.141592653589793D / (double)n * (double)(4 * i));
         this.trig[AO + i * 2] = (float)(-Math.sin(3.141592653589793D / (double)n * (double)(4 * i)));
         this.trig[BE + i * 2] = (float)Math.cos(3.141592653589793D / (double)(2 * n) * (double)(2 * i + 1));
         this.trig[BO + i * 2] = (float)Math.sin(3.141592653589793D / (double)(2 * n) * (double)(2 * i + 1));
      }

      for(int i = 0; i < n / 8; ++i) {
         this.trig[CE + i * 2] = (float)Math.cos(3.141592653589793D / (double)n * (double)(4 * i + 2));
         this.trig[CO + i * 2] = (float)(-Math.sin(3.141592653589793D / (double)n * (double)(4 * i + 2)));
      }

      int mask = (1 << this.log2n - 1) - 1;
      int msb = 1 << this.log2n - 2;

      for(int i = 0; i < n / 8; ++i) {
         int acc = 0;

         for(int j = 0; msb >>> j != 0; ++j) {
            if((msb >>> j & i) != 0) {
               acc |= 1 << j;
            }
         }

         this.bitrev[i * 2] = ~acc & mask;
         this.bitrev[i * 2 + 1] = acc;
      }

      this.scale = 4.0F / (float)n;
   }

   void clear() {
   }

   void forward(float[] in, float[] out) {
   }

   synchronized void backward(float[] in, float[] out) {
      if(this._x.length < this.n / 2) {
         this._x = new float[this.n / 2];
      }

      if(this._w.length < this.n / 2) {
         this._w = new float[this.n / 2];
      }

      float[] x = this._x;
      float[] w = this._w;
      int n2 = this.n >>> 1;
      int n4 = this.n >>> 2;
      int n8 = this.n >>> 3;
      int inO = 1;
      int xO = 0;
      int A = n2;

      for(int i = 0; i < n8; ++i) {
         A -= 2;
         x[xO++] = -in[inO + 2] * this.trig[A + 1] - in[inO] * this.trig[A];
         x[xO++] = in[inO] * this.trig[A + 1] - in[inO + 2] * this.trig[A];
         inO += 4;
      }

      inO = n2 - 4;

      for(int o1 = 0; o1 < n8; ++o1) {
         A -= 2;
         x[xO++] = in[inO] * this.trig[A + 1] + in[inO + 2] * this.trig[A];
         x[xO++] = in[inO] * this.trig[A] - in[inO + 2] * this.trig[A + 1];
         inO -= 4;
      }

      float[] xxx = this.mdct_kernel(x, w, this.n, n2, n4, n8);
      xO = 0;
      A = n2;
      int o1 = n4;
      int o2 = n4 - 1;
      int o3 = n4 + n2;
      int o4 = o3 - 1;

      for(int i = 0; i < n4; ++i) {
         float temp1 = xxx[xO] * this.trig[A + 1] - xxx[xO + 1] * this.trig[A];
         float temp2 = -(xxx[xO] * this.trig[A] + xxx[xO + 1] * this.trig[A + 1]);
         out[o1] = -temp1;
         out[o2] = temp1;
         out[o3] = temp2;
         out[o4] = temp2;
         ++o1;
         --o2;
         ++o3;
         --o4;
         xO += 2;
         A += 2;
      }

   }

   private float[] mdct_kernel(float[] x, float[] w, int n, int n2, int n4, int n8) {
      int xA = n4;
      int xB = 0;
      int w2 = n4;
      int A = n2;

      for(int i = 0; i < n4; ++i) {
         float x0 = x[xA] - x[xB];
         w[w2 + i] = x[xA++] + x[xB++];
         float x1 = x[xA] - x[xB];
         A -= 4;
         w[i++] = x0 * this.trig[A] + x1 * this.trig[A + 1];
         w[i] = x1 * this.trig[A] - x0 * this.trig[A + 1];
         w[w2 + i] = x[xA++] + x[xB++];
      }

      for(int i = 0; i < this.log2n - 3; ++i) {
         int k0 = n >>> i + 2;
         int k1 = 1 << i + 3;
         int wbase = n2 - 2;
         A = 0;

         for(int r = 0; r < k0 >>> 2; ++r) {
            int w1 = wbase;
            w2 = wbase - (k0 >> 1);
            float AEv = this.trig[A];
            float AOv = this.trig[A + 1];
            wbase -= 2;
            ++k0;

            for(int s = 0; s < 2 << i; ++s) {
               float wB = w[w1] - w[w2];
               x[w1] = w[w1] + w[w2];
               ++w1;
               float var10000 = w[w1];
               ++w2;
               float wA = var10000 - w[w2];
               x[w1] = w[w1] + w[w2];
               x[w2] = wA * AEv - wB * AOv;
               x[w2 - 1] = wB * AEv + wA * AOv;
               w1 -= k0;
               w2 -= k0;
            }

            --k0;
            A += k1;
         }

         float[] temp = w;
         w = x;
         x = temp;
      }

      int C = n;
      int bit = 0;
      int x1 = 0;
      int x2 = n2 - 1;

      for(int i = 0; i < n8; ++i) {
         int t1 = this.bitrev[bit++];
         int t2 = this.bitrev[bit++];
         float wA = w[t1] - w[t2 + 1];
         float wB = w[t1 - 1] + w[t2];
         float wC = w[t1] + w[t2 + 1];
         float wD = w[t1 - 1] - w[t2];
         float wACE = wA * this.trig[C];
         float wBCE = wB * this.trig[C++];
         float wACO = wA * this.trig[C];
         float wBCO = wB * this.trig[C++];
         x[x1++] = (wC + wACO + wBCE) * 0.5F;
         x[x2--] = (-wD + wBCO - wACE) * 0.5F;
         x[x1++] = (wD + wBCO - wACE) * 0.5F;
         x[x2--] = (wC - wACO - wBCE) * 0.5F;
      }

      return x;
   }
}
