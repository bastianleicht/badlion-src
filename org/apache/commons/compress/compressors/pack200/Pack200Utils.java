package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;

public class Pack200Utils {
   public static void normalize(File jar) throws IOException {
      normalize(jar, jar, (Map)null);
   }

   public static void normalize(File jar, Map props) throws IOException {
      normalize(jar, jar, props);
   }

   public static void normalize(File from, File to) throws IOException {
      normalize(from, to, (Map)null);
   }

   public static void normalize(File from, File to, Map props) throws IOException {
      if(props == null) {
         props = new HashMap();
      }

      ((Map)props).put("pack.segment.limit", "-1");
      File f = File.createTempFile("commons-compress", "pack200normalize");
      f.deleteOnExit();

      try {
         OutputStream os = new FileOutputStream(f);
         JarFile j = null;

         try {
            Packer p = Pack200.newPacker();
            p.properties().putAll((Map)props);
            p.pack(new JarFile(from), os);
            j = null;
            os.close();
            os = null;
            Unpacker u = Pack200.newUnpacker();
            os = new JarOutputStream(new FileOutputStream(to));
            u.unpack(f, (JarOutputStream)os);
         } finally {
            if(j != null) {
               j.close();
            }

            if(os != null) {
               os.close();
            }

         }
      } finally {
         f.delete();
      }

   }
}
