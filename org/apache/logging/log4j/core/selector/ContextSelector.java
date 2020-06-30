package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;

public interface ContextSelector {
   LoggerContext getContext(String var1, ClassLoader var2, boolean var3);

   LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4);

   List getLoggerContexts();

   void removeContext(LoggerContext var1);
}
