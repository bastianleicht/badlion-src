package net.badlion.client.mods.render;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.input.Mouse;

public class Scoreboard extends RenderMod {
   private transient TextButton showNumbersButton;
   private MutableBoolean showNumbers = new MutableBoolean(true);
   private ModColor backgroundColor = new ModColor(1342177280);
   private ModColor topBackground = new ModColor(1610612736);
   private int startMouseX;
   private int startMouseY;
   private int tempOffsetX;
   private int tempOffsetY;
   private int offsetX;
   private int offsetY;
   private boolean hovered;

   public Scoreboard() {
      super("Scoreboard", 250, -40, 1, 1);
      this.iconDimension = new ImageDimension(84, 60);
      this.defaultTopLeftBox = new BoxedCoord(0, 0, 0.21248339973439576D, 0.12052730696798493D);
      this.defaultCenterBox = new BoxedCoord(0, 0, 0.3144754316069057D, 0.3013182674199623D);
      this.defaultBottomRightBox = new BoxedCoord(0, 0, 0.41646746347941566D, 0.4821092278719397D);
   }

   public void init() {
      this.setFontOffset(0.034D);
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.GUI_CLICK_MOUSE);
      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.showNumbersButton = new TextButton("Show Numbers", this.showNumbers, 1.0D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.backgroundColor.init();
      this.slideCogMenu.addElement(new ColorPicker("Top Background Color", this.topBackground, 0.13D, true));
      this.topBackground.init();
      super.createCogMenu();
   }

   public void reset() {
      this.showNumbersButton.setEnabled(true);
      this.offsetX = 0;
      this.offsetY = 0;
      this.backgroundColor.setColor(1342177280);
      this.backgroundColor.setEnabled(true);
      this.backgroundColor.setMode(ModColor.DynamicColorMode.STATIC);
      this.topBackground.setColor(1610612736);
      this.topBackground.setEnabled(true);
      this.topBackground.setMode(ModColor.DynamicColorMode.STATIC);
      super.reset();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.backgroundColor.tickColor();
         this.topBackground.tickColor();
      }

      if(e instanceof GUIClickMouse && this.hovered && Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
         this.startMouseX = Wrapper.getInstance().getMouseX();
         this.startMouseY = Wrapper.getInstance().getMouseY();
         this.tempOffsetX = this.offsetX;
         this.tempOffsetY = this.offsetY;
      }

      if(e instanceof RenderGame && this.isEnabled()) {
         if(Mouse.isButtonDown(0) && this.startMouseX != 0 && this.startMouseY != 0 && Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
            int i = Wrapper.getInstance().getMouseX();
            int j = Wrapper.getInstance().getMouseY();
            int k = i - this.startMouseX;
            int l = j - this.startMouseY;
            if((new ScaledResolution(this.gameInstance)).getScaleFactor() < 2) {
               k *= 2;
               l *= 2;
            }

            this.offsetX = this.tempOffsetX + k;
            this.offsetY = this.tempOffsetY + l;
         } else {
            this.startMouseX = this.startMouseY = 0;
         }
      }

      super.onEvent(e);
   }

   public void setHovered(boolean hovered) {
      this.hovered = hovered;
   }

   public void setOffsetX(int offsetX) {
      this.offsetX = offsetX;
   }

   public void setOffsetY(int offsetY) {
      this.offsetY = offsetY;
   }

   public int getOffsetX() {
      return this.offsetX;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   public boolean shouldShowNumbers() {
      return this.showNumbers.booleanValue();
   }

   public ModColor getTopBackground() {
      return this.topBackground;
   }

   public ModColor getBackgroundColor() {
      return this.backgroundColor;
   }

   public int getBackgroundColorInt() {
      return this.backgroundColor.getColorInt();
   }

   public int getTopBackgroundColorInt() {
      return this.topBackground.getColorInt();
   }

   public static void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
      Scoreboard scoreboard = Wrapper.getInstance().getActiveModProfile().getScoreboard();
      if(scoreboard.isEnabled()) {
         net.minecraft.scoreboard.Scoreboard scoreboard1 = objective.getScoreboard();
         Collection<Score> collection = scoreboard1.getSortedScores(objective);
         List<Score> list = Lists.newArrayList(Iterables.filter(collection, (Predicate)(new Predicate() {
            public boolean apply(Score p_apply_1_) {
               return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
         })));
         if(list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
         } else {
            collection = list;
         }

         FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
         int i = fontrenderer.getStringWidth(objective.getDisplayName());

         for(Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard1.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            i = Math.max(i, fontrenderer.getStringWidth(s));
         }

         int i1 = collection.size() * fontrenderer.FONT_HEIGHT;
         int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
         int k1 = 3;
         int l1 = scaledRes.getScaledWidth() - i - k1;
         int j = 0;
         l1 = l1 + scoreboard.getOffsetX();

         for(Score score1 : collection) {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard1.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = scoreboard.shouldShowNumbers()?"" + EnumChatFormatting.RED + score1.getScorePoints():"";
            int k = j1 - j * fontrenderer.FONT_HEIGHT;
            k = k + scoreboard.getOffsetY();
            int l = scaledRes.getScaledWidth() - k1 + 2;
            if(scoreboard.getBackgroundColor().isEnabled()) {
               Gui.drawRect(l1 - 2, k, l, k + fontrenderer.FONT_HEIGHT, scoreboard.getBackgroundColorInt());
            }

            fontrenderer.drawString(s1, l1, k, 553648127);
            fontrenderer.drawString(s2, l - fontrenderer.getStringWidth(s2), k, 553648127);
            if(j == collection.size()) {
               String s3 = objective.getDisplayName();
               if(scoreboard.getTopBackground().isEnabled()) {
                  Gui.drawRect(l1 - 2, k - fontrenderer.FONT_HEIGHT - 1, l, k - 1, scoreboard.getTopBackgroundColorInt());
               }

               if(scoreboard.getBackgroundColor().isEnabled()) {
                  Gui.drawRect(l1 - 2, k - 1, l, k, scoreboard.getBackgroundColorInt());
               }

               fontrenderer.drawString(s3, l1 + i / 2 - fontrenderer.getStringWidth(s3) / 2, k - fontrenderer.FONT_HEIGHT, 553648127);
            }
         }
      }

   }
}
