package net.badlion.client.gui.slideout.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.gui.InputField;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SimpleButton;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ColorPicker extends RenderElement {
   private transient boolean allowDisable;
   private List quickColorButtons;
   private final String name;
   private final int defaultColor;
   private double scale;
   private int color;
   private boolean modalOpen;
   private float hue;
   private float saturation;
   private float brightness;
   private float alpha;
   private SimpleButton rainbowButton;
   private SimpleButton breathingButton;
   private transient SimpleButton applyToOtherButton;
   private InputField hexField;
   public static final ResourceLocation buttonOn = new ResourceLocation("textures/slideout/cogwheel/active.png");
   public static final ResourceLocation buttonOff = new ResourceLocation("textures/slideout/cogwheel/deactive.png");
   private ResourceLocation colorPicker;
   private static final ResourceLocation colorPickerBackground = new ResourceLocation("textures/slideout/cogwheel/color-picker-bg-gradient.png");
   private boolean dragMainColorBox;
   private boolean dragHueSlider;
   private boolean dragAlphaSlider;
   private ModColor modColor;
   private int colorBoxHeight;
   private int colorBoxWidth;
   private int colorBoxTextHeightOffset;
   private int slideoutHalfX;
   private int mainColorBoxSize;
   private int paddingX;
   private int paddingX2;
   private int paddingY;
   private int hueSelectorWidth;
   private int hueColorSpacing;
   private int alphaSelectorWidth;
   private int textPaddingTop;
   private int textYOffset;
   private int textPaddingBottom;
   private int buttonXOffset;
   private int hexButtonWidth;
   private int presetColorsWidth;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode;

   public ColorPicker(String name, ModColor color, double scale) {
      this.allowDisable = false;
      this.modalOpen = false;
      this.colorPicker = new ResourceLocation("textures/slideout/cogwheel/color-picker-circle.svg_large.png");
      this.colorBoxHeight = 12;
      this.colorBoxWidth = 80;
      this.colorBoxTextHeightOffset = 11;
      this.slideoutHalfX = (int)((float)Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() / 2.0F) - 8;
      this.mainColorBoxSize = 66;
      this.paddingX = 7;
      this.paddingX2 = 4;
      this.paddingY = 10;
      this.hueSelectorWidth = 17;
      this.hueColorSpacing = 11;
      this.alphaSelectorWidth = 17;
      this.textPaddingTop = 2;
      this.textYOffset = 7;
      this.textPaddingBottom = 8;
      this.buttonXOffset = 32;
      this.hexButtonWidth = 100;
      this.presetColorsWidth = 17;
      this.name = name;
      this.defaultColor = color.getColorInt();
      this.color = this.defaultColor;
      this.modColor = color;
      int i = ColorUtil.getRed(this.defaultColor);
      int j = ColorUtil.getGreen(this.defaultColor);
      int k = ColorUtil.getBlue(this.defaultColor);
      this.alpha = (float)ColorUtil.getAlpha(this.defaultColor) / 255.0F;
      float[] afloat = new float[3];
      float[] afloat1 = Color.RGBtoHSB(i, j, k, afloat);
      this.hue = afloat1[0];
      this.saturation = afloat1[1];
      this.brightness = afloat1[2];
      this.scale = scale;
   }

   public ColorPicker(String name, ModColor color, double scale, boolean allowDisable) {
      this(name, color, scale);
      this.allowDisable = allowDisable;
   }

   public ModColor getModColor() {
      return this.modColor;
   }

   public String getName() {
      return this.name;
   }

   public void init() {
      this.rainbowButton = new SimpleButton("Rainbow", -12040120, -11119018, -1, true, 8);
      this.breathingButton = new SimpleButton("Breathing", -12040120, -11119018, -1, true, 8);
      this.applyToOtherButton = new SimpleButton("Apply to others", -12040120, -11119018, -1, true, 8);
      this.rainbowButton.init();
      this.breathingButton.init();
      this.applyToOtherButton.init();
      this.rainbowButton.setTextScale(0.75D);
      this.rainbowButton.setOffsetY(1);
      this.breathingButton.setTextScale(0.65D);
      this.breathingButton.setOffsetY(2);
      this.applyToOtherButton.setTextScale(0.75D);
      this.quickColorButtons = new ArrayList();

      int[] var4;
      for(int i : var4 = new int[]{-16777216, -2302756, -1310720, -214856, -197448, -3867584, -16716800, -4653828, -12537604, -16776980, -204748, -214788, -204592, -4951996, -16751516, -6497060}) {
         this.quickColorButtons.add(new SimpleButton("", i, i, i));
      }

      int j = 30;
      int k = 10;
      this.rainbowButton.setSize(j, k);
      this.breathingButton.setSize(j, k);
      this.applyToOtherButton.setSize(j * 2 + 3, k);
      if(this.modColor == null) {
         this.modColor = new ModColor(-1);
      }

      String s = String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf((int)(this.alpha * 255.0F)), Integer.valueOf(this.modColor.getColor().getRed()), Integer.valueOf(this.modColor.getColor().getGreen()), Integer.valueOf(this.modColor.getColor().getBlue())}).toUpperCase(Locale.US);
      this.hexField = new InputField(110, 9, false, s, "#", 8, InputField.InputFlavor.HEX_COLOR);
      this.hexField.init();
      switch($SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode()[this.modColor.getMode().ordinal()]) {
      case 1:
         this.breathingButton.setColor(-12019384);
         this.breathingButton.setHoverColor(-10905255);
         break;
      case 2:
         this.rainbowButton.setColor(-12019384);
         this.rainbowButton.setHoverColor(-10905255);
      }

      super.init();
   }

   public SimpleButton getRainbowButton() {
      return this.rainbowButton;
   }

   public SimpleButton getBreathingButton() {
      return this.breathingButton;
   }

   public double getScale() {
      return this.scale;
   }

   public void updateColor(Color updateColor) {
      this.modColor.setColor(this.color);
      if(updateColor != null) {
         this.hexField.setText(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf((int)(this.alpha * 255.0F)), Integer.valueOf(updateColor.getRed()), Integer.valueOf(updateColor.getGreen()), Integer.valueOf(updateColor.getBlue())}).toUpperCase(Locale.US));
         this.hexField.setTextColor(-1);
      }

   }

   public void setDynamicColorMode(ModColor.DynamicColorMode mode) {
      this.modColor.setMode(mode);
   }

   public void update(int mouseX, int mouseY) {
      int i = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8;
      int j = Math.min(this.getY(), Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - 135);
      this.rainbowButton.update(mouseX, mouseY);
      this.breathingButton.update(mouseX, mouseY);
      this.applyToOtherButton.update(mouseX, mouseY);
      this.hexField.update(mouseX, mouseY);

      for(SimpleButton simplebutton : this.quickColorButtons) {
         simplebutton.update(mouseX, mouseY);
      }

      boolean flag = false;
      if(this.dragMainColorBox && this.modColor.isEnabled()) {
         int l = Math.min(i + this.paddingX + this.mainColorBoxSize, Math.max(i + this.paddingX, mouseX));
         int k = Math.min(j + this.paddingY + this.mainColorBoxSize, Math.max(j + this.paddingY, mouseY));
         float f = (float)(l - (i + this.paddingX));
         float f1 = (float)(j + this.paddingY + this.mainColorBoxSize - k);
         this.saturation = (float)((double)(f / (float)this.mainColorBoxSize) + 0.0D);
         this.brightness = (float)((double)(f1 / (float)this.mainColorBoxSize) + 0.0D);
         if(!Mouse.isButtonDown(0)) {
            this.dragMainColorBox = false;
         }

         flag = true;
      }

      if(this.dragHueSlider && this.modColor.isEnabled()) {
         int i1 = Math.min(j + this.paddingY + this.mainColorBoxSize, Math.max(j + this.paddingY, mouseY));
         float f2 = (float)(i1 - (j + this.paddingY));
         this.hue = Math.min(0.99F, f2 / (float)this.mainColorBoxSize + 0.0F);
         if(!Mouse.isButtonDown(0)) {
            this.dragHueSlider = false;
         }

         flag = true;
      }

      if(this.dragAlphaSlider && this.modColor.isEnabled()) {
         int j1 = Math.min(j + this.paddingY + this.mainColorBoxSize, Math.max(j + this.paddingY, mouseY));
         float f3 = (float)(j + this.paddingY + this.mainColorBoxSize - j1);
         this.alpha = f3 / (float)this.mainColorBoxSize;
         if((double)this.alpha < 0.11D) {
            this.alpha = 0.11F;
         }

         if(!Mouse.isButtonDown(0)) {
            this.dragAlphaSlider = false;
         }

         flag = true;
      }

      if(flag) {
         Color color = ColorUtil.hsvToRgb(this.hue, this.saturation, this.brightness);
         this.color = (int)Long.parseLong(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf((int)(this.alpha * 255.0F)), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}), 16);
         this.updateColor(color);
      }

   }

   public void setColor(int colorRgb) {
      Color color = new Color(colorRgb);
      this.color = (int)Long.parseLong(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf((int)(this.alpha * 255.0F)), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}), 16);
      this.updateColor(color);
   }

   public boolean onClick(int mouseButton) {
      if(mouseButton == 0) {
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         int k = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8;
         int l = Math.min(this.getY(), Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - 135);
         if(!this.modalOpen) {
            if(this.modColor.isEnabled()) {
               int k1 = (int)((float)this.colorBoxWidth / 2.0F);
               if(i > this.getX() + this.slideoutHalfX * 2 - 30 && i < this.getX() + this.slideoutHalfX * 2 && j > this.getY() && j < this.getY() + 12) {
                  this.modalOpen = true;
               }
            }
         } else {
            this.rainbowButton.onClick(mouseButton);
            this.breathingButton.onClick(mouseButton);
            this.applyToOtherButton.onClick(mouseButton);
            this.hexField.onClick(mouseButton);

            for(SimpleButton simplebutton : this.quickColorButtons) {
               simplebutton.onClick(mouseButton);
               if(simplebutton.isSelected()) {
                  Color color = new Color(simplebutton.getColor());
                  this.hexField.setText(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf((int)(this.alpha * 255.0F)), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}).toUpperCase(Locale.US));
                  this.color = (int)Long.parseLong(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf(color.getAlpha()), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}), 16);
                  this.updateColor((Color)null);
                  this.hexField.setTextColor(-1);
                  float[] afloat = new float[3];
                  float[] afloat1 = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), afloat);
                  this.hue = afloat1[0];
                  this.saturation = afloat1[1];
                  this.brightness = afloat1[2];
               }
            }

            if(this.rainbowButton.isSelected()) {
               if(this.modColor.getMode().equals(ModColor.DynamicColorMode.RAINBOW)) {
                  this.setDynamicColorMode(ModColor.DynamicColorMode.STATIC);
                  this.rainbowButton.setColor(-12040120);
                  this.rainbowButton.setHoverColor(-10921639);
               } else {
                  this.setDynamicColorMode(ModColor.DynamicColorMode.RAINBOW);
                  this.breathingButton.setColor(-12040120);
                  this.breathingButton.setHoverColor(-10921639);
                  this.rainbowButton.setHoverColor(-10905255);
                  this.rainbowButton.setColor(-12019384);
               }
            }

            if(this.breathingButton.isSelected()) {
               if(this.modColor.getMode().equals(ModColor.DynamicColorMode.BREATHING)) {
                  this.setDynamicColorMode(ModColor.DynamicColorMode.STATIC);
                  this.breathingButton.setColor(-12040120);
                  this.breathingButton.setHoverColor(-10921639);
               } else {
                  this.setDynamicColorMode(ModColor.DynamicColorMode.BREATHING);
                  this.rainbowButton.setColor(-12040120);
                  this.rainbowButton.setHoverColor(-10921639);
                  this.breathingButton.setColor(-12019384);
                  this.breathingButton.setHoverColor(-10905255);
               }
            }

            if(this.applyToOtherButton.isSelected()) {
               Wrapper.getInstance().getActiveModProfile().getChangeColorMod().beforeOpen(this.modColor, Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSelectedPage());
               Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage(Wrapper.getInstance().getActiveModProfile().getChangeColorMod().getName());
            }

            int j1 = this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2 + this.alphaSelectorWidth + this.paddingX + this.presetColorsWidth + this.paddingX;
            int i2 = this.paddingX + this.mainColorBoxSize + 50 + this.paddingX;
            if(i > k && i < k + j1 && j > l && j < l + i2) {
               if(i > k + this.paddingX && i < k + this.paddingX + this.mainColorBoxSize && j > l + this.paddingY && j < l + this.paddingY + this.mainColorBoxSize) {
                  float f1 = (float)(i - (k + this.paddingX));
                  float f2 = (float)(l + this.paddingY + this.mainColorBoxSize - j);
                  this.saturation = (float)((double)(f1 / (float)this.mainColorBoxSize) + 0.0D);
                  this.brightness = (float)((double)(f2 / (float)this.mainColorBoxSize) + 0.0D);
                  this.dragMainColorBox = true;
                  return false;
               }

               int j2 = k + this.paddingX + this.mainColorBoxSize + this.paddingX;
               int k2 = k + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth;
               if(i > j2 && i < k2 && j > l + this.paddingY && j < l + this.paddingY + this.mainColorBoxSize) {
                  float f3 = (float)(l + this.paddingY + this.mainColorBoxSize - j);
                  this.hue = Math.min(0.99F, f3 / (float)this.mainColorBoxSize + 0.0F);
                  this.dragHueSlider = true;
                  return false;
               }

               int l2 = k + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2;
               int i1 = k + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2 + this.alphaSelectorWidth;
               if(i > l2 && i < i1 && j > l + this.paddingY && j < l + this.paddingY + this.mainColorBoxSize) {
                  float f = (float)(j - (l + this.paddingY));
                  this.alpha = f / (float)this.mainColorBoxSize;
                  if((double)this.alpha < 0.11D) {
                     this.alpha = 0.11F;
                  }

                  this.dragAlphaSlider = true;
                  return false;
               }
            } else {
               this.modalOpen = false;
            }
         }

         if(this.allowDisable) {
            int l1 = (int)((float)this.colorBoxWidth / 2.0F);
            if(i > this.getX() + this.slideoutHalfX + l1 - 10 && i < this.getX() + this.slideoutHalfX + l1 && j > this.getY() + this.colorBoxTextHeightOffset - 9 && j < this.getY() + this.colorBoxTextHeightOffset - 2) {
               this.modColor.setEnabled(!this.modColor.isEnabled());
            }
         }
      }

      return false;
   }

   public void render() {
      if(this.color != this.modColor.getColorInt()) {
         this.color = this.modColor.getColorInt();
         int i = ColorUtil.getRed(this.color);
         int j = ColorUtil.getGreen(this.color);
         int k = ColorUtil.getBlue(this.color);
         this.alpha = (float)ColorUtil.getAlpha(this.color) / 255.0F;
         float[] afloat = new float[3];
         float[] afloat1 = Color.RGBtoHSB(i, j, k, afloat);
         this.hue = afloat1[0];
         this.saturation = afloat1[1];
         this.brightness = afloat1[2];
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      double d0 = 1.0D;
      int l = (int)((float)this.colorBoxWidth / 2.0F);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(this.getX(), this.getY() + 1, this.name, 11, BadlionFontRenderer.FontType.TITLE, true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glScaled(1.0D, 1.0D, 99998.0D);
      BadlionGuiScreen.drawRoundedRect(this.getX() + this.slideoutHalfX * 2 - 31, this.getY() - 1, this.getX() + this.slideoutHalfX * 2 + 1, this.getY() + 13, 2.0F, -16777216);
      GL11.glScaled(1.0D, 1.0D, 0.0D);
      GL11.glScaled(1.0D, 1.0D, 99999.0D);
      BadlionGuiScreen.drawRoundedRect(this.getX() + this.slideoutHalfX * 2 - 30, this.getY(), this.getX() + this.slideoutHalfX * 2, this.getY() + 12, 2.0F, this.modColor.getColorInt());
      GL11.glScaled(1.0D, 1.0D, 0.0D);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawRect(this.getX(), this.getY() + 19, this.getX() + this.slideoutHalfX * 2, this.getY() + 20, -1144206132);
      if(this.modalOpen) {
         this.renderModalColorPicker();
      }

      if(this.allowDisable) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.modColor.isEnabled()?buttonOn:buttonOff);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         Gui.drawModalRectWithCustomSizedTexture(this.getX() + this.slideoutHalfX + l - 8, this.getY() + this.colorBoxTextHeightOffset - 9, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      }

   }

   public void renderModalColorPicker() {
      GL11.glDisable(3553);
      int i = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() + 8;
      int j = Math.min(this.getY(), Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutHeight() - 135);
      int k = this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2 + this.alphaSelectorWidth + this.paddingX + this.presetColorsWidth + this.paddingX;
      int l = this.paddingX + this.mainColorBoxSize + 50 + this.paddingX;
      Color color = ColorUtil.hsvToRgb(this.hue, this.saturation, this.brightness);
      int i1 = 6;
      int j1 = 7;
      int k1 = 10;
      GL11.glDisable(3553);
      GL11.glEnable(2881);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(4);
      ColorUtil.bindHexColorRGBA(-685233615);
      GL11.glVertex2f((float)(i - i1), (float)(j + j1 + k1 / 2));
      GL11.glVertex2f((float)i, (float)(j + j1 + k1));
      GL11.glVertex2f((float)i, (float)(j + j1));
      GL11.glEnd();
      GL11.glDisable(2881);
      GL11.glShadeModel(7425);
      BadlionGuiScreen.drawRoundedRect(i, j, i + k, j + l, 1.75F, -685233615);
      Color color1 = ColorUtil.hsvToRgb(this.hue, 1.0F, 1.0F);
      int l1 = (int)Long.parseLong(String.format("FF%02x%02x%02x", new Object[]{Integer.valueOf(color1.getRed()), Integer.valueOf(color1.getGreen()), Integer.valueOf(color1.getBlue())}), 16);
      Gui.drawRect(i + this.paddingX, j + this.paddingY, i + this.paddingX + this.mainColorBoxSize, j + this.paddingY + this.mainColorBoxSize, l1);
      GL11.glEnable(3553);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(colorPickerBackground);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(i + this.paddingX, j + this.paddingY, 0.0F, 0.0F, this.mainColorBoxSize, this.mainColorBoxSize, (float)this.mainColorBoxSize, (float)this.mainColorBoxSize);
      GL11.glDisable(3553);
      int i2 = 0;
      GL11.glBegin(8);
      int j2 = i + this.paddingX + this.mainColorBoxSize + this.paddingX;
      int k2 = i + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth;
      int l2 = j + this.paddingY;
      GL11.glColor3f(1.0F, 0.0F, 0.0F);
      GL11.glVertex2f((float)k2, (float)l2);
      GL11.glVertex2f((float)j2, (float)l2);
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(1.0F, 1.0F, 0.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(0.0F, 1.0F, 0.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(0.0F, 1.0F, 1.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(0.0F, 0.0F, 1.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(1.0F, 0.0F, 1.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      i2 = i2 + this.hueColorSpacing;
      GL11.glColor3f(1.0F, 0.0F, 0.0F);
      GL11.glVertex2f((float)k2, (float)(l2 + i2));
      GL11.glVertex2f((float)j2, (float)(l2 + i2));
      GL11.glEnd();
      GL11.glBegin(7);
      int i3 = i + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2;
      int j3 = i + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2 + this.alphaSelectorWidth;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glVertex2f((float)j3, (float)(j + this.paddingY));
      GL11.glVertex2f((float)i3, (float)(j + this.paddingY));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F);
      GL11.glVertex2f((float)i3, (float)(j + this.paddingY + this.mainColorBoxSize));
      GL11.glVertex2f((float)j3, (float)(j + this.paddingY + this.mainColorBoxSize));
      GL11.glEnd();
      GL11.glEnable(3553);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.colorPicker);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture((int)((float)(i + this.paddingX) + this.saturation * (float)this.mainColorBoxSize) - 3, (int)((float)(j + this.paddingY + this.mainColorBoxSize) - this.brightness * (float)this.mainColorBoxSize) - 3, 0.0F, 0.0F, 6, 6, 6.0F, 6.0F);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.colorPicker);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture((int)((float)(i + this.paddingX + this.mainColorBoxSize + this.paddingX) + (float)this.hueSelectorWidth / 2.0F - 3.0F), (int)((float)(j + this.paddingY) + this.hue * (float)this.mainColorBoxSize - 3.0F), 0.0F, 0.0F, 6, 6, 6.0F, 6.0F);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.colorPicker);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture((int)((float)(i + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2) + (float)this.alphaSelectorWidth / 2.0F - 3.0F), (int)((float)(j + this.paddingY + this.mainColorBoxSize) - this.alpha * (float)this.mainColorBoxSize - 3.0F), 0.0F, 0.0F, 6, 6, 6.0F, 6.0F);
      double d0 = 1.2D;
      int k3 = i + this.paddingX + this.mainColorBoxSize + this.paddingX;
      int l3 = j + this.paddingY + this.mainColorBoxSize + this.textPaddingTop;
      GL11.glScaled(d0, d0, d0);
      Wrapper.getInstance().getBadlionFontRenderer().drawString((int)((double)k3 / d0), (int)((double)l3 / d0), "R: " + color.getRed(), 8, BadlionFontRenderer.FontType.TEXT, true);
      Wrapper.getInstance().getBadlionFontRenderer().drawString((int)((double)k3 / d0), (int)((double)(l3 + this.textYOffset) / d0), "G: " + color.getGreen(), 8, BadlionFontRenderer.FontType.TEXT, true);
      Wrapper.getInstance().getBadlionFontRenderer().drawString((int)((double)k3 / d0), (int)((double)(l3 + 2 * this.textYOffset) / d0), "B: " + color.getBlue(), 8, BadlionFontRenderer.FontType.TEXT, true);
      k3 = k3 + this.hueSelectorWidth + this.paddingX2;
      Wrapper.getInstance().getBadlionFontRenderer().drawString((int)((double)k3 / d0), (int)((double)l3 / d0), "A: " + (int)(this.alpha * 100.0F) + "%", 8, BadlionFontRenderer.FontType.TEXT, true);
      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
      this.rainbowButton.setPosition(i + this.paddingX, l3);
      this.rainbowButton.render();
      this.breathingButton.setPosition(i + this.paddingX + this.buttonXOffset, l3);
      this.breathingButton.render();
      this.applyToOtherButton.setPosition(i + this.paddingX, l3 + 1 + this.rainbowButton.getHeight());
      this.applyToOtherButton.render();
      int i4 = 0;
      int j4 = 0;

      for(SimpleButton simplebutton : this.quickColorButtons) {
         if(i4 == 8) {
            j4 = 0;
         }

         simplebutton.setSize(7, 7);
         simplebutton.setPosition(i + this.paddingX + this.mainColorBoxSize + this.paddingX + this.hueSelectorWidth + this.paddingX2 + this.alphaSelectorWidth + this.paddingX + (i4 >= 8?9:0), j + this.paddingY + j4);
         j4 += simplebutton.getHeight() + 1;
         simplebutton.render();
         ++i4;
      }

      int k4 = j + this.paddingY + this.mainColorBoxSize + this.textPaddingTop + 3 * this.textYOffset + this.textPaddingBottom;
      this.hexField.setPosition(i + this.paddingX, k4);
      this.hexField.render();
      GL11.glDisable(3042);
   }

   public void keyTyped(char character, int keyCode) {
      if(this.modalOpen) {
         this.hexField.keyTyped(character, keyCode);
         if(this.hexField.isFocused()) {
            if(this.hexField.getText().length() == this.hexField.getMaximumLength()) {
               Color color = ColorUtil.getColorFromString(this.hexField.getText());
               this.color = (int)Long.parseLong(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf(color.getAlpha()), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}), 16);
               this.updateColor((Color)null);
               this.hexField.setTextColor(-1);
               float[] afloat = new float[3];
               float[] afloat1 = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), afloat);
               this.hue = afloat1[0];
               this.saturation = afloat1[1];
               this.brightness = afloat1[2];
            } else {
               this.hexField.setTextColor(-65536);
            }
         }
      }

   }

   public int getWidth() {
      return (int)(1136.0D * this.scale);
   }

   public int getHeight() {
      return this.colorBoxTextHeightOffset + this.colorBoxHeight;
   }

   public void setModalOpen(boolean open) {
      this.modalOpen = open;
      this.hexField.setFocused(false);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode;
      if($SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode != null) {
         return var10000;
      } else {
         int[] var0 = new int[ModColor.DynamicColorMode.values().length];

         try {
            var0[ModColor.DynamicColorMode.BREATHING.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[ModColor.DynamicColorMode.RAINBOW.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ModColor.DynamicColorMode.STATIC.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode = var0;
         return var0;
      }
   }

   public interface ColorChangeListener {
      ModColor getColor();
   }
}
