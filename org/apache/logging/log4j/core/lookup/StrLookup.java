package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;

public interface StrLookup {
   String lookup(String var1);

   String lookup(LogEvent var1, String var2);
}
