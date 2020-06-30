package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.helper.Action;

public class CompositeAction extends AbstractAction {
   private final Action[] actions;
   private final boolean stopOnError;

   public CompositeAction(List actions, boolean stopOnError) {
      this.actions = new Action[actions.size()];
      actions.toArray(this.actions);
      this.stopOnError = stopOnError;
   }

   public void run() {
      try {
         this.execute();
      } catch (IOException var2) {
         LOGGER.warn((String)"Exception during file rollover.", (Throwable)var2);
      }

   }

   public boolean execute() throws IOException {
      if(this.stopOnError) {
         for(Action action : this.actions) {
            if(!action.execute()) {
               return false;
            }
         }

         return true;
      } else {
         boolean status = true;
         IOException exception = null;

         for(Action action : this.actions) {
            try {
               status &= action.execute();
            } catch (IOException var8) {
               status = false;
               if(exception == null) {
                  exception = var8;
               }
            }
         }

         if(exception != null) {
            throw exception;
         } else {
            return status;
         }
      }
   }
}
