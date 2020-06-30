package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;

public class MessageFormatMessageFactory extends AbstractMessageFactory {
   public Message newMessage(String message, Object... params) {
      return new MessageFormatMessage(message, params);
   }
}
