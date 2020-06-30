package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractRolloverStrategy implements RolloverStrategy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
}
