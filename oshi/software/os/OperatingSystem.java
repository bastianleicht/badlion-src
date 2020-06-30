package oshi.software.os;

import oshi.software.os.OperatingSystemVersion;

public interface OperatingSystem {
   String getFamily();

   String getManufacturer();

   OperatingSystemVersion getVersion();
}
