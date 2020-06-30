package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.FileUtils;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacFileUtils extends FileUtils {
   public boolean hasTrash() {
      return true;
   }

   public void moveToTrash(File[] files) throws IOException {
      File home = new File(System.getProperty("user.home"));
      File trash = new File(home, ".Trash");
      if(!trash.exists()) {
         throw new IOException("The Trash was not found in its expected location (" + trash + ")");
      } else {
         List<File> failed = new ArrayList();

         for(int i = 0; i < files.length; ++i) {
            File src = files[i];
            if(MacFileUtils.FileManager.INSTANCE.FSPathMoveObjectToTrashSync(src.getAbsolutePath(), (PointerByReference)null, 0) != 0) {
               failed.add(src);
            }
         }

         if(failed.size() > 0) {
            throw new IOException("The following files could not be trashed: " + failed);
         }
      }
   }

   public interface FileManager extends Library {
      int kFSFileOperationDefaultOptions = 0;
      int kFSFileOperationsOverwrite = 1;
      int kFSFileOperationsSkipSourcePermissionErrors = 2;
      int kFSFileOperationsDoNotMoveAcrossVolumes = 4;
      int kFSFileOperationsSkipPreflight = 8;
      MacFileUtils.FileManager INSTANCE = (MacFileUtils.FileManager)Native.loadLibrary("CoreServices", MacFileUtils.FileManager.class);

      int FSPathMoveObjectToTrashSync(String var1, PointerByReference var2, int var3);
   }
}
