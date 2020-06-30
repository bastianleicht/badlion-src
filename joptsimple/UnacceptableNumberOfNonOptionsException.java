package joptsimple;

import java.util.Collections;
import joptsimple.OptionException;

class UnacceptableNumberOfNonOptionsException extends OptionException {
   private static final long serialVersionUID = -1L;
   private final int minimum;
   private final int maximum;
   private final int actual;

   UnacceptableNumberOfNonOptionsException(int minimum, int maximum, int actual) {
      super(Collections.singletonList("[arguments]"));
      this.minimum = minimum;
      this.maximum = maximum;
      this.actual = actual;
   }

   public String getMessage() {
      return String.format("actual = %d, minimum = %d, maximum = %d", new Object[]{Integer.valueOf(this.actual), Integer.valueOf(this.minimum), Integer.valueOf(this.maximum)});
   }
}
