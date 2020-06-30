package net.minecraft.util;

import net.minecraft.util.RegistrySimple;

public class RegistryDefaulted extends RegistrySimple {
   private final Object defaultObject;

   public RegistryDefaulted(Object defaultObjectIn) {
      this.defaultObject = defaultObjectIn;
   }

   public Object getObject(Object name) {
      V v = super.getObject(name);
      return v == null?this.defaultObject:v;
   }
}
