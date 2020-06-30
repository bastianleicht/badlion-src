package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUmesh;
import org.lwjgl.util.glu.tessellation.Geom;
import org.lwjgl.util.glu.tessellation.Mesh;

class TessMono {
   static boolean __gl_meshTessellateMonoRegion(GLUface face) {
      GLUhalfEdge up = face.anEdge;
      if($assertionsDisabled || up.Lnext != up && up.Lnext.Lnext != up) {
         while(Geom.VertLeq(up.Sym.Org, up.Org)) {
            up = up.Onext.Sym;
         }

         while(Geom.VertLeq(up.Org, up.Sym.Org)) {
            up = up.Lnext;
         }

         GLUhalfEdge lo = up.Onext.Sym;

         while(up.Lnext != lo) {
            if(Geom.VertLeq(up.Sym.Org, lo.Org)) {
               while(lo.Lnext != up && (Geom.EdgeGoesLeft(lo.Lnext) || Geom.EdgeSign(lo.Org, lo.Sym.Org, lo.Lnext.Sym.Org) <= 0.0D)) {
                  GLUhalfEdge tempHalfEdge = Mesh.__gl_meshConnect(lo.Lnext, lo);
                  if(tempHalfEdge == null) {
                     return false;
                  }

                  lo = tempHalfEdge.Sym;
               }

               lo = lo.Onext.Sym;
            } else {
               while(lo.Lnext != up && (Geom.EdgeGoesRight(up.Onext.Sym) || Geom.EdgeSign(up.Sym.Org, up.Org, up.Onext.Sym.Org) >= 0.0D)) {
                  GLUhalfEdge tempHalfEdge = Mesh.__gl_meshConnect(up, up.Onext.Sym);
                  if(tempHalfEdge == null) {
                     return false;
                  }

                  up = tempHalfEdge.Sym;
               }

               up = up.Lnext;
            }
         }

         assert lo.Lnext != up;

         while(lo.Lnext.Lnext != up) {
            GLUhalfEdge tempHalfEdge = Mesh.__gl_meshConnect(lo.Lnext, lo);
            if(tempHalfEdge == null) {
               return false;
            }

            lo = tempHalfEdge.Sym;
         }

         return true;
      } else {
         throw new AssertionError();
      }
   }

   public static boolean __gl_meshTessellateInterior(GLUmesh mesh) {
      GLUface next;
      for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = next) {
         next = f.next;
         if(f.inside && !__gl_meshTessellateMonoRegion(f)) {
            return false;
         }
      }

      return true;
   }

   public static void __gl_meshDiscardExterior(GLUmesh mesh) {
      GLUface next;
      for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = next) {
         next = f.next;
         if(!f.inside) {
            Mesh.__gl_meshZapFace(f);
         }
      }

   }

   public static boolean __gl_meshSetWindingNumber(GLUmesh mesh, int value, boolean keepOnlyBoundary) {
      GLUhalfEdge eNext;
      for(GLUhalfEdge e = mesh.eHead.next; e != mesh.eHead; e = eNext) {
         eNext = e.next;
         if(e.Sym.Lface.inside != e.Lface.inside) {
            e.winding = e.Lface.inside?value:-value;
         } else if(!keepOnlyBoundary) {
            e.winding = 0;
         } else if(!Mesh.__gl_meshDelete(e)) {
            return false;
         }
      }

      return true;
   }
}
