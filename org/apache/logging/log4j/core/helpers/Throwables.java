package org.apache.logging.log4j.core.helpers;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Throwables {
   public static List toStringList(Throwable throwable) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      try {
         throwable.printStackTrace(pw);
      } catch (RuntimeException var6) {
         ;
      }

      pw.flush();
      LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
      ArrayList<String> lines = new ArrayList();

      try {
         for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            lines.add(line);
         }
      } catch (IOException var7) {
         if(var7 instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
         }

         lines.add(var7.toString());
      }

      return lines;
   }
}
