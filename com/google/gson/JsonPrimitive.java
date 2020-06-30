package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
   private static final Class[] PRIMITIVE_TYPES = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
   private Object value;

   public JsonPrimitive(Boolean bool) {
      this.setValue(bool);
   }

   public JsonPrimitive(Number number) {
      this.setValue(number);
   }

   public JsonPrimitive(String string) {
      this.setValue(string);
   }

   public JsonPrimitive(Character c) {
      this.setValue(c);
   }

   JsonPrimitive(Object primitive) {
      this.setValue(primitive);
   }

   JsonPrimitive deepCopy() {
      return this;
   }

   void setValue(Object primitive) {
      if(primitive instanceof Character) {
         char c = ((Character)primitive).charValue();
         this.value = String.valueOf(c);
      } else {
         $Gson$Preconditions.checkArgument(primitive instanceof Number || isPrimitiveOrString(primitive));
         this.value = primitive;
      }

   }

   public boolean isBoolean() {
      return this.value instanceof Boolean;
   }

   Boolean getAsBooleanWrapper() {
      return (Boolean)this.value;
   }

   public boolean getAsBoolean() {
      return this.isBoolean()?this.getAsBooleanWrapper().booleanValue():Boolean.parseBoolean(this.getAsString());
   }

   public boolean isNumber() {
      return this.value instanceof Number;
   }

   public Number getAsNumber() {
      return (Number)(this.value instanceof String?new LazilyParsedNumber((String)this.value):(Number)this.value);
   }

   public boolean isString() {
      return this.value instanceof String;
   }

   public String getAsString() {
      return this.isNumber()?this.getAsNumber().toString():(this.isBoolean()?this.getAsBooleanWrapper().toString():(String)this.value);
   }

   public double getAsDouble() {
      return this.isNumber()?this.getAsNumber().doubleValue():Double.parseDouble(this.getAsString());
   }

   public BigDecimal getAsBigDecimal() {
      return this.value instanceof BigDecimal?(BigDecimal)this.value:new BigDecimal(this.value.toString());
   }

   public BigInteger getAsBigInteger() {
      return this.value instanceof BigInteger?(BigInteger)this.value:new BigInteger(this.value.toString());
   }

   public float getAsFloat() {
      return this.isNumber()?this.getAsNumber().floatValue():Float.parseFloat(this.getAsString());
   }

   public long getAsLong() {
      return this.isNumber()?this.getAsNumber().longValue():Long.parseLong(this.getAsString());
   }

   public short getAsShort() {
      return this.isNumber()?this.getAsNumber().shortValue():Short.parseShort(this.getAsString());
   }

   public int getAsInt() {
      return this.isNumber()?this.getAsNumber().intValue():Integer.parseInt(this.getAsString());
   }

   public byte getAsByte() {
      return this.isNumber()?this.getAsNumber().byteValue():Byte.parseByte(this.getAsString());
   }

   public char getAsCharacter() {
      return this.getAsString().charAt(0);
   }

   private static boolean isPrimitiveOrString(Object target) {
      if(target instanceof String) {
         return true;
      } else {
         Class<?> classOfPrimitive = target.getClass();

         for(Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if(standardPrimitive.isAssignableFrom(classOfPrimitive)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      if(this.value == null) {
         return 31;
      } else if(isIntegral(this)) {
         long value = this.getAsNumber().longValue();
         return (int)(value ^ value >>> 32);
      } else if(this.value instanceof Number) {
         long value = Double.doubleToLongBits(this.getAsNumber().doubleValue());
         return (int)(value ^ value >>> 32);
      } else {
         return this.value.hashCode();
      }
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(obj != null && this.getClass() == obj.getClass()) {
         JsonPrimitive other = (JsonPrimitive)obj;
         if(this.value == null) {
            return other.value == null;
         } else if(isIntegral(this) && isIntegral(other)) {
            return this.getAsNumber().longValue() == other.getAsNumber().longValue();
         } else if(this.value instanceof Number && other.value instanceof Number) {
            double a = this.getAsNumber().doubleValue();
            double b = other.getAsNumber().doubleValue();
            return a == b || Double.isNaN(a) && Double.isNaN(b);
         } else {
            return this.value.equals(other.value);
         }
      } else {
         return false;
      }
   }

   private static boolean isIntegral(JsonPrimitive primitive) {
      if(!(primitive.value instanceof Number)) {
         return false;
      } else {
         Number number = (Number)primitive.value;
         return number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte;
      }
   }
}
