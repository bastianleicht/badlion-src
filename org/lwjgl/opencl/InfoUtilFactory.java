package org.lwjgl.opencl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferChecks;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.PointerWrapper;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.opencl.CL11;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLChecks;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLObject;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.CLSampler;
import org.lwjgl.opencl.CallbackUtil;
import org.lwjgl.opencl.InfoUtil;
import org.lwjgl.opencl.InfoUtilAbstract;
import org.lwjgl.opencl.Util;
import org.lwjgl.opencl.api.CLBufferRegion;
import org.lwjgl.opencl.api.CLImageFormat;
import org.lwjgl.opencl.api.Filter;
import org.lwjgl.opengl.Drawable;

final class InfoUtilFactory {
   static final InfoUtil CL_COMMAND_QUEUE_UTIL = new InfoUtilAbstract() {
      protected int getInfo(CLCommandQueue object, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetCommandQueueInfo(object, param_name, param_value, (PointerBuffer)null);
      }
   };
   static final CLContext.CLContextUtil CL_CONTEXT_UTIL = new InfoUtilFactory.CLContextUtil();
   static final InfoUtil CL_DEVICE_UTIL = new InfoUtilFactory.CLDeviceUtil();
   static final CLEvent.CLEventUtil CL_EVENT_UTIL = new InfoUtilFactory.CLEventUtil();
   static final CLKernel.CLKernelUtil CL_KERNEL_UTIL = new InfoUtilFactory.CLKernelUtil();
   static final CLMem.CLMemUtil CL_MEM_UTIL = new InfoUtilFactory.CLMemUtil();
   static final CLPlatform.CLPlatformUtil CL_PLATFORM_UTIL = new InfoUtilFactory.CLPlatformUtil();
   static final CLProgram.CLProgramUtil CL_PROGRAM_UTIL = new InfoUtilFactory.CLProgramUtil();
   static final InfoUtil CL_SAMPLER_UTIL = new InfoUtilAbstract() {
      protected int getInfo(CLSampler sampler, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetSamplerInfo(sampler, param_name, param_value, param_value_size_ret);
      }
   };

   private static final class CLContextUtil extends InfoUtilAbstract implements CLContext.CLContextUtil {
      private CLContextUtil() {
      }

      protected int getInfo(CLContext context, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetContextInfo(context, param_name, param_value, param_value_size_ret);
      }

      public List getInfoDevices(CLContext context) {
         context.checkValid();
         int num_devices;
         if(CLCapabilities.getPlatformCapabilities((CLPlatform)context.getParent()).OpenCL11) {
            num_devices = this.getInfoInt(context, 4227);
         } else {
            PointerBuffer size_ret = APIUtil.getBufferPointer();
            CL10.clGetContextInfo(context, 4225, (ByteBuffer)null, size_ret);
            num_devices = (int)(size_ret.get(0) / (long)PointerBuffer.getPointerSize());
         }

         PointerBuffer deviceIDs = APIUtil.getBufferPointer(num_devices);
         CL10.clGetContextInfo(context, 4225, deviceIDs.getBuffer(), (PointerBuffer)null);
         List<CLDevice> devices = new ArrayList(num_devices);

         for(int i = 0; i < num_devices; ++i) {
            devices.add(((CLPlatform)context.getParent()).getCLDevice(deviceIDs.get(i)));
         }

         return devices.size() == 0?null:devices;
      }

