package com.sun.jna.platform.win32;

import com.sun.jna.win32.StdCallLibrary;

public interface LMCons extends StdCallLibrary {
   int NETBIOS_NAME_LEN = 16;
   int MAX_PREFERRED_LENGTH = -1;
}
