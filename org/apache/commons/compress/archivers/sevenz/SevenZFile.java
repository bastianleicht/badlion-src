package org.apache.commons.compress.archivers.sevenz;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.sevenz.Archive;
import org.apache.commons.compress.archivers.sevenz.BindPair;
import org.apache.commons.compress.archivers.sevenz.BoundedRandomAccessFileInputStream;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.Coders;
import org.apache.commons.compress.archivers.sevenz.Folder;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration;
import org.apache.commons.compress.archivers.sevenz.StartHeader;
import org.apache.commons.compress.archivers.sevenz.StreamMap;
import org.apache.commons.compress.archivers.sevenz.SubStreamsInfo;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class SevenZFile implements Closeable {
   static final int SIGNATURE_HEADER_SIZE = 32;
   private RandomAccessFile file;
   private final Archive archive;
   private int currentEntryIndex;
   private int currentFolderIndex;
   private InputStream currentFolderInputStream;
   private InputStream currentEntryInputStream;
   private byte[] password;
   static final byte[] sevenZSignature = new byte[]{(byte)55, (byte)122, (byte)-68, (byte)-81, (byte)39, (byte)28};

   public SevenZFile(File filename, byte[] password) throws IOException {
      this.currentEntryIndex = -1;
      this.currentFolderIndex = -1;
      this.currentFolderInputStream = null;
      this.currentEntryInputStream = null;
      boolean succeeded = false;
      this.file = new RandomAccessFile(filename, "r");

      try {
         this.archive = this.readHeaders(password);
         if(password != null) {
            this.password = new byte[password.length];
            System.arraycopy(password, 0, this.password, 0, password.length);
         } else {
            this.password = null;
         }

         succeeded = true;
      } finally {
         if(!succeeded) {
            this.file.close();
         }

      }

   }

   public SevenZFile(File filename) throws IOException {
      this(filename, (byte[])null);
   }

   public void close() throws IOException {
      if(this.file != null) {
         try {
            this.file.close();
         } finally {
            this.file = null;
            if(this.password != null) {
               Arrays.fill(this.password, (byte)0);
            }

            this.password = null;
         }
      }

   }

   public SevenZArchiveEntry getNextEntry() throws IOException {
      if(this.currentEntryIndex >= this.archive.files.length - 1) {
         return null;
      } else {
         ++this.currentEntryIndex;
         SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
         this.buildDecodingStream();
         return entry;
      }
   }

   private Archive readHeaders(byte[] password) throws IOException {
      byte[] signature = new byte[6];
      this.file.readFully(signature);
      if(!Arrays.equals(signature, sevenZSignature)) {
         throw new IOException("Bad 7z signature");
      } else {
         byte archiveVersionMajor = this.file.readByte();
         byte archiveVersionMinor = this.file.readByte();
         if(archiveVersionMajor != 0) {
            throw new IOException(String.format("Unsupported 7z version (%d,%d)", new Object[]{Byte.valueOf(archiveVersionMajor), Byte.valueOf(archiveVersionMinor)}));
         } else {
            long startHeaderCrc = 4294967295L & (long)Integer.reverseBytes(this.file.readInt());
            StartHeader startHeader = this.readStartHeader(startHeaderCrc);
            int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
            if((long)nextHeaderSizeInt != startHeader.nextHeaderSize) {
               throw new IOException("cannot handle nextHeaderSize " + startHeader.nextHeaderSize);
            } else {
               this.file.seek(32L + startHeader.nextHeaderOffset);
               byte[] nextHeader = new byte[nextHeaderSizeInt];
               this.file.readFully(nextHeader);
               CRC32 crc = new CRC32();
               crc.update(nextHeader);
               if(startHeader.nextHeaderCrc != crc.getValue()) {
                  throw new IOException("NextHeader CRC mismatch");
               } else {
                  ByteArrayInputStream byteStream = new ByteArrayInputStream(nextHeader);
                  DataInputStream nextHeaderInputStream = new DataInputStream(byteStream);
                  Archive archive = new Archive();
                  int nid = nextHeaderInputStream.readUnsignedByte();
                  if(nid == 23) {
                     nextHeaderInputStream = this.readEncodedHeader(nextHeaderInputStream, archive, password);
                     archive = new Archive();
                     nid = nextHeaderInputStream.readUnsignedByte();
                  }

                  if(nid == 1) {
                     this.readHeader(nextHeaderInputStream, archive);
                     nextHeaderInputStream.close();
                     return archive;
                  } else {
                     throw new IOException("Broken or unsupported archive: no Header");
                  }
               }
            }
         }
      }
   }

   private StartHeader readStartHeader(long startHeaderCrc) throws IOException {
      StartHeader startHeader = new StartHeader();
      DataInputStream dataInputStream = null;

      StartHeader var5;
      try {
         dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedRandomAccessFileInputStream(this.file, 20L), 20L, startHeaderCrc));
         startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
         startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
         startHeader.nextHeaderCrc = 4294967295L & (long)Integer.reverseBytes(dataInputStream.readInt());
         var5 = startHeader;
      } finally {
         if(dataInputStream != null) {
            dataInputStream.close();
         }

      }

      return var5;
   }

   private void readHeader(DataInput header, Archive archive) throws IOException {
      int nid = header.readUnsignedByte();
      if(nid == 2) {
         this.readArchiveProperties(header);
         nid = header.readUnsignedByte();
      }

      if(nid == 3) {
         throw new IOException("Additional streams unsupported");
      } else {
         if(nid == 4) {
            this.readStreamsInfo(header, archive);
            nid = header.readUnsignedByte();
         }

         if(nid == 5) {
            this.readFilesInfo(header, archive);
            nid = header.readUnsignedByte();
         }

         if(nid != 0) {
            throw new IOException("Badly terminated header");
         }
      }
   }

   private void readArchiveProperties(DataInput input) throws IOException {
      for(int nid = input.readUnsignedByte(); nid != 0; nid = input.readUnsignedByte()) {
         long propertySize = readUint64(input);
         byte[] property = new byte[(int)propertySize];
         input.readFully(property);
      }

   }

   private DataInputStream readEncodedHeader(DataInputStream header, Archive archive, byte[] password) throws IOException {
      this.readStreamsInfo(header, archive);
      Folder folder = archive.folders[0];
      int firstPackStreamIndex = 0;
      long folderOffset = 32L + archive.packPos + 0L;
      this.file.seek(folderOffset);
      InputStream inputStreamStack = new BoundedRandomAccessFileInputStream(this.file, archive.packSizes[0]);

      for(Coder coder : folder.getOrderedCoders()) {
         if(coder.numInStreams != 1L || coder.numOutStreams != 1L) {
            throw new IOException("Multi input/output stream coders are not yet supported");
         }

         inputStreamStack = Coders.addDecoder(inputStreamStack, coder, password);
      }

      if(folder.hasCrc) {
         inputStreamStack = new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
      }

      byte[] nextHeader = new byte[(int)folder.getUnpackSize()];
      DataInputStream nextHeaderInputStream = new DataInputStream(inputStreamStack);

      try {
         nextHeaderInputStream.readFully(nextHeader);
      } finally {
         nextHeaderInputStream.close();
      }

      return new DataInputStream(new ByteArrayInputStream(nextHeader));
   }

   private void readStreamsInfo(DataInput header, Archive archive) throws IOException {
      int nid = header.readUnsignedByte();
      if(nid == 6) {
         this.readPackInfo(header, archive);
         nid = header.readUnsignedByte();
      }

      if(nid == 7) {
         this.readUnpackInfo(header, archive);
         nid = header.readUnsignedByte();
      } else {
         archive.folders = new Folder[0];
      }

      if(nid == 8) {
         this.readSubStreamsInfo(header, archive);
         nid = header.readUnsignedByte();
      }

      if(nid != 0) {
         throw new IOException("Badly terminated StreamsInfo");
      }
   }

   private void readPackInfo(DataInput header, Archive archive) throws IOException {
      archive.packPos = readUint64(header);
      long numPackStreams = readUint64(header);
      int nid = header.readUnsignedByte();
      if(nid == 9) {
         archive.packSizes = new long[(int)numPackStreams];

         for(int i = 0; i < archive.packSizes.length; ++i) {
            archive.packSizes[i] = readUint64(header);
         }

         nid = header.readUnsignedByte();
      }

      if(nid == 10) {
         archive.packCrcsDefined = this.readAllOrBits(header, (int)numPackStreams);
         archive.packCrcs = new long[(int)numPackStreams];

         for(int i = 0; i < (int)numPackStreams; ++i) {
            if(archive.packCrcsDefined.get(i)) {
               archive.packCrcs[i] = 4294967295L & (long)Integer.reverseBytes(header.readInt());
            }
         }

         nid = header.readUnsignedByte();
      }

      if(nid != 0) {
         throw new IOException("Badly terminated PackInfo (" + nid + ")");
      }
   }

   private void readUnpackInfo(DataInput header, Archive archive) throws IOException {
      int nid = header.readUnsignedByte();
      if(nid != 11) {
         throw new IOException("Expected kFolder, got " + nid);
      } else {
         long numFolders = readUint64(header);
         Folder[] folders = new Folder[(int)numFolders];
         archive.folders = folders;
         int external = header.readUnsignedByte();
         if(external != 0) {
            throw new IOException("External unsupported");
         } else {
            for(int i = 0; i < (int)numFolders; ++i) {
               folders[i] = this.readFolder(header);
            }

            nid = header.readUnsignedByte();
            if(nid != 12) {
               throw new IOException("Expected kCodersUnpackSize, got " + nid);
            } else {
               for(Folder folder : folders) {
                  folder.unpackSizes = new long[(int)folder.totalOutputStreams];

                  for(int i = 0; (long)i < folder.totalOutputStreams; ++i) {
                     folder.unpackSizes[i] = readUint64(header);
                  }
               }

               nid = header.readUnsignedByte();
               if(nid == 10) {
                  BitSet crcsDefined = this.readAllOrBits(header, (int)numFolders);

                  for(int i = 0; i < (int)numFolders; ++i) {
                     if(crcsDefined.get(i)) {
                        folders[i].hasCrc = true;
                        folders[i].crc = 4294967295L & (long)Integer.reverseBytes(header.readInt());
                     } else {
                        folders[i].hasCrc = false;
                     }
                  }

                  nid = header.readUnsignedByte();
               }

               if(nid != 0) {
                  throw new IOException("Badly terminated UnpackInfo");
               }
            }
         }
      }
   }

   private void readSubStreamsInfo(DataInput header, Archive archive) throws IOException {
      for(Folder folder : archive.folders) {
         folder.numUnpackSubStreams = 1;
      }

      int totalUnpackStreams = archive.folders.length;
      int nid = header.readUnsignedByte();
      if(nid == 13) {
         totalUnpackStreams = 0;

         for(Folder folder : archive.folders) {
            long numStreams = readUint64(header);
            folder.numUnpackSubStreams = (int)numStreams;
            totalUnpackStreams = (int)((long)totalUnpackStreams + numStreams);
         }

         nid = header.readUnsignedByte();
      }

      SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
      subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
      subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
      subStreamsInfo.crcs = new long[totalUnpackStreams];
      int nextUnpackStream = 0;

      for(Folder folder : archive.folders) {
         if(folder.numUnpackSubStreams != 0) {
            long sum = 0L;
            if(nid == 9) {
               for(int i = 0; i < folder.numUnpackSubStreams - 1; ++i) {
                  long size = readUint64(header);
                  subStreamsInfo.unpackSizes[nextUnpackStream++] = size;
                  sum += size;
               }
            }

            subStreamsInfo.unpackSizes[nextUnpackStream++] = folder.getUnpackSize() - sum;
         }
      }

      if(nid == 9) {
         nid = header.readUnsignedByte();
      }

      int numDigests = 0;

      for(Folder folder : archive.folders) {
         if(folder.numUnpackSubStreams != 1 || !folder.hasCrc) {
            numDigests += folder.numUnpackSubStreams;
         }
      }

      if(nid == 10) {
         BitSet hasMissingCrc = this.readAllOrBits(header, numDigests);
         long[] missingCrcs = new long[numDigests];

         for(int i = 0; i < numDigests; ++i) {
            if(hasMissingCrc.get(i)) {
               missingCrcs[i] = 4294967295L & (long)Integer.reverseBytes(header.readInt());
            }
         }

         int nextCrc = 0;
         int nextMissingCrc = 0;

         for(Folder folder : archive.folders) {
            if(folder.numUnpackSubStreams == 1 && folder.hasCrc) {
               subStreamsInfo.hasCrc.set(nextCrc, true);
               subStreamsInfo.crcs[nextCrc] = folder.crc;
               ++nextCrc;
            } else {
               for(int i = 0; i < folder.numUnpackSubStreams; ++i) {
                  subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                  subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                  ++nextCrc;
                  ++nextMissingCrc;
               }
            }
         }

         nid = header.readUnsignedByte();
      }

      if(nid != 0) {
         throw new IOException("Badly terminated SubStreamsInfo");
      } else {
         archive.subStreamsInfo = subStreamsInfo;
      }
   }

   private Folder readFolder(DataInput header) throws IOException {
      Folder folder = new Folder();
      long numCoders = readUint64(header);
      Coder[] coders = new Coder[(int)numCoders];
      long totalInStreams = 0L;
      long totalOutStreams = 0L;

      for(int i = 0; i < coders.length; ++i) {
         coders[i] = new Coder();
         int bits = header.readUnsignedByte();
         int idSize = bits & 15;
         boolean isSimple = (bits & 16) == 0;
         boolean hasAttributes = (bits & 32) != 0;
         boolean moreAlternativeMethods = (bits & 128) != 0;
         coders[i].decompressionMethodId = new byte[idSize];
         header.readFully(coders[i].decompressionMethodId);
         if(isSimple) {
            coders[i].numInStreams = 1L;
            coders[i].numOutStreams = 1L;
         } else {
            coders[i].numInStreams = readUint64(header);
            coders[i].numOutStreams = readUint64(header);
         }

         totalInStreams += coders[i].numInStreams;
         totalOutStreams += coders[i].numOutStreams;
         if(hasAttributes) {
            long propertiesSize = readUint64(header);
            coders[i].properties = new byte[(int)propertiesSize];
            header.readFully(coders[i].properties);
         }

         if(moreAlternativeMethods) {
            throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn\'t support them either.");
         }
      }

      folder.coders = coders;
      folder.totalInputStreams = totalInStreams;
      folder.totalOutputStreams = totalOutStreams;
      if(totalOutStreams == 0L) {
         throw new IOException("Total output streams can\'t be 0");
      } else {
         long numBindPairs = totalOutStreams - 1L;
         BindPair[] bindPairs = new BindPair[(int)numBindPairs];

         for(int i = 0; i < bindPairs.length; ++i) {
            bindPairs[i] = new BindPair();
            bindPairs[i].inIndex = readUint64(header);
            bindPairs[i].outIndex = readUint64(header);
         }

         folder.bindPairs = bindPairs;
         if(totalInStreams < numBindPairs) {
            throw new IOException("Total input streams can\'t be less than the number of bind pairs");
         } else {
            long numPackedStreams = totalInStreams - numBindPairs;
            long[] packedStreams = new long[(int)numPackedStreams];
            if(numPackedStreams == 1L) {
               int i;
               for(i = 0; i < (int)totalInStreams && folder.findBindPairForInStream(i) >= 0; ++i) {
                  ;
               }

               if(i == (int)totalInStreams) {
                  throw new IOException("Couldn\'t find stream\'s bind pair index");
               }

               packedStreams[0] = (long)i;
            } else {
               for(int i = 0; i < (int)numPackedStreams; ++i) {
                  packedStreams[i] = readUint64(header);
               }
            }

            folder.packedStreams = packedStreams;
            return folder;
         }
      }
   }

   private BitSet readAllOrBits(DataInput header, int size) throws IOException {
      int areAllDefined = header.readUnsignedByte();
      BitSet bits;
      if(areAllDefined != 0) {
         bits = new BitSet(size);

         for(int i = 0; i < size; ++i) {
            bits.set(i, true);
         }
      } else {
         bits = this.readBits(header, size);
      }

      return bits;
   }

   private BitSet readBits(DataInput header, int size) throws IOException {
      BitSet bits = new BitSet(size);
      int mask = 0;
      int cache = 0;

      for(int i = 0; i < size; ++i) {
         if(mask == 0) {
            mask = 128;
            cache = header.readUnsignedByte();
         }

         bits.set(i, (cache & mask) != 0);
         mask >>>= 1;
      }

      return bits;
   }

   private void readFilesInfo(DataInput header, Archive archive) throws IOException {
      long numFiles = readUint64(header);
      SevenZArchiveEntry[] files = new SevenZArchiveEntry[(int)numFiles];

      for(int i = 0; i < files.length; ++i) {
         files[i] = new SevenZArchiveEntry();
      }

      BitSet isEmptyStream = null;
      BitSet isEmptyFile = null;
      BitSet isAnti = null;

      label50:
      while(true) {
         int propertyType = header.readUnsignedByte();
         if(propertyType == 0) {
            propertyType = 0;
            int emptyFileCounter = 0;

            for(int i = 0; i < files.length; ++i) {
               files[i].setHasStream(isEmptyStream == null?true:!isEmptyStream.get(i));
               if(files[i].hasStream()) {
                  files[i].setDirectory(false);
                  files[i].setAntiItem(false);
                  files[i].setHasCrc(archive.subStreamsInfo.hasCrc.get(propertyType));
                  files[i].setCrcValue(archive.subStreamsInfo.crcs[propertyType]);
                  files[i].setSize(archive.subStreamsInfo.unpackSizes[propertyType]);
                  ++propertyType;
               } else {
                  files[i].setDirectory(isEmptyFile == null?true:!isEmptyFile.get(emptyFileCounter));
                  files[i].setAntiItem(isAnti == null?false:isAnti.get(emptyFileCounter));
                  files[i].setHasCrc(false);
                  files[i].setSize(0L);
                  ++emptyFileCounter;
               }
            }

            archive.files = files;
            this.calculateStreamMap(archive);
            return;
         }

         long size = readUint64(header);
         switch(propertyType) {
         case 14:
            isEmptyStream = this.readBits(header, files.length);
            break;
         case 15:
            if(isEmptyStream == null) {
               throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
            }

            isEmptyFile = this.readBits(header, isEmptyStream.cardinality());
            break;
         case 16:
            if(isEmptyStream == null) {
               throw new IOException("Header format error: kEmptyStream must appear before kAnti");
            }

            isAnti = this.readBits(header, isEmptyStream.cardinality());
            break;
         case 17:
            int external = header.readUnsignedByte();
            if(external != 0) {
               throw new IOException("Not implemented");
            }

            if((size - 1L & 1L) != 0L) {
               throw new IOException("File names length invalid");
            }

            byte[] names = new byte[(int)(size - 1L)];
            header.readFully(names);
            int nextFile = 0;
            int nextName = 0;
            int i = 0;

            for(; i < names.length; i += 2) {
               if(names[i] == 0 && names[i + 1] == 0) {
                  files[nextFile++].setName(new String(names, nextName, i - nextName, "UTF-16LE"));
                  nextName = i + 2;
               }
            }

            if(nextName != names.length || nextFile != files.length) {
               throw new IOException("Error parsing file names");
            }
            break;
         case 18:
            BitSet timesDefined = this.readAllOrBits(header, files.length);
            int external = header.readUnsignedByte();
            if(external != 0) {
               throw new IOException("Unimplemented");
            }

            int i = 0;

            while(true) {
               if(i >= files.length) {
                  continue label50;
               }

               files[i].setHasCreationDate(timesDefined.get(i));
               if(files[i].getHasCreationDate()) {
                  files[i].setCreationDate(Long.reverseBytes(header.readLong()));
               }

               ++i;
            }
         case 19:
            BitSet timesDefined = this.readAllOrBits(header, files.length);
            int external = header.readUnsignedByte();
            if(external != 0) {
               throw new IOException("Unimplemented");
            }

            int i = 0;

            while(true) {
               if(i >= files.length) {
                  continue label50;
               }

               files[i].setHasAccessDate(timesDefined.get(i));
               if(files[i].getHasAccessDate()) {
                  files[i].setAccessDate(Long.reverseBytes(header.readLong()));
               }

               ++i;
            }
         case 20:
            BitSet timesDefined = this.readAllOrBits(header, files.length);
            int external = header.readUnsignedByte();
            if(external != 0) {
               throw new IOException("Unimplemented");
            }

            int i = 0;

            while(true) {
               if(i >= files.length) {
                  continue label50;
               }

               files[i].setHasLastModifiedDate(timesDefined.get(i));
               if(files[i].getHasLastModifiedDate()) {
                  files[i].setLastModifiedDate(Long.reverseBytes(header.readLong()));
               }

               ++i;
            }
         case 21:
            BitSet attributesDefined = this.readAllOrBits(header, files.length);
            int external = header.readUnsignedByte();
            if(external != 0) {
               throw new IOException("Unimplemented");
            }

            int i = 0;

            while(true) {
               if(i >= files.length) {
                  continue label50;
               }

               files[i].setHasWindowsAttributes(attributesDefined.get(i));
               if(files[i].getHasWindowsAttributes()) {
                  files[i].setWindowsAttributes(Integer.reverseBytes(header.readInt()));
               }

               ++i;
            }
         case 22:
         case 23:
         default:
            throw new IOException("Unknown property " + propertyType);
         case 24:
            throw new IOException("kStartPos is unsupported, please report");
         case 25:
            throw new IOException("kDummy is unsupported, please report");
         }
      }
   }

   private void calculateStreamMap(Archive archive) throws IOException {
      StreamMap streamMap = new StreamMap();
      int nextFolderPackStreamIndex = 0;
      int numFolders = archive.folders != null?archive.folders.length:0;
      streamMap.folderFirstPackStreamIndex = new int[numFolders];

      for(int i = 0; i < numFolders; ++i) {
         streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
         nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
      }

      long nextPackStreamOffset = 0L;
      int numPackSizes = archive.packSizes != null?archive.packSizes.length:0;
      streamMap.packStreamOffsets = new long[numPackSizes];

      for(int i = 0; i < numPackSizes; ++i) {
         streamMap.packStreamOffsets[i] = nextPackStreamOffset;
         nextPackStreamOffset += archive.packSizes[i];
      }

      streamMap.folderFirstFileIndex = new int[numFolders];
      streamMap.fileFolderIndex = new int[archive.files.length];
      int nextFolderIndex = 0;
      int nextFolderUnpackStreamIndex = 0;

      for(int i = 0; i < archive.files.length; ++i) {
         if(!archive.files[i].hasStream() && nextFolderUnpackStreamIndex == 0) {
            streamMap.fileFolderIndex[i] = -1;
         } else {
            if(nextFolderUnpackStreamIndex == 0) {
               while(nextFolderIndex < archive.folders.length) {
                  streamMap.folderFirstFileIndex[nextFolderIndex] = i;
                  if(archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                     break;
                  }

                  ++nextFolderIndex;
               }

               if(nextFolderIndex >= archive.folders.length) {
                  throw new IOException("Too few folders in archive");
               }
            }

            streamMap.fileFolderIndex[i] = nextFolderIndex;
            if(archive.files[i].hasStream()) {
               ++nextFolderUnpackStreamIndex;
               if(nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams) {
                  ++nextFolderIndex;
                  nextFolderUnpackStreamIndex = 0;
               }
            }
         }
      }

      archive.streamMap = streamMap;
   }

   private void buildDecodingStream() throws IOException {
      int folderIndex = this.archive.streamMap.fileFolderIndex[this.currentEntryIndex];
      if(folderIndex < 0) {
         this.currentEntryInputStream = new BoundedInputStream(new ByteArrayInputStream(new byte[0]), 0L);
      } else {
         SevenZArchiveEntry file = this.archive.files[this.currentEntryIndex];
         if(this.currentFolderIndex == folderIndex) {
            this.drainPreviousEntry();
            file.setContentMethods(this.archive.files[this.currentEntryIndex - 1].getContentMethods());
         } else {
            this.currentFolderIndex = folderIndex;
            if(this.currentFolderInputStream != null) {
               this.currentFolderInputStream.close();
               this.currentFolderInputStream = null;
            }

            Folder folder = this.archive.folders[folderIndex];
            int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
            long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
            this.currentFolderInputStream = this.buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
         }

         InputStream fileStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
         if(file.getHasCrc()) {
            this.currentEntryInputStream = new CRC32VerifyingInputStream(fileStream, file.getSize(), file.getCrcValue());
         } else {
            this.currentEntryInputStream = fileStream;
         }

      }
   }

   private void drainPreviousEntry() throws IOException {
      if(this.currentEntryInputStream != null) {
         IOUtils.skip(this.currentEntryInputStream, Long.MAX_VALUE);
         this.currentEntryInputStream.close();
         this.currentEntryInputStream = null;
      }

   }

   private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry) throws IOException {
      this.file.seek(folderOffset);
      InputStream inputStreamStack = new BoundedRandomAccessFileInputStream(this.file, this.archive.packSizes[firstPackStreamIndex]);
      LinkedList<SevenZMethodConfiguration> methods = new LinkedList();

      for(Coder coder : folder.getOrderedCoders()) {
         if(coder.numInStreams != 1L || coder.numOutStreams != 1L) {
            throw new IOException("Multi input/output stream coders are not yet supported");
         }

         SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
         inputStreamStack = Coders.addDecoder(inputStreamStack, coder, this.password);
         methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
      }

      entry.setContentMethods(methods);
      if(folder.hasCrc) {
         return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
      } else {
         return inputStreamStack;
      }
   }

   public int read() throws IOException {
      if(this.currentEntryInputStream == null) {
         throw new IllegalStateException("No current 7z entry");
      } else {
         return this.currentEntryInputStream.read();
      }
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if(this.currentEntryInputStream == null) {
         throw new IllegalStateException("No current 7z entry");
      } else {
         return this.currentEntryInputStream.read(b, off, len);
      }
   }

   private static long readUint64(DataInput in) throws IOException {
      long firstByte = (long)in.readUnsignedByte();
      int mask = 128;
      long value = 0L;

      for(int i = 0; i < 8; ++i) {
         if((firstByte & (long)mask) == 0L) {
            return value | (firstByte & (long)(mask - 1)) << 8 * i;
         }

         long nextByte = (long)in.readUnsignedByte();
         value |= nextByte << 8 * i;
         mask >>>= 1;
      }

      return value;
   }

   public static boolean matches(byte[] signature, int length) {
      if(length < sevenZSignature.length) {
         return false;
      } else {
         for(int i = 0; i < sevenZSignature.length; ++i) {
            if(signature[i] != sevenZSignature[i]) {
               return false;
            }
         }

         return true;
      }
   }
}
