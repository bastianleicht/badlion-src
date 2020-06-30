package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;
import org.lwjgl.util.glu.tessellation.CachedVertex;
import org.lwjgl.util.glu.tessellation.Dict;
import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUmesh;
import org.lwjgl.util.glu.tessellation.GLUvertex;
import org.lwjgl.util.glu.tessellation.Mesh;
import org.lwjgl.util.glu.tessellation.Normal;
import org.lwjgl.util.glu.tessellation.PriorityQ;
import org.lwjgl.util.glu.tessellation.Render;
import org.lwjgl.util.glu.tessellation.Sweep;
import org.lwjgl.util.glu.tessellation.TessMono;

public class GLUtessellatorImpl implements GLUtessellator {
   public static final int TESS_MAX_CACHE = 100;
   private int state = 0;
   private GLUhalfEdge lastEdge;
   GLUmesh mesh;
   double[] normal = new double[3];
   double[] sUnit = new double[3];
   double[] tUnit = new double[3];
   private double relTolerance;
   int windingRule;
   boolean fatalError;
   Dict dict;
   PriorityQ pq;
   GLUvertex event;
   boolean flagBoundary;
   boolean boundaryOnly;
   GLUface lonelyTriList;
   private boolean flushCacheOnNextVertex;
   int cacheCount;
   CachedVertex[] cache = new CachedVertex[100];
   private Object polygonData;
   private GLUtessellatorCallback callBegin;
   private GLUtessellatorCallback callEdgeFlag;
   private GLUtessellatorCallback callVertex;
   private GLUtessellatorCallback callEnd;
   private GLUtessellatorCallback callError;
   private GLUtessellatorCallback callCombine;
   private GLUtessellatorCallback callBeginData;
   private GLUtessellatorCallback callEdgeFlagData;
   private GLUtessellatorCallback callVertexData;
   private GLUtessellatorCallback callEndData;
   private GLUtessellatorCallback callErrorData;
   private GLUtessellatorCallback callCombineData;
   private static final double GLU_TESS_DEFAULT_TOLERANCE = 0.0D;
   private static GLUtessellatorCallback NULL_CB = new GLUtessellatorCallbackAdapter();

   public GLUtessellatorImpl() {
      this.normal[0] = 0.0D;
      this.normal[1] = 0.0D;
      this.normal[2] = 0.0D;
      this.relTolerance = 0.0D;
      this.windingRule = 100130;
      this.flagBoundary = false;
      this.boundaryOnly = false;
      this.callBegin = NULL_CB;
      this.callEdgeFlag = NULL_CB;
      this.callVertex = NULL_CB;
      this.callEnd = NULL_CB;
      this.callError = NULL_CB;
      this.callCombine = NULL_CB;
      this.callBeginData = NULL_CB;
      this.callEdgeFlagData = NULL_CB;
      this.callVertexData = NULL_CB;
      this.callEndData = NULL_CB;
      this.callErrorData = NULL_CB;
      this.callCombineData = NULL_CB;
      this.polygonData = null;

      for(int i = 0; i < this.cache.length; ++i) {
         this.cache[i] = new CachedVertex();
      }

   }

   public static GLUtessellator gluNewTess() {
      return new GLUtessellatorImpl();
   }

   private void makeDormant() {
      if(this.mesh != null) {
         Mesh.__gl_meshDeleteMesh(this.mesh);
      }

      this.state = 0;
      this.lastEdge = null;
      this.mesh = null;
   }

   private void requireState(int newState) {
      if(this.state != newState) {
         this.gotoState(newState);
      }

   }

   private void gotoState(int newState) {
      while(this.state != newState) {
         if(this.state < newState) {
            if(this.state == 0) {
               this.callErrorOrErrorData(100151);
               this.gluTessBeginPolygon((Object)null);
            } else if(this.state == 1) {
               this.callErrorOrErrorData(100152);
               this.gluTessBeginContour();
            }
         } else if(this.state == 2) {
            this.callErrorOrErrorData(100154);
            this.gluTessEndContour();
         } else if(this.state == 1) {
            this.callErrorOrErrorData(100153);
            this.makeDormant();
         }
      }

   }

   public void gluDeleteTess() {
      this.requireState(0);
   }

   public void gluTessProperty(int which, double value) {
      switch(which) {
      case 100140:
         int windingRule = (int)value;
         if((double)windingRule != value) {
            break;
         }

         switch(windingRule) {
         case 100130:
         case 100131:
         case 100132:
         case 100133:
         case 100134:
            this.windingRule = windingRule;
            return;
         }
      case 100141:
         this.boundaryOnly = value != 0.0D;
         return;
      case 100142:
         if(value >= 0.0D && value <= 1.0D) {
            this.relTolerance = value;
            return;
         }
         break;
      default:
         this.callErrorOrErrorData(100900);
         return;
      }

      this.callErrorOrErrorData(100901);
   }

