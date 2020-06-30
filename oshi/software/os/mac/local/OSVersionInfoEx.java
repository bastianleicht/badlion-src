package oshi.software.os.mac.local;

import oshi.software.os.OperatingSystemVersion;
import oshi.util.ExecutingCommand;

public class OSVersionInfoEx implements OperatingSystemVersion {
   private String _version = null;
   private String _codeName = null;
   private String version = null;
   private String _buildNumber = null;

   public String getVersion() {
      if(this._version == null) {
         this._version = ExecutingCommand.getFirstAnswer("sw_vers -productVersion");
      }

      return this._version;
   }

   public void setVersion(String _version) {
      this._version = _version;
   }

   public String getCodeName() {
      if(this._codeName == null && this.getVersion() != null) {
         if(!"10.0".equals(this.getVersion()) && !this.getVersion().startsWith("10.0.")) {
            if(!"10.1".equals(this.getVersion()) && !this.getVersion().startsWith("10.1.")) {
               if(!"10.2".equals(this.getVersion()) && !this.getVersion().startsWith("10.2.")) {
                  if(!"10.3".equals(this.getVersion()) && !this.getVersion().startsWith("10.3.")) {
                     if(!"10.4".equals(this.getVersion()) && !this.getVersion().startsWith("10.4.")) {
                        if(!"10.5".equals(this.getVersion()) && !this.getVersion().startsWith("10.5.")) {
                           if(!"10.6".equals(this.getVersion()) && !this.getVersion().startsWith("10.6.")) {
                              if(!"10.7".equals(this.getVersion()) && !this.getVersion().startsWith("10.7.")) {
                                 if(!"10.8".equals(this.getVersion()) && !this.getVersion().startsWith("10.8.")) {
                                    if(!"10.9".equals(this.getVersion()) && !this.getVersion().startsWith("10.9.")) {
                                       if("10.10".equals(this.getVersion()) || this.getVersion().startsWith("10.10.")) {
                                          this._codeName = "Yosemite";
                                       }
                                    } else {
                                       this._codeName = "Mavericks";
                                    }
                                 } else {
                                    this._codeName = "Mountain Lion";
                                 }
                              } else {
                                 this._codeName = "Lion";
                              }
                           } else {
                              this._codeName = "Snow Leopard";
                           }
                        } else {
                           this._codeName = "Leopard";
                        }
                     } else {
                        this._codeName = "Tiger";
                     }
                  } else {
                     this._codeName = "Panther";
                  }
               } else {
                  this._codeName = "Jaguar";
               }
            } else {
               this._codeName = "Puma";
            }
         } else {
            this._codeName = "Cheetah";
         }
      }

      return this._codeName;
   }

   public void setCodeName(String _codeName) {
      this._codeName = _codeName;
   }

   public String getBuildNumber() {
      if(this._buildNumber == null) {
         this._buildNumber = ExecutingCommand.getFirstAnswer("sw_vers -buildVersion");
      }

      return this._buildNumber;
   }

   public void setBuildNumber(String buildNumber) {
      this._buildNumber = buildNumber;
   }

   public String toString() {
      if(this.version == null) {
         this.version = this.getVersion() + " (" + this.getCodeName() + ") build " + this.getBuildNumber();
      }

      return this.version;
   }
}
