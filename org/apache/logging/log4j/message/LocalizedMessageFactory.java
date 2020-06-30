package org.apache.logging.log4j.message;

import java.util.ResourceBundle;
import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.LocalizedMessage;
import org.apache.logging.log4j.message.Message;

public class LocalizedMessageFactory extends AbstractMessageFactory {
   private final ResourceBundle bundle;
   private final String bundleId;

   public LocalizedMessageFactory(ResourceBundle bundle) {
      this.bundle = bundle;
      this.bundleId = null;
   }

   public LocalizedMessageFactory(String bundleId) {
      this.bundle = null;
      this.bundleId = bundleId;
   }

   public Message newMessage(String message, Object... params) {
      return this.bundle == null?new LocalizedMessage(this.bundleId, message, params):new LocalizedMessage(this.bundle, message, params);
   }
}
