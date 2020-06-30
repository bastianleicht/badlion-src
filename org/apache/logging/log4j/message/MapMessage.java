package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.util.EnglishEnums;

public class MapMessage implements MultiformatMessage {
   private static final long serialVersionUID = -5031471831131487120L;
   private final SortedMap data;

   public MapMessage() {
      this.data = new TreeMap();
   }

   public MapMessage(Map map) {
      this.data = (SortedMap)(map instanceof SortedMap?(SortedMap)map:new TreeMap(map));
   }

   public String[] getFormats() {
      String[] formats = new String[MapMessage.MapFormat.values().length];
      int i = 0;

      for(MapMessage.MapFormat format : MapMessage.MapFormat.values()) {
         formats[i++] = format.name();
      }

      return formats;
   }

   public Object[] getParameters() {
      return this.data.values().toArray();
   }

   public String getFormat() {
      return "";
   }

   public Map getData() {
      return Collections.unmodifiableMap(this.data);
   }

   public void clear() {
      this.data.clear();
   }

   public void put(String key, String value) {
      if(value == null) {
         throw new IllegalArgumentException("No value provided for key " + key);
      } else {
         this.validate(key, value);
         this.data.put(key, value);
      }
   }

   protected void validate(String key, String value) {
   }

   public void putAll(Map map) {
      this.data.putAll(map);
   }

   public String get(String key) {
      return (String)this.data.get(key);
   }

   public String remove(String key) {
      return (String)this.data.remove(key);
   }

   public String asString() {
      return this.asString((MapMessage.MapFormat)null);
   }

   public String asString(String format) {
      try {
         return this.asString((MapMessage.MapFormat)EnglishEnums.valueOf(MapMessage.MapFormat.class, format));
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   private String asString(MapMessage.MapFormat format) {
      StringBuilder sb = new StringBuilder();
      if(format == null) {
         this.appendMap(sb);
      } else {
         switch(format) {
         case XML:
            this.asXML(sb);
            break;
         case JSON:
            this.asJSON(sb);
            break;
         case JAVA:
            this.asJava(sb);
            break;
         default:
            this.appendMap(sb);
         }
      }

      return sb.toString();
   }

   public void asXML(StringBuilder sb) {
      sb.append("<Map>\n");

      for(Entry<String, String> entry : this.data.entrySet()) {
         sb.append("  <Entry key=\"").append((String)entry.getKey()).append("\">").append((String)entry.getValue()).append("</Entry>\n");
      }

      sb.append("</Map>");
   }

   public String getFormattedMessage() {
      return this.asString();
   }

   public String getFormattedMessage(String[] formats) {
      if(formats != null && formats.length != 0) {
         for(String format : formats) {
            for(MapMessage.MapFormat mapFormat : MapMessage.MapFormat.values()) {
               if(mapFormat.name().equalsIgnoreCase(format)) {
                  return this.asString(mapFormat);
               }
            }
         }

         return this.asString();
      } else {
         return this.asString();
      }
   }

   protected void appendMap(StringBuilder sb) {
      boolean first = true;

      for(Entry<String, String> entry : this.data.entrySet()) {
         if(!first) {
            sb.append(" ");
         }

         first = false;
         sb.append((String)entry.getKey()).append("=\"").append((String)entry.getValue()).append("\"");
      }

   }

   protected void asJSON(StringBuilder sb) {
      boolean first = true;
      sb.append("{");

      for(Entry<String, String> entry : this.data.entrySet()) {
         if(!first) {
            sb.append(", ");
         }

         first = false;
         sb.append("\"").append((String)entry.getKey()).append("\":");
         sb.append("\"").append((String)entry.getValue()).append("\"");
      }

      sb.append("}");
   }

   protected void asJava(StringBuilder sb) {
      boolean first = true;
      sb.append("{");

      for(Entry<String, String> entry : this.data.entrySet()) {
         if(!first) {
            sb.append(", ");
         }

         first = false;
         sb.append((String)entry.getKey()).append("=\"").append((String)entry.getValue()).append("\"");
      }

      sb.append("}");
   }

   public MapMessage newInstance(Map map) {
      return new MapMessage(map);
   }

   public String toString() {
      return this.asString();
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         MapMessage that = (MapMessage)o;
         return this.data.equals(that.data);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public Throwable getThrowable() {
      return null;
   }

   public static enum MapFormat {
      XML,
      JSON,
      JAVA;
   }
}
