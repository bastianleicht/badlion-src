package com.sun.jna;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import java.util.ArrayList;
import java.util.List;

public class DefaultTypeMapper implements TypeMapper {
   private List toNativeConverters = new ArrayList();
   private List fromNativeConverters = new ArrayList();

   private Class getAltClass(Class cls) {
      return cls == Boolean.class?Boolean.TYPE:(cls == Boolean.TYPE?Boolean.class:(cls == Byte.class?Byte.TYPE:(cls == Byte.TYPE?Byte.class:(cls == Character.class?Character.TYPE:(cls == Character.TYPE?Character.class:(cls == Short.class?Short.TYPE:(cls == Short.TYPE?Short.class:(cls == Integer.class?Integer.TYPE:(cls == Integer.TYPE?Integer.class:(cls == Long.class?Long.TYPE:(cls == Long.TYPE?Long.class:(cls == Float.class?Float.TYPE:(cls == Float.TYPE?Float.class:(cls == Double.class?Double.TYPE:(cls == Double.TYPE?Double.class:null)))))))))))))));
   }

   public void addToNativeConverter(Class cls, ToNativeConverter converter) {
      this.toNativeConverters.add(new DefaultTypeMapper.Entry(cls, converter));
      Class alt = this.getAltClass(cls);
      if(alt != null) {
         this.toNativeConverters.add(new DefaultTypeMapper.Entry(alt, converter));
      }

   }

   public void addFromNativeConverter(Class cls, FromNativeConverter converter) {
      this.fromNativeConverters.add(new DefaultTypeMapper.Entry(cls, converter));
      Class alt = this.getAltClass(cls);
      if(alt != null) {
         this.fromNativeConverters.add(new DefaultTypeMapper.Entry(alt, converter));
      }

   }

   protected void addTypeConverter(Class cls, TypeConverter converter) {
      this.addFromNativeConverter(cls, converter);
      this.addToNativeConverter(cls, converter);
   }

   private Object lookupConverter(Class javaClass, List converters) {
      for(DefaultTypeMapper.Entry entry : converters) {
         if(entry.type.isAssignableFrom(javaClass)) {
            return entry.converter;
         }
      }

      return null;
   }

   public FromNativeConverter getFromNativeConverter(Class javaType) {
      return (FromNativeConverter)this.lookupConverter(javaType, this.fromNativeConverters);
   }

   public ToNativeConverter getToNativeConverter(Class javaType) {
      return (ToNativeConverter)this.lookupConverter(javaType, this.toNativeConverters);
   }

   private static class Entry {
      public Class type;
      public Object converter;

      public Entry(Class type, Object converter) {
         this.type = type;
         this.converter = converter;
      }
   }
}
