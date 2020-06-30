package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.ActiveRegion;
import org.lwjgl.util.glu.tessellation.Dict;
import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUmesh;
import org.lwjgl.util.glu.tessellation.GLUtessellatorImpl;
import org.lwjgl.util.glu.tessellation.GLUvertex;
import org.lwjgl.util.glu.tessellation.Geom;
import org.lwjgl.util.glu.tessellation.Mesh;
import org.lwjgl.util.glu.tessellation.PriorityQ;

class Sweep {
   private static final boolean TOLERANCE_NONZERO = false;
   private static final double SENTINEL_COORD = 4.0E15D;

   private static void DebugEvent(GLUtessellatorImpl tess) {
   }

   private static void AddWinding(GLUhalfEdge eDst, GLUhalfEdge eSrc) {
      eDst.winding += eSrc.winding;
      eDst.Sym.winding += eSrc.Sym.winding;
   }

   private static ActiveRegion RegionBelow(ActiveRegion r) {
      return (ActiveRegion)Dict.dictKey(Dict.dictPred(r.nodeUp));
   }

   private static ActiveRegion RegionAbove(ActiveRegion r) {
      return (ActiveRegion)Dict.dictKey(Dict.dictSucc(r.nodeUp));
   }

   static boolean EdgeLeq(GLUtessellatorImpl tess, ActiveRegion reg1, ActiveRegion reg2) {
      GLUvertex event = tess.event;
      GLUhalfEdge e1 = reg1.eUp;
      GLUhalfEdge e2 = reg2.eUp;
      if(e1.Sym.Org == event) {
         return e2.Sym.Org == event?(Geom.VertLeq(e1.Org, e2.Org)?Geom.EdgeSign(e2.Sym.Org, e1.Org, e2.Org) <= 0.0D:Geom.EdgeSign(e1.Sym.Org, e2.Org, e1.Org) >= 0.0D):Geom.EdgeSign(e2.Sym.Org, event, e2.Org) <= 0.0D;
      } else if(e2.Sym.Org == event) {
         return Geom.EdgeSign(e1.Sym.Org, event, e1.Org) >= 0.0D;
      } else {
         double t1 = Geom.EdgeEval(e1.Sym.Org, event, e1.Org);
         double t2 = Geom.EdgeEval(e2.Sym.Org, event, e2.Org);
         return t1 >= t2;
      }
   }

   static void DeleteRegion(GLUtessellatorImpl tess, ActiveRegion reg) {
      assert !reg.fixUpperEdge || reg.eUp.winding == 0;

      reg.eUp.activeRegion = null;
      Dict.dictDelete(tess.dict, reg.nodeUp);
   }

   static boolean FixUpperEdge(ActiveRegion reg, GLUhalfEdge newEdge) {
      assert reg.fixUpperEdge;

      if(!Mesh.__gl_meshDelete(reg.eUp)) {
         return false;
      } else {
         reg.fixUpperEdge = false;
         reg.eUp = newEdge;
         newEdge.activeRegion = reg;
         return true;
      }
   }

   static ActiveRegion TopLeftRegion(ActiveRegion reg) {
      GLUvertex org = reg.eUp.Org;

      while(true) {
         reg = RegionAbove(reg);
         if(reg.eUp.Org != org) {
            break;
         }
      }

      if(reg.fixUpperEdge) {
         GLUhalfEdge e = Mesh.__gl_meshConnect(RegionBelow(reg).eUp.Sym, reg.eUp.Lnext);
         if(e == null) {
            return null;
         }

         if(!FixUpperEdge(reg, e)) {
            return null;
         }

         reg = RegionAbove(reg);
      }

      return reg;
   }

   static ActiveRegion TopRightRegion(ActiveRegion reg) {
      GLUvertex dst = reg.eUp.Sym.Org;

      while(true) {
         reg = RegionAbove(reg);
         if(reg.eUp.Sym.Org != dst) {
            break;
         }
      }

      return reg;
   }

   static ActiveRegion AddRegionBelow(GLUtessellatorImpl tess, ActiveRegion regAbove, GLUhalfEdge eNewUp) {
      ActiveRegion regNew = new ActiveRegion();
      if(regNew == null) {
         throw new RuntimeException();
      } else {
         regNew.eUp = eNewUp;
         regNew.nodeUp = Dict.dictInsertBefore(tess.dict, regAbove.nodeUp, regNew);
         if(regNew.nodeUp == null) {
            throw new RuntimeException();
         } else {
            regNew.fixUpperEdge = false;
            regNew.sentinel = false;
            regNew.dirty = false;
            eNewUp.activeRegion = regNew;
            return regNew;
         }
      }
   }

