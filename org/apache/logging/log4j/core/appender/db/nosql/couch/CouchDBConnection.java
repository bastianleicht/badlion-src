package org.apache.logging.log4j.core.appender.db.nosql.couch;

import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.apache.logging.log4j.core.appender.db.nosql.couch.CouchDBObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

public final class CouchDBConnection implements NoSQLConnection {
   private final CouchDbClient client;
   private boolean closed = false;

   public CouchDBConnection(CouchDbClient client) {
      this.client = client;
   }

   public CouchDBObject createObject() {
      return new CouchDBObject();
   }

   public CouchDBObject[] createList(int length) {
      return new CouchDBObject[length];
   }

   public void insertObject(NoSQLObject object) {
      try {
         Response response = this.client.save(object.unwrap());
         if(response.getError() != null && response.getError().length() > 0) {
            throw new AppenderLoggingException("Failed to write log event to CouchDB due to error: " + response.getError() + ".");
         }
      } catch (Exception var3) {
         throw new AppenderLoggingException("Failed to write log event to CouchDB due to error: " + var3.getMessage(), var3);
      }
   }

   public synchronized void close() {
      this.closed = true;
      this.client.shutdown();
   }

   public synchronized boolean isClosed() {
      return this.closed;
   }
}
