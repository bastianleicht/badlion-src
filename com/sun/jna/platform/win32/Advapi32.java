package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.Winsvc;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Advapi32 extends StdCallLibrary {
   Advapi32 INSTANCE = (Advapi32)Native.loadLibrary("Advapi32", Advapi32.class, W32APIOptions.UNICODE_OPTIONS);

   boolean GetUserNameW(char[] var1, IntByReference var2);

   boolean LookupAccountName(String var1, String var2, WinNT.PSID var3, IntByReference var4, char[] var5, IntByReference var6, PointerByReference var7);

   boolean LookupAccountSid(String var1, WinNT.PSID var2, char[] var3, IntByReference var4, char[] var5, IntByReference var6, PointerByReference var7);

   boolean ConvertSidToStringSid(WinNT.PSID var1, PointerByReference var2);

   boolean ConvertStringSidToSid(String var1, WinNT.PSIDByReference var2);

   int GetLengthSid(WinNT.PSID var1);

   boolean IsValidSid(WinNT.PSID var1);

   boolean IsWellKnownSid(WinNT.PSID var1, int var2);

   boolean CreateWellKnownSid(int var1, WinNT.PSID var2, WinNT.PSID var3, IntByReference var4);

   boolean LogonUser(String var1, String var2, String var3, int var4, int var5, WinNT.HANDLEByReference var6);

   boolean OpenThreadToken(WinNT.HANDLE var1, int var2, boolean var3, WinNT.HANDLEByReference var4);

   boolean OpenProcessToken(WinNT.HANDLE var1, int var2, WinNT.HANDLEByReference var3);

   boolean DuplicateToken(WinNT.HANDLE var1, int var2, WinNT.HANDLEByReference var3);

   boolean DuplicateTokenEx(WinNT.HANDLE var1, int var2, WinBase.SECURITY_ATTRIBUTES var3, int var4, int var5, WinNT.HANDLEByReference var6);

   boolean GetTokenInformation(WinNT.HANDLE var1, int var2, Structure var3, int var4, IntByReference var5);

   boolean ImpersonateLoggedOnUser(WinNT.HANDLE var1);

   boolean ImpersonateSelf(int var1);

   boolean RevertToSelf();

   int RegOpenKeyEx(WinReg.HKEY var1, String var2, int var3, int var4, WinReg.HKEYByReference var5);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, char[] var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, byte[] var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, LongByReference var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, Pointer var5, IntByReference var6);

   int RegCloseKey(WinReg.HKEY var1);

   int RegDeleteValue(WinReg.HKEY var1, String var2);

   int RegSetValueEx(WinReg.HKEY var1, String var2, int var3, int var4, char[] var5, int var6);

   int RegSetValueEx(WinReg.HKEY var1, String var2, int var3, int var4, byte[] var5, int var6);

   int RegCreateKeyEx(WinReg.HKEY var1, String var2, int var3, String var4, int var5, int var6, WinBase.SECURITY_ATTRIBUTES var7, WinReg.HKEYByReference var8, IntByReference var9);

   int RegDeleteKey(WinReg.HKEY var1, String var2);

   int RegEnumKeyEx(WinReg.HKEY var1, int var2, char[] var3, IntByReference var4, IntByReference var5, char[] var6, IntByReference var7, WinBase.FILETIME var8);

   int RegEnumValue(WinReg.HKEY var1, int var2, char[] var3, IntByReference var4, IntByReference var5, IntByReference var6, byte[] var7, IntByReference var8);

   int RegQueryInfoKey(WinReg.HKEY var1, char[] var2, IntByReference var3, IntByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9, IntByReference var10, IntByReference var11, WinBase.FILETIME var12);

   WinNT.HANDLE RegisterEventSource(String var1, String var2);

   boolean DeregisterEventSource(WinNT.HANDLE var1);

   WinNT.HANDLE OpenEventLog(String var1, String var2);

   boolean CloseEventLog(WinNT.HANDLE var1);

   boolean GetNumberOfEventLogRecords(WinNT.HANDLE var1, IntByReference var2);

   boolean ReportEvent(WinNT.HANDLE var1, int var2, int var3, int var4, WinNT.PSID var5, int var6, int var7, String[] var8, Pointer var9);

   boolean ClearEventLog(WinNT.HANDLE var1, String var2);

   boolean BackupEventLog(WinNT.HANDLE var1, String var2);

   WinNT.HANDLE OpenBackupEventLog(String var1, String var2);

   boolean ReadEventLog(WinNT.HANDLE var1, int var2, int var3, Pointer var4, int var5, IntByReference var6, IntByReference var7);

   boolean GetOldestEventLogRecord(WinNT.HANDLE var1, IntByReference var2);

   boolean QueryServiceStatusEx(Winsvc.SC_HANDLE var1, int var2, Winsvc.SERVICE_STATUS_PROCESS var3, int var4, IntByReference var5);

   boolean ControlService(Winsvc.SC_HANDLE var1, int var2, Winsvc.SERVICE_STATUS var3);

   boolean StartService(Winsvc.SC_HANDLE var1, int var2, String[] var3);

   boolean CloseServiceHandle(Winsvc.SC_HANDLE var1);

   Winsvc.SC_HANDLE OpenService(Winsvc.SC_HANDLE var1, String var2, int var3);

   Winsvc.SC_HANDLE OpenSCManager(String var1, String var2, int var3);

   boolean CreateProcessAsUser(WinNT.HANDLE var1, String var2, String var3, WinBase.SECURITY_ATTRIBUTES var4, WinBase.SECURITY_ATTRIBUTES var5, boolean var6, int var7, String var8, String var9, WinBase.STARTUPINFO var10, WinBase.PROCESS_INFORMATION var11);

   boolean AdjustTokenPrivileges(WinNT.HANDLE var1, boolean var2, WinNT.TOKEN_PRIVILEGES var3, int var4, WinNT.TOKEN_PRIVILEGES var5, IntByReference var6);

   boolean LookupPrivilegeName(String var1, WinNT.LUID var2, char[] var3, IntByReference var4);

   boolean LookupPrivilegeValue(String var1, String var2, WinNT.LUID var3);

   boolean GetFileSecurity(WString var1, int var2, Pointer var3, int var4, IntByReference var5);
}
