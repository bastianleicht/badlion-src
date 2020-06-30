package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage;

   public void func_181679_a(WorldRenderer p_181679_1_) {
      if(p_181679_1_.getVertexCount() > 0) {
         VertexFormat vertexformat = p_181679_1_.getVertexFormat();
         int i = vertexformat.getNextOffset();
         ByteBuffer bytebuffer = p_181679_1_.getByteBuffer();
         List<VertexFormatElement> list = vertexformat.getElements();

         for(int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexformatelement = (VertexFormatElement)list.get(j);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
            int k = vertexformatelement.getType().getGlConstant();
            int l = vertexformatelement.getIndex();
            bytebuffer.position(vertexformat.func_181720_d(j));
            switch($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage()[vertexformatelement$enumusage.ordinal()]) {
            case 1:
               GL11.glVertexPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GL11.glEnableClientState('聴');
               break;
            case 2:
               GL11.glNormalPointer(k, i, bytebuffer);
               GL11.glEnableClientState('聵');
               break;
            case 3:
               GL11.glColorPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GL11.glEnableClientState('聶');
               break;
            case 4:
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + l);
               GL11.glTexCoordPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
               GL11.glEnableClientState('聸');
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }
         }

         GL11.glDrawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
         int i1 = 0;

         for(int j1 = list.size(); i1 < j1; ++i1) {
            VertexFormatElement vertexformatelement1 = (VertexFormatElement)list.get(i1);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
            int k1 = vertexformatelement1.getIndex();
            switch($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage()[vertexformatelement$enumusage1.ordinal()]) {
            case 1:
               GL11.glDisableClientState('聴');
               break;
            case 2:
               GL11.glDisableClientState('聵');
               break;
            case 3:
               GL11.glDisableClientState('聶');
               GlStateManager.resetColor();
               break;
            case 4:
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k1);
               GL11.glDisableClientState('聸');
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }
         }
      }

      p_181679_1_.reset();
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
