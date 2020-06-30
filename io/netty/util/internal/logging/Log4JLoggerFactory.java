package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLogger;
import org.apache.log4j.Logger;

public class Log4JLoggerFactory extends InternalLoggerFactory {
   public InternalLogger newInstance(String name) {
      return new Log4JLogger(Logger.getLogger(name));
   }
}
