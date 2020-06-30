package org.apache.logging.log4j.core.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.net.AbstractJMSReceiver;

public class JMSTopicReceiver extends AbstractJMSReceiver {
   public JMSTopicReceiver(String tcfBindingName, String topicBindingName, String username, String password) {
      try {
         Context ctx = new InitialContext();
         TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)this.lookup(ctx, tcfBindingName);
         TopicConnection topicConnection = topicConnectionFactory.createTopicConnection(username, password);
         topicConnection.start();
         TopicSession topicSession = topicConnection.createTopicSession(false, 1);
         Topic topic = (Topic)ctx.lookup(topicBindingName);
         TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
         topicSubscriber.setMessageListener(this);
      } catch (JMSException var11) {
         this.logger.error((String)"Could not read JMS message.", (Throwable)var11);
      } catch (NamingException var12) {
         this.logger.error((String)"Could not read JMS message.", (Throwable)var12);
      } catch (RuntimeException var13) {
         this.logger.error((String)"Could not read JMS message.", (Throwable)var13);
      }

   }

   public static void main(String[] args) throws Exception {
      if(args.length != 4) {
         usage("Wrong number of arguments.");
      }

      String tcfBindingName = args[0];
      String topicBindingName = args[1];
      String username = args[2];
      String password = args[3];
      new JMSTopicReceiver(tcfBindingName, topicBindingName, username, password);
      Charset enc = Charset.defaultCharset();
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, enc));
      System.out.println("Type \"exit\" to quit JMSTopicReceiver.");

      while(true) {
         String line = stdin.readLine();
         if(line == null || line.equalsIgnoreCase("exit")) {
            break;
         }
      }

      System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
   }

   private static void usage(String msg) {
      System.err.println(msg);
      System.err.println("Usage: java " + JMSTopicReceiver.class.getName() + " TopicConnectionFactoryBindingName TopicBindingName username password");
      System.exit(1);
   }
}
