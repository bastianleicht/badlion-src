package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class Advapi32Util {
   public static String getUserName() {
      char[] buffer = new char[128];
      IntByReference len = new IntByReference(buffer.length);
      boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
      if(!result) {
         switch(Kernel32.INSTANCE.GetLastError()) {
         case 122:
            buffer = new char[len.getValue()];
            result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
            break;
         default:
            throw new Win32Exception(Native.getLastError());
         }
      }

      if(!result) {
         throw new Win32Exception(Native.getLastError());
      } else {
         return Native.toString(buffer);
      }
   }

   public static Advapi32Util.Account getAccountByName(String accountName) {
      return getAccountByName((String)null, accountName);
   }

   public static Advapi32Util.Account getAccountByName(String systemName, String accountName) {
      IntByReference pSid = new IntByReference(0);
      IntByReference cchDomainName = new IntByReference(0);
      PointerByReference peUse = new PointerByReference();
      if(Advapi32.INSTANCE.LookupAccountName(systemName, accountName, (WinNT.PSID)null, pSid, (char[])null, cchDomainName, peUse)) {
         throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if(pSid.getValue() != 0 && rc == 122) {
            Memory sidMemory = new Memory((long)pSid.getValue());
            WinNT.PSID result = new WinNT.PSID(sidMemory);
            char[] referencedDomainName = new char[cchDomainName.getValue() + 1];
            if(!Advapi32.INSTANCE.LookupAccountName(systemName, accountName, result, pSid, referencedDomainName, cchDomainName, peUse)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               Advapi32Util.Account account = new Advapi32Util.Account();
               account.accountType = peUse.getPointer().getInt(0L);
               account.name = accountName;
               String[] accountNamePartsBs = accountName.split("\\\\", 2);
               String[] accountNamePartsAt = accountName.split("@", 2);
               if(accountNamePartsBs.length == 2) {
                  account.name = accountNamePartsBs[1];
               } else if(accountNamePartsAt.length == 2) {
                  account.name = accountNamePartsAt[0];
               } else {
                  account.name = accountName;
               }

               if(cchDomainName.getValue() > 0) {
                  account.domain = Native.toString(referencedDomainName);
                  account.fqn = account.domain + "\\" + account.name;
               } else {
                  account.fqn = account.name;
               }

               account.sid = result.getBytes();
               account.sidString = convertSidToStringSid(new WinNT.PSID(account.sid));
               return account;
            }
         } else {
            throw new Win32Exception(rc);
         }
      }
   }

   public static Advapi32Util.Account getAccountBySid(WinNT.PSID sid) {
      return getAccountBySid((String)null, (WinNT.PSID)sid);
   }

   public static Advapi32Util.Account getAccountBySid(String systemName, WinNT.PSID sid) {
      IntByReference cchName = new IntByReference();
      IntByReference cchDomainName = new IntByReference();
      PointerByReference peUse = new PointerByReference();
      if(Advapi32.INSTANCE.LookupAccountSid((String)null, sid, (char[])null, cchName, (char[])null, cchDomainName, peUse)) {
         throw new RuntimeException("LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if(cchName.getValue() != 0 && rc == 122) {
            char[] domainName = new char[cchDomainName.getValue()];
            char[] name = new char[cchName.getValue()];
            if(!Advapi32.INSTANCE.LookupAccountSid((String)null, sid, name, cchName, domainName, cchDomainName, peUse)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               Advapi32Util.Account account = new Advapi32Util.Account();
               account.accountType = peUse.getPointer().getInt(0L);
               account.name = Native.toString(name);
               if(cchDomainName.getValue() > 0) {
                  account.domain = Native.toString(domainName);
                  account.fqn = account.domain + "\\" + account.name;
               } else {
                  account.fqn = account.name;
               }

               account.sid = sid.getBytes();
               account.sidString = convertSidToStringSid(sid);
               return account;
            }
         } else {
            throw new Win32Exception(rc);
         }
      }
   }

   public static String convertSidToStringSid(WinNT.PSID sid) {
      PointerByReference stringSid = new PointerByReference();
      if(!Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         String result = stringSid.getValue().getString(0L, true);
         Kernel32.INSTANCE.LocalFree(stringSid.getValue());
         return result;
      }
   }

   public static byte[] convertStringSidToSid(String sidString) {
      WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
      if(!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return pSID.getValue().getBytes();
      }
   }

   public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
      WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
      if(!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return Advapi32.INSTANCE.IsWellKnownSid(pSID.getValue(), wellKnownSidType);
      }
   }

   public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
      WinNT.PSID pSID = new WinNT.PSID(sidBytes);
      return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);
   }

   public static Advapi32Util.Account getAccountBySid(String sidString) {
      return getAccountBySid((String)null, (String)sidString);
   }

   public static Advapi32Util.Account getAccountBySid(String systemName, String sidString) {
      return getAccountBySid(systemName, new WinNT.PSID(convertStringSidToSid(sidString)));
   }

   public static Advapi32Util.Account[] getTokenGroups(WinNT.HANDLE hToken) {
      IntByReference tokenInformationLength = new IntByReference();
      if(Advapi32.INSTANCE.GetTokenInformation(hToken, 2, (Structure)null, 0, tokenInformationLength)) {
         throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if(rc != 122) {
            throw new Win32Exception(rc);
         } else {
            WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
            if(!Advapi32.INSTANCE.GetTokenInformation(hToken, 2, groups, tokenInformationLength.getValue(), tokenInformationLength)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               ArrayList<Advapi32Util.Account> userGroups = new ArrayList();

               for(WinNT.SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
                  Advapi32Util.Account group = null;

                  try {
                     group = getAccountBySid((WinNT.PSID)sidAndAttribute.Sid);
                  } catch (Exception var11) {
                     group = new Advapi32Util.Account();
                     group.sid = sidAndAttribute.Sid.getBytes();
                     group.sidString = convertSidToStringSid(sidAndAttribute.Sid);
                     group.name = group.sidString;
                     group.fqn = group.sidString;
                     group.accountType = 2;
                  }

                  userGroups.add(group);
               }

               return (Advapi32Util.Account[])userGroups.toArray(new Advapi32Util.Account[0]);
            }
         }
      }
   }

   public static Advapi32Util.Account getTokenAccount(WinNT.HANDLE hToken) {
      IntByReference tokenInformationLength = new IntByReference();
      if(Advapi32.INSTANCE.GetTokenInformation(hToken, 1, (Structure)null, 0, tokenInformationLength)) {
         throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if(rc != 122) {
            throw new Win32Exception(rc);
         } else {
            WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
            if(!Advapi32.INSTANCE.GetTokenInformation(hToken, 1, user, tokenInformationLength.getValue(), tokenInformationLength)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               return getAccountBySid((WinNT.PSID)user.User.Sid);
            }
         }
      }
   }

   public static Advapi32Util.Account[] getCurrentUserGroups() {
      WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();

      Advapi32Util.Account[] var6;
      try {
         WinNT.HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
         if(!Advapi32.INSTANCE.OpenThreadToken(threadHandle, 10, true, phToken)) {
            if(1008 != Kernel32.INSTANCE.GetLastError()) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            WinNT.HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
            if(!Advapi32.INSTANCE.OpenProcessToken(processHandle, 10, phToken)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
         }

         var6 = getTokenGroups(phToken.getValue());
      } finally {
         if(phToken.getValue() != WinBase.INVALID_HANDLE_VALUE && !Kernel32.INSTANCE.CloseHandle(phToken.getValue())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

      }

      return var6;
   }

   public static boolean registryKeyExists(WinReg.HKEY root, String key) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      switch(rc) {
      case 0:
         Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
         return true;
      case 2:
         return false;
      default:
         throw new Win32Exception(rc);
      }
   }

   public static boolean registryValueExists(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);

      try {
         switch(rc) {
         case 0:
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            switch(rc) {
            case 0:
            case 122:
               boolean var14 = true;
               return var14;
            case 2:
               boolean var7 = false;
               return var7;
            default:
               throw new Win32Exception(rc);
            }
         case 2:
            boolean lpcbData = false;
            return lpcbData;
         default:
            throw new Win32Exception(rc);
         }
      } finally {
         if(phkKey.getValue() != WinBase.INVALID_HANDLE_VALUE) {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }
         }

      }
   }

   public static String registryGetStringValue(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String var8;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 1 && lpType.getValue() != 2) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ or REG_EXPAND_SZ");
            }

            char[] data = new char[lpcbData.getValue()];
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            var8 = Native.toString(data);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var8;
      }
   }

   public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String var8;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 2) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
            }

            char[] data = new char[lpcbData.getValue()];
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            var8 = Native.toString(data);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var8;
      }
   }

   public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String[] var18;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 7) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
            }

            Memory data = new Memory((long)lpcbData.getValue());
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (Pointer)data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            ArrayList<String> result = new ArrayList();
            int offset = 0;

            while((long)offset < data.size()) {
               String s = data.getString((long)offset, true);
               offset = offset + s.length() * Native.WCHAR_SIZE;
               offset = offset + Native.WCHAR_SIZE;
               result.add(s);
            }

            var18 = (String[])result.toArray(new String[0]);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var18;
      }
   }

   public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         byte[] var8;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 3) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_BINARY");
            }

            byte[] data = new byte[lpcbData.getValue()];
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (byte[])data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            var8 = data;
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var8;
      }
   }

   public static int registryGetIntValue(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         int var8;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 4) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_DWORD");
            }

            IntByReference data = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (IntByReference)data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            var8 = data.getValue();
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var8;
      }
   }

   public static long registryGetLongValue(WinReg.HKEY root, String key, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         long var8;
         try {
            IntByReference lpcbData = new IntByReference();
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (char[])((char[])null), lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            if(lpType.getValue() != 11) {
               throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_QWORD");
            }

            LongByReference data = new LongByReference();
            rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (LongByReference)data, lpcbData);
            if(rc != 0 && rc != 122) {
               throw new Win32Exception(rc);
            }

            var8 = data.getValue();
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var8;
      }
   }

   public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName) {
      WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
      IntByReference lpdwDisposition = new IntByReference();
      int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, (String)null, 0, 131097, (WinBase.SECURITY_ATTRIBUTES)null, phkResult, lpdwDisposition);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         rc = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
         if(rc != 0) {
            throw new Win32Exception(rc);
         } else {
            return 1 == lpdwDisposition.getValue();
         }
      }
   }

   public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0, 4, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         boolean var5;
         try {
            var5 = registryCreateKey(phkKey.getValue(), keyName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var5;
      }
   }

   public static void registrySetIntValue(WinReg.HKEY hKey, String name, int value) {
      byte[] data = new byte[]{(byte)(value & 255), (byte)(value >> 8 & 255), (byte)(value >> 16 & 255), (byte)(value >> 24 & 255)};
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 4, (byte[])data, 4);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetIntValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetLongValue(WinReg.HKEY hKey, String name, long value) {
      byte[] data = new byte[]{(byte)((int)(value & 255L)), (byte)((int)(value >> 8 & 255L)), (byte)((int)(value >> 16 & 255L)), (byte)((int)(value >> 24 & 255L)), (byte)((int)(value >> 32 & 255L)), (byte)((int)(value >> 40 & 255L)), (byte)((int)(value >> 48 & 255L)), (byte)((int)(value >> 56 & 255L))};
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 11, (byte[])data, 8);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetLongValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetStringValue(WinReg.HKEY hKey, String name, String value) {
      char[] data = Native.toCharArray(value);
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 1, (char[])data, data.length * Native.WCHAR_SIZE);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetStringValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetExpandableStringValue(WinReg.HKEY hKey, String name, String value) {
      char[] data = Native.toCharArray(value);
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 2, (char[])data, data.length * Native.WCHAR_SIZE);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetExpandableStringValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetStringArray(WinReg.HKEY hKey, String name, String[] arr) {
      int size = 0;

      for(String s : arr) {
         size = size + s.length() * Native.WCHAR_SIZE;
         size = size + Native.WCHAR_SIZE;
      }

      int offset = 0;
      Memory data = new Memory((long)size);

      for(String s : arr) {
         data.setString((long)offset, s, true);
         offset = offset + s.length() * Native.WCHAR_SIZE;
         offset = offset + Native.WCHAR_SIZE;
      }

      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 7, (byte[])data.getByteArray(0L, size), size);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetStringArray(phkKey.getValue(), name, arr);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetBinaryValue(WinReg.HKEY hKey, String name, byte[] data) {
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 3, (byte[])data, data.length);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetBinaryValue(phkKey.getValue(), name, data);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registryDeleteKey(WinReg.HKEY hKey, String keyName) {
      int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registryDeleteKey(phkKey.getValue(), keyName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registryDeleteValue(WinReg.HKEY hKey, String valueName) {
      int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
      if(rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registryDeleteValue(phkKey.getValue(), valueName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static String[] registryGetKeys(WinReg.HKEY hKey) {
      IntByReference lpcSubKeys = new IntByReference();
      IntByReference lpcMaxSubKeyLen = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, (char[])null, (IntByReference)null, (IntByReference)null, lpcSubKeys, lpcMaxSubKeyLen, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (WinBase.FILETIME)null);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         ArrayList<String> keys = new ArrayList(lpcSubKeys.getValue());
         char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];

         for(int i = 0; i < lpcSubKeys.getValue(); ++i) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
            rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, i, name, lpcchValueName, (IntByReference)null, (char[])null, (IntByReference)null, (WinBase.FILETIME)null);
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

            keys.add(Native.toString(name));
         }

         return (String[])keys.toArray(new String[0]);
      }
   }

   public static String[] registryGetKeys(WinReg.HKEY root, String keyPath) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String[] var4;
         try {
            var4 = registryGetKeys(phkKey.getValue());
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var4;
      }
   }

   public static TreeMap registryGetValues(WinReg.HKEY hKey) {
      IntByReference lpcValues = new IntByReference();
      IntByReference lpcMaxValueNameLen = new IntByReference();
      IntByReference lpcMaxValueLen = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, (char[])null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, lpcValues, lpcMaxValueNameLen, lpcMaxValueLen, (IntByReference)null, (WinBase.FILETIME)null);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         TreeMap<String, Object> keyValues = new TreeMap();
         char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
         byte[] data = new byte[lpcMaxValueLen.getValue()];

         for(int i = 0; i < lpcValues.getValue(); ++i) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
            IntByReference lpcbData = new IntByReference(lpcMaxValueLen.getValue());
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName, (IntByReference)null, lpType, data, lpcbData);
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

            String nameString = Native.toString(name);
            Memory byteData = new Memory((long)lpcbData.getValue());
            byteData.write(0L, (byte[])data, 0, lpcbData.getValue());
            switch(lpType.getValue()) {
            case 1:
            case 2:
               keyValues.put(nameString, byteData.getString(0L, true));
               break;
            case 3:
               keyValues.put(nameString, byteData.getByteArray(0L, lpcbData.getValue()));
               break;
            case 4:
               keyValues.put(nameString, Integer.valueOf(byteData.getInt(0L)));
               break;
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            default:
               throw new RuntimeException("Unsupported type: " + lpType.getValue());
            case 7:
               Memory stringData = new Memory((long)lpcbData.getValue());
               stringData.write(0L, (byte[])data, 0, lpcbData.getValue());
               ArrayList<String> result = new ArrayList();
               int offset = 0;

               while((long)offset < stringData.size()) {
                  String s = stringData.getString((long)offset, true);
                  offset = offset + s.length() * Native.WCHAR_SIZE;
                  offset = offset + Native.WCHAR_SIZE;
                  result.add(s);
               }

               keyValues.put(nameString, result.toArray(new String[0]));
               break;
            case 11:
               keyValues.put(nameString, Long.valueOf(byteData.getLong(0L)));
            }
         }

         return keyValues;
      }
   }

   public static TreeMap registryGetValues(WinReg.HKEY root, String keyPath) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097, phkKey);
      if(rc != 0) {
         throw new Win32Exception(rc);
      } else {
         TreeMap var4;
         try {
            var4 = registryGetValues(phkKey.getValue());
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if(rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var4;
      }
   }

   public static String getEnvironmentBlock(Map environment) {
      StringBuffer out = new StringBuffer();

      for(Entry<String, String> entry : environment.entrySet()) {
         if(entry.getValue() != null) {
            out.append((String)entry.getKey() + "=" + (String)entry.getValue() + "\u0000");
         }
      }

      return out.toString() + "\u0000";
   }

   public static WinNT.ACCESS_ACEStructure[] getFileSecurity(String fileName, boolean compact) {
      int infoType = 4;
      int nLength = 1024;
      boolean repeat = false;
      Memory memory = null;

      while(true) {
         repeat = false;
         memory = new Memory((long)nLength);
         IntByReference lpnSize = new IntByReference();
         boolean succeded = Advapi32.INSTANCE.GetFileSecurity(new WString(fileName), infoType, memory, nLength, lpnSize);
         if(!succeded) {
            int lastError = Kernel32.INSTANCE.GetLastError();
            memory.clear();
            if(122 != lastError) {
               throw new Win32Exception(lastError);
            }
         }

         int lengthNeeded = lpnSize.getValue();
         if(nLength < lengthNeeded) {
            repeat = true;
            nLength = lengthNeeded;
            memory.clear();
         }

         if(!repeat) {
            break;
         }
      }

      WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(memory);
      memory.clear();
      WinNT.ACL dacl = sdr.getDiscretionaryACL();
      WinNT.ACCESS_ACEStructure[] aceStructures = dacl.getACEStructures();
      if(compact) {
         Map<String, WinNT.ACCESS_ACEStructure> aceMap = new HashMap();

         for(WinNT.ACCESS_ACEStructure aceStructure : aceStructures) {
            boolean inherted = (aceStructure.AceFlags & 31) != 0;
            String key = aceStructure.getSidString() + "/" + inherted + "/" + aceStructure.getClass().getName();
            WinNT.ACCESS_ACEStructure aceStructure2 = (WinNT.ACCESS_ACEStructure)aceMap.get(key);
            if(aceStructure2 != null) {
               int accessMask = aceStructure2.Mask;
               accessMask = accessMask | aceStructure.Mask;
               aceStructure2.Mask = accessMask;
            } else {
               aceMap.put(key, aceStructure);
            }
         }

         return (WinNT.ACCESS_ACEStructure[])aceMap.values().toArray(new WinNT.ACCESS_ACEStructure[aceMap.size()]);
      } else {
         return aceStructures;
      }
   }

   public static class Account {
      public String name;
      public String domain;
      public byte[] sid;
      public String sidString;
      public int accountType;
      public String fqn;
   }

   public static class EventLogIterator implements Iterable, Iterator {
      private WinNT.HANDLE _h;
      private Memory _buffer;
      private boolean _done;
      private int _dwRead;
      private Pointer _pevlr;
      private int _flags;

      public EventLogIterator(String sourceName) {
         this((String)null, sourceName, 4);
      }

      public EventLogIterator(String serverName, String sourceName, int flags) {
         this._h = null;
         this._buffer = new Memory(65536L);
         this._done = false;
         this._dwRead = 0;
         this._pevlr = null;
         this._flags = 4;
         this._flags = flags;
         this._h = Advapi32.INSTANCE.OpenEventLog(serverName, sourceName);
         if(this._h == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }
      }

      private boolean read() {
         if(!this._done && this._dwRead <= 0) {
            IntByReference pnBytesRead = new IntByReference();
            IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
            if(!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
               int rc = Kernel32.INSTANCE.GetLastError();
               if(rc != 122) {
                  this.close();
                  if(rc != 38) {
                     throw new Win32Exception(rc);
                  }

                  return false;
               }

               this._buffer = new Memory((long)pnMinNumberOfBytesNeeded.getValue());
               if(!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }
            }

            this._dwRead = pnBytesRead.getValue();
            this._pevlr = this._buffer;
            return true;
         } else {
            return false;
         }
      }

      public void close() {
         this._done = true;
         if(this._h != null) {
            if(!Advapi32.INSTANCE.CloseEventLog(this._h)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            this._h = null;
         }

      }

      public Iterator iterator() {
         return this;
      }

      public boolean hasNext() {
         this.read();
         return !this._done;
      }

      public Advapi32Util.EventLogRecord next() {
         this.read();
         Advapi32Util.EventLogRecord record = new Advapi32Util.EventLogRecord(this._pevlr);
         this._dwRead -= record.getLength();
         this._pevlr = this._pevlr.share((long)record.getLength());
         return record;
      }

      public void remove() {
      }
   }

   public static class EventLogRecord {
      private WinNT.EVENTLOGRECORD _record = null;
      private String _source;
      private byte[] _data;
      private String[] _strings;

      public WinNT.EVENTLOGRECORD getRecord() {
         return this._record;
      }

      public int getEventId() {
         return this._record.EventID.intValue();
      }

      public String getSource() {
         return this._source;
      }

      public int getStatusCode() {
         return this._record.EventID.intValue() & '\uffff';
      }

      public int getRecordNumber() {
         return this._record.RecordNumber.intValue();
      }

      public int getLength() {
         return this._record.Length.intValue();
      }

      public String[] getStrings() {
         return this._strings;
      }

      public Advapi32Util.EventLogType getType() {
         switch(this._record.EventType.intValue()) {
         case 0:
         case 4:
            return Advapi32Util.EventLogType.Informational;
         case 1:
            return Advapi32Util.EventLogType.Error;
         case 2:
            return Advapi32Util.EventLogType.Warning;
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            throw new RuntimeException("Invalid type: " + this._record.EventType.intValue());
         case 8:
            return Advapi32Util.EventLogType.AuditSuccess;
         case 16:
            return Advapi32Util.EventLogType.AuditFailure;
         }
      }

      public byte[] getData() {
         return this._data;
      }

      public EventLogRecord(Pointer pevlr) {
         this._record = new WinNT.EVENTLOGRECORD(pevlr);
         this._source = pevlr.getString((long)this._record.size(), true);
         if(this._record.DataLength.intValue() > 0) {
            this._data = pevlr.getByteArray((long)this._record.DataOffset.intValue(), this._record.DataLength.intValue());
         }

         if(this._record.NumStrings.intValue() > 0) {
            ArrayList<String> strings = new ArrayList();
            int count = this._record.NumStrings.intValue();

            for(long offset = (long)this._record.StringOffset.intValue(); count > 0; --count) {
               String s = pevlr.getString(offset, true);
               strings.add(s);
               offset = offset + (long)(s.length() * Native.WCHAR_SIZE);
               offset = offset + (long)Native.WCHAR_SIZE;
            }

            this._strings = (String[])strings.toArray(new String[0]);
         }

      }
   }

   public static enum EventLogType {
      Error,
      Warning,
      Informational,
      AuditSuccess,
      AuditFailure;
   }
}
