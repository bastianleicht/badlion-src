package org.apache.commons.compress.archivers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public final class Lister {
   private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

   public static void main(String[] args) throws Exception {
      if(args.length == 0) {
         usage();
      } else {
         System.out.println("Analysing " + args[0]);
         File f = new File(args[0]);
         if(!f.isFile()) {
            System.err.println(f + " doesn\'t exist or is a directory");
         }

         InputStream fis = new BufferedInputStream(new FileInputStream(f));
         ArchiveInputStream ais;
         if(args.length > 1) {
            ais = factory.createArchiveInputStream(args[1], fis);
         } else {
            ais = factory.createArchiveInputStream(fis);
         }

         System.out.println("Created " + ais.toString());

         ArchiveEntry ae;
         while((ae = ais.getNextEntry()) != null) {
            System.out.println(ae.getName());
         }

         ais.close();
         fis.close();
      }
   }

   private static void usage() {
      System.out.println("Parameters: archive-name [archive-type]");
   }
}
