package org.apache.commons.compress.compressors.z._internal_;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;

public abstract class InternalLZWInputStream extends CompressorInputStream {
   private final byte[] oneByte = new byte[1];
   protected final InputStream in;
   protected int clearCode = -1;
   protected int codeSize = 9;
   protected int bitsCached = 0;
   protected int bitsCachedSize = 0;
   protected int previousCode = -1;
   protected int tableSize = 0;
   protected int[] prefixes;
   protected byte[] characters;
   private byte[] outputStack;
   private int outputStackLocation;

   protected InternalLZWInputStream(InputStream inputStream) {
      this.in = inputStream;
   }

   public void close() throws IOException {
      this.in.close();
   }

   public int read() throws IOException {
      int ret = this.read(this.oneByte);
      return ret < 0?ret:255 & this.oneByte[0];
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int bytesRead;
      for(bytesRead = this.readFromStack(b, off, len); len - bytesRead > 0; bytesRead += this.readFromStack(b, off + bytesRead, len - bytesRead)) {
         int result = this.decompressNextSymbol();
         if(result < 0) {
            if(bytesRead > 0) {
               this.count(bytesRead);
               return bytesRead;
            }

            return result;
         }
      }

      this.count(bytesRead);
      return bytesRead;
   }

   protected abstract int decompressNextSymbol() throws IOException;

   protected abstract int addEntry(int var1, byte var2) throws IOException;

   protected void setClearCode(int codeSize) {
      this.clearCode = 1 << codeSize - 1;
   }

   protected void initializeTables(int maxCodeSize) {
      int maxTableSize = 1 << maxCodeSize;
      this.prefixes = new int[maxTableSize];
      this.characters = new byte[maxTableSize];
      this.outputStack = new byte[maxTableSize];
      this.outputStackLocation = maxTableSize;
      int max = 256;

      for(int i = 0; i < 256; ++i) {
         this.prefixes[i] = -1;
         this.characters[i] = (byte)i;
      }

   }

   protected int readNextCode() throws IOException {
      while(this.bitsCachedSize < this.codeSize) {
         int nextByte = this.in.read();
         if(nextByte < 0) {
            return nextByte;
         }

         this.bitsCached |= nextByte << this.bitsCachedSize;
         this.bitsCachedSize += 8;
      }

      int mask = (1 << this.codeSize) - 1;
      int code = this.bitsCached & mask;
      this.bitsCached >>>= this.codeSize;
      this.bitsCachedSize -= this.codeSize;
      return code;
   }

   protected int addEntry(int previousCode, byte character, int maxTableSize) {
      if(this.tableSize < maxTableSize) {
         int index = this.tableSize;
         this.prefixes[this.tableSize] = previousCode;
         this.characters[this.tableSize] = character;
         ++this.tableSize;
         return index;
      } else {
         return -1;
      }
   }

   protected int addRepeatOfPreviousCode() throws IOException {
      if(this.previousCode == -1) {
         throw new IOException("The first code can\'t be a reference to its preceding code");
      } else {
         byte firstCharacter = 0;

         for(int last = this.previousCode; last >= 0; last = this.prefixes[last]) {
            firstCharacter = this.characters[last];
         }

         return this.addEntry(this.previousCode, firstCharacter);
      }
   }

   protected int expandCodeToOutputStack(int code, boolean addedUnfinishedEntry) throws IOException {
      for(int entry = code; entry >= 0; entry = this.prefixes[entry]) {
         this.outputStack[--this.outputStackLocation] = this.characters[entry];
      }

      if(this.previousCode != -1 && !addedUnfinishedEntry) {
         this.addEntry(this.previousCode, this.outputStack[this.outputStackLocation]);
      }

      this.previousCode = code;
      return this.outputStackLocation;
   }

   private int readFromStack(byte[] b, int off, int len) {
      int remainingInStack = this.outputStack.length - this.outputStackLocation;
      if(remainingInStack > 0) {
         int maxLength = Math.min(remainingInStack, len);
         System.arraycopy(this.outputStack, this.outputStackLocation, b, off, maxLength);
         this.outputStackLocation += maxLength;
         return maxLength;
      } else {
         return 0;
      }
   }
}
