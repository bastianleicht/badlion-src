package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.concurrent.BackgroundInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public class MultiBackgroundInitializer extends BackgroundInitializer {
   private final Map childInitializers = new HashMap();

   public MultiBackgroundInitializer() {
   }

   public MultiBackgroundInitializer(ExecutorService exec) {
      super(exec);
   }

   public void addInitializer(String name, BackgroundInitializer init) {
      if(name == null) {
         throw new IllegalArgumentException("Name of child initializer must not be null!");
      } else if(init == null) {
         throw new IllegalArgumentException("Child initializer must not be null!");
      } else {
         synchronized(this) {
            if(this.isStarted()) {
               throw new IllegalStateException("addInitializer() must not be called after start()!");
            } else {
               this.childInitializers.put(name, init);
            }
         }
      }
   }

   protected int getTaskCount() {
      int result = 1;

      for(BackgroundInitializer<?> bi : this.childInitializers.values()) {
         result += bi.getTaskCount();
      }

      return result;
   }

   protected MultiBackgroundInitializer.MultiBackgroundInitializerResults initialize() throws Exception {
      Map<String, BackgroundInitializer<?>> inits;
      synchronized(this) {
         inits = new HashMap(this.childInitializers);
      }

      ExecutorService exec = this.getActiveExecutor();

      for(BackgroundInitializer<?> bi : inits.values()) {
         if(bi.getExternalExecutor() == null) {
            bi.setExternalExecutor(exec);
         }

         bi.start();
      }

      Map<String, Object> results = new HashMap();
      Map<String, ConcurrentException> excepts = new HashMap();

      for(Entry<String, BackgroundInitializer<?>> e : inits.entrySet()) {
         try {
            results.put(e.getKey(), ((BackgroundInitializer)e.getValue()).get());
         } catch (ConcurrentException var8) {
            excepts.put(e.getKey(), var8);
         }
      }

      return new MultiBackgroundInitializer.MultiBackgroundInitializerResults(inits, results, excepts);
   }

   public static class MultiBackgroundInitializerResults {
      private final Map initializers;
      private final Map resultObjects;
      private final Map exceptions;

      private MultiBackgroundInitializerResults(Map inits, Map results, Map excepts) {
         this.initializers = inits;
         this.resultObjects = results;
         this.exceptions = excepts;
      }

      public BackgroundInitializer getInitializer(String name) {
         return this.checkName(name);
      }

      public Object getResultObject(String name) {
         this.checkName(name);
         return this.resultObjects.get(name);
      }

      public boolean isException(String name) {
         this.checkName(name);
         return this.exceptions.containsKey(name);
      }

      public ConcurrentException getException(String name) {
         this.checkName(name);
         return (ConcurrentException)this.exceptions.get(name);
      }

      public Set initializerNames() {
         return Collections.unmodifiableSet(this.initializers.keySet());
      }

      public boolean isSuccessful() {
         return this.exceptions.isEmpty();
      }

      private BackgroundInitializer checkName(String name) {
         BackgroundInitializer<?> init = (BackgroundInitializer)this.initializers.get(name);
         if(init == null) {
            throw new NoSuchElementException("No child initializer with name " + name);
         } else {
            return init;
         }
      }
   }
}
