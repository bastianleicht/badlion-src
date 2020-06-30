package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;

@Plugin(
   name = "MessagePatternConverter",
   category = "Converter"
)
@ConverterKeys({"m", "msg", "message"})
public final class MessagePatternConverter extends LogEventPatternConverter {
   private final String[] formats;
   private final Configuration config;

   private MessagePatternConverter(Configuration config, String[] options) {
      super("Message", "message");
      this.formats = options;
      this.config = config;
   }

   public static MessagePatternConverter newInstance(Configuration config, String[] options) {
      return new MessagePatternConverter(config, options);
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      Message msg = event.getMessage();
      if(msg != null) {
         String result;
         if(msg instanceof MultiformatMessage) {
            result = ((MultiformatMessage)msg).getFormattedMessage(this.formats);
         } else {
            result = msg.getFormattedMessage();
         }

         if(result != null) {
            toAppendTo.append(this.config != null && result.contains("${")?this.config.getStrSubstitutor().replace(event, result):result);
         } else {
            toAppendTo.append("null");
         }
      }

   }
}
