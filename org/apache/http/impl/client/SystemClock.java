package org.apache.http.impl.client;

import org.apache.http.impl.client.Clock;

class SystemClock implements Clock {
   public long getCurrentTime() {
      return System.currentTimeMillis();
   }
}
