package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.GeneralPurposeBit;
import org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodePathExtraField;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.Zip64RequiredException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipEightByteInteger;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.commons.compress.utils.IOUtils;

public class ZipArchiveOutputStream extends ArchiveOutputStream {
   static final int BUFFER_SIZE = 512;
   protected boolean finished = false;
   private static final int DEFLATER_BLOCK_SIZE = 8192;
   public static final int DEFLATED = 8;
   public static final int DEFAULT_COMPRESSION = -1;
   public static final int STORED = 0;
   static final String DEFAULT_ENCODING = "UTF8";
   /** @deprecated */
   @Deprecated
   public static final int EFS_FLAG = 2048;
   private static final byte[] EMPTY = new byte[0];
   private ZipArchiveOutputStream.CurrentEntry entry;
   private String comment = "";
   private int level = -1;
   private boolean hasCompressionLevelChanged = false;
   private int method = 8;
   private final List entries = new LinkedList();
   private final CRC32 crc = new CRC32();
   private long written = 0L;
   private long cdOffset = 0L;
   private long cdLength = 0L;
   private static final byte[] ZERO = new byte[]{(byte)0, (byte)0};
   private static final byte[] LZERO = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0};
   private final Map offsets = new HashMap();
   private String encoding = "UTF8";
   private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
   protected final Deflater def;
   private final byte[] buf;
   private final RandomAccessFile raf;
   private final OutputStream out;
   private boolean useUTF8Flag;
   private boolean fallbackToUTF8;
   private ZipArchiveOutputStream.UnicodeExtraFieldPolicy createUnicodeExtraFields;
   private boolean hasUsedZip64;
   private Zip64Mode zip64Mode;
   static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
   static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
   static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
   static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
   static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
   static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
   private static final byte[] ONE = ZipLong.getBytes(1L);

   public ZipArchiveOutputStream(OutputStream out) {
      this.def = new Deflater(this.level, true);
      this.buf = new byte[512];
      this.useUTF8Flag = true;
      this.fallbackToUTF8 = false;
      this.createUnicodeExtraFields = ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NEVER;
      this.hasUsedZip64 = false;
      this.zip64Mode = Zip64Mode.AsNeeded;
      this.out = out;
      this.raf = null;
   }

   public ZipArchiveOutputStream(File file) throws IOException {
      this.def = new Deflater(this.level, true);
      this.buf = new byte[512];
      this.useUTF8Flag = true;
      this.fallbackToUTF8 = false;
      this.createUnicodeExtraFields = ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NEVER;
      this.hasUsedZip64 = false;
      this.zip64Mode = Zip64Mode.AsNeeded;
      OutputStream o = null;
      RandomAccessFile _raf = null;

      try {
         _raf = new RandomAccessFile(file, "rw");
         _raf.setLength(0L);
      } catch (IOException var5) {
         IOUtils.closeQuietly(_raf);
         _raf = null;
         o = new FileOutputStream(file);
      }

      this.out = o;
      this.raf = _raf;
   }

   public boolean isSeekable() {
      return this.raf != null;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      if(this.useUTF8Flag && !ZipEncodingHelper.isUTF8(encoding)) {
         this.useUTF8Flag = false;
      }

   }

   public String getEncoding() {
      return this.encoding;
   }

   public void setUseLanguageEncodingFlag(boolean b) {
      this.useUTF8Flag = b && ZipEncodingHelper.isUTF8(this.encoding);
   }

   public void setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy b) {
      this.createUnicodeExtraFields = b;
   }

   public void setFallbackToUTF8(boolean b) {
      this.fallbackToUTF8 = b;
   }

   public void setUseZip64(Zip64Mode mode) {
      this.zip64Mode = mode;
   }

   public void finish() throws IOException {
      if(this.finished) {
         throw new IOException("This archive has already been finished");
      } else if(this.entry != null) {
         throw new IOException("This archive contains unclosed entries.");
      } else {
         this.cdOffset = this.written;

         for(ZipArchiveEntry ze : this.entries) {
            this.writeCentralFileHeader(ze);
         }

         this.cdLength = this.written - this.cdOffset;
         this.writeZip64CentralDirectory();
         this.writeCentralDirectoryEnd();
         this.offsets.clear();
         this.entries.clear();
         this.def.end();
         this.finished = true;
      }
   }

   public void closeArchiveEntry() throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else if(this.entry == null) {
         throw new IOException("No current entry to close");
      } else {
         if(!this.entry.hasWritten) {
            this.write(EMPTY, 0, 0);
         }

         this.flushDeflater();
         Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
         long bytesWritten = this.written - this.entry.dataStart;
         long realCrc = this.crc.getValue();
         this.crc.reset();
         boolean actuallyNeedsZip64 = this.handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
         if(this.raf != null) {
            this.rewriteSizesAndCrc(actuallyNeedsZip64);
         }

         this.writeDataDescriptor(this.entry.entry);
         this.entry = null;
      }
   }

   private void flushDeflater() throws IOException {
      if(this.entry.entry.getMethod() == 8) {
         this.def.finish();

         while(!this.def.finished()) {
            this.deflate();
         }
      }

   }

   private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode) throws ZipException {
      if(this.entry.entry.getMethod() == 8) {
         this.entry.entry.setSize(this.entry.bytesRead);
         this.entry.entry.setCompressedSize(bytesWritten);
         this.entry.entry.setCrc(crc);
         this.def.reset();
      } else if(this.raf == null) {
         if(this.entry.entry.getCrc() != crc) {
            throw new ZipException("bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
         }

         if(this.entry.entry.getSize() != bytesWritten) {
            throw new ZipException("bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + bytesWritten);
         }
      } else {
         this.entry.entry.setSize(bytesWritten);
         this.entry.entry.setCompressedSize(bytesWritten);
         this.entry.entry.setCrc(crc);
      }

      boolean actuallyNeedsZip64 = effectiveMode == Zip64Mode.Always || this.entry.entry.getSize() >= 4294967295L || this.entry.entry.getCompressedSize() >= 4294967295L;
      if(actuallyNeedsZip64 && effectiveMode == Zip64Mode.Never) {
         throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
      } else {
         return actuallyNeedsZip64;
      }
   }

   private void rewriteSizesAndCrc(boolean actuallyNeedsZip64) throws IOException {
      long save = this.raf.getFilePointer();
      this.raf.seek(this.entry.localDataStart);
      this.writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
      if(this.hasZip64Extra(this.entry.entry) && actuallyNeedsZip64) {
         this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
         this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
      } else {
         this.writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
         this.writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
      }

      if(this.hasZip64Extra(this.entry.entry)) {
         this.raf.seek(this.entry.localDataStart + 12L + 4L + (long)this.getName(this.entry.entry).limit() + 4L);
         this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
         this.writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
         if(!actuallyNeedsZip64) {
            this.raf.seek(this.entry.localDataStart - 10L);
            this.writeOut(ZipShort.getBytes(10));
            this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
            this.entry.entry.setExtra();
            if(this.entry.causedUseOfZip64) {
               this.hasUsedZip64 = false;
            }
         }
      }

      this.raf.seek(save);
   }

   public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         if(this.entry != null) {
            this.closeArchiveEntry();
         }

         this.entry = new ZipArchiveOutputStream.CurrentEntry((ZipArchiveEntry)archiveEntry);
         this.entries.add(this.entry.entry);
         this.setDefaults(this.entry.entry);
         Zip64Mode effectiveMode = this.getEffectiveZip64Mode(this.entry.entry);
         this.validateSizeInformation(effectiveMode);
         if(this.shouldAddZip64Extra(this.entry.entry, effectiveMode)) {
            Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(this.entry.entry);
            ZipEightByteInteger size = ZipEightByteInteger.ZERO;
            if(this.entry.entry.getMethod() == 0 && this.entry.entry.getSize() != -1L) {
               size = new ZipEightByteInteger(this.entry.entry.getSize());
            }

            z64.setSize(size);
            z64.setCompressedSize(size);
            this.entry.entry.setExtra();
         }

         if(this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
            this.def.setLevel(this.level);
            this.hasCompressionLevelChanged = false;
         }

         this.writeLocalFileHeader(this.entry.entry);
      }
   }

   private void setDefaults(ZipArchiveEntry entry) {
      if(entry.getMethod() == -1) {
         entry.setMethod(this.method);
      }

      if(entry.getTime() == -1L) {
         entry.setTime(System.currentTimeMillis());
      }

   }

   private void validateSizeInformation(Zip64Mode effectiveMode) throws ZipException {
      if(this.entry.entry.getMethod() == 0 && this.raf == null) {
         if(this.entry.entry.getSize() == -1L) {
            throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
         }

         if(this.entry.entry.getCrc() == -1L) {
            throw new ZipException("crc checksum is required for STORED method when not writing to a file");
         }

         this.entry.entry.setCompressedSize(this.entry.entry.getSize());
      }

      if((this.entry.entry.getSize() >= 4294967295L || this.entry.entry.getCompressedSize() >= 4294967295L) && effectiveMode == Zip64Mode.Never) {
         throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
      }
   }

   private boolean shouldAddZip64Extra(ZipArchiveEntry entry, Zip64Mode mode) {
      return mode == Zip64Mode.Always || entry.getSize() >= 4294967295L || entry.getCompressedSize() >= 4294967295L || entry.getSize() == -1L && this.raf != null && mode != Zip64Mode.Never;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setLevel(int level) {
      if(level >= -1 && level <= 9) {
         this.hasCompressionLevelChanged = this.level != level;
         this.level = level;
      } else {
         throw new IllegalArgumentException("Invalid compression level: " + level);
      }
   }

   public void setMethod(int method) {
      this.method = method;
   }

   public boolean canWriteEntryData(ArchiveEntry ae) {
      if(!(ae instanceof ZipArchiveEntry)) {
         return false;
      } else {
         ZipArchiveEntry zae = (ZipArchiveEntry)ae;
         return zae.getMethod() != ZipMethod.IMPLODING.getCode() && zae.getMethod() != ZipMethod.UNSHRINKING.getCode() && ZipUtil.canHandleEntryData(zae);
      }
   }

   public void write(byte[] b, int offset, int length) throws IOException {
      if(this.entry == null) {
         throw new IllegalStateException("No current entry");
      } else {
         ZipUtil.checkRequestedFeatures(this.entry.entry);
         this.entry.hasWritten = true;
         if(this.entry.entry.getMethod() == 8) {
            this.writeDeflated(b, offset, length);
         } else {
            this.writeOut(b, offset, length);
            this.written += (long)length;
         }

         this.crc.update(b, offset, length);
         this.count(length);
      }
   }

   private void writeDeflated(byte[] b, int offset, int length) throws IOException {
      if(length > 0 && !this.def.finished()) {
         this.entry.bytesRead = (long)length;
         if(length <= 8192) {
            this.def.setInput(b, offset, length);
            this.deflateUntilInputIsNeeded();
         } else {
            int fullblocks = length / 8192;

            for(int i = 0; i < fullblocks; ++i) {
               this.def.setInput(b, offset + i * 8192, 8192);
               this.deflateUntilInputIsNeeded();
            }

            int done = fullblocks * 8192;
            if(done < length) {
               this.def.setInput(b, offset + done, length - done);
               this.deflateUntilInputIsNeeded();
            }
         }
      }

   }

   public void close() throws IOException {
      if(!this.finished) {
         this.finish();
      }

      this.destroy();
   }

   public void flush() throws IOException {
      if(this.out != null) {
         this.out.flush();
      }

   }

   protected final void deflate() throws IOException {
      int len = this.def.deflate(this.buf, 0, this.buf.length);
      if(len > 0) {
         this.writeOut(this.buf, 0, len);
         this.written += (long)len;
      }

   }

   protected void writeLocalFileHeader(ZipArchiveEntry ze) throws IOException {
      boolean encodable = this.zipEncoding.canEncode(ze.getName());
      ByteBuffer name = this.getName(ze);
      if(this.createUnicodeExtraFields != ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NEVER) {
         this.addUnicodeExtraFields(ze, encodable, name);
      }

      this.offsets.put(ze, Long.valueOf(this.written));
      this.writeOut(LFH_SIG);
      this.written += 4L;
      int zipMethod = ze.getMethod();
      this.writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, !encodable && this.fallbackToUTF8, this.hasZip64Extra(ze));
      this.written += 4L;
      this.writeOut(ZipShort.getBytes(zipMethod));
      this.written += 2L;
      this.writeOut(ZipUtil.toDosTime(ze.getTime()));
      this.written += 4L;
      this.entry.localDataStart = this.written;
      if(zipMethod != 8 && this.raf == null) {
         this.writeOut(ZipLong.getBytes(ze.getCrc()));
         byte[] size = ZipLong.ZIP64_MAGIC.getBytes();
         if(!this.hasZip64Extra(ze)) {
            size = ZipLong.getBytes(ze.getSize());
         }

         this.writeOut(size);
         this.writeOut(size);
      } else {
         this.writeOut(LZERO);
         if(this.hasZip64Extra(this.entry.entry)) {
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
         } else {
            this.writeOut(LZERO);
            this.writeOut(LZERO);
         }
      }

      this.written += 12L;
      this.writeOut(ZipShort.getBytes(name.limit()));
      this.written += 2L;
      byte[] extra = ze.getLocalFileDataExtra();
      this.writeOut(ZipShort.getBytes(extra.length));
      this.written += 2L;
      this.writeOut(name.array(), name.arrayOffset(), name.limit() - name.position());
      this.written += (long)name.limit();
      this.writeOut(extra);
      this.written += (long)extra.length;
      this.entry.dataStart = this.written;
   }

   private void addUnicodeExtraFields(ZipArchiveEntry ze, boolean encodable, ByteBuffer name) throws IOException {
      if(this.createUnicodeExtraFields == ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS || !encodable) {
         ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit() - name.position()));
      }

      String comm = ze.getComment();
      if(comm != null && !"".equals(comm)) {
         boolean commentEncodable = this.zipEncoding.canEncode(comm);
         if(this.createUnicodeExtraFields == ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS || !commentEncodable) {
            ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
            ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position()));
         }
      }

   }

   protected void writeDataDescriptor(ZipArchiveEntry ze) throws IOException {
      if(ze.getMethod() == 8 && this.raf == null) {
         this.writeOut(DD_SIG);
         this.writeOut(ZipLong.getBytes(ze.getCrc()));
         int sizeFieldSize = 4;
         if(!this.hasZip64Extra(ze)) {
            this.writeOut(ZipLong.getBytes(ze.getCompressedSize()));
            this.writeOut(ZipLong.getBytes(ze.getSize()));
         } else {
            sizeFieldSize = 8;
            this.writeOut(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
            this.writeOut(ZipEightByteInteger.getBytes(ze.getSize()));
         }

         this.written += (long)(8 + 2 * sizeFieldSize);
      }
   }

   protected void writeCentralFileHeader(ZipArchiveEntry ze) throws IOException {
      this.writeOut(CFH_SIG);
      this.written += 4L;
      long lfhOffset = ((Long)this.offsets.get(ze)).longValue();
      boolean needsZip64Extra = this.hasZip64Extra(ze) || ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || lfhOffset >= 4294967295L;
      if(needsZip64Extra && this.zip64Mode == Zip64Mode.Never) {
         throw new Zip64RequiredException("archive\'s size exceeds the limit of 4GByte.");
      } else {
         this.handleZip64Extra(ze, lfhOffset, needsZip64Extra);
         this.writeOut(ZipShort.getBytes(ze.getPlatform() << 8 | (!this.hasUsedZip64?20:45)));
         this.written += 2L;
         int zipMethod = ze.getMethod();
         boolean encodable = this.zipEncoding.canEncode(ze.getName());
         this.writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, !encodable && this.fallbackToUTF8, needsZip64Extra);
         this.written += 4L;
         this.writeOut(ZipShort.getBytes(zipMethod));
         this.written += 2L;
         this.writeOut(ZipUtil.toDosTime(ze.getTime()));
         this.written += 4L;
         this.writeOut(ZipLong.getBytes(ze.getCrc()));
         if(ze.getCompressedSize() < 4294967295L && ze.getSize() < 4294967295L) {
            this.writeOut(ZipLong.getBytes(ze.getCompressedSize()));
            this.writeOut(ZipLong.getBytes(ze.getSize()));
         } else {
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            this.writeOut(ZipLong.ZIP64_MAGIC.getBytes());
         }

         this.written += 12L;
         ByteBuffer name = this.getName(ze);
         this.writeOut(ZipShort.getBytes(name.limit()));
         this.written += 2L;
         byte[] extra = ze.getCentralDirectoryExtra();
         this.writeOut(ZipShort.getBytes(extra.length));
         this.written += 2L;
         String comm = ze.getComment();
         if(comm == null) {
            comm = "";
         }

         ByteBuffer commentB = this.getEntryEncoding(ze).encode(comm);
         this.writeOut(ZipShort.getBytes(commentB.limit()));
         this.written += 2L;
         this.writeOut(ZERO);
         this.written += 2L;
         this.writeOut(ZipShort.getBytes(ze.getInternalAttributes()));
         this.written += 2L;
         this.writeOut(ZipLong.getBytes(ze.getExternalAttributes()));
         this.written += 4L;
         this.writeOut(ZipLong.getBytes(Math.min(lfhOffset, 4294967295L)));
         this.written += 4L;
         this.writeOut(name.array(), name.arrayOffset(), name.limit() - name.position());
         this.written += (long)name.limit();
         this.writeOut(extra);
         this.written += (long)extra.length;
         this.writeOut(commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position());
         this.written += (long)commentB.limit();
      }
   }

   private void handleZip64Extra(ZipArchiveEntry ze, long lfhOffset, boolean needsZip64Extra) {
      if(needsZip64Extra) {
         Zip64ExtendedInformationExtraField z64 = this.getZip64Extra(ze);
         if(ze.getCompressedSize() < 4294967295L && ze.getSize() < 4294967295L) {
            z64.setCompressedSize((ZipEightByteInteger)null);
            z64.setSize((ZipEightByteInteger)null);
         } else {
            z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            z64.setSize(new ZipEightByteInteger(ze.getSize()));
         }

         if(lfhOffset >= 4294967295L) {
            z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
         }

         ze.setExtra();
      }

   }

   protected void writeCentralDirectoryEnd() throws IOException {
      this.writeOut(EOCD_SIG);
      this.writeOut(ZERO);
      this.writeOut(ZERO);
      int numberOfEntries = this.entries.size();
      if(numberOfEntries > '\uffff' && this.zip64Mode == Zip64Mode.Never) {
         throw new Zip64RequiredException("archive contains more than 65535 entries.");
      } else if(this.cdOffset > 4294967295L && this.zip64Mode == Zip64Mode.Never) {
         throw new Zip64RequiredException("archive\'s size exceeds the limit of 4GByte.");
      } else {
         byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, '\uffff'));
         this.writeOut(num);
         this.writeOut(num);
         this.writeOut(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
         this.writeOut(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
         ByteBuffer data = this.zipEncoding.encode(this.comment);
         this.writeOut(ZipShort.getBytes(data.limit()));
         this.writeOut(data.array(), data.arrayOffset(), data.limit() - data.position());
      }
   }

   protected void writeZip64CentralDirectory() throws IOException {
      if(this.zip64Mode != Zip64Mode.Never) {
         if(!this.hasUsedZip64 && (this.cdOffset >= 4294967295L || this.cdLength >= 4294967295L || this.entries.size() >= '\uffff')) {
            this.hasUsedZip64 = true;
         }

         if(this.hasUsedZip64) {
            long offset = this.written;
            this.writeOut(ZIP64_EOCD_SIG);
            this.writeOut(ZipEightByteInteger.getBytes(44L));
            this.writeOut(ZipShort.getBytes(45));
            this.writeOut(ZipShort.getBytes(45));
            this.writeOut(LZERO);
            this.writeOut(LZERO);
            byte[] num = ZipEightByteInteger.getBytes((long)this.entries.size());
            this.writeOut(num);
            this.writeOut(num);
            this.writeOut(ZipEightByteInteger.getBytes(this.cdLength));
            this.writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
            this.writeOut(ZIP64_EOCD_LOC_SIG);
            this.writeOut(LZERO);
            this.writeOut(ZipEightByteInteger.getBytes(offset));
            this.writeOut(ONE);
         }
      }
   }

   protected final void writeOut(byte[] data) throws IOException {
      this.writeOut(data, 0, data.length);
   }

   protected final void writeOut(byte[] data, int offset, int length) throws IOException {
      if(this.raf != null) {
         this.raf.write(data, offset, length);
      } else {
         this.out.write(data, offset, length);
      }

   }

   private void deflateUntilInputIsNeeded() throws IOException {
      while(!this.def.needsInput()) {
         this.deflate();
      }

   }

   private void writeVersionNeededToExtractAndGeneralPurposeBits(int zipMethod, boolean utfFallback, boolean zip64) throws IOException {
      int versionNeededToExtract = 10;
      GeneralPurposeBit b = new GeneralPurposeBit();
      b.useUTF8ForNames(this.useUTF8Flag || utfFallback);
      if(zipMethod == 8 && this.raf == null) {
         versionNeededToExtract = 20;
         b.useDataDescriptor(true);
      }

      if(zip64) {
         versionNeededToExtract = 45;
      }

      this.writeOut(ZipShort.getBytes(versionNeededToExtract));
      this.writeOut(b.encode());
   }

   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         return new ZipArchiveEntry(inputFile, entryName);
      }
   }

   private Zip64ExtendedInformationExtraField getZip64Extra(ZipArchiveEntry ze) {
      if(this.entry != null) {
         this.entry.causedUseOfZip64 = !this.hasUsedZip64;
      }

      this.hasUsedZip64 = true;
      Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
      if(z64 == null) {
         z64 = new Zip64ExtendedInformationExtraField();
      }

      ze.addAsFirstExtraField(z64);
      return z64;
   }

   private boolean hasZip64Extra(ZipArchiveEntry ze) {
      return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
   }

   private Zip64Mode getEffectiveZip64Mode(ZipArchiveEntry ze) {
      return this.zip64Mode == Zip64Mode.AsNeeded && this.raf == null && ze.getMethod() == 8 && ze.getSize() == -1L?Zip64Mode.Never:this.zip64Mode;
   }

   private ZipEncoding getEntryEncoding(ZipArchiveEntry ze) {
      boolean encodable = this.zipEncoding.canEncode(ze.getName());
      return !encodable && this.fallbackToUTF8?ZipEncodingHelper.UTF8_ZIP_ENCODING:this.zipEncoding;
   }

   private ByteBuffer getName(ZipArchiveEntry ze) throws IOException {
      return this.getEntryEncoding(ze).encode(ze.getName());
   }

   void destroy() throws IOException {
      if(this.raf != null) {
         this.raf.close();
      }

      if(this.out != null) {
         this.out.close();
      }

   }

   private static final class CurrentEntry {
      private final ZipArchiveEntry entry;
      private long localDataStart;
      private long dataStart;
      private long bytesRead;
      private boolean causedUseOfZip64;
      private boolean hasWritten;

      private CurrentEntry(ZipArchiveEntry entry) {
         this.localDataStart = 0L;
         this.dataStart = 0L;
         this.bytesRead = 0L;
         this.causedUseOfZip64 = false;
         this.entry = entry;
      }
   }

   public static final class UnicodeExtraFieldPolicy {
      public static final ZipArchiveOutputStream.UnicodeExtraFieldPolicy ALWAYS = new ZipArchiveOutputStream.UnicodeExtraFieldPolicy("always");
      public static final ZipArchiveOutputStream.UnicodeExtraFieldPolicy NEVER = new ZipArchiveOutputStream.UnicodeExtraFieldPolicy("never");
      public static final ZipArchiveOutputStream.UnicodeExtraFieldPolicy NOT_ENCODEABLE = new ZipArchiveOutputStream.UnicodeExtraFieldPolicy("not encodeable");
      private final String name;

      private UnicodeExtraFieldPolicy(String n) {
         this.name = n;
      }

      public String toString() {
         return this.name;
      }
   }
}
