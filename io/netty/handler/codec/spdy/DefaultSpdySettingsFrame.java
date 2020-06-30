package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame {
   private boolean clear;
   private final Map settingsMap = new TreeMap();

   public Set ids() {
      return this.settingsMap.keySet();
   }

   public boolean isSet(int id) {
      Integer key = Integer.valueOf(id);
      return this.settingsMap.containsKey(key);
   }

   public int getValue(int id) {
      Integer key = Integer.valueOf(id);
      return this.settingsMap.containsKey(key)?((DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key)).getValue():-1;
   }

   public SpdySettingsFrame setValue(int id, int value) {
      return this.setValue(id, value, false, false);
   }

   public SpdySettingsFrame setValue(int id, int value, boolean persistValue, boolean persisted) {
      if(id >= 0 && id <= 16777215) {
         Integer key = Integer.valueOf(id);
         if(this.settingsMap.containsKey(key)) {
            DefaultSpdySettingsFrame.Setting setting = (DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key);
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
         } else {
            this.settingsMap.put(key, new DefaultSpdySettingsFrame.Setting(value, persistValue, persisted));
         }

         return this;
      } else {
         throw new IllegalArgumentException("Setting ID is not valid: " + id);
      }
   }

   public SpdySettingsFrame removeValue(int id) {
      Integer key = Integer.valueOf(id);
      if(this.settingsMap.containsKey(key)) {
         this.settingsMap.remove(key);
      }

      return this;
   }

   public boolean isPersistValue(int id) {
      Integer key = Integer.valueOf(id);
      return this.settingsMap.containsKey(key)?((DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key)).isPersist():false;
   }

   public SpdySettingsFrame setPersistValue(int id, boolean persistValue) {
      Integer key = Integer.valueOf(id);
      if(this.settingsMap.containsKey(key)) {
         ((DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key)).setPersist(persistValue);
      }

      return this;
   }

   public boolean isPersisted(int id) {
      Integer key = Integer.valueOf(id);
      return this.settingsMap.containsKey(key)?((DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key)).isPersisted():false;
   }

   public SpdySettingsFrame setPersisted(int id, boolean persisted) {
      Integer key = Integer.valueOf(id);
      if(this.settingsMap.containsKey(key)) {
         ((DefaultSpdySettingsFrame.Setting)this.settingsMap.get(key)).setPersisted(persisted);
      }

      return this;
   }

   public boolean clearPreviouslyPersistedSettings() {
      return this.clear;
   }

   public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
      this.clear = clear;
      return this;
   }

   private Set getSettings() {
      return this.settingsMap.entrySet();
   }

   private void appendSettings(StringBuilder buf) {
      for(Entry<Integer, DefaultSpdySettingsFrame.Setting> e : this.getSettings()) {
         DefaultSpdySettingsFrame.Setting setting = (DefaultSpdySettingsFrame.Setting)e.getValue();
         buf.append("--> ");
         buf.append(e.getKey());
         buf.append(':');
         buf.append(setting.getValue());
         buf.append(" (persist value: ");
         buf.append(setting.isPersist());
         buf.append("; persisted: ");
         buf.append(setting.isPersisted());
         buf.append(')');
         buf.append(StringUtil.NEWLINE);
      }

   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append(StringUtil.NEWLINE);
      this.appendSettings(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }

   private static final class Setting {
      private int value;
      private boolean persist;
      private boolean persisted;

      Setting(int value, boolean persist, boolean persisted) {
         this.value = value;
         this.persist = persist;
         this.persisted = persisted;
      }

      int getValue() {
         return this.value;
      }

      void setValue(int value) {
         this.value = value;
      }

      boolean isPersist() {
         return this.persist;
      }

      void setPersist(boolean persist) {
         this.persist = persist;
      }

      boolean isPersisted() {
         return this.persisted;
      }

      void setPersisted(boolean persisted) {
         this.persisted = persisted;
      }
   }
}
