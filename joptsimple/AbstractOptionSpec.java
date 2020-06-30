package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import joptsimple.ArgumentList;
import joptsimple.OptionArgumentConversionException;
import joptsimple.OptionDescriptor;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;
import joptsimple.internal.ReflectionException;

abstract class AbstractOptionSpec implements OptionSpec, OptionDescriptor {
   private final List options;
   private final String description;
   private boolean forHelp;

   protected AbstractOptionSpec(String option) {
      this(Collections.singletonList(option), "");
   }

   protected AbstractOptionSpec(Collection options, String description) {
      this.options = new ArrayList();
      this.arrangeOptions(options);
      this.description = description;
   }

   public final Collection options() {
      return Collections.unmodifiableList(this.options);
   }

   public final List values(OptionSet detectedOptions) {
      return detectedOptions.valuesOf((OptionSpec)this);
   }

   public final Object value(OptionSet detectedOptions) {
      return detectedOptions.valueOf((OptionSpec)this);
   }

   public String description() {
      return this.description;
   }

   public final AbstractOptionSpec forHelp() {
      this.forHelp = true;
      return this;
   }

   public final boolean isForHelp() {
      return this.forHelp;
   }

   public boolean representsNonOptions() {
      return false;
   }

   protected abstract Object convert(String var1);

   protected Object convertWith(ValueConverter converter, String argument) {
      try {
         return Reflection.convertWith(converter, argument);
      } catch (ReflectionException var4) {
         throw new OptionArgumentConversionException(this.options(), argument, var4);
      } catch (ValueConversionException var5) {
         throw new OptionArgumentConversionException(this.options(), argument, var5);
      }
   }

   protected String argumentTypeIndicatorFrom(ValueConverter converter) {
      if(converter == null) {
         return null;
      } else {
         String pattern = converter.valuePattern();
         return pattern == null?converter.valueType().getName():pattern;
      }
   }

   abstract void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4);

   private void arrangeOptions(Collection unarranged) {
      if(unarranged.size() == 1) {
         this.options.addAll(unarranged);
      } else {
         List<String> shortOptions = new ArrayList();
         List<String> longOptions = new ArrayList();

         for(String each : unarranged) {
            if(each.length() == 1) {
               shortOptions.add(each);
            } else {
               longOptions.add(each);
            }
         }

         Collections.sort(shortOptions);
         Collections.sort(longOptions);
         this.options.addAll(shortOptions);
         this.options.addAll(longOptions);
      }
   }

   public boolean equals(Object that) {
      if(!(that instanceof AbstractOptionSpec)) {
         return false;
      } else {
         AbstractOptionSpec<?> other = (AbstractOptionSpec)that;
         return this.options.equals(other.options);
      }
   }

   public int hashCode() {
      return this.options.hashCode();
   }

   public String toString() {
      return this.options.toString();
   }
}
