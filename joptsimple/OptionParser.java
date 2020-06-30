package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import joptsimple.AbstractOptionSpec;
import joptsimple.AlternativeLongOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.HelpFormatter;
import joptsimple.MissingRequiredOptionException;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionDeclarer;
import joptsimple.OptionException;
import joptsimple.OptionParserState;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import joptsimple.OptionSpecTokenizer;
import joptsimple.ParserRules;
import joptsimple.UnconfiguredOptionException;
import joptsimple.internal.AbbreviationMap;
import joptsimple.util.KeyValuePair;

public class OptionParser implements OptionDeclarer {
   private final AbbreviationMap recognizedOptions;
   private final Map requiredIf;
   private final Map requiredUnless;
   private OptionParserState state;
   private boolean posixlyCorrect;
   private boolean allowsUnrecognizedOptions;
   private HelpFormatter helpFormatter;

   public OptionParser() {
      this.helpFormatter = new BuiltinHelpFormatter();
      this.recognizedOptions = new AbbreviationMap();
      this.requiredIf = new HashMap();
      this.requiredUnless = new HashMap();
      this.state = OptionParserState.moreOptions(false);
      this.recognize(new NonOptionArgumentSpec());
   }

   public OptionParser(String optionSpecification) {
      this();
      (new OptionSpecTokenizer(optionSpecification)).configure(this);
   }

   public OptionSpecBuilder accepts(String option) {
      return this.acceptsAll(Collections.singletonList(option));
   }

   public OptionSpecBuilder accepts(String option, String description) {
      return this.acceptsAll(Collections.singletonList(option), description);
   }

   public OptionSpecBuilder acceptsAll(Collection options) {
      return this.acceptsAll(options, "");
   }

   public OptionSpecBuilder acceptsAll(Collection options, String description) {
      if(options.isEmpty()) {
         throw new IllegalArgumentException("need at least one option");
      } else {
         ParserRules.ensureLegalOptions(options);
         return new OptionSpecBuilder(this, options, description);
      }
   }

   public NonOptionArgumentSpec nonOptions() {
      NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec();
      this.recognize(spec);
      return spec;
   }

   public NonOptionArgumentSpec nonOptions(String description) {
      NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec(description);
      this.recognize(spec);
      return spec;
   }

   public void posixlyCorrect(boolean setting) {
      this.posixlyCorrect = setting;
      this.state = OptionParserState.moreOptions(setting);
   }

   boolean posixlyCorrect() {
      return this.posixlyCorrect;
   }

   public void allowsUnrecognizedOptions() {
      this.allowsUnrecognizedOptions = true;
   }

   boolean doesAllowsUnrecognizedOptions() {
      return this.allowsUnrecognizedOptions;
   }

   public void recognizeAlternativeLongOptions(boolean recognize) {
      if(recognize) {
         this.recognize(new AlternativeLongOptionSpec());
      } else {
         this.recognizedOptions.remove(String.valueOf("W"));
      }

   }

   void recognize(AbstractOptionSpec spec) {
      this.recognizedOptions.putAll(spec.options(), spec);
   }

   public void printHelpOn(OutputStream sink) throws IOException {
      this.printHelpOn((Writer)(new OutputStreamWriter(sink)));
   }

   public void printHelpOn(Writer sink) throws IOException {
      sink.write(this.helpFormatter.format(this.recognizedOptions.toJavaUtilMap()));
      sink.flush();
   }

   public void formatHelpWith(HelpFormatter formatter) {
      if(formatter == null) {
         throw new NullPointerException();
      } else {
         this.helpFormatter = formatter;
      }
   }

   public Map recognizedOptions() {
      return new HashMap(this.recognizedOptions.toJavaUtilMap());
   }

   public OptionSet parse(String... arguments) {
      ArgumentList argumentList = new ArgumentList(arguments);
      OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
      detected.add((AbstractOptionSpec)this.recognizedOptions.get("[arguments]"));

      while(argumentList.hasMore()) {
         this.state.handleArgument(this, argumentList, detected);
      }

      this.reset();
      this.ensureRequiredOptions(detected);
      return detected;
   }

   private void ensureRequiredOptions(OptionSet options) {
      Collection<String> missingRequiredOptions = this.missingRequiredOptions(options);
      boolean helpOptionPresent = this.isHelpOptionPresent(options);
      if(!missingRequiredOptions.isEmpty() && !helpOptionPresent) {
         throw new MissingRequiredOptionException(missingRequiredOptions);
      }
   }

