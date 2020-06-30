package net.badlion.client.gui;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import net.badlion.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;

public class BadlionFontRenderer {
   private static final ResourceLocation badlionFontHeader = new ResourceLocation("font/badlion-font-0.png");
   private static final ResourceLocation badlionFontTitle = new ResourceLocation("font/badlion-font-1.png");
   private static final ResourceLocation badlionFontText = new ResourceLocation("font/badlion-font-2.png");
   private static final ResourceLocation badlionFontDataHeader = new ResourceLocation("font/badlion-font-data-0.json");
   private static final ResourceLocation badlionFontDataTitle = new ResourceLocation("font/badlion-font-data-1.json");
   private static final ResourceLocation badlionFontDataText = new ResourceLocation("font/badlion-font-data-2.json");
   private final int fontSize = 128;
   private Map fontDataHeader;
   private Map fontDataTitle;
   private Map fontDataText;
   private int[] colorCode = new int[32];
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType;

   public BadlionFontRenderer() throws Throwable {
      try {
         InputStreamReader inputstreamreader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(badlionFontDataHeader).getInputStream(), Charsets.UTF_8);
         Throwable var6 = null;

         try {
            this.fontDataHeader = (Map)Wrapper.getInstance().getGson().fromJson((Reader)inputstreamreader, (Type)(new TypeToken() {
            }).getType());
         } catch (Throwable var53) {
            var6 = var53;
            throw var53;
         } finally {
            if(inputstreamreader != null) {
               if(var6 != null) {
                  try {
                     inputstreamreader.close();
                  } catch (Throwable var52) {
                     var6.addSuppressed(var52);
                  }
               } else {
                  inputstreamreader.close();
               }
            }

         }
      } catch (IOException var59) {
         var59.printStackTrace();
      }

      try {
         InputStreamReader inputstreamreader1 = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(badlionFontDataTitle).getInputStream(), Charsets.UTF_8);
         Throwable throwable7 = null;

