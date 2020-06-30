package com.sun.jna;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.NativeString;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.StructureReadContext;
import com.sun.jna.StructureWriteContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.Union;
import com.sun.jna.WString;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.Adler32;

public abstract class Structure {
   private static final boolean REVERSE_FIELDS;
   private static final boolean REQUIRES_FIELD_ORDER;
   static final boolean isPPC;
   static final boolean isSPARC;
   static final boolean isARM;
   public static final int ALIGN_DEFAULT = 0;
   public static final int ALIGN_NONE = 1;
   public static final int ALIGN_GNUC = 2;
   public static final int ALIGN_MSVC = 3;
   static final int MAX_GNUC_ALIGNMENT;
   protected static final int CALCULATE_SIZE = -1;
   static final Map layoutInfo;
   private Pointer memory;
   private int size;
   private int alignType;
   private int structAlignment;
   private Map structFields;
   private final Map nativeStrings;
   private TypeMapper typeMapper;
   private long typeInfo;
   private List fieldOrder;
   private boolean autoRead;
   private boolean autoWrite;
   private Structure[] array;
   private static final ThreadLocal reads;
   private static final ThreadLocal busy;

   protected Structure() {
      this((Pointer)null);
   }

   protected Structure(TypeMapper mapper) {
      this((Pointer)null, 0, mapper);
   }

   protected Structure(Pointer p) {
      this(p, 0);
   }

   protected Structure(Pointer p, int alignType) {
      this(p, alignType, (TypeMapper)null);
   }

   protected Structure(Pointer p, int alignType, TypeMapper mapper) {
      this.size = -1;
      this.nativeStrings = new HashMap();
      this.autoRead = true;
      this.autoWrite = true;
      this.setAlignType(alignType);
      this.setTypeMapper(mapper);
      if(p != null) {
         this.useMemory(p);
      } else {
         this.allocateMemory(-1);
      }

   }

   Map fields() {
      return this.structFields;
   }

   TypeMapper getTypeMapper() {
      return this.typeMapper;
   }

   protected void setTypeMapper(TypeMapper mapper) {
      if(mapper == null) {
         Class declaring = this.getClass().getDeclaringClass();
         if(declaring != null) {
            mapper = Native.getTypeMapper(declaring);
         }
      }

      this.typeMapper = mapper;
      this.size = -1;
      if(this.memory instanceof Structure.AutoAllocated) {
         this.memory = null;
      }

   }

   protected void setAlignType(int alignType) {
      if(alignType == 0) {
         Class declaring = this.getClass().getDeclaringClass();
         if(declaring != null) {
            alignType = Native.getStructureAlignment(declaring);
         }

         if(alignType == 0) {
            if(Platform.isWindows()) {
               alignType = 3;
            } else {
               alignType = 2;
            }
         }
      }

      this.alignType = alignType;
      this.size = -1;
      if(this.memory instanceof Structure.AutoAllocated) {
         this.memory = null;
      }

   }

   protected Memory autoAllocate(int size) {
      return new Structure.AutoAllocated(size);
   }

   protected void useMemory(Pointer m) {
      this.useMemory(m, 0);
   }

   protected void useMemory(Pointer m, int offset) {
      try {
         this.memory = m.share((long)offset);
         if(this.size == -1) {
            this.size = this.calculateSize(false);
         }

         if(this.size != -1) {
            this.memory = m.share((long)offset, (long)this.size);
         }

         this.array = null;
      } catch (IndexOutOfBoundsException var4) {
         throw new IllegalArgumentException("Structure exceeds provided memory bounds");
      }
   }

   protected void ensureAllocated() {
      this.ensureAllocated(false);
   }

   private void ensureAllocated(boolean avoidFFIType) {
      if(this.memory == null) {
         this.allocateMemory(avoidFFIType);
      } else if(this.size == -1) {
         this.size = this.calculateSize(true, avoidFFIType);
      }

   }

   protected void allocateMemory() {
      this.allocateMemory(false);
   }

   private void allocateMemory(boolean avoidFFIType) {
      this.allocateMemory(this.calculateSize(true, avoidFFIType));
   }

   protected void allocateMemory(int size) {
      if(size == -1) {
         size = this.calculateSize(false);
      } else if(size <= 0) {
         throw new IllegalArgumentException("Structure size must be greater than zero: " + size);
      }

      if(size != -1) {
         if(this.memory == null || this.memory instanceof Structure.AutoAllocated) {
            this.memory = this.autoAllocate(size);
         }

         this.size = size;
      }

   }

   public int size() {
      this.ensureAllocated();
      if(this.size == -1) {
         this.size = this.calculateSize(true);
      }

      return this.size;
   }

   public void clear() {
      this.memory.clear((long)this.size());
   }

   public Pointer getPointer() {
      this.ensureAllocated();
      return this.memory;
   }

   static Set busy() {
      return (Set)busy.get();
   }

