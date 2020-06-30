package org.lwjgl.opencl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.InfoUtil;
import org.lwjgl.opencl.api.CLBufferRegion;
import org.lwjgl.opencl.api.CLImageFormat;

public final class CLMem extends CLObjectChild {
   private static final CLMem.CLMemUtil util = (CLMem.CLMemUtil)CLPlatform.getInfoUtilInstance(CLMem.class, "CL_MEM_UTIL");

   CLMem(long pointer, CLContext context) {
      super(pointer, context);
      if(this.isValid()) {
         context.getCLMemRegistry().registerObject(this);
      }

   }

   public static CLMem createImage2D(CLContext context, long flags, CLImageFormat image_format, long image_width, long image_height, long image_row_pitch, Buffer host_ptr, IntBuffer errcode_ret) {
      return util.createImage2D(context, flags, image_format, image_width, image_height, image_row_pitch, host_ptr, errcode_ret);
   }

   public static CLMem createImage3D(CLContext context, long flags, CLImageFormat image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, Buffer host_ptr, IntBuffer errcode_ret) {
      return util.createImage3D(context, flags, image_format, image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, host_ptr, errcode_ret);
   }

   public CLMem createSubBuffer(long flags, int buffer_create_type, CLBufferRegion buffer_create_info, IntBuffer errcode_ret) {
      return util.createSubBuffer(this, flags, buffer_create_type, buffer_create_info, errcode_ret);
   }

   public int getInfoInt(int param_name) {
      return util.getInfoInt(this, param_name);
   }

   public long getInfoSize(int param_name) {
      return util.getInfoSize(this, param_name);
   }

   public long getInfoLong(int param_name) {
      return util.getInfoLong(this, param_name);
   }

   public ByteBuffer getInfoHostBuffer() {
      return util.getInfoHostBuffer(this);
   }

   public long getImageInfoSize(int param_name) {
      return util.getImageInfoSize(this, param_name);
   }

   public CLImageFormat getImageFormat() {
      return util.getImageInfoFormat(this);
   }

   public int getImageChannelOrder() {
      return util.getImageInfoFormat(this, 0);
   }

   public int getImageChannelType() {
      return util.getImageInfoFormat(this, 1);
   }

   public int getGLObjectType() {
      return util.getGLObjectType(this);
   }

   public int getGLObjectName() {
      return util.getGLObjectName(this);
   }

   public int getGLTextureInfoInt(int param_name) {
      return util.getGLTextureInfoInt(this, param_name);
   }

   static CLMem create(long pointer, CLContext context) {
      CLMem clMem = (CLMem)context.getCLMemRegistry().getObject(pointer);
      if(clMem == null) {
         clMem = new CLMem(pointer, context);
      } else {
         clMem.retain();
      }

      return clMem;
   }

   int release() {
      int var1;
      try {
         var1 = super.release();
      } finally {
         if(!this.isValid()) {
            ((CLContext)this.getParent()).getCLMemRegistry().unregisterObject(this);
         }

      }

      return var1;
   }

   interface CLMemUtil extends InfoUtil {
      CLMem createImage2D(CLContext var1, long var2, CLImageFormat var4, long var5, long var7, long var9, Buffer var11, IntBuffer var12);

      CLMem createImage3D(CLContext var1, long var2, CLImageFormat var4, long var5, long var7, long var9, long var11, long var13, Buffer var15, IntBuffer var16);

      CLMem createSubBuffer(CLMem var1, long var2, int var4, CLBufferRegion var5, IntBuffer var6);

      ByteBuffer getInfoHostBuffer(CLMem var1);

      long getImageInfoSize(CLMem var1, int var2);

      CLImageFormat getImageInfoFormat(CLMem var1);

      int getImageInfoFormat(CLMem var1, int var2);

      int getGLObjectType(CLMem var1);

      int getGLObjectName(CLMem var1);

      int getGLTextureInfoInt(CLMem var1, int var2);
   }
}
