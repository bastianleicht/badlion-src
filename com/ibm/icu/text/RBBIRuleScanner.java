package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.RBBINode;
import com.ibm.icu.text.RBBIRuleBuilder;
import com.ibm.icu.text.RBBIRuleParseTable;
import com.ibm.icu.text.RBBISymbolTable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.text.ParsePosition;
import java.util.HashMap;

class RBBIRuleScanner {
   private static final int kStackSize = 100;
   RBBIRuleBuilder fRB;
   int fScanIndex;
   int fNextIndex;
   boolean fQuoteMode;
   int fLineNum;
   int fCharNum;
   int fLastChar;
   RBBIRuleScanner.RBBIRuleChar fC = new RBBIRuleScanner.RBBIRuleChar();
   String fVarName;
   short[] fStack = new short[100];
   int fStackPtr;
   RBBINode[] fNodeStack = new RBBINode[100];
   int fNodeStackPtr;
   boolean fReverseRule;
   boolean fLookAheadRule;
   RBBISymbolTable fSymbolTable;
   HashMap fSetTable = new HashMap();
   UnicodeSet[] fRuleSets = new UnicodeSet[10];
   int fRuleNum;
   int fOptionStart;
   private static String gRuleSet_rule_char_pattern = "[^[\\p{Z}\\u0020-\\u007f]-[\\p{L}]-[\\p{N}]]";
   private static String gRuleSet_name_char_pattern = "[_\\p{L}\\p{N}]";
   private static String gRuleSet_digit_char_pattern = "[0-9]";
   private static String gRuleSet_name_start_char_pattern = "[_\\p{L}]";
   private static String gRuleSet_white_space_pattern = "[\\p{Pattern_White_Space}]";
   private static String kAny = "any";
   static final int chNEL = 133;
   static final int chLS = 8232;

   RBBIRuleScanner(RBBIRuleBuilder rb) {
      this.fRB = rb;
      this.fLineNum = 1;
      this.fRuleSets[3] = new UnicodeSet(gRuleSet_rule_char_pattern);
      this.fRuleSets[4] = new UnicodeSet(gRuleSet_white_space_pattern);
      this.fRuleSets[1] = new UnicodeSet(gRuleSet_name_char_pattern);
      this.fRuleSets[2] = new UnicodeSet(gRuleSet_name_start_char_pattern);
      this.fRuleSets[0] = new UnicodeSet(gRuleSet_digit_char_pattern);
      this.fSymbolTable = new RBBISymbolTable(this, rb.fRules);
   }

