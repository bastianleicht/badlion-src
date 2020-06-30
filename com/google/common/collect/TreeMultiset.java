package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractSortedMultiset;
import com.google.common.collect.BoundType;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.GeneralRange;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Serialization;
import com.google.common.collect.SortedMultiset;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class TreeMultiset extends AbstractSortedMultiset implements Serializable {
   private final transient TreeMultiset.Reference rootReference;
   private final transient GeneralRange range;
   private final transient TreeMultiset.AvlNode header;
   @GwtIncompatible("not needed in emulated source")
   private static final long serialVersionUID = 1L;

   public static TreeMultiset create() {
      return new TreeMultiset(Ordering.natural());
   }

   public static TreeMultiset create(@Nullable Comparator comparator) {
      return comparator == null?new TreeMultiset(Ordering.natural()):new TreeMultiset(comparator);
   }

   public static TreeMultiset create(Iterable elements) {
      TreeMultiset<E> multiset = create();
      Iterables.addAll(multiset, elements);
      return multiset;
   }

   TreeMultiset(TreeMultiset.Reference rootReference, GeneralRange range, TreeMultiset.AvlNode endLink) {
      super(range.comparator());
      this.rootReference = rootReference;
      this.range = range;
      this.header = endLink;
   }

   TreeMultiset(Comparator comparator) {
      super(comparator);
      this.range = GeneralRange.all(comparator);
      this.header = new TreeMultiset.AvlNode((Object)null, 1);
      successor(this.header, this.header);
      this.rootReference = new TreeMultiset.Reference();
   }

   private long aggregateForEntries(TreeMultiset.Aggregate aggr) {
      TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
      long total = aggr.treeAggregate(root);
      if(this.range.hasLowerBound()) {
         total -= this.aggregateBelowRange(aggr, root);
      }

      if(this.range.hasUpperBound()) {
         total -= this.aggregateAboveRange(aggr, root);
      }

      return total;
   }

   private long aggregateBelowRange(TreeMultiset.Aggregate aggr, @Nullable TreeMultiset.AvlNode node) {
      if(node == null) {
         return 0L;
      } else {
         int cmp = this.comparator().compare(this.range.getLowerEndpoint(), node.elem);
         if(cmp < 0) {
            return this.aggregateBelowRange(aggr, node.left);
         } else if(cmp == 0) {
            switch(this.range.getLowerBoundType()) {
            case OPEN:
               return (long)aggr.nodeAggregate(node) + aggr.treeAggregate(node.left);
            case CLOSED:
               return aggr.treeAggregate(node.left);
            default:
               throw new AssertionError();
            }
         } else {
            return aggr.treeAggregate(node.left) + (long)aggr.nodeAggregate(node) + this.aggregateBelowRange(aggr, node.right);
         }
      }
   }

   private long aggregateAboveRange(TreeMultiset.Aggregate aggr, @Nullable TreeMultiset.AvlNode node) {
      if(node == null) {
         return 0L;
      } else {
         int cmp = this.comparator().compare(this.range.getUpperEndpoint(), node.elem);
         if(cmp > 0) {
            return this.aggregateAboveRange(aggr, node.right);
         } else if(cmp == 0) {
            switch(this.range.getUpperBoundType()) {
            case OPEN:
               return (long)aggr.nodeAggregate(node) + aggr.treeAggregate(node.right);
            case CLOSED:
               return aggr.treeAggregate(node.right);
            default:
               throw new AssertionError();
            }
         } else {
            return aggr.treeAggregate(node.right) + (long)aggr.nodeAggregate(node) + this.aggregateAboveRange(aggr, node.left);
         }
      }
   }

   public int size() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.SIZE));
   }

   int distinctElements() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.DISTINCT));
   }

   public int count(@Nullable Object element) {
      try {
         TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
         return this.range.contains(element) && root != null?root.count(this.comparator(), element):0;
      } catch (ClassCastException var4) {
         return 0;
      } catch (NullPointerException var5) {
         return 0;
      }
   }

   public int add(@Nullable Object element, int occurrences) {
      CollectPreconditions.checkNonnegative(occurrences, "occurrences");
      if(occurrences == 0) {
         return this.count(element);
      } else {
         Preconditions.checkArgument(this.range.contains(element));
         TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
         if(root == null) {
            this.comparator().compare(element, element);
            TreeMultiset.AvlNode<E> newRoot = new TreeMultiset.AvlNode(element, occurrences);
            successor(this.header, newRoot, this.header);
            this.rootReference.checkAndSet(root, newRoot);
            return 0;
         } else {
            int[] result = new int[1];
            TreeMultiset.AvlNode<E> newRoot = root.add(this.comparator(), element, occurrences, result);
            this.rootReference.checkAndSet(root, newRoot);
            return result[0];
         }
      }
   }

   public int remove(@Nullable Object element, int occurrences) {
      CollectPreconditions.checkNonnegative(occurrences, "occurrences");
      if(occurrences == 0) {
         return this.count(element);
      } else {
         TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
         int[] result = new int[1];

         TreeMultiset.AvlNode<E> newRoot;
         try {
            if(!this.range.contains(element) || root == null) {
               return 0;
            }

            newRoot = root.remove(this.comparator(), element, occurrences, result);
         } catch (ClassCastException var7) {
            return 0;
         } catch (NullPointerException var8) {
            return 0;
         }

         this.rootReference.checkAndSet(root, newRoot);
         return result[0];
      }
   }

   public int setCount(@Nullable Object element, int count) {
      CollectPreconditions.checkNonnegative(count, "count");
      if(!this.range.contains(element)) {
         Preconditions.checkArgument(count == 0);
         return 0;
      } else {
         TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
         if(root == null) {
            if(count > 0) {
               this.add(element, count);
            }

            return 0;
         } else {
            int[] result = new int[1];
            TreeMultiset.AvlNode<E> newRoot = root.setCount(this.comparator(), element, count, result);
            this.rootReference.checkAndSet(root, newRoot);
            return result[0];
         }
      }
   }

   public boolean setCount(@Nullable Object element, int oldCount, int newCount) {
      CollectPreconditions.checkNonnegative(newCount, "newCount");
      CollectPreconditions.checkNonnegative(oldCount, "oldCount");
      Preconditions.checkArgument(this.range.contains(element));
      TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
      if(root == null) {
         if(oldCount == 0) {
            if(newCount > 0) {
               this.add(element, newCount);
            }

            return true;
         } else {
            return false;
         }
      } else {
         int[] result = new int[1];
         TreeMultiset.AvlNode<E> newRoot = root.setCount(this.comparator(), element, oldCount, newCount, result);
         this.rootReference.checkAndSet(root, newRoot);
         return result[0] == oldCount;
      }
   }

   private Multiset.Entry wrapEntry(final TreeMultiset.AvlNode baseEntry) {
      return new Multisets.AbstractEntry() {
         public Object getElement() {
            return baseEntry.getElement();
         }

         public int getCount() {
            int result = baseEntry.getCount();
            return result == 0?TreeMultiset.this.count(this.getElement()):result;
         }
      };
   }

   @Nullable
   private TreeMultiset.AvlNode firstNode() {
      TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
      if(root == null) {
         return null;
      } else {
         TreeMultiset.AvlNode<E> node;
         if(this.range.hasLowerBound()) {
            E endpoint = this.range.getLowerEndpoint();
            node = ((TreeMultiset.AvlNode)this.rootReference.get()).ceiling(this.comparator(), endpoint);
            if(node == null) {
               return null;
            }

            if(this.range.getLowerBoundType() == BoundType.OPEN && this.comparator().compare(endpoint, node.getElement()) == 0) {
               node = node.succ;
            }
         } else {
            node = this.header.succ;
         }

         return node != this.header && this.range.contains(node.getElement())?node:null;
      }
   }

   @Nullable
   private TreeMultiset.AvlNode lastNode() {
      TreeMultiset.AvlNode<E> root = (TreeMultiset.AvlNode)this.rootReference.get();
      if(root == null) {
         return null;
      } else {
         TreeMultiset.AvlNode<E> node;
         if(this.range.hasUpperBound()) {
            E endpoint = this.range.getUpperEndpoint();
            node = ((TreeMultiset.AvlNode)this.rootReference.get()).floor(this.comparator(), endpoint);
            if(node == null) {
               return null;
            }

            if(this.range.getUpperBoundType() == BoundType.OPEN && this.comparator().compare(endpoint, node.getElement()) == 0) {
               node = node.pred;
            }
         } else {
            node = this.header.pred;
         }

         return node != this.header && this.range.contains(node.getElement())?node:null;
      }
   }

   Iterator entryIterator() {
      return new Iterator() {
         TreeMultiset.AvlNode current = TreeMultiset.this.firstNode();
         Multiset.Entry prevEntry;

         public boolean hasNext() {
            if(this.current == null) {
               return false;
            } else if(TreeMultiset.this.range.tooHigh(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Multiset.Entry<E> result = TreeMultiset.this.wrapEntry(this.current);
               this.prevEntry = result;
               if(this.current.succ == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.succ;
               }

               return result;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.prevEntry != null);
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   Iterator descendingEntryIterator() {
      return new Iterator() {
         TreeMultiset.AvlNode current = TreeMultiset.this.lastNode();
         Multiset.Entry prevEntry = null;

         public boolean hasNext() {
            if(this.current == null) {
               return false;
            } else if(TreeMultiset.this.range.tooLow(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Multiset.Entry<E> result = TreeMultiset.this.wrapEntry(this.current);
               this.prevEntry = result;
               if(this.current.pred == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.pred;
               }

               return result;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.prevEntry != null);
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   public SortedMultiset headMultiset(@Nullable Object upperBound, BoundType boundType) {
      return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.upTo(this.comparator(), upperBound, boundType)), this.header);
   }

   public SortedMultiset tailMultiset(@Nullable Object lowerBound, BoundType boundType) {
      return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.downTo(this.comparator(), lowerBound, boundType)), this.header);
   }

   static int distinctElements(@Nullable TreeMultiset.AvlNode node) {
      return node == null?0:node.distinctElements;
   }

   private static void successor(TreeMultiset.AvlNode a, TreeMultiset.AvlNode b) {
      a.succ = b;
      b.pred = a;
   }

   private static void successor(TreeMultiset.AvlNode a, TreeMultiset.AvlNode b, TreeMultiset.AvlNode c) {
      successor(a, b);
      successor(b, c);
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(this.elementSet().comparator());
      Serialization.writeMultiset(this, stream);
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      Comparator<? super E> comparator = (Comparator)stream.readObject();
      Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, comparator);
      Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(comparator));
      Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new TreeMultiset.Reference());
      TreeMultiset.AvlNode<E> header = new TreeMultiset.AvlNode((Object)null, 1);
      Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, header);
      successor(header, header);
      Serialization.populateMultiset(this, stream);
   }

   private static enum Aggregate {
      SIZE {
         int nodeAggregate(TreeMultiset.AvlNode node) {
            return node.elemCount;
         }

         long treeAggregate(@Nullable TreeMultiset.AvlNode root) {
            return root == null?0L:root.totalCount;
         }
      },
      DISTINCT {
         int nodeAggregate(TreeMultiset.AvlNode node) {
            return 1;
         }

         long treeAggregate(@Nullable TreeMultiset.AvlNode root) {
            return root == null?0L:(long)root.distinctElements;
         }
      };

      private Aggregate() {
      }

      abstract int nodeAggregate(TreeMultiset.AvlNode var1);

      abstract long treeAggregate(@Nullable TreeMultiset.AvlNode var1);
   }

   private static final class AvlNode extends Multisets.AbstractEntry {
      @Nullable
      private final Object elem;
      private int elemCount;
      private int distinctElements;
      private long totalCount;
      private int height;
      private TreeMultiset.AvlNode left;
      private TreeMultiset.AvlNode right;
      private TreeMultiset.AvlNode pred;
      private TreeMultiset.AvlNode succ;

      AvlNode(@Nullable Object elem, int elemCount) {
         Preconditions.checkArgument(elemCount > 0);
         this.elem = elem;
         this.elemCount = elemCount;
         this.totalCount = (long)elemCount;
         this.distinctElements = 1;
         this.height = 1;
         this.left = null;
         this.right = null;
      }

      public int count(Comparator comparator, Object e) {
         int cmp = comparator.compare(e, this.elem);
         return cmp < 0?(this.left == null?0:this.left.count(comparator, e)):(cmp > 0?(this.right == null?0:this.right.count(comparator, e)):this.elemCount);
      }

      private TreeMultiset.AvlNode addRightChild(Object e, int count) {
         this.right = new TreeMultiset.AvlNode(e, count);
         TreeMultiset.successor(this, this.right, this.succ);
         this.height = Math.max(2, this.height);
         ++this.distinctElements;
         this.totalCount += (long)count;
         return this;
      }

      private TreeMultiset.AvlNode addLeftChild(Object e, int count) {
         this.left = new TreeMultiset.AvlNode(e, count);
         TreeMultiset.successor(this.pred, this.left, this);
         this.height = Math.max(2, this.height);
         ++this.distinctElements;
         this.totalCount += (long)count;
         return this;
      }

      TreeMultiset.AvlNode add(Comparator comparator, @Nullable Object e, int count, int[] result) {
         int cmp = comparator.compare(e, this.elem);
         if(cmp < 0) {
            TreeMultiset.AvlNode<E> initLeft = this.left;
            if(initLeft == null) {
               result[0] = 0;
               return this.addLeftChild(e, count);
            } else {
               int initHeight = initLeft.height;
               this.left = initLeft.add(comparator, e, count, result);
               if(result[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)count;
               return this.left.height == initHeight?this:this.rebalance();
            }
         } else if(cmp > 0) {
            TreeMultiset.AvlNode<E> initRight = this.right;
            if(initRight == null) {
               result[0] = 0;
               return this.addRightChild(e, count);
            } else {
               int initHeight = initRight.height;
               this.right = initRight.add(comparator, e, count, result);
               if(result[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)count;
               return this.right.height == initHeight?this:this.rebalance();
            }
         } else {
            result[0] = this.elemCount;
            long resultCount = (long)this.elemCount + (long)count;
            Preconditions.checkArgument(resultCount <= 2147483647L);
            this.elemCount += count;
            this.totalCount += (long)count;
            return this;
         }
      }

      TreeMultiset.AvlNode remove(Comparator comparator, @Nullable Object e, int count, int[] result) {
         int cmp = comparator.compare(e, this.elem);
         if(cmp < 0) {
            TreeMultiset.AvlNode<E> initLeft = this.left;
            if(initLeft == null) {
               result[0] = 0;
               return this;
            } else {
               this.left = initLeft.remove(comparator, e, count, result);
               if(result[0] > 0) {
                  if(count >= result[0]) {
                     --this.distinctElements;
                     this.totalCount -= (long)result[0];
                  } else {
                     this.totalCount -= (long)count;
                  }
               }

               return result[0] == 0?this:this.rebalance();
            }
         } else if(cmp > 0) {
            TreeMultiset.AvlNode<E> initRight = this.right;
            if(initRight == null) {
               result[0] = 0;
               return this;
            } else {
               this.right = initRight.remove(comparator, e, count, result);
               if(result[0] > 0) {
                  if(count >= result[0]) {
                     --this.distinctElements;
                     this.totalCount -= (long)result[0];
                  } else {
                     this.totalCount -= (long)count;
                  }
               }

               return this.rebalance();
            }
         } else {
            result[0] = this.elemCount;
            if(count >= this.elemCount) {
               return this.deleteMe();
            } else {
               this.elemCount -= count;
               this.totalCount -= (long)count;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode setCount(Comparator comparator, @Nullable Object e, int count, int[] result) {
         int cmp = comparator.compare(e, this.elem);
         if(cmp < 0) {
            TreeMultiset.AvlNode<E> initLeft = this.left;
            if(initLeft == null) {
               result[0] = 0;
               return count > 0?this.addLeftChild(e, count):this;
            } else {
               this.left = initLeft.setCount(comparator, e, count, result);
               if(count == 0 && result[0] != 0) {
                  --this.distinctElements;
               } else if(count > 0 && result[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)(count - result[0]);
               return this.rebalance();
            }
         } else if(cmp > 0) {
            TreeMultiset.AvlNode<E> initRight = this.right;
            if(initRight == null) {
               result[0] = 0;
               return count > 0?this.addRightChild(e, count):this;
            } else {
               this.right = initRight.setCount(comparator, e, count, result);
               if(count == 0 && result[0] != 0) {
                  --this.distinctElements;
               } else if(count > 0 && result[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)(count - result[0]);
               return this.rebalance();
            }
         } else {
            result[0] = this.elemCount;
            if(count == 0) {
               return this.deleteMe();
            } else {
               this.totalCount += (long)(count - this.elemCount);
               this.elemCount = count;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode setCount(Comparator comparator, @Nullable Object e, int expectedCount, int newCount, int[] result) {
         int cmp = comparator.compare(e, this.elem);
         if(cmp < 0) {
            TreeMultiset.AvlNode<E> initLeft = this.left;
            if(initLeft == null) {
               result[0] = 0;
               return expectedCount == 0 && newCount > 0?this.addLeftChild(e, newCount):this;
            } else {
               this.left = initLeft.setCount(comparator, e, expectedCount, newCount, result);
               if(result[0] == expectedCount) {
                  if(newCount == 0 && result[0] != 0) {
                     --this.distinctElements;
                  } else if(newCount > 0 && result[0] == 0) {
                     ++this.distinctElements;
                  }

                  this.totalCount += (long)(newCount - result[0]);
               }

               return this.rebalance();
            }
         } else if(cmp > 0) {
            TreeMultiset.AvlNode<E> initRight = this.right;
            if(initRight == null) {
               result[0] = 0;
               return expectedCount == 0 && newCount > 0?this.addRightChild(e, newCount):this;
            } else {
               this.right = initRight.setCount(comparator, e, expectedCount, newCount, result);
               if(result[0] == expectedCount) {
                  if(newCount == 0 && result[0] != 0) {
                     --this.distinctElements;
                  } else if(newCount > 0 && result[0] == 0) {
                     ++this.distinctElements;
                  }

                  this.totalCount += (long)(newCount - result[0]);
               }

               return this.rebalance();
            }
         } else {
            result[0] = this.elemCount;
            if(expectedCount == this.elemCount) {
               if(newCount == 0) {
                  return this.deleteMe();
               }

               this.totalCount += (long)(newCount - this.elemCount);
               this.elemCount = newCount;
            }

            return this;
         }
      }

      private TreeMultiset.AvlNode deleteMe() {
         int oldElemCount = this.elemCount;
         this.elemCount = 0;
         TreeMultiset.successor(this.pred, this.succ);
         if(this.left == null) {
            return this.right;
         } else if(this.right == null) {
            return this.left;
         } else if(this.left.height >= this.right.height) {
            TreeMultiset.AvlNode<E> newTop = this.pred;
            newTop.left = this.left.removeMax(newTop);
            newTop.right = this.right;
            newTop.distinctElements = this.distinctElements - 1;
            newTop.totalCount = this.totalCount - (long)oldElemCount;
            return newTop.rebalance();
         } else {
            TreeMultiset.AvlNode<E> newTop = this.succ;
            newTop.right = this.right.removeMin(newTop);
            newTop.left = this.left;
            newTop.distinctElements = this.distinctElements - 1;
            newTop.totalCount = this.totalCount - (long)oldElemCount;
            return newTop.rebalance();
         }
      }

      private TreeMultiset.AvlNode removeMin(TreeMultiset.AvlNode node) {
         if(this.left == null) {
            return this.right;
         } else {
            this.left = this.left.removeMin(node);
            --this.distinctElements;
            this.totalCount -= (long)node.elemCount;
            return this.rebalance();
         }
      }

      private TreeMultiset.AvlNode removeMax(TreeMultiset.AvlNode node) {
         if(this.right == null) {
            return this.left;
         } else {
            this.right = this.right.removeMax(node);
            --this.distinctElements;
            this.totalCount -= (long)node.elemCount;
            return this.rebalance();
         }
      }

      private void recomputeMultiset() {
         this.distinctElements = 1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right);
         this.totalCount = (long)this.elemCount + totalCount(this.left) + totalCount(this.right);
      }

      private void recomputeHeight() {
         this.height = 1 + Math.max(height(this.left), height(this.right));
      }

      private void recompute() {
         this.recomputeMultiset();
         this.recomputeHeight();
      }

      private TreeMultiset.AvlNode rebalance() {
         switch(this.balanceFactor()) {
         case -2:
            if(this.right.balanceFactor() > 0) {
               this.right = this.right.rotateRight();
            }

            return this.rotateLeft();
         case 2:
            if(this.left.balanceFactor() < 0) {
               this.left = this.left.rotateLeft();
            }

            return this.rotateRight();
         default:
            this.recomputeHeight();
            return this;
         }
      }

      private int balanceFactor() {
         return height(this.left) - height(this.right);
      }

      private TreeMultiset.AvlNode rotateLeft() {
         Preconditions.checkState(this.right != null);
         TreeMultiset.AvlNode<E> newTop = this.right;
         this.right = newTop.left;
         newTop.left = this;
         newTop.totalCount = this.totalCount;
         newTop.distinctElements = this.distinctElements;
         this.recompute();
         newTop.recomputeHeight();
         return newTop;
      }

      private TreeMultiset.AvlNode rotateRight() {
         Preconditions.checkState(this.left != null);
         TreeMultiset.AvlNode<E> newTop = this.left;
         this.left = newTop.right;
         newTop.right = this;
         newTop.totalCount = this.totalCount;
         newTop.distinctElements = this.distinctElements;
         this.recompute();
         newTop.recomputeHeight();
         return newTop;
      }

      private static long totalCount(@Nullable TreeMultiset.AvlNode node) {
         return node == null?0L:node.totalCount;
      }

      private static int height(@Nullable TreeMultiset.AvlNode node) {
         return node == null?0:node.height;
      }

      @Nullable
      private TreeMultiset.AvlNode ceiling(Comparator comparator, Object e) {
         int cmp = comparator.compare(e, this.elem);
         return cmp < 0?(this.left == null?this:(TreeMultiset.AvlNode)Objects.firstNonNull(this.left.ceiling(comparator, e), this)):(cmp == 0?this:(this.right == null?null:this.right.ceiling(comparator, e)));
      }

      @Nullable
      private TreeMultiset.AvlNode floor(Comparator comparator, Object e) {
         int cmp = comparator.compare(e, this.elem);
         return cmp > 0?(this.right == null?this:(TreeMultiset.AvlNode)Objects.firstNonNull(this.right.floor(comparator, e), this)):(cmp == 0?this:(this.left == null?null:this.left.floor(comparator, e)));
      }

      public Object getElement() {
         return this.elem;
      }

      public int getCount() {
         return this.elemCount;
      }

      public String toString() {
         return Multisets.immutableEntry(this.getElement(), this.getCount()).toString();
      }
   }

   private static final class Reference {
      @Nullable
      private Object value;

      private Reference() {
      }

      @Nullable
      public Object get() {
         return this.value;
      }

      public void checkAndSet(@Nullable Object expected, Object newValue) {
         if(this.value != expected) {
            throw new ConcurrentModificationException();
         } else {
            this.value = newValue;
         }
      }
   }
}
