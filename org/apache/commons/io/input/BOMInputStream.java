package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.ProxyInputStream;

public class BOMInputStream extends ProxyInputStream {
   private final boolean include;
   private final List boms;
   private ByteOrderMark byteOrderMark;
   private int[] firstBytes;
   private int fbLength;
   private int fbIndex;
   private int markFbIndex;
   private boolean markedAtStart;
   private static final Comparator ByteOrderMarkLengthComparator = new Comparator() {
      public int compare(ByteOrderMark bom1, ByteOrderMark bom2) {
         int len1 = bom1.length();
         int len2 = bom2.length();
         return len1 > len2?-1:(len2 > len1?1:0);
      }
   };

   public BOMInputStream(InputStream delegate) {
      this(delegate, false, new ByteOrderMark[]{ByteOrderMark.UTF_8});
   }

   public BOMInputStream(InputStream delegate, boolean include) {
      this(delegate, include, new ByteOrderMark[]{ByteOrderMark.UTF_8});
   }

   public BOMInputStream(InputStream delegate, ByteOrderMark... boms) {
      this(delegate, false, boms);
   }

   public BOMInputStream(InputStream delegate, boolean include, ByteOrderMark... boms) {
      super(delegate);
      if(boms != null && boms.length != 0) {
         this.include = include;
         Arrays.sort(boms, ByteOrderMarkLengthComparator);
         this.boms = Arrays.asList(boms);
      } else {
         throw new IllegalArgumentException("No BOMs specified");
      }
   }

   public boolean hasBOM() throws IOException {
      return this.getBOM() != null;
   }

   public boolean hasBOM(ByteOrderMark bom) throws IOException {
      if(!this.boms.contains(bom)) {
         throw new IllegalArgumentException("Stream not configure to detect " + bom);
      } else {
         return this.byteOrderMark != null && this.getBOM().equals(bom);
      }
   }

   public ByteOrderMark getBOM() throws IOException {
      if(this.firstBytes == null) {
         this.fbLength = 0;
         int maxBomSize = ((ByteOrderMark)this.boms.get(0)).length();
         this.firstBytes = new int[maxBomSize];

         for(int i = 0; i < this.firstBytes.length; ++i) {
            this.firstBytes[i] = this.in.read();
            ++this.fbLength;
            if(this.firstBytes[i] < 0) {
               break;
            }
         }

         this.byteOrderMark = this.find();
         if(this.byteOrderMark != null && !this.include) {
            if(this.byteOrderMark.length() < this.firstBytes.length) {
               this.fbIndex = this.byteOrderMark.length();
            } else {
               this.fbLength = 0;
            }
         }
      }

      return this.byteOrderMark;
   }

   public String getBOMCharsetName() throws IOException {
      this.getBOM();
      return this.byteOrderMark == null?null:this.byteOrderMark.getCharsetName();
   }

   private int readFirstBytes() throws IOException {
      this.getBOM();
      return this.fbIndex < this.fbLength?this.firstBytes[this.fbIndex++]:-1;
   }

   private ByteOrderMark find() {
      for(ByteOrderMark bom : this.boms) {
         if(this.matches(bom)) {
            return bom;
         }
      }

      return null;
   }

   private boolean matches(ByteOrderMark bom) {
      for(int i = 0; i < bom.length(); ++i) {
         if(bom.get(i) != this.firstBytes[i]) {
            return false;
         }
      }

      return true;
   }

   public int read() throws IOException {
      int b = this.readFirstBytes();
      return b >= 0?b:this.in.read();
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int firstCount = 0;
      int b = 0;

      while(len > 0 && b >= 0) {
         b = this.readFirstBytes();
         if(b >= 0) {
            buf[off++] = (byte)(b & 255);
            --len;
            ++firstCount;
         }
      }

      int secondCount = this.in.read(buf, off, len);
      return secondCount < 0?(firstCount > 0?firstCount:-1):firstCount + secondCount;
   }

   public int read(byte[] buf) throws IOException {
      return this.read(buf, 0, buf.length);
   }

   public synchronized void mark(int readlimit) {
      this.markFbIndex = this.fbIndex;
      this.markedAtStart = this.firstBytes == null;
      this.in.mark(readlimit);
   }

   public synchronized void reset() throws IOException {
      this.fbIndex = this.markFbIndex;
      if(this.markedAtStart) {
         this.firstBytes = null;
      }

      this.in.reset();
   }

   public long skip(long n) throws IOException {
      while(n > 0L && this.readFirstBytes() >= 0) {
         --n;
      }

      return this.in.skip(n);
   }
}
