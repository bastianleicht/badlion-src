package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int CANCEL_BUTTON = 0;
   private static final int BACK_BUTTON = 1;
   private final RealmsResetWorldScreen lastScreen;
   private final RealmsLevelSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private volatile String errorMessage = null;
   private volatile String status = null;
   private volatile String progress = null;
   private volatile boolean cancelled = false;
   private volatile boolean uploadFinished = false;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted = false;
   private RealmsButton backButton;
   private RealmsButton cancelButton;
   private int animTick = 0;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex = 0;
   private Long previousWrittenBytes = null;
   private Long previousTimeSnapshot = null;
   private long bytesPersSecond = 0L;
   private static final ReentrantLock uploadLock = new ReentrantLock();

   public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, RealmsLevelSummary selectedLevel) {
      this.worldId = worldId;
      this.slotId = slotId;
      this.lastScreen = lastScreen;
      this.selectedLevel = selectedLevel;
      this.uploadStatus = new UploadStatus();
   }

   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttonsClear();
      this.backButton = newButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back"));
      this.buttonsAdd(this.cancelButton = newButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")));
      if(!this.uploadStarted) {
         if(this.lastScreen.slot != -1) {
            this.lastScreen.switchSlot(this);
         } else {
            this.upload();
         }
      }

   }

   public void confirmResult(boolean result, int buttonId) {
      if(result && !this.uploadStarted) {
         this.uploadStarted = true;
         Realms.setScreen(this);
         this.upload();
      }

   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         if(button.id() == 1) {
            this.lastScreen.confirmResult(true, 0);
         } else if(button.id() == 0) {
            this.cancelled = true;
            Realms.setScreen(this.lastScreen);
         }

      }
   }

   public void keyPressed(char ch, int eventKey) {
      if(eventKey == 1) {
         this.cancelled = true;
         Realms.setScreen(this.lastScreen);
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      if(!this.uploadFinished && this.uploadStatus.bytesWritten.longValue() != 0L && this.uploadStatus.bytesWritten.longValue() == this.uploadStatus.totalBytes.longValue()) {
         this.status = getLocalizedString("mco.upload.verifying");
      }

      this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
      if(this.showDots) {
         this.drawDots();
      }

      if(this.uploadStatus.bytesWritten.longValue() != 0L && !this.cancelled) {
         this.drawProgressBar();
         this.drawUploadSpeed();
      }

      if(this.errorMessage != null) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, 110, 16711680);
      }

      super.render(xm, ym, a);
   }

   private void drawDots() {
      int statusWidth = this.fontWidth(this.status);
      if(this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + statusWidth / 2 + 5, 50, 16777215);
   }

   private void drawProgressBar() {
      double percentage = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0D;
      if(percentage > 100.0D) {
         percentage = 100.0D;
      }

      this.progress = String.format("%.1f", new Object[]{Double.valueOf(percentage)});
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3553);
      double base = (double)(this.width() / 2 - 100);
      double diff = 0.5D;
      Tezzelator t = Tezzelator.instance;
      t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      t.vertex(base - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      t.vertex(base + 200.0D * percentage / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      t.vertex(base + 200.0D * percentage / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      t.vertex(base - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      t.vertex(base, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(base + 200.0D * percentage / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(base + 200.0D * percentage / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(base, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.end();
      GL11.glEnable(3553);
      this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
   }

   private void drawUploadSpeed() {
      if(this.animTick % RealmsSharedConstants.TICKS_PER_SECOND == 0) {
         if(this.previousWrittenBytes != null) {
            long timeElapsed = System.currentTimeMillis() - this.previousTimeSnapshot.longValue();
            if(timeElapsed == 0L) {
               timeElapsed = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten.longValue() - this.previousWrittenBytes.longValue()) / timeElapsed;
            this.drawUploadSpeed0(this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = Long.valueOf(System.currentTimeMillis());
      } else {
         this.drawUploadSpeed0(this.bytesPersSecond);
      }

   }

   private void drawUploadSpeed0(long bytesPersSecond) {
      if(bytesPersSecond > 0L) {
         int progressLength = this.fontWidth(this.progress);
         String stringPresentation = "(" + humanReadableByteCount(bytesPersSecond) + ")";
         this.drawString(stringPresentation, this.width() / 2 + progressLength / 2 + 15, 84, 16777215);
      }

   }

   public static String humanReadableByteCount(long bytes) {
      int unit = 1024;
      if(bytes < (long)unit) {
         return bytes + " B";
      } else {
         int exp = (int)(Math.log((double)bytes) / Math.log((double)unit));
         String pre = "KMGTPE".charAt(exp - 1) + "";
         return String.format("%.1f %sB/s", new Object[]{Double.valueOf((double)bytes / Math.pow((double)unit, (double)exp)), pre});
      }
   }

   public void mouseEvent() {
      super.mouseEvent();
   }

   public void tick() {
      super.tick();
      ++this.animTick;
   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread() {
         public void run() {
            File archive = null;
            RealmsClient client = RealmsClient.createRealmsClient();
            long wid = RealmsUploadScreen.this.worldId;

            try {
               if(RealmsUploadScreen.uploadLock.tryLock(1L, TimeUnit.SECONDS)) {
                  RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.preparing");
                  UploadInfo uploadInfo = null;

                  for(int i = 0; i < 20; ++i) {
                     try {
                        if(RealmsUploadScreen.this.cancelled) {
                           RealmsUploadScreen.this.uploadCancelled(wid);
                           return;
                        }

                        uploadInfo = client.upload(wid, UploadTokenCache.get(wid));
                        break;
                     } catch (RetryCallException var32) {
                        Thread.sleep((long)(var32.delaySeconds * 1000));
                     }
                  }

                  if(uploadInfo == null) {
                     RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                     return;
                  }

                  UploadTokenCache.put(wid, uploadInfo.getToken());
                  if(!uploadInfo.isWorldClosed()) {
                     RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                     return;
                  }

                  if(RealmsUploadScreen.this.cancelled) {
                     RealmsUploadScreen.this.uploadCancelled(wid);
                     return;
                  }

                  File saves = new File(Realms.getGameDirectoryPath(), "saves");
                  archive = RealmsUploadScreen.this.tarGzipArchive(new File(saves, RealmsUploadScreen.this.selectedLevel.getLevelId()));
                  if(RealmsUploadScreen.this.cancelled) {
                     RealmsUploadScreen.this.uploadCancelled(wid);
                     return;
                  }

                  if(!RealmsUploadScreen.this.verify(archive)) {
                     RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure", new Object[]{RealmsUploadScreen.this.selectedLevel.getLevelName()});
                     return;
                  }

                  RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.uploading", new Object[]{RealmsUploadScreen.this.selectedLevel.getLevelName()});
                  FileUpload fileUpload = new FileUpload();
                  fileUpload.upload(archive, RealmsUploadScreen.this.worldId, RealmsUploadScreen.this.slotId, uploadInfo, Realms.getSessionId(), Realms.getName(), RealmsSharedConstants.VERSION_STRING, RealmsUploadScreen.this.uploadStatus);

                  while(!fileUpload.isFinished()) {
                     if(RealmsUploadScreen.this.cancelled) {
                        fileUpload.cancel();
                        RealmsUploadScreen.this.uploadCancelled(wid);
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException var31) {
                        RealmsUploadScreen.LOGGER.error("Failed to check Realms file upload status");
                     }
                  }

                  if(fileUpload.getStatusCode() >= 200 && fileUpload.getStatusCode() < 300) {
                     RealmsUploadScreen.this.uploadFinished = true;
                     RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.done");
                     RealmsUploadScreen.this.backButton.msg(RealmsScreen.getLocalizedString("gui.done"));
                     UploadTokenCache.invalidate(wid);
                     return;
                  } else {
                     if(fileUpload.getStatusCode() == 400 && fileUpload.getErrorMessage() != null) {
                        RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[]{fileUpload.getErrorMessage()});
                     } else {
                        RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[]{Integer.valueOf(fileUpload.getStatusCode())});
                     }

                     return;
                  }
               }
            } catch (IOException var33) {
               RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[]{var33.getMessage()});
               return;
            } catch (RealmsServiceException var34) {
               RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", new Object[]{var34.toString()});
               return;
            } catch (InterruptedException var35) {
               RealmsUploadScreen.LOGGER.error("Could not acquire upload lock");
               return;
            } finally {
               RealmsUploadScreen.this.uploadFinished = true;
               if(RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
                  RealmsUploadScreen.uploadLock.unlock();
                  RealmsUploadScreen.this.showDots = false;
                  RealmsUploadScreen.this.buttonsRemove(RealmsUploadScreen.this.cancelButton);
                  RealmsUploadScreen.this.buttonsAdd(RealmsUploadScreen.this.backButton);
                  if(archive != null) {
                     RealmsUploadScreen.LOGGER.debug("Deleting file " + archive.getAbsolutePath());
                     archive.delete();
                  }

                  if(RealmsUploadScreen.this.cancelled) {
                     return;
                  }

                  try {
                     client.uploadFinished(wid);
                  } catch (RealmsServiceException var30) {
                     RealmsUploadScreen.LOGGER.error("Failed to request upload-finished to Realms", new Object[]{var30.toString()});
                  }

               }

               return;
            }

         }
      }).start();
   }

   private void uploadCancelled(long worldId) {
      this.status = getLocalizedString("mco.upload.cancelled");
      String oldToken = UploadTokenCache.get(worldId);
      UploadTokenCache.invalidate(worldId);

      try {
         RealmsClient client = RealmsClient.createRealmsClient();
         client.uploadCancelled(worldId, oldToken);
      } catch (RealmsServiceException var5) {
         LOGGER.error((String)"Failed to cancel upload", (Throwable)var5);
      }

   }

   private boolean verify(File archive) {
      return archive.length() < 1048576000L;
   }

   private File tarGzipArchive(File pathToDirectoryFile) throws IOException {
      TarArchiveOutputStream tar = null;

      File var4;
      try {
         File file = File.createTempFile("realms-upload-file", ".tar.gz");
         tar = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
         this.addFileToTarGz(tar, pathToDirectoryFile.getAbsolutePath(), "world", true);
         tar.finish();
         var4 = file;
      } finally {
         if(tar != null) {
            tar.close();
         }

      }

      return var4;
   }

   private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean root) throws IOException {
      if(!this.cancelled) {
         File f = new File(path);
         String entryName = root?base:base + f.getName();
         TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
         tOut.putArchiveEntry(tarEntry);
         if(f.isFile()) {
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
         } else {
            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if(children != null) {
               for(File child : children) {
                  this.addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/", false);
               }
            }
         }

      }
   }
}
