package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.StringUtils;

abstract class RFC1522Codec {
   protected static final char SEP = '?';
   protected static final String POSTFIX = "?=";
   protected static final String PREFIX = "=?";

   protected String encodeText(String text, Charset charset) throws EncoderException {
      if(text == null) {
         return null;
      } else {
         StringBuilder buffer = new StringBuilder();
         buffer.append("=?");
         buffer.append(charset);
         buffer.append('?');
         buffer.append(this.getEncoding());
         buffer.append('?');
         byte[] rawData = this.doEncoding(text.getBytes(charset));
         buffer.append(StringUtils.newStringUsAscii(rawData));
         buffer.append("?=");
         return buffer.toString();
      }
   }

   protected String encodeText(String text, String charsetName) throws EncoderException, UnsupportedEncodingException {
      return text == null?null:this.encodeText(text, Charset.forName(charsetName));
   }

   protected String decodeText(String text) throws DecoderException, UnsupportedEncodingException {
      if(text == null) {
         return null;
      } else if(text.startsWith("=?") && text.endsWith("?=")) {
         int terminator = text.length() - 2;
         int from = 2;
         int to = text.indexOf(63, from);
         if(to == terminator) {
            throw new DecoderException("RFC 1522 violation: charset token not found");
         } else {
            String charset = text.substring(from, to);
            if(charset.equals("")) {
               throw new DecoderException("RFC 1522 violation: charset not specified");
            } else {
               from = to + 1;
               to = text.indexOf(63, from);
               if(to == terminator) {
                  throw new DecoderException("RFC 1522 violation: encoding token not found");
               } else {
                  String encoding = text.substring(from, to);
                  if(!this.getEncoding().equalsIgnoreCase(encoding)) {
                     throw new DecoderException("This codec cannot decode " + encoding + " encoded content");
                  } else {
                     from = to + 1;
                     to = text.indexOf(63, from);
                     byte[] data = StringUtils.getBytesUsAscii(text.substring(from, to));
                     data = this.doDecoding(data);
                     return new String(data, charset);
                  }
               }
            }
         }
      } else {
         throw new DecoderException("RFC 1522 violation: malformed encoded content");
      }
   }

   protected abstract String getEncoding();

   protected abstract byte[] doEncoding(byte[] var1) throws EncoderException;

   protected abstract byte[] doDecoding(byte[] var1) throws DecoderException;
}
