package org.apache.logging.log4j.core.appender.rewrite;

import org.apache.logging.log4j.core.LogEvent;

public interface RewritePolicy {
   LogEvent rewrite(LogEvent var1);
}
