package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class W32FileMonitor extends FileMonitor {
   private static final int BUFFER_SIZE = 4096;
   private Thread watcher;
   private WinNT.HANDLE port;
   private final Map fileMap = new HashMap();
   private final Map handleMap = new HashMap();
   private boolean disposing = false;
   private static int watcherThreadID;

   private void handleChanges(W32FileMonitor.FileInfo finfo) throws IOException {
      Kernel32 klib = Kernel32.INSTANCE;
      WinNT.FILE_NOTIFY_INFORMATION fni = finfo.info;
      fni.read();

      while(true) {
         FileMonitor.FileEvent event = null;
         File file = new File(finfo.file, fni.getFilename());
         switch(fni.Action) {
         case 0:
            break;
         case 1:
            event = new FileMonitor.FileEvent(file, 1);
            break;
         case 2:
            event = new FileMonitor.FileEvent(file, 2);
            break;
         case 3:
            event = new FileMonitor.FileEvent(file, 4);
            break;
         case 4:
            event = new FileMonitor.FileEvent(file, 16);
            break;
         case 5:
            event = new FileMonitor.FileEvent(file, 32);
            break;
         default:
            System.err.println("Unrecognized file action \'" + fni.Action + "\'");
         }

         if(event != null) {
            this.notify(event);
         }

         fni = fni.next();
         if(fni == null) {
            break;
         }
      }

      if(!finfo.file.exists()) {
         this.unwatch(finfo.file);
      } else if(!klib.ReadDirectoryChangesW(finfo.handle, finfo.info, finfo.info.size(), finfo.recursive, finfo.notifyMask, finfo.infoLength, finfo.overlapped, (WinNT.OVERLAPPED_COMPLETION_ROUTINE)null) && !this.disposing) {
         int err = klib.GetLastError();
         throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ": \'" + Kernel32Util.formatMessageFromLastErrorCode(err) + "\' (" + err + ")");
      }
   }

   private W32FileMonitor.FileInfo waitForChange() {
      Kernel32 klib = Kernel32.INSTANCE;
      IntByReference rcount = new IntByReference();
      BaseTSD.ULONG_PTRByReference rkey = new BaseTSD.ULONG_PTRByReference();
      PointerByReference roverlap = new PointerByReference();
      klib.GetQueuedCompletionStatus(this.port, rcount, rkey, roverlap, -1);
      synchronized(this) {
         return (W32FileMonitor.FileInfo)this.handleMap.get(rkey.getValue());
      }
   }

   private int convertMask(int mask) {
      int result = 0;
      if((mask & 1) != 0) {
         result |= 64;
      }

      if((mask & 2) != 0) {
         result |= 3;
      }

      if((mask & 4) != 0) {
         result |= 16;
      }

      if((mask & 48) != 0) {
         result |= 3;
      }

      if((mask & 64) != 0) {
         result |= 8;
      }

      if((mask & 8) != 0) {
         result |= 32;
      }

      if((mask & 128) != 0) {
         result |= 4;
      }

      if((mask & 256) != 0) {
         result |= 256;
      }

      return result;
   }

   protected synchronized void watch(File file, int eventMask, boolean recursive) throws IOException {
      File dir = file;
      if(!file.isDirectory()) {
         recursive = false;
         dir = file.getParentFile();
      }

      while(dir != null && !dir.exists()) {
         recursive = true;
         dir = dir.getParentFile();
      }

      if(dir == null) {
         throw new FileNotFoundException("No ancestor found for " + file);
      } else {
         Kernel32 klib = Kernel32.INSTANCE;
         int mask = 7;
         int flags = 1107296256;
         WinNT.HANDLE handle = klib.CreateFile(file.getAbsolutePath(), 1, mask, (WinBase.SECURITY_ATTRIBUTES)null, 3, flags, (WinNT.HANDLE)null);
         if(WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
            throw new IOException("Unable to open " + file + " (" + klib.GetLastError() + ")");
         } else {
            int notifyMask = this.convertMask(eventMask);
            W32FileMonitor.FileInfo finfo = new W32FileMonitor.FileInfo(file, handle, notifyMask, recursive);
            this.fileMap.put(file, finfo);
            this.handleMap.put(handle, finfo);
            this.port = klib.CreateIoCompletionPort(handle, this.port, handle.getPointer(), 0);
            if(WinBase.INVALID_HANDLE_VALUE.equals(this.port)) {
               throw new IOException("Unable to create/use I/O Completion port for " + file + " (" + klib.GetLastError() + ")");
            } else if(!klib.ReadDirectoryChangesW(handle, finfo.info, finfo.info.size(), recursive, notifyMask, finfo.infoLength, finfo.overlapped, (WinNT.OVERLAPPED_COMPLETION_ROUTINE)null)) {
               int err = klib.GetLastError();
               throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ", handle " + handle + ": \'" + Kernel32Util.formatMessageFromLastErrorCode(err) + "\' (" + err + ")");
            } else {
               if(this.watcher == null) {
                  this.watcher = new Thread("W32 File Monitor-" + watcherThreadID++) {
                     public void run() {
                        while(true) {
                           W32FileMonitor.FileInfo finfo = W32FileMonitor.this.waitForChange();
                           if(finfo == null) {
                              synchronized(W32FileMonitor.this) {
                                 if(W32FileMonitor.this.fileMap.isEmpty()) {
                                    W32FileMonitor.this.watcher = null;
                                    return;
                                 }
                              }
                           } else {
                              try {
                                 W32FileMonitor.this.handleChanges(finfo);
                              } catch (IOException var5) {
                                 var5.printStackTrace();
                              }
                           }
                        }
                     }
                  };
                  this.watcher.setDaemon(true);
                  this.watcher.start();
               }

            }
         }
      }
   }

   protected synchronized void unwatch(File file) {
      W32FileMonitor.FileInfo finfo = (W32FileMonitor.FileInfo)this.fileMap.remove(file);
      if(finfo != null) {
         this.handleMap.remove(finfo.handle);
         Kernel32 klib = Kernel32.INSTANCE;
         klib.CloseHandle(finfo.handle);
      }

   }

   public synchronized void dispose() {
      this.disposing = true;
      int i = 0;
      Object[] keys = this.fileMap.keySet().toArray();

      while(!this.fileMap.isEmpty()) {
         this.unwatch((File)keys[i++]);
      }

      Kernel32 klib = Kernel32.INSTANCE;
      klib.PostQueuedCompletionStatus(this.port, 0, (Pointer)null, (WinBase.OVERLAPPED)null);
      klib.CloseHandle(this.port);
      this.port = null;
      this.watcher = null;
   }

   private class FileInfo {
      public final File file;
      public final WinNT.HANDLE handle;
      public final int notifyMask;
      public final boolean recursive;
      public final WinNT.FILE_NOTIFY_INFORMATION info = new WinNT.FILE_NOTIFY_INFORMATION(4096);
      public final IntByReference infoLength = new IntByReference();
      public final WinBase.OVERLAPPED overlapped = new WinBase.OVERLAPPED();

      public FileInfo(File f, WinNT.HANDLE h, int mask, boolean recurse) {
         this.file = f;
         this.handle = h;
         this.notifyMask = mask;
         this.recursive = recurse;
      }
   }
}
