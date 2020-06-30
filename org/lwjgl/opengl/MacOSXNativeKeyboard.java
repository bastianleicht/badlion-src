package org.lwjgl.opengl;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.lwjgl.opengl.EventQueue;

final class MacOSXNativeKeyboard extends EventQueue {
   private final byte[] key_states = new byte[256];
   private final ByteBuffer event = ByteBuffer.allocate(18);
   private ByteBuffer window_handle;
   private boolean has_deferred_event;
   private long deferred_nanos;
   private int deferred_key_code;
   private byte deferred_key_state;
   private int deferred_character;
   private HashMap nativeToLwjglMap = new HashMap();

   MacOSXNativeKeyboard(ByteBuffer window_handle) {
      super(18);
      this.initKeyboardMappings();
      this.window_handle = window_handle;
   }

   private native void nRegisterKeyListener(ByteBuffer var1);

   private native void nUnregisterKeyListener(ByteBuffer var1);

   private void initKeyboardMappings() {
      this.nativeToLwjglMap.put(Short.valueOf((short)29), Integer.valueOf(11));
      this.nativeToLwjglMap.put(Short.valueOf((short)18), Integer.valueOf(2));
      this.nativeToLwjglMap.put(Short.valueOf((short)19), Integer.valueOf(3));
      this.nativeToLwjglMap.put(Short.valueOf((short)20), Integer.valueOf(4));
      this.nativeToLwjglMap.put(Short.valueOf((short)21), Integer.valueOf(5));
      this.nativeToLwjglMap.put(Short.valueOf((short)23), Integer.valueOf(6));
      this.nativeToLwjglMap.put(Short.valueOf((short)22), Integer.valueOf(7));
      this.nativeToLwjglMap.put(Short.valueOf((short)26), Integer.valueOf(8));
      this.nativeToLwjglMap.put(Short.valueOf((short)28), Integer.valueOf(9));
      this.nativeToLwjglMap.put(Short.valueOf((short)25), Integer.valueOf(10));
      this.nativeToLwjglMap.put(Short.valueOf((short)0), Integer.valueOf(30));
      this.nativeToLwjglMap.put(Short.valueOf((short)11), Integer.valueOf(48));
      this.nativeToLwjglMap.put(Short.valueOf((short)8), Integer.valueOf(46));
      this.nativeToLwjglMap.put(Short.valueOf((short)2), Integer.valueOf(32));
      this.nativeToLwjglMap.put(Short.valueOf((short)14), Integer.valueOf(18));
      this.nativeToLwjglMap.put(Short.valueOf((short)3), Integer.valueOf(33));
      this.nativeToLwjglMap.put(Short.valueOf((short)5), Integer.valueOf(34));
      this.nativeToLwjglMap.put(Short.valueOf((short)4), Integer.valueOf(35));
      this.nativeToLwjglMap.put(Short.valueOf((short)34), Integer.valueOf(23));
      this.nativeToLwjglMap.put(Short.valueOf((short)38), Integer.valueOf(36));
      this.nativeToLwjglMap.put(Short.valueOf((short)40), Integer.valueOf(37));
      this.nativeToLwjglMap.put(Short.valueOf((short)37), Integer.valueOf(38));
      this.nativeToLwjglMap.put(Short.valueOf((short)46), Integer.valueOf(50));
      this.nativeToLwjglMap.put(Short.valueOf((short)45), Integer.valueOf(49));
      this.nativeToLwjglMap.put(Short.valueOf((short)31), Integer.valueOf(24));
      this.nativeToLwjglMap.put(Short.valueOf((short)35), Integer.valueOf(25));
      this.nativeToLwjglMap.put(Short.valueOf((short)12), Integer.valueOf(16));
      this.nativeToLwjglMap.put(Short.valueOf((short)15), Integer.valueOf(19));
      this.nativeToLwjglMap.put(Short.valueOf((short)1), Integer.valueOf(31));
      this.nativeToLwjglMap.put(Short.valueOf((short)17), Integer.valueOf(20));
      this.nativeToLwjglMap.put(Short.valueOf((short)32), Integer.valueOf(22));
      this.nativeToLwjglMap.put(Short.valueOf((short)9), Integer.valueOf(47));
      this.nativeToLwjglMap.put(Short.valueOf((short)13), Integer.valueOf(17));
      this.nativeToLwjglMap.put(Short.valueOf((short)7), Integer.valueOf(45));
      this.nativeToLwjglMap.put(Short.valueOf((short)16), Integer.valueOf(21));
      this.nativeToLwjglMap.put(Short.valueOf((short)6), Integer.valueOf(44));
      this.nativeToLwjglMap.put(Short.valueOf((short)42), Integer.valueOf(43));
      this.nativeToLwjglMap.put(Short.valueOf((short)43), Integer.valueOf(51));
      this.nativeToLwjglMap.put(Short.valueOf((short)24), Integer.valueOf(13));
      this.nativeToLwjglMap.put(Short.valueOf((short)33), Integer.valueOf(26));
      this.nativeToLwjglMap.put(Short.valueOf((short)27), Integer.valueOf(12));
      this.nativeToLwjglMap.put(Short.valueOf((short)39), Integer.valueOf(40));
      this.nativeToLwjglMap.put(Short.valueOf((short)30), Integer.valueOf(27));
      this.nativeToLwjglMap.put(Short.valueOf((short)41), Integer.valueOf(39));
      this.nativeToLwjglMap.put(Short.valueOf((short)44), Integer.valueOf(53));
      this.nativeToLwjglMap.put(Short.valueOf((short)47), Integer.valueOf(52));
      this.nativeToLwjglMap.put(Short.valueOf((short)50), Integer.valueOf(41));
      this.nativeToLwjglMap.put(Short.valueOf((short)65), Integer.valueOf(83));
      this.nativeToLwjglMap.put(Short.valueOf((short)67), Integer.valueOf(55));
      this.nativeToLwjglMap.put(Short.valueOf((short)69), Integer.valueOf(78));
      this.nativeToLwjglMap.put(Short.valueOf((short)71), Integer.valueOf(218));
      this.nativeToLwjglMap.put(Short.valueOf((short)75), Integer.valueOf(181));
      this.nativeToLwjglMap.put(Short.valueOf((short)76), Integer.valueOf(156));
      this.nativeToLwjglMap.put(Short.valueOf((short)78), Integer.valueOf(74));
      this.nativeToLwjglMap.put(Short.valueOf((short)81), Integer.valueOf(141));
      this.nativeToLwjglMap.put(Short.valueOf((short)82), Integer.valueOf(82));
      this.nativeToLwjglMap.put(Short.valueOf((short)83), Integer.valueOf(79));
      this.nativeToLwjglMap.put(Short.valueOf((short)84), Integer.valueOf(80));
      this.nativeToLwjglMap.put(Short.valueOf((short)85), Integer.valueOf(81));
      this.nativeToLwjglMap.put(Short.valueOf((short)86), Integer.valueOf(75));
      this.nativeToLwjglMap.put(Short.valueOf((short)87), Integer.valueOf(76));
      this.nativeToLwjglMap.put(Short.valueOf((short)88), Integer.valueOf(77));
      this.nativeToLwjglMap.put(Short.valueOf((short)89), Integer.valueOf(71));
      this.nativeToLwjglMap.put(Short.valueOf((short)91), Integer.valueOf(72));
      this.nativeToLwjglMap.put(Short.valueOf((short)92), Integer.valueOf(73));
      this.nativeToLwjglMap.put(Short.valueOf((short)36), Integer.valueOf(28));
      this.nativeToLwjglMap.put(Short.valueOf((short)48), Integer.valueOf(15));
      this.nativeToLwjglMap.put(Short.valueOf((short)49), Integer.valueOf(57));
      this.nativeToLwjglMap.put(Short.valueOf((short)51), Integer.valueOf(14));
      this.nativeToLwjglMap.put(Short.valueOf((short)53), Integer.valueOf(1));
      this.nativeToLwjglMap.put(Short.valueOf((short)54), Integer.valueOf(220));
      this.nativeToLwjglMap.put(Short.valueOf((short)55), Integer.valueOf(219));
      this.nativeToLwjglMap.put(Short.valueOf((short)56), Integer.valueOf(42));
      this.nativeToLwjglMap.put(Short.valueOf((short)57), Integer.valueOf(58));
      this.nativeToLwjglMap.put(Short.valueOf((short)58), Integer.valueOf(56));
      this.nativeToLwjglMap.put(Short.valueOf((short)59), Integer.valueOf(29));
      this.nativeToLwjglMap.put(Short.valueOf((short)60), Integer.valueOf(54));
      this.nativeToLwjglMap.put(Short.valueOf((short)61), Integer.valueOf(184));
      this.nativeToLwjglMap.put(Short.valueOf((short)62), Integer.valueOf(157));
      this.nativeToLwjglMap.put(Short.valueOf((short)63), Integer.valueOf(196));
      this.nativeToLwjglMap.put(Short.valueOf((short)119), Integer.valueOf(207));
      this.nativeToLwjglMap.put(Short.valueOf((short)122), Integer.valueOf(59));
      this.nativeToLwjglMap.put(Short.valueOf((short)120), Integer.valueOf(60));
      this.nativeToLwjglMap.put(Short.valueOf((short)99), Integer.valueOf(61));
      this.nativeToLwjglMap.put(Short.valueOf((short)118), Integer.valueOf(62));
      this.nativeToLwjglMap.put(Short.valueOf((short)96), Integer.valueOf(63));
      this.nativeToLwjglMap.put(Short.valueOf((short)97), Integer.valueOf(64));
      this.nativeToLwjglMap.put(Short.valueOf((short)98), Integer.valueOf(65));
      this.nativeToLwjglMap.put(Short.valueOf((short)100), Integer.valueOf(66));
      this.nativeToLwjglMap.put(Short.valueOf((short)101), Integer.valueOf(67));
      this.nativeToLwjglMap.put(Short.valueOf((short)109), Integer.valueOf(68));
      this.nativeToLwjglMap.put(Short.valueOf((short)103), Integer.valueOf(87));
      this.nativeToLwjglMap.put(Short.valueOf((short)111), Integer.valueOf(88));
      this.nativeToLwjglMap.put(Short.valueOf((short)105), Integer.valueOf(100));
      this.nativeToLwjglMap.put(Short.valueOf((short)107), Integer.valueOf(101));
      this.nativeToLwjglMap.put(Short.valueOf((short)113), Integer.valueOf(102));
      this.nativeToLwjglMap.put(Short.valueOf((short)106), Integer.valueOf(103));
      this.nativeToLwjglMap.put(Short.valueOf((short)64), Integer.valueOf(104));
      this.nativeToLwjglMap.put(Short.valueOf((short)79), Integer.valueOf(105));
      this.nativeToLwjglMap.put(Short.valueOf((short)80), Integer.valueOf(113));
      this.nativeToLwjglMap.put(Short.valueOf((short)117), Integer.valueOf(211));
      this.nativeToLwjglMap.put(Short.valueOf((short)114), Integer.valueOf(210));
      this.nativeToLwjglMap.put(Short.valueOf((short)115), Integer.valueOf(199));
      this.nativeToLwjglMap.put(Short.valueOf((short)121), Integer.valueOf(209));
      this.nativeToLwjglMap.put(Short.valueOf((short)116), Integer.valueOf(201));
      this.nativeToLwjglMap.put(Short.valueOf((short)123), Integer.valueOf(203));
      this.nativeToLwjglMap.put(Short.valueOf((short)124), Integer.valueOf(205));
      this.nativeToLwjglMap.put(Short.valueOf((short)125), Integer.valueOf(208));
      this.nativeToLwjglMap.put(Short.valueOf((short)126), Integer.valueOf(200));
      this.nativeToLwjglMap.put(Short.valueOf((short)10), Integer.valueOf(167));
      this.nativeToLwjglMap.put(Short.valueOf((short)110), Integer.valueOf(221));
      this.nativeToLwjglMap.put(Short.valueOf((short)297), Integer.valueOf(146));
   }

