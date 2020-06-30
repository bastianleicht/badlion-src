package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   protected Map mapStateModelLocations = Maps.newLinkedHashMap();

   public String getPropertyString(Map p_178131_1_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<IProperty, Comparable> entry : p_178131_1_.entrySet()) {
         if(stringbuilder.length() != 0) {
            stringbuilder.append(",");
         }

         IProperty iproperty = (IProperty)entry.getKey();
         Comparable comparable = (Comparable)entry.getValue();
         stringbuilder.append(iproperty.getName());
         stringbuilder.append("=");
         stringbuilder.append(iproperty.getName(comparable));
      }

      if(stringbuilder.length() == 0) {
         stringbuilder.append("normal");
      }

      return stringbuilder.toString();
   }

   public Map putStateModelLocations(Block blockIn) {
      for(IBlockState iblockstate : blockIn.getBlockState().getValidStates()) {
         this.mapStateModelLocations.put(iblockstate, this.getModelResourceLocation(iblockstate));
      }

      return this.mapStateModelLocations;
   }

   protected abstract ModelResourceLocation getModelResourceLocation(IBlockState var1);
}
