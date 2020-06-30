package org.apache.logging.log4j.core.appender.db.jpa.converter;

import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(
   autoApply = false
)
public class ContextMapAttributeConverter implements AttributeConverter {
   public String convertToDatabaseColumn(Map contextMap) {
      return contextMap == null?null:contextMap.toString();
   }

   public Map convertToEntityAttribute(String s) {
      throw new UnsupportedOperationException("Log events can only be persisted, not extracted.");
   }
}
