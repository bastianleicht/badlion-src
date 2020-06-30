package com.ibm.icu.text;

import java.math.BigDecimal;
import java.math.BigInteger;

final class DigitList {
   public static final int MAX_LONG_DIGITS = 19;
   public static final int DBL_DIG = 17;
   public int decimalAt = 0;
   public int count = 0;
   public byte[] digits = new byte[19];
   private static byte[] LONG_MIN_REP = new byte[19];

   private final void ensureCapacity(int digitCapacity, int digitsToCopy) {
      if(digitCapacity > this.digits.length) {
         byte[] newDigits = new byte[digitCapacity * 2];
         System.arraycopy(this.digits, 0, newDigits, 0, digitsToCopy);
         this.digits = newDigits;
      }

   }

   boolean isZero() {
      for(int i = 0; i < this.count; ++i) {
         if(this.digits[i] != 48) {
            return false;
         }
      }

      return true;
   }

   public void append(int digit) {
      this.ensureCapacity(this.count + 1, this.count);
      this.digits[this.count++] = (byte)digit;
   }

   public byte getDigitValue(int i) {
      return (byte)(this.digits[i] - 48);
   }

   public final double getDouble() {
      if(this.count == 0) {
         return 0.0D;
      } else {
         StringBuilder temp = new StringBuilder(this.count);
         temp.append('.');

         for(int i = 0; i < this.count; ++i) {
            temp.append((char)this.digits[i]);
         }

         temp.append('E');
         temp.append(Integer.toString(this.decimalAt));
         return Double.valueOf(temp.toString()).doubleValue();
      }
   }

   public final long getLong() {
      if(this.count == 0) {
         return 0L;
      } else if(this.isLongMIN_VALUE()) {
         return Long.MIN_VALUE;
      } else {
         StringBuilder temp = new StringBuilder(this.count);

         for(int i = 0; i < this.decimalAt; ++i) {
            temp.append(i < this.count?(char)this.digits[i]:'0');
         }

         return Long.parseLong(temp.toString());
      }
   }

   public BigInteger getBigInteger(boolean isPositive) {
      if(this.isZero()) {
         return BigInteger.valueOf(0L);
      } else {
         int len = this.decimalAt > this.count?this.decimalAt:this.count;
         if(!isPositive) {
            ++len;
         }

         char[] text = new char[len];
         int n = 0;
         if(!isPositive) {
            text[0] = 45;

            for(int i = 0; i < this.count; ++i) {
               text[i + 1] = (char)this.digits[i];
            }

            n = this.count + 1;
         } else {
            for(int i = 0; i < this.count; ++i) {
               text[i] = (char)this.digits[i];
            }

            n = this.count;
         }

         for(int i = n; i < text.length; ++i) {
            text[i] = 48;
         }

         return new BigInteger(new String(text));
      }
   }

   private String getStringRep(boolean isPositive) {
      if(this.isZero()) {
         return "0";
      } else {
         StringBuilder stringRep = new StringBuilder(this.count + 1);
         if(!isPositive) {
            stringRep.append('-');
         }

         int d = this.decimalAt;
         if(d < 0) {
            stringRep.append('.');

            while(d < 0) {
               stringRep.append('0');
               ++d;
            }

            d = -1;
         }

         for(int i = 0; i < this.count; ++i) {
            if(d == i) {
               stringRep.append('.');
            }

            stringRep.append((char)this.digits[i]);
         }

         while(d-- > this.count) {
            stringRep.append('0');
         }

         return stringRep.toString();
      }
   }

   public BigDecimal getBigDecimal(boolean isPositive) {
      if(this.isZero()) {
         return BigDecimal.valueOf(0L);
      } else {
         long scale = (long)this.count - (long)this.decimalAt;
         if(scale <= 0L) {
            return new BigDecimal(this.getStringRep(isPositive));
         } else {
            int numDigits = this.count;
            if(scale > 2147483647L) {
               long numShift = scale - 2147483647L;
               if(numShift >= (long)this.count) {
                  return new BigDecimal(0);
               }

               numDigits = (int)((long)numDigits - numShift);
            }

            StringBuilder significantDigits = new StringBuilder(numDigits + 1);
            if(!isPositive) {
               significantDigits.append('-');
            }

            for(int i = 0; i < numDigits; ++i) {
               significantDigits.append((char)this.digits[i]);
            }

            BigInteger unscaledVal = new BigInteger(significantDigits.toString());
            return new BigDecimal(unscaledVal, (int)scale);
         }
      }
   }

