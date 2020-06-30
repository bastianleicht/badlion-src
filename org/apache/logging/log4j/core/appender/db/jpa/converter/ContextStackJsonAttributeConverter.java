package org.apache.logging.log4j.core.appender.db.jpa.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextMapJsonAttributeConverter;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.spi.DefaultThreadContextStack;

@Converter(
   autoApply = false
)
public class ContextStackJsonAttributeConverter implements AttributeConverter {
   public String convertToDatabaseColumn(ThreadContext.ContextStack contextStack) {
      if(contextStack == null) {
         return null;
      } else {
         try {
            return ContextMapJsonAttributeConverter.OBJECT_MAPPER.writeValueAsString(contextStack.asList());
         } catch (IOException var3) {
            throw new PersistenceException("Failed to convert stack list to JSON string.", var3);
         }
      }
   }

   public ThreadContext.ContextStack convertToEntityAttribute(String s) {
      if(Strings.isEmpty(s)) {
         return null;
      } else {
         List<String> list;
         try {
            list = (List)ContextMapJsonAttributeConverter.OBJECT_MAPPER.readValue(s, new TypeReference() {
            });
         } catch (IOException var4) {
            throw new PersistenceException("Failed to convert JSON string to list for stack.", var4);
         }

         DefaultThreadContextStack result = new DefaultThreadContextStack(true);
         result.addAll(list);
         return result;
      }
   }
}
