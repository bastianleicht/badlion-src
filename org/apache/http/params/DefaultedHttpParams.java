package org.apache.http.params;

import java.util.HashSet;
import java.util.Set;
import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public final class DefaultedHttpParams extends AbstractHttpParams {
   private final HttpParams local;
   private final HttpParams defaults;

   public DefaultedHttpParams(HttpParams local, HttpParams defaults) {
      this.local = (HttpParams)Args.notNull(local, "Local HTTP parameters");
      this.defaults = defaults;
   }

   public HttpParams copy() {
      HttpParams clone = this.local.copy();
      return new DefaultedHttpParams(clone, this.defaults);
   }

   public Object getParameter(String name) {
      Object obj = this.local.getParameter(name);
      if(obj == null && this.defaults != null) {
         obj = this.defaults.getParameter(name);
      }

      return obj;
   }

   public boolean removeParameter(String name) {
      return this.local.removeParameter(name);
   }

   public HttpParams setParameter(String name, Object value) {
      return this.local.setParameter(name, value);
   }

   public HttpParams getDefaults() {
      return this.defaults;
   }

   public Set getNames() {
      Set<String> combined = new HashSet(this.getNames(this.defaults));
      combined.addAll(this.getNames(this.local));
      return combined;
   }

   public Set getDefaultNames() {
      return new HashSet(this.getNames(this.defaults));
   }

   public Set getLocalNames() {
      return new HashSet(this.getNames(this.local));
   }

   private Set getNames(HttpParams params) {
      if(params instanceof HttpParamsNames) {
         return ((HttpParamsNames)params).getNames();
      } else {
         throw new UnsupportedOperationException("HttpParams instance does not implement HttpParamsNames");
      }
   }
}
