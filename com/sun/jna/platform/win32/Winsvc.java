package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface Winsvc extends StdCallLibrary {
   int SERVICE_RUNS_IN_SYSTEM_PROCESS = 1;
   int SC_MANAGER_CONNECT = 1;
   int SC_MANAGER_CREATE_SERVICE = 2;
   int SC_MANAGER_ENUMERATE_SERVICE = 4;
   int SC_MANAGER_LOCK = 8;
   int SC_MANAGER_QUERY_LOCK_STATUS = 16;
   int SC_MANAGER_MODIFY_BOOT_CONFIG = 32;
   int SC_MANAGER_ALL_ACCESS = 983103;
   int SERVICE_QUERY_CONFIG = 1;
   int SERVICE_CHANGE_CONFIG = 2;
   int SERVICE_QUERY_STATUS = 4;
   int SERVICE_ENUMERATE_DEPENDENTS = 8;
   int SERVICE_START = 16;
   int SERVICE_STOP = 32;
   int SERVICE_PAUSE_CONTINUE = 64;
   int SERVICE_INTERROGATE = 128;
   int SERVICE_USER_DEFINED_CONTROL = 256;
   int SERVICE_ALL_ACCESS = 983551;
   int SERVICE_CONTROL_STOP = 1;
   int SERVICE_CONTROL_PAUSE = 2;
   int SERVICE_CONTROL_CONTINUE = 3;
   int SERVICE_CONTROL_INTERROGATE = 4;
   int SERVICE_CONTROL_PARAMCHANGE = 6;
   int SERVICE_CONTROL_NETBINDADD = 7;
   int SERVICE_CONTROL_NETBINDREMOVE = 8;
   int SERVICE_CONTROL_NETBINDENABLE = 9;
   int SERVICE_CONTROL_NETBINDDISABLE = 10;
   int SERVICE_STOPPED = 1;
   int SERVICE_START_PENDING = 2;
   int SERVICE_STOP_PENDING = 3;
   int SERVICE_RUNNING = 4;
   int SERVICE_CONTINUE_PENDING = 5;
   int SERVICE_PAUSE_PENDING = 6;
   int SERVICE_PAUSED = 7;
   int SERVICE_ACCEPT_STOP = 1;
   int SERVICE_ACCEPT_PAUSE_CONTINUE = 2;
   int SERVICE_ACCEPT_SHUTDOWN = 4;
   int SERVICE_ACCEPT_PARAMCHANGE = 8;
   int SERVICE_ACCEPT_NETBINDCHANGE = 16;
   int SERVICE_ACCEPT_HARDWAREPROFILECHANGE = 32;
   int SERVICE_ACCEPT_POWEREVENT = 64;
   int SERVICE_ACCEPT_SESSIONCHANGE = 128;
   int SERVICE_ACCEPT_PRESHUTDOWN = 256;
   int SERVICE_ACCEPT_TIMECHANGE = 512;
   int SERVICE_ACCEPT_TRIGGEREVENT = 1024;

   public static class SC_HANDLE extends WinNT.HANDLE {
   }

   public abstract static class SC_STATUS_TYPE {
      public static final int SC_STATUS_PROCESS_INFO = 0;
   }

   public static class SERVICE_STATUS extends Structure {
      public int dwServiceType;
      public int dwCurrentState;
      public int dwControlsAccepted;
      public int dwWin32ExitCode;
      public int dwServiceSpecificExitCode;
      public int dwCheckPoint;
      public int dwWaitHint;
   }

   public static class SERVICE_STATUS_PROCESS extends Structure {
      public int dwServiceType;
      public int dwCurrentState;
      public int dwControlsAccepted;
      public int dwWin32ExitCode;
      public int dwServiceSpecificExitCode;
      public int dwCheckPoint;
      public int dwWaitHint;
      public int dwProcessId;
      public int dwServiceFlags;

      public SERVICE_STATUS_PROCESS() {
      }

      public SERVICE_STATUS_PROCESS(int size) {
         super((Pointer)(new Memory((long)size)));
      }
   }
}
