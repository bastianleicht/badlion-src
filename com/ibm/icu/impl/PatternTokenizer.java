package com.ibm.icu.impl;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;

public class PatternTokenizer {
   private UnicodeSet ignorableCharacters = new UnicodeSet();
   private UnicodeSet syntaxCharacters = new UnicodeSet();
   private UnicodeSet extraQuotingCharacters = new UnicodeSet();
   private UnicodeSet escapeCharacters = new UnicodeSet();
   private boolean usingSlash = false;
   private boolean usingQuote = false;
   private transient UnicodeSet needingQuoteCharacters = null;
   private int start;
   private int limit;
   private String pattern;
   public static final char SINGLE_QUOTE = '\'';
   public static final char BACK_SLASH = '\\';
   private static int NO_QUOTE = -1;
   private static int IN_QUOTE = -2;
   public static final int DONE = 0;
   public static final int SYNTAX = 1;
   public static final int LITERAL = 2;
   public static final int BROKEN_QUOTE = 3;
   public static final int BROKEN_ESCAPE = 4;
   public static final int UNKNOWN = 5;
   private static final int AFTER_QUOTE = -1;
   private static final int NONE = 0;
   private static final int START_QUOTE = 1;
   private static final int NORMAL_QUOTE = 2;
   private static final int SLASH_START = 3;
   private static final int HEX = 4;

   public UnicodeSet getIgnorableCharacters() {
      return (UnicodeSet)this.ignorableCharacters.clone();
   }

   public PatternTokenizer setIgnorableCharacters(UnicodeSet ignorableCharacters) {
      this.ignorableCharacters = (UnicodeSet)ignorableCharacters.clone();
      this.needingQuoteCharacters = null;
      return this;
   }

   public UnicodeSet getSyntaxCharacters() {
      return (UnicodeSet)this.syntaxCharacters.clone();
   }

   public UnicodeSet getExtraQuotingCharacters() {
      return (UnicodeSet)this.extraQuotingCharacters.clone();
   }

   public PatternTokenizer setSyntaxCharacters(UnicodeSet syntaxCharacters) {
      this.syntaxCharacters = (UnicodeSet)syntaxCharacters.clone();
      this.needingQuoteCharacters = null;
      return this;
   }

   public PatternTokenizer setExtraQuotingCharacters(UnicodeSet syntaxCharacters) {
      this.extraQuotingCharacters = (UnicodeSet)syntaxCharacters.clone();
      this.needingQuoteCharacters = null;
      return this;
   }

   public UnicodeSet getEscapeCharacters() {
      return (UnicodeSet)this.escapeCharacters.clone();
   }

   public PatternTokenizer setEscapeCharacters(UnicodeSet escapeCharacters) {
      this.escapeCharacters = (UnicodeSet)escapeCharacters.clone();
      return this;
   }

   public boolean isUsingQuote() {
      return this.usingQuote;
   }

   public PatternTokenizer setUsingQuote(boolean usingQuote) {
      this.usingQuote = usingQuote;
      this.needingQuoteCharacters = null;
      return this;
   }

   public boolean isUsingSlash() {
      return this.usingSlash;
   }

   public PatternTokenizer setUsingSlash(boolean usingSlash) {
      this.usingSlash = usingSlash;
      this.needingQuoteCharacters = null;
      return this;
   }

   public int getLimit() {
      return this.limit;
   }

   public PatternTokenizer setLimit(int limit) {
      this.limit = limit;
      return this;
   }

   public int getStart() {
      return this.start;
   }

   public PatternTokenizer setStart(int start) {
      this.start = start;
      return this;
   }

   public PatternTokenizer setPattern(CharSequence pattern) {
      return this.setPattern(pattern.toString());
   }

   public PatternTokenizer setPattern(String pattern) {
      if(pattern == null) {
         throw new IllegalArgumentException("Inconsistent arguments");
      } else {
         this.start = 0;
         this.limit = pattern.length();
         this.pattern = pattern;
         return this;
      }
   }

   public String quoteLiteral(CharSequence string) {
      return this.quoteLiteral(string.toString());
   }

   public String quoteLiteral(String string) {
      if(this.needingQuoteCharacters == null) {
         this.needingQuoteCharacters = (new UnicodeSet()).addAll(this.syntaxCharacters).addAll(this.ignorableCharacters).addAll(this.extraQuotingCharacters);
         if(this.usingSlash) {
            this.needingQuoteCharacters.add(92);
         }

         if(this.usingQuote) {
            this.needingQuoteCharacters.add(39);
         }
      }

      StringBuffer result = new StringBuffer();
      int quotedChar = NO_QUOTE;

      int cp;
      for(int i = 0; i < string.length(); i += UTF16.getCharCount(cp)) {
         cp = UTF16.charAt(string, i);
         if(this.escapeCharacters.contains(cp)) {
            if(quotedChar == IN_QUOTE) {
               result.append('\'');
               quotedChar = NO_QUOTE;
            }

            this.appendEscaped(result, cp);
         } else if(this.needingQuoteCharacters.contains(cp)) {
            if(quotedChar == IN_QUOTE) {
               UTF16.append(result, cp);
               if(this.usingQuote && cp == 39) {
                  result.append('\'');
               }
            } else if(this.usingSlash) {
               result.append('\\');
               UTF16.append(result, cp);
            } else if(this.usingQuote) {
               if(cp == 39) {
                  result.append('\'');
                  result.append('\'');
               } else {
                  result.append('\'');
                  UTF16.append(result, cp);
                  quotedChar = IN_QUOTE;
               }
            } else {
               this.appendEscaped(result, cp);
            }
         } else {
            if(quotedChar == IN_QUOTE) {
               result.append('\'');
               quotedChar = NO_QUOTE;
            }

            UTF16.append(result, cp);
         }
      }

      if(quotedChar == IN_QUOTE) {
         result.append('\'');
      }

      return result.toString();
   }

