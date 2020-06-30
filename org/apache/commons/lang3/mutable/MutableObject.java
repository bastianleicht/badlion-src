package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.mutable.Mutable;

public class MutableObject implements Mutable, Serializable {
   private static final long serialVersionUID = 86241875189L;
   private Object value;

   public MutableObject() {
   }

   public MutableObject(Object value) {
      this.value = value;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(this == obj) {
         return true;
      } else if(this.getClass() == obj.getClass()) {
         MutableObject<?> that = (MutableObject)obj;
         return this.value.equals(that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value == null?0:this.value.hashCode();
   }

   public String toString() {
      return this.value == null?"null":this.value.toString();
   }
}
