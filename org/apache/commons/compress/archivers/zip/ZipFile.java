package org.apache.commons.compress.archivers.zip;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ExplodingInputStream;
import org.apache.commons.compress.archivers.zip.GeneralPurposeBit;
import org.apache.commons.compress.archivers.zip.UnshrinkingInputStream;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEightByteInteger;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.commons.compress.utils.IOUtils;

public class ZipFile implements Closeable {
   private static final int HASH_SIZE = 509;
   static final int NIBLET_MASK = 15;
   static final int BYTE_SHIFT = 8;
   private static final int POS_0 = 0;
   private static final int POS_1 = 1;
   private static final int POS_2 = 2;
   private static final int POS_3 = 3;
   private final List entries;
   private final Map nameMap;
   private final String encoding;
   private final ZipEncoding zipEncoding;
   private final String archiveName;
   private final RandomAccessFile archive;
   private final boolean useUnicodeExtraFields;
   private boolean closed;
   private final byte[] DWORD_BUF;
   private final byte[] WORD_BUF;
   private final byte[] CFH_BUF;
   private final byte[] SHORT_BUF;
   private static final int CFH_LEN = 42;
   private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
   static final int MIN_EOCD_SIZE = 22;
   private static final int MAX_EOCD_SIZE = 65557;
   private static final int CFD_LOCATOR_OFFSET = 16;
   private static final int ZIP64_EOCDL_LENGTH = 20;
   private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
   private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
   private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
   private final Comparator OFFSET_COMPARATOR;

   public ZipFile(File f) throws IOException {
      this(f, "UTF8");
   }

   public ZipFile(String name) throws IOException {
      this(new File(name), "UTF8");
   }

   public ZipFile(String name, String encoding) throws IOException {
      this(new File(name), encoding, true);
   }

   public ZipFile(File f, String encoding) throws IOException {
      this(f, encoding, true);
   }

   public ZipFile(File f, String encoding, boolean useUnicodeExtraFields) throws IOException {
      this.entries = new LinkedList();
      this.nameMap = new HashMap(509);
      this.DWORD_BUF = new byte[8];
      this.WORD_BUF = new byte[4];
      this.CFH_BUF = new byte[42];
      this.SHORT_BUF = new byte[2];
      this.OFFSET_COMPARATOR = new Comparator() {
         public int compare(ZipArchiveEntry e1, ZipArchiveEntry e2) {
            if(e1 == e2) {
               return 0;
            } else {
               ZipFile.Entry ent1 = e1 instanceof ZipFile.Entry?(ZipFile.Entry)e1:null;
               ZipFile.Entry ent2 = e2 instanceof ZipFile.Entry?(ZipFile.Entry)e2:null;
               if(ent1 == null) {
                  return 1;
               } else if(ent2 == null) {
                  return -1;
               } else {
                  long val = ent1.getOffsetEntry().headerOffset - ent2.getOffsetEntry().headerOffset;
                  return val == 0L?0:(val < 0L?-1:1);
               }
            }
         }
      };
      this.archiveName = f.getAbsolutePath();
      this.encoding = encoding;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      this.useUnicodeExtraFields = useUnicodeExtraFields;
      this.archive = new RandomAccessFile(f, "r");
      boolean success = false;

      try {
         Map<ZipArchiveEntry, ZipFile.NameAndComment> entriesWithoutUTF8Flag = this.populateFromCentralDirectory();
         this.resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
         success = true;
      } finally {
         if(!success) {
            this.closed = true;
            IOUtils.closeQuietly(this.archive);
         }

      }

   }

   public String getEncoding() {
      return this.encoding;
   }

   public void close() throws IOException {
      this.closed = true;
      this.archive.close();
   }

   public static void closeQuietly(ZipFile zipfile) {
      IOUtils.closeQuietly(zipfile);
   }

   public Enumeration getEntries() {
      return Collections.enumeration(this.entries);
   }

   public Enumeration getEntriesInPhysicalOrder() {
      ZipArchiveEntry[] allEntries = (ZipArchiveEntry[])this.entries.toArray(new ZipArchiveEntry[0]);
      Arrays.sort(allEntries, this.OFFSET_COMPARATOR);
      return Collections.enumeration(Arrays.asList(allEntries));
   }

