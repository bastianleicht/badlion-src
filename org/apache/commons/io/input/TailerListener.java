package org.apache.commons.io.input;

import org.apache.commons.io.input.Tailer;

public interface TailerListener {
   void init(Tailer var1);

   void fileNotFound();

   void fileRotated();

   void handle(String var1);

   void handle(Exception var1);
}
