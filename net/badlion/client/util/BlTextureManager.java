package net.badlion.client.util;

import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.MipMapSimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class BlTextureManager {
   private static final Logger logger = LogManager.getLogger();
   private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
   private final Map mipMapTextures = Maps.newHashMap();
   private IResourceManager theResourceManager;

   public BlTextureManager(IResourceManager resourceManager) {
      this.theResourceManager = resourceManager;
   }

   public void bindTextureMipmapped(ResourceLocation resourceLocation) {
      ITextureObject itextureobject = (ITextureObject)this.mipMapTextures.get(resourceLocation);
      if(itextureobject == null) {
         itextureobject = new MipMapSimpleTexture(resourceLocation);
         this.loadTextureMipMap(resourceLocation, itextureobject);
      }

      GlStateManager.bindTexture(itextureobject.getGlTextureId());
   }

   public boolean loadTextureMipMap(ResourceLocation resourceLocation, final ITextureObject textureObject) {
      boolean flag = true;

      try {
         textureObject.loadTexture(this.theResourceManager);
      } catch (IOException var7) {
         logger.warn((String)("Failed to load texture: " + resourceLocation), (Throwable)var7);
         Object textObject1 = TextureUtil.missingTexture;
         this.mipMapTextures.put(resourceLocation, textureObject);
         flag = false;
      } catch (Throwable var8) {
         CrashReport crashreport = CrashReport.makeCrashReport(var8, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
         crashreportcategory.addCrashSection("Resource location", resourceLocation);
         crashreportcategory.addCrashSectionCallable("Texture object class", new Callable() {
            private static final String __OBFID = "CL_00001065";

            public String call() {
               return textureObject.getClass().getName();
            }
         });
         throw new ReportedException(crashreport);
      }

      this.mipMapTextures.put(resourceLocation, textureObject);
      return flag;
   }

   public static int uploadTextureImageAllocateMipMap(int glTextureId, BufferedImage textureImage, boolean blur, boolean clamp) {
      allocateTexture(glTextureId, 0, textureImage.getWidth(), textureImage.getHeight(), 1.0F);
      return uploadTextureImageSubMipMap(glTextureId, textureImage, 0, 0, blur, clamp);
   }

   private static void allocateTexture(int glTextureId, int p_147946_1_, int p_147946_2_, int p_147946_3_, float p_147946_4_) {
      GL11.glDeleteTextures(glTextureId);
      GL11.glBindTexture(3553, glTextureId);
      GL11.glTexImage2D(3553, 0, 6408, p_147946_2_ >> 0, p_147946_3_ >> 0, 0, '胡', '荧', (IntBuffer)null);
   }

   private static int uploadTextureImageSubMipMap(int glTextureId, BufferedImage image, int p_110995_2_, int p_110995_3_, boolean blur, boolean clamp) {
      GL11.glBindTexture(3553, glTextureId);
      uploadTextureImageSubImplMipMap(image, p_110995_2_, p_110995_3_, blur, clamp);
      GL30.glGenerateMipmap(3553);
      return glTextureId;
   }

   private static void uploadTextureImageSubImplMipMap(BufferedImage image, int p_110993_1_, int p_110993_2_, boolean blur, boolean clamp) {
      int i = image.getWidth();
      int j = image.getHeight();
      int k = 4194304 / i;
      int[] aint = new int[k * i];
      GL11.glTexParameteri(3553, 10241, 9987);
      setTextureClamped(clamp);

      for(int l = 0; l < i * j; l += i * k) {
         int i1 = l / i;
         int j1 = Math.min(k, j - i1);
         int k1 = i * j1;
         image.getRGB(0, i1, i, j1, aint, 0, i);
         copyToBuffer(aint, k1);
         GL11.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i, j1, '胡', '荧', (IntBuffer)dataBuffer);
      }

   }

   private static void setTextureClamped(boolean p_110997_0_) {
      if(p_110997_0_) {
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }

   }

   private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
      copyToBufferPos(p_110990_0_, 0, p_110990_1_);
   }

   private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
      int[] aint = p_110994_0_;
      if(Minecraft.getMinecraft().gameSettings.anaglyph) {
         aint = updateAnaglyph(p_110994_0_);
      }

      dataBuffer.clear();
      dataBuffer.put(aint, p_110994_1_, p_110994_2_);
      dataBuffer.position(0).limit(p_110994_2_);
   }

   public static int[] updateAnaglyph(int[] p_110985_0_) {
      int[] aint = new int[p_110985_0_.length];

      for(int i = 0; i < p_110985_0_.length; ++i) {
         int j = p_110985_0_[i] >> 24 & 255;
         int k = p_110985_0_[i] >> 16 & 255;
         int l = p_110985_0_[i] >> 8 & 255;
         int i1 = p_110985_0_[i] & 255;
         int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
         int k1 = (k * 30 + l * 70) / 100;
         int l1 = (k * 30 + i1 * 70) / 100;
         aint[i] = j << 24 | j1 << 16 | k1 << 8 | l1;
      }

      return aint;
   }
}
