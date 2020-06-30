package org.lwjgl.util.mapped;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import sun.misc.Unsafe;

final class MappedObjectUnsafe {
   static final Unsafe INSTANCE = getUnsafeInstance();
   private static final long BUFFER_ADDRESS_OFFSET = getObjectFieldOffset(ByteBuffer.class, "address");
   private static final long BUFFER_CAPACITY_OFFSET = getObjectFieldOffset(ByteBuffer.class, "capacity");
   private static final ByteBuffer global = ByteBuffer.allocateDirect(4096);

   static ByteBuffer newBuffer(long address, int capacity) {
      if(address > 0L && capacity >= 0) {
         ByteBuffer buffer = global.duplicate().order(ByteOrder.nativeOrder());
         INSTANCE.putLong(buffer, BUFFER_ADDRESS_OFFSET, address);
         INSTANCE.putInt(buffer, BUFFER_CAPACITY_OFFSET, capacity);
         buffer.position(0);
         buffer.limit(capacity);
         return buffer;
      } else {
         throw new IllegalStateException("you almost crashed the jvm");
      }
   }

   private static long getObjectFieldOffset(Class type, String fieldName) {
      while(type != null) {
         try {
            return INSTANCE.objectFieldOffset(type.getDeclaredField(fieldName));
         } catch (Throwable var3) {
            type = type.getSuperclass();
         }
      }

      throw new UnsupportedOperationException();
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
