package com.jcraft.jorbis;

class Util {
   static int ilog(int v) {
      int ret;
      for(ret = 0; v != 0; v >>>= 1) {
         ++ret;
      }

      return ret;
   }

   static int ilog2(int v) {
      int ret;
      for(ret = 0; v > 1; v >>>= 1) {
         ++ret;
      }

      return ret;
   }

   static int icount(int v) {
      int ret;
      for(ret = 0; v != 0; v >>>= 1) {
         ret += v & 1;
      }

      return ret;
   }
}
