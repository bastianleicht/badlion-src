package com.google.common.collect;

import com.google.common.collect.ForwardingQueue;
import java.util.Deque;
import java.util.Iterator;

public abstract class ForwardingDeque extends ForwardingQueue implements Deque {
   protected abstract Deque delegate();

   public void addFirst(Object e) {
      this.delegate().addFirst(e);
   }

   public void addLast(Object e) {
      this.delegate().addLast(e);
   }

   public Iterator descendingIterator() {
      return this.delegate().descendingIterator();
   }

   public Object getFirst() {
      return this.delegate().getFirst();
   }

   public Object getLast() {
      return this.delegate().getLast();
   }

   public boolean offerFirst(Object e) {
      return this.delegate().offerFirst(e);
   }

   public boolean offerLast(Object e) {
      return this.delegate().offerLast(e);
   }

   public Object peekFirst() {
      return this.delegate().peekFirst();
   }

   public Object peekLast() {
      return this.delegate().peekLast();
   }

   public Object pollFirst() {
      return this.delegate().pollFirst();
   }

   public Object pollLast() {
      return this.delegate().pollLast();
   }

   public Object pop() {
      return this.delegate().pop();
   }

   public void push(Object e) {
      this.delegate().push(e);
   }

   public Object removeFirst() {
      return this.delegate().removeFirst();
   }

   public Object removeLast() {
      return this.delegate().removeLast();
   }

   public boolean removeFirstOccurrence(Object o) {
      return this.delegate().removeFirstOccurrence(o);
   }

   public boolean removeLastOccurrence(Object o) {
      return this.delegate().removeLastOccurrence(o);
   }
}