   static boolean IsWindingInside(GLUtessellatorImpl tess, int n) {
      switch(tess.windingRule) {
      case 100130:
         return (n & 1) != 0;
      case 100131:
         return n != 0;
      case 100132:
         return n > 0;
      case 100133:
         return n < 0;
      case 100134:
         return n >= 2 || n <= -2;
      default:
         throw new InternalError();
      }
   }

   static void ComputeWinding(GLUtessellatorImpl tess, ActiveRegion reg) {
      reg.windingNumber = RegionAbove(reg).windingNumber + reg.eUp.winding;
      reg.inside = IsWindingInside(tess, reg.windingNumber);
   }

   static void FinishRegion(GLUtessellatorImpl tess, ActiveRegion reg) {
      GLUhalfEdge e = reg.eUp;
      GLUface f = e.Lface;
      f.inside = reg.inside;
      f.anEdge = e;
      DeleteRegion(tess, reg);
   }

   static GLUhalfEdge FinishLeftRegions(GLUtessellatorImpl tess, ActiveRegion regFirst, ActiveRegion regLast) {
      ActiveRegion regPrev = regFirst;

      ActiveRegion reg;
      GLUhalfEdge ePrev;
      for(ePrev = regFirst.eUp; regPrev != regLast; regPrev = reg) {
         regPrev.fixUpperEdge = false;
         reg = RegionBelow(regPrev);
         GLUhalfEdge e = reg.eUp;
         if(e.Org != ePrev.Org) {
            if(!reg.fixUpperEdge) {
               FinishRegion(tess, regPrev);
               break;
            }

            e = Mesh.__gl_meshConnect(ePrev.Onext.Sym, e.Sym);
            if(e == null) {
               throw new RuntimeException();
            }

            if(!FixUpperEdge(reg, e)) {
               throw new RuntimeException();
            }
         }

         if(ePrev.Onext != e) {
            if(!Mesh.__gl_meshSplice(e.Sym.Lnext, e)) {
               throw new RuntimeException();
            }

            if(!Mesh.__gl_meshSplice(ePrev, e)) {
               throw new RuntimeException();
            }
         }

         FinishRegion(tess, regPrev);
         ePrev = reg.eUp;
      }

      return ePrev;
   }

   static void AddRightEdges(GLUtessellatorImpl tess, ActiveRegion regUp, GLUhalfEdge eFirst, GLUhalfEdge eLast, GLUhalfEdge eTopLeft, boolean cleanUp) {
      boolean firstTime = true;
      GLUhalfEdge e = eFirst;

      while($assertionsDisabled || Geom.VertLeq(e.Org, e.Sym.Org)) {
         AddRegionBelow(tess, regUp, e.Sym);
         e = e.Onext;
         if(e == eLast) {
            if(eTopLeft == null) {
               eTopLeft = RegionBelow(regUp).eUp.Sym.Onext;
            }

            ActiveRegion regPrev = regUp;
            GLUhalfEdge ePrev = eTopLeft;

            while(true) {
               ActiveRegion reg = RegionBelow(regPrev);
               e = reg.eUp.Sym;
               if(e.Org != ePrev.Org) {
                  regPrev.dirty = true;

                  assert regPrev.windingNumber - e.winding == reg.windingNumber;

                  if(cleanUp) {
                     WalkDirtyRegions(tess, regPrev);
                  }

                  return;
               }

               if(e.Onext != ePrev) {
                  if(!Mesh.__gl_meshSplice(e.Sym.Lnext, e)) {
                     throw new RuntimeException();
                  }

                  if(!Mesh.__gl_meshSplice(ePrev.Sym.Lnext, e)) {
                     throw new RuntimeException();
                  }
               }

               reg.windingNumber = regPrev.windingNumber - e.winding;
               reg.inside = IsWindingInside(tess, reg.windingNumber);
               regPrev.dirty = true;
               if(!firstTime && CheckForRightSplice(tess, regPrev)) {
                  AddWinding(e, ePrev);
                  DeleteRegion(tess, regPrev);
                  if(!Mesh.__gl_meshDelete(ePrev)) {
                     throw new RuntimeException();
                  }
               }

               firstTime = false;
               regPrev = reg;
               ePrev = e;
            }
         }
      }

      throw new AssertionError();
   }