   static Map reading() {
      return (Map)reads.get();
   }

   public void read() {
      this.ensureAllocated();
      if(!busy().contains(this)) {
         busy().add(this);
         if(this instanceof Structure.ByReference) {
            reading().put(this.getPointer(), this);
         }

         try {
            Iterator i = this.fields().values().iterator();

            while(i.hasNext()) {
               this.readField((Structure.StructField)i.next());
            }
         } finally {
            busy().remove(this);
            if(reading().get(this.getPointer()) == this) {
               reading().remove(this.getPointer());
            }

         }

      }
   }

   protected int fieldOffset(String name) {
      this.ensureAllocated();
      Structure.StructField f = (Structure.StructField)this.fields().get(name);
      if(f == null) {
         throw new IllegalArgumentException("No such field: " + name);
      } else {
         return f.offset;
      }
   }

   public Object readField(String name) {
      this.ensureAllocated();
      Structure.StructField f = (Structure.StructField)this.fields().get(name);
      if(f == null) {
         throw new IllegalArgumentException("No such field: " + name);
      } else {
         return this.readField(f);
      }
   }

   Object getField(Structure.StructField structField) {
      try {
         return structField.field.get(this);
      } catch (Exception var3) {
         throw new Error("Exception reading field \'" + structField.name + "\' in " + this.getClass() + ": " + var3);
      }
   }

   void setField(Structure.StructField structField, Object value) {
      this.setField(structField, value, false);
   }

   void setField(Structure.StructField structField, Object value, boolean overrideFinal) {
      try {
         structField.field.set(this, value);
      } catch (IllegalAccessException var6) {
         int modifiers = structField.field.getModifiers();
         if(Modifier.isFinal(modifiers)) {
            if(overrideFinal) {
               throw new UnsupportedOperationException("This VM does not support Structures with final fields (field \'" + structField.name + "\' within " + this.getClass() + ")");
            } else {
               throw new UnsupportedOperationException("Attempt to write to read-only field \'" + structField.name + "\' within " + this.getClass());
            }
         } else {
            throw new Error("Unexpectedly unable to write to field \'" + structField.name + "\' within " + this.getClass() + ": " + var6);
         }
      }
   }

   static Structure updateStructureByReference(Class type, Structure s, Pointer address) {
      if(address == null) {
         s = null;
      } else {
         if(s == null || !address.equals(s.getPointer())) {
            Structure s1 = (Structure)reading().get(address);
            if(s1 != null && type.equals(s1.getClass())) {
               s = s1;
            } else {
               s = newInstance(type);
               s.useMemory(address);
            }
         }

         s.autoRead();
      }

      return s;
   }

   Object readField(Structure.StructField structField) {
      int offset = structField.offset;
      Class fieldType = structField.type;
      FromNativeConverter readConverter = structField.readConverter;
      if(readConverter != null) {
         fieldType = readConverter.nativeType();
      }

      Object currentValue = !Structure.class.isAssignableFrom(fieldType) && !Callback.class.isAssignableFrom(fieldType) && (!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(fieldType)) && !Pointer.class.isAssignableFrom(fieldType) && !NativeMapped.class.isAssignableFrom(fieldType) && !fieldType.isArray()?null:this.getField(structField);
      Object result = this.memory.getValue((long)offset, fieldType, currentValue);
      if(readConverter != null) {
         result = readConverter.fromNative(result, structField.context);
      }

      this.setField(structField, result, true);
      return result;
   }

   public void write() {
      this.ensureAllocated();
      if(this instanceof Structure.ByValue) {
         this.getTypeInfo();
      }

      if(!busy().contains(this)) {
         busy().add(this);

         try {
            for(Structure.StructField sf : this.fields().values()) {
               if(!sf.isVolatile) {
                  this.writeField(sf);
               }
            }
         } finally {
            busy().remove(this);
         }

      }
   }

   public void writeField(String name) {
      this.ensureAllocated();
      Structure.StructField f = (Structure.StructField)this.fields().get(name);
      if(f == null) {
         throw new IllegalArgumentException("No such field: " + name);
      } else {
         this.writeField(f);
      }
   }

   public void writeField(String name, Object value) {
      this.ensureAllocated();
      Structure.StructField f = (Structure.StructField)this.fields().get(name);
      if(f == null) {
         throw new IllegalArgumentException("No such field: " + name);
      } else {
         this.setField(f, value);
         this.writeField(f);
      }
   }

