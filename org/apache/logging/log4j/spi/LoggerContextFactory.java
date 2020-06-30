package org.apache.logging.log4j.spi;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;

public interface LoggerContextFactory {
   LoggerContext getContext(String var1, ClassLoader var2, boolean var3);

   LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4);

   void removeContext(LoggerContext var1);
}
