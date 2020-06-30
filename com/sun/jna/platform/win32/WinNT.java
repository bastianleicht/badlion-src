package com.sun.jna.platform.win32;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {
   int DELETE = 65536;
   int READ_CONTROL = 131072;
   int WRITE_DAC = 262144;
   int WRITE_OWNER = 524288;
   int SYNCHRONIZE = 1048576;
   int STANDARD_RIGHTS_REQUIRED = 983040;
   int STANDARD_RIGHTS_READ = 131072;
   int STANDARD_RIGHTS_WRITE = 131072;
   int STANDARD_RIGHTS_EXECUTE = 131072;
   int STANDARD_RIGHTS_ALL = 2031616;
   int SPECIFIC_RIGHTS_ALL = 65535;
   int TOKEN_ASSIGN_PRIMARY = 1;
   int TOKEN_DUPLICATE = 2;
   int TOKEN_IMPERSONATE = 4;
   int TOKEN_QUERY = 8;
   int TOKEN_QUERY_SOURCE = 16;
   int TOKEN_ADJUST_PRIVILEGES = 32;
   int TOKEN_ADJUST_GROUPS = 64;
   int TOKEN_ADJUST_DEFAULT = 128;
   int TOKEN_ADJUST_SESSIONID = 256;
   int TOKEN_ALL_ACCESS_P = 983295;
   int TOKEN_ALL_ACCESS = 983551;
   int TOKEN_READ = 131080;
   int TOKEN_WRITE = 131296;
   int TOKEN_EXECUTE = 131072;
   int THREAD_TERMINATE = 1;
   int THREAD_SUSPEND_RESUME = 2;
   int THREAD_GET_CONTEXT = 8;
   int THREAD_SET_CONTEXT = 16;
   int THREAD_QUERY_INFORMATION = 64;
   int THREAD_SET_INFORMATION = 32;
   int THREAD_SET_THREAD_TOKEN = 128;
   int THREAD_IMPERSONATE = 256;
   int THREAD_DIRECT_IMPERSONATION = 512;
   int THREAD_SET_LIMITED_INFORMATION = 1024;
   int THREAD_QUERY_LIMITED_INFORMATION = 2048;
   int THREAD_ALL_ACCESS = 2032639;
   int FILE_READ_DATA = 1;
   int FILE_LIST_DIRECTORY = 1;
   int FILE_WRITE_DATA = 2;
   int FILE_ADD_FILE = 2;
   int FILE_APPEND_DATA = 4;
   int FILE_ADD_SUBDIRECTORY = 4;
   int FILE_CREATE_PIPE_INSTANCE = 4;
   int FILE_READ_EA = 8;
   int FILE_WRITE_EA = 16;
   int FILE_EXECUTE = 32;
   int FILE_TRAVERSE = 32;
   int FILE_DELETE_CHILD = 64;
   int FILE_READ_ATTRIBUTES = 128;
   int FILE_WRITE_ATTRIBUTES = 256;
   int FILE_ALL_ACCESS = 2032127;
   int FILE_GENERIC_READ = 1179785;
   int FILE_GENERIC_WRITE = 1179926;
   int FILE_GENERIC_EXECUTE = 1179808;
   int CREATE_NEW = 1;
   int CREATE_ALWAYS = 2;
   int OPEN_EXISTING = 3;
   int OPEN_ALWAYS = 4;
   int TRUNCATE_EXISTING = 5;
   int FILE_FLAG_WRITE_THROUGH = Integer.MIN_VALUE;
   int FILE_FLAG_OVERLAPPED = 1073741824;
   int FILE_FLAG_NO_BUFFERING = 536870912;
   int FILE_FLAG_RANDOM_ACCESS = 268435456;
   int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
   int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
   int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
   int FILE_FLAG_POSIX_SEMANTICS = 16777216;
   int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
   int FILE_FLAG_OPEN_NO_RECALL = 1048576;
   int GENERIC_READ = Integer.MIN_VALUE;
   int GENERIC_WRITE = 1073741824;
   int GENERIC_EXECUTE = 536870912;
   int GENERIC_ALL = 268435456;
   int ACCESS_SYSTEM_SECURITY = 16777216;
   int PAGE_READONLY = 2;
   int PAGE_READWRITE = 4;
   int PAGE_WRITECOPY = 8;
   int PAGE_EXECUTE = 16;
   int PAGE_EXECUTE_READ = 32;
   int PAGE_EXECUTE_READWRITE = 64;
   int SECTION_QUERY = 1;
   int SECTION_MAP_WRITE = 2;
   int SECTION_MAP_READ = 4;
   int SECTION_MAP_EXECUTE = 8;
   int SECTION_EXTEND_SIZE = 16;
   int FILE_SHARE_READ = 1;
   int FILE_SHARE_WRITE = 2;
   int FILE_SHARE_DELETE = 4;
   int FILE_ATTRIBUTE_READONLY = 1;
   int FILE_ATTRIBUTE_HIDDEN = 2;
   int FILE_ATTRIBUTE_SYSTEM = 4;
   int FILE_ATTRIBUTE_DIRECTORY = 16;
   int FILE_ATTRIBUTE_ARCHIVE = 32;
   int FILE_ATTRIBUTE_DEVICE = 64;
   int FILE_ATTRIBUTE_NORMAL = 128;
   int FILE_ATTRIBUTE_TEMPORARY = 256;
   int FILE_ATTRIBUTE_SPARSE_FILE = 512;
   int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
   int FILE_ATTRIBUTE_COMPRESSED = 2048;
   int FILE_ATTRIBUTE_OFFLINE = 4096;
   int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 8192;
   int FILE_ATTRIBUTE_ENCRYPTED = 16384;
   int FILE_ATTRIBUTE_VIRTUAL = 65536;
   int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
   int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
   int FILE_NOTIFY_CHANGE_NAME = 3;
   int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
   int FILE_NOTIFY_CHANGE_SIZE = 8;
   int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
   int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
   int FILE_NOTIFY_CHANGE_CREATION = 64;
   int FILE_NOTIFY_CHANGE_SECURITY = 256;
   int FILE_ACTION_ADDED = 1;
   int FILE_ACTION_REMOVED = 2;
   int FILE_ACTION_MODIFIED = 3;
   int FILE_ACTION_RENAMED_OLD_NAME = 4;
   int FILE_ACTION_RENAMED_NEW_NAME = 5;
   int FILE_CASE_SENSITIVE_SEARCH = 1;
   int FILE_CASE_PRESERVED_NAMES = 2;
   int FILE_UNICODE_ON_DISK = 4;
   int FILE_PERSISTENT_ACLS = 8;
   int FILE_FILE_COMPRESSION = 16;
   int FILE_VOLUME_QUOTAS = 32;
   int FILE_SUPPORTS_SPARSE_FILES = 64;
   int FILE_SUPPORTS_REPARSE_POINTS = 128;
   int FILE_SUPPORTS_REMOTE_STORAGE = 256;
   int FILE_VOLUME_IS_COMPRESSED = 32768;
   int FILE_SUPPORTS_OBJECT_IDS = 65536;
   int FILE_SUPPORTS_ENCRYPTION = 131072;
   int FILE_NAMED_STREAMS = 262144;
   int FILE_READ_ONLY_VOLUME = 524288;
   int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
   int FILE_SUPPORTS_TRANSACTIONS = 2097152;
   int KEY_QUERY_VALUE = 1;
   int KEY_SET_VALUE = 2;
   int KEY_CREATE_SUB_KEY = 4;
   int KEY_ENUMERATE_SUB_KEYS = 8;
   int KEY_NOTIFY = 16;
   int KEY_CREATE_LINK = 32;
   int KEY_WOW64_32KEY = 512;
   int KEY_WOW64_64KEY = 256;
   int KEY_WOW64_RES = 768;
   int KEY_READ = 131097;
   int KEY_WRITE = 131078;
   int KEY_EXECUTE = 131097;
   int KEY_ALL_ACCESS = 2031679;
   int REG_OPTION_RESERVED = 0;
   int REG_OPTION_NON_VOLATILE = 0;
   int REG_OPTION_VOLATILE = 1;
   int REG_OPTION_CREATE_LINK = 2;
   int REG_OPTION_BACKUP_RESTORE = 4;
   int REG_OPTION_OPEN_LINK = 8;
   int REG_LEGAL_OPTION = 15;
   int REG_CREATED_NEW_KEY = 1;
   int REG_OPENED_EXISTING_KEY = 2;
   int REG_STANDARD_FORMAT = 1;
   int REG_LATEST_FORMAT = 2;
   int REG_NO_COMPRESSION = 4;
   int REG_WHOLE_HIVE_VOLATILE = 1;
   int REG_REFRESH_HIVE = 2;
   int REG_NO_LAZY_FLUSH = 4;
   int REG_FORCE_RESTORE = 8;
   int REG_APP_HIVE = 16;
   int REG_PROCESS_PRIVATE = 32;
   int REG_START_JOURNAL = 64;
   int REG_HIVE_EXACT_FILE_GROWTH = 128;
   int REG_HIVE_NO_RM = 256;
   int REG_HIVE_SINGLE_LOG = 512;
   int REG_FORCE_UNLOAD = 1;
   int REG_NOTIFY_CHANGE_NAME = 1;
   int REG_NOTIFY_CHANGE_ATTRIBUTES = 2;
   int REG_NOTIFY_CHANGE_LAST_SET = 4;
   int REG_NOTIFY_CHANGE_SECURITY = 8;
   int REG_LEGAL_CHANGE_FILTER = 15;
   int REG_NONE = 0;
   int REG_SZ = 1;
   int REG_EXPAND_SZ = 2;
   int REG_BINARY = 3;
   int REG_DWORD = 4;
   int REG_DWORD_LITTLE_ENDIAN = 4;
   int REG_DWORD_BIG_ENDIAN = 5;
   int REG_LINK = 6;
   int REG_MULTI_SZ = 7;
   int REG_RESOURCE_LIST = 8;
   int REG_FULL_RESOURCE_DESCRIPTOR = 9;
   int REG_RESOURCE_REQUIREMENTS_LIST = 10;
   int REG_QWORD = 11;
   int REG_QWORD_LITTLE_ENDIAN = 11;
   int SID_REVISION = 1;
   int SID_MAX_SUB_AUTHORITIES = 15;
   int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
   int SECURITY_MAX_SID_SIZE = 68;
   int VER_EQUAL = 1;
   int VER_GREATER = 2;
   int VER_GREATER_EQUAL = 3;
   int VER_LESS = 4;
   int VER_LESS_EQUAL = 5;
   int VER_AND = 6;
   int VER_OR = 7;
   int VER_CONDITION_MASK = 7;
   int VER_NUM_BITS_PER_CONDITION_MASK = 3;
   int VER_MINORVERSION = 1;
   int VER_MAJORVERSION = 2;
   int VER_BUILDNUMBER = 4;
   int VER_PLATFORMID = 8;
   int VER_SERVICEPACKMINOR = 16;
   int VER_SERVICEPACKMAJOR = 32;
   int VER_SUITENAME = 64;
   int VER_PRODUCT_TYPE = 128;
   int VER_NT_WORKSTATION = 1;
   int VER_NT_DOMAIN_CONTROLLER = 2;
   int VER_NT_SERVER = 3;
   int VER_PLATFORM_WIN32s = 0;
   int VER_PLATFORM_WIN32_WINDOWS = 1;
   int VER_PLATFORM_WIN32_NT = 2;
   int EVENTLOG_SEQUENTIAL_READ = 1;
   int EVENTLOG_SEEK_READ = 2;
   int EVENTLOG_FORWARDS_READ = 4;
   int EVENTLOG_BACKWARDS_READ = 8;
   int EVENTLOG_SUCCESS = 0;
   int EVENTLOG_ERROR_TYPE = 1;
   int EVENTLOG_WARNING_TYPE = 2;
   int EVENTLOG_INFORMATION_TYPE = 4;
   int EVENTLOG_AUDIT_SUCCESS = 8;
   int EVENTLOG_AUDIT_FAILURE = 16;
   int SERVICE_KERNEL_DRIVER = 1;
   int SERVICE_FILE_SYSTEM_DRIVER = 2;
   int SERVICE_ADAPTER = 4;
   int SERVICE_RECOGNIZER_DRIVER = 8;
   int SERVICE_DRIVER = 11;
   int SERVICE_WIN32_OWN_PROCESS = 16;
   int SERVICE_WIN32_SHARE_PROCESS = 32;
   int SERVICE_WIN32 = 48;
   int SERVICE_INTERACTIVE_PROCESS = 256;
   int SERVICE_TYPE_ALL = 319;
   int STATUS_PENDING = 259;
   String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
   String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
   String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
   String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
   String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
   String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
   String SE_TCB_NAME = "SeTcbPrivilege";
   String SE_SECURITY_NAME = "SeSecurityPrivilege";
   String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
   String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
   String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
   String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
   String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
   String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
   String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
   String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
   String SE_BACKUP_NAME = "SeBackupPrivilege";
   String SE_RESTORE_NAME = "SeRestorePrivilege";
   String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
   String SE_DEBUG_NAME = "SeDebugPrivilege";
   String SE_AUDIT_NAME = "SeAuditPrivilege";
   String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
   String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
   String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
   String SE_UNDOCK_NAME = "SeUndockPrivilege";
   String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
   String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
   String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
   String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
   String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";
   int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 1;
   int SE_PRIVILEGE_ENABLED = 2;
   int SE_PRIVILEGE_REMOVED = 4;
   int SE_PRIVILEGE_USED_FOR_ACCESS = Integer.MIN_VALUE;
   int PROCESS_TERMINATE = 1;
   int PROCESS_SYNCHRONIZE = 1048576;
   int OWNER_SECURITY_INFORMATION = 1;
   int GROUP_SECURITY_INFORMATION = 2;
   int DACL_SECURITY_INFORMATION = 4;
   int SACL_SECURITY_INFORMATION = 8;
   int LABEL_SECURITY_INFORMATION = 16;
   int PROTECTED_DACL_SECURITY_INFORMATION = Integer.MIN_VALUE;
   int PROTECTED_SACL_SECURITY_INFORMATION = 1073741824;
   int UNPROTECTED_DACL_SECURITY_INFORMATION = 536870912;
   int UNPROTECTED_SACL_SECURITY_INFORMATION = 268435456;
   byte ACCESS_ALLOWED_ACE_TYPE = 0;
   byte ACCESS_DENIED_ACE_TYPE = 1;
   byte SYSTEM_AUDIT_ACE_TYPE = 2;
   byte SYSTEM_ALARM_ACE_TYPE = 3;
   byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE = 4;
   byte ACCESS_ALLOWED_OBJECT_ACE_TYPE = 5;
   byte ACCESS_DENIED_OBJECT_ACE_TYPE = 6;
   byte SYSTEM_AUDIT_OBJECT_ACE_TYPE = 7;
   byte SYSTEM_ALARM_OBJECT_ACE_TYPE = 8;
   byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE = 9;
   byte ACCESS_DENIED_CALLBACK_ACE_TYPE = 10;
   byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE = 11;
   byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE = 12;
   byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE = 13;
   byte SYSTEM_ALARM_CALLBACK_ACE_TYPE = 14;
   byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE = 15;
   byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE = 16;
   byte SYSTEM_MANDATORY_LABEL_ACE_TYPE = 17;
   byte OBJECT_INHERIT_ACE = 1;
   byte CONTAINER_INHERIT_ACE = 2;
   byte NO_PROPAGATE_INHERIT_ACE = 4;
   byte INHERIT_ONLY_ACE = 8;
   byte INHERITED_ACE = 16;
   byte VALID_INHERIT_FLAGS = 31;

   Pointer LocalFree(Pointer var1);

   Pointer GlobalFree(Pointer var1);

   WinDef.HMODULE GetModuleHandle(String var1);

   void GetSystemTime(WinBase.SYSTEMTIME var1);

   int GetTickCount();

   int GetCurrentThreadId();

   WinNT.HANDLE GetCurrentThread();

   int GetCurrentProcessId();

   WinNT.HANDLE GetCurrentProcess();

   int GetProcessId(WinNT.HANDLE var1);

   int GetProcessVersion(int var1);

   boolean GetExitCodeProcess(WinNT.HANDLE var1, IntByReference var2);

   boolean TerminateProcess(WinNT.HANDLE var1, int var2);

   int GetLastError();

   void SetLastError(int var1);

   int GetDriveType(String var1);

   int FormatMessage(int var1, Pointer var2, int var3, int var4, Pointer var5, int var6, Pointer var7);

   int FormatMessage(int var1, Pointer var2, int var3, int var4, PointerByReference var5, int var6, Pointer var7);

   WinNT.HANDLE CreateFile(String var1, int var2, int var3, WinBase.SECURITY_ATTRIBUTES var4, int var5, int var6, WinNT.HANDLE var7);

   boolean CopyFile(String var1, String var2, boolean var3);

   boolean MoveFile(String var1, String var2);

   boolean MoveFileEx(String var1, String var2, WinDef.DWORD var3);

   boolean CreateDirectory(String var1, WinBase.SECURITY_ATTRIBUTES var2);

   boolean ReadFile(WinNT.HANDLE var1, Pointer var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

   WinNT.HANDLE CreateIoCompletionPort(WinNT.HANDLE var1, WinNT.HANDLE var2, Pointer var3, int var4);

   boolean GetQueuedCompletionStatus(WinNT.HANDLE var1, IntByReference var2, BaseTSD.ULONG_PTRByReference var3, PointerByReference var4, int var5);

   boolean PostQueuedCompletionStatus(WinNT.HANDLE var1, int var2, Pointer var3, WinBase.OVERLAPPED var4);

   int WaitForSingleObject(WinNT.HANDLE var1, int var2);

   int WaitForMultipleObjects(int var1, WinNT.HANDLE[] var2, boolean var3, int var4);

   boolean DuplicateHandle(WinNT.HANDLE var1, WinNT.HANDLE var2, WinNT.HANDLE var3, WinNT.HANDLEByReference var4, int var5, boolean var6, int var7);

   boolean CloseHandle(WinNT.HANDLE var1);

   boolean ReadDirectoryChangesW(WinNT.HANDLE var1, WinNT.FILE_NOTIFY_INFORMATION var2, int var3, boolean var4, int var5, IntByReference var6, WinBase.OVERLAPPED var7, WinNT.OVERLAPPED_COMPLETION_ROUTINE var8);

   int GetShortPathName(String var1, char[] var2, int var3);

   Pointer LocalAlloc(int var1, int var2);

   boolean WriteFile(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

   WinNT.HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES var1, boolean var2, boolean var3, String var4);

   boolean SetEvent(WinNT.HANDLE var1);

   boolean PulseEvent(WinNT.HANDLE var1);

   WinNT.HANDLE CreateFileMapping(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, int var4, int var5, String var6);

   Pointer MapViewOfFile(WinNT.HANDLE var1, int var2, int var3, int var4, int var5);

   boolean UnmapViewOfFile(Pointer var1);

   boolean GetComputerName(char[] var1, IntByReference var2);

   WinNT.HANDLE OpenThread(int var1, boolean var2, int var3);

   boolean CreateProcess(String var1, String var2, WinBase.SECURITY_ATTRIBUTES var3, WinBase.SECURITY_ATTRIBUTES var4, boolean var5, WinDef.DWORD var6, Pointer var7, String var8, WinBase.STARTUPINFO var9, WinBase.PROCESS_INFORMATION.ByReference var10);

   WinNT.HANDLE OpenProcess(int var1, boolean var2, int var3);

   WinDef.DWORD GetTempPath(WinDef.DWORD var1, char[] var2);

   boolean SetEnvironmentVariable(String var1, String var2);

   WinDef.DWORD GetVersion();

   boolean GetVersionEx(WinNT.OSVERSIONINFO var1);

   boolean GetVersionEx(WinNT.OSVERSIONINFOEX var1);

   void GetSystemInfo(WinBase.SYSTEM_INFO var1);

   void GetNativeSystemInfo(WinBase.SYSTEM_INFO var1);

   boolean IsWow64Process(WinNT.HANDLE var1, IntByReference var2);

   boolean GlobalMemoryStatusEx(WinBase.MEMORYSTATUSEX var1);

   boolean GetFileTime(WinNT.HANDLE var1, WinBase.FILETIME.ByReference var2, WinBase.FILETIME.ByReference var3, WinBase.FILETIME.ByReference var4);

   int SetFileTime(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4);

   boolean SetFileAttributes(String var1, WinDef.DWORD var2);

   WinDef.DWORD GetLogicalDriveStrings(WinDef.DWORD var1, char[] var2);

   boolean GetDiskFreeSpaceEx(String var1, WinNT.LARGE_INTEGER.ByReference var2, WinNT.LARGE_INTEGER.ByReference var3, WinNT.LARGE_INTEGER.ByReference var4);

   boolean DeleteFile(String var1);

   boolean CreatePipe(WinNT.HANDLEByReference var1, WinNT.HANDLEByReference var2, WinBase.SECURITY_ATTRIBUTES var3, int var4);

   boolean SetHandleInformation(WinNT.HANDLE var1, int var2, int var3);

   int GetFileAttributes(String var1);

   boolean DeviceIoControl(WinNT.HANDLE var1, int var2, Pointer var3, int var4, Pointer var5, int var6, IntByReference var7, Pointer var8);

   boolean GetDiskFreeSpaceEx(String var1, LongByReference var2, LongByReference var3, LongByReference var4);

   WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD var1, WinDef.DWORD var2);

   boolean Process32First(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32.ByReference var2);

   boolean Process32Next(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32.ByReference var2);

   public abstract static class ACCESS_ACEStructure extends WinNT.ACEStructure {
      public int Mask;
      public WinDef.DWORD SidStart;

      public ACCESS_ACEStructure(Pointer p) {
         super(p);
         this.read();
         int sizeOfSID = super.AceSize - this.size() + 4;
         int offsetOfSID = 8;
         byte[] data = p.getByteArray((long)offsetOfSID, sizeOfSID);
         this.psid = new WinNT.PSID(data);
      }
   }

   public static class ACCESS_ALLOWED_ACE extends WinNT.ACCESS_ACEStructure {
      public ACCESS_ALLOWED_ACE(Pointer p) {
         super(p);
      }
   }

   public static class ACCESS_DENIED_ACE extends WinNT.ACCESS_ACEStructure {
      public ACCESS_DENIED_ACE(Pointer p) {
         super(p);
      }
   }

   public abstract static class ACEStructure extends Structure {
      public byte AceType;
      public byte AceFlags;
      public short AceSize;
      WinNT.PSID psid;

      public ACEStructure(Pointer p) {
         super(p);
      }

      public String getSidString() {
         return Advapi32Util.convertSidToStringSid(this.psid);
      }

      public WinNT.PSID getSID() {
         return this.psid;
      }
   }

   public static class ACE_HEADER extends WinNT.ACEStructure {
      public ACE_HEADER(Pointer p) {
         super(p);
         this.read();
      }
   }

   public static class ACL extends Structure {
      public byte AclRevision;
      public byte Sbz1;
      public short AclSize;
      public short AceCount;
      public short Sbz2;
      WinNT.ACCESS_ACEStructure[] ACEs;

      public ACL() {
      }

      public ACL(Pointer pointer) {
         super(pointer);
         this.read();
         this.ACEs = new WinNT.ACCESS_ACEStructure[this.AceCount];
         int offset = this.size();

         for(int i = 0; i < this.AceCount; ++i) {
            Pointer share = pointer.share((long)offset);
            byte aceType = share.getByte(0L);
            WinNT.ACCESS_ACEStructure ace = null;
            switch(aceType) {
            case 0:
               ace = new WinNT.ACCESS_ALLOWED_ACE(share);
               break;
            case 1:
               ace = new WinNT.ACCESS_DENIED_ACE(share);
               break;
            default:
               throw new IllegalArgumentException("Unknwon ACE type " + aceType);
            }

            this.ACEs[i] = ace;
            offset += ace.AceSize;
         }

      }

      public WinNT.ACCESS_ACEStructure[] getACEStructures() {
         return this.ACEs;
      }
   }

   public static class EVENTLOGRECORD extends Structure {
      public WinDef.DWORD Length;
      public WinDef.DWORD Reserved;
      public WinDef.DWORD RecordNumber;
      public WinDef.DWORD TimeGenerated;
      public WinDef.DWORD TimeWritten;
      public WinDef.DWORD EventID;
      public WinDef.WORD EventType;
      public WinDef.WORD NumStrings;
      public WinDef.WORD EventCategory;
      public WinDef.WORD ReservedFlags;
      public WinDef.DWORD ClosingRecordNumber;
      public WinDef.DWORD StringOffset;
      public WinDef.DWORD UserSidLength;
      public WinDef.DWORD UserSidOffset;
      public WinDef.DWORD DataLength;
      public WinDef.DWORD DataOffset;

      public EVENTLOGRECORD() {
      }

      public EVENTLOGRECORD(Pointer p) {
         super(p);
         this.read();
      }
   }

   public static class FILE_NOTIFY_INFORMATION extends Structure {
      public int NextEntryOffset;
      public int Action;
      public int FileNameLength;
      public char[] FileName = new char[1];

      private FILE_NOTIFY_INFORMATION() {
      }

      public FILE_NOTIFY_INFORMATION(int size) {
         if(size < this.size()) {
            throw new IllegalArgumentException("Size must greater than " + this.size() + ", requested " + size);
         } else {
            this.allocateMemory(size);
         }
      }

      public String getFilename() {
         return new String(this.FileName, 0, this.FileNameLength / 2);
      }

      public void read() {
         this.FileName = new char[0];
         super.read();
         this.FileName = this.getPointer().getCharArray(12L, this.FileNameLength / 2);
      }

      public WinNT.FILE_NOTIFY_INFORMATION next() {
         if(this.NextEntryOffset == 0) {
            return null;
         } else {
            WinNT.FILE_NOTIFY_INFORMATION next = new WinNT.FILE_NOTIFY_INFORMATION();
            next.useMemory(this.getPointer(), this.NextEntryOffset);
            next.read();
            return next;
         }
      }
   }

   public static class HANDLE extends PointerType {
      private boolean immutable;

      public HANDLE() {
      }

      public HANDLE(Pointer p) {
         this.setPointer(p);
         this.immutable = true;
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         Object o = super.fromNative(nativeValue, context);
         return WinBase.INVALID_HANDLE_VALUE.equals(o)?WinBase.INVALID_HANDLE_VALUE:o;
      }

      public void setPointer(Pointer p) {
         if(this.immutable) {
            throw new UnsupportedOperationException("immutable reference");
         } else {
            super.setPointer(p);
         }
      }
   }

   public static class HANDLEByReference extends com.sun.jna.ptr.ByReference {
      public HANDLEByReference() {
         this((WinNT.HANDLE)null);
      }

      public HANDLEByReference(WinNT.HANDLE h) {
         super(Pointer.SIZE);
         this.setValue(h);
      }

      public void setValue(WinNT.HANDLE h) {
         this.getPointer().setPointer(0L, h != null?h.getPointer():null);
      }

      public WinNT.HANDLE getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         if(p == null) {
            return null;
         } else if(WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
            return WinBase.INVALID_HANDLE_VALUE;
         } else {
            WinNT.HANDLE h = new WinNT.HANDLE();
            h.setPointer(p);
            return h;
         }
      }
   }

   public static class HRESULT extends NativeLong {
      public HRESULT() {
      }

      public HRESULT(int value) {
         super((long)value);
      }
   }

   public static class LARGE_INTEGER extends Structure {
      public WinNT.LARGE_INTEGER.UNION u;

      public WinDef.DWORD getLow() {
         return this.u.lh.LowPart;
      }

      public WinDef.DWORD getHigh() {
         return this.u.lh.HighPart;
      }

      public long getValue() {
         return this.u.value;
      }

      public static class ByReference extends WinNT.LARGE_INTEGER implements Structure.ByReference {
      }

      public static class LowHigh extends Structure {
         public WinDef.DWORD LowPart;
         public WinDef.DWORD HighPart;
      }

      public static class UNION extends Union {
         public WinNT.LARGE_INTEGER.LowHigh lh;
         public long value;
      }
   }

   public static class LUID extends Structure {
      public int LowPart;
      public int HighPart;
   }

   public static class LUID_AND_ATTRIBUTES extends Structure {
      public WinNT.LUID Luid;
      public WinDef.DWORD Attributes;

      public LUID_AND_ATTRIBUTES() {
      }

      public LUID_AND_ATTRIBUTES(WinNT.LUID luid, WinDef.DWORD attributes) {
         this.Luid = luid;
         this.Attributes = attributes;
      }
   }

   public static class OSVERSIONINFO extends Structure {
      public WinDef.DWORD dwOSVersionInfoSize;
      public WinDef.DWORD dwMajorVersion;
      public WinDef.DWORD dwMinorVersion;
      public WinDef.DWORD dwBuildNumber;
      public WinDef.DWORD dwPlatformId;
      public char[] szCSDVersion;

      public OSVERSIONINFO() {
         this.szCSDVersion = new char[128];
         this.dwOSVersionInfoSize = new WinDef.DWORD((long)this.size());
      }

      public OSVERSIONINFO(Pointer memory) {
         this.useMemory(memory);
         this.read();
      }
   }

   public static class OSVERSIONINFOEX extends Structure {
      public WinDef.DWORD dwOSVersionInfoSize;
      public WinDef.DWORD dwMajorVersion;
      public WinDef.DWORD dwMinorVersion;
      public WinDef.DWORD dwBuildNumber;
      public WinDef.DWORD dwPlatformId;
      public char[] szCSDVersion;
      public WinDef.WORD wServicePackMajor;
      public WinDef.WORD wServicePackMinor;
      public WinDef.WORD wSuiteMask;
      public byte wProductType;
      public byte wReserved;

      public OSVERSIONINFOEX() {
         this.szCSDVersion = new char[128];
         this.dwOSVersionInfoSize = new WinDef.DWORD((long)this.size());
      }

      public OSVERSIONINFOEX(Pointer memory) {
         this.useMemory(memory);
         this.read();
      }
   }

   public interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallLibrary.StdCallCallback {
      void callback(int var1, int var2, WinBase.OVERLAPPED var3);
   }

   public static class PSID extends Structure {
      public Pointer sid;

      public PSID() {
      }

      public PSID(byte[] data) {
         super((Pointer)(new Memory((long)data.length)));
         this.getPointer().write(0L, (byte[])data, 0, data.length);
         this.read();
      }

      public PSID(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public PSID(Pointer memory) {
         super(memory);
         this.read();
      }

      public byte[] getBytes() {
         int len = Advapi32.INSTANCE.GetLengthSid(this);
         return this.getPointer().getByteArray(0L, len);
      }

      public static class ByReference extends WinNT.PSID implements Structure.ByReference {
      }
   }

   public static class PSIDByReference extends com.sun.jna.ptr.ByReference {
      public PSIDByReference() {
         this((WinNT.PSID)null);
      }

      public PSIDByReference(WinNT.PSID h) {
         super(Pointer.SIZE);
         this.setValue(h);
      }

      public void setValue(WinNT.PSID h) {
         this.getPointer().setPointer(0L, h != null?h.getPointer():null);
      }

      public WinNT.PSID getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         return p == null?null:new WinNT.PSID(p);
      }
   }

   public static class SECURITY_DESCRIPTOR extends Structure {
      public byte[] data;

      public SECURITY_DESCRIPTOR() {
      }

      public SECURITY_DESCRIPTOR(byte[] data) {
         this.data = data;
         this.useMemory(new Memory((long)data.length));
      }

      public SECURITY_DESCRIPTOR(Pointer memory) {
         super(memory);
      }

      public static class ByReference extends WinNT.SECURITY_DESCRIPTOR implements Structure.ByReference {
      }
   }

   public static class SECURITY_DESCRIPTOR_RELATIVE extends Structure {
      public byte Revision;
      public byte Sbz1;
      public short Control;
      public int Owner;
      public int Group;
      public int Sacl;
      public int Dacl;
      private WinNT.ACL DACL = null;

      public SECURITY_DESCRIPTOR_RELATIVE() {
      }

      public SECURITY_DESCRIPTOR_RELATIVE(byte[] data) {
         super((Pointer)(new Memory((long)data.length)));
         this.getPointer().write(0L, (byte[])data, 0, data.length);
         this.setDacl();
      }

      public SECURITY_DESCRIPTOR_RELATIVE(Memory memory) {
         super((Pointer)memory);
         this.setDacl();
      }

      public WinNT.ACL getDiscretionaryACL() {
         return this.DACL;
      }

      private final void setDacl() {
         this.read();
         if(this.Dacl != 0) {
            this.DACL = new WinNT.ACL(this.getPointer().share((long)this.Dacl));
         }

      }

      public static class ByReference extends WinNT.SECURITY_DESCRIPTOR_RELATIVE implements Structure.ByReference {
      }
   }

   public abstract static class SECURITY_IMPERSONATION_LEVEL {
      public static final int SecurityAnonymous = 0;
      public static final int SecurityIdentification = 1;
      public static final int SecurityImpersonation = 2;
      public static final int SecurityDelegation = 3;
   }

   public static class SID_AND_ATTRIBUTES extends Structure {
      public WinNT.PSID.ByReference Sid;
      public int Attributes;

      public SID_AND_ATTRIBUTES() {
      }

      public SID_AND_ATTRIBUTES(Pointer memory) {
         super(memory);
      }
   }

   public abstract static class SID_NAME_USE {
      public static final int SidTypeUser = 1;
      public static final int SidTypeGroup = 2;
      public static final int SidTypeDomain = 3;
      public static final int SidTypeAlias = 4;
      public static final int SidTypeWellKnownGroup = 5;
      public static final int SidTypeDeletedAccount = 6;
      public static final int SidTypeInvalid = 7;
      public static final int SidTypeUnknown = 8;
      public static final int SidTypeComputer = 9;
      public static final int SidTypeLabel = 10;
   }

   public static class TOKEN_GROUPS extends Structure {
      public int GroupCount;
      public WinNT.SID_AND_ATTRIBUTES Group0;

      public TOKEN_GROUPS() {
      }

      public TOKEN_GROUPS(Pointer memory) {
         super(memory);
         this.read();
      }

      public TOKEN_GROUPS(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public WinNT.SID_AND_ATTRIBUTES[] getGroups() {
         return (WinNT.SID_AND_ATTRIBUTES[])((WinNT.SID_AND_ATTRIBUTES[])this.Group0.toArray(this.GroupCount));
      }
   }

   public abstract static class TOKEN_INFORMATION_CLASS {
      public static final int TokenUser = 1;
      public static final int TokenGroups = 2;
      public static final int TokenPrivileges = 3;
      public static final int TokenOwner = 4;
      public static final int TokenPrimaryGroup = 5;
      public static final int TokenDefaultDacl = 6;
      public static final int TokenSource = 7;
      public static final int TokenType = 8;
      public static final int TokenImpersonationLevel = 9;
      public static final int TokenStatistics = 10;
      public static final int TokenRestrictedSids = 11;
      public static final int TokenSessionId = 12;
      public static final int TokenGroupsAndPrivileges = 13;
      public static final int TokenSessionReference = 14;
      public static final int TokenSandBoxInert = 15;
      public static final int TokenAuditPolicy = 16;
      public static final int TokenOrigin = 17;
      public static final int TokenElevationType = 18;
      public static final int TokenLinkedToken = 19;
      public static final int TokenElevation = 20;
      public static final int TokenHasRestrictions = 21;
      public static final int TokenAccessInformation = 22;
      public static final int TokenVirtualizationAllowed = 23;
      public static final int TokenVirtualizationEnabled = 24;
      public static final int TokenIntegrityLevel = 25;
      public static final int TokenUIAccess = 26;
      public static final int TokenMandatoryPolicy = 27;
      public static final int TokenLogonSid = 28;
   }

   public static class TOKEN_OWNER extends Structure {
      public WinNT.PSID.ByReference Owner;

      public TOKEN_OWNER() {
      }

      public TOKEN_OWNER(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public TOKEN_OWNER(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   public static class TOKEN_PRIVILEGES extends Structure {
      public WinDef.DWORD PrivilegeCount;
      public WinNT.LUID_AND_ATTRIBUTES[] Privileges;

      public TOKEN_PRIVILEGES(int nbOfPrivileges) {
         this.PrivilegeCount = new WinDef.DWORD((long)nbOfPrivileges);
         this.Privileges = new WinNT.LUID_AND_ATTRIBUTES[nbOfPrivileges];
      }
   }

   public abstract static class TOKEN_TYPE {
      public static final int TokenPrimary = 1;
      public static final int TokenImpersonation = 2;
   }

   public static class TOKEN_USER extends Structure {
      public WinNT.SID_AND_ATTRIBUTES User;

      public TOKEN_USER() {
      }

      public TOKEN_USER(Pointer memory) {
         super(memory);
         this.read();
      }

      public TOKEN_USER(int size) {
         super((Pointer)(new Memory((long)size)));
      }
   }

   public abstract static class WELL_KNOWN_SID_TYPE {
      public static final int WinNullSid = 0;
      public static final int WinWorldSid = 1;
      public static final int WinLocalSid = 2;
      public static final int WinCreatorOwnerSid = 3;
      public static final int WinCreatorGroupSid = 4;
      public static final int WinCreatorOwnerServerSid = 5;
      public static final int WinCreatorGroupServerSid = 6;
      public static final int WinNtAuthoritySid = 7;
      public static final int WinDialupSid = 8;
      public static final int WinNetworkSid = 9;
      public static final int WinBatchSid = 10;
      public static final int WinInteractiveSid = 11;
      public static final int WinServiceSid = 12;
      public static final int WinAnonymousSid = 13;
      public static final int WinProxySid = 14;
      public static final int WinEnterpriseControllersSid = 15;
      public static final int WinSelfSid = 16;
      public static final int WinAuthenticatedUserSid = 17;
      public static final int WinRestrictedCodeSid = 18;
      public static final int WinTerminalServerSid = 19;
      public static final int WinRemoteLogonIdSid = 20;
      public static final int WinLogonIdsSid = 21;
      public static final int WinLocalSystemSid = 22;
      public static final int WinLocalServiceSid = 23;
      public static final int WinNetworkServiceSid = 24;
      public static final int WinBuiltinDomainSid = 25;
      public static final int WinBuiltinAdministratorsSid = 26;
      public static final int WinBuiltinUsersSid = 27;
      public static final int WinBuiltinGuestsSid = 28;
      public static final int WinBuiltinPowerUsersSid = 29;
      public static final int WinBuiltinAccountOperatorsSid = 30;
      public static final int WinBuiltinSystemOperatorsSid = 31;
      public static final int WinBuiltinPrintOperatorsSid = 32;
      public static final int WinBuiltinBackupOperatorsSid = 33;
      public static final int WinBuiltinReplicatorSid = 34;
      public static final int WinBuiltinPreWindows2000CompatibleAccessSid = 35;
      public static final int WinBuiltinRemoteDesktopUsersSid = 36;
      public static final int WinBuiltinNetworkConfigurationOperatorsSid = 37;
      public static final int WinAccountAdministratorSid = 38;
      public static final int WinAccountGuestSid = 39;
      public static final int WinAccountKrbtgtSid = 40;
      public static final int WinAccountDomainAdminsSid = 41;
      public static final int WinAccountDomainUsersSid = 42;
      public static final int WinAccountDomainGuestsSid = 43;
      public static final int WinAccountComputersSid = 44;
      public static final int WinAccountControllersSid = 45;
      public static final int WinAccountCertAdminsSid = 46;
      public static final int WinAccountSchemaAdminsSid = 47;
      public static final int WinAccountEnterpriseAdminsSid = 48;
      public static final int WinAccountPolicyAdminsSid = 49;
      public static final int WinAccountRasAndIasServersSid = 50;
      public static final int WinNTLMAuthenticationSid = 51;
      public static final int WinDigestAuthenticationSid = 52;
      public static final int WinSChannelAuthenticationSid = 53;
      public static final int WinThisOrganizationSid = 54;
      public static final int WinOtherOrganizationSid = 55;
      public static final int WinBuiltinIncomingForestTrustBuildersSid = 56;
      public static final int WinBuiltinPerfMonitoringUsersSid = 57;
      public static final int WinBuiltinPerfLoggingUsersSid = 58;
      public static final int WinBuiltinAuthorizationAccessSid = 59;
      public static final int WinBuiltinTerminalServerLicenseServersSid = 60;
      public static final int WinBuiltinDCOMUsersSid = 61;
      public static final int WinBuiltinIUsersSid = 62;
      public static final int WinIUserSid = 63;
      public static final int WinBuiltinCryptoOperatorsSid = 64;
      public static final int WinUntrustedLabelSid = 65;
      public static final int WinLowLabelSid = 66;
      public static final int WinMediumLabelSid = 67;
      public static final int WinHighLabelSid = 68;
      public static final int WinSystemLabelSid = 69;
      public static final int WinWriteRestrictedCodeSid = 70;
      public static final int WinCreatorOwnerRightsSid = 71;
      public static final int WinCacheablePrincipalsGroupSid = 72;
      public static final int WinNonCacheablePrincipalsGroupSid = 73;
      public static final int WinEnterpriseReadonlyControllersSid = 74;
      public static final int WinAccountReadonlyControllersSid = 75;
      public static final int WinBuiltinEventLogReadersGroup = 76;
   }
}
