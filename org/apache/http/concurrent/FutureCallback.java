package org.apache.http.concurrent;

public interface FutureCallback {
   void completed(Object var1);

   void failed(Exception var1);

   void cancelled();
}
