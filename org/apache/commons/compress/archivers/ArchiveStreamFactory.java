package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.StreamingNotSupportedException;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ArchiveStreamFactory {
   public static final String AR = "ar";
   public static final String ARJ = "arj";
   public static final String CPIO = "cpio";
   public static final String DUMP = "dump";
   public static final String JAR = "jar";
   public static final String TAR = "tar";
   public static final String ZIP = "zip";
   public static final String SEVEN_Z = "7z";
   private String entryEncoding = null;

   public String getEntryEncoding() {
      return this.entryEncoding;
   }

   public void setEntryEncoding(String entryEncoding) {
      this.entryEncoding = entryEncoding;
   }

   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
      if(archiverName == null) {
         throw new IllegalArgumentException("Archivername must not be null.");
      } else if(in == null) {
         throw new IllegalArgumentException("InputStream must not be null.");
      } else if("ar".equalsIgnoreCase(archiverName)) {
         return new ArArchiveInputStream(in);
      } else if("arj".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new ArjArchiveInputStream(in, this.entryEncoding):new ArjArchiveInputStream(in);
      } else if("zip".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new ZipArchiveInputStream(in, this.entryEncoding):new ZipArchiveInputStream(in);
      } else if("tar".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new TarArchiveInputStream(in, this.entryEncoding):new TarArchiveInputStream(in);
      } else if("jar".equalsIgnoreCase(archiverName)) {
         return new JarArchiveInputStream(in);
      } else if("cpio".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new CpioArchiveInputStream(in, this.entryEncoding):new CpioArchiveInputStream(in);
      } else if("dump".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new DumpArchiveInputStream(in, this.entryEncoding):new DumpArchiveInputStream(in);
      } else if("7z".equalsIgnoreCase(archiverName)) {
         throw new StreamingNotSupportedException("7z");
      } else {
         throw new ArchiveException("Archiver: " + archiverName + " not found.");
      }
   }

   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
      if(archiverName == null) {
         throw new IllegalArgumentException("Archivername must not be null.");
      } else if(out == null) {
         throw new IllegalArgumentException("OutputStream must not be null.");
      } else if("ar".equalsIgnoreCase(archiverName)) {
         return new ArArchiveOutputStream(out);
      } else if("zip".equalsIgnoreCase(archiverName)) {
         ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
         if(this.entryEncoding != null) {
            zip.setEncoding(this.entryEncoding);
         }

         return zip;
      } else if("tar".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new TarArchiveOutputStream(out, this.entryEncoding):new TarArchiveOutputStream(out);
      } else if("jar".equalsIgnoreCase(archiverName)) {
         return new JarArchiveOutputStream(out);
      } else if("cpio".equalsIgnoreCase(archiverName)) {
         return this.entryEncoding != null?new CpioArchiveOutputStream(out, this.entryEncoding):new CpioArchiveOutputStream(out);
      } else if("7z".equalsIgnoreCase(archiverName)) {
         throw new StreamingNotSupportedException("7z");
      } else {
         throw new ArchiveException("Archiver: " + archiverName + " not found.");
      }
   }

   public ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
      if(in == null) {
         throw new IllegalArgumentException("Stream must not be null.");
      } else if(!in.markSupported()) {
         throw new IllegalArgumentException("Mark is not supported.");
      } else {
         byte[] signature = new byte[12];
         in.mark(signature.length);

         try {
            int signatureLength = IOUtils.readFully(in, signature);
            in.reset();
            if(ZipArchiveInputStream.matches(signature, signatureLength)) {
               if(this.entryEncoding != null) {
                  return new ZipArchiveInputStream(in, this.entryEncoding);
               }

               return new ZipArchiveInputStream(in);
            }

            if(JarArchiveInputStream.matches(signature, signatureLength)) {
               return new JarArchiveInputStream(in);
            }

            if(ArArchiveInputStream.matches(signature, signatureLength)) {
               return new ArArchiveInputStream(in);
            }

            if(CpioArchiveInputStream.matches(signature, signatureLength)) {
               return new CpioArchiveInputStream(in);
            }

            if(ArjArchiveInputStream.matches(signature, signatureLength)) {
               return new ArjArchiveInputStream(in);
            }

            if(SevenZFile.matches(signature, signatureLength)) {
               throw new StreamingNotSupportedException("7z");
            }

            byte[] dumpsig = new byte[32];
            in.mark(dumpsig.length);
            signatureLength = IOUtils.readFully(in, dumpsig);
            in.reset();
            if(DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
               return new DumpArchiveInputStream(in);
            }

            byte[] tarheader = new byte[512];
            in.mark(tarheader.length);
            signatureLength = IOUtils.readFully(in, tarheader);
            in.reset();
            if(TarArchiveInputStream.matches(tarheader, signatureLength)) {
               if(this.entryEncoding != null) {
                  return new TarArchiveInputStream(in, this.entryEncoding);
               }

               return new TarArchiveInputStream(in);
            }

            if(signatureLength >= 512) {
               TarArchiveInputStream tais = null;

               TarArchiveInputStream e;
               try {
                  tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
                  if(!tais.getNextTarEntry().isCheckSumOK()) {
                     throw new ArchiveException("No Archiver found for the stream signature");
                  }

                  e = new TarArchiveInputStream(in);
               } catch (Exception var12) {
                  throw new ArchiveException("No Archiver found for the stream signature");
               } finally {
                  IOUtils.closeQuietly(tais);
               }

               return e;
            }
         } catch (IOException var14) {
            throw new ArchiveException("Could not use reset and mark operations.", var14);
         }

         throw new ArchiveException("No Archiver found for the stream signature");
      }
   }
}
