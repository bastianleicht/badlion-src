package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

@Converter(
   autoApply = false
)
public class MessageAttributeConverter implements AttributeConverter {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   public String convertToDatabaseColumn(Message message) {
      return message == null?null:message.getFormattedMessage();
   }

   public Message convertToEntityAttribute(String s) {
      return Strings.isEmpty(s)?null:LOGGER.getMessageFactory().newMessage(s);
   }
}
