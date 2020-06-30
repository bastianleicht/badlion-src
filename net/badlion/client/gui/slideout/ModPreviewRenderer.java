package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.mods.render.RenderMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ModPreviewRenderer extends RenderElement {
   private int offsetX;
   private int offsetY;
   private int extraY;
   private RenderMod mod;
   private boolean editMode = true;

   public ModPreviewRenderer(RenderMod mod, int offsetX, int offsetY) {
      this.mod = mod;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
   }

   public ModPreviewRenderer(RenderMod mod, int offsetX, int offsetY, boolean editMode) {
      this.mod = mod;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.editMode = editMode;
   }

   public ModPreviewRenderer(RenderMod mod, int offsetX, int offsetY, boolean editMode, int extraY) {
      this.mod = mod;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.editMode = editMode;
      this.extraY = extraY;
   }

   public void render() {
      new ScaledResolution(Minecraft.getMinecraft());
      int i = this.mod.x;
      int j = this.mod.y;
      boolean flag = this.mod.isEnabled();
      this.mod.setEnabled(true);
      int k = this.mod.getSizeX();
      int l = this.mod.getSizeY();
      this.mod.setSize((double)this.mod.getDefaultSizeX(), (double)this.mod.getDefaultSizeY());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if(this.editMode) {
         Wrapper.getInstance().getActiveModProfile().getModConfigurator().setEditing(true);
      }

      boolean flag1 = this.mod.getSizeY() < 20;
      int i1 = Math.max(20, this.mod.getSizeY());
      int j1 = (int)(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - (double)Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8.0D);
      j1 = (int)((double)j1 + (Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - 8.0D) / 2.0D);
      j1 = (int)((double)j1 - (double)this.mod.getSizeX() / 2.0D);
      int k1 = this.getY() + 3;
      if(flag1) {
         k1 = (int)((double)(this.getY() - 2 + 14) - (double)this.mod.getSizeY() / 2.0D);
      }

      int l1 = (int)(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - (double)Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8.0D);
      int i2 = (int)(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - 8.0D);
      int j2 = this.mod.getSizeX();
      int k2 = l1 + (i2 - l1 - j2) / 2;
      if(this.mod.getSizeX() < Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 16) {
         Gui.drawRect((int)(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - (double)Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8.0D), this.getY() - 2, (int)(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide - 8.0D), this.getY() + i1 + this.extraY + 2, 855638016);
      }

      GL11.glPushMatrix();
      int l2 = Wrapper.getInstance().getScaleFactor();
      GL11.glScaled((double)l2, (double)l2, 1.0D);
      this.mod.setForcedRenderPos(k2, (int)((double)k1));
      this.mod.onEvent(new RenderGame(Minecraft.getMinecraft().ingameGUI));
      this.mod.setForcedRenderPos(-1, -1);
      GL11.glPopMatrix();
      Wrapper.getInstance().getActiveModProfile().getModConfigurator().setEditing(false);
      this.mod.setPosition2(i, j);
      this.mod.setSize((double)k, (double)l);
      this.mod.setEnabled(flag);
   }

   public int getOffsetX() {
      return this.offsetX;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   public RenderMod getMod() {
      return this.mod;
   }

   public int getWidth() {
      return Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 16;
   }

   public int getHeight() {
      return Math.max(20, this.mod.getDefaultSizeY());
   }
}