   static void CallCombine(GLUtessellatorImpl tess, GLUvertex isect, Object[] data, float[] weights, boolean needed) {
      double[] coords = new double[]{isect.coords[0], isect.coords[1], isect.coords[2]};
      Object[] outData = new Object[1];
      tess.callCombineOrCombineData(coords, data, weights, outData);
      isect.data = outData[0];
      if(isect.data == null) {
         if(!needed) {
            isect.data = data[0];
         } else if(!tess.fatalError) {
            tess.callErrorOrErrorData(100156);
            tess.fatalError = true;
         }
      }

   }

   static void SpliceMergeVertices(GLUtessellatorImpl tess, GLUhalfEdge e1, GLUhalfEdge e2) {
      Object[] data = new Object[4];
      float[] weights = new float[]{0.5F, 0.5F, 0.0F, 0.0F};
      data[0] = e1.Org.data;
      data[1] = e2.Org.data;
      CallCombine(tess, e1.Org, data, weights, false);
      if(!Mesh.__gl_meshSplice(e1, e2)) {
         throw new RuntimeException();
      }
   }

   static void VertexWeights(GLUvertex isect, GLUvertex org, GLUvertex dst, float[] weights) {
      double t1 = Geom.VertL1dist(org, isect);
      double t2 = Geom.VertL1dist(dst, isect);
      weights[0] = (float)(0.5D * t2 / (t1 + t2));
      weights[1] = (float)(0.5D * t1 / (t1 + t2));
      isect.coords[0] += (double)weights[0] * org.coords[0] + (double)weights[1] * dst.coords[0];
      isect.coords[1] += (double)weights[0] * org.coords[1] + (double)weights[1] * dst.coords[1];
      isect.coords[2] += (double)weights[0] * org.coords[2] + (double)weights[1] * dst.coords[2];
   }

   static void GetIntersectData(GLUtessellatorImpl tess, GLUvertex isect, GLUvertex orgUp, GLUvertex dstUp, GLUvertex orgLo, GLUvertex dstLo) {
      Object[] data = new Object[4];
      float[] weights = new float[4];
      float[] weights1 = new float[2];
      float[] weights2 = new float[2];
      data[0] = orgUp.data;
      data[1] = dstUp.data;
      data[2] = orgLo.data;
      data[3] = dstLo.data;
      isect.coords[0] = isect.coords[1] = isect.coords[2] = 0.0D;
      VertexWeights(isect, orgUp, dstUp, weights1);
      VertexWeights(isect, orgLo, dstLo, weights2);
      System.arraycopy(weights1, 0, weights, 0, 2);
      System.arraycopy(weights2, 0, weights, 2, 2);
      CallCombine(tess, isect, data, weights, true);
   }

   static boolean CheckForRightSplice(GLUtessellatorImpl tess, ActiveRegion regUp) {
      ActiveRegion regLo = RegionBelow(regUp);
      GLUhalfEdge eUp = regUp.eUp;
      GLUhalfEdge eLo = regLo.eUp;
      if(Geom.VertLeq(eUp.Org, eLo.Org)) {
         if(Geom.EdgeSign(eLo.Sym.Org, eUp.Org, eLo.Org) > 0.0D) {
            return false;
         }

         if(!Geom.VertEq(eUp.Org, eLo.Org)) {
            if(Mesh.__gl_meshSplitEdge(eLo.Sym) == null) {
               throw new RuntimeException();
            }

            if(!Mesh.__gl_meshSplice(eUp, eLo.Sym.Lnext)) {
               throw new RuntimeException();
            }

            regUp.dirty = regLo.dirty = true;
         } else if(eUp.Org != eLo.Org) {
            tess.pq.pqDelete(eUp.Org.pqHandle);
            SpliceMergeVertices(tess, eLo.Sym.Lnext, eUp);
         }
      } else {
         if(Geom.EdgeSign(eUp.Sym.Org, eLo.Org, eUp.Org) < 0.0D) {
            return false;
         }

         RegionAbove(regUp).dirty = regUp.dirty = true;
         if(Mesh.__gl_meshSplitEdge(eUp.Sym) == null) {
            throw new RuntimeException();
         }

         if(!Mesh.__gl_meshSplice(eLo.Sym.Lnext, eUp)) {
            throw new RuntimeException();
         }
      }

      return true;
   }

