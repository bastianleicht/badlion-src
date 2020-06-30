package org.apache.logging.log4j.core.pattern;

public final class FormattingInfo {
   private static final char[] SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
   private static final FormattingInfo DEFAULT = new FormattingInfo(false, 0, Integer.MAX_VALUE);
   private final int minLength;
   private final int maxLength;
   private final boolean leftAlign;

   public FormattingInfo(boolean leftAlign, int minLength, int maxLength) {
      this.leftAlign = leftAlign;
      this.minLength = minLength;
      this.maxLength = maxLength;
   }

   public static FormattingInfo getDefault() {
      return DEFAULT;
   }

   public boolean isLeftAligned() {
      return this.leftAlign;
   }

   public int getMinLength() {
      return this.minLength;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void format(int fieldStart, StringBuilder buffer) {
      int rawLength = buffer.length() - fieldStart;
      if(rawLength > this.maxLength) {
         buffer.delete(fieldStart, buffer.length() - this.maxLength);
      } else if(rawLength < this.minLength) {
         if(this.leftAlign) {
            int fieldEnd = buffer.length();
            buffer.setLength(fieldStart + this.minLength);

            for(int i = fieldEnd; i < buffer.length(); ++i) {
               buffer.setCharAt(i, ' ');
            }
         } else {
            int padLength;
            for(padLength = this.minLength - rawLength; padLength > SPACES.length; padLength -= SPACES.length) {
               buffer.insert(fieldStart, SPACES);
            }

            buffer.insert(fieldStart, SPACES, 0, padLength);
         }
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.toString());
      sb.append("[leftAlign=");
      sb.append(this.leftAlign);
      sb.append(", maxLength=");
      sb.append(this.maxLength);
      sb.append(", minLength=");
      sb.append(this.minLength);
      sb.append("]");
      return sb.toString();
   }
}
