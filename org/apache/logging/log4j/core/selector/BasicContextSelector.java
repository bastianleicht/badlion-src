package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class BasicContextSelector implements ContextSelector {
   private static final LoggerContext CONTEXT = new LoggerContext("Default");

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      LoggerContext ctx = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
      return ctx != null?ctx:CONTEXT;
   }

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation) {
      LoggerContext ctx = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
      return ctx != null?ctx:CONTEXT;
   }

   public LoggerContext locateContext(String name, String configLocation) {
      return CONTEXT;
   }

   public void removeContext(LoggerContext context) {
   }

   public List getLoggerContexts() {
      List<LoggerContext> list = new ArrayList();
      list.add(CONTEXT);
      return Collections.unmodifiableList(list);
   }
}
