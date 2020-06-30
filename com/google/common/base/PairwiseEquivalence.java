package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class PairwiseEquivalence extends Equivalence implements Serializable {
   final Equivalence elementEquivalence;
   private static final long serialVersionUID = 1L;

   PairwiseEquivalence(Equivalence elementEquivalence) {
      this.elementEquivalence = (Equivalence)Preconditions.checkNotNull(elementEquivalence);
   }

   protected boolean doEquivalent(Iterable iterableA, Iterable iterableB) {
      Iterator<T> iteratorA = iterableA.iterator();
      Iterator<T> iteratorB = iterableB.iterator();

      while(iteratorA.hasNext() && iteratorB.hasNext()) {
         if(!this.elementEquivalence.equivalent(iteratorA.next(), iteratorB.next())) {
            return false;
         }
      }

      return !iteratorA.hasNext() && !iteratorB.hasNext();
   }

   protected int doHash(Iterable iterable) {
      int hash = 78721;

      for(T element : iterable) {
         hash = hash * 24943 + this.elementEquivalence.hash(element);
      }

      return hash;
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof PairwiseEquivalence) {
         PairwiseEquivalence<?> that = (PairwiseEquivalence)object;
         return this.elementEquivalence.equals(that.elementEquivalence);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.elementEquivalence.hashCode() ^ 1185147655;
   }

   public String toString() {
      return this.elementEquivalence + ".pairwise()";
   }
}
