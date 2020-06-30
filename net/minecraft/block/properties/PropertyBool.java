package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import net.minecraft.block.properties.PropertyHelper;

public class PropertyBool extends PropertyHelper {
   private final ImmutableSet allowedValues = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

   protected PropertyBool(String name) {
      super(name, Boolean.class);
   }

   public Collection getAllowedValues() {
      return this.allowedValues;
   }

   public static PropertyBool create(String name) {
      return new PropertyBool(name);
   }

   public String getName(Boolean value) {
      return value.toString();
   }
}
