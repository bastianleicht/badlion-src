package org.apache.logging.log4j.message;

import org.apache.logging.log4j.message.Message;

public class SimpleMessage implements Message {
   private static final long serialVersionUID = -8398002534962715992L;
   private final String message;

   public SimpleMessage() {
      this((String)null);
   }

   public SimpleMessage(String message) {
      this.message = message;
   }

   public String getFormattedMessage() {
      return this.message;
   }

   public String getFormat() {
      return this.message;
   }

   public Object[] getParameters() {
      return null;
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         boolean var10000;
         label0: {
            SimpleMessage that = (SimpleMessage)o;
            if(this.message != null) {
               if(this.message.equals(that.message)) {
                  break label0;
               }
            } else if(that.message == null) {
               break label0;
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.message != null?this.message.hashCode():0;
   }

   public String toString() {
      return "SimpleMessage[message=" + this.message + "]";
   }

   public Throwable getThrowable() {
      return null;
   }
}
