package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFormatMessage;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;

public class FormattedMessage implements Message {
   private static final long serialVersionUID = -665975803997290697L;
   private static final int HASHVAL = 31;
   private static final String FORMAT_SPECIFIER = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
   private static final Pattern MSG_PATTERN = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");
   private String messagePattern;
   private transient Object[] argArray;
   private String[] stringArgs;
   private transient String formattedMessage;
   private final Throwable throwable;
   private Message message;

   public FormattedMessage(String messagePattern, Object[] arguments, Throwable throwable) {
      this.messagePattern = messagePattern;
      this.argArray = arguments;
      this.throwable = throwable;
   }

   public FormattedMessage(String messagePattern, Object[] arguments) {
      this.messagePattern = messagePattern;
      this.argArray = arguments;
      this.throwable = null;
   }

   public FormattedMessage(String messagePattern, Object arg) {
      this.messagePattern = messagePattern;
      this.argArray = new Object[]{arg};
      this.throwable = null;
   }

   public FormattedMessage(String messagePattern, Object arg1, Object arg2) {
      this(messagePattern, new Object[]{arg1, arg2});
   }

   public String getFormattedMessage() {
      if(this.formattedMessage == null) {
         if(this.message == null) {
            this.message = this.getMessage(this.messagePattern, this.argArray, this.throwable);
         }

         this.formattedMessage = this.message.getFormattedMessage();
      }

      return this.formattedMessage;
   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return (Object[])(this.argArray != null?this.argArray:this.stringArgs);
   }

   protected Message getMessage(String msgPattern, Object[] args, Throwable throwable) {
      try {
         MessageFormat format = new MessageFormat(msgPattern);
         Format[] formats = format.getFormats();
         if(formats != null && formats.length > 0) {
            return new MessageFormatMessage(msgPattern, args);
         }
      } catch (Exception var7) {
         ;
      }

      try {
         if(MSG_PATTERN.matcher(msgPattern).find()) {
            return new StringFormattedMessage(msgPattern, args);
         }
      } catch (Exception var6) {
         ;
      }

      return new ParameterizedMessage(msgPattern, args, throwable);
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         FormattedMessage that = (FormattedMessage)o;
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
      return "FormattedMessage[messagePattern=" + this.messagePattern + ", args=" + Arrays.toString(this.argArray) + "]";
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
      if(this.throwable != null) {
         return this.throwable;
      } else {
         if(this.message == null) {
            this.message = this.getMessage(this.messagePattern, this.argArray, this.throwable);
         }

         return this.message.getThrowable();
      }
   }
}
