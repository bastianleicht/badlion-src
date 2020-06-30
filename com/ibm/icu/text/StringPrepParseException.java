package com.ibm.icu.text;

import java.text.ParseException;

public class StringPrepParseException extends ParseException {
   static final long serialVersionUID = 7160264827701651255L;
   public static final int INVALID_CHAR_FOUND = 0;
   public static final int ILLEGAL_CHAR_FOUND = 1;
   public static final int PROHIBITED_ERROR = 2;
   public static final int UNASSIGNED_ERROR = 3;
   public static final int CHECK_BIDI_ERROR = 4;
   public static final int STD3_ASCII_RULES_ERROR = 5;
   public static final int ACE_PREFIX_ERROR = 6;
   public static final int VERIFICATION_ERROR = 7;
   public static final int LABEL_TOO_LONG_ERROR = 8;
   public static final int BUFFER_OVERFLOW_ERROR = 9;
   public static final int ZERO_LENGTH_LABEL = 10;
   public static final int DOMAIN_NAME_TOO_LONG_ERROR = 11;
   private int error;
   private int line;
   private StringBuffer preContext = new StringBuffer();
   private StringBuffer postContext = new StringBuffer();
   private static final int PARSE_CONTEXT_LEN = 16;

   public StringPrepParseException(String message, int error) {
      super(message, -1);
      this.error = error;
      this.line = 0;
   }

   public StringPrepParseException(String message, int error, String rules, int pos) {
      super(message, -1);
      this.error = error;
      this.setContext(rules, pos);
      this.line = 0;
   }

   public StringPrepParseException(String message, int error, String rules, int pos, int lineNumber) {
      super(message, -1);
      this.error = error;
      this.setContext(rules, pos);
      this.line = lineNumber;
   }

   public boolean equals(Object other) {
      return !(other instanceof StringPrepParseException)?false:((StringPrepParseException)other).error == this.error;
   }

   /** @deprecated */
   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(super.getMessage());
      buf.append(". line:  ");
      buf.append(this.line);
      buf.append(". preContext:  ");
      buf.append(this.preContext);
      buf.append(". postContext: ");
      buf.append(this.postContext);
      buf.append("\n");
      return buf.toString();
   }

   private void setPreContext(String str, int pos) {
      this.setPreContext(str.toCharArray(), pos);
   }

   private void setPreContext(char[] str, int pos) {
      int start = pos <= 16?0:pos - 15;
      int len = start <= 16?start:16;
      this.preContext.append(str, start, len);
   }

   private void setPostContext(String str, int pos) {
      this.setPostContext(str.toCharArray(), pos);
   }

   private void setPostContext(char[] str, int pos) {
      int len = str.length - pos;
      this.postContext.append(str, pos, len);
   }

   private void setContext(String str, int pos) {
      this.setPreContext(str, pos);
      this.setPostContext(str, pos);
   }

   public int getError() {
      return this.error;
   }
}
