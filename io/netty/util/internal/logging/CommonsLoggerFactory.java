package io.netty.util.internal.logging;

import io.netty.util.internal.logging.CommonsLogger;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.LogFactory;

public class CommonsLoggerFactory extends InternalLoggerFactory {
   Map loggerMap = new HashMap();

   public InternalLogger newInstance(String name) {
      return new CommonsLogger(LogFactory.getLog(name), name);
   }
}
