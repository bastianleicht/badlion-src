package org.apache.logging.log4j.core.net.ssl;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.ssl.KeyStoreConfiguration;
import org.apache.logging.log4j.core.net.ssl.KeyStoreConfigurationException;
import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;
import org.apache.logging.log4j.core.net.ssl.TrustStoreConfiguration;
import org.apache.logging.log4j.core.net.ssl.TrustStoreConfigurationException;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ssl",
   category = "Core",
   printObject = true
)
public class SSLConfiguration {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private KeyStoreConfiguration keyStoreConfig;
   private TrustStoreConfiguration trustStoreConfig;
   private SSLContext sslContext;

   private SSLConfiguration(KeyStoreConfiguration keyStoreConfig, TrustStoreConfiguration trustStoreConfig) {
      this.keyStoreConfig = keyStoreConfig;
      this.trustStoreConfig = trustStoreConfig;
      this.sslContext = null;
   }

   public SSLSocketFactory getSSLSocketFactory() {
      if(this.sslContext == null) {
         this.sslContext = this.createSSLContext();
      }

      return this.sslContext.getSocketFactory();
   }

   public SSLServerSocketFactory getSSLServerSocketFactory() {
      if(this.sslContext == null) {
         this.sslContext = this.createSSLContext();
      }

      return this.sslContext.getServerSocketFactory();
   }

   private SSLContext createSSLContext() {
      SSLContext context = null;

      try {
         context = this.createSSLContextBasedOnConfiguration();
         LOGGER.debug("Creating SSLContext with the given parameters");
      } catch (TrustStoreConfigurationException var3) {
         context = this.createSSLContextWithTrustStoreFailure();
      } catch (KeyStoreConfigurationException var4) {
         context = this.createSSLContextWithKeyStoreFailure();
      }

      return context;
   }

   private SSLContext createSSLContextWithTrustStoreFailure() {
      SSLContext context;
      try {
         context = this.createSSLContextWithDefaultTrustManagerFactory();
         LOGGER.debug("Creating SSLContext with default truststore");
      } catch (KeyStoreConfigurationException var3) {
         context = this.createDefaultSSLContext();
         LOGGER.debug("Creating SSLContext with default configuration");
      }

      return context;
   }

   private SSLContext createSSLContextWithKeyStoreFailure() {
      SSLContext context;
      try {
         context = this.createSSLContextWithDefaultKeyManagerFactory();
         LOGGER.debug("Creating SSLContext with default keystore");
      } catch (TrustStoreConfigurationException var3) {
         context = this.createDefaultSSLContext();
         LOGGER.debug("Creating SSLContext with default configuration");
      }

      return context;
   }

   private SSLContext createSSLContextBasedOnConfiguration() throws KeyStoreConfigurationException, TrustStoreConfigurationException {
      return this.createSSLContext(false, false);
   }

   private SSLContext createSSLContextWithDefaultKeyManagerFactory() throws TrustStoreConfigurationException {
      try {
         return this.createSSLContext(true, false);
      } catch (KeyStoreConfigurationException var2) {
         LOGGER.debug("Exception occured while using default keystore. This should be a BUG");
         return null;
      }
   }

   private SSLContext createSSLContextWithDefaultTrustManagerFactory() throws KeyStoreConfigurationException {
      try {
         return this.createSSLContext(false, true);
      } catch (TrustStoreConfigurationException var2) {
         LOGGER.debug("Exception occured while using default truststore. This should be a BUG");
         return null;
      }
   }

   private SSLContext createDefaultSSLContext() {
      try {
         return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException var2) {
         LOGGER.error("Failed to create an SSLContext with default configuration");
         return null;
      }
   }

