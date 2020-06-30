package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import joptsimple.UnrecognizedOptionException;

public abstract class OptionException extends RuntimeException {
   private static final long serialVersionUID = -1L;
   private final List options = new ArrayList();

   protected OptionException(Collection options) {
      this.options.addAll(options);
   }

   protected OptionException(Collection options, Throwable cause) {
      super(cause);
      this.options.addAll(options);
   }

   public Collection options() {
      return Collections.unmodifiableCollection(this.options);
   }

   protected final String singleOptionMessage() {
      return this.singleOptionMessage((String)this.options.get(0));
   }

   protected final String singleOptionMessage(String option) {
      return "\'" + option + "\'";
   }

   protected final String multipleOptionMessage() {
      StringBuilder buffer = new StringBuilder("[");
      Iterator<String> iter = this.options.iterator();

      while(iter.hasNext()) {
         buffer.append(this.singleOptionMessage((String)iter.next()));
         if(iter.hasNext()) {
            buffer.append(", ");
         }
      }

      buffer.append(']');
      return buffer.toString();
   }

   static OptionException unrecognizedOption(String option) {
      return new UnrecognizedOptionException(option);
   }
}
