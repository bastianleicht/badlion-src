package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Secur32 extends StdCallLibrary {
   Secur32 INSTANCE = (Secur32)Native.loadLibrary("Secur32", Secur32.class, W32APIOptions.UNICODE_OPTIONS);

   boolean GetUserNameEx(int var1, char[] var2, IntByReference var3);

   int AcquireCredentialsHandle(String var1, String var2, NativeLong var3, WinNT.LUID var4, Pointer var5, Pointer var6, Pointer var7, Sspi.CredHandle var8, Sspi.TimeStamp var9);

   int InitializeSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, String var3, NativeLong var4, NativeLong var5, NativeLong var6, Sspi.SecBufferDesc var7, NativeLong var8, Sspi.CtxtHandle var9, Sspi.SecBufferDesc var10, NativeLongByReference var11, Sspi.TimeStamp var12);

   int DeleteSecurityContext(Sspi.CtxtHandle var1);

   int FreeCredentialsHandle(Sspi.CredHandle var1);

   int AcceptSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, Sspi.SecBufferDesc var3, NativeLong var4, NativeLong var5, Sspi.CtxtHandle var6, Sspi.SecBufferDesc var7, NativeLongByReference var8, Sspi.TimeStamp var9);

   int EnumerateSecurityPackages(IntByReference var1, Sspi.PSecPkgInfo.ByReference var2);

   int FreeContextBuffer(Pointer var1);

   int QuerySecurityContextToken(Sspi.CtxtHandle var1, WinNT.HANDLEByReference var2);

   int ImpersonateSecurityContext(Sspi.CtxtHandle var1);

   int RevertSecurityContext(Sspi.CtxtHandle var1);

   public abstract static class EXTENDED_NAME_FORMAT {
      public static final int NameUnknown = 0;
      public static final int NameFullyQualifiedDN = 1;
      public static final int NameSamCompatible = 2;
      public static final int NameDisplay = 3;
      public static final int NameUniqueId = 6;
      public static final int NameCanonical = 7;
      public static final int NameUserPrincipal = 8;
      public static final int NameCanonicalEx = 9;
      public static final int NameServicePrincipal = 10;
      public static final int NameDnsDomain = 12;
   }
}
