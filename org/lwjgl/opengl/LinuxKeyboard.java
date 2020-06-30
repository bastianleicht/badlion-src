package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.EventQueue;
import org.lwjgl.opengl.LinuxEvent;
import org.lwjgl.opengl.LinuxKeycodes;

final class LinuxKeyboard {
   private static final int LockMapIndex = 1;
   private static final long NoSymbol = 0L;
   private static final long ShiftMask = 1L;
   private static final long LockMask = 2L;
   private static final int XLookupChars = 2;
   private static final int XLookupBoth = 4;
   private static final int KEYBOARD_BUFFER_SIZE = 50;
   private final long xim;
   private final long xic;
   private final int numlock_mask;
   private final int modeswitch_mask;
   private final int caps_lock_mask;
   private final int shift_lock_mask;
   private final ByteBuffer compose_status;
   private final byte[] key_down_buffer = new byte[256];
   private final EventQueue event_queue = new EventQueue(18);
   private final ByteBuffer tmp_event = ByteBuffer.allocate(18);
   private final int[] temp_translation_buffer = new int[50];
   private final ByteBuffer native_translation_buffer = BufferUtils.createByteBuffer(50);
   private final CharsetDecoder utf8_decoder = Charset.forName("UTF-8").newDecoder();
   private final CharBuffer char_buffer = CharBuffer.allocate(50);
   private boolean has_deferred_event;
   private int deferred_keycode;
   private int deferred_event_keycode;
   private long deferred_nanos;
   private byte deferred_key_state;

   LinuxKeyboard(long display, long window) {
      long modifier_map = getModifierMapping(display);
      int tmp_numlock_mask = 0;
      int tmp_modeswitch_mask = 0;
      int tmp_caps_lock_mask = 0;
      int tmp_shift_lock_mask = 0;
      if(modifier_map != 0L) {
         int max_keypermod = getMaxKeyPerMod(modifier_map);

         for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < max_keypermod; ++j) {
               int key_code = lookupModifierMap(modifier_map, i * max_keypermod + j);
               int key_sym = (int)keycodeToKeySym(display, key_code);
               int mask = 1 << i;
               switch(key_sym) {
               case 65406:
                  tmp_modeswitch_mask |= mask;
                  break;
               case 65407:
                  tmp_numlock_mask |= mask;
                  break;
               case 65509:
                  if(i == 1) {
                     tmp_caps_lock_mask = mask;
                     tmp_shift_lock_mask = 0;
                  }
                  break;
               case 65510:
                  if(i == 1 && tmp_caps_lock_mask == 0) {
                     tmp_shift_lock_mask = mask;
                  }
               }
            }
         }

