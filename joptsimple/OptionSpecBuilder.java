package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NoArgumentOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionalArgumentOptionSpec;
import joptsimple.RequiredArgumentOptionSpec;
import joptsimple.UnconfiguredOptionException;

public class OptionSpecBuilder extends NoArgumentOptionSpec {
   private final OptionParser parser;

   OptionSpecBuilder(OptionParser parser, Collection options, String description) {
      super(options, description);
      this.parser = parser;
      this.attachToParser();
   }

   private void attachToParser() {
      this.parser.recognize(this);
   }

   public ArgumentAcceptingOptionSpec withRequiredArg() {
      ArgumentAcceptingOptionSpec<String> newSpec = new RequiredArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public ArgumentAcceptingOptionSpec withOptionalArg() {
      ArgumentAcceptingOptionSpec<String> newSpec = new OptionalArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(newSpec);
      return newSpec;
   }

   public OptionSpecBuilder requiredIf(String dependent, String... otherDependents) {
      for(String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredIf(OptionSpec dependent, OptionSpec... otherDependents) {
      this.parser.requiredIf(this.options(), dependent);

      for(OptionSpec<?> each : otherDependents) {
         this.parser.requiredIf(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(String dependent, String... otherDependents) {
      for(String each : this.validatedDependents(dependent, otherDependents)) {
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(OptionSpec dependent, OptionSpec... otherDependents) {
      this.parser.requiredUnless(this.options(), dependent);

      for(OptionSpec<?> each : otherDependents) {
         this.parser.requiredUnless(this.options(), each);
      }

      return this;
   }

   private List validatedDependents(String dependent, String... otherDependents) {
      List<String> dependents = new ArrayList();
      dependents.add(dependent);
      Collections.addAll(dependents, otherDependents);

      for(String each : dependents) {
         if(!this.parser.isRecognized(each)) {
            throw new UnconfiguredOptionException(each);
         }
      }

      return dependents;
   }
}
