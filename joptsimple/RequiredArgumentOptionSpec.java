package joptsimple;

import java.util.Collection;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionMissingRequiredArgumentException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

class RequiredArgumentOptionSpec extends ArgumentAcceptingOptionSpec {
   RequiredArgumentOptionSpec(String option) {
      super(option, true);
   }

   RequiredArgumentOptionSpec(Collection options, String description) {
      super(options, true, description);
   }

   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if(!arguments.hasMore()) {
         throw new OptionMissingRequiredArgumentException(this.options());
      } else {
         this.addArguments(detectedOptions, arguments.next());
      }
   }
}
