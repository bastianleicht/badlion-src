package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CompareToBuilder implements Builder {
   private int comparison = 0;

   public static int reflectionCompare(Object lhs, Object rhs) {
      return reflectionCompare(lhs, rhs, false, (Class)null, new String[0]);
   }

   public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients) {
      return reflectionCompare(lhs, rhs, compareTransients, (Class)null, new String[0]);
   }

   public static int reflectionCompare(Object lhs, Object rhs, Collection excludeFields) {
      return reflectionCompare(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
   }

   public static int reflectionCompare(Object lhs, Object rhs, String... excludeFields) {
      return reflectionCompare(lhs, rhs, false, (Class)null, excludeFields);
   }

   public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients, Class reflectUpToClass, String... excludeFields) {
      if(lhs == rhs) {
         return 0;
      } else if(lhs != null && rhs != null) {
         Class<?> lhsClazz = lhs.getClass();
         if(!lhsClazz.isInstance(rhs)) {
            throw new ClassCastException();
         } else {
            CompareToBuilder compareToBuilder = new CompareToBuilder();
            reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);

            while(lhsClazz.getSuperclass() != null && lhsClazz != reflectUpToClass) {
               lhsClazz = lhsClazz.getSuperclass();
               reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);
            }

            return compareToBuilder.toComparison();
         }
      } else {
         throw new NullPointerException();
      }
   }

   private static void reflectionAppend(Object lhs, Object rhs, Class clazz, CompareToBuilder builder, boolean useTransients, String[] excludeFields) {
      Field[] fields = clazz.getDeclaredFields();
      AccessibleObject.setAccessible(fields, true);

      for(int i = 0; i < fields.length && builder.comparison == 0; ++i) {
         Field f = fields[i];
         if(!ArrayUtils.contains(excludeFields, f.getName()) && f.getName().indexOf(36) == -1 && (useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers())) {
            try {
               builder.append(f.get(lhs), f.get(rhs));
            } catch (IllegalAccessException var10) {
               throw new InternalError("Unexpected IllegalAccessException");
            }
         }
      }

   }

   public CompareToBuilder appendSuper(int superCompareTo) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = superCompareTo;
         return this;
      }
   }

   public CompareToBuilder append(Object lhs, Object rhs) {
      return this.append((Object)lhs, (Object)rhs, (Comparator)null);
   }

   public CompareToBuilder append(Object lhs, Object rhs, Comparator comparator) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else {
         if(lhs.getClass().isArray()) {
            if(lhs instanceof long[]) {
               this.append((long[])((long[])lhs), (long[])((long[])rhs));
            } else if(lhs instanceof int[]) {
               this.append((int[])((int[])lhs), (int[])((int[])rhs));
            } else if(lhs instanceof short[]) {
               this.append((short[])((short[])lhs), (short[])((short[])rhs));
            } else if(lhs instanceof char[]) {
               this.append((char[])((char[])lhs), (char[])((char[])rhs));
            } else if(lhs instanceof byte[]) {
               this.append((byte[])((byte[])lhs), (byte[])((byte[])rhs));
            } else if(lhs instanceof double[]) {
               this.append((double[])((double[])lhs), (double[])((double[])rhs));
            } else if(lhs instanceof float[]) {
               this.append((float[])((float[])lhs), (float[])((float[])rhs));
            } else if(lhs instanceof boolean[]) {
               this.append((boolean[])((boolean[])lhs), (boolean[])((boolean[])rhs));
            } else {
               this.append((Object[])((Object[])lhs), (Object[])((Object[])rhs), comparator);
            }
         } else if(comparator == null) {
            Comparable<Object> comparable = (Comparable)lhs;
            this.comparison = comparable.compareTo(rhs);
         } else {
            this.comparison = comparator.compare(lhs, rhs);
         }

         return this;
      }
   }

   public CompareToBuilder append(long lhs, long rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = lhs < rhs?-1:(lhs > rhs?1:0);
         return this;
      }
   }

   public CompareToBuilder append(int lhs, int rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = lhs < rhs?-1:(lhs > rhs?1:0);
         return this;
      }
   }

   public CompareToBuilder append(short lhs, short rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = lhs < rhs?-1:(lhs > rhs?1:0);
         return this;
      }
   }

   public CompareToBuilder append(char lhs, char rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = lhs < rhs?-1:(lhs > rhs?1:0);
         return this;
      }
   }

   public CompareToBuilder append(byte lhs, byte rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = lhs < rhs?-1:(lhs > rhs?1:0);
         return this;
      }
   }

   public CompareToBuilder append(double lhs, double rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = Double.compare(lhs, rhs);
         return this;
      }
   }

   public CompareToBuilder append(float lhs, float rhs) {
      if(this.comparison != 0) {
         return this;
      } else {
         this.comparison = Float.compare(lhs, rhs);
         return this;
      }
   }

   public CompareToBuilder append(boolean lhs, boolean rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else {
         if(!lhs) {
            this.comparison = -1;
         } else {
            this.comparison = 1;
         }

         return this;
      }
   }

   public CompareToBuilder append(Object[] lhs, Object[] rhs) {
      return this.append((Object[])lhs, (Object[])rhs, (Comparator)null);
   }

   public CompareToBuilder append(Object[] lhs, Object[] rhs, Comparator comparator) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i], comparator);
         }

         return this;
      }
   }

   public CompareToBuilder append(long[] lhs, long[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(int[] lhs, int[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(short[] lhs, short[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(char[] lhs, char[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(byte[] lhs, byte[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(double[] lhs, double[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(float[] lhs, float[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public CompareToBuilder append(boolean[] lhs, boolean[] rhs) {
      if(this.comparison != 0) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else if(lhs == null) {
         this.comparison = -1;
         return this;
      } else if(rhs == null) {
         this.comparison = 1;
         return this;
      } else if(lhs.length != rhs.length) {
         this.comparison = lhs.length < rhs.length?-1:1;
         return this;
      } else {
         for(int i = 0; i < lhs.length && this.comparison == 0; ++i) {
            this.append(lhs[i], rhs[i]);
         }

         return this;
      }
   }

   public int toComparison() {
      return this.comparison;
   }

   public Integer build() {
      return Integer.valueOf(this.toComparison());
   }
}
