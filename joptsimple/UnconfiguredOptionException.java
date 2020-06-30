package joptsimple;

import java.util.Collection;
import java.util.Collections;
import joptsimple.OptionException;

class UnconfiguredOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnconfiguredOptionException(String option) {
      this((Collection)Collections.singletonList(option));
   }

   UnconfiguredOptionException(Collection options) {
      super(options);
   }

   public String getMessage() {
      return "Option " + this.multipleOptionMessage() + " has not been configured on this parser";
   }
}
