package org.apache.http.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.Registry;
import org.apache.http.util.Args;

@NotThreadSafe
public final class RegistryBuilder {
   private final Map items = new HashMap();

   public static RegistryBuilder create() {
      return new RegistryBuilder();
   }

   public RegistryBuilder register(String id, Object item) {
      Args.notEmpty((CharSequence)id, "ID");
      Args.notNull(item, "Item");
      this.items.put(id.toLowerCase(Locale.US), item);
      return this;
   }

   public Registry build() {
      return new Registry(this.items);
   }

   public String toString() {
      return this.items.toString();
   }
}
