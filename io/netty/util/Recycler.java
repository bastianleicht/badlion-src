package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
   private static final AtomicInteger ID_GENERATOR = new AtomicInteger(Integer.MIN_VALUE);
   private static final int OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
   private static final int DEFAULT_MAX_CAPACITY;
   private static final int INITIAL_CAPACITY;
   private final int maxCapacity;
   private final FastThreadLocal threadLocal;
   private static final FastThreadLocal DELAYED_RECYCLED;

   protected Recycler() {
      this(DEFAULT_MAX_CAPACITY);
   }

   protected Recycler(int maxCapacity) {
      this.threadLocal = new FastThreadLocal() {
         protected Recycler.Stack initialValue() {
            return new Recycler.Stack(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacity);
         }
      };
      this.maxCapacity = Math.max(0, maxCapacity);
   }

   public final Object get() {
      Recycler.Stack<T> stack = (Recycler.Stack)this.threadLocal.get();
      Recycler.DefaultHandle handle = stack.pop();
      if(handle == null) {
         handle = stack.newHandle();
         handle.value = this.newObject(handle);
      }

      return handle.value;
   }

   public final boolean recycle(Object o, Recycler.Handle handle) {
      Recycler.DefaultHandle h = (Recycler.DefaultHandle)handle;
      if(h.stack.parent != this) {
         return false;
      } else if(o != h.value) {
         throw new IllegalArgumentException("o does not belong to handle");
      } else {
         h.recycle();
         return true;
      }
   }

   protected abstract Object newObject(Recycler.Handle var1);

   static {
      int maxCapacity = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity.default", 0);
      if(maxCapacity <= 0) {
         maxCapacity = 262144;
      }

      DEFAULT_MAX_CAPACITY = maxCapacity;
      if(logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.recycler.maxCapacity.default: {}", (Object)Integer.valueOf(DEFAULT_MAX_CAPACITY));
      }

      INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY, 256);
      DELAYED_RECYCLED = new FastThreadLocal() {
         protected Map initialValue() {
            return new WeakHashMap();
         }
      };
   }

   static final class DefaultHandle implements Recycler.Handle {
      private int lastRecycledId;
      private int recycleId;
      private Recycler.Stack stack;
      private Object value;

      DefaultHandle(Recycler.Stack stack) {
         this.stack = stack;
      }

      public void recycle() {
         Thread thread = Thread.currentThread();
         if(thread == this.stack.thread) {
            this.stack.push(this);
         } else {
            Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> delayedRecycled = (Map)Recycler.DELAYED_RECYCLED.get();
            Recycler.WeakOrderQueue queue = (Recycler.WeakOrderQueue)delayedRecycled.get(this.stack);
            if(queue == null) {
               delayedRecycled.put(this.stack, queue = new Recycler.WeakOrderQueue(this.stack, thread));
            }

            queue.add(this);
         }
      }
   }

   public interface Handle {
   }

   static final class Stack {
      final Recycler parent;
      final Thread thread;
      private Recycler.DefaultHandle[] elements;
      private final int maxCapacity;
      private int size;
      private volatile Recycler.WeakOrderQueue head;
      private Recycler.WeakOrderQueue cursor;
      private Recycler.WeakOrderQueue prev;

      Stack(Recycler parent, Thread thread, int maxCapacity) {
         this.parent = parent;
         this.thread = thread;
         this.maxCapacity = maxCapacity;
         this.elements = new Recycler.DefaultHandle[Recycler.INITIAL_CAPACITY];
      }

      Recycler.DefaultHandle pop() {
         int size = this.size;
         if(size == 0) {
            if(!this.scavenge()) {
               return null;
            }

            size = this.size;
         }

         --size;
         Recycler.DefaultHandle ret = this.elements[size];
         if(ret.lastRecycledId != ret.recycleId) {
            throw new IllegalStateException("recycled multiple times");
         } else {
            ret.recycleId = 0;
            ret.lastRecycledId = 0;
            this.size = size;
            return ret;
         }
      }

      boolean scavenge() {
         if(this.scavengeSome()) {
            return true;
         } else {
            this.prev = null;
            this.cursor = this.head;
            return false;
         }
      }

      boolean scavengeSome() {
         boolean success = false;
         Recycler.WeakOrderQueue cursor = this.cursor;

         Recycler.WeakOrderQueue prev;
         Recycler.WeakOrderQueue next;
         for(prev = this.prev; cursor != null; cursor = next) {
            if(cursor.transfer(this)) {
               success = true;
               break;
            }

            next = cursor.next;
            if(cursor.owner.get() == null) {
               if(cursor.hasFinalData()) {
                  while(cursor.transfer(this)) {
                     ;
                  }
               }

               if(prev != null) {
                  prev.next = next;
               }
            } else {
               prev = cursor;
            }
         }

         this.prev = prev;
         this.cursor = cursor;
         return success;
      }

      void push(Recycler.DefaultHandle item) {
         if((item.recycleId | item.lastRecycledId) != 0) {
            throw new IllegalStateException("recycled already");
         } else {
            item.recycleId = item.lastRecycledId = Recycler.OWN_THREAD_ID;
            int size = this.size;
            if(size == this.elements.length) {
               if(size == this.maxCapacity) {
                  return;
               }

               this.elements = (Recycler.DefaultHandle[])Arrays.copyOf(this.elements, size << 1);
            }

            this.elements[size] = item;
            this.size = size + 1;
         }
      }

      Recycler.DefaultHandle newHandle() {
         return new Recycler.DefaultHandle(this);
      }
   }

   private static final class WeakOrderQueue {
      private static final int LINK_CAPACITY = 16;
      private Recycler.WeakOrderQueue.Link head;
      private Recycler.WeakOrderQueue.Link tail;
      private Recycler.WeakOrderQueue next;
      private final WeakReference owner;
      private final int id = Recycler.ID_GENERATOR.getAndIncrement();

      WeakOrderQueue(Recycler.Stack stack, Thread thread) {
         this.head = this.tail = new Recycler.WeakOrderQueue.Link();
         this.owner = new WeakReference(thread);
         synchronized(stack) {
            this.next = stack.head;
            stack.head = this;
         }
      }

      void add(Recycler.DefaultHandle handle) {
         handle.lastRecycledId = this.id;
         Recycler.WeakOrderQueue.Link tail = this.tail;
         int writeIndex;
         if((writeIndex = tail.get()) == 16) {
            this.tail = tail = tail.next = new Recycler.WeakOrderQueue.Link();
            writeIndex = tail.get();
         }

         tail.elements[writeIndex] = handle;
         handle.stack = null;
         tail.lazySet(writeIndex + 1);
      }

      boolean hasFinalData() {
         return this.tail.readIndex != this.tail.get();
      }

      boolean transfer(Recycler.Stack to) {
         Recycler.WeakOrderQueue.Link head = this.head;
         if(head == null) {
            return false;
         } else {
            if(head.readIndex == 16) {
               if(head.next == null) {
                  return false;
               }

               this.head = head = head.next;
            }

            int start = head.readIndex;
            int end = head.get();
            if(start == end) {
               return false;
            } else {
               int count = end - start;
               if(to.size + count > to.elements.length) {
                  to.elements = (Recycler.DefaultHandle[])Arrays.copyOf(to.elements, (to.size + count) * 2);
               }

               Recycler.DefaultHandle[] src = head.elements;
               Recycler.DefaultHandle[] trg = to.elements;

               int size;
               for(size = to.size; start < end; src[start++] = null) {
                  Recycler.DefaultHandle element = src[start];
                  if(element.recycleId == 0) {
                     element.recycleId = element.lastRecycledId;
                  } else if(element.recycleId != element.lastRecycledId) {
                     throw new IllegalStateException("recycled already");
                  }

                  element.stack = to;
                  trg[size++] = element;
               }

               to.size = size;
               if(end == 16 && head.next != null) {
                  this.head = head.next;
               }

               head.readIndex = end;
               return true;
            }
         }
      }

      private static final class Link extends AtomicInteger {
         private final Recycler.DefaultHandle[] elements;
         private int readIndex;
         private Recycler.WeakOrderQueue.Link next;

         private Link() {
            this.elements = new Recycler.DefaultHandle[16];
         }
      }
   }
}
