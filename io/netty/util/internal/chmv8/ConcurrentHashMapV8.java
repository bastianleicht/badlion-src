package io.netty.util.internal.chmv8;

import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.chmv8.CountedCompleter;
import io.netty.util.internal.chmv8.ForkJoinPool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class ConcurrentHashMapV8 implements ConcurrentMap, Serializable {
   private static final long serialVersionUID = 7249069246763182397L;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final int DEFAULT_CAPACITY = 16;
   static final int MAX_ARRAY_SIZE = 2147483639;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
   private static final float LOAD_FACTOR = 0.75F;
   static final int TREEIFY_THRESHOLD = 8;
   static final int UNTREEIFY_THRESHOLD = 6;
   static final int MIN_TREEIFY_CAPACITY = 64;
   private static final int MIN_TRANSFER_STRIDE = 16;
   static final int MOVED = -1;
   static final int TREEBIN = -2;
   static final int RESERVED = -3;
   static final int HASH_BITS = Integer.MAX_VALUE;
   static final int NCPU = Runtime.getRuntime().availableProcessors();
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", ConcurrentHashMapV8.Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};
   transient volatile ConcurrentHashMapV8.Node[] table;
   private transient volatile ConcurrentHashMapV8.Node[] nextTable;
   private transient volatile long baseCount;
   private transient volatile int sizeCtl;
   private transient volatile int transferIndex;
   private transient volatile int transferOrigin;
   private transient volatile int cellsBusy;
   private transient volatile ConcurrentHashMapV8.CounterCell[] counterCells;
   private transient ConcurrentHashMapV8.KeySetView keySet;
   private transient ConcurrentHashMapV8.ValuesView values;
   private transient ConcurrentHashMapV8.EntrySetView entrySet;
   static final AtomicInteger counterHashCodeGenerator = new AtomicInteger();
   static final int SEED_INCREMENT = 1640531527;
   private static final Unsafe U;
   private static final long SIZECTL;
   private static final long TRANSFERINDEX;
   private static final long TRANSFERORIGIN;
   private static final long BASECOUNT;
   private static final long CELLSBUSY;
   private static final long CELLVALUE;
   private static final long ABASE;
   private static final int ASHIFT;

   static final int spread(int h) {
      return (h ^ h >>> 16) & Integer.MAX_VALUE;
   }

   private static final int tableSizeFor(int c) {
      int n = c - 1;
      n = n | n >>> 1;
      n = n | n >>> 2;
      n = n | n >>> 4;
      n = n | n >>> 8;
      n = n | n >>> 16;
      return n < 0?1:(n >= 1073741824?1073741824:n + 1);
   }

   static Class comparableClassFor(Object x) {
      if(x instanceof Comparable) {
         Class<?> c;
         if((c = x.getClass()) == String.class) {
            return c;
         }

         Type[] ts;
         if((ts = c.getGenericInterfaces()) != null) {
            for(int i = 0; i < ts.length; ++i) {
               Type[] as;
               Type t;
               ParameterizedType p;
               if((t = ts[i]) instanceof ParameterizedType && (p = (ParameterizedType)t).getRawType() == Comparable.class && (as = p.getActualTypeArguments()) != null && as.length == 1 && as[0] == c) {
                  return c;
               }
            }
         }
      }

      return null;
   }

   static int compareComparables(Class kc, Object k, Object x) {
      return x != null && x.getClass() == kc?((Comparable)k).compareTo(x):0;
   }

   static final ConcurrentHashMapV8.Node tabAt(ConcurrentHashMapV8.Node[] tab, int i) {
      return (ConcurrentHashMapV8.Node)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
   }

   static final boolean casTabAt(ConcurrentHashMapV8.Node[] tab, int i, ConcurrentHashMapV8.Node c, ConcurrentHashMapV8.Node v) {
      return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
   }

   static final void setTabAt(ConcurrentHashMapV8.Node[] tab, int i, ConcurrentHashMapV8.Node v) {
      U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
   }

   public ConcurrentHashMapV8() {
   }

   public ConcurrentHashMapV8(int initialCapacity) {
      if(initialCapacity < 0) {
         throw new IllegalArgumentException();
      } else {
         int cap = initialCapacity >= 536870912?1073741824:tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
         this.sizeCtl = cap;
      }
   }

   public ConcurrentHashMapV8(Map m) {
      this.sizeCtl = 16;
      this.putAll(m);
   }

   public ConcurrentHashMapV8(int initialCapacity, float loadFactor) {
      this(initialCapacity, loadFactor, 1);
   }

   public ConcurrentHashMapV8(int initialCapacity, float loadFactor, int concurrencyLevel) {
      if(loadFactor > 0.0F && initialCapacity >= 0 && concurrencyLevel > 0) {
         if(initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
         }

         long size = (long)(1.0D + (double)((float)((long)initialCapacity) / loadFactor));
         int cap = size >= 1073741824L?1073741824:tableSizeFor((int)size);
         this.sizeCtl = cap;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int size() {
      long n = this.sumCount();
      return n < 0L?0:(n > 2147483647L?Integer.MAX_VALUE:(int)n);
   }

   public boolean isEmpty() {
      return this.sumCount() <= 0L;
   }

   public Object get(Object key) {
      int h = spread(key.hashCode());
      ConcurrentHashMapV8.Node<K, V>[] tab = this.table;
      ConcurrentHashMapV8.Node<K, V> e;
      int n;
      if(this.table != null && (n = tab.length) > 0 && (e = tabAt(tab, n - 1 & h)) != null) {
         int eh = e.hash;
         if(e.hash == h) {
            K ek = e.key;
            if(e.key == key || ek != null && key.equals(ek)) {
               return e.val;
            }
         } else if(eh < 0) {
            ConcurrentHashMapV8.Node<K, V> p;
            return (p = e.find(h, key)) != null?p.val:null;
         }

         while((e = e.next) != null) {
            if(e.hash == h) {
               K ek = e.key;
               if(e.key == key || ek != null && key.equals(ek)) {
                  return e.val;
               }
            }
         }
      }

      return null;
   }

   public boolean containsKey(Object key) {
      return this.get(key) != null;
   }

   public boolean containsValue(Object value) {
      if(value == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMapV8.Node<K, V>[] t = this.table;
         if(this.table != null) {
            ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

            ConcurrentHashMapV8.Node<K, V> p;
            while((p = it.advance()) != null) {
               V v = p.val;
               if(p.val == value || v != null && value.equals(v)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public Object put(Object key, Object value) {
      return this.putVal(key, value, false);
   }

   final Object putVal(Object key, Object value, boolean onlyIfAbsent) {
      if(key != null && value != null) {
         int hash = spread(key.hashCode());
         int binCount = 0;
         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

         while(true) {
            int n;
            while(tab == null || (n = tab.length) == 0) {
               tab = this.initTable();
            }

            ConcurrentHashMapV8.Node<K, V> f;
            int i;
            if((f = tabAt(tab, i = n - 1 & hash)) == null) {
               if(casTabAt(tab, i, (ConcurrentHashMapV8.Node)null, new ConcurrentHashMapV8.Node(hash, key, value, (ConcurrentHashMapV8.Node)null))) {
                  break;
               }
            } else {
               int fh = f.hash;
               if(f.hash == -1) {
                  tab = this.helpTransfer(tab, f);
               } else {
                  V oldVal = null;
                  synchronized(f) {
                     if(tabAt(tab, i) == f) {
                        if(fh < 0) {
                           if(f instanceof ConcurrentHashMapV8.TreeBin) {
                              binCount = 2;
                              ConcurrentHashMapV8.Node<K, V> p;
                              if((p = ((ConcurrentHashMapV8.TreeBin)f).putTreeVal(hash, key, value)) != null) {
                                 oldVal = p.val;
                                 if(!onlyIfAbsent) {
                                    p.val = value;
                                 }
                              }
                           }
                        } else {
                           binCount = 1;
                           ConcurrentHashMapV8.Node<K, V> e = f;

                           while(true) {
                              if(e.hash == hash) {
                                 K ek = e.key;
                                 if(e.key == key || ek != null && key.equals(ek)) {
                                    oldVal = e.val;
                                    if(!onlyIfAbsent) {
                                       e.val = value;
                                    }
                                    break;
                                 }
                              }

                              ConcurrentHashMapV8.Node<K, V> pred = e;
                              if((e = e.next) == null) {
                                 pred.next = new ConcurrentHashMapV8.Node(hash, key, value, (ConcurrentHashMapV8.Node)null);
                                 break;
                              }

                              ++binCount;
                           }
                        }
                     }
                  }

                  if(binCount != 0) {
                     if(binCount >= 8) {
                        this.treeifyBin(tab, i);
                     }

                     if(oldVal != null) {
                        return oldVal;
                     }
                     break;
                  }
               }
            }
         }

         this.addCount(1L, binCount);
         return null;
      } else {
         throw new NullPointerException();
      }
   }

   public void putAll(Map m) {
      this.tryPresize(m.size());

      for(Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.putVal(e.getKey(), e.getValue(), false);
      }

   }

   public Object remove(Object key) {
      return this.replaceNode(key, (Object)null, (Object)null);
   }

   final Object replaceNode(Object key, Object value, Object cv) {
      int hash = spread(key.hashCode());
      ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

      ConcurrentHashMapV8.Node<K, V> f;
      int n;
      int i;
      while(tab != null && (n = tab.length) != 0 && (f = tabAt(tab, i = n - 1 & hash)) != null) {
         int fh = f.hash;
         if(f.hash == -1) {
            tab = this.helpTransfer(tab, f);
         } else {
            V oldVal = null;
            boolean validated = false;
            synchronized(f) {
               if(tabAt(tab, i) == f) {
                  if(fh < 0) {
                     if(f instanceof ConcurrentHashMapV8.TreeBin) {
                        validated = true;
                        ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                        ConcurrentHashMapV8.TreeNode<K, V> r = t.root;
                        ConcurrentHashMapV8.TreeNode<K, V> p;
                        if(t.root != null && (p = r.findTreeNode(hash, key, (Class)null)) != null) {
                           V pv = p.val;
                           if(cv == null || cv == pv || pv != null && cv.equals(pv)) {
                              oldVal = pv;
                              if(value != null) {
                                 p.val = value;
                              } else if(t.removeTreeNode(p)) {
                                 setTabAt(tab, i, untreeify(t.first));
                              }
                           }
                        }
                     }
                  } else {
                     validated = true;
                     ConcurrentHashMapV8.Node<K, V> e = f;
                     ConcurrentHashMapV8.Node<K, V> pred = null;

                     while(true) {
                        if(e.hash == hash) {
                           K ek = e.key;
                           if(e.key == key || ek != null && key.equals(ek)) {
                              V ev = e.val;
                              if(cv == null || cv == ev || ev != null && cv.equals(ev)) {
                                 oldVal = ev;
                                 if(value != null) {
                                    e.val = value;
                                 } else if(pred != null) {
                                    pred.next = e.next;
                                 } else {
                                    setTabAt(tab, i, e.next);
                                 }
                              }
                              break;
                           }
                        }

                        pred = e;
                        if((e = e.next) == null) {
                           break;
                        }
                     }
                  }
               }
            }

            if(validated) {
               if(oldVal != null) {
                  if(value == null) {
                     this.addCount(-1L, -1);
                  }

                  return oldVal;
               }
               break;
            }
         }
      }

      return null;
   }

   public void clear() {
      long delta = 0L;
      int i = 0;
      ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

      while(tab != null && i < tab.length) {
         ConcurrentHashMapV8.Node<K, V> f = tabAt(tab, i);
         if(f == null) {
            ++i;
         } else {
            int fh = f.hash;
            if(f.hash == -1) {
               tab = this.helpTransfer(tab, f);
               i = 0;
            } else {
               synchronized(f) {
                  if(tabAt(tab, i) == f) {
                     for(ConcurrentHashMapV8.Node<K, V> p = (ConcurrentHashMapV8.Node)(fh >= 0?f:(f instanceof ConcurrentHashMapV8.TreeBin?((ConcurrentHashMapV8.TreeBin)f).first:null)); p != null; p = p.next) {
                        --delta;
                     }

                     setTabAt(tab, i++, (ConcurrentHashMapV8.Node)null);
                  }
               }
            }
         }
      }

      if(delta != 0L) {
         this.addCount(delta, -1);
      }

   }

   public ConcurrentHashMapV8.KeySetView keySet() {
      ConcurrentHashMapV8.KeySetView<K, V> ks = this.keySet;
      return this.keySet != null?ks:(this.keySet = new ConcurrentHashMapV8.KeySetView(this, (Object)null));
   }

   public Collection values() {
      ConcurrentHashMapV8.ValuesView<K, V> vs = this.values;
      return this.values != null?vs:(this.values = new ConcurrentHashMapV8.ValuesView(this));
   }

   public Set entrySet() {
      ConcurrentHashMapV8.EntrySetView<K, V> es = this.entrySet;
      return this.entrySet != null?es:(this.entrySet = new ConcurrentHashMapV8.EntrySetView(this));
   }

   public int hashCode() {
      int h = 0;
      ConcurrentHashMapV8.Node<K, V>[] t = this.table;
      ConcurrentHashMapV8.Node<K, V> p;
      if(this.table != null) {
         for(ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length); (p = it.advance()) != null; h += p.key.hashCode() ^ p.val.hashCode()) {
            ;
         }
      }

      return h;
   }

   public String toString() {
      ConcurrentHashMapV8.Node<K, V>[] t = this.table;
      int f = this.table == null?0:t.length;
      ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, f, 0, f);
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      ConcurrentHashMapV8.Node<K, V> p;
      if((p = it.advance()) != null) {
         while(true) {
            K k = p.key;
            V v = p.val;
            sb.append(k == this?"(this Map)":k);
            sb.append('=');
            sb.append(v == this?"(this Map)":v);
            if((p = it.advance()) == null) {
               break;
            }

            sb.append(',').append(' ');
         }
      }

      return sb.append('}').toString();
   }

   public boolean equals(Object o) {
      if(o != this) {
         if(!(o instanceof Map)) {
            return false;
         }

         Map<?, ?> m = (Map)o;
         ConcurrentHashMapV8.Node<K, V>[] t = this.table;
         int f = this.table == null?0:t.length;
         ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, f, 0, f);

         ConcurrentHashMapV8.Node<K, V> p;
         while((p = it.advance()) != null) {
            V val = p.val;
            Object v = m.get(p.key);
            if(v == null || v != val && !v.equals(val)) {
               return false;
            }
         }

         for(Entry<?, ?> e : m.entrySet()) {
            Object mv;
            Object v;
            Object mk;
            if((mk = e.getKey()) == null || (mv = e.getValue()) == null || (v = this.get(mk)) == null || mv != v && !mv.equals(v)) {
               return false;
            }
         }
      }

      return true;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int sshift = 0;

      int ssize;
      for(ssize = 1; ssize < 16; ssize <<= 1) {
         ++sshift;
      }

      int segmentShift = 32 - sshift;
      int segmentMask = ssize - 1;
      ConcurrentHashMapV8.Segment<K, V>[] segments = (ConcurrentHashMapV8.Segment[])(new ConcurrentHashMapV8.Segment[16]);

      for(int i = 0; i < segments.length; ++i) {
         segments[i] = new ConcurrentHashMapV8.Segment(0.75F);
      }

      s.putFields().put("segments", segments);
      s.putFields().put("segmentShift", segmentShift);
      s.putFields().put("segmentMask", segmentMask);
      s.writeFields();
      ConcurrentHashMapV8.Node<K, V>[] t = this.table;
      if(this.table != null) {
         ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

         ConcurrentHashMapV8.Node<K, V> p;
         while((p = it.advance()) != null) {
            s.writeObject(p.key);
            s.writeObject(p.val);
         }
      }

      s.writeObject((Object)null);
      s.writeObject((Object)null);
      Object var10 = null;
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      this.sizeCtl = -1;
      s.defaultReadObject();
      long size = 0L;
      ConcurrentHashMapV8.Node<K, V> p = null;

      while(true) {
         K k = s.readObject();
         V v = s.readObject();
         if(k == null || v == null) {
            if(size == 0L) {
               this.sizeCtl = 0;
            } else {
               int n;
               if(size >= 536870912L) {
                  n = 1073741824;
               } else {
                  int sz = (int)size;
                  n = tableSizeFor(sz + (sz >>> 1) + 1);
               }

               ConcurrentHashMapV8.Node<K, V>[] tab = (ConcurrentHashMapV8.Node[])(new ConcurrentHashMapV8.Node[n]);
               int mask = n - 1;

               long added;
               ConcurrentHashMapV8.Node<K, V> next;
               for(added = 0L; p != null; p = next) {
                  next = p.next;
                  int h = p.hash;
                  int j = h & mask;
                  boolean insertAtFront;
                  ConcurrentHashMapV8.Node<K, V> first;
                  if((first = tabAt(tab, j)) == null) {
                     insertAtFront = true;
                  } else {
                     K k = p.key;
                     if(first.hash < 0) {
                        ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)first;
                        if(t.putTreeVal(h, k, p.val) == null) {
                           ++added;
                        }

                        insertAtFront = false;
                     } else {
                        int binCount = 0;
                        insertAtFront = true;

                        for(ConcurrentHashMapV8.Node<K, V> q = first; q != null; q = q.next) {
                           if(q.hash == h) {
                              K qk = q.key;
                              if(q.key == k || qk != null && k.equals(qk)) {
                                 insertAtFront = false;
                                 break;
                              }
                           }

                           ++binCount;
                        }

                        if(insertAtFront && binCount >= 8) {
                           insertAtFront = false;
                           ++added;
                           p.next = first;
                           ConcurrentHashMapV8.TreeNode<K, V> hd = null;
                           ConcurrentHashMapV8.TreeNode<K, V> tl = null;

                           for(ConcurrentHashMapV8.Node var26 = p; var26 != null; var26 = var26.next) {
                              ConcurrentHashMapV8.TreeNode<K, V> t = new ConcurrentHashMapV8.TreeNode(var26.hash, var26.key, var26.val, (ConcurrentHashMapV8.Node)null, (ConcurrentHashMapV8.TreeNode)null);
                              if((t.prev = tl) == null) {
                                 hd = t;
                              } else {
                                 tl.next = t;
                              }

                              tl = t;
                           }

                           setTabAt(tab, j, new ConcurrentHashMapV8.TreeBin(hd));
                        }
                     }
                  }

                  if(insertAtFront) {
                     ++added;
                     p.next = first;
                     setTabAt(tab, j, p);
                  }
               }

               this.table = tab;
               this.sizeCtl = n - (n >>> 2);
               this.baseCount = added;
            }

            return;
         }

         p = new ConcurrentHashMapV8.Node(spread(k.hashCode()), k, v, p);
         ++size;
      }
   }

   public Object putIfAbsent(Object key, Object value) {
      return this.putVal(key, value, true);
   }

   public boolean remove(Object key, Object value) {
      if(key == null) {
         throw new NullPointerException();
      } else {
         return value != null && this.replaceNode(key, (Object)null, value) != null;
      }
   }

   public boolean replace(Object key, Object oldValue, Object newValue) {
      if(key != null && oldValue != null && newValue != null) {
         return this.replaceNode(key, newValue, oldValue) != null;
      } else {
         throw new NullPointerException();
      }
   }

   public Object replace(Object key, Object value) {
      if(key != null && value != null) {
         return this.replaceNode(key, value, (Object)null);
      } else {
         throw new NullPointerException();
      }
   }

   public Object getOrDefault(Object key, Object defaultValue) {
      V v;
      return (v = this.get(key)) == null?defaultValue:v;
   }

   public void forEach(ConcurrentHashMapV8.BiAction action) {
      if(action == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMapV8.Node<K, V>[] t = this.table;
         if(this.table != null) {
            ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

            ConcurrentHashMapV8.Node<K, V> p;
            while((p = it.advance()) != null) {
               action.apply(p.key, p.val);
            }
         }

      }
   }

   public void replaceAll(ConcurrentHashMapV8.BiFun function) {
      if(function == null) {
         throw new NullPointerException();
      } else {
         ConcurrentHashMapV8.Node<K, V>[] t = this.table;
         if(this.table != null) {
            ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

            ConcurrentHashMapV8.Node<K, V> p;
            while((p = it.advance()) != null) {
               V oldValue = p.val;
               K key = p.key;

               while(true) {
                  V newValue = function.apply(key, oldValue);
                  if(newValue == null) {
                     throw new NullPointerException();
                  }

                  if(this.replaceNode(key, newValue, oldValue) != null || (oldValue = this.get(key)) == null) {
                     break;
                  }
               }
            }
         }

      }
   }

   public Object computeIfAbsent(Object key, ConcurrentHashMapV8.Fun mappingFunction) {
      if(key != null && mappingFunction != null) {
         int h = spread(key.hashCode());
         V val = null;
         int binCount = 0;
         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

         while(true) {
            int n;
            while(tab == null || (n = tab.length) == 0) {
               tab = this.initTable();
            }

            ConcurrentHashMapV8.Node<K, V> f;
            int i;
            if((f = tabAt(tab, i = n - 1 & h)) == null) {
               ConcurrentHashMapV8.Node<K, V> r = new ConcurrentHashMapV8.ReservationNode();
               synchronized(r) {
                  if(casTabAt(tab, i, (ConcurrentHashMapV8.Node)null, r)) {
                     binCount = 1;
                     ConcurrentHashMapV8.Node<K, V> node = null;

                     try {
                        if((val = mappingFunction.apply(key)) != null) {
                           node = new ConcurrentHashMapV8.Node(h, key, val, (ConcurrentHashMapV8.Node)null);
                        }
                     } finally {
                        setTabAt(tab, i, node);
                     }
                  }
               }

               if(binCount != 0) {
                  break;
               }
            } else {
               int fh = f.hash;
               if(f.hash == -1) {
                  tab = this.helpTransfer(tab, f);
               } else {
                  boolean added = false;
                  synchronized(f) {
                     if(tabAt(tab, i) == f) {
                        if(fh < 0) {
                           if(f instanceof ConcurrentHashMapV8.TreeBin) {
                              binCount = 2;
                              ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                              ConcurrentHashMapV8.TreeNode<K, V> r = t.root;
                              ConcurrentHashMapV8.TreeNode<K, V> p;
                              if(t.root != null && (p = r.findTreeNode(h, key, (Class)null)) != null) {
                                 val = p.val;
                              } else if((val = mappingFunction.apply(key)) != null) {
                                 added = true;
                                 t.putTreeVal(h, key, val);
                              }
                           }
                        } else {
                           binCount = 1;
                           ConcurrentHashMapV8.Node<K, V> e = f;

                           while(true) {
                              if(e.hash == h) {
                                 K ek = e.key;
                                 if(e.key == key || ek != null && key.equals(ek)) {
                                    val = e.val;
                                    break;
                                 }
                              }

                              ConcurrentHashMapV8.Node<K, V> pred = e;
                              if((e = e.next) == null) {
                                 if((val = mappingFunction.apply(key)) != null) {
                                    added = true;
                                    pred.next = new ConcurrentHashMapV8.Node(h, key, val, (ConcurrentHashMapV8.Node)null);
                                 }
                                 break;
                              }

                              ++binCount;
                           }
                        }
                     }
                  }

                  if(binCount != 0) {
                     if(binCount >= 8) {
                        this.treeifyBin(tab, i);
                     }

                     if(!added) {
                        return val;
                     }
                     break;
                  }
               }
            }
         }

         if(val != null) {
            this.addCount(1L, binCount);
         }

         return val;
      } else {
         throw new NullPointerException();
      }
   }

   public Object computeIfPresent(Object key, ConcurrentHashMapV8.BiFun remappingFunction) {
      if(key != null && remappingFunction != null) {
         int h = spread(key.hashCode());
         V val = null;
         int delta = 0;
         int binCount = 0;
         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

         while(true) {
            int n;
            while(tab == null || (n = tab.length) == 0) {
               tab = this.initTable();
            }

            ConcurrentHashMapV8.Node<K, V> f;
            int i;
            if((f = tabAt(tab, i = n - 1 & h)) == null) {
               break;
            }

            int fh = f.hash;
            if(f.hash == -1) {
               tab = this.helpTransfer(tab, f);
            } else {
               synchronized(f) {
                  if(tabAt(tab, i) == f) {
                     if(fh < 0) {
                        if(f instanceof ConcurrentHashMapV8.TreeBin) {
                           binCount = 2;
                           ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                           ConcurrentHashMapV8.TreeNode<K, V> r = t.root;
                           ConcurrentHashMapV8.TreeNode<K, V> p;
                           if(t.root != null && (p = r.findTreeNode(h, key, (Class)null)) != null) {
                              val = remappingFunction.apply(key, p.val);
                              if(val != null) {
                                 p.val = val;
                              } else {
                                 delta = -1;
                                 if(t.removeTreeNode(p)) {
                                    setTabAt(tab, i, untreeify(t.first));
                                 }
                              }
                           }
                        }
                     } else {
                        binCount = 1;
                        ConcurrentHashMapV8.Node<K, V> e = f;
                        ConcurrentHashMapV8.Node<K, V> pred = null;

                        while(true) {
                           if(e.hash == h) {
                              K ek = e.key;
                              if(e.key == key || ek != null && key.equals(ek)) {
                                 val = remappingFunction.apply(key, e.val);
                                 if(val != null) {
                                    e.val = val;
                                 } else {
                                    delta = -1;
                                    ConcurrentHashMapV8.Node<K, V> en = e.next;
                                    if(pred != null) {
                                       pred.next = en;
                                    } else {
                                       setTabAt(tab, i, en);
                                    }
                                 }
                                 break;
                              }
                           }

                           pred = e;
                           if((e = e.next) == null) {
                              break;
                           }

                           ++binCount;
                        }
                     }
                  }
               }

               if(binCount != 0) {
                  break;
               }
            }
         }

         if(delta != 0) {
            this.addCount((long)delta, binCount);
         }

         return val;
      } else {
         throw new NullPointerException();
      }
   }

   public Object compute(Object key, ConcurrentHashMapV8.BiFun remappingFunction) {
      if(key != null && remappingFunction != null) {
         int h = spread(key.hashCode());
         V val = null;
         int delta = 0;
         int binCount = 0;
         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

         while(true) {
            int n;
            while(tab == null || (n = tab.length) == 0) {
               tab = this.initTable();
            }

            ConcurrentHashMapV8.Node<K, V> f;
            int i;
            if((f = tabAt(tab, i = n - 1 & h)) == null) {
               ConcurrentHashMapV8.Node<K, V> r = new ConcurrentHashMapV8.ReservationNode();
               synchronized(r) {
                  if(casTabAt(tab, i, (ConcurrentHashMapV8.Node)null, r)) {
                     binCount = 1;
                     ConcurrentHashMapV8.Node<K, V> node = null;

                     try {
                        if((val = remappingFunction.apply(key, (Object)null)) != null) {
                           delta = 1;
                           node = new ConcurrentHashMapV8.Node(h, key, val, (ConcurrentHashMapV8.Node)null);
                        }
                     } finally {
                        setTabAt(tab, i, node);
                     }
                  }
               }

               if(binCount != 0) {
                  break;
               }
            } else {
               int fh = f.hash;
               if(f.hash == -1) {
                  tab = this.helpTransfer(tab, f);
               } else {
                  synchronized(f) {
                     if(tabAt(tab, i) == f) {
                        if(fh < 0) {
                           if(f instanceof ConcurrentHashMapV8.TreeBin) {
                              binCount = 1;
                              ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                              ConcurrentHashMapV8.TreeNode<K, V> r = t.root;
                              ConcurrentHashMapV8.TreeNode<K, V> p;
                              if(t.root != null) {
                                 p = r.findTreeNode(h, key, (Class)null);
                              } else {
                                 p = null;
                              }

                              V pv = p == null?null:p.val;
                              val = remappingFunction.apply(key, pv);
                              if(val != null) {
                                 if(p != null) {
                                    p.val = val;
                                 } else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                 }
                              } else if(p != null) {
                                 delta = -1;
                                 if(t.removeTreeNode(p)) {
                                    setTabAt(tab, i, untreeify(t.first));
                                 }
                              }
                           }
                        } else {
                           binCount = 1;
                           ConcurrentHashMapV8.Node<K, V> e = f;
                           ConcurrentHashMapV8.Node<K, V> pred = null;

                           while(true) {
                              if(e.hash == h) {
                                 K ek = e.key;
                                 if(e.key == key || ek != null && key.equals(ek)) {
                                    val = remappingFunction.apply(key, e.val);
                                    if(val != null) {
                                       e.val = val;
                                    } else {
                                       delta = -1;
                                       ConcurrentHashMapV8.Node<K, V> en = e.next;
                                       if(pred != null) {
                                          pred.next = en;
                                       } else {
                                          setTabAt(tab, i, en);
                                       }
                                    }
                                    break;
                                 }
                              }

                              pred = e;
                              if((e = e.next) == null) {
                                 val = remappingFunction.apply(key, (Object)null);
                                 if(val != null) {
                                    delta = 1;
                                    pred.next = new ConcurrentHashMapV8.Node(h, key, val, (ConcurrentHashMapV8.Node)null);
                                 }
                                 break;
                              }

                              ++binCount;
                           }
                        }
                     }
                  }

                  if(binCount != 0) {
                     if(binCount >= 8) {
                        this.treeifyBin(tab, i);
                     }
                     break;
                  }
               }
            }
         }

         if(delta != 0) {
            this.addCount((long)delta, binCount);
         }

         return val;
      } else {
         throw new NullPointerException();
      }
   }

   public Object merge(Object key, Object value, ConcurrentHashMapV8.BiFun remappingFunction) {
      if(key != null && value != null && remappingFunction != null) {
         int h = spread(key.hashCode());
         V val = null;
         int delta = 0;
         int binCount = 0;
         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;

         while(true) {
            int n;
            while(tab == null || (n = tab.length) == 0) {
               tab = this.initTable();
            }

            ConcurrentHashMapV8.Node<K, V> f;
            int i;
            if((f = tabAt(tab, i = n - 1 & h)) == null) {
               if(casTabAt(tab, i, (ConcurrentHashMapV8.Node)null, new ConcurrentHashMapV8.Node(h, key, value, (ConcurrentHashMapV8.Node)null))) {
                  delta = 1;
                  val = value;
                  break;
               }
            } else {
               int fh = f.hash;
               if(f.hash == -1) {
                  tab = this.helpTransfer(tab, f);
               } else {
                  synchronized(f) {
                     if(tabAt(tab, i) == f) {
                        if(fh < 0) {
                           if(f instanceof ConcurrentHashMapV8.TreeBin) {
                              binCount = 2;
                              ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                              ConcurrentHashMapV8.TreeNode<K, V> r = t.root;
                              ConcurrentHashMapV8.TreeNode<K, V> p = r == null?null:r.findTreeNode(h, key, (Class)null);
                              val = p == null?value:remappingFunction.apply(p.val, value);
                              if(val != null) {
                                 if(p != null) {
                                    p.val = val;
                                 } else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                 }
                              } else if(p != null) {
                                 delta = -1;
                                 if(t.removeTreeNode(p)) {
                                    setTabAt(tab, i, untreeify(t.first));
                                 }
                              }
                           }
                        } else {
                           binCount = 1;
                           ConcurrentHashMapV8.Node<K, V> e = f;
                           ConcurrentHashMapV8.Node<K, V> pred = null;

                           while(true) {
                              if(e.hash == h) {
                                 K ek = e.key;
                                 if(e.key == key || ek != null && key.equals(ek)) {
                                    val = remappingFunction.apply(e.val, value);
                                    if(val != null) {
                                       e.val = val;
                                    } else {
                                       delta = -1;
                                       ConcurrentHashMapV8.Node<K, V> en = e.next;
                                       if(pred != null) {
                                          pred.next = en;
                                       } else {
                                          setTabAt(tab, i, en);
                                       }
                                    }
                                    break;
                                 }
                              }

                              pred = e;
                              if((e = e.next) == null) {
                                 delta = 1;
                                 val = value;
                                 pred.next = new ConcurrentHashMapV8.Node(h, key, value, (ConcurrentHashMapV8.Node)null);
                                 break;
                              }

                              ++binCount;
                           }
                        }
                     }
                  }

                  if(binCount != 0) {
                     if(binCount >= 8) {
                        this.treeifyBin(tab, i);
                     }
                     break;
                  }
               }
            }
         }

         if(delta != 0) {
            this.addCount((long)delta, binCount);
         }

         return val;
      } else {
         throw new NullPointerException();
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object value) {
      return this.containsValue(value);
   }

   public Enumeration keys() {
      ConcurrentHashMapV8.Node<K, V>[] t = this.table;
      int f = this.table == null?0:t.length;
      return new ConcurrentHashMapV8.KeyIterator(t, f, 0, f, this);
   }

   public Enumeration elements() {
      ConcurrentHashMapV8.Node<K, V>[] t = this.table;
      int f = this.table == null?0:t.length;
      return new ConcurrentHashMapV8.ValueIterator(t, f, 0, f, this);
   }

   public long mappingCount() {
      long n = this.sumCount();
      return n < 0L?0L:n;
   }

   public static ConcurrentHashMapV8.KeySetView newKeySet() {
      return new ConcurrentHashMapV8.KeySetView(new ConcurrentHashMapV8(), Boolean.TRUE);
   }

   public static ConcurrentHashMapV8.KeySetView newKeySet(int initialCapacity) {
      return new ConcurrentHashMapV8.KeySetView(new ConcurrentHashMapV8(initialCapacity), Boolean.TRUE);
   }

   public ConcurrentHashMapV8.KeySetView keySet(Object mappedValue) {
      if(mappedValue == null) {
         throw new NullPointerException();
      } else {
         return new ConcurrentHashMapV8.KeySetView(this, mappedValue);
      }
   }

   private final ConcurrentHashMapV8.Node[] initTable() {
      ConcurrentHashMapV8.Node<K, V>[] tab;
      while(true) {
         tab = this.table;
         if(this.table != null && tab.length != 0) {
            break;
         }

         int sc = this.sizeCtl;
         if(this.sizeCtl < 0) {
            Thread.yield();
         } else if(U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
               tab = this.table;
               if(this.table == null || tab.length == 0) {
                  int n = sc > 0?sc:16;
                  ConcurrentHashMapV8.Node<K, V>[] nt = (ConcurrentHashMapV8.Node[])(new ConcurrentHashMapV8.Node[n]);
                  tab = nt;
                  this.table = nt;
                  sc = n - (n >>> 2);
               }
               break;
            } finally {
               this.sizeCtl = sc;
            }
         }
      }

      return tab;
   }

   private final void addCount(long x, int check) {
      IntegerHolder hc;
      boolean uncontended;
      InternalThreadLocalMap threadLocals;
      label0: {
         long s;
         label0: {
            ConcurrentHashMapV8.CounterCell[] as = this.counterCells;
            if(this.counterCells == null) {
               long b = this.baseCount;
               if(U.compareAndSwapLong(this, BASECOUNT, this.baseCount, s = b + x)) {
                  break label0;
               }
            }

            uncontended = true;
            threadLocals = InternalThreadLocalMap.get();
            ConcurrentHashMapV8.CounterCell a;
            int m;
            if((hc = threadLocals.counterHashCode()) == null || as == null || (m = as.length - 1) < 0 || (a = as[m & hc.value]) == null) {
               break label0;
            }

            long v = a.value;
            if(!(uncontended = U.compareAndSwapLong(a, CELLVALUE, a.value, v + x))) {
               break label0;
            }

            if(check <= 1) {
               return;
            }

            s = this.sumCount();
         }

         if(check >= 0) {
            while(true) {
               int sc = this.sizeCtl;
               if(s < (long)this.sizeCtl) {
                  break;
               }

               ConcurrentHashMapV8.Node<K, V>[] tab = this.table;
               if(this.table == null || tab.length >= 1073741824) {
                  break;
               }

               if(sc < 0) {
                  if(sc == -1 || this.transferIndex <= this.transferOrigin) {
                     break;
                  }

                  ConcurrentHashMapV8.Node<K, V>[] nt = this.nextTable;
                  if(this.nextTable == null) {
                     break;
                  }

                  if(U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) {
                     this.transfer(tab, nt);
                  }
               } else if(U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                  this.transfer(tab, (ConcurrentHashMapV8.Node[])null);
               }

               s = this.sumCount();
            }
         }

         return;
      }

      this.fullAddCount(threadLocals, x, hc, uncontended);
   }

   final ConcurrentHashMapV8.Node[] helpTransfer(ConcurrentHashMapV8.Node[] tab, ConcurrentHashMapV8.Node f) {
      if(f instanceof ConcurrentHashMapV8.ForwardingNode) {
         ConcurrentHashMapV8.Node<K, V>[] nextTab = ((ConcurrentHashMapV8.ForwardingNode)f).nextTable;
         if(((ConcurrentHashMapV8.ForwardingNode)f).nextTable != null) {
            if(nextTab == this.nextTable && tab == this.table && this.transferIndex > this.transferOrigin) {
               int sc = this.sizeCtl;
               if(this.sizeCtl < -1 && U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) {
                  this.transfer(tab, nextTab);
               }
            }

            return nextTab;
         }
      }

      return this.table;
   }

   private final void tryPresize(int size) {
      int c = size >= 536870912?1073741824:tableSizeFor(size + (size >>> 1) + 1);

      while(true) {
         int sc = this.sizeCtl;
         if(this.sizeCtl < 0) {
            break;
         }

         ConcurrentHashMapV8.Node<K, V>[] tab = this.table;
         int n;
         if(tab != null && (n = tab.length) != 0) {
            if(c <= sc || n >= 1073741824) {
               break;
            }

            if(tab == this.table && U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
               this.transfer(tab, (ConcurrentHashMapV8.Node[])null);
            }
         } else {
            n = sc > c?sc:c;
            if(U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
               try {
                  if(this.table == tab) {
                     ConcurrentHashMapV8.Node<K, V>[] nt = (ConcurrentHashMapV8.Node[])(new ConcurrentHashMapV8.Node[n]);
                     this.table = nt;
                     sc = n - (n >>> 2);
                  }
               } finally {
                  this.sizeCtl = sc;
               }
            }
         }
      }

   }

   private final void transfer(ConcurrentHashMapV8.Node[] tab, ConcurrentHashMapV8.Node[] nextTab) {
      int n = tab.length;
      int stride;
      if((stride = NCPU > 1?(n >>> 3) / NCPU:n) < 16) {
         stride = 16;
      }

      if(nextTab == null) {
         try {
            ConcurrentHashMapV8.Node<K, V>[] nt = (ConcurrentHashMapV8.Node[])(new ConcurrentHashMapV8.Node[n << 1]);
            nextTab = nt;
         } catch (Throwable var29) {
            this.sizeCtl = Integer.MAX_VALUE;
            return;
         }

         this.nextTable = nextTab;
         this.transferOrigin = n;
         this.transferIndex = n;
         ConcurrentHashMapV8.ForwardingNode<K, V> rev = new ConcurrentHashMapV8.ForwardingNode(tab);
         int k = n;

         while(k > 0) {
            int nextk = k > stride?k - stride:0;

            for(int m = nextk; m < k; ++m) {
               nextTab[m] = rev;
            }

            for(int m = n + nextk; m < n + k; ++m) {
               nextTab[m] = rev;
            }

            k = nextk;
            U.putOrderedInt(this, TRANSFERORIGIN, nextk);
         }
      }

      int nextn = nextTab.length;
      ConcurrentHashMapV8.ForwardingNode<K, V> fwd = new ConcurrentHashMapV8.ForwardingNode(nextTab);
      boolean advance = true;
      boolean finishing = false;
      int i = 0;
      int bound = 0;

      while(true) {
         while(!advance) {
            if(i >= 0 && i < n && i + n < nextn) {
               ConcurrentHashMapV8.Node<K, V> f;
               if((f = tabAt(tab, i)) == null) {
                  if(casTabAt(tab, i, (ConcurrentHashMapV8.Node)null, fwd)) {
                     setTabAt(nextTab, i, (ConcurrentHashMapV8.Node)null);
                     setTabAt(nextTab, i + n, (ConcurrentHashMapV8.Node)null);
                     advance = true;
                  }
               } else {
                  int fh = f.hash;
                  if(f.hash == -1) {
                     advance = true;
                  } else {
                     synchronized(f) {
                        if(tabAt(tab, i) == f) {
                           if(fh >= 0) {
                              int runBit = fh & n;
                              ConcurrentHashMapV8.Node<K, V> lastRun = f;

                              for(ConcurrentHashMapV8.Node<K, V> p = f.next; p != null; p = p.next) {
                                 int b = p.hash & n;
                                 if(b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                 }
                              }

                              ConcurrentHashMapV8.Node<K, V> ln;
                              ConcurrentHashMapV8.Node<K, V> hn;
                              if(runBit == 0) {
                                 ln = lastRun;
                                 hn = null;
                              } else {
                                 hn = lastRun;
                                 ln = null;
                              }

                              for(ConcurrentHashMapV8.Node<K, V> p = f; p != lastRun; p = p.next) {
                                 int ph = p.hash;
                                 K pk = p.key;
                                 V pv = p.val;
                                 if((ph & n) == 0) {
                                    ln = new ConcurrentHashMapV8.Node(ph, pk, pv, ln);
                                 } else {
                                    hn = new ConcurrentHashMapV8.Node(ph, pk, pv, hn);
                                 }
                              }

                              setTabAt(nextTab, i, ln);
                              setTabAt(nextTab, i + n, hn);
                              setTabAt(tab, i, fwd);
                              advance = true;
                           } else if(f instanceof ConcurrentHashMapV8.TreeBin) {
                              ConcurrentHashMapV8.TreeBin<K, V> t = (ConcurrentHashMapV8.TreeBin)f;
                              ConcurrentHashMapV8.TreeNode<K, V> lo = null;
                              ConcurrentHashMapV8.TreeNode<K, V> loTail = null;
                              ConcurrentHashMapV8.TreeNode<K, V> hi = null;
                              ConcurrentHashMapV8.TreeNode<K, V> hiTail = null;
                              int lc = 0;
                              int hc = 0;

                              for(ConcurrentHashMapV8.Node<K, V> e = t.first; e != null; e = e.next) {
                                 int h = e.hash;
                                 ConcurrentHashMapV8.TreeNode<K, V> p = new ConcurrentHashMapV8.TreeNode(h, e.key, e.val, (ConcurrentHashMapV8.Node)null, (ConcurrentHashMapV8.TreeNode)null);
                                 if((h & n) == 0) {
                                    if((p.prev = loTail) == null) {
                                       lo = p;
                                    } else {
                                       loTail.next = p;
                                    }

                                    loTail = p;
                                    ++lc;
                                 } else {
                                    if((p.prev = hiTail) == null) {
                                       hi = p;
                                    } else {
                                       hiTail.next = p;
                                    }

                                    hiTail = p;
                                    ++hc;
                                 }
                              }

                              ConcurrentHashMapV8.Node<K, V> ln = (ConcurrentHashMapV8.Node)(lc <= 6?untreeify(lo):(hc != 0?new ConcurrentHashMapV8.TreeBin(lo):t));
                              ConcurrentHashMapV8.Node<K, V> hn = (ConcurrentHashMapV8.Node)(hc <= 6?untreeify(hi):(lc != 0?new ConcurrentHashMapV8.TreeBin(hi):t));
                              setTabAt(nextTab, i, ln);
                              setTabAt(nextTab, i + n, hn);
                              setTabAt(tab, i, fwd);
                              advance = true;
                           }
                        }
                     }
                  }
               }
            } else {
               if(finishing) {
                  this.nextTable = null;
                  this.table = nextTab;
                  this.sizeCtl = (n << 1) - (n >>> 1);
                  return;
               }

               int sc;
               while(true) {
                  sc = this.sizeCtl;
                  ++sc;
                  if(U.compareAndSwapInt(this, SIZECTL, this.sizeCtl, sc)) {
                     break;
                  }
               }

               if(sc != -1) {
                  return;
               }

               advance = true;
               finishing = true;
               i = n;
            }
         }

         --i;
         if(i < bound && !finishing) {
            int nextIndex = this.transferIndex;
            if(this.transferIndex <= this.transferOrigin) {
               i = -1;
               advance = false;
            } else {
               int nextBound;
               if(U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex, nextBound = nextIndex > stride?nextIndex - stride:0)) {
                  bound = nextBound;
                  i = nextIndex - 1;
                  advance = false;
               }
            }
         } else {
            advance = false;
         }
      }
   }

   private final void treeifyBin(ConcurrentHashMapV8.Node[] tab, int index) {
      if(tab != null) {
         int n;
         if((n = tab.length) < 64) {
            if(tab == this.table) {
               int sc = this.sizeCtl;
               if(this.sizeCtl >= 0 && U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                  this.transfer(tab, (ConcurrentHashMapV8.Node[])null);
               }
            }
         } else {
            ConcurrentHashMapV8.Node<K, V> b;
            if((b = tabAt(tab, index)) != null && b.hash >= 0) {
               synchronized(b) {
                  if(tabAt(tab, index) == b) {
                     ConcurrentHashMapV8.TreeNode<K, V> hd = null;
                     ConcurrentHashMapV8.TreeNode<K, V> tl = null;

                     for(ConcurrentHashMapV8.Node<K, V> e = b; e != null; e = e.next) {
                        ConcurrentHashMapV8.TreeNode<K, V> p = new ConcurrentHashMapV8.TreeNode(e.hash, e.key, e.val, (ConcurrentHashMapV8.Node)null, (ConcurrentHashMapV8.TreeNode)null);
                        if((p.prev = tl) == null) {
                           hd = p;
                        } else {
                           tl.next = p;
                        }

                        tl = p;
                     }

                     setTabAt(tab, index, new ConcurrentHashMapV8.TreeBin(hd));
                  }
               }
            }
         }
      }

   }

   static ConcurrentHashMapV8.Node untreeify(ConcurrentHashMapV8.Node b) {
      ConcurrentHashMapV8.Node<K, V> hd = null;
      ConcurrentHashMapV8.Node<K, V> tl = null;

      for(ConcurrentHashMapV8.Node<K, V> q = b; q != null; q = q.next) {
         ConcurrentHashMapV8.Node<K, V> p = new ConcurrentHashMapV8.Node(q.hash, q.key, q.val, (ConcurrentHashMapV8.Node)null);
         if(tl == null) {
            hd = p;
         } else {
            tl.next = p;
         }

         tl = p;
      }

      return hd;
   }

   final int batchFor(long b) {
      long n;
      if(b != Long.MAX_VALUE && (n = this.sumCount()) > 1L && n >= b) {
         int sp = ForkJoinPool.getCommonPoolParallelism() << 2;
         return b > 0L && (n = n / b) < (long)sp?(int)n:sp;
      } else {
         return 0;
      }
   }

   public void forEach(long parallelismThreshold, ConcurrentHashMapV8.BiAction action) {
      if(action == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMapV8.ForEachMappingTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, action)).invoke();
      }
   }

   public void forEach(long parallelismThreshold, ConcurrentHashMapV8.BiFun transformer, ConcurrentHashMapV8.Action action) {
      if(transformer != null && action != null) {
         (new ConcurrentHashMapV8.ForEachTransformedMappingTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public Object search(long parallelismThreshold, ConcurrentHashMapV8.BiFun searchFunction) {
      if(searchFunction == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.SearchMappingsTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference())).invoke();
      }
   }

   public Object reduce(long parallelismThreshold, ConcurrentHashMapV8.BiFun transformer, ConcurrentHashMapV8.BiFun reducer) {
      if(transformer != null && reducer != null) {
         return (new ConcurrentHashMapV8.MapReduceMappingsTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsTask)null, transformer, reducer)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceToDouble(long parallelismThreshold, ConcurrentHashMapV8.ObjectByObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
      if(transformer != null && reducer != null) {
         return ((Double)(new ConcurrentHashMapV8.MapReduceMappingsToDoubleTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToDoubleTask)null, transformer, basis, reducer)).invoke()).doubleValue();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceToLong(long parallelismThreshold, ConcurrentHashMapV8.ObjectByObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
      if(transformer != null && reducer != null) {
         return ((Long)(new ConcurrentHashMapV8.MapReduceMappingsToLongTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToLongTask)null, transformer, basis, reducer)).invoke()).longValue();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceToInt(long parallelismThreshold, ConcurrentHashMapV8.ObjectByObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
      if(transformer != null && reducer != null) {
         return ((Integer)(new ConcurrentHashMapV8.MapReduceMappingsToIntTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceMappingsToIntTask)null, transformer, basis, reducer)).invoke()).intValue();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachKey(long parallelismThreshold, ConcurrentHashMapV8.Action action) {
      if(action == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMapV8.ForEachKeyTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, action)).invoke();
      }
   }

   public void forEachKey(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
      if(transformer != null && action != null) {
         (new ConcurrentHashMapV8.ForEachTransformedKeyTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public Object searchKeys(long parallelismThreshold, ConcurrentHashMapV8.Fun searchFunction) {
      if(searchFunction == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.SearchKeysTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference())).invoke();
      }
   }

   public Object reduceKeys(long parallelismThreshold, ConcurrentHashMapV8.BiFun reducer) {
      if(reducer == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.ReduceKeysTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.ReduceKeysTask)null, reducer)).invoke();
      }
   }

   public Object reduceKeys(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
      if(transformer != null && reducer != null) {
         return (new ConcurrentHashMapV8.MapReduceKeysTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysTask)null, transformer, reducer)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceKeysToDouble(long parallelismThreshold, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
      if(transformer != null && reducer != null) {
         return ((Double)(new ConcurrentHashMapV8.MapReduceKeysToDoubleTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToDoubleTask)null, transformer, basis, reducer)).invoke()).doubleValue();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceKeysToLong(long parallelismThreshold, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
      if(transformer != null && reducer != null) {
         return ((Long)(new ConcurrentHashMapV8.MapReduceKeysToLongTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToLongTask)null, transformer, basis, reducer)).invoke()).longValue();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceKeysToInt(long parallelismThreshold, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
      if(transformer != null && reducer != null) {
         return ((Integer)(new ConcurrentHashMapV8.MapReduceKeysToIntTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceKeysToIntTask)null, transformer, basis, reducer)).invoke()).intValue();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachValue(long parallelismThreshold, ConcurrentHashMapV8.Action action) {
      if(action == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMapV8.ForEachValueTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, action)).invoke();
      }
   }

   public void forEachValue(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
      if(transformer != null && action != null) {
         (new ConcurrentHashMapV8.ForEachTransformedValueTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public Object searchValues(long parallelismThreshold, ConcurrentHashMapV8.Fun searchFunction) {
      if(searchFunction == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.SearchValuesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference())).invoke();
      }
   }

   public Object reduceValues(long parallelismThreshold, ConcurrentHashMapV8.BiFun reducer) {
      if(reducer == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.ReduceValuesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.ReduceValuesTask)null, reducer)).invoke();
      }
   }

   public Object reduceValues(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
      if(transformer != null && reducer != null) {
         return (new ConcurrentHashMapV8.MapReduceValuesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesTask)null, transformer, reducer)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceValuesToDouble(long parallelismThreshold, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
      if(transformer != null && reducer != null) {
         return ((Double)(new ConcurrentHashMapV8.MapReduceValuesToDoubleTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToDoubleTask)null, transformer, basis, reducer)).invoke()).doubleValue();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceValuesToLong(long parallelismThreshold, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
      if(transformer != null && reducer != null) {
         return ((Long)(new ConcurrentHashMapV8.MapReduceValuesToLongTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToLongTask)null, transformer, basis, reducer)).invoke()).longValue();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceValuesToInt(long parallelismThreshold, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
      if(transformer != null && reducer != null) {
         return ((Integer)(new ConcurrentHashMapV8.MapReduceValuesToIntTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceValuesToIntTask)null, transformer, basis, reducer)).invoke()).intValue();
      } else {
         throw new NullPointerException();
      }
   }

   public void forEachEntry(long parallelismThreshold, ConcurrentHashMapV8.Action action) {
      if(action == null) {
         throw new NullPointerException();
      } else {
         (new ConcurrentHashMapV8.ForEachEntryTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, action)).invoke();
      }
   }

   public void forEachEntry(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
      if(transformer != null && action != null) {
         (new ConcurrentHashMapV8.ForEachTransformedEntryTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public Object searchEntries(long parallelismThreshold, ConcurrentHashMapV8.Fun searchFunction) {
      if(searchFunction == null) {
         throw new NullPointerException();
      } else {
         return (new ConcurrentHashMapV8.SearchEntriesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference())).invoke();
      }
   }

   public Entry reduceEntries(long parallelismThreshold, ConcurrentHashMapV8.BiFun reducer) {
      if(reducer == null) {
         throw new NullPointerException();
      } else {
         return (Entry)(new ConcurrentHashMapV8.ReduceEntriesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.ReduceEntriesTask)null, reducer)).invoke();
      }
   }

   public Object reduceEntries(long parallelismThreshold, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
      if(transformer != null && reducer != null) {
         return (new ConcurrentHashMapV8.MapReduceEntriesTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesTask)null, transformer, reducer)).invoke();
      } else {
         throw new NullPointerException();
      }
   }

   public double reduceEntriesToDouble(long parallelismThreshold, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
      if(transformer != null && reducer != null) {
         return ((Double)(new ConcurrentHashMapV8.MapReduceEntriesToDoubleTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToDoubleTask)null, transformer, basis, reducer)).invoke()).doubleValue();
      } else {
         throw new NullPointerException();
      }
   }

   public long reduceEntriesToLong(long parallelismThreshold, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
      if(transformer != null && reducer != null) {
         return ((Long)(new ConcurrentHashMapV8.MapReduceEntriesToLongTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToLongTask)null, transformer, basis, reducer)).invoke()).longValue();
      } else {
         throw new NullPointerException();
      }
   }

   public int reduceEntriesToInt(long parallelismThreshold, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
      if(transformer != null && reducer != null) {
         return ((Integer)(new ConcurrentHashMapV8.MapReduceEntriesToIntTask((ConcurrentHashMapV8.BulkTask)null, this.batchFor(parallelismThreshold), 0, 0, this.table, (ConcurrentHashMapV8.MapReduceEntriesToIntTask)null, transformer, basis, reducer)).invoke()).intValue();
      } else {
         throw new NullPointerException();
      }
   }

   final long sumCount() {
      ConcurrentHashMapV8.CounterCell[] as = this.counterCells;
      long sum = this.baseCount;
      if(as != null) {
         for(int i = 0; i < as.length; ++i) {
            ConcurrentHashMapV8.CounterCell a;
            if((a = as[i]) != null) {
               sum += a.value;
            }
         }
      }

      return sum;
   }

   private final void fullAddCount(InternalThreadLocalMap threadLocals, long x, IntegerHolder hc, boolean wasUncontended) {
      int h;
      if(hc == null) {
         hc = new IntegerHolder();
         int s = counterHashCodeGenerator.addAndGet(1640531527);
         h = hc.value = s == 0?1:s;
         threadLocals.setCounterHashCode(hc);
      } else {
         h = hc.value;
      }

      boolean collide = false;

      while(true) {
         ConcurrentHashMapV8.CounterCell[] as = this.counterCells;
         int n;
         if(this.counterCells != null && (n = as.length) > 0) {
            ConcurrentHashMapV8.CounterCell a;
            if((a = as[n - 1 & h]) == null) {
               if(this.cellsBusy == 0) {
                  ConcurrentHashMapV8.CounterCell r = new ConcurrentHashMapV8.CounterCell(x);
                  if(this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                     boolean created = false;

                     try {
                        ConcurrentHashMapV8.CounterCell[] rs = this.counterCells;
                        int m;
                        int j;
                        if(this.counterCells != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                           rs[j] = r;
                           created = true;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     if(created) {
                        break;
                     }
                     continue;
                  }
               }

               collide = false;
            } else if(!wasUncontended) {
               wasUncontended = true;
            } else {
               long v = a.value;
               if(U.compareAndSwapLong(a, CELLVALUE, a.value, v + x)) {
                  break;
               }

               if(this.counterCells == as && n < NCPU) {
                  if(!collide) {
                     collide = true;
                  } else if(this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                     try {
                        if(this.counterCells == as) {
                           ConcurrentHashMapV8.CounterCell[] rs = new ConcurrentHashMapV8.CounterCell[n << 1];

                           for(int i = 0; i < n; ++i) {
                              rs[i] = as[i];
                           }

                           this.counterCells = rs;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     collide = false;
                     continue;
                  }
               } else {
                  collide = false;
               }
            }

            h = h ^ h << 13;
            h = h ^ h >>> 17;
            h = h ^ h << 5;
         } else if(this.cellsBusy == 0 && this.counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
            boolean init = false;

            try {
               if(this.counterCells == as) {
                  ConcurrentHashMapV8.CounterCell[] rs = new ConcurrentHashMapV8.CounterCell[2];
                  rs[h & 1] = new ConcurrentHashMapV8.CounterCell(x);
                  this.counterCells = rs;
                  init = true;
               }
            } finally {
               this.cellsBusy = 0;
            }

            if(init) {
               break;
            }
         } else {
            long v = this.baseCount;
            if(U.compareAndSwapLong(this, BASECOUNT, this.baseCount, v + x)) {
               break;
            }
         }
      }

      hc.value = h;
   }

   private static Unsafe getUnsafe() {
      try {
         return Unsafe.getUnsafe();
      } catch (SecurityException var2) {
         try {
            return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Unsafe run() throws Exception {
                  Class<Unsafe> k = Unsafe.class;

                  for(Field f : k.getDeclaredFields()) {
                     f.setAccessible(true);
                     Object x = f.get((Object)null);
                     if(k.isInstance(x)) {
                        return (Unsafe)k.cast(x);
                     }
                  }

                  throw new NoSuchFieldError("the Unsafe");
               }
            });
         } catch (PrivilegedActionException var1) {
            throw new RuntimeException("Could not initialize intrinsics", var1.getCause());
         }
      }
   }

   static {
      try {
         U = getUnsafe();
         Class<?> k = ConcurrentHashMapV8.class;
         SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
         TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
         TRANSFERORIGIN = U.objectFieldOffset(k.getDeclaredField("transferOrigin"));
         BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
         CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
         Class<?> ck = ConcurrentHashMapV8.CounterCell.class;
         CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
         Class<?> ak = ConcurrentHashMapV8.Node[].class;
         ABASE = (long)U.arrayBaseOffset(ak);
         int scale = U.arrayIndexScale(ak);
         if((scale & scale - 1) != 0) {
            throw new Error("data type scale not a power of two");
         } else {
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
         }
      } catch (Exception var4) {
         throw new Error(var4);
      }
   }

   public interface Action {
      void apply(Object var1);
   }

   static class BaseIterator extends ConcurrentHashMapV8.Traverser {
      final ConcurrentHashMapV8 map;
      ConcurrentHashMapV8.Node lastReturned;

      BaseIterator(ConcurrentHashMapV8.Node[] tab, int size, int index, int limit, ConcurrentHashMapV8 map) {
         super(tab, size, index, limit);
         this.map = map;
         this.advance();
      }

      public final boolean hasNext() {
         return this.next != null;
      }

      public final boolean hasMoreElements() {
         return this.next != null;
      }

      public final void remove() {
         ConcurrentHashMapV8.Node<K, V> p = this.lastReturned;
         if(this.lastReturned == null) {
            throw new IllegalStateException();
         } else {
            this.lastReturned = null;
            this.map.replaceNode(p.key, (Object)null, (Object)null);
         }
      }
   }

   public interface BiAction {
      void apply(Object var1, Object var2);
   }

   public interface BiFun {
      Object apply(Object var1, Object var2);
   }

   abstract static class BulkTask extends CountedCompleter {
      ConcurrentHashMapV8.Node[] tab;
      ConcurrentHashMapV8.Node next;
      int index;
      int baseIndex;
      int baseLimit;
      final int baseSize;
      int batch;

      BulkTask(ConcurrentHashMapV8.BulkTask par, int b, int i, int f, ConcurrentHashMapV8.Node[] t) {
         super(par);
         this.batch = b;
         this.index = this.baseIndex = i;
         if((this.tab = t) == null) {
            this.baseSize = this.baseLimit = 0;
         } else if(par == null) {
            this.baseSize = this.baseLimit = t.length;
         } else {
            this.baseLimit = f;
            this.baseSize = par.baseSize;
         }

      }

      final ConcurrentHashMapV8.Node advance() {
         ConcurrentHashMapV8.Node<K, V> e = this.next;
         if(this.next != null) {
            e = e.next;
         }

         while(true) {
            if(e != null) {
               return this.next = e;
            }

            if(this.baseIndex >= this.baseLimit) {
               break;
            }

            ConcurrentHashMapV8.Node<K, V>[] t = this.tab;
            if(this.tab == null) {
               break;
            }

            int n;
            int var10000 = n = t.length;
            int i = this.index;
            if(var10000 <= this.index || i < 0) {
               break;
            }

            if((e = ConcurrentHashMapV8.tabAt(t, this.index)) != null && e.hash < 0) {
               if(e instanceof ConcurrentHashMapV8.ForwardingNode) {
                  this.tab = ((ConcurrentHashMapV8.ForwardingNode)e).nextTable;
                  e = null;
                  continue;
               }

               if(e instanceof ConcurrentHashMapV8.TreeBin) {
                  e = ((ConcurrentHashMapV8.TreeBin)e).first;
               } else {
                  e = null;
               }
            }

            if((this.index += this.baseSize) >= n) {
               this.index = ++this.baseIndex;
            }
         }

         return this.next = null;
      }
   }

   abstract static class CollectionView implements Collection, Serializable {
      private static final long serialVersionUID = 7249069246763182397L;
      final ConcurrentHashMapV8 map;
      private static final String oomeMsg = "Required array size too large";

      CollectionView(ConcurrentHashMapV8 map) {
         this.map = map;
      }

      public ConcurrentHashMapV8 getMap() {
         return this.map;
      }

      public final void clear() {
         this.map.clear();
      }

      public final int size() {
         return this.map.size();
      }

      public final boolean isEmpty() {
         return this.map.isEmpty();
      }

      public abstract Iterator iterator();

      public abstract boolean contains(Object var1);

      public abstract boolean remove(Object var1);

      public final Object[] toArray() {
         long sz = this.map.mappingCount();
         if(sz > 2147483639L) {
            throw new OutOfMemoryError("Required array size too large");
         } else {
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;

            for(E e : this) {
               if(i == n) {
                  if(n >= 2147483639) {
                     throw new OutOfMemoryError("Required array size too large");
                  }

                  if(n >= 1073741819) {
                     n = 2147483639;
                  } else {
                     n += (n >>> 1) + 1;
                  }

                  r = Arrays.copyOf(r, n);
               }

               r[i++] = e;
            }

            return i == n?r:Arrays.copyOf(r, i);
         }
      }

      public final Object[] toArray(Object[] a) {
         long sz = this.map.mappingCount();
         if(sz > 2147483639L) {
            throw new OutOfMemoryError("Required array size too large");
         } else {
            int m = (int)sz;
            T[] r = a.length >= m?a:(Object[])((Object[])Array.newInstance(a.getClass().getComponentType(), m));
            int n = r.length;
            int i = 0;

            for(E e : this) {
               if(i == n) {
                  if(n >= 2147483639) {
                     throw new OutOfMemoryError("Required array size too large");
                  }

                  if(n >= 1073741819) {
                     n = 2147483639;
                  } else {
                     n += (n >>> 1) + 1;
                  }

                  r = Arrays.copyOf(r, n);
               }

               r[i++] = e;
            }

            if(a == r && i < n) {
               r[i] = null;
               return r;
            } else {
               return i == n?r:Arrays.copyOf(r, i);
            }
         }
      }

      public final String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append('[');
         Iterator<E> it = this.iterator();
         if(it.hasNext()) {
            while(true) {
               Object e = it.next();
               sb.append(e == this?"(this Collection)":e);
               if(!it.hasNext()) {
                  break;
               }

               sb.append(',').append(' ');
            }
         }

         return sb.append(']').toString();
      }

      public final boolean containsAll(Collection c) {
         if(c != this) {
            for(Object e : c) {
               if(e == null || !this.contains(e)) {
                  return false;
               }
            }
         }

         return true;
      }

      public final boolean removeAll(Collection c) {
         boolean modified = false;
         Iterator<E> it = this.iterator();

         while(it.hasNext()) {
            if(c.contains(it.next())) {
               it.remove();
               modified = true;
            }
         }

         return modified;
      }

      public final boolean retainAll(Collection c) {
         boolean modified = false;
         Iterator<E> it = this.iterator();

         while(it.hasNext()) {
            if(!c.contains(it.next())) {
               it.remove();
               modified = true;
            }
         }

         return modified;
      }
   }

   public interface ConcurrentHashMapSpliterator {
      ConcurrentHashMapV8.ConcurrentHashMapSpliterator trySplit();

      long estimateSize();

      void forEachRemaining(ConcurrentHashMapV8.Action var1);

      boolean tryAdvance(ConcurrentHashMapV8.Action var1);
   }

   static final class CounterCell {
      volatile long p0;
      volatile long p1;
      volatile long p2;
      volatile long p3;
      volatile long p4;
      volatile long p5;
      volatile long p6;
      volatile long value;
      volatile long q0;
      volatile long q1;
      volatile long q2;
      volatile long q3;
      volatile long q4;
      volatile long q5;
      volatile long q6;

      CounterCell(long x) {
         this.value = x;
      }
   }

   static final class CounterHashCode {
      int code;
   }

   public interface DoubleByDoubleToDouble {
      double apply(double var1, double var3);
   }

   static final class EntryIterator extends ConcurrentHashMapV8.BaseIterator implements Iterator {
      EntryIterator(ConcurrentHashMapV8.Node[] tab, int index, int size, int limit, ConcurrentHashMapV8 map) {
         super(tab, index, size, limit, map);
      }

      public final Entry next() {
         ConcurrentHashMapV8.Node<K, V> p = this.next;
         if(this.next == null) {
            throw new NoSuchElementException();
         } else {
            K k = p.key;
            V v = p.val;
            this.lastReturned = p;
            this.advance();
            return new ConcurrentHashMapV8.MapEntry(k, v, this.map);
         }
      }
   }

   static final class EntrySetView extends ConcurrentHashMapV8.CollectionView implements Set, Serializable {
      private static final long serialVersionUID = 2249069246763182397L;

      EntrySetView(ConcurrentHashMapV8 map) {
         super(map);
      }

      public boolean contains(Object o) {
         Object k;
         Object v;
         Object r;
         Entry<?, ?> e;
         return o instanceof Entry && (k = (e = (Entry)o).getKey()) != null && (r = this.map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r));
      }

      public boolean remove(Object o) {
         Object k;
         Object v;
         Entry<?, ?> e;
         return o instanceof Entry && (k = (e = (Entry)o).getKey()) != null && (v = e.getValue()) != null && this.map.remove(k, v);
      }

      public Iterator iterator() {
         ConcurrentHashMapV8<K, V> m = this.map;
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.EntryIterator(t, f, 0, f, m);
      }

      public boolean add(Entry e) {
         return this.map.putVal(e.getKey(), e.getValue(), false) == null;
      }

      public boolean addAll(Collection c) {
         boolean added = false;

         for(Entry<K, V> e : c) {
            if(this.add(e)) {
               added = true;
            }
         }

         return added;
      }

      public final int hashCode() {
         int h = 0;
         ConcurrentHashMapV8.Node<K, V>[] t = this.map.table;
         ConcurrentHashMapV8.Node<K, V> p;
         if(this.map.table != null) {
            for(ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length); (p = it.advance()) != null; h += p.hashCode()) {
               ;
            }
         }

         return h;
      }

      public final boolean equals(Object o) {
         Set<?> c;
         return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator spliterator166() {
         ConcurrentHashMapV8<K, V> m = this.map;
         long n = m.sumCount();
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.EntrySpliterator(t, f, 0, f, n < 0L?0L:n, m);
      }

      public void forEach(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V>[] t = this.map.table;
            if(this.map.table != null) {
               ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = it.advance()) != null) {
                  action.apply(new ConcurrentHashMapV8.MapEntry(p.key, p.val, this.map));
               }
            }

         }
      }
   }

   static final class EntrySpliterator extends ConcurrentHashMapV8.Traverser implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator {
      final ConcurrentHashMapV8 map;
      long est;

      EntrySpliterator(ConcurrentHashMapV8.Node[] tab, int size, int index, int limit, long est, ConcurrentHashMapV8 map) {
         super(tab, size, index, limit);
         this.map = map;
         this.est = est;
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator trySplit() {
         int i = this.baseIndex;
         int f = this.baseLimit;
         int h;
         return (h = this.baseIndex + this.baseLimit >>> 1) <= i?null:new ConcurrentHashMapV8.EntrySpliterator(this.tab, this.baseSize, this.baseLimit = h, f, this.est >>>= 1, this.map);
      }

      public void forEachRemaining(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            while((p = this.advance()) != null) {
               action.apply(new ConcurrentHashMapV8.MapEntry(p.key, p.val, this.map));
            }

         }
      }

      public boolean tryAdvance(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            if((p = this.advance()) == null) {
               return false;
            } else {
               action.apply(new ConcurrentHashMapV8.MapEntry(p.key, p.val, this.map));
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }
   }

   static final class ForEachEntryTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Action action;

      ForEachEntryTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Action<? super Entry<K, V>> action = this.action;
         if(this.action != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMapV8.ForEachEntryTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, action)).fork();
            }

            while((p = this.advance()) != null) {
               action.apply(p);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachKeyTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Action action;

      ForEachKeyTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Action<? super K> action = this.action;
         if(this.action != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMapV8.ForEachKeyTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, action)).fork();
            }

            while((p = this.advance()) != null) {
               action.apply(p.key);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachMappingTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiAction action;

      ForEachMappingTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.BiAction action) {
         super(p, b, i, f, t);
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiAction<? super K, ? super V> action = this.action;
         if(this.action != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMapV8.ForEachMappingTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, action)).fork();
            }

            while((p = this.advance()) != null) {
               action.apply(p.key, p.val);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForEachTransformedEntryTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.Action action;

      ForEachTransformedEntryTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.transformer = transformer;
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.Action<? super U> action = this.action;
            if(this.action != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.ForEachTransformedEntryTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, transformer, action)).fork();
               }

               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p)) != null) {
                     action.apply(u);
                  }
               }

               this.propagateCompletion();
            }
         }

      }
   }

   static final class ForEachTransformedKeyTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.Action action;

      ForEachTransformedKeyTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.transformer = transformer;
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super K, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.Action<? super U> action = this.action;
            if(this.action != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.ForEachTransformedKeyTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, transformer, action)).fork();
               }

               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.key)) != null) {
                     action.apply(u);
                  }
               }

               this.propagateCompletion();
            }
         }

      }
   }

   static final class ForEachTransformedMappingTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun transformer;
      final ConcurrentHashMapV8.Action action;

      ForEachTransformedMappingTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.BiFun transformer, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.transformer = transformer;
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.Action<? super U> action = this.action;
            if(this.action != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.ForEachTransformedMappingTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, transformer, action)).fork();
               }

               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.key, p.val)) != null) {
                     action.apply(u);
                  }
               }

               this.propagateCompletion();
            }
         }

      }
   }

   static final class ForEachTransformedValueTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.Action action;

      ForEachTransformedValueTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.transformer = transformer;
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super V, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.Action<? super U> action = this.action;
            if(this.action != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.ForEachTransformedValueTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, transformer, action)).fork();
               }

               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.val)) != null) {
                     action.apply(u);
                  }
               }

               this.propagateCompletion();
            }
         }

      }
   }

   static final class ForEachValueTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Action action;

      ForEachValueTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Action action) {
         super(p, b, i, f, t);
         this.action = action;
      }

      public final void compute() {
         ConcurrentHashMapV8.Action<? super V> action = this.action;
         if(this.action != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (new ConcurrentHashMapV8.ForEachValueTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, action)).fork();
            }

            while((p = this.advance()) != null) {
               action.apply(p.val);
            }

            this.propagateCompletion();
         }

      }
   }

   static final class ForwardingNode extends ConcurrentHashMapV8.Node {
      final ConcurrentHashMapV8.Node[] nextTable;

      ForwardingNode(ConcurrentHashMapV8.Node[] tab) {
         super(-1, (Object)null, (Object)null, (ConcurrentHashMapV8.Node)null);
         this.nextTable = tab;
      }

      ConcurrentHashMapV8.Node find(int h, Object k) {
         ConcurrentHashMapV8.Node<K, V>[] tab = this.nextTable;

         label5:
         while(true) {
            ConcurrentHashMapV8.Node<K, V> e;
            int n;
            if(k != null && tab != null && (n = tab.length) != 0 && (e = ConcurrentHashMapV8.tabAt(tab, n - 1 & h)) != null) {
               while(true) {
                  int eh = e.hash;
                  if(e.hash == h) {
                     K ek = e.key;
                     if(e.key == k || ek != null && k.equals(ek)) {
                        return e;
                     }
                  }

                  if(eh < 0) {
                     if(!(e instanceof ConcurrentHashMapV8.ForwardingNode)) {
                        return e.find(h, k);
                     }

                     tab = ((ConcurrentHashMapV8.ForwardingNode)e).nextTable;
                     continue label5;
                  }

                  if((e = e.next) == null) {
                     break;
                  }
               }

               return null;
            }

            return null;
         }
      }
   }

   public interface Fun {
      Object apply(Object var1);
   }

   public interface IntByIntToInt {
      int apply(int var1, int var2);
   }

   static final class KeyIterator extends ConcurrentHashMapV8.BaseIterator implements Iterator, Enumeration {
      KeyIterator(ConcurrentHashMapV8.Node[] tab, int index, int size, int limit, ConcurrentHashMapV8 map) {
         super(tab, index, size, limit, map);
      }

      public final Object next() {
         ConcurrentHashMapV8.Node<K, V> p = this.next;
         if(this.next == null) {
            throw new NoSuchElementException();
         } else {
            K k = p.key;
            this.lastReturned = p;
            this.advance();
            return k;
         }
      }

      public final Object nextElement() {
         return this.next();
      }
   }

   public static class KeySetView extends ConcurrentHashMapV8.CollectionView implements Set, Serializable {
      private static final long serialVersionUID = 7249069246763182397L;
      private final Object value;

      KeySetView(ConcurrentHashMapV8 map, Object value) {
         super(map);
         this.value = value;
      }

      public Object getMappedValue() {
         return this.value;
      }

      public boolean contains(Object o) {
         return this.map.containsKey(o);
      }

      public boolean remove(Object o) {
         return this.map.remove(o) != null;
      }

      public Iterator iterator() {
         ConcurrentHashMapV8<K, V> m = this.map;
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.KeyIterator(t, f, 0, f, m);
      }

      public boolean add(Object e) {
         V v = this.value;
         if(this.value == null) {
            throw new UnsupportedOperationException();
         } else {
            return this.map.putVal(e, v, true) == null;
         }
      }

      public boolean addAll(Collection c) {
         boolean added = false;
         V v = this.value;
         if(this.value == null) {
            throw new UnsupportedOperationException();
         } else {
            for(K e : c) {
               if(this.map.putVal(e, v, true) == null) {
                  added = true;
               }
            }

            return added;
         }
      }

      public int hashCode() {
         int h = 0;

         for(K e : this) {
            h += e.hashCode();
         }

         return h;
      }

      public boolean equals(Object o) {
         Set<?> c;
         return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator spliterator166() {
         ConcurrentHashMapV8<K, V> m = this.map;
         long n = m.sumCount();
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.KeySpliterator(t, f, 0, f, n < 0L?0L:n);
      }

      public void forEach(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V>[] t = this.map.table;
            if(this.map.table != null) {
               ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = it.advance()) != null) {
                  action.apply(p.key);
               }
            }

         }
      }
   }

   static final class KeySpliterator extends ConcurrentHashMapV8.Traverser implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator {
      long est;

      KeySpliterator(ConcurrentHashMapV8.Node[] tab, int size, int index, int limit, long est) {
         super(tab, size, index, limit);
         this.est = est;
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator trySplit() {
         int i = this.baseIndex;
         int f = this.baseLimit;
         int h;
         return (h = this.baseIndex + this.baseLimit >>> 1) <= i?null:new ConcurrentHashMapV8.KeySpliterator(this.tab, this.baseSize, this.baseLimit = h, f, this.est >>>= 1);
      }

      public void forEachRemaining(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            while((p = this.advance()) != null) {
               action.apply(p.key);
            }

         }
      }

      public boolean tryAdvance(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            if((p = this.advance()) == null) {
               return false;
            } else {
               action.apply(p.key);
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }
   }

   public interface LongByLongToLong {
      long apply(long var1, long var3);
   }

   static final class MapEntry implements Entry {
      final Object key;
      Object val;
      final ConcurrentHashMapV8 map;

      MapEntry(Object key, Object val, ConcurrentHashMapV8 map) {
         this.key = key;
         this.val = val;
         this.map = map;
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.val;
      }

      public int hashCode() {
         return this.key.hashCode() ^ this.val.hashCode();
      }

      public String toString() {
         return this.key + "=" + this.val;
      }

      public boolean equals(Object o) {
         Object k;
         Object v;
         Entry<?, ?> e;
         return o instanceof Entry && (k = (e = (Entry)o).getKey()) != null && (v = e.getValue()) != null && (k == this.key || k.equals(this.key)) && (v == this.val || v.equals(this.val));
      }

      public Object setValue(Object value) {
         if(value == null) {
            throw new NullPointerException();
         } else {
            V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
         }
      }
   }

   static final class MapReduceEntriesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.MapReduceEntriesTask rights;
      ConcurrentHashMapV8.MapReduceEntriesTask nextRight;

      MapReduceEntriesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceEntriesTask nextRight, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer = this.reducer;
            if(this.reducer != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, reducer)).fork();
               }

               U r = null;

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p)) != null) {
                     r = r == null?u:reducer.apply(r, u);
                  }
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceEntriesTask<K, V, U> t = (ConcurrentHashMapV8.MapReduceEntriesTask)c;

                  for(ConcurrentHashMapV8.MapReduceEntriesTask<K, V, U> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     U sr = s.result;
                     if(s.result != null) {
                        U tr = t.result;
                        t.result = t.result == null?sr:reducer.apply(tr, sr);
                     }
                  }
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToDoubleTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToDouble transformer;
      final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
      final double basis;
      double result;
      ConcurrentHashMapV8.MapReduceEntriesToDoubleTask rights;
      ConcurrentHashMapV8.MapReduceEntriesToDoubleTask nextRight;

      MapReduceEntriesToDoubleTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceEntriesToDoubleTask nextRight, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Double getRawResult() {
         return Double.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToDouble<Entry<K, V>> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.DoubleByDoubleToDouble reducer = this.reducer;
            if(this.reducer != null) {
               double r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<K, V> t = (ConcurrentHashMapV8.MapReduceEntriesToDoubleTask)c;

                  for(ConcurrentHashMapV8.MapReduceEntriesToDoubleTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToIntTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToInt transformer;
      final ConcurrentHashMapV8.IntByIntToInt reducer;
      final int basis;
      int result;
      ConcurrentHashMapV8.MapReduceEntriesToIntTask rights;
      ConcurrentHashMapV8.MapReduceEntriesToIntTask nextRight;

      MapReduceEntriesToIntTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceEntriesToIntTask nextRight, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Integer getRawResult() {
         return Integer.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToInt<Entry<K, V>> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.IntByIntToInt reducer = this.reducer;
            if(this.reducer != null) {
               int r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToIntTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceEntriesToIntTask<K, V> t = (ConcurrentHashMapV8.MapReduceEntriesToIntTask)c;

                  for(ConcurrentHashMapV8.MapReduceEntriesToIntTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceEntriesToLongTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToLong transformer;
      final ConcurrentHashMapV8.LongByLongToLong reducer;
      final long basis;
      long result;
      ConcurrentHashMapV8.MapReduceEntriesToLongTask rights;
      ConcurrentHashMapV8.MapReduceEntriesToLongTask nextRight;

      MapReduceEntriesToLongTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceEntriesToLongTask nextRight, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Long getRawResult() {
         return Long.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToLong<Entry<K, V>> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.LongByLongToLong reducer = this.reducer;
            if(this.reducer != null) {
               long r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceEntriesToLongTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceEntriesToLongTask<K, V> t = (ConcurrentHashMapV8.MapReduceEntriesToLongTask)c;

                  for(ConcurrentHashMapV8.MapReduceEntriesToLongTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceKeysTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.MapReduceKeysTask rights;
      ConcurrentHashMapV8.MapReduceKeysTask nextRight;

      MapReduceKeysTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceKeysTask nextRight, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super K, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer = this.reducer;
            if(this.reducer != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, reducer)).fork();
               }

               U r = null;

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.key)) != null) {
                     r = r == null?u:reducer.apply(r, u);
                  }
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceKeysTask<K, V, U> t = (ConcurrentHashMapV8.MapReduceKeysTask)c;

                  for(ConcurrentHashMapV8.MapReduceKeysTask<K, V, U> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     U sr = s.result;
                     if(s.result != null) {
                        U tr = t.result;
                        t.result = t.result == null?sr:reducer.apply(tr, sr);
                     }
                  }
               }
            }
         }

      }
   }

   static final class MapReduceKeysToDoubleTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToDouble transformer;
      final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
      final double basis;
      double result;
      ConcurrentHashMapV8.MapReduceKeysToDoubleTask rights;
      ConcurrentHashMapV8.MapReduceKeysToDoubleTask nextRight;

      MapReduceKeysToDoubleTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceKeysToDoubleTask nextRight, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Double getRawResult() {
         return Double.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToDouble<? super K> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.DoubleByDoubleToDouble reducer = this.reducer;
            if(this.reducer != null) {
               double r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceKeysToDoubleTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceKeysToDoubleTask<K, V> t = (ConcurrentHashMapV8.MapReduceKeysToDoubleTask)c;

                  for(ConcurrentHashMapV8.MapReduceKeysToDoubleTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceKeysToIntTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToInt transformer;
      final ConcurrentHashMapV8.IntByIntToInt reducer;
      final int basis;
      int result;
      ConcurrentHashMapV8.MapReduceKeysToIntTask rights;
      ConcurrentHashMapV8.MapReduceKeysToIntTask nextRight;

      MapReduceKeysToIntTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceKeysToIntTask nextRight, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Integer getRawResult() {
         return Integer.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToInt<? super K> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.IntByIntToInt reducer = this.reducer;
            if(this.reducer != null) {
               int r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceKeysToIntTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceKeysToIntTask<K, V> t = (ConcurrentHashMapV8.MapReduceKeysToIntTask)c;

                  for(ConcurrentHashMapV8.MapReduceKeysToIntTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceKeysToLongTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToLong transformer;
      final ConcurrentHashMapV8.LongByLongToLong reducer;
      final long basis;
      long result;
      ConcurrentHashMapV8.MapReduceKeysToLongTask rights;
      ConcurrentHashMapV8.MapReduceKeysToLongTask nextRight;

      MapReduceKeysToLongTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceKeysToLongTask nextRight, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Long getRawResult() {
         return Long.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToLong<? super K> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.LongByLongToLong reducer = this.reducer;
            if(this.reducer != null) {
               long r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceKeysToLongTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceKeysToLongTask<K, V> t = (ConcurrentHashMapV8.MapReduceKeysToLongTask)c;

                  for(ConcurrentHashMapV8.MapReduceKeysToLongTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceMappingsTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun transformer;
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.MapReduceMappingsTask rights;
      ConcurrentHashMapV8.MapReduceMappingsTask nextRight;

      MapReduceMappingsTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceMappingsTask nextRight, ConcurrentHashMapV8.BiFun transformer, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer = this.reducer;
            if(this.reducer != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceMappingsTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, reducer)).fork();
               }

               U r = null;

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.key, p.val)) != null) {
                     r = r == null?u:reducer.apply(r, u);
                  }
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceMappingsTask<K, V, U> t = (ConcurrentHashMapV8.MapReduceMappingsTask)c;

                  for(ConcurrentHashMapV8.MapReduceMappingsTask<K, V, U> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     U sr = s.result;
                     if(s.result != null) {
                        U tr = t.result;
                        t.result = t.result == null?sr:reducer.apply(tr, sr);
                     }
                  }
               }
            }
         }

      }
   }

   static final class MapReduceMappingsToDoubleTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectByObjectToDouble transformer;
      final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
      final double basis;
      double result;
      ConcurrentHashMapV8.MapReduceMappingsToDoubleTask rights;
      ConcurrentHashMapV8.MapReduceMappingsToDoubleTask nextRight;

      MapReduceMappingsToDoubleTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceMappingsToDoubleTask nextRight, ConcurrentHashMapV8.ObjectByObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Double getRawResult() {
         return Double.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectByObjectToDouble<? super K, ? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.DoubleByDoubleToDouble reducer = this.reducer;
            if(this.reducer != null) {
               double r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToDoubleTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key, p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<K, V> t = (ConcurrentHashMapV8.MapReduceMappingsToDoubleTask)c;

                  for(ConcurrentHashMapV8.MapReduceMappingsToDoubleTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceMappingsToIntTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectByObjectToInt transformer;
      final ConcurrentHashMapV8.IntByIntToInt reducer;
      final int basis;
      int result;
      ConcurrentHashMapV8.MapReduceMappingsToIntTask rights;
      ConcurrentHashMapV8.MapReduceMappingsToIntTask nextRight;

      MapReduceMappingsToIntTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceMappingsToIntTask nextRight, ConcurrentHashMapV8.ObjectByObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Integer getRawResult() {
         return Integer.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectByObjectToInt<? super K, ? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.IntByIntToInt reducer = this.reducer;
            if(this.reducer != null) {
               int r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToIntTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key, p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceMappingsToIntTask<K, V> t = (ConcurrentHashMapV8.MapReduceMappingsToIntTask)c;

                  for(ConcurrentHashMapV8.MapReduceMappingsToIntTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceMappingsToLongTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectByObjectToLong transformer;
      final ConcurrentHashMapV8.LongByLongToLong reducer;
      final long basis;
      long result;
      ConcurrentHashMapV8.MapReduceMappingsToLongTask rights;
      ConcurrentHashMapV8.MapReduceMappingsToLongTask nextRight;

      MapReduceMappingsToLongTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceMappingsToLongTask nextRight, ConcurrentHashMapV8.ObjectByObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Long getRawResult() {
         return Long.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectByObjectToLong<? super K, ? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.LongByLongToLong reducer = this.reducer;
            if(this.reducer != null) {
               long r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceMappingsToLongTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.key, p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceMappingsToLongTask<K, V> t = (ConcurrentHashMapV8.MapReduceMappingsToLongTask)c;

                  for(ConcurrentHashMapV8.MapReduceMappingsToLongTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceValuesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun transformer;
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.MapReduceValuesTask rights;
      ConcurrentHashMapV8.MapReduceValuesTask nextRight;

      MapReduceValuesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceValuesTask nextRight, ConcurrentHashMapV8.Fun transformer, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super V, ? extends U> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.BiFun<? super U, ? super U, ? extends U> reducer = this.reducer;
            if(this.reducer != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, reducer)).fork();
               }

               U r = null;

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = this.advance()) != null) {
                  U u;
                  if((u = transformer.apply(p.val)) != null) {
                     r = r == null?u:reducer.apply(r, u);
                  }
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceValuesTask<K, V, U> t = (ConcurrentHashMapV8.MapReduceValuesTask)c;

                  for(ConcurrentHashMapV8.MapReduceValuesTask<K, V, U> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     U sr = s.result;
                     if(s.result != null) {
                        U tr = t.result;
                        t.result = t.result == null?sr:reducer.apply(tr, sr);
                     }
                  }
               }
            }
         }

      }
   }

   static final class MapReduceValuesToDoubleTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToDouble transformer;
      final ConcurrentHashMapV8.DoubleByDoubleToDouble reducer;
      final double basis;
      double result;
      ConcurrentHashMapV8.MapReduceValuesToDoubleTask rights;
      ConcurrentHashMapV8.MapReduceValuesToDoubleTask nextRight;

      MapReduceValuesToDoubleTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceValuesToDoubleTask nextRight, ConcurrentHashMapV8.ObjectToDouble transformer, double basis, ConcurrentHashMapV8.DoubleByDoubleToDouble reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Double getRawResult() {
         return Double.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToDouble<? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.DoubleByDoubleToDouble reducer = this.reducer;
            if(this.reducer != null) {
               double r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceValuesToDoubleTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceValuesToDoubleTask<K, V> t = (ConcurrentHashMapV8.MapReduceValuesToDoubleTask)c;

                  for(ConcurrentHashMapV8.MapReduceValuesToDoubleTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceValuesToIntTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToInt transformer;
      final ConcurrentHashMapV8.IntByIntToInt reducer;
      final int basis;
      int result;
      ConcurrentHashMapV8.MapReduceValuesToIntTask rights;
      ConcurrentHashMapV8.MapReduceValuesToIntTask nextRight;

      MapReduceValuesToIntTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceValuesToIntTask nextRight, ConcurrentHashMapV8.ObjectToInt transformer, int basis, ConcurrentHashMapV8.IntByIntToInt reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Integer getRawResult() {
         return Integer.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToInt<? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.IntByIntToInt reducer = this.reducer;
            if(this.reducer != null) {
               int r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceValuesToIntTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceValuesToIntTask<K, V> t = (ConcurrentHashMapV8.MapReduceValuesToIntTask)c;

                  for(ConcurrentHashMapV8.MapReduceValuesToIntTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static final class MapReduceValuesToLongTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.ObjectToLong transformer;
      final ConcurrentHashMapV8.LongByLongToLong reducer;
      final long basis;
      long result;
      ConcurrentHashMapV8.MapReduceValuesToLongTask rights;
      ConcurrentHashMapV8.MapReduceValuesToLongTask nextRight;

      MapReduceValuesToLongTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.MapReduceValuesToLongTask nextRight, ConcurrentHashMapV8.ObjectToLong transformer, long basis, ConcurrentHashMapV8.LongByLongToLong reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.transformer = transformer;
         this.basis = basis;
         this.reducer = reducer;
      }

      public final Long getRawResult() {
         return Long.valueOf(this.result);
      }

      public final void compute() {
         ConcurrentHashMapV8.ObjectToLong<? super V> transformer = this.transformer;
         if(this.transformer != null) {
            ConcurrentHashMapV8.LongByLongToLong reducer = this.reducer;
            if(this.reducer != null) {
               long r = this.basis;
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  this.addToPendingCount(1);
                  (this.rights = new ConcurrentHashMapV8.MapReduceValuesToLongTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, transformer, r, reducer)).fork();
               }

               while((p = this.advance()) != null) {
                  r = reducer.apply(r, transformer.apply(p.val));
               }

               this.result = r;

               for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                  ConcurrentHashMapV8.MapReduceValuesToLongTask<K, V> t = (ConcurrentHashMapV8.MapReduceValuesToLongTask)c;

                  for(ConcurrentHashMapV8.MapReduceValuesToLongTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                     t.result = reducer.apply(t.result, s.result);
                  }
               }
            }
         }

      }
   }

   static class Node implements Entry {
      final int hash;
      final Object key;
      volatile Object val;
      volatile ConcurrentHashMapV8.Node next;

      Node(int hash, Object key, Object val, ConcurrentHashMapV8.Node next) {
         this.hash = hash;
         this.key = key;
         this.val = val;
         this.next = next;
      }

      public final Object getKey() {
         return this.key;
      }

      public final Object getValue() {
         return this.val;
      }

      public final int hashCode() {
         return this.key.hashCode() ^ this.val.hashCode();
      }

      public final String toString() {
         return this.key + "=" + this.val;
      }

      public final Object setValue(Object value) {
         throw new UnsupportedOperationException();
      }

      public final boolean equals(Object o) {
         Object k;
         Object v;
         Entry<?, ?> e;
         boolean var10000;
         if(o instanceof Entry && (k = (e = (Entry)o).getKey()) != null && (v = e.getValue()) != null && (k == this.key || k.equals(this.key))) {
            Object u = this.val;
            if(v == this.val || v.equals(u)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      ConcurrentHashMapV8.Node find(int h, Object k) {
         ConcurrentHashMapV8.Node<K, V> e = this;
         if(k != null) {
            while(true) {
               if(e.hash == h) {
                  K ek = e.key;
                  if(e.key == k || ek != null && k.equals(ek)) {
                     return e;
                  }
               }

               if((e = e.next) == null) {
                  break;
               }
            }
         }

         return null;
      }
   }

   public interface ObjectByObjectToDouble {
      double apply(Object var1, Object var2);
   }

   public interface ObjectByObjectToInt {
      int apply(Object var1, Object var2);
   }

   public interface ObjectByObjectToLong {
      long apply(Object var1, Object var2);
   }

   public interface ObjectToDouble {
      double apply(Object var1);
   }

   public interface ObjectToInt {
      int apply(Object var1);
   }

   public interface ObjectToLong {
      long apply(Object var1);
   }

   static final class ReduceEntriesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun reducer;
      Entry result;
      ConcurrentHashMapV8.ReduceEntriesTask rights;
      ConcurrentHashMapV8.ReduceEntriesTask nextRight;

      ReduceEntriesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.ReduceEntriesTask nextRight, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.reducer = reducer;
      }

      public final Entry getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<Entry<K, V>, Entry<K, V>, ? extends Entry<K, V>> reducer = this.reducer;
         if(this.reducer != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMapV8.ReduceEntriesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, reducer)).fork();
            }

            ConcurrentHashMapV8.Node<K, V> p;
            for(r = null; (p = this.advance()) != null; r = (Entry)(r == null?p:(Entry)reducer.apply(r, p))) {
               ;
            }

            this.result = r;

            for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
               ConcurrentHashMapV8.ReduceEntriesTask<K, V> t = (ConcurrentHashMapV8.ReduceEntriesTask)c;

               for(ConcurrentHashMapV8.ReduceEntriesTask<K, V> s = t.rights; s != null; s = t.rights = s.nextRight) {
                  Entry<K, V> sr = s.result;
                  if(s.result != null) {
                     Entry<K, V> tr = t.result;
                     t.result = t.result == null?sr:(Entry)reducer.apply(tr, sr);
                  }
               }
            }
         }

      }
   }

   static final class ReduceKeysTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.ReduceKeysTask rights;
      ConcurrentHashMapV8.ReduceKeysTask nextRight;

      ReduceKeysTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.ReduceKeysTask nextRight, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<? super K, ? super K, ? extends K> reducer = this.reducer;
         if(this.reducer != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMapV8.ReduceKeysTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, reducer)).fork();
            }

            ConcurrentHashMapV8.Node<K, V> p;
            K u;
            for(r = null; (p = this.advance()) != null; r = r == null?u:(u == null?r:reducer.apply(r, u))) {
               u = p.key;
            }

            this.result = r;

            for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
               u = (ConcurrentHashMapV8.ReduceKeysTask)c;

               for(ConcurrentHashMapV8.ReduceKeysTask<K, V> s = u.rights; s != null; s = u.rights = s.nextRight) {
                  K sr = s.result;
                  if(s.result != null) {
                     K tr = u.result;
                     u.result = u.result == null?sr:reducer.apply(tr, sr);
                  }
               }
            }
         }

      }
   }

   static final class ReduceValuesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun reducer;
      Object result;
      ConcurrentHashMapV8.ReduceValuesTask rights;
      ConcurrentHashMapV8.ReduceValuesTask nextRight;

      ReduceValuesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.ReduceValuesTask nextRight, ConcurrentHashMapV8.BiFun reducer) {
         super(p, b, i, f, t);
         this.nextRight = nextRight;
         this.reducer = reducer;
      }

      public final Object getRawResult() {
         return this.result;
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<? super V, ? super V, ? extends V> reducer = this.reducer;
         if(this.reducer != null) {
            int i = this.baseIndex;

            while(this.batch > 0) {
               int f = this.baseLimit;
               int h;
               if((h = this.baseLimit + i >>> 1) <= i) {
                  break;
               }

               this.addToPendingCount(1);
               (this.rights = new ConcurrentHashMapV8.ReduceValuesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, this.rights, reducer)).fork();
            }

            ConcurrentHashMapV8.Node<K, V> p;
            V v;
            for(r = null; (p = this.advance()) != null; r = r == null?v:reducer.apply(r, v)) {
               v = p.val;
            }

            this.result = r;

            for(CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
               v = (ConcurrentHashMapV8.ReduceValuesTask)c;

               for(ConcurrentHashMapV8.ReduceValuesTask<K, V> s = v.rights; s != null; s = v.rights = s.nextRight) {
                  V sr = s.result;
                  if(s.result != null) {
                     V tr = v.result;
                     v.result = v.result == null?sr:reducer.apply(tr, sr);
                  }
               }
            }
         }

      }
   }

   static final class ReservationNode extends ConcurrentHashMapV8.Node {
      ReservationNode() {
         super(-3, (Object)null, (Object)null, (ConcurrentHashMapV8.Node)null);
      }

      ConcurrentHashMapV8.Node find(int h, Object k) {
         return null;
      }
   }

   static final class SearchEntriesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun searchFunction;
      final AtomicReference result;

      SearchEntriesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun searchFunction, AtomicReference result) {
         super(p, b, i, f, t);
         this.searchFunction = searchFunction;
         this.result = result;
      }

      public final Object getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<Entry<K, V>, ? extends U> searchFunction = this.searchFunction;
         if(this.searchFunction != null) {
            AtomicReference<U> result = this.result;
            if(this.result != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  if(result.get() != null) {
                     return;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.SearchEntriesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, searchFunction, result)).fork();
               }

               while(result.get() == null) {
                  ConcurrentHashMapV8.Node<K, V> p;
                  if((p = this.advance()) == null) {
                     this.propagateCompletion();
                     break;
                  }

                  U u;
                  if((u = searchFunction.apply(p)) != null) {
                     if(result.compareAndSet((Object)null, u)) {
                        this.quietlyCompleteRoot();
                     }

                     return;
                  }
               }
            }
         }

      }
   }

   static final class SearchKeysTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun searchFunction;
      final AtomicReference result;

      SearchKeysTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun searchFunction, AtomicReference result) {
         super(p, b, i, f, t);
         this.searchFunction = searchFunction;
         this.result = result;
      }

      public final Object getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super K, ? extends U> searchFunction = this.searchFunction;
         if(this.searchFunction != null) {
            AtomicReference<U> result = this.result;
            if(this.result != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  if(result.get() != null) {
                     return;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.SearchKeysTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, searchFunction, result)).fork();
               }

               while(result.get() == null) {
                  ConcurrentHashMapV8.Node<K, V> p;
                  if((p = this.advance()) == null) {
                     this.propagateCompletion();
                     break;
                  }

                  U u;
                  if((u = searchFunction.apply(p.key)) != null) {
                     if(result.compareAndSet((Object)null, u)) {
                        this.quietlyCompleteRoot();
                     }
                     break;
                  }
               }
            }
         }

      }
   }

   static final class SearchMappingsTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.BiFun searchFunction;
      final AtomicReference result;

      SearchMappingsTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.BiFun searchFunction, AtomicReference result) {
         super(p, b, i, f, t);
         this.searchFunction = searchFunction;
         this.result = result;
      }

      public final Object getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         ConcurrentHashMapV8.BiFun<? super K, ? super V, ? extends U> searchFunction = this.searchFunction;
         if(this.searchFunction != null) {
            AtomicReference<U> result = this.result;
            if(this.result != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  if(result.get() != null) {
                     return;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.SearchMappingsTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, searchFunction, result)).fork();
               }

               while(result.get() == null) {
                  ConcurrentHashMapV8.Node<K, V> p;
                  if((p = this.advance()) == null) {
                     this.propagateCompletion();
                     break;
                  }

                  U u;
                  if((u = searchFunction.apply(p.key, p.val)) != null) {
                     if(result.compareAndSet((Object)null, u)) {
                        this.quietlyCompleteRoot();
                     }
                     break;
                  }
               }
            }
         }

      }
   }

   static final class SearchValuesTask extends ConcurrentHashMapV8.BulkTask {
      final ConcurrentHashMapV8.Fun searchFunction;
      final AtomicReference result;

      SearchValuesTask(ConcurrentHashMapV8.BulkTask p, int b, int i, int f, ConcurrentHashMapV8.Node[] t, ConcurrentHashMapV8.Fun searchFunction, AtomicReference result) {
         super(p, b, i, f, t);
         this.searchFunction = searchFunction;
         this.result = result;
      }

      public final Object getRawResult() {
         return this.result.get();
      }

      public final void compute() {
         ConcurrentHashMapV8.Fun<? super V, ? extends U> searchFunction = this.searchFunction;
         if(this.searchFunction != null) {
            AtomicReference<U> result = this.result;
            if(this.result != null) {
               int i = this.baseIndex;

               while(this.batch > 0) {
                  int f = this.baseLimit;
                  int h;
                  if((h = this.baseLimit + i >>> 1) <= i) {
                     break;
                  }

                  if(result.get() != null) {
                     return;
                  }

                  this.addToPendingCount(1);
                  (new ConcurrentHashMapV8.SearchValuesTask(this, this.batch >>>= 1, this.baseLimit = h, f, this.tab, searchFunction, result)).fork();
               }

               while(result.get() == null) {
                  ConcurrentHashMapV8.Node<K, V> p;
                  if((p = this.advance()) == null) {
                     this.propagateCompletion();
                     break;
                  }

                  U u;
                  if((u = searchFunction.apply(p.val)) != null) {
                     if(result.compareAndSet((Object)null, u)) {
                        this.quietlyCompleteRoot();
                     }
                     break;
                  }
               }
            }
         }

      }
   }

   static class Segment extends ReentrantLock implements Serializable {
      private static final long serialVersionUID = 2249069246763182397L;
      final float loadFactor;

      Segment(float lf) {
         this.loadFactor = lf;
      }
   }

   static class Traverser {
      ConcurrentHashMapV8.Node[] tab;
      ConcurrentHashMapV8.Node next;
      int index;
      int baseIndex;
      int baseLimit;
      final int baseSize;

      Traverser(ConcurrentHashMapV8.Node[] tab, int size, int index, int limit) {
         this.tab = tab;
         this.baseSize = size;
         this.baseIndex = this.index = index;
         this.baseLimit = limit;
         this.next = null;
      }

      final ConcurrentHashMapV8.Node advance() {
         ConcurrentHashMapV8.Node<K, V> e = this.next;
         if(this.next != null) {
            e = e.next;
         }

         while(true) {
            if(e != null) {
               return this.next = e;
            }

            if(this.baseIndex >= this.baseLimit) {
               break;
            }

            ConcurrentHashMapV8.Node<K, V>[] t = this.tab;
            if(this.tab == null) {
               break;
            }

            int n;
            int var10000 = n = t.length;
            int i = this.index;
            if(var10000 <= this.index || i < 0) {
               break;
            }

            if((e = ConcurrentHashMapV8.tabAt(t, this.index)) != null && e.hash < 0) {
               if(e instanceof ConcurrentHashMapV8.ForwardingNode) {
                  this.tab = ((ConcurrentHashMapV8.ForwardingNode)e).nextTable;
                  e = null;
                  continue;
               }

               if(e instanceof ConcurrentHashMapV8.TreeBin) {
                  e = ((ConcurrentHashMapV8.TreeBin)e).first;
               } else {
                  e = null;
               }
            }

            if((this.index += this.baseSize) >= n) {
               this.index = ++this.baseIndex;
            }
         }

         return this.next = null;
      }
   }

   static final class TreeBin extends ConcurrentHashMapV8.Node {
      ConcurrentHashMapV8.TreeNode root;
      volatile ConcurrentHashMapV8.TreeNode first;
      volatile Thread waiter;
      volatile int lockState;
      static final int WRITER = 1;
      static final int WAITER = 2;
      static final int READER = 4;
      private static final Unsafe U;
      private static final long LOCKSTATE;

      TreeBin(ConcurrentHashMapV8.TreeNode b) {
         super(-2, (Object)null, (Object)null, (ConcurrentHashMapV8.Node)null);
         this.first = b;
         ConcurrentHashMapV8.TreeNode<K, V> r = null;

         ConcurrentHashMapV8.TreeNode<K, V> next;
         for(ConcurrentHashMapV8.TreeNode<K, V> x = b; x != null; x = next) {
            next = (ConcurrentHashMapV8.TreeNode)x.next;
            x.left = x.right = null;
            if(r == null) {
               x.parent = null;
               x.red = false;
               r = x;
            } else {
               Object key = x.key;
               int hash = x.hash;
               Class<?> kc = null;
               ConcurrentHashMapV8.TreeNode<K, V> p = r;

               int dir;
               ConcurrentHashMapV8.TreeNode<K, V> xp;
               while(true) {
                  int ph = p.hash;
                  if(p.hash > hash) {
                     dir = -1;
                  } else if(ph < hash) {
                     dir = 1;
                  } else if(kc == null && (kc = ConcurrentHashMapV8.comparableClassFor(key)) == null) {
                     dir = 0;
                  } else {
                     dir = ConcurrentHashMapV8.compareComparables(kc, key, p.key);
                  }

                  xp = p;
                  if((p = dir <= 0?p.left:p.right) == null) {
                     break;
                  }
               }

               x.parent = xp;
               if(dir <= 0) {
                  xp.left = x;
               } else {
                  xp.right = x;
               }

               r = balanceInsertion(r, x);
            }
         }

         this.root = r;
      }

      private final void lockRoot() {
         if(!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
            this.contendedLock();
         }

      }

      private final void unlockRoot() {
         this.lockState = 0;
      }

      private final void contendedLock() {
         boolean waiting = false;

         while(true) {
            int s = this.lockState;
            if((this.lockState & 1) == 0) {
               if(U.compareAndSwapInt(this, LOCKSTATE, s, 1)) {
                  break;
               }
            } else if((s & 2) == 0) {
               if(U.compareAndSwapInt(this, LOCKSTATE, s, s | 2)) {
                  waiting = true;
                  this.waiter = Thread.currentThread();
               }
            } else if(waiting) {
               LockSupport.park(this);
            }
         }

         if(waiting) {
            this.waiter = null;
         }

      }

      final ConcurrentHashMapV8.Node find(int h, Object k) {
         if(k != null) {
            for(ConcurrentHashMapV8.Node<K, V> e = this.first; e != null; e = e.next) {
               int s = this.lockState;
               if((this.lockState & 3) != 0) {
                  if(e.hash == h) {
                     K ek = e.key;
                     if(e.key == k || ek != null && k.equals(ek)) {
                        return e;
                     }
                  }
               } else if(U.compareAndSwapInt(this, LOCKSTATE, s, s + 4)) {
                  boolean var14 = false;

                  ConcurrentHashMapV8.TreeNode<K, V> p;
                  try {
                     var14 = true;
                     ConcurrentHashMapV8.TreeNode r = this.root;
                     p = this.root == null?null:r.findTreeNode(h, k, (Class)null);
                     var14 = false;
                  } finally {
                     if(var14) {
                        int ls;
                        while(true) {
                           ls = this.lockState;
                           if(U.compareAndSwapInt(this, LOCKSTATE, this.lockState, ls - 4)) {
                              break;
                           }
                        }

                        if(ls == 6) {
                           Thread w = this.waiter;
                           if(this.waiter != null) {
                              LockSupport.unpark(w);
                           }
                        }

                     }
                  }

                  int ls;
                  while(true) {
                     ls = this.lockState;
                     if(U.compareAndSwapInt(this, LOCKSTATE, this.lockState, ls - 4)) {
                        break;
                     }
                  }

                  if(ls == 6) {
                     Thread w = this.waiter;
                     if(this.waiter != null) {
                        LockSupport.unpark(w);
                     }
                  }

                  return p;
               }
            }
         }

         return null;
      }

      final ConcurrentHashMapV8.TreeNode putTreeVal(int h, Object k, Object v) {
         Class<?> kc = null;
         ConcurrentHashMapV8.TreeNode<K, V> p = this.root;

         while(true) {
            if(p == null) {
               this.first = this.root = new ConcurrentHashMapV8.TreeNode(h, k, v, (ConcurrentHashMapV8.Node)null, (ConcurrentHashMapV8.TreeNode)null);
            } else {
               int ph = p.hash;
               int dir;
               if(p.hash > h) {
                  dir = -1;
               } else if(ph < h) {
                  dir = 1;
               } else {
                  K pk = p.key;
                  if(p.key == k || pk != null && k.equals(pk)) {
                     return p;
                  }

                  if(kc == null && (kc = ConcurrentHashMapV8.comparableClassFor(k)) == null || (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) == 0) {
                     if(p.left == null) {
                        dir = 1;
                     } else {
                        ConcurrentHashMapV8.TreeNode<K, V> pr = p.right;
                        ConcurrentHashMapV8.TreeNode<K, V> q;
                        if(p.right != null && (q = pr.findTreeNode(h, k, kc)) != null) {
                           return q;
                        }

                        dir = -1;
                     }
                  }
               }

               ConcurrentHashMapV8.TreeNode<K, V> xp = p;
               if((p = dir < 0?p.left:p.right) != null) {
                  continue;
               }

               ConcurrentHashMapV8.TreeNode<K, V> f = this.first;
               ConcurrentHashMapV8.TreeNode<K, V> x;
               this.first = x = new ConcurrentHashMapV8.TreeNode(h, k, v, f, xp);
               if(f != null) {
                  f.prev = x;
               }

               if(dir < 0) {
                  xp.left = x;
               } else {
                  xp.right = x;
               }

               if(!xp.red) {
                  x.red = true;
               } else {
                  this.lockRoot();

                  try {
                     this.root = balanceInsertion(this.root, x);
                  } finally {
                     this.unlockRoot();
                  }
               }
            }

            assert checkInvariants(this.root);

            return null;
         }
      }

      final boolean removeTreeNode(ConcurrentHashMapV8.TreeNode p) {
         ConcurrentHashMapV8.TreeNode<K, V> next = (ConcurrentHashMapV8.TreeNode)p.next;
         ConcurrentHashMapV8.TreeNode<K, V> pred = p.prev;
         if(pred == null) {
            this.first = next;
         } else {
            pred.next = next;
         }

         if(next != null) {
            next.prev = pred;
         }

         if(this.first == null) {
            this.root = null;
            return true;
         } else {
            ConcurrentHashMapV8.TreeNode<K, V> r = this.root;
            if(this.root != null && r.right != null) {
               ConcurrentHashMapV8.TreeNode<K, V> rl = r.left;
               if(r.left != null && rl.left != null) {
                  this.lockRoot();

                  try {
                     ConcurrentHashMapV8.TreeNode<K, V> pl = p.left;
                     ConcurrentHashMapV8.TreeNode<K, V> pr = p.right;
                     ConcurrentHashMapV8.TreeNode<K, V> replacement;
                     if(pl != null && pr != null) {
                        ConcurrentHashMapV8.TreeNode<K, V> s = pr;

                        while(true) {
                           ConcurrentHashMapV8.TreeNode<K, V> sl = s.left;
                           if(s.left == null) {
                              boolean c = s.red;
                              s.red = p.red;
                              p.red = c;
                              ConcurrentHashMapV8.TreeNode<K, V> sr = s.right;
                              ConcurrentHashMapV8.TreeNode<K, V> pp = p.parent;
                              if(s == pr) {
                                 p.parent = s;
                                 s.right = p;
                              } else {
                                 ConcurrentHashMapV8.TreeNode<K, V> sp = s.parent;
                                 if((p.parent = sp) != null) {
                                    if(s == sp.left) {
                                       sp.left = p;
                                    } else {
                                       sp.right = p;
                                    }
                                 }

                                 s.right = pr;
                                 pr.parent = s;
                              }

                              p.left = null;
                              s.left = pl;
                              pl.parent = s;
                              if((p.right = sr) != null) {
                                 sr.parent = p;
                              }

                              if((s.parent = pp) == null) {
                                 r = s;
                              } else if(p == pp.left) {
                                 pp.left = s;
                              } else {
                                 pp.right = s;
                              }

                              if(sr != null) {
                                 replacement = sr;
                              } else {
                                 replacement = p;
                              }
                              break;
                           }

                           s = sl;
                        }
                     } else if(pl != null) {
                        replacement = pl;
                     } else if(pr != null) {
                        replacement = pr;
                     } else {
                        replacement = p;
                     }

                     if(replacement != p) {
                        ConcurrentHashMapV8.TreeNode<K, V> pp = replacement.parent = p.parent;
                        if(pp == null) {
                           r = replacement;
                        } else if(p == pp.left) {
                           pp.left = replacement;
                        } else {
                           pp.right = replacement;
                        }

                        p.left = p.right = p.parent = null;
                     }

                     this.root = p.red?r:balanceDeletion(r, replacement);
                     if(p == replacement) {
                        ConcurrentHashMapV8.TreeNode<K, V> pp = p.parent;
                        if(p.parent != null) {
                           if(p == pp.left) {
                              pp.left = null;
                           } else if(p == pp.right) {
                              pp.right = null;
                           }

                           p.parent = null;
                        }
                     }
                  } finally {
                     this.unlockRoot();
                  }

                  assert checkInvariants(this.root);

                  return false;
               }
            }

            return true;
         }
      }

      static ConcurrentHashMapV8.TreeNode rotateLeft(ConcurrentHashMapV8.TreeNode root, ConcurrentHashMapV8.TreeNode p) {
         if(p != null) {
            ConcurrentHashMapV8.TreeNode<K, V> r = p.right;
            if(p.right != null) {
               ConcurrentHashMapV8.TreeNode<K, V> rl;
               if((rl = p.right = r.left) != null) {
                  rl.parent = p;
               }

               ConcurrentHashMapV8.TreeNode<K, V> pp;
               if((pp = r.parent = p.parent) == null) {
                  root = r;
                  r.red = false;
               } else if(pp.left == p) {
                  pp.left = r;
               } else {
                  pp.right = r;
               }

               r.left = p;
               p.parent = r;
            }
         }

         return root;
      }

      static ConcurrentHashMapV8.TreeNode rotateRight(ConcurrentHashMapV8.TreeNode root, ConcurrentHashMapV8.TreeNode p) {
         if(p != null) {
            ConcurrentHashMapV8.TreeNode<K, V> l = p.left;
            if(p.left != null) {
               ConcurrentHashMapV8.TreeNode<K, V> lr;
               if((lr = p.left = l.right) != null) {
                  lr.parent = p;
               }

               ConcurrentHashMapV8.TreeNode<K, V> pp;
               if((pp = l.parent = p.parent) == null) {
                  root = l;
                  l.red = false;
               } else if(pp.right == p) {
                  pp.right = l;
               } else {
                  pp.left = l;
               }

               l.right = p;
               p.parent = l;
            }
         }

         return root;
      }

      static ConcurrentHashMapV8.TreeNode balanceInsertion(ConcurrentHashMapV8.TreeNode root, ConcurrentHashMapV8.TreeNode x) {
         x.red = true;

         while(true) {
            ConcurrentHashMapV8.TreeNode<K, V> xp = x.parent;
            if(x.parent == null) {
               x.red = false;
               return x;
            }

            if(!xp.red) {
               break;
            }

            ConcurrentHashMapV8.TreeNode<K, V> xpp = xp.parent;
            if(xp.parent == null) {
               break;
            }

            ConcurrentHashMapV8.TreeNode<K, V> xppl = xpp.left;
            if(xp == xpp.left) {
               ConcurrentHashMapV8.TreeNode<K, V> xppr = xpp.right;
               if(xpp.right != null && xppr.red) {
                  xppr.red = false;
                  xp.red = false;
                  xpp.red = true;
                  x = xpp;
               } else {
                  if(x == xp.right) {
                     x = xp;
                     root = rotateLeft(root, xp);
                     xpp = (xp = xp.parent) == null?null:xp.parent;
                  }

                  if(xp != null) {
                     xp.red = false;
                     if(xpp != null) {
                        xpp.red = true;
                        root = rotateRight(root, xpp);
                     }
                  }
               }
            } else if(xppl != null && xppl.red) {
               xppl.red = false;
               xp.red = false;
               xpp.red = true;
               x = xpp;
            } else {
               if(x == xp.left) {
                  x = xp;
                  root = rotateRight(root, xp);
                  xpp = (xp = xp.parent) == null?null:xp.parent;
               }

               if(xp != null) {
                  xp.red = false;
                  if(xpp != null) {
                     xpp.red = true;
                     root = rotateLeft(root, xpp);
                  }
               }
            }
         }

         return root;
      }

      static ConcurrentHashMapV8.TreeNode balanceDeletion(ConcurrentHashMapV8.TreeNode root, ConcurrentHashMapV8.TreeNode x) {
         while(x != null && x != root) {
            ConcurrentHashMapV8.TreeNode<K, V> xp = x.parent;
            if(x.parent == null) {
               x.red = false;
               return x;
            }

            if(x.red) {
               x.red = false;
               return root;
            }

            ConcurrentHashMapV8.TreeNode<K, V> xpl = xp.left;
            if(xp.left == x) {
               ConcurrentHashMapV8.TreeNode<K, V> xpr = xp.right;
               if(xp.right != null && xpr.red) {
                  xpr.red = false;
                  xp.red = true;
                  root = rotateLeft(root, xp);
                  xp = x.parent;
                  xpr = x.parent == null?null:xp.right;
               }

               if(xpr == null) {
                  x = xp;
               } else {
                  ConcurrentHashMapV8.TreeNode<K, V> sl = xpr.left;
                  ConcurrentHashMapV8.TreeNode<K, V> sr = xpr.right;
                  if(sr != null && sr.red || sl != null && sl.red) {
                     if(sr == null || !sr.red) {
                        if(sl != null) {
                           sl.red = false;
                        }

                        xpr.red = true;
                        root = rotateRight(root, xpr);
                        xp = x.parent;
                        xpr = x.parent == null?null:xp.right;
                     }

                     if(xpr != null) {
                        xpr.red = xp == null?false:xp.red;
                        sr = xpr.right;
                        if(xpr.right != null) {
                           sr.red = false;
                        }
                     }

                     if(xp != null) {
                        xp.red = false;
                        root = rotateLeft(root, xp);
                     }

                     x = root;
                  } else {
                     xpr.red = true;
                     x = xp;
                  }
               }
            } else {
               if(xpl != null && xpl.red) {
                  xpl.red = false;
                  xp.red = true;
                  root = rotateRight(root, xp);
                  xp = x.parent;
                  xpl = x.parent == null?null:xp.left;
               }

               if(xpl == null) {
                  x = xp;
               } else {
                  ConcurrentHashMapV8.TreeNode<K, V> sl = xpl.left;
                  ConcurrentHashMapV8.TreeNode<K, V> sr = xpl.right;
                  if(sl != null && sl.red || sr != null && sr.red) {
                     if(sl == null || !sl.red) {
                        if(sr != null) {
                           sr.red = false;
                        }

                        xpl.red = true;
                        root = rotateLeft(root, xpl);
                        xp = x.parent;
                        xpl = x.parent == null?null:xp.left;
                     }

                     if(xpl != null) {
                        xpl.red = xp == null?false:xp.red;
                        sl = xpl.left;
                        if(xpl.left != null) {
                           sl.red = false;
                        }
                     }

                     if(xp != null) {
                        xp.red = false;
                        root = rotateRight(root, xp);
                     }

                     x = root;
                  } else {
                     xpl.red = true;
                     x = xp;
                  }
               }
            }
         }

         return root;
      }

      static boolean checkInvariants(ConcurrentHashMapV8.TreeNode t) {
         ConcurrentHashMapV8.TreeNode<K, V> tp = t.parent;
         ConcurrentHashMapV8.TreeNode<K, V> tl = t.left;
         ConcurrentHashMapV8.TreeNode<K, V> tr = t.right;
         ConcurrentHashMapV8.TreeNode<K, V> tb = t.prev;
         ConcurrentHashMapV8.TreeNode<K, V> tn = (ConcurrentHashMapV8.TreeNode)t.next;
         return tb != null && tb.next != t?false:(tn != null && tn.prev != t?false:(tp != null && t != tp.left && t != tp.right?false:(tl == null || tl.parent == t && tl.hash <= t.hash?(tr == null || tr.parent == t && tr.hash >= t.hash?(t.red && tl != null && tl.red && tr != null && tr.red?false:(tl != null && !checkInvariants(tl)?false:tr == null || checkInvariants(tr))):false):false)));
      }

      static {
         try {
            U = ConcurrentHashMapV8.getUnsafe();
            Class<?> k = ConcurrentHashMapV8.TreeBin.class;
            LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }

   static final class TreeNode extends ConcurrentHashMapV8.Node {
      ConcurrentHashMapV8.TreeNode parent;
      ConcurrentHashMapV8.TreeNode left;
      ConcurrentHashMapV8.TreeNode right;
      ConcurrentHashMapV8.TreeNode prev;
      boolean red;

      TreeNode(int hash, Object key, Object val, ConcurrentHashMapV8.Node next, ConcurrentHashMapV8.TreeNode parent) {
         super(hash, key, val, next);
         this.parent = parent;
      }

      ConcurrentHashMapV8.Node find(int h, Object k) {
         return this.findTreeNode(h, k, (Class)null);
      }

      final ConcurrentHashMapV8.TreeNode findTreeNode(int h, Object k, Class kc) {
         if(k != null) {
            ConcurrentHashMapV8.TreeNode<K, V> p = this;

            while(true) {
               ConcurrentHashMapV8.TreeNode<K, V> pl = p.left;
               ConcurrentHashMapV8.TreeNode<K, V> pr = p.right;
               int ph = p.hash;
               if(p.hash > h) {
                  p = pl;
               } else if(ph < h) {
                  p = pr;
               } else {
                  K pk = p.key;
                  if(p.key == k || pk != null && k.equals(pk)) {
                     return p;
                  }

                  if(pl == null && pr == null) {
                     break;
                  }

                  int dir;
                  if((kc != null || (kc = ConcurrentHashMapV8.comparableClassFor(k)) != null) && (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) != 0) {
                     p = dir < 0?pl:pr;
                  } else if(pl == null) {
                     p = pr;
                  } else {
                     ConcurrentHashMapV8.TreeNode<K, V> q;
                     if(pr != null && (q = pr.findTreeNode(h, k, kc)) != null) {
                        return q;
                     }

                     p = pl;
                  }
               }

               if(p == null) {
                  break;
               }
            }
         }

         return null;
      }
   }

   static final class ValueIterator extends ConcurrentHashMapV8.BaseIterator implements Iterator, Enumeration {
      ValueIterator(ConcurrentHashMapV8.Node[] tab, int index, int size, int limit, ConcurrentHashMapV8 map) {
         super(tab, index, size, limit, map);
      }

      public final Object next() {
         ConcurrentHashMapV8.Node<K, V> p = this.next;
         if(this.next == null) {
            throw new NoSuchElementException();
         } else {
            V v = p.val;
            this.lastReturned = p;
            this.advance();
            return v;
         }
      }

      public final Object nextElement() {
         return this.next();
      }
   }

   static final class ValueSpliterator extends ConcurrentHashMapV8.Traverser implements ConcurrentHashMapV8.ConcurrentHashMapSpliterator {
      long est;

      ValueSpliterator(ConcurrentHashMapV8.Node[] tab, int size, int index, int limit, long est) {
         super(tab, size, index, limit);
         this.est = est;
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator trySplit() {
         int i = this.baseIndex;
         int f = this.baseLimit;
         int h;
         return (h = this.baseIndex + this.baseLimit >>> 1) <= i?null:new ConcurrentHashMapV8.ValueSpliterator(this.tab, this.baseSize, this.baseLimit = h, f, this.est >>>= 1);
      }

      public void forEachRemaining(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            while((p = this.advance()) != null) {
               action.apply(p.val);
            }

         }
      }

      public boolean tryAdvance(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V> p;
            if((p = this.advance()) == null) {
               return false;
            } else {
               action.apply(p.val);
               return true;
            }
         }
      }

      public long estimateSize() {
         return this.est;
      }
   }

   static final class ValuesView extends ConcurrentHashMapV8.CollectionView implements Collection, Serializable {
      private static final long serialVersionUID = 2249069246763182397L;

      ValuesView(ConcurrentHashMapV8 map) {
         super(map);
      }

      public final boolean contains(Object o) {
         return this.map.containsValue(o);
      }

      public final boolean remove(Object o) {
         if(o != null) {
            Iterator<V> it = this.iterator();

            while(it.hasNext()) {
               if(o.equals(it.next())) {
                  it.remove();
                  return true;
               }
            }
         }

         return false;
      }

      public final Iterator iterator() {
         ConcurrentHashMapV8<K, V> m = this.map;
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.ValueIterator(t, f, 0, f, m);
      }

      public final boolean add(Object e) {
         throw new UnsupportedOperationException();
      }

      public final boolean addAll(Collection c) {
         throw new UnsupportedOperationException();
      }

      public ConcurrentHashMapV8.ConcurrentHashMapSpliterator spliterator166() {
         ConcurrentHashMapV8<K, V> m = this.map;
         long n = m.sumCount();
         ConcurrentHashMapV8.Node<K, V>[] t = m.table;
         int f = m.table == null?0:t.length;
         return new ConcurrentHashMapV8.ValueSpliterator(t, f, 0, f, n < 0L?0L:n);
      }

      public void forEach(ConcurrentHashMapV8.Action action) {
         if(action == null) {
            throw new NullPointerException();
         } else {
            ConcurrentHashMapV8.Node<K, V>[] t = this.map.table;
            if(this.map.table != null) {
               ConcurrentHashMapV8.Traverser<K, V> it = new ConcurrentHashMapV8.Traverser(t, t.length, 0, t.length);

               ConcurrentHashMapV8.Node<K, V> p;
               while((p = it.advance()) != null) {
                  action.apply(p.val);
               }
            }

         }
      }
   }
}
