package org.lwjgl.util.jinput;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Keyboard;
import net.java.games.input.Rumbler;
import org.lwjgl.util.jinput.KeyMap;

final class LWJGLKeyboard extends Keyboard {
   LWJGLKeyboard() {
      super("LWJGLKeyboard", createComponents(), new Controller[0], new Rumbler[0]);
   }

   private static Component[] createComponents() {
      List<LWJGLKeyboard.Key> components = new ArrayList();
      Field[] vkey_fields = org.lwjgl.input.Keyboard.class.getFields();

      for(Field vkey_field : vkey_fields) {
         try {
            if(Modifier.isStatic(vkey_field.getModifiers()) && vkey_field.getType() == Integer.TYPE && vkey_field.getName().startsWith("KEY_")) {
               int vkey_code = vkey_field.getInt((Object)null);
               Component.Identifier.Key key_id = KeyMap.map(vkey_code);
               if(key_id != Component.Identifier.Key.UNKNOWN) {
                  components.add(new LWJGLKeyboard.Key(key_id, vkey_code));
               }
            }
         } catch (IllegalAccessException var8) {
            throw new RuntimeException(var8);
         }
      }

      return (Component[])components.toArray(new Component[components.size()]);
   }

   public synchronized void pollDevice() throws IOException {
      if(org.lwjgl.input.Keyboard.isCreated()) {
         org.lwjgl.input.Keyboard.poll();

         for(Component component : this.getComponents()) {
            LWJGLKeyboard.Key key = (LWJGLKeyboard.Key)component;
            key.update();
         }

      }
   }

   protected synchronized boolean getNextDeviceEvent(Event event) throws IOException {
      if(!org.lwjgl.input.Keyboard.isCreated()) {
         return false;
      } else if(!org.lwjgl.input.Keyboard.next()) {
         return false;
      } else {
         int lwjgl_key = org.lwjgl.input.Keyboard.getEventKey();
         if(lwjgl_key == 0) {
            return false;
         } else {
            Component.Identifier.Key key_id = KeyMap.map(lwjgl_key);
            if(key_id == null) {
               return false;
            } else {
               Component key = this.getComponent(key_id);
               if(key == null) {
                  return false;
               } else {
                  float value = org.lwjgl.input.Keyboard.getEventKeyState()?1.0F:0.0F;
                  event.set(key, value, org.lwjgl.input.Keyboard.getEventNanoseconds());
                  return true;
               }
            }
         }
      }
   }

   private static final class Key extends AbstractComponent {
      private final int lwjgl_key;
      private float value;

      Key(Component.Identifier.Key key_id, int lwjgl_key) {
         super(key_id.getName(), key_id);
         this.lwjgl_key = lwjgl_key;
      }

      public void update() {
         this.value = org.lwjgl.input.Keyboard.isKeyDown(this.lwjgl_key)?1.0F:0.0F;
      }

      protected float poll() {
         return this.value;
      }

      public boolean isRelative() {
         return false;
      }

      public boolean isAnalog() {
         return false;
      }
   }
}
