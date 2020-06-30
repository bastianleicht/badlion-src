package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLogger;
import java.util.logging.Logger;

public class JdkLoggerFactory extends InternalLoggerFactory {
   public InternalLogger newInstance(String name) {
      return new JdkLogger(Logger.getLogger(name));
   }
}
