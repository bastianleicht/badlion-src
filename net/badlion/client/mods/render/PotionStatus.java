package net.badlion.client.mods.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.PotionCircle;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.DisplayUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class PotionStatus extends RenderMod {
   private transient Slider blinkingSlider;
   private double blinking;
   private int blinkAt;
   private PotionStatus.PotionStatusType potionStatusType = PotionStatus.PotionStatusType.VANILLA;
   private ModColor timeTextColor = new ModColor(-5592321);
   private ModColor potionTextColor = new ModColor(-1);
   private MutableBoolean vanillaPotions = new MutableBoolean(true);
   private transient Dropdown typeDropdown;
   protected static final transient ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType;

   public void reset() {
      this.potionStatusType = PotionStatus.PotionStatusType.VANILLA;
      this.defaultSizeY = this.potionStatusType.y;
      this.defaultSizeX = this.potionStatusType.x;
      this.timeTextColor.setColor(-5592321);
      this.potionTextColor.setColor(-1);
      this.vanillaPotions.setValue(true);
      this.blinking = 0.0D;
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 15));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.typeDropdown = new Dropdown(new String[]{"Minimal", "Vanilla"}, this.potionStatusType.equals(PotionStatus.PotionStatusType.VANILLA)?1:0, 0.19D));
      this.slideCogMenu.addElement(this.blinkingSlider = new Slider("Start blinking at:", 0.0D, 1.0D, this.blinking, 0.2D));
      this.blinkingSlider.setDisplayText(new String[]{"Disabled", "1s", "2s", "3s", "4s", "5s", "6s", "7s", "8s", "9s", "10s", "11s", "12s", "13s", "14s", "15s", "16s", "17s", "18s", "19s", "20s", "21s", "22s", "23s", "24s", "25s", "26s", "27s", "28s", "29s", "30s", "31s", "32s", "33s", "34s", "35s", "36s", "37s", "38s", "39s", "40s", "41s", "42s", "43s", "44s", "45s", "46s", "47s", "48s", "49s", "50s", "51s", "52s", "53s", "54s", "55s", "56s", "57s", "58s", "59s", "60s"});
      this.blinkingSlider.init();
      this.slideCogMenu.addElement(new ColorPicker("Time Text Color", this.timeTextColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Potion Name Color", this.potionTextColor, 0.13D));
      this.slideCogMenu.addElement(new TextButton("Vanilla Potions", this.vanillaPotions, 1.0D));
      this.timeTextColor.init();
      this.potionTextColor.init();
      this.defaultSizeY = this.potionStatusType.y;
      this.defaultSizeX = this.potionStatusType.x;
      super.createCogMenu();
   }

   public PotionStatus() {
      super("PotionStatus", -210, -6, PotionStatus.PotionStatusType.VANILLA.x, PotionStatus.PotionStatusType.VANILLA.y);
      this.iconDimension = new ImageDimension(58, 84);
      this.defaultTopLeftBox = new BoxedCoord(0, 4, 0.0D, 0.9481481481481482D);
      this.defaultCenterBox = new BoxedCoord(1, 9, 0.7666666666666667D, 0.32592592592592595D);
      this.defaultBottomRightBox = new BoxedCoord(3, 13, 0.55D, 0.7111111111111111D);
   }

   public void init() {
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.setFontOffset(0.017D);
      super.init();
   }

   private void drawActivePotionEffects(GuiIngame gameRenderer, Collection collection) {
      int i = 0;
      int j = 0;
      this.beginRender();
      if(!collection.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int k = 25;
         if(collection.size() > 5) {
            k = 132 / (collection.size() - 1);
         }

         for(Object object : collection) {
            PotionEffect potioneffect = (PotionEffect)object;
            if(this.blinkAt == -1 || potioneffect.getDuration() / 20 > this.blinkAt || potioneffect.getDuration() / 20 <= this.blinkAt && System.currentTimeMillis() % 2000L > 1000L) {
               Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               this.gameInstance.getTextureManager().bindTexture(inventoryBackground);
               this.gameInstance.fontRendererObj.drawString("", 0, 0, 1118481);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               if(potion.hasStatusIcon()) {
                  int l = potion.getStatusIconIndex();
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  gameRenderer.drawTexturedModalRect(i + 6, j + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
               }

               String s1 = I18n.format(potion.getName(), new Object[0]);
               if(potioneffect.getAmplifier() == 1) {
                  s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
               } else if(potioneffect.getAmplifier() == 2) {
                  s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
               } else if(potioneffect.getAmplifier() == 3) {
                  s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
               }

               this.gameInstance.fontRendererObj.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), this.potionTextColor.getColorInt());
               String s = Potion.getDurationString(potioneffect);
               this.gameInstance.fontRendererObj.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), this.timeTextColor.getColorInt());
            }

            j += k;
         }

         GL11.glDisable(2896);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      this.endRender();
   }

   public void onEvent(Event event) {
      if(event instanceof MotionUpdate) {
         PotionStatus.PotionStatusType potionstatus$potionstatustype = this.typeDropdown.getValue().equals("Minimal")?PotionStatus.PotionStatusType.CIRCLE:PotionStatus.PotionStatusType.VANILLA;
         if(potionstatus$potionstatustype != this.potionStatusType) {
            this.potionStatusType = potionstatus$potionstatustype;
            int i = this.defaultSizeX;
            int j = this.defaultSizeY;
            this.defaultSizeX = this.potionStatusType.x;
            this.defaultSizeY = this.potionStatusType.y;
            this.sizeX = (double)((int)(this.sizeX / (double)i * (double)this.defaultSizeX));
            this.sizeY = (double)((int)(this.sizeY / (double)j * (double)this.defaultSizeY));
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.blinking = this.blinkingSlider.getValue();

         try {
            this.blinkAt = Integer.parseInt(this.blinkingSlider.getCurrentDisplayText().substring(0, this.blinkingSlider.getCurrentDisplayText().length() - 1));
         } catch (NumberFormatException var10) {
            this.blinkAt = -1;
         }

         this.timeTextColor.tickColor();
         this.potionTextColor.tickColor();
      }

      if(event instanceof RenderGame && this.isEnabled()) {
         RenderGame rendergame = (RenderGame)event;
         switch($SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType()[this.potionStatusType.ordinal()]) {
         case 1:
            GL11.glColor3d(1.0D, 1.0D, 1.0D);
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               Collection<PotionEffect> collection = new ArrayList();
               collection.add(new PotionEffect(1, 20, 1));
               collection.add(new PotionEffect(2, 40, 1));
               collection.add(new PotionEffect(3, 60, 1));
               collection.add(new PotionEffect(4, 80, 1));
               collection.add(new PotionEffect(5, 100, 1));
               collection.add(new PotionEffect(Potion.nightVision.id, 120, 1));
               this.drawActivePotionEffects(rendergame.getGameRenderer(), collection);
            } else if(this.gameInstance.thePlayer.getActivePotionEffects().size() > 0) {
               this.drawActivePotionEffects(rendergame.getGameRenderer(), this.gameInstance.thePlayer.getActivePotionEffects());
            }
            break;
         case 2:
            int k = 0;
            int l = 0;
            int i1 = 0;
            int j1 = 0;
            this.beginRender();
            List<PotionCircle> list;
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               list = Arrays.asList(new PotionCircle[]{new PotionCircle(new PotionEffect(1, 20, 1)), new PotionCircle(new PotionEffect(2, 40, 1)), new PotionCircle(new PotionEffect(3, 60, 1)), new PotionCircle(new PotionEffect(4, 80, 1)), new PotionCircle(new PotionEffect(5, 100, 1)), new PotionCircle(new PotionEffect(Potion.nightVision.id, 120, 1))});
            } else {
               list = new ArrayList();

               for(PotionEffect potioneffect : this.gameInstance.thePlayer.getActivePotionEffects()) {
                  list.add(new PotionCircle(potioneffect));
               }
            }

            if(i1 - 13 > DisplayUtil.getCenterX()) {
               k = 26;
            }

            for(PotionCircle potioncircle : list) {
               if(potioncircle.getType().getDuration() >= 20) {
                  if(this.blinkAt == -1 || potioncircle.getType().getDuration() / 20 > this.blinkAt || potioncircle.getType().getDuration() / 20 <= this.blinkAt && System.currentTimeMillis() % 2000L > 1000L) {
                     potioncircle.render(rendergame.getGameRenderer(), (int)((double)i1 / this.getScaleX() + (double)k), (int)((double)j1 / this.getScaleY() + (double)l), this.timeTextColor.getColorInt());
                  }

                  l += 25;
                  if(l > 95) {
                     l = 0;
                     if(i1 - 13 > DisplayUtil.getCenterX()) {
                        k -= 26;
                     } else {
                        k += 26;
                     }
                  }
               }
            }

            this.endRender();
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(event);
   }

   public boolean allowsVanillaPotions() {
      return !this.isEnabled() || this.vanillaPotions.getValue().booleanValue();
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType;
      if($SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType != null) {
         return var10000;
      } else {
         int[] var0 = new int[PotionStatus.PotionStatusType.values().length];

         try {
            var0[PotionStatus.PotionStatusType.CIRCLE.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[PotionStatus.PotionStatusType.VANILLA.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$PotionStatus$PotionStatusType = var0;
         return var0;
      }
   }

   private static enum PotionStatusType {
      VANILLA(115, 160),
      CIRCLE(50, 100);

      private int x;
      private int y;

      private PotionStatusType(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }
}