   void writeField(Structure.StructField structField) {
      if(!structField.isReadOnly) {
         int offset = structField.offset;
         Object value = this.getField(structField);
         Class fieldType = structField.type;
         ToNativeConverter converter = structField.writeConverter;
         if(converter != null) {
            value = converter.toNative(value, new StructureWriteContext(this, structField.field));
            fieldType = converter.nativeType();
         }

         if(String.class == fieldType || WString.class == fieldType) {
            boolean wide = fieldType == WString.class;
            if(value != null) {
               NativeString nativeString = new NativeString(value.toString(), wide);
               this.nativeStrings.put(structField.name, nativeString);
               value = nativeString.getPointer();
            } else {
               value = null;
               this.nativeStrings.remove(structField.name);
            }
         }

         try {
            this.memory.setValue((long)offset, value, fieldType);
         } catch (IllegalArgumentException var8) {
            String msg = "Structure field \"" + structField.name + "\" was declared as " + structField.type + (structField.type == fieldType?"":" (native type " + fieldType + ")") + ", which is not supported within a Structure";
            throw new IllegalArgumentException(msg);
         }
      }
   }

   private boolean hasFieldOrder() {
      synchronized(this) {
         return this.fieldOrder != null;
      }
   }

   protected List getFieldOrder() {
      synchronized(this) {
         if(this.fieldOrder == null) {
            this.fieldOrder = new ArrayList();
         }

         return this.fieldOrder;
      }
   }

   protected void setFieldOrder(String[] fields) {
      this.getFieldOrder().addAll(Arrays.asList(fields));
      this.size = -1;
      if(this.memory instanceof Structure.AutoAllocated) {
         this.memory = null;
      }

   }

   protected void sortFields(List fields, List names) {
      for(int i = 0; i < names.size(); ++i) {
         String name = (String)names.get(i);

         for(int f = 0; f < fields.size(); ++f) {
            Field field = (Field)fields.get(f);
            if(name.equals(field.getName())) {
               Collections.swap(fields, i, f);
               break;
            }
         }
      }

   }