   static boolean CheckForLeftSplice(GLUtessellatorImpl tess, ActiveRegion regUp) {
      ActiveRegion regLo = RegionBelow(regUp);
      GLUhalfEdge eUp = regUp.eUp;
      GLUhalfEdge eLo = regLo.eUp;

      assert !Geom.VertEq(eUp.Sym.Org, eLo.Sym.Org);

      if(Geom.VertLeq(eUp.Sym.Org, eLo.Sym.Org)) {
         if(Geom.EdgeSign(eUp.Sym.Org, eLo.Sym.Org, eUp.Org) < 0.0D) {
            return false;
         }

         RegionAbove(regUp).dirty = regUp.dirty = true;
         GLUhalfEdge e = Mesh.__gl_meshSplitEdge(eUp);
         if(e == null) {
            throw new RuntimeException();
         }

         if(!Mesh.__gl_meshSplice(eLo.Sym, e)) {
            throw new RuntimeException();
         }

         e.Lface.inside = regUp.inside;
      } else {
         if(Geom.EdgeSign(eLo.Sym.Org, eUp.Sym.Org, eLo.Org) > 0.0D) {
            return false;
         }

         regUp.dirty = regLo.dirty = true;
         GLUhalfEdge e = Mesh.__gl_meshSplitEdge(eLo);
         if(e == null) {
            throw new RuntimeException();
         }

         if(!Mesh.__gl_meshSplice(eUp.Lnext, eLo.Sym)) {
            throw new RuntimeException();
         }

         e.Sym.Lface.inside = regUp.inside;
      }

      return true;
   }

   static boolean CheckForIntersect(GLUtessellatorImpl tess, ActiveRegion regUp) {
      ActiveRegion regLo = RegionBelow(regUp);
      GLUhalfEdge eUp = regUp.eUp;
      GLUhalfEdge eLo = regLo.eUp;
      GLUvertex orgUp = eUp.Org;
      GLUvertex orgLo = eLo.Org;
      GLUvertex dstUp = eUp.Sym.Org;
      GLUvertex dstLo = eLo.Sym.Org;
      GLUvertex isect = new GLUvertex();

      assert !Geom.VertEq(dstLo, dstUp);

      assert Geom.EdgeSign(dstUp, tess.event, orgUp) <= 0.0D;

      assert Geom.EdgeSign(dstLo, tess.event, orgLo) >= 0.0D;

      if($assertionsDisabled || orgUp != tess.event && orgLo != tess.event) {
         if($assertionsDisabled || !regUp.fixUpperEdge && !regLo.fixUpperEdge) {
            if(orgUp == orgLo) {
               return false;
            } else {
               double tMinUp = Math.min(orgUp.t, dstUp.t);
               double tMaxLo = Math.max(orgLo.t, dstLo.t);
               if(tMinUp > tMaxLo) {
                  return false;
               } else {
                  if(Geom.VertLeq(orgUp, orgLo)) {
                     if(Geom.EdgeSign(dstLo, orgUp, orgLo) > 0.0D) {
                        return false;
                     }
                  } else if(Geom.EdgeSign(dstUp, orgLo, orgUp) < 0.0D) {
                     return false;
                  }

                  DebugEvent(tess);
                  Geom.EdgeIntersect(dstUp, orgUp, dstLo, orgLo, isect);

                  assert Math.min(orgUp.t, dstUp.t) <= isect.t;

                  assert isect.t <= Math.max(orgLo.t, dstLo.t);

                  assert Math.min(dstLo.s, dstUp.s) <= isect.s;

                  assert isect.s <= Math.max(orgLo.s, orgUp.s);

                  if(Geom.VertLeq(isect, tess.event)) {
                     isect.s = tess.event.s;
                     isect.t = tess.event.t;
                  }

                  GLUvertex orgMin = Geom.VertLeq(orgUp, orgLo)?orgUp:orgLo;
                  if(Geom.VertLeq(orgMin, isect)) {
                     isect.s = orgMin.s;
                     isect.t = orgMin.t;
                  }

                  if(!Geom.VertEq(isect, orgUp) && !Geom.VertEq(isect, orgLo)) {
                     if((Geom.VertEq(dstUp, tess.event) || Geom.EdgeSign(dstUp, tess.event, isect) < 0.0D) && (Geom.VertEq(dstLo, tess.event) || Geom.EdgeSign(dstLo, tess.event, isect) > 0.0D)) {
                        if(Mesh.__gl_meshSplitEdge(eUp.Sym) == null) {
                           throw new RuntimeException();
                        } else if(Mesh.__gl_meshSplitEdge(eLo.Sym) == null) {
                           throw new RuntimeException();
                        } else if(!Mesh.__gl_meshSplice(eLo.Sym.Lnext, eUp)) {
                           throw new RuntimeException();
                        } else {
                           eUp.Org.s = isect.s;
                           eUp.Org.t = isect.t;
                           eUp.Org.pqHandle = tess.pq.pqInsert(eUp.Org);
                           if((long)eUp.Org.pqHandle == Long.MAX_VALUE) {
                              tess.pq.pqDeletePriorityQ();
                              tess.pq = null;
                              throw new RuntimeException();
                           } else {
                              GetIntersectData(tess, eUp.Org, orgUp, dstUp, orgLo, dstLo);
                              RegionAbove(regUp).dirty = regUp.dirty = regLo.dirty = true;
                              return false;
                           }
                        }
                     } else if(dstLo == tess.event) {
                        if(Mesh.__gl_meshSplitEdge(eUp.Sym) == null) {
                           throw new RuntimeException();
                        } else if(!Mesh.__gl_meshSplice(eLo.Sym, eUp)) {
                           throw new RuntimeException();
                        } else {
                           regUp = TopLeftRegion(regUp);
                           if(regUp == null) {
                              throw new RuntimeException();
                           } else {
                              eUp = RegionBelow(regUp).eUp;
                              FinishLeftRegions(tess, RegionBelow(regUp), regLo);
                              AddRightEdges(tess, regUp, eUp.Sym.Lnext, eUp, eUp, true);
                              return true;
                           }
                        }
                     } else if(dstUp == tess.event) {
                        if(Mesh.__gl_meshSplitEdge(eLo.Sym) == null) {
                           throw new RuntimeException();
                        } else if(!Mesh.__gl_meshSplice(eUp.Lnext, eLo.Sym.Lnext)) {
                           throw new RuntimeException();
                        } else {
                           regLo = regUp;
                           regUp = TopRightRegion(regUp);
                           GLUhalfEdge e = RegionBelow(regUp).eUp.Sym.Onext;
                           regLo.eUp = eLo.Sym.Lnext;
                           eLo = FinishLeftRegions(tess, regLo, (ActiveRegion)null);
                           AddRightEdges(tess, regUp, eLo.Onext, eUp.Sym.Onext, e, true);
                           return true;
                        }
                     } else {
                        if(Geom.EdgeSign(dstUp, tess.event, isect) >= 0.0D) {
                           RegionAbove(regUp).dirty = regUp.dirty = true;
                           if(Mesh.__gl_meshSplitEdge(eUp.Sym) == null) {
                              throw new RuntimeException();
                           }

                           eUp.Org.s = tess.event.s;
                           eUp.Org.t = tess.event.t;
                        }

                        if(Geom.EdgeSign(dstLo, tess.event, isect) <= 0.0D) {
                           regUp.dirty = regLo.dirty = true;
                           if(Mesh.__gl_meshSplitEdge(eLo.Sym) == null) {
                              throw new RuntimeException();
                           }

                           eLo.Org.s = tess.event.s;
                           eLo.Org.t = tess.event.t;
                        }

                        return false;
                     }
                  } else {
                     CheckForRightSplice(tess, regUp);
                     return false;
                  }
               }
            }
         } else {
            throw new AssertionError();
         }
      } else {
         throw new AssertionError();
      }
   }

