package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class ParseRequest {
   final String rawValue;
   final int radix;

   private ParseRequest(String rawValue, int radix) {
      this.rawValue = rawValue;
      this.radix = radix;
   }

   static ParseRequest fromString(String stringValue) {
      if(stringValue.length() == 0) {
         throw new NumberFormatException("empty string");
      } else {
         char firstChar = stringValue.charAt(0);
         String rawValue;
         int radix;
         if(!stringValue.startsWith("0x") && !stringValue.startsWith("0X")) {
            if(firstChar == 35) {
               rawValue = stringValue.substring(1);
               radix = 16;
            } else if(firstChar == 48 && stringValue.length() > 1) {
               rawValue = stringValue.substring(1);
               radix = 8;
            } else {
               rawValue = stringValue;
               radix = 10;
            }
         } else {
            rawValue = stringValue.substring(2);
            radix = 16;
         }

         return new ParseRequest(rawValue, radix);
      }
   }
}