   private SSLContext createSSLContext(boolean loadDefaultKeyManagerFactory, boolean loadDefaultTrustManagerFactory) throws KeyStoreConfigurationException, TrustStoreConfigurationException {
      try {
         KeyManager[] kManagers = null;
         TrustManager[] tManagers = null;
         SSLContext sslContext = SSLContext.getInstance("SSL");
         if(!loadDefaultKeyManagerFactory) {
            KeyManagerFactory kmFactory = this.loadKeyManagerFactory();
            kManagers = kmFactory.getKeyManagers();
         }

         if(!loadDefaultTrustManagerFactory) {
            TrustManagerFactory tmFactory = this.loadTrustManagerFactory();
            tManagers = tmFactory.getTrustManagers();
         }

         sslContext.init(kManagers, tManagers, (SecureRandom)null);
         return sslContext;
      } catch (NoSuchAlgorithmException var7) {
         LOGGER.error("No Provider supports a TrustManagerFactorySpi implementation for the specified protocol");
         throw new TrustStoreConfigurationException(var7);
      } catch (KeyManagementException var8) {
         LOGGER.error("Failed to initialize the SSLContext");
         throw new KeyStoreConfigurationException(var8);
      }
   }

   private TrustManagerFactory loadTrustManagerFactory() throws TrustStoreConfigurationException {
      KeyStore trustStore = null;
      TrustManagerFactory tmFactory = null;
      if(this.trustStoreConfig == null) {
         throw new TrustStoreConfigurationException(new Exception("The trustStoreConfiguration is null"));
      } else {
         try {
            trustStore = this.trustStoreConfig.getTrustStore();
            tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmFactory.init(trustStore);
            return tmFactory;
         } catch (NoSuchAlgorithmException var4) {
            LOGGER.error("The specified algorithm is not available from the specified provider");
            throw new TrustStoreConfigurationException(var4);
         } catch (KeyStoreException var5) {
            LOGGER.error("Failed to initialize the TrustManagerFactory");
            throw new TrustStoreConfigurationException(var5);
         } catch (StoreConfigurationException var6) {
            throw new TrustStoreConfigurationException(var6);
         }
      }
   }

   private KeyManagerFactory loadKeyManagerFactory() throws KeyStoreConfigurationException {
      KeyStore keyStore = null;
      KeyManagerFactory kmFactory = null;
      if(this.keyStoreConfig == null) {
         throw new KeyStoreConfigurationException(new Exception("The keyStoreConfiguration is null"));
      } else {
         try {
            keyStore = this.keyStoreConfig.getKeyStore();
            kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmFactory.init(keyStore, this.keyStoreConfig.getPasswordAsCharArray());
            return kmFactory;
         } catch (NoSuchAlgorithmException var4) {
            LOGGER.error("The specified algorithm is not available from the specified provider");
            throw new KeyStoreConfigurationException(var4);
         } catch (KeyStoreException var5) {
            LOGGER.error("Failed to initialize the TrustManagerFactory");
            throw new KeyStoreConfigurationException(var5);
         } catch (StoreConfigurationException var6) {
            throw new KeyStoreConfigurationException(var6);
         } catch (UnrecoverableKeyException var7) {
            LOGGER.error("The key cannot be recovered (e.g. the given password is wrong)");
            throw new KeyStoreConfigurationException(var7);
         }
      }
   }

   public boolean equals(SSLConfiguration config) {
      if(config == null) {
         return false;
      } else {
         boolean keyStoreEquals = false;
         boolean trustStoreEquals = false;
         if(this.keyStoreConfig != null) {
            keyStoreEquals = this.keyStoreConfig.equals(config.keyStoreConfig);
         } else {
            keyStoreEquals = this.keyStoreConfig == config.keyStoreConfig;
         }

         if(this.trustStoreConfig != null) {
            trustStoreEquals = this.trustStoreConfig.equals(config.trustStoreConfig);
         } else {
            trustStoreEquals = this.trustStoreConfig == config.trustStoreConfig;
         }

         return keyStoreEquals && trustStoreEquals;
      }
   }

   @PluginFactory
   public static SSLConfiguration createSSLConfiguration(@PluginElement("keyStore") KeyStoreConfiguration keyStoreConfig, @PluginElement("trustStore") TrustStoreConfiguration trustStoreConfig) {
      return new SSLConfiguration(keyStoreConfig, trustStoreConfig);
   }
}
