package net.badlion.client.gui;

import java.io.IOException;
import net.badlion.client.Wrapper;
import net.badlion.client.events.event.KeyPress;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.ModConfigurator;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.Scoreboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class GuiScreenEditing extends GuiScreen {
   private GuiScreen screen;
   private boolean tutorial = false;
   private transient CustomFontRenderer fontRenderer;
   private boolean confirm;

   public GuiScreenEditing(GuiScreen screen) {
      this.screen = screen;
   }

   protected void actionPerformed(GuiButton button) {
      if(button.id == 0) {
         this.mc.displayGuiScreen(this.screen);
         ModConfigurator modconfigurator = Wrapper.getInstance().getActiveModProfile().getModConfigurator();
         if(modconfigurator != null) {
            modconfigurator.setEditing(false);
         }

         if(this.screen == null) {
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().toggle();
         }
      } else if(button.id == 1) {
         if(this.confirm) {
            this.confirm = false;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);

            for(Mod mod : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
               if(mod instanceof RenderMod) {
                  RenderMod rendermod = (RenderMod)mod;
                  int i = -1;
                  if(rendermod.getSaveX() < 0 && rendermod.getSaveY() < 0) {
                     i = 0;
                  }

                  if(rendermod.getSaveX() >= 0 && rendermod.getSaveY() < 0) {
                     i = 1;
                  }

                  if(rendermod.getSaveX() < 0 && rendermod.getSaveY() >= 0) {
                     i = 2;
                  }

                  if(rendermod.getSaveX() >= 0 && rendermod.getSaveY() >= 0) {
                     i = 3;
                  }

                  double d0 = (double)(rendermod.startDisplayX / (2 * scaledresolution.getScaleFactor()));
                  double d1 = (double)(this.mc.displayWidth / (2 * scaledresolution.getScaleFactor()));
                  double d2 = (double)(rendermod.startDisplayY / (2 * scaledresolution.getScaleFactor()));
                  double d3 = (double)(this.mc.displayHeight / (2 * scaledresolution.getScaleFactor()));
                  int j = (int)(d0 - d1);
                  if(i == 1 || i == 3) {
                     j = -j;
                  }

                  int k = (int)(d2 - d3);
                  if(i == 2 || i == 3) {
                     k = -k;
                  }

                  rendermod.setPosition(this.mc.displayWidth / (2 * scaledresolution.getScaleFactor()) - rendermod.getSaveX() - j, this.mc.displayHeight / (2 * scaledresolution.getScaleFactor()) - rendermod.getSaveY() - k);
                  rendermod.startDisplayX = 854;
                  rendermod.startDisplayY = 480;
               }

               if(mod instanceof Scoreboard) {
                  ((Scoreboard)mod).setOffsetX(0);
                  ((Scoreboard)mod).setOffsetY(0);
               }
            }

            for(Mod mod1 : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
               if(mod1 instanceof RenderMod) {
                  RenderMod rendermod1 = (RenderMod)mod1;
                  rendermod1.reset();
               }
            }
         } else {
            this.confirm = true;
         }
      } else if(button.id == 2 && !this.tutorial) {
         this.tutorial = true;
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      boolean flag = false;

      for(Object object : this.buttonList) {
         GuiButton guibutton = (GuiButton)object;
         if(guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            flag = true;
         }
      }

      if(!flag) {
         this.confirm = false;
      }

      ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      int i = scaledresolution.getScaledWidth() / 2 - 70;
      int j = scaledresolution.getScaledHeight() / 2 - 45;
      if(mouseX >= i && mouseX <= i + 140 && mouseY >= j && mouseY <= j + 74) {
         this.tutorial = false;
      }

      try {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      } catch (IOException var9) {
         var9.printStackTrace();
      }

   }

   protected void keyTyped(char typedChar, int keyCode) {
      if(keyCode == 1) {
         this.mc.displayGuiScreen(this.screen);
         ModConfigurator modconfigurator = Wrapper.getInstance().getActiveModProfile().getModConfigurator();
         if(modconfigurator != null) {
            modconfigurator.setEditing(false);
         }

         if(this.screen == null) {
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().toggle();
         }
      } else {
         Wrapper.getInstance().getActiveModProfile().getModConfigurator().onEvent(new KeyPress(keyCode, true));
      }

   }

   public void initGui() {
      ScaledResolution scaledresolution = new ScaledResolution(this.mc);
      this.buttonList.add(new GuiButton(0, scaledresolution.getScaledWidth() / 2 - 50, scaledresolution.getScaledHeight() - 65, 100, 20, "Back", GuiButton.ButtonType.THICK_LINES));
      this.buttonList.add(new GuiButton(1, scaledresolution.getScaledWidth() - 105, scaledresolution.getScaledHeight() - 25, 100, 20, "Reset defaults", GuiButton.ButtonType.THICK_LINES));
      this.buttonList.add(new GuiButton(2, scaledresolution.getScaledWidth() / 2 - 50, scaledresolution.getScaledHeight() - 86, 100, 20, "Help", GuiButton.ButtonType.THICK_LINES));
      this.fontRenderer = new CustomFontRenderer();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if(this.buttonList != null && this.buttonList.size() != 0) {
         if(this.tutorial) {
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int i = scaledresolution.getScaledWidth() / 2 - 70;
            int j = scaledresolution.getScaledHeight() / 2 - 45;
            int k = 140;
            int l = 74;
            Gui.drawRect(i, j, i + k, j + l, -14540254);
            i = i - 4;
            double d0 = 1.25D;
            GL11.glScaled(d0, d0, 1.0D);
            int currentY = j + 2;
            this.drawString("Gui Editing Explained", i, currentY, d0, k, -1);
            GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D);
            double d1 = 0.8D;
            GL11.glScaled(d1, d1, 1.0D);
            currentY = currentY + 15;
            this.drawString("Mouse 1 - Hold | drag mod", i, currentY, d1, k, -1);
            currentY = currentY + 10;
            this.drawString("Mouse 1 - Click | select mod", i, currentY, d1, k, -1);
            currentY = currentY + 10;
            this.drawString("Arrow keys to move selected", i, currentY, d1, k, -1);
            currentY = currentY + 8;
            this.drawString("mod with precision", i, currentY, d1, k, -1);
            GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D);
            currentY = currentY + 11;
            this.drawString("Click anywhere to close", i, currentY, k, -1);
         }

         if(this.confirm) {
            ((GuiButton)this.buttonList.get(1)).displayString = "Are you sure?";
         } else {
            ((GuiButton)this.buttonList.get(1)).displayString = "Reset defaults";
         }
      } else {
         this.initGui();
      }

   }

   public void drawString(String text, int baseX, int baseY, double textScale, int width, int color) {
      double d0 = (double)width - (double)this.fontRenderer.getStringWidth(text) * textScale;
      d0 = d0 / 2.0D;
      this.fontRenderer.drawString(text, (int)(((double)baseX + d0) / textScale), (int)((double)baseY / textScale), color);
   }

   public void drawString(String text, int baseX, int baseY, int width, int color) {
      this.drawString(text, baseX, baseY, 1.0D, width, color);
   }
}
