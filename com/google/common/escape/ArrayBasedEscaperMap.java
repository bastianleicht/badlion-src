package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@Beta
@GwtCompatible
public final class ArrayBasedEscaperMap {
   private final char[][] replacementArray;
   private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];

   public static ArrayBasedEscaperMap create(Map replacements) {
      return new ArrayBasedEscaperMap(createReplacementArray(replacements));
   }

   private ArrayBasedEscaperMap(char[][] replacementArray) {
      this.replacementArray = replacementArray;
   }

   char[][] getReplacementArray() {
      return this.replacementArray;
   }

   @VisibleForTesting
   static char[][] createReplacementArray(Map map) {
      Preconditions.checkNotNull(map);
      if(map.isEmpty()) {
         return EMPTY_REPLACEMENT_ARRAY;
      } else {
         char max = ((Character)Collections.max(map.keySet())).charValue();
         char[][] replacements = new char[max + 1][];

         char c;
         for(Iterator i$ = map.keySet().iterator(); i$.hasNext(); replacements[c] = ((String)map.get(Character.valueOf(c))).toCharArray()) {
            c = ((Character)i$.next()).charValue();
         }

         return replacements;
      }
   }
}
