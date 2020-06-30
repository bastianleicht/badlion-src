package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.core.net.ssl.StoreConfigurationException;
import org.apache.logging.log4j.status.StatusLogger;

public class StoreConfiguration {
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   private String location;
   private String password;

   public StoreConfiguration(String location, String password) {
      this.location = location;
      this.password = password;
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getPassword() {
      return this.password;
   }

   public char[] getPasswordAsCharArray() {
      return this.password == null?null:this.password.toCharArray();
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean equals(StoreConfiguration config) {
      if(config == null) {
         return false;
      } else {
         boolean locationEquals = false;
         boolean passwordEquals = false;
         if(this.location != null) {
            locationEquals = this.location.equals(config.location);
         } else {
            locationEquals = this.location == config.location;
         }

         if(this.password != null) {
            passwordEquals = this.password.equals(config.password);
         } else {
            passwordEquals = this.password == config.password;
         }

         return locationEquals && passwordEquals;
      }
   }

   protected void load() throws StoreConfigurationException {
   }
}
