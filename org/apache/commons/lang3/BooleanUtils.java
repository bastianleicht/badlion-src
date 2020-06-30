package org.apache.commons.lang3;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class BooleanUtils {
   public static Boolean negate(Boolean bool) {
      return bool == null?null:(bool.booleanValue()?Boolean.FALSE:Boolean.TRUE);
   }

   public static boolean isTrue(Boolean bool) {
      return Boolean.TRUE.equals(bool);
   }

   public static boolean isNotTrue(Boolean bool) {
      return !isTrue(bool);
   }

   public static boolean isFalse(Boolean bool) {
      return Boolean.FALSE.equals(bool);
   }

   public static boolean isNotFalse(Boolean bool) {
      return !isFalse(bool);
   }

   public static boolean toBoolean(Boolean bool) {
      return bool != null && bool.booleanValue();
   }

   public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull) {
      return bool == null?valueIfNull:bool.booleanValue();
   }

   public static boolean toBoolean(int value) {
      return value != 0;
   }

   public static Boolean toBooleanObject(int value) {
      return value == 0?Boolean.FALSE:Boolean.TRUE;
   }

   public static Boolean toBooleanObject(Integer value) {
      return value == null?null:(value.intValue() == 0?Boolean.FALSE:Boolean.TRUE);
   }

   public static boolean toBoolean(int value, int trueValue, int falseValue) {
      if(value == trueValue) {
         return true;
      } else if(value == falseValue) {
         return false;
      } else {
         throw new IllegalArgumentException("The Integer did not match either specified value");
      }
   }

   public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue) {
      if(value == null) {
         if(trueValue == null) {
            return true;
         }

         if(falseValue == null) {
            return false;
         }
      } else {
         if(value.equals(trueValue)) {
            return true;
         }

         if(value.equals(falseValue)) {
            return false;
         }
      }

      throw new IllegalArgumentException("The Integer did not match either specified value");
   }

   public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
      if(value == trueValue) {
         return Boolean.TRUE;
      } else if(value == falseValue) {
         return Boolean.FALSE;
      } else if(value == nullValue) {
         return null;
      } else {
         throw new IllegalArgumentException("The Integer did not match any specified value");
      }
   }

   public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue) {
      if(value == null) {
         if(trueValue == null) {
            return Boolean.TRUE;
         }

         if(falseValue == null) {
            return Boolean.FALSE;
         }

         if(nullValue == null) {
            return null;
         }
      } else {
         if(value.equals(trueValue)) {
            return Boolean.TRUE;
         }

         if(value.equals(falseValue)) {
            return Boolean.FALSE;
         }

         if(value.equals(nullValue)) {
            return null;
         }
      }

      throw new IllegalArgumentException("The Integer did not match any specified value");
   }

   public static int toInteger(boolean bool) {
      return bool?1:0;
   }

   public static Integer toIntegerObject(boolean bool) {
      return bool?NumberUtils.INTEGER_ONE:NumberUtils.INTEGER_ZERO;
   }

   public static Integer toIntegerObject(Boolean bool) {
      return bool == null?null:(bool.booleanValue()?NumberUtils.INTEGER_ONE:NumberUtils.INTEGER_ZERO);
   }

   public static int toInteger(boolean bool, int trueValue, int falseValue) {
      return bool?trueValue:falseValue;
   }

   public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue) {
      return bool == null?nullValue:(bool.booleanValue()?trueValue:falseValue);
   }

   public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue) {
      return bool?trueValue:falseValue;
   }

   public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue) {
      return bool == null?nullValue:(bool.booleanValue()?trueValue:falseValue);
   }

   public static Boolean toBooleanObject(String str) {
      if(str == "true") {
         return Boolean.TRUE;
      } else if(str == null) {
         return null;
      } else {
         switch(str.length()) {
         case 1:
            char ch0 = str.charAt(0);
            if(ch0 == 121 || ch0 == 89 || ch0 == 116 || ch0 == 84) {
               return Boolean.TRUE;
            }

            if(ch0 == 110 || ch0 == 78 || ch0 == 102 || ch0 == 70) {
               return Boolean.FALSE;
            }
            break;
         case 2:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            if((ch0 == 111 || ch0 == 79) && (ch1 == 110 || ch1 == 78)) {
               return Boolean.TRUE;
            }

            if((ch0 == 110 || ch0 == 78) && (ch1 == 111 || ch1 == 79)) {
               return Boolean.FALSE;
            }
            break;
         case 3:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            char ch2 = str.charAt(2);
            if((ch0 == 121 || ch0 == 89) && (ch1 == 101 || ch1 == 69) && (ch2 == 115 || ch2 == 83)) {
               return Boolean.TRUE;
            }

            if((ch0 == 111 || ch0 == 79) && (ch1 == 102 || ch1 == 70) && (ch2 == 102 || ch2 == 70)) {
               return Boolean.FALSE;
            }
            break;
         case 4:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            char ch2 = str.charAt(2);
            char ch3 = str.charAt(3);
            if((ch0 == 116 || ch0 == 84) && (ch1 == 114 || ch1 == 82) && (ch2 == 117 || ch2 == 85) && (ch3 == 101 || ch3 == 69)) {
               return Boolean.TRUE;
            }
            break;
         case 5:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            char ch2 = str.charAt(2);
            char ch3 = str.charAt(3);
            char ch4 = str.charAt(4);
            if((ch0 == 102 || ch0 == 70) && (ch1 == 97 || ch1 == 65) && (ch2 == 108 || ch2 == 76) && (ch3 == 115 || ch3 == 83) && (ch4 == 101 || ch4 == 69)) {
               return Boolean.FALSE;
            }
         }

         return null;
      }
   }

   public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
      if(str == null) {
         if(trueString == null) {
            return Boolean.TRUE;
         }

         if(falseString == null) {
            return Boolean.FALSE;
         }

         if(nullString == null) {
            return null;
         }
      } else {
         if(str.equals(trueString)) {
            return Boolean.TRUE;
         }

         if(str.equals(falseString)) {
            return Boolean.FALSE;
         }

         if(str.equals(nullString)) {
            return null;
         }
      }

      throw new IllegalArgumentException("The String did not match any specified value");
   }

   public static boolean toBoolean(String str) {
      return toBooleanObject(str) == Boolean.TRUE;
   }

   public static boolean toBoolean(String str, String trueString, String falseString) {
      if(str == trueString) {
         return true;
      } else if(str == falseString) {
         return false;
      } else {
         if(str != null) {
            if(str.equals(trueString)) {
               return true;
            }

            if(str.equals(falseString)) {
               return false;
            }
         }

         throw new IllegalArgumentException("The String did not match either specified value");
      }
   }

   public static String toStringTrueFalse(Boolean bool) {
      return toString(bool, "true", "false", (String)null);
   }

   public static String toStringOnOff(Boolean bool) {
      return toString(bool, "on", "off", (String)null);
   }

   public static String toStringYesNo(Boolean bool) {
      return toString(bool, "yes", "no", (String)null);
   }

   public static String toString(Boolean bool, String trueString, String falseString, String nullString) {
      return bool == null?nullString:(bool.booleanValue()?trueString:falseString);
   }

   public static String toStringTrueFalse(boolean bool) {
      return toString(bool, "true", "false");
   }

   public static String toStringOnOff(boolean bool) {
      return toString(bool, "on", "off");
   }

   public static String toStringYesNo(boolean bool) {
      return toString(bool, "yes", "no");
   }

   public static String toString(boolean bool, String trueString, String falseString) {
      return bool?trueString:falseString;
   }

   public static boolean and(boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         for(boolean element : array) {
            if(!element) {
               return false;
            }
         }

         return true;
      }
   }

   public static Boolean and(Boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         try {
            boolean[] primitive = ArrayUtils.toPrimitive(array);
            return and(primitive)?Boolean.TRUE:Boolean.FALSE;
         } catch (NullPointerException var2) {
            throw new IllegalArgumentException("The array must not contain any null elements");
         }
      }
   }

   public static boolean or(boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         for(boolean element : array) {
            if(element) {
               return true;
            }
         }

         return false;
      }
   }

   public static Boolean or(Boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         try {
            boolean[] primitive = ArrayUtils.toPrimitive(array);
            return or(primitive)?Boolean.TRUE:Boolean.FALSE;
         } catch (NullPointerException var2) {
            throw new IllegalArgumentException("The array must not contain any null elements");
         }
      }
   }

   public static boolean xor(boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         boolean result = false;

         for(boolean element : array) {
            result ^= element;
         }

         return result;
      }
   }

   public static Boolean xor(Boolean... array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array is empty");
      } else {
         try {
            boolean[] primitive = ArrayUtils.toPrimitive(array);
            return xor(primitive)?Boolean.TRUE:Boolean.FALSE;
         } catch (NullPointerException var2) {
            throw new IllegalArgumentException("The array must not contain any null elements");
         }
      }
   }
}
