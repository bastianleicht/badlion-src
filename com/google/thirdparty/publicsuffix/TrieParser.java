package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import java.util.List;

@GwtCompatible
class TrieParser {
   private static final Joiner PREFIX_JOINER = Joiner.on("");

   static ImmutableMap parseTrie(CharSequence encoded) {
      ImmutableMap.Builder<String, PublicSuffixType> builder = ImmutableMap.builder();
      int encodedLen = encoded.length();

      for(int idx = 0; idx < encodedLen; idx += doParseTrieToBuilder(Lists.newLinkedList(), encoded.subSequence(idx, encodedLen), builder)) {
         ;
      }

      return builder.build();
   }

   private static int doParseTrieToBuilder(List stack, CharSequence encoded, ImmutableMap.Builder builder) {
      int encodedLen = encoded.length();
      int idx = 0;

      char c;
      for(c = 0; idx < encodedLen; ++idx) {
         c = encoded.charAt(idx);
         if(c == 38 || c == 63 || c == 33 || c == 58 || c == 44) {
            break;
         }
      }

      stack.add(0, reverse(encoded.subSequence(0, idx)));
      if(c == 33 || c == 63 || c == 58 || c == 44) {
         String domain = PREFIX_JOINER.join((Iterable)stack);
         if(domain.length() > 0) {
            builder.put(domain, PublicSuffixType.fromCode(c));
         }
      }

      ++idx;
      if(c != 63 && c != 44) {
         while(idx < encodedLen) {
            idx += doParseTrieToBuilder(stack, encoded.subSequence(idx, encodedLen), builder);
            if(encoded.charAt(idx) == 63 || encoded.charAt(idx) == 44) {
               ++idx;
               break;
            }
         }
      }

      stack.remove(0);
      return idx;
   }

   private static CharSequence reverse(CharSequence s) {
      int length = s.length();
      if(length <= 1) {
         return s;
      } else {
         char[] buffer = new char[length];
         buffer[0] = s.charAt(length - 1);

         for(int i = 1; i < length; ++i) {
            buffer[i] = s.charAt(length - 1 - i);
            if(Character.isSurrogatePair(buffer[i], buffer[i - 1])) {
               swap(buffer, i - 1, i);
            }
         }

         return new String(buffer);
      }
   }

   private static void swap(char[] buffer, int f, int s) {
      char tmp = buffer[f];
      buffer[f] = buffer[s];
      buffer[s] = tmp;
   }
}
