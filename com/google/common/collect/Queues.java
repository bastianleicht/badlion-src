package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Synchronized;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public final class Queues {
   public static ArrayBlockingQueue newArrayBlockingQueue(int capacity) {
      return new ArrayBlockingQueue(capacity);
   }

   public static ArrayDeque newArrayDeque() {
      return new ArrayDeque();
   }

   public static ArrayDeque newArrayDeque(Iterable elements) {
      if(elements instanceof Collection) {
         return new ArrayDeque(Collections2.cast(elements));
      } else {
         ArrayDeque<E> deque = new ArrayDeque();
         Iterables.addAll(deque, elements);
         return deque;
      }
   }

   public static ConcurrentLinkedQueue newConcurrentLinkedQueue() {
      return new ConcurrentLinkedQueue();
   }

   public static ConcurrentLinkedQueue newConcurrentLinkedQueue(Iterable elements) {
      if(elements instanceof Collection) {
         return new ConcurrentLinkedQueue(Collections2.cast(elements));
      } else {
         ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue();
         Iterables.addAll(queue, elements);
         return queue;
      }
   }

   public static LinkedBlockingDeque newLinkedBlockingDeque() {
      return new LinkedBlockingDeque();
   }

   public static LinkedBlockingDeque newLinkedBlockingDeque(int capacity) {
      return new LinkedBlockingDeque(capacity);
   }

   public static LinkedBlockingDeque newLinkedBlockingDeque(Iterable elements) {
      if(elements instanceof Collection) {
         return new LinkedBlockingDeque(Collections2.cast(elements));
      } else {
         LinkedBlockingDeque<E> deque = new LinkedBlockingDeque();
         Iterables.addAll(deque, elements);
         return deque;
      }
   }

   public static LinkedBlockingQueue newLinkedBlockingQueue() {
      return new LinkedBlockingQueue();
   }

   public static LinkedBlockingQueue newLinkedBlockingQueue(int capacity) {
      return new LinkedBlockingQueue(capacity);
   }

   public static LinkedBlockingQueue newLinkedBlockingQueue(Iterable elements) {
      if(elements instanceof Collection) {
         return new LinkedBlockingQueue(Collections2.cast(elements));
      } else {
         LinkedBlockingQueue<E> queue = new LinkedBlockingQueue();
         Iterables.addAll(queue, elements);
         return queue;
      }
   }

   public static PriorityBlockingQueue newPriorityBlockingQueue() {
      return new PriorityBlockingQueue();
   }

   public static PriorityBlockingQueue newPriorityBlockingQueue(Iterable elements) {
      if(elements instanceof Collection) {
         return new PriorityBlockingQueue(Collections2.cast(elements));
      } else {
         PriorityBlockingQueue<E> queue = new PriorityBlockingQueue();
         Iterables.addAll(queue, elements);
         return queue;
      }
   }

   public static PriorityQueue newPriorityQueue() {
      return new PriorityQueue();
   }

   public static PriorityQueue newPriorityQueue(Iterable elements) {
      if(elements instanceof Collection) {
         return new PriorityQueue(Collections2.cast(elements));
      } else {
         PriorityQueue<E> queue = new PriorityQueue();
         Iterables.addAll(queue, elements);
         return queue;
      }
   }

   public static SynchronousQueue newSynchronousQueue() {
      return new SynchronousQueue();
   }

   @Beta
   public static int drain(BlockingQueue q, Collection buffer, int numElements, long timeout, TimeUnit unit) throws InterruptedException {
      Preconditions.checkNotNull(buffer);
      long deadline = System.nanoTime() + unit.toNanos(timeout);
      int added = 0;

      while(added < numElements) {
         added += q.drainTo(buffer, numElements - added);
         if(added < numElements) {
            E e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
            if(e == null) {
               break;
            }

            buffer.add(e);
            ++added;
         }
      }

      return added;
   }

   @Beta
   public static int drainUninterruptibly(BlockingQueue q, Collection buffer, int numElements, long timeout, TimeUnit unit) {
      Preconditions.checkNotNull(buffer);
      long deadline = System.nanoTime() + unit.toNanos(timeout);
      int added = 0;
      boolean interrupted = false;

      try {
         while(added < numElements) {
            added += q.drainTo(buffer, numElements - added);
            if(added < numElements) {
               E e;
               while(true) {
                  try {
                     e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                     break;
                  } catch (InterruptedException var15) {
                     interrupted = true;
                  }
               }

               if(e == null) {
                  break;
               }

               buffer.add(e);
               ++added;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }

      return added;
   }

   @Beta
   public static Queue synchronizedQueue(Queue queue) {
      return Synchronized.queue(queue, (Object)null);
   }

   @Beta
   public static Deque synchronizedDeque(Deque deque) {
      return Synchronized.deque(deque, (Object)null);
   }
}
