package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTFramebufferObject {
   public static final int GL_FRAMEBUFFER_EXT = 36160;
   public static final int GL_RENDERBUFFER_EXT = 36161;
   public static final int GL_STENCIL_INDEX1_EXT = 36166;
   public static final int GL_STENCIL_INDEX4_EXT = 36167;
   public static final int GL_STENCIL_INDEX8_EXT = 36168;
   public static final int GL_STENCIL_INDEX16_EXT = 36169;
   public static final int GL_RENDERBUFFER_WIDTH_EXT = 36162;
   public static final int GL_RENDERBUFFER_HEIGHT_EXT = 36163;
   public static final int GL_RENDERBUFFER_INTERNAL_FORMAT_EXT = 36164;
   public static final int GL_RENDERBUFFER_RED_SIZE_EXT = 36176;
   public static final int GL_RENDERBUFFER_GREEN_SIZE_EXT = 36177;
   public static final int GL_RENDERBUFFER_BLUE_SIZE_EXT = 36178;
   public static final int GL_RENDERBUFFER_ALPHA_SIZE_EXT = 36179;
   public static final int GL_RENDERBUFFER_DEPTH_SIZE_EXT = 36180;
   public static final int GL_RENDERBUFFER_STENCIL_SIZE_EXT = 36181;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_EXT = 36048;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME_EXT = 36049;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL_EXT = 36050;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE_EXT = 36051;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_3D_ZOFFSET_EXT = 36052;
   public static final int GL_COLOR_ATTACHMENT0_EXT = 36064;
   public static final int GL_COLOR_ATTACHMENT1_EXT = 36065;
   public static final int GL_COLOR_ATTACHMENT2_EXT = 36066;
   public static final int GL_COLOR_ATTACHMENT3_EXT = 36067;
   public static final int GL_COLOR_ATTACHMENT4_EXT = 36068;
   public static final int GL_COLOR_ATTACHMENT5_EXT = 36069;
   public static final int GL_COLOR_ATTACHMENT6_EXT = 36070;
   public static final int GL_COLOR_ATTACHMENT7_EXT = 36071;
   public static final int GL_COLOR_ATTACHMENT8_EXT = 36072;
   public static final int GL_COLOR_ATTACHMENT9_EXT = 36073;
   public static final int GL_COLOR_ATTACHMENT10_EXT = 36074;
   public static final int GL_COLOR_ATTACHMENT11_EXT = 36075;
   public static final int GL_COLOR_ATTACHMENT12_EXT = 36076;
   public static final int GL_COLOR_ATTACHMENT13_EXT = 36077;
   public static final int GL_COLOR_ATTACHMENT14_EXT = 36078;
   public static final int GL_COLOR_ATTACHMENT15_EXT = 36079;
   public static final int GL_DEPTH_ATTACHMENT_EXT = 36096;
   public static final int GL_STENCIL_ATTACHMENT_EXT = 36128;
   public static final int GL_FRAMEBUFFER_COMPLETE_EXT = 36053;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT = 36054;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT = 36055;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT = 36057;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT = 36058;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT = 36059;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT = 36060;
   public static final int GL_FRAMEBUFFER_UNSUPPORTED_EXT = 36061;
   public static final int GL_FRAMEBUFFER_BINDING_EXT = 36006;
   public static final int GL_RENDERBUFFER_BINDING_EXT = 36007;
   public static final int GL_MAX_COLOR_ATTACHMENTS_EXT = 36063;
   public static final int GL_MAX_RENDERBUFFER_SIZE_EXT = 34024;
   public static final int GL_INVALID_FRAMEBUFFER_OPERATION_EXT = 1286;

   public static boolean glIsRenderbufferEXT(int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsRenderbufferEXT(renderbuffer, function_pointer);
      return __result;
   }

   static native boolean nglIsRenderbufferEXT(int var0, long var1);

   public static void glBindRenderbufferEXT(int target, int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindRenderbufferEXT(target, renderbuffer, function_pointer);
   }

   static native void nglBindRenderbufferEXT(int var0, int var1, long var2);

   public static void glDeleteRenderbuffersEXT(IntBuffer renderbuffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteRenderbuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(renderbuffers);
      nglDeleteRenderbuffersEXT(renderbuffers.remaining(), MemoryUtil.getAddress(renderbuffers), function_pointer);
   }

   static native void nglDeleteRenderbuffersEXT(int var0, long var1, long var3);

   public static void glDeleteRenderbuffersEXT(int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteRenderbuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteRenderbuffersEXT(1, APIUtil.getInt(caps, renderbuffer), function_pointer);
   }

   public static void glGenRenderbuffersEXT(IntBuffer renderbuffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenRenderbuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(renderbuffers);
      nglGenRenderbuffersEXT(renderbuffers.remaining(), MemoryUtil.getAddress(renderbuffers), function_pointer);
   }

   static native void nglGenRenderbuffersEXT(int var0, long var1, long var3);

   public static int glGenRenderbuffersEXT() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenRenderbuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer renderbuffers = APIUtil.getBufferInt(caps);
      nglGenRenderbuffersEXT(1, MemoryUtil.getAddress(renderbuffers), function_pointer);
      return renderbuffers.get(0);
   }

   public static void glRenderbufferStorageEXT(int target, int internalformat, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRenderbufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRenderbufferStorageEXT(target, internalformat, width, height, function_pointer);
   }

   static native void nglRenderbufferStorageEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glGetRenderbufferParameterEXT(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetRenderbufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetRenderbufferParameterivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetRenderbufferParameterivEXT(int var0, int var1, long var2, long var4);

   /** @deprecated */
   @Deprecated
   public static int glGetRenderbufferParameterEXT(int target, int pname) {
      return glGetRenderbufferParameteriEXT(target, pname);
   }

   public static int glGetRenderbufferParameteriEXT(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetRenderbufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetRenderbufferParameterivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static boolean glIsFramebufferEXT(int framebuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsFramebufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsFramebufferEXT(framebuffer, function_pointer);
      return __result;
   }

   static native boolean nglIsFramebufferEXT(int var0, long var1);

   public static void glBindFramebufferEXT(int target, int framebuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindFramebufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindFramebufferEXT(target, framebuffer, function_pointer);
   }

   static native void nglBindFramebufferEXT(int var0, int var1, long var2);

   public static void glDeleteFramebuffersEXT(IntBuffer framebuffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFramebuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(framebuffers);
      nglDeleteFramebuffersEXT(framebuffers.remaining(), MemoryUtil.getAddress(framebuffers), function_pointer);
   }

   static native void nglDeleteFramebuffersEXT(int var0, long var1, long var3);

   public static void glDeleteFramebuffersEXT(int framebuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFramebuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteFramebuffersEXT(1, APIUtil.getInt(caps, framebuffer), function_pointer);
   }

   public static void glGenFramebuffersEXT(IntBuffer framebuffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFramebuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(framebuffers);
      nglGenFramebuffersEXT(framebuffers.remaining(), MemoryUtil.getAddress(framebuffers), function_pointer);
   }

   static native void nglGenFramebuffersEXT(int var0, long var1, long var3);

   public static int glGenFramebuffersEXT() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFramebuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer framebuffers = APIUtil.getBufferInt(caps);
      nglGenFramebuffersEXT(1, MemoryUtil.getAddress(framebuffers), function_pointer);
      return framebuffers.get(0);
   }

   public static int glCheckFramebufferStatusEXT(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCheckFramebufferStatusEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglCheckFramebufferStatusEXT(target, function_pointer);
      return __result;
   }

   static native int nglCheckFramebufferStatusEXT(int var0, long var1);

   public static void glFramebufferTexture1DEXT(int target, int attachment, int textarget, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTexture1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTexture1DEXT(target, attachment, textarget, texture, level, function_pointer);
   }

   static native void nglFramebufferTexture1DEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glFramebufferTexture2DEXT(int target, int attachment, int textarget, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTexture2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTexture2DEXT(target, attachment, textarget, texture, level, function_pointer);
   }

   static native void nglFramebufferTexture2DEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glFramebufferTexture3DEXT(int target, int attachment, int textarget, int texture, int level, int zoffset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTexture3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTexture3DEXT(target, attachment, textarget, texture, level, zoffset, function_pointer);
   }

   static native void nglFramebufferTexture3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glFramebufferRenderbufferEXT(int target, int attachment, int renderbuffertarget, int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferRenderbufferEXT(target, attachment, renderbuffertarget, renderbuffer, function_pointer);
   }

   static native void nglFramebufferRenderbufferEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glGetFramebufferAttachmentParameterEXT(int target, int attachment, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferAttachmentParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetFramebufferAttachmentParameterivEXT(target, attachment, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetFramebufferAttachmentParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   /** @deprecated */
   @Deprecated
   public static int glGetFramebufferAttachmentParameterEXT(int target, int attachment, int pname) {
      return glGetFramebufferAttachmentParameteriEXT(target, attachment, pname);
   }

   public static int glGetFramebufferAttachmentParameteriEXT(int target, int attachment, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferAttachmentParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetFramebufferAttachmentParameterivEXT(target, attachment, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGenerateMipmapEXT(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenerateMipmapEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglGenerateMipmapEXT(target, function_pointer);
   }

   static native void nglGenerateMipmapEXT(int var0, long var1);
}
