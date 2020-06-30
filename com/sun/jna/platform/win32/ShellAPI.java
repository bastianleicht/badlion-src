package com.sun.jna.platform.win32;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface ShellAPI extends StdCallLibrary {
   int STRUCTURE_ALIGNMENT = Platform.is64Bit()?0:1;
   int FO_MOVE = 1;
   int FO_COPY = 2;
   int FO_DELETE = 3;
   int FO_RENAME = 4;
   int FOF_MULTIDESTFILES = 1;
   int FOF_CONFIRMMOUSE = 2;
   int FOF_SILENT = 4;
   int FOF_RENAMEONCOLLISION = 8;
   int FOF_NOCONFIRMATION = 16;
   int FOF_WANTMAPPINGHANDLE = 32;
   int FOF_ALLOWUNDO = 64;
   int FOF_FILESONLY = 128;
   int FOF_SIMPLEPROGRESS = 256;
   int FOF_NOCONFIRMMKDIR = 512;
   int FOF_NOERRORUI = 1024;
   int FOF_NOCOPYSECURITYATTRIBS = 2048;
   int FOF_NORECURSION = 4096;
   int FOF_NO_CONNECTED_ELEMENTS = 8192;
   int FOF_WANTNUKEWARNING = 16384;
   int FOF_NORECURSEREPARSE = 32768;
   int FOF_NO_UI = 1556;
   int PO_DELETE = 19;
   int PO_RENAME = 20;
   int PO_PORTCHANGE = 32;
   int PO_REN_PORT = 52;

   public static class SHFILEOPSTRUCT extends Structure {
      public WinNT.HANDLE hwnd;
      public int wFunc;
      public WString pFrom;
      public WString pTo;
      public short fFlags;
      public boolean fAnyOperationsAborted;
      public Pointer pNameMappings;
      public WString lpszProgressTitle;

      public String encodePaths(String[] paths) {
         String encoded = "";

         for(int i = 0; i < paths.length; ++i) {
            encoded = encoded + paths[i];
            encoded = encoded + "\u0000";
         }

         return encoded + "\u0000";
      }
   }
}
