package org.apache.http.pool;

import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;

public interface ConnPool {
   Future lease(Object var1, Object var2, FutureCallback var3);

   void release(Object var1, boolean var2);
}
