package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.config.Reconfigurable;

public interface ConfigurationListener {
   void onChange(Reconfigurable var1);
}
