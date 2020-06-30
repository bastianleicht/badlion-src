package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.IllegalFormatException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

public class MessageFormatMessage implements Message {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final long serialVersionUID = -665975803997290697L;
   private static final int HASHVAL = 31;
   private String messagePattern;
   private transient Object[] argArray;
   private String[] stringArgs;
   private transient String formattedMessage;
   private transient Throwable throwable;

   public MessageFormatMessage(String messagePattern, Object... arguments) {
      this.messagePattern = messagePattern;
      this.argArray = arguments;
      if(arguments != null && arguments.length > 0 && arguments[arguments.length - 1] instanceof Throwable) {
         this.throwable = (Throwable)arguments[arguments.length - 1];
      }

   }

   public String getFormattedMessage() {
      if(this.formattedMessage == null) {
         this.formattedMessage = this.formatMessage(this.messagePattern, this.argArray);
      }

      return this.formattedMessage;
   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return (Object[])(this.argArray != null?this.argArray:this.stringArgs);
   }

   protected String formatMessage(String msgPattern, Object... args) {
      try {
         return MessageFormat.format(msgPattern, args);
      } catch (IllegalFormatException var4) {
         LOGGER.error((String)("Unable to format msg: " + msgPattern), (Throwable)var4);
         return msgPattern;
      }
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         MessageFormatMessage that = (MessageFormatMessage)o;
         if(this.messagePattern != null) {
            if(!this.messagePattern.equals(that.messagePattern)) {
               return false;
            }
         } else if(that.messagePattern != null) {
            return false;
         }

         if(!Arrays.equals(this.stringArgs, that.stringArgs)) {
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.messagePattern != null?this.messagePattern.hashCode():0;
      result = 31 * result + (this.stringArgs != null?Arrays.hashCode(this.stringArgs):0);
      return result;
   }

   public String toString() {
      return "StringFormatMessage[messagePattern=" + this.messagePattern + ", args=" + Arrays.toString(this.argArray) + "]";
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.getFormattedMessage();
      out.writeUTF(this.formattedMessage);
      out.writeUTF(this.messagePattern);
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
      int length = in.readInt();
      this.stringArgs = new String[length];

      for(int i = 0; i < length; ++i) {
         this.stringArgs[i] = in.readUTF();
      }

   }

   public Throwable getThrowable() {
      return this.throwable;
   }
}
