package org.apache.commons.io.filefilter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class MagicNumberFileFilter extends AbstractFileFilter implements Serializable {
   private static final long serialVersionUID = -547733176983104172L;
   private final byte[] magicNumbers;
   private final long byteOffset;

   public MagicNumberFileFilter(byte[] magicNumber) {
      this(magicNumber, 0L);
   }

   public MagicNumberFileFilter(String magicNumber) {
      this(magicNumber, 0L);
   }

   public MagicNumberFileFilter(String magicNumber, long offset) {
      if(magicNumber == null) {
         throw new IllegalArgumentException("The magic number cannot be null");
      } else if(magicNumber.length() == 0) {
         throw new IllegalArgumentException("The magic number must contain at least one byte");
      } else if(offset < 0L) {
         throw new IllegalArgumentException("The offset cannot be negative");
      } else {
         this.magicNumbers = magicNumber.getBytes();
         this.byteOffset = offset;
      }
   }

   public MagicNumberFileFilter(byte[] magicNumber, long offset) {
      if(magicNumber == null) {
         throw new IllegalArgumentException("The magic number cannot be null");
      } else if(magicNumber.length == 0) {
         throw new IllegalArgumentException("The magic number must contain at least one byte");
      } else if(offset < 0L) {
         throw new IllegalArgumentException("The offset cannot be negative");
      } else {
         this.magicNumbers = new byte[magicNumber.length];
         System.arraycopy(magicNumber, 0, this.magicNumbers, 0, magicNumber.length);
         this.byteOffset = offset;
      }
   }

   public boolean accept(File file) {
      if(file != null && file.isFile() && file.canRead()) {
         RandomAccessFile randomAccessFile = null;

         boolean var5;
         try {
            byte[] fileBytes = new byte[this.magicNumbers.length];
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(this.byteOffset);
            int read = randomAccessFile.read(fileBytes);
            if(read == this.magicNumbers.length) {
               var5 = Arrays.equals(this.magicNumbers, fileBytes);
               return var5;
            }

            var5 = false;
         } catch (IOException var9) {
            return false;
         } finally {
            IOUtils.closeQuietly((Closeable)randomAccessFile);
         }

         return var5;
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuilder builder = new StringBuilder(super.toString());
      builder.append("(");
      builder.append(new String(this.magicNumbers));
      builder.append(",");
      builder.append(this.byteOffset);
      builder.append(")");
      return builder.toString();
   }
}