      public CLContext create(CLPlatform platform, List devices, CLContextCallback pfn_notify, Drawable share_drawable, IntBuffer errcode_ret) throws LWJGLException {
         int propertyCount = 2 + (share_drawable == null?0:4) + 1;
         PointerBuffer properties = APIUtil.getBufferPointer(propertyCount + devices.size());
         properties.put(4228L).put((PointerWrapper)platform);
         if(share_drawable != null) {
            share_drawable.setCLSharingProperties(properties);
         }

         properties.put(0L);
         properties.position(propertyCount);

         for(CLDevice device : devices) {
            properties.put((PointerWrapper)device);
         }

         long function_pointer = CLCapabilities.clCreateContext;
         BufferChecks.checkFunctionAddress(function_pointer);
         if(errcode_ret != null) {
            BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
         } else if(LWJGLUtil.DEBUG) {
            errcode_ret = APIUtil.getBufferInt();
         }

         long user_data = pfn_notify != null && !pfn_notify.isCustom()?CallbackUtil.createGlobalRef(pfn_notify):0L;
         CLContext __result = null;

         CLContext var13;
         try {
            __result = new CLContext(CL10.nclCreateContext(MemoryUtil.getAddress0((Buffer)properties.getBuffer()), devices.size(), MemoryUtil.getAddress(properties, propertyCount), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), platform);
            if(LWJGLUtil.DEBUG) {
               Util.checkCLError(errcode_ret.get(0));
            }

            var13 = __result;
         } finally {
            if(__result != null) {
               __result.setContextCallback(user_data);
            }

         }

         return var13;
      }

      public CLContext createFromType(CLPlatform platform, long device_type, CLContextCallback pfn_notify, Drawable share_drawable, IntBuffer errcode_ret) throws LWJGLException {
         int propertyCount = 2 + (share_drawable == null?0:4) + 1;
         PointerBuffer properties = APIUtil.getBufferPointer(propertyCount);
         properties.put(4228L).put((PointerWrapper)platform);
         if(share_drawable != null) {
            share_drawable.setCLSharingProperties(properties);
         }

         properties.put(0L);
         properties.flip();
         return CL10.clCreateContextFromType(properties, device_type, pfn_notify, errcode_ret);
      }

      public List getSupportedImageFormats(CLContext context, long flags, int image_type, Filter filter) {
         IntBuffer numBuffer = APIUtil.getBufferInt();
         CL10.clGetSupportedImageFormats(context, flags, image_type, (ByteBuffer)null, numBuffer);
         int num_image_formats = numBuffer.get(0);
         if(num_image_formats == 0) {
            return null;
         } else {
            ByteBuffer formatBuffer = BufferUtils.createByteBuffer(num_image_formats * 8);
            CL10.clGetSupportedImageFormats(context, flags, image_type, formatBuffer, (IntBuffer)null);
            List<CLImageFormat> formats = new ArrayList(num_image_formats);

            for(int i = 0; i < num_image_formats; ++i) {
               int offset = num_image_formats * 8;
               CLImageFormat format = new CLImageFormat(formatBuffer.getInt(offset), formatBuffer.getInt(offset + 4));
               if(filter == null || filter.accept(format)) {
                  formats.add(format);
               }
            }

            return formats.size() == 0?null:formats;
         }
      }
   }

   private static final class CLDeviceUtil extends InfoUtilAbstract {
      private CLDeviceUtil() {
      }

      protected int getInfo(CLDevice device, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetDeviceInfo(device, param_name, param_value, param_value_size_ret);
      }

      protected int getInfoSizeArraySize(CLDevice device, int param_name) {
         switch(param_name) {
         case 4101:
            return this.getInfoInt(device, 4099);
         default:
            throw new IllegalArgumentException("Unsupported parameter: " + LWJGLUtil.toHexString(param_name));
         }
      }
   }

   private static final class CLEventUtil extends InfoUtilAbstract implements CLEvent.CLEventUtil {
      private CLEventUtil() {
      }

      protected int getInfo(CLEvent event, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetEventInfo(event, param_name, param_value, param_value_size_ret);
      }

      public long getProfilingInfoLong(CLEvent event, int param_name) {
         event.checkValid();
         ByteBuffer buffer = APIUtil.getBufferByte(8);
         CL10.clGetEventProfilingInfo(event, param_name, buffer, (PointerBuffer)null);
         return buffer.getLong(0);
      }
   }

