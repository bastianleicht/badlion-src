package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.RFC1522Codec;

public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder {
   private final Charset charset;

   public BCodec() {
      this(Charsets.UTF_8);
   }

   public BCodec(Charset charset) {
      this.charset = charset;
   }

   public BCodec(String charsetName) {
      this(Charset.forName(charsetName));
   }

   protected String getEncoding() {
      return "B";
   }

   protected byte[] doEncoding(byte[] bytes) {
      return bytes == null?null:Base64.encodeBase64(bytes);
   }

   protected byte[] doDecoding(byte[] bytes) {
      return bytes == null?null:Base64.decodeBase64(bytes);
   }

   public String encode(String value, Charset charset) throws EncoderException {
      return value == null?null:this.encodeText(value, charset);
   }

   public String encode(String value, String charset) throws EncoderException {
      if(value == null) {
         return null;
      } else {
         try {
            return this.encodeText(value, charset);
         } catch (UnsupportedEncodingException var4) {
            throw new EncoderException(var4.getMessage(), var4);
         }
      }
   }

   public String encode(String value) throws EncoderException {
      return value == null?null:this.encode(value, this.getCharset());
   }

   public String decode(String value) throws DecoderException {
      if(value == null) {
         return null;
      } else {
         try {
            return this.decodeText(value);
         } catch (UnsupportedEncodingException var3) {
            throw new DecoderException(var3.getMessage(), var3);
         }
      }
   }

   public Object encode(Object value) throws EncoderException {
      if(value == null) {
         return null;
      } else if(value instanceof String) {
         return this.encode((String)value);
      } else {
         throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
      }
   }

   public Object decode(Object value) throws DecoderException {
      if(value == null) {
         return null;
      } else if(value instanceof String) {
         return this.decode((String)value);
      } else {
         throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
      }
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getDefaultCharset() {
      return this.charset.name();
   }
}
