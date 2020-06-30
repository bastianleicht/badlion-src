package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;
import java.util.Set;

public interface SpdySettingsFrame extends SpdyFrame {
   int SETTINGS_MINOR_VERSION = 0;
   int SETTINGS_UPLOAD_BANDWIDTH = 1;
   int SETTINGS_DOWNLOAD_BANDWIDTH = 2;
   int SETTINGS_ROUND_TRIP_TIME = 3;
   int SETTINGS_MAX_CONCURRENT_STREAMS = 4;
   int SETTINGS_CURRENT_CWND = 5;
   int SETTINGS_DOWNLOAD_RETRANS_RATE = 6;
   int SETTINGS_INITIAL_WINDOW_SIZE = 7;
   int SETTINGS_CLIENT_CERTIFICATE_VECTOR_SIZE = 8;

   Set ids();

   boolean isSet(int var1);

   int getValue(int var1);

   SpdySettingsFrame setValue(int var1, int var2);

   SpdySettingsFrame setValue(int var1, int var2, boolean var3, boolean var4);

   SpdySettingsFrame removeValue(int var1);

   boolean isPersistValue(int var1);

   SpdySettingsFrame setPersistValue(int var1, boolean var2);

   boolean isPersisted(int var1);

   SpdySettingsFrame setPersisted(int var1, boolean var2);

   boolean clearPreviouslyPersistedSettings();

   SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean var1);
}