   public void gluGetTessProperty(int which, double[] value, int value_offset) {
      switch(which) {
      case 100140:
         assert this.windingRule == 100130 || this.windingRule == 100131 || this.windingRule == 100132 || this.windingRule == 100133 || this.windingRule == 100134;

         value[value_offset] = (double)this.windingRule;
         break;
      case 100141:
         assert this.boundaryOnly || !this.boundaryOnly;

         value[value_offset] = this.boundaryOnly?1.0D:0.0D;
         break;
      case 100142:
         assert 0.0D <= this.relTolerance && this.relTolerance <= 1.0D;

         value[value_offset] = this.relTolerance;
         break;
      default:
         value[value_offset] = 0.0D;
         this.callErrorOrErrorData(100900);
      }

   }

   public void gluTessNormal(double x, double y, double z) {
      this.normal[0] = x;
      this.normal[1] = y;
      this.normal[2] = z;
   }

   public void gluTessCallback(int which, GLUtessellatorCallback aCallback) {
      switch(which) {
      case 100100:
         this.callBegin = aCallback == null?NULL_CB:aCallback;
         return;
      case 100101:
         this.callVertex = aCallback == null?NULL_CB:aCallback;
         return;
      case 100102:
         this.callEnd = aCallback == null?NULL_CB:aCallback;
         return;
      case 100103:
         this.callError = aCallback == null?NULL_CB:aCallback;
         return;
      case 100104:
         this.callEdgeFlag = aCallback == null?NULL_CB:aCallback;
         this.flagBoundary = aCallback != null;
         return;
      case 100105:
         this.callCombine = aCallback == null?NULL_CB:aCallback;
         return;
      case 100106:
         this.callBeginData = aCallback == null?NULL_CB:aCallback;
         return;
      case 100107:
         this.callVertexData = aCallback == null?NULL_CB:aCallback;
         return;
      case 100108:
         this.callEndData = aCallback == null?NULL_CB:aCallback;
         return;
      case 100109:
         this.callErrorData = aCallback == null?NULL_CB:aCallback;
         return;
      case 100110:
         this.callEdgeFlagData = this.callBegin = aCallback == null?NULL_CB:aCallback;
         this.flagBoundary = aCallback != null;
         return;
      case 100111:
         this.callCombineData = aCallback == null?NULL_CB:aCallback;
         return;
      default:
         this.callErrorOrErrorData(100900);
      }
   }

   private boolean addVertex(double[] coords, Object vertexData) {
      GLUhalfEdge e = this.lastEdge;
      if(e == null) {
         e = Mesh.__gl_meshMakeEdge(this.mesh);
         if(e == null) {
            return false;
         }

         if(!Mesh.__gl_meshSplice(e, e.Sym)) {
            return false;
         }
      } else {
         if(Mesh.__gl_meshSplitEdge(e) == null) {
            return false;
         }

         e = e.Lnext;
      }

      e.Org.data = vertexData;
      e.Org.coords[0] = coords[0];
      e.Org.coords[1] = coords[1];
      e.Org.coords[2] = coords[2];
      e.winding = 1;
      e.Sym.winding = -1;
      this.lastEdge = e;
      return true;
   }

   private void cacheVertex(double[] coords, Object vertexData) {
      if(this.cache[this.cacheCount] == null) {
         this.cache[this.cacheCount] = new CachedVertex();
      }

      CachedVertex v = this.cache[this.cacheCount];
      v.data = vertexData;
      v.coords[0] = coords[0];
      v.coords[1] = coords[1];
      v.coords[2] = coords[2];
      ++this.cacheCount;
   }

   private boolean flushCache() {
      CachedVertex[] v = this.cache;
      this.mesh = Mesh.__gl_meshNewMesh();
      if(this.mesh == null) {
         return false;
      } else {
         for(int i = 0; i < this.cacheCount; ++i) {
            CachedVertex vertex = v[i];
            if(!this.addVertex(vertex.coords, vertex.data)) {
               return false;
            }
         }

         this.cacheCount = 0;
         this.flushCacheOnNextVertex = false;
         return true;
      }
   }

