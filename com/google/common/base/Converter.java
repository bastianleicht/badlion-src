package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract class Converter implements Function {
   private final boolean handleNullAutomatically;
   private transient Converter reverse;

   protected Converter() {
      this(true);
   }

   Converter(boolean handleNullAutomatically) {
      this.handleNullAutomatically = handleNullAutomatically;
   }

   protected abstract Object doForward(Object var1);

   protected abstract Object doBackward(Object var1);

   @Nullable
   public final Object convert(@Nullable Object a) {
      return this.correctedDoForward(a);
   }

   @Nullable
   Object correctedDoForward(@Nullable Object a) {
      return this.handleNullAutomatically?(a == null?null:Preconditions.checkNotNull(this.doForward(a))):this.doForward(a);
   }

   @Nullable
   Object correctedDoBackward(@Nullable Object b) {
      return this.handleNullAutomatically?(b == null?null:Preconditions.checkNotNull(this.doBackward(b))):this.doBackward(b);
   }

   public Iterable convertAll(final Iterable fromIterable) {
      Preconditions.checkNotNull(fromIterable, "fromIterable");
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               private final Iterator fromIterator = fromIterable.iterator();

               public boolean hasNext() {
                  return this.fromIterator.hasNext();
               }

               public Object next() {
                  return Converter.this.convert(this.fromIterator.next());
               }

               public void remove() {
                  this.fromIterator.remove();
               }
            };
         }
      };
   }

   public Converter reverse() {
      Converter<B, A> result = this.reverse;
      return result == null?(this.reverse = new Converter.ReverseConverter(this)):result;
   }

   public Converter andThen(Converter secondConverter) {
      return new Converter.ConverterComposition(this, (Converter)Preconditions.checkNotNull(secondConverter));
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public final Object apply(@Nullable Object a) {
      return this.convert(a);
   }

   public boolean equals(@Nullable Object object) {
      return super.equals(object);
   }

   public static Converter from(Function forwardFunction, Function backwardFunction) {
      return new Converter.FunctionBasedConverter(forwardFunction, backwardFunction);
   }

   public static Converter identity() {
      return Converter.IdentityConverter.INSTANCE;
   }

   private static final class ConverterComposition extends Converter implements Serializable {
      final Converter first;
      final Converter second;
      private static final long serialVersionUID = 0L;

      ConverterComposition(Converter first, Converter second) {
         this.first = first;
         this.second = second;
      }

      protected Object doForward(Object a) {
         throw new AssertionError();
      }

      protected Object doBackward(Object c) {
         throw new AssertionError();
      }

      @Nullable
      Object correctedDoForward(@Nullable Object a) {
         return this.second.correctedDoForward(this.first.correctedDoForward(a));
      }

      @Nullable
      Object correctedDoBackward(@Nullable Object c) {
         return this.first.correctedDoBackward(this.second.correctedDoBackward(c));
      }

      public boolean equals(@Nullable Object object) {
         if(!(object instanceof Converter.ConverterComposition)) {
            return false;
         } else {
            Converter.ConverterComposition<?, ?, ?> that = (Converter.ConverterComposition)object;
            return this.first.equals(that.first) && this.second.equals(that.second);
         }
      }

      public int hashCode() {
         return 31 * this.first.hashCode() + this.second.hashCode();
      }

      public String toString() {
         return this.first + ".andThen(" + this.second + ")";
      }
   }

   private static final class FunctionBasedConverter extends Converter implements Serializable {
      private final Function forwardFunction;
      private final Function backwardFunction;

      private FunctionBasedConverter(Function forwardFunction, Function backwardFunction) {
         this.forwardFunction = (Function)Preconditions.checkNotNull(forwardFunction);
         this.backwardFunction = (Function)Preconditions.checkNotNull(backwardFunction);
      }

      protected Object doForward(Object a) {
         return this.forwardFunction.apply(a);
      }

      protected Object doBackward(Object b) {
         return this.backwardFunction.apply(b);
      }

      public boolean equals(@Nullable Object object) {
         if(!(object instanceof Converter.FunctionBasedConverter)) {
            return false;
         } else {
            Converter.FunctionBasedConverter<?, ?> that = (Converter.FunctionBasedConverter)object;
            return this.forwardFunction.equals(that.forwardFunction) && this.backwardFunction.equals(that.backwardFunction);
         }
      }

      public int hashCode() {
         return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
      }

      public String toString() {
         return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
      }
   }

   private static final class IdentityConverter extends Converter implements Serializable {
      static final Converter.IdentityConverter INSTANCE = new Converter.IdentityConverter();
      private static final long serialVersionUID = 0L;

      protected Object doForward(Object t) {
         return t;
      }

      protected Object doBackward(Object t) {
         return t;
      }

      public Converter.IdentityConverter reverse() {
         return this;
      }

      public Converter andThen(Converter otherConverter) {
         return (Converter)Preconditions.checkNotNull(otherConverter, "otherConverter");
      }

      public String toString() {
         return "Converter.identity()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class ReverseConverter extends Converter implements Serializable {
      final Converter original;
      private static final long serialVersionUID = 0L;

      ReverseConverter(Converter original) {
         this.original = original;
      }

      protected Object doForward(Object b) {
         throw new AssertionError();
      }

      protected Object doBackward(Object a) {
         throw new AssertionError();
      }

      @Nullable
      Object correctedDoForward(@Nullable Object b) {
         return this.original.correctedDoBackward(b);
      }

      @Nullable
      Object correctedDoBackward(@Nullable Object a) {
         return this.original.correctedDoForward(a);
      }

      public Converter reverse() {
         return this.original;
      }

      public boolean equals(@Nullable Object object) {
         if(object instanceof Converter.ReverseConverter) {
            Converter.ReverseConverter<?, ?> that = (Converter.ReverseConverter)object;
            return this.original.equals(that.original);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return ~this.original.hashCode();
      }

      public String toString() {
         return this.original + ".reverse()";
      }
   }
}
