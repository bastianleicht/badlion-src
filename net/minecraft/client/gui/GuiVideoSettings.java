package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiVideoSettings extends GuiScreen {
   private GuiScreen parentGuiScreen;
   protected String screenTitle = "Video Settings";
   private GameSettings guiGameSettings;
   private GuiListExtended optionsRowList;
   private static final GameSettings.Options[] videoOptions = new GameSettings.Options[]{GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.ANAGLYPH, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.PARTICLES, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.ENABLE_VSYNC, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.BLOCK_ALTERNATIVES, GameSettings.Options.USE_VBO, GameSettings.Options.ENTITY_SHADOWS};

   public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   public void initGui() {
      this.screenTitle = I18n.format("options.videoTitle", new Object[0]);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done", new Object[0])));
      if(!OpenGlHelper.vboSupported) {
         GameSettings.Options[] agamesettings$options = new GameSettings.Options[videoOptions.length - 1];
         int i = 0;

         for(GameSettings.Options gamesettings$options : videoOptions) {
            if(gamesettings$options == GameSettings.Options.USE_VBO) {
               break;
            }

            agamesettings$options[i] = gamesettings$options;
            ++i;
         }

         this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, agamesettings$options);
      } else {
         this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, videoOptions);
      }

   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.optionsRowList.handleMouseInput();
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.enabled && button.id == 200) {
         this.mc.gameSettings.saveOptions();
         this.mc.displayGuiScreen(this.parentGuiScreen);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      int i = this.guiGameSettings.particleSetting;
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.optionsRowList.mouseClicked(mouseX, mouseY, mouseButton);
      if(this.guiGameSettings.particleSetting != i) {
         ScaledResolution scaledresolution = new ScaledResolution(this.mc);
         int j = scaledresolution.getScaledWidth();
         int k = scaledresolution.getScaledHeight();
         this.setWorldAndResolution(this.mc, j, k);
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      int i = this.guiGameSettings.particleSetting;
      super.mouseReleased(mouseX, mouseY, state);
      this.optionsRowList.mouseReleased(mouseX, mouseY, state);
      if(this.guiGameSettings.particleSetting != i) {
         ScaledResolution scaledresolution = new ScaledResolution(this.mc);
         int j = scaledresolution.getScaledWidth();
         int k = scaledresolution.getScaledHeight();
         this.setWorldAndResolution(this.mc, j, k);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 5, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
