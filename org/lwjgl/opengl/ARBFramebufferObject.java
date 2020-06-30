package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL30;

public final class ARBFramebufferObject {
   public static final int GL_FRAMEBUFFER = 36160;
   public static final int GL_READ_FRAMEBUFFER = 36008;
   public static final int GL_DRAW_FRAMEBUFFER = 36009;
   public static final int GL_RENDERBUFFER = 36161;
   public static final int GL_STENCIL_INDEX1 = 36166;
   public static final int GL_STENCIL_INDEX4 = 36167;
   public static final int GL_STENCIL_INDEX8 = 36168;
   public static final int GL_STENCIL_INDEX16 = 36169;
   public static final int GL_RENDERBUFFER_WIDTH = 36162;
   public static final int GL_RENDERBUFFER_HEIGHT = 36163;
   public static final int GL_RENDERBUFFER_INTERNAL_FORMAT = 36164;
   public static final int GL_RENDERBUFFER_RED_SIZE = 36176;
   public static final int GL_RENDERBUFFER_GREEN_SIZE = 36177;
   public static final int GL_RENDERBUFFER_BLUE_SIZE = 36178;
   public static final int GL_RENDERBUFFER_ALPHA_SIZE = 36179;
   public static final int GL_RENDERBUFFER_DEPTH_SIZE = 36180;
   public static final int GL_RENDERBUFFER_STENCIL_SIZE = 36181;
   public static final int GL_RENDERBUFFER_SAMPLES = 36011;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 36052;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 33296;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 33297;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE = 33298;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 33299;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 33300;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 33301;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 33302;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 33303;
   public static final int GL_SRGB = 35904;
   public static final int GL_UNSIGNED_NORMALIZED = 35863;
   public static final int GL_FRAMEBUFFER_DEFAULT = 33304;
   public static final int GL_INDEX = 33314;
   public static final int GL_COLOR_ATTACHMENT0 = 36064;
   public static final int GL_COLOR_ATTACHMENT1 = 36065;
   public static final int GL_COLOR_ATTACHMENT2 = 36066;
   public static final int GL_COLOR_ATTACHMENT3 = 36067;
   public static final int GL_COLOR_ATTACHMENT4 = 36068;
   public static final int GL_COLOR_ATTACHMENT5 = 36069;
   public static final int GL_COLOR_ATTACHMENT6 = 36070;
   public static final int GL_COLOR_ATTACHMENT7 = 36071;
   public static final int GL_COLOR_ATTACHMENT8 = 36072;
   public static final int GL_COLOR_ATTACHMENT9 = 36073;
   public static final int GL_COLOR_ATTACHMENT10 = 36074;
   public static final int GL_COLOR_ATTACHMENT11 = 36075;
   public static final int GL_COLOR_ATTACHMENT12 = 36076;
   public static final int GL_COLOR_ATTACHMENT13 = 36077;
   public static final int GL_COLOR_ATTACHMENT14 = 36078;
   public static final int GL_COLOR_ATTACHMENT15 = 36079;
   public static final int GL_DEPTH_ATTACHMENT = 36096;
   public static final int GL_STENCIL_ATTACHMENT = 36128;
   public static final int GL_DEPTH_STENCIL_ATTACHMENT = 33306;
   public static final int GL_MAX_SAMPLES = 36183;
   public static final int GL_FRAMEBUFFER_COMPLETE = 36053;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
   public static final int GL_FRAMEBUFFER_UNSUPPORTED = 36061;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 36182;
   public static final int GL_FRAMEBUFFER_UNDEFINED = 33305;
   public static final int GL_FRAMEBUFFER_BINDING = 36006;
   public static final int GL_DRAW_FRAMEBUFFER_BINDING = 36006;
   public static final int GL_READ_FRAMEBUFFER_BINDING = 36010;
   public static final int GL_RENDERBUFFER_BINDING = 36007;
   public static final int GL_MAX_COLOR_ATTACHMENTS = 36063;
   public static final int GL_MAX_RENDERBUFFER_SIZE = 34024;
   public static final int GL_INVALID_FRAMEBUFFER_OPERATION = 1286;
   public static final int GL_DEPTH_STENCIL = 34041;
   public static final int GL_UNSIGNED_INT_24_8 = 34042;
   public static final int GL_DEPTH24_STENCIL8 = 35056;
   public static final int GL_TEXTURE_STENCIL_SIZE = 35057;

