package org.apache.logging.log4j.core.async;

import java.net.URI;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.message.MessageFactory;

public class AsyncLoggerContext extends LoggerContext {
   public AsyncLoggerContext(String name) {
      super(name);
   }

   public AsyncLoggerContext(String name, Object externalContext) {
      super(name, externalContext);
   }

   public AsyncLoggerContext(String name, Object externalContext, URI configLocn) {
      super(name, externalContext, configLocn);
   }

   public AsyncLoggerContext(String name, Object externalContext, String configLocn) {
      super(name, externalContext, configLocn);
   }

   protected Logger newInstance(LoggerContext ctx, String name, MessageFactory messageFactory) {
      return new AsyncLogger(ctx, name, messageFactory);
   }

   public void stop() {
      AsyncLogger.stop();
      super.stop();
   }
}