   protected List getFields(boolean force) {
      List flist = new ArrayList();

      for(Class cls = this.getClass(); !cls.equals(Structure.class); cls = cls.getSuperclass()) {
         List classFields = new ArrayList();
         Field[] fields = cls.getDeclaredFields();

         for(int i = 0; i < fields.length; ++i) {
            int modifiers = fields[i].getModifiers();
            if(!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
               classFields.add(fields[i]);
            }
         }

         if(REVERSE_FIELDS) {
            Collections.reverse(classFields);
         }

         flist.addAll(0, classFields);
      }

      if(REQUIRES_FIELD_ORDER || this.hasFieldOrder()) {
         List fieldOrder = this.getFieldOrder();
         if(fieldOrder.size() < flist.size()) {
            if(force) {
               throw new Error("This VM does not store fields in a predictable order; you must use Structure.setFieldOrder to explicitly indicate the field order: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
            }

            return null;
         }

         this.sortFields(flist, fieldOrder);
      }

      return flist;
   }

   private synchronized boolean fieldOrderMatch(List fieldOrder) {
      return this.fieldOrder == fieldOrder || this.fieldOrder != null && this.fieldOrder.equals(fieldOrder);
   }

   private int calculateSize(boolean force) {
      return this.calculateSize(force, false);
   }

   int calculateSize(boolean force, boolean avoidFFIType) {
      boolean needsInit = true;
      Structure.LayoutInfo info;
      synchronized(layoutInfo) {
         info = (Structure.LayoutInfo)layoutInfo.get(this.getClass());
      }

      if(info == null || this.alignType != info.alignType || this.typeMapper != info.typeMapper || !this.fieldOrderMatch(info.fieldOrder)) {
         info = this.deriveLayout(force, avoidFFIType);
         needsInit = false;
      }

      if(info != null) {
         this.structAlignment = info.alignment;
         this.structFields = info.fields;
         info.alignType = this.alignType;
         info.typeMapper = this.typeMapper;
         info.fieldOrder = this.fieldOrder;
         if(!info.variable) {
            synchronized(layoutInfo) {
               layoutInfo.put(this.getClass(), info);
            }
         }

         if(needsInit) {
            this.initializeFields();
         }

         return info.size;
      } else {
         return -1;
      }
   }

   private Structure.LayoutInfo deriveLayout(boolean force, boolean avoidFFIType) {
      Structure.LayoutInfo info = new Structure.LayoutInfo();
      int calculatedSize = 0;
      List fields = this.getFields(force);
      if(fields == null) {
         return null;
      } else {
         boolean firstField = true;

         for(Field field : fields) {
            int modifiers = field.getModifiers();
            Class type = field.getType();
            if(type.isArray()) {
               info.variable = true;
            }

            Structure.StructField structField = new Structure.StructField();
            structField.isVolatile = Modifier.isVolatile(modifiers);
            structField.isReadOnly = Modifier.isFinal(modifiers);
            if(structField.isReadOnly) {
               if(!Platform.RO_FIELDS) {
                  throw new IllegalArgumentException("This VM does not support read-only fields (field \'" + field.getName() + "\' within " + this.getClass() + ")");
               }

               field.setAccessible(true);
            }

            structField.field = field;
            structField.name = field.getName();
            structField.type = type;
            if(Callback.class.isAssignableFrom(type) && !type.isInterface()) {
               throw new IllegalArgumentException("Structure Callback field \'" + field.getName() + "\' must be an interface");
            }

            if(type.isArray() && Structure.class.equals(type.getComponentType())) {
               String msg = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
               throw new IllegalArgumentException(msg);
            }

            int fieldAlignment = 1;
            if(Modifier.isPublic(field.getModifiers())) {
               Object value = this.getField(structField);
               if(value == null && type.isArray()) {
                  if(force) {
                     throw new IllegalStateException("Array fields must be initialized");
                  }

                  return null;
               }

               Class nativeType = type;
               if(NativeMapped.class.isAssignableFrom(type)) {
                  NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
                  nativeType = tc.nativeType();
                  structField.writeConverter = tc;
                  structField.readConverter = tc;
                  structField.context = new StructureReadContext(this, field);
               } else if(this.typeMapper != null) {
                  ToNativeConverter writeConverter = this.typeMapper.getToNativeConverter(type);
                  FromNativeConverter readConverter = this.typeMapper.getFromNativeConverter(type);
                  if(writeConverter != null && readConverter != null) {
                     value = writeConverter.toNative(value, new StructureWriteContext(this, structField.field));
                     nativeType = value != null?value.getClass():Pointer.class;
                     structField.writeConverter = writeConverter;
                     structField.readConverter = readConverter;
                     structField.context = new StructureReadContext(this, field);
                  } else if(writeConverter != null || readConverter != null) {
                     String msg = "Structures require bidirectional type conversion for " + type;
                     throw new IllegalArgumentException(msg);
                  }
               }

               if(value == null) {
                  value = this.initializeField(structField, type);
               }

               try {
                  structField.size = this.getNativeSize(nativeType, value);
                  fieldAlignment = this.getNativeAlignment(nativeType, value, firstField);
               } catch (IllegalArgumentException var18) {
                  if(!force && this.typeMapper == null) {
                     return null;
                  }

                  String msg = "Invalid Structure field in " + this.getClass() + ", field name \'" + structField.name + "\', " + structField.type + ": " + var18.getMessage();
                  throw new IllegalArgumentException(msg);
               }

               info.alignment = Math.max(info.alignment, fieldAlignment);
               if(calculatedSize % fieldAlignment != 0) {
                  calculatedSize += fieldAlignment - calculatedSize % fieldAlignment;
               }

               structField.offset = calculatedSize;
               calculatedSize += structField.size;
               info.fields.put(structField.name, structField);
            }

            firstField = false;
         }

         if(calculatedSize > 0) {
            int size = this.calculateAlignedSize(calculatedSize, info.alignment);
            if(this instanceof Structure.ByValue && !avoidFFIType) {
               this.getTypeInfo();
            }

            if(this.memory != null && !(this.memory instanceof Structure.AutoAllocated)) {
               this.memory = this.memory.share(0L, (long)size);
            }

            info.size = size;
            return info;
         } else {
            throw new IllegalArgumentException("Structure " + this.getClass() + " has unknown size (ensure " + "all fields are public)");
         }
      }
   }

   private void initializeFields() {
      for(Structure.StructField f : this.fields().values()) {
         this.initializeField(f, f.type);
      }

   }

   private Object initializeField(Structure.StructField structField, Class type) {
      Object value = null;
      if(Structure.class.isAssignableFrom(type) && !Structure.ByReference.class.isAssignableFrom(type)) {
         try {
            value = newInstance(type);
            this.setField(structField, value);
         } catch (IllegalArgumentException var6) {
            String msg = "Can\'t determine size of nested structure: " + var6.getMessage();
            throw new IllegalArgumentException(msg);
         }
      } else if(NativeMapped.class.isAssignableFrom(type)) {
         NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
         value = tc.defaultValue();
         this.setField(structField, value);
      }

      return value;
   }

   int calculateAlignedSize(int calculatedSize) {
      return this.calculateAlignedSize(calculatedSize, this.structAlignment);
   }

   private int calculateAlignedSize(int calculatedSize, int alignment) {
      if(this.alignType != 1 && calculatedSize % alignment != 0) {
         calculatedSize += alignment - calculatedSize % alignment;
      }

      return calculatedSize;
   }

   protected int getStructAlignment() {
      if(this.size == -1) {
         this.calculateSize(true);
      }

      return this.structAlignment;
   }

   protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
      int alignment = 1;
      if(NativeMapped.class.isAssignableFrom(type)) {
         NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
         type = tc.nativeType();
         value = tc.toNative(value, new ToNativeContext());
      }

      int size = Native.getNativeSize(type, value);
      if(!type.isPrimitive() && Long.class != type && Integer.class != type && Short.class != type && Character.class != type && Byte.class != type && Boolean.class != type && Float.class != type && Double.class != type) {
         if(Pointer.class != type && (!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(type)) && !Callback.class.isAssignableFrom(type) && WString.class != type && String.class != type) {
            if(Structure.class.isAssignableFrom(type)) {
               if(Structure.ByReference.class.isAssignableFrom(type)) {
                  alignment = Pointer.SIZE;
               } else {
                  if(value == null) {
                     value = newInstance(type);
                  }

                  alignment = ((Structure)value).getStructAlignment();
               }
            } else {
               if(!type.isArray()) {
                  throw new IllegalArgumentException("Type " + type + " has unknown " + "native alignment");
               }

               alignment = this.getNativeAlignment(type.getComponentType(), (Object)null, isFirstElement);
            }
         } else {
            alignment = Pointer.SIZE;
         }
      } else {
         alignment = size;
      }

      if(this.alignType == 1) {
         alignment = 1;
      } else if(this.alignType == 3) {
         alignment = Math.min(8, alignment);
      } else if(this.alignType == 2 && (!isFirstElement || !Platform.isMac() || !isPPC)) {
         alignment = Math.min(MAX_GNUC_ALIGNMENT, alignment);
      }

      return alignment;
   }

