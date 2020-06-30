package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.helper.Action;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractAction implements Action {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private boolean complete = false;
   private boolean interrupted = false;

   public abstract boolean execute() throws IOException;

   public synchronized void run() {
      if(!this.interrupted) {
         try {
            this.execute();
         } catch (IOException var2) {
            this.reportException(var2);
         }

         this.complete = true;
         this.interrupted = true;
      }

   }

   public synchronized void close() {
      this.interrupted = true;
   }

   public boolean isComplete() {
      return this.complete;
   }

   protected void reportException(Exception ex) {
   }
}
