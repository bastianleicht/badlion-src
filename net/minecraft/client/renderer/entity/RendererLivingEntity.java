package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity extends Render {
   private static final Logger logger = LogManager.getLogger();
   private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
   protected ModelBase mainModel;
   protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   protected List layerRenderers = Lists.newArrayList();
   protected boolean renderOutlines = false;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible;

   static {
      int[] aint = field_177096_e.getTextureData();

      for(int i = 0; i < 256; ++i) {
         aint[i] = -1;
      }

      field_177096_e.updateDynamicTexture();
   }

   public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.mainModel = modelBaseIn;
      this.shadowSize = shadowSizeIn;
   }

   protected boolean addLayer(LayerRenderer layer) {
      return this.layerRenderers.add(layer);
   }

   protected boolean removeLayer(LayerRenderer layer) {
      return this.layerRenderers.remove(layer);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   protected float interpolateRotation(float par1, float par2, float par3) {
      float f;
      for(f = par2 - par1; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return par1 + par3 * f;
   }

   public void transformHeldFull3DItemLayer() {
   }

   public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
      this.mainModel.isRiding = entity.isRiding();
      this.mainModel.isChild = entity.isChild();

      try {
         float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
         float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
         float f2 = f1 - f;
         if(entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity.ridingEntity;
            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
            f2 = f1 - f;
            float f3 = MathHelper.wrapAngleTo180_float(f2);
            if(f3 < -85.0F) {
               f3 = -85.0F;
            }

            if(f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if(f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }
         }

         float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         this.renderLivingAt(entity, x, y, z);
         float f8 = this.handleRotationFloat(entity, partialTicks);
         this.rotateCorpse(entity, f8, f, partialTicks);
         GlStateManager.enableRescaleNormal();
         GlStateManager.scale(-1.0F, -1.0F, 1.0F);
         this.preRenderCallback(entity, partialTicks);
         float f4 = 0.0625F;
         GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
         float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
         float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
         if(entity.isChild()) {
            f6 *= 3.0F;
         }

         if(f5 > 1.0F) {
            f5 = 1.0F;
         }

         GlStateManager.enableAlpha();
         this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
         this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F, entity);
         if(this.renderOutlines) {
            boolean flag1 = this.setScoreTeamColor(entity);
            this.renderModel(entity, f6, f5, f8, f2, f7, 0.0625F);
            if(flag1) {
               this.unsetScoreTeamColor();
            }
         } else {
            boolean flag = this.setDoRenderBrightness(entity, partialTicks);
            this.renderModel(entity, f6, f5, f8, f2, f7, 0.0625F);
            if(flag) {
               this.unsetBrightness();
            }

            GlStateManager.depthMask(true);
            if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, 0.0625F);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
         logger.error((String)"Couldn\'t render entity", (Throwable)var19);
      }

      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      if(!this.renderOutlines) {
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }

   }

   protected boolean setScoreTeamColor(EntityLivingBase entityLivingBaseIn) {
      int i = 16777215;
      if(entityLivingBaseIn instanceof EntityPlayer) {
         ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)entityLivingBaseIn.getTeam();
         if(scoreplayerteam != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
            if(s.length() >= 2) {
               i = this.getFontRendererFromRenderManager().getColorCode(s.charAt(1));
            }
         }
      }

      float f1 = (float)(i >> 16 & 255) / 255.0F;
      float f2 = (float)(i >> 8 & 255) / 255.0F;
      float f = (float)(i & 255) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(f1, f2, f, 1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   protected void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   protected void renderModel(EntityLivingBase entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
      boolean flag = !entitylivingbaseIn.isInvisible();
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
      if(flag || flag1) {
         if(!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if(flag1) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
         if(flag1) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }

   }

   protected boolean setDoRenderBrightness(EntityLivingBase entityLivingBaseIn, float partialTicks) {
      return this.setBrightness(entityLivingBaseIn, partialTicks, true);
   }

   protected boolean setBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float f = entitylivingbaseIn.getBrightness(partialTicks);
      int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if(!flag && !flag1) {
         return false;
      } else if(!flag && !combineTextures) {
         return false;
      } else {
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableTexture2D();
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         this.brightnessBuffer.position(0);
         if(flag1) {
            this.brightnessBuffer.put(1.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.3F);
         } else {
            float f1 = (float)(i >> 24 & 255) / 255.0F;
            float f2 = (float)(i >> 16 & 255) / 255.0F;
            float f3 = (float)(i >> 8 & 255) / 255.0F;
            float f4 = (float)(i & 255) / 255.0F;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0F - f1);
         }

         this.brightnessBuffer.flip();
         GL11.glTexEnv(8960, 8705, (FloatBuffer)this.brightnessBuffer);
         GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
         GlStateManager.enableTexture2D();
         GlStateManager.bindTexture(field_177096_e.getGlTextureId());
         GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         return true;
      }
   }

   protected void unsetBrightness() {
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   protected void renderLivingAt(EntityLivingBase entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   protected void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
      GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
      if(bat.deathTime > 0) {
         float f = ((float)bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt_float(f);
         if(f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotate(f * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
      } else {
         String s = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName());
         if(s != null && (s.equals("Dinnerbone") || s.equals("Grumm")) && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float getSwingProgress(EntityLivingBase livingBase, float partialTickTime) {
      return livingBase.getSwingProgress(partialTickTime);
   }

   protected float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void renderLayers(EntityLivingBase entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
      for(LayerRenderer<T> layerrenderer : this.layerRenderers) {
         boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
         layerrenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
         if(flag) {
            this.unsetBrightness();
         }
      }

   }

   protected float getDeathMaxRotation(EntityLivingBase entityLivingBaseIn) {
      return 90.0F;
   }

   protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
   }

   public void renderName(EntityLivingBase entity, double x, double y, double z) {
      if(this.canRenderName(entity)) {
         double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
         float f = entity.isSneaking()?32.0F:64.0F;
         if(d0 < (double)(f * f)) {
            String s = entity.getDisplayName().getFormattedText();
            float f1 = 0.02666667F;
            GlStateManager.alphaFunc(516, 0.1F);
            if(entity.isSneaking()) {
               FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild()?entity.height / 2.0F:0.0F), (float)z);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
               GlStateManager.translate(0.0F, 9.374999F, 0.0F);
               GlStateManager.disableLighting();
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.disableTexture2D();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               int i = fontrenderer.getStringWidth(s) / 2;
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldrenderer = tessellator.getWorldRenderer();
               worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               worldrenderer.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldrenderer.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderOffsetLivingLabel(entity, x, y - (entity.isChild()?(double)(entity.height / 2.0F):0.0D), z, s, 0.02666667F, d0);
            }
         }
      }

   }

   protected boolean canRenderName(EntityLivingBase entity) {
      EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;
      if(entity instanceof EntityPlayer && entity != entityplayersp) {
         Team team = entity.getTeam();
         Team team1 = entityplayersp.getTeam();
         if(team != null) {
            Team.EnumVisible team$enumvisible = team.getNameTagVisibility();
            switch($SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible()[team$enumvisible.ordinal()]) {
            case 1:
               return true;
            case 2:
               return false;
            case 3:
               if(team1 != null && !team.isSameTeam(team1)) {
                  return false;
               }

               return true;
            case 4:
               if(team1 != null && team.isSameTeam(team1)) {
                  return false;
               }

               return true;
            default:
               return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer(entityplayersp) && entity.riddenByEntity == null;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible;
      if($SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible != null) {
         return var10000;
      } else {
         int[] var0 = new int[Team.EnumVisible.values().length];

         try {
            var0[Team.EnumVisible.ALWAYS.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[Team.EnumVisible.HIDE_FOR_OTHER_TEAMS.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[Team.EnumVisible.HIDE_FOR_OWN_TEAM.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[Team.EnumVisible.NEVER.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$scoreboard$Team$EnumVisible = var0;
         return var0;
      }
   }
}