   public void register() {
      this.nRegisterKeyListener(this.window_handle);
   }

   public void unregister() {
      this.nUnregisterKeyListener(this.window_handle);
   }

   public void putKeyboardEvent(int key_code, byte state, int character, long nanos, boolean repeat) {
      this.event.clear();
      this.event.putInt(key_code).put(state).putInt(character).putLong(nanos).put((byte)(repeat?1:0));
      this.event.flip();
      this.putEvent(this.event);
   }

   public synchronized void poll(ByteBuffer key_down_buffer) {
      this.flushDeferredEvent();
      int old_position = key_down_buffer.position();
      key_down_buffer.put(this.key_states);
      key_down_buffer.position(old_position);
   }

   public synchronized void copyEvents(ByteBuffer dest) {
      this.flushDeferredEvent();
      super.copyEvents(dest);
   }

   private synchronized void handleKey(int key_code, byte state, int character, long nanos) {
      if(character == '\uffff') {
         character = 0;
      }

      if(state == 1) {
         boolean repeat = false;
         if(this.has_deferred_event) {
            if(nanos == this.deferred_nanos && this.deferred_key_code == key_code) {
               this.has_deferred_event = false;
               repeat = true;
            } else {
               this.flushDeferredEvent();
            }
         }

         this.putKeyEvent(key_code, state, character, nanos, repeat);
      } else {
         this.flushDeferredEvent();
         this.has_deferred_event = true;
         this.deferred_nanos = nanos;
         this.deferred_key_code = key_code;
         this.deferred_key_state = state;
         this.deferred_character = character;
      }

   }

