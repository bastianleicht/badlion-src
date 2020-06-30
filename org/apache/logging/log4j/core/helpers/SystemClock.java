package org.apache.logging.log4j.core.helpers;

import org.apache.logging.log4j.core.helpers.Clock;

public class SystemClock implements Clock {
   public long currentTimeMillis() {
      return System.currentTimeMillis();
   }
}
