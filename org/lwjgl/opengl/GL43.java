package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerWrapper;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.CallbackUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.KHRDebugCallback;

public final class GL43 {
   public static final int GL_NUM_SHADING_LANGUAGE_VERSIONS = 33513;
   public static final int GL_VERTEX_ATTRIB_ARRAY_LONG = 34638;
   public static final int GL_COMPRESSED_RGB8_ETC2 = 37492;
   public static final int GL_COMPRESSED_SRGB8_ETC2 = 37493;
   public static final int GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37494;
   public static final int GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37495;
   public static final int GL_COMPRESSED_RGBA8_ETC2_EAC = 37496;
   public static final int GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 37497;
   public static final int GL_COMPRESSED_R11_EAC = 37488;
   public static final int GL_COMPRESSED_SIGNED_R11_EAC = 37489;
   public static final int GL_COMPRESSED_RG11_EAC = 37490;
   public static final int GL_COMPRESSED_SIGNED_RG11_EAC = 37491;
   public static final int GL_PRIMITIVE_RESTART_FIXED_INDEX = 36201;
   public static final int GL_ANY_SAMPLES_PASSED_CONSERVATIVE = 36202;
   public static final int GL_MAX_ELEMENT_INDEX = 36203;
   public static final int GL_COMPUTE_SHADER = 37305;
   public static final int GL_MAX_COMPUTE_UNIFORM_BLOCKS = 37307;
   public static final int GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS = 37308;
   public static final int GL_MAX_COMPUTE_IMAGE_UNIFORMS = 37309;
   public static final int GL_MAX_COMPUTE_SHARED_MEMORY_SIZE = 33378;
   public static final int GL_MAX_COMPUTE_UNIFORM_COMPONENTS = 33379;
   public static final int GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS = 33380;
   public static final int GL_MAX_COMPUTE_ATOMIC_COUNTERS = 33381;
   public static final int GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS = 33382;
   public static final int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 37099;
   public static final int GL_MAX_COMPUTE_WORK_GROUP_COUNT = 37310;
   public static final int GL_MAX_COMPUTE_WORK_GROUP_SIZE = 37311;
   public static final int GL_COMPUTE_WORK_GROUP_SIZE = 33383;
   public static final int GL_UNIFORM_BLOCK_REFERENCED_BY_COMPUTE_SHADER = 37100;
   public static final int GL_ATOMIC_COUNTER_BUFFER_REFERENCED_BY_COMPUTE_SHADER = 37101;
   public static final int GL_DISPATCH_INDIRECT_BUFFER = 37102;
   public static final int GL_DISPATCH_INDIRECT_BUFFER_BINDING = 37103;
   public static final int GL_COMPUTE_SHADER_BIT = 32;
   public static final int GL_DEBUG_OUTPUT = 37600;
   public static final int GL_DEBUG_OUTPUT_SYNCHRONOUS = 33346;
   public static final int GL_CONTEXT_FLAG_DEBUG_BIT = 2;
   public static final int GL_MAX_DEBUG_MESSAGE_LENGTH = 37187;
   public static final int GL_MAX_DEBUG_LOGGED_MESSAGES = 37188;
   public static final int GL_DEBUG_LOGGED_MESSAGES = 37189;
   public static final int GL_DEBUG_NEXT_LOGGED_MESSAGE_LENGTH = 33347;
   public static final int GL_MAX_DEBUG_GROUP_STACK_DEPTH = 33388;
   public static final int GL_DEBUG_GROUP_STACK_DEPTH = 33389;
   public static final int GL_MAX_LABEL_LENGTH = 33512;
   public static final int GL_DEBUG_CALLBACK_FUNCTION = 33348;
   public static final int GL_DEBUG_CALLBACK_USER_PARAM = 33349;
   public static final int GL_DEBUG_SOURCE_API = 33350;
   public static final int GL_DEBUG_SOURCE_WINDOW_SYSTEM = 33351;
   public static final int GL_DEBUG_SOURCE_SHADER_COMPILER = 33352;
   public static final int GL_DEBUG_SOURCE_THIRD_PARTY = 33353;
   public static final int GL_DEBUG_SOURCE_APPLICATION = 33354;
   public static final int GL_DEBUG_SOURCE_OTHER = 33355;
   public static final int GL_DEBUG_TYPE_ERROR = 33356;
   public static final int GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR = 33357;
   public static final int GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR = 33358;
   public static final int GL_DEBUG_TYPE_PORTABILITY = 33359;
   public static final int GL_DEBUG_TYPE_PERFORMANCE = 33360;
   public static final int GL_DEBUG_TYPE_OTHER = 33361;
   public static final int GL_DEBUG_TYPE_MARKER = 33384;
   public static final int GL_DEBUG_TYPE_PUSH_GROUP = 33385;
   public static final int GL_DEBUG_TYPE_POP_GROUP = 33386;
   public static final int GL_DEBUG_SEVERITY_HIGH = 37190;
   public static final int GL_DEBUG_SEVERITY_MEDIUM = 37191;
   public static final int GL_DEBUG_SEVERITY_LOW = 37192;
   public static final int GL_DEBUG_SEVERITY_NOTIFICATION = 33387;
   public static final int GL_BUFFER = 33504;
   public static final int GL_SHADER = 33505;
   public static final int GL_PROGRAM = 33506;
   public static final int GL_QUERY = 33507;
   public static final int GL_PROGRAM_PIPELINE = 33508;
   public static final int GL_SAMPLER = 33510;
   public static final int GL_DISPLAY_LIST = 33511;
   public static final int GL_MAX_UNIFORM_LOCATIONS = 33390;
   public static final int GL_FRAMEBUFFER_DEFAULT_WIDTH = 37648;
   public static final int GL_FRAMEBUFFER_DEFAULT_HEIGHT = 37649;
   public static final int GL_FRAMEBUFFER_DEFAULT_LAYERS = 37650;
   public static final int GL_FRAMEBUFFER_DEFAULT_SAMPLES = 37651;
   public static final int GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS = 37652;
   public static final int GL_MAX_FRAMEBUFFER_WIDTH = 37653;
   public static final int GL_MAX_FRAMEBUFFER_HEIGHT = 37654;
   public static final int GL_MAX_FRAMEBUFFER_LAYERS = 37655;
   public static final int GL_MAX_FRAMEBUFFER_SAMPLES = 37656;
   public static final int GL_INTERNALFORMAT_SUPPORTED = 33391;
   public static final int GL_INTERNALFORMAT_PREFERRED = 33392;
   public static final int GL_INTERNALFORMAT_RED_SIZE = 33393;
   public static final int GL_INTERNALFORMAT_GREEN_SIZE = 33394;
   public static final int GL_INTERNALFORMAT_BLUE_SIZE = 33395;
   public static final int GL_INTERNALFORMAT_ALPHA_SIZE = 33396;
   public static final int GL_INTERNALFORMAT_DEPTH_SIZE = 33397;
   public static final int GL_INTERNALFORMAT_STENCIL_SIZE = 33398;
   public static final int GL_INTERNALFORMAT_SHARED_SIZE = 33399;
   public static final int GL_INTERNALFORMAT_RED_TYPE = 33400;
   public static final int GL_INTERNALFORMAT_GREEN_TYPE = 33401;
   public static final int GL_INTERNALFORMAT_BLUE_TYPE = 33402;
   public static final int GL_INTERNALFORMAT_ALPHA_TYPE = 33403;
   public static final int GL_INTERNALFORMAT_DEPTH_TYPE = 33404;
   public static final int GL_INTERNALFORMAT_STENCIL_TYPE = 33405;
   public static final int GL_MAX_WIDTH = 33406;
   public static final int GL_MAX_HEIGHT = 33407;
   public static final int GL_MAX_DEPTH = 33408;
   public static final int GL_MAX_LAYERS = 33409;
   public static final int GL_MAX_COMBINED_DIMENSIONS = 33410;
   public static final int GL_COLOR_COMPONENTS = 33411;
   public static final int GL_DEPTH_COMPONENTS = 33412;
   public static final int GL_STENCIL_COMPONENTS = 33413;
   public static final int GL_COLOR_RENDERABLE = 33414;
   public static final int GL_DEPTH_RENDERABLE = 33415;
   public static final int GL_STENCIL_RENDERABLE = 33416;
   public static final int GL_FRAMEBUFFER_RENDERABLE = 33417;
   public static final int GL_FRAMEBUFFER_RENDERABLE_LAYERED = 33418;
   public static final int GL_FRAMEBUFFER_BLEND = 33419;
   public static final int GL_READ_PIXELS = 33420;
   public static final int GL_READ_PIXELS_FORMAT = 33421;
   public static final int GL_READ_PIXELS_TYPE = 33422;
   public static final int GL_TEXTURE_IMAGE_FORMAT = 33423;
   public static final int GL_TEXTURE_IMAGE_TYPE = 33424;
   public static final int GL_GET_TEXTURE_IMAGE_FORMAT = 33425;
   public static final int GL_GET_TEXTURE_IMAGE_TYPE = 33426;
   public static final int GL_MIPMAP = 33427;
   public static final int GL_MANUAL_GENERATE_MIPMAP = 33428;
   public static final int GL_AUTO_GENERATE_MIPMAP = 33429;
   public static final int GL_COLOR_ENCODING = 33430;
   public static final int GL_SRGB_READ = 33431;
   public static final int GL_SRGB_WRITE = 33432;
   public static final int GL_SRGB_DECODE_ARB = 33433;
   public static final int GL_FILTER = 33434;
   public static final int GL_VERTEX_TEXTURE = 33435;
   public static final int GL_TESS_CONTROL_TEXTURE = 33436;
   public static final int GL_TESS_EVALUATION_TEXTURE = 33437;
   public static final int GL_GEOMETRY_TEXTURE = 33438;
   public static final int GL_FRAGMENT_TEXTURE = 33439;
   public static final int GL_COMPUTE_TEXTURE = 33440;
   public static final int GL_TEXTURE_SHADOW = 33441;
   public static final int GL_TEXTURE_GATHER = 33442;
   public static final int GL_TEXTURE_GATHER_SHADOW = 33443;
   public static final int GL_SHADER_IMAGE_LOAD = 33444;
   public static final int GL_SHADER_IMAGE_STORE = 33445;
   public static final int GL_SHADER_IMAGE_ATOMIC = 33446;
   public static final int GL_IMAGE_TEXEL_SIZE = 33447;
   public static final int GL_IMAGE_COMPATIBILITY_CLASS = 33448;
   public static final int GL_IMAGE_PIXEL_FORMAT = 33449;
   public static final int GL_IMAGE_PIXEL_TYPE = 33450;
   public static final int GL_SIMULTANEOUS_TEXTURE_AND_DEPTH_TEST = 33452;
   public static final int GL_SIMULTANEOUS_TEXTURE_AND_STENCIL_TEST = 33453;
   public static final int GL_SIMULTANEOUS_TEXTURE_AND_DEPTH_WRITE = 33454;
   public static final int GL_SIMULTANEOUS_TEXTURE_AND_STENCIL_WRITE = 33455;
   public static final int GL_TEXTURE_COMPRESSED_BLOCK_WIDTH = 33457;
   public static final int GL_TEXTURE_COMPRESSED_BLOCK_HEIGHT = 33458;
   public static final int GL_TEXTURE_COMPRESSED_BLOCK_SIZE = 33459;
   public static final int GL_CLEAR_BUFFER = 33460;
   public static final int GL_TEXTURE_VIEW = 33461;
   public static final int GL_VIEW_COMPATIBILITY_CLASS = 33462;
   public static final int GL_FULL_SUPPORT = 33463;
   public static final int GL_CAVEAT_SUPPORT = 33464;
   public static final int GL_IMAGE_CLASS_4_X_32 = 33465;
   public static final int GL_IMAGE_CLASS_2_X_32 = 33466;
   public static final int GL_IMAGE_CLASS_1_X_32 = 33467;
   public static final int GL_IMAGE_CLASS_4_X_16 = 33468;
   public static final int GL_IMAGE_CLASS_2_X_16 = 33469;
   public static final int GL_IMAGE_CLASS_1_X_16 = 33470;
   public static final int GL_IMAGE_CLASS_4_X_8 = 33471;
   public static final int GL_IMAGE_CLASS_2_X_8 = 33472;
   public static final int GL_IMAGE_CLASS_1_X_8 = 33473;
   public static final int GL_IMAGE_CLASS_11_11_10 = 33474;
   public static final int GL_IMAGE_CLASS_10_10_10_2 = 33475;
   public static final int GL_VIEW_CLASS_128_BITS = 33476;
   public static final int GL_VIEW_CLASS_96_BITS = 33477;
   public static final int GL_VIEW_CLASS_64_BITS = 33478;
   public static final int GL_VIEW_CLASS_48_BITS = 33479;
   public static final int GL_VIEW_CLASS_32_BITS = 33480;
   public static final int GL_VIEW_CLASS_24_BITS = 33481;
   public static final int GL_VIEW_CLASS_16_BITS = 33482;
   public static final int GL_VIEW_CLASS_8_BITS = 33483;
   public static final int GL_VIEW_CLASS_S3TC_DXT1_RGB = 33484;
   public static final int GL_VIEW_CLASS_S3TC_DXT1_RGBA = 33485;
   public static final int GL_VIEW_CLASS_S3TC_DXT3_RGBA = 33486;
   public static final int GL_VIEW_CLASS_S3TC_DXT5_RGBA = 33487;
   public static final int GL_VIEW_CLASS_RGTC1_RED = 33488;
   public static final int GL_VIEW_CLASS_RGTC2_RG = 33489;
   public static final int GL_VIEW_CLASS_BPTC_UNORM = 33490;
   public static final int GL_VIEW_CLASS_BPTC_FLOAT = 33491;
   public static final int GL_UNIFORM = 37601;
   public static final int GL_UNIFORM_BLOCK = 37602;
   public static final int GL_PROGRAM_INPUT = 37603;
   public static final int GL_PROGRAM_OUTPUT = 37604;
   public static final int GL_BUFFER_VARIABLE = 37605;
   public static final int GL_SHADER_STORAGE_BLOCK = 37606;
   public static final int GL_VERTEX_SUBROUTINE = 37608;
   public static final int GL_TESS_CONTROL_SUBROUTINE = 37609;
   public static final int GL_TESS_EVALUATION_SUBROUTINE = 37610;
   public static final int GL_GEOMETRY_SUBROUTINE = 37611;
   public static final int GL_FRAGMENT_SUBROUTINE = 37612;
   public static final int GL_COMPUTE_SUBROUTINE = 37613;
   public static final int GL_VERTEX_SUBROUTINE_UNIFORM = 37614;
   public static final int GL_TESS_CONTROL_SUBROUTINE_UNIFORM = 37615;
   public static final int GL_TESS_EVALUATION_SUBROUTINE_UNIFORM = 37616;
   public static final int GL_GEOMETRY_SUBROUTINE_UNIFORM = 37617;
   public static final int GL_FRAGMENT_SUBROUTINE_UNIFORM = 37618;
   public static final int GL_COMPUTE_SUBROUTINE_UNIFORM = 37619;
   public static final int GL_TRANSFORM_FEEDBACK_VARYING = 37620;
   public static final int GL_ACTIVE_RESOURCES = 37621;
   public static final int GL_MAX_NAME_LENGTH = 37622;
   public static final int GL_MAX_NUM_ACTIVE_VARIABLES = 37623;
   public static final int GL_MAX_NUM_COMPATIBLE_SUBROUTINES = 37624;
   public static final int GL_NAME_LENGTH = 37625;
   public static final int GL_TYPE = 37626;
   public static final int GL_ARRAY_SIZE = 37627;
   public static final int GL_OFFSET = 37628;
   public static final int GL_BLOCK_INDEX = 37629;
   public static final int GL_ARRAY_STRIDE = 37630;
   public static final int GL_MATRIX_STRIDE = 37631;
   public static final int GL_IS_ROW_MAJOR = 37632;
   public static final int GL_ATOMIC_COUNTER_BUFFER_INDEX = 37633;
   public static final int GL_BUFFER_BINDING = 37634;
   public static final int GL_BUFFER_DATA_SIZE = 37635;
   public static final int GL_NUM_ACTIVE_VARIABLES = 37636;
   public static final int GL_ACTIVE_VARIABLES = 37637;
   public static final int GL_REFERENCED_BY_VERTEX_SHADER = 37638;
   public static final int GL_REFERENCED_BY_TESS_CONTROL_SHADER = 37639;
   public static final int GL_REFERENCED_BY_TESS_EVALUATION_SHADER = 37640;
   public static final int GL_REFERENCED_BY_GEOMETRY_SHADER = 37641;
   public static final int GL_REFERENCED_BY_FRAGMENT_SHADER = 37642;
   public static final int GL_REFERENCED_BY_COMPUTE_SHADER = 37643;
   public static final int GL_TOP_LEVEL_ARRAY_SIZE = 37644;
   public static final int GL_TOP_LEVEL_ARRAY_STRIDE = 37645;
   public static final int GL_LOCATION = 37646;
   public static final int GL_LOCATION_INDEX = 37647;
   public static final int GL_IS_PER_PATCH = 37607;
   public static final int GL_SHADER_STORAGE_BUFFER = 37074;
   public static final int GL_SHADER_STORAGE_BUFFER_BINDING = 37075;
   public static final int GL_SHADER_STORAGE_BUFFER_START = 37076;
   public static final int GL_SHADER_STORAGE_BUFFER_SIZE = 37077;
   public static final int GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS = 37078;
   public static final int GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS = 37079;
   public static final int GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS = 37080;
   public static final int GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS = 37081;
   public static final int GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS = 37082;
   public static final int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS = 37083;
   public static final int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS = 37084;
   public static final int GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS = 37085;
   public static final int GL_MAX_SHADER_STORAGE_BLOCK_SIZE = 37086;
   public static final int GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT = 37087;
   public static final int GL_SHADER_STORAGE_BARRIER_BIT = 8192;
   public static final int GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES = 36665;
   public static final int GL_DEPTH_STENCIL_TEXTURE_MODE = 37098;
   public static final int GL_TEXTURE_BUFFER_OFFSET = 37277;
   public static final int GL_TEXTURE_BUFFER_SIZE = 37278;
   public static final int GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT = 37279;
   public static final int GL_TEXTURE_VIEW_MIN_LEVEL = 33499;
   public static final int GL_TEXTURE_VIEW_NUM_LEVELS = 33500;
   public static final int GL_TEXTURE_VIEW_MIN_LAYER = 33501;
   public static final int GL_TEXTURE_VIEW_NUM_LAYERS = 33502;
   public static final int GL_TEXTURE_IMMUTABLE_LEVELS = 33503;
   public static final int GL_VERTEX_ATTRIB_BINDING = 33492;
   public static final int GL_VERTEX_ATTRIB_RELATIVE_OFFSET = 33493;
   public static final int GL_VERTEX_BINDING_DIVISOR = 33494;
   public static final int GL_VERTEX_BINDING_OFFSET = 33495;
   public static final int GL_VERTEX_BINDING_STRIDE = 33496;
   public static final int GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET = 33497;
   public static final int GL_MAX_VERTEX_ATTRIB_BINDINGS = 33498;

