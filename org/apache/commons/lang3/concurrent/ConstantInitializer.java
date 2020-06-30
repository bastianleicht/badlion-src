package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;

public class ConstantInitializer implements ConcurrentInitializer {
   private static final String FMT_TO_STRING = "ConstantInitializer@%d [ object = %s ]";
   private final Object object;

   public ConstantInitializer(Object obj) {
      this.object = obj;
   }

   public final Object getObject() {
      return this.object;
   }

   public Object get() throws ConcurrentException {
      return this.getObject();
   }

   public int hashCode() {
      return this.getObject() != null?this.getObject().hashCode():0;
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof ConstantInitializer)) {
         return false;
      } else {
         ConstantInitializer<?> c = (ConstantInitializer)obj;
         return ObjectUtils.equals(this.getObject(), c.getObject());
      }
   }

   public String toString() {
      return String.format("ConstantInitializer@%d [ object = %s ]", new Object[]{Integer.valueOf(System.identityHashCode(this)), String.valueOf(this.getObject())});
   }
}
