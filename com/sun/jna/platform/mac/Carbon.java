package com.sun.jna.platform.mac;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import java.nio.IntBuffer;

public interface Carbon {
   Carbon INSTANCE = (Carbon)Native.loadLibrary("Carbon", Carbon.class);
   int cmdKey = 256;
   int shiftKey = 512;
   int optionKey = 2048;
   int controlKey = 4096;

   Pointer GetEventDispatcherTarget();

   int InstallEventHandler(Pointer var1, Carbon.EventHandlerProcPtr var2, int var3, Carbon.EventTypeSpec[] var4, Pointer var5, PointerByReference var6);

   int RegisterEventHotKey(int var1, int var2, Carbon.EventHotKeyID.ByValue var3, Pointer var4, int var5, PointerByReference var6);

   int GetEventParameter(Pointer var1, int var2, int var3, Pointer var4, int var5, IntBuffer var6, Carbon.EventHotKeyID var7);

   int RemoveEventHandler(Pointer var1);

   int UnregisterEventHotKey(Pointer var1);

   public interface EventHandlerProcPtr extends Callback {
      int callback(Pointer var1, Pointer var2, Pointer var3);
   }

   public static class EventHotKeyID extends Structure {
      public int signature;
      public int id;

      public static class ByValue extends Carbon.EventHotKeyID implements Structure.ByValue {
      }
   }

   public static class EventTypeSpec extends Structure {
      public int eventClass;
      public int eventKind;
   }
}
