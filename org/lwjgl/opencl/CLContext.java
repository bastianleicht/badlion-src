package org.lwjgl.opencl;

import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.CLObjectRegistry;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.CLSampler;
import org.lwjgl.opencl.CallbackUtil;
import org.lwjgl.opencl.InfoUtil;
import org.lwjgl.opencl.api.Filter;
import org.lwjgl.opengl.Drawable;

public final class CLContext extends CLObjectChild {
   private static final CLContext.CLContextUtil util = (CLContext.CLContextUtil)CLPlatform.getInfoUtilInstance(CLContext.class, "CL_CONTEXT_UTIL");
   private final CLObjectRegistry clCommandQueues;
   private final CLObjectRegistry clMems;
   private final CLObjectRegistry clSamplers;
   private final CLObjectRegistry clPrograms;
   private final CLObjectRegistry clEvents;
   private long contextCallback;
   private long printfCallback;

   CLContext(long pointer, CLPlatform platform) {
      super(pointer, platform);
      if(this.isValid()) {
         this.clCommandQueues = new CLObjectRegistry();
         this.clMems = new CLObjectRegistry();
         this.clSamplers = new CLObjectRegistry();
         this.clPrograms = new CLObjectRegistry();
         this.clEvents = new CLObjectRegistry();
      } else {
         this.clCommandQueues = null;
         this.clMems = null;
         this.clSamplers = null;
         this.clPrograms = null;
         this.clEvents = null;
      }

   }

   public CLCommandQueue getCLCommandQueue(long id) {
      return (CLCommandQueue)this.clCommandQueues.getObject(id);
   }

   public CLMem getCLMem(long id) {
      return (CLMem)this.clMems.getObject(id);
   }

   public CLSampler getCLSampler(long id) {
      return (CLSampler)this.clSamplers.getObject(id);
   }

   public CLProgram getCLProgram(long id) {
      return (CLProgram)this.clPrograms.getObject(id);
   }

   public CLEvent getCLEvent(long id) {
      return (CLEvent)this.clEvents.getObject(id);
   }

   public static CLContext create(CLPlatform platform, List devices, IntBuffer errcode_ret) throws LWJGLException {
      return create(platform, devices, (CLContextCallback)null, (Drawable)null, errcode_ret);
   }

   public static CLContext create(CLPlatform platform, List devices, CLContextCallback pfn_notify, IntBuffer errcode_ret) throws LWJGLException {
      return create(platform, devices, pfn_notify, (Drawable)null, errcode_ret);
   }

   public static CLContext create(CLPlatform platform, List devices, CLContextCallback pfn_notify, Drawable share_drawable, IntBuffer errcode_ret) throws LWJGLException {
      return util.create(platform, devices, pfn_notify, share_drawable, errcode_ret);
   }

   public static CLContext createFromType(CLPlatform platform, long device_type, IntBuffer errcode_ret) throws LWJGLException {
      return util.createFromType(platform, device_type, (CLContextCallback)null, (Drawable)null, errcode_ret);
   }

   public static CLContext createFromType(CLPlatform platform, long device_type, CLContextCallback pfn_notify, IntBuffer errcode_ret) throws LWJGLException {
      return util.createFromType(platform, device_type, pfn_notify, (Drawable)null, errcode_ret);
   }

   public static CLContext createFromType(CLPlatform platform, long device_type, CLContextCallback pfn_notify, Drawable share_drawable, IntBuffer errcode_ret) throws LWJGLException {
      return util.createFromType(platform, device_type, pfn_notify, share_drawable, errcode_ret);
   }

   public int getInfoInt(int param_name) {
      return util.getInfoInt(this, param_name);
   }

   public List getInfoDevices() {
      return util.getInfoDevices(this);
   }

   public List getSupportedImageFormats(long flags, int image_type) {
      return this.getSupportedImageFormats(flags, image_type, (Filter)null);
   }

   public List getSupportedImageFormats(long flags, int image_type, Filter filter) {
      return util.getSupportedImageFormats(this, flags, image_type, filter);
   }

   CLObjectRegistry getCLCommandQueueRegistry() {
      return this.clCommandQueues;
   }

   CLObjectRegistry getCLMemRegistry() {
      return this.clMems;
   }

   CLObjectRegistry getCLSamplerRegistry() {
      return this.clSamplers;
   }

   CLObjectRegistry getCLProgramRegistry() {
      return this.clPrograms;
   }

   CLObjectRegistry getCLEventRegistry() {
      return this.clEvents;
   }

   private boolean checkCallback(long callback, int result) {
      if(result != 0 || callback != 0L && !this.isValid()) {
         if(callback != 0L) {
            CallbackUtil.deleteGlobalRef(callback);
         }

         return false;
      } else {
         return true;
      }
   }

   void setContextCallback(long callback) {
      if(this.checkCallback(callback, 0)) {
         this.contextCallback = callback;
      }

   }

   void setPrintfCallback(long callback, int result) {
      if(this.checkCallback(callback, result)) {
         this.printfCallback = callback;
      }

   }

   void releaseImpl() {
      if(this.release() <= 0) {
         if(this.contextCallback != 0L) {
            CallbackUtil.deleteGlobalRef(this.contextCallback);
         }

         if(this.printfCallback != 0L) {
            CallbackUtil.deleteGlobalRef(this.printfCallback);
         }

      }
   }

   interface CLContextUtil extends InfoUtil {
      List getInfoDevices(CLContext var1);

      CLContext create(CLPlatform var1, List var2, CLContextCallback var3, Drawable var4, IntBuffer var5) throws LWJGLException;

      CLContext createFromType(CLPlatform var1, long var2, CLContextCallback var4, Drawable var5, IntBuffer var6) throws LWJGLException;

      List getSupportedImageFormats(CLContext var1, long var2, int var4, Filter var5);
   }
}
