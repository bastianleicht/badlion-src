package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaders;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class DefaultSpdyHeaders extends SpdyHeaders {
   private static final int BUCKET_SIZE = 17;
   private final DefaultSpdyHeaders.HeaderEntry[] entries = new DefaultSpdyHeaders.HeaderEntry[17];
   private final DefaultSpdyHeaders.HeaderEntry head = new DefaultSpdyHeaders.HeaderEntry(-1, (String)null, (String)null);

   private static int hash(String name) {
      int h = 0;

      for(int i = name.length() - 1; i >= 0; --i) {
         char c = name.charAt(i);
         if(c >= 65 && c <= 90) {
            c = (char)(c + 32);
         }

         h = 31 * h + c;
      }

      if(h > 0) {
         return h;
      } else if(h == Integer.MIN_VALUE) {
         return Integer.MAX_VALUE;
      } else {
         return -h;
      }
   }

   private static boolean eq(String name1, String name2) {
      int nameLen = name1.length();
      if(nameLen != name2.length()) {
         return false;
      } else {
         for(int i = nameLen - 1; i >= 0; --i) {
            char c1 = name1.charAt(i);
            char c2 = name2.charAt(i);
            if(c1 != c2) {
               if(c1 >= 65 && c1 <= 90) {
                  c1 = (char)(c1 + 32);
               }

               if(c2 >= 65 && c2 <= 90) {
                  c2 = (char)(c2 + 32);
               }

               if(c1 != c2) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private static int index(int hash) {
      return hash % 17;
   }

   DefaultSpdyHeaders() {
      this.head.before = this.head.after = this.head;
   }

   public SpdyHeaders add(String name, Object value) {
      String lowerCaseName = name.toLowerCase();
      SpdyCodecUtil.validateHeaderName(lowerCaseName);
      String strVal = toString(value);
      SpdyCodecUtil.validateHeaderValue(strVal);
      int h = hash(lowerCaseName);
      int i = index(h);
      this.add0(h, i, lowerCaseName, strVal);
      return this;
   }

   private void add0(int h, int i, String name, String value) {
      DefaultSpdyHeaders.HeaderEntry e = this.entries[i];
      DefaultSpdyHeaders.HeaderEntry newEntry;
      this.entries[i] = newEntry = new DefaultSpdyHeaders.HeaderEntry(h, name, value);
      newEntry.next = e;
      newEntry.addBefore(this.head);
   }

   public SpdyHeaders remove(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         String lowerCaseName = name.toLowerCase();
         int h = hash(lowerCaseName);
         int i = index(h);
         this.remove0(h, i, lowerCaseName);
         return this;
      }
   }

   private void remove0(int h, int i, String name) {
      DefaultSpdyHeaders.HeaderEntry e = this.entries[i];
      if(e != null) {
         while(e.hash == h && eq(name, e.key)) {
            e.remove();
            DefaultSpdyHeaders.HeaderEntry next = e.next;
            if(next == null) {
               this.entries[i] = null;
               return;
            }

            this.entries[i] = next;
            e = next;
         }

         while(true) {
            DefaultSpdyHeaders.HeaderEntry next = e.next;
            if(next == null) {
               return;
            }

            if(next.hash == h && eq(name, next.key)) {
               e.next = next.next;
               next.remove();
            } else {
               e = next;
            }
         }
      }
   }

   public SpdyHeaders set(String name, Object value) {
      String lowerCaseName = name.toLowerCase();
      SpdyCodecUtil.validateHeaderName(lowerCaseName);
      String strVal = toString(value);
      SpdyCodecUtil.validateHeaderValue(strVal);
      int h = hash(lowerCaseName);
      int i = index(h);
      this.remove0(h, i, lowerCaseName);
      this.add0(h, i, lowerCaseName, strVal);
      return this;
   }

   public SpdyHeaders set(String name, Iterable values) {
      if(values == null) {
         throw new NullPointerException("values");
      } else {
         String lowerCaseName = name.toLowerCase();
         SpdyCodecUtil.validateHeaderName(lowerCaseName);
         int h = hash(lowerCaseName);
         int i = index(h);
         this.remove0(h, i, lowerCaseName);

         for(Object v : values) {
            if(v == null) {
               break;
            }

            String strVal = toString(v);
            SpdyCodecUtil.validateHeaderValue(strVal);
            this.add0(h, i, lowerCaseName, strVal);
         }

         return this;
      }
   }

   public SpdyHeaders clear() {
      for(int i = 0; i < this.entries.length; ++i) {
         this.entries[i] = null;
      }

      this.head.before = this.head.after = this.head;
      return this;
   }

   public String get(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         int h = hash(name);
         int i = index(h);

         for(DefaultSpdyHeaders.HeaderEntry e = this.entries[i]; e != null; e = e.next) {
            if(e.hash == h && eq(name, e.key)) {
               return e.value;
            }
         }

         return null;
      }
   }

   public List getAll(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         LinkedList<String> values = new LinkedList();
         int h = hash(name);
         int i = index(h);

         for(DefaultSpdyHeaders.HeaderEntry e = this.entries[i]; e != null; e = e.next) {
            if(e.hash == h && eq(name, e.key)) {
               values.addFirst(e.value);
            }
         }

         return values;
      }
   }

   public List entries() {
      List<Entry<String, String>> all = new LinkedList();

      for(DefaultSpdyHeaders.HeaderEntry e = this.head.after; e != this.head; e = e.after) {
         all.add(e);
      }

      return all;
   }

   public Iterator iterator() {
      return new DefaultSpdyHeaders.HeaderIterator();
   }

   public boolean contains(String name) {
      return this.get(name) != null;
   }

   public Set names() {
      Set<String> names = new TreeSet();

      for(DefaultSpdyHeaders.HeaderEntry e = this.head.after; e != this.head; e = e.after) {
         names.add(e.key);
      }

      return names;
   }

   public SpdyHeaders add(String name, Iterable values) {
      SpdyCodecUtil.validateHeaderValue(name);
      int h = hash(name);
      int i = index(h);

      for(Object v : values) {
         String vstr = toString(v);
         SpdyCodecUtil.validateHeaderValue(vstr);
         this.add0(h, i, name, vstr);
      }

      return this;
   }

   public boolean isEmpty() {
      return this.head == this.head.after;
   }

   private static String toString(Object value) {
      return value == null?null:value.toString();
   }

   private static final class HeaderEntry implements Entry {
      final int hash;
      final String key;
      String value;
      DefaultSpdyHeaders.HeaderEntry next;
      DefaultSpdyHeaders.HeaderEntry before;
      DefaultSpdyHeaders.HeaderEntry after;

      HeaderEntry(int hash, String key, String value) {
         this.hash = hash;
         this.key = key;
         this.value = value;
      }

      void remove() {
         this.before.after = this.after;
         this.after.before = this.before;
      }

      void addBefore(DefaultSpdyHeaders.HeaderEntry e) {
         this.after = e;
         this.before = e.before;
         this.before.after = this;
         this.after.before = this;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String setValue(String value) {
         if(value == null) {
            throw new NullPointerException("value");
         } else {
            SpdyCodecUtil.validateHeaderValue(value);
            String oldValue = this.value;
            this.value = value;
            return oldValue;
         }
      }

      public String toString() {
         return this.key + '=' + this.value;
      }
   }

   private final class HeaderIterator implements Iterator {
      private DefaultSpdyHeaders.HeaderEntry current;

      private HeaderIterator() {
         this.current = DefaultSpdyHeaders.this.head;
      }

      public boolean hasNext() {
         return this.current.after != DefaultSpdyHeaders.this.head;
      }

      public Entry next() {
         this.current = this.current.after;
         if(this.current == DefaultSpdyHeaders.this.head) {
            throw new NoSuchElementException();
         } else {
            return this.current;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
