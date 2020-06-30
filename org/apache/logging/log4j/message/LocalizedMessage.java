package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

public class LocalizedMessage implements Message, LoggerNameAwareMessage {
   private static final long serialVersionUID = 3893703791567290742L;
   private String bundleId;
   private transient ResourceBundle bundle;
   private Locale locale;
   private transient StatusLogger logger;
   private String loggerName;
   private String messagePattern;
   private String[] stringArgs;
   private transient Object[] argArray;
   private String formattedMessage;
   private transient Throwable throwable;

   public LocalizedMessage(String messagePattern, Object[] arguments) {
      this((ResourceBundle)null, (Locale)null, messagePattern, arguments);
   }

   public LocalizedMessage(String bundleId, String key, Object[] arguments) {
      this(bundleId, (Locale)null, key, arguments);
   }

   public LocalizedMessage(ResourceBundle bundle, String key, Object[] arguments) {
      this(bundle, (Locale)null, key, arguments);
   }

   public LocalizedMessage(String bundleId, Locale locale, String key, Object[] arguments) {
      this.logger = StatusLogger.getLogger();
      this.messagePattern = key;
      this.argArray = arguments;
      this.throwable = null;
      this.setup(bundleId, (ResourceBundle)null, locale);
   }

   public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object[] arguments) {
      this.logger = StatusLogger.getLogger();
      this.messagePattern = key;
      this.argArray = arguments;
      this.throwable = null;
      this.setup((String)null, bundle, locale);
   }

   public LocalizedMessage(Locale locale, String key, Object[] arguments) {
      this((ResourceBundle)null, locale, key, arguments);
   }

   public LocalizedMessage(String messagePattern, Object arg) {
      this((ResourceBundle)null, (Locale)null, messagePattern, new Object[]{arg});
   }

   public LocalizedMessage(String bundleId, String key, Object arg) {
      this(bundleId, (Locale)null, key, new Object[]{arg});
   }

   public LocalizedMessage(ResourceBundle bundle, String key, Object arg) {
      this(bundle, (Locale)null, key, new Object[]{arg});
   }

   public LocalizedMessage(String bundleId, Locale locale, String key, Object arg) {
      this(bundleId, locale, key, new Object[]{arg});
   }

   public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object arg) {
      this(bundle, locale, key, new Object[]{arg});
   }

   public LocalizedMessage(Locale locale, String key, Object arg) {
      this((ResourceBundle)null, locale, key, new Object[]{arg});
   }

   public LocalizedMessage(String messagePattern, Object arg1, Object arg2) {
      this((ResourceBundle)null, (Locale)null, messagePattern, new Object[]{arg1, arg2});
   }

   public LocalizedMessage(String bundleId, String key, Object arg1, Object arg2) {
      this(bundleId, (Locale)null, key, new Object[]{arg1, arg2});
   }

   public LocalizedMessage(ResourceBundle bundle, String key, Object arg1, Object arg2) {
      this(bundle, (Locale)null, key, new Object[]{arg1, arg2});
   }

   public LocalizedMessage(String bundleId, Locale locale, String key, Object arg1, Object arg2) {
      this(bundleId, locale, key, new Object[]{arg1, arg2});
   }

   public LocalizedMessage(ResourceBundle bundle, Locale locale, String key, Object arg1, Object arg2) {
      this(bundle, locale, key, new Object[]{arg1, arg2});
   }

   public LocalizedMessage(Locale locale, String key, Object arg1, Object arg2) {
      this((ResourceBundle)null, locale, key, new Object[]{arg1, arg2});
   }

   public void setLoggerName(String name) {
      this.loggerName = name;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   private void setup(String bundleId, ResourceBundle bundle, Locale locale) {
      this.bundleId = bundleId;
      this.bundle = bundle;
      this.locale = locale;
   }

   public String getFormattedMessage() {
      if(this.formattedMessage != null) {
         return this.formattedMessage;
      } else {
         ResourceBundle bundle = this.bundle;
         if(bundle == null) {
            if(this.bundleId != null) {
               bundle = this.getBundle(this.bundleId, this.locale, false);
            } else {
               bundle = this.getBundle(this.loggerName, this.locale, true);
            }
         }

         String messagePattern = this.getFormat();
         String msgPattern = bundle != null && bundle.containsKey(messagePattern)?bundle.getString(messagePattern):messagePattern;
         Object[] array = (Object[])(this.argArray == null?this.stringArgs:this.argArray);
         FormattedMessage msg = new FormattedMessage(msgPattern, array);
         this.formattedMessage = msg.getFormattedMessage();
         this.throwable = msg.getThrowable();
         return this.formattedMessage;
      }
   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return (Object[])(this.argArray != null?this.argArray:this.stringArgs);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   protected ResourceBundle getBundle(String key, Locale locale, boolean loop) {
      ResourceBundle rb = null;
      if(key == null) {
         return null;
      } else {
         try {
            if(locale != null) {
               rb = ResourceBundle.getBundle(key, locale);
            } else {
               rb = ResourceBundle.getBundle(key);
            }
         } catch (MissingResourceException var9) {
            if(!loop) {
               this.logger.debug("Unable to locate ResourceBundle " + key);
               return null;
            }
         }

         String substr = key;

         int i;
         while(rb == null && (i = substr.lastIndexOf(46)) > 0) {
            substr = substr.substring(0, i);

            try {
               if(locale != null) {
                  rb = ResourceBundle.getBundle(substr, locale);
               } else {
                  rb = ResourceBundle.getBundle(substr);
               }
            } catch (MissingResourceException var8) {
               this.logger.debug("Unable to locate ResourceBundle " + substr);
            }
         }

         return rb;
      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.getFormattedMessage();
      out.writeUTF(this.formattedMessage);
      out.writeUTF(this.messagePattern);
      out.writeUTF(this.bundleId);
      out.writeInt(this.argArray.length);
      this.stringArgs = new String[this.argArray.length];
      int i = 0;

      for(Object obj : this.argArray) {
         this.stringArgs[i] = obj.toString();
         ++i;
      }

   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.formattedMessage = in.readUTF();
      this.messagePattern = in.readUTF();
      this.bundleId = in.readUTF();
      int length = in.readInt();
      this.stringArgs = new String[length];

      for(int i = 0; i < length; ++i) {
         this.stringArgs[i] = in.readUTF();
      }

      this.logger = StatusLogger.getLogger();
      this.bundle = null;
      this.argArray = null;
   }
}
