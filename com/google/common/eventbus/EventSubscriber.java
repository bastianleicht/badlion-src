package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

class EventSubscriber {
   private final Object target;
   private final Method method;

   EventSubscriber(Object target, Method method) {
      Preconditions.checkNotNull(target, "EventSubscriber target cannot be null.");
      Preconditions.checkNotNull(method, "EventSubscriber method cannot be null.");
      this.target = target;
      this.method = method;
      method.setAccessible(true);
   }

   public void handleEvent(Object event) throws InvocationTargetException {
      Preconditions.checkNotNull(event);

      try {
         this.method.invoke(this.target, new Object[]{event});
      } catch (IllegalArgumentException var3) {
         throw new Error("Method rejected target/argument: " + event, var3);
      } catch (IllegalAccessException var4) {
         throw new Error("Method became inaccessible: " + event, var4);
      } catch (InvocationTargetException var5) {
         if(var5.getCause() instanceof Error) {
            throw (Error)var5.getCause();
         } else {
            throw var5;
         }
      }
   }

   public String toString() {
      return "[wrapper " + this.method + "]";
   }

   public int hashCode() {
      int PRIME = 31;
      return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
   }

   public boolean equals(@Nullable Object obj) {
      if(!(obj instanceof EventSubscriber)) {
         return false;
      } else {
         EventSubscriber that = (EventSubscriber)obj;
         return this.target == that.target && this.method.equals(that.method);
      }
   }

   public Object getSubscriber() {
      return this.target;
   }

   public Method getMethod() {
      return this.method;
   }
}
