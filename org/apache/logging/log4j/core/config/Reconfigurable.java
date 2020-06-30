package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.config.Configuration;

public interface Reconfigurable {
   Configuration reconfigure();
}
