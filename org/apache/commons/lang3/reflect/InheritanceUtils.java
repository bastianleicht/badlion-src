package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.BooleanUtils;

public class InheritanceUtils {
   public static int distance(Class child, Class parent) {
      if(child != null && parent != null) {
         if(child.equals(parent)) {
            return 0;
         } else {
            Class<?> cParent = child.getSuperclass();
            int d = BooleanUtils.toInteger(parent.equals(cParent));
            if(d == 1) {
               return d;
            } else {
               d = d + distance(cParent, parent);
               return d > 0?d + 1:-1;
            }
         }
      } else {
         return -1;
      }
   }
}
