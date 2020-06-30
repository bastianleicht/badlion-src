package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.RBBINode;
import com.ibm.icu.text.RBBIRuleScanner;
import com.ibm.icu.text.SymbolTable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeMatcher;
import com.ibm.icu.text.UnicodeSet;
import java.text.ParsePosition;
import java.util.HashMap;

class RBBISymbolTable implements SymbolTable {
   String fRules;
   HashMap fHashTable;
   RBBIRuleScanner fRuleScanner;
   String ffffString;
   UnicodeSet fCachedSetLookup;

   RBBISymbolTable(RBBIRuleScanner rs, String rules) {
      this.fRules = rules;
      this.fRuleScanner = rs;
      this.fHashTable = new HashMap();
      this.ffffString = "\uffff";
   }

   public char[] lookup(String s) {
      RBBISymbolTable.RBBISymbolTableEntry el = (RBBISymbolTable.RBBISymbolTableEntry)this.fHashTable.get(s);
      if(el == null) {
         return null;
      } else {
         RBBINode varRefNode;
         for(varRefNode = el.val; varRefNode.fLeftChild.fType == 2; varRefNode = varRefNode.fLeftChild) {
            ;
         }

         RBBINode exprNode = varRefNode.fLeftChild;
         String retString;
         if(exprNode.fType == 0) {
            RBBINode usetNode = exprNode.fLeftChild;
            this.fCachedSetLookup = usetNode.fInputSet;
            retString = this.ffffString;
         } else {
            this.fRuleScanner.error(66063);
            retString = exprNode.fText;
            this.fCachedSetLookup = null;
         }

         return retString.toCharArray();
      }
   }

   public UnicodeMatcher lookupMatcher(int ch) {
      UnicodeSet retVal = null;
      if(ch == '\uffff') {
         retVal = this.fCachedSetLookup;
         this.fCachedSetLookup = null;
      }

      return retVal;
   }

   public String parseReference(String text, ParsePosition pos, int limit) {
      int start = pos.getIndex();
      int i = start;

      String result;
      int c;
      for(result = ""; i < limit; i += UTF16.getCharCount(c)) {
         c = UTF16.charAt(text, i);
         if(i == start && !UCharacter.isUnicodeIdentifierStart(c) || !UCharacter.isUnicodeIdentifierPart(c)) {
            break;
         }
      }

      if(i == start) {
         return result;
      } else {
         pos.setIndex(i);
         result = text.substring(start, i);
         return result;
      }
   }

   RBBINode lookupNode(String key) {
      RBBINode retNode = null;
      RBBISymbolTable.RBBISymbolTableEntry el = (RBBISymbolTable.RBBISymbolTableEntry)this.fHashTable.get(key);
      if(el != null) {
         retNode = el.val;
      }

      return retNode;
   }

   void addEntry(String key, RBBINode val) {
      RBBISymbolTable.RBBISymbolTableEntry e = (RBBISymbolTable.RBBISymbolTableEntry)this.fHashTable.get(key);
      if(e != null) {
         this.fRuleScanner.error(66055);
      } else {
         e = new RBBISymbolTable.RBBISymbolTableEntry();
         e.key = key;
         e.val = val;
         this.fHashTable.put(e.key, e);
      }
   }

   void rbbiSymtablePrint() {
      System.out.print("Variable Definitions\nName               Node Val     String Val\n----------------------------------------------------------------------\n");
      RBBISymbolTable.RBBISymbolTableEntry[] syms = (RBBISymbolTable.RBBISymbolTableEntry[])this.fHashTable.values().toArray(new RBBISymbolTable.RBBISymbolTableEntry[0]);

      for(int i = 0; i < syms.length; ++i) {
         RBBISymbolTable.RBBISymbolTableEntry s = syms[i];
         System.out.print("  " + s.key + "  ");
         System.out.print("  " + s.val + "  ");
         System.out.print(s.val.fLeftChild.fText);
         System.out.print("\n");
      }

      System.out.println("\nParsed Variable Definitions\n");

      for(int i = 0; i < syms.length; ++i) {
         RBBISymbolTable.RBBISymbolTableEntry s = syms[i];
         System.out.print(s.key);
         s.val.fLeftChild.printTree(true);
         System.out.print("\n");
      }

   }

   static class RBBISymbolTableEntry {
      String key;
      RBBINode val;
   }
}
