package org.lwjgl.util.mapped;

import java.util.Iterator;
import org.lwjgl.util.mapped.MappedObject;

final class MappedForeach implements Iterable {
   final MappedObject mapped;
   final int elementCount;

   MappedForeach(MappedObject mapped, int elementCount) {
      this.mapped = mapped;
      this.elementCount = elementCount;
   }

   public Iterator iterator() {
      return new Iterator() {
         private int index;

         public boolean hasNext() {
            return this.index < MappedForeach.this.elementCount;
         }

         public MappedObject next() {
            MappedForeach.this.mapped.setViewAddress(MappedForeach.this.mapped.getViewAddress(this.index++));
            return MappedForeach.this.mapped;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }
}