   private Collection missingRequiredOptions(OptionSet options) {
      Collection<String> missingRequiredOptions = new HashSet();

      for(AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
         if(each.isRequired() && !options.has((OptionSpec)each)) {
            missingRequiredOptions.addAll(each.options());
         }
      }

      for(Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : this.requiredIf.entrySet()) {
         AbstractOptionSpec<?> required = this.specFor((String)((Collection)eachEntry.getKey()).iterator().next());
         if(this.optionsHasAnyOf(options, (Collection)eachEntry.getValue()) && !options.has((OptionSpec)required)) {
            missingRequiredOptions.addAll(required.options());
         }
      }

      for(Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : this.requiredUnless.entrySet()) {
         AbstractOptionSpec<?> required = this.specFor((String)((Collection)eachEntry.getKey()).iterator().next());
         if(!this.optionsHasAnyOf(options, (Collection)eachEntry.getValue()) && !options.has((OptionSpec)required)) {
            missingRequiredOptions.addAll(required.options());
         }
      }

      return missingRequiredOptions;
   }

   private boolean optionsHasAnyOf(OptionSet options, Collection specs) {
      for(OptionSpec<?> each : specs) {
         if(options.has(each)) {
            return true;
         }
      }

      return false;
   }

   private boolean isHelpOptionPresent(OptionSet options) {
      boolean helpOptionPresent = false;

      for(AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
         if(each.isForHelp() && options.has((OptionSpec)each)) {
            helpOptionPresent = true;
            break;
         }
      }

      return helpOptionPresent;
   }

   void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
      if(!this.isRecognized(optionAndArgument.key)) {
         throw OptionException.unrecognizedOption(optionAndArgument.key);
      } else {
         AbstractOptionSpec<?> optionSpec = this.specFor(optionAndArgument.key);
         optionSpec.handleOption(this, arguments, detected, optionAndArgument.value);
      }
   }

   void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
      KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
      if(this.isRecognized(optionAndArgument.key)) {
         this.specFor(optionAndArgument.key).handleOption(this, arguments, detected, optionAndArgument.value);
      } else {
         this.handleShortOptionCluster(candidate, arguments, detected);
      }

   }

   private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
      char[] options = extractShortOptionsFrom(candidate);
      this.validateOptionCharacters(options);

      for(int i = 0; i < options.length; ++i) {
         AbstractOptionSpec<?> optionSpec = this.specFor(options[i]);
         if(optionSpec.acceptsArguments() && options.length > i + 1) {
            String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
            optionSpec.handleOption(this, arguments, detected, detectedArgument);
            break;
         }

         optionSpec.handleOption(this, arguments, detected, (String)null);
      }

   }

   void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
      this.specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
   }

   void noMoreOptions() {
      this.state = OptionParserState.noMoreOptions();
   }

   boolean looksLikeAnOption(String argument) {
      return ParserRules.isShortOptionToken(argument) || ParserRules.isLongOptionToken(argument);
   }

   boolean isRecognized(String option) {
      return this.recognizedOptions.contains(option);
   }

   void requiredIf(Collection precedentSynonyms, String required) {
      this.requiredIf(precedentSynonyms, (OptionSpec)this.specFor(required));
   }

   void requiredIf(Collection precedentSynonyms, OptionSpec required) {
      this.putRequiredOption(precedentSynonyms, required, this.requiredIf);
   }

   void requiredUnless(Collection precedentSynonyms, String required) {
      this.requiredUnless(precedentSynonyms, (OptionSpec)this.specFor(required));
   }

   void requiredUnless(Collection precedentSynonyms, OptionSpec required) {
      this.putRequiredOption(precedentSynonyms, required, this.requiredUnless);
   }

   private void putRequiredOption(Collection precedentSynonyms, OptionSpec required, Map target) {
      for(String each : precedentSynonyms) {
         AbstractOptionSpec<?> spec = this.specFor(each);
         if(spec == null) {
            throw new UnconfiguredOptionException(precedentSynonyms);
         }
      }

      Set<OptionSpec<?>> associated = (Set)target.get(precedentSynonyms);
      if(associated == null) {
         associated = new HashSet();
         target.put(precedentSynonyms, associated);
      }

      associated.add(required);
   }

   private AbstractOptionSpec specFor(char option) {
      return this.specFor(String.valueOf(option));
   }

   private AbstractOptionSpec specFor(String option) {
      return (AbstractOptionSpec)this.recognizedOptions.get(option);
   }

   private void reset() {
      this.state = OptionParserState.moreOptions(this.posixlyCorrect);
   }

   private static char[] extractShortOptionsFrom(String argument) {
      char[] options = new char[argument.length() - 1];
      argument.getChars(1, argument.length(), options, 0);
      return options;
   }

   private void validateOptionCharacters(char[] options) {
      for(char each : options) {
         String option = String.valueOf(each);
         if(!this.isRecognized(option)) {
            throw OptionException.unrecognizedOption(option);
         }

         if(this.specFor(option).acceptsArguments()) {
            return;
         }
      }

   }

   private static KeyValuePair parseLongOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(2));
   }

   private static KeyValuePair parseShortOptionWithArgument(String argument) {
      return KeyValuePair.valueOf(argument.substring(1));
   }
}
