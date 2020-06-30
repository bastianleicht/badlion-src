package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public class NativeMappedConverter implements TypeConverter {
   private static final Map converters = new WeakHashMap();
   private final Class type;
   private final Class nativeType;
   private final NativeMapped instance;

   public static NativeMappedConverter getInstance(Class cls) {
      synchronized(converters) {
         Reference r = (Reference)converters.get(cls);
         NativeMappedConverter nmc = r != null?(NativeMappedConverter)r.get():null;
         if(nmc == null) {
            nmc = new NativeMappedConverter(cls);
            converters.put(cls, new SoftReference(nmc));
         }

         return nmc;
      }
   }

   public NativeMappedConverter(Class type) {
      if(!NativeMapped.class.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
      } else {
         this.type = type;
         this.instance = this.defaultValue();
         this.nativeType = this.instance.nativeType();
      }
   }

   public NativeMapped defaultValue() {
      try {
         return (NativeMapped)this.type.newInstance();
      } catch (InstantiationException var3) {
         String msg = "Can\'t create an instance of " + this.type + ", requires a no-arg constructor: " + var3;
         throw new IllegalArgumentException(msg);
      } catch (IllegalAccessException var4) {
         String msg = "Not allowed to create an instance of " + this.type + ", requires a public, no-arg constructor: " + var4;
         throw new IllegalArgumentException(msg);
      }
   }

   public Object fromNative(Object nativeValue, FromNativeContext context) {
      return this.instance.fromNative(nativeValue, context);
   }

   public Class nativeType() {
      return this.nativeType;
   }

   public Object toNative(Object value, ToNativeContext context) {
      if(value == null) {
         if(Pointer.class.isAssignableFrom(this.nativeType)) {
            return null;
         }

         value = this.defaultValue();
      }

      return ((NativeMapped)value).toNative();
   }
}