   public static void glClearBufferData(int target, int internalformat, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)data, 1);
      nglClearBufferData(target, internalformat, format, type, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglClearBufferData(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glClearBufferSubData(int target, int internalformat, long offset, long size, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)data, 1);
      nglClearBufferSubData(target, internalformat, offset, size, format, type, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglClearBufferSubData(int var0, int var1, long var2, long var4, int var6, int var7, long var8, long var10);

   public static void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDispatchCompute;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDispatchCompute(num_groups_x, num_groups_y, num_groups_z, function_pointer);
   }

   static native void nglDispatchCompute(int var0, int var1, int var2, long var3);

   public static void glDispatchComputeIndirect(long indirect) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDispatchComputeIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDispatchComputeIndirect(indirect, function_pointer);
   }

   static native void nglDispatchComputeIndirect(long var0, long var2);

   public static void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyImageSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth, function_pointer);
   }

   static native void nglCopyImageSubData(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, long var15);

   public static void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageControl;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(ids != null) {
         BufferChecks.checkDirect(ids);
      }

      nglDebugMessageControl(source, type, severity, ids == null?0:ids.remaining(), MemoryUtil.getAddressSafe(ids), enabled, function_pointer);
   }

   static native void nglDebugMessageControl(int var0, int var1, int var2, int var3, long var4, boolean var6, long var7);

   public static void glDebugMessageInsert(int source, int type, int id, int severity, ByteBuffer buf) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageInsert;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buf);
      nglDebugMessageInsert(source, type, id, severity, buf.remaining(), MemoryUtil.getAddress(buf), function_pointer);
   }

   static native void nglDebugMessageInsert(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glDebugMessageInsert(int source, int type, int id, int severity, CharSequence buf) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageInsert;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDebugMessageInsert(source, type, id, severity, buf.length(), APIUtil.getBuffer(caps, buf), function_pointer);
   }

   public static void glDebugMessageCallback(KHRDebugCallback callback) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageCallback;
      BufferChecks.checkFunctionAddress(function_pointer);
      long userParam = callback == null?0L:CallbackUtil.createGlobalRef(callback.getHandler());
      CallbackUtil.registerContextCallbackKHR(userParam);
      nglDebugMessageCallback(callback == null?0L:callback.getPointer(), userParam, function_pointer);
   }

   static native void nglDebugMessageCallback(long var0, long var2, long var4);

   public static int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDebugMessageLog;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(sources != null) {
         BufferChecks.checkBuffer(sources, count);
      }

      if(types != null) {
         BufferChecks.checkBuffer(types, count);
      }

      if(ids != null) {
         BufferChecks.checkBuffer(ids, count);
      }

      if(severities != null) {
         BufferChecks.checkBuffer(severities, count);
      }

      if(lengths != null) {
         BufferChecks.checkBuffer(lengths, count);
      }

      if(messageLog != null) {
         BufferChecks.checkDirect(messageLog);
      }

      int __result = nglGetDebugMessageLog(count, messageLog == null?0:messageLog.remaining(), MemoryUtil.getAddressSafe(sources), MemoryUtil.getAddressSafe(types), MemoryUtil.getAddressSafe(ids), MemoryUtil.getAddressSafe(severities), MemoryUtil.getAddressSafe(lengths), MemoryUtil.getAddressSafe(messageLog), function_pointer);
      return __result;
   }

   static native int nglGetDebugMessageLog(int var0, int var1, long var2, long var4, long var6, long var8, long var10, long var12, long var14);

   public static void glPushDebugGroup(int source, int id, ByteBuffer message) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushDebugGroup;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(message);
      nglPushDebugGroup(source, id, message.remaining(), MemoryUtil.getAddress(message), function_pointer);
   }

   static native void nglPushDebugGroup(int var0, int var1, int var2, long var3, long var5);

   public static void glPushDebugGroup(int source, int id, CharSequence message) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushDebugGroup;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPushDebugGroup(source, id, message.length(), APIUtil.getBuffer(caps, message), function_pointer);
   }

   public static void glPopDebugGroup() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPopDebugGroup;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPopDebugGroup(function_pointer);
   }

   static native void nglPopDebugGroup(long var0);

   public static void glObjectLabel(int identifier, int name, ByteBuffer label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glObjectLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(label != null) {
         BufferChecks.checkDirect(label);
      }

      nglObjectLabel(identifier, name, label == null?0:label.remaining(), MemoryUtil.getAddressSafe(label), function_pointer);
   }

   static native void nglObjectLabel(int var0, int var1, int var2, long var3, long var5);

   public static void glObjectLabel(int identifier, int name, CharSequence label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glObjectLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglObjectLabel(identifier, name, label.length(), APIUtil.getBuffer(caps, label), function_pointer);
   }

   public static void glGetObjectLabel(int identifier, int name, IntBuffer length, ByteBuffer label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(label);
      nglGetObjectLabel(identifier, name, label.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(label), function_pointer);
   }

   static native void nglGetObjectLabel(int var0, int var1, int var2, long var3, long var5, long var7);

   public static String glGetObjectLabel(int identifier, int name, int bufSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer label_length = APIUtil.getLengths(caps);
      ByteBuffer label = APIUtil.getBufferByte(caps, bufSize);
      nglGetObjectLabel(identifier, name, bufSize, MemoryUtil.getAddress0((Buffer)label_length), MemoryUtil.getAddress(label), function_pointer);
      label.limit(label_length.get(0));
      return APIUtil.getString(caps, label);
   }

   public static void glObjectPtrLabel(PointerWrapper ptr, ByteBuffer label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glObjectPtrLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(label != null) {
         BufferChecks.checkDirect(label);
      }

      nglObjectPtrLabel(ptr.getPointer(), label == null?0:label.remaining(), MemoryUtil.getAddressSafe(label), function_pointer);
   }

   static native void nglObjectPtrLabel(long var0, int var2, long var3, long var5);

   public static void glObjectPtrLabel(PointerWrapper ptr, CharSequence label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glObjectPtrLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglObjectPtrLabel(ptr.getPointer(), label.length(), APIUtil.getBuffer(caps, label), function_pointer);
   }

   public static void glGetObjectPtrLabel(PointerWrapper ptr, IntBuffer length, ByteBuffer label) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectPtrLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(label);
      nglGetObjectPtrLabel(ptr.getPointer(), label.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(label), function_pointer);
   }

   static native void nglGetObjectPtrLabel(long var0, int var2, long var3, long var5, long var7);

   public static String glGetObjectPtrLabel(PointerWrapper ptr, int bufSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectPtrLabel;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer label_length = APIUtil.getLengths(caps);
      ByteBuffer label = APIUtil.getBufferByte(caps, bufSize);
      nglGetObjectPtrLabel(ptr.getPointer(), bufSize, MemoryUtil.getAddress0((Buffer)label_length), MemoryUtil.getAddress(label), function_pointer);
      label.limit(label_length.get(0));
      return APIUtil.getString(caps, label);
   }

   public static void glFramebufferParameteri(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferParameteri;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferParameteri(target, pname, param, function_pointer);
   }

   static native void nglFramebufferParameteri(int var0, int var1, int var2, long var3);

   public static void glGetFramebufferParameter(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetFramebufferParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetFramebufferParameteriv(int var0, int var1, long var2, long var4);

   public static int glGetFramebufferParameteri(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetFramebufferParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetInternalformat(int target, int internalformat, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInternalformati64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglGetInternalformati64v(target, internalformat, pname, params.remaining(), MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetInternalformati64v(int var0, int var1, int var2, int var3, long var4, long var6);

   public static long glGetInternalformati64(int target, int internalformat, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInternalformati64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetInternalformati64v(target, internalformat, pname, 1, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glInvalidateTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInvalidateTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, function_pointer);
   }

   static native void nglInvalidateTexSubImage(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glInvalidateTexImage(int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInvalidateTexImage(texture, level, function_pointer);
   }

   static native void nglInvalidateTexImage(int var0, int var1, long var2);

   public static void glInvalidateBufferSubData(int buffer, long offset, long length) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInvalidateBufferSubData(buffer, offset, length, function_pointer);
   }

   static native void nglInvalidateBufferSubData(int var0, long var1, long var3, long var5);

   public static void glInvalidateBufferData(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInvalidateBufferData(buffer, function_pointer);
   }

   static native void nglInvalidateBufferData(int var0, long var1);

   public static void glInvalidateFramebuffer(int target, IntBuffer attachments) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateFramebuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(attachments);
      nglInvalidateFramebuffer(target, attachments.remaining(), MemoryUtil.getAddress(attachments), function_pointer);
   }

   static native void nglInvalidateFramebuffer(int var0, int var1, long var2, long var4);

   public static void glInvalidateSubFramebuffer(int target, IntBuffer attachments, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInvalidateSubFramebuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(attachments);
      nglInvalidateSubFramebuffer(target, attachments.remaining(), MemoryUtil.getAddress(attachments), x, y, width, height, function_pointer);
   }

   static native void nglInvalidateSubFramebuffer(int var0, int var1, long var2, int var4, int var5, int var6, int var7, long var8);

   public static void glMultiDrawArraysIndirect(int mode, ByteBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?16:stride) * primcount);
      nglMultiDrawArraysIndirect(mode, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirect(int var0, long var1, int var3, int var4, long var5);

   public static void glMultiDrawArraysIndirect(int mode, long indirect_buffer_offset, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawArraysIndirectBO(mode, indirect_buffer_offset, primcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirectBO(int var0, long var1, int var3, int var4, long var5);

   public static void glMultiDrawArraysIndirect(int mode, IntBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?4:stride >> 2) * primcount);
      nglMultiDrawArraysIndirect(mode, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   public static void glMultiDrawElementsIndirect(int mode, int type, ByteBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?20:stride) * primcount);
      nglMultiDrawElementsIndirect(mode, type, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirect(int var0, int var1, long var2, int var4, int var5, long var6);

   public static void glMultiDrawElementsIndirect(int mode, int type, long indirect_buffer_offset, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawElementsIndirectBO(mode, type, indirect_buffer_offset, primcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirectBO(int var0, int var1, long var2, int var4, int var5, long var6);

   public static void glMultiDrawElementsIndirect(int mode, int type, IntBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirect;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?5:stride >> 2) * primcount);
      nglMultiDrawElementsIndirect(mode, type, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   public static void glGetProgramInterface(int program, int programInterface, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramInterfaceiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetProgramInterfaceiv(program, programInterface, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramInterfaceiv(int var0, int var1, int var2, long var3, long var5);

   public static int glGetProgramInterfacei(int program, int programInterface, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramInterfaceiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetProgramInterfaceiv(program, programInterface, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static int glGetProgramResourceIndex(int program, int programInterface, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceIndex;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetProgramResourceIndex(program, programInterface, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetProgramResourceIndex(int var0, int var1, long var2, long var4);

   public static int glGetProgramResourceIndex(int program, int programInterface, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceIndex;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetProgramResourceIndex(program, programInterface, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static void glGetProgramResourceName(int program, int programInterface, int index, IntBuffer length, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceName;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      if(name != null) {
         BufferChecks.checkDirect(name);
      }

      nglGetProgramResourceName(program, programInterface, index, name == null?0:name.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddressSafe(name), function_pointer);
   }

   static native void nglGetProgramResourceName(int var0, int var1, int var2, int var3, long var4, long var6, long var8);

   public static String glGetProgramResourceName(int program, int programInterface, int index, int bufSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceName;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, bufSize);
      nglGetProgramResourceName(program, programInterface, index, bufSize, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static void glGetProgramResource(int program, int programInterface, int index, IntBuffer props, IntBuffer length, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(props);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(params);
      nglGetProgramResourceiv(program, programInterface, index, props.remaining(), MemoryUtil.getAddress(props), params.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramResourceiv(int var0, int var1, int var2, int var3, long var4, int var6, long var7, long var9, long var11);

   public static int glGetProgramResourceLocation(int program, int programInterface, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceLocation;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetProgramResourceLocation(program, programInterface, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetProgramResourceLocation(int var0, int var1, long var2, long var4);

   public static int glGetProgramResourceLocation(int program, int programInterface, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceLocation;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetProgramResourceLocation(program, programInterface, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static int glGetProgramResourceLocationIndex(int program, int programInterface, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceLocationIndex;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetProgramResourceLocationIndex(program, programInterface, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetProgramResourceLocationIndex(int var0, int var1, long var2, long var4);

   public static int glGetProgramResourceLocationIndex(int program, int programInterface, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramResourceLocationIndex;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetProgramResourceLocationIndex(program, programInterface, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static void glShaderStorageBlockBinding(int program, int storageBlockIndex, int storageBlockBinding) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderStorageBlockBinding;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShaderStorageBlockBinding(program, storageBlockIndex, storageBlockBinding, function_pointer);
   }

   static native void nglShaderStorageBlockBinding(int var0, int var1, int var2, long var3);

   public static void glTexBufferRange(int target, int internalformat, int buffer, long offset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexBufferRange;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexBufferRange(target, internalformat, buffer, offset, size, function_pointer);
   }

   static native void nglTexBufferRange(int var0, int var1, int var2, long var3, long var5, long var7);

   public static void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexStorage2DMultisample;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations, function_pointer);
   }

   static native void nglTexStorage2DMultisample(int var0, int var1, int var2, int var3, int var4, boolean var5, long var6);

   public static void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexStorage3DMultisample;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations, function_pointer);
   }

   static native void nglTexStorage3DMultisample(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, long var7);

   public static void glTextureView(int texture, int target, int origtexture, int internalformat, int minlevel, int numlevels, int minlayer, int numlayers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureView;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureView(texture, target, origtexture, internalformat, minlevel, numlevels, minlayer, numlayers, function_pointer);
   }

   static native void nglTextureView(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindVertexBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindVertexBuffer(bindingindex, buffer, offset, stride, function_pointer);
   }

   static native void nglBindVertexBuffer(int var0, int var1, long var2, int var4, long var5);

   public static void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribFormat;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribFormat(attribindex, size, type, normalized, relativeoffset, function_pointer);
   }

   static native void nglVertexAttribFormat(int var0, int var1, int var2, boolean var3, int var4, long var5);

   public static void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribIFormat;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribIFormat(attribindex, size, type, relativeoffset, function_pointer);
   }

   static native void nglVertexAttribIFormat(int var0, int var1, int var2, int var3, long var4);

   public static void glVertexAttribLFormat(int attribindex, int size, int type, int relativeoffset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribLFormat;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribLFormat(attribindex, size, type, relativeoffset, function_pointer);
   }

   static native void nglVertexAttribLFormat(int var0, int var1, int var2, int var3, long var4);

   public static void glVertexAttribBinding(int attribindex, int bindingindex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribBinding;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribBinding(attribindex, bindingindex, function_pointer);
   }

   static native void nglVertexAttribBinding(int var0, int var1, long var2);

   public static void glVertexBindingDivisor(int bindingindex, int divisor) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexBindingDivisor;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexBindingDivisor(bindingindex, divisor, function_pointer);
   }

   static native void nglVertexBindingDivisor(int var0, int var1, long var2);
}
