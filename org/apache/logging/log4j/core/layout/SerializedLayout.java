package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractLayout;

@Plugin(
   name = "SerializedLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class SerializedLayout extends AbstractLayout {
   private static byte[] header;

   public byte[] toByteArray(LogEvent event) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         ObjectOutputStream oos = new SerializedLayout.PrivateObjectOutputStream(baos);

         try {
            oos.writeObject(event);
            oos.reset();
         } finally {
            oos.close();
         }
      } catch (IOException var8) {
         LOGGER.error((String)"Serialization of LogEvent failed.", (Throwable)var8);
      }

      return baos.toByteArray();
   }

   public LogEvent toSerializable(LogEvent event) {
      return event;
   }

   @PluginFactory
   public static SerializedLayout createLayout() {
      return new SerializedLayout();
   }

   public byte[] getHeader() {
      return header;
   }

   public Map getContentFormat() {
      return new HashMap();
   }

   public String getContentType() {
      return "application/octet-stream";
   }

   static {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.close();
         header = baos.toByteArray();
      } catch (Exception var2) {
         LOGGER.error((String)"Unable to generate Object stream header", (Throwable)var2);
      }

   }

   private class PrivateObjectOutputStream extends ObjectOutputStream {
      public PrivateObjectOutputStream(OutputStream os) throws IOException {
         super(os);
      }

      protected void writeStreamHeader() {
      }
   }
}
