package org.apache.http.params;

/** @deprecated */
@Deprecated
public interface HttpParams {
   Object getParameter(String var1);

   HttpParams setParameter(String var1, Object var2);

   HttpParams copy();

   boolean removeParameter(String var1);

   long getLongParameter(String var1, long var2);

   HttpParams setLongParameter(String var1, long var2);

   int getIntParameter(String var1, int var2);

   HttpParams setIntParameter(String var1, int var2);

   double getDoubleParameter(String var1, double var2);

   HttpParams setDoubleParameter(String var1, double var2);

   boolean getBooleanParameter(String var1, boolean var2);

   HttpParams setBooleanParameter(String var1, boolean var2);

   boolean isParameterTrue(String var1);

   boolean isParameterFalse(String var1);
}
