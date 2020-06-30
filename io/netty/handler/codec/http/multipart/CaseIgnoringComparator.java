package io.netty.handler.codec.http.multipart;

import java.io.Serializable;
import java.util.Comparator;

final class CaseIgnoringComparator implements Comparator, Serializable {
   private static final long serialVersionUID = 4582133183775373862L;
   static final CaseIgnoringComparator INSTANCE = new CaseIgnoringComparator();

   public int compare(CharSequence o1, CharSequence o2) {
      int o1Length = o1.length();
      int o2Length = o2.length();
      int min = Math.min(o1Length, o2Length);

      for(int i = 0; i < min; ++i) {
         char c1 = o1.charAt(i);
         char c2 = o2.charAt(i);
         if(c1 != c2) {
            c1 = Character.toUpperCase(c1);
            c2 = Character.toUpperCase(c2);
            if(c1 != c2) {
               c1 = Character.toLowerCase(c1);
               c2 = Character.toLowerCase(c2);
               if(c1 != c2) {
                  return c1 - c2;
               }
            }
         }
      }

      return o1Length - o2Length;
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
