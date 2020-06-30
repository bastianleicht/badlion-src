package org.apache.logging.log4j.core.pattern;

public interface PatternConverter {
   void format(Object var1, StringBuilder var2);

   String getName();

   String getStyleClass(Object var1);
}
