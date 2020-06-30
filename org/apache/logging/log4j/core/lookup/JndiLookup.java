package org.apache.logging.log4j.core.lookup;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(
   name = "jndi",
   category = "Lookup"
)
public class JndiLookup implements StrLookup {
   static final String CONTAINER_JNDI_RESOURCE_PATH_PREFIX = "java:comp/env/";

   public String lookup(String key) {
      return this.lookup((LogEvent)null, key);
   }

   public String lookup(LogEvent event, String key) {
      if(key == null) {
         return null;
      } else {
         try {
            InitialContext ctx = new InitialContext();
            return (String)ctx.lookup(this.convertJndiName(key));
         } catch (NamingException var4) {
            return null;
         }
      }
   }

   private String convertJndiName(String jndiName) {
      if(!jndiName.startsWith("java:comp/env/") && jndiName.indexOf(58) == -1) {
         jndiName = "java:comp/env/" + jndiName;
      }

      return jndiName;
   }
}