   static void WalkDirtyRegions(GLUtessellatorImpl tess, ActiveRegion regUp) {
      ActiveRegion regLo = RegionBelow(regUp);

      while(true) {
         while(regLo.dirty) {
            regUp = regLo;
            regLo = RegionBelow(regLo);
         }

         if(!regUp.dirty) {
            regLo = regUp;
            regUp = RegionAbove(regUp);
            if(regUp == null || !regUp.dirty) {
               return;
            }
         }

         regUp.dirty = false;
         GLUhalfEdge eUp = regUp.eUp;
         GLUhalfEdge eLo = regLo.eUp;
         if(eUp.Sym.Org != eLo.Sym.Org && CheckForLeftSplice(tess, regUp)) {
            if(regLo.fixUpperEdge) {
               DeleteRegion(tess, regLo);
               if(!Mesh.__gl_meshDelete(eLo)) {
                  throw new RuntimeException();
               }

               regLo = RegionBelow(regUp);
               eLo = regLo.eUp;
            } else if(regUp.fixUpperEdge) {
               DeleteRegion(tess, regUp);
               if(!Mesh.__gl_meshDelete(eUp)) {
                  throw new RuntimeException();
               }

               regUp = RegionAbove(regLo);
               eUp = regUp.eUp;
            }
         }

         if(eUp.Org != eLo.Org) {
            if(eUp.Sym.Org != eLo.Sym.Org && !regUp.fixUpperEdge && !regLo.fixUpperEdge && (eUp.Sym.Org == tess.event || eLo.Sym.Org == tess.event)) {
               if(CheckForIntersect(tess, regUp)) {
                  return;
               }
            } else {
               CheckForRightSplice(tess, regUp);
            }
         }

         if(eUp.Org == eLo.Org && eUp.Sym.Org == eLo.Sym.Org) {
            AddWinding(eLo, eUp);
            DeleteRegion(tess, regUp);
            if(!Mesh.__gl_meshDelete(eUp)) {
               throw new RuntimeException();
            }

            regUp = RegionAbove(regLo);
         }
      }
   }

