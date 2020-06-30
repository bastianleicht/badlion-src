package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ShowArrows extends RenderMod {
   private ModColor labelColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);

   public ShowArrows() {
      super("ShowArrows", 174, -115, 18, 28);
      this.iconDimension = new ImageDimension(78, 78);
      this.defaultTopLeftBox = new BoxedCoord(19, 30, 0.3333333333333333D, 0.7703703703703704D);
      this.defaultCenterBox = new BoxedCoord(19, 31, 0.6666666666666666D, 0.6814814814814815D);
      this.defaultBottomRightBox = new BoxedCoord(20, 32, 0.0D, 0.6222222222222222D);
   }

   public void init() {
      this.setDisplayName("Show Arrows");
      this.setFontOffset(0.013D);
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void reset() {
      this.labelColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 0, true, 6));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 4));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new ColorPicker("Text Color", this.labelColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.labelColor.init();
      this.backgroundColor.init();
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.labelColor.tickColor();
         this.backgroundColor.tickColor();
      }

      if(e instanceof RenderGame && this.isEnabled()) {
         RenderGame rendergame = (RenderGame)e;
         int i = Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()?64:this.getRemainingArrows();
         int j = 2;
         int k = 0;
         int l = 0;
         this.beginRender();
         int i1 = k + 14 + j * 2;
         int j1 = l + 24 + j * 2;
         Gui.drawRect(k, l, i1, j1, this.backgroundColor.getColorInt());
         int k1 = (i1 - this.gameInstance.fontRendererObj.getStringWidth(String.valueOf(i))) / 2;
         rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, String.valueOf(i), k1, 17 + l, this.labelColor.getColorInt());
         RenderHelper.enableStandardItemLighting();
         this.gameInstance.getRenderItem().renderItemIntoGUI(new ItemStack(Items.arrow), k, l);
         RenderHelper.disableStandardItemLighting();
         this.endRender();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(e);
   }

   private ItemStack getFirstArrow() {
      for(ItemStack itemstack : this.gameInstance.thePlayer.inventory.mainInventory) {
         if(itemstack != null && itemstack.getItem().equals(Items.arrow)) {
            return itemstack;
         }
      }

      return null;
   }

   private int getRemainingArrows() {
      int i = 0;

      for(ItemStack itemstack : this.gameInstance.thePlayer.inventory.mainInventory) {
         if(itemstack != null && itemstack.getItem().equals(Items.arrow)) {
            i += itemstack.stackSize;
         }
      }

      return i;
   }
}
