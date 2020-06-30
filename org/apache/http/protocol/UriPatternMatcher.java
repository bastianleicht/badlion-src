package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.util.Args;

@ThreadSafe
public class UriPatternMatcher {
   @GuardedBy("this")
   private final Map map = new HashMap();

   public synchronized void register(String pattern, Object obj) {
      Args.notNull(pattern, "URI request pattern");
      this.map.put(pattern, obj);
   }

   public synchronized void unregister(String pattern) {
      if(pattern != null) {
         this.map.remove(pattern);
      }
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setHandlers(Map map) {
      Args.notNull(map, "Map of handlers");
      this.map.clear();
      this.map.putAll(map);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setObjects(Map map) {
      Args.notNull(map, "Map of handlers");
      this.map.clear();
      this.map.putAll(map);
   }

   /** @deprecated */
   @Deprecated
   public synchronized Map getObjects() {
      return this.map;
   }

   public synchronized Object lookup(String path) {
      Args.notNull(path, "Request path");
      T obj = this.map.get(path);
      if(obj == null) {
         String bestMatch = null;

         for(String pattern : this.map.keySet()) {
            if(this.matchUriRequestPattern(pattern, path) && (bestMatch == null || bestMatch.length() < pattern.length() || bestMatch.length() == pattern.length() && pattern.endsWith("*"))) {
               obj = this.map.get(pattern);
               bestMatch = pattern;
            }
         }
      }

      return obj;
   }

   protected boolean matchUriRequestPattern(String pattern, String path) {
      return pattern.equals("*")?true:pattern.endsWith("*") && path.startsWith(pattern.substring(0, pattern.length() - 1)) || pattern.startsWith("*") && path.endsWith(pattern.substring(1, pattern.length()));
   }

   public String toString() {
      return this.map.toString();
   }
}
