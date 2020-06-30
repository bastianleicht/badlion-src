package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;

public class KeyStoreConfigurationException extends StoreConfigurationException {
   public KeyStoreConfigurationException(Exception e) {
      super(e);
   }
}
