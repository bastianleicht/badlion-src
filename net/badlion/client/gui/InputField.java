package net.badlion.client.gui;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class InputField extends RenderElement {
   private String text;
   private final String textPrefix;
   private int maximumLength;
   private boolean focused;
   private int textColor;
   private final int width;
   private final int height;
   private boolean hideText;
   private int cursorPosition;
   private int firstRenderedCharacterPosition;
   private int lastRenderedCharacterPosition;
   private boolean textSelected;
   private int stringHeight;
   private final int blinkingUnderscoreWidth;
   private InputField.InputFlavor inputFlavor;

   public InputField(int width, int height, boolean hideText, String defaultText, String textPrefix, int maximumLength, InputField.InputFlavor inputFlavor) {
      this(-1, -1, width, height, hideText, defaultText, textPrefix, maximumLength, inputFlavor);
   }

   public InputField(int x, int y, int width, int height, boolean hideText, InputField.InputFlavor inputFlavor) {
      this(x, y, width, height, hideText, "", "", -1, inputFlavor);
   }

   public InputField(int x, int y, int width, int height, boolean hideText, String defaultText, String textPrefix, int maximumLength, InputField.InputFlavor inputFlavor) {
      this.text = "";
      this.focused = false;
      this.textColor = -1;
      this.cursorPosition = 0;
      this.textSelected = false;
      if(hideText && !textPrefix.isEmpty()) {
         throw new IllegalArgumentException("Cannot have a text prefix is text is being hidden!");
      } else {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.hideText = hideText;
         this.text = defaultText;
         this.textPrefix = textPrefix;
         this.maximumLength = maximumLength;
         this.inputFlavor = inputFlavor;
         this.firstRenderedCharacterPosition = 0;
         this.lastRenderedCharacterPosition = textPrefix.length() + defaultText.length();
         Keyboard.enableRepeatEvents(true);
         if(this.height > 14) {
            this.stringHeight = this.height - 4;
         } else {
            this.stringHeight = this.height;
         }

         this.blinkingUnderscoreWidth = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("_", this.stringHeight, BadlionFontRenderer.FontType.TITLE);
      }
   }

   public void keyTyped(char character, int keyCode) {
      if(this.focused) {
         if(keyCode != 1 && keyCode != 28) {
            if(this.text.length() > 0) {
               if(keyCode == 14) {
                  if(this.textSelected) {
                     this.text = "";
                     this.textSelected = false;
                     this.resetCursorPosition();
                  } else {
                     if(this.cursorPosition == 0) {
                        return;
                     }

                     this.text = this.text.substring(0, this.cursorPosition - 1) + this.text.substring(this.cursorPosition, this.text.length());
                     if(this.cursorPosition > 0) {
                        --this.cursorPosition;
                     }

                     --this.lastRenderedCharacterPosition;
                     this.fitTextInFieldDeletion();
                  }

                  return;
               }

               if(keyCode == 211) {
                  if(this.textSelected) {
                     this.text = "";
                     this.textSelected = false;
                     this.resetCursorPosition();
                  } else {
                     if(this.cursorPosition == this.text.length()) {
                        return;
                     }

                     this.text = this.text.substring(0, this.cursorPosition) + this.text.substring(this.cursorPosition + 1, this.text.length());
                     --this.lastRenderedCharacterPosition;
                     this.fitTextInFieldDeletion();
                  }

                  return;
               }
            }

            Character character1 = this.checkInputCharacter(character);
            if(character1 != null) {
               if(this.textSelected) {
                  this.text = "";
                  this.textSelected = false;
                  this.resetCursorPosition();
               }

               if(this.text.length() != this.maximumLength) {
                  this.text = this.text.substring(0, this.cursorPosition) + character1 + this.text.substring(this.cursorPosition, this.text.length());
                  ++this.cursorPosition;
                  this.textSelected = false;
                  this.fitTextInFieldKeyTyped();
               }
            } else if(keyCode == 203) {
               if(this.cursorPosition > 0) {
                  --this.cursorPosition;
               }

               this.textSelected = false;
            } else if(keyCode == 205) {
               if(this.cursorPosition < this.text.length()) {
                  ++this.cursorPosition;
               }

               this.textSelected = false;
            } else if(GuiScreen.isCtrlKeyDown()) {
               switch(keyCode) {
               case 30:
                  this.textSelected = true;
                  return;
               case 46:
                  if(!this.hideText && this.textSelected) {
                     GuiScreen.setClipboardString(this.text);
                  }

                  return;
               case 47:
                  if(this.textSelected) {
                     this.text = "";
                     this.textSelected = false;
                     this.resetCursorPosition();
                  }

                  String s = GuiScreen.getClipboardString();

                  char[] var8;
                  for(char c0 : var8 = s.toCharArray()) {
                     character1 = this.checkInputCharacter(c0);
                     if(character1 != null) {
                        if(this.text.length() == this.maximumLength) {
                           return;
                        }

                        this.text = this.text + character1;
                        ++this.cursorPosition;
                     }
                  }

                  return;
               }
            }
         } else {
            this.focused = false;
            this.textSelected = false;
            this.resetCursorPosition();
         }
      }

   }

   private Character checkInputCharacter(char character) {
      if(this.inputFlavor.contains(character)) {
         if(this.inputFlavor == InputField.InputFlavor.HEX_COLOR) {
            character = String.valueOf(character).toUpperCase().toCharArray()[0];
         }

         return Character.valueOf(character);
      } else {
         return null;
      }
   }

   public void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(GuiButton.backToGameButton);
      int i = this.width;
      int j = this.height;
      Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, (float)i, (float)j, i, j, (float)i, (float)j);
      String s = this.getTextToDraw();
      this.fitTextInField();
      int k = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, this.lastRenderedCharacterPosition), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
      int l = this.lastRenderedCharacterPosition;
      if(this.isFocused() && System.currentTimeMillis() % 1060L >= 530L) {
         if(this.cursorPosition == this.text.length()) {
            s = s + "_";
            ++l;
         } else {
            int i1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, this.cursorPosition + this.textPrefix.length()), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            Gui.drawRect(this.x + 5 + i1, this.y + 2, this.x + 5 + i1 + 1, this.y + this.height - 1, -570425345);
         }
      }

      ColorUtil.bindHexColorRGBA(this.textColor);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(this.x + 5, this.y, s.substring(this.firstRenderedCharacterPosition, l), this.stringHeight, BadlionFontRenderer.FontType.TITLE, true);
      if(this.textSelected) {
         Gui.drawRect(this.x + 5, this.y + 1, this.x + 5 + k, this.y + this.height - 1, -2130706433);
      }

   }

   public void update(int mouseX, int mouseY) {
      if(this.focused) {
         ;
      }

   }

   public boolean onClick(int mouseButton) {
      this.focused = false;
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      if(i > this.x && i < this.x + this.width && j > this.y && j < this.y + this.height) {
         this.focused = true;
         this.textSelected = false;
         this.resetCursorPosition();
         return true;
      } else {
         return false;
      }
   }

   private String getTextToDraw() {
      String s = this.textPrefix + this.text;
      if(this.hideText) {
         s = StringUtils.leftPad("", this.text.length(), '*');
      }

      return s;
   }

   private void fitTextInFieldKeyTyped() {
      String s = this.getTextToDraw();
      int i = this.width - 10;
      if(this.cursorPosition == s.length()) {
         i -= this.blinkingUnderscoreWidth;
      }

      for(int j = s.length(); j >= this.firstRenderedCharacterPosition; --j) {
         int k = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, j), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
         if(k <= i) {
            this.lastRenderedCharacterPosition = j;
            break;
         }
      }

   }

   private void fitTextInFieldDeletion() {
      String s = this.getTextToDraw();
      int i = this.width - 10;
      if(this.cursorPosition == s.length()) {
         i -= this.blinkingUnderscoreWidth;
      }

      if(this.lastRenderedCharacterPosition == s.length()) {
         for(int j = 0; j <= this.lastRenderedCharacterPosition; ++j) {
            int k = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(j, this.lastRenderedCharacterPosition), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            if(k <= i) {
               this.firstRenderedCharacterPosition = j;
               break;
            }
         }
      } else {
         for(int l = s.length(); l >= this.firstRenderedCharacterPosition; --l) {
            int i1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, l), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            if(i1 <= i) {
               this.lastRenderedCharacterPosition = l;
               break;
            }
         }
      }

   }

   private void fitTextInField() {
      String s = this.getTextToDraw();
      int i = this.width - 10;
      if(this.cursorPosition == s.length() - this.textPrefix.length()) {
         i -= this.blinkingUnderscoreWidth;
      }

      int j = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, this.lastRenderedCharacterPosition), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
      if(j > i && this.lastRenderedCharacterPosition == s.length()) {
         for(int k = 0; k < s.length(); ++k) {
            int l = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(k, s.length()), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            if(l <= i) {
               this.firstRenderedCharacterPosition = k;
               break;
            }
         }
      }

      if(this.cursorPosition < this.firstRenderedCharacterPosition) {
         int i1 = this.firstRenderedCharacterPosition - this.cursorPosition;
         this.firstRenderedCharacterPosition -= i1;

         for(int k1 = s.length(); k1 >= this.firstRenderedCharacterPosition; --k1) {
            j = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(this.firstRenderedCharacterPosition, k1), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            if(j <= i) {
               this.lastRenderedCharacterPosition = k1;
               break;
            }
         }
      } else if(this.cursorPosition > this.lastRenderedCharacterPosition) {
         int j1 = this.cursorPosition - this.lastRenderedCharacterPosition;
         this.lastRenderedCharacterPosition += j1;

         for(int l1 = 0; l1 <= this.lastRenderedCharacterPosition; ++l1) {
            j = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s.substring(l1, this.lastRenderedCharacterPosition), this.stringHeight, BadlionFontRenderer.FontType.TITLE);
            if(j <= i) {
               this.firstRenderedCharacterPosition = l1;
               break;
            }
         }
      }

   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
      if(focused) {
         this.resetCursorPosition();
      }

   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
      this.firstRenderedCharacterPosition = 0;
      this.lastRenderedCharacterPosition = this.textPrefix.length() + text.length();
   }

   public int getMaximumLength() {
      return this.maximumLength;
   }

   public void reset() {
      this.focused = false;
      this.text = "";
      this.textSelected = false;
      this.resetCursorPosition();
   }

   private void resetCursorPosition() {
      this.cursorPosition = this.text.length();
      this.firstRenderedCharacterPosition = 0;
      this.lastRenderedCharacterPosition = this.textPrefix.length() + this.text.length();
   }

   public int getTextColor() {
      return this.textColor;
   }

   public void setTextColor(int color) {
      this.textColor = color;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public static class InputFlavor {
      public static final InputField.InputFlavor EMAIL = new InputField.InputFlavor("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%&\'*+-/=?^_`{|}~;.@\"(),:;<>@[\\]");
      public static final InputField.InputFlavor HEX_COLOR = new InputField.InputFlavor("abcdefABCDEF0123456789");
      public static final InputField.InputFlavor MOD_PROFILE_NAME = new InputField.InputFlavor("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-(),. ");
      public static final InputField.InputFlavor PASSWORD = new InputField.InputFlavor("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%&\'*+-/=?^_`{|}~;.@\"(),:;<>@[\\] ");
      private final String flavor;

      public InputFlavor(String flavor) {
         this.flavor = flavor;
      }

      public boolean contains(char ch) {
         return this.flavor.contains(String.valueOf(ch));
      }
   }
}
