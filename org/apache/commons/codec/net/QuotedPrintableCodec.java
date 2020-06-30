package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.net.Utils;

public class QuotedPrintableCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder {
   private final Charset charset;
   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
   private static final byte ESCAPE_CHAR = 61;
   private static final byte TAB = 9;
   private static final byte SPACE = 32;

   public QuotedPrintableCodec() {
      this(Charsets.UTF_8);
   }

   public QuotedPrintableCodec(Charset charset) {
      this.charset = charset;
   }

   public QuotedPrintableCodec(String charsetName) throws IllegalCharsetNameException, IllegalArgumentException, UnsupportedCharsetException {
      this(Charset.forName(charsetName));
   }

   private static final void encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
      buffer.write(61);
      char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 15, 16));
      char hex2 = Character.toUpperCase(Character.forDigit(b & 15, 16));
      buffer.write(hex1);
      buffer.write(hex2);
   }

   public static final byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes) {
      if(bytes == null) {
         return null;
      } else {
         if(printable == null) {
            printable = PRINTABLE_CHARS;
         }

         ByteArrayOutputStream buffer = new ByteArrayOutputStream();

         for(byte c : bytes) {
            int b = c;
            if(c < 0) {
               b = 256 + c;
            }

            if(printable.get(b)) {
               buffer.write(b);
            } else {
               encodeQuotedPrintable(b, buffer);
            }
         }

         return buffer.toByteArray();
      }
   }

   public static final byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException {
      if(bytes == null) {
         return null;
      } else {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();

         for(int i = 0; i < bytes.length; ++i) {
            int b = bytes[i];
            if(b == 61) {
               try {
                  ++i;
                  int u = Utils.digit16(bytes[i]);
                  ++i;
                  int l = Utils.digit16(bytes[i]);
                  buffer.write((char)((u << 4) + l));
               } catch (ArrayIndexOutOfBoundsException var6) {
                  throw new DecoderException("Invalid quoted-printable encoding", var6);
               }
            } else {
               buffer.write(b);
            }
         }

         return buffer.toByteArray();
      }
   }

   public byte[] encode(byte[] bytes) {
      return encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
   }

   public byte[] decode(byte[] bytes) throws DecoderException {
      return decodeQuotedPrintable(bytes);
   }

   public String encode(String str) throws EncoderException {
      return this.encode(str, this.getCharset());
   }

   public String decode(String str, Charset charset) throws DecoderException {
      return str == null?null:new String(this.decode(StringUtils.getBytesUsAscii(str)), charset);
   }

   public String decode(String str, String charset) throws DecoderException, UnsupportedEncodingException {
      return str == null?null:new String(this.decode(StringUtils.getBytesUsAscii(str)), charset);
   }

   public String decode(String str) throws DecoderException {
      return this.decode(str, this.getCharset());
   }

   public Object encode(Object obj) throws EncoderException {
      if(obj == null) {
         return null;
      } else if(obj instanceof byte[]) {
         return this.encode((byte[])((byte[])obj));
      } else if(obj instanceof String) {
         return this.encode((String)obj);
      } else {
         throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable encoded");
      }
   }

   public Object decode(Object obj) throws DecoderException {
      if(obj == null) {
         return null;
      } else if(obj instanceof byte[]) {
         return this.decode((byte[])((byte[])obj));
      } else if(obj instanceof String) {
         return this.decode((String)obj);
      } else {
         throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be quoted-printable decoded");
      }
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getDefaultCharset() {
      return this.charset.name();
   }

   public String encode(String str, Charset charset) {
      return str == null?null:StringUtils.newStringUsAscii(this.encode(str.getBytes(charset)));
   }

   public String encode(String str, String charset) throws UnsupportedEncodingException {
      return str == null?null:StringUtils.newStringUsAscii(this.encode(str.getBytes(charset)));
   }

   static {
      for(int i = 33; i <= 60; ++i) {
         PRINTABLE_CHARS.set(i);
      }

      for(int i = 62; i <= 126; ++i) {
         PRINTABLE_CHARS.set(i);
      }

      PRINTABLE_CHARS.set(9);
      PRINTABLE_CHARS.set(32);
   }
}
