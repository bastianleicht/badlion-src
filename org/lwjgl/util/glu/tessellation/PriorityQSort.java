package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.PriorityQ;
import org.lwjgl.util.glu.tessellation.PriorityQHeap;

class PriorityQSort extends PriorityQ {
   PriorityQHeap heap;
   Object[] keys;
   int[] order;
   int size;
   int max;
   boolean initialized;
   PriorityQ.Leq leq;

   PriorityQSort(PriorityQ.Leq leq) {
      this.heap = new PriorityQHeap(leq);
      this.keys = new Object[32];
      this.size = 0;
      this.max = 32;
      this.initialized = false;
      this.leq = leq;
   }

   void pqDeletePriorityQ() {
      if(this.heap != null) {
         this.heap.pqDeletePriorityQ();
      }

      this.order = null;
      this.keys = null;
   }

   private static boolean LT(PriorityQ.Leq leq, Object x, Object y) {
      return !PriorityQHeap.LEQ(leq, y, x);
   }

   private static boolean GT(PriorityQ.Leq leq, Object x, Object y) {
      return !PriorityQHeap.LEQ(leq, x, y);
   }

   private static void Swap(int[] array, int a, int b) {
      int tmp = array[a];
      array[a] = array[b];
      array[b] = tmp;
   }

   boolean pqInit() {
      PriorityQSort.Stack[] stack = new PriorityQSort.Stack[50];

      for(int k = 0; k < stack.length; ++k) {
         stack[k] = new PriorityQSort.Stack();
      }

      int top = 0;
      int seed = 2016473283;
      this.order = new int[this.size + 1];
      int p = 0;
      int r = this.size - 1;
      int piv = 0;

      for(int i = p; i <= r; ++i) {
         this.order[i] = piv++;
      }

      stack[top].p = p;
      stack[top].r = r;
      ++top;

      while(true) {
         --top;
         if(top < 0) {
            this.max = this.size;
            this.initialized = true;
            this.heap.pqInit();
            return true;
         }

         p = stack[top].p;
         r = stack[top].r;

         while(r > p + 10) {
            seed = Math.abs(seed * 1539415821 + 1);
            int var11 = p + seed % (r - p + 1);
            piv = this.order[var11];
            this.order[var11] = this.order[p];
            this.order[p] = piv;
            var11 = p - 1;
            int j = r + 1;

            while(true) {
               ++var11;
               if(!GT(this.leq, this.keys[this.order[var11]], this.keys[piv])) {
                  while(true) {
                     --j;
                     if(!LT(this.leq, this.keys[this.order[j]], this.keys[piv])) {
                        break;
                     }
                  }

                  Swap(this.order, var11, j);
                  if(var11 >= j) {
                     Swap(this.order, var11, j);
                     if(var11 - p < r - j) {
                        stack[top].p = j + 1;
                        stack[top].r = r;
                        ++top;
                        r = var11 - 1;
                     } else {
                        stack[top].p = p;
                        stack[top].r = var11 - 1;
                        ++top;
                        p = j + 1;
                     }
                     break;
                  }
               }
            }
         }

         for(int var13 = p + 1; var13 <= r; ++var13) {
            piv = this.order[var13];

            int j;
            for(j = var13; j > p && LT(this.leq, this.keys[this.order[j - 1]], this.keys[piv]); --j) {
               this.order[j] = this.order[j - 1];
            }

            this.order[j] = piv;
         }
      }
   }

   int pqInsert(Object keyNew) {
      if(this.initialized) {
         return this.heap.pqInsert(keyNew);
      } else {
         int curr = this.size;
         if(++this.size >= this.max) {
            Object[] saveKey = this.keys;
            this.max <<= 1;
            Object[] pqKeys = new Object[this.max];
            System.arraycopy(this.keys, 0, pqKeys, 0, this.keys.length);
            this.keys = pqKeys;
            if(this.keys == null) {
               this.keys = saveKey;
               return Integer.MAX_VALUE;
            }
         }

         assert curr != Integer.MAX_VALUE;

         this.keys[curr] = keyNew;
         return -(curr + 1);
      }
   }

   Object pqExtractMin() {
      if(this.size == 0) {
         return this.heap.pqExtractMin();
      } else {
         Object sortMin = this.keys[this.order[this.size - 1]];
         if(!this.heap.pqIsEmpty()) {
            Object heapMin = this.heap.pqMinimum();
            if(LEQ(this.leq, heapMin, sortMin)) {
               return this.heap.pqExtractMin();
            }
         }

         while(true) {
            --this.size;
            if(this.size <= 0 || this.keys[this.order[this.size - 1]] != null) {
               break;
            }
         }

         return sortMin;
      }
   }

   Object pqMinimum() {
      if(this.size == 0) {
         return this.heap.pqMinimum();
      } else {
         Object sortMin = this.keys[this.order[this.size - 1]];
         if(!this.heap.pqIsEmpty()) {
            Object heapMin = this.heap.pqMinimum();
            if(PriorityQHeap.LEQ(this.leq, heapMin, sortMin)) {
               return heapMin;
            }
         }

         return sortMin;
      }
   }

   boolean pqIsEmpty() {
      return this.size == 0 && this.heap.pqIsEmpty();
   }

   void pqDelete(int curr) {
      if(curr >= 0) {
         this.heap.pqDelete(curr);
      } else {
         curr = -(curr + 1);
         if($assertionsDisabled || curr < this.max && this.keys[curr] != null) {
            for(this.keys[curr] = null; this.size > 0 && this.keys[this.order[this.size - 1]] == null; --this.size) {
               ;
            }

         } else {
            throw new AssertionError();
         }
      }
   }

   private static class Stack {
      int p;
      int r;

      private Stack() {
      }
   }
}