   boolean doParseActions(int action) {
      RBBINode n = null;
      boolean returnVal = true;
      switch(action) {
      case 1:
         n = this.fNodeStack[this.fNodeStackPtr];
         if(n.fLeftChild == null) {
            this.error(66058);
            returnVal = false;
         }
         break;
      case 2:
         n = this.pushNewNode(0);
         this.findSetFor(kAny, n, (UnicodeSet)null);
         n.fFirstPos = this.fScanIndex;
         n.fLastPos = this.fNextIndex;
         n.fText = this.fRB.fRules.substring(n.fFirstPos, n.fLastPos);
         break;
      case 3:
         this.fixOpStack(1);
         RBBINode startExprNode = this.fNodeStack[this.fNodeStackPtr - 2];
         RBBINode varRefNode = this.fNodeStack[this.fNodeStackPtr - 1];
         RBBINode RHSExprNode = this.fNodeStack[this.fNodeStackPtr];
         RHSExprNode.fFirstPos = startExprNode.fFirstPos;
         RHSExprNode.fLastPos = this.fScanIndex;
         RHSExprNode.fText = this.fRB.fRules.substring(RHSExprNode.fFirstPos, RHSExprNode.fLastPos);
         varRefNode.fLeftChild = RHSExprNode;
         RHSExprNode.fParent = varRefNode;
         this.fSymbolTable.addEntry(varRefNode.fText, varRefNode);
         this.fNodeStackPtr -= 3;
         break;
      case 4:
         this.fixOpStack(1);
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("rtree") >= 0) {
            this.printNodeStack("end of rule");
         }

         Assert.assrt(this.fNodeStackPtr == 1);
         if(this.fLookAheadRule) {
            RBBINode thisRule = this.fNodeStack[this.fNodeStackPtr];
            RBBINode endNode = this.pushNewNode(6);
            RBBINode catNode = this.pushNewNode(8);
            this.fNodeStackPtr -= 2;
            catNode.fLeftChild = thisRule;
            catNode.fRightChild = endNode;
            this.fNodeStack[this.fNodeStackPtr] = catNode;
            endNode.fVal = this.fRuleNum;
            endNode.fLookAheadEnd = true;
         }

         int destRules = this.fReverseRule?1:this.fRB.fDefaultTree;
         if(this.fRB.fTreeRoots[destRules] != null) {
            RBBINode thisRule = this.fNodeStack[this.fNodeStackPtr];
            RBBINode prevRules = this.fRB.fTreeRoots[destRules];
            RBBINode orNode = this.pushNewNode(9);
            orNode.fLeftChild = prevRules;
            prevRules.fParent = orNode;
            orNode.fRightChild = thisRule;
            thisRule.fParent = orNode;
            this.fRB.fTreeRoots[destRules] = orNode;
         } else {
            this.fRB.fTreeRoots[destRules] = this.fNodeStack[this.fNodeStackPtr];
         }

         this.fReverseRule = false;
         this.fLookAheadRule = false;
         this.fNodeStackPtr = 0;
         break;
      case 5:
         n = this.fNodeStack[this.fNodeStackPtr];
         if(n != null && n.fType == 2) {
            n.fLastPos = this.fScanIndex;
            n.fText = this.fRB.fRules.substring(n.fFirstPos + 1, n.fLastPos);
            n.fLeftChild = this.fSymbolTable.lookupNode(n.fText);
         } else {
            this.error(66049);
         }
         break;
      case 6:
         returnVal = false;
         break;
      case 7:
         this.fixOpStack(4);
         RBBINode operandNode = this.fNodeStack[this.fNodeStackPtr--];
         RBBINode catNode = this.pushNewNode(8);
         catNode.fLeftChild = operandNode;
         operandNode.fParent = catNode;
      case 8:
      case 13:
         break;
      case 9:
         this.fixOpStack(4);
         RBBINode operandNode = this.fNodeStack[this.fNodeStackPtr--];
         RBBINode orNode = this.pushNewNode(9);
         orNode.fLeftChild = operandNode;
         operandNode.fParent = orNode;
         break;
      case 10:
         this.fixOpStack(2);
         break;
      case 11:
         this.pushNewNode(7);
         ++this.fRuleNum;
         break;
      case 12:
         this.pushNewNode(15);
         break;
      case 14:
         String opt = this.fRB.fRules.substring(this.fOptionStart, this.fScanIndex);
         if(opt.equals("chain")) {
            this.fRB.fChainRules = true;
         } else if(opt.equals("LBCMNoChain")) {
            this.fRB.fLBCMNoChain = true;
         } else if(opt.equals("forward")) {
            this.fRB.fDefaultTree = 0;
         } else if(opt.equals("reverse")) {
            this.fRB.fDefaultTree = 1;
         } else if(opt.equals("safe_forward")) {
            this.fRB.fDefaultTree = 2;
         } else if(opt.equals("safe_reverse")) {
            this.fRB.fDefaultTree = 3;
         } else if(opt.equals("lookAheadHardBreak")) {
            this.fRB.fLookAheadHardBreak = true;
         } else {
            this.error(66061);
         }
         break;
      case 15:
         this.fOptionStart = this.fScanIndex;
         break;
      case 16:
         this.fReverseRule = true;
         break;
      case 17:
         n = this.pushNewNode(0);
         String s = String.valueOf((char)this.fC.fChar);
         this.findSetFor(s, n, (UnicodeSet)null);
         n.fFirstPos = this.fScanIndex;
         n.fLastPos = this.fNextIndex;
         n.fText = this.fRB.fRules.substring(n.fFirstPos, n.fLastPos);
         break;
      case 18:
         this.error(66052);
         returnVal = false;
         break;
      case 19:
         this.error(66054);
         returnVal = false;
         break;
      case 20:
         this.scanSet();
         break;
      case 21:
         n = this.pushNewNode(4);
         n.fVal = this.fRuleNum;
         n.fFirstPos = this.fScanIndex;
         n.fLastPos = this.fNextIndex;
         n.fText = this.fRB.fRules.substring(n.fFirstPos, n.fLastPos);
         this.fLookAheadRule = true;
         break;
      case 22:
         n = this.fNodeStack[this.fNodeStackPtr - 1];
         n.fFirstPos = this.fNextIndex;
         this.pushNewNode(7);
         break;
      case 23:
         n = this.pushNewNode(5);
         n.fVal = 0;
         n.fFirstPos = this.fScanIndex;
         n.fLastPos = this.fNextIndex;
         break;
      case 24:
         n = this.pushNewNode(2);
         n.fFirstPos = this.fScanIndex;
         break;
      case 25:
         n = this.fNodeStack[this.fNodeStackPtr];
         int v = UCharacter.digit((char)this.fC.fChar, 10);
         n.fVal = n.fVal * 10 + v;
         break;
      case 26:
         this.error(66062);
         returnVal = false;
         break;
      case 27:
         n = this.fNodeStack[this.fNodeStackPtr];
         n.fLastPos = this.fNextIndex;
         n.fText = this.fRB.fRules.substring(n.fFirstPos, n.fLastPos);
         break;
      case 28:
         RBBINode operandNode = this.fNodeStack[this.fNodeStackPtr--];
         RBBINode plusNode = this.pushNewNode(11);
         plusNode.fLeftChild = operandNode;
         operandNode.fParent = plusNode;
         break;
      case 29:
         RBBINode operandNode = this.fNodeStack[this.fNodeStackPtr--];
         RBBINode qNode = this.pushNewNode(12);
         qNode.fLeftChild = operandNode;
         operandNode.fParent = qNode;
         break;
      case 30:
         RBBINode operandNode = this.fNodeStack[this.fNodeStackPtr--];
         RBBINode starNode = this.pushNewNode(10);
         starNode.fLeftChild = operandNode;
         operandNode.fParent = starNode;
         break;
      case 31:
         this.error(66052);
         break;
      default:
         this.error(66049);
         returnVal = false;
      }