   public ZipArchiveEntry getEntry(String name) {
      LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList)this.nameMap.get(name);
      return entriesOfThatName != null?(ZipArchiveEntry)entriesOfThatName.getFirst():null;
   }

   public Iterable getEntries(String name) {
      List<ZipArchiveEntry> entriesOfThatName = (List)this.nameMap.get(name);
      return entriesOfThatName != null?entriesOfThatName:Collections.emptyList();
   }

   public Iterable getEntriesInPhysicalOrder(String name) {
      ZipArchiveEntry[] entriesOfThatName = new ZipArchiveEntry[0];
      if(this.nameMap.containsKey(name)) {
         entriesOfThatName = (ZipArchiveEntry[])((LinkedList)this.nameMap.get(name)).toArray(entriesOfThatName);
         Arrays.sort(entriesOfThatName, this.OFFSET_COMPARATOR);
      }

      return Arrays.asList(entriesOfThatName);
   }

   public boolean canReadEntryData(ZipArchiveEntry ze) {
      return ZipUtil.canHandleEntryData(ze);
   }

   public InputStream getInputStream(ZipArchiveEntry ze) throws IOException, ZipException {
      if(!(ze instanceof ZipFile.Entry)) {
         return null;
      } else {
         ZipFile.OffsetEntry offsetEntry = ((ZipFile.Entry)ze).getOffsetEntry();
         ZipUtil.checkRequestedFeatures(ze);
         long start = offsetEntry.dataOffset;
         final ZipFile.BoundedInputStream bis = new ZipFile.BoundedInputStream(start, ze.getCompressedSize());
         switch(ZipMethod.getMethodByCode(ze.getMethod())) {
         case STORED:
            return bis;
         case UNSHRINKING:
            return new UnshrinkingInputStream(bis);
         case IMPLODING:
            return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new BufferedInputStream(bis));
         case DEFLATED:
            bis.addDummy();
            final Inflater inflater = new Inflater(true);
            return new InflaterInputStream(bis, inflater) {
               public void close() throws IOException {
                  super.close();
                  inflater.end();
               }
            };
         default:
            throw new ZipException("Found unsupported compression method " + ze.getMethod());
         }
      }
   }

   public String getUnixSymlink(ZipArchiveEntry entry) throws IOException {
      if(entry != null && entry.isUnixSymlink()) {
         InputStream in = null;

         String var4;
         try {
            in = this.getInputStream(entry);
            byte[] symlinkBytes = IOUtils.toByteArray(in);
            var4 = this.zipEncoding.decode(symlinkBytes);
         } finally {
            if(in != null) {
               in.close();
            }

         }

         return var4;
      } else {
         return null;
      }
   }

   protected void finalize() throws Throwable {
      try {
         if(!this.closed) {
            System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
            this.close();
         }
      } finally {
         super.finalize();
      }

   }

   private Map populateFromCentralDirectory() throws IOException {
      HashMap<ZipArchiveEntry, ZipFile.NameAndComment> noUTF8Flag = new HashMap();
      this.positionAtCentralDirectory();
      this.archive.readFully(this.WORD_BUF);
      long sig = ZipLong.getValue(this.WORD_BUF);
      if(sig != CFH_SIG && this.startsWithLocalFileHeader()) {
         throw new IOException("central directory is empty, can\'t expand corrupt archive.");
      } else {
         while(sig == CFH_SIG) {
            this.readCentralDirectoryEntry(noUTF8Flag);
            this.archive.readFully(this.WORD_BUF);
            sig = ZipLong.getValue(this.WORD_BUF);
         }

         return noUTF8Flag;
      }
   }

   private void readCentralDirectoryEntry(Map noUTF8Flag) throws IOException {
      this.archive.readFully(this.CFH_BUF);
      int off = 0;
      ZipFile.OffsetEntry offset = new ZipFile.OffsetEntry();
      ZipFile.Entry ze = new ZipFile.Entry(offset);
      int versionMadeBy = ZipShort.getValue(this.CFH_BUF, off);
      off = off + 2;
      ze.setPlatform(versionMadeBy >> 8 & 15);
      off = off + 2;
      GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.CFH_BUF, off);
      boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
      ZipEncoding entryEncoding = hasUTF8Flag?ZipEncodingHelper.UTF8_ZIP_ENCODING:this.zipEncoding;
      ze.setGeneralPurposeBit(gpFlag);
      off = off + 2;
      ze.setMethod(ZipShort.getValue(this.CFH_BUF, off));
      off = off + 2;
      long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.CFH_BUF, off));
      ze.setTime(time);
      off = off + 4;
      ze.setCrc(ZipLong.getValue(this.CFH_BUF, off));
      off = off + 4;
      ze.setCompressedSize(ZipLong.getValue(this.CFH_BUF, off));
      off = off + 4;
      ze.setSize(ZipLong.getValue(this.CFH_BUF, off));
      off = off + 4;
      int fileNameLen = ZipShort.getValue(this.CFH_BUF, off);
      off = off + 2;
      int extraLen = ZipShort.getValue(this.CFH_BUF, off);
      off = off + 2;
      int commentLen = ZipShort.getValue(this.CFH_BUF, off);
      off = off + 2;
      int diskStart = ZipShort.getValue(this.CFH_BUF, off);
      off = off + 2;
      ze.setInternalAttributes(ZipShort.getValue(this.CFH_BUF, off));
      off = off + 2;
      ze.setExternalAttributes(ZipLong.getValue(this.CFH_BUF, off));
      off = off + 4;
      byte[] fileName = new byte[fileNameLen];
      this.archive.readFully(fileName);
      ze.setName(entryEncoding.decode(fileName), fileName);
      offset.headerOffset = ZipLong.getValue(this.CFH_BUF, off);
      this.entries.add(ze);
      byte[] cdExtraData = new byte[extraLen];
      this.archive.readFully(cdExtraData);
      ze.setCentralDirectoryExtra(cdExtraData);
      this.setSizesAndOffsetFromZip64Extra(ze, offset, diskStart);
      byte[] comment = new byte[commentLen];
      this.archive.readFully(comment);
      ze.setComment(entryEncoding.decode(comment));
      if(!hasUTF8Flag && this.useUnicodeExtraFields) {
         noUTF8Flag.put(ze, new ZipFile.NameAndComment(fileName, comment));
      }

   }

   private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze, ZipFile.OffsetEntry offset, int diskStart) throws IOException {
      Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
      if(z64 != null) {
         boolean hasUncompressedSize = ze.getSize() == 4294967295L;
         boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
         boolean hasRelativeHeaderOffset = offset.headerOffset == 4294967295L;
         z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, diskStart == '\uffff');
         if(hasUncompressedSize) {
            ze.setSize(z64.getSize().getLongValue());
         } else if(hasCompressedSize) {
            z64.setSize(new ZipEightByteInteger(ze.getSize()));
         }

         if(hasCompressedSize) {
            ze.setCompressedSize(z64.getCompressedSize().getLongValue());
         } else if(hasUncompressedSize) {
            z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
         }

         if(hasRelativeHeaderOffset) {
            offset.headerOffset = z64.getRelativeHeaderOffset().getLongValue();
         }
      }

   }

   private void positionAtCentralDirectory() throws IOException {
      this.positionAtEndOfCentralDirectoryRecord();
      boolean found = false;
      boolean searchedForZip64EOCD = this.archive.getFilePointer() > 20L;
      if(searchedForZip64EOCD) {
         this.archive.seek(this.archive.getFilePointer() - 20L);
         this.archive.readFully(this.WORD_BUF);
         found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.WORD_BUF);
      }

      if(!found) {
         if(searchedForZip64EOCD) {
            this.skipBytes(16);
         }

         this.positionAtCentralDirectory32();
      } else {
         this.positionAtCentralDirectory64();
      }

   }

   private void positionAtCentralDirectory64() throws IOException {
      this.skipBytes(4);
      this.archive.readFully(this.DWORD_BUF);
      this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
      this.archive.readFully(this.WORD_BUF);
      if(!Arrays.equals(this.WORD_BUF, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
         throw new ZipException("archive\'s ZIP64 end of central directory locator is corrupt.");
      } else {
         this.skipBytes(44);
         this.archive.readFully(this.DWORD_BUF);
         this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
      }
   }

   private void positionAtCentralDirectory32() throws IOException {
      this.skipBytes(16);
      this.archive.readFully(this.WORD_BUF);
      this.archive.seek(ZipLong.getValue(this.WORD_BUF));
   }

   private void positionAtEndOfCentralDirectoryRecord() throws IOException {
      boolean found = this.tryToLocateSignature(22L, 65557L, ZipArchiveOutputStream.EOCD_SIG);
      if(!found) {
         throw new ZipException("archive is not a ZIP archive");
      }
   }

   private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig) throws IOException {
      boolean found = false;
      long off = this.archive.length() - minDistanceFromEnd;
      long stopSearching = Math.max(0L, this.archive.length() - maxDistanceFromEnd);
      if(off >= 0L) {
         for(; off >= stopSearching; --off) {
            this.archive.seek(off);
            int curr = this.archive.read();
            if(curr == -1) {
               break;
            }

            if(curr == sig[0]) {
               curr = this.archive.read();
               if(curr == sig[1]) {
                  curr = this.archive.read();
                  if(curr == sig[2]) {
                     curr = this.archive.read();
                     if(curr == sig[3]) {
                        found = true;
                        break;
                     }
                  }
               }
            }
         }
      }

      if(found) {
         this.archive.seek(off);
      }

      return found;
   }

   private void skipBytes(int count) throws IOException {
      int skippedNow;
      for(int totalSkipped = 0; totalSkipped < count; totalSkipped += skippedNow) {
         skippedNow = this.archive.skipBytes(count - totalSkipped);
         if(skippedNow <= 0) {
            throw new EOFException();
         }
      }

   }

   private void resolveLocalFileHeaderData(Map entriesWithoutUTF8Flag) throws IOException {
      for(ZipArchiveEntry zipArchiveEntry : this.entries) {
         ZipFile.Entry ze = (ZipFile.Entry)zipArchiveEntry;
         ZipFile.OffsetEntry offsetEntry = ze.getOffsetEntry();
         long offset = offsetEntry.headerOffset;
         this.archive.seek(offset + 26L);
         this.archive.readFully(this.SHORT_BUF);
         int fileNameLen = ZipShort.getValue(this.SHORT_BUF);
         this.archive.readFully(this.SHORT_BUF);
         int extraFieldLen = ZipShort.getValue(this.SHORT_BUF);

         int skipped;
         for(int lenToSkip = fileNameLen; lenToSkip > 0; lenToSkip -= skipped) {
            skipped = this.archive.skipBytes(lenToSkip);
            if(skipped <= 0) {
               throw new IOException("failed to skip file name in local file header");
            }
         }

         byte[] localExtraData = new byte[extraFieldLen];
         this.archive.readFully(localExtraData);
         ze.setExtra(localExtraData);
         offsetEntry.dataOffset = offset + 26L + 2L + 2L + (long)fileNameLen + (long)extraFieldLen;
         if(entriesWithoutUTF8Flag.containsKey(ze)) {
            ZipFile.NameAndComment nc = (ZipFile.NameAndComment)entriesWithoutUTF8Flag.get(ze);
            ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
         }

         String name = ze.getName();
         LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList)this.nameMap.get(name);
         if(entriesOfThatName == null) {
            entriesOfThatName = new LinkedList();
            this.nameMap.put(name, entriesOfThatName);
         }

         entriesOfThatName.addLast(ze);
      }

   }

   private boolean startsWithLocalFileHeader() throws IOException {
      this.archive.seek(0L);
      this.archive.readFully(this.WORD_BUF);
      return Arrays.equals(this.WORD_BUF, ZipArchiveOutputStream.LFH_SIG);
   }

   private class BoundedInputStream extends InputStream {
      private long remaining;
      private long loc;
      private boolean addDummyByte = false;

      BoundedInputStream(long start, long remaining) {
         this.remaining = remaining;
         this.loc = start;
      }

      public int read() throws IOException {
         if(this.remaining-- <= 0L) {
            if(this.addDummyByte) {
               this.addDummyByte = false;
               return 0;
            } else {
               return -1;
            }
         } else {
            synchronized(ZipFile.this.archive) {
               ZipFile.this.archive.seek((long)(this.loc++));
               return ZipFile.this.archive.read();
            }
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if(this.remaining <= 0L) {
            if(this.addDummyByte) {
               this.addDummyByte = false;
               b[off] = 0;
               return 1;
            } else {
               return -1;
            }
         } else if(len <= 0) {
            return 0;
         } else {
            if((long)len > this.remaining) {
               len = (int)this.remaining;
            }

            int ret = -1;
            synchronized(ZipFile.this.archive) {
               ZipFile.this.archive.seek(this.loc);
               ret = ZipFile.this.archive.read(b, off, len);
            }

            if(ret > 0) {
               this.loc += (long)ret;
               this.remaining -= (long)ret;
            }

            return ret;
         }
      }

      void addDummy() {
         this.addDummyByte = true;
      }
   }

   private static class Entry extends ZipArchiveEntry {
      private final ZipFile.OffsetEntry offsetEntry;

      Entry(ZipFile.OffsetEntry offset) {
         this.offsetEntry = offset;
      }

      ZipFile.OffsetEntry getOffsetEntry() {
         return this.offsetEntry;
      }

      public int hashCode() {
         return 3 * super.hashCode() + (int)(this.offsetEntry.headerOffset % 2147483647L);
      }

      public boolean equals(Object other) {
         if(!super.equals(other)) {
            return false;
         } else {
            ZipFile.Entry otherEntry = (ZipFile.Entry)other;
            return this.offsetEntry.headerOffset == otherEntry.offsetEntry.headerOffset && this.offsetEntry.dataOffset == otherEntry.offsetEntry.dataOffset;
         }
      }
   }

   private static final class NameAndComment {
      private final byte[] name;
      private final byte[] comment;

      private NameAndComment(byte[] name, byte[] comment) {
         this.name = name;
         this.comment = comment;
      }
   }

   private static final class OffsetEntry {
      private long headerOffset;
      private long dataOffset;

      private OffsetEntry() {
         this.headerOffset = -1L;
         this.dataOffset = -1L;
      }
   }
}