   public String toString() {
      return this.toString(Boolean.getBoolean("jna.dump_memory"));
   }

   public String toString(boolean debug) {
      return this.toString(0, true, true);
   }

   private String format(Class type) {
      String s = type.getName();
      int dot = s.lastIndexOf(".");
      return s.substring(dot + 1);
   }

   private String toString(int indent, boolean showContents, boolean dumpMemory) {
      this.ensureAllocated();
      String LS = System.getProperty("line.separator");
      String name = this.format(this.getClass()) + "(" + this.getPointer() + ")";
      if(!(this.getPointer() instanceof Memory)) {
         name = name + " (" + this.size() + " bytes)";
      }

      String prefix = "";

      for(int idx = 0; idx < indent; ++idx) {
         prefix = prefix + "  ";
      }

      String contents = LS;
      if(!showContents) {
         contents = "...}";
      } else {
         Iterator i = this.fields().values().iterator();

         while(i.hasNext()) {
            Structure.StructField sf = (Structure.StructField)i.next();
            Object value = this.getField(sf);
            String type = this.format(sf.type);
            String index = "";
            contents = contents + prefix;
            if(sf.type.isArray() && value != null) {
               type = this.format(sf.type.getComponentType());
               index = "[" + Array.getLength(value) + "]";
            }

            contents = contents + "  " + type + " " + sf.name + index + "@" + Integer.toHexString(sf.offset);
            if(value instanceof Structure) {
               value = ((Structure)value).toString(indent + 1, !(value instanceof Structure.ByReference), dumpMemory);
            }

            contents = contents + "=";
            if(value instanceof Long) {
               contents = contents + Long.toHexString(((Long)value).longValue());
            } else if(value instanceof Integer) {
               contents = contents + Integer.toHexString(((Integer)value).intValue());
            } else if(value instanceof Short) {
               contents = contents + Integer.toHexString(((Short)value).shortValue());
            } else if(value instanceof Byte) {
               contents = contents + Integer.toHexString(((Byte)value).byteValue());
            } else {
               contents = contents + String.valueOf(value).trim();
            }

            contents = contents + LS;
            if(!i.hasNext()) {
               contents = contents + prefix + "}";
            }
         }
      }

      if(indent == 0 && dumpMemory) {
         int BYTES_PER_ROW = 4;
         contents = contents + LS + "memory dump" + LS;
         byte[] buf = this.getPointer().getByteArray(0L, this.size());

         for(int i = 0; i < buf.length; ++i) {
            if(i % 4 == 0) {
               contents = contents + "[";
            }

            if(buf[i] >= 0 && buf[i] < 16) {
               contents = contents + "0";
            }

            contents = contents + Integer.toHexString(buf[i] & 255);
            if(i % 4 == 3 && i < buf.length - 1) {
               contents = contents + "]" + LS;
            }
         }

         contents = contents + "]";
      }

      return name + " {" + contents;
   }

   public Structure[] toArray(Structure[] array) {
      this.ensureAllocated();
      if(this.memory instanceof Structure.AutoAllocated) {
         Memory m = (Memory)this.memory;
         int requiredSize = array.length * this.size();
         if(m.size() < (long)requiredSize) {
            this.useMemory(this.autoAllocate(requiredSize));
         }
      }

      array[0] = this;
      int size = this.size();

      for(int i = 1; i < array.length; ++i) {
         array[i] = newInstance(this.getClass());
         array[i].useMemory(this.memory.share((long)(i * size), (long)size));
         array[i].read();
      }

      if(!(this instanceof Structure.ByValue)) {
         this.array = array;
      }

      return array;
   }

   public Structure[] toArray(int size) {
      return this.toArray((Structure[])((Structure[])Array.newInstance(this.getClass(), size)));
   }

   private Class baseClass() {
      return (this instanceof Structure.ByReference || this instanceof Structure.ByValue) && Structure.class.isAssignableFrom(this.getClass().getSuperclass())?this.getClass().getSuperclass():this.getClass();
   }

