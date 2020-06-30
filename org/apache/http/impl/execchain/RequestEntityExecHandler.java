package org.apache.http.impl.execchain;

import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
class RequestEntityExecHandler implements InvocationHandler {
   private static final Method WRITE_TO_METHOD;
   private final HttpEntity original;
   private boolean consumed = false;

   RequestEntityExecHandler(HttpEntity original) {
      this.original = original;
   }

   public HttpEntity getOriginal() {
      return this.original;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
         if(method.equals(WRITE_TO_METHOD)) {
            this.consumed = true;
         }

         return method.invoke(this.original, args);
      } catch (InvocationTargetException var6) {
         Throwable cause = var6.getCause();
         if(cause != null) {
            throw cause;
         } else {
            throw var6;
         }
      }
   }

   static {
      try {
         WRITE_TO_METHOD = HttpEntity.class.getMethod("writeTo", new Class[]{OutputStream.class});
      } catch (NoSuchMethodException var1) {
         throw new Error(var1);
      }
   }
}
