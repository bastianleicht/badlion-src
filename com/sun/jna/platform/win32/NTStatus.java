package com.sun.jna.platform.win32;

public interface NTStatus {
   int STATUS_SUCCESS = 0;
   int STATUS_BUFFER_TOO_SMALL = -1073741789;
   int STATUS_WAIT_0 = 0;
   int STATUS_WAIT_1 = 1;
   int STATUS_WAIT_2 = 2;
   int STATUS_WAIT_3 = 3;
   int STATUS_WAIT_63 = 63;
   int STATUS_ABANDONED = 128;
   int STATUS_ABANDONED_WAIT_0 = 128;
   int STATUS_ABANDONED_WAIT_63 = 191;
}
