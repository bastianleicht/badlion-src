package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.CachedVertex;
import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUmesh;
import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;

class Render {
   private static final boolean USE_OPTIMIZED_CODE_PATH = false;
   private static final Render.RenderFan renderFan = new Render.RenderFan();
   private static final Render.RenderStrip renderStrip = new Render.RenderStrip();
   private static final Render.RenderTriangle renderTriangle = new Render.RenderTriangle();
   private static final int SIGN_INCONSISTENT = 2;

   public static void __gl_renderMesh(GLUtessellatorImpl tess, GLUmesh mesh) {
      tess.lonelyTriList = null;

      for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = f.next) {
         f.marked = false;
      }

      for(GLUface var3 = mesh.fHead.next; var3 != mesh.fHead; var3 = var3.next) {
         if(var3.inside && !var3.marked) {
            RenderMaximumFaceGroup(tess, var3);

            assert var3.marked;
         }
      }

      if(tess.lonelyTriList != null) {
         RenderLonelyTriangles(tess, tess.lonelyTriList);
         tess.lonelyTriList = null;
      }

   }

   static void RenderMaximumFaceGroup(GLUtessellatorImpl tess, GLUface fOrig) {
      GLUhalfEdge e = fOrig.anEdge;
      Render.FaceCount max = new Render.FaceCount();
      max.size = 1L;
      max.eStart = e;
      max.render = renderTriangle;
      if(!tess.flagBoundary) {
         Render.FaceCount newFace = MaximumFan(e);
         if(newFace.size > max.size) {
            max = newFace;
         }

         newFace = MaximumFan(e.Lnext);
         if(newFace.size > max.size) {
            max = newFace;
         }

         newFace = MaximumFan(e.Onext.Sym);
         if(newFace.size > max.size) {
            max = newFace;
         }

         newFace = MaximumStrip(e);
         if(newFace.size > max.size) {
            max = newFace;
         }

         newFace = MaximumStrip(e.Lnext);
         if(newFace.size > max.size) {
            max = newFace;
         }

         newFace = MaximumStrip(e.Onext.Sym);
         if(newFace.size > max.size) {
            max = newFace;
         }
      }

      max.render.render(tess, max.eStart, max.size);
   }

   private static boolean Marked(GLUface f) {
      return !f.inside || f.marked;
   }

   private static GLUface AddToTrail(GLUface f, GLUface t) {
      f.trail = t;
      f.marked = true;
      return f;
   }

   private static void FreeTrail(GLUface t) {
      while(t != null) {
         t.marked = false;
         t = t.trail;
      }

   }

   static Render.FaceCount MaximumFan(GLUhalfEdge eOrig) {
      Render.FaceCount newFace = new Render.FaceCount(0L, (GLUhalfEdge)null, renderFan);
      GLUface trail = null;

      for(GLUhalfEdge e = eOrig; !Marked(e.Lface); e = e.Onext) {
         trail = AddToTrail(e.Lface, trail);
         ++newFace.size;
      }

      GLUhalfEdge var4;
      for(var4 = eOrig; !Marked(var4.Sym.Lface); var4 = var4.Sym.Lnext) {
         trail = AddToTrail(var4.Sym.Lface, trail);
         ++newFace.size;
      }

      newFace.eStart = var4;
      FreeTrail(trail);
      return newFace;
   }

   private static boolean IsEven(long n) {
      return (n & 1L) == 0L;
   }

   static Render.FaceCount MaximumStrip(GLUhalfEdge eOrig) {
      Render.FaceCount newFace = new Render.FaceCount(0L, (GLUhalfEdge)null, renderStrip);
      long headSize = 0L;
      long tailSize = 0L;
      GLUface trail = null;

      GLUhalfEdge e;
      for(e = eOrig; !Marked(e.Lface); e = e.Onext) {
         trail = AddToTrail(e.Lface, trail);
         ++tailSize;
         e = e.Lnext.Sym;
         if(Marked(e.Lface)) {
            break;
         }

         trail = AddToTrail(e.Lface, trail);
         ++tailSize;
      }

      for(e = eOrig; !Marked(e.Sym.Lface); e = e.Sym.Onext.Sym) {
         trail = AddToTrail(e.Sym.Lface, trail);
         ++headSize;
         e = e.Sym.Lnext;
         if(Marked(e.Sym.Lface)) {
            break;
         }

         trail = AddToTrail(e.Sym.Lface, trail);
         ++headSize;
      }

      newFace.size = tailSize + headSize;
      if(IsEven(tailSize)) {
         newFace.eStart = e.Sym;
      } else if(IsEven(headSize)) {
         newFace.eStart = e;
      } else {
         --newFace.size;
         newFace.eStart = e.Onext;
      }

      FreeTrail(trail);
      return newFace;
   }

   static void RenderLonelyTriangles(GLUtessellatorImpl tess, GLUface f) {
      int edgeState = -1;
      tess.callBeginOrBeginData(4);

      for(; f != null; f = f.trail) {
         GLUhalfEdge e = f.anEdge;

         while(true) {
            if(tess.flagBoundary) {
               int newState = !e.Sym.Lface.inside?1:0;
               if(edgeState != newState) {
                  edgeState = newState;
                  tess.callEdgeFlagOrEdgeFlagData(newState != 0);
               }
            }

            tess.callVertexOrVertexData(e.Org.data);
            e = e.Lnext;
            if(e == f.anEdge) {
               break;
            }
         }
      }

      tess.callEndOrEndData();
   }

   public static void __gl_renderBoundary(GLUtessellatorImpl tess, GLUmesh mesh) {
      for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = f.next) {
         if(f.inside) {
            tess.callBeginOrBeginData(2);
            GLUhalfEdge e = f.anEdge;

            while(true) {
               tess.callVertexOrVertexData(e.Org.data);
               e = e.Lnext;
               if(e == f.anEdge) {
                  break;
               }
            }

            tess.callEndOrEndData();
         }
      }

   }

   static int ComputeNormal(GLUtessellatorImpl tess, double[] norm, boolean check) {
      CachedVertex[] v = tess.cache;
      int vn = tess.cacheCount;
      double[] n = new double[3];
      int sign = 0;
      if(!check) {
         norm[0] = norm[1] = norm[2] = 0.0D;
      }

      int vc = 1;
      double xc = v[vc].coords[0] - v[0].coords[0];
      double yc = v[vc].coords[1] - v[0].coords[1];
      double zc = v[vc].coords[2] - v[0].coords[2];

      while(true) {
         ++vc;
         if(vc >= vn) {
            return sign;
         }

         double xp = xc;
         double yp = yc;
         double zp = zc;
         xc = v[vc].coords[0] - v[0].coords[0];
         yc = v[vc].coords[1] - v[0].coords[1];
         zc = v[vc].coords[2] - v[0].coords[2];
         n[0] = yp * zc - zp * yc;
         n[1] = zp * xc - xp * zc;
         n[2] = xp * yc - yp * xc;
         double dot = n[0] * norm[0] + n[1] * norm[1] + n[2] * norm[2];
         if(!check) {
            if(dot >= 0.0D) {
               norm[0] += n[0];
               norm[1] += n[1];
               norm[2] += n[2];
            } else {
               norm[0] -= n[0];
               norm[1] -= n[1];
               norm[2] -= n[2];
            }
         } else if(dot != 0.0D) {
            if(dot > 0.0D) {
               if(sign < 0) {
                  return 2;
               }

               sign = 1;
            } else {
               if(sign > 0) {
                  return 2;
               }

               sign = -1;
            }
         }
      }
   }

   public static boolean __gl_renderCache(GLUtessellatorImpl tess) {
      CachedVertex[] v = tess.cache;
      int vn = tess.cacheCount;
      double[] norm = new double[3];
      if(tess.cacheCount < 3) {
         return true;
      } else {
         norm[0] = tess.normal[0];
         norm[1] = tess.normal[1];
         norm[2] = tess.normal[2];
         if(norm[0] == 0.0D && norm[1] == 0.0D && norm[2] == 0.0D) {
            ComputeNormal(tess, norm, false);
         }

         int sign = ComputeNormal(tess, norm, true);
         return sign == 2?false:sign == 0;
      }
   }

   private static class FaceCount {
      long size;
      GLUhalfEdge eStart;
      Render.renderCallBack render;

      private FaceCount() {
      }

      private FaceCount(long size, GLUhalfEdge eStart, Render.renderCallBack render) {
         this.size = size;
         this.eStart = eStart;
         this.render = render;
      }
   }

   private static class RenderFan implements Render.renderCallBack {
      private RenderFan() {
      }

      public void render(GLUtessellatorImpl tess, GLUhalfEdge e, long size) {
         tess.callBeginOrBeginData(6);
         tess.callVertexOrVertexData(e.Org.data);
         tess.callVertexOrVertexData(e.Sym.Org.data);

         while(!Render.Marked(e.Lface)) {
            e.Lface.marked = true;
            --size;
            e = e.Onext;
            tess.callVertexOrVertexData(e.Sym.Org.data);
         }

         assert size == 0L;

         tess.callEndOrEndData();
      }
   }

   private static class RenderStrip implements Render.renderCallBack {
      private RenderStrip() {
      }

      public void render(GLUtessellatorImpl tess, GLUhalfEdge e, long size) {
         tess.callBeginOrBeginData(5);
         tess.callVertexOrVertexData(e.Org.data);
         tess.callVertexOrVertexData(e.Sym.Org.data);

         while(!Render.Marked(e.Lface)) {
            e.Lface.marked = true;
            --size;
            e = e.Lnext.Sym;
            tess.callVertexOrVertexData(e.Org.data);
            if(Render.Marked(e.Lface)) {
               break;
            }

            e.Lface.marked = true;
            --size;
            e = e.Onext;
            tess.callVertexOrVertexData(e.Sym.Org.data);
         }

         assert size == 0L;

         tess.callEndOrEndData();
      }
   }

   private static class RenderTriangle implements Render.renderCallBack {
      private RenderTriangle() {
      }

      public void render(GLUtessellatorImpl tess, GLUhalfEdge e, long size) {
         assert size == 1L;

         tess.lonelyTriList = Render.AddToTrail(e.Lface, tess.lonelyTriList);
      }
   }

   private interface renderCallBack {
      void render(GLUtessellatorImpl var1, GLUhalfEdge var2, long var3);
   }
}