   private static final class CLKernelUtil extends InfoUtilAbstract implements CLKernel.CLKernelUtil {
      private CLKernelUtil() {
      }

      public void setArg(CLKernel kernel, int index, byte value) {
         CL10.clSetKernelArg(kernel, index, 1L, APIUtil.getBufferByte(1).put(0, value));
      }

      public void setArg(CLKernel kernel, int index, short value) {
         CL10.clSetKernelArg(kernel, index, 2L, APIUtil.getBufferShort().put(0, value));
      }

      public void setArg(CLKernel kernel, int index, int value) {
         CL10.clSetKernelArg(kernel, index, 4L, APIUtil.getBufferInt().put(0, value));
      }

      public void setArg(CLKernel kernel, int index, long value) {
         CL10.clSetKernelArg(kernel, index, 8L, APIUtil.getBufferLong().put(0, value));
      }

      public void setArg(CLKernel kernel, int index, float value) {
         CL10.clSetKernelArg(kernel, index, 4L, APIUtil.getBufferFloat().put(0, value));
      }

      public void setArg(CLKernel kernel, int index, double value) {
         CL10.clSetKernelArg(kernel, index, 8L, APIUtil.getBufferDouble().put(0, value));
      }

      public void setArg(CLKernel kernel, int index, CLObject value) {
         CL10.clSetKernelArg(kernel, index, value);
      }

      public void setArgSize(CLKernel kernel, int index, long size) {
         CL10.clSetKernelArg(kernel, index, size);
      }

      protected int getInfo(CLKernel kernel, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetKernelInfo(kernel, param_name, param_value, param_value_size_ret);
      }

      public long getWorkGroupInfoSize(CLKernel kernel, CLDevice device, int param_name) {
         device.checkValid();
         PointerBuffer buffer = APIUtil.getBufferPointer();
         CL10.clGetKernelWorkGroupInfo(kernel, device, param_name, buffer.getBuffer(), (PointerBuffer)null);
         return buffer.get(0);
      }

      public long[] getWorkGroupInfoSizeArray(CLKernel kernel, CLDevice device, int param_name) {
         device.checkValid();
         switch(param_name) {
         case 4529:
            int size = 3;
            PointerBuffer buffer = APIUtil.getBufferPointer(size);
            CL10.clGetKernelWorkGroupInfo(kernel, device, param_name, buffer.getBuffer(), (PointerBuffer)null);
            long[] array = new long[size];

            for(int i = 0; i < size; ++i) {
               array[i] = buffer.get(i);
            }

            return array;
         default:
            throw new IllegalArgumentException("Unsupported parameter: " + LWJGLUtil.toHexString(param_name));
         }
      }

      public long getWorkGroupInfoLong(CLKernel kernel, CLDevice device, int param_name) {
         device.checkValid();
         ByteBuffer buffer = APIUtil.getBufferByte(8);
         CL10.clGetKernelWorkGroupInfo(kernel, device, param_name, buffer, (PointerBuffer)null);
         return buffer.getLong(0);
      }
   }

   private static final class CLMemUtil extends InfoUtilAbstract implements CLMem.CLMemUtil {
      private CLMemUtil() {
      }

      protected int getInfo(CLMem mem, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetMemObjectInfo(mem, param_name, param_value, param_value_size_ret);
      }

