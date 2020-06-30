package org.apache.commons.lang3.math;

public class IEEE754rUtils {
   public static double min(double[] array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array cannot be empty.");
      } else {
         double min = array[0];

         for(int i = 1; i < array.length; ++i) {
            min = min(array[i], min);
         }

         return min;
      }
   }

   public static float min(float[] array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array cannot be empty.");
      } else {
         float min = array[0];

         for(int i = 1; i < array.length; ++i) {
            min = min(array[i], min);
         }

         return min;
      }
   }

   public static double min(double a, double b, double c) {
      return min(min(a, b), c);
   }

   public static double min(double a, double b) {
      return Double.isNaN(a)?b:(Double.isNaN(b)?a:Math.min(a, b));
   }

   public static float min(float a, float b, float c) {
      return min(min(a, b), c);
   }

   public static float min(float a, float b) {
      return Float.isNaN(a)?b:(Float.isNaN(b)?a:Math.min(a, b));
   }

   public static double max(double[] array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array cannot be empty.");
      } else {
         double max = array[0];

         for(int j = 1; j < array.length; ++j) {
            max = max(array[j], max);
         }

         return max;
      }
   }

   public static float max(float[] array) {
      if(array == null) {
         throw new IllegalArgumentException("The Array must not be null");
      } else if(array.length == 0) {
         throw new IllegalArgumentException("Array cannot be empty.");
      } else {
         float max = array[0];

         for(int j = 1; j < array.length; ++j) {
            max = max(array[j], max);
         }

         return max;
      }
   }

   public static double max(double a, double b, double c) {
      return max(max(a, b), c);
   }

   public static double max(double a, double b) {
      return Double.isNaN(a)?b:(Double.isNaN(b)?a:Math.max(a, b));
   }

   public static float max(float a, float b, float c) {
      return max(max(a, b), c);
   }

   public static float max(float a, float b) {
      return Float.isNaN(a)?b:(Float.isNaN(b)?a:Math.max(a, b));
   }
}
