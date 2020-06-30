package org.apache.logging.log4j.core.net.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.ssl.StoreConfiguration;
import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;

@Plugin(
   name = "keyStore",
   category = "Core",
   printObject = true
)
public class KeyStoreConfiguration extends StoreConfiguration {
   private KeyStore keyStore = null;
   private String keyStoreType = "JKS";

   public KeyStoreConfiguration(String location, String password) {
      super(location, password);
   }

   protected void load() throws StoreConfigurationException {
      FileInputStream fin = null;
      LOGGER.debug("Loading keystore from file with params(location={})", new Object[]{this.getLocation()});

      try {
         if(this.getLocation() == null) {
            throw new IOException("The location is null");
         }

         fin = new FileInputStream(this.getLocation());
         KeyStore ks = KeyStore.getInstance(this.keyStoreType);
         ks.load(fin, this.getPasswordAsCharArray());
         this.keyStore = ks;
      } catch (CertificateException var14) {
         LOGGER.error("No Provider supports a KeyStoreSpi implementation for the specified type {}", new Object[]{this.keyStoreType});
         throw new StoreConfigurationException(var14);
      } catch (NoSuchAlgorithmException var15) {
         LOGGER.error("The algorithm used to check the integrity of the keystore cannot be found");
         throw new StoreConfigurationException(var15);
      } catch (KeyStoreException var16) {
         LOGGER.error(var16);
         throw new StoreConfigurationException(var16);
      } catch (FileNotFoundException var17) {
         LOGGER.error("The keystore file({}) is not found", new Object[]{this.getLocation()});
         throw new StoreConfigurationException(var17);
      } catch (IOException var18) {
         LOGGER.error("Something is wrong with the format of the keystore or the given password");
         throw new StoreConfigurationException(var18);
      } finally {
         try {
            if(fin != null) {
               fin.close();
            }
         } catch (IOException var13) {
            ;
         }

      }

      LOGGER.debug("Keystore successfully loaded with params(location={})", new Object[]{this.getLocation()});
   }

   public KeyStore getKeyStore() throws StoreConfigurationException {
      if(this.keyStore == null) {
         this.load();
      }

      return this.keyStore;
   }

   @PluginFactory
   public static KeyStoreConfiguration createKeyStoreConfiguration(@PluginAttribute("location") String location, @PluginAttribute("password") String password) {
      return new KeyStoreConfiguration(location, password);
   }
}
