package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.multipart.AbstractDiskHttpData;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DiskFileUpload extends AbstractDiskHttpData implements FileUpload {
   public static String baseDirectory;
   public static boolean deleteOnExitTemporaryFile = true;
   public static final String prefix = "FUp_";
   public static final String postfix = ".tmp";
   private String filename;
   private String contentType;
   private String contentTransferEncoding;

   public DiskFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
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
      return "Content-Disposition: form-data; name=\"" + this.getName() + "\"; " + "filename" + "=\"" + this.filename + "\"\r\n" + "Content-Type" + ": " + this.contentType + (this.charset != null?"; charset=" + this.charset + "\r\n":"\r\n") + "Content-Length" + ": " + this.length() + "\r\n" + "Completed: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory() + "\r\nRealFile: " + (this.file != null?this.file.getAbsolutePath():"null") + " DefaultDeleteAfter: " + deleteOnExitTemporaryFile;
   }

   protected boolean deleteOnExit() {
      return deleteOnExitTemporaryFile;
   }

   protected String getBaseDirectory() {
      return baseDirectory;
   }

   protected String getDiskFilename() {
      File file = new File(this.filename);
      return file.getName();
   }

   protected String getPostfix() {
      return ".tmp";
   }

   protected String getPrefix() {
      return "FUp_";
   }

   public FileUpload copy() {
      DiskFileUpload upload = new DiskFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
      ByteBuf buf = this.content();
      if(buf != null) {
         try {
            upload.setContent(buf.copy());
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }

      return upload;
   }

   public FileUpload duplicate() {
      DiskFileUpload upload = new DiskFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
      ByteBuf buf = this.content();
      if(buf != null) {
         try {
            upload.setContent(buf.duplicate());
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }

      return upload;
   }

   public FileUpload retain(int increment) {
      super.retain(increment);
      return this;
   }

   public FileUpload retain() {
      super.retain();
      return this;
   }
}
