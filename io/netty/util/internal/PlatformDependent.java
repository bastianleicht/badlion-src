package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.JavassistTypeParameterMatcherGenerator;
import io.netty.util.internal.MpscLinkedQueue;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
   private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
   private static final boolean IS_ANDROID = isAndroid0();
   private static final boolean IS_WINDOWS = isWindows0();
   private static final boolean IS_ROOT = isRoot0();
   private static final int JAVA_VERSION = javaVersion0();
   private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();
   private static final boolean HAS_UNSAFE = hasUnsafe0();
   private static final boolean CAN_USE_CHM_V8 = HAS_UNSAFE && JAVA_VERSION < 8;
   private static final boolean DIRECT_BUFFER_PREFERRED = HAS_UNSAFE && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
   private static final long MAX_DIRECT_MEMORY = maxDirectMemory0();
   private static final long ARRAY_BASE_OFFSET = arrayBaseOffset0();
   private static final boolean HAS_JAVASSIST = hasJavassist0();
   private static final File TMPDIR = tmpdir0();
   private static final int BIT_MODE = bitMode0();
   private static final int ADDRESS_SIZE = addressSize0();

   public static boolean isAndroid() {
      return IS_ANDROID;
   }

   public static boolean isWindows() {
      return IS_WINDOWS;
   }

   public static boolean isRoot() {
      return IS_ROOT;
   }

   public static int javaVersion() {
      return JAVA_VERSION;
   }

   public static boolean canEnableTcpNoDelayByDefault() {
      return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
   }

   public static boolean hasUnsafe() {
      return HAS_UNSAFE;
   }

   public static boolean directBufferPreferred() {
      return DIRECT_BUFFER_PREFERRED;
   }

   public static long maxDirectMemory() {
      return MAX_DIRECT_MEMORY;
   }

   public static boolean hasJavassist() {
      return HAS_JAVASSIST;
   }

   public static File tmpdir() {
      return TMPDIR;
   }

   public static int bitMode() {
      return BIT_MODE;
   }

   public static int addressSize() {
      return ADDRESS_SIZE;
   }

   public static long allocateMemory(long size) {
      return PlatformDependent0.allocateMemory(size);
   }

   public static void freeMemory(long address) {
      PlatformDependent0.freeMemory(address);
   }

   public static void throwException(Throwable t) {
      if(hasUnsafe()) {
         PlatformDependent0.throwException(t);
      } else {
         throwException0(t);
      }

   }

   private static void throwException0(Throwable t) throws Throwable {
      throw t;
   }

   public static ConcurrentMap newConcurrentHashMap() {
      return (ConcurrentMap)(CAN_USE_CHM_V8?new ConcurrentHashMapV8():new ConcurrentHashMap());
   }

   public static ConcurrentMap newConcurrentHashMap(int initialCapacity) {
      return (ConcurrentMap)(CAN_USE_CHM_V8?new ConcurrentHashMapV8(initialCapacity):new ConcurrentHashMap(initialCapacity));
   }

   public static ConcurrentMap newConcurrentHashMap(int initialCapacity, float loadFactor) {
      return (ConcurrentMap)(CAN_USE_CHM_V8?new ConcurrentHashMapV8(initialCapacity, loadFactor):new ConcurrentHashMap(initialCapacity, loadFactor));
   }

   public static ConcurrentMap newConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
      return (ConcurrentMap)(CAN_USE_CHM_V8?new ConcurrentHashMapV8(initialCapacity, loadFactor, concurrencyLevel):new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel));
   }

   public static ConcurrentMap newConcurrentHashMap(Map map) {
      return (ConcurrentMap)(CAN_USE_CHM_V8?new ConcurrentHashMapV8(map):new ConcurrentHashMap(map));
   }

   public static void freeDirectBuffer(ByteBuffer buffer) {
      if(hasUnsafe() && !isAndroid()) {
         PlatformDependent0.freeDirectBuffer(buffer);
      }

   }

   public static long directBufferAddress(ByteBuffer buffer) {
      return PlatformDependent0.directBufferAddress(buffer);
   }

   public static Object getObject(Object object, long fieldOffset) {
      return PlatformDependent0.getObject(object, fieldOffset);
   }

   public static Object getObjectVolatile(Object object, long fieldOffset) {
      return PlatformDependent0.getObjectVolatile(object, fieldOffset);
   }

   public static int getInt(Object object, long fieldOffset) {
      return PlatformDependent0.getInt(object, fieldOffset);
   }

   public static long objectFieldOffset(Field field) {
      return PlatformDependent0.objectFieldOffset(field);
   }

   public static byte getByte(long address) {
      return PlatformDependent0.getByte(address);
   }

   public static short getShort(long address) {
      return PlatformDependent0.getShort(address);
   }

   public static int getInt(long address) {
      return PlatformDependent0.getInt(address);
   }

   public static long getLong(long address) {
      return PlatformDependent0.getLong(address);
   }

   public static void putOrderedObject(Object object, long address, Object value) {
      PlatformDependent0.putOrderedObject(object, address, value);
   }

   public static void putByte(long address, byte value) {
      PlatformDependent0.putByte(address, value);
   }

   public static void putShort(long address, short value) {
      PlatformDependent0.putShort(address, value);
   }

   public static void putInt(long address, int value) {
      PlatformDependent0.putInt(address, value);
   }

   public static void putLong(long address, long value) {
      PlatformDependent0.putLong(address, value);
   }

   public static void copyMemory(long srcAddr, long dstAddr, long length) {
      PlatformDependent0.copyMemory(srcAddr, dstAddr, length);
   }

   public static void copyMemory(byte[] src, int srcIndex, long dstAddr, long length) {
      PlatformDependent0.copyMemory(src, ARRAY_BASE_OFFSET + (long)srcIndex, (Object)null, dstAddr, length);
   }

   public static void copyMemory(long srcAddr, byte[] dst, int dstIndex, long length) {
      PlatformDependent0.copyMemory((Object)null, srcAddr, dst, ARRAY_BASE_OFFSET + (long)dstIndex, length);
   }

   public static AtomicReferenceFieldUpdater newAtomicReferenceFieldUpdater(Class tclass, String fieldName) {
      if(hasUnsafe()) {
         try {
            return PlatformDependent0.newAtomicReferenceFieldUpdater(tclass, fieldName);
         } catch (Throwable var3) {
            ;
         }
      }

      return null;
   }

   public static AtomicIntegerFieldUpdater newAtomicIntegerFieldUpdater(Class tclass, String fieldName) {
      if(hasUnsafe()) {
         try {
            return PlatformDependent0.newAtomicIntegerFieldUpdater(tclass, fieldName);
         } catch (Throwable var3) {
            ;
         }
      }

      return null;
   }

   public static AtomicLongFieldUpdater newAtomicLongFieldUpdater(Class tclass, String fieldName) {
      if(hasUnsafe()) {
         try {
            return PlatformDependent0.newAtomicLongFieldUpdater(tclass, fieldName);
         } catch (Throwable var3) {
            ;
         }
      }

      return null;
   }

   public static Queue newMpscQueue() {
      return new MpscLinkedQueue();
   }

   public static ClassLoader getClassLoader(Class clazz) {
      return PlatformDependent0.getClassLoader(clazz);
   }

   public static ClassLoader getContextClassLoader() {
      return PlatformDependent0.getContextClassLoader();
   }

   public static ClassLoader getSystemClassLoader() {
      return PlatformDependent0.getSystemClassLoader();
   }

   private static boolean isAndroid0() {
      boolean android;
      try {
         Class.forName("android.app.Application", false, getSystemClassLoader());
         android = true;
      } catch (Exception var2) {
         android = false;
      }

      if(android) {
         logger.debug("Platform: Android");
      }

      return android;
   }

   private static boolean isWindows0() {
      boolean windows = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).contains("win");
      if(windows) {
         logger.debug("Platform: Windows");
      }

      return windows;
   }

   private static boolean isRoot0() {
      if(isWindows()) {
         return false;
      } else {
         String[] ID_COMMANDS = new String[]{"/usr/bin/id", "/bin/id", "id", "/usr/xpg4/bin/id"};
         Pattern UID_PATTERN = Pattern.compile("^(?:0|[1-9][0-9]*)$");

         for(String idCmd : ID_COMMANDS) {
            Process p = null;
            BufferedReader in = null;
            String uid = null;

            try {
               p = Runtime.getRuntime().exec(new String[]{idCmd, "-u"});
               in = new BufferedReader(new InputStreamReader(p.getInputStream(), CharsetUtil.US_ASCII));
               uid = in.readLine();
               in.close();

               while(true) {
                  try {
                     int exitCode = p.waitFor();
                     if(exitCode != 0) {
                        uid = null;
                     }
                     break;
                  } catch (InterruptedException var46) {
                     ;
                  }
               }
            } catch (Exception var47) {
               uid = null;
            } finally {
               if(in != null) {
                  try {
                     in.close();
                  } catch (IOException var43) {
                     ;
                  }
               }

               if(p != null) {
                  try {
                     p.destroy();
                  } catch (Exception var42) {
                     ;
                  }
               }

            }

            if(uid != null && UID_PATTERN.matcher(uid).matches()) {
               logger.debug("UID: {}", (Object)uid);
               return "0".equals(uid);
            }
         }

         logger.debug("Could not determine the current UID using /usr/bin/id; attempting to bind at privileged ports.");
         Pattern PERMISSION_DENIED = Pattern.compile(".*(?:denied|not.*permitted).*");
         int i = 1023;

         while(true) {
            if(i > 0) {
               ServerSocket ss = null;

               try {
                  String message;
                  try {
                     ss = new ServerSocket();
                     ss.setReuseAddress(true);
                     ss.bind(new InetSocketAddress(i));
                     if(logger.isDebugEnabled()) {
                        logger.debug("UID: 0 (succeded to bind at port {})", (Object)Integer.valueOf(i));
                     }

                     boolean e = true;
                     return e;
                  } catch (Exception var44) {
                     message = var44.getMessage();
                     if(message == null) {
                        message = "";
                     }
                  }

                  message = message.toLowerCase();
                  if(PERMISSION_DENIED.matcher(message).matches()) {
                     break label492;
                  }
               } finally {
                  if(ss != null) {
                     try {
                        ss.close();
                     } catch (Exception var41) {
                        ;
                     }
                  }

               }

               --i;
               continue;
            }

            logger.debug("UID: non-root (failed to bind at any privileged ports)");
            return false;
         }
      }
   }

   private static int javaVersion0() {
      int javaVersion;
      if(isAndroid()) {
         javaVersion = 6;
      } else {
         try {
            Class.forName("java.time.Clock", false, getClassLoader(Object.class));
            javaVersion = 8;
         } catch (Exception var3) {
            try {
               Class.forName("java.util.concurrent.LinkedTransferQueue", false, getClassLoader(BlockingQueue.class));
               javaVersion = 7;
            } catch (Exception var2) {
               javaVersion = 6;
            }
         }
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Java version: {}", (Object)Integer.valueOf(javaVersion));
      }

      return javaVersion;
   }

   private static boolean hasUnsafe0() {
      boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
      logger.debug("-Dio.netty.noUnsafe: {}", (Object)Boolean.valueOf(noUnsafe));
      if(isAndroid()) {
         logger.debug("sun.misc.Unsafe: unavailable (Android)");
         return false;
      } else if(noUnsafe) {
         logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
         return false;
      } else {
         boolean tryUnsafe;
         if(SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            tryUnsafe = SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true);
         } else {
            tryUnsafe = SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
         }

         if(!tryUnsafe) {
            logger.debug("sun.misc.Unsafe: unavailable (io.netty.tryUnsafe/org.jboss.netty.tryUnsafe)");
            return false;
         } else {
            try {
               boolean hasUnsafe = PlatformDependent0.hasUnsafe();
               logger.debug("sun.misc.Unsafe: {}", (Object)(hasUnsafe?"available":"unavailable"));
               return hasUnsafe;
            } catch (Throwable var3) {
               return false;
            }
         }
      }
   }

   private static long arrayBaseOffset0() {
      return !hasUnsafe()?-1L:PlatformDependent0.arrayBaseOffset();
   }

   private static long maxDirectMemory0() {
      long maxDirectMemory = 0L;

      try {
         Class<?> vmClass = Class.forName("sun.misc.VM", true, getSystemClassLoader());
         Method m = vmClass.getDeclaredMethod("maxDirectMemory", new Class[0]);
         maxDirectMemory = ((Number)m.invoke((Object)null, new Object[0])).longValue();
      } catch (Throwable var8) {
         ;
      }

      if(maxDirectMemory > 0L) {
         return maxDirectMemory;
      } else {
         try {
            Class<?> mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory", true, getSystemClassLoader());
            Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, getSystemClassLoader());
            Object runtime = mgmtFactoryClass.getDeclaredMethod("getRuntimeMXBean", new Class[0]).invoke((Object)null, new Object[0]);
            List<String> vmArgs = (List)runtimeClass.getDeclaredMethod("getInputArguments", new Class[0]).invoke(runtime, new Object[0]);

            label131:
            for(int i = vmArgs.size() - 1; i >= 0; --i) {
               Matcher m = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence)vmArgs.get(i));
               if(m.matches()) {
                  maxDirectMemory = Long.parseLong(m.group(1));
                  switch(m.group(2).charAt(0)) {
                  case 'G':
                  case 'g':
                     maxDirectMemory *= 1073741824L;
                     break label131;
                  case 'K':
                  case 'k':
                     maxDirectMemory *= 1024L;
                     break label131;
                  case 'M':
                  case 'm':
                     maxDirectMemory *= 1048576L;
                  default:
                     break label131;
                  }
               }
            }
         } catch (Throwable var9) {
            ;
         }

         if(maxDirectMemory <= 0L) {
            maxDirectMemory = Runtime.getRuntime().maxMemory();
            logger.debug("maxDirectMemory: {} bytes (maybe)", (Object)Long.valueOf(maxDirectMemory));
         } else {
            logger.debug("maxDirectMemory: {} bytes", (Object)Long.valueOf(maxDirectMemory));
         }

         return maxDirectMemory;
      }
   }

   private static boolean hasJavassist0() {
      if(isAndroid()) {
         return false;
      } else {
         boolean noJavassist = SystemPropertyUtil.getBoolean("io.netty.noJavassist", false);
         logger.debug("-Dio.netty.noJavassist: {}", (Object)Boolean.valueOf(noJavassist));
         if(noJavassist) {
            logger.debug("Javassist: unavailable (io.netty.noJavassist)");
            return false;
         } else {
            try {
               JavassistTypeParameterMatcherGenerator.generate(Object.class, getClassLoader(PlatformDependent.class));
               logger.debug("Javassist: available");
               return true;
            } catch (Throwable var2) {
               logger.debug("Javassist: unavailable");
               logger.debug("You don\'t have Javassist in your class path or you don\'t have enough permission to load dynamically generated classes.  Please check the configuration for better performance.");
               return false;
            }
         }
      }
   }

   private static File tmpdir0() {
      try {
         File f = toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
         if(f != null) {
            logger.debug("-Dio.netty.tmpdir: {}", (Object)f);
            return f;
         }

         f = toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
         if(f != null) {
            logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", (Object)f);
            return f;
         }

         if(isWindows()) {
            f = toDirectory(System.getenv("TEMP"));
            if(f != null) {
               logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", (Object)f);
               return f;
            }

            String userprofile = System.getenv("USERPROFILE");
            if(userprofile != null) {
               f = toDirectory(userprofile + "\\AppData\\Local\\Temp");
               if(f != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", (Object)f);
                  return f;
               }

               f = toDirectory(userprofile + "\\Local Settings\\Temp");
               if(f != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", (Object)f);
                  return f;
               }
            }
         } else {
            f = toDirectory(System.getenv("TMPDIR"));
            if(f != null) {
               logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", (Object)f);
               return f;
            }
         }
      } catch (Exception var2) {
         ;
      }

      File f;
      if(isWindows()) {
         f = new File("C:\\Windows\\Temp");
      } else {
         f = new File("/tmp");
      }

      logger.warn("Failed to get the temporary directory; falling back to: {}", (Object)f);
      return f;
   }

   private static File toDirectory(String path) {
      if(path == null) {
         return null;
      } else {
         File f = new File(path);
         f.mkdirs();
         if(!f.isDirectory()) {
            return null;
         } else {
            try {
               return f.getAbsoluteFile();
            } catch (Exception var3) {
               return f;
            }
         }
      }
   }

   private static int bitMode0() {
      int bitMode = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
      if(bitMode > 0) {
         logger.debug("-Dio.netty.bitMode: {}", (Object)Integer.valueOf(bitMode));
         return bitMode;
      } else {
         bitMode = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
         if(bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)Integer.valueOf(bitMode));
            return bitMode;
         } else {
            bitMode = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
            if(bitMode > 0) {
               logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)Integer.valueOf(bitMode));
               return bitMode;
            } else {
               String arch = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
               if(!"amd64".equals(arch) && !"x86_64".equals(arch)) {
                  if("i386".equals(arch) || "i486".equals(arch) || "i586".equals(arch) || "i686".equals(arch)) {
                     bitMode = 32;
                  }
               } else {
                  bitMode = 64;
               }

               if(bitMode > 0) {
                  logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", Integer.valueOf(bitMode), arch);
               }

               String vm = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
               Pattern BIT_PATTERN = Pattern.compile("([1-9][0-9]+)-?bit");
               Matcher m = BIT_PATTERN.matcher(vm);
               return m.find()?Integer.parseInt(m.group(1)):64;
            }
         }
      }
   }

   private static int addressSize0() {
      return !hasUnsafe()?-1:PlatformDependent0.addressSize();
   }

   static {
      if(logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noPreferDirect: {}", (Object)Boolean.valueOf(!DIRECT_BUFFER_PREFERRED));
      }

      if(!hasUnsafe() && !isAndroid()) {
         logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system unstability.");
      }

   }
}
