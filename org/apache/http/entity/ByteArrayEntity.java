package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

@NotThreadSafe
public class ByteArrayEntity extends AbstractHttpEntity implements Cloneable {
   /** @deprecated */
   @Deprecated
   protected final byte[] content;
   private final byte[] b;
   private final int off;
   private final int len;

   public ByteArrayEntity(byte[] b, ContentType contentType) {
      Args.notNull(b, "Source byte array");
      this.content = b;
      this.b = b;
      this.off = 0;
      this.len = this.b.length;
      if(contentType != null) {
         this.setContentType(contentType.toString());
      }

   }

   public ByteArrayEntity(byte[] b, int off, int len, ContentType contentType) {
      Args.notNull(b, "Source byte array");
      if(off >= 0 && off <= b.length && len >= 0 && off + len >= 0 && off + len <= b.length) {
         this.content = b;
         this.b = b;
         this.off = off;
         this.len = len;
         if(contentType != null) {
            this.setContentType(contentType.toString());
         }

      } else {
         throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
      }
   }

   public ByteArrayEntity(byte[] b) {
      this(b, (ContentType)null);
   }

   public ByteArrayEntity(byte[] b, int off, int len) {
      this(b, off, len, (ContentType)null);
   }

   public boolean isRepeatable() {
      return true;
   }

   public long getContentLength() {
      return (long)this.len;
   }

   public InputStream getContent() {
      return new ByteArrayInputStream(this.b, this.off, this.len);
   }

   public void writeTo(OutputStream outstream) throws IOException {
      Args.notNull(outstream, "Output stream");
      outstream.write(this.b, this.off, this.len);
      outstream.flush();
   }

   public boolean isStreaming() {
      return false;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