      return returnVal;
   }

   void error(int e) {
      String s = "Error " + e + " at line " + this.fLineNum + " column " + this.fCharNum;
      IllegalArgumentException ex = new IllegalArgumentException(s);
      throw ex;
   }

   void fixOpStack(int p) {
      while(true) {
         RBBINode n = this.fNodeStack[this.fNodeStackPtr - 1];
         if(n.fPrecedence == 0) {
            System.out.print("RBBIRuleScanner.fixOpStack, bad operator node");
            this.error(66049);
            return;
         }

         if(n.fPrecedence < p || n.fPrecedence <= 2) {
            if(p <= 2) {
               if(n.fPrecedence != p) {
                  this.error(66056);
               }

               this.fNodeStack[this.fNodeStackPtr - 1] = this.fNodeStack[this.fNodeStackPtr];
               --this.fNodeStackPtr;
            }

            return;
         }

         n.fRightChild = this.fNodeStack[this.fNodeStackPtr];
         this.fNodeStack[this.fNodeStackPtr].fParent = n;
         --this.fNodeStackPtr;
      }
   }

   void findSetFor(String s, RBBINode node, UnicodeSet setToAdopt) {
      RBBIRuleScanner.RBBISetTableEl el = (RBBIRuleScanner.RBBISetTableEl)this.fSetTable.get(s);
      if(el != null) {
         node.fLeftChild = el.val;
         Assert.assrt(node.fLeftChild.fType == 1);
      } else {
         if(setToAdopt == null) {
            if(s.equals(kAny)) {
               setToAdopt = new UnicodeSet(0, 1114111);
            } else {
               int c = UTF16.charAt((String)s, 0);
               setToAdopt = new UnicodeSet(c, c);
            }
         }

         RBBINode usetNode = new RBBINode(1);
         usetNode.fInputSet = setToAdopt;
         usetNode.fParent = node;
         node.fLeftChild = usetNode;
         usetNode.fText = s;
         this.fRB.fUSetNodes.add(usetNode);
         el = new RBBIRuleScanner.RBBISetTableEl();
         el.key = s;
         el.val = usetNode;
         this.fSetTable.put(el.key, el);
      }
   }

   static String stripRules(String rules) {
      StringBuilder strippedRules = new StringBuilder();
      int rulesLength = rules.length();
      int idx = 0;

      while(idx < rulesLength) {
         char ch = rules.charAt(idx++);
         if(ch == 35) {
            while(idx < rulesLength && ch != 13 && ch != 10 && ch != 133) {
               ch = rules.charAt(idx++);
            }
         }

         if(!UCharacter.isISOControl(ch)) {
            strippedRules.append(ch);
         }
      }

      return strippedRules.toString();
   }

   int nextCharLL() {
      if(this.fNextIndex >= this.fRB.fRules.length()) {
         return -1;
      } else {
         int ch = UTF16.charAt(this.fRB.fRules, this.fNextIndex);
         this.fNextIndex = UTF16.moveCodePointOffset((String)this.fRB.fRules, this.fNextIndex, 1);
         if(ch == 13 || ch == 133 || ch == 8232 || ch == 10 && this.fLastChar != 13) {
            ++this.fLineNum;
            this.fCharNum = 0;
            if(this.fQuoteMode) {
               this.error(66057);
               this.fQuoteMode = false;
            }
         } else if(ch != 10) {
            ++this.fCharNum;
         }

         this.fLastChar = ch;
         return ch;
      }
   }

   void nextChar(RBBIRuleScanner.RBBIRuleChar c) {
      this.fScanIndex = this.fNextIndex;
      c.fChar = this.nextCharLL();
      c.fEscaped = false;
      if(c.fChar == 39) {
         if(UTF16.charAt(this.fRB.fRules, this.fNextIndex) != 39) {
            this.fQuoteMode = !this.fQuoteMode;
            if(this.fQuoteMode) {
               c.fChar = 40;
            } else {
               c.fChar = 41;
            }

            c.fEscaped = false;
            return;
         }

         c.fChar = this.nextCharLL();
         c.fEscaped = true;
      }

      if(this.fQuoteMode) {
         c.fEscaped = true;
      } else {
         if(c.fChar == 35) {
            while(true) {
               c.fChar = this.nextCharLL();
               if(c.fChar == -1 || c.fChar == 13 || c.fChar == 10 || c.fChar == 133 || c.fChar == 8232) {
                  break;
               }
            }
         }

         if(c.fChar == -1) {
            return;
         }

         if(c.fChar == 92) {
            c.fEscaped = true;
            int[] unescapeIndex = new int[]{this.fNextIndex};
            c.fChar = Utility.unescapeAt(this.fRB.fRules, unescapeIndex);
            if(unescapeIndex[0] == this.fNextIndex) {
               this.error(66050);
            }

            this.fCharNum += unescapeIndex[0] - this.fNextIndex;
            this.fNextIndex = unescapeIndex[0];
         }
      }

   }

   void parse() {
      int state = 1;
      this.nextChar(this.fC);

      while(state != 0) {
         RBBIRuleParseTable.RBBIRuleTableElement tableEl = RBBIRuleParseTable.gRuleParseStateTable[state];
         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("scan") >= 0) {
            System.out.println("char, line, col = (\'" + (char)this.fC.fChar + "\', " + this.fLineNum + ", " + this.fCharNum + "    state = " + tableEl.fStateName);
         }

         int tableRow = state;

         while(true) {
            tableEl = RBBIRuleParseTable.gRuleParseStateTable[tableRow];
            if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("scan") >= 0) {
               System.out.print(".");
            }

            if(tableEl.fCharClass < 127 && !this.fC.fEscaped && tableEl.fCharClass == this.fC.fChar || tableEl.fCharClass == 255 || tableEl.fCharClass == 254 && this.fC.fEscaped || tableEl.fCharClass == 253 && this.fC.fEscaped && (this.fC.fChar == 80 || this.fC.fChar == 112) || tableEl.fCharClass == 252 && this.fC.fChar == -1) {
               break;
            }

            if(tableEl.fCharClass >= 128 && tableEl.fCharClass < 240 && !this.fC.fEscaped && this.fC.fChar != -1) {
               UnicodeSet uniset = this.fRuleSets[tableEl.fCharClass - 128];
               if(uniset.contains(this.fC.fChar)) {
                  break;
               }
            }

            ++tableRow;
         }

         if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("scan") >= 0) {
            System.out.println("");
         }

         if(!this.doParseActions(tableEl.fAction)) {
            break;
         }

         if(tableEl.fPushState != 0) {
            ++this.fStackPtr;
            if(this.fStackPtr >= 100) {
               System.out.println("RBBIRuleScanner.parse() - state stack overflow.");
               this.error(66049);
            }

            this.fStack[this.fStackPtr] = tableEl.fPushState;
         }

         if(tableEl.fNextChar) {
            this.nextChar(this.fC);
         }

         if(tableEl.fNextState != 255) {
            state = tableEl.fNextState;
         } else {
            state = this.fStack[this.fStackPtr];
            --this.fStackPtr;
            if(this.fStackPtr < 0) {
               System.out.println("RBBIRuleScanner.parse() - state stack underflow.");
               this.error(66049);
            }
         }
      }

      if(this.fRB.fTreeRoots[1] == null) {
         this.fRB.fTreeRoots[1] = this.pushNewNode(10);
         RBBINode operand = this.pushNewNode(0);
         this.findSetFor(kAny, operand, (UnicodeSet)null);
         this.fRB.fTreeRoots[1].fLeftChild = operand;
         operand.fParent = this.fRB.fTreeRoots[1];
         this.fNodeStackPtr -= 2;
      }

      if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("symbols") >= 0) {
         this.fSymbolTable.rbbiSymtablePrint();
      }

      if(this.fRB.fDebugEnv != null && this.fRB.fDebugEnv.indexOf("ptree") >= 0) {
         System.out.println("Completed Forward Rules Parse Tree...");
         this.fRB.fTreeRoots[0].printTree(true);
         System.out.println("\nCompleted Reverse Rules Parse Tree...");
         this.fRB.fTreeRoots[1].printTree(true);
         System.out.println("\nCompleted Safe Point Forward Rules Parse Tree...");
         if(this.fRB.fTreeRoots[2] == null) {
            System.out.println("  -- null -- ");
         } else {
            this.fRB.fTreeRoots[2].printTree(true);
         }

         System.out.println("\nCompleted Safe Point Reverse Rules Parse Tree...");
         if(this.fRB.fTreeRoots[3] == null) {
            System.out.println("  -- null -- ");
         } else {
            this.fRB.fTreeRoots[3].printTree(true);
         }
      }

   }

   void printNodeStack(String title) {
      System.out.println(title + ".  Dumping node stack...\n");

      for(int i = this.fNodeStackPtr; i > 0; --i) {
         this.fNodeStack[i].printTree(true);
      }

   }

   RBBINode pushNewNode(int nodeType) {
      ++this.fNodeStackPtr;
      if(this.fNodeStackPtr >= 100) {
         System.out.println("RBBIRuleScanner.pushNewNode - stack overflow.");
         this.error(66049);
      }

      this.fNodeStack[this.fNodeStackPtr] = new RBBINode(nodeType);
      return this.fNodeStack[this.fNodeStackPtr];
   }

   void scanSet() {
      UnicodeSet uset = null;
      ParsePosition pos = new ParsePosition(this.fScanIndex);
      int startPos = this.fScanIndex;

      try {
         uset = new UnicodeSet(this.fRB.fRules, pos, this.fSymbolTable, 1);
      } catch (Exception var6) {
         this.error(66063);
      }

      if(uset.isEmpty()) {
         this.error(66060);
      }

      int i = pos.getIndex();

      while(this.fNextIndex < i) {
         this.nextCharLL();
      }

      RBBINode n = this.pushNewNode(0);
      n.fFirstPos = startPos;
      n.fLastPos = this.fNextIndex;
      n.fText = this.fRB.fRules.substring(n.fFirstPos, n.fLastPos);
      this.findSetFor(n.fText, n, uset);
   }

   static class RBBIRuleChar {
      int fChar;
      boolean fEscaped;
   }

   static class RBBISetTableEl {
      String key;
      RBBINode val;
   }
}
