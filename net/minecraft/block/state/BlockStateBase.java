package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public abstract class BlockStateBase implements IBlockState {
   private static final Joiner COMMA_JOINER = Joiner.on(',');
   private static final Function MAP_ENTRY_TO_STRING = new Function() {
      public String apply(Entry p_apply_1_) {
         if(p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty iproperty = (IProperty)p_apply_1_.getKey();
            return iproperty.getName() + "=" + iproperty.getName((Comparable)p_apply_1_.getValue());
         }
      }
   };

   public IBlockState cycleProperty(IProperty property) {
      return this.withProperty(property, (Comparable)cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
   }

   protected static Object cyclePropertyValue(Collection values, Object currentValue) {
      Iterator<T> iterator = values.iterator();

      while(iterator.hasNext()) {
         if(iterator.next().equals(currentValue)) {
            if(iterator.hasNext()) {
               return iterator.next();
            }

            return values.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));
      if(!this.getProperties().isEmpty()) {
         stringbuilder.append("[");
         COMMA_JOINER.appendTo(stringbuilder, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
         stringbuilder.append("]");
      }

      return stringbuilder.toString();
   }
}
