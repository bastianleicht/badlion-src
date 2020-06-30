package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
final class CartesianList extends AbstractList implements RandomAccess {
   private final transient ImmutableList axes;
   private final transient int[] axesSizeProduct;

   static List create(List lists) {
      ImmutableList.Builder<List<E>> axesBuilder = new ImmutableList.Builder(lists.size());

      for(List<? extends E> list : lists) {
         List<E> copy = ImmutableList.copyOf((Collection)list);
         if(copy.isEmpty()) {
            return ImmutableList.of();
         }

         axesBuilder.add((Object)copy);
      }

      return new CartesianList(axesBuilder.build());
   }

   CartesianList(ImmutableList axes) {
      this.axes = axes;
      int[] axesSizeProduct = new int[axes.size() + 1];
      axesSizeProduct[axes.size()] = 1;

      try {
         for(int i = axes.size() - 1; i >= 0; --i) {
            axesSizeProduct[i] = IntMath.checkedMultiply(axesSizeProduct[i + 1], ((List)axes.get(i)).size());
         }
      } catch (ArithmeticException var4) {
         throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
      }

      this.axesSizeProduct = axesSizeProduct;
   }

   private int getAxisIndexForProductIndex(int index, int axis) {
      return index / this.axesSizeProduct[axis + 1] % ((List)this.axes.get(axis)).size();
   }

   public ImmutableList get(final int index) {
      Preconditions.checkElementIndex(index, this.size());
      return new ImmutableList() {
         public int size() {
            return CartesianList.this.axes.size();
         }

         public Object get(int axis) {
            Preconditions.checkElementIndex(axis, this.size());
            int axisIndex = CartesianList.this.getAxisIndexForProductIndex(index, axis);
            return ((List)CartesianList.this.axes.get(axis)).get(axisIndex);
         }

         boolean isPartialView() {
            return true;
         }
      };
   }

   public int size() {
      return this.axesSizeProduct[0];
   }

   public boolean contains(@Nullable Object o) {
      if(!(o instanceof List)) {
         return false;
      } else {
         List<?> list = (List)o;
         if(list.size() != this.axes.size()) {
            return false;
         } else {
            ListIterator<?> itr = list.listIterator();

            while(itr.hasNext()) {
               int index = itr.nextIndex();
               if(!((List)this.axes.get(index)).contains(itr.next())) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
