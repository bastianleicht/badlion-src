package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

class NioZipEncoding implements ZipEncoding {
   private final Charset charset;

   public NioZipEncoding(Charset charset) {
      this.charset = charset;
   }

   public boolean canEncode(String name) {
      CharsetEncoder enc = this.charset.newEncoder();
      enc.onMalformedInput(CodingErrorAction.REPORT);
      enc.onUnmappableCharacter(CodingErrorAction.REPORT);
      return enc.canEncode(name);
   }

   public ByteBuffer encode(String name) {
      CharsetEncoder enc = this.charset.newEncoder();
      enc.onMalformedInput(CodingErrorAction.REPORT);
      enc.onUnmappableCharacter(CodingErrorAction.REPORT);
      CharBuffer cb = CharBuffer.wrap(name);
      ByteBuffer out = ByteBuffer.allocate(name.length() + (name.length() + 1) / 2);

      while(cb.remaining() > 0) {
         CoderResult res = enc.encode(cb, out, true);
         if(!res.isUnmappable() && !res.isMalformed()) {
            if(res.isOverflow()) {
               out = ZipEncodingHelper.growBuffer(out, 0);
            } else if(res.isUnderflow()) {
               enc.flush(out);
               break;
            }
         } else {
            if(res.length() * 6 > out.remaining()) {
               out = ZipEncodingHelper.growBuffer(out, out.position() + res.length() * 6);
            }

            for(int i = 0; i < res.length(); ++i) {
               ZipEncodingHelper.appendSurrogate(out, cb.get());
            }
         }
      }

      out.limit(out.position());
      out.rewind();
      return out;
   }

   public String decode(byte[] data) throws IOException {
      return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();
   }
}
