package org.apache.commons.lang3.exception;

import java.util.List;
import java.util.Set;

public interface ExceptionContext {
   ExceptionContext addContextValue(String var1, Object var2);

   ExceptionContext setContextValue(String var1, Object var2);

   List getContextValues(String var1);

   Object getFirstContextValue(String var1);

   Set getContextLabels();

   List getContextEntries();

   String getFormattedExceptionMessage(String var1);
}
