package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.spi.MutableThreadContextStack;
import org.apache.logging.log4j.spi.ThreadContextStack;

public class DefaultThreadContextStack implements ThreadContextStack {
   private static final long serialVersionUID = 5050501L;
   private static ThreadLocal stack = new ThreadLocal();
   private final boolean useStack;

   public DefaultThreadContextStack(boolean useStack) {
      this.useStack = useStack;
   }

   public String pop() {
      if(!this.useStack) {
         return "";
      } else {
         List<String> list = (List)stack.get();
         if(list != null && list.size() != 0) {
            List<String> copy = new ArrayList(list);
            int last = copy.size() - 1;
            String result = (String)copy.remove(last);
            stack.set(Collections.unmodifiableList(copy));
            return result;
         } else {
            throw new NoSuchElementException("The ThreadContext stack is empty");
         }
      }
   }

   public String peek() {
      List<String> list = (List)stack.get();
      if(list != null && list.size() != 0) {
         int last = list.size() - 1;
         return (String)list.get(last);
      } else {
         return null;
      }
   }

   public void push(String message) {
      if(this.useStack) {
         this.add(message);
      }
   }

   public int getDepth() {
      List<String> list = (List)stack.get();
      return list == null?0:list.size();
   }

   public List asList() {
      List<String> list = (List)stack.get();
      return list == null?Collections.emptyList():list;
   }

   public void trim(int depth) {
      if(depth < 0) {
         throw new IllegalArgumentException("Maximum stack depth cannot be negative");
      } else {
         List<String> list = (List)stack.get();
         if(list != null) {
            List<String> copy = new ArrayList();
            int count = Math.min(depth, list.size());

            for(int i = 0; i < count; ++i) {
               copy.add(list.get(i));
            }

            stack.set(copy);
         }
      }
   }

   public ThreadContextStack copy() {
      List<String> result = null;
      return this.useStack && (result = (List)stack.get()) != null?new MutableThreadContextStack(result):new MutableThreadContextStack(new ArrayList());
   }

   public void clear() {
      stack.remove();
   }

   public int size() {
      List<String> result = (List)stack.get();
      return result == null?0:result.size();
   }

   public boolean isEmpty() {
      List<String> result = (List)stack.get();
      return result == null || result.isEmpty();
   }

   public boolean contains(Object o) {
      List<String> result = (List)stack.get();
      return result != null && result.contains(o);
   }

   public Iterator iterator() {
      List<String> immutable = (List)stack.get();
      if(immutable == null) {
         List<String> empty = Collections.emptyList();
         return empty.iterator();
      } else {
         return immutable.iterator();
      }
   }

   public Object[] toArray() {
      List<String> result = (List)stack.get();
      return (Object[])(result == null?new String[0]:result.toArray(new Object[result.size()]));
   }

   public Object[] toArray(Object[] ts) {
      List<String> result = (List)stack.get();
      if(result == null) {
         if(ts.length > 0) {
            ts[0] = null;
         }

         return ts;
      } else {
         return result.toArray(ts);
      }
   }

   public boolean add(String s) {
      if(!this.useStack) {
         return false;
      } else {
         List<String> list = (List)stack.get();
         List<String> copy = list == null?new ArrayList():new ArrayList(list);
         copy.add(s);
         stack.set(Collections.unmodifiableList(copy));
         return true;
      }
   }

   public boolean remove(Object o) {
      if(!this.useStack) {
         return false;
      } else {
         List<String> list = (List)stack.get();
         if(list != null && list.size() != 0) {
            List<String> copy = new ArrayList(list);
            boolean result = copy.remove(o);
            stack.set(Collections.unmodifiableList(copy));
            return result;
         } else {
            return false;
         }
      }
   }

   public boolean containsAll(Collection objects) {
      if(objects.isEmpty()) {
         return true;
      } else {
         List<String> list = (List)stack.get();
         return list != null && list.containsAll(objects);
      }
   }

   public boolean addAll(Collection strings) {
      if(this.useStack && !strings.isEmpty()) {
         List<String> list = (List)stack.get();
         List<String> copy = list == null?new ArrayList():new ArrayList(list);
         copy.addAll(strings);
         stack.set(Collections.unmodifiableList(copy));
         return true;
      } else {
         return false;
      }
   }

   public boolean removeAll(Collection objects) {
      if(this.useStack && !objects.isEmpty()) {
         List<String> list = (List)stack.get();
         if(list != null && !list.isEmpty()) {
            List<String> copy = new ArrayList(list);
            boolean result = copy.removeAll(objects);
            stack.set(Collections.unmodifiableList(copy));
            return result;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean retainAll(Collection objects) {
      if(this.useStack && !objects.isEmpty()) {
         List<String> list = (List)stack.get();
         if(list != null && !list.isEmpty()) {
            List<String> copy = new ArrayList(list);
            boolean result = copy.retainAll(objects);
            stack.set(Collections.unmodifiableList(copy));
            return result;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      List<String> list = (List)stack.get();
      return list == null?"[]":list.toString();
   }
}
