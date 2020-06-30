package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractMemoryHttpData extends AbstractHttpData {
   private ByteBuf byteBuf;
   private int chunkPosition;
   protected boolean isRenamed;

   protected AbstractMemoryHttpData(String name, Charset charset, long size) {
      super(name, charset, size);
   }

   public void setContent(ByteBuf buffer) throws IOException {
      if(buffer == null) {
         throw new NullPointerException("buffer");
      } else {
         long localsize = (long)buffer.readableBytes();
         if(this.definedSize > 0L && this.definedSize < localsize) {
            throw new IOException("Out of size: " + localsize + " > " + this.definedSize);
         } else {
            if(this.byteBuf != null) {
               this.byteBuf.release();
            }

            this.byteBuf = buffer;
            this.size = localsize;
            this.completed = true;
         }
      }
   }

   public void setContent(InputStream inputStream) throws IOException {
      if(inputStream == null) {
         throw new NullPointerException("inputStream");
      } else {
         ByteBuf buffer = Unpooled.buffer();
         byte[] bytes = new byte[16384];
         int read = inputStream.read(bytes);

         int written;
         for(written = 0; read > 0; read = inputStream.read(bytes)) {
            buffer.writeBytes((byte[])bytes, 0, read);
            written += read;
         }

         this.size = (long)written;
         if(this.definedSize > 0L && this.definedSize < this.size) {
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
         } else {
            if(this.byteBuf != null) {
               this.byteBuf.release();
            }

            this.byteBuf = buffer;
            this.completed = true;
         }
      }
   }

   public void addContent(ByteBuf buffer, boolean last) throws IOException {
      if(buffer != null) {
         long localsize = (long)buffer.readableBytes();
         if(this.definedSize > 0L && this.definedSize < this.size + localsize) {
            throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
         }

         this.size += localsize;
         if(this.byteBuf == null) {
            this.byteBuf = buffer;
         } else if(this.byteBuf instanceof CompositeByteBuf) {
            CompositeByteBuf cbb = (CompositeByteBuf)this.byteBuf;
            cbb.addComponent(buffer);
            cbb.writerIndex(cbb.writerIndex() + buffer.readableBytes());
         } else {
            CompositeByteBuf cbb = Unpooled.compositeBuffer(Integer.MAX_VALUE);
            cbb.addComponents(new ByteBuf[]{this.byteBuf, buffer});
            cbb.writerIndex(this.byteBuf.readableBytes() + buffer.readableBytes());
            this.byteBuf = cbb;
         }
      }

      if(last) {
         this.completed = true;
      } else if(buffer == null) {
         throw new NullPointerException("buffer");
      }

   }

   public void setContent(File file) throws IOException {
      if(file == null) {
         throw new NullPointerException("file");
      } else {
         long newsize = file.length();
         if(newsize > 2147483647L) {
            throw new IllegalArgumentException("File too big to be loaded in memory");
         } else {
            FileInputStream inputStream = new FileInputStream(file);
            FileChannel fileChannel = inputStream.getChannel();
            byte[] array = new byte[(int)newsize];
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);

            for(int read = 0; (long)read < newsize; read += fileChannel.read(byteBuffer)) {
               ;
            }

            fileChannel.close();
            inputStream.close();
            byteBuffer.flip();
            if(this.byteBuf != null) {
               this.byteBuf.release();
            }

            this.byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, new ByteBuffer[]{byteBuffer});
            this.size = newsize;
            this.completed = true;
         }
      }
   }

   public void delete() {
      if(this.byteBuf != null) {
         this.byteBuf.release();
         this.byteBuf = null;
      }

   }

   public byte[] get() {
      if(this.byteBuf == null) {
         return Unpooled.EMPTY_BUFFER.array();
      } else {
         byte[] array = new byte[this.byteBuf.readableBytes()];
         this.byteBuf.getBytes(this.byteBuf.readerIndex(), array);
         return array;
      }
   }

   public String getString() {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   public String getString(Charset encoding) {
      if(this.byteBuf == null) {
         return "";
      } else {
         if(encoding == null) {
            encoding = HttpConstants.DEFAULT_CHARSET;
         }

         return this.byteBuf.toString(encoding);
      }
   }

   public ByteBuf getByteBuf() {
      return this.byteBuf;
   }

   public ByteBuf getChunk(int length) throws IOException {
      if(this.byteBuf != null && length != 0 && this.byteBuf.readableBytes() != 0) {
         int sizeLeft = this.byteBuf.readableBytes() - this.chunkPosition;
         if(sizeLeft == 0) {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
         } else {
            int sliceLength = length;
            if(sizeLeft < length) {
               sliceLength = sizeLeft;
            }

            ByteBuf chunk = this.byteBuf.slice(this.chunkPosition, sliceLength).retain();
            this.chunkPosition += sliceLength;
            return chunk;
         }
      } else {
         this.chunkPosition = 0;
         return Unpooled.EMPTY_BUFFER;
      }
   }

   public boolean isInMemory() {
      return true;
   }

   public boolean renameTo(File dest) throws IOException {
      if(dest == null) {
         throw new NullPointerException("dest");
      } else if(this.byteBuf == null) {
         dest.createNewFile();
         this.isRenamed = true;
         return true;
      } else {
         int length = this.byteBuf.readableBytes();
         FileOutputStream outputStream = new FileOutputStream(dest);
         FileChannel fileChannel = outputStream.getChannel();
         int written = 0;
         if(this.byteBuf.nioBufferCount() == 1) {
            for(ByteBuffer byteBuffer = this.byteBuf.nioBuffer(); written < length; written += fileChannel.write(byteBuffer)) {
               ;
            }
         } else {
            for(ByteBuffer[] byteBuffers = this.byteBuf.nioBuffers(); written < length; written = (int)((long)written + fileChannel.write(byteBuffers))) {
               ;
            }
         }

         fileChannel.force(false);
         fileChannel.close();
         outputStream.close();
         this.isRenamed = true;
         return written == length;
      }
   }

   public File getFile() throws IOException {
      throw new IOException("Not represented by a file");
   }
}