   static void ConnectRightVertex(GLUtessellatorImpl tess, ActiveRegion regUp, GLUhalfEdge eBottomLeft) {
      GLUhalfEdge eTopLeft = eBottomLeft.Onext;
      ActiveRegion regLo = RegionBelow(regUp);
      GLUhalfEdge eUp = regUp.eUp;
      GLUhalfEdge eLo = regLo.eUp;
      boolean degenerate = false;
      if(eUp.Sym.Org != eLo.Sym.Org) {
         CheckForIntersect(tess, regUp);
      }

      if(Geom.VertEq(eUp.Org, tess.event)) {
         if(!Mesh.__gl_meshSplice(eTopLeft.Sym.Lnext, eUp)) {
            throw new RuntimeException();
         }

         regUp = TopLeftRegion(regUp);
         if(regUp == null) {
            throw new RuntimeException();
         }

         eTopLeft = RegionBelow(regUp).eUp;
         FinishLeftRegions(tess, RegionBelow(regUp), regLo);
         degenerate = true;
      }

      if(Geom.VertEq(eLo.Org, tess.event)) {
         if(!Mesh.__gl_meshSplice(eBottomLeft, eLo.Sym.Lnext)) {
            throw new RuntimeException();
         }

         eBottomLeft = FinishLeftRegions(tess, regLo, (ActiveRegion)null);
         degenerate = true;
      }

      if(degenerate) {
         AddRightEdges(tess, regUp, eBottomLeft.Onext, eTopLeft, eTopLeft, true);
      } else {
         GLUhalfEdge eNew;
         if(Geom.VertLeq(eLo.Org, eUp.Org)) {
            eNew = eLo.Sym.Lnext;
         } else {
            eNew = eUp;
         }

         eNew = Mesh.__gl_meshConnect(eBottomLeft.Onext.Sym, eNew);
         if(eNew == null) {
            throw new RuntimeException();
         } else {
            AddRightEdges(tess, regUp, eNew, eNew.Onext, eNew.Onext, false);
            eNew.Sym.activeRegion.fixUpperEdge = true;
            WalkDirtyRegions(tess, regUp);
         }
      }
   }

   static void ConnectLeftDegenerate(GLUtessellatorImpl tess, ActiveRegion regUp, GLUvertex vEvent) {
      GLUhalfEdge e = regUp.eUp;
      if(Geom.VertEq(e.Org, vEvent)) {
         assert false;

         SpliceMergeVertices(tess, e, vEvent.anEdge);
      } else if(!Geom.VertEq(e.Sym.Org, vEvent)) {
         if(Mesh.__gl_meshSplitEdge(e.Sym) == null) {
            throw new RuntimeException();
         } else {
            if(regUp.fixUpperEdge) {
               if(!Mesh.__gl_meshDelete(e.Onext)) {
                  throw new RuntimeException();
               }

               regUp.fixUpperEdge = false;
            }

            if(!Mesh.__gl_meshSplice(vEvent.anEdge, e)) {
               throw new RuntimeException();
            } else {
               SweepEvent(tess, vEvent);
            }
         }
      } else {
         assert false;

         regUp = TopRightRegion(regUp);
         ActiveRegion reg = RegionBelow(regUp);
         GLUhalfEdge eTopRight = reg.eUp.Sym;
         GLUhalfEdge eLast = eTopRight.Onext;
         GLUhalfEdge eTopLeft = eTopRight.Onext;
         if(reg.fixUpperEdge) {
            assert eTopLeft != eTopRight;

            DeleteRegion(tess, reg);
            if(!Mesh.__gl_meshDelete(eTopRight)) {
               throw new RuntimeException();
            }

            eTopRight = eTopLeft.Sym.Lnext;
         }

         if(!Mesh.__gl_meshSplice(vEvent.anEdge, eTopRight)) {
            throw new RuntimeException();
         } else {
            if(!Geom.EdgeGoesLeft(eTopLeft)) {
               eTopLeft = null;
            }

            AddRightEdges(tess, regUp, eTopRight.Onext, eLast, eTopLeft, true);
         }
      }
   }

