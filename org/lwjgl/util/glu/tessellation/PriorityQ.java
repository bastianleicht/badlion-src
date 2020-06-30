package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUvertex;
import org.lwjgl.util.glu.tessellation.Geom;
import org.lwjgl.util.glu.tessellation.PriorityQSort;

abstract class PriorityQ {
   public static final int INIT_SIZE = 32;

   public static boolean LEQ(PriorityQ.Leq leq, Object x, Object y) {
      return Geom.VertLeq((GLUvertex)x, (GLUvertex)y);
   }

   static PriorityQ pqNewPriorityQ(PriorityQ.Leq leq) {
      return new PriorityQSort(leq);
   }

   abstract void pqDeletePriorityQ();

   abstract boolean pqInit();

   abstract int pqInsert(Object var1);

   abstract Object pqExtractMin();

   abstract void pqDelete(int var1);

   abstract Object pqMinimum();

   abstract boolean pqIsEmpty();

   public interface Leq {
      boolean leq(Object var1, Object var2);
   }

   public static class PQhandleElem {
      Object key;
      int node;
   }

   public static class PQnode {
      int handle;
   }
}
