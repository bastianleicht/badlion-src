package com.mojang.realmsclient.client;

import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileDownload {
   private static final Logger LOGGER = LogManager.getLogger();
   private volatile boolean cancelled = false;
   private volatile boolean finished = false;
   private volatile boolean error = false;
   private volatile boolean extracting = false;
   private volatile File tempFile;
   private volatile HttpGet request;
   private Thread currentThread;
   private RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long contentLength(String downloadLink) {
      CloseableHttpClient client = null;
      HttpGet httpGet = null;

      long var5;
      try {
         httpGet = new HttpGet(downloadLink);
         client = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse response = client.execute(httpGet);
         var5 = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
         return var5;
      } catch (Throwable var16) {
         LOGGER.error("Unable to get content length for download");
         var5 = 0L;
      } finally {
         if(httpGet != null) {
            httpGet.releaseConnection();
         }

         if(client != null) {
            try {
               client.close();
            } catch (IOException var15) {
               LOGGER.error((String)"Could not close http client", (Throwable)var15);
            }
         }

      }

      return var5;
   }

   public void download(final String downloadLink, final String worldName, final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, final RealmsAnvilLevelStorageSource levelStorageSource) {
      if(this.currentThread == null) {
         this.currentThread = new Thread() {
            public void run() {
               CloseableHttpClient client = null;

               try {
                  FileDownload.this.tempFile = File.createTempFile("backup", ".tar.gz");
                  FileDownload.this.request = new HttpGet(downloadLink);
                  client = HttpClientBuilder.create().setDefaultRequestConfig(FileDownload.this.requestConfig).build();
                  HttpResponse response = client.execute(FileDownload.this.request);
                  downloadStatus.totalBytes = Long.valueOf(Long.parseLong(response.getFirstHeader("Content-Length").getValue()));
                  if(response.getStatusLine().getStatusCode() == 200) {
                     OutputStream os = new FileOutputStream(FileDownload.this.tempFile);
                     FileDownload.ProgressListener progressListener = FileDownload.this.new ProgressListener(worldName.trim(), FileDownload.this.tempFile, levelStorageSource, downloadStatus);
                     FileDownload.DownloadCountingOutputStream dcount = FileDownload.this.new DownloadCountingOutputStream(os);
                     dcount.setListener(progressListener);
                     IOUtils.copy((InputStream)response.getEntity().getContent(), (OutputStream)dcount);
                     return;
                  }

                  FileDownload.this.error = true;
                  FileDownload.this.request.abort();
               } catch (Exception var15) {
                  FileDownload.LOGGER.error("Caught exception while downloading: " + var15.getMessage());
                  FileDownload.this.error = true;
                  return;
               } finally {
                  FileDownload.this.request.releaseConnection();
                  if(FileDownload.this.tempFile != null) {
                     FileDownload.this.tempFile.delete();
                  }

                  if(client != null) {
                     try {
                        client.close();
                     } catch (IOException var14) {
                        FileDownload.LOGGER.error("Failed to close Realms download client");
                     }
                  }

               }

            }
         };
         this.currentThread.start();
      }
   }

   public void cancel() {
      if(this.request != null) {
         this.request.abort();
      }

      if(this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String folder) {
      folder = folder.replaceAll("[\\./\"]", "_");

      for(String invalidName : INVALID_FILE_NAMES) {
         if(folder.equalsIgnoreCase(invalidName)) {
            folder = "_" + folder + "_";
         }
      }

      return folder;
   }

   private void untarGzipArchive(String name, File file, RealmsAnvilLevelStorageSource levelStorageSource) throws IOException {
      // $FF: Couldn't be decompiled
   }

   private class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener listener = null;

      public DownloadCountingOutputStream(OutputStream out) {
         super(out);
      }

      public void setListener(ActionListener listener) {
         this.listener = listener;
      }

      protected void afterWrite(int n) throws IOException {
         super.afterWrite(n);
         if(this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   private class ProgressListener implements ActionListener {
      private volatile String worldName;
      private volatile File tempFile;
      private volatile RealmsAnvilLevelStorageSource levelStorageSource;
      private volatile RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      private ProgressListener(String worldName, File tempFile, RealmsAnvilLevelStorageSource levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
         this.worldName = worldName;
         this.tempFile = tempFile;
         this.levelStorageSource = levelStorageSource;
         this.downloadStatus = downloadStatus;
      }

      public void actionPerformed(ActionEvent e) {
         this.downloadStatus.bytesWritten = Long.valueOf(((FileDownload.DownloadCountingOutputStream)e.getSource()).getByteCount());
         if(this.downloadStatus.bytesWritten.longValue() >= this.downloadStatus.totalBytes.longValue() && !FileDownload.this.cancelled) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException var3) {
               FileDownload.this.error = true;
            }
         }

      }
   }
}
