package net.badlion.client.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.badlion.client.Wrapper;
import net.badlion.client.thread.CapeLookupThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CapeManager {
   public ModelRenderer bipedCloakShoulders;
   public ModelRenderer miniBipedCloak;
   public ModelRenderer miniBipedCloakShoulders;
   private final CapeLookupThread capeLookupThread = new CapeLookupThread();
   private Map userToCape = new ConcurrentHashMap();
   private Map capeResources = new HashMap();
   private int capeTick = 0;
   private int ticks = 0;

   public CapeManager() {
      this.capeLookupThread.start();
   }

   public void tickCapes() {
      ++this.ticks;
      if((double)this.ticks % 1.5D != 0.0D) {
         ++this.capeTick;
      }

   }

   public void checkUser(UUID playerId) {
      if(!this.userToCape.containsKey(playerId)) {
         this.capeLookupThread.addPlayer(playerId);
      }

   }

   public Map getUserToCape() {
      return this.userToCape;
   }

   public boolean bindCape(AbstractClientPlayer player, boolean hasCape) {
      if(!this.userToCape.containsKey(player.getUniqueID())) {
         return false;
      } else {
         int i = ((Integer)this.userToCape.get(player.getUniqueID())).intValue();
         if(i != -1) {
            if(i == 0) {
               if(!hasCape) {
                  return false;
               }

               Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationCape());
               return true;
            }

            if(i == 1) {
               if(!hasCape) {
                  return false;
               }

               Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationCape());
               return true;
            }

            if(i > 1) {
               if(i != 4 && i != 3) {
                  String s = String.valueOf(i);
                  if(!this.capeResources.containsKey(s)) {
                     this.capeResources.put(s, new ResourceLocation("textures/capes/cape_" + i + ".png"));
                  }

                  Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped((ResourceLocation)this.capeResources.get(s));
               } else {
                  int j = 11;
                  int k = this.capeTick % (j * 2);
                  if(k >= j) {
                     k = j * 2 - 1 - k;
                  }

                  this.bindTextureFrame(i, k);
               }

               return true;
            }
         }

         if(hasCape) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationCape());
            return true;
         } else {
            return false;
         }
      }
   }

   public void bindTextureFrame(int capeId, int frame) {
      if(!this.capeResources.containsKey(capeId + "-" + frame)) {
         this.capeResources.put(capeId + "-" + frame, new ResourceLocation("textures/capes/" + capeId + "/cape_" + capeId + "-" + frame + ".png"));
      }

      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped((ResourceLocation)this.capeResources.get(capeId + "-" + frame));
   }

   public boolean bindCape(int capeId) {
      if(capeId <= 1) {
         return false;
      } else {
         if(capeId != 4 && capeId != 3) {
            if(!this.capeResources.containsKey(String.valueOf(capeId))) {
               this.capeResources.put(String.valueOf(capeId), new ResourceLocation("textures/capes/cape_" + capeId + ".png"));
            }

            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped((ResourceLocation)this.capeResources.get(Integer.valueOf(capeId)));
         } else {
            int i = 11;
            int j = this.capeTick % (i * 2);
            if(j >= i) {
               j = i * 2 - 1 - j;
            }

            this.bindTextureFrame(capeId, j);
         }

         return true;
      }
   }

   public void initCapes(ModelPlayer modelPlayer) {
      this.bipedCloakShoulders = new ModelRenderer(modelPlayer, 0, 0);
      this.bipedCloakShoulders.addBox(-5.0F, -1.0F, -2.0F, 2, 1, 5, 0.0F);
      this.bipedCloakShoulders.addBox(3.0F, -1.0F, -2.0F, 2, 1, 5, 0.0F);
      this.miniBipedCloak = new ModelRenderer(modelPlayer, 0, 0);
      this.miniBipedCloak.addBox(-3.0F, 0.0F, -1.0F, 6, 10, 1, 0.0F);
      this.miniBipedCloakShoulders = new ModelRenderer(modelPlayer, 0, 0);
      this.miniBipedCloakShoulders.addBox(-3.0F, -1.0F, -2.0F, 1, 1, 5, 0.0F);
      this.miniBipedCloakShoulders.addBox(2.0F, -1.0F, -2.0F, 1, 1, 5, 0.0F);
   }

   public static void renderCape(RenderPlayer playerRenderer, AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
      boolean flag = entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getLocationCape() != null;
      if(!entitylivingbaseIn.isInvisible() && Wrapper.getInstance().getCapeManager().bindCape(entitylivingbaseIn, flag)) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 0.125F);
         double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
         double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
         double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
         float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
         double d3 = (double)MathHelper.sin(f * 3.1415927F / 180.0F);
         double d4 = (double)(-MathHelper.cos(f * 3.1415927F / 180.0F));
         float f1 = (float)d1 * 10.0F;
         f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
         float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
         float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
         if(f2 < 0.0F) {
            f2 = 0.0F;
         }

         if(f2 > 165.0F) {
            f2 = 165.0F;
         }

         float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
         f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;
         if(entitylivingbaseIn.isSneaking()) {
            f1 += 25.0F;
            GlStateManager.translate(0.0F, 0.05F, -0.0178F);
         }

         GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         playerRenderer.getMainModel().renderCape(0.0625F);
         GlStateManager.popMatrix();
         GL11.glPushMatrix();
         if(entitylivingbaseIn.isSneaking()) {
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
         }

         GL11.glRotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
         Wrapper.getInstance().getCapeManager().renderCloakShoulders(0.0625F);
         GL11.glPopMatrix();
      }

   }

   public void renderCloakShoulders(float p_78111_1_) {
      this.bipedCloakShoulders.render(p_78111_1_);
   }
}
