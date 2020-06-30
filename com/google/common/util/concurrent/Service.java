package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public interface Service {
   Service startAsync();

   boolean isRunning();

   Service.State state();

   Service stopAsync();

   void awaitRunning();

   void awaitRunning(long var1, TimeUnit var3) throws TimeoutException;

   void awaitTerminated();

   void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException;

   Throwable failureCause();

   void addListener(Service.Listener var1, Executor var2);

   @Beta
   public abstract static class Listener {
      public void starting() {
      }

      public void running() {
      }

      public void stopping(Service.State from) {
      }

      public void terminated(Service.State from) {
      }

      public void failed(Service.State from, Throwable failure) {
      }
   }

   @Beta
   public static enum State {
      NEW {
         boolean isTerminal() {
            return false;
         }
      },
      STARTING {
         boolean isTerminal() {
            return false;
         }
      },
      RUNNING {
         boolean isTerminal() {
            return false;
         }
      },
      STOPPING {
         boolean isTerminal() {
            return false;
         }
      },
      TERMINATED {
         boolean isTerminal() {
            return true;
         }
      },
      FAILED {
         boolean isTerminal() {
            return true;
         }
      };

      private State() {
      }

      abstract boolean isTerminal();
   }
}
