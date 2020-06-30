package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMapEntry implements Entry {
   public abstract Object getKey();

   public abstract Object getValue();

   public Object setValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public boolean equals(@Nullable Object object) {
      if(!(object instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> that = (Entry)object;
         return Objects.equal(this.getKey(), that.getKey()) && Objects.equal(this.getValue(), that.getValue());
      }
   }

   public int hashCode() {
      K k = this.getKey();
      V v = this.getValue();
      return (k == null?0:k.hashCode()) ^ (v == null?0:v.hashCode());
   }

   public String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
