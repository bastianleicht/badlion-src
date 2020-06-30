package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.FunctionalEquivalence;
import com.google.common.base.Objects;
import com.google.common.base.PairwiseEquivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Equivalence {
   public final boolean equivalent(@Nullable Object a, @Nullable Object b) {
      return a == b?true:(a != null && b != null?this.doEquivalent(a, b):false);
   }

   protected abstract boolean doEquivalent(Object var1, Object var2);

   public final int hash(@Nullable Object t) {
      return t == null?0:this.doHash(t);
   }

   protected abstract int doHash(Object var1);

   public final Equivalence onResultOf(Function function) {
      return new FunctionalEquivalence(function, this);
   }

   public final Equivalence.Wrapper wrap(@Nullable Object reference) {
      return new Equivalence.Wrapper(this, reference);
   }

   @GwtCompatible(
      serializable = true
   )
   public final Equivalence pairwise() {
      return new PairwiseEquivalence(this);
   }

   @Beta
   public final Predicate equivalentTo(@Nullable Object target) {
      return new Equivalence.EquivalentToPredicate(this, target);
   }

   public static Equivalence equals() {
      return Equivalence.Equals.INSTANCE;
   }

   public static Equivalence identity() {
      return Equivalence.Identity.INSTANCE;
   }

   static final class Equals extends Equivalence implements Serializable {
      static final Equivalence.Equals INSTANCE = new Equivalence.Equals();
      private static final long serialVersionUID = 1L;

      protected boolean doEquivalent(Object a, Object b) {
         return a.equals(b);
      }

      public int doHash(Object o) {
         return o.hashCode();
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class EquivalentToPredicate implements Predicate, Serializable {
      private final Equivalence equivalence;
      @Nullable
      private final Object target;
      private static final long serialVersionUID = 0L;

      EquivalentToPredicate(Equivalence equivalence, @Nullable Object target) {
         this.equivalence = (Equivalence)Preconditions.checkNotNull(equivalence);
         this.target = target;
      }

      public boolean apply(@Nullable Object input) {
         return this.equivalence.equivalent(input, this.target);
      }

      public boolean equals(@Nullable Object obj) {
         if(this == obj) {
            return true;
         } else if(!(obj instanceof Equivalence.EquivalentToPredicate)) {
            return false;
         } else {
            Equivalence.EquivalentToPredicate<?> that = (Equivalence.EquivalentToPredicate)obj;
            return this.equivalence.equals(that.equivalence) && Objects.equal(this.target, that.target);
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.equivalence, this.target});
      }

      public String toString() {
         return this.equivalence + ".equivalentTo(" + this.target + ")";
      }
   }

   static final class Identity extends Equivalence implements Serializable {
      static final Equivalence.Identity INSTANCE = new Equivalence.Identity();
      private static final long serialVersionUID = 1L;

      protected boolean doEquivalent(Object a, Object b) {
         return false;
      }

      protected int doHash(Object o) {
         return System.identityHashCode(o);
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   public static final class Wrapper implements Serializable {
      private final Equivalence equivalence;
      @Nullable
      private final Object reference;
      private static final long serialVersionUID = 0L;

      private Wrapper(Equivalence equivalence, @Nullable Object reference) {
         this.equivalence = (Equivalence)Preconditions.checkNotNull(equivalence);
         this.reference = reference;
      }

      @Nullable
      public Object get() {
         return this.reference;
      }

      public boolean equals(@Nullable Object obj) {
         if(obj == this) {
            return true;
         } else {
            if(obj instanceof Equivalence.Wrapper) {
               Equivalence.Wrapper<?> that = (Equivalence.Wrapper)obj;
               if(this.equivalence.equals(that.equivalence)) {
                  Equivalence<Object> equivalence = this.equivalence;
                  return equivalence.equivalent(this.reference, that.reference);
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.equivalence.hash(this.reference);
      }

      public String toString() {
         return this.equivalence + ".wrap(" + this.reference + ")";
      }
   }
}
