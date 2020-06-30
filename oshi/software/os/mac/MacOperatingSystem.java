package oshi.software.os.mac;

import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.software.os.mac.local.OSVersionInfoEx;
import oshi.util.ExecutingCommand;

public class MacOperatingSystem implements OperatingSystem {
   private String _family;
   private OperatingSystemVersion _version = null;

   public OperatingSystemVersion getVersion() {
      if(this._version == null) {
         this._version = new OSVersionInfoEx();
      }

      return this._version;
   }

   public String getFamily() {
      if(this._family == null) {
         this._family = ExecutingCommand.getFirstAnswer("sw_vers -productName");
      }

      return this._family;
   }

   public String getManufacturer() {
      return "Apple";
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getManufacturer());
      sb.append(" ");
      sb.append(this.getFamily());
      sb.append(" ");
      sb.append(this.getVersion().toString());
      return sb.toString();
   }
}
