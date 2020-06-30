package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List elements;
   private final List offsets;
   private int nextOffset;
   private int colorElementOffset;
   private List uvOffsetsById;
   private int normalElementOffset;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage;

   public VertexFormat(VertexFormat vertexFormatIn) {
      this();

      for(int i = 0; i < vertexFormatIn.getElementCount(); ++i) {
         this.func_181721_a(vertexFormatIn.getElement(i));
      }

      this.nextOffset = vertexFormatIn.getNextOffset();
   }

   public VertexFormat() {
      this.elements = Lists.newArrayList();
      this.offsets = Lists.newArrayList();
      this.nextOffset = 0;
      this.colorElementOffset = -1;
      this.uvOffsetsById = Lists.newArrayList();
      this.normalElementOffset = -1;
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.normalElementOffset = -1;
      this.nextOffset = 0;
   }

   public VertexFormat func_181721_a(VertexFormatElement p_181721_1_) {
      if(p_181721_1_.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.elements.add(p_181721_1_);
         this.offsets.add(Integer.valueOf(this.nextOffset));
         switch($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage()[p_181721_1_.getUsage().ordinal()]) {
         case 2:
            this.normalElementOffset = this.nextOffset;
            break;
         case 3:
            this.colorElementOffset = this.nextOffset;
            break;
         case 4:
            this.uvOffsetsById.add(p_181721_1_.getIndex(), Integer.valueOf(this.nextOffset));
         }

         this.nextOffset += p_181721_1_.getSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public boolean hasColor() {
      return this.colorElementOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasUvOffset(int id) {
      return this.uvOffsetsById.size() - 1 >= id;
   }

   public int getUvOffsetById(int id) {
      return ((Integer)this.uvOffsetsById.get(id)).intValue();
   }

   public String toString() {
      String s = "format: " + this.elements.size() + " elements: ";

      for(int i = 0; i < this.elements.size(); ++i) {
         s = s + ((VertexFormatElement)this.elements.get(i)).toString();
         if(i != this.elements.size() - 1) {
            s = s + " ";
         }
      }

      return s;
   }

   private boolean hasPosition() {
      int i = 0;

      for(int j = this.elements.size(); i < j; ++i) {
         VertexFormatElement vertexformatelement = (VertexFormatElement)this.elements.get(i);
         if(vertexformatelement.isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int func_181719_f() {
      return this.getNextOffset() / 4;
   }

   public int getNextOffset() {
      return this.nextOffset;
   }

   public List getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int index) {
      return (VertexFormatElement)this.elements.get(index);
   }

   public int func_181720_d(int p_181720_1_) {
      return ((Integer)this.offsets.get(p_181720_1_)).intValue();
   }

   public boolean equals(Object p_equals_1_) {
      if(this == p_equals_1_) {
         return true;
      } else if(p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_equals_1_;
         return this.nextOffset != vertexformat.nextOffset?false:(!this.elements.equals(vertexformat.elements)?false:this.offsets.equals(vertexformat.offsets));
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      i = 31 * i + this.nextOffset;
      return i;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage;
      if($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage != null) {
         return var10000;
      } else {
         int[] var0 = new int[VertexFormatElement.EnumUsage.values().length];

         try {
            var0[VertexFormatElement.EnumUsage.BLEND_WEIGHT.ordinal()] = 6;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.MATRIX.ordinal()] = 5;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.NORMAL.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.PADDING.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.POSITION.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[VertexFormatElement.EnumUsage.UV.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage = var0;
         return var0;
      }
   }
}
