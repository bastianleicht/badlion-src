package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

public interface LogEventFactory {
   LogEvent createEvent(String var1, Marker var2, String var3, Level var4, Message var5, List var6, Throwable var7);
}
