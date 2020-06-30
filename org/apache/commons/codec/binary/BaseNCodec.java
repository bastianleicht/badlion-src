package org.apache.commons.codec.binary;

import java.util.Arrays;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.StringUtils;

public abstract class BaseNCodec implements BinaryEncoder, BinaryDecoder {
   static final int EOF = -1;
   public static final int MIME_CHUNK_SIZE = 76;
   public static final int PEM_CHUNK_SIZE = 64;
   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   protected static final int MASK_8BITS = 255;
   protected static final byte PAD_DEFAULT = 61;
   protected final byte PAD = 61;
   private final int unencodedBlockSize;
   private final int encodedBlockSize;
   protected final int lineLength;
   private final int chunkSeparatorLength;

   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
      this.unencodedBlockSize = unencodedBlockSize;
      this.encodedBlockSize = encodedBlockSize;
      boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
      this.lineLength = useChunking?lineLength / encodedBlockSize * encodedBlockSize:0;
      this.chunkSeparatorLength = chunkSeparatorLength;
   }

   boolean hasData(BaseNCodec.Context context) {
      return context.buffer != null;
   }

   int available(BaseNCodec.Context context) {
      return context.buffer != null?context.pos - context.readPos:0;
   }

   protected int getDefaultBufferSize() {
      return 8192;
   }

   private byte[] resizeBuffer(BaseNCodec.Context context) {
      if(context.buffer == null) {
         context.buffer = new byte[this.getDefaultBufferSize()];
         context.pos = 0;
         context.readPos = 0;
      } else {
         byte[] b = new byte[context.buffer.length * 2];
         System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
         context.buffer = b;
      }

      return context.buffer;
   }

   protected byte[] ensureBufferSize(int size, BaseNCodec.Context context) {
      return context.buffer != null && context.buffer.length >= context.pos + size?context.buffer:this.resizeBuffer(context);
   }

   int readResults(byte[] b, int bPos, int bAvail, BaseNCodec.Context context) {
      if(context.buffer != null) {
         int len = Math.min(this.available(context), bAvail);
         System.arraycopy(context.buffer, context.readPos, b, bPos, len);
         context.readPos += len;
         if(context.readPos >= context.pos) {
            context.buffer = null;
         }

         return len;
      } else {
         return context.eof?-1:0;
      }
   }

   protected static boolean isWhiteSpace(byte byteToCheck) {
      switch(byteToCheck) {
      case 9:
      case 10:
      case 13:
      case 32:
         return true;
      default:
         return false;
      }
   }

   public Object encode(Object obj) throws EncoderException {
      if(!(obj instanceof byte[])) {
         throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
      } else {
         return this.encode((byte[])((byte[])obj));
      }
   }

   public String encodeToString(byte[] pArray) {
      return StringUtils.newStringUtf8(this.encode(pArray));
   }

   public String encodeAsString(byte[] pArray) {
      return StringUtils.newStringUtf8(this.encode(pArray));
   }

   public Object decode(Object obj) throws DecoderException {
      if(obj instanceof byte[]) {
         return this.decode((byte[])((byte[])obj));
      } else if(obj instanceof String) {
         return this.decode((String)obj);
      } else {
         throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
      }
   }

   public byte[] decode(String pArray) {
      return this.decode(StringUtils.getBytesUtf8(pArray));
   }

   public byte[] decode(byte[] pArray) {
      if(pArray != null && pArray.length != 0) {
         BaseNCodec.Context context = new BaseNCodec.Context();
         this.decode(pArray, 0, pArray.length, context);
         this.decode(pArray, 0, -1, context);
         byte[] result = new byte[context.pos];
         this.readResults(result, 0, result.length, context);
         return result;
      } else {
         return pArray;
      }
   }

   public byte[] encode(byte[] pArray) {
      if(pArray != null && pArray.length != 0) {
         BaseNCodec.Context context = new BaseNCodec.Context();
         this.encode(pArray, 0, pArray.length, context);
         this.encode(pArray, 0, -1, context);
         byte[] buf = new byte[context.pos - context.readPos];
         this.readResults(buf, 0, buf.length, context);
         return buf;
      } else {
         return pArray;
      }
   }

   abstract void encode(byte[] var1, int var2, int var3, BaseNCodec.Context var4);

   abstract void decode(byte[] var1, int var2, int var3, BaseNCodec.Context var4);

   protected abstract boolean isInAlphabet(byte var1);

   public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
      for(int i = 0; i < arrayOctet.length; ++i) {
         if(!this.isInAlphabet(arrayOctet[i]) && (!allowWSPad || arrayOctet[i] != 61 && !isWhiteSpace(arrayOctet[i]))) {
            return false;
         }
      }

      return true;
   }

   public boolean isInAlphabet(String basen) {
      return this.isInAlphabet(StringUtils.getBytesUtf8(basen), true);
   }

   protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
      if(arrayOctet == null) {
         return false;
      } else {
         for(byte element : arrayOctet) {
            if(61 == element || this.isInAlphabet(element)) {
               return true;
            }
         }

         return false;
      }
   }

   public long getEncodedLength(byte[] pArray) {
      long len = (long)((pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize) * (long)this.encodedBlockSize;
      if(this.lineLength > 0) {
         len += (len + (long)this.lineLength - 1L) / (long)this.lineLength * (long)this.chunkSeparatorLength;
      }

      return len;
   }

   static class Context {
      int ibitWorkArea;
      long lbitWorkArea;
      byte[] buffer;
      int pos;
      int readPos;
      boolean eof;
      int currentLinePos;
      int modulus;

      public String toString() {
         return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", new Object[]{this.getClass().getSimpleName(), Arrays.toString(this.buffer), Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos)});
      }
   }
}
