package org.apache.logging.log4j.core.async;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class AsyncLoggerContextSelector implements ContextSelector {
   private static final AsyncLoggerContext CONTEXT = new AsyncLoggerContext("AsyncLoggerContext");

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      return CONTEXT;
   }

   public List getLoggerContexts() {
      List<LoggerContext> list = new ArrayList();
      list.add(CONTEXT);
      return Collections.unmodifiableList(list);
   }

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation) {
      return CONTEXT;
   }

   public void removeContext(LoggerContext context) {
   }
}
