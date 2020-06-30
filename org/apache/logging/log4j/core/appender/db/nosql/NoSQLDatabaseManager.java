package org.apache.logging.log4j.core.appender.db.nosql;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;

public final class NoSQLDatabaseManager extends AbstractDatabaseManager {
   private static final NoSQLDatabaseManager.NoSQLDatabaseManagerFactory FACTORY = new NoSQLDatabaseManager.NoSQLDatabaseManagerFactory();
   private final NoSQLProvider provider;
   private NoSQLConnection connection;

   private NoSQLDatabaseManager(String name, int bufferSize, NoSQLProvider provider) {
      super(name, bufferSize);
      this.provider = provider;
   }

   protected void connectInternal() {
      this.connection = this.provider.getConnection();
   }

   protected void disconnectInternal() {
      if(this.connection != null && !this.connection.isClosed()) {
         this.connection.close();
      }

   }

   protected void writeInternal(LogEvent event) {
      if(this.isConnected() && this.connection != null && !this.connection.isClosed()) {
         NoSQLObject<W> entity = this.connection.createObject();
         entity.set("level", (Object)event.getLevel());
         entity.set("loggerName", (Object)event.getLoggerName());
         entity.set("message", (Object)(event.getMessage() == null?null:event.getMessage().getFormattedMessage()));
         StackTraceElement source = event.getSource();
         if(source == null) {
            entity.set("source", (Object)null);
         } else {
            entity.set("source", this.convertStackTraceElement(source));
         }

         Marker marker = event.getMarker();
         if(marker == null) {
            entity.set("marker", (Object)null);
         } else {
            NoSQLObject<W> originalMarkerEntity = this.connection.createObject();
            NoSQLObject<W> markerEntity = originalMarkerEntity;
            originalMarkerEntity.set("name", (Object)marker.getName());

            while(marker.getParent() != null) {
               marker = marker.getParent();
               NoSQLObject<W> parentMarkerEntity = this.connection.createObject();
               parentMarkerEntity.set("name", (Object)marker.getName());
               markerEntity.set("parent", parentMarkerEntity);
               markerEntity = parentMarkerEntity;
            }

            entity.set("marker", originalMarkerEntity);
         }

         entity.set("threadName", (Object)event.getThreadName());
         entity.set("millis", (Object)Long.valueOf(event.getMillis()));
         entity.set("date", (Object)(new Date(event.getMillis())));
         Throwable thrown = event.getThrown();
         if(thrown == null) {
            entity.set("thrown", (Object)null);
         } else {
            NoSQLObject<W> originalExceptionEntity = this.connection.createObject();
            NoSQLObject<W> exceptionEntity = originalExceptionEntity;
            originalExceptionEntity.set("type", (Object)thrown.getClass().getName());
            originalExceptionEntity.set("message", (Object)thrown.getMessage());
            originalExceptionEntity.set("stackTrace", this.convertStackTrace(thrown.getStackTrace()));

            while(thrown.getCause() != null) {
               thrown = thrown.getCause();
               NoSQLObject<W> causingExceptionEntity = this.connection.createObject();
               causingExceptionEntity.set("type", (Object)thrown.getClass().getName());
               causingExceptionEntity.set("message", (Object)thrown.getMessage());
               causingExceptionEntity.set("stackTrace", this.convertStackTrace(thrown.getStackTrace()));
               exceptionEntity.set("cause", causingExceptionEntity);
               exceptionEntity = causingExceptionEntity;
            }

            entity.set("thrown", originalExceptionEntity);
         }

         Map<String, String> contextMap = event.getContextMap();
         if(contextMap == null) {
            entity.set("contextMap", (Object)null);
         } else {
            NoSQLObject<W> contextMapEntity = this.connection.createObject();

            for(Entry<String, String> entry : contextMap.entrySet()) {
               contextMapEntity.set((String)entry.getKey(), entry.getValue());
            }

            entity.set("contextMap", contextMapEntity);
         }

         ThreadContext.ContextStack contextStack = event.getContextStack();
         if(contextStack == null) {
            entity.set("contextStack", (Object)null);
         } else {
            entity.set("contextStack", contextStack.asList().toArray());
         }

         this.connection.insertObject(entity);
      } else {
         throw new AppenderLoggingException("Cannot write logging event; NoSQL manager not connected to the database.");
      }
   }

   private NoSQLObject[] convertStackTrace(StackTraceElement[] stackTrace) {
      NoSQLObject<W>[] stackTraceEntities = this.connection.createList(stackTrace.length);

      for(int i = 0; i < stackTrace.length; ++i) {
         stackTraceEntities[i] = this.convertStackTraceElement(stackTrace[i]);
      }

      return stackTraceEntities;
   }

   private NoSQLObject convertStackTraceElement(StackTraceElement element) {
      NoSQLObject<W> elementEntity = this.connection.createObject();
      elementEntity.set("className", (Object)element.getClassName());
      elementEntity.set("methodName", (Object)element.getMethodName());
      elementEntity.set("fileName", (Object)element.getFileName());
      elementEntity.set("lineNumber", (Object)Integer.valueOf(element.getLineNumber()));
      return elementEntity;
   }

   public static NoSQLDatabaseManager getNoSQLDatabaseManager(String name, int bufferSize, NoSQLProvider provider) {
      return (NoSQLDatabaseManager)AbstractDatabaseManager.getManager(name, new NoSQLDatabaseManager.FactoryData(bufferSize, provider), FACTORY);
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final NoSQLProvider provider;

      protected FactoryData(int bufferSize, NoSQLProvider provider) {
         super(bufferSize);
         this.provider = provider;
      }
   }

   private static final class NoSQLDatabaseManagerFactory implements ManagerFactory {
      private NoSQLDatabaseManagerFactory() {
      }

      public NoSQLDatabaseManager createManager(String name, NoSQLDatabaseManager.FactoryData data) {
         return new NoSQLDatabaseManager(name, data.getBufferSize(), data.provider);
      }
   }
}
