package org.apache.logging.log4j.core.net;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.net.AbstractJMSManager;

public class JMSTopicManager extends AbstractJMSManager {
   private static final JMSTopicManager.JMSTopicManagerFactory FACTORY = new JMSTopicManager.JMSTopicManagerFactory();
   private JMSTopicManager.TopicInfo info;
   private final String factoryBindingName;
   private final String topicBindingName;
   private final String userName;
   private final String password;
   private final Context context;

   protected JMSTopicManager(String name, Context context, String factoryBindingName, String topicBindingName, String userName, String password, JMSTopicManager.TopicInfo info) {
      super(name);
      this.context = context;
      this.factoryBindingName = factoryBindingName;
      this.topicBindingName = topicBindingName;
      this.userName = userName;
      this.password = password;
      this.info = info;
   }

   public static JMSTopicManager getJMSTopicManager(String factoryName, String providerURL, String urlPkgPrefixes, String securityPrincipalName, String securityCredentials, String factoryBindingName, String topicBindingName, String userName, String password) {
      if(factoryBindingName == null) {
         LOGGER.error("No factory name provided for JMSTopicManager");
         return null;
      } else if(topicBindingName == null) {
         LOGGER.error("No topic name provided for JMSTopicManager");
         return null;
      } else {
         String name = "JMSTopic:" + factoryBindingName + '.' + topicBindingName;
         return (JMSTopicManager)getManager(name, FACTORY, new JMSTopicManager.FactoryData(factoryName, providerURL, urlPkgPrefixes, securityPrincipalName, securityCredentials, factoryBindingName, topicBindingName, userName, password));
      }
   }

   public void send(Serializable object) throws Exception {
      if(this.info == null) {
         this.info = connect(this.context, this.factoryBindingName, this.topicBindingName, this.userName, this.password, false);
      }

      try {
         super.send(object, this.info.session, this.info.publisher);
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

   private static JMSTopicManager.TopicInfo connect(Context context, String factoryBindingName, String queueBindingName, String userName, String password, boolean suppress) throws Exception {
      try {
         TopicConnectionFactory factory = (TopicConnectionFactory)lookup(context, factoryBindingName);
         TopicConnection conn;
         if(userName != null) {
            conn = factory.createTopicConnection(userName, password);
         } else {
            conn = factory.createTopicConnection();
         }

         TopicSession sess = conn.createTopicSession(false, 1);
         Topic topic = (Topic)lookup(context, queueBindingName);
         TopicPublisher publisher = sess.createPublisher(topic);
         conn.start();
         return new JMSTopicManager.TopicInfo(conn, sess, publisher);
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
      private final String topicBindingName;
      private final String userName;
      private final String password;

      public FactoryData(String factoryName, String providerURL, String urlPkgPrefixes, String securityPrincipalName, String securityCredentials, String factoryBindingName, String topicBindingName, String userName, String password) {
         this.factoryName = factoryName;
         this.providerURL = providerURL;
         this.urlPkgPrefixes = urlPkgPrefixes;
         this.securityPrincipalName = securityPrincipalName;
         this.securityCredentials = securityCredentials;
         this.factoryBindingName = factoryBindingName;
         this.topicBindingName = topicBindingName;
         this.userName = userName;
         this.password = password;
      }
   }

   private static class JMSTopicManagerFactory implements ManagerFactory {
      private JMSTopicManagerFactory() {
      }

      public JMSTopicManager createManager(String name, JMSTopicManager.FactoryData data) {
         try {
            Context ctx = AbstractJMSManager.createContext(data.factoryName, data.providerURL, data.urlPkgPrefixes, data.securityPrincipalName, data.securityCredentials);
            JMSTopicManager.TopicInfo info = JMSTopicManager.connect(ctx, data.factoryBindingName, data.topicBindingName, data.userName, data.password, true);
            return new JMSTopicManager(name, ctx, data.factoryBindingName, data.topicBindingName, data.userName, data.password, info);
         } catch (NamingException var5) {
            JMSTopicManager.LOGGER.error((String)"Unable to locate resource", (Throwable)var5);
         } catch (Exception var6) {
            JMSTopicManager.LOGGER.error((String)"Unable to connect", (Throwable)var6);
         }

         return null;
      }
   }

   private static class TopicInfo {
      private final TopicConnection conn;
      private final TopicSession session;
      private final TopicPublisher publisher;

      public TopicInfo(TopicConnection conn, TopicSession session, TopicPublisher publisher) {
         this.conn = conn;
         this.session = session;
         this.publisher = publisher;
      }
   }
}
