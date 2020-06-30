package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class GL11 {
   public static final int GL_ACCUM = 256;
   public static final int GL_LOAD = 257;
   public static final int GL_RETURN = 258;
   public static final int GL_MULT = 259;
   public static final int GL_ADD = 260;
   public static final int GL_NEVER = 512;
   public static final int GL_LESS = 513;
   public static final int GL_EQUAL = 514;
   public static final int GL_LEQUAL = 515;
   public static final int GL_GREATER = 516;
   public static final int GL_NOTEQUAL = 517;
   public static final int GL_GEQUAL = 518;
   public static final int GL_ALWAYS = 519;
   public static final int GL_CURRENT_BIT = 1;
   public static final int GL_POINT_BIT = 2;
   public static final int GL_LINE_BIT = 4;
   public static final int GL_POLYGON_BIT = 8;
   public static final int GL_POLYGON_STIPPLE_BIT = 16;
   public static final int GL_PIXEL_MODE_BIT = 32;
   public static final int GL_LIGHTING_BIT = 64;
   public static final int GL_FOG_BIT = 128;
   public static final int GL_DEPTH_BUFFER_BIT = 256;
   public static final int GL_ACCUM_BUFFER_BIT = 512;
   public static final int GL_STENCIL_BUFFER_BIT = 1024;
   public static final int GL_VIEWPORT_BIT = 2048;
   public static final int GL_TRANSFORM_BIT = 4096;
   public static final int GL_ENABLE_BIT = 8192;
   public static final int GL_COLOR_BUFFER_BIT = 16384;
   public static final int GL_HINT_BIT = 32768;
   public static final int GL_EVAL_BIT = 65536;
   public static final int GL_LIST_BIT = 131072;
   public static final int GL_TEXTURE_BIT = 262144;
   public static final int GL_SCISSOR_BIT = 524288;
   public static final int GL_ALL_ATTRIB_BITS = 1048575;
   public static final int GL_POINTS = 0;
   public static final int GL_LINES = 1;
   public static final int GL_LINE_LOOP = 2;
   public static final int GL_LINE_STRIP = 3;
   public static final int GL_TRIANGLES = 4;
   public static final int GL_TRIANGLE_STRIP = 5;
   public static final int GL_TRIANGLE_FAN = 6;
   public static final int GL_QUADS = 7;
   public static final int GL_QUAD_STRIP = 8;
   public static final int GL_POLYGON = 9;
   public static final int GL_ZERO = 0;
   public static final int GL_ONE = 1;
   public static final int GL_SRC_COLOR = 768;
   public static final int GL_ONE_MINUS_SRC_COLOR = 769;
   public static final int GL_SRC_ALPHA = 770;
   public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
   public static final int GL_DST_ALPHA = 772;
   public static final int GL_ONE_MINUS_DST_ALPHA = 773;
   public static final int GL_DST_COLOR = 774;
   public static final int GL_ONE_MINUS_DST_COLOR = 775;
   public static final int GL_SRC_ALPHA_SATURATE = 776;
   public static final int GL_CONSTANT_COLOR = 32769;
   public static final int GL_ONE_MINUS_CONSTANT_COLOR = 32770;
   public static final int GL_CONSTANT_ALPHA = 32771;
   public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
   public static final int GL_TRUE = 1;
   public static final int GL_FALSE = 0;
   public static final int GL_CLIP_PLANE0 = 12288;
   public static final int GL_CLIP_PLANE1 = 12289;
   public static final int GL_CLIP_PLANE2 = 12290;
   public static final int GL_CLIP_PLANE3 = 12291;
   public static final int GL_CLIP_PLANE4 = 12292;
   public static final int GL_CLIP_PLANE5 = 12293;
   public static final int GL_BYTE = 5120;
   public static final int GL_UNSIGNED_BYTE = 5121;
   public static final int GL_SHORT = 5122;
   public static final int GL_UNSIGNED_SHORT = 5123;
   public static final int GL_INT = 5124;
   public static final int GL_UNSIGNED_INT = 5125;
   public static final int GL_FLOAT = 5126;
   public static final int GL_2_BYTES = 5127;
   public static final int GL_3_BYTES = 5128;
   public static final int GL_4_BYTES = 5129;
   public static final int GL_DOUBLE = 5130;
   public static final int GL_NONE = 0;
   public static final int GL_FRONT_LEFT = 1024;
   public static final int GL_FRONT_RIGHT = 1025;
   public static final int GL_BACK_LEFT = 1026;
   public static final int GL_BACK_RIGHT = 1027;
   public static final int GL_FRONT = 1028;
   public static final int GL_BACK = 1029;
   public static final int GL_LEFT = 1030;
   public static final int GL_RIGHT = 1031;
   public static final int GL_FRONT_AND_BACK = 1032;
   public static final int GL_AUX0 = 1033;
   public static final int GL_AUX1 = 1034;
   public static final int GL_AUX2 = 1035;
   public static final int GL_AUX3 = 1036;
   public static final int GL_NO_ERROR = 0;
   public static final int GL_INVALID_ENUM = 1280;
   public static final int GL_INVALID_VALUE = 1281;
   public static final int GL_INVALID_OPERATION = 1282;
   public static final int GL_STACK_OVERFLOW = 1283;
   public static final int GL_STACK_UNDERFLOW = 1284;
   public static final int GL_OUT_OF_MEMORY = 1285;
   public static final int GL_2D = 1536;
   public static final int GL_3D = 1537;
   public static final int GL_3D_COLOR = 1538;
   public static final int GL_3D_COLOR_TEXTURE = 1539;
   public static final int GL_4D_COLOR_TEXTURE = 1540;
   public static final int GL_PASS_THROUGH_TOKEN = 1792;
   public static final int GL_POINT_TOKEN = 1793;
   public static final int GL_LINE_TOKEN = 1794;
   public static final int GL_POLYGON_TOKEN = 1795;
   public static final int GL_BITMAP_TOKEN = 1796;
   public static final int GL_DRAW_PIXEL_TOKEN = 1797;
   public static final int GL_COPY_PIXEL_TOKEN = 1798;
   public static final int GL_LINE_RESET_TOKEN = 1799;
   public static final int GL_EXP = 2048;
   public static final int GL_EXP2 = 2049;
   public static final int GL_CW = 2304;
   public static final int GL_CCW = 2305;
   public static final int GL_COEFF = 2560;
   public static final int GL_ORDER = 2561;
   public static final int GL_DOMAIN = 2562;
   public static final int GL_CURRENT_COLOR = 2816;
   public static final int GL_CURRENT_INDEX = 2817;
   public static final int GL_CURRENT_NORMAL = 2818;
   public static final int GL_CURRENT_TEXTURE_COORDS = 2819;
   public static final int GL_CURRENT_RASTER_COLOR = 2820;
   public static final int GL_CURRENT_RASTER_INDEX = 2821;
   public static final int GL_CURRENT_RASTER_TEXTURE_COORDS = 2822;
   public static final int GL_CURRENT_RASTER_POSITION = 2823;
   public static final int GL_CURRENT_RASTER_POSITION_VALID = 2824;
   public static final int GL_CURRENT_RASTER_DISTANCE = 2825;
   public static final int GL_POINT_SMOOTH = 2832;
   public static final int GL_POINT_SIZE = 2833;
   public static final int GL_POINT_SIZE_RANGE = 2834;
   public static final int GL_POINT_SIZE_GRANULARITY = 2835;
   public static final int GL_LINE_SMOOTH = 2848;
   public static final int GL_LINE_WIDTH = 2849;
   public static final int GL_LINE_WIDTH_RANGE = 2850;
   public static final int GL_LINE_WIDTH_GRANULARITY = 2851;
   public static final int GL_LINE_STIPPLE = 2852;
   public static final int GL_LINE_STIPPLE_PATTERN = 2853;
   public static final int GL_LINE_STIPPLE_REPEAT = 2854;
   public static final int GL_LIST_MODE = 2864;
   public static final int GL_MAX_LIST_NESTING = 2865;
   public static final int GL_LIST_BASE = 2866;
   public static final int GL_LIST_INDEX = 2867;
   public static final int GL_POLYGON_MODE = 2880;
   public static final int GL_POLYGON_SMOOTH = 2881;
   public static final int GL_POLYGON_STIPPLE = 2882;
   public static final int GL_EDGE_FLAG = 2883;
   public static final int GL_CULL_FACE = 2884;
   public static final int GL_CULL_FACE_MODE = 2885;
   public static final int GL_FRONT_FACE = 2886;
   public static final int GL_LIGHTING = 2896;
   public static final int GL_LIGHT_MODEL_LOCAL_VIEWER = 2897;
   public static final int GL_LIGHT_MODEL_TWO_SIDE = 2898;
   public static final int GL_LIGHT_MODEL_AMBIENT = 2899;
   public static final int GL_SHADE_MODEL = 2900;
   public static final int GL_COLOR_MATERIAL_FACE = 2901;
   public static final int GL_COLOR_MATERIAL_PARAMETER = 2902;
   public static final int GL_COLOR_MATERIAL = 2903;
   public static final int GL_FOG = 2912;
   public static final int GL_FOG_INDEX = 2913;
   public static final int GL_FOG_DENSITY = 2914;
   public static final int GL_FOG_START = 2915;
   public static final int GL_FOG_END = 2916;
   public static final int GL_FOG_MODE = 2917;
   public static final int GL_FOG_COLOR = 2918;
   public static final int GL_DEPTH_RANGE = 2928;
   public static final int GL_DEPTH_TEST = 2929;
   public static final int GL_DEPTH_WRITEMASK = 2930;
   public static final int GL_DEPTH_CLEAR_VALUE = 2931;
   public static final int GL_DEPTH_FUNC = 2932;
   public static final int GL_ACCUM_CLEAR_VALUE = 2944;
   public static final int GL_STENCIL_TEST = 2960;
   public static final int GL_STENCIL_CLEAR_VALUE = 2961;
   public static final int GL_STENCIL_FUNC = 2962;
   public static final int GL_STENCIL_VALUE_MASK = 2963;
   public static final int GL_STENCIL_FAIL = 2964;
   public static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965;
   public static final int GL_STENCIL_PASS_DEPTH_PASS = 2966;
   public static final int GL_STENCIL_REF = 2967;
   public static final int GL_STENCIL_WRITEMASK = 2968;
   public static final int GL_MATRIX_MODE = 2976;
   public static final int GL_NORMALIZE = 2977;
   public static final int GL_VIEWPORT = 2978;
   public static final int GL_MODELVIEW_STACK_DEPTH = 2979;
   public static final int GL_PROJECTION_STACK_DEPTH = 2980;
   public static final int GL_TEXTURE_STACK_DEPTH = 2981;
   public static final int GL_MODELVIEW_MATRIX = 2982;
   public static final int GL_PROJECTION_MATRIX = 2983;
   public static final int GL_TEXTURE_MATRIX = 2984;
   public static final int GL_ATTRIB_STACK_DEPTH = 2992;
   public static final int GL_CLIENT_ATTRIB_STACK_DEPTH = 2993;
   public static final int GL_ALPHA_TEST = 3008;
   public static final int GL_ALPHA_TEST_FUNC = 3009;
   public static final int GL_ALPHA_TEST_REF = 3010;
   public static final int GL_DITHER = 3024;
   public static final int GL_BLEND_DST = 3040;
   public static final int GL_BLEND_SRC = 3041;
   public static final int GL_BLEND = 3042;
   public static final int GL_LOGIC_OP_MODE = 3056;
   public static final int GL_INDEX_LOGIC_OP = 3057;
   public static final int GL_COLOR_LOGIC_OP = 3058;
   public static final int GL_AUX_BUFFERS = 3072;
   public static final int GL_DRAW_BUFFER = 3073;
   public static final int GL_READ_BUFFER = 3074;
   public static final int GL_SCISSOR_BOX = 3088;
   public static final int GL_SCISSOR_TEST = 3089;
   public static final int GL_INDEX_CLEAR_VALUE = 3104;
   public static final int GL_INDEX_WRITEMASK = 3105;
   public static final int GL_COLOR_CLEAR_VALUE = 3106;
   public static final int GL_COLOR_WRITEMASK = 3107;
   public static final int GL_INDEX_MODE = 3120;
   public static final int GL_RGBA_MODE = 3121;
   public static final int GL_DOUBLEBUFFER = 3122;
   public static final int GL_STEREO = 3123;
   public static final int GL_RENDER_MODE = 3136;
   public static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152;
   public static final int GL_POINT_SMOOTH_HINT = 3153;
   public static final int GL_LINE_SMOOTH_HINT = 3154;
   public static final int GL_POLYGON_SMOOTH_HINT = 3155;
   public static final int GL_FOG_HINT = 3156;
   public static final int GL_TEXTURE_GEN_S = 3168;
   public static final int GL_TEXTURE_GEN_T = 3169;
   public static final int GL_TEXTURE_GEN_R = 3170;
   public static final int GL_TEXTURE_GEN_Q = 3171;
   public static final int GL_PIXEL_MAP_I_TO_I = 3184;
   public static final int GL_PIXEL_MAP_S_TO_S = 3185;
   public static final int GL_PIXEL_MAP_I_TO_R = 3186;
   public static final int GL_PIXEL_MAP_I_TO_G = 3187;
   public static final int GL_PIXEL_MAP_I_TO_B = 3188;
   public static final int GL_PIXEL_MAP_I_TO_A = 3189;
   public static final int GL_PIXEL_MAP_R_TO_R = 3190;
   public static final int GL_PIXEL_MAP_G_TO_G = 3191;
   public static final int GL_PIXEL_MAP_B_TO_B = 3192;
   public static final int GL_PIXEL_MAP_A_TO_A = 3193;
   public static final int GL_PIXEL_MAP_I_TO_I_SIZE = 3248;
   public static final int GL_PIXEL_MAP_S_TO_S_SIZE = 3249;
   public static final int GL_PIXEL_MAP_I_TO_R_SIZE = 3250;
   public static final int GL_PIXEL_MAP_I_TO_G_SIZE = 3251;
   public static final int GL_PIXEL_MAP_I_TO_B_SIZE = 3252;
   public static final int GL_PIXEL_MAP_I_TO_A_SIZE = 3253;
   public static final int GL_PIXEL_MAP_R_TO_R_SIZE = 3254;
   public static final int GL_PIXEL_MAP_G_TO_G_SIZE = 3255;
   public static final int GL_PIXEL_MAP_B_TO_B_SIZE = 3256;
   public static final int GL_PIXEL_MAP_A_TO_A_SIZE = 3257;
   public static final int GL_UNPACK_SWAP_BYTES = 3312;
   public static final int GL_UNPACK_LSB_FIRST = 3313;
   public static final int GL_UNPACK_ROW_LENGTH = 3314;
   public static final int GL_UNPACK_SKIP_ROWS = 3315;
   public static final int GL_UNPACK_SKIP_PIXELS = 3316;
   public static final int GL_UNPACK_ALIGNMENT = 3317;
   public static final int GL_PACK_SWAP_BYTES = 3328;
   public static final int GL_PACK_LSB_FIRST = 3329;
   public static final int GL_PACK_ROW_LENGTH = 3330;
   public static final int GL_PACK_SKIP_ROWS = 3331;
   public static final int GL_PACK_SKIP_PIXELS = 3332;
   public static final int GL_PACK_ALIGNMENT = 3333;
   public static final int GL_MAP_COLOR = 3344;
   public static final int GL_MAP_STENCIL = 3345;
   public static final int GL_INDEX_SHIFT = 3346;
   public static final int GL_INDEX_OFFSET = 3347;
   public static final int GL_RED_SCALE = 3348;
   public static final int GL_RED_BIAS = 3349;
   public static final int GL_ZOOM_X = 3350;
   public static final int GL_ZOOM_Y = 3351;
   public static final int GL_GREEN_SCALE = 3352;
   public static final int GL_GREEN_BIAS = 3353;
   public static final int GL_BLUE_SCALE = 3354;
   public static final int GL_BLUE_BIAS = 3355;
   public static final int GL_ALPHA_SCALE = 3356;
   public static final int GL_ALPHA_BIAS = 3357;
   public static final int GL_DEPTH_SCALE = 3358;
   public static final int GL_DEPTH_BIAS = 3359;
   public static final int GL_MAX_EVAL_ORDER = 3376;
   public static final int GL_MAX_LIGHTS = 3377;
   public static final int GL_MAX_CLIP_PLANES = 3378;
   public static final int GL_MAX_TEXTURE_SIZE = 3379;
   public static final int GL_MAX_PIXEL_MAP_TABLE = 3380;
   public static final int GL_MAX_ATTRIB_STACK_DEPTH = 3381;
   public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 3382;
   public static final int GL_MAX_NAME_STACK_DEPTH = 3383;
   public static final int GL_MAX_PROJECTION_STACK_DEPTH = 3384;
   public static final int GL_MAX_TEXTURE_STACK_DEPTH = 3385;
   public static final int GL_MAX_VIEWPORT_DIMS = 3386;
   public static final int GL_MAX_CLIENT_ATTRIB_STACK_DEPTH = 3387;
   public static final int GL_SUBPIXEL_BITS = 3408;
   public static final int GL_INDEX_BITS = 3409;
   public static final int GL_RED_BITS = 3410;
   public static final int GL_GREEN_BITS = 3411;
   public static final int GL_BLUE_BITS = 3412;
   public static final int GL_ALPHA_BITS = 3413;
   public static final int GL_DEPTH_BITS = 3414;
   public static final int GL_STENCIL_BITS = 3415;
   public static final int GL_ACCUM_RED_BITS = 3416;
   public static final int GL_ACCUM_GREEN_BITS = 3417;
   public static final int GL_ACCUM_BLUE_BITS = 3418;
   public static final int GL_ACCUM_ALPHA_BITS = 3419;
   public static final int GL_NAME_STACK_DEPTH = 3440;
   public static final int GL_AUTO_NORMAL = 3456;
   public static final int GL_MAP1_COLOR_4 = 3472;
   public static final int GL_MAP1_INDEX = 3473;
   public static final int GL_MAP1_NORMAL = 3474;
   public static final int GL_MAP1_TEXTURE_COORD_1 = 3475;
   public static final int GL_MAP1_TEXTURE_COORD_2 = 3476;
   public static final int GL_MAP1_TEXTURE_COORD_3 = 3477;
   public static final int GL_MAP1_TEXTURE_COORD_4 = 3478;
   public static final int GL_MAP1_VERTEX_3 = 3479;
   public static final int GL_MAP1_VERTEX_4 = 3480;
   public static final int GL_MAP2_COLOR_4 = 3504;
   public static final int GL_MAP2_INDEX = 3505;
   public static final int GL_MAP2_NORMAL = 3506;
   public static final int GL_MAP2_TEXTURE_COORD_1 = 3507;
   public static final int GL_MAP2_TEXTURE_COORD_2 = 3508;
   public static final int GL_MAP2_TEXTURE_COORD_3 = 3509;
   public static final int GL_MAP2_TEXTURE_COORD_4 = 3510;
   public static final int GL_MAP2_VERTEX_3 = 3511;
   public static final int GL_MAP2_VERTEX_4 = 3512;
   public static final int GL_MAP1_GRID_DOMAIN = 3536;
   public static final int GL_MAP1_GRID_SEGMENTS = 3537;
   public static final int GL_MAP2_GRID_DOMAIN = 3538;
   public static final int GL_MAP2_GRID_SEGMENTS = 3539;
   public static final int GL_TEXTURE_1D = 3552;
   public static final int GL_TEXTURE_2D = 3553;
   public static final int GL_FEEDBACK_BUFFER_POINTER = 3568;
   public static final int GL_FEEDBACK_BUFFER_SIZE = 3569;
   public static final int GL_FEEDBACK_BUFFER_TYPE = 3570;
   public static final int GL_SELECTION_BUFFER_POINTER = 3571;
   public static final int GL_SELECTION_BUFFER_SIZE = 3572;
   public static final int GL_TEXTURE_WIDTH = 4096;
   public static final int GL_TEXTURE_HEIGHT = 4097;
   public static final int GL_TEXTURE_INTERNAL_FORMAT = 4099;
   public static final int GL_TEXTURE_BORDER_COLOR = 4100;
   public static final int GL_TEXTURE_BORDER = 4101;
   public static final int GL_DONT_CARE = 4352;
   public static final int GL_FASTEST = 4353;
   public static final int GL_NICEST = 4354;
   public static final int GL_LIGHT0 = 16384;
   public static final int GL_LIGHT1 = 16385;
   public static final int GL_LIGHT2 = 16386;
   public static final int GL_LIGHT3 = 16387;
   public static final int GL_LIGHT4 = 16388;
   public static final int GL_LIGHT5 = 16389;
   public static final int GL_LIGHT6 = 16390;
   public static final int GL_LIGHT7 = 16391;
   public static final int GL_AMBIENT = 4608;
   public static final int GL_DIFFUSE = 4609;
   public static final int GL_SPECULAR = 4610;
   public static final int GL_POSITION = 4611;
   public static final int GL_SPOT_DIRECTION = 4612;
   public static final int GL_SPOT_EXPONENT = 4613;
   public static final int GL_SPOT_CUTOFF = 4614;
   public static final int GL_CONSTANT_ATTENUATION = 4615;
   public static final int GL_LINEAR_ATTENUATION = 4616;
   public static final int GL_QUADRATIC_ATTENUATION = 4617;
   public static final int GL_COMPILE = 4864;
   public static final int GL_COMPILE_AND_EXECUTE = 4865;
   public static final int GL_CLEAR = 5376;
   public static final int GL_AND = 5377;
   public static final int GL_AND_REVERSE = 5378;
   public static final int GL_COPY = 5379;
   public static final int GL_AND_INVERTED = 5380;
   public static final int GL_NOOP = 5381;
   public static final int GL_XOR = 5382;
   public static final int GL_OR = 5383;
   public static final int GL_NOR = 5384;
   public static final int GL_EQUIV = 5385;
   public static final int GL_INVERT = 5386;
   public static final int GL_OR_REVERSE = 5387;
   public static final int GL_COPY_INVERTED = 5388;
   public static final int GL_OR_INVERTED = 5389;
   public static final int GL_NAND = 5390;
   public static final int GL_SET = 5391;
   public static final int GL_EMISSION = 5632;
   public static final int GL_SHININESS = 5633;
   public static final int GL_AMBIENT_AND_DIFFUSE = 5634;
   public static final int GL_COLOR_INDEXES = 5635;
   public static final int GL_MODELVIEW = 5888;
   public static final int GL_PROJECTION = 5889;
   public static final int GL_TEXTURE = 5890;
   public static final int GL_COLOR = 6144;
   public static final int GL_DEPTH = 6145;
   public static final int GL_STENCIL = 6146;
   public static final int GL_COLOR_INDEX = 6400;
   public static final int GL_STENCIL_INDEX = 6401;
   public static final int GL_DEPTH_COMPONENT = 6402;
   public static final int GL_RED = 6403;
   public static final int GL_GREEN = 6404;
   public static final int GL_BLUE = 6405;
   public static final int GL_ALPHA = 6406;
   public static final int GL_RGB = 6407;
   public static final int GL_RGBA = 6408;
   public static final int GL_LUMINANCE = 6409;
   public static final int GL_LUMINANCE_ALPHA = 6410;
   public static final int GL_BITMAP = 6656;
   public static final int GL_POINT = 6912;
   public static final int GL_LINE = 6913;
   public static final int GL_FILL = 6914;
   public static final int GL_RENDER = 7168;
   public static final int GL_FEEDBACK = 7169;
   public static final int GL_SELECT = 7170;
   public static final int GL_FLAT = 7424;
   public static final int GL_SMOOTH = 7425;
   public static final int GL_KEEP = 7680;
   public static final int GL_REPLACE = 7681;
   public static final int GL_INCR = 7682;
   public static final int GL_DECR = 7683;
   public static final int GL_VENDOR = 7936;
   public static final int GL_RENDERER = 7937;
   public static final int GL_VERSION = 7938;
   public static final int GL_EXTENSIONS = 7939;
   public static final int GL_S = 8192;
   public static final int GL_T = 8193;
   public static final int GL_R = 8194;
   public static final int GL_Q = 8195;
   public static final int GL_MODULATE = 8448;
   public static final int GL_DECAL = 8449;
   public static final int GL_TEXTURE_ENV_MODE = 8704;
   public static final int GL_TEXTURE_ENV_COLOR = 8705;
   public static final int GL_TEXTURE_ENV = 8960;
   public static final int GL_EYE_LINEAR = 9216;
   public static final int GL_OBJECT_LINEAR = 9217;
   public static final int GL_SPHERE_MAP = 9218;
   public static final int GL_TEXTURE_GEN_MODE = 9472;
   public static final int GL_OBJECT_PLANE = 9473;
   public static final int GL_EYE_PLANE = 9474;
   public static final int GL_NEAREST = 9728;
   public static final int GL_LINEAR = 9729;
   public static final int GL_NEAREST_MIPMAP_NEAREST = 9984;
   public static final int GL_LINEAR_MIPMAP_NEAREST = 9985;
   public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
   public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
   public static final int GL_TEXTURE_MAG_FILTER = 10240;
   public static final int GL_TEXTURE_MIN_FILTER = 10241;
   public static final int GL_TEXTURE_WRAP_S = 10242;
   public static final int GL_TEXTURE_WRAP_T = 10243;
   public static final int GL_CLAMP = 10496;
   public static final int GL_REPEAT = 10497;
   public static final int GL_CLIENT_PIXEL_STORE_BIT = 1;
   public static final int GL_CLIENT_VERTEX_ARRAY_BIT = 2;
   public static final int GL_ALL_CLIENT_ATTRIB_BITS = -1;
   public static final int GL_POLYGON_OFFSET_FACTOR = 32824;
   public static final int GL_POLYGON_OFFSET_UNITS = 10752;
   public static final int GL_POLYGON_OFFSET_POINT = 10753;
   public static final int GL_POLYGON_OFFSET_LINE = 10754;
   public static final int GL_POLYGON_OFFSET_FILL = 32823;
   public static final int GL_ALPHA4 = 32827;
   public static final int GL_ALPHA8 = 32828;
   public static final int GL_ALPHA12 = 32829;
   public static final int GL_ALPHA16 = 32830;
   public static final int GL_LUMINANCE4 = 32831;
   public static final int GL_LUMINANCE8 = 32832;
   public static final int GL_LUMINANCE12 = 32833;
   public static final int GL_LUMINANCE16 = 32834;
   public static final int GL_LUMINANCE4_ALPHA4 = 32835;
   public static final int GL_LUMINANCE6_ALPHA2 = 32836;
   public static final int GL_LUMINANCE8_ALPHA8 = 32837;
   public static final int GL_LUMINANCE12_ALPHA4 = 32838;
   public static final int GL_LUMINANCE12_ALPHA12 = 32839;
   public static final int GL_LUMINANCE16_ALPHA16 = 32840;
   public static final int GL_INTENSITY = 32841;
   public static final int GL_INTENSITY4 = 32842;
   public static final int GL_INTENSITY8 = 32843;
   public static final int GL_INTENSITY12 = 32844;
   public static final int GL_INTENSITY16 = 32845;
   public static final int GL_R3_G3_B2 = 10768;
   public static final int GL_RGB4 = 32847;
   public static final int GL_RGB5 = 32848;
   public static final int GL_RGB8 = 32849;
   public static final int GL_RGB10 = 32850;
   public static final int GL_RGB12 = 32851;
   public static final int GL_RGB16 = 32852;
   public static final int GL_RGBA2 = 32853;
   public static final int GL_RGBA4 = 32854;
   public static final int GL_RGB5_A1 = 32855;
   public static final int GL_RGBA8 = 32856;
   public static final int GL_RGB10_A2 = 32857;
   public static final int GL_RGBA12 = 32858;
   public static final int GL_RGBA16 = 32859;
   public static final int GL_TEXTURE_RED_SIZE = 32860;
   public static final int GL_TEXTURE_GREEN_SIZE = 32861;
   public static final int GL_TEXTURE_BLUE_SIZE = 32862;
   public static final int GL_TEXTURE_ALPHA_SIZE = 32863;
   public static final int GL_TEXTURE_LUMINANCE_SIZE = 32864;
   public static final int GL_TEXTURE_INTENSITY_SIZE = 32865;
   public static final int GL_PROXY_TEXTURE_1D = 32867;
   public static final int GL_PROXY_TEXTURE_2D = 32868;
   public static final int GL_TEXTURE_PRIORITY = 32870;
   public static final int GL_TEXTURE_RESIDENT = 32871;
   public static final int GL_TEXTURE_BINDING_1D = 32872;
   public static final int GL_TEXTURE_BINDING_2D = 32873;
   public static final int GL_VERTEX_ARRAY = 32884;
   public static final int GL_NORMAL_ARRAY = 32885;
   public static final int GL_COLOR_ARRAY = 32886;
   public static final int GL_INDEX_ARRAY = 32887;
   public static final int GL_TEXTURE_COORD_ARRAY = 32888;
   public static final int GL_EDGE_FLAG_ARRAY = 32889;
   public static final int GL_VERTEX_ARRAY_SIZE = 32890;
   public static final int GL_VERTEX_ARRAY_TYPE = 32891;
   public static final int GL_VERTEX_ARRAY_STRIDE = 32892;
   public static final int GL_NORMAL_ARRAY_TYPE = 32894;
   public static final int GL_NORMAL_ARRAY_STRIDE = 32895;
   public static final int GL_COLOR_ARRAY_SIZE = 32897;
   public static final int GL_COLOR_ARRAY_TYPE = 32898;
   public static final int GL_COLOR_ARRAY_STRIDE = 32899;
   public static final int GL_INDEX_ARRAY_TYPE = 32901;
   public static final int GL_INDEX_ARRAY_STRIDE = 32902;
   public static final int GL_TEXTURE_COORD_ARRAY_SIZE = 32904;
   public static final int GL_TEXTURE_COORD_ARRAY_TYPE = 32905;
   public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906;
   public static final int GL_EDGE_FLAG_ARRAY_STRIDE = 32908;
   public static final int GL_VERTEX_ARRAY_POINTER = 32910;
   public static final int GL_NORMAL_ARRAY_POINTER = 32911;
   public static final int GL_COLOR_ARRAY_POINTER = 32912;
   public static final int GL_INDEX_ARRAY_POINTER = 32913;
   public static final int GL_TEXTURE_COORD_ARRAY_POINTER = 32914;
   public static final int GL_EDGE_FLAG_ARRAY_POINTER = 32915;
   public static final int GL_V2F = 10784;
   public static final int GL_V3F = 10785;
   public static final int GL_C4UB_V2F = 10786;
   public static final int GL_C4UB_V3F = 10787;
   public static final int GL_C3F_V3F = 10788;
   public static final int GL_N3F_V3F = 10789;
   public static final int GL_C4F_N3F_V3F = 10790;
   public static final int GL_T2F_V3F = 10791;
   public static final int GL_T4F_V4F = 10792;
   public static final int GL_T2F_C4UB_V3F = 10793;
   public static final int GL_T2F_C3F_V3F = 10794;
   public static final int GL_T2F_N3F_V3F = 10795;
   public static final int GL_T2F_C4F_N3F_V3F = 10796;
   public static final int GL_T4F_C4F_N3F_V4F = 10797;
   public static final int GL_LOGIC_OP = 3057;
   public static final int GL_TEXTURE_COMPONENTS = 4099;

   public static void glAccum(int op, float value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glAccum;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglAccum(op, value, function_pointer);
   }

   static native void nglAccum(int var0, float var1, long var2);

   public static void glAlphaFunc(int func, float ref) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glAlphaFunc;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglAlphaFunc(func, ref, function_pointer);
   }

   static native void nglAlphaFunc(int var0, float var1, long var2);

   public static void glClearColor(float red, float green, float blue, float alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearColor;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearColor(red, green, blue, alpha, function_pointer);
   }

   static native void nglClearColor(float var0, float var1, float var2, float var3, long var4);

   public static void glClearAccum(float red, float green, float blue, float alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearAccum;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearAccum(red, green, blue, alpha, function_pointer);
   }

   static native void nglClearAccum(float var0, float var1, float var2, float var3, long var4);

   public static void glClear(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClear;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClear(mask, function_pointer);
   }

   static native void nglClear(int var0, long var1);

   public static void glCallLists(ByteBuffer lists) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCallLists;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(lists);
      nglCallLists(lists.remaining(), 5121, MemoryUtil.getAddress(lists), function_pointer);
   }

   public static void glCallLists(IntBuffer lists) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCallLists;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(lists);
      nglCallLists(lists.remaining(), 5125, MemoryUtil.getAddress(lists), function_pointer);
   }

   public static void glCallLists(ShortBuffer lists) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCallLists;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(lists);
      nglCallLists(lists.remaining(), 5123, MemoryUtil.getAddress(lists), function_pointer);
   }

   static native void nglCallLists(int var0, int var1, long var2, long var4);

   public static void glCallList(int list) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCallList;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCallList(list, function_pointer);
   }

   static native void nglCallList(int var0, long var1);

   public static void glBlendFunc(int sfactor, int dfactor) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBlendFunc;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBlendFunc(sfactor, dfactor, function_pointer);
   }

   static native void nglBlendFunc(int var0, int var1, long var2);

   public static void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, ByteBuffer bitmap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBitmap;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(bitmap != null) {
         BufferChecks.checkBuffer(bitmap, (width + 7) / 8 * height);
      }

      nglBitmap(width, height, xorig, yorig, xmove, ymove, MemoryUtil.getAddressSafe(bitmap), function_pointer);
   }

   static native void nglBitmap(int var0, int var1, float var2, float var3, float var4, float var5, long var6, long var8);

   public static void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, long bitmap_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBitmap;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglBitmapBO(width, height, xorig, yorig, xmove, ymove, bitmap_buffer_offset, function_pointer);
   }

   static native void nglBitmapBO(int var0, int var1, float var2, float var3, float var4, float var5, long var6, long var8);

   public static void glBindTexture(int target, int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindTexture(target, texture, function_pointer);
   }

   static native void nglBindTexture(int var0, int var1, long var2);

   public static void glPrioritizeTextures(IntBuffer textures, FloatBuffer priorities) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPrioritizeTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(textures);
      BufferChecks.checkBuffer(priorities, textures.remaining());
      nglPrioritizeTextures(textures.remaining(), MemoryUtil.getAddress(textures), MemoryUtil.getAddress(priorities), function_pointer);
   }

   static native void nglPrioritizeTextures(int var0, long var1, long var3, long var5);

   public static boolean glAreTexturesResident(IntBuffer textures, ByteBuffer residences) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glAreTexturesResident;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(textures);
      BufferChecks.checkBuffer(residences, textures.remaining());
      boolean __result = nglAreTexturesResident(textures.remaining(), MemoryUtil.getAddress(textures), MemoryUtil.getAddress(residences), function_pointer);
      return __result;
   }

   static native boolean nglAreTexturesResident(int var0, long var1, long var3, long var5);

   public static void glBegin(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBegin;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBegin(mode, function_pointer);
   }

   static native void nglBegin(int var0, long var1);

   public static void glEnd() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnd;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnd(function_pointer);
   }

   static native void nglEnd(long var0);

   public static void glArrayElement(int i) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glArrayElement;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglArrayElement(i, function_pointer);
   }

   static native void nglArrayElement(int var0, long var1);

   public static void glClearDepth(double depth) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearDepth;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearDepth(depth, function_pointer);
   }

   static native void nglClearDepth(double var0, long var2);

   public static void glDeleteLists(int list, int range) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteLists;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteLists(list, range, function_pointer);
   }

   static native void nglDeleteLists(int var0, int var1, long var2);

   public static void glDeleteTextures(IntBuffer textures) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(textures);
      nglDeleteTextures(textures.remaining(), MemoryUtil.getAddress(textures), function_pointer);
   }

   static native void nglDeleteTextures(int var0, long var1, long var3);

   public static void glDeleteTextures(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteTextures(1, APIUtil.getInt(caps, texture), function_pointer);
   }

   public static void glCullFace(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCullFace;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCullFace(mode, function_pointer);
   }

   static native void nglCullFace(int var0, long var1);

   public static void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glCopyTexSubImage1D(int target, int level, int xoffset, int x, int y, int width) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTexSubImage1D(target, level, xoffset, x, y, width, function_pointer);
   }

   static native void nglCopyTexSubImage1D(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glCopyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTexImage2D(target, level, internalFormat, x, y, width, height, border, function_pointer);
   }

   static native void nglCopyTexImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glCopyTexImage1D(int target, int level, int internalFormat, int x, int y, int width, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTexImage1D(target, level, internalFormat, x, y, width, border, function_pointer);
   }

   static native void nglCopyTexImage1D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7);

   public static void glCopyPixels(int x, int y, int width, int height, int type) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyPixels(x, y, width, height, type, function_pointer);
   }

   static native void nglCopyPixels(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glColorPointer(int size, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glColorPointer_pointer = pointer;
      }

      nglColorPointer(size, 5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glColorPointer(int size, int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glColorPointer_pointer = pointer;
      }

      nglColorPointer(size, 5126, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glColorPointer(int size, boolean unsigned, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glColorPointer_pointer = pointer;
      }

      nglColorPointer(size, unsigned?5121:5120, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglColorPointer(int var0, int var1, int var2, long var3, long var5);

   public static void glColorPointer(int size, int type, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglColorPointerBO(size, type, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglColorPointerBO(int var0, int var1, int var2, long var3, long var5);

   public static void glColorPointer(int size, int type, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glColorPointer_pointer = pointer;
      }

      nglColorPointer(size, type, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glColorMaterial(int face, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorMaterial;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColorMaterial(face, mode, function_pointer);
   }

   static native void nglColorMaterial(int var0, int var1, long var2);

   public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorMask;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColorMask(red, green, blue, alpha, function_pointer);
   }

   static native void nglColorMask(boolean var0, boolean var1, boolean var2, boolean var3, long var4);

   public static void glColor3b(byte red, byte green, byte blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor3b;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor3b(red, green, blue, function_pointer);
   }

   static native void nglColor3b(byte var0, byte var1, byte var2, long var3);

   public static void glColor3f(float red, float green, float blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor3f(red, green, blue, function_pointer);
   }

   static native void nglColor3f(float var0, float var1, float var2, long var3);

   public static void glColor3d(double red, double green, double blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor3d(red, green, blue, function_pointer);
   }

   static native void nglColor3d(double var0, double var2, double var4, long var6);

   public static void glColor3ub(byte red, byte green, byte blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor3ub;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor3ub(red, green, blue, function_pointer);
   }

   static native void nglColor3ub(byte var0, byte var1, byte var2, long var3);

   public static void glColor4b(byte red, byte green, byte blue, byte alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor4b;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor4b(red, green, blue, alpha, function_pointer);
   }

   static native void nglColor4b(byte var0, byte var1, byte var2, byte var3, long var4);

   public static void glColor4f(float red, float green, float blue, float alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor4f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor4f(red, green, blue, alpha, function_pointer);
   }

   static native void nglColor4f(float var0, float var1, float var2, float var3, long var4);

   public static void glColor4d(double red, double green, double blue, double alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor4d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor4d(red, green, blue, alpha, function_pointer);
   }

   static native void nglColor4d(double var0, double var2, double var4, double var6, long var8);

   public static void glColor4ub(byte red, byte green, byte blue, byte alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColor4ub;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColor4ub(red, green, blue, alpha, function_pointer);
   }

   static native void nglColor4ub(byte var0, byte var1, byte var2, byte var3, long var4);

   public static void glClipPlane(int plane, DoubleBuffer equation) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClipPlane;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)equation, 4);
      nglClipPlane(plane, MemoryUtil.getAddress(equation), function_pointer);
   }

   static native void nglClipPlane(int var0, long var1, long var3);

   public static void glClearStencil(int s) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearStencil;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearStencil(s, function_pointer);
   }

   static native void nglClearStencil(int var0, long var1);

   public static void glEvalPoint1(int i) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalPoint1;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalPoint1(i, function_pointer);
   }

   static native void nglEvalPoint1(int var0, long var1);

   public static void glEvalPoint2(int i, int j) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalPoint2;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalPoint2(i, j, function_pointer);
   }

   static native void nglEvalPoint2(int var0, int var1, long var2);

   public static void glEvalMesh1(int mode, int i1, int i2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalMesh1;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalMesh1(mode, i1, i2, function_pointer);
   }

   static native void nglEvalMesh1(int var0, int var1, int var2, long var3);

   public static void glEvalMesh2(int mode, int i1, int i2, int j1, int j2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalMesh2;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalMesh2(mode, i1, i2, j1, j2, function_pointer);
   }

   static native void nglEvalMesh2(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glEvalCoord1f(float u) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalCoord1f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalCoord1f(u, function_pointer);
   }

   static native void nglEvalCoord1f(float var0, long var1);

   public static void glEvalCoord1d(double u) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalCoord1d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalCoord1d(u, function_pointer);
   }

   static native void nglEvalCoord1d(double var0, long var2);

   public static void glEvalCoord2f(float u, float v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalCoord2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalCoord2f(u, v, function_pointer);
   }

   static native void nglEvalCoord2f(float var0, float var1, long var2);

   public static void glEvalCoord2d(double u, double v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalCoord2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalCoord2d(u, v, function_pointer);
   }

   static native void nglEvalCoord2d(double var0, double var2, long var4);

   public static void glEnableClientState(int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableClientState;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableClientState(cap, function_pointer);
   }

   static native void nglEnableClientState(int var0, long var1);

   public static void glDisableClientState(int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableClientState;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableClientState(cap, function_pointer);
   }

   static native void nglDisableClientState(int var0, long var1);

   public static void glEnable(int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnable;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnable(cap, function_pointer);
   }

   static native void nglEnable(int var0, long var1);

   public static void glDisable(int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisable;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisable(cap, function_pointer);
   }

   static native void nglDisable(int var0, long var1);

   public static void glEdgeFlagPointer(int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEdgeFlagPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glEdgeFlagPointer_pointer = pointer;
      }

      nglEdgeFlagPointer(stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglEdgeFlagPointer(int var0, long var1, long var3);

   public static void glEdgeFlagPointer(int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEdgeFlagPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglEdgeFlagPointerBO(stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglEdgeFlagPointerBO(int var0, long var1, long var3);

   public static void glEdgeFlag(boolean flag) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEdgeFlag;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEdgeFlag(flag, function_pointer);
   }

   static native void nglEdgeFlag(boolean var0, long var1);

   public static void glDrawPixels(int width, int height, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglDrawPixels(width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glDrawPixels(int width, int height, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglDrawPixels(width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glDrawPixels(int width, int height, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglDrawPixels(width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglDrawPixels(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glDrawPixels(int width, int height, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglDrawPixelsBO(width, height, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglDrawPixelsBO(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glDrawElements(int mode, ByteBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElements(mode, indices.remaining(), 5121, MemoryUtil.getAddress(indices), function_pointer);
   }

   public static void glDrawElements(int mode, IntBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElements(mode, indices.remaining(), 5125, MemoryUtil.getAddress(indices), function_pointer);
   }

   public static void glDrawElements(int mode, ShortBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElements(mode, indices.remaining(), 5123, MemoryUtil.getAddress(indices), function_pointer);
   }

   static native void nglDrawElements(int var0, int var1, int var2, long var3, long var5);

   public static void glDrawElements(int mode, int indices_count, int type, long indices_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawElementsBO(mode, indices_count, type, indices_buffer_offset, function_pointer);
   }

   static native void nglDrawElementsBO(int var0, int var1, int var2, long var3, long var5);

   public static void glDrawElements(int mode, int count, int type, ByteBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkBuffer(indices, count);
      nglDrawElements(mode, count, type, MemoryUtil.getAddress(indices), function_pointer);
   }

   public static void glDrawBuffer(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawBuffer(mode, function_pointer);
   }

   static native void nglDrawBuffer(int var0, long var1);

   public static void glDrawArrays(int mode, int first, int count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawArrays(mode, first, count, function_pointer);
   }

   static native void nglDrawArrays(int var0, int var1, int var2, long var3);

   public static void glDepthRange(double zNear, double zFar) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDepthRange;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDepthRange(zNear, zFar, function_pointer);
   }

   static native void nglDepthRange(double var0, double var2, long var4);

   public static void glDepthMask(boolean flag) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDepthMask;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDepthMask(flag, function_pointer);
   }

   static native void nglDepthMask(boolean var0, long var1);

   public static void glDepthFunc(int func) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDepthFunc;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDepthFunc(func, function_pointer);
   }

   static native void nglDepthFunc(int var0, long var1);

   public static void glFeedbackBuffer(int type, FloatBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFeedbackBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buffer);
      nglFeedbackBuffer(buffer.remaining(), type, MemoryUtil.getAddress(buffer), function_pointer);
   }

   static native void nglFeedbackBuffer(int var0, int var1, long var2, long var4);

   public static void glGetPixelMap(int map, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer((FloatBuffer)values, 256);
      nglGetPixelMapfv(map, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglGetPixelMapfv(int var0, long var1, long var3);

   public static void glGetPixelMapfv(int map, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetPixelMapfvBO(map, values_buffer_offset, function_pointer);
   }

   static native void nglGetPixelMapfvBO(int var0, long var1, long var3);

   public static void glGetPixelMapu(int map, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer((IntBuffer)values, 256);
      nglGetPixelMapuiv(map, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglGetPixelMapuiv(int var0, long var1, long var3);

   public static void glGetPixelMapuiv(int map, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetPixelMapuivBO(map, values_buffer_offset, function_pointer);
   }

   static native void nglGetPixelMapuivBO(int var0, long var1, long var3);

   public static void glGetPixelMapu(int map, ShortBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapusv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer((ShortBuffer)values, 256);
      nglGetPixelMapusv(map, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglGetPixelMapusv(int var0, long var1, long var3);

   public static void glGetPixelMapusv(int map, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPixelMapusv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetPixelMapusvBO(map, values_buffer_offset, function_pointer);
   }

   static native void nglGetPixelMapusvBO(int var0, long var1, long var3);

   public static void glGetMaterial(int face, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMaterialfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMaterialfv(face, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMaterialfv(int var0, int var1, long var2, long var4);

   public static void glGetMaterial(int face, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMaterialiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMaterialiv(face, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMaterialiv(int var0, int var1, long var2, long var4);

   public static void glGetMap(int target, int query, FloatBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)v, 256);
      nglGetMapfv(target, query, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglGetMapfv(int var0, int var1, long var2, long var4);

   public static void glGetMap(int target, int query, DoubleBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapdv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)v, 256);
      nglGetMapdv(target, query, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglGetMapdv(int var0, int var1, long var2, long var4);

   public static void glGetMap(int target, int query, IntBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)v, 256);
      nglGetMapiv(target, query, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglGetMapiv(int var0, int var1, long var2, long var4);

   public static void glGetLight(int light, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetLightfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetLightfv(light, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetLightfv(int var0, int var1, long var2, long var4);

   public static void glGetLight(int light, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetLightiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetLightiv(light, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetLightiv(int var0, int var1, long var2, long var4);

   public static int glGetError() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetError;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetError(function_pointer);
      return __result;
   }

   static native int nglGetError(long var0);

   public static void glGetClipPlane(int plane, DoubleBuffer equation) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetClipPlane;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)equation, 4);
      nglGetClipPlane(plane, MemoryUtil.getAddress(equation), function_pointer);
   }

   static native void nglGetClipPlane(int var0, long var1, long var3);

   public static void glGetBoolean(int pname, ByteBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBooleanv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)params, 16);
      nglGetBooleanv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetBooleanv(int var0, long var1, long var3);

   public static boolean glGetBoolean(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBooleanv;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer params = APIUtil.getBufferByte(caps, 1);
      nglGetBooleanv(pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0) == 1;
   }

   public static void glGetDouble(int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoublev;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 16);
      nglGetDoublev(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetDoublev(int var0, long var1, long var3);

   public static double glGetDouble(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoublev;
      BufferChecks.checkFunctionAddress(function_pointer);
      DoubleBuffer params = APIUtil.getBufferDouble(caps);
      nglGetDoublev(pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetFloat(int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloatv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 16);
      nglGetFloatv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetFloatv(int var0, long var1, long var3);

   public static float glGetFloat(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloatv;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetFloatv(pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetInteger(int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 16);
      nglGetIntegerv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetIntegerv(int var0, long var1, long var3);

   public static int glGetInteger(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetIntegerv(pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGenTextures(IntBuffer textures) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(textures);
      nglGenTextures(textures.remaining(), MemoryUtil.getAddress(textures), function_pointer);
   }

   static native void nglGenTextures(int var0, long var1, long var3);

   public static int glGenTextures() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer textures = APIUtil.getBufferInt(caps);
      nglGenTextures(1, MemoryUtil.getAddress(textures), function_pointer);
      return textures.get(0);
   }

   public static int glGenLists(int range) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenLists;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGenLists(range, function_pointer);
      return __result;
   }

   static native int nglGenLists(int var0, long var1);

   public static void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFrustum;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFrustum(left, right, bottom, top, zNear, zFar, function_pointer);
   }

   static native void nglFrustum(double var0, double var2, double var4, double var6, double var8, double var10, long var12);

   public static void glFrontFace(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFrontFace;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFrontFace(mode, function_pointer);
   }

   static native void nglFrontFace(int var0, long var1);

   public static void glFogf(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogf(pname, param, function_pointer);
   }

   static native void nglFogf(int var0, float var1, long var2);

   public static void glFogi(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogi;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogi(pname, param, function_pointer);
   }

   static native void nglFogi(int var0, int var1, long var2);

   public static void glFog(int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglFogfv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglFogfv(int var0, long var1, long var3);

   public static void glFog(int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglFogiv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglFogiv(int var0, long var1, long var3);

   public static void glFlush() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFlush;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFlush(function_pointer);
   }

   static native void nglFlush(long var0);

   public static void glFinish() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFinish;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFinish(function_pointer);
   }

   static native void nglFinish(long var0);

   public static ByteBuffer glGetPointer(int pname, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPointerv;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetPointerv(pname, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetPointerv(int var0, long var1, long var3);

   public static boolean glIsEnabled(int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsEnabled;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsEnabled(cap, function_pointer);
      return __result;
   }

   static native boolean nglIsEnabled(int var0, long var1);

   public static void glInterleavedArrays(int format, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglInterleavedArrays(format, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glInterleavedArrays(int format, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglInterleavedArrays(format, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glInterleavedArrays(int format, int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglInterleavedArrays(format, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glInterleavedArrays(int format, int stride, IntBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglInterleavedArrays(format, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glInterleavedArrays(int format, int stride, ShortBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglInterleavedArrays(format, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglInterleavedArrays(int var0, int var1, long var2, long var4);

   public static void glInterleavedArrays(int format, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterleavedArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglInterleavedArraysBO(format, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglInterleavedArraysBO(int var0, int var1, long var2, long var4);

   public static void glInitNames() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInitNames;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInitNames(function_pointer);
   }

   static native void nglInitNames(long var0);

   public static void glHint(int target, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glHint;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglHint(target, mode, function_pointer);
   }

   static native void nglHint(int var0, int var1, long var2);

   public static void glGetTexParameter(int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTexParameterfv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexParameterfv(int var0, int var1, long var2, long var4);

   public static float glGetTexParameterf(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTexParameterfv(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexParameter(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexParameteriv(int var0, int var1, long var2, long var4);

   public static int glGetTexParameteri(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexLevelParameter(int target, int level, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexLevelParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTexLevelParameterfv(target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexLevelParameterfv(int var0, int var1, int var2, long var3, long var5);

   public static float glGetTexLevelParameterf(int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexLevelParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTexLevelParameterfv(target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexLevelParameter(int target, int level, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexLevelParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexLevelParameteriv(target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexLevelParameteriv(int var0, int var1, int var2, long var3, long var5);

   public static int glGetTexLevelParameteri(int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexLevelParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexLevelParameteriv(target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTexImage(target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTexImage(int target, int level, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTexImage(target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTexImage(int target, int level, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTexImage(target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTexImage(int target, int level, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTexImage(target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTexImage(int target, int level, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTexImage(target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglGetTexImage(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glGetTexImage(int target, int level, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetTexImageBO(target, level, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglGetTexImageBO(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glGetTexGen(int coord, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGeniv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexGeniv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexGeniv(int var0, int var1, long var2, long var4);

   public static int glGetTexGeni(int coord, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGeniv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexGeniv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexGen(int coord, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGenfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTexGenfv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexGenfv(int var0, int var1, long var2, long var4);

   public static float glGetTexGenf(int coord, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGenfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTexGenfv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexGen(int coord, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGendv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetTexGendv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexGendv(int var0, int var1, long var2, long var4);

   public static double glGetTexGend(int coord, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexGendv;
      BufferChecks.checkFunctionAddress(function_pointer);
      DoubleBuffer params = APIUtil.getBufferDouble(caps);
      nglGetTexGendv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexEnv(int coord, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexEnviv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexEnviv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexEnviv(int var0, int var1, long var2, long var4);

   public static int glGetTexEnvi(int coord, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexEnviv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexEnviv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexEnv(int coord, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexEnvfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTexEnvfv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexEnvfv(int var0, int var1, long var2, long var4);

   public static float glGetTexEnvf(int coord, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexEnvfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTexEnvfv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static String glGetString(int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetString;
      BufferChecks.checkFunctionAddress(function_pointer);
      String __result = nglGetString(name, function_pointer);
      return __result;
   }

   static native String nglGetString(int var0, long var1);

   public static void glGetPolygonStipple(ByteBuffer mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPolygonStipple;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer((ByteBuffer)mask, 128);
      nglGetPolygonStipple(MemoryUtil.getAddress(mask), function_pointer);
   }

   static native void nglGetPolygonStipple(long var0, long var2);

   public static void glGetPolygonStipple(long mask_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPolygonStipple;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetPolygonStippleBO(mask_buffer_offset, function_pointer);
   }

   static native void nglGetPolygonStippleBO(long var0, long var2);

   public static boolean glIsList(int list) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsList;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsList(list, function_pointer);
      return __result;
   }

   static native boolean nglIsList(int var0, long var1);

   public static void glMaterialf(int face, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMaterialf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMaterialf(face, pname, param, function_pointer);
   }

   static native void nglMaterialf(int var0, int var1, float var2, long var3);

   public static void glMateriali(int face, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMateriali;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMateriali(face, pname, param, function_pointer);
   }

   static native void nglMateriali(int var0, int var1, int var2, long var3);

   public static void glMaterial(int face, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMaterialfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglMaterialfv(face, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMaterialfv(int var0, int var1, long var2, long var4);

   public static void glMaterial(int face, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMaterialiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMaterialiv(face, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMaterialiv(int var0, int var1, long var2, long var4);

   public static void glMapGrid1f(int un, float u1, float u2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapGrid1f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMapGrid1f(un, u1, u2, function_pointer);
   }

   static native void nglMapGrid1f(int var0, float var1, float var2, long var3);

   public static void glMapGrid1d(int un, double u1, double u2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapGrid1d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMapGrid1d(un, u1, u2, function_pointer);
   }

   static native void nglMapGrid1d(int var0, double var1, double var3, long var5);

   public static void glMapGrid2f(int un, float u1, float u2, int vn, float v1, float v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapGrid2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMapGrid2f(un, u1, u2, vn, v1, v2, function_pointer);
   }

   static native void nglMapGrid2f(int var0, float var1, float var2, int var3, float var4, float var5, long var6);

   public static void glMapGrid2d(int un, double u1, double u2, int vn, double v1, double v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapGrid2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMapGrid2d(un, u1, u2, vn, v1, v2, function_pointer);
   }

   static native void nglMapGrid2d(int var0, double var1, double var3, int var5, double var6, double var8, long var10);

   public static void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2, int vstride, int vorder, FloatBuffer points) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMap2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(points);
      nglMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, MemoryUtil.getAddress(points), function_pointer);
   }

   static native void nglMap2f(int var0, float var1, float var2, int var3, int var4, float var5, float var6, int var7, int var8, long var9, long var11);

   public static void glMap2d(int target, double u1, double u2, int ustride, int uorder, double v1, double v2, int vstride, int vorder, DoubleBuffer points) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMap2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(points);
      nglMap2d(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, MemoryUtil.getAddress(points), function_pointer);
   }

   static native void nglMap2d(int var0, double var1, double var3, int var5, int var6, double var7, double var9, int var11, int var12, long var13, long var15);

   public static void glMap1f(int target, float u1, float u2, int stride, int order, FloatBuffer points) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMap1f;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(points);
      nglMap1f(target, u1, u2, stride, order, MemoryUtil.getAddress(points), function_pointer);
   }

   static native void nglMap1f(int var0, float var1, float var2, int var3, int var4, long var5, long var7);

   public static void glMap1d(int target, double u1, double u2, int stride, int order, DoubleBuffer points) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMap1d;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(points);
      nglMap1d(target, u1, u2, stride, order, MemoryUtil.getAddress(points), function_pointer);
   }

   static native void nglMap1d(int var0, double var1, double var3, int var5, int var6, long var7, long var9);

   public static void glLogicOp(int opcode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLogicOp;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLogicOp(opcode, function_pointer);
   }

   static native void nglLogicOp(int var0, long var1);

   public static void glLoadName(int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadName;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLoadName(name, function_pointer);
   }

   static native void nglLoadName(int var0, long var1);

   public static void glLoadMatrix(FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadMatrixf;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglLoadMatrixf(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglLoadMatrixf(long var0, long var2);

   public static void glLoadMatrix(DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadMatrixd;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglLoadMatrixd(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglLoadMatrixd(long var0, long var2);

   public static void glLoadIdentity() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadIdentity;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLoadIdentity(function_pointer);
   }

   static native void nglLoadIdentity(long var0);

   public static void glListBase(int base) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glListBase;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglListBase(base, function_pointer);
   }

   static native void nglListBase(int var0, long var1);

   public static void glLineWidth(float width) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLineWidth;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLineWidth(width, function_pointer);
   }

   static native void nglLineWidth(float var0, long var1);

   public static void glLineStipple(int factor, short pattern) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLineStipple;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLineStipple(factor, pattern, function_pointer);
   }

   static native void nglLineStipple(int var0, short var1, long var2);

   public static void glLightModelf(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightModelf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLightModelf(pname, param, function_pointer);
   }

   static native void nglLightModelf(int var0, float var1, long var2);

   public static void glLightModeli(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightModeli;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLightModeli(pname, param, function_pointer);
   }

   static native void nglLightModeli(int var0, int var1, long var2);

   public static void glLightModel(int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightModelfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglLightModelfv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglLightModelfv(int var0, long var1, long var3);

   public static void glLightModel(int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightModeliv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglLightModeliv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglLightModeliv(int var0, long var1, long var3);

   public static void glLightf(int light, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLightf(light, pname, param, function_pointer);
   }

   static native void nglLightf(int var0, int var1, float var2, long var3);

   public static void glLighti(int light, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLighti;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLighti(light, pname, param, function_pointer);
   }

   static native void nglLighti(int var0, int var1, int var2, long var3);

   public static void glLight(int light, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglLightfv(light, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglLightfv(int var0, int var1, long var2, long var4);

   public static void glLight(int light, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLightiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglLightiv(light, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglLightiv(int var0, int var1, long var2, long var4);

   public static boolean glIsTexture(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsTexture(texture, function_pointer);
      return __result;
   }

   static native boolean nglIsTexture(int var0, long var1);

   public static void glMatrixMode(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixMode;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixMode(mode, function_pointer);
   }

   static native void nglMatrixMode(int var0, long var1);

   public static void glPolygonStipple(ByteBuffer mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPolygonStipple;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer((ByteBuffer)mask, 128);
      nglPolygonStipple(MemoryUtil.getAddress(mask), function_pointer);
   }

   static native void nglPolygonStipple(long var0, long var2);

   public static void glPolygonStipple(long mask_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPolygonStipple;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglPolygonStippleBO(mask_buffer_offset, function_pointer);
   }

   static native void nglPolygonStippleBO(long var0, long var2);

   public static void glPolygonOffset(float factor, float units) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPolygonOffset;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPolygonOffset(factor, units, function_pointer);
   }

   static native void nglPolygonOffset(float var0, float var1, long var2);

   public static void glPolygonMode(int face, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPolygonMode;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPolygonMode(face, mode, function_pointer);
   }

   static native void nglPolygonMode(int var0, int var1, long var2);

   public static void glPointSize(float size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointSize;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPointSize(size, function_pointer);
   }

   static native void nglPointSize(float var0, long var1);

   public static void glPixelZoom(float xfactor, float yfactor) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelZoom;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPixelZoom(xfactor, yfactor, function_pointer);
   }

   static native void nglPixelZoom(float var0, float var1, long var2);

   public static void glPixelTransferf(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelTransferf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPixelTransferf(pname, param, function_pointer);
   }

   static native void nglPixelTransferf(int var0, float var1, long var2);

   public static void glPixelTransferi(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelTransferi;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPixelTransferi(pname, param, function_pointer);
   }

   static native void nglPixelTransferi(int var0, int var1, long var2);

   public static void glPixelStoref(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelStoref;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPixelStoref(pname, param, function_pointer);
   }

   static native void nglPixelStoref(int var0, float var1, long var2);

   public static void glPixelStorei(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelStorei;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPixelStorei(pname, param, function_pointer);
   }

   static native void nglPixelStorei(int var0, int var1, long var2);

   public static void glPixelMap(int map, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(values);
      nglPixelMapfv(map, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglPixelMapfv(int var0, int var1, long var2, long var4);

   public static void glPixelMapfv(int map, int values_mapsize, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglPixelMapfvBO(map, values_mapsize, values_buffer_offset, function_pointer);
   }

   static native void nglPixelMapfvBO(int var0, int var1, long var2, long var4);

   public static void glPixelMapu(int map, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(values);
      nglPixelMapuiv(map, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglPixelMapuiv(int var0, int var1, long var2, long var4);

   public static void glPixelMapuiv(int map, int values_mapsize, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglPixelMapuivBO(map, values_mapsize, values_buffer_offset, function_pointer);
   }

   static native void nglPixelMapuivBO(int var0, int var1, long var2, long var4);

   public static void glPixelMapu(int map, ShortBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapusv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(values);
      nglPixelMapusv(map, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglPixelMapusv(int var0, int var1, long var2, long var4);

   public static void glPixelMapusv(int map, int values_mapsize, long values_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPixelMapusv;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglPixelMapusvBO(map, values_mapsize, values_buffer_offset, function_pointer);
   }

   static native void nglPixelMapusvBO(int var0, int var1, long var2, long var4);

   public static void glPassThrough(float token) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPassThrough;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPassThrough(token, function_pointer);
   }

   static native void nglPassThrough(float var0, long var1);

   public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glOrtho;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglOrtho(left, right, bottom, top, zNear, zFar, function_pointer);
   }

   static native void nglOrtho(double var0, double var2, double var4, double var6, double var8, double var10, long var12);

   public static void glNormalPointer(int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
      }

      nglNormalPointer(5120, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glNormalPointer(int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
      }

      nglNormalPointer(5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glNormalPointer(int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
      }

      nglNormalPointer(5126, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glNormalPointer(int stride, IntBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
      }

      nglNormalPointer(5124, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglNormalPointer(int var0, int var1, long var2, long var4);

   public static void glNormalPointer(int type, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglNormalPointerBO(type, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglNormalPointerBO(int var0, int var1, long var2, long var4);

   public static void glNormalPointer(int type, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
      }

      nglNormalPointer(type, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glNormal3b(byte nx, byte ny, byte nz) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormal3b;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormal3b(nx, ny, nz, function_pointer);
   }

   static native void nglNormal3b(byte var0, byte var1, byte var2, long var3);

   public static void glNormal3f(float nx, float ny, float nz) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormal3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormal3f(nx, ny, nz, function_pointer);
   }

   static native void nglNormal3f(float var0, float var1, float var2, long var3);

   public static void glNormal3d(double nx, double ny, double nz) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormal3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormal3d(nx, ny, nz, function_pointer);
   }

   static native void nglNormal3d(double var0, double var2, double var4, long var6);

   public static void glNormal3i(int nx, int ny, int nz) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormal3i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormal3i(nx, ny, nz, function_pointer);
   }

   static native void nglNormal3i(int var0, int var1, int var2, long var3);

   public static void glNewList(int list, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNewList;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNewList(list, mode, function_pointer);
   }

   static native void nglNewList(int var0, int var1, long var2);

   public static void glEndList() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndList;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndList(function_pointer);
   }

   static native void nglEndList(long var0);

   public static void glMultMatrix(FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultMatrixf;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMultMatrixf(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMultMatrixf(long var0, long var2);

   public static void glMultMatrix(DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultMatrixd;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMultMatrixd(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMultMatrixd(long var0, long var2);

   public static void glShadeModel(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShadeModel;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShadeModel(mode, function_pointer);
   }

   static native void nglShadeModel(int var0, long var1);

   public static void glSelectBuffer(IntBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSelectBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buffer);
      nglSelectBuffer(buffer.remaining(), MemoryUtil.getAddress(buffer), function_pointer);
   }

   static native void nglSelectBuffer(int var0, long var1, long var3);

   public static void glScissor(int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glScissor;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglScissor(x, y, width, height, function_pointer);
   }

   static native void nglScissor(int var0, int var1, int var2, int var3, long var4);

   public static void glScalef(float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glScalef;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglScalef(x, y, z, function_pointer);
   }

   static native void nglScalef(float var0, float var1, float var2, long var3);

   public static void glScaled(double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glScaled;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglScaled(x, y, z, function_pointer);
   }

   static native void nglScaled(double var0, double var2, double var4, long var6);

   public static void glRotatef(float angle, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRotatef;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRotatef(angle, x, y, z, function_pointer);
   }

   static native void nglRotatef(float var0, float var1, float var2, float var3, long var4);

   public static void glRotated(double angle, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRotated;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRotated(angle, x, y, z, function_pointer);
   }

   static native void nglRotated(double var0, double var2, double var4, double var6, long var8);

   public static int glRenderMode(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRenderMode;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglRenderMode(mode, function_pointer);
      return __result;
   }

   static native int nglRenderMode(int var0, long var1);

   public static void glRectf(float x1, float y1, float x2, float y2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRectf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRectf(x1, y1, x2, y2, function_pointer);
   }

   static native void nglRectf(float var0, float var1, float var2, float var3, long var4);

   public static void glRectd(double x1, double y1, double x2, double y2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRectd;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRectd(x1, y1, x2, y2, function_pointer);
   }

   static native void nglRectd(double var0, double var2, double var4, double var6, long var8);

   public static void glRecti(int x1, int y1, int x2, int y2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRecti;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRecti(x1, y1, x2, y2, function_pointer);
   }

   static native void nglRecti(int var0, int var1, int var2, int var3, long var4);

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglReadPixels(x, y, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglReadPixels(x, y, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglReadPixels(x, y, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglReadPixels(x, y, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglReadPixels(x, y, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglReadPixels(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glReadPixels(int x, int y, int width, int height, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadPixels;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglReadPixelsBO(x, y, width, height, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglReadPixelsBO(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glReadBuffer(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglReadBuffer(mode, function_pointer);
   }

   static native void nglReadBuffer(int var0, long var1);

   public static void glRasterPos2f(float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos2f(x, y, function_pointer);
   }

   static native void nglRasterPos2f(float var0, float var1, long var2);

   public static void glRasterPos2d(double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos2d(x, y, function_pointer);
   }

   static native void nglRasterPos2d(double var0, double var2, long var4);

   public static void glRasterPos2i(int x, int y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos2i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos2i(x, y, function_pointer);
   }

   static native void nglRasterPos2i(int var0, int var1, long var2);

   public static void glRasterPos3f(float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos3f(x, y, z, function_pointer);
   }

   static native void nglRasterPos3f(float var0, float var1, float var2, long var3);

   public static void glRasterPos3d(double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos3d(x, y, z, function_pointer);
   }

   static native void nglRasterPos3d(double var0, double var2, double var4, long var6);

   public static void glRasterPos3i(int x, int y, int z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos3i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos3i(x, y, z, function_pointer);
   }

   static native void nglRasterPos3i(int var0, int var1, int var2, long var3);

   public static void glRasterPos4f(float x, float y, float z, float w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos4f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos4f(x, y, z, w, function_pointer);
   }

   static native void nglRasterPos4f(float var0, float var1, float var2, float var3, long var4);

   public static void glRasterPos4d(double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos4d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos4d(x, y, z, w, function_pointer);
   }

   static native void nglRasterPos4d(double var0, double var2, double var4, double var6, long var8);

   public static void glRasterPos4i(int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRasterPos4i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRasterPos4i(x, y, z, w, function_pointer);
   }

   static native void nglRasterPos4i(int var0, int var1, int var2, int var3, long var4);

   public static void glPushName(int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushName;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPushName(name, function_pointer);
   }

   static native void nglPushName(int var0, long var1);

   public static void glPopName() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPopName;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPopName(function_pointer);
   }

   static native void nglPopName(long var0);

   public static void glPushMatrix() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushMatrix;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPushMatrix(function_pointer);
   }

   static native void nglPushMatrix(long var0);

   public static void glPopMatrix() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPopMatrix;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPopMatrix(function_pointer);
   }

   static native void nglPopMatrix(long var0);

   public static void glPushClientAttrib(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushClientAttrib;
      BufferChecks.checkFunctionAddress(function_pointer);
      StateTracker.pushAttrib(caps, mask);
      nglPushClientAttrib(mask, function_pointer);
   }

   static native void nglPushClientAttrib(int var0, long var1);

   public static void glPopClientAttrib() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPopClientAttrib;
      BufferChecks.checkFunctionAddress(function_pointer);
      StateTracker.popAttrib(caps);
      nglPopClientAttrib(function_pointer);
   }

   static native void nglPopClientAttrib(long var0);

   public static void glPushAttrib(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushAttrib;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPushAttrib(mask, function_pointer);
   }

   static native void nglPushAttrib(int var0, long var1);

   public static void glPopAttrib() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPopAttrib;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPopAttrib(function_pointer);
   }

   static native void nglPopAttrib(long var0);

   public static void glStencilFunc(int func, int ref, int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilFunc;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilFunc(func, ref, mask, function_pointer);
   }

   static native void nglStencilFunc(int var0, int var1, int var2, long var3);

   public static void glVertexPointer(int size, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glVertexPointer_pointer = pointer;
      }

      nglVertexPointer(size, 5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glVertexPointer(int size, int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glVertexPointer_pointer = pointer;
      }

      nglVertexPointer(size, 5126, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glVertexPointer(int size, int stride, IntBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glVertexPointer_pointer = pointer;
      }

      nglVertexPointer(size, 5124, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glVertexPointer(int size, int stride, ShortBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glVertexPointer_pointer = pointer;
      }

      nglVertexPointer(size, 5122, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglVertexPointer(int var0, int var1, int var2, long var3, long var5);

   public static void glVertexPointer(int size, int type, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglVertexPointerBO(size, type, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglVertexPointerBO(int var0, int var1, int var2, long var3, long var5);

   public static void glVertexPointer(int size, int type, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL11_glVertexPointer_pointer = pointer;
      }

      nglVertexPointer(size, type, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glVertex2f(float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex2f(x, y, function_pointer);
   }

   static native void nglVertex2f(float var0, float var1, long var2);

   public static void glVertex2d(double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex2d(x, y, function_pointer);
   }

   static native void nglVertex2d(double var0, double var2, long var4);

   public static void glVertex2i(int x, int y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex2i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex2i(x, y, function_pointer);
   }

   static native void nglVertex2i(int var0, int var1, long var2);

   public static void glVertex3f(float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex3f(x, y, z, function_pointer);
   }

   static native void nglVertex3f(float var0, float var1, float var2, long var3);

   public static void glVertex3d(double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex3d(x, y, z, function_pointer);
   }

   static native void nglVertex3d(double var0, double var2, double var4, long var6);

   public static void glVertex3i(int x, int y, int z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex3i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex3i(x, y, z, function_pointer);
   }

   static native void nglVertex3i(int var0, int var1, int var2, long var3);

   public static void glVertex4f(float x, float y, float z, float w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex4f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex4f(x, y, z, w, function_pointer);
   }

   static native void nglVertex4f(float var0, float var1, float var2, float var3, long var4);

   public static void glVertex4d(double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex4d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex4d(x, y, z, w, function_pointer);
   }

   static native void nglVertex4d(double var0, double var2, double var4, double var6, long var8);

   public static void glVertex4i(int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertex4i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertex4i(x, y, z, w, function_pointer);
   }

   static native void nglVertex4i(int var0, int var1, int var2, int var3, long var4);

   public static void glTranslatef(float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTranslatef;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTranslatef(x, y, z, function_pointer);
   }

   static native void nglTranslatef(float var0, float var1, float var2, long var3);

   public static void glTranslated(double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTranslated;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTranslated(x, y, z, function_pointer);
   }

   static native void nglTranslated(double var0, double var2, double var4, long var6);

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTexImage1D(target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTexImage1D(target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTexImage1D(target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTexImage1D(target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTexImage1D(target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTexImage1D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexImage1DBO(target, level, internalformat, width, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexImage1DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTexImage2D(target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTexImage2D(target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTexImage2D(target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTexImage2D(target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTexImage2D(target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTexImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexImage2DBO(target, level, internalformat, width, height, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexImage2DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTexSubImage1D(target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTexSubImage1D(target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTexSubImage1D(target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTexSubImage1D(target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTexSubImage1D(target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTexSubImage1D(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexSubImage1DBO(target, level, xoffset, width, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexSubImage1DBO(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexSubImage2DBO(target, level, xoffset, yoffset, width, height, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexSubImage2DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTexParameterf(int target, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexParameterf(target, pname, param, function_pointer);
   }

   static native void nglTexParameterf(int var0, int var1, float var2, long var3);

   public static void glTexParameteri(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameteri;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexParameteri(target, pname, param, function_pointer);
   }

   static native void nglTexParameteri(int var0, int var1, int var2, long var3);

   public static void glTexParameter(int target, int pname, FloatBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)param, 4);
      nglTexParameterfv(target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglTexParameterfv(int var0, int var1, long var2, long var4);

   public static void glTexParameter(int target, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 4);
      nglTexParameteriv(target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglTexParameteriv(int var0, int var1, long var2, long var4);

   public static void glTexGenf(int coord, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGenf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexGenf(coord, pname, param, function_pointer);
   }

   static native void nglTexGenf(int var0, int var1, float var2, long var3);

   public static void glTexGend(int coord, int pname, double param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGend;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexGend(coord, pname, param, function_pointer);
   }

   static native void nglTexGend(int var0, int var1, double var2, long var4);

   public static void glTexGen(int coord, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGenfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglTexGenfv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexGenfv(int var0, int var1, long var2, long var4);

   public static void glTexGen(int coord, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGendv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglTexGendv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexGendv(int var0, int var1, long var2, long var4);

   public static void glTexGeni(int coord, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGeni;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexGeni(coord, pname, param, function_pointer);
   }

   static native void nglTexGeni(int var0, int var1, int var2, long var3);

   public static void glTexGen(int coord, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexGeniv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTexGeniv(coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexGeniv(int var0, int var1, long var2, long var4);

   public static void glTexEnvf(int target, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexEnvf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexEnvf(target, pname, param, function_pointer);
   }

   static native void nglTexEnvf(int var0, int var1, float var2, long var3);

   public static void glTexEnvi(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexEnvi;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexEnvi(target, pname, param, function_pointer);
   }

   static native void nglTexEnvi(int var0, int var1, int var2, long var3);

   public static void glTexEnv(int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexEnvfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglTexEnvfv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexEnvfv(int var0, int var1, long var2, long var4);

   public static void glTexEnv(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexEnviv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTexEnviv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexEnviv(int var0, int var1, long var2, long var4);

   public static void glTexCoordPointer(int size, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glTexCoordPointer_buffer[StateTracker.getReferences(caps).glClientActiveTexture] = pointer;
      }

      nglTexCoordPointer(size, 5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glTexCoordPointer(int size, int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glTexCoordPointer_buffer[StateTracker.getReferences(caps).glClientActiveTexture] = pointer;
      }

      nglTexCoordPointer(size, 5126, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glTexCoordPointer(int size, int stride, IntBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glTexCoordPointer_buffer[StateTracker.getReferences(caps).glClientActiveTexture] = pointer;
      }

      nglTexCoordPointer(size, 5124, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glTexCoordPointer(int size, int stride, ShortBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glTexCoordPointer_buffer[StateTracker.getReferences(caps).glClientActiveTexture] = pointer;
      }

      nglTexCoordPointer(size, 5122, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglTexCoordPointer(int var0, int var1, int var2, long var3, long var5);

   public static void glTexCoordPointer(int size, int type, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglTexCoordPointerBO(size, type, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglTexCoordPointerBO(int var0, int var1, int var2, long var3, long var5);

   public static void glTexCoordPointer(int size, int type, int stride, ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glTexCoordPointer_buffer[StateTracker.getReferences(caps).glClientActiveTexture] = pointer;
      }

      nglTexCoordPointer(size, type, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glTexCoord1f(float s) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord1f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord1f(s, function_pointer);
   }

   static native void nglTexCoord1f(float var0, long var1);

   public static void glTexCoord1d(double s) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord1d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord1d(s, function_pointer);
   }

   static native void nglTexCoord1d(double var0, long var2);

   public static void glTexCoord2f(float s, float t) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord2f(s, t, function_pointer);
   }

   static native void nglTexCoord2f(float var0, float var1, long var2);

   public static void glTexCoord2d(double s, double t) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord2d(s, t, function_pointer);
   }

   static native void nglTexCoord2d(double var0, double var2, long var4);

   public static void glTexCoord3f(float s, float t, float r) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord3f(s, t, r, function_pointer);
   }

   static native void nglTexCoord3f(float var0, float var1, float var2, long var3);

   public static void glTexCoord3d(double s, double t, double r) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord3d(s, t, r, function_pointer);
   }

   static native void nglTexCoord3d(double var0, double var2, double var4, long var6);

   public static void glTexCoord4f(float s, float t, float r, float q) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord4f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord4f(s, t, r, q, function_pointer);
   }

   static native void nglTexCoord4f(float var0, float var1, float var2, float var3, long var4);

   public static void glTexCoord4d(double s, double t, double r, double q) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoord4d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoord4d(s, t, r, q, function_pointer);
   }

   static native void nglTexCoord4d(double var0, double var2, double var4, double var6, long var8);

   public static void glStencilOp(int fail, int zfail, int zpass) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilOp;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilOp(fail, zfail, zpass, function_pointer);
   }

   static native void nglStencilOp(int var0, int var1, int var2, long var3);

   public static void glStencilMask(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilMask;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilMask(mask, function_pointer);
   }

   static native void nglStencilMask(int var0, long var1);

   public static void glViewport(int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glViewport;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglViewport(x, y, width, height, function_pointer);
   }

   static native void nglViewport(int var0, int var1, int var2, int var3, long var4);
}