   public static boolean glIsRenderbuffer(int renderbuffer) {
      return GL30.glIsRenderbuffer(renderbuffer);
   }

   public static void glBindRenderbuffer(int target, int renderbuffer) {
      GL30.glBindRenderbuffer(target, renderbuffer);
   }

   public static void glDeleteRenderbuffers(IntBuffer renderbuffers) {
      GL30.glDeleteRenderbuffers(renderbuffers);
   }

   public static void glDeleteRenderbuffers(int renderbuffer) {
      GL30.glDeleteRenderbuffers(renderbuffer);
   }

   public static void glGenRenderbuffers(IntBuffer renderbuffers) {
      GL30.glGenRenderbuffers(renderbuffers);
   }

   public static int glGenRenderbuffers() {
      return GL30.glGenRenderbuffers();
   }

   public static void glRenderbufferStorage(int target, int internalformat, int width, int height) {
      GL30.glRenderbufferStorage(target, internalformat, width, height);
   }

   public static void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
      GL30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
   }

   public static void glGetRenderbufferParameter(int target, int pname, IntBuffer params) {
      GL30.glGetRenderbufferParameter(target, pname, params);
   }

   /** @deprecated */
   @Deprecated
   public static int glGetRenderbufferParameter(int target, int pname) {
      return glGetRenderbufferParameteri(target, pname);
   }

   public static int glGetRenderbufferParameteri(int target, int pname) {
      return GL30.glGetRenderbufferParameteri(target, pname);
   }

   public static boolean glIsFramebuffer(int framebuffer) {
      return GL30.glIsFramebuffer(framebuffer);
   }

   public static void glBindFramebuffer(int target, int framebuffer) {
      GL30.glBindFramebuffer(target, framebuffer);
   }

   public static void glDeleteFramebuffers(IntBuffer framebuffers) {
      GL30.glDeleteFramebuffers(framebuffers);
   }

   public static void glDeleteFramebuffers(int framebuffer) {
      GL30.glDeleteFramebuffers(framebuffer);
   }

   public static void glGenFramebuffers(IntBuffer framebuffers) {
      GL30.glGenFramebuffers(framebuffers);
   }

   public static int glGenFramebuffers() {
      return GL30.glGenFramebuffers();
   }

   public static int glCheckFramebufferStatus(int target) {
      return GL30.glCheckFramebufferStatus(target);
   }

   public static void glFramebufferTexture1D(int target, int attachment, int textarget, int texture, int level) {
      GL30.glFramebufferTexture1D(target, attachment, textarget, texture, level);
   }

   public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
      GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
   }

   public static void glFramebufferTexture3D(int target, int attachment, int textarget, int texture, int level, int layer) {
      GL30.glFramebufferTexture3D(target, attachment, textarget, texture, level, layer);
   }

   public static void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
      GL30.glFramebufferTextureLayer(target, attachment, texture, level, layer);
   }

   public static void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
      GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
   }

   public static void glGetFramebufferAttachmentParameter(int target, int attachment, int pname, IntBuffer params) {
      GL30.glGetFramebufferAttachmentParameter(target, attachment, pname, params);
   }

   /** @deprecated */
   @Deprecated
   public static int glGetFramebufferAttachmentParameter(int target, int attachment, int pname) {
      return GL30.glGetFramebufferAttachmentParameteri(target, attachment, pname);
   }

   public static int glGetFramebufferAttachmentParameteri(int target, int attachment, int pname) {
      return GL30.glGetFramebufferAttachmentParameteri(target, attachment, pname);
   }

   public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
      GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
   }

   public static void glGenerateMipmap(int target) {
      GL30.glGenerateMipmap(target);
   }
}
