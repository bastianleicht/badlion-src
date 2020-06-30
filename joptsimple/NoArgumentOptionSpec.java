package joptsimple;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

class NoArgumentOptionSpec extends AbstractOptionSpec {
   NoArgumentOptionSpec(String option) {
      this(Collections.singletonList(option), "");
   }

   NoArgumentOptionSpec(Collection options, String description) {
      super(options, description);
   }

   void handleOption(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument) {
      detectedOptions.add(this);
   }

   public boolean acceptsArguments() {
      return false;
   }

   public boolean requiresArgument() {
      return false;
   }

   public boolean isRequired() {
      return false;
   }

   public String argumentDescription() {
      return "";
   }

   public String argumentTypeIndicator() {
      return "";
   }

   protected Void convert(String argument) {
      return null;
   }

   public List defaultValues() {
      return Collections.emptyList();
   }
}
