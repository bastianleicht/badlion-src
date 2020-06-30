package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.pattern.PatternConverter;

public interface ArrayPatternConverter extends PatternConverter {
   void format(StringBuilder var1, Object... var2);
}
