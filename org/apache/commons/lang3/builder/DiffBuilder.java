package org.apache.commons.lang3.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiffBuilder implements Builder {
   private final List diffs;
   private final boolean objectsTriviallyEqual;
   private final Object left;
   private final Object right;
   private final ToStringStyle style;

   public DiffBuilder(Object lhs, Object rhs, ToStringStyle style) {
      if(lhs == null) {
         throw new IllegalArgumentException("lhs cannot be null");
      } else if(rhs == null) {
         throw new IllegalArgumentException("rhs cannot be null");
      } else {
         this.diffs = new ArrayList();
         this.left = lhs;
         this.right = rhs;
         this.style = style;
         this.objectsTriviallyEqual = lhs == rhs || lhs.equals(rhs);
      }
   }

   public DiffBuilder append(final String fieldName, final boolean lhs, final boolean rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Boolean getLeft() {
                  return Boolean.valueOf(lhs);
               }

               public Boolean getRight() {
                  return Boolean.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final boolean[] lhs, final boolean[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Boolean[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Boolean[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final byte lhs, final byte rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Byte getLeft() {
                  return Byte.valueOf(lhs);
               }

               public Byte getRight() {
                  return Byte.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final byte[] lhs, final byte[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Byte[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Byte[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final char lhs, final char rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Character getLeft() {
                  return Character.valueOf(lhs);
               }

               public Character getRight() {
                  return Character.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final char[] lhs, final char[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Character[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Character[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final double lhs, final double rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(Double.doubleToLongBits(lhs) != Double.doubleToLongBits(rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Double getLeft() {
                  return Double.valueOf(lhs);
               }

               public Double getRight() {
                  return Double.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final double[] lhs, final double[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Double[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Double[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final float lhs, final float rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(Float.floatToIntBits(lhs) != Float.floatToIntBits(rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Float getLeft() {
                  return Float.valueOf(lhs);
               }

               public Float getRight() {
                  return Float.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final float[] lhs, final float[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Float[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Float[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final int lhs, final int rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Integer getLeft() {
                  return Integer.valueOf(lhs);
               }

               public Integer getRight() {
                  return Integer.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final int[] lhs, final int[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Integer[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Integer[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final long lhs, final long rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Long getLeft() {
                  return Long.valueOf(lhs);
               }

               public Long getRight() {
                  return Long.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final long[] lhs, final long[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Long[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Long[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final short lhs, final short rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(lhs != rhs) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Short getLeft() {
                  return Short.valueOf(lhs);
               }

               public Short getRight() {
                  return Short.valueOf(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final short[] lhs, final short[] rhs) {
      if(fieldName == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Short[] getLeft() {
                  return ArrayUtils.toObject(lhs);
               }

               public Short[] getRight() {
                  return ArrayUtils.toObject(rhs);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(final String fieldName, final Object lhs, final Object rhs) {
      if(this.objectsTriviallyEqual) {
         return this;
      } else if(lhs == rhs) {
         return this;
      } else {
         Object objectToTest;
         if(lhs != null) {
            objectToTest = lhs;
         } else {
            objectToTest = rhs;
         }

         if(objectToTest.getClass().isArray()) {
            return objectToTest instanceof boolean[]?this.append(fieldName, (boolean[])((boolean[])lhs), (boolean[])((boolean[])rhs)):(objectToTest instanceof byte[]?this.append(fieldName, (byte[])((byte[])lhs), (byte[])((byte[])rhs)):(objectToTest instanceof char[]?this.append(fieldName, (char[])((char[])lhs), (char[])((char[])rhs)):(objectToTest instanceof double[]?this.append(fieldName, (double[])((double[])lhs), (double[])((double[])rhs)):(objectToTest instanceof float[]?this.append(fieldName, (float[])((float[])lhs), (float[])((float[])rhs)):(objectToTest instanceof int[]?this.append(fieldName, (int[])((int[])lhs), (int[])((int[])rhs)):(objectToTest instanceof long[]?this.append(fieldName, (long[])((long[])lhs), (long[])((long[])rhs)):(objectToTest instanceof short[]?this.append(fieldName, (short[])((short[])lhs), (short[])((short[])rhs)):this.append(fieldName, (Object[])((Object[])lhs), (Object[])((Object[])rhs)))))))));
         } else {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Object getLeft() {
                  return lhs;
               }

               public Object getRight() {
                  return rhs;
               }
            });
            return this;
         }
      }
   }

   public DiffBuilder append(final String fieldName, final Object[] lhs, final Object[] rhs) {
      if(this.objectsTriviallyEqual) {
         return this;
      } else {
         if(!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff(fieldName) {
               private static final long serialVersionUID = 1L;

               public Object[] getLeft() {
                  return lhs;
               }

               public Object[] getRight() {
                  return rhs;
               }
            });
         }

         return this;
      }
   }

   public DiffResult build() {
      return new DiffResult(this.left, this.right, this.diffs, this.style);
   }
}
