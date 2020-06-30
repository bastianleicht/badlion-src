package com.sun.jna.platform.win32;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.DsGetDC;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Ole32Util;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;

public abstract class Netapi32Util {
   public static String getDCName() {
      return getDCName((String)null, (String)null);
   }

   public static String getDCName(String serverName, String domainName) {
      PointerByReference bufptr = new PointerByReference();

      String var4;
      try {
         int rc = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
         if(0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = bufptr.getValue().getString(0L, true);
      } finally {
         if(0 != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

      }

      return var4;
   }

   public static int getJoinStatus() {
      return getJoinStatus((String)null);
   }

   public static int getJoinStatus(String computerName) {
      PointerByReference lpNameBuffer = new PointerByReference();
      IntByReference bufferType = new IntByReference();
      boolean var9 = false;

      int var4;
      try {
         var9 = true;
         int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
         if(0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = bufferType.getValue();
         var9 = false;
      } finally {
         if(var9) {
            if(lpNameBuffer.getPointer() != null) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(lpNameBuffer.getPointer() != null) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var4;
   }

   public static String getDomainName(String computerName) {
      PointerByReference lpNameBuffer = new PointerByReference();
      IntByReference bufferType = new IntByReference();
      boolean var9 = false;

      String var4;
      try {
         var9 = true;
         int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
         if(0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = lpNameBuffer.getValue().getString(0L, true);
         var9 = false;
      } finally {
         if(var9) {
            if(lpNameBuffer.getPointer() != null) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(lpNameBuffer.getPointer() != null) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var4;
   }

   public static Netapi32Util.LocalGroup[] getLocalGroups() {
      return getLocalGroups((String)null);
   }

   public static Netapi32Util.LocalGroup[] getLocalGroups(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      Netapi32Util.LocalGroup[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if(0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
         LMAccess.LOCALGROUP_INFO_1[] groups = (LMAccess.LOCALGROUP_INFO_1[])((LMAccess.LOCALGROUP_INFO_1[])group.toArray(entriesRead.getValue()));
         ArrayList<Netapi32Util.LocalGroup> result = new ArrayList();

         for(LMAccess.LOCALGROUP_INFO_1 lgpi : groups) {
            Netapi32Util.LocalGroup lgp = new Netapi32Util.LocalGroup();
            lgp.name = lgpi.lgrui1_name.toString();
            lgp.comment = lgpi.lgrui1_comment.toString();
            result.add(lgp);
         }

         var18 = (Netapi32Util.LocalGroup[])result.toArray(new Netapi32Util.LocalGroup[0]);
         var16 = false;
      } finally {
         if(var16) {
            if(bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static Netapi32Util.Group[] getGlobalGroups() {
      return getGlobalGroups((String)null);
   }

   public static Netapi32Util.Group[] getGlobalGroups(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      Netapi32Util.Group[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if(0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
         LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[])((LMAccess.GROUP_INFO_1[])group.toArray(entriesRead.getValue()));
         ArrayList<Netapi32Util.LocalGroup> result = new ArrayList();

         for(LMAccess.GROUP_INFO_1 lgpi : groups) {
            Netapi32Util.LocalGroup lgp = new Netapi32Util.LocalGroup();
            lgp.name = lgpi.grpi1_name.toString();
            lgp.comment = lgpi.grpi1_comment.toString();
            result.add(lgp);
         }

         var18 = (Netapi32Util.Group[])result.toArray(new Netapi32Util.LocalGroup[0]);
         var16 = false;
      } finally {
         if(var16) {
            if(bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static Netapi32Util.User[] getUsers() {
      return getUsers((String)null);
   }

   public static Netapi32Util.User[] getUsers(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      Netapi32Util.User[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if(0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
         LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[])((LMAccess.USER_INFO_1[])user.toArray(entriesRead.getValue()));
         ArrayList<Netapi32Util.User> result = new ArrayList();

         for(LMAccess.USER_INFO_1 lu : users) {
            Netapi32Util.User auser = new Netapi32Util.User();
            auser.name = lu.usri1_name.toString();
            result.add(auser);
         }

         var18 = (Netapi32Util.User[])result.toArray(new Netapi32Util.User[0]);
         var16 = false;
      } finally {
         if(var16) {
            if(bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static Netapi32Util.Group[] getCurrentUserLocalGroups() {
      return getUserLocalGroups(Secur32Util.getUserNameEx(2));
   }

   public static Netapi32Util.Group[] getUserLocalGroups(String userName) {
      return getUserLocalGroups(userName, (String)null);
   }

   public static Netapi32Util.Group[] getUserLocalGroups(String userName, String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesread = new IntByReference();
      IntByReference totalentries = new IntByReference();
      boolean var17 = false;

      Netapi32Util.Group[] var19;
      try {
         var17 = true;
         int rc = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 0, 0, bufptr, -1, entriesread, totalentries);
         if(rc != 0) {
            throw new Win32Exception(rc);
         }

         LMAccess.LOCALGROUP_USERS_INFO_0 lgroup = new LMAccess.LOCALGROUP_USERS_INFO_0(bufptr.getValue());
         LMAccess.LOCALGROUP_USERS_INFO_0[] lgroups = (LMAccess.LOCALGROUP_USERS_INFO_0[])((LMAccess.LOCALGROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue()));
         ArrayList<Netapi32Util.Group> result = new ArrayList();

         for(LMAccess.LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
            Netapi32Util.LocalGroup lgp = new Netapi32Util.LocalGroup();
            lgp.name = lgpi.lgrui0_name.toString();
            result.add(lgp);
         }

         var19 = (Netapi32Util.Group[])result.toArray(new Netapi32Util.Group[0]);
         var17 = false;
      } finally {
         if(var17) {
            if(bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var19;
   }

   public static Netapi32Util.Group[] getUserGroups(String userName) {
      return getUserGroups(userName, (String)null);
   }

   public static Netapi32Util.Group[] getUserGroups(String userName, String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesread = new IntByReference();
      IntByReference totalentries = new IntByReference();
      boolean var17 = false;

      Netapi32Util.Group[] var19;
      try {
         var17 = true;
         int rc = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 0, bufptr, -1, entriesread, totalentries);
         if(rc != 0) {
            throw new Win32Exception(rc);
         }

         LMAccess.GROUP_USERS_INFO_0 lgroup = new LMAccess.GROUP_USERS_INFO_0(bufptr.getValue());
         LMAccess.GROUP_USERS_INFO_0[] lgroups = (LMAccess.GROUP_USERS_INFO_0[])((LMAccess.GROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue()));
         ArrayList<Netapi32Util.Group> result = new ArrayList();

         for(LMAccess.GROUP_USERS_INFO_0 lgpi : lgroups) {
            Netapi32Util.Group lgp = new Netapi32Util.Group();
            lgp.name = lgpi.grui0_name.toString();
            result.add(lgp);
         }

         var19 = (Netapi32Util.Group[])result.toArray(new Netapi32Util.Group[0]);
         var17 = false;
      } finally {
         if(var17) {
            if(bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if(0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if(bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if(0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var19;
   }

   public static Netapi32Util.DomainController getDC() {
      DsGetDC.PDOMAIN_CONTROLLER_INFO.ByReference pdci = new DsGetDC.PDOMAIN_CONTROLLER_INFO.ByReference();
      int rc = Netapi32.INSTANCE.DsGetDcName((String)null, (String)null, (Guid.GUID)null, (String)null, 0, pdci);
      if(0 != rc) {
         throw new Win32Exception(rc);
      } else {
         Netapi32Util.DomainController dc = new Netapi32Util.DomainController();
         dc.address = pdci.dci.DomainControllerAddress.toString();
         dc.addressType = pdci.dci.DomainControllerAddressType;
         dc.clientSiteName = pdci.dci.ClientSiteName.toString();
         dc.dnsForestName = pdci.dci.DnsForestName.toString();
         dc.domainGuid = pdci.dci.DomainGuid;
         dc.domainName = pdci.dci.DomainName.toString();
         dc.flags = pdci.dci.Flags;
         dc.name = pdci.dci.DomainControllerName.toString();
         rc = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
         if(0 != rc) {
            throw new Win32Exception(rc);
         } else {
            return dc;
         }
      }
   }

   public static Netapi32Util.DomainTrust[] getDomainTrusts() {
      return getDomainTrusts((String)null);
   }

   public static Netapi32Util.DomainTrust[] getDomainTrusts(String serverName) {
      NativeLongByReference domainCount = new NativeLongByReference();
      DsGetDC.PDS_DOMAIN_TRUSTS.ByReference domains = new DsGetDC.PDS_DOMAIN_TRUSTS.ByReference();
      int rc = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, new NativeLong(63L), domains, domainCount);
      if(0 != rc) {
         throw new Win32Exception(rc);
      } else {
         Netapi32Util.DomainTrust[] var15;
         try {
            int domainCountValue = domainCount.getValue().intValue();
            ArrayList<Netapi32Util.DomainTrust> trusts = new ArrayList(domainCountValue);

            for(DsGetDC.DS_DOMAIN_TRUSTS trust : domains.getTrusts(domainCountValue)) {
               Netapi32Util.DomainTrust t = new Netapi32Util.DomainTrust();
               t.DnsDomainName = trust.DnsDomainName.toString();
               t.NetbiosDomainName = trust.NetbiosDomainName.toString();
               t.DomainSid = trust.DomainSid;
               t.DomainSidString = Advapi32Util.convertSidToStringSid(trust.DomainSid);
               t.DomainGuid = trust.DomainGuid;
               t.DomainGuidString = Ole32Util.getStringFromGUID(trust.DomainGuid);
               t.flags = trust.Flags.intValue();
               trusts.add(t);
            }

            var15 = (Netapi32Util.DomainTrust[])trusts.toArray(new Netapi32Util.DomainTrust[0]);
         } finally {
            rc = Netapi32.INSTANCE.NetApiBufferFree(domains.getPointer());
            if(0 != rc) {
               throw new Win32Exception(rc);
            }

         }

         return var15;
      }
   }

   public static Netapi32Util.UserInfo getUserInfo(String accountName) {
      return getUserInfo(accountName, getDCName());
   }

   public static Netapi32Util.UserInfo getUserInfo(String accountName, String domainName) {
      PointerByReference bufptr = new PointerByReference();
      int rc = -1;

      Netapi32Util.UserInfo var6;
      try {
         rc = Netapi32.INSTANCE.NetUserGetInfo(getDCName(), accountName, 23, bufptr);
         if(rc != 0) {
            throw new Win32Exception(rc);
         }

         LMAccess.USER_INFO_23 info_23 = new LMAccess.USER_INFO_23(bufptr.getValue());
         Netapi32Util.UserInfo userInfo = new Netapi32Util.UserInfo();
         userInfo.comment = info_23.usri23_comment.toString();
         userInfo.flags = info_23.usri23_flags;
         userInfo.fullName = info_23.usri23_full_name.toString();
         userInfo.name = info_23.usri23_name.toString();
         userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
         userInfo.sid = info_23.usri23_user_sid;
         var6 = userInfo;
      } finally {
         if(bufptr.getValue() != Pointer.NULL) {
            Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         }

      }

      return var6;
   }

   public static class DomainController {
      public String name;
      public String address;
      public int addressType;
      public Guid.GUID domainGuid;
      public String domainName;
      public String dnsForestName;
      public int flags;
      public String clientSiteName;
   }

   public static class DomainTrust {
      public String NetbiosDomainName;
      public String DnsDomainName;
      public WinNT.PSID DomainSid;
      public String DomainSidString;
      public Guid.GUID DomainGuid;
      public String DomainGuidString;
      private int flags;

      public boolean isInForest() {
         return (this.flags & 1) != 0;
      }

      public boolean isOutbound() {
         return (this.flags & 2) != 0;
      }

      public boolean isRoot() {
         return (this.flags & 4) != 0;
      }

      public boolean isPrimary() {
         return (this.flags & 8) != 0;
      }

      public boolean isNativeMode() {
         return (this.flags & 16) != 0;
      }

      public boolean isInbound() {
         return (this.flags & 32) != 0;
      }
   }

   public static class Group {
      public String name;
   }

   public static class LocalGroup extends Netapi32Util.Group {
      public String comment;
   }

   public static class User {
      public String name;
      public String comment;
   }

   public static class UserInfo extends Netapi32Util.User {
      public String fullName;
      public String sidString;
      public WinNT.PSID sid;
      public int flags;
   }
}
