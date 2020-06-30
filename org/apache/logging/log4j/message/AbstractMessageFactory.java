package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.SimpleMessage;

public abstract class AbstractMessageFactory implements MessageFactory {
   public Message newMessage(Object message) {
      return new ObjectMessage(message);
   }

   public Message newMessage(String message) {
      return new SimpleMessage(message);
   }

   public abstract Message newMessage(String var1, Object... var2);
}
