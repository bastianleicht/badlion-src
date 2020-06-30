package joptsimple;

import java.util.Collections;
import joptsimple.OptionException;

class UnrecognizedOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnrecognizedOptionException(String option) {
      super(Collections.singletonList(option));
   }

   public String getMessage() {
      return this.singleOptionMessage() + " is not a recognized option";
   }
}
