package org.apache.http.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

@NotThreadSafe
public class FileEntity extends AbstractHttpEntity implements Cloneable {
   protected final File file;

   /** @deprecated */
   @Deprecated
   public FileEntity(File file, String contentType) {
      this.file = (File)Args.notNull(file, "File");
      this.setContentType(contentType);
   }

   public FileEntity(File file, ContentType contentType) {
      this.file = (File)Args.notNull(file, "File");
      if(contentType != null) {
         this.setContentType(contentType.toString());
      }

   }

   public FileEntity(File file) {
      this.file = (File)Args.notNull(file, "File");
   }

   public boolean isRepeatable() {
      return true;
   }

   public long getContentLength() {
      return this.file.length();
   }

   public InputStream getContent() throws IOException {
      return new FileInputStream(this.file);
   }

   public void writeTo(OutputStream outstream) throws IOException {
      Args.notNull(outstream, "Output stream");
      InputStream instream = new FileInputStream(this.file);

      try {
         byte[] tmp = new byte[4096];

         int l;
         while((l = ((InputStream)instream).read(tmp)) != -1) {
            outstream.write(tmp, 0, l);
         }

         outstream.flush();
      } finally {
         instream.close();
      }

   }

   public boolean isStreaming() {
      return false;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
