package org.apache.logging.log4j.core.appender.db.jpa.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.core.helpers.Strings;

@Converter(
   autoApply = false
)
public class ContextMapJsonAttributeConverter implements AttributeConverter {
   static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public String convertToDatabaseColumn(Map contextMap) {
      if(contextMap == null) {
         return null;
      } else {
         try {
            return OBJECT_MAPPER.writeValueAsString(contextMap);
         } catch (IOException var3) {
            throw new PersistenceException("Failed to convert map to JSON string.", var3);
         }
      }
   }

   public Map convertToEntityAttribute(String s) {
      if(Strings.isEmpty(s)) {
         return null;
      } else {
         try {
            return (Map)OBJECT_MAPPER.readValue(s, new TypeReference() {
            });
         } catch (IOException var3) {
            throw new PersistenceException("Failed to convert JSON string to map.", var3);
         }
      }
   }
}
