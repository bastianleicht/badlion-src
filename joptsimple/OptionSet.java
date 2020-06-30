package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.AbstractOptionSpec;
import joptsimple.MultipleArgumentsForOptionException;
import joptsimple.OptionSpec;
import joptsimple.internal.Objects;

public class OptionSet {
   private final List detectedSpecs = new ArrayList();
   private final Map detectedOptions = new HashMap();
   private final Map optionsToArguments = new IdentityHashMap();
   private final Map recognizedSpecs;
   private final Map defaultValues;

   OptionSet(Map recognizedSpecs) {
      this.defaultValues = defaultValues(recognizedSpecs);
      this.recognizedSpecs = recognizedSpecs;
   }

   public boolean hasOptions() {
      return !this.detectedOptions.isEmpty();
   }

   public boolean has(String option) {
      return this.detectedOptions.containsKey(option);
   }

   public boolean has(OptionSpec option) {
      return this.optionsToArguments.containsKey(option);
   }

   public boolean hasArgument(String option) {
      AbstractOptionSpec<?> spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      return spec != null && this.hasArgument((OptionSpec)spec);
   }

   public boolean hasArgument(OptionSpec option) {
      Objects.ensureNotNull(option);
      List<String> values = (List)this.optionsToArguments.get(option);
      return values != null && !values.isEmpty();
   }

   public Object valueOf(String option) {
      Objects.ensureNotNull(option);
      AbstractOptionSpec<?> spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      if(spec == null) {
         List<?> defaults = this.defaultValuesFor(option);
         return defaults.isEmpty()?null:defaults.get(0);
      } else {
         return this.valueOf((OptionSpec)spec);
      }
   }

   public Object valueOf(OptionSpec option) {
      Objects.ensureNotNull(option);
      List<V> values = this.valuesOf(option);
      switch(values.size()) {
      case 0:
         return null;
      case 1:
         return values.get(0);
      default:
         throw new MultipleArgumentsForOptionException(option.options());
      }
   }

   public List valuesOf(String option) {
      Objects.ensureNotNull(option);
      AbstractOptionSpec<?> spec = (AbstractOptionSpec)this.detectedOptions.get(option);
      return spec == null?this.defaultValuesFor(option):this.valuesOf((OptionSpec)spec);
   }

   public List valuesOf(OptionSpec option) {
      Objects.ensureNotNull(option);
      List<String> values = (List)this.optionsToArguments.get(option);
      if(values != null && !values.isEmpty()) {
         AbstractOptionSpec<V> spec = (AbstractOptionSpec)option;
         List<V> convertedValues = new ArrayList();

         for(String each : values) {
            convertedValues.add(spec.convert(each));
         }

         return Collections.unmodifiableList(convertedValues);
      } else {
         return this.defaultValueFor(option);
      }
   }

   public List specs() {
      List<OptionSpec<?>> specs = this.detectedSpecs;
      specs.remove(this.detectedOptions.get("[arguments]"));
      return Collections.unmodifiableList(specs);
   }

   public Map asMap() {
      Map<OptionSpec<?>, List<?>> map = new HashMap();

      for(AbstractOptionSpec<?> spec : this.recognizedSpecs.values()) {
         if(!spec.representsNonOptions()) {
            map.put(spec, this.valuesOf((OptionSpec)spec));
         }
      }

      return Collections.unmodifiableMap(map);
   }

   public List nonOptionArguments() {
      return Collections.unmodifiableList(this.valuesOf((OptionSpec)this.detectedOptions.get("[arguments]")));
   }

   void add(AbstractOptionSpec spec) {
      this.addWithArgument(spec, (String)null);
   }

   void addWithArgument(AbstractOptionSpec spec, String argument) {
      this.detectedSpecs.add(spec);

      for(String each : spec.options()) {
         this.detectedOptions.put(each, spec);
      }

      List<String> optionArguments = (List)this.optionsToArguments.get(spec);
      if(optionArguments == null) {
         optionArguments = new ArrayList();
         this.optionsToArguments.put(spec, optionArguments);
      }

      if(argument != null) {
         optionArguments.add(argument);
      }

   }

   public boolean equals(Object that) {
      if(this == that) {
         return true;
      } else if(that != null && this.getClass().equals(that.getClass())) {
         OptionSet other = (OptionSet)that;
         Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap(this.optionsToArguments);
         Map<AbstractOptionSpec<?>, List<String>> otherOptionsToArguments = new HashMap(other.optionsToArguments);
         return this.detectedOptions.equals(other.detectedOptions) && thisOptionsToArguments.equals(otherOptionsToArguments);
      } else {
         return false;
      }
   }

   public int hashCode() {
      Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap(this.optionsToArguments);
      return this.detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
   }

   private List defaultValuesFor(String option) {
      return this.defaultValues.containsKey(option)?(List)this.defaultValues.get(option):Collections.emptyList();
   }

   private List defaultValueFor(OptionSpec option) {
      return this.defaultValuesFor((String)option.options().iterator().next());
   }

   private static Map defaultValues(Map recognizedSpecs) {
      Map<String, List<?>> defaults = new HashMap();

      for(Entry<String, AbstractOptionSpec<?>> each : recognizedSpecs.entrySet()) {
         defaults.put(each.getKey(), ((AbstractOptionSpec)each.getValue()).defaultValues());
      }

      return defaults;
   }
}