   static void ConnectLeftVertex(GLUtessellatorImpl tess, GLUvertex vEvent) {
      ActiveRegion tmp = new ActiveRegion();
      tmp.eUp = vEvent.anEdge.Sym;
      ActiveRegion regUp = (ActiveRegion)Dict.dictKey(Dict.dictSearch(tess.dict, tmp));
      ActiveRegion regLo = RegionBelow(regUp);
      GLUhalfEdge eUp = regUp.eUp;
      GLUhalfEdge eLo = regLo.eUp;
      if(Geom.EdgeSign(eUp.Sym.Org, vEvent, eUp.Org) == 0.0D) {
         ConnectLeftDegenerate(tess, regUp, vEvent);
      } else {
         ActiveRegion reg = Geom.VertLeq(eLo.Sym.Org, eUp.Sym.Org)?regUp:regLo;
         if(!regUp.inside && !reg.fixUpperEdge) {
            AddRightEdges(tess, regUp, vEvent.anEdge, vEvent.anEdge, (GLUhalfEdge)null, true);
         } else {
            GLUhalfEdge eNew;
            if(reg == regUp) {
               eNew = Mesh.__gl_meshConnect(vEvent.anEdge.Sym, eUp.Lnext);
               if(eNew == null) {
                  throw new RuntimeException();
               }
            } else {
               GLUhalfEdge tempHalfEdge = Mesh.__gl_meshConnect(eLo.Sym.Onext.Sym, vEvent.anEdge);
               if(tempHalfEdge == null) {
                  throw new RuntimeException();
               }

               eNew = tempHalfEdge.Sym;
            }

            if(reg.fixUpperEdge) {
               if(!FixUpperEdge(reg, eNew)) {
                  throw new RuntimeException();
               }
            } else {
               ComputeWinding(tess, AddRegionBelow(tess, regUp, eNew));
            }

            SweepEvent(tess, vEvent);
         }

      }
   }

   static void SweepEvent(GLUtessellatorImpl tess, GLUvertex vEvent) {
      tess.event = vEvent;
      DebugEvent(tess);
      GLUhalfEdge e = vEvent.anEdge;

      while(e.activeRegion == null) {
         e = e.Onext;
         if(e == vEvent.anEdge) {
            ConnectLeftVertex(tess, vEvent);
            return;
         }
      }

      ActiveRegion regUp = TopLeftRegion(e.activeRegion);
      if(regUp == null) {
         throw new RuntimeException();
      } else {
         ActiveRegion reg = RegionBelow(regUp);
         GLUhalfEdge eTopLeft = reg.eUp;
         GLUhalfEdge eBottomLeft = FinishLeftRegions(tess, reg, (ActiveRegion)null);
         if(eBottomLeft.Onext == eTopLeft) {
            ConnectRightVertex(tess, regUp, eBottomLeft);
         } else {
            AddRightEdges(tess, regUp, eBottomLeft.Onext, eTopLeft, eTopLeft, true);
         }

      }
   }

   static void AddSentinel(GLUtessellatorImpl tess, double t) {
      ActiveRegion reg = new ActiveRegion();
      if(reg == null) {
         throw new RuntimeException();
      } else {
         GLUhalfEdge e = Mesh.__gl_meshMakeEdge(tess.mesh);
         if(e == null) {
            throw new RuntimeException();
         } else {
            e.Org.s = 4.0E15D;
            e.Org.t = t;
            e.Sym.Org.s = -4.0E15D;
            e.Sym.Org.t = t;
            tess.event = e.Sym.Org;
            reg.eUp = e;
            reg.windingNumber = 0;
            reg.inside = false;
            reg.fixUpperEdge = false;
            reg.sentinel = true;
            reg.dirty = false;
            reg.nodeUp = Dict.dictInsert(tess.dict, reg);
            if(reg.nodeUp == null) {
               throw new RuntimeException();
            }
         }
      }
   }

   static void InitEdgeDict(final GLUtessellatorImpl tess) {
      tess.dict = Dict.dictNewDict(tess, new Dict.DictLeq() {
         public boolean leq(Object frame, Object key1, Object key2) {
            return Sweep.EdgeLeq(tess, (ActiveRegion)key1, (ActiveRegion)key2);
         }
      });
      if(tess.dict == null) {
         throw new RuntimeException();
      } else {
         AddSentinel(tess, -4.0E15D);
         AddSentinel(tess, 4.0E15D);
      }
   }

