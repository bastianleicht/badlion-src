package joptsimple;

import java.util.Collection;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

class OptionalArgumentOptionSpec extends ArgumentAcceptingOptionSpec {
   OptionalArgumentOptionSpec(String option) {
      super(option, false);
   }

   OptionalArgumentOptionSpec(Collection options, String description) {
      super(options, false, description);
   }

   protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
      if(arguments.hasMore()) {
         String nextArgument = arguments.peek();
         if(!parser.looksLikeAnOption(nextArgument)) {
            this.handleOptionArgument(parser, detectedOptions, arguments);
         } else if(this.isArgumentOfNumberType() && this.canConvertArgument(nextArgument)) {
            this.addArguments(detectedOptions, arguments.next());
         } else {
            detectedOptions.add(this);
         }
      } else {
         detectedOptions.add(this);
      }

   }

   private void handleOptionArgument(OptionParser parser, OptionSet detectedOptions, ArgumentList arguments) {
      if(parser.posixlyCorrect()) {
         detectedOptions.add(this);
         parser.noMoreOptions();
      } else {
         this.addArguments(detectedOptions, arguments.next());
      }

   }
}
