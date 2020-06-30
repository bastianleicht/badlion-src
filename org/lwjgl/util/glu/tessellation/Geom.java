package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUvertex;

class Geom {
   // $FF: synthetic field
   static final boolean $assertionsDisabled = !Geom.class.desiredAssertionStatus();

   static double EdgeEval(GLUvertex u, GLUvertex v, GLUvertex w) {
      if($assertionsDisabled || VertLeq(u, v) && VertLeq(v, w)) {
         double gapL = v.s - u.s;
         double gapR = w.s - v.s;
         return gapL + gapR > 0.0D?(gapL < gapR?v.t - u.t + (u.t - w.t) * (gapL / (gapL + gapR)):v.t - w.t + (w.t - u.t) * (gapR / (gapL + gapR))):0.0D;
      } else {
         throw new AssertionError();
      }
   }

   static double EdgeSign(GLUvertex u, GLUvertex v, GLUvertex w) {
      if($assertionsDisabled || VertLeq(u, v) && VertLeq(v, w)) {
         double gapL = v.s - u.s;
         double gapR = w.s - v.s;
         return gapL + gapR > 0.0D?(v.t - w.t) * gapL + (v.t - u.t) * gapR:0.0D;
      } else {
         throw new AssertionError();
      }
   }

   static double TransEval(GLUvertex u, GLUvertex v, GLUvertex w) {
      if($assertionsDisabled || TransLeq(u, v) && TransLeq(v, w)) {
         double gapL = v.t - u.t;
         double gapR = w.t - v.t;
         return gapL + gapR > 0.0D?(gapL < gapR?v.s - u.s + (u.s - w.s) * (gapL / (gapL + gapR)):v.s - w.s + (w.s - u.s) * (gapR / (gapL + gapR))):0.0D;
      } else {
         throw new AssertionError();
      }
   }

   static double TransSign(GLUvertex u, GLUvertex v, GLUvertex w) {
      if($assertionsDisabled || TransLeq(u, v) && TransLeq(v, w)) {
         double gapL = v.t - u.t;
         double gapR = w.t - v.t;
         return gapL + gapR > 0.0D?(v.s - w.s) * gapL + (v.s - u.s) * gapR:0.0D;
      } else {
         throw new AssertionError();
      }
   }

   static boolean VertCCW(GLUvertex u, GLUvertex v, GLUvertex w) {
      return u.s * (v.t - w.t) + v.s * (w.t - u.t) + w.s * (u.t - v.t) >= 0.0D;
   }

   static double Interpolate(double a, double x, double b, double y) {
      a = a < 0.0D?0.0D:a;
      b = b < 0.0D?0.0D:b;
      return a <= b?(b == 0.0D?(x + y) / 2.0D:x + (y - x) * (a / (a + b))):y + (x - y) * (b / (a + b));
   }

   static void EdgeIntersect(GLUvertex o1, GLUvertex d1, GLUvertex o2, GLUvertex d2, GLUvertex v) {
      if(!VertLeq(o1, d1)) {
         GLUvertex temp = o1;
         o1 = d1;
         d1 = temp;
      }

      if(!VertLeq(o2, d2)) {
         GLUvertex temp = o2;
         o2 = d2;
         d2 = temp;
      }

      if(!VertLeq(o1, o2)) {
         GLUvertex temp = o1;
         o1 = o2;
         o2 = temp;
         temp = d1;
         d1 = d2;
         d2 = temp;
      }

      if(!VertLeq(o2, d1)) {
         v.s = (o2.s + d1.s) / 2.0D;
      } else if(VertLeq(d1, d2)) {
         double z1 = EdgeEval(o1, o2, d1);
         double z2 = EdgeEval(o2, d1, d2);
         if(z1 + z2 < 0.0D) {
            z1 = -z1;
            z2 = -z2;
         }

         v.s = Interpolate(z1, o2.s, z2, d1.s);
      } else {
         double z1 = EdgeSign(o1, o2, d1);
         double z2 = -EdgeSign(o1, d2, d1);
         if(z1 + z2 < 0.0D) {
            z1 = -z1;
            z2 = -z2;
         }

         v.s = Interpolate(z1, o2.s, z2, d2.s);
      }

      if(!TransLeq(o1, d1)) {
         GLUvertex temp = o1;
         o1 = d1;
         d1 = temp;
      }

      if(!TransLeq(o2, d2)) {
         GLUvertex temp = o2;
         o2 = d2;
         d2 = temp;
      }

      if(!TransLeq(o1, o2)) {
         GLUvertex temp = o2;
         o2 = o1;
         o1 = temp;
         temp = d2;
         d2 = d1;
         d1 = temp;
      }

      if(!TransLeq(o2, d1)) {
         v.t = (o2.t + d1.t) / 2.0D;
      } else if(TransLeq(d1, d2)) {
         double z1 = TransEval(o1, o2, d1);
         double z2 = TransEval(o2, d1, d2);
         if(z1 + z2 < 0.0D) {
            z1 = -z1;
            z2 = -z2;
         }

         v.t = Interpolate(z1, o2.t, z2, d1.t);
      } else {
         double z1 = TransSign(o1, o2, d1);
         double z2 = -TransSign(o1, d2, d1);
         if(z1 + z2 < 0.0D) {
            z1 = -z1;
            z2 = -z2;
         }

         v.t = Interpolate(z1, o2.t, z2, d2.t);
      }

   }

   static boolean VertEq(GLUvertex u, GLUvertex v) {
      return u.s == v.s && u.t == v.t;
   }

   static boolean VertLeq(GLUvertex u, GLUvertex v) {
      return u.s < v.s || u.s == v.s && u.t <= v.t;
   }

   static boolean TransLeq(GLUvertex u, GLUvertex v) {
      return u.t < v.t || u.t == v.t && u.s <= v.s;
   }

   static boolean EdgeGoesLeft(GLUhalfEdge e) {
      return VertLeq(e.Sym.Org, e.Org);
   }

   static boolean EdgeGoesRight(GLUhalfEdge e) {
      return VertLeq(e.Org, e.Sym.Org);
   }

   static double VertL1dist(GLUvertex u, GLUvertex v) {
      return Math.abs(u.s - v.s) + Math.abs(u.t - v.t);
   }
}
