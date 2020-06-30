package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.badlion.client.util.BlTextureManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MipMapSimpleTexture extends AbstractTexture {
   private static final Logger logger = LogManager.getLogger();
   private final ResourceLocation textureLocation;

   public MipMapSimpleTexture(ResourceLocation p_i3_1_) {
      this.textureLocation = p_i3_1_;
   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputstream = null;

      try {
         IResource iresource = resourceManager.getResource(this.textureLocation);
         inputstream = iresource.getInputStream();
         BufferedImage bufferedimage = ImageIO.read(inputstream);
         boolean flag = false;
         boolean flag1 = false;
         if(iresource.hasMetadata()) {
            try {
               TextureMetadataSection texturemetadatasection = (TextureMetadataSection)iresource.getMetadata("texture");
               if(texturemetadatasection != null) {
                  flag = texturemetadatasection.getTextureBlur();
                  flag1 = texturemetadatasection.getTextureClamp();
               }
            } catch (RuntimeException var11) {
               logger.warn((String)("Failed reading metadata of: " + this.textureLocation), (Throwable)var11);
            }
         }

         BlTextureManager.uploadTextureImageAllocateMipMap(this.getGlTextureId(), bufferedimage, flag, flag1);
      } finally {
         if(inputstream != null) {
            inputstream.close();
         }

      }

   }
}
