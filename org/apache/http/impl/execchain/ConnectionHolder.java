package org.apache.http.impl.execchain;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.HttpClientConnectionManager;

@ThreadSafe
class ConnectionHolder implements ConnectionReleaseTrigger, Cancellable, Closeable {
   private final Log log;
   private final HttpClientConnectionManager manager;
   private final HttpClientConnection managedConn;
   private volatile boolean reusable;
   private volatile Object state;
   private volatile long validDuration;
   private volatile TimeUnit tunit;
   private volatile boolean released;

   public ConnectionHolder(Log log, HttpClientConnectionManager manager, HttpClientConnection managedConn) {
      this.log = log;
      this.manager = manager;
      this.managedConn = managedConn;
   }

   public boolean isReusable() {
      return this.reusable;
   }

   public void markReusable() {
      this.reusable = true;
   }

   public void markNonReusable() {
      this.reusable = false;
   }

   public void setState(Object state) {
      this.state = state;
   }

   public void setValidFor(long duration, TimeUnit tunit) {
      synchronized(this.managedConn) {
         this.validDuration = duration;
         this.tunit = tunit;
      }
   }

   public void releaseConnection() {
      synchronized(this.managedConn) {
         if(!this.released) {
            this.released = true;
            if(this.reusable) {
               this.manager.releaseConnection(this.managedConn, this.state, this.validDuration, this.tunit);
            } else {
               try {
                  this.managedConn.close();
                  this.log.debug("Connection discarded");
               } catch (IOException var8) {
                  if(this.log.isDebugEnabled()) {
                     this.log.debug(var8.getMessage(), var8);
                  }
               } finally {
                  this.manager.releaseConnection(this.managedConn, (Object)null, 0L, TimeUnit.MILLISECONDS);
               }
            }

         }
      }
   }

   public void abortConnection() {
      synchronized(this.managedConn) {
         if(!this.released) {
            this.released = true;

            try {
               this.managedConn.shutdown();
               this.log.debug("Connection discarded");
            } catch (IOException var8) {
               if(this.log.isDebugEnabled()) {
                  this.log.debug(var8.getMessage(), var8);
               }
            } finally {
               this.manager.releaseConnection(this.managedConn, (Object)null, 0L, TimeUnit.MILLISECONDS);
            }

         }
      }
   }

   public boolean cancel() {
      boolean alreadyReleased = this.released;
      this.log.debug("Cancelling request execution");
      this.abortConnection();
      return !alreadyReleased;
   }

   public boolean isReleased() {
      return this.released;
   }

   public void close() throws IOException {
      this.abortConnection();
   }
}
