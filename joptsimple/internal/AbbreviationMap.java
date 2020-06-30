package joptsimple.internal;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class AbbreviationMap {
   private String key;
   private Object value;
   private final Map children = new TreeMap();
   private int keysBeyond;

   public boolean contains(String aKey) {
      return this.get(aKey) != null;
   }

   public Object get(String aKey) {
      char[] chars = charsOf(aKey);
      AbbreviationMap<V> child = this;

      for(char each : chars) {
         child = (AbbreviationMap)child.children.get(Character.valueOf(each));
         if(child == null) {
            return null;
         }
      }

      return child.value;
   }

   public void put(String aKey, Object newValue) {
      if(newValue == null) {
         throw new NullPointerException();
      } else if(aKey.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] chars = charsOf(aKey);
         this.add(chars, newValue, 0, chars.length);
      }
   }

   public void putAll(Iterable keys, Object newValue) {
      for(String each : keys) {
         this.put(each, newValue);
      }

   }

   private boolean add(char[] chars, Object newValue, int offset, int length) {
      if(offset == length) {
         this.value = newValue;
         boolean wasAlreadyAKey = this.key != null;
         this.key = new String(chars);
         return !wasAlreadyAKey;
      } else {
         char nextChar = chars[offset];
         AbbreviationMap<V> child = (AbbreviationMap)this.children.get(Character.valueOf(nextChar));
         if(child == null) {
            child = new AbbreviationMap();
            this.children.put(Character.valueOf(nextChar), child);
         }

         boolean newKeyAdded = child.add(chars, newValue, offset + 1, length);
         if(newKeyAdded) {
            ++this.keysBeyond;
         }

         if(this.key == null) {
            this.value = this.keysBeyond > 1?null:newValue;
         }

         return newKeyAdded;
      }
   }

   public void remove(String aKey) {
      if(aKey.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] keyChars = charsOf(aKey);
         this.remove(keyChars, 0, keyChars.length);
      }
   }

   private boolean remove(char[] aKey, int offset, int length) {
      if(offset == length) {
         return this.removeAtEndOfKey();
      } else {
         char nextChar = aKey[offset];
         AbbreviationMap<V> child = (AbbreviationMap)this.children.get(Character.valueOf(nextChar));
         if(child != null && child.remove(aKey, offset + 1, length)) {
            --this.keysBeyond;
            if(child.keysBeyond == 0) {
               this.children.remove(Character.valueOf(nextChar));
            }

            if(this.keysBeyond == 1 && this.key == null) {
               this.setValueToThatOfOnlyChild();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void setValueToThatOfOnlyChild() {
      Entry<Character, AbbreviationMap<V>> entry = (Entry)this.children.entrySet().iterator().next();
      AbbreviationMap<V> onlyChild = (AbbreviationMap)entry.getValue();
      this.value = onlyChild.value;
   }

   private boolean removeAtEndOfKey() {
      if(this.key == null) {
         return false;
      } else {
         this.key = null;
         if(this.keysBeyond == 1) {
            this.setValueToThatOfOnlyChild();
         } else {
            this.value = null;
         }

         return true;
      }
   }

   public Map toJavaUtilMap() {
      Map<String, V> mappings = new TreeMap();
      this.addToMappings(mappings);
      return mappings;
   }

   private void addToMappings(Map mappings) {
      if(this.key != null) {
         mappings.put(this.key, this.value);
      }

      for(AbbreviationMap<V> each : this.children.values()) {
         each.addToMappings(mappings);
      }

   }

   private static char[] charsOf(String aKey) {
      char[] chars = new char[aKey.length()];
      aKey.getChars(0, aKey.length(), chars, 0);
      return chars;
   }
}
