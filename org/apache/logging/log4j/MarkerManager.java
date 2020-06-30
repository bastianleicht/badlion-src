package org.apache.logging.log4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Marker;

public final class MarkerManager {
   private static ConcurrentMap markerMap = new ConcurrentHashMap();

   public static Marker getMarker(String name) {
      markerMap.putIfAbsent(name, new MarkerManager.Log4jMarker(name));
      return (Marker)markerMap.get(name);
   }

   public static Marker getMarker(String name, String parent) {
      Marker parentMarker = (Marker)markerMap.get(parent);
      if(parentMarker == null) {
         throw new IllegalArgumentException("Parent Marker " + parent + " has not been defined");
      } else {
         return getMarker(name, parentMarker);
      }
   }

   public static Marker getMarker(String name, Marker parent) {
      markerMap.putIfAbsent(name, new MarkerManager.Log4jMarker(name, parent));
      return (Marker)markerMap.get(name);
   }

   private static class Log4jMarker implements Marker {
      private static final long serialVersionUID = 100L;
      private final String name;
      private final Marker parent;

      public Log4jMarker(String name) {
         this.name = name;
         this.parent = null;
      }

      public Log4jMarker(String name, Marker parent) {
         this.name = name;
         this.parent = parent;
      }

      public String getName() {
         return this.name;
      }

      public Marker getParent() {
         return this.parent;
      }

      public boolean isInstanceOf(Marker m) {
         if(m == null) {
            throw new IllegalArgumentException("A marker parameter is required");
         } else {
            Marker test = this;

            while(test != m) {
               test = test.getParent();
               if(test == null) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean isInstanceOf(String name) {
         if(name == null) {
            throw new IllegalArgumentException("A marker name is required");
         } else {
            Marker toTest = this;

            while(!name.equals(((Marker)toTest).getName())) {
               toTest = toTest.getParent();
               if(toTest == null) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean equals(Object o) {
         if(this == o) {
            return true;
         } else if(o != null && o instanceof Marker) {
            Marker marker = (Marker)o;
            if(this.name != null) {
               if(!this.name.equals(marker.getName())) {
                  return false;
               }
            } else if(marker.getName() != null) {
               return false;
            }

            return true;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.name != null?this.name.hashCode():0;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder(this.name);
         if(this.parent != null) {
            Marker m = this.parent;
            sb.append("[ ");

            for(boolean first = true; m != null; m = m.getParent()) {
               if(!first) {
                  sb.append(", ");
               }

               sb.append(m.getName());
               first = false;
            }

            sb.append(" ]");
         }

         return sb.toString();
      }
   }
}
