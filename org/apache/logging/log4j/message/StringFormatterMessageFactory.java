package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StringFormattedMessage;

public final class StringFormatterMessageFactory extends AbstractMessageFactory {
   public static final StringFormatterMessageFactory INSTANCE = new StringFormatterMessageFactory();

   public Message newMessage(String message, Object... params) {
      return new StringFormattedMessage(message, params);
   }
}