   private void appendEscaped(StringBuffer result, int cp) {
      if(cp <= '\uffff') {
         result.append("\\u").append(Utility.hex((long)cp, 4));
      } else {
         result.append("\\U").append(Utility.hex((long)cp, 8));
      }

   }

   public String normalize() {
      int oldStart = this.start;
      StringBuffer result = new StringBuffer();
      StringBuffer buffer = new StringBuffer();

      while(true) {
         buffer.setLength(0);
         int status = this.next(buffer);
         if(status == 0) {
            this.start = oldStart;
            return result.toString();
         }

         if(status != 1) {
            result.append(this.quoteLiteral((CharSequence)buffer));
         } else {
            result.append(buffer);
         }
      }
   }

   public int next(StringBuffer buffer) {
      if(this.start >= this.limit) {
         return 0;
      } else {
         int status = 5;
         int lastQuote = 5;
         int quoteStatus = 0;
         int hexCount = 0;
         int hexValue = 0;

         int cp;
         for(int i = this.start; i < this.limit; i += UTF16.getCharCount(cp)) {
            cp = UTF16.charAt(this.pattern, i);
            label0:
            switch(quoteStatus) {
            case -1:
               if(cp == lastQuote) {
                  UTF16.append(buffer, cp);
                  quoteStatus = 2;
                  continue;
               }

               quoteStatus = 0;
            case 0:
            default:
               break;
            case 1:
               if(cp == lastQuote) {
                  UTF16.append(buffer, cp);
                  quoteStatus = 0;
               } else {
                  UTF16.append(buffer, cp);
                  quoteStatus = 2;
               }
               continue;
            case 2:
               if(cp == lastQuote) {
                  quoteStatus = -1;
               } else {
                  UTF16.append(buffer, cp);
               }
               continue;
            case 3:
               switch(cp) {
               case 85:
                  quoteStatus = 4;
                  hexCount = 8;
                  hexValue = 0;
                  continue;
               case 117:
                  quoteStatus = 4;
                  hexCount = 4;
                  hexValue = 0;
                  continue;
               default:
                  if(this.usingSlash) {
                     UTF16.append(buffer, cp);
                     quoteStatus = 0;
                     continue;
                  }

                  buffer.append('\\');
                  quoteStatus = 0;
                  break label0;
               }
            case 4:
               hexValue = hexValue << 4;
               hexValue = hexValue + cp;
               switch(cp) {
               case 48:
               case 49:
               case 50:
               case 51:
               case 52:
               case 53:
               case 54:
               case 55:
               case 56:
               case 57:
                  hexValue = hexValue - 48;
                  break;
               case 58:
               case 59:
               case 60:
               case 61:
               case 62:
               case 63:
               case 64:
               case 71:
               case 72:
               case 73:
               case 74:
               case 75:
               case 76:
               case 77:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 83:
               case 84:
               case 85:
               case 86:
               case 87:
               case 88:
               case 89:
               case 90:
               case 91:
               case 92:
               case 93:
               case 94:
               case 95:
               case 96:
               default:
                  this.start = i;
                  return 4;
               case 65:
               case 66:
               case 67:
               case 68:
               case 69:
               case 70:
                  hexValue = hexValue - 55;
                  break;
               case 97:
               case 98:
               case 99:
               case 100:
               case 101:
               case 102:
                  hexValue = hexValue - 87;
               }

               --hexCount;
               if(hexCount == 0) {
                  quoteStatus = 0;
                  UTF16.append(buffer, hexValue);
               }
               continue;
            }

            if(!this.ignorableCharacters.contains(cp)) {
               if(this.syntaxCharacters.contains(cp)) {
                  if(status == 5) {
                     UTF16.append(buffer, cp);
                     this.start = i + UTF16.getCharCount(cp);
                     return 1;
                  }

                  this.start = i;
                  return status;
               }

               status = 2;
               if(cp == 92) {
                  quoteStatus = 3;
               } else if(this.usingQuote && cp == 39) {
                  lastQuote = cp;
                  quoteStatus = 1;
               } else {
                  UTF16.append(buffer, cp);
               }
            }
         }

         this.start = this.limit;
         switch(quoteStatus) {
         case 1:
         case 2:
            status = 3;
            break;
         case 3:
            if(this.usingSlash) {
               status = 4;
            } else {
               buffer.append('\\');
            }
            break;
         case 4:
            status = 4;
         }

         return status;
      }
   }
}
