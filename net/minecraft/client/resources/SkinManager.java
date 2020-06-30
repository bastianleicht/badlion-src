package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.badlion.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
   private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache skinCacheLoader;

   public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
      this.textureManager = textureManagerInstance;
      this.skinCacheDir = skinCacheDirectory;
      this.sessionService = sessionService;
      this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader() {
         public Map load(GameProfile p_load_1_) throws Exception {
            return Minecraft.getMinecraft().getSessionService().getTextures(p_load_1_, false);
         }
      });
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type p_152792_2_) {
      return this.loadSkin(profileTexture, p_152792_2_, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type p_152789_2_, final SkinManager.SkinAvailableCallback skinAvailableCallback) {
      final ResourceLocation resourcelocation = new ResourceLocation("skins/" + profileTexture.getHash());
      ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
      if(itextureobject != null) {
         if(skinAvailableCallback != null) {
            skinAvailableCallback.skinAvailable(p_152789_2_, resourcelocation, profileTexture);
         }
      } else {
         File file1 = new File(this.skinCacheDir, profileTexture.getHash().length() > 2?profileTexture.getHash().substring(0, 2):"xx");
         File file2 = new File(file1, profileTexture.getHash());
         final IImageBuffer iimagebuffer = p_152789_2_ == MinecraftProfileTexture.Type.SKIN?new ImageBufferDownload():null;
         ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
            public BufferedImage parseUserSkin(BufferedImage image) {
               if(iimagebuffer != null) {
                  image = iimagebuffer.parseUserSkin(image);
               }

               return image;
            }

            public void skinAvailable() {
               if(iimagebuffer != null) {
                  iimagebuffer.skinAvailable();
               }

               if(skinAvailableCallback != null) {
                  skinAvailableCallback.skinAvailable(p_152789_2_, resourcelocation, profileTexture);
               }

            }
         });
         this.textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
      }

      return resourcelocation;
   }

   public void loadProfileTextures(GameProfile profile, SkinManager.SkinAvailableCallback skinAvailableCallback, boolean requireSecure) {
      this.loadProfileTextures(profile, skinAvailableCallback, requireSecure, false);
   }

   public void loadProfileTextures(final GameProfile p_loadProfileTextures_1_, final SkinManager.SkinAvailableCallback p_loadProfileTextures_2_, final boolean p_loadProfileTextures_3_, final boolean p_loadProfileTextures_4_) {
      Wrapper.getInstance().getCapeManager().checkUser(p_loadProfileTextures_1_.getId());
      THREAD_POOL.submit(new Runnable() {
         public void run() {
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();

            try {
               map.putAll(SkinManager.this.sessionService.getTextures(p_loadProfileTextures_1_, p_loadProfileTextures_3_));
            } catch (InsecureTextureException var3) {
               ;
            }

            if(map.isEmpty() && (p_loadProfileTextures_4_ || p_loadProfileTextures_1_.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId()))) {
               p_loadProfileTextures_1_.getProperties().clear();
               p_loadProfileTextures_1_.getProperties().putAll(Minecraft.getMinecraft().func_181037_M());
               map.putAll(SkinManager.this.sessionService.getTextures(p_loadProfileTextures_1_, false));
            }

            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
               public void run() {
                  if(map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, p_loadProfileTextures_2_);
                  }

                  if(map.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, p_loadProfileTextures_2_);
                  }

               }
            });
         }
      });
   }

   public Map loadSkinFromCache(GameProfile profile) {
      return (Map)this.skinCacheLoader.getUnchecked(profile);
   }

   public interface SkinAvailableCallback {
      void skinAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
