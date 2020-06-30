package com.sun.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;

public abstract class Union extends Structure {
   private Structure.StructField activeField;
   Structure.StructField biggestField;

   protected Union() {
   }

   protected Union(Pointer p) {
      super(p);
   }

   protected Union(Pointer p, int alignType) {
      super(p, alignType);
   }

   protected Union(TypeMapper mapper) {
      super(mapper);
   }

   protected Union(Pointer p, int alignType, TypeMapper mapper) {
      super(p, alignType, mapper);
   }

   public void setType(Class type) {
      this.ensureAllocated();

      for(Structure.StructField f : this.fields().values()) {
         if(f.type == type) {
            this.activeField = f;
            return;
         }
      }

      throw new IllegalArgumentException("No field of type " + type + " in " + this);
   }

   public void setType(String fieldName) {
      this.ensureAllocated();
      Structure.StructField f = (Structure.StructField)this.fields().get(fieldName);
      if(f != null) {
         this.activeField = f;
      } else {
         throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
      }
   }

   public Object readField(String fieldName) {
      this.ensureAllocated();
      this.setType(fieldName);
      return super.readField(fieldName);
   }

   public void writeField(String fieldName) {
      this.ensureAllocated();
      this.setType(fieldName);
      super.writeField(fieldName);
   }

   public void writeField(String fieldName, Object value) {
      this.ensureAllocated();
      this.setType(fieldName);
      super.writeField(fieldName, value);
   }

   public Object getTypedValue(Class type) {
      this.ensureAllocated();

      for(Structure.StructField f : this.fields().values()) {
         if(f.type == type) {
            this.activeField = f;
            this.read();
            return this.getField(this.activeField);
         }
      }

      throw new IllegalArgumentException("No field of type " + type + " in " + this);
   }

   public Object setTypedValue(Object object) {
      Structure.StructField f = this.findField(object.getClass());
      if(f != null) {
         this.activeField = f;
         this.setField(f, object);
         return this;
      } else {
         throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
      }
   }

   private Structure.StructField findField(Class type) {
      this.ensureAllocated();

      for(Structure.StructField f : this.fields().values()) {
         if(f.type.isAssignableFrom(type)) {
            return f;
         }
      }

      return null;
   }

   void writeField(Structure.StructField field) {
      if(field == this.activeField) {
         super.writeField(field);
      }

   }

   Object readField(Structure.StructField field) {
      return field != this.activeField && (Structure.class.isAssignableFrom(field.type) || String.class.isAssignableFrom(field.type) || WString.class.isAssignableFrom(field.type))?null:super.readField(field);
   }

   int calculateSize(boolean force, boolean avoidFFIType) {
      int size = super.calculateSize(force, avoidFFIType);
      if(size != -1) {
         int fsize = 0;

         for(Structure.StructField f : this.fields().values()) {
            f.offset = 0;
            if(f.size > fsize || f.size == fsize && Structure.class.isAssignableFrom(f.type)) {
               fsize = f.size;
               this.biggestField = f;
            }
         }

         size = this.calculateAlignedSize(fsize);
         if(size > 0 && this instanceof Structure.ByValue && !avoidFFIType) {
            this.getTypeInfo();
         }
      }

      return size;
   }

   protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
      return super.getNativeAlignment(type, value, true);
   }

   Pointer getTypeInfo() {
      return this.biggestField == null?null:super.getTypeInfo();
   }
}
