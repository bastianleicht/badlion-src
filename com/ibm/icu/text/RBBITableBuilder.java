package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.RBBINode;
import com.ibm.icu.text.RBBIRuleBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

class RBBITableBuilder {
   private RBBIRuleBuilder fRB;
   private int fRootIx;
   private List fDStates;

   RBBITableBuilder(RBBIRuleBuilder rb, int rootNodeIx) {
      this.fRootIx = rootNodeIx;
      this.fRB = rb;
      this.fDStates = new ArrayList();
   }

   void build() {
      if(this.fRB.fTreeRoots[this.fRootIx] != null) {
         this.fRB.fTreeRoots[this.fRootIx] = this.fRB.fTreeRoots[this.fRootIx].flattenVariables();
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("ftree") >= 0) {
            System.out.println("Parse tree after flattening variable references.");
            this.fRB.fTreeRoots[this.fRootIx].printTree(true);
         }

         if(this.fRB.fSetBuilder.sawBOF()) {
            RBBINode bofTop = new RBBINode(8);
            RBBINode bofLeaf = new RBBINode(3);
            bofTop.fLeftChild = bofLeaf;
            bofTop.fRightChild = this.fRB.fTreeRoots[this.fRootIx];
            bofLeaf.fParent = bofTop;
            bofLeaf.fVal = 2;
            this.fRB.fTreeRoots[this.fRootIx] = bofTop;
         }

         RBBINode cn = new RBBINode(8);
         cn.fLeftChild = this.fRB.fTreeRoots[this.fRootIx];
         this.fRB.fTreeRoots[this.fRootIx].fParent = cn;
         cn.fRightChild = new RBBINode(6);
         cn.fRightChild.fParent = cn;
         this.fRB.fTreeRoots[this.fRootIx] = cn;
         this.fRB.fTreeRoots[this.fRootIx].flattenSets();
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("stree") >= 0) {
            System.out.println("Parse tree after flattening Unicode Set references.");
            this.fRB.fTreeRoots[this.fRootIx].printTree(true);
         }

