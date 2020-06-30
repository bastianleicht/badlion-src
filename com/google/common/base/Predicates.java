package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Predicates {
   private static final Joiner COMMA_JOINER = Joiner.on(',');

   @GwtCompatible(
      serializable = true
   )
   public static Predicate alwaysTrue() {
      return Predicates.ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static Predicate alwaysFalse() {
      return Predicates.ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static Predicate isNull() {
      return Predicates.ObjectPredicate.IS_NULL.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static Predicate notNull() {
      return Predicates.ObjectPredicate.NOT_NULL.withNarrowedType();
   }

   public static Predicate not(Predicate predicate) {
      return new Predicates.NotPredicate(predicate);
   }

   public static Predicate and(Iterable components) {
      return new Predicates.AndPredicate(defensiveCopy(components));
   }

   public static Predicate and(Predicate... components) {
      return new Predicates.AndPredicate(defensiveCopy((Object[])components));
   }

   public static Predicate and(Predicate first, Predicate second) {
      return new Predicates.AndPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)));
   }

   public static Predicate or(Iterable components) {
      return new Predicates.OrPredicate(defensiveCopy(components));
   }

   public static Predicate or(Predicate... components) {
      return new Predicates.OrPredicate(defensiveCopy((Object[])components));
   }

   public static Predicate or(Predicate first, Predicate second) {
      return new Predicates.OrPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)));
   }

   public static Predicate equalTo(@Nullable Object target) {
      return (Predicate)(target == null?isNull():new Predicates.IsEqualToPredicate(target));
   }

   @GwtIncompatible("Class.isInstance")
   public static Predicate instanceOf(Class clazz) {
      return new Predicates.InstanceOfPredicate(clazz);
   }

   @GwtIncompatible("Class.isAssignableFrom")
   @Beta
   public static Predicate assignableFrom(Class clazz) {
      return new Predicates.AssignableFromPredicate(clazz);
   }

   public static Predicate in(Collection target) {
      return new Predicates.InPredicate(target);
   }

   public static Predicate compose(Predicate predicate, Function function) {
      return new Predicates.CompositionPredicate(predicate, function);
   }

   @GwtIncompatible("java.util.regex.Pattern")
   public static Predicate containsPattern(String pattern) {
      return new Predicates.ContainsPatternFromStringPredicate(pattern);
   }

   @GwtIncompatible("java.util.regex.Pattern")
   public static Predicate contains(Pattern pattern) {
      return new Predicates.ContainsPatternPredicate(pattern);
   }

   private static List asList(Predicate first, Predicate second) {
      return Arrays.asList(new Predicate[]{first, second});
   }

   private static List defensiveCopy(Object... array) {
      return defensiveCopy((Iterable)Arrays.asList(array));
   }

   static List defensiveCopy(Iterable iterable) {
      ArrayList<T> list = new ArrayList();

      for(T element : iterable) {
         list.add(Preconditions.checkNotNull(element));
      }

      return list;
   }

   private static class AndPredicate implements Predicate, Serializable {
      private final List components;
      private static final long serialVersionUID = 0L;

      private AndPredicate(List components) {
         this.components = components;
      }

      public boolean apply(@Nullable Object t) {
         for(int i = 0; i < this.components.size(); ++i) {
            if(!((Predicate)this.components.get(i)).apply(t)) {
               return false;
            }
         }

         return true;
      }

      public int hashCode() {
         return this.components.hashCode() + 306654252;
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.AndPredicate) {
            Predicates.AndPredicate<?> that = (Predicates.AndPredicate)obj;
            return this.components.equals(that.components);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.and(" + Predicates.COMMA_JOINER.join((Iterable)this.components) + ")";
      }
   }

   @GwtIncompatible("Class.isAssignableFrom")
   private static class AssignableFromPredicate implements Predicate, Serializable {
      private final Class clazz;
      private static final long serialVersionUID = 0L;

      private AssignableFromPredicate(Class clazz) {
         this.clazz = (Class)Preconditions.checkNotNull(clazz);
      }

      public boolean apply(Class input) {
         return this.clazz.isAssignableFrom(input);
      }

      public int hashCode() {
         return this.clazz.hashCode();
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.AssignableFromPredicate) {
            Predicates.AssignableFromPredicate that = (Predicates.AssignableFromPredicate)obj;
            return this.clazz == that.clazz;
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.assignableFrom(" + this.clazz.getName() + ")";
      }
   }

   private static class CompositionPredicate implements Predicate, Serializable {
      final Predicate p;
      final Function f;
      private static final long serialVersionUID = 0L;

      private CompositionPredicate(Predicate p, Function f) {
         this.p = (Predicate)Preconditions.checkNotNull(p);
         this.f = (Function)Preconditions.checkNotNull(f);
      }

      public boolean apply(@Nullable Object a) {
         return this.p.apply(this.f.apply(a));
      }

      public boolean equals(@Nullable Object obj) {
         if(!(obj instanceof Predicates.CompositionPredicate)) {
            return false;
         } else {
            Predicates.CompositionPredicate<?, ?> that = (Predicates.CompositionPredicate)obj;
            return this.f.equals(that.f) && this.p.equals(that.p);
         }
      }

      public int hashCode() {
         return this.f.hashCode() ^ this.p.hashCode();
      }

      public String toString() {
         return this.p.toString() + "(" + this.f.toString() + ")";
      }
   }

   @GwtIncompatible("Only used by other GWT-incompatible code.")
   private static class ContainsPatternFromStringPredicate extends Predicates.ContainsPatternPredicate {
      private static final long serialVersionUID = 0L;

      ContainsPatternFromStringPredicate(String string) {
         super(Pattern.compile(string));
      }

      public String toString() {
         return "Predicates.containsPattern(" + this.pattern.pattern() + ")";
      }
   }

   @GwtIncompatible("Only used by other GWT-incompatible code.")
   private static class ContainsPatternPredicate implements Predicate, Serializable {
      final Pattern pattern;
      private static final long serialVersionUID = 0L;

      ContainsPatternPredicate(Pattern pattern) {
         this.pattern = (Pattern)Preconditions.checkNotNull(pattern);
      }

      public boolean apply(CharSequence t) {
         return this.pattern.matcher(t).find();
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.pattern.pattern(), Integer.valueOf(this.pattern.flags())});
      }

      public boolean equals(@Nullable Object obj) {
         if(!(obj instanceof Predicates.ContainsPatternPredicate)) {
            return false;
         } else {
            Predicates.ContainsPatternPredicate that = (Predicates.ContainsPatternPredicate)obj;
            return Objects.equal(this.pattern.pattern(), that.pattern.pattern()) && Objects.equal(Integer.valueOf(this.pattern.flags()), Integer.valueOf(that.pattern.flags()));
         }
      }

      public String toString() {
         String patternString = Objects.toStringHelper((Object)this.pattern).add("pattern", this.pattern.pattern()).add("pattern.flags", this.pattern.flags()).toString();
         return "Predicates.contains(" + patternString + ")";
      }
   }

   private static class InPredicate implements Predicate, Serializable {
      private final Collection target;
      private static final long serialVersionUID = 0L;

      private InPredicate(Collection target) {
         this.target = (Collection)Preconditions.checkNotNull(target);
      }

      public boolean apply(@Nullable Object t) {
         try {
            return this.target.contains(t);
         } catch (NullPointerException var3) {
            return false;
         } catch (ClassCastException var4) {
            return false;
         }
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.InPredicate) {
            Predicates.InPredicate<?> that = (Predicates.InPredicate)obj;
            return this.target.equals(that.target);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.target.hashCode();
      }

      public String toString() {
         return "Predicates.in(" + this.target + ")";
      }
   }

   @GwtIncompatible("Class.isInstance")
   private static class InstanceOfPredicate implements Predicate, Serializable {
      private final Class clazz;
      private static final long serialVersionUID = 0L;

      private InstanceOfPredicate(Class clazz) {
         this.clazz = (Class)Preconditions.checkNotNull(clazz);
      }

      public boolean apply(@Nullable Object o) {
         return this.clazz.isInstance(o);
      }

      public int hashCode() {
         return this.clazz.hashCode();
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.InstanceOfPredicate) {
            Predicates.InstanceOfPredicate that = (Predicates.InstanceOfPredicate)obj;
            return this.clazz == that.clazz;
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.instanceOf(" + this.clazz.getName() + ")";
      }
   }

   private static class IsEqualToPredicate implements Predicate, Serializable {
      private final Object target;
      private static final long serialVersionUID = 0L;

      private IsEqualToPredicate(Object target) {
         this.target = target;
      }

      public boolean apply(Object t) {
         return this.target.equals(t);
      }

      public int hashCode() {
         return this.target.hashCode();
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.IsEqualToPredicate) {
            Predicates.IsEqualToPredicate<?> that = (Predicates.IsEqualToPredicate)obj;
            return this.target.equals(that.target);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.equalTo(" + this.target + ")";
      }
   }

   private static class NotPredicate implements Predicate, Serializable {
      final Predicate predicate;
      private static final long serialVersionUID = 0L;

      NotPredicate(Predicate predicate) {
         this.predicate = (Predicate)Preconditions.checkNotNull(predicate);
      }

      public boolean apply(@Nullable Object t) {
         return !this.predicate.apply(t);
      }

      public int hashCode() {
         return ~this.predicate.hashCode();
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.NotPredicate) {
            Predicates.NotPredicate<?> that = (Predicates.NotPredicate)obj;
            return this.predicate.equals(that.predicate);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.not(" + this.predicate.toString() + ")";
      }
   }

   static enum ObjectPredicate implements Predicate {
      ALWAYS_TRUE {
         public boolean apply(@Nullable Object o) {
            return true;
         }

         public String toString() {
            return "Predicates.alwaysTrue()";
         }
      },
      ALWAYS_FALSE {
         public boolean apply(@Nullable Object o) {
            return false;
         }

         public String toString() {
            return "Predicates.alwaysFalse()";
         }
      },
      IS_NULL {
         public boolean apply(@Nullable Object o) {
            return o == null;
         }

         public String toString() {
            return "Predicates.isNull()";
         }
      },
      NOT_NULL {
         public boolean apply(@Nullable Object o) {
            return o != null;
         }

         public String toString() {
            return "Predicates.notNull()";
         }
      };

      private ObjectPredicate() {
      }

      Predicate withNarrowedType() {
         return this;
      }
   }

   private static class OrPredicate implements Predicate, Serializable {
      private final List components;
      private static final long serialVersionUID = 0L;

      private OrPredicate(List components) {
         this.components = components;
      }

      public boolean apply(@Nullable Object t) {
         for(int i = 0; i < this.components.size(); ++i) {
            if(((Predicate)this.components.get(i)).apply(t)) {
               return true;
            }
         }

         return false;
      }

      public int hashCode() {
         return this.components.hashCode() + 87855567;
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Predicates.OrPredicate) {
            Predicates.OrPredicate<?> that = (Predicates.OrPredicate)obj;
            return this.components.equals(that.components);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.or(" + Predicates.COMMA_JOINER.join((Iterable)this.components) + ")";
      }
   }
}