   public com.ibm.icu.math.BigDecimal getBigDecimalICU(boolean isPositive) {
      if(this.isZero()) {
         return com.ibm.icu.math.BigDecimal.valueOf(0L);
      } else {
         long scale = (long)this.count - (long)this.decimalAt;
         if(scale <= 0L) {
            return new com.ibm.icu.math.BigDecimal(this.getStringRep(isPositive));
         } else {
            int numDigits = this.count;
            if(scale > 2147483647L) {
               long numShift = scale - 2147483647L;
               if(numShift >= (long)this.count) {
                  return new com.ibm.icu.math.BigDecimal(0);
               }

               numDigits = (int)((long)numDigits - numShift);
            }

            StringBuilder significantDigits = new StringBuilder(numDigits + 1);
            if(!isPositive) {
               significantDigits.append('-');
            }

            for(int i = 0; i < numDigits; ++i) {
               significantDigits.append((char)this.digits[i]);
            }

            BigInteger unscaledVal = new BigInteger(significantDigits.toString());
            return new com.ibm.icu.math.BigDecimal(unscaledVal, (int)scale);
         }
      }
   }

   boolean isIntegral() {
      while(this.count > 0 && this.digits[this.count - 1] == 48) {
         --this.count;
      }

      return this.count == 0 || this.decimalAt >= this.count;
   }

   final void set(double source, int maximumDigits, boolean fixedPoint) {
      if(source == 0.0D) {
         source = 0.0D;
      }

      String rep = Double.toString(source);
      this.set((String)rep, 19);
      if(fixedPoint) {
         if(-this.decimalAt > maximumDigits) {
            this.count = 0;
            return;
         }

         if(-this.decimalAt == maximumDigits) {
            if(this.shouldRoundUp(0)) {
               this.count = 1;
               ++this.decimalAt;
               this.digits[0] = 49;
            } else {
               this.count = 0;
            }

            return;
         }
      }

      while(this.count > 1 && this.digits[this.count - 1] == 48) {
         --this.count;
      }

      this.round(fixedPoint?maximumDigits + this.decimalAt:(maximumDigits == 0?-1:maximumDigits));
   }

   private void set(String rep, int maxCount) {
      this.decimalAt = -1;
      this.count = 0;
      int exponent = 0;
      int leadingZerosAfterDecimal = 0;
      boolean nonZeroDigitSeen = false;
      int i = 0;
      if(rep.charAt(i) == 45) {
         ++i;
      }

      for(; i < rep.length(); ++i) {
         char c = rep.charAt(i);
         if(c == 46) {
            this.decimalAt = this.count;
         } else {
            if(c == 101 || c == 69) {
               ++i;
               if(rep.charAt(i) == 43) {
                  ++i;
               }

               exponent = Integer.valueOf(rep.substring(i)).intValue();
               break;
            }

            if(this.count < maxCount) {
               if(!nonZeroDigitSeen) {
                  nonZeroDigitSeen = c != 48;
                  if(!nonZeroDigitSeen && this.decimalAt != -1) {
                     ++leadingZerosAfterDecimal;
                  }
               }

               if(nonZeroDigitSeen) {
                  this.ensureCapacity(this.count + 1, this.count);
                  this.digits[this.count++] = (byte)c;
               }
            }
         }
      }

      if(this.decimalAt == -1) {
         this.decimalAt = this.count;
      }

      this.decimalAt += exponent - leadingZerosAfterDecimal;
   }

   private boolean shouldRoundUp(int maximumDigits) {
      if(maximumDigits < this.count) {
         if(this.digits[maximumDigits] > 53) {
            return true;
         }

         if(this.digits[maximumDigits] == 53) {
            for(int i = maximumDigits + 1; i < this.count; ++i) {
               if(this.digits[i] != 48) {
                  return true;
               }
            }

            return maximumDigits > 0 && this.digits[maximumDigits - 1] % 2 != 0;
         }
      }

      return false;
   }

