package joptsimple;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.ParserRules;
import joptsimple.internal.Classes;
import joptsimple.internal.Rows;
import joptsimple.internal.Strings;

public class BuiltinHelpFormatter implements HelpFormatter {
   private final Rows nonOptionRows;
   private final Rows optionRows;

   BuiltinHelpFormatter() {
      this(80, 2);
   }

   public BuiltinHelpFormatter(int desiredOverallWidth, int desiredColumnSeparatorWidth) {
      this.nonOptionRows = new Rows(desiredOverallWidth * 2, 0);
      this.optionRows = new Rows(desiredOverallWidth, desiredColumnSeparatorWidth);
   }

   public String format(Map options) {
      Comparator<OptionDescriptor> comparator = new Comparator() {
         public int compare(OptionDescriptor first, OptionDescriptor second) {
            return ((String)first.options().iterator().next()).compareTo((String)second.options().iterator().next());
         }
      };
      Set<OptionDescriptor> sorted = new TreeSet(comparator);
      sorted.addAll(options.values());
      this.addRows(sorted);
      return this.formattedHelpOutput();
   }

   private String formattedHelpOutput() {
      StringBuilder formatted = new StringBuilder();
      String nonOptionDisplay = this.nonOptionRows.render();
      if(!Strings.isNullOrEmpty(nonOptionDisplay)) {
         formatted.append(nonOptionDisplay).append(Strings.LINE_SEPARATOR);
      }

      formatted.append(this.optionRows.render());
      return formatted.toString();
   }

   private void addRows(Collection options) {
      this.addNonOptionsDescription(options);
      if(options.isEmpty()) {
         this.optionRows.add("No options specified", "");
      } else {
         this.addHeaders(options);
         this.addOptions(options);
      }

      this.fitRowsToWidth();
   }

   private void addNonOptionsDescription(Collection options) {
      OptionDescriptor nonOptions = this.findAndRemoveNonOptionsSpec(options);
      if(this.shouldShowNonOptionArgumentDisplay(nonOptions)) {
         this.nonOptionRows.add("Non-option arguments:", "");
         this.nonOptionRows.add(this.createNonOptionArgumentsDisplay(nonOptions), "");
      }

   }

   private boolean shouldShowNonOptionArgumentDisplay(OptionDescriptor nonOptions) {
      return !Strings.isNullOrEmpty(nonOptions.description()) || !Strings.isNullOrEmpty(nonOptions.argumentTypeIndicator()) || !Strings.isNullOrEmpty(nonOptions.argumentDescription());
   }

   private String createNonOptionArgumentsDisplay(OptionDescriptor nonOptions) {
      StringBuilder buffer = new StringBuilder();
      this.maybeAppendOptionInfo(buffer, nonOptions);
      this.maybeAppendNonOptionsDescription(buffer, nonOptions);
      return buffer.toString();
   }

   private void maybeAppendNonOptionsDescription(StringBuilder buffer, OptionDescriptor nonOptions) {
      buffer.append(buffer.length() > 0 && !Strings.isNullOrEmpty(nonOptions.description())?" -- ":"").append(nonOptions.description());
   }

   private OptionDescriptor findAndRemoveNonOptionsSpec(Collection options) {
      Iterator<? extends OptionDescriptor> it = options.iterator();

      while(it.hasNext()) {
         OptionDescriptor next = (OptionDescriptor)it.next();
         if(next.representsNonOptions()) {
            it.remove();
            return next;
         }
      }

      throw new AssertionError("no non-options argument spec");
   }

   private void addHeaders(Collection options) {
      if(this.hasRequiredOption(options)) {
         this.optionRows.add("Option (* = required)", "Description");
         this.optionRows.add("---------------------", "-----------");
      } else {
         this.optionRows.add("Option", "Description");
         this.optionRows.add("------", "-----------");
      }

   }

   private boolean hasRequiredOption(Collection options) {
      for(OptionDescriptor each : options) {
         if(each.isRequired()) {
            return true;
         }
      }

      return false;
   }

   private void addOptions(Collection options) {
      for(OptionDescriptor each : options) {
         if(!each.representsNonOptions()) {
            this.optionRows.add(this.createOptionDisplay(each), this.createDescriptionDisplay(each));
         }
      }

   }

   private String createOptionDisplay(OptionDescriptor descriptor) {
      StringBuilder buffer = new StringBuilder(descriptor.isRequired()?"* ":"");
      Iterator<String> i = descriptor.options().iterator();

      while(i.hasNext()) {
         String option = (String)i.next();
         buffer.append(option.length() > 1?"--":ParserRules.HYPHEN);
         buffer.append(option);
         if(i.hasNext()) {
            buffer.append(", ");
         }
      }

      this.maybeAppendOptionInfo(buffer, descriptor);
      return buffer.toString();
   }

   private void maybeAppendOptionInfo(StringBuilder buffer, OptionDescriptor descriptor) {
      String indicator = this.extractTypeIndicator(descriptor);
      String description = descriptor.argumentDescription();
      if(indicator != null || !Strings.isNullOrEmpty(description)) {
         this.appendOptionHelp(buffer, indicator, description, descriptor.requiresArgument());
      }

   }

   private String extractTypeIndicator(OptionDescriptor descriptor) {
      String indicator = descriptor.argumentTypeIndicator();
      return !Strings.isNullOrEmpty(indicator) && !String.class.getName().equals(indicator)?Classes.shortNameOf(indicator):null;
   }

   private void appendOptionHelp(StringBuilder buffer, String typeIndicator, String description, boolean required) {
      if(required) {
         this.appendTypeIndicator(buffer, typeIndicator, description, '<', '>');
      } else {
         this.appendTypeIndicator(buffer, typeIndicator, description, '[', ']');
      }

   }

   private void appendTypeIndicator(StringBuilder buffer, String typeIndicator, String description, char start, char end) {
      buffer.append(' ').append(start);
      if(typeIndicator != null) {
         buffer.append(typeIndicator);
      }

      if(!Strings.isNullOrEmpty(description)) {
         if(typeIndicator != null) {
            buffer.append(": ");
         }

         buffer.append(description);
      }

      buffer.append(end);
   }

   private String createDescriptionDisplay(OptionDescriptor descriptor) {
      List<?> defaultValues = descriptor.defaultValues();
      if(defaultValues.isEmpty()) {
         return descriptor.description();
      } else {
         String defaultValuesDisplay = this.createDefaultValuesDisplay(defaultValues);
         return (descriptor.description() + ' ' + Strings.surround("default: " + defaultValuesDisplay, '(', ')')).trim();
      }
   }

   private String createDefaultValuesDisplay(List defaultValues) {
      return defaultValues.size() == 1?defaultValues.get(0).toString():defaultValues.toString();
   }

   private void fitRowsToWidth() {
      this.nonOptionRows.fitToWidth();
      this.optionRows.fitToWidth();
   }
}