   private void flushDeferredEvent() {
      if(this.has_deferred_event) {
         this.putKeyEvent(this.deferred_key_code, this.deferred_key_state, this.deferred_character, this.deferred_nanos, false);
         this.has_deferred_event = false;
      }

   }

   public void putKeyEvent(int key_code, byte state, int character, long nanos, boolean repeat) {
      int mapped_code = this.getMappedKeyCode((short)key_code);
      if(mapped_code < 0) {
         System.out.println("Unrecognized keycode: " + key_code);
      } else {
         if(this.key_states[mapped_code] == state) {
            repeat = true;
         }

         this.key_states[mapped_code] = state;
         int key_int_char = character & '\uffff';
         this.putKeyboardEvent(mapped_code, state, key_int_char, nanos, repeat);
      }
   }

   private int getMappedKeyCode(short key_code) {
      return this.nativeToLwjglMap.containsKey(Short.valueOf(key_code))?((Integer)this.nativeToLwjglMap.get(Short.valueOf(key_code))).intValue():-1;
   }

   public void keyPressed(int key_code, String chars, long nanos) {
      int character = chars != null && chars.length() != 0?chars.charAt(0):0;
      this.handleKey(key_code, (byte)1, character, nanos);
   }

   public void keyReleased(int key_code, String chars, long nanos) {
      int character = chars != null && chars.length() != 0?chars.charAt(0):0;
      this.handleKey(key_code, (byte)0, character, nanos);
   }

   public void keyTyped(KeyEvent e) {
   }
}
