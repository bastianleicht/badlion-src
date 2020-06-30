package org.apache.commons.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.NullOutputStream;

public class FileUtils {
   public static final long ONE_KB = 1024L;
   public static final BigInteger ONE_KB_BI = BigInteger.valueOf(1024L);
   public static final long ONE_MB = 1048576L;
   public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
   private static final long FILE_COPY_BUFFER_SIZE = 31457280L;
   public static final long ONE_GB = 1073741824L;
   public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
   public static final long ONE_TB = 1099511627776L;
   public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
   public static final long ONE_PB = 1125899906842624L;
   public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
   public static final long ONE_EB = 1152921504606846976L;
   public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);
   public static final BigInteger ONE_ZB = BigInteger.valueOf(1024L).multiply(BigInteger.valueOf(1152921504606846976L));
   public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);
   public static final File[] EMPTY_FILE_ARRAY = new File[0];
   private static final Charset UTF8 = Charset.forName("UTF-8");

   public static File getFile(File directory, String... names) {
      if(directory == null) {
         throw new NullPointerException("directorydirectory must not be null");
      } else if(names == null) {
         throw new NullPointerException("names must not be null");
      } else {
         File file = directory;

         for(String name : names) {
            file = new File(file, name);
         }

         return file;
      }
   }

   public static File getFile(String... names) {
      if(names == null) {
         throw new NullPointerException("names must not be null");
      } else {
         File file = null;

         for(String name : names) {
            if(file == null) {
               file = new File(name);
            } else {
               file = new File(file, name);
            }
         }

         return file;
      }
   }

   public static String getTempDirectoryPath() {
      return System.getProperty("java.io.tmpdir");
   }

   public static File getTempDirectory() {
      return new File(getTempDirectoryPath());
   }

   public static String getUserDirectoryPath() {
      return System.getProperty("user.home");
   }

   public static File getUserDirectory() {
      return new File(getUserDirectoryPath());
   }

   public static FileInputStream openInputStream(File file) throws IOException {
      if(file.exists()) {
         if(file.isDirectory()) {
            throw new IOException("File \'" + file + "\' exists but is a directory");
         } else if(!file.canRead()) {
            throw new IOException("File \'" + file + "\' cannot be read");
         } else {
            return new FileInputStream(file);
         }
      } else {
         throw new FileNotFoundException("File \'" + file + "\' does not exist");
      }
   }

   public static FileOutputStream openOutputStream(File file) throws IOException {
      return openOutputStream(file, false);
   }

   public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
      if(file.exists()) {
         if(file.isDirectory()) {
            throw new IOException("File \'" + file + "\' exists but is a directory");
         }

         if(!file.canWrite()) {
            throw new IOException("File \'" + file + "\' cannot be written to");
         }
      } else {
         File parent = file.getParentFile();
         if(parent != null && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Directory \'" + parent + "\' could not be created");
         }
      }

      return new FileOutputStream(file, append);
   }

   public static String byteCountToDisplaySize(BigInteger size) {
      String displaySize;
      if(size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_EB_BI) + " EB";
      } else if(size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_PB_BI) + " PB";
      } else if(size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_TB_BI) + " TB";
      } else if(size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_GB_BI) + " GB";
      } else if(size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_MB_BI) + " MB";
      } else if(size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
         displaySize = size.divide(ONE_KB_BI) + " KB";
      } else {
         displaySize = size + " bytes";
      }

      return displaySize;
   }

   public static String byteCountToDisplaySize(long size) {
      return byteCountToDisplaySize(BigInteger.valueOf(size));
   }

   public static void touch(File file) throws IOException {
      if(!file.exists()) {
         OutputStream out = openOutputStream(file);
         IOUtils.closeQuietly(out);
      }

      boolean success = file.setLastModified(System.currentTimeMillis());
      if(!success) {
         throw new IOException("Unable to set the last modification time for " + file);
      }
   }

   public static File[] convertFileCollectionToFileArray(Collection files) {
      return (File[])files.toArray(new File[files.size()]);
   }

   private static void innerListFiles(Collection files, File directory, IOFileFilter filter, boolean includeSubDirectories) {
      File[] found = directory.listFiles(filter);
      if(found != null) {
         for(File file : found) {
            if(file.isDirectory()) {
               if(includeSubDirectories) {
                  files.add(file);
               }

               innerListFiles(files, file, filter, includeSubDirectories);
            } else {
               files.add(file);
            }
         }
      }

   }

   public static Collection listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
      validateListFilesParameters(directory, fileFilter);
      IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
      IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
      Collection<File> files = new LinkedList();
      innerListFiles(files, directory, FileFilterUtils.or(new IOFileFilter[]{effFileFilter, effDirFilter}), false);
      return files;
   }

   private static void validateListFilesParameters(File directory, IOFileFilter fileFilter) {
      if(!directory.isDirectory()) {
         throw new IllegalArgumentException("Parameter \'directory\' is not a directory");
      } else if(fileFilter == null) {
         throw new NullPointerException("Parameter \'fileFilter\' is null");
      }
   }

   private static IOFileFilter setUpEffectiveFileFilter(IOFileFilter fileFilter) {
      return FileFilterUtils.and(new IOFileFilter[]{fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE)});
   }

   private static IOFileFilter setUpEffectiveDirFilter(IOFileFilter dirFilter) {
      return dirFilter == null?FalseFileFilter.INSTANCE:FileFilterUtils.and(new IOFileFilter[]{dirFilter, DirectoryFileFilter.INSTANCE});
   }

   public static Collection listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
      validateListFilesParameters(directory, fileFilter);
      IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
      IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
      Collection<File> files = new LinkedList();
      if(directory.isDirectory()) {
         files.add(directory);
      }

      innerListFiles(files, directory, FileFilterUtils.or(new IOFileFilter[]{effFileFilter, effDirFilter}), true);
      return files;
   }

   public static Iterator iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
      return listFiles(directory, fileFilter, dirFilter).iterator();
   }

   public static Iterator iterateFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
      return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
   }

   private static String[] toSuffixes(String[] extensions) {
      String[] suffixes = new String[extensions.length];

      for(int i = 0; i < extensions.length; ++i) {
         suffixes[i] = "." + extensions[i];
      }

      return suffixes;
   }

   public static Collection listFiles(File directory, String[] extensions, boolean recursive) {
      IOFileFilter filter;
      if(extensions == null) {
         filter = TrueFileFilter.INSTANCE;
      } else {
         String[] suffixes = toSuffixes(extensions);
         filter = new SuffixFileFilter(suffixes);
      }

      return listFiles(directory, filter, recursive?TrueFileFilter.INSTANCE:FalseFileFilter.INSTANCE);
   }

   public static Iterator iterateFiles(File directory, String[] extensions, boolean recursive) {
      return listFiles(directory, extensions, recursive).iterator();
   }

   public static boolean contentEquals(File file1, File file2) throws IOException {
      boolean file1Exists = file1.exists();
      if(file1Exists != file2.exists()) {
         return false;
      } else if(!file1Exists) {
         return true;
      } else if(!file1.isDirectory() && !file2.isDirectory()) {
         if(file1.length() != file2.length()) {
            return false;
         } else if(file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
         } else {
            InputStream input1 = null;
            InputStream input2 = null;

            boolean var5;
            try {
               input1 = new FileInputStream(file1);
               input2 = new FileInputStream(file2);
               var5 = IOUtils.contentEquals(input1, input2);
            } finally {
               IOUtils.closeQuietly(input1);
               IOUtils.closeQuietly(input2);
            }

            return var5;
         }
      } else {
         throw new IOException("Can\'t compare directories, only files");
      }
   }

   public static boolean contentEqualsIgnoreEOL(File file1, File file2, String charsetName) throws IOException {
      boolean file1Exists = file1.exists();
      if(file1Exists != file2.exists()) {
         return false;
      } else if(!file1Exists) {
         return true;
      } else if(!file1.isDirectory() && !file2.isDirectory()) {
         if(file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
         } else {
            Reader input1 = null;
            Reader input2 = null;

            boolean var6;
            try {
               if(charsetName == null) {
                  input1 = new InputStreamReader(new FileInputStream(file1));
                  input2 = new InputStreamReader(new FileInputStream(file2));
               } else {
                  input1 = new InputStreamReader(new FileInputStream(file1), charsetName);
                  input2 = new InputStreamReader(new FileInputStream(file2), charsetName);
               }

               var6 = IOUtils.contentEqualsIgnoreEOL(input1, input2);
            } finally {
               IOUtils.closeQuietly(input1);
               IOUtils.closeQuietly(input2);
            }

            return var6;
         }
      } else {
         throw new IOException("Can\'t compare directories, only files");
      }
   }

   public static File toFile(URL url) {
      if(url != null && "file".equalsIgnoreCase(url.getProtocol())) {
         String filename = url.getFile().replace('/', File.separatorChar);
         filename = decodeUrl(filename);
         return new File(filename);
      } else {
         return null;
      }
   }

   static String decodeUrl(String url) {
      String decoded = url;
      if(url != null && url.indexOf(37) >= 0) {
         int n = url.length();
         StringBuffer buffer = new StringBuffer();
         ByteBuffer bytes = ByteBuffer.allocate(n);
         int i = 0;

         while(true) {
            while(true) {
               if(i >= n) {
                  decoded = buffer.toString();
                  return decoded;
               }

               if(url.charAt(i) != 37) {
                  break;
               }

               try {
                  while(true) {
                     byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                     bytes.put(octet);
                     i += 3;
                     if(i >= n || url.charAt(i) != 37) {
                        break;
                     }
                  }
               } catch (RuntimeException var10) {
                  break;
               } finally {
                  if(bytes.position() > 0) {
                     bytes.flip();
                     buffer.append(UTF8.decode(bytes).toString());
                     bytes.clear();
                  }

               }
            }

            buffer.append(url.charAt(i++));
         }
      } else {
         return decoded;
      }
   }

   public static File[] toFiles(URL[] urls) {
      if(urls != null && urls.length != 0) {
         File[] files = new File[urls.length];

         for(int i = 0; i < urls.length; ++i) {
            URL url = urls[i];
            if(url != null) {
               if(!url.getProtocol().equals("file")) {
                  throw new IllegalArgumentException("URL could not be converted to a File: " + url);
               }

               files[i] = toFile(url);
            }
         }

         return files;
      } else {
         return EMPTY_FILE_ARRAY;
      }
   }

   public static URL[] toURLs(File[] files) throws IOException {
      URL[] urls = new URL[files.length];

      for(int i = 0; i < urls.length; ++i) {
         urls[i] = files[i].toURI().toURL();
      }

      return urls;
   }

   public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
      copyFileToDirectory(srcFile, destDir, true);
   }

   public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
      if(destDir == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(destDir.exists() && !destDir.isDirectory()) {
         throw new IllegalArgumentException("Destination \'" + destDir + "\' is not a directory");
      } else {
         File destFile = new File(destDir, srcFile.getName());
         copyFile(srcFile, destFile, preserveFileDate);
      }
   }

   public static void copyFile(File srcFile, File destFile) throws IOException {
      copyFile(srcFile, destFile, true);
   }

   public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
      if(srcFile == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destFile == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(!srcFile.exists()) {
         throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
      } else if(srcFile.isDirectory()) {
         throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
      } else if(srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
         throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
      } else {
         File parentFile = destFile.getParentFile();
         if(parentFile != null && !parentFile.mkdirs() && !parentFile.isDirectory()) {
            throw new IOException("Destination \'" + parentFile + "\' directory cannot be created");
         } else if(destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
         } else {
            doCopyFile(srcFile, destFile, preserveFileDate);
         }
      }
   }

   public static long copyFile(File input, OutputStream output) throws IOException {
      FileInputStream fis = new FileInputStream(input);

      long var3;
      try {
         var3 = IOUtils.copyLarge((InputStream)fis, (OutputStream)output);
      } finally {
         fis.close();
      }

      return var3;
   }

   private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
      if(destFile.exists() && destFile.isDirectory()) {
         throw new IOException("Destination \'" + destFile + "\' exists but is a directory");
      } else {
         FileInputStream fis = null;
         FileOutputStream fos = null;
         FileChannel input = null;
         FileChannel output = null;

         try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0L;

            long var16;
            for(count = 0L; pos < size; pos += output.transferFrom(input, pos, var16)) {
               var16 = size - pos > 31457280L?31457280L:size - pos;
            }
         } finally {
            IOUtils.closeQuietly((Closeable)output);
            IOUtils.closeQuietly((OutputStream)fos);
            IOUtils.closeQuietly((Closeable)input);
            IOUtils.closeQuietly((InputStream)fis);
         }

         if(srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from \'" + srcFile + "\' to \'" + destFile + "\'");
         } else {
            if(preserveFileDate) {
               destFile.setLastModified(srcFile.lastModified());
            }

         }
      }
   }

   public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
      if(srcDir == null) {
         throw new NullPointerException("Source must not be null");
      } else if(srcDir.exists() && !srcDir.isDirectory()) {
         throw new IllegalArgumentException("Source \'" + destDir + "\' is not a directory");
      } else if(destDir == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(destDir.exists() && !destDir.isDirectory()) {
         throw new IllegalArgumentException("Destination \'" + destDir + "\' is not a directory");
      } else {
         copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
      }
   }

   public static void copyDirectory(File srcDir, File destDir) throws IOException {
      copyDirectory(srcDir, destDir, true);
   }

   public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
      copyDirectory(srcDir, destDir, (FileFilter)null, preserveFileDate);
   }

   public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
      copyDirectory(srcDir, destDir, filter, true);
   }

   public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
      if(srcDir == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destDir == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(!srcDir.exists()) {
         throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
      } else if(!srcDir.isDirectory()) {
         throw new IOException("Source \'" + srcDir + "\' exists but is not a directory");
      } else if(srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
         throw new IOException("Source \'" + srcDir + "\' and destination \'" + destDir + "\' are the same");
      } else {
         List<String> exclusionList = null;
         if(destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null?srcDir.listFiles():srcDir.listFiles(filter);
            if(srcFiles != null && srcFiles.length > 0) {
               exclusionList = new ArrayList(srcFiles.length);

               for(File srcFile : srcFiles) {
                  File copiedFile = new File(destDir, srcFile.getName());
                  exclusionList.add(copiedFile.getCanonicalPath());
               }
            }
         }

         doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
      }
   }

   private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate, List exclusionList) throws IOException {
      File[] srcFiles = filter == null?srcDir.listFiles():srcDir.listFiles(filter);
      if(srcFiles == null) {
         throw new IOException("Failed to list contents of " + srcDir);
      } else {
         if(destDir.exists()) {
            if(!destDir.isDirectory()) {
               throw new IOException("Destination \'" + destDir + "\' exists but is not a directory");
            }
         } else if(!destDir.mkdirs() && !destDir.isDirectory()) {
            throw new IOException("Destination \'" + destDir + "\' directory cannot be created");
         }

         if(!destDir.canWrite()) {
            throw new IOException("Destination \'" + destDir + "\' cannot be written to");
         } else {
            for(File srcFile : srcFiles) {
               File dstFile = new File(destDir, srcFile.getName());
               if(exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                  if(srcFile.isDirectory()) {
                     doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                  } else {
                     doCopyFile(srcFile, dstFile, preserveFileDate);
                  }
               }
            }

            if(preserveFileDate) {
               destDir.setLastModified(srcDir.lastModified());
            }

         }
      }
   }

   public static void copyURLToFile(URL source, File destination) throws IOException {
      InputStream input = source.openStream();
      copyInputStreamToFile(input, destination);
   }

   public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
      URLConnection connection = source.openConnection();
      connection.setConnectTimeout(connectionTimeout);
      connection.setReadTimeout(readTimeout);
      InputStream input = connection.getInputStream();
      copyInputStreamToFile(input, destination);
   }

   public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
      try {
         FileOutputStream output = openOutputStream(destination);

         try {
            IOUtils.copy((InputStream)source, (OutputStream)output);
            output.close();
         } finally {
            IOUtils.closeQuietly((OutputStream)output);
         }
      } finally {
         IOUtils.closeQuietly(source);
      }

   }

   public static void deleteDirectory(File directory) throws IOException {
      if(directory.exists()) {
         if(!isSymlink(directory)) {
            cleanDirectory(directory);
         }

         if(!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
         }
      }
   }

   public static boolean deleteQuietly(File file) {
      if(file == null) {
         return false;
      } else {
         try {
            if(file.isDirectory()) {
               cleanDirectory(file);
            }
         } catch (Exception var3) {
            ;
         }

         try {
            return file.delete();
         } catch (Exception var2) {
            return false;
         }
      }
   }

   public static boolean directoryContains(File directory, File child) throws IOException {
      if(directory == null) {
         throw new IllegalArgumentException("Directory must not be null");
      } else if(!directory.isDirectory()) {
         throw new IllegalArgumentException("Not a directory: " + directory);
      } else if(child == null) {
         return false;
      } else if(directory.exists() && child.exists()) {
         String canonicalParent = directory.getCanonicalPath();
         String canonicalChild = child.getCanonicalPath();
         return FilenameUtils.directoryContains(canonicalParent, canonicalChild);
      } else {
         return false;
      }
   }

   public static void cleanDirectory(File directory) throws IOException {
      if(!directory.exists()) {
         String message = directory + " does not exist";
         throw new IllegalArgumentException(message);
      } else if(!directory.isDirectory()) {
         String message = directory + " is not a directory";
         throw new IllegalArgumentException(message);
      } else {
         File[] files = directory.listFiles();
         if(files == null) {
            throw new IOException("Failed to list contents of " + directory);
         } else {
            IOException exception = null;

            for(File file : files) {
               try {
                  forceDelete(file);
               } catch (IOException var8) {
                  exception = var8;
               }
            }

            if(null != exception) {
               throw exception;
            }
         }
      }
   }

   public static boolean waitFor(File file, int seconds) {
      int timeout = 0;
      int tick = 0;

      while(!file.exists()) {
         if(tick++ >= 10) {
            tick = 0;
            if(timeout++ > seconds) {
               return false;
            }
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var5) {
            ;
         } catch (Exception var6) {
            break;
         }
      }

      return true;
   }

   public static String readFileToString(File file, Charset encoding) throws IOException {
      InputStream in = null;

      String var3;
      try {
         in = openInputStream(file);
         var3 = IOUtils.toString(in, Charsets.toCharset(encoding));
      } finally {
         IOUtils.closeQuietly(in);
      }

      return var3;
   }

   public static String readFileToString(File file, String encoding) throws IOException {
      return readFileToString(file, Charsets.toCharset(encoding));
   }

   public static String readFileToString(File file) throws IOException {
      return readFileToString(file, Charset.defaultCharset());
   }

   public static byte[] readFileToByteArray(File file) throws IOException {
      InputStream in = null;

      byte[] var2;
      try {
         in = openInputStream(file);
         var2 = IOUtils.toByteArray(in, file.length());
      } finally {
         IOUtils.closeQuietly(in);
      }

      return var2;
   }

   public static List readLines(File file, Charset encoding) throws IOException {
      InputStream in = null;

      List var3;
      try {
         in = openInputStream(file);
         var3 = IOUtils.readLines(in, Charsets.toCharset(encoding));
      } finally {
         IOUtils.closeQuietly(in);
      }

      return var3;
   }

   public static List readLines(File file, String encoding) throws IOException {
      return readLines(file, Charsets.toCharset(encoding));
   }

   public static List readLines(File file) throws IOException {
      return readLines(file, Charset.defaultCharset());
   }

   public static LineIterator lineIterator(File file, String encoding) throws IOException {
      InputStream in = null;

      try {
         FileInputStream var6 = openInputStream(file);
         return IOUtils.lineIterator(var6, (String)encoding);
      } catch (IOException var4) {
         IOUtils.closeQuietly(in);
         throw var4;
      } catch (RuntimeException var5) {
         IOUtils.closeQuietly(in);
         throw var5;
      }
   }

   public static LineIterator lineIterator(File file) throws IOException {
      return lineIterator(file, (String)null);
   }

   public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
      writeStringToFile(file, data, encoding, false);
   }

   public static void writeStringToFile(File file, String data, String encoding) throws IOException {
      writeStringToFile(file, data, encoding, false);
   }

   public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
      OutputStream out = null;

      try {
         out = openOutputStream(file, append);
         IOUtils.write(data, out, encoding);
         out.close();
      } finally {
         IOUtils.closeQuietly(out);
      }

   }

   public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
      writeStringToFile(file, data, Charsets.toCharset(encoding), append);
   }

   public static void writeStringToFile(File file, String data) throws IOException {
      writeStringToFile(file, data, Charset.defaultCharset(), false);
   }

   public static void writeStringToFile(File file, String data, boolean append) throws IOException {
      writeStringToFile(file, data, Charset.defaultCharset(), append);
   }

   public static void write(File file, CharSequence data) throws IOException {
      write(file, data, Charset.defaultCharset(), false);
   }

   public static void write(File file, CharSequence data, boolean append) throws IOException {
      write(file, data, Charset.defaultCharset(), append);
   }

   public static void write(File file, CharSequence data, Charset encoding) throws IOException {
      write(file, data, encoding, false);
   }

   public static void write(File file, CharSequence data, String encoding) throws IOException {
      write(file, data, encoding, false);
   }

   public static void write(File file, CharSequence data, Charset encoding, boolean append) throws IOException {
      String str = data == null?null:data.toString();
      writeStringToFile(file, str, encoding, append);
   }

   public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
      write(file, data, Charsets.toCharset(encoding), append);
   }

   public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
      writeByteArrayToFile(file, data, false);
   }

   public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
      OutputStream out = null;

      try {
         out = openOutputStream(file, append);
         out.write(data);
         out.close();
      } finally {
         IOUtils.closeQuietly(out);
      }

   }

   public static void writeLines(File file, String encoding, Collection lines) throws IOException {
      writeLines(file, encoding, lines, (String)null, false);
   }

   public static void writeLines(File file, String encoding, Collection lines, boolean append) throws IOException {
      writeLines(file, encoding, lines, (String)null, append);
   }

   public static void writeLines(File file, Collection lines) throws IOException {
      writeLines(file, (String)null, lines, (String)null, false);
   }

   public static void writeLines(File file, Collection lines, boolean append) throws IOException {
      writeLines(file, (String)null, lines, (String)null, append);
   }

   public static void writeLines(File file, String encoding, Collection lines, String lineEnding) throws IOException {
      writeLines(file, encoding, lines, lineEnding, false);
   }

   public static void writeLines(File file, String encoding, Collection lines, String lineEnding, boolean append) throws IOException {
      FileOutputStream out = null;

      try {
         out = openOutputStream(file, append);
         BufferedOutputStream buffer = new BufferedOutputStream(out);
         IOUtils.writeLines(lines, lineEnding, buffer, (String)encoding);
         buffer.flush();
         out.close();
      } finally {
         IOUtils.closeQuietly((OutputStream)out);
      }

   }

   public static void writeLines(File file, Collection lines, String lineEnding) throws IOException {
      writeLines(file, (String)null, lines, lineEnding, false);
   }

   public static void writeLines(File file, Collection lines, String lineEnding, boolean append) throws IOException {
      writeLines(file, (String)null, lines, lineEnding, append);
   }

   public static void forceDelete(File file) throws IOException {
      if(file.isDirectory()) {
         deleteDirectory(file);
      } else {
         boolean filePresent = file.exists();
         if(!file.delete()) {
            if(!filePresent) {
               throw new FileNotFoundException("File does not exist: " + file);
            }

            String message = "Unable to delete file: " + file;
            throw new IOException(message);
         }
      }

   }

   public static void forceDeleteOnExit(File file) throws IOException {
      if(file.isDirectory()) {
         deleteDirectoryOnExit(file);
      } else {
         file.deleteOnExit();
      }

   }

   private static void deleteDirectoryOnExit(File directory) throws IOException {
      if(directory.exists()) {
         directory.deleteOnExit();
         if(!isSymlink(directory)) {
            cleanDirectoryOnExit(directory);
         }

      }
   }

   private static void cleanDirectoryOnExit(File directory) throws IOException {
      if(!directory.exists()) {
         String message = directory + " does not exist";
         throw new IllegalArgumentException(message);
      } else if(!directory.isDirectory()) {
         String message = directory + " is not a directory";
         throw new IllegalArgumentException(message);
      } else {
         File[] files = directory.listFiles();
         if(files == null) {
            throw new IOException("Failed to list contents of " + directory);
         } else {
            IOException exception = null;

            for(File file : files) {
               try {
                  forceDeleteOnExit(file);
               } catch (IOException var8) {
                  exception = var8;
               }
            }

            if(null != exception) {
               throw exception;
            }
         }
      }
   }

   public static void forceMkdir(File directory) throws IOException {
      if(directory.exists()) {
         if(!directory.isDirectory()) {
            String message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
            throw new IOException(message);
         }
      } else if(!directory.mkdirs() && !directory.isDirectory()) {
         String message = "Unable to create directory " + directory;
         throw new IOException(message);
      }

   }

   public static long sizeOf(File file) {
      if(!file.exists()) {
         String message = file + " does not exist";
         throw new IllegalArgumentException(message);
      } else {
         return file.isDirectory()?sizeOfDirectory(file):file.length();
      }
   }

   public static BigInteger sizeOfAsBigInteger(File file) {
      if(!file.exists()) {
         String message = file + " does not exist";
         throw new IllegalArgumentException(message);
      } else {
         return file.isDirectory()?sizeOfDirectoryAsBigInteger(file):BigInteger.valueOf(file.length());
      }
   }

   public static long sizeOfDirectory(File directory) {
      checkDirectory(directory);
      File[] files = directory.listFiles();
      if(files == null) {
         return 0L;
      } else {
         long size = 0L;

         for(File file : files) {
            try {
               if(!isSymlink(file)) {
                  size += sizeOf(file);
                  if(size < 0L) {
                     break;
                  }
               }
            } catch (IOException var9) {
               ;
            }
         }

         return size;
      }
   }

   public static BigInteger sizeOfDirectoryAsBigInteger(File directory) {
      checkDirectory(directory);
      File[] files = directory.listFiles();
      if(files == null) {
         return BigInteger.ZERO;
      } else {
         BigInteger size = BigInteger.ZERO;

         for(File file : files) {
            try {
               if(!isSymlink(file)) {
                  size = size.add(BigInteger.valueOf(sizeOf(file)));
               }
            } catch (IOException var8) {
               ;
            }
         }

         return size;
      }
   }

   private static void checkDirectory(File directory) {
      if(!directory.exists()) {
         throw new IllegalArgumentException(directory + " does not exist");
      } else if(!directory.isDirectory()) {
         throw new IllegalArgumentException(directory + " is not a directory");
      }
   }

   public static boolean isFileNewer(File file, File reference) {
      if(reference == null) {
         throw new IllegalArgumentException("No specified reference file");
      } else if(!reference.exists()) {
         throw new IllegalArgumentException("The reference file \'" + reference + "\' doesn\'t exist");
      } else {
         return isFileNewer(file, reference.lastModified());
      }
   }

   public static boolean isFileNewer(File file, Date date) {
      if(date == null) {
         throw new IllegalArgumentException("No specified date");
      } else {
         return isFileNewer(file, date.getTime());
      }
   }

   public static boolean isFileNewer(File file, long timeMillis) {
      if(file == null) {
         throw new IllegalArgumentException("No specified file");
      } else {
         return !file.exists()?false:file.lastModified() > timeMillis;
      }
   }

   public static boolean isFileOlder(File file, File reference) {
      if(reference == null) {
         throw new IllegalArgumentException("No specified reference file");
      } else if(!reference.exists()) {
         throw new IllegalArgumentException("The reference file \'" + reference + "\' doesn\'t exist");
      } else {
         return isFileOlder(file, reference.lastModified());
      }
   }

   public static boolean isFileOlder(File file, Date date) {
      if(date == null) {
         throw new IllegalArgumentException("No specified date");
      } else {
         return isFileOlder(file, date.getTime());
      }
   }

   public static boolean isFileOlder(File file, long timeMillis) {
      if(file == null) {
         throw new IllegalArgumentException("No specified file");
      } else {
         return !file.exists()?false:file.lastModified() < timeMillis;
      }
   }

   public static long checksumCRC32(File file) throws IOException {
      CRC32 crc = new CRC32();
      checksum(file, crc);
      return crc.getValue();
   }

   public static Checksum checksum(File file, Checksum checksum) throws IOException {
      if(file.isDirectory()) {
         throw new IllegalArgumentException("Checksums can\'t be computed on directories");
      } else {
         InputStream in = null;

         try {
            in = new CheckedInputStream(new FileInputStream(file), checksum);
            IOUtils.copy((InputStream)in, (OutputStream)(new NullOutputStream()));
         } finally {
            IOUtils.closeQuietly(in);
         }

         return checksum;
      }
   }

   public static void moveDirectory(File srcDir, File destDir) throws IOException {
      if(srcDir == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destDir == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(!srcDir.exists()) {
         throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
      } else if(!srcDir.isDirectory()) {
         throw new IOException("Source \'" + srcDir + "\' is not a directory");
      } else if(destDir.exists()) {
         throw new FileExistsException("Destination \'" + destDir + "\' already exists");
      } else {
         boolean rename = srcDir.renameTo(destDir);
         if(!rename) {
            if(destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
               throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }

            copyDirectory(srcDir, destDir);
            deleteDirectory(srcDir);
            if(srcDir.exists()) {
               throw new IOException("Failed to delete original directory \'" + srcDir + "\' after copy to \'" + destDir + "\'");
            }
         }

      }
   }

   public static void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
      if(src == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destDir == null) {
         throw new NullPointerException("Destination directory must not be null");
      } else {
         if(!destDir.exists() && createDestDir) {
            destDir.mkdirs();
         }

         if(!destDir.exists()) {
            throw new FileNotFoundException("Destination directory \'" + destDir + "\' does not exist [createDestDir=" + createDestDir + "]");
         } else if(!destDir.isDirectory()) {
            throw new IOException("Destination \'" + destDir + "\' is not a directory");
         } else {
            moveDirectory(src, new File(destDir, src.getName()));
         }
      }
   }

   public static void moveFile(File srcFile, File destFile) throws IOException {
      if(srcFile == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destFile == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(!srcFile.exists()) {
         throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
      } else if(srcFile.isDirectory()) {
         throw new IOException("Source \'" + srcFile + "\' is a directory");
      } else if(destFile.exists()) {
         throw new FileExistsException("Destination \'" + destFile + "\' already exists");
      } else if(destFile.isDirectory()) {
         throw new IOException("Destination \'" + destFile + "\' is a directory");
      } else {
         boolean rename = srcFile.renameTo(destFile);
         if(!rename) {
            copyFile(srcFile, destFile);
            if(!srcFile.delete()) {
               deleteQuietly(destFile);
               throw new IOException("Failed to delete original file \'" + srcFile + "\' after copy to \'" + destFile + "\'");
            }
         }

      }
   }

   public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
      if(srcFile == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destDir == null) {
         throw new NullPointerException("Destination directory must not be null");
      } else {
         if(!destDir.exists() && createDestDir) {
            destDir.mkdirs();
         }

         if(!destDir.exists()) {
            throw new FileNotFoundException("Destination directory \'" + destDir + "\' does not exist [createDestDir=" + createDestDir + "]");
         } else if(!destDir.isDirectory()) {
            throw new IOException("Destination \'" + destDir + "\' is not a directory");
         } else {
            moveFile(srcFile, new File(destDir, srcFile.getName()));
         }
      }
   }

   public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
      if(src == null) {
         throw new NullPointerException("Source must not be null");
      } else if(destDir == null) {
         throw new NullPointerException("Destination must not be null");
      } else if(!src.exists()) {
         throw new FileNotFoundException("Source \'" + src + "\' does not exist");
      } else {
         if(src.isDirectory()) {
            moveDirectoryToDirectory(src, destDir, createDestDir);
         } else {
            moveFileToDirectory(src, destDir, createDestDir);
         }

      }
   }

   public static boolean isSymlink(File file) throws IOException {
      if(file == null) {
         throw new NullPointerException("File must not be null");
      } else if(FilenameUtils.isSystemWindows()) {
         return false;
      } else {
         File fileInCanonicalDir = null;
         if(file.getParent() == null) {
            fileInCanonicalDir = file;
         } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
         }

         return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
      }
   }
}
