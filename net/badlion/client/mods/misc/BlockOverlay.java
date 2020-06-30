package net.badlion.client.mods.misc;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

public class BlockOverlay extends Mod {
   private ModColor outlineColor = new ModColor(1744830464);
   private ModColor fillColor = new ModColor(906035199);
   private transient Slider thicknessSlider;
   private double thickness = 2.0D;

   public BlockOverlay() {
      super("Block Overlay", false);
      this.iconDimension = new ImageDimension(128, 128);
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      this.setFontOffset(0.083D);
      this.offsetX = -2;
      super.init();
   }

   public void reset() {
      this.outlineColor = new ModColor(1744830464);
      this.fillColor = new ModColor(906035199);
      this.thickness = 2.0D;
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new ColorPicker("Outline Color", this.outlineColor, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Fill Color", this.fillColor, 0.13D, true));
      this.outlineColor.init();
      this.fillColor.init();
      this.slideCogMenu.addElement(this.thicknessSlider = new Slider("Thickness", 0.0D, 10.0D, this.thickness, 0.2D));
      this.thicknessSlider.init();
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.fillColor.tickColor();
         this.outlineColor.tickColor();
         this.thickness = this.thicknessSlider.getValue();
      }

      super.onEvent(e);
   }

   public ModColor getOutlineColor() {
      return this.outlineColor;
   }

   public ModColor getFillColor() {
      return this.fillColor;
   }

   public double getThickness() {
      return this.thickness;
   }

   public static void drawFillBoundingBox(AxisAlignedBB p_147590_0_) {
      ModColor modcolor = Wrapper.getInstance().getActiveModProfile().getBlockOverlay().getFillColor();
      if(modcolor.isEnabled()) {
         ColorUtil.bindColor(modcolor.getColor());
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         tessellator.draw();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         tessellator.draw();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         tessellator.draw();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         tessellator.draw();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         tessellator.draw();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.maxZ).endVertex();
         worldrenderer.pos(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ).endVertex();
         tessellator.draw();
      }

   }

   public static void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
      if(execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         boolean flag = Wrapper.getInstance().getActiveModProfile().getBlockOverlay().isEnabled();
         if(flag) {
            ColorUtil.bindColor(Wrapper.getInstance().getActiveModProfile().getBlockOverlay().getOutlineColor().getColor());
            GL11.glLineWidth((float)Wrapper.getInstance().getActiveModProfile().getBlockOverlay().getThickness());
         } else {
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
         }

         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         float f = 0.002F;
         BlockPos blockpos = movingObjectPositionIn.getBlockPos();
         Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockpos).getBlock();
         if(block.getMaterial() != Material.air && Minecraft.getMinecraft().theWorld.getWorldBorder().contains(blockpos)) {
            block.setBlockBoundsBasedOnState(Minecraft.getMinecraft().theWorld, blockpos);
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
            AxisAlignedBB axisalignedbb = block.getSelectedBoundingBox(Minecraft.getMinecraft().theWorld, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2);
            if(Wrapper.getInstance().getActiveModProfile().getBlockOverlay().getOutlineColor().isEnabled() || !flag) {
               drawOutlinedBoundingBox(axisalignedbb);
            }

            if(flag) {
               drawFillBoundingBox(axisalignedbb);
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }

   }

   public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(3, DefaultVertexFormats.POSITION);
      worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldrenderer.begin(3, DefaultVertexFormats.POSITION);
      worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldrenderer.begin(1, DefaultVertexFormats.POSITION);
      worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      tessellator.draw();
   }
}
