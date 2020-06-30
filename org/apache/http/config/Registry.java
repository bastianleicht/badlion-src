package org.apache.http.config;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.config.Lookup;

@ThreadSafe
public final class Registry implements Lookup {
   private final Map map;

   Registry(Map map) {
      this.map = new ConcurrentHashMap(map);
   }

   public Object lookup(String key) {
      return key == null?null:this.map.get(key.toLowerCase(Locale.US));
   }

   public String toString() {
      return this.map.toString();
   }
}