         try {
            this.fontDataTitle = (Map)Wrapper.getInstance().getGson().fromJson((Reader)inputstreamreader1, (Type)(new TypeToken() {
            }).getType());
         } catch (Throwable var51) {
            throwable7 = var51;
            throw var51;
         } finally {
            if(inputstreamreader1 != null) {
               if(throwable7 != null) {
                  try {
                     inputstreamreader1.close();
                  } catch (Throwable var50) {
                     throwable7.addSuppressed(var50);
                  }
               } else {
                  inputstreamreader1.close();
               }
            }

         }
      } catch (IOException var57) {
         var57.printStackTrace();
      }

      try {
         InputStreamReader inputstreamreader2 = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(badlionFontDataText).getInputStream(), Charsets.UTF_8);
         Throwable throwable8 = null;

         try {
            this.fontDataText = (Map)Wrapper.getInstance().getGson().fromJson((Reader)inputstreamreader2, (Type)(new TypeToken() {
            }).getType());
         } catch (Throwable var49) {
            throwable8 = var49;
            throw var49;
         } finally {
            if(inputstreamreader2 != null) {
               if(throwable8 != null) {
                  try {
                     inputstreamreader2.close();
                  } catch (Throwable var48) {
                     throwable8.addSuppressed(var48);
                  }
               } else {
                  inputstreamreader2.close();
               }
            }

         }
      } catch (IOException var55) {
         var55.printStackTrace();
      }

      for(int l = 0; l < 32; ++l) {
         int i1 = (l >> 3 & 1) * 85;
         int i = (l >> 2 & 1) * 170 + i1;
         int j = (l >> 1 & 1) * 170 + i1;
         int k = (l >> 0 & 1) * 170 + i1;
         if(l == 6) {
            i += 85;
         }

         if(l >= 16) {
            i /= 4;
            j /= 4;
            k /= 4;
         }

         this.colorCode[l] = (i & 255) << 16 | (j & 255) << 8 | k & 255;
      }

   }

   private Map bindFontTexture(BadlionFontRenderer.FontType fontType, boolean mipmapped) {
      switch($SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType()[fontType.ordinal()]) {
      case 1:
         if(mipmapped) {
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionFontHeader);
         } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(badlionFontHeader);
         }

         return this.fontDataHeader;
      case 2:
         if(mipmapped) {
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionFontTitle);
         } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(badlionFontTitle);
         }

         return this.fontDataTitle;
      case 3:
         if(mipmapped) {
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionFontText);
         } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(badlionFontText);
         }

         return this.fontDataText;
      default:
         return null;
      }
   }

   private void renderString(int x, int y, String string, int scale, BadlionFontRenderer.FontType fontType, boolean mipmapped) {
      this.renderString(x, y, string, scale, fontType, mipmapped, 0);
   }

   private void renderString(int x, int y, String string, int scale, BadlionFontRenderer.FontType fontType, boolean mipmapped, int italicsWeight) {
      GL11.glEnable(3553);
      Map<Integer, Integer> map = this.bindFontTexture(fontType, mipmapped);
      if(map != null) {
         int i = 0;
         boolean flag = false;
         GlStateManager.enableAlpha();
         GL11.glEnable(3042);

         char[] var14;
         for(char c0 : var14 = string.toCharArray()) {
            if(flag) {
               ++i;
               flag = false;
            } else if(c0 == 167 && i + 1 < string.length()) {
               int j = "0123456789abcdefklmnor".indexOf(string.toLowerCase().charAt(i + 1));
               if(j < 16) {
                  if(j < 0 || j > 15) {
                     j = 15;
                  }

                  int l1 = this.colorCode[j];
                  GL11.glColor4f((float)(l1 >> 16) / 255.0F, (float)(l1 >> 8 & 255) / 255.0F, (float)(l1 & 255) / 255.0F, 1.0F);
               }

               ++i;
               flag = true;
            } else if(map.containsKey(Integer.valueOf(c0))) {
               int k = ((Integer)map.get(Integer.valueOf(c0))).intValue();
               int l = (c0 - 32) * 128 % 2048;
               int i1 = (c0 - 32 >> 4) * 128;
               int j1 = k / scale;
               this.getClass();
               int k1 = 128 / scale;
               float f = 4.8828125E-4F;
               float f1 = 4.8828125E-4F;
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldrenderer = tessellator.getWorldRenderer();
               worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
               worldrenderer.pos((double)x, (double)(y + k1), 0.0D).tex((double)((float)l * f), (double)((float)(i1 + 128) * f1)).endVertex();
               worldrenderer.pos((double)(x + j1), (double)(y + k1), 0.0D).tex((double)((float)(l + k) * f), (double)((float)(i1 + 128) * f1)).endVertex();
               worldrenderer.pos((double)(x + j1 + italicsWeight), (double)y, 0.0D).tex((double)((float)(l + k) * f), (double)((float)i1 * f1)).endVertex();
               worldrenderer.pos((double)(x + italicsWeight), (double)y, 0.0D).tex((double)((float)l * f), (double)((float)i1 * f1)).endVertex();
               tessellator.draw();
               x += j1;
               ++i;
            }
         }

         GL11.glDisable(3042);
      }

   }

   public void drawString(int x, int y, String string, int fontHeight, BadlionFontRenderer.FontType fontType) {
      this.getClass();
      this.renderString(x, y, string, 128 / fontHeight, fontType, true);
   }

   public void drawString(int x, int y, String string, int fontHeight, BadlionFontRenderer.FontType fontType, boolean mipmapped) {
      this.getClass();
      this.renderString(x, y, string, 128 / fontHeight, fontType, mipmapped);
   }

   public void drawItalicizedString(int x, int y, String string, int fontHeight, BadlionFontRenderer.FontType fontType) {
      this.getClass();
      this.renderString(x, y, string, 128 / fontHeight, fontType, true, 4);
   }

   public void drawItalicizedString(int x, int y, String string, int fontHeight, BadlionFontRenderer.FontType fontType, boolean mipmapped) {
      this.getClass();
      this.renderString(x, y, string, 128 / fontHeight, fontType, mipmapped, 4);
   }

   public int getStringWidth(String string, BadlionFontRenderer.FontType fontType) {
      Map<Integer, Integer> map = this.getFontDataMap(fontType);
      if(map != null) {
         int i = 0;

         char[] var8;
         for(char c0 : var8 = string.toCharArray()) {
            if(map.containsKey(Integer.valueOf(c0))) {
               int j = ((Integer)map.get(Integer.valueOf(c0))).intValue();
               i += j;
            }
         }

         return i;
      } else {
         return 0;
      }
   }

   public int getCharWidth(char c, BadlionFontRenderer.FontType fontType) {
      Map<Integer, Integer> map = this.getFontDataMap(fontType);
      return map.containsKey(Integer.valueOf(c))?((Integer)map.get(Integer.valueOf(c))).intValue():0;
   }

   public int getStringWidth(String string, int fontHeight, BadlionFontRenderer.FontType fontType) {
      Map<Integer, Integer> map = this.getFontDataMap(fontType);
      int i = 0;
      int j = 0;
      boolean flag = false;

      char[] var11;
      for(char c0 : var11 = string.toCharArray()) {
         if(flag) {
            ++j;
            flag = false;
         } else if(c0 == 167 && j + 1 < string.length()) {
            ++j;
            flag = true;
         } else if(map.containsKey(Integer.valueOf(c0))) {
            int l = ((Integer)map.get(Integer.valueOf(c0))).intValue();
            this.getClass();
            int k = l / (128 / fontHeight);
            i += k;
         }
      }

      return i;
   }

   public int getCharWidth(char c, int fontHeight, BadlionFontRenderer.FontType fontType) {
      Map<Integer, Integer> map = this.getFontDataMap(fontType);
      if(map != null && map.containsKey(Integer.valueOf(c))) {
         int i = ((Integer)map.get(Integer.valueOf(c))).intValue();
         this.getClass();
         return i / (128 / fontHeight);
      } else {
         return 0;
      }
   }

   private Map getFontDataMap(BadlionFontRenderer.FontType fontType) {
      switch($SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType()[fontType.ordinal()]) {
      case 1:
         return this.fontDataHeader;
      case 2:
         return this.fontDataTitle;
      case 3:
         return this.fontDataText;
      default:
         return null;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType;
      if($SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType != null) {
         return var10000;
      } else {
         int[] var0 = new int[BadlionFontRenderer.FontType.values().length];

         try {
            var0[BadlionFontRenderer.FontType.HEADER.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BadlionFontRenderer.FontType.TEXT.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BadlionFontRenderer.FontType.TITLE.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$gui$BadlionFontRenderer$FontType = var0;
         return var0;
      }
   }

   public static enum FontType {
      HEADER,
      TITLE,
      TEXT;
   }
}
