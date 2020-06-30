package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.util.EnglishEnums;

public class StructuredDataMessage extends MapMessage {
   private static final long serialVersionUID = 1703221292892071920L;
   private static final int MAX_LENGTH = 32;
   private static final int HASHVAL = 31;
   private StructuredDataId id;
   private String message;
   private String type;

   public StructuredDataMessage(String id, String msg, String type) {
      this.id = new StructuredDataId(id, (String[])null, (String[])null);
      this.message = msg;
      this.type = type;
   }

   public StructuredDataMessage(String id, String msg, String type, Map data) {
      super(data);
      this.id = new StructuredDataId(id, (String[])null, (String[])null);
      this.message = msg;
      this.type = type;
   }

   public StructuredDataMessage(StructuredDataId id, String msg, String type) {
      this.id = id;
      this.message = msg;
      this.type = type;
   }

   public StructuredDataMessage(StructuredDataId id, String msg, String type, Map data) {
      super(data);
      this.id = id;
      this.message = msg;
      this.type = type;
   }

   private StructuredDataMessage(StructuredDataMessage msg, Map map) {
      super(map);
      this.id = msg.id;
      this.message = msg.message;
      this.type = msg.type;
   }

   protected StructuredDataMessage() {
   }

   public String[] getFormats() {
      String[] formats = new String[StructuredDataMessage.Format.values().length];
      int i = 0;

      for(StructuredDataMessage.Format format : StructuredDataMessage.Format.values()) {
         formats[i++] = format.name();
      }

      return formats;
   }

   public StructuredDataId getId() {
      return this.id;
   }

   protected void setId(String id) {
      this.id = new StructuredDataId(id, (String[])null, (String[])null);
   }

   protected void setId(StructuredDataId id) {
      this.id = id;
   }

   public String getType() {
      return this.type;
   }

   protected void setType(String type) {
      if(type.length() > 32) {
         throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + type);
      } else {
         this.type = type;
      }
   }

   public String getFormat() {
      return this.message;
   }

   protected void setMessageFormat(String msg) {
      this.message = msg;
   }

   protected void validate(String key, String value) {
      this.validateKey(key);
   }

   private void validateKey(String key) {
      if(key.length() > 32) {
         throw new IllegalArgumentException("Structured data keys are limited to 32 characters. key: " + key);
      } else {
         char[] chars = key.toCharArray();

         for(char c : chars) {
            if(c < 33 || c > 126 || c == 61 || c == 93 || c == 34) {
               throw new IllegalArgumentException("Structured data keys must contain printable US ASCII charactersand may not contain a space, =, ], or \"");
            }
         }

      }
   }

   public String asString() {
      return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
   }

   public String asString(String format) {
      try {
         return this.asString((StructuredDataMessage.Format)EnglishEnums.valueOf(StructuredDataMessage.Format.class, format), (StructuredDataId)null);
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   public final String asString(StructuredDataMessage.Format format, StructuredDataId structuredDataId) {
      StringBuilder sb = new StringBuilder();
      boolean full = StructuredDataMessage.Format.FULL.equals(format);
      if(full) {
         String type = this.getType();
         if(type == null) {
            return sb.toString();
         }

         sb.append(this.getType()).append(" ");
      }

      StructuredDataId id = this.getId();
      if(id != null) {
         id = id.makeId(structuredDataId);
      } else {
         id = structuredDataId;
      }

      if(id != null && id.getName() != null) {
         sb.append("[");
         sb.append(id);
         sb.append(" ");
         this.appendMap(sb);
         sb.append("]");
         if(full) {
            String msg = this.getFormat();
            if(msg != null) {
               sb.append(" ").append(msg);
            }
         }

         return sb.toString();
      } else {
         return sb.toString();
      }
   }

   public String getFormattedMessage() {
      return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
   }

   public String getFormattedMessage(String[] formats) {
      if(formats != null && formats.length > 0) {
         for(String format : formats) {
            if(StructuredDataMessage.Format.XML.name().equalsIgnoreCase(format)) {
               return this.asXML();
            }

            if(StructuredDataMessage.Format.FULL.name().equalsIgnoreCase(format)) {
               return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
            }
         }

         return this.asString((StructuredDataMessage.Format)null, (StructuredDataId)null);
      } else {
         return this.asString(StructuredDataMessage.Format.FULL, (StructuredDataId)null);
      }
   }

   private String asXML() {
      StringBuilder sb = new StringBuilder();
      StructuredDataId id = this.getId();
      if(id != null && id.getName() != null && this.type != null) {
         sb.append("<StructuredData>\n");
         sb.append("<type>").append(this.type).append("</type>\n");
         sb.append("<id>").append(id).append("</id>\n");
         super.asXML(sb);
         sb.append("</StructuredData>\n");
         return sb.toString();
      } else {
         return sb.toString();
      }
   }

   public String toString() {
      return this.asString((StructuredDataMessage.Format)null, (StructuredDataId)null);
   }

   public MapMessage newInstance(Map map) {
      return new StructuredDataMessage(this, map);
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         StructuredDataMessage that = (StructuredDataMessage)o;
         if(!super.equals(o)) {
            return false;
         } else {
            if(this.type != null) {
               if(!this.type.equals(that.type)) {
                  return false;
               }
            } else if(that.type != null) {
               return false;
            }

            if(this.id != null) {
               if(!this.id.equals(that.id)) {
                  return false;
               }
            } else if(that.id != null) {
               return false;
            }

            if(this.message != null) {
               if(!this.message.equals(that.message)) {
                  return false;
               }
            } else if(that.message != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.type != null?this.type.hashCode():0);
      result = 31 * result + (this.id != null?this.id.hashCode():0);
      result = 31 * result + (this.message != null?this.message.hashCode():0);
      return result;
   }

   public static enum Format {
      XML,
      FULL;
   }
}
