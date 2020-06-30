package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;
import org.lwjgl.util.glu.tessellation.GLUvertex;

class Normal {
   static boolean SLANTED_SWEEP;
   static double S_UNIT_X;
   static double S_UNIT_Y;
   private static final boolean TRUE_PROJECT = false;

   private static double Dot(double[] u, double[] v) {
      return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
   }

   static void Normalize(double[] v) {
      double len = v[0] * v[0] + v[1] * v[1] + v[2] * v[2];

      assert len > 0.0D;

      len = Math.sqrt(len);
      v[0] /= len;
      v[1] /= len;
      v[2] /= len;
   }

   static int LongAxis(double[] v) {
      int i = 0;
      if(Math.abs(v[1]) > Math.abs(v[0])) {
         i = 1;
      }

      if(Math.abs(v[2]) > Math.abs(v[i])) {
         i = 2;
      }

      return i;
   }

   static void ComputeNormal(GLUtessellatorImpl tess, double[] norm) {
      GLUvertex vHead = tess.mesh.vHead;
      double[] maxVal = new double[3];
      double[] minVal = new double[3];
      GLUvertex[] minVert = new GLUvertex[3];
      GLUvertex[] maxVert = new GLUvertex[3];
      double[] d1 = new double[3];
      double[] d2 = new double[3];
      double[] tNorm = new double[3];
      maxVal[0] = maxVal[1] = maxVal[2] = -2.0E15D;
      minVal[0] = minVal[1] = minVal[2] = 2.0E15D;

      for(GLUvertex v = vHead.next; v != vHead; v = v.next) {
         for(int i = 0; i < 3; ++i) {
            double c = v.coords[i];
            if(c < minVal[i]) {
               minVal[i] = c;
               minVert[i] = v;
            }

            if(c > maxVal[i]) {
               maxVal[i] = c;
               maxVert[i] = v;
            }
         }
      }

      int i = 0;
      if(maxVal[1] - minVal[1] > maxVal[0] - minVal[0]) {
         i = 1;
      }

      if(maxVal[2] - minVal[2] > maxVal[i] - minVal[i]) {
         i = 2;
      }

      if(minVal[i] >= maxVal[i]) {
         norm[0] = 0.0D;
         norm[1] = 0.0D;
         norm[2] = 1.0D;
      } else {
         double maxLen2 = 0.0D;
         GLUvertex v1 = minVert[i];
         GLUvertex v2 = maxVert[i];
         d1[0] = v1.coords[0] - v2.coords[0];
         d1[1] = v1.coords[1] - v2.coords[1];
         d1[2] = v1.coords[2] - v2.coords[2];

         for(GLUvertex var20 = vHead.next; var20 != vHead; var20 = var20.next) {
            d2[0] = var20.coords[0] - v2.coords[0];
            d2[1] = var20.coords[1] - v2.coords[1];
            d2[2] = var20.coords[2] - v2.coords[2];
            tNorm[0] = d1[1] * d2[2] - d1[2] * d2[1];
            tNorm[1] = d1[2] * d2[0] - d1[0] * d2[2];
            tNorm[2] = d1[0] * d2[1] - d1[1] * d2[0];
            double tLen2 = tNorm[0] * tNorm[0] + tNorm[1] * tNorm[1] + tNorm[2] * tNorm[2];
            if(tLen2 > maxLen2) {
               maxLen2 = tLen2;
               norm[0] = tNorm[0];
               norm[1] = tNorm[1];
               norm[2] = tNorm[2];
            }
         }

         if(maxLen2 <= 0.0D) {
            norm[0] = norm[1] = norm[2] = 0.0D;
            norm[LongAxis(d1)] = 1.0D;
         }

      }
   }

   static void CheckOrientation(GLUtessellatorImpl tess) {
      GLUface fHead = tess.mesh.fHead;
      GLUvertex vHead = tess.mesh.vHead;
      double area = 0.0D;

      for(GLUface f = fHead.next; f != fHead; f = f.next) {
         GLUhalfEdge e = f.anEdge;
         if(e.winding > 0) {
            while(true) {
               area += (e.Org.s - e.Sym.Org.s) * (e.Org.t + e.Sym.Org.t);
               e = e.Lnext;
               if(e == f.anEdge) {
                  break;
               }
            }
         }
      }

      if(area < 0.0D) {
         for(GLUvertex v = vHead.next; v != vHead; v = v.next) {
            v.t = -v.t;
         }

         tess.tUnit[0] = -tess.tUnit[0];
         tess.tUnit[1] = -tess.tUnit[1];
         tess.tUnit[2] = -tess.tUnit[2];
      }

   }

   public static void __gl_projectPolygon(GLUtessellatorImpl tess) {
      GLUvertex vHead = tess.mesh.vHead;
      double[] norm = new double[3];
      boolean computedNormal = false;
      norm[0] = tess.normal[0];
      norm[1] = tess.normal[1];
      norm[2] = tess.normal[2];
      if(norm[0] == 0.0D && norm[1] == 0.0D && norm[2] == 0.0D) {
         ComputeNormal(tess, norm);
         computedNormal = true;
      }

      double[] sUnit = tess.sUnit;
      double[] tUnit = tess.tUnit;
      int i = LongAxis(norm);
      sUnit[i] = 0.0D;
      sUnit[(i + 1) % 3] = S_UNIT_X;
      sUnit[(i + 2) % 3] = S_UNIT_Y;
      tUnit[i] = 0.0D;
      tUnit[(i + 1) % 3] = norm[i] > 0.0D?-S_UNIT_Y:S_UNIT_Y;
      tUnit[(i + 2) % 3] = norm[i] > 0.0D?S_UNIT_X:-S_UNIT_X;

      for(GLUvertex v = vHead.next; v != vHead; v = v.next) {
         v.s = Dot(v.coords, sUnit);
         v.t = Dot(v.coords, tUnit);
      }

      if(computedNormal) {
         CheckOrientation(tess);
      }

   }

   static {
      if(SLANTED_SWEEP) {
         S_UNIT_X = 0.5094153956495538D;
         S_UNIT_Y = 0.8605207462201063D;
      } else {
         S_UNIT_X = 1.0D;
         S_UNIT_Y = 0.0D;
      }

   }
}
