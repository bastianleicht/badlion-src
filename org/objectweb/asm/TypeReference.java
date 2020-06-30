package org.objectweb.asm;

public class TypeReference {
   public static final int CLASS_TYPE_PARAMETER = 0;
   public static final int METHOD_TYPE_PARAMETER = 1;
   public static final int CLASS_EXTENDS = 16;
   public static final int CLASS_TYPE_PARAMETER_BOUND = 17;
   public static final int METHOD_TYPE_PARAMETER_BOUND = 18;
   public static final int FIELD = 19;
   public static final int METHOD_RETURN = 20;
   public static final int METHOD_RECEIVER = 21;
   public static final int METHOD_FORMAL_PARAMETER = 22;
   public static final int THROWS = 23;
   public static final int LOCAL_VARIABLE = 64;
   public static final int RESOURCE_VARIABLE = 65;
   public static final int EXCEPTION_PARAMETER = 66;
   public static final int INSTANCEOF = 67;
   public static final int NEW = 68;
   public static final int CONSTRUCTOR_REFERENCE = 69;
   public static final int METHOD_REFERENCE = 70;
   public static final int CAST = 71;
   public static final int CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
   public static final int METHOD_INVOCATION_TYPE_ARGUMENT = 73;
   public static final int CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
   public static final int METHOD_REFERENCE_TYPE_ARGUMENT = 75;
   private int a;

   public TypeReference(int var1) {
      this.a = var1;
   }

   public static TypeReference newTypeReference(int var0) {
      return new TypeReference(var0 << 24);
   }

   public static TypeReference newTypeParameterReference(int var0, int var1) {
      return new TypeReference(var0 << 24 | var1 << 16);
   }

   public static TypeReference newTypeParameterBoundReference(int var0, int var1, int var2) {
      return new TypeReference(var0 << 24 | var1 << 16 | var2 << 8);
   }

   public static TypeReference newSuperTypeReference(int var0) {
      var0 = var0 & '\uffff';
      return new TypeReference(268435456 | var0 << 8);
   }

   public static TypeReference newFormalParameterReference(int var0) {
      return new TypeReference(369098752 | var0 << 16);
   }

   public static TypeReference newExceptionReference(int var0) {
      return new TypeReference(385875968 | var0 << 8);
   }

   public static TypeReference newTryCatchReference(int var0) {
      return new TypeReference(1107296256 | var0 << 8);
   }

   public static TypeReference newTypeArgumentReference(int var0, int var1) {
      return new TypeReference(var0 << 24 | var1);
   }

   public int getSort() {
      return this.a >>> 24;
   }

   public int getTypeParameterIndex() {
      return (this.a & 16711680) >> 16;
   }

   public int getTypeParameterBoundIndex() {
      return (this.a & '\uff00') >> 8;
   }

   public int getSuperTypeIndex() {
      return (short)((this.a & 16776960) >> 8);
   }

   public int getFormalParameterIndex() {
      return (this.a & 16711680) >> 16;
   }

   public int getExceptionIndex() {
      return (this.a & 16776960) >> 8;
   }

   public int getTryCatchBlockIndex() {
      return (this.a & 16776960) >> 8;
   }

   public int getTypeArgumentIndex() {
      return this.a & 255;
   }

   public int getValue() {
      return this.a;
   }
}
