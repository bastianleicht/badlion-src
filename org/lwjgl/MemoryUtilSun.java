package org.lwjgl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import org.lwjgl.MemoryUtil;
import sun.misc.Unsafe;
import sun.reflect.FieldAccessor;

final class MemoryUtilSun {
   private static class AccessorReflectFast implements MemoryUtil.Accessor {
      private final FieldAccessor addressAccessor;

      AccessorReflectFast() {
         Field address;
         try {
            address = MemoryUtil.getAddressField();
         } catch (NoSuchFieldException var4) {
            throw new UnsupportedOperationException(var4);
         }

         address.setAccessible(true);

         try {
            Method m = Field.class.getDeclaredMethod("acquireFieldAccessor", new Class[]{Boolean.TYPE});
            m.setAccessible(true);
            this.addressAccessor = (FieldAccessor)m.invoke(address, new Object[]{Boolean.valueOf(true)});
         } catch (Exception var3) {
            throw new UnsupportedOperationException(var3);
         }
      }

      public long getAddress(Buffer buffer) {
         return this.addressAccessor.getLong(buffer);
      }
   }

   private static class AccessorUnsafe implements MemoryUtil.Accessor {
      private final Unsafe unsafe;
      private final long address;

      AccessorUnsafe() {
         try {
            this.unsafe = getUnsafeInstance();
            this.address = this.unsafe.objectFieldOffset(MemoryUtil.getAddressField());
         } catch (Exception var2) {
            throw new UnsupportedOperationException(var2);
         }
      }

      public long getAddress(Buffer buffer) {
         return this.unsafe.getLong(buffer, this.address);
      }

      private static Unsafe getUnsafeInstance() {
         Field[] fields = Unsafe.class.getDeclaredFields();
         Field[] arr$ = fields;
         int len$ = fields.length;
         int i$ = 0;

         while(true) {
            label14: {
               if(i$ < len$) {
                  Field field = arr$[i$];
                  if(!field.getType().equals(Unsafe.class)) {
                     break label14;
                  }

                  int modifiers = field.getModifiers();
                  if(!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                     break label14;
                  }

                  field.setAccessible(true);

                  try {
                     return (Unsafe)field.get((Object)null);
                  } catch (IllegalAccessException var7) {
                     ;
                  }
               }

               throw new UnsupportedOperationException();
            }

            ++i$;
         }
      }
   }
}
