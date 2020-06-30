package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
   public static Map createMap(Iterable keys, Iterable values) {
      return populateMap(keys, values, Maps.newLinkedHashMap());
   }

   public static Map populateMap(Iterable keys, Iterable values, Map map) {
      Iterator<V> iterator = values.iterator();

      for(K k : keys) {
         map.put(k, iterator.next());
      }

      if(iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return map;
      }
   }
}
