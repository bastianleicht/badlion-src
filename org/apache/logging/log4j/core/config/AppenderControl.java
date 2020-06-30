package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.filter.Filterable;

public class AppenderControl extends AbstractFilterable {
   private final ThreadLocal recursive = new ThreadLocal();
   private final Appender appender;
   private final Level level;
   private final int intLevel;

   public AppenderControl(Appender appender, Level level, Filter filter) {
      super(filter);
      this.appender = appender;
      this.level = level;
      this.intLevel = level == null?Level.ALL.intLevel():level.intLevel();
      this.startFilter();
   }

   public Appender getAppender() {
      return this.appender;
   }

   public void callAppender(LogEvent event) {
      if(this.getFilter() != null) {
         Filter.Result r = this.getFilter().filter(event);
         if(r == Filter.Result.DENY) {
            return;
         }
      }

      if(this.level == null || this.intLevel >= event.getLevel().intLevel()) {
         if(this.recursive.get() != null) {
            this.appender.getHandler().error("Recursive call to appender " + this.appender.getName());
         } else {
            try {
               this.recursive.set(this);
               if(!this.appender.isStarted()) {
                  this.appender.getHandler().error("Attempted to append to non-started appender " + this.appender.getName());
                  if(!this.appender.ignoreExceptions()) {
                     throw new AppenderLoggingException("Attempted to append to non-started appender " + this.appender.getName());
                  }
               }

               if(!(this.appender instanceof Filterable) || !((Filterable)this.appender).isFiltered(event)) {
                  try {
                     this.appender.append(event);
                     return;
                  } catch (RuntimeException var7) {
                     this.appender.getHandler().error("An exception occurred processing Appender " + this.appender.getName(), var7);
                     if(this.appender.ignoreExceptions()) {
                        return;
                     }

                     throw var7;
                  } catch (Exception var8) {
                     this.appender.getHandler().error("An exception occurred processing Appender " + this.appender.getName(), var8);
                     if(this.appender.ignoreExceptions()) {
                        return;
                     }

                     throw new AppenderLoggingException(var8);
                  }
               }
            } finally {
               this.recursive.set((Object)null);
            }

         }
      }
   }
}
