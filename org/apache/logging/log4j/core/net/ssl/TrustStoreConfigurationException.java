package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;

public class TrustStoreConfigurationException extends StoreConfigurationException {
   public TrustStoreConfigurationException(Exception e) {
      super(e);
   }
}
