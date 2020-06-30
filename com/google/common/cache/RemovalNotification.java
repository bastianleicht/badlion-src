package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.RemovalCause;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class RemovalNotification implements Entry {
   @Nullable
   private final Object key;
   @Nullable
   private final Object value;
   private final RemovalCause cause;
   private static final long serialVersionUID = 0L;

   RemovalNotification(@Nullable Object key, @Nullable Object value, RemovalCause cause) {
      this.key = key;
      this.value = value;
      this.cause = (RemovalCause)Preconditions.checkNotNull(cause);
   }

   public RemovalCause getCause() {
      return this.cause;
   }

   public boolean wasEvicted() {
      return this.cause.wasEvicted();
   }

   @Nullable
   public Object getKey() {
      return this.key;
   }

   @Nullable
   public Object getValue() {
      return this.value;
   }

   public final Object setValue(Object value) {
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
