package org.apache.http.params;

import java.util.Set;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;

/** @deprecated */
@Deprecated
public abstract class AbstractHttpParams implements HttpParams, HttpParamsNames {
   public long getLongParameter(String name, long defaultValue) {
      Object param = this.getParameter(name);
      return param == null?defaultValue:((Long)param).longValue();
   }

   public HttpParams setLongParameter(String name, long value) {
      this.setParameter(name, Long.valueOf(value));
      return this;
   }

   public int getIntParameter(String name, int defaultValue) {
      Object param = this.getParameter(name);
      return param == null?defaultValue:((Integer)param).intValue();
   }

   public HttpParams setIntParameter(String name, int value) {
      this.setParameter(name, Integer.valueOf(value));
      return this;
   }

   public double getDoubleParameter(String name, double defaultValue) {
      Object param = this.getParameter(name);
      return param == null?defaultValue:((Double)param).doubleValue();
   }

   public HttpParams setDoubleParameter(String name, double value) {
      this.setParameter(name, Double.valueOf(value));
      return this;
   }

   public boolean getBooleanParameter(String name, boolean defaultValue) {
      Object param = this.getParameter(name);
      return param == null?defaultValue:((Boolean)param).booleanValue();
   }

   public HttpParams setBooleanParameter(String name, boolean value) {
      this.setParameter(name, value?Boolean.TRUE:Boolean.FALSE);
      return this;
   }

   public boolean isParameterTrue(String name) {
      return this.getBooleanParameter(name, false);
   }

   public boolean isParameterFalse(String name) {
      return !this.getBooleanParameter(name, false);
   }

   public Set getNames() {
      throw new UnsupportedOperationException();
   }
}
