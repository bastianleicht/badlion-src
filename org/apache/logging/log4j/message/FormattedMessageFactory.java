package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.Message;

public class FormattedMessageFactory extends AbstractMessageFactory {
   public Message newMessage(String message, Object... params) {
      return new FormattedMessage(message, params);
   }
}
