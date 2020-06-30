package org.apache.logging.log4j.core.appender;

public class TLSSyslogFrame {
   public static final char SPACE = ' ';
   private String message;
   private int messageLengthInBytes;

   public TLSSyslogFrame(String message) {
      this.setMessage(message);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
      this.setLengthInBytes();
   }

   private void setLengthInBytes() {
      this.messageLengthInBytes = this.message.length();
   }

   public byte[] getBytes() {
      String frame = this.toString();
      return frame.getBytes();
   }

   public String toString() {
      String length = Integer.toString(this.messageLengthInBytes);
      return length + ' ' + this.message;
   }

   public boolean equals(Object frame) {
      return super.equals(frame);
   }

   public boolean equals(TLSSyslogFrame frame) {
      return this.isLengthEquals(frame) && this.isMessageEquals(frame);
   }

   private boolean isLengthEquals(TLSSyslogFrame frame) {
      return this.messageLengthInBytes == frame.messageLengthInBytes;
   }

   private boolean isMessageEquals(TLSSyslogFrame frame) {
      return this.message.equals(frame.message);
   }
}
