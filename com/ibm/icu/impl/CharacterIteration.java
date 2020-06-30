package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import java.text.CharacterIterator;

public final class CharacterIteration {
   public static final int DONE32 = Integer.MAX_VALUE;

   public static int next32(CharacterIterator ci) {
      int c = ci.current();
      if(c >= '\ud800' && c <= '\udbff') {
         c = ci.next();
         if(c < '\udc00' || c > '\udfff') {
            c = ci.previous();
         }
      }

      c = ci.next();
      if(c >= '\ud800') {
         c = nextTrail32(ci, c);
      }

      if(c >= 65536 && c != Integer.MAX_VALUE) {
         ci.previous();
      }

      return c;
   }

   public static int nextTrail32(CharacterIterator ci, int lead) {
      if(lead == '\uffff' && ci.getIndex() >= ci.getEndIndex()) {
         return Integer.MAX_VALUE;
      } else {
         int retVal = lead;
         if(lead <= '\udbff') {
            char cTrail = ci.next();
            if(UTF16.isTrailSurrogate(cTrail)) {
               retVal = (lead - '\ud800' << 10) + (cTrail - '\udc00') + 65536;
            } else {
               ci.previous();
            }
         }

         return retVal;
      }
   }

   public static int previous32(CharacterIterator ci) {
      if(ci.getIndex() <= ci.getBeginIndex()) {
         return Integer.MAX_VALUE;
      } else {
         char trail = ci.previous();
         int retVal = trail;
         if(UTF16.isTrailSurrogate(trail) && ci.getIndex() > ci.getBeginIndex()) {
            char lead = ci.previous();
            if(UTF16.isLeadSurrogate(lead)) {
               retVal = (lead - '\ud800' << 10) + (trail - '\udc00') + 65536;
            } else {
               ci.next();
            }
         }

         return retVal;
      }
   }

   public static int current32(CharacterIterator ci) {
      char lead = ci.current();
      int retVal = lead;
      if(lead < '\ud800') {
         return lead;
      } else {
         if(UTF16.isLeadSurrogate(lead)) {
            int trail = ci.next();
            ci.previous();
            if(UTF16.isTrailSurrogate((char)trail)) {
               retVal = (lead - '\ud800' << 10) + (trail - '\udc00') + 65536;
            }
         } else if(lead == '\uffff' && ci.getIndex() >= ci.getEndIndex()) {
            retVal = Integer.MAX_VALUE;
         }

         return retVal;
      }
   }
}
