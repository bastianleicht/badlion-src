package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ThreadContextMap {
   void put(String var1, String var2);

   String get(String var1);

   void remove(String var1);

   void clear();

   boolean containsKey(String var1);

   Map getCopy();

   Map getImmutableMapOrNull();

   boolean isEmpty();
}
