package io.netty.channel;

import io.netty.channel.FileRegion;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
   private final FileChannel file;
   private final long position;
   private final long count;
   private long transfered;

   public DefaultFileRegion(FileChannel file, long position, long count) {
      if(file == null) {
         throw new NullPointerException("file");
      } else if(position < 0L) {
         throw new IllegalArgumentException("position must be >= 0 but was " + position);
      } else if(count < 0L) {
         throw new IllegalArgumentException("count must be >= 0 but was " + count);
      } else {
         this.file = file;
         this.position = position;
         this.count = count;
      }
   }

   public long position() {
      return this.position;
   }

   public long count() {
      return this.count;
   }

   public long transfered() {
      return this.transfered;
   }

   public long transferTo(WritableByteChannel target, long position) throws IOException {
      long count = this.count - position;
      if(count >= 0L && position >= 0L) {
         if(count == 0L) {
            return 0L;
         } else {
            long written = this.file.transferTo(this.position + position, count, target);
            if(written > 0L) {
               this.transfered += written;
            }

            return written;
         }
      } else {
         throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
      }
   }

   protected void deallocate() {
      try {
         this.file.close();
      } catch (IOException var2) {
         if(logger.isWarnEnabled()) {
            logger.warn("Failed to close a file.", (Throwable)var2);
         }
      }

   }
}
