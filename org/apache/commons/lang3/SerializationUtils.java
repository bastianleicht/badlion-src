package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.SerializationException;

public class SerializationUtils {
   public static Serializable clone(Serializable object) {
      if(object == null) {
         return null;
      } else {
         byte[] objectData = serialize(object);
         ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
         SerializationUtils.ClassLoaderAwareObjectInputStream in = null;

         Serializable var5;
         try {
            in = new SerializationUtils.ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader());
            T readObject = (Serializable)in.readObject();
            var5 = readObject;
         } catch (ClassNotFoundException var14) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", var14);
         } catch (IOException var15) {
            throw new SerializationException("IOException while reading cloned object data", var15);
         } finally {
            try {
               if(in != null) {
                  in.close();
               }
            } catch (IOException var16) {
               throw new SerializationException("IOException on closing cloned object data InputStream.", var16);
            }

         }

         return var5;
      }
   }

   public static Serializable roundtrip(Serializable msg) {
      return (Serializable)deserialize(serialize(msg));
   }

   public static void serialize(Serializable obj, OutputStream outputStream) {
      if(outputStream == null) {
         throw new IllegalArgumentException("The OutputStream must not be null");
      } else {
         ObjectOutputStream out = null;

         try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
         } catch (IOException var11) {
            throw new SerializationException(var11);
         } finally {
            try {
               if(out != null) {
                  out.close();
               }
            } catch (IOException var10) {
               ;
            }

         }

      }
   }

   public static byte[] serialize(Serializable obj) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
      serialize(obj, baos);
      return baos.toByteArray();
   }

   public static Object deserialize(InputStream inputStream) {
      if(inputStream == null) {
         throw new IllegalArgumentException("The InputStream must not be null");
      } else {
         ObjectInputStream in = null;

         Object var3;
         try {
            in = new ObjectInputStream(inputStream);
            T obj = in.readObject();
            var3 = obj;
         } catch (ClassCastException var14) {
            throw new SerializationException(var14);
         } catch (ClassNotFoundException var15) {
            throw new SerializationException(var15);
         } catch (IOException var16) {
            throw new SerializationException(var16);
         } finally {
            try {
               if(in != null) {
                  in.close();
               }
            } catch (IOException var13) {
               ;
            }

         }

         return var3;
      }
   }

   public static Object deserialize(byte[] objectData) {
      if(objectData == null) {
         throw new IllegalArgumentException("The byte[] must not be null");
      } else {
         return deserialize((InputStream)(new ByteArrayInputStream(objectData)));
      }
   }

   static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
      private static final Map primitiveTypes = new HashMap();
      private final ClassLoader classLoader;

      public ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
         super(in);
         this.classLoader = classLoader;
         primitiveTypes.put("byte", Byte.TYPE);
         primitiveTypes.put("short", Short.TYPE);
         primitiveTypes.put("int", Integer.TYPE);
         primitiveTypes.put("long", Long.TYPE);
         primitiveTypes.put("float", Float.TYPE);
         primitiveTypes.put("double", Double.TYPE);
         primitiveTypes.put("boolean", Boolean.TYPE);
         primitiveTypes.put("char", Character.TYPE);
         primitiveTypes.put("void", Void.TYPE);
      }

      protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
         String name = desc.getName();

         try {
            return Class.forName(name, false, this.classLoader);
         } catch (ClassNotFoundException var7) {
            try {
               return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException var6) {
               Class<?> cls = (Class)primitiveTypes.get(name);
               if(cls != null) {
                  return cls;
               } else {
                  throw var6;
               }
            }
         }
      }
   }
}