   public final void round(int maximumDigits) {
      if(maximumDigits >= 0 && maximumDigits < this.count) {
         if(this.shouldRoundUp(maximumDigits)) {
            while(true) {
               --maximumDigits;
               if(maximumDigits < 0) {
                  this.digits[0] = 49;
                  ++this.decimalAt;
                  maximumDigits = 0;
                  break;
               }

               ++this.digits[maximumDigits];
               if(this.digits[maximumDigits] <= 57) {
                  break;
               }
            }

            ++maximumDigits;
         }

         this.count = maximumDigits;
      }

      while(this.count > 1 && this.digits[this.count - 1] == 48) {
         --this.count;
      }

   }

   public final void set(long source) {
      this.set(source, 0);
   }

   public final void set(long source, int maximumDigits) {
      if(source <= 0L) {
         if(source == Long.MIN_VALUE) {
            this.decimalAt = this.count = 19;
            System.arraycopy(LONG_MIN_REP, 0, this.digits, 0, this.count);
         } else {
            this.count = 0;
            this.decimalAt = 0;
         }
      } else {
         int left;
         for(left = 19; source > 0L; source /= 10L) {
            --left;
            this.digits[left] = (byte)((int)(48L + source % 10L));
         }

         this.decimalAt = 19 - left;

         int right;
         for(right = 18; this.digits[right] == 48; --right) {
            ;
         }

         this.count = right - left + 1;
         System.arraycopy(this.digits, left, this.digits, 0, this.count);
      }

      if(maximumDigits > 0) {
         this.round(maximumDigits);
      }

   }

   public final void set(BigInteger source, int maximumDigits) {
      String stringDigits = source.toString();

      for(this.count = this.decimalAt = stringDigits.length(); this.count > 1 && stringDigits.charAt(this.count - 1) == 48; --this.count) {
         ;
      }

      int offset = 0;
      if(stringDigits.charAt(0) == 45) {
         ++offset;
         --this.count;
         --this.decimalAt;
      }

      this.ensureCapacity(this.count, 0);

      for(int i = 0; i < this.count; ++i) {
         this.digits[i] = (byte)stringDigits.charAt(i + offset);
      }

      if(maximumDigits > 0) {
         this.round(maximumDigits);
      }

   }

   private void setBigDecimalDigits(String stringDigits, int maximumDigits, boolean fixedPoint) {
      this.set(stringDigits, stringDigits.length());
      this.round(fixedPoint?maximumDigits + this.decimalAt:(maximumDigits == 0?-1:maximumDigits));
   }

   public final void set(BigDecimal source, int maximumDigits, boolean fixedPoint) {
      this.setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
   }

   public final void set(com.ibm.icu.math.BigDecimal source, int maximumDigits, boolean fixedPoint) {
      this.setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
   }

   private boolean isLongMIN_VALUE() {
      if(this.decimalAt == this.count && this.count == 19) {
         for(int i = 0; i < this.count; ++i) {
            if(this.digits[i] != LONG_MIN_REP[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof DigitList)) {
         return false;
      } else {
         DigitList other = (DigitList)obj;
         if(this.count == other.count && this.decimalAt == other.decimalAt) {
            for(int i = 0; i < this.count; ++i) {
               if(this.digits[i] != other.digits[i]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      int hashcode = this.decimalAt;

      for(int i = 0; i < this.count; ++i) {
         hashcode = hashcode * 37 + this.digits[i];
      }

      return hashcode;
   }

   public String toString() {
      if(this.isZero()) {
         return "0";
      } else {
         StringBuilder buf = new StringBuilder("0.");

         for(int i = 0; i < this.count; ++i) {
            buf.append((char)this.digits[i]);
         }

         buf.append("x10^");
         buf.append(this.decimalAt);
         return buf.toString();
      }
   }

   static {
      String s = Long.toString(Long.MIN_VALUE);

      for(int i = 0; i < 19; ++i) {
         LONG_MIN_REP[i] = (byte)s.charAt(i + 1);
      }

   }
}