   static void DoneEdgeDict(GLUtessellatorImpl tess) {
      int fixedEdges = 0;

      ActiveRegion reg;
      while((reg = (ActiveRegion)Dict.dictKey(Dict.dictMin(tess.dict))) != null) {
         if(!reg.sentinel) {
            assert reg.fixUpperEdge;

            if(!$assertionsDisabled) {
               ++fixedEdges;
               if(fixedEdges != 1) {
                  throw new AssertionError();
               }
            }
         }

         assert reg.windingNumber == 0;

         DeleteRegion(tess, reg);
      }

      Dict.dictDeleteDict(tess.dict);
   }

   static void RemoveDegenerateEdges(GLUtessellatorImpl tess) {
      GLUhalfEdge eHead = tess.mesh.eHead;

      GLUhalfEdge eNext;
      for(GLUhalfEdge e = eHead.next; e != eHead; e = eNext) {
         eNext = e.next;
         GLUhalfEdge eLnext = e.Lnext;
         if(Geom.VertEq(e.Org, e.Sym.Org) && e.Lnext.Lnext != e) {
            SpliceMergeVertices(tess, eLnext, e);
            if(!Mesh.__gl_meshDelete(e)) {
               throw new RuntimeException();
            }

            e = eLnext;
            eLnext = eLnext.Lnext;
         }

         if(eLnext.Lnext == e) {
            if(eLnext != e) {
               if(eLnext == eNext || eLnext == eNext.Sym) {
                  eNext = eNext.next;
               }

               if(!Mesh.__gl_meshDelete(eLnext)) {
                  throw new RuntimeException();
               }
            }

            if(e == eNext || e == eNext.Sym) {
               eNext = eNext.next;
            }

            if(!Mesh.__gl_meshDelete(e)) {
               throw new RuntimeException();
            }
         }
      }

   }

   static boolean InitPriorityQ(GLUtessellatorImpl tess) {
      PriorityQ pq = tess.pq = PriorityQ.pqNewPriorityQ(new PriorityQ.Leq() {
         public boolean leq(Object key1, Object key2) {
            return Geom.VertLeq((GLUvertex)key1, (GLUvertex)key2);
         }
      });
      if(pq == null) {
         return false;
      } else {
         GLUvertex vHead = tess.mesh.vHead;

         GLUvertex v;
         for(v = vHead.next; v != vHead; v = v.next) {
            v.pqHandle = pq.pqInsert(v);
            if((long)v.pqHandle == Long.MAX_VALUE) {
               break;
            }
         }

         if(v == vHead && pq.pqInit()) {
            return true;
         } else {
            tess.pq.pqDeletePriorityQ();
            tess.pq = null;
            return false;
         }
      }
   }

   static void DonePriorityQ(GLUtessellatorImpl tess) {
      tess.pq.pqDeletePriorityQ();
   }

   static boolean RemoveDegenerateFaces(GLUmesh mesh) {
      GLUface fNext;
      for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = fNext) {
         fNext = f.next;
         GLUhalfEdge e = f.anEdge;

         assert e.Lnext != e;

         if(e.Lnext.Lnext == e) {
            AddWinding(e.Onext, e);
            if(!Mesh.__gl_meshDelete(e)) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean __gl_computeInterior(GLUtessellatorImpl tess) {
      tess.fatalError = false;
      RemoveDegenerateEdges(tess);
      if(!InitPriorityQ(tess)) {
         return false;
      } else {
         InitEdgeDict(tess);

         GLUvertex v;
         for(; (v = (GLUvertex)tess.pq.pqExtractMin()) != null; SweepEvent(tess, v)) {
            GLUvertex vNext = (GLUvertex)tess.pq.pqMinimum();
            if(vNext != null && Geom.VertEq(vNext, v)) {
               vNext = (GLUvertex)tess.pq.pqExtractMin();
               SpliceMergeVertices(tess, v.anEdge, vNext.anEdge);
            }
         }

         tess.event = ((ActiveRegion)Dict.dictKey(Dict.dictMin(tess.dict))).eUp.Org;
         DebugEvent(tess);
         DoneEdgeDict(tess);
         DonePriorityQ(tess);
         if(!RemoveDegenerateFaces(tess.mesh)) {
            return false;
         } else {
            Mesh.__gl_meshCheckMesh(tess.mesh);
            return true;
         }
      }
   }
}