         this.calcNullable(this.fRB.fTreeRoots[this.fRootIx]);
         this.calcFirstPos(this.fRB.fTreeRoots[this.fRootIx]);
         this.calcLastPos(this.fRB.fTreeRoots[this.fRootIx]);
         this.calcFollowPos(this.fRB.fTreeRoots[this.fRootIx]);
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("pos") >= 0) {
            System.out.print("\n");
            this.printPosSets(this.fRB.fTreeRoots[this.fRootIx]);
         }

         if(this.fRB.fChainRules) {
            this.calcChainedFollowPos(this.fRB.fTreeRoots[this.fRootIx]);
         }

         if(this.fRB.fSetBuilder.sawBOF()) {
            this.bofFixup();
         }

         this.buildStateTable();
         this.flagAcceptingStates();
         this.flagLookAheadStates();
         this.flagTaggedStates();
         this.mergeRuleStatusVals();
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("states") >= 0) {
            this.printStates();
         }

      }
   }

   void calcNullable(RBBINode n) {
      if(n != null) {
         if(n.fType != 0 && n.fType != 6) {
            if(n.fType != 4 && n.fType != 5) {
               this.calcNullable(n.fLeftChild);
               this.calcNullable(n.fRightChild);
               if(n.fType == 9) {
                  n.fNullable = n.fLeftChild.fNullable || n.fRightChild.fNullable;
               } else if(n.fType == 8) {
                  n.fNullable = n.fLeftChild.fNullable && n.fRightChild.fNullable;
               } else if(n.fType != 10 && n.fType != 12) {
                  n.fNullable = false;
               } else {
                  n.fNullable = true;
               }

            } else {
               n.fNullable = true;
            }
         } else {
            n.fNullable = false;
         }
      }
   }

   void calcFirstPos(RBBINode n) {
      if(n != null) {
         if(n.fType != 3 && n.fType != 6 && n.fType != 4 && n.fType != 5) {
            this.calcFirstPos(n.fLeftChild);
            this.calcFirstPos(n.fRightChild);
            if(n.fType == 9) {
               n.fFirstPosSet.addAll(n.fLeftChild.fFirstPosSet);
               n.fFirstPosSet.addAll(n.fRightChild.fFirstPosSet);
            } else if(n.fType == 8) {
               n.fFirstPosSet.addAll(n.fLeftChild.fFirstPosSet);
               if(n.fLeftChild.fNullable) {
                  n.fFirstPosSet.addAll(n.fRightChild.fFirstPosSet);
               }
            } else if(n.fType == 10 || n.fType == 12 || n.fType == 11) {
               n.fFirstPosSet.addAll(n.fLeftChild.fFirstPosSet);
            }

         } else {
            n.fFirstPosSet.add(n);
         }
      }
   }

   void calcLastPos(RBBINode n) {
      if(n != null) {
         if(n.fType != 3 && n.fType != 6 && n.fType != 4 && n.fType != 5) {
            this.calcLastPos(n.fLeftChild);
            this.calcLastPos(n.fRightChild);
            if(n.fType == 9) {
               n.fLastPosSet.addAll(n.fLeftChild.fLastPosSet);
               n.fLastPosSet.addAll(n.fRightChild.fLastPosSet);
            } else if(n.fType == 8) {
               n.fLastPosSet.addAll(n.fRightChild.fLastPosSet);
               if(n.fRightChild.fNullable) {
                  n.fLastPosSet.addAll(n.fLeftChild.fLastPosSet);
               }
            } else if(n.fType == 10 || n.fType == 12 || n.fType == 11) {
               n.fLastPosSet.addAll(n.fLeftChild.fLastPosSet);
            }

         } else {
            n.fLastPosSet.add(n);
         }
      }
   }

   void calcFollowPos(RBBINode n) {
      if(n != null && n.fType != 3 && n.fType != 6) {
         this.calcFollowPos(n.fLeftChild);
         this.calcFollowPos(n.fRightChild);
         if(n.fType == 8) {
            for(RBBINode i : n.fLeftChild.fLastPosSet) {
               i.fFollowPos.addAll(n.fRightChild.fFirstPosSet);
            }
         }

         if(n.fType == 10 || n.fType == 11) {
            for(RBBINode i : n.fLastPosSet) {
               i.fFollowPos.addAll(n.fFirstPosSet);
            }
         }

      }
   }

   void calcChainedFollowPos(RBBINode tree) {
      List<RBBINode> endMarkerNodes = new ArrayList();
      List<RBBINode> leafNodes = new ArrayList();
      tree.findNodes(endMarkerNodes, 6);
      tree.findNodes(leafNodes, 3);
      RBBINode userRuleRoot = tree;
      if(this.fRB.fSetBuilder.sawBOF()) {
         userRuleRoot = tree.fLeftChild.fRightChild;
      }

      Assert.assrt(userRuleRoot != null);
      Set<RBBINode> matchStartNodes = userRuleRoot.fFirstPosSet;
      Iterator i$ = leafNodes.iterator();

      while(true) {
         RBBINode endNode;
         while(true) {
            if(!i$.hasNext()) {
               return;
            }

            RBBINode tNode = (RBBINode)i$.next();
            endNode = null;

            for(RBBINode endMarkerNode : endMarkerNodes) {
               if(tNode.fFollowPos.contains(endMarkerNode)) {
                  endNode = tNode;
                  break;
               }
            }

            if(endNode != null) {
               if(!this.fRB.fLBCMNoChain) {
                  break;
               }

               int c = this.fRB.fSetBuilder.getFirstChar(endNode.fVal);
               if(c == -1) {
                  break;
               }

               int cLBProp = UCharacter.getIntPropertyValue(c, 4104);
               if(cLBProp != 9) {
                  break;
               }
            }
         }

         for(RBBINode startNode : matchStartNodes) {
            if(startNode.fType == 3 && endNode.fVal == startNode.fVal) {
               endNode.fFollowPos.addAll(startNode.fFollowPos);
            }
         }
      }
   }

   void bofFixup() {
      RBBINode bofNode = this.fRB.fTreeRoots[this.fRootIx].fLeftChild.fLeftChild;
      Assert.assrt(bofNode.fType == 3);
      Assert.assrt(bofNode.fVal == 2);

      for(RBBINode startNode : this.fRB.fTreeRoots[this.fRootIx].fLeftChild.fRightChild.fFirstPosSet) {
         if(startNode.fType == 3 && startNode.fVal == bofNode.fVal) {
            bofNode.fFollowPos.addAll(startNode.fFollowPos);
         }
      }

   }

   void buildStateTable() {
      int lastInputSymbol = this.fRB.fSetBuilder.getNumCharCategories() - 1;
      RBBITableBuilder.RBBIStateDescriptor failState = new RBBITableBuilder.RBBIStateDescriptor(lastInputSymbol);
      this.fDStates.add(failState);
      RBBITableBuilder.RBBIStateDescriptor initialState = new RBBITableBuilder.RBBIStateDescriptor(lastInputSymbol);
      initialState.fPositions.addAll(this.fRB.fTreeRoots[this.fRootIx].fFirstPosSet);
      this.fDStates.add(initialState);

      while(true) {
         RBBITableBuilder.RBBIStateDescriptor T = null;

         for(int tx = 1; tx < this.fDStates.size(); ++tx) {
            RBBITableBuilder.RBBIStateDescriptor temp = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(tx);
            if(!temp.fMarked) {
               T = temp;
               break;
            }
         }

         if(T == null) {
            return;
         }

         T.fMarked = true;

         for(int a = 1; a <= lastInputSymbol; ++a) {
            Set<RBBINode> U = null;

            for(RBBINode p : T.fPositions) {
               if(p.fType == 3 && p.fVal == a) {
                  if(U == null) {
                     U = new HashSet();
                  }

                  U.addAll(p.fFollowPos);
               }
            }

            int ux = 0;
            boolean UinDstates = false;
            if(U != null) {
               Assert.assrt(U.size() > 0);

               for(int ix = 0; ix < this.fDStates.size(); ++ix) {
                  RBBITableBuilder.RBBIStateDescriptor temp2 = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(ix);
                  if(U.equals(temp2.fPositions)) {
                     U = temp2.fPositions;
                     ux = ix;
                     UinDstates = true;
                     break;
                  }
               }

               if(!UinDstates) {
                  RBBITableBuilder.RBBIStateDescriptor newState = new RBBITableBuilder.RBBIStateDescriptor(lastInputSymbol);
                  newState.fPositions = U;
                  this.fDStates.add(newState);
                  ux = this.fDStates.size() - 1;
               }

               T.fDtran[a] = ux;
            }
         }
      }
   }

   void flagAcceptingStates() {
      List<RBBINode> endMarkerNodes = new ArrayList();
      this.fRB.fTreeRoots[this.fRootIx].findNodes(endMarkerNodes, 6);

      for(int i = 0; i < ((List)endMarkerNodes).size(); ++i) {
         RBBINode endMarker = (RBBINode)endMarkerNodes.get(i);

         for(int n = 0; n < this.fDStates.size(); ++n) {
            RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(n);
            if(sd.fPositions.contains(endMarker)) {
               if(sd.fAccepting == 0) {
                  sd.fAccepting = endMarker.fVal;
                  if(sd.fAccepting == 0) {
                     sd.fAccepting = -1;
                  }
               }

               if(sd.fAccepting == -1 && endMarker.fVal != 0) {
                  sd.fAccepting = endMarker.fVal;
               }

               if(endMarker.fLookAheadEnd) {
                  sd.fLookAhead = sd.fAccepting;
               }
            }
         }
      }

   }

   void flagLookAheadStates() {
      List<RBBINode> lookAheadNodes = new ArrayList();
      this.fRB.fTreeRoots[this.fRootIx].findNodes(lookAheadNodes, 4);

      for(int i = 0; i < ((List)lookAheadNodes).size(); ++i) {
         RBBINode lookAheadNode = (RBBINode)lookAheadNodes.get(i);

         for(int n = 0; n < this.fDStates.size(); ++n) {
            RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(n);
            if(sd.fPositions.contains(lookAheadNode)) {
               sd.fLookAhead = lookAheadNode.fVal;
            }
         }
      }

   }

   void flagTaggedStates() {
      List<RBBINode> tagNodes = new ArrayList();
      this.fRB.fTreeRoots[this.fRootIx].findNodes(tagNodes, 5);

      for(int i = 0; i < ((List)tagNodes).size(); ++i) {
         RBBINode tagNode = (RBBINode)tagNodes.get(i);

         for(int n = 0; n < this.fDStates.size(); ++n) {
            RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(n);
            if(sd.fPositions.contains(tagNode)) {
               sd.fTagVals.add(Integer.valueOf(tagNode.fVal));
            }
         }
      }

   }

   void mergeRuleStatusVals() {
      if(this.fRB.fRuleStatusVals.size() == 0) {
         this.fRB.fRuleStatusVals.add(Integer.valueOf(1));
         this.fRB.fRuleStatusVals.add(Integer.valueOf(0));
         SortedSet<Integer> s0 = new TreeSet();
         Integer izero = Integer.valueOf(0);
         this.fRB.fStatusSets.put(s0, izero);
         SortedSet<Integer> s1 = new TreeSet();
         s1.add(izero);
         this.fRB.fStatusSets.put(s0, izero);
      }

      for(int n = 0; n < this.fDStates.size(); ++n) {
         RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(n);
         Set<Integer> statusVals = sd.fTagVals;
         Integer arrayIndexI = (Integer)this.fRB.fStatusSets.get(statusVals);
         if(arrayIndexI == null) {
            arrayIndexI = Integer.valueOf(this.fRB.fRuleStatusVals.size());
            this.fRB.fStatusSets.put(statusVals, arrayIndexI);
            this.fRB.fRuleStatusVals.add(Integer.valueOf(statusVals.size()));
            this.fRB.fRuleStatusVals.addAll(statusVals);
         }

         sd.fTagsIdx = arrayIndexI.intValue();
      }

   }

   void printPosSets(RBBINode n) {
      if(n != null) {
         RBBINode.printNode(n);
         System.out.print("         Nullable:  " + n.fNullable);
         System.out.print("         firstpos:  ");
         this.printSet(n.fFirstPosSet);
         System.out.print("         lastpos:   ");
         this.printSet(n.fLastPosSet);
         System.out.print("         followpos: ");
         this.printSet(n.fFollowPos);
         this.printPosSets(n.fLeftChild);
         this.printPosSets(n.fRightChild);
      }
   }

   int getTableSize() {
      int size = 0;
      if(this.fRB.fTreeRoots[this.fRootIx] == null) {
         return 0;
      } else {
         size = 16;
         int numRows = this.fDStates.size();
         int numCols = this.fRB.fSetBuilder.getNumCharCategories();
         int rowSize = 8 + 2 * numCols;

         for(size = size + numRows * rowSize; size % 8 > 0; ++size) {
            ;
         }

         return size;
      }
   }

   short[] exportTable() {
      if(this.fRB.fTreeRoots[this.fRootIx] == null) {
         return new short[0];
      } else {
         Assert.assrt(this.fRB.fSetBuilder.getNumCharCategories() < 32767 && this.fDStates.size() < 32767);
         int numStates = this.fDStates.size();
         int rowLen = 4 + this.fRB.fSetBuilder.getNumCharCategories();
         int tableSize = this.getTableSize() / 2;
         short[] table = new short[tableSize];
         table[0] = (short)(numStates >>> 16);
         table[1] = (short)(numStates & '\uffff');
         table[2] = (short)(rowLen >>> 16);
         table[3] = (short)(rowLen & '\uffff');
         int flags = 0;
         if(this.fRB.fLookAheadHardBreak) {
            flags |= 1;
         }

         if(this.fRB.fSetBuilder.sawBOF()) {
            flags |= 2;
         }

         table[4] = (short)(flags >>> 16);
         table[5] = (short)(flags & '\uffff');
         int numCharCategories = this.fRB.fSetBuilder.getNumCharCategories();

         for(int state = 0; state < numStates; ++state) {
            RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(state);
            int row = 8 + state * rowLen;
            Assert.assrt(-32768 < sd.fAccepting && sd.fAccepting <= 32767);
            Assert.assrt(-32768 < sd.fLookAhead && sd.fLookAhead <= 32767);
            table[row + 0] = (short)sd.fAccepting;
            table[row + 1] = (short)sd.fLookAhead;
            table[row + 2] = (short)sd.fTagsIdx;

            for(int col = 0; col < numCharCategories; ++col) {
               table[row + 4 + col] = (short)sd.fDtran[col];
            }
         }

         return table;
      }
   }

   void printSet(Collection s) {
      for(RBBINode n : s) {
         RBBINode.printInt(n.fSerialNum, 8);
      }

      System.out.println();
   }

   void printStates() {
      System.out.print("state |           i n p u t     s y m b o l s \n");
      System.out.print("      | Acc  LA    Tag");

      for(int c = 0; c < this.fRB.fSetBuilder.getNumCharCategories(); ++c) {
         RBBINode.printInt(c, 3);
      }

      System.out.print("\n");
      System.out.print("      |---------------");

      for(int var4 = 0; var4 < this.fRB.fSetBuilder.getNumCharCategories(); ++var4) {
         System.out.print("---");
      }

      System.out.print("\n");

      for(int n = 0; n < this.fDStates.size(); ++n) {
         RBBITableBuilder.RBBIStateDescriptor sd = (RBBITableBuilder.RBBIStateDescriptor)this.fDStates.get(n);
         RBBINode.printInt(n, 5);
         System.out.print(" | ");
         RBBINode.printInt(sd.fAccepting, 3);
         RBBINode.printInt(sd.fLookAhead, 4);
         RBBINode.printInt(sd.fTagsIdx, 6);
         System.out.print(" ");

         for(int var5 = 0; var5 < this.fRB.fSetBuilder.getNumCharCategories(); ++var5) {
            RBBINode.printInt(sd.fDtran[var5], 3);
         }

         System.out.print("\n");
      }

      System.out.print("\n\n");
   }

   void printRuleStatusTable() {
      int thisRecord = 0;
      int nextRecord = 0;
      List<Integer> tbl = this.fRB.fRuleStatusVals;
      System.out.print("index |  tags \n");
      System.out.print("-------------------\n");

      while(nextRecord < tbl.size()) {
         thisRecord = nextRecord;
         nextRecord = nextRecord + ((Integer)tbl.get(nextRecord)).intValue() + 1;
         RBBINode.printInt(thisRecord, 7);

         for(int i = thisRecord + 1; i < nextRecord; ++i) {
            int val = ((Integer)tbl.get(i)).intValue();
            RBBINode.printInt(val, 7);
         }

         System.out.print("\n");
      }

      System.out.print("\n\n");
   }

   static class RBBIStateDescriptor {
      boolean fMarked;
      int fAccepting;
      int fLookAhead;
      SortedSet fTagVals = new TreeSet();
      int fTagsIdx;
      Set fPositions = new HashSet();
      int[] fDtran;

      RBBIStateDescriptor(int maxInputSymbol) {
         this.fDtran = new int[maxInputSymbol + 1];
      }
   }
}
