package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   private int[] charWidth = new int[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private byte[] glyphWidth = new byte[65536];
   private int[] colorCode = new int[32];
   private final ResourceLocation locationFontTexture;
   private final TextureManager renderEngine;
   private float posX;
   private float posY;
   private boolean unicodeFlag;
   private boolean bidiFlag;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean randomStyle;
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;

   public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
      this.locationFontTexture = location;
      this.renderEngine = textureManagerIn;
      this.unicodeFlag = unicode;
      textureManagerIn.bindTexture(this.locationFontTexture);

      for(int i = 0; i < 32; ++i) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int i1 = (i >> 0 & 1) * 170 + j;
         if(i == 6) {
            k += 85;
         }

         if(gameSettingsIn.anaglyph) {
            int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
            int k1 = (k * 30 + l * 70) / 100;
            int l1 = (k * 30 + i1 * 70) / 100;
            k = j1;
            l = k1;
            i1 = l1;
         }

         if(i >= 16) {
            k /= 4;
            l /= 4;
            i1 /= 4;
         }

         this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
      }

      this.readGlyphSizes();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.readFontTexture();
   }

   private void readFontTexture() {
      BufferedImage bufferedimage;
      try {
         bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }

      int i = bufferedimage.getWidth();
      int j = bufferedimage.getHeight();
      int[] aint = new int[i * j];
      bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
      int k = j / 16;
      int l = i / 16;
      int i1 = 1;
      float f = 8.0F / (float)l;

      for(int j1 = 0; j1 < 256; ++j1) {
         int k1 = j1 % 16;
         int l1 = j1 / 16;
         if(j1 == 32) {
            this.charWidth[j1] = 3 + i1;
         }

         int i2;
         for(i2 = l - 1; i2 >= 0; --i2) {
            int j2 = k1 * l + i2;
            boolean flag = true;

            for(int k2 = 0; k2 < k && flag; ++k2) {
               int l2 = (l1 * l + k2) * i;
               if((aint[j2 + l2] >> 24 & 255) != 0) {
                  flag = false;
               }
            }

            if(!flag) {
               break;
            }
         }

         ++i2;
         this.charWidth[j1] = (int)(0.5D + (double)((float)i2 * f)) + i1;
      }

   }

   private void readGlyphSizes() {
      InputStream inputstream = null;

      try {
         inputstream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
         inputstream.read(this.glyphWidth);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      } finally {
         IOUtils.closeQuietly(inputstream);
      }

   }

   private float func_181559_a(char p_181559_1_, boolean p_181559_2_) {
      if(p_181559_1_ == 32) {
         return 4.0F;
      } else {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(p_181559_1_);
         return i != -1 && !this.unicodeFlag?this.renderDefaultChar(i, p_181559_2_):this.renderUnicodeChar(p_181559_1_, p_181559_2_);
      }
   }

   private float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
      int i = p_78266_1_ % 16 * 8;
      int j = p_78266_1_ / 16 * 8;
      int k = p_78266_2_?1:0;
      this.renderEngine.bindTexture(this.locationFontTexture);
      int l = this.charWidth[p_78266_1_];
      float f = (float)l - 0.01F;
      GL11.glBegin(5);
      GL11.glTexCoord2f((float)i / 128.0F, (float)j / 128.0F);
      GL11.glVertex3f(this.posX + (float)k, this.posY, 0.0F);
      GL11.glTexCoord2f((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - (float)k, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, (float)j / 128.0F);
      GL11.glVertex3f(this.posX + f - 1.0F + (float)k, this.posY, 0.0F);
      GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + f - 1.0F - (float)k, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return (float)l;
   }

   private ResourceLocation getUnicodePageLocation(int p_111271_1_) {
      if(unicodePageLocations[p_111271_1_] == null) {
         unicodePageLocations[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[]{Integer.valueOf(p_111271_1_)}));
      }

      return unicodePageLocations[p_111271_1_];
   }

   private void loadGlyphTexture(int p_78257_1_) {
      this.renderEngine.bindTexture(this.getUnicodePageLocation(p_78257_1_));
   }

   private float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_) {
      if(this.glyphWidth[p_78277_1_] == 0) {
         return 0.0F;
      } else {
         int i = p_78277_1_ / 256;
         this.loadGlyphTexture(i);
         int j = this.glyphWidth[p_78277_1_] >>> 4;
         int k = this.glyphWidth[p_78277_1_] & 15;
         float f = (float)j;
         float f1 = (float)(k + 1);
         float f2 = (float)(p_78277_1_ % 16 * 16) + f;
         float f3 = (float)((p_78277_1_ & 255) / 16 * 16);
         float f4 = f1 - f - 0.02F;
         float f5 = p_78277_2_?1.0F:0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
         GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
         GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
         GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
         GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
         GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
         GL11.glEnd();
         return (f1 - f) / 2.0F + 1.0F;
      }
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   public int drawString(String text, int x, int y, int color) {
      return this.drawString(text, (float)x, (float)y, color, false);
   }

   public int drawString(String text, float x, float y, int color, boolean dropShadow) {
      GlStateManager.enableAlpha();
      this.resetStyles();
      int i;
      if(dropShadow) {
         i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         i = Math.max(i, this.renderString(text, x, y, color, false));
      } else {
         i = this.renderString(text, x, y, color, false);
      }

      return i;
   }

   private String bidiReorder(String p_147647_1_) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   private void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
      for(int i = 0; i < p_78255_1_.length(); ++i) {
         char c0 = p_78255_1_.charAt(i);
         if(c0 == 167 && i + 1 < p_78255_1_.length()) {
            int i1 = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase(Locale.ENGLISH).charAt(i + 1));
            if(i1 < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if(i1 < 0 || i1 > 15) {
                  i1 = 15;
               }

               if(p_78255_2_) {
                  i1 += 16;
               }

               int j1 = this.colorCode[i1];
               this.textColor = j1;
               GlStateManager.color((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.alpha);
            } else if(i1 == 16) {
               this.randomStyle = true;
            } else if(i1 == 17) {
               this.boldStyle = true;
            } else if(i1 == 18) {
               this.strikethroughStyle = true;
            } else if(i1 == 19) {
               this.underlineStyle = true;
            } else if(i1 == 20) {
               this.italicStyle = true;
            } else if(i1 == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               GlStateManager.color(this.red, this.blue, this.green, this.alpha);
            }

            ++i;
         } else {
            int j = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(c0);
            if(this.randomStyle && j != -1) {
               int k = this.getCharWidth(c0);

               char c1;
               while(true) {
                  j = this.fontRandom.nextInt("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".length());
                  c1 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".charAt(j);
                  if(k == this.getCharWidth(c1)) {
                     break;
                  }
               }

               c0 = c1;
            }

            float f1 = this.unicodeFlag?0.5F:1.0F;
            boolean flag = (c0 == 0 || j == -1 || this.unicodeFlag) && p_78255_2_;
            if(flag) {
               this.posX -= f1;
               this.posY -= f1;
            }

            float f = this.func_181559_a(c0, this.italicStyle);
            if(flag) {
               this.posX += f1;
               this.posY += f1;
            }

            if(this.boldStyle) {
               this.posX += f1;
               if(flag) {
                  this.posX -= f1;
                  this.posY -= f1;
               }

               this.func_181559_a(c0, this.italicStyle);
               this.posX -= f1;
               if(flag) {
                  this.posX += f1;
                  this.posY += f1;
               }

               ++f;
            }

            if(this.strikethroughStyle) {
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldrenderer = tessellator.getWorldRenderer();
               GlStateManager.disableTexture2D();
               worldrenderer.begin(7, DefaultVertexFormats.POSITION);
               worldrenderer.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
               worldrenderer.pos((double)(this.posX + f), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
               worldrenderer.pos((double)(this.posX + f), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
               worldrenderer.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
            }

            if(this.underlineStyle) {
               Tessellator tessellator1 = Tessellator.getInstance();
               WorldRenderer worldrenderer1 = tessellator1.getWorldRenderer();
               GlStateManager.disableTexture2D();
               worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
               int l = this.underlineStyle?-1:0;
               worldrenderer1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
               worldrenderer1.pos((double)(this.posX + f), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
               worldrenderer1.pos((double)(this.posX + f), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
               worldrenderer1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
               tessellator1.draw();
               GlStateManager.enableTexture2D();
            }

            this.posX += (float)((int)f);
         }
      }

   }

   private int renderStringAligned(String text, int x, int y, int p_78274_4_, int color, boolean dropShadow) {
      if(this.bidiFlag) {
         int i = this.getStringWidth(this.bidiReorder(text));
         x = x + p_78274_4_ - i;
      }

      return this.renderString(text, (float)x, (float)y, color, dropShadow);
   }

   private int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if(text == null) {
         return 0;
      } else {
         if(this.bidiFlag) {
            text = this.bidiReorder(text);
         }

         if((color & -67108864) == 0) {
            color |= -16777216;
         }

         if(dropShadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
         }

         this.red = (float)(color >> 16 & 255) / 255.0F;
         this.blue = (float)(color >> 8 & 255) / 255.0F;
         this.green = (float)(color & 255) / 255.0F;
         this.alpha = (float)(color >> 24 & 255) / 255.0F;
         GlStateManager.color(this.red, this.blue, this.green, this.alpha);
         this.posX = x;
         this.posY = y;
         this.renderStringAtPos(text, dropShadow);
         return (int)this.posX;
      }
   }

   public int getStringWidth(String text) {
      if(text == null) {
         return 0;
      } else {
         int i = 0;
         boolean flag = false;

         for(int j = 0; j < text.length(); ++j) {
            char c0 = text.charAt(j);
            int k = this.getCharWidth(c0);
            if(k < 0 && j < text.length() - 1) {
               ++j;
               c0 = text.charAt(j);
               if(c0 != 108 && c0 != 76) {
                  if(c0 == 114 || c0 == 82) {
                     flag = false;
                  }
               } else {
                  flag = true;
               }

               k = 0;
            }

            i += k;
            if(flag && k > 0) {
               ++i;
            }
         }

         return i;
      }
   }

   public int getCharWidth(char character) {
      if(character == 167) {
         return -1;
      } else if(character == 32) {
         return 4;
      } else {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(character);
         if(character > 0 && i != -1 && !this.unicodeFlag) {
            return this.charWidth[i];
         } else if(this.glyphWidth[character] != 0) {
            int j = this.glyphWidth[character] >>> 4;
            int k = this.glyphWidth[character] & 15;
            if(k > 7) {
               k = 15;
               j = 0;
            }

            ++k;
            return (k - j) / 2 + 1;
         } else {
            return 0;
         }
      }
   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = 0;
      int j = reverse?text.length() - 1:0;
      int k = reverse?-1:1;
      boolean flag = false;
      boolean flag1 = false;

      for(int l = j; l >= 0 && l < text.length() && i < width; l += k) {
         char c0 = text.charAt(l);
         int i1 = this.getCharWidth(c0);
         if(flag) {
            flag = false;
            if(c0 != 108 && c0 != 76) {
               if(c0 == 114 || c0 == 82) {
                  flag1 = false;
               }
            } else {
               flag1 = true;
            }
         } else if(i1 < 0) {
            flag = true;
         } else {
            i += i1;
            if(flag1) {
               ++i;
            }
         }

         if(i > width) {
            break;
         }

         if(reverse) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   private String trimStringNewline(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      this.resetStyles();
      this.textColor = textColor;
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, false);
   }

   private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
      for(String s : this.listFormattedStringToWidth(str, wrapWidth)) {
         this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
         y += this.FONT_HEIGHT;
      }

   }

   public int splitStringWidth(String p_78267_1_, int p_78267_2_) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
   }

   public void setUnicodeFlag(boolean unicodeFlagIn) {
      this.unicodeFlag = unicodeFlagIn;
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   public List listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   String wrapFormattedStringToWidth(String str, int wrapWidth) {
      int i = this.sizeStringToWidth(str, wrapWidth);
      if(str.length() <= i) {
         return str;
      } else {
         String s = str.substring(0, i);
         char c0 = str.charAt(i);
         boolean flag = c0 == 32 || c0 == 10;
         String s1 = getFormatFromString(s) + str.substring(i + (flag?1:0));
         return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
      }
   }

   private int sizeStringToWidth(String str, int wrapWidth) {
      int i = str.length();
      int j = 0;
      int k = 0;
      int l = -1;

      for(boolean flag = false; k < i; ++k) {
         char c0 = str.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            j += this.getCharWidth(c0);
            if(flag) {
               ++j;
            }
            break;
         case '§':
            if(k < i - 1) {
               ++k;
               char c1 = str.charAt(k);
               if(c1 != 108 && c1 != 76) {
                  if(c1 == 114 || c1 == 82 || isFormatColor(c1)) {
                     flag = false;
                  }
               } else {
                  flag = true;
               }
            }
         }

         if(c0 == 10) {
            ++k;
            l = k;
            break;
         }

         if(j > wrapWidth) {
            break;
         }
      }

      return k != i && l != -1 && l < k?l:k;
   }

   private static boolean isFormatColor(char colorChar) {
      return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
   }

   private static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
   }

   public static String getFormatFromString(String text) {
      String s = "";
      int i = -1;
      int j = text.length();

      while((i = text.indexOf(167, i + 1)) != -1) {
         if(i < j - 1) {
            char c0 = text.charAt(i + 1);
            if(isFormatColor(c0)) {
               s = "§" + c0;
            } else if(isFormatSpecial(c0)) {
               s = s + "§" + c0;
            }
         }
      }

      return s;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   public int getColorCode(char character) {
      return this.colorCode["0123456789abcdef".indexOf(character)];
   }
}
