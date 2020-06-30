package org.apache.logging.log4j.core.helpers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class FileUtils {
   private static final String PROTOCOL_FILE = "file";
   private static final String JBOSS_FILE = "vfsfile";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public static File fileFromURI(URI uri) {
      if(uri != null && (uri.getScheme() == null || "file".equals(uri.getScheme()) || "vfsfile".equals(uri.getScheme()))) {
         if(uri.getScheme() == null) {
            try {
               uri = (new File(uri.getPath())).toURI();
            } catch (Exception var4) {
               LOGGER.warn("Invalid URI " + uri);
               return null;
            }
         }

         try {
            return new File(URLDecoder.decode(uri.toURL().getFile(), "UTF8"));
         } catch (MalformedURLException var2) {
            LOGGER.warn((String)("Invalid URL " + uri), (Throwable)var2);
         } catch (UnsupportedEncodingException var3) {
            LOGGER.warn((String)"Invalid encoding: UTF8", (Throwable)var3);
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean isFile(URL url) {
      return url != null && (url.getProtocol().equals("file") || url.getProtocol().equals("vfsfile"));
   }

   public static void mkdir(File dir, boolean createDirectoryIfNotExisting) throws IOException {
      if(!dir.exists()) {
         if(!createDirectoryIfNotExisting) {
            throw new IOException("The directory " + dir.getAbsolutePath() + " does not exist.");
         }

         if(!dir.mkdirs()) {
            throw new IOException("Could not create directory " + dir.getAbsolutePath());
         }
      }

      if(!dir.isDirectory()) {
         throw new IOException("File " + dir + " exists and is not a directory. Unable to create directory.");
      }
   }
}