   public boolean equals(Object o) {
      if(o == this) {
         return true;
      } else if(!(o instanceof Structure)) {
         return false;
      } else if(o.getClass() != this.getClass() && ((Structure)o).baseClass() != this.baseClass()) {
         return false;
      } else {
         Structure s = (Structure)o;
         if(s.getPointer().equals(this.getPointer())) {
            return true;
         } else if(s.size() == this.size()) {
            this.clear();
            this.write();
            byte[] buf = this.getPointer().getByteArray(0L, this.size());
            s.clear();
            s.write();
            byte[] sbuf = s.getPointer().getByteArray(0L, s.size());
            return Arrays.equals(buf, sbuf);
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      this.clear();
      this.write();
      Adler32 code = new Adler32();
      code.update(this.getPointer().getByteArray(0L, this.size()));
      return (int)code.getValue();
   }

   protected void cacheTypeInfo(Pointer p) {
      this.typeInfo = p.peer;
   }

   protected Pointer getFieldTypeInfo(Structure.StructField f) {
      Class type = f.type;
      Object value = this.getField(f);
      if(this.typeMapper != null) {
         ToNativeConverter nc = this.typeMapper.getToNativeConverter(type);
         if(nc != null) {
            type = nc.nativeType();
            value = nc.toNative(value, new ToNativeContext());
         }
      }

      return Structure.FFIType.get(value, type);
   }

   Pointer getTypeInfo() {
      Pointer p = getTypeInfo(this);
      this.cacheTypeInfo(p);
      return p;
   }

   public void setAutoSynch(boolean auto) {
      this.setAutoRead(auto);
      this.setAutoWrite(auto);
   }

   public void setAutoRead(boolean auto) {
      this.autoRead = auto;
   }

   public boolean getAutoRead() {
      return this.autoRead;
   }

   public void setAutoWrite(boolean auto) {
      this.autoWrite = auto;
   }

   public boolean getAutoWrite() {
      return this.autoWrite;
   }

   static Pointer getTypeInfo(Object obj) {
      return Structure.FFIType.get(obj);
   }

   public static Structure newInstance(Class type) throws IllegalArgumentException {
      try {
         Structure s = (Structure)type.newInstance();
         if(s instanceof Structure.ByValue) {
            s.allocateMemory();
         }

         return s;
      } catch (InstantiationException var3) {
         String msg = "Can\'t instantiate " + type + " (" + var3 + ")";
         throw new IllegalArgumentException(msg);
      } catch (IllegalAccessException var4) {
         String msg = "Instantiation of " + type + " not allowed, is it public? (" + var4 + ")";
         throw new IllegalArgumentException(msg);
      }
   }

   private static void structureArrayCheck(Structure[] ss) {
      Pointer base = ss[0].getPointer();
      int size = ss[0].size();

      for(int si = 1; si < ss.length; ++si) {
         if(ss[si].getPointer().peer != base.peer + (long)(size * si)) {
            String msg = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + si + ")";
            throw new IllegalArgumentException(msg);
         }
      }

   }

   public static void autoRead(Structure[] ss) {
      structureArrayCheck(ss);
      if(ss[0].array == ss) {
         ss[0].autoRead();
      } else {
         for(int si = 0; si < ss.length; ++si) {
            ss[si].autoRead();
         }
      }

   }

   public void autoRead() {
      if(this.getAutoRead()) {
         this.read();
         if(this.array != null) {
            for(int i = 1; i < this.array.length; ++i) {
               this.array[i].autoRead();
            }
         }
      }

   }

   public static void autoWrite(Structure[] ss) {
      structureArrayCheck(ss);
      if(ss[0].array == ss) {
         ss[0].autoWrite();
      } else {
         for(int si = 0; si < ss.length; ++si) {
            ss[si].autoWrite();
         }
      }

   }

   public void autoWrite() {
      if(this.getAutoWrite()) {
         this.write();
         if(this.array != null) {
            for(int i = 1; i < this.array.length; ++i) {
               this.array[i].autoWrite();
            }
         }
      }

   }

   protected int getNativeSize(Class nativeType, Object value) {
      return Native.getNativeSize(nativeType, value);
   }

   static {
      Field[] fields = Structure.MemberOrder.class.getFields();
      List names = new ArrayList();

      for(int i = 0; i < fields.length; ++i) {
         names.add(fields[i].getName());
      }

      List expected = Arrays.asList(Structure.MemberOrder.FIELDS);
      List reversed = new ArrayList(expected);
      Collections.reverse(reversed);
      REVERSE_FIELDS = names.equals(reversed);
      REQUIRES_FIELD_ORDER = !names.equals(expected) && !REVERSE_FIELDS;
      String arch = System.getProperty("os.arch").toLowerCase();
      isPPC = "ppc".equals(arch) || "powerpc".equals(arch);
      isSPARC = "sparc".equals(arch);
      isARM = "arm".equals(arch);
      MAX_GNUC_ALIGNMENT = !isSPARC && (!isPPC && !isARM || !Platform.isLinux())?Native.LONG_SIZE:8;
      layoutInfo = new WeakHashMap();
      reads = new ThreadLocal() {
         protected synchronized Object initialValue() {
            return new HashMap();
         }
      };
      busy = new ThreadLocal() {
         protected synchronized Object initialValue() {
            return new null.StructureSet();
         }

         class StructureSet extends AbstractCollection implements Set {
            private Structure[] elements;
            private int count;

            private void ensureCapacity(int size) {
               if(this.elements == null) {
                  this.elements = new Structure[size * 3 / 2];
               } else if(this.elements.length < size) {
                  Structure[] e = new Structure[size * 3 / 2];
                  System.arraycopy(this.elements, 0, e, 0, this.elements.length);
                  this.elements = e;
               }

            }

            public int size() {
               return this.count;
            }

            public boolean contains(Object o) {
               return this.indexOf(o) != -1;
            }

            public boolean add(Object o) {
               if(!this.contains(o)) {
                  this.ensureCapacity(this.count + 1);
                  this.elements[this.count++] = (Structure)o;
               }

               return true;
            }

            private int indexOf(Object o) {
               Structure s1 = (Structure)o;

               for(int i = 0; i < this.count; ++i) {
                  Structure s2 = this.elements[i];
                  if(s1 == s2 || s1.getClass() == s2.getClass() && s1.size() == s2.size() && s1.getPointer().equals(s2.getPointer())) {
                     return i;
                  }
               }

               return -1;
            }

            public boolean remove(Object o) {
               int idx = this.indexOf(o);
               if(idx != -1) {
                  if(--this.count > 0) {
                     this.elements[idx] = this.elements[this.count];
                     this.elements[this.count] = null;
                  }

                  return true;
               } else {
                  return false;
               }
            }

            public Iterator iterator() {
               Structure[] e = new Structure[this.count];
               if(this.count > 0) {
                  System.arraycopy(this.elements, 0, e, 0, this.count);
               }

               return Arrays.asList(e).iterator();
            }
         }
      };
   }

   private class AutoAllocated extends Memory {
      public AutoAllocated(int size) {
         super((long)size);
         super.clear();
      }
   }

   public interface ByReference {
   }

   public interface ByValue {
   }

   static class FFIType extends Structure {
      private static Map typeInfoMap = new WeakHashMap();
      private static final int FFI_TYPE_STRUCT = 13;
      public Structure.FFIType.size_t size;
      public short alignment;
      public short type = 13;
      public Pointer elements;

      private FFIType(Structure ref) {
         ref.ensureAllocated(true);
         Pointer[] els;
         if(ref instanceof Union) {
            Structure.StructField sf = ((Union)ref).biggestField;
            els = new Pointer[]{get(ref.getField(sf), sf.type), null};
         } else {
            els = new Pointer[ref.fields().size() + 1];
            int idx = 0;

            for(Structure.StructField sf : ref.fields().values()) {
               els[idx++] = ref.getFieldTypeInfo(sf);
            }
         }

         this.init(els);
      }

      private FFIType(Object array, Class type) {
         int length = Array.getLength(array);
         Pointer[] els = new Pointer[length + 1];
         Pointer p = get((Object)null, type.getComponentType());

         for(int i = 0; i < length; ++i) {
            els[i] = p;
         }

         this.init(els);
      }

      private void init(Pointer[] els) {
         this.elements = new Memory((long)(Pointer.SIZE * els.length));
         this.elements.write(0L, (Pointer[])els, 0, els.length);
         this.write();
      }

      static Pointer get(Object obj) {
         return obj == null?Structure.FFIType.FFITypes.ffi_type_pointer:(obj instanceof Class?get((Object)null, (Class)obj):get(obj, obj.getClass()));
      }

      private static Pointer get(Object obj, Class cls) {
         TypeMapper mapper = Native.getTypeMapper(cls);
         if(mapper != null) {
            ToNativeConverter nc = mapper.getToNativeConverter(cls);
            if(nc != null) {
               cls = nc.nativeType();
            }
         }

         synchronized(typeInfoMap) {
            Object o = typeInfoMap.get(cls);
            if(o instanceof Pointer) {
               return (Pointer)o;
            } else if(o instanceof Structure.FFIType) {
               return ((Structure.FFIType)o).getPointer();
            } else if((!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(cls)) && !Callback.class.isAssignableFrom(cls)) {
               if(Structure.class.isAssignableFrom(cls)) {
                  if(obj == null) {
                     obj = newInstance(cls);
                  }

                  if(Structure.ByReference.class.isAssignableFrom(cls)) {
                     typeInfoMap.put(cls, Structure.FFIType.FFITypes.ffi_type_pointer);
                     return Structure.FFIType.FFITypes.ffi_type_pointer;
                  } else {
                     Structure.FFIType type = new Structure.FFIType((Structure)obj);
                     typeInfoMap.put(cls, type);
                     return type.getPointer();
                  }
               } else if(NativeMapped.class.isAssignableFrom(cls)) {
                  NativeMappedConverter c = NativeMappedConverter.getInstance(cls);
                  return get(c.toNative(obj, new ToNativeContext()), c.nativeType());
               } else if(cls.isArray()) {
                  Structure.FFIType type = new Structure.FFIType(obj, cls);
                  typeInfoMap.put(obj, type);
                  return type.getPointer();
               } else {
                  throw new IllegalArgumentException("Unsupported Structure field type " + cls);
               }
            } else {
               typeInfoMap.put(cls, Structure.FFIType.FFITypes.ffi_type_pointer);
               return Structure.FFIType.FFITypes.ffi_type_pointer;
            }
         }
      }

      static {
         if(Native.POINTER_SIZE == 0) {
            throw new Error("Native library not initialized");
         } else if(Structure.FFIType.FFITypes.ffi_type_void == null) {
            throw new Error("FFI types not initialized");
         } else {
            typeInfoMap.put(Void.TYPE, Structure.FFIType.FFITypes.ffi_type_void);
            typeInfoMap.put(Void.class, Structure.FFIType.FFITypes.ffi_type_void);
            typeInfoMap.put(Float.TYPE, Structure.FFIType.FFITypes.ffi_type_float);
            typeInfoMap.put(Float.class, Structure.FFIType.FFITypes.ffi_type_float);
            typeInfoMap.put(Double.TYPE, Structure.FFIType.FFITypes.ffi_type_double);
            typeInfoMap.put(Double.class, Structure.FFIType.FFITypes.ffi_type_double);
            typeInfoMap.put(Long.TYPE, Structure.FFIType.FFITypes.ffi_type_sint64);
            typeInfoMap.put(Long.class, Structure.FFIType.FFITypes.ffi_type_sint64);
            typeInfoMap.put(Integer.TYPE, Structure.FFIType.FFITypes.ffi_type_sint32);
            typeInfoMap.put(Integer.class, Structure.FFIType.FFITypes.ffi_type_sint32);
            typeInfoMap.put(Short.TYPE, Structure.FFIType.FFITypes.ffi_type_sint16);
            typeInfoMap.put(Short.class, Structure.FFIType.FFITypes.ffi_type_sint16);
            Pointer ctype = Native.WCHAR_SIZE == 2?Structure.FFIType.FFITypes.ffi_type_uint16:Structure.FFIType.FFITypes.ffi_type_uint32;
            typeInfoMap.put(Character.TYPE, ctype);
            typeInfoMap.put(Character.class, ctype);
            typeInfoMap.put(Byte.TYPE, Structure.FFIType.FFITypes.ffi_type_sint8);
            typeInfoMap.put(Byte.class, Structure.FFIType.FFITypes.ffi_type_sint8);
            typeInfoMap.put(Pointer.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(Boolean.TYPE, Structure.FFIType.FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, Structure.FFIType.FFITypes.ffi_type_uint32);
         }
      }

      private static class FFITypes {
         private static Pointer ffi_type_void;
         private static Pointer ffi_type_float;
         private static Pointer ffi_type_double;
         private static Pointer ffi_type_longdouble;
         private static Pointer ffi_type_uint8;
         private static Pointer ffi_type_sint8;
         private static Pointer ffi_type_uint16;
         private static Pointer ffi_type_sint16;
         private static Pointer ffi_type_uint32;
         private static Pointer ffi_type_sint32;
         private static Pointer ffi_type_uint64;
         private static Pointer ffi_type_sint64;
         private static Pointer ffi_type_pointer;
      }

      public static class size_t extends IntegerType {
         public size_t() {
            this(0L);
         }

         public size_t(long value) {
            super(Native.POINTER_SIZE, value);
         }
      }
   }

   private class LayoutInfo {
      int size;
      int alignment;
      Map fields;
      int alignType;
      TypeMapper typeMapper;
      List fieldOrder;
      boolean variable;

      private LayoutInfo() {
         this.size = -1;
         this.alignment = 1;
         this.fields = Collections.synchronizedMap(new LinkedHashMap());
         this.alignType = 0;
      }
   }

   private static class MemberOrder {
      private static final String[] FIELDS = new String[]{"first", "second", "middle", "penultimate", "last"};
      public int first;
      public int second;
      public int middle;
      public int penultimate;
      public int last;
   }

   class StructField {
      public String name;
      public Class type;
      public Field field;
      public int size = -1;
      public int offset = -1;
      public boolean isVolatile;
      public boolean isReadOnly;
      public FromNativeConverter readConverter;
      public ToNativeConverter writeConverter;
      public FromNativeContext context;

      public String toString() {
         return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
      }
   }
}
