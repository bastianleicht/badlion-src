package org.apache.commons.io.input;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class TailerListenerAdapter implements TailerListener {
   public void init(Tailer tailer) {
   }

   public void fileNotFound() {
   }

   public void fileRotated() {
   }

   public void handle(String line) {
   }

   public void handle(Exception ex) {
   }
}
