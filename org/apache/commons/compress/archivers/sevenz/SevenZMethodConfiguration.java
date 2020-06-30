package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.archivers.sevenz.Coders;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;

public class SevenZMethodConfiguration {
   private final SevenZMethod method;
   private final Object options;

   public SevenZMethodConfiguration(SevenZMethod method) {
      this(method, (Object)null);
   }

   public SevenZMethodConfiguration(SevenZMethod method, Object options) {
      this.method = method;
      this.options = options;
      if(options != null && !Coders.findByMethod(method).canAcceptOptions(options)) {
         throw new IllegalArgumentException("The " + method + " method doesn\'t support options of type " + options.getClass());
      }
   }

   public SevenZMethod getMethod() {
      return this.method;
   }

   public Object getOptions() {
      return this.options;
   }
}
