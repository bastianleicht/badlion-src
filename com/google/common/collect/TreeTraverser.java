package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

@Beta
@GwtCompatible(
   emulated = true
)
public abstract class TreeTraverser {
   public abstract Iterable children(Object var1);

   public final FluentIterable preOrderTraversal(final Object root) {
      Preconditions.checkNotNull(root);
      return new FluentIterable() {
         public UnmodifiableIterator iterator() {
            return TreeTraverser.this.preOrderIterator(root);
         }
      };
   }

   UnmodifiableIterator preOrderIterator(Object root) {
      return new TreeTraverser.PreOrderIterator(root);
   }

   public final FluentIterable postOrderTraversal(final Object root) {
      Preconditions.checkNotNull(root);
      return new FluentIterable() {
         public UnmodifiableIterator iterator() {
            return TreeTraverser.this.postOrderIterator(root);
         }
      };
   }

   UnmodifiableIterator postOrderIterator(Object root) {
      return new TreeTraverser.PostOrderIterator(root);
   }

   public final FluentIterable breadthFirstTraversal(final Object root) {
      Preconditions.checkNotNull(root);
      return new FluentIterable() {
         public UnmodifiableIterator iterator() {
            return TreeTraverser.this.new BreadthFirstIterator(root);
         }
      };
   }

   private final class BreadthFirstIterator extends UnmodifiableIterator implements PeekingIterator {
      private final Queue queue = new ArrayDeque();

      BreadthFirstIterator(Object root) {
         this.queue.add(root);
      }

      public boolean hasNext() {
         return !this.queue.isEmpty();
      }

      public Object peek() {
         return this.queue.element();
      }

      public Object next() {
         T result = this.queue.remove();
         Iterables.addAll(this.queue, TreeTraverser.this.children(result));
         return result;
      }
   }

   private final class PostOrderIterator extends AbstractIterator {
      private final ArrayDeque stack = new ArrayDeque();

      PostOrderIterator(Object root) {
         this.stack.addLast(this.expand(root));
      }

      protected Object computeNext() {
         while(true) {
            if(!this.stack.isEmpty()) {
               TreeTraverser.PostOrderNode<T> top = (TreeTraverser.PostOrderNode)this.stack.getLast();
               if(top.childIterator.hasNext()) {
                  T child = top.childIterator.next();
                  this.stack.addLast(this.expand(child));
                  continue;
               }

               this.stack.removeLast();
               return top.root;
            }

            return this.endOfData();
         }
      }

      private TreeTraverser.PostOrderNode expand(Object t) {
         return new TreeTraverser.PostOrderNode(t, TreeTraverser.this.children(t).iterator());
      }
   }

   private static final class PostOrderNode {
      final Object root;
      final Iterator childIterator;

      PostOrderNode(Object root, Iterator childIterator) {
         this.root = Preconditions.checkNotNull(root);
         this.childIterator = (Iterator)Preconditions.checkNotNull(childIterator);
      }
   }

   private final class PreOrderIterator extends UnmodifiableIterator {
      private final Deque stack = new ArrayDeque();

      PreOrderIterator(Object root) {
         this.stack.addLast(Iterators.singletonIterator(Preconditions.checkNotNull(root)));
      }

      public boolean hasNext() {
         return !this.stack.isEmpty();
      }

      public Object next() {
         Iterator<T> itr = (Iterator)this.stack.getLast();
         T result = Preconditions.checkNotNull(itr.next());
         if(!itr.hasNext()) {
            this.stack.removeLast();
         }

         Iterator<T> childItr = TreeTraverser.this.children(result).iterator();
         if(childItr.hasNext()) {
            this.stack.addLast(childItr);
         }

         return result;
      }
   }
}
