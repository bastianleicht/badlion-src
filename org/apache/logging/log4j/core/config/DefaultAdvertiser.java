package org.apache.logging.log4j.core.config;

import java.util.Map;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.net.Advertiser;

@Plugin(
   name = "default",
   category = "Core",
   elementType = "advertiser",
   printObject = false
)
public class DefaultAdvertiser implements Advertiser {
   public Object advertise(Map properties) {
      return null;
   }

   public void unadvertise(Object advertisedObject) {
   }
}
