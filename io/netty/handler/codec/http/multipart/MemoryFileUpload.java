package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.multipart.AbstractMemoryHttpData;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryFileUpload extends AbstractMemoryHttpData implements FileUpload {
   private String filename;
   private String contentType;
   private String contentTransferEncoding;

   public MemoryFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
      super(name, charset, size);
      this.setFilename(filename);
      this.setContentType(contentType);
      this.setContentTransferEncoding(contentTransferEncoding);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.FileUpload;
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String filename) {
      if(filename == null) {
         throw new NullPointerException("filename");
      } else {
         this.filename = filename;
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object o) {
      if(!(o instanceof Attribute)) {
         return false;
      } else {
         Attribute attribute = (Attribute)o;
         return this.getName().equalsIgnoreCase(attribute.getName());
      }
   }

   public int compareTo(InterfaceHttpData o) {
      if(!(o instanceof FileUpload)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + o.getHttpDataType());
      } else {
         return this.compareTo((FileUpload)o);
      }
   }

   public int compareTo(FileUpload o) {
      int v = this.getName().compareToIgnoreCase(o.getName());
      return v != 0?v:v;
   }

   public void setContentType(String contentType) {
      if(contentType == null) {
         throw new NullPointerException("contentType");
      } else {
         this.contentType = contentType;
      }
   }

   public String getContentType() {
      return this.contentType;
   }

   public String getContentTransferEncoding() {
      return this.contentTransferEncoding;
   }

   public void setContentTransferEncoding(String contentTransferEncoding) {
      this.contentTransferEncoding = contentTransferEncoding;
   }

   public String toString() {
      return "Content-Disposition: form-data; name=\"" + this.getName() + "\"; " + "filename" + "=\"" + this.filename + "\"\r\n" + "Content-Type" + ": " + this.contentType + (this.charset != null?"; charset=" + this.charset + "\r\n":"\r\n") + "Content-Length" + ": " + this.length() + "\r\n" + "Completed: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory();
   }

   public FileUpload copy() {
      MemoryFileUpload upload = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
      ByteBuf buf = this.content();
      if(buf != null) {
         try {
            upload.setContent(buf.copy());
            return upload;
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      } else {
         return upload;
      }
   }

   public FileUpload duplicate() {
      MemoryFileUpload upload = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
      ByteBuf buf = this.content();
      if(buf != null) {
         try {
            upload.setContent(buf.duplicate());
            return upload;
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      } else {
         return upload;
      }
   }

   public FileUpload retain() {
      super.retain();
      return this;
   }

   public FileUpload retain(int increment) {
      super.retain(increment);
      return this;
   }
}
