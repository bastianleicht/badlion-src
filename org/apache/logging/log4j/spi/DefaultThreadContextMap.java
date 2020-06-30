package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.spi.ThreadContextMap;

public class DefaultThreadContextMap implements ThreadContextMap {
   private final boolean useMap;
   private final ThreadLocal localMap = new InheritableThreadLocal() {
      protected Map childValue(Map parentValue) {
         return parentValue != null && DefaultThreadContextMap.this.useMap?Collections.unmodifiableMap(new HashMap(parentValue)):null;
      }
   };

   public DefaultThreadContextMap(boolean useMap) {
      this.useMap = useMap;
   }

   public void put(String key, String value) {
      if(this.useMap) {
         Map<String, String> map = (Map)this.localMap.get();
         HashMap var4 = map == null?new HashMap():new HashMap(map);
         var4.put(key, value);
         this.localMap.set(Collections.unmodifiableMap(var4));
      }
   }

   public String get(String key) {
      Map<String, String> map = (Map)this.localMap.get();
      return map == null?null:(String)map.get(key);
   }

   public void remove(String key) {
      Map<String, String> map = (Map)this.localMap.get();
      if(map != null) {
         Map<String, String> copy = new HashMap(map);
         copy.remove(key);
         this.localMap.set(Collections.unmodifiableMap(copy));
      }

   }

   public void clear() {
      this.localMap.remove();
   }

   public boolean containsKey(String key) {
      Map<String, String> map = (Map)this.localMap.get();
      return map != null && map.containsKey(key);
   }

   public Map getCopy() {
      Map<String, String> map = (Map)this.localMap.get();
      return map == null?new HashMap():new HashMap(map);
   }

   public Map getImmutableMapOrNull() {
      return (Map)this.localMap.get();
   }

   public boolean isEmpty() {
      Map<String, String> map = (Map)this.localMap.get();
      return map == null || map.size() == 0;
   }

   public String toString() {
      Map<String, String> map = (Map)this.localMap.get();
      return map == null?"{}":map.toString();
   }
}