   public void gluTessVertex(double[] coords, int coords_offset, Object vertexData) {
      boolean tooLarge = false;
      double[] clamped = new double[3];
      this.requireState(2);
      if(this.flushCacheOnNextVertex) {
         if(!this.flushCache()) {
            this.callErrorOrErrorData(100902);
            return;
         }

         this.lastEdge = null;
      }

      for(int i = 0; i < 3; ++i) {
         double x = coords[i + coords_offset];
         if(x < -1.0E15D) {
            x = -1.0E15D;
            tooLarge = true;
         }

         if(x > 1.0E15D) {
            x = 1.0E15D;
            tooLarge = true;
         }

         clamped[i] = x;
      }

      if(tooLarge) {
         this.callErrorOrErrorData(100155);
      }

      if(this.mesh == null) {
         if(this.cacheCount < 100) {
            this.cacheVertex(clamped, vertexData);
            return;
         }

         if(!this.flushCache()) {
            this.callErrorOrErrorData(100902);
            return;
         }
      }

      if(!this.addVertex(clamped, vertexData)) {
         this.callErrorOrErrorData(100902);
      }

   }

   public void gluTessBeginPolygon(Object data) {
      this.requireState(0);
      this.state = 1;
      this.cacheCount = 0;
      this.flushCacheOnNextVertex = false;
      this.mesh = null;
      this.polygonData = data;
   }

   public void gluTessBeginContour() {
      this.requireState(1);
      this.state = 2;
      this.lastEdge = null;
      if(this.cacheCount > 0) {
         this.flushCacheOnNextVertex = true;
      }

   }

   public void gluTessEndContour() {
      this.requireState(2);
      this.state = 1;
   }

   public void gluTessEndPolygon() {
      try {
         this.requireState(1);
         this.state = 0;
         if(this.mesh == null) {
            if(!this.flagBoundary && Render.__gl_renderCache(this)) {
               this.polygonData = null;
               return;
            }

            if(!this.flushCache()) {
               throw new RuntimeException();
            }
         }

         Normal.__gl_projectPolygon(this);
         if(!Sweep.__gl_computeInterior(this)) {
            throw new RuntimeException();
         }

         GLUmesh mesh = this.mesh;
         if(!this.fatalError) {
            boolean rc = true;
            if(this.boundaryOnly) {
               rc = TessMono.__gl_meshSetWindingNumber(mesh, 1, true);
            } else {
               rc = TessMono.__gl_meshTessellateInterior(mesh);
            }

            if(!rc) {
               throw new RuntimeException();
            }

            Mesh.__gl_meshCheckMesh(mesh);
            if(this.callBegin != NULL_CB || this.callEnd != NULL_CB || this.callVertex != NULL_CB || this.callEdgeFlag != NULL_CB || this.callBeginData != NULL_CB || this.callEndData != NULL_CB || this.callVertexData != NULL_CB || this.callEdgeFlagData != NULL_CB) {
               if(this.boundaryOnly) {
                  Render.__gl_renderBoundary(this, mesh);
               } else {
                  Render.__gl_renderMesh(this, mesh);
               }
            }
         }

         Mesh.__gl_meshDeleteMesh(mesh);
         this.polygonData = null;
         mesh = null;
      } catch (Exception var3) {
         var3.printStackTrace();
         this.callErrorOrErrorData(100902);
      }

   }

   public void gluBeginPolygon() {
      this.gluTessBeginPolygon((Object)null);
      this.gluTessBeginContour();
   }

   public void gluNextContour(int type) {
      this.gluTessEndContour();
      this.gluTessBeginContour();
   }

   public void gluEndPolygon() {
      this.gluTessEndContour();
      this.gluTessEndPolygon();
   }

   void callBeginOrBeginData(int a) {
      if(this.callBeginData != NULL_CB) {
         this.callBeginData.beginData(a, this.polygonData);
      } else {
         this.callBegin.begin(a);
      }

   }

   void callVertexOrVertexData(Object a) {
      if(this.callVertexData != NULL_CB) {
         this.callVertexData.vertexData(a, this.polygonData);
      } else {
         this.callVertex.vertex(a);
      }

   }

   void callEdgeFlagOrEdgeFlagData(boolean a) {
      if(this.callEdgeFlagData != NULL_CB) {
         this.callEdgeFlagData.edgeFlagData(a, this.polygonData);
      } else {
         this.callEdgeFlag.edgeFlag(a);
      }

   }

   void callEndOrEndData() {
      if(this.callEndData != NULL_CB) {
         this.callEndData.endData(this.polygonData);
      } else {
         this.callEnd.end();
      }

   }

   void callCombineOrCombineData(double[] coords, Object[] vertexData, float[] weights, Object[] outData) {
      if(this.callCombineData != NULL_CB) {
         this.callCombineData.combineData(coords, vertexData, weights, outData, this.polygonData);
      } else {
         this.callCombine.combine(coords, vertexData, weights, outData);
      }

   }

   void callErrorOrErrorData(int a) {
      if(this.callErrorData != NULL_CB) {
         this.callErrorData.errorData(a, this.polygonData);
      } else {
         this.callError.error(a);
      }

   }
}
