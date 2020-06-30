package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload implements FileUpload {
   private FileUpload fileUpload;
   private final long limitSize;
   private final long definedSize;

   public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
      this.limitSize = limitSize;
      if(size > this.limitSize) {
         this.fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
      } else {
         this.fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
      }

      this.definedSize = size;
   }

   public void addContent(ByteBuf buffer, boolean last) throws IOException {
      if(this.fileUpload instanceof MemoryFileUpload && this.fileUpload.length() + (long)buffer.readableBytes() > this.limitSize) {
         DiskFileUpload diskFileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
         ByteBuf data = this.fileUpload.getByteBuf();
         if(data != null && data.isReadable()) {
            diskFileUpload.addContent(data.retain(), false);
         }

         this.fileUpload.release();
         this.fileUpload = diskFileUpload;
      }

      this.fileUpload.addContent(buffer, last);
   }

   public void delete() {
      this.fileUpload.delete();
   }

   public byte[] get() throws IOException {
      return this.fileUpload.get();
   }

   public ByteBuf getByteBuf() throws IOException {
      return this.fileUpload.getByteBuf();
   }

   public Charset getCharset() {
      return this.fileUpload.getCharset();
   }

   public String getContentType() {
      return this.fileUpload.getContentType();
   }

   public String getContentTransferEncoding() {
      return this.fileUpload.getContentTransferEncoding();
   }

   public String getFilename() {
      return this.fileUpload.getFilename();
   }

   public String getString() throws IOException {
      return this.fileUpload.getString();
   }

   public String getString(Charset encoding) throws IOException {
      return this.fileUpload.getString(encoding);
   }

   public boolean isCompleted() {
      return this.fileUpload.isCompleted();
   }

   public boolean isInMemory() {
      return this.fileUpload.isInMemory();
   }

   public long length() {
      return this.fileUpload.length();
   }

   public boolean renameTo(File dest) throws IOException {
      return this.fileUpload.renameTo(dest);
   }

   public void setCharset(Charset charset) {
      this.fileUpload.setCharset(charset);
   }

   public void setContent(ByteBuf buffer) throws IOException {
      if((long)buffer.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
         FileUpload memoryUpload = this.fileUpload;
         this.fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
         memoryUpload.release();
      }

      this.fileUpload.setContent(buffer);
   }

   public void setContent(File file) throws IOException {
      if(file.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
         FileUpload memoryUpload = this.fileUpload;
         this.fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
         memoryUpload.release();
      }

      this.fileUpload.setContent(file);
   }

   public void setContent(InputStream inputStream) throws IOException {
      if(this.fileUpload instanceof MemoryFileUpload) {
         FileUpload memoryUpload = this.fileUpload;
         this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
         memoryUpload.release();
      }

      this.fileUpload.setContent(inputStream);
   }

   public void setContentType(String contentType) {
      this.fileUpload.setContentType(contentType);
   }

   public void setContentTransferEncoding(String contentTransferEncoding) {
      this.fileUpload.setContentTransferEncoding(contentTransferEncoding);
   }

   public void setFilename(String filename) {
      this.fileUpload.setFilename(filename);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return this.fileUpload.getHttpDataType();
   }

   public String getName() {
      return this.fileUpload.getName();
   }

   public int compareTo(InterfaceHttpData o) {
      return this.fileUpload.compareTo(o);
   }

   public String toString() {
      return "Mixed: " + this.fileUpload.toString();
   }

   public ByteBuf getChunk(int length) throws IOException {
      return this.fileUpload.getChunk(length);
   }

   public File getFile() throws IOException {
      return this.fileUpload.getFile();
   }

   public FileUpload copy() {
      return this.fileUpload.copy();
   }

   public FileUpload duplicate() {
      return this.fileUpload.duplicate();
   }

   public ByteBuf content() {
      return this.fileUpload.content();
   }

   public int refCnt() {
      return this.fileUpload.refCnt();
   }

   public FileUpload retain() {
      this.fileUpload.retain();
      return this;
   }

   public FileUpload retain(int increment) {
      this.fileUpload.retain(increment);
      return this;
   }

   public boolean release() {
      return this.fileUpload.release();
   }

   public boolean release(int decrement) {
      return this.fileUpload.release(decrement);
   }
}
