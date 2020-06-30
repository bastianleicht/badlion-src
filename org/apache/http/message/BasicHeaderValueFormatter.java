package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.HeaderValueFormatter;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
public class BasicHeaderValueFormatter implements HeaderValueFormatter {
   /** @deprecated */
   @Deprecated
   public static final BasicHeaderValueFormatter DEFAULT = new BasicHeaderValueFormatter();
   public static final BasicHeaderValueFormatter INSTANCE = new BasicHeaderValueFormatter();
   public static final String SEPARATORS = " ;,:@()<>\\\"/[]?={}\t";
   public static final String UNSAFE_CHARS = "\"\\";

   public static String formatElements(HeaderElement[] elems, boolean quote, HeaderValueFormatter formatter) {
      return ((HeaderValueFormatter)(formatter != null?formatter:INSTANCE)).formatElements((CharArrayBuffer)null, elems, quote).toString();
   }

   public CharArrayBuffer formatElements(CharArrayBuffer charBuffer, HeaderElement[] elems, boolean quote) {
      Args.notNull(elems, "Header element array");
      int len = this.estimateElementsLen(elems);
      CharArrayBuffer buffer = charBuffer;
      if(charBuffer == null) {
         buffer = new CharArrayBuffer(len);
      } else {
         charBuffer.ensureCapacity(len);
      }

      for(int i = 0; i < elems.length; ++i) {
         if(i > 0) {
            buffer.append(", ");
         }

         this.formatHeaderElement(buffer, elems[i], quote);
      }

      return buffer;
   }

   protected int estimateElementsLen(HeaderElement[] elems) {
      if(elems != null && elems.length >= 1) {
         int result = (elems.length - 1) * 2;

         for(HeaderElement elem : elems) {
            result += this.estimateHeaderElementLen(elem);
         }

         return result;
      } else {
         return 0;
      }
   }

   public static String formatHeaderElement(HeaderElement elem, boolean quote, HeaderValueFormatter formatter) {
      return ((HeaderValueFormatter)(formatter != null?formatter:INSTANCE)).formatHeaderElement((CharArrayBuffer)null, elem, quote).toString();
   }

   public CharArrayBuffer formatHeaderElement(CharArrayBuffer charBuffer, HeaderElement elem, boolean quote) {
      Args.notNull(elem, "Header element");
      int len = this.estimateHeaderElementLen(elem);
      CharArrayBuffer buffer = charBuffer;
      if(charBuffer == null) {
         buffer = new CharArrayBuffer(len);
      } else {
         charBuffer.ensureCapacity(len);
      }

      buffer.append(elem.getName());
      String value = elem.getValue();
      if(value != null) {
         buffer.append('=');
         this.doFormatValue(buffer, value, quote);
      }

      int parcnt = elem.getParameterCount();
      if(parcnt > 0) {
         for(int i = 0; i < parcnt; ++i) {
            buffer.append("; ");
            this.formatNameValuePair(buffer, elem.getParameter(i), quote);
         }
      }

      return buffer;
   }

   protected int estimateHeaderElementLen(HeaderElement elem) {
      if(elem == null) {
         return 0;
      } else {
         int result = elem.getName().length();
         String value = elem.getValue();
         if(value != null) {
            result += 3 + value.length();
         }

         int parcnt = elem.getParameterCount();
         if(parcnt > 0) {
            for(int i = 0; i < parcnt; ++i) {
               result += 2 + this.estimateNameValuePairLen(elem.getParameter(i));
            }
         }

         return result;
      }
   }

   public static String formatParameters(NameValuePair[] nvps, boolean quote, HeaderValueFormatter formatter) {
      return ((HeaderValueFormatter)(formatter != null?formatter:INSTANCE)).formatParameters((CharArrayBuffer)null, nvps, quote).toString();
   }

   public CharArrayBuffer formatParameters(CharArrayBuffer charBuffer, NameValuePair[] nvps, boolean quote) {
      Args.notNull(nvps, "Header parameter array");
      int len = this.estimateParametersLen(nvps);
      CharArrayBuffer buffer = charBuffer;
      if(charBuffer == null) {
         buffer = new CharArrayBuffer(len);
      } else {
         charBuffer.ensureCapacity(len);
      }

      for(int i = 0; i < nvps.length; ++i) {
         if(i > 0) {
            buffer.append("; ");
         }

         this.formatNameValuePair(buffer, nvps[i], quote);
      }

      return buffer;
   }

   protected int estimateParametersLen(NameValuePair[] nvps) {
      if(nvps != null && nvps.length >= 1) {
         int result = (nvps.length - 1) * 2;

         for(NameValuePair nvp : nvps) {
            result += this.estimateNameValuePairLen(nvp);
         }

         return result;
      } else {
         return 0;
      }
   }

   public static String formatNameValuePair(NameValuePair nvp, boolean quote, HeaderValueFormatter formatter) {
      return ((HeaderValueFormatter)(formatter != null?formatter:INSTANCE)).formatNameValuePair((CharArrayBuffer)null, nvp, quote).toString();
   }

   public CharArrayBuffer formatNameValuePair(CharArrayBuffer charBuffer, NameValuePair nvp, boolean quote) {
      Args.notNull(nvp, "Name / value pair");
      int len = this.estimateNameValuePairLen(nvp);
      CharArrayBuffer buffer = charBuffer;
      if(charBuffer == null) {
         buffer = new CharArrayBuffer(len);
      } else {
         charBuffer.ensureCapacity(len);
      }

      buffer.append(nvp.getName());
      String value = nvp.getValue();
      if(value != null) {
         buffer.append('=');
         this.doFormatValue(buffer, value, quote);
      }

      return buffer;
   }

   protected int estimateNameValuePairLen(NameValuePair nvp) {
      if(nvp == null) {
         return 0;
      } else {
         int result = nvp.getName().length();
         String value = nvp.getValue();
         if(value != null) {
            result += 3 + value.length();
         }

         return result;
      }
   }

   protected void doFormatValue(CharArrayBuffer buffer, String value, boolean quote) {
      boolean quoteFlag = quote;
      if(!quote) {
         for(int i = 0; i < value.length() && !quoteFlag; ++i) {
            quoteFlag = this.isSeparator(value.charAt(i));
         }
      }

      if(quoteFlag) {
         buffer.append('\"');
      }

      for(int i = 0; i < value.length(); ++i) {
         char ch = value.charAt(i);
         if(this.isUnsafe(ch)) {
            buffer.append('\\');
         }

         buffer.append(ch);
      }

      if(quoteFlag) {
         buffer.append('\"');
      }

   }

   protected boolean isSeparator(char ch) {
      return " ;,:@()<>\\\"/[]?={}\t".indexOf(ch) >= 0;
   }

   protected boolean isUnsafe(char ch) {
      return "\"\\".indexOf(ch) >= 0;
   }
}
