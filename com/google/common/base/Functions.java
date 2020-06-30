package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public final class Functions {
   public static Function toStringFunction() {
      return Functions.ToStringFunction.INSTANCE;
   }

   public static Function identity() {
      return Functions.IdentityFunction.INSTANCE;
   }

   public static Function forMap(Map map) {
      return new Functions.FunctionForMapNoDefault(map);
   }

   public static Function forMap(Map map, @Nullable Object defaultValue) {
      return new Functions.ForMapWithDefault(map, defaultValue);
   }

   public static Function compose(Function g, Function f) {
      return new Functions.FunctionComposition(g, f);
   }

   public static Function forPredicate(Predicate predicate) {
      return new Functions.PredicateFunction(predicate);
   }

   public static Function constant(@Nullable Object value) {
      return new Functions.ConstantFunction(value);
   }

   @Beta
   public static Function forSupplier(Supplier supplier) {
      return new Functions.SupplierFunction(supplier);
   }

   private static class ConstantFunction implements Function, Serializable {
      private final Object value;
      private static final long serialVersionUID = 0L;

      public ConstantFunction(@Nullable Object value) {
         this.value = value;
      }

      public Object apply(@Nullable Object from) {
         return this.value;
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Functions.ConstantFunction) {
            Functions.ConstantFunction<?> that = (Functions.ConstantFunction)obj;
            return Objects.equal(this.value, that.value);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.value == null?0:this.value.hashCode();
      }

      public String toString() {
         return "constant(" + this.value + ")";
      }
   }

   private static class ForMapWithDefault implements Function, Serializable {
      final Map map;
      final Object defaultValue;
      private static final long serialVersionUID = 0L;

      ForMapWithDefault(Map map, @Nullable Object defaultValue) {
         this.map = (Map)Preconditions.checkNotNull(map);
         this.defaultValue = defaultValue;
      }

      public Object apply(@Nullable Object key) {
         V result = this.map.get(key);
         return result == null && !this.map.containsKey(key)?this.defaultValue:result;
      }

      public boolean equals(@Nullable Object o) {
         if(!(o instanceof Functions.ForMapWithDefault)) {
            return false;
         } else {
            Functions.ForMapWithDefault<?, ?> that = (Functions.ForMapWithDefault)o;
            return this.map.equals(that.map) && Objects.equal(this.defaultValue, that.defaultValue);
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.map, this.defaultValue});
      }

      public String toString() {
         return "forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
      }
   }

   private static class FunctionComposition implements Function, Serializable {
      private final Function g;
      private final Function f;
      private static final long serialVersionUID = 0L;

      public FunctionComposition(Function g, Function f) {
         this.g = (Function)Preconditions.checkNotNull(g);
         this.f = (Function)Preconditions.checkNotNull(f);
      }

      public Object apply(@Nullable Object a) {
         return this.g.apply(this.f.apply(a));
      }

      public boolean equals(@Nullable Object obj) {
         if(!(obj instanceof Functions.FunctionComposition)) {
            return false;
         } else {
            Functions.FunctionComposition<?, ?, ?> that = (Functions.FunctionComposition)obj;
            return this.f.equals(that.f) && this.g.equals(that.g);
         }
      }

      public int hashCode() {
         return this.f.hashCode() ^ this.g.hashCode();
      }

      public String toString() {
         return this.g + "(" + this.f + ")";
      }
   }

   private static class FunctionForMapNoDefault implements Function, Serializable {
      final Map map;
      private static final long serialVersionUID = 0L;

      FunctionForMapNoDefault(Map map) {
         this.map = (Map)Preconditions.checkNotNull(map);
      }

      public Object apply(@Nullable Object key) {
         V result = this.map.get(key);
         Preconditions.checkArgument(result != null || this.map.containsKey(key), "Key \'%s\' not present in map", new Object[]{key});
         return result;
      }

      public boolean equals(@Nullable Object o) {
         if(o instanceof Functions.FunctionForMapNoDefault) {
            Functions.FunctionForMapNoDefault<?, ?> that = (Functions.FunctionForMapNoDefault)o;
            return this.map.equals(that.map);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public String toString() {
         return "forMap(" + this.map + ")";
      }
   }

   private static enum IdentityFunction implements Function {
      INSTANCE;

      @Nullable
      public Object apply(@Nullable Object o) {
         return o;
      }

      public String toString() {
         return "identity";
      }
   }

   private static class PredicateFunction implements Function, Serializable {
      private final Predicate predicate;
      private static final long serialVersionUID = 0L;

      private PredicateFunction(Predicate predicate) {
         this.predicate = (Predicate)Preconditions.checkNotNull(predicate);
      }

      public Boolean apply(@Nullable Object t) {
         return Boolean.valueOf(this.predicate.apply(t));
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Functions.PredicateFunction) {
            Functions.PredicateFunction<?> that = (Functions.PredicateFunction)obj;
            return this.predicate.equals(that.predicate);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.predicate.hashCode();
      }

      public String toString() {
         return "forPredicate(" + this.predicate + ")";
      }
   }

   private static class SupplierFunction implements Function, Serializable {
      private final Supplier supplier;
      private static final long serialVersionUID = 0L;

      private SupplierFunction(Supplier supplier) {
         this.supplier = (Supplier)Preconditions.checkNotNull(supplier);
      }

      public Object apply(@Nullable Object input) {
         return this.supplier.get();
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Functions.SupplierFunction) {
            Functions.SupplierFunction<?> that = (Functions.SupplierFunction)obj;
            return this.supplier.equals(that.supplier);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.supplier.hashCode();
      }

      public String toString() {
         return "forSupplier(" + this.supplier + ")";
      }
   }

   private static enum ToStringFunction implements Function {
      INSTANCE;

      public String apply(Object o) {
         Preconditions.checkNotNull(o);
         return o.toString();
      }

      public String toString() {
         return "toString";
      }
   }
}
