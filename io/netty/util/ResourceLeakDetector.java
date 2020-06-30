package io.netty.util;

import io.netty.util.ResourceLeak;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ResourceLeakDetector {
   private static final String PROP_LEVEL = "io.netty.leakDetectionLevel";
   private static final ResourceLeakDetector.Level DEFAULT_LEVEL = ResourceLeakDetector.Level.SIMPLE;
   private static ResourceLeakDetector.Level level;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
   private static final int DEFAULT_SAMPLING_INTERVAL = 113;
   private final ResourceLeakDetector.DefaultResourceLeak head;
   private final ResourceLeakDetector.DefaultResourceLeak tail;
   private final ReferenceQueue refQueue;
   private final ConcurrentMap reportedLeaks;
   private final String resourceType;
   private final int samplingInterval;
   private final long maxActive;
   private long active;
   private final AtomicBoolean loggedTooManyActive;
   private long leakCheckCnt;
   private static final String[] STACK_TRACE_ELEMENT_EXCLUSIONS;

   /** @deprecated */
   @Deprecated
   public static void setEnabled(boolean enabled) {
      setLevel(enabled?ResourceLeakDetector.Level.SIMPLE:ResourceLeakDetector.Level.DISABLED);
   }

   public static boolean isEnabled() {
      return getLevel().ordinal() > ResourceLeakDetector.Level.DISABLED.ordinal();
   }

   public static void setLevel(ResourceLeakDetector.Level level) {
      if(level == null) {
         throw new NullPointerException("level");
      } else {
         level = level;
      }
   }

   public static ResourceLeakDetector.Level getLevel() {
      return level;
   }

   public ResourceLeakDetector(Class resourceType) {
      this(StringUtil.simpleClassName(resourceType));
   }

   public ResourceLeakDetector(String resourceType) {
      this((String)resourceType, 113, Long.MAX_VALUE);
   }

   public ResourceLeakDetector(Class resourceType, int samplingInterval, long maxActive) {
      this(StringUtil.simpleClassName(resourceType), samplingInterval, maxActive);
   }

   public ResourceLeakDetector(String resourceType, int samplingInterval, long maxActive) {
      this.head = new ResourceLeakDetector.DefaultResourceLeak((Object)null);
      this.tail = new ResourceLeakDetector.DefaultResourceLeak((Object)null);
      this.refQueue = new ReferenceQueue();
      this.reportedLeaks = PlatformDependent.newConcurrentHashMap();
      this.loggedTooManyActive = new AtomicBoolean();
      if(resourceType == null) {
         throw new NullPointerException("resourceType");
      } else if(samplingInterval <= 0) {
         throw new IllegalArgumentException("samplingInterval: " + samplingInterval + " (expected: 1+)");
      } else if(maxActive <= 0L) {
         throw new IllegalArgumentException("maxActive: " + maxActive + " (expected: 1+)");
      } else {
         this.resourceType = resourceType;
         this.samplingInterval = samplingInterval;
         this.maxActive = maxActive;
         this.head.next = this.tail;
         this.tail.prev = this.head;
      }
   }

   public ResourceLeak open(Object obj) {
      ResourceLeakDetector.Level level = level;
      if(level == ResourceLeakDetector.Level.DISABLED) {
         return null;
      } else if(level.ordinal() < ResourceLeakDetector.Level.PARANOID.ordinal()) {
         if(this.leakCheckCnt++ % (long)this.samplingInterval == 0L) {
            this.reportLeak(level);
            return new ResourceLeakDetector.DefaultResourceLeak(obj);
         } else {
            return null;
         }
      } else {
         this.reportLeak(level);
         return new ResourceLeakDetector.DefaultResourceLeak(obj);
      }
   }

   private void reportLeak(ResourceLeakDetector.Level level) {
      if(logger.isErrorEnabled()) {
         int samplingInterval = level == ResourceLeakDetector.Level.PARANOID?1:this.samplingInterval;
         if(this.active * (long)samplingInterval > this.maxActive && this.loggedTooManyActive.compareAndSet(false, true)) {
            logger.error("LEAK: You are creating too many " + this.resourceType + " instances.  " + this.resourceType + " is a shared resource that must be reused across the JVM," + "so that only a few instances are created.");
         }

         while(true) {
            ResourceLeakDetector<T>.DefaultResourceLeak ref = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
            if(ref == null) {
               return;
            }

            ref.clear();
            if(ref.close()) {
               String records = ref.toString();
               if(this.reportedLeaks.putIfAbsent(records, Boolean.TRUE) == null) {
                  if(records.isEmpty()) {
                     logger.error("LEAK: {}.release() was not called before it\'s garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option \'-D{}={}\' or call {}.setLevel()", new Object[]{this.resourceType, "io.netty.leakDetectionLevel", ResourceLeakDetector.Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName((Object)this)});
                  } else {
                     logger.error("LEAK: {}.release() was not called before it\'s garbage-collected.{}", this.resourceType, records);
                  }
               }
            }
         }
      } else {
         while(true) {
            ResourceLeakDetector<T>.DefaultResourceLeak ref = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
            if(ref == null) {
               return;
            }

            ref.close();
         }
      }
   }

   static String newRecord(int recordsToSkip) {
      StringBuilder buf = new StringBuilder(4096);
      StackTraceElement[] array = (new Throwable()).getStackTrace();

      for(StackTraceElement e : array) {
         if(recordsToSkip > 0) {
            --recordsToSkip;
         } else {
            String estr = e.toString();
            boolean excluded = false;

            for(String exclusion : STACK_TRACE_ELEMENT_EXCLUSIONS) {
               if(estr.startsWith(exclusion)) {
                  excluded = true;
                  break;
               }
            }

            if(!excluded) {
               buf.append('\t');
               buf.append(estr);
               buf.append(StringUtil.NEWLINE);
            }
         }
      }

      return buf.toString();
   }

   static {
      boolean disabled;
      if(SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
         disabled = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
         logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object)Boolean.valueOf(disabled));
         logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use \'-D{}={}\' instead.", "io.netty.leakDetectionLevel", DEFAULT_LEVEL.name().toLowerCase());
      } else {
         disabled = false;
      }

      ResourceLeakDetector.Level defaultLevel = disabled?ResourceLeakDetector.Level.DISABLED:DEFAULT_LEVEL;
      String levelStr = SystemPropertyUtil.get("io.netty.leakDetectionLevel", defaultLevel.name()).trim().toUpperCase();
      ResourceLeakDetector.Level level = DEFAULT_LEVEL;

      for(ResourceLeakDetector.Level l : EnumSet.allOf(ResourceLeakDetector.Level.class)) {
         if(levelStr.equals(l.name()) || levelStr.equals(String.valueOf(l.ordinal()))) {
            level = l;
         }
      }

      level = level;
      if(logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.leakDetectionLevel", level.name().toLowerCase());
      }

      STACK_TRACE_ELEMENT_EXCLUSIONS = new String[]{"io.netty.buffer.AbstractByteBufAllocator.toLeakAwareBuffer("};
   }

   private final class DefaultResourceLeak extends PhantomReference implements ResourceLeak {
      private static final int MAX_RECORDS = 4;
      private final String creationRecord;
      private final Deque lastRecords = new ArrayDeque();
      private final AtomicBoolean freed;
      private ResourceLeakDetector.DefaultResourceLeak prev;
      private ResourceLeakDetector.DefaultResourceLeak next;

      DefaultResourceLeak(Object referent) {
         super(referent, referent != null?ResourceLeakDetector.this.refQueue:null);
         if(referent != null) {
            ResourceLeakDetector.Level level = ResourceLeakDetector.level;
            if(level.ordinal() >= ResourceLeakDetector.Level.ADVANCED.ordinal()) {
               this.creationRecord = ResourceLeakDetector.newRecord(3);
            } else {
               this.creationRecord = null;
            }

            synchronized(ResourceLeakDetector.this.head) {
               this.prev = ResourceLeakDetector.this.head;
               this.next = ResourceLeakDetector.this.head.next;
               ResourceLeakDetector.this.head.next.prev = this;
               ResourceLeakDetector.this.head.next = this;
               ResourceLeakDetector.this.active++;
            }

            this.freed = new AtomicBoolean();
         } else {
            this.creationRecord = null;
            this.freed = new AtomicBoolean(true);
         }

      }

      public void record() {
         if(this.creationRecord != null) {
            String value = ResourceLeakDetector.newRecord(2);
            synchronized(this.lastRecords) {
               int size = this.lastRecords.size();
               if(size == 0 || !((String)this.lastRecords.getLast()).equals(value)) {
                  this.lastRecords.add(value);
               }

               if(size > 4) {
                  this.lastRecords.removeFirst();
               }
            }
         }

      }

      public boolean close() {
         if(this.freed.compareAndSet(false, true)) {
            synchronized(ResourceLeakDetector.this.head) {
               ResourceLeakDetector.this.active--;
               this.prev.next = this.next;
               this.next.prev = this.prev;
               this.prev = null;
               this.next = null;
               return true;
            }
         } else {
            return false;
         }
      }

      public String toString() {
         if(this.creationRecord == null) {
            return "";
         } else {
            Object[] array;
            synchronized(this.lastRecords) {
               array = this.lastRecords.toArray();
            }

            StringBuilder buf = new StringBuilder(16384);
            buf.append(StringUtil.NEWLINE);
            buf.append("Recent access records: ");
            buf.append(array.length);
            buf.append(StringUtil.NEWLINE);
            if(array.length > 0) {
               for(int i = array.length - 1; i >= 0; --i) {
                  buf.append('#');
                  buf.append(i + 1);
                  buf.append(':');
                  buf.append(StringUtil.NEWLINE);
                  buf.append(array[i]);
               }
            }

            buf.append("Created at:");
            buf.append(StringUtil.NEWLINE);
            buf.append(this.creationRecord);
            buf.setLength(buf.length() - StringUtil.NEWLINE.length());
            return buf.toString();
         }
      }
   }

   public static enum Level {
      DISABLED,
      SIMPLE,
      ADVANCED,
      PARANOID;
   }
}
