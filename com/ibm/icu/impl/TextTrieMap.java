package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TextTrieMap {
   private TextTrieMap.Node _root = new TextTrieMap.Node();
   boolean _ignoreCase;

   public TextTrieMap(boolean ignoreCase) {
      this._ignoreCase = ignoreCase;
   }

   public TextTrieMap put(CharSequence text, Object val) {
      TextTrieMap.CharIterator chitr = new TextTrieMap.CharIterator(text, 0, this._ignoreCase);
      this._root.add(chitr, val);
      return this;
   }

   public Iterator get(String text) {
      return this.get(text, 0);
   }

   public Iterator get(CharSequence text, int start) {
      return this.get(text, start, (int[])null);
   }

   public Iterator get(CharSequence text, int start, int[] matchLen) {
      TextTrieMap.LongestMatchHandler<V> handler = new TextTrieMap.LongestMatchHandler();
      this.find(text, start, handler);
      if(matchLen != null && matchLen.length > 0) {
         matchLen[0] = handler.getMatchLength();
      }

      return handler.getMatches();
   }

   public void find(CharSequence text, TextTrieMap.ResultHandler handler) {
      this.find(text, 0, handler);
   }

   public void find(CharSequence text, int offset, TextTrieMap.ResultHandler handler) {
      TextTrieMap.CharIterator chitr = new TextTrieMap.CharIterator(text, offset, this._ignoreCase);
      this.find(this._root, chitr, handler);
   }

   private synchronized void find(TextTrieMap.Node node, TextTrieMap.CharIterator chitr, TextTrieMap.ResultHandler handler) {
      Iterator<V> values = node.values();
      if(values == null || handler.handlePrefixMatch(chitr.processedLength(), values)) {
         TextTrieMap<V>.Node nextMatch = node.findMatch(chitr);
         if(nextMatch != null) {
            this.find(nextMatch, chitr, handler);
         }

      }
   }

   private static char[] toCharArray(CharSequence text) {
      char[] array = new char[text.length()];

      for(int i = 0; i < array.length; ++i) {
         array[i] = text.charAt(i);
      }

      return array;
   }

   private static char[] subArray(char[] array, int start) {
      if(start == 0) {
         return array;
      } else {
         char[] sub = new char[array.length - start];
         System.arraycopy(array, start, sub, 0, sub.length);
         return sub;
      }
   }

   private static char[] subArray(char[] array, int start, int limit) {
      if(start == 0 && limit == array.length) {
         return array;
      } else {
         char[] sub = new char[limit - start];
         System.arraycopy(array, start, sub, 0, limit - start);
         return sub;
      }
   }

   public static class CharIterator implements Iterator {
      private boolean _ignoreCase;
      private CharSequence _text;
      private int _nextIdx;
      private int _startIdx;
      private Character _remainingChar;

      CharIterator(CharSequence text, int offset, boolean ignoreCase) {
         this._text = text;
         this._nextIdx = this._startIdx = offset;
         this._ignoreCase = ignoreCase;
      }

      public boolean hasNext() {
         return this._nextIdx != this._text.length() || this._remainingChar != null;
      }

      public Character next() {
         if(this._nextIdx == this._text.length() && this._remainingChar == null) {
            return null;
         } else {
            Character next;
            if(this._remainingChar != null) {
               next = this._remainingChar;
               this._remainingChar = null;
            } else if(this._ignoreCase) {
               int cp = UCharacter.foldCase(Character.codePointAt(this._text, this._nextIdx), true);
               this._nextIdx += Character.charCount(cp);
               char[] chars = Character.toChars(cp);
               next = Character.valueOf(chars[0]);
               if(chars.length == 2) {
                  this._remainingChar = Character.valueOf(chars[1]);
               }
            } else {
               next = Character.valueOf(this._text.charAt(this._nextIdx));
               ++this._nextIdx;
            }

            return next;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("remove() not supproted");
      }

      public int nextIndex() {
         return this._nextIdx;
      }

      public int processedLength() {
         if(this._remainingChar != null) {
            throw new IllegalStateException("In the middle of surrogate pair");
         } else {
            return this._nextIdx - this._startIdx;
         }
      }
   }

   private static class LongestMatchHandler implements TextTrieMap.ResultHandler {
      private Iterator matches;
      private int length;

      private LongestMatchHandler() {
         this.matches = null;
         this.length = 0;
      }

      public boolean handlePrefixMatch(int matchLength, Iterator values) {
         if(matchLength > this.length) {
            this.length = matchLength;
            this.matches = values;
         }

         return true;
      }

      public Iterator getMatches() {
         return this.matches;
      }

      public int getMatchLength() {
         return this.length;
      }
   }

   private class Node {
      private char[] _text;
      private List _values;
      private List _children;

      private Node() {
      }

      private Node(char[] text, List values, List children) {
         this._text = text;
         this._values = values;
         this._children = children;
      }

      public Iterator values() {
         return this._values == null?null:this._values.iterator();
      }

      public void add(TextTrieMap.CharIterator chitr, Object value) {
         StringBuilder buf = new StringBuilder();

         while(chitr.hasNext()) {
            buf.append(chitr.next());
         }

         this.add(TextTrieMap.toCharArray(buf), 0, value);
      }

      public TextTrieMap.Node findMatch(TextTrieMap.CharIterator chitr) {
         if(this._children == null) {
            return null;
         } else if(!chitr.hasNext()) {
            return null;
         } else {
            TextTrieMap<V>.Node match = null;
            Character ch = chitr.next();

            for(TextTrieMap<V>.Node child : this._children) {
               if(ch.charValue() < child._text[0]) {
                  break;
               }

               if(ch.charValue() == child._text[0]) {
                  if(child.matchFollowing(chitr)) {
                     match = child;
                  }
                  break;
               }
            }

            return match;
         }
      }

      private void add(char[] text, int offset, Object value) {
         if(text.length == offset) {
            this._values = this.addValue(this._values, value);
         } else if(this._children == null) {
            this._children = new LinkedList();
            TextTrieMap<V>.Node child = TextTrieMap.this.new Node(TextTrieMap.subArray(text, offset), this.addValue((List)null, value), (List)null);
            this._children.add(child);
         } else {
            ListIterator<TextTrieMap<V>.Node> litr = this._children.listIterator();

            while(litr.hasNext()) {
               TextTrieMap<V>.Node next = (TextTrieMap.Node)litr.next();
               if(text[offset] < next._text[0]) {
                  litr.previous();
                  break;
               }

               if(text[offset] == next._text[0]) {
                  int matchLen = next.lenMatches(text, offset);
                  if(matchLen == next._text.length) {
                     next.add(text, offset + matchLen, value);
                  } else {
                     next.split(matchLen);
                     next.add(text, offset + matchLen, value);
                  }

                  return;
               }
            }

            litr.add(TextTrieMap.this.new Node(TextTrieMap.subArray(text, offset), this.addValue((List)null, value), (List)null));
         }
      }

      private boolean matchFollowing(TextTrieMap.CharIterator chitr) {
         boolean matched = true;

         for(int idx = 1; idx < this._text.length; ++idx) {
            if(!chitr.hasNext()) {
               matched = false;
               break;
            }

            Character ch = chitr.next();
            if(ch.charValue() != this._text[idx]) {
               matched = false;
               break;
            }
         }

         return matched;
      }

      private int lenMatches(char[] text, int offset) {
         int textLen = text.length - offset;
         int limit = this._text.length < textLen?this._text.length:textLen;

         int len;
         for(len = 0; len < limit && this._text[len] == text[offset + len]; ++len) {
            ;
         }

         return len;
      }

      private void split(int offset) {
         char[] childText = TextTrieMap.subArray(this._text, offset);
         this._text = TextTrieMap.subArray(this._text, 0, offset);
         TextTrieMap<V>.Node child = TextTrieMap.this.new Node(childText, this._values, this._children);
         this._values = null;
         this._children = new LinkedList();
         this._children.add(child);
      }

      private List addValue(List list, Object value) {
         if(list == null) {
            list = new LinkedList();
         }

         ((List)list).add(value);
         return (List)list;
      }
   }

   public interface ResultHandler {
      boolean handlePrefixMatch(int var1, Iterator var2);
   }
}
