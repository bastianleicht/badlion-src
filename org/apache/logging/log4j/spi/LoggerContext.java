package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;

public interface LoggerContext {
   Object getExternalContext();

   Logger getLogger(String var1);

   Logger getLogger(String var1, MessageFactory var2);

   boolean hasLogger(String var1);
}
