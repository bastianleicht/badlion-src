package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class EnumUtils {
   private static final String NULL_ELEMENTS_NOT_PERMITTED = "null elements not permitted";
   private static final String CANNOT_STORE_S_S_VALUES_IN_S_BITS = "Cannot store %s %s values in %s bits";
   private static final String S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE = "%s does not seem to be an Enum type";
   private static final String ENUM_CLASS_MUST_BE_DEFINED = "EnumClass must be defined.";

   public static Map getEnumMap(Class enumClass) {
      Map<String, E> map = new LinkedHashMap();

      for(E e : (Enum[])enumClass.getEnumConstants()) {
         map.put(e.name(), e);
      }

      return map;
   }

   public static List getEnumList(Class enumClass) {
      return new ArrayList(Arrays.asList(enumClass.getEnumConstants()));
   }

   public static boolean isValidEnum(Class enumClass, String enumName) {
      if(enumName == null) {
         return false;
      } else {
         try {
            Enum.valueOf(enumClass, enumName);
            return true;
         } catch (IllegalArgumentException var3) {
            return false;
         }
      }
   }

   public static Enum getEnum(Class enumClass, String enumName) {
      if(enumName == null) {
         return null;
      } else {
         try {
            return Enum.valueOf(enumClass, enumName);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }
   }

   public static long generateBitVector(Class enumClass, Iterable values) {
      checkBitVectorable(enumClass);
      Validate.notNull(values);
      long total = 0L;

      for(E constant : values) {
         Validate.isTrue(constant != null, "null elements not permitted", new Object[0]);
         total |= (long)(1 << constant.ordinal());
      }

      return total;
   }

   public static long[] generateBitVectors(Class enumClass, Iterable values) {
      asEnum(enumClass);
      Validate.notNull(values);
      EnumSet<E> condensed = EnumSet.noneOf(enumClass);

      for(E constant : values) {
         Validate.isTrue(constant != null, "null elements not permitted", new Object[0]);
         condensed.add(constant);
      }

      long[] result = new long[(((Enum[])enumClass.getEnumConstants()).length - 1) / 64 + 1];

      for(E value : condensed) {
         int var10001 = value.ordinal() / 64;
         result[var10001] |= (long)(1 << value.ordinal() % 64);
      }

      ArrayUtils.reverse(result);
      return result;
   }

   public static long generateBitVector(Class enumClass, Enum... values) {
      Validate.noNullElements((Object[])values);
      return generateBitVector(enumClass, (Iterable)Arrays.asList(values));
   }

   public static long[] generateBitVectors(Class enumClass, Enum... values) {
      asEnum(enumClass);
      Validate.noNullElements((Object[])values);
      EnumSet<E> condensed = EnumSet.noneOf(enumClass);
      Collections.addAll(condensed, values);
      long[] result = new long[(((Enum[])enumClass.getEnumConstants()).length - 1) / 64 + 1];

      for(E value : condensed) {
         int var10001 = value.ordinal() / 64;
         result[var10001] |= (long)(1 << value.ordinal() % 64);
      }

      ArrayUtils.reverse(result);
      return result;
   }

   public static EnumSet processBitVector(Class enumClass, long value) {
      checkBitVectorable(enumClass).getEnumConstants();
      return processBitVectors(enumClass, new long[]{value});
   }

   public static EnumSet processBitVectors(Class enumClass, long... values) {
      EnumSet<E> results = EnumSet.noneOf(asEnum(enumClass));
      long[] lvalues = ArrayUtils.clone((long[])Validate.notNull(values));
      ArrayUtils.reverse(lvalues);

      for(E constant : (Enum[])enumClass.getEnumConstants()) {
         int block = constant.ordinal() / 64;
         if(block < lvalues.length && (lvalues[block] & (long)(1 << constant.ordinal() % 64)) != 0L) {
            results.add(constant);
         }
      }

      return results;
   }

   private static Class checkBitVectorable(Class enumClass) {
      E[] constants = (Enum[])asEnum(enumClass).getEnumConstants();
      Validate.isTrue(constants.length <= 64, "Cannot store %s %s values in %s bits", new Object[]{Integer.valueOf(constants.length), enumClass.getSimpleName(), Integer.valueOf(64)});
      return enumClass;
   }

   private static Class asEnum(Class enumClass) {
      Validate.notNull(enumClass, "EnumClass must be defined.", new Object[0]);
      Validate.isTrue(enumClass.isEnum(), "%s does not seem to be an Enum type", new Object[]{enumClass});
      return enumClass;
   }
}