         freeModifierMapping(modifier_map);
      }

      this.numlock_mask = tmp_numlock_mask;
      this.modeswitch_mask = tmp_modeswitch_mask;
      this.caps_lock_mask = tmp_caps_lock_mask;
      this.shift_lock_mask = tmp_shift_lock_mask;
      setDetectableKeyRepeat(display, true);
      this.xim = openIM(display);
      if(this.xim != 0L) {
         this.xic = createIC(this.xim, window);
         if(this.xic != 0L) {
            setupIMEventMask(display, window, this.xic);
         } else {
            this.destroy(display);
         }
      } else {
         this.xic = 0L;
      }

      this.compose_status = allocateComposeStatus();
   }

   private static native long getModifierMapping(long var0);

   private static native void freeModifierMapping(long var0);

   private static native int getMaxKeyPerMod(long var0);

   private static native int lookupModifierMap(long var0, int var2);

   private static native long keycodeToKeySym(long var0, int var2);

   private static native long openIM(long var0);

   private static native long createIC(long var0, long var2);

   private static native void setupIMEventMask(long var0, long var2, long var4);

   private static native ByteBuffer allocateComposeStatus();

   private static void setDetectableKeyRepeat(long display, boolean enabled) {
      boolean success = nSetDetectableKeyRepeat(display, enabled);
      if(!success) {
         LWJGLUtil.log("Failed to set detectable key repeat to " + enabled);
      }

   }

   private static native boolean nSetDetectableKeyRepeat(long var0, boolean var2);

   public void destroy(long display) {
      if(this.xic != 0L) {
         destroyIC(this.xic);
      }

      if(this.xim != 0L) {
         closeIM(this.xim);
      }

      setDetectableKeyRepeat(display, false);
   }

   private static native void destroyIC(long var0);

   private static native void closeIM(long var0);

   public void read(ByteBuffer buffer) {
      this.flushDeferredEvent();
      this.event_queue.copyEvents(buffer);
   }

   public void poll(ByteBuffer keyDownBuffer) {
      this.flushDeferredEvent();
      int old_position = keyDownBuffer.position();
      keyDownBuffer.put(this.key_down_buffer);
      keyDownBuffer.position(old_position);
   }

   private void putKeyboardEvent(int keycode, byte state, int ch, long nanos, boolean repeat) {
      this.tmp_event.clear();
      this.tmp_event.putInt(keycode).put(state).putInt(ch).putLong(nanos).put((byte)(repeat?1:0));
      this.tmp_event.flip();
      this.event_queue.putEvent(this.tmp_event);
   }

   private int lookupStringISO88591(long event_ptr, int[] translation_buffer) {
      int num_chars = lookupString(event_ptr, this.native_translation_buffer, this.compose_status);

      for(int i = 0; i < num_chars; ++i) {
         translation_buffer[i] = this.native_translation_buffer.get(i) & 255;
      }

      return num_chars;
   }

   private static native int lookupString(long var0, ByteBuffer var2, ByteBuffer var3);

   private int lookupStringUnicode(long event_ptr, int[] translation_buffer) {
      int status = utf8LookupString(this.xic, event_ptr, this.native_translation_buffer, this.native_translation_buffer.position(), this.native_translation_buffer.remaining());
      if(status != 2 && status != 4) {
         return 0;
      } else {
         this.native_translation_buffer.flip();
         this.utf8_decoder.decode(this.native_translation_buffer, this.char_buffer, true);
         this.native_translation_buffer.compact();
         this.char_buffer.flip();

         int i;
         for(i = 0; this.char_buffer.hasRemaining() && i < translation_buffer.length; translation_buffer[i++] = this.char_buffer.get()) {
            ;
         }

         this.char_buffer.compact();
         return i;
      }
   }

   private static native int utf8LookupString(long var0, long var2, ByteBuffer var4, int var5, int var6);

   private int lookupString(long event_ptr, int[] translation_buffer) {
      return this.xic != 0L?this.lookupStringUnicode(event_ptr, translation_buffer):this.lookupStringISO88591(event_ptr, translation_buffer);
   }

   private void translateEvent(long event_ptr, int keycode, byte key_state, long nanos, boolean repeat) {
      int num_chars = this.lookupString(event_ptr, this.temp_translation_buffer);
      if(num_chars > 0) {
         int ch = this.temp_translation_buffer[0];
         this.putKeyboardEvent(keycode, key_state, ch, nanos, repeat);

         for(int i = 1; i < num_chars; ++i) {
            ch = this.temp_translation_buffer[i];
            this.putKeyboardEvent(0, (byte)0, ch, nanos, repeat);
         }
      } else {
         this.putKeyboardEvent(keycode, key_state, 0, nanos, repeat);
      }

   }

   private static boolean isKeypadKeysym(long keysym) {
      return 65408L <= keysym && keysym <= 65469L || 285212672L <= keysym && keysym <= 285278207L;
   }

   private static boolean isNoSymbolOrVendorSpecific(long keysym) {
      return keysym == 0L || (keysym & 268435456L) != 0L;
   }

   private static long getKeySym(long event_ptr, int group, int index) {
      long keysym = lookupKeysym(event_ptr, group * 2 + index);
      if(isNoSymbolOrVendorSpecific(keysym) && index == 1) {
         keysym = lookupKeysym(event_ptr, group * 2 + 0);
      }

      if(isNoSymbolOrVendorSpecific(keysym) && group == 1) {
         keysym = getKeySym(event_ptr, 0, index);
      }

      return keysym;
   }

   private static native long lookupKeysym(long var0, int var2);

   private static native long toUpper(long var0);

   private long mapEventToKeySym(long event_ptr, int event_state) {
      int group;
      if((event_state & this.modeswitch_mask) != 0) {
         group = 1;
      } else {
         group = 0;
      }

      long keysym;
      if((event_state & this.numlock_mask) != 0 && isKeypadKeysym(keysym = getKeySym(event_ptr, group, 1))) {
         return ((long)event_state & (1L | (long)this.shift_lock_mask)) != 0L?getKeySym(event_ptr, group, 0):keysym;
      } else if(((long)event_state & 3L) == 0L) {
         return getKeySym(event_ptr, group, 0);
      } else if(((long)event_state & 1L) == 0L) {
         keysym = getKeySym(event_ptr, group, 0);
         if((event_state & this.caps_lock_mask) != 0) {
            keysym = toUpper(keysym);
         }

         return keysym;
      } else {
         keysym = getKeySym(event_ptr, group, 1);
         if((event_state & this.caps_lock_mask) != 0) {
            keysym = toUpper(keysym);
         }

         return keysym;
      }
   }

   private int getKeycode(long event_ptr, int event_state) {
      long keysym = this.mapEventToKeySym(event_ptr, event_state);
      int keycode = LinuxKeycodes.mapKeySymToLWJGLKeyCode(keysym);
      if(keycode == 0) {
         keysym = lookupKeysym(event_ptr, 0);
         keycode = LinuxKeycodes.mapKeySymToLWJGLKeyCode(keysym);
      }

      return keycode;
   }

   private static byte getKeyState(int event_type) {
      switch(event_type) {
      case 2:
         return (byte)1;
      case 3:
         return (byte)0;
      default:
         throw new IllegalArgumentException("Unknown event_type: " + event_type);
      }
   }

   void releaseAll() {
      for(int i = 0; i < this.key_down_buffer.length; ++i) {
         if(this.key_down_buffer[i] != 0) {
            this.key_down_buffer[i] = 0;
            this.putKeyboardEvent(i, (byte)0, 0, 0L, false);
         }
      }

   }

   private void handleKeyEvent(long event_ptr, long millis, int event_type, int event_keycode, int event_state) {
      int keycode = this.getKeycode(event_ptr, event_state);
      byte key_state = getKeyState(event_type);
      boolean repeat = key_state == this.key_down_buffer[keycode];
      if(!repeat || event_type != 3) {
         this.key_down_buffer[keycode] = key_state;
         long nanos = millis * 1000000L;
         if(event_type == 2) {
            if(this.has_deferred_event) {
               if(nanos == this.deferred_nanos && event_keycode == this.deferred_event_keycode) {
                  this.has_deferred_event = false;
                  repeat = true;
               } else {
                  this.flushDeferredEvent();
               }
            }

            this.translateEvent(event_ptr, keycode, key_state, nanos, repeat);
         } else {
            this.flushDeferredEvent();
            this.has_deferred_event = true;
            this.deferred_keycode = keycode;
            this.deferred_event_keycode = event_keycode;
            this.deferred_nanos = nanos;
            this.deferred_key_state = key_state;
         }

      }
   }

   private void flushDeferredEvent() {
      if(this.has_deferred_event) {
         this.putKeyboardEvent(this.deferred_keycode, this.deferred_key_state, 0, this.deferred_nanos, false);
         this.has_deferred_event = false;
      }

   }

   public boolean filterEvent(LinuxEvent event) {
      switch(event.getType()) {
      case 2:
      case 3:
         this.handleKeyEvent(event.getKeyAddress(), event.getKeyTime(), event.getKeyType(), event.getKeyKeyCode(), event.getKeyState());
         return true;
      default:
         return false;
      }
   }
}
