package com.ibm.icu.util;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class StringTrieBuilder {
   private StringTrieBuilder.State state = StringTrieBuilder.State.ADDING;
   /** @deprecated */
   protected StringBuilder strings = new StringBuilder();
   private StringTrieBuilder.Node root;
   private HashMap nodes = new HashMap();
   private StringTrieBuilder.ValueNode lookupFinalValueNode = new StringTrieBuilder.ValueNode();

   /** @deprecated */
   protected void addImpl(CharSequence s, int value) {
      if(this.state != StringTrieBuilder.State.ADDING) {
         throw new IllegalStateException("Cannot add (string, value) pairs after build().");
      } else if(s.length() > '\uffff') {
         throw new IndexOutOfBoundsException("The maximum string length is 0xffff.");
      } else {
         if(this.root == null) {
            this.root = this.createSuffixNode(s, 0, value);
         } else {
            this.root = this.root.add(this, s, 0, value);
         }

      }
   }

   /** @deprecated */
   protected final void buildImpl(StringTrieBuilder.Option buildOption) {
      switch(this.state) {
      case ADDING:
         if(this.root == null) {
            throw new IndexOutOfBoundsException("No (string, value) pairs were added.");
         } else if(buildOption == StringTrieBuilder.Option.FAST) {
            this.state = StringTrieBuilder.State.BUILDING_FAST;
         } else {
            this.state = StringTrieBuilder.State.BUILDING_SMALL;
         }
      default:
         this.root = this.root.register(this);
         this.root.markRightEdgesFirst(-1);
         this.root.write(this);
         this.state = StringTrieBuilder.State.BUILT;
         return;
      case BUILDING_FAST:
      case BUILDING_SMALL:
         throw new IllegalStateException("Builder failed and must be clear()ed.");
      case BUILT:
      }
   }

   /** @deprecated */
   protected void clearImpl() {
      this.strings.setLength(0);
      this.nodes.clear();
      this.root = null;
      this.state = StringTrieBuilder.State.ADDING;
   }

   private final StringTrieBuilder.Node registerNode(StringTrieBuilder.Node newNode) {
      if(this.state == StringTrieBuilder.State.BUILDING_FAST) {
         return newNode;
      } else {
         StringTrieBuilder.Node oldNode = (StringTrieBuilder.Node)this.nodes.get(newNode);
         if(oldNode != null) {
            return oldNode;
         } else {
            oldNode = (StringTrieBuilder.Node)this.nodes.put(newNode, newNode);

            assert oldNode == null;

            return newNode;
         }
      }
   }

   private final StringTrieBuilder.ValueNode registerFinalValue(int value) {
      this.lookupFinalValueNode.setFinalValue(value);
      StringTrieBuilder.Node oldNode = (StringTrieBuilder.Node)this.nodes.get(this.lookupFinalValueNode);
      if(oldNode != null) {
         return (StringTrieBuilder.ValueNode)oldNode;
      } else {
         StringTrieBuilder.ValueNode newNode = new StringTrieBuilder.ValueNode(value);
         oldNode = (StringTrieBuilder.Node)this.nodes.put(newNode, newNode);

         assert oldNode == null;

         return newNode;
      }
   }

   private StringTrieBuilder.ValueNode createSuffixNode(CharSequence s, int start, int sValue) {
      StringTrieBuilder.ValueNode node = this.registerFinalValue(sValue);
      if(start < s.length()) {
         int offset = this.strings.length();
         this.strings.append(s, start, s.length());
         node = new StringTrieBuilder.LinearMatchNode(this.strings, offset, s.length() - start, node);
      }

      return node;
   }

   /** @deprecated */
   protected abstract boolean matchNodesCanHaveValues();

   /** @deprecated */
   protected abstract int getMaxBranchLinearSubNodeLength();

   /** @deprecated */
   protected abstract int getMinLinearMatch();

   /** @deprecated */
   protected abstract int getMaxLinearMatchLength();

   /** @deprecated */
   protected abstract int write(int var1);

   /** @deprecated */
   protected abstract int write(int var1, int var2);

   /** @deprecated */
   protected abstract int writeValueAndFinal(int var1, boolean var2);

   /** @deprecated */
   protected abstract int writeValueAndType(boolean var1, int var2, int var3);

   /** @deprecated */
   protected abstract int writeDeltaTo(int var1);

   private static final class BranchHeadNode extends StringTrieBuilder.ValueNode {
      private int length;
      private StringTrieBuilder.Node next;

      public BranchHeadNode(int len, StringTrieBuilder.Node subNode) {
         this.length = len;
         this.next = subNode;
      }

      public int hashCode() {
         return (248302782 + this.length) * 37 + this.next.hashCode();
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.BranchHeadNode o = (StringTrieBuilder.BranchHeadNode)other;
            return this.length == o.length && this.next == o.next;
         }
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.offset = edgeNumber = this.next.markRightEdgesFirst(edgeNumber);
         }

         return edgeNumber;
      }

      public void write(StringTrieBuilder builder) {
         this.next.write(builder);
         if(this.length <= builder.getMinLinearMatch()) {
            this.offset = builder.writeValueAndType(this.hasValue, this.value, this.length - 1);
         } else {
            builder.write(this.length - 1);
            this.offset = builder.writeValueAndType(this.hasValue, this.value, 0);
         }

      }
   }

   private abstract static class BranchNode extends StringTrieBuilder.Node {
      protected int hash;
      protected int firstEdgeNumber;

      public int hashCode() {
         return this.hash;
      }
   }

   private static final class DynamicBranchNode extends StringTrieBuilder.ValueNode {
      private StringBuilder chars = new StringBuilder();
      private ArrayList equal = new ArrayList();

      public void add(char c, StringTrieBuilder.Node node) {
         int i = this.find(c);
         this.chars.insert(i, c);
         this.equal.add(i, node);
      }

      public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
         if(start == s.length()) {
            if(this.hasValue) {
               throw new IllegalArgumentException("Duplicate string.");
            } else {
               this.setValue(sValue);
               return this;
            }
         } else {
            char c = s.charAt(start++);
            int i = this.find(c);
            if(i < this.chars.length() && c == this.chars.charAt(i)) {
               this.equal.set(i, ((StringTrieBuilder.Node)this.equal.get(i)).add(builder, s, start, sValue));
            } else {
               this.chars.insert(i, c);
               this.equal.add(i, builder.createSuffixNode(s, start, sValue));
            }

            return this;
         }
      }

      public StringTrieBuilder.Node register(StringTrieBuilder builder) {
         StringTrieBuilder.Node subNode = this.register(builder, 0, this.chars.length());
         StringTrieBuilder.BranchHeadNode head = new StringTrieBuilder.BranchHeadNode(this.chars.length(), subNode);
         StringTrieBuilder.Node result = head;
         if(this.hasValue) {
            if(builder.matchNodesCanHaveValues()) {
               head.setValue(this.value);
            } else {
               result = new StringTrieBuilder.IntermediateValueNode(this.value, builder.registerNode(head));
            }
         }

         return builder.registerNode(result);
      }

      private StringTrieBuilder.Node register(StringTrieBuilder builder, int start, int limit) {
         int length = limit - start;
         if(length > builder.getMaxBranchLinearSubNodeLength()) {
            int middle = start + length / 2;
            return builder.registerNode(new StringTrieBuilder.SplitBranchNode(this.chars.charAt(middle), this.register(builder, start, middle), this.register(builder, middle, limit)));
         } else {
            StringTrieBuilder.ListBranchNode listNode = new StringTrieBuilder.ListBranchNode(length);

            while(true) {
               char c = this.chars.charAt(start);
               StringTrieBuilder.Node node = (StringTrieBuilder.Node)this.equal.get(start);
               if(node.getClass() == StringTrieBuilder.ValueNode.class) {
                  listNode.add(c, ((StringTrieBuilder.ValueNode)node).value);
               } else {
                  listNode.add(c, node.register(builder));
               }

               ++start;
               if(start >= limit) {
                  break;
               }
            }

            return builder.registerNode(listNode);
         }
      }

      private int find(char c) {
         int start = 0;
         int limit = this.chars.length();

         while(start < limit) {
            int i = (start + limit) / 2;
            char middleChar = this.chars.charAt(i);
            if(c < middleChar) {
               limit = i;
            } else {
               if(c == middleChar) {
                  return i;
               }

               start = i + 1;
            }
         }

         return start;
      }
   }

   private static final class IntermediateValueNode extends StringTrieBuilder.ValueNode {
      private StringTrieBuilder.Node next;

      public IntermediateValueNode(int v, StringTrieBuilder.Node nextNode) {
         this.next = nextNode;
         this.setValue(v);
      }

      public int hashCode() {
         return (82767594 + this.value) * 37 + this.next.hashCode();
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.IntermediateValueNode o = (StringTrieBuilder.IntermediateValueNode)other;
            return this.next == o.next;
         }
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.offset = edgeNumber = this.next.markRightEdgesFirst(edgeNumber);
         }

         return edgeNumber;
      }

      public void write(StringTrieBuilder builder) {
         this.next.write(builder);
         this.offset = builder.writeValueAndFinal(this.value, false);
      }
   }

   private static final class LinearMatchNode extends StringTrieBuilder.ValueNode {
      private CharSequence strings;
      private int stringOffset;
      private int length;
      private StringTrieBuilder.Node next;
      private int hash;

      public LinearMatchNode(CharSequence builderStrings, int sOffset, int len, StringTrieBuilder.Node nextNode) {
         this.strings = builderStrings;
         this.stringOffset = sOffset;
         this.length = len;
         this.next = nextNode;
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.LinearMatchNode o = (StringTrieBuilder.LinearMatchNode)other;
            if(this.length == o.length && this.next == o.next) {
               int i = this.stringOffset;
               int j = o.stringOffset;

               for(int limit = this.stringOffset + this.length; i < limit; ++j) {
                  if(this.strings.charAt(i) != this.strings.charAt(j)) {
                     return false;
                  }

                  ++i;
               }

               return true;
            } else {
               return false;
            }
         }
      }

      public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
         if(start == s.length()) {
            if(this.hasValue) {
               throw new IllegalArgumentException("Duplicate string.");
            } else {
               this.setValue(sValue);
               return this;
            }
         } else {
            int limit = this.stringOffset + this.length;

            for(int i = this.stringOffset; i < limit; ++start) {
               if(start == s.length()) {
                  int prefixLength = i - this.stringOffset;
                  StringTrieBuilder.LinearMatchNode suffixNode = new StringTrieBuilder.LinearMatchNode(this.strings, i, this.length - prefixLength, this.next);
                  suffixNode.setValue(sValue);
                  this.length = prefixLength;
                  this.next = suffixNode;
                  return this;
               }

               char thisChar = this.strings.charAt(i);
               char newChar = s.charAt(start);
               if(thisChar != newChar) {
                  StringTrieBuilder.DynamicBranchNode branchNode = new StringTrieBuilder.DynamicBranchNode();
                  StringTrieBuilder.Node result;
                  StringTrieBuilder.Node thisSuffixNode;
                  if(i == this.stringOffset) {
                     if(this.hasValue) {
                        branchNode.setValue(this.value);
                        this.value = 0;
                        this.hasValue = false;
                     }

                     ++this.stringOffset;
                     --this.length;
                     thisSuffixNode = (StringTrieBuilder.Node)(this.length > 0?this:this.next);
                     result = branchNode;
                  } else if(i == limit - 1) {
                     --this.length;
                     thisSuffixNode = this.next;
                     this.next = branchNode;
                     result = this;
                  } else {
                     int prefixLength = i - this.stringOffset;
                     ++i;
                     thisSuffixNode = new StringTrieBuilder.LinearMatchNode(this.strings, i, this.length - (prefixLength + 1), this.next);
                     this.length = prefixLength;
                     this.next = branchNode;
                     result = this;
                  }

                  StringTrieBuilder.ValueNode newSuffixNode = builder.createSuffixNode(s, start + 1, sValue);
                  branchNode.add(thisChar, thisSuffixNode);
                  branchNode.add(newChar, newSuffixNode);
                  return result;
               }

               ++i;
            }

            this.next = this.next.add(builder, s, start, sValue);
            return this;
         }
      }

      public StringTrieBuilder.Node register(StringTrieBuilder builder) {
         this.next = this.next.register(builder);

         StringTrieBuilder.LinearMatchNode suffixNode;
         for(int maxLinearMatchLength = builder.getMaxLinearMatchLength(); this.length > maxLinearMatchLength; this.next = builder.registerNode(suffixNode)) {
            int nextOffset = this.stringOffset + this.length - maxLinearMatchLength;
            this.length -= maxLinearMatchLength;
            suffixNode = new StringTrieBuilder.LinearMatchNode(this.strings, nextOffset, maxLinearMatchLength, this.next);
            suffixNode.setHashCode();
         }

         StringTrieBuilder.Node result;
         if(this.hasValue && !builder.matchNodesCanHaveValues()) {
            int intermediateValue = this.value;
            this.value = 0;
            this.hasValue = false;
            this.setHashCode();
            result = new StringTrieBuilder.IntermediateValueNode(intermediateValue, builder.registerNode(this));
         } else {
            this.setHashCode();
            result = this;
         }

         return builder.registerNode(result);
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.offset = edgeNumber = this.next.markRightEdgesFirst(edgeNumber);
         }

         return edgeNumber;
      }

      public void write(StringTrieBuilder builder) {
         this.next.write(builder);
         builder.write(this.stringOffset, this.length);
         this.offset = builder.writeValueAndType(this.hasValue, this.value, builder.getMinLinearMatch() + this.length - 1);
      }

      private void setHashCode() {
         this.hash = (124151391 + this.length) * 37 + this.next.hashCode();
         if(this.hasValue) {
            this.hash = this.hash * 37 + this.value;
         }

         int i = this.stringOffset;

         for(int limit = this.stringOffset + this.length; i < limit; ++i) {
            this.hash = this.hash * 37 + this.strings.charAt(i);
         }

      }
   }

   private static final class ListBranchNode extends StringTrieBuilder.BranchNode {
      private StringTrieBuilder.Node[] equal;
      private int length;
      private int[] values;
      private char[] units;

      public ListBranchNode(int capacity) {
         this.hash = 165535188 + capacity;
         this.equal = new StringTrieBuilder.Node[capacity];
         this.values = new int[capacity];
         this.units = new char[capacity];
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.ListBranchNode o = (StringTrieBuilder.ListBranchNode)other;

            for(int i = 0; i < this.length; ++i) {
               if(this.units[i] != o.units[i] || this.values[i] != o.values[i] || this.equal[i] != o.equal[i]) {
                  return false;
               }
            }

            return true;
         }
      }

      public int hashCode() {
         return super.hashCode();
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.firstEdgeNumber = edgeNumber;
            int step = 0;
            int i = this.length;

            while(true) {
               --i;
               StringTrieBuilder.Node edge = this.equal[i];
               if(edge != null) {
                  edgeNumber = edge.markRightEdgesFirst(edgeNumber - step);
               }

               step = 1;
               if(i <= 0) {
                  break;
               }
            }

            this.offset = edgeNumber;
         }

         return edgeNumber;
      }

      public void write(StringTrieBuilder builder) {
         int unitNumber = this.length - 1;
         StringTrieBuilder.Node rightEdge = this.equal[unitNumber];
         int rightEdgeNumber = rightEdge == null?this.firstEdgeNumber:rightEdge.getOffset();

         while(true) {
            --unitNumber;
            if(this.equal[unitNumber] != null) {
               this.equal[unitNumber].writeUnlessInsideRightEdge(this.firstEdgeNumber, rightEdgeNumber, builder);
            }

            if(unitNumber <= 0) {
               break;
            }
         }

         unitNumber = this.length - 1;
         if(rightEdge == null) {
            builder.writeValueAndFinal(this.values[unitNumber], true);
         } else {
            rightEdge.write(builder);
         }

         this.offset = builder.write(this.units[unitNumber]);

         while(true) {
            --unitNumber;
            if(unitNumber < 0) {
               return;
            }

            int value;
            boolean isFinal;
            if(this.equal[unitNumber] == null) {
               value = this.values[unitNumber];
               isFinal = true;
            } else {
               assert this.equal[unitNumber].getOffset() > 0;

               value = this.offset - this.equal[unitNumber].getOffset();
               isFinal = false;
            }

            builder.writeValueAndFinal(value, isFinal);
            this.offset = builder.write(this.units[unitNumber]);
         }
      }

      public void add(int c, int value) {
         this.units[this.length] = (char)c;
         this.equal[this.length] = null;
         this.values[this.length] = value;
         ++this.length;
         this.hash = (this.hash * 37 + c) * 37 + value;
      }

      public void add(int c, StringTrieBuilder.Node node) {
         this.units[this.length] = (char)c;
         this.equal[this.length] = node;
         this.values[this.length] = 0;
         ++this.length;
         this.hash = (this.hash * 37 + c) * 37 + node.hashCode();
      }
   }

   private abstract static class Node {
      protected int offset = 0;

      public abstract int hashCode();

      public boolean equals(Object other) {
         return this == other || this.getClass() == other.getClass();
      }

      public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
         return this;
      }

      public StringTrieBuilder.Node register(StringTrieBuilder builder) {
         return this;
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.offset = edgeNumber;
         }

         return edgeNumber;
      }

      public abstract void write(StringTrieBuilder var1);

      public final void writeUnlessInsideRightEdge(int firstRight, int lastRight, StringTrieBuilder builder) {
         if(this.offset < 0 && (this.offset < lastRight || firstRight < this.offset)) {
            this.write(builder);
         }

      }

      public final int getOffset() {
         return this.offset;
      }
   }

   public static enum Option {
      FAST,
      SMALL;
   }

   private static final class SplitBranchNode extends StringTrieBuilder.BranchNode {
      private char unit;
      private StringTrieBuilder.Node lessThan;
      private StringTrieBuilder.Node greaterOrEqual;

      public SplitBranchNode(char middleUnit, StringTrieBuilder.Node lessThanNode, StringTrieBuilder.Node greaterOrEqualNode) {
         this.hash = ((206918985 + middleUnit) * 37 + lessThanNode.hashCode()) * 37 + greaterOrEqualNode.hashCode();
         this.unit = middleUnit;
         this.lessThan = lessThanNode;
         this.greaterOrEqual = greaterOrEqualNode;
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.SplitBranchNode o = (StringTrieBuilder.SplitBranchNode)other;
            return this.unit == o.unit && this.lessThan == o.lessThan && this.greaterOrEqual == o.greaterOrEqual;
         }
      }

      public int hashCode() {
         return super.hashCode();
      }

      public int markRightEdgesFirst(int edgeNumber) {
         if(this.offset == 0) {
            this.firstEdgeNumber = edgeNumber;
            edgeNumber = this.greaterOrEqual.markRightEdgesFirst(edgeNumber);
            this.offset = edgeNumber = this.lessThan.markRightEdgesFirst(edgeNumber - 1);
         }

         return edgeNumber;
      }

      public void write(StringTrieBuilder builder) {
         this.lessThan.writeUnlessInsideRightEdge(this.firstEdgeNumber, this.greaterOrEqual.getOffset(), builder);
         this.greaterOrEqual.write(builder);

         assert this.lessThan.getOffset() > 0;

         builder.writeDeltaTo(this.lessThan.getOffset());
         this.offset = builder.write(this.unit);
      }
   }

   private static enum State {
      ADDING,
      BUILDING_FAST,
      BUILDING_SMALL,
      BUILT;
   }

   private static class ValueNode extends StringTrieBuilder.Node {
      protected boolean hasValue;
      protected int value;

      public ValueNode() {
      }

      public ValueNode(int v) {
         this.hasValue = true;
         this.value = v;
      }

      public final void setValue(int v) {
         assert !this.hasValue;

         this.hasValue = true;
         this.value = v;
      }

      private void setFinalValue(int v) {
         this.hasValue = true;
         this.value = v;
      }

      public int hashCode() {
         int hash = 1118481;
         if(this.hasValue) {
            hash = hash * 37 + this.value;
         }

         return hash;
      }

      public boolean equals(Object other) {
         if(this == other) {
            return true;
         } else if(!super.equals(other)) {
            return false;
         } else {
            StringTrieBuilder.ValueNode o = (StringTrieBuilder.ValueNode)other;
            return this.hasValue == o.hasValue && (!this.hasValue || this.value == o.value);
         }
      }

      public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
         if(start == s.length()) {
            throw new IllegalArgumentException("Duplicate string.");
         } else {
            StringTrieBuilder.ValueNode node = builder.createSuffixNode(s, start, sValue);
            node.setValue(this.value);
            return node;
         }
      }

      public void write(StringTrieBuilder builder) {
         this.offset = builder.writeValueAndFinal(this.value, true);
      }
   }
}
