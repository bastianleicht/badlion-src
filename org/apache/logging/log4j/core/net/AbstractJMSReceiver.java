package org.apache.logging.log4j.core.net;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractServer;
import org.apache.logging.log4j.core.LogEvent;

public abstract class AbstractJMSReceiver extends AbstractServer implements MessageListener {
   protected Logger logger = LogManager.getLogger(this.getClass().getName());

   public void onMessage(Message message) {
      try {
         if(message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage)message;
            this.log((LogEvent)objectMessage.getObject());
         } else {
            this.logger.warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
         }
      } catch (JMSException var3) {
         this.logger.error((String)"Exception thrown while processing incoming message.", (Throwable)var3);
      }

   }

   protected Object lookup(Context ctx, String name) throws NamingException {
      try {
         return ctx.lookup(name);
      } catch (NameNotFoundException var4) {
         this.logger.error("Could not find name [" + name + "].");
         throw var4;
      }
   }
}
