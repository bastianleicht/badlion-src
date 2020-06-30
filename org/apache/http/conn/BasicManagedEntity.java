package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class BasicManagedEntity extends HttpEntityWrapper implements ConnectionReleaseTrigger, EofSensorWatcher {
   protected ManagedClientConnection managedConn;
   protected final boolean attemptReuse;

   public BasicManagedEntity(HttpEntity entity, ManagedClientConnection conn, boolean reuse) {
      super(entity);
      Args.notNull(conn, "Connection");
      this.managedConn = conn;
      this.attemptReuse = reuse;
   }

   public boolean isRepeatable() {
      return false;
   }

   public InputStream getContent() throws IOException {
      return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
   }

   private void ensureConsumed() throws IOException {
      if(this.managedConn != null) {
         try {
            if(this.attemptReuse) {
               EntityUtils.consume(this.wrappedEntity);
               this.managedConn.markReusable();
            } else {
               this.managedConn.unmarkReusable();
            }
         } finally {
            this.releaseManagedConnection();
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void consumeContent() throws IOException {
      this.ensureConsumed();
   }

   public void writeTo(OutputStream outstream) throws IOException {
      super.writeTo(outstream);
      this.ensureConsumed();
   }

   public void releaseConnection() throws IOException {
      this.ensureConsumed();
   }

   public void abortConnection() throws IOException {
      if(this.managedConn != null) {
         try {
            this.managedConn.abortConnection();
         } finally {
            this.managedConn = null;
         }
      }

   }

   public boolean eofDetected(InputStream wrapped) throws IOException {
      try {
         if(this.managedConn != null) {
            if(this.attemptReuse) {
               wrapped.close();
               this.managedConn.markReusable();
            } else {
               this.managedConn.unmarkReusable();
            }
         }
      } finally {
         this.releaseManagedConnection();
      }

      return false;
   }

   public boolean streamClosed(InputStream wrapped) throws IOException {
      try {
         if(this.managedConn != null) {
            if(this.attemptReuse) {
               boolean valid = this.managedConn.isOpen();

               try {
                  wrapped.close();
                  this.managedConn.markReusable();
               } catch (SocketException var7) {
                  if(valid) {
                     throw var7;
                  }
               }
            } else {
               this.managedConn.unmarkReusable();
            }
         }
      } finally {
         this.releaseManagedConnection();
      }

      return false;
   }

   public boolean streamAbort(InputStream wrapped) throws IOException {
      if(this.managedConn != null) {
         this.managedConn.abortConnection();
      }

      return false;
   }

   protected void releaseManagedConnection() throws IOException {
      if(this.managedConn != null) {
         try {
            this.managedConn.releaseConnection();
         } finally {
            this.managedConn = null;
         }
      }

   }
}
