package org.apache.logging.log4j.core.net;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.net.AbstractJMSManager;

public class JMSQueueManager extends AbstractJMSManager {
   private static final JMSQueueManager.JMSQueueManagerFactory FACTORY = new JMSQueueManager.JMSQueueManagerFactory();
   private JMSQueueManager.QueueInfo info;
   private final String factoryBindingName;
   private final String queueBindingName;
   private final String userName;
   private final String password;
   private final Context context;

   protected JMSQueueManager(String name, Context context, String factoryBindingName, String queueBindingName, String userName, String password, JMSQueueManager.QueueInfo info) {
      super(name);
      this.context = context;
      this.factoryBindingName = factoryBindingName;
      this.queueBindingName = queueBindingName;
      this.userName = userName;
      this.password = password;
      this.info = info;
   }

   public static JMSQueueManager getJMSQueueManager(String factoryName, String providerURL, String urlPkgPrefixes, String securityPrincipalName, String securityCredentials, String factoryBindingName, String queueBindingName, String userName, String password) {
      if(factoryBindingName == null) {
         LOGGER.error("No factory name provided for JMSQueueManager");
         return null;
      } else if(queueBindingName == null) {
         LOGGER.error("No topic name provided for JMSQueueManager");
         return null;
      } else {
         String name = "JMSQueue:" + factoryBindingName + '.' + queueBindingName;
         return (JMSQueueManager)getManager(name, FACTORY, new JMSQueueManager.FactoryData(factoryName, providerURL, urlPkgPrefixes, securityPrincipalName, securityCredentials, factoryBindingName, queueBindingName, userName, password));
      }
   }

   public synchronized void send(Serializable object) throws Exception {
      if(this.info == null) {
         this.info = connect(this.context, this.factoryBindingName, this.queueBindingName, this.userName, this.password, false);
      }

      try {
         super.send(object, this.info.session, this.info.sender);
      } catch (Exception var3) {
         this.cleanup(true);
         throw var3;
      }
   }

   public void releaseSub() {
      if(this.info != null) {
         this.cleanup(false);
      }

   }

   private void cleanup(boolean quiet) {
      try {
         this.info.session.close();
      } catch (Exception var4) {
         if(!quiet) {
            LOGGER.error((String)("Error closing session for " + this.getName()), (Throwable)var4);
         }
      }

      try {
         this.info.conn.close();
      } catch (Exception var3) {
         if(!quiet) {
            LOGGER.error((String)("Error closing connection for " + this.getName()), (Throwable)var3);
         }
      }

      this.info = null;
   }

   private static JMSQueueManager.QueueInfo connect(Context context, String factoryBindingName, String queueBindingName, String userName, String password, boolean suppress) throws Exception {
      try {
         QueueConnectionFactory factory = (QueueConnectionFactory)lookup(context, factoryBindingName);
         QueueConnection conn;
         if(userName != null) {
            conn = factory.createQueueConnection(userName, password);
         } else {
            conn = factory.createQueueConnection();
         }

         QueueSession sess = conn.createQueueSession(false, 1);
         Queue queue = (Queue)lookup(context, queueBindingName);
         QueueSender sender = sess.createSender(queue);
         conn.start();
         return new JMSQueueManager.QueueInfo(conn, sess, sender);
      } catch (NamingException var11) {
         LOGGER.warn((String)("Unable to locate connection factory " + factoryBindingName), (Throwable)var11);
         if(!suppress) {
            throw var11;
         }
      } catch (JMSException var12) {
         LOGGER.warn((String)("Unable to create connection to queue " + queueBindingName), (Throwable)var12);
         if(!suppress) {
            throw var12;
         }
      }

      return null;
   }

   private static class FactoryData {
      private final String factoryName;
      private final String providerURL;
      private final String urlPkgPrefixes;
      private final String securityPrincipalName;
      private final String securityCredentials;
      private final String factoryBindingName;
      private final String queueBindingName;
      private final String userName;
      private final String password;

      public FactoryData(String factoryName, String providerURL, String urlPkgPrefixes, String securityPrincipalName, String securityCredentials, String factoryBindingName, String queueBindingName, String userName, String password) {
         this.factoryName = factoryName;
         this.providerURL = providerURL;
         this.urlPkgPrefixes = urlPkgPrefixes;
         this.securityPrincipalName = securityPrincipalName;
         this.securityCredentials = securityCredentials;
         this.factoryBindingName = factoryBindingName;
         this.queueBindingName = queueBindingName;
         this.userName = userName;
         this.password = password;
      }
   }

   private static class JMSQueueManagerFactory implements ManagerFactory {
      private JMSQueueManagerFactory() {
      }

      public JMSQueueManager createManager(String name, JMSQueueManager.FactoryData data) {
         try {
            Context ctx = AbstractJMSManager.createContext(data.factoryName, data.providerURL, data.urlPkgPrefixes, data.securityPrincipalName, data.securityCredentials);
            JMSQueueManager.QueueInfo info = JMSQueueManager.connect(ctx, data.factoryBindingName, data.queueBindingName, data.userName, data.password, true);
            return new JMSQueueManager(name, ctx, data.factoryBindingName, data.queueBindingName, data.userName, data.password, info);
         } catch (NamingException var5) {
            JMSQueueManager.LOGGER.error((String)"Unable to locate resource", (Throwable)var5);
         } catch (Exception var6) {
            JMSQueueManager.LOGGER.error((String)"Unable to connect", (Throwable)var6);
         }

         return null;
      }
   }

   private static class QueueInfo {
      private final QueueConnection conn;
      private final QueueSession session;
      private final QueueSender sender;

      public QueueInfo(QueueConnection conn, QueueSession session, QueueSender sender) {
         this.conn = conn;
         this.session = session;
         this.sender = sender;
      }
   }
}
