package org.apache.http.pool;

import org.apache.http.pool.PoolStats;

public interface ConnPoolControl {
   void setMaxTotal(int var1);

   int getMaxTotal();

   void setDefaultMaxPerRoute(int var1);

   int getDefaultMaxPerRoute();

   void setMaxPerRoute(Object var1, int var2);

   int getMaxPerRoute(Object var1);

   PoolStats getTotalStats();

   PoolStats getStats(Object var1);
}