      public CLMem createImage2D(CLContext context, long flags, CLImageFormat image_format, long image_width, long image_height, long image_row_pitch, Buffer host_ptr, IntBuffer errcode_ret) {
         ByteBuffer formatBuffer = APIUtil.getBufferByte(8);
         formatBuffer.putInt(0, image_format.getChannelOrder());
         formatBuffer.putInt(4, image_format.getChannelType());
         long function_pointer = CLCapabilities.clCreateImage2D;
         BufferChecks.checkFunctionAddress(function_pointer);
         if(errcode_ret != null) {
            BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
         } else if(LWJGLUtil.DEBUG) {
            errcode_ret = APIUtil.getBufferInt();
         }

         CLMem __result = new CLMem(CL10.nclCreateImage2D(context.getPointer(), flags, MemoryUtil.getAddress((ByteBuffer)formatBuffer, 0), image_width, image_height, image_row_pitch, MemoryUtil.getAddress0Safe(host_ptr) + (long)(host_ptr != null?BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage2DSize(formatBuffer, image_width, image_height, image_row_pitch)):0), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
         if(LWJGLUtil.DEBUG) {
            Util.checkCLError(errcode_ret.get(0));
         }

         return __result;
      }

      public CLMem createImage3D(CLContext context, long flags, CLImageFormat image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, Buffer host_ptr, IntBuffer errcode_ret) {
         ByteBuffer formatBuffer = APIUtil.getBufferByte(8);
         formatBuffer.putInt(0, image_format.getChannelOrder());
         formatBuffer.putInt(4, image_format.getChannelType());
         long function_pointer = CLCapabilities.clCreateImage3D;
         BufferChecks.checkFunctionAddress(function_pointer);
         if(errcode_ret != null) {
            BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
         } else if(LWJGLUtil.DEBUG) {
            errcode_ret = APIUtil.getBufferInt();
         }

         CLMem __result = new CLMem(CL10.nclCreateImage3D(context.getPointer(), flags, MemoryUtil.getAddress((ByteBuffer)formatBuffer, 0), image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, MemoryUtil.getAddress0Safe(host_ptr) + (long)(host_ptr != null?BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage3DSize(formatBuffer, image_width, image_height, image_depth, image_row_pitch, image_slice_pitch)):0), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
         if(LWJGLUtil.DEBUG) {
            Util.checkCLError(errcode_ret.get(0));
         }

         return __result;
      }

      public CLMem createSubBuffer(CLMem mem, long flags, int buffer_create_type, CLBufferRegion buffer_create_info, IntBuffer errcode_ret) {
         PointerBuffer infoBuffer = APIUtil.getBufferPointer(2);
         infoBuffer.put((long)buffer_create_info.getOrigin());
         infoBuffer.put((long)buffer_create_info.getSize());
         return CL11.clCreateSubBuffer(mem, flags, buffer_create_type, infoBuffer.getBuffer(), errcode_ret);
      }

      public ByteBuffer getInfoHostBuffer(CLMem mem) {
         mem.checkValid();
         if(LWJGLUtil.DEBUG) {
            long mem_flags = this.getInfoLong(mem, 4353);
            if((mem_flags & 8L) != 8L) {
               throw new IllegalArgumentException("The specified CLMem object does not use host memory.");
            }
         }

         long size = this.getInfoSize(mem, 4354);
         if(size == 0L) {
            return null;
         } else {
            long address = this.getInfoSize(mem, 4355);
            return CL.getHostBuffer(address, (int)size);
         }
      }

      public long getImageInfoSize(CLMem mem, int param_name) {
         mem.checkValid();
         PointerBuffer buffer = APIUtil.getBufferPointer();
         CL10.clGetImageInfo(mem, param_name, buffer.getBuffer(), (PointerBuffer)null);
         return buffer.get(0);
      }

      public CLImageFormat getImageInfoFormat(CLMem mem) {
         mem.checkValid();
         ByteBuffer format = APIUtil.getBufferByte(8);
         CL10.clGetImageInfo(mem, 4368, format, (PointerBuffer)null);
         return new CLImageFormat(format.getInt(0), format.getInt(4));
      }

      public int getImageInfoFormat(CLMem mem, int index) {
         mem.checkValid();
         ByteBuffer format = APIUtil.getBufferByte(8);
         CL10.clGetImageInfo(mem, 4368, format, (PointerBuffer)null);
         return format.getInt(index << 2);
      }

      public int getGLObjectType(CLMem mem) {
         mem.checkValid();
         IntBuffer buffer = APIUtil.getBufferInt();
         CL10GL.clGetGLObjectInfo(mem, buffer, (IntBuffer)null);
         return buffer.get(0);
      }

      public int getGLObjectName(CLMem mem) {
         mem.checkValid();
         IntBuffer buffer = APIUtil.getBufferInt();
         CL10GL.clGetGLObjectInfo(mem, (IntBuffer)null, buffer);
         return buffer.get(0);
      }

      public int getGLTextureInfoInt(CLMem mem, int param_name) {
         mem.checkValid();
         ByteBuffer buffer = APIUtil.getBufferByte(4);
         CL10GL.clGetGLTextureInfo(mem, param_name, buffer, (PointerBuffer)null);
         return buffer.getInt(0);
      }
   }

   private static final class CLPlatformUtil extends InfoUtilAbstract implements CLPlatform.CLPlatformUtil {
      private CLPlatformUtil() {
      }

      protected int getInfo(CLPlatform platform, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetPlatformInfo(platform, param_name, param_value, param_value_size_ret);
      }

      public List getPlatforms(Filter filter) {
         IntBuffer numBuffer = APIUtil.getBufferInt();
         CL10.clGetPlatformIDs((PointerBuffer)null, numBuffer);
         int num_platforms = numBuffer.get(0);
         if(num_platforms == 0) {
            return null;
         } else {
            PointerBuffer platformIDs = APIUtil.getBufferPointer(num_platforms);
            CL10.clGetPlatformIDs(platformIDs, (IntBuffer)null);
            List<CLPlatform> platforms = new ArrayList(num_platforms);

            for(int i = 0; i < num_platforms; ++i) {
               CLPlatform platform = CLPlatform.getCLPlatform(platformIDs.get(i));
               if(filter == null || filter.accept(platform)) {
                  platforms.add(platform);
               }
            }

            return platforms.size() == 0?null:platforms;
         }
      }

      public List getDevices(CLPlatform platform, int device_type, Filter filter) {
         platform.checkValid();
         IntBuffer numBuffer = APIUtil.getBufferInt();
         CL10.clGetDeviceIDs(platform, (long)device_type, (PointerBuffer)null, numBuffer);
         int num_devices = numBuffer.get(0);
         if(num_devices == 0) {
            return null;
         } else {
            PointerBuffer deviceIDs = APIUtil.getBufferPointer(num_devices);
            CL10.clGetDeviceIDs(platform, (long)device_type, deviceIDs, (IntBuffer)null);
            List<CLDevice> devices = new ArrayList(num_devices);

            for(int i = 0; i < num_devices; ++i) {
               CLDevice device = platform.getCLDevice(deviceIDs.get(i));
               if(filter == null || filter.accept(device)) {
                  devices.add(device);
               }
            }

            return devices.size() == 0?null:devices;
         }
      }
   }

   private static final class CLProgramUtil extends InfoUtilAbstract implements CLProgram.CLProgramUtil {
      private CLProgramUtil() {
      }

      protected int getInfo(CLProgram program, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
         return CL10.clGetProgramInfo(program, param_name, param_value, param_value_size_ret);
      }

      protected int getInfoSizeArraySize(CLProgram program, int param_name) {
         switch(param_name) {
         case 4453:
            return this.getInfoInt(program, 4450);
         default:
            throw new IllegalArgumentException("Unsupported parameter: " + LWJGLUtil.toHexString(param_name));
         }
      }

      public CLKernel[] createKernelsInProgram(CLProgram program) {
         IntBuffer numBuffer = APIUtil.getBufferInt();
         CL10.clCreateKernelsInProgram(program, (PointerBuffer)null, numBuffer);
         int num_kernels = numBuffer.get(0);
         if(num_kernels == 0) {
            return null;
         } else {
            PointerBuffer kernelIDs = APIUtil.getBufferPointer(num_kernels);
            CL10.clCreateKernelsInProgram(program, kernelIDs, (IntBuffer)null);
            CLKernel[] kernels = new CLKernel[num_kernels];

            for(int i = 0; i < num_kernels; ++i) {
               kernels[i] = program.getCLKernel(kernelIDs.get(i));
            }

            return kernels;
         }
      }

      public CLDevice[] getInfoDevices(CLProgram program) {
         program.checkValid();
         int size = this.getInfoInt(program, 4450);
         PointerBuffer buffer = APIUtil.getBufferPointer(size);
         CL10.clGetProgramInfo(program, 4451, buffer.getBuffer(), (PointerBuffer)null);
         CLPlatform platform = (CLPlatform)((CLContext)program.getParent()).getParent();
         CLDevice[] array = new CLDevice[size];

         for(int i = 0; i < size; ++i) {
            array[i] = platform.getCLDevice(buffer.get(i));
         }

         return array;
      }

      public ByteBuffer getInfoBinaries(CLProgram program, ByteBuffer target) {
         program.checkValid();
         PointerBuffer sizes = this.getSizesBuffer(program, 4453);
         int totalSize = 0;

         for(int i = 0; i < sizes.limit(); ++i) {
            totalSize = (int)((long)totalSize + sizes.get(i));
         }

         if(target == null) {
            target = BufferUtils.createByteBuffer(totalSize);
         } else if(LWJGLUtil.DEBUG) {
            BufferChecks.checkBuffer(target, totalSize);
         }

         CL10.clGetProgramInfo(program, sizes, target, (PointerBuffer)null);
         return target;
      }

      public ByteBuffer[] getInfoBinaries(CLProgram program, ByteBuffer[] target) {
         program.checkValid();
         if(target == null) {
            PointerBuffer sizes = this.getSizesBuffer(program, 4453);
            target = new ByteBuffer[sizes.remaining()];

            for(int i = 0; i < sizes.remaining(); ++i) {
               target[i] = BufferUtils.createByteBuffer((int)sizes.get(i));
            }
         } else if(LWJGLUtil.DEBUG) {
            PointerBuffer sizes = this.getSizesBuffer(program, 4453);
            if(target.length < sizes.remaining()) {
               throw new IllegalArgumentException("The target array is not big enough: " + sizes.remaining() + " buffers are required.");
            }

            for(int i = 0; i < target.length; ++i) {
               BufferChecks.checkBuffer(target[i], (int)sizes.get(i));
            }
         }

         CL10.clGetProgramInfo(program, target, (PointerBuffer)null);
         return target;
      }

      public String getBuildInfoString(CLProgram program, CLDevice device, int param_name) {
         program.checkValid();
         int bytes = getBuildSizeRet(program, device, param_name);
         if(bytes <= 1) {
            return null;
         } else {
            ByteBuffer buffer = APIUtil.getBufferByte(bytes);
            CL10.clGetProgramBuildInfo(program, device, param_name, buffer, (PointerBuffer)null);
            buffer.limit(bytes - 1);
            return APIUtil.getString(buffer);
         }
      }

      public int getBuildInfoInt(CLProgram program, CLDevice device, int param_name) {
         program.checkValid();
         ByteBuffer buffer = APIUtil.getBufferByte(4);
         CL10.clGetProgramBuildInfo(program, device, param_name, buffer, (PointerBuffer)null);
         return buffer.getInt(0);
      }

      private static int getBuildSizeRet(CLProgram program, CLDevice device, int param_name) {
         PointerBuffer bytes = APIUtil.getBufferPointer();
         int errcode = CL10.clGetProgramBuildInfo(program, device, param_name, (ByteBuffer)null, bytes);
         if(errcode != 0) {
            throw new IllegalArgumentException("Invalid parameter specified: " + LWJGLUtil.toHexString(param_name));
         } else {
            return (int)bytes.get(0);
         }
      }
   }
}
