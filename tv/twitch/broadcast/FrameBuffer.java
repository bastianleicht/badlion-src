package tv.twitch.broadcast;

import java.util.HashMap;
import java.util.Map;
import tv.twitch.broadcast.StreamAPI;

public class FrameBuffer {
   private static Map s_OutstandingBuffers = new HashMap();
   protected long m_NativeAddress = 0L;
   protected int m_Size = 0;
   protected StreamAPI m_API = null;

   public static FrameBuffer lookupBuffer(long var0) {
      return (FrameBuffer)s_OutstandingBuffers.get(Long.valueOf(var0));
   }

   protected static void registerBuffer(FrameBuffer var0) {
      if(var0.getAddress() != 0L) {
         s_OutstandingBuffers.put(Long.valueOf(var0.getAddress()), var0);
      }

   }

   protected static void unregisterBuffer(FrameBuffer var0) {
      s_OutstandingBuffers.remove(Long.valueOf(var0.getAddress()));
   }

   FrameBuffer(StreamAPI var1, int var2) {
      this.m_NativeAddress = var1.allocateFrameBuffer(var2);
      if(this.m_NativeAddress != 0L) {
         this.m_API = var1;
         this.m_Size = var2;
         registerBuffer(this);
      }
   }

   public boolean getIsValid() {
      return this.m_NativeAddress != 0L;
   }

   public int getSize() {
      return this.m_Size;
   }

   public long getAddress() {
      return this.m_NativeAddress;
   }

   public void free() {
      if(this.m_NativeAddress != 0L) {
         unregisterBuffer(this);
         this.m_API.freeFrameBuffer(this.m_NativeAddress);
         this.m_NativeAddress = 0L;
      }

   }

   protected void finalize() {
      this.free();
   }
}
