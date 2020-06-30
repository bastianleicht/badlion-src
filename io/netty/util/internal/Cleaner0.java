package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import sun.misc.Cleaner;

final class Cleaner0 {
   private static final long CLEANER_FIELD_OFFSET;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Cleaner0.class);

   static void freeDirectBuffer(ByteBuffer buffer) {
      if(CLEANER_FIELD_OFFSET != -1L && buffer.isDirect()) {
         try {
            Cleaner cleaner = (Cleaner)PlatformDependent0.getObject(buffer, CLEANER_FIELD_OFFSET);
            if(cleaner != null) {
               cleaner.clean();
            }
         } catch (Throwable var2) {
            ;
         }

      }
   }

   static {
      ByteBuffer direct = ByteBuffer.allocateDirect(1);
      long fieldOffset = -1L;
      if(PlatformDependent0.hasUnsafe()) {
         try {
            Field cleanerField = direct.getClass().getDeclaredField("cleaner");
            cleanerField.setAccessible(true);
            Cleaner cleaner = (Cleaner)cleanerField.get(direct);
            cleaner.clean();
            fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
         } catch (Throwable var5) {
            fieldOffset = -1L;
         }
      }

      logger.debug("java.nio.ByteBuffer.cleaner(): {}", (Object)(fieldOffset != -1L?"available":"unavailable"));
      CLEANER_FIELD_OFFSET = fieldOffset;
      freeDirectBuffer(direct);
   }
}
