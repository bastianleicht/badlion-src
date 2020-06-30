package org.apache.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnnotationUtils {
   private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
      private static final long serialVersionUID = 1L;

      {
         this.setDefaultFullDetail(true);
         this.setArrayContentDetail(true);
         this.setUseClassName(true);
         this.setUseShortClassName(true);
         this.setUseIdentityHashCode(false);
         this.setContentStart("(");
         this.setContentEnd(")");
         this.setFieldSeparator(", ");
         this.setArrayStart("[");
         this.setArrayEnd("]");
      }

      protected String getShortClassName(Class cls) {
         Class<? extends Annotation> annotationType = null;

         for(Class<?> iface : ClassUtils.getAllInterfaces(cls)) {
            if(Annotation.class.isAssignableFrom(iface)) {
               annotationType = iface;
               break;
            }
         }

         return (new StringBuilder(annotationType == null?"":annotationType.getName())).insert(0, '@').toString();
      }

      protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
         if(value instanceof Annotation) {
            value = AnnotationUtils.toString((Annotation)value);
         }

         super.appendDetail(buffer, fieldName, value);
      }
   };

   public static boolean equals(Annotation a1, Annotation a2) {
      if(a1 == a2) {
         return true;
      } else if(a1 != null && a2 != null) {
         Class<? extends Annotation> type = a1.annotationType();
         Class<? extends Annotation> type2 = a2.annotationType();
         Validate.notNull(type, "Annotation %s with null annotationType()", new Object[]{a1});
         Validate.notNull(type2, "Annotation %s with null annotationType()", new Object[]{a2});
         if(!type.equals(type2)) {
            return false;
         } else {
            try {
               for(Method m : type.getDeclaredMethods()) {
                  if(m.getParameterTypes().length == 0 && isValidAnnotationMemberType(m.getReturnType())) {
                     Object v1 = m.invoke(a1, new Object[0]);
                     Object v2 = m.invoke(a2, new Object[0]);
                     if(!memberEquals(m.getReturnType(), v1, v2)) {
                        return false;
                     }
                  }
               }

               return true;
            } catch (IllegalAccessException var10) {
               return false;
            } catch (InvocationTargetException var11) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static int hashCode(Annotation a) {
      int result = 0;
      Class<? extends Annotation> type = a.annotationType();

      for(Method m : type.getDeclaredMethods()) {
         try {
            Object value = m.invoke(a, new Object[0]);
            if(value == null) {
               throw new IllegalStateException(String.format("Annotation method %s returned null", new Object[]{m}));
            }

            result += hashMember(m.getName(), value);
         } catch (RuntimeException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }
      }

      return result;
   }

   public static String toString(Annotation a) {
      ToStringBuilder builder = new ToStringBuilder(a, TO_STRING_STYLE);

      for(Method m : a.annotationType().getDeclaredMethods()) {
         if(m.getParameterTypes().length <= 0) {
            try {
               builder.append(m.getName(), m.invoke(a, new Object[0]));
            } catch (RuntimeException var7) {
               throw var7;
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      return builder.build();
   }

   public static boolean isValidAnnotationMemberType(Class type) {
      if(type == null) {
         return false;
      } else {
         if(type.isArray()) {
            type = type.getComponentType();
         }

         return type.isPrimitive() || type.isEnum() || type.isAnnotation() || String.class.equals(type) || Class.class.equals(type);
      }
   }

   private static int hashMember(String name, Object value) {
      int part1 = name.hashCode() * 127;
      return value.getClass().isArray()?part1 ^ arrayMemberHash(value.getClass().getComponentType(), value):(value instanceof Annotation?part1 ^ hashCode((Annotation)value):part1 ^ value.hashCode());
   }

   private static boolean memberEquals(Class type, Object o1, Object o2) {
      return o1 == o2?true:(o1 != null && o2 != null?(type.isArray()?arrayMemberEquals(type.getComponentType(), o1, o2):(type.isAnnotation()?equals((Annotation)o1, (Annotation)o2):o1.equals(o2))):false);
   }

   private static boolean arrayMemberEquals(Class componentType, Object o1, Object o2) {
      return componentType.isAnnotation()?annotationArrayMemberEquals((Annotation[])((Annotation[])o1), (Annotation[])((Annotation[])o2)):(componentType.equals(Byte.TYPE)?Arrays.equals((byte[])((byte[])o1), (byte[])((byte[])o2)):(componentType.equals(Short.TYPE)?Arrays.equals((short[])((short[])o1), (short[])((short[])o2)):(componentType.equals(Integer.TYPE)?Arrays.equals((int[])((int[])o1), (int[])((int[])o2)):(componentType.equals(Character.TYPE)?Arrays.equals((char[])((char[])o1), (char[])((char[])o2)):(componentType.equals(Long.TYPE)?Arrays.equals((long[])((long[])o1), (long[])((long[])o2)):(componentType.equals(Float.TYPE)?Arrays.equals((float[])((float[])o1), (float[])((float[])o2)):(componentType.equals(Double.TYPE)?Arrays.equals((double[])((double[])o1), (double[])((double[])o2)):(componentType.equals(Boolean.TYPE)?Arrays.equals((boolean[])((boolean[])o1), (boolean[])((boolean[])o2)):Arrays.equals((Object[])((Object[])o1), (Object[])((Object[])o2))))))))));
   }

   private static boolean annotationArrayMemberEquals(Annotation[] a1, Annotation[] a2) {
      if(a1.length != a2.length) {
         return false;
      } else {
         for(int i = 0; i < a1.length; ++i) {
            if(!equals(a1[i], a2[i])) {
               return false;
            }
         }

         return true;
      }
   }

   private static int arrayMemberHash(Class componentType, Object o) {
      return componentType.equals(Byte.TYPE)?Arrays.hashCode((byte[])((byte[])o)):(componentType.equals(Short.TYPE)?Arrays.hashCode((short[])((short[])o)):(componentType.equals(Integer.TYPE)?Arrays.hashCode((int[])((int[])o)):(componentType.equals(Character.TYPE)?Arrays.hashCode((char[])((char[])o)):(componentType.equals(Long.TYPE)?Arrays.hashCode((long[])((long[])o)):(componentType.equals(Float.TYPE)?Arrays.hashCode((float[])((float[])o)):(componentType.equals(Double.TYPE)?Arrays.hashCode((double[])((double[])o)):(componentType.equals(Boolean.TYPE)?Arrays.hashCode((boolean[])((boolean[])o)):Arrays.hashCode((Object[])((Object[])o)))))))));
   }
}
