package com.mojang.realmsclient.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class RealmsTextureManager {
   private static Map textures = new HashMap();
   private static Boolean useMultitextureArb;
   public static int GL_TEXTURE0 = -1;

   public static void bindWorldTemplate(String id, String image) {
      if(image == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int textureId = getTextureId(id, image);
         GL11.glBindTexture(3553, textureId);
      }
   }

   public static int getTextureId(String id, String image) {
      int textureId;
      if(textures.containsKey(id)) {
         RealmsTextureManager.RealmsTexture texture = (RealmsTextureManager.RealmsTexture)textures.get(id);
         if(texture.image.equals(image)) {
            return texture.textureId;
         }

         GL11.glDeleteTextures(texture.textureId);
         textureId = texture.textureId;
      } else {
         textureId = GL11.glGenTextures();
      }

      IntBuffer buf = null;
      int width = 0;
      int height = 0;

      try {
         InputStream in = new ByteArrayInputStream((new Base64()).decode(image));

         BufferedImage img;
         try {
            img = ImageIO.read(in);
         } finally {
            IOUtils.closeQuietly(in);
         }

         width = img.getWidth();
         height = img.getHeight();
         int[] data = new int[width * height];
         img.getRGB(0, 0, width, height, data, 0, width);
         buf = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
         buf.put(data);
         buf.flip();
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      if(GL_TEXTURE0 == -1) {
         if(getUseMultiTextureArb()) {
            GL_TEXTURE0 = '蓀';
         } else {
            GL_TEXTURE0 = '蓀';
         }
      }

      glActiveTexture(GL_TEXTURE0);
      GL11.glBindTexture(3553, textureId);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, '胡', '荧', (IntBuffer)buf);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
      textures.put(id, new RealmsTextureManager.RealmsTexture(image, textureId));
      return textureId;
   }

   public static void glActiveTexture(int texture) {
      if(getUseMultiTextureArb()) {
         ARBMultitexture.glActiveTextureARB(texture);
      } else {
         GL13.glActiveTexture(texture);
      }

   }

   public static boolean getUseMultiTextureArb() {
      if(useMultitextureArb == null) {
         ContextCapabilities caps = GLContext.getCapabilities();
         useMultitextureArb = Boolean.valueOf(caps.GL_ARB_multitexture && !caps.OpenGL13);
      }

      return useMultitextureArb.booleanValue();
   }

   public static class RealmsTexture {
      String image;
      int textureId;

      public RealmsTexture(String image, int textureId) {
         this.image = image;
         this.textureId = textureId;
      }
   }
}
