package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

public final class ParameterizedMessageFactory extends AbstractMessageFactory {
   public static final ParameterizedMessageFactory INSTANCE = new ParameterizedMessageFactory();

   public Message newMessage(String message, Object... params) {
      return new ParameterizedMessage(message, params);
   }
}
