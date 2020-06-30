package com.ibm.icu.text;

import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.text.RBBINode;
import com.ibm.icu.text.RBBIRuleScanner;
import com.ibm.icu.text.RBBISetBuilder;
import com.ibm.icu.text.RBBITableBuilder;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RBBIRuleBuilder {
   String fDebugEnv = ICUDebug.enabled("rbbi")?ICUDebug.value("rbbi"):null;
   String fRules;
   RBBIRuleScanner fScanner;
   RBBINode[] fTreeRoots = new RBBINode[4];
   static final int fForwardTree = 0;
   static final int fReverseTree = 1;
   static final int fSafeFwdTree = 2;
   static final int fSafeRevTree = 3;
   int fDefaultTree = 0;
   boolean fChainRules;
   boolean fLBCMNoChain;
   boolean fLookAheadHardBreak;
   RBBISetBuilder fSetBuilder;
   List fUSetNodes;
   RBBITableBuilder fForwardTables;
   RBBITableBuilder fReverseTables;
   RBBITableBuilder fSafeFwdTables;
   RBBITableBuilder fSafeRevTables;
   Map fStatusSets = new HashMap();
   List fRuleStatusVals;
   static final int U_BRK_ERROR_START = 66048;
   static final int U_BRK_INTERNAL_ERROR = 66049;
   static final int U_BRK_HEX_DIGITS_EXPECTED = 66050;
   static final int U_BRK_SEMICOLON_EXPECTED = 66051;
   static final int U_BRK_RULE_SYNTAX = 66052;
   static final int U_BRK_UNCLOSED_SET = 66053;
   static final int U_BRK_ASSIGN_ERROR = 66054;
   static final int U_BRK_VARIABLE_REDFINITION = 66055;
   static final int U_BRK_MISMATCHED_PAREN = 66056;
   static final int U_BRK_NEW_LINE_IN_QUOTED_STRING = 66057;
   static final int U_BRK_UNDEFINED_VARIABLE = 66058;
   static final int U_BRK_INIT_ERROR = 66059;
   static final int U_BRK_RULE_EMPTY_SET = 66060;
   static final int U_BRK_UNRECOGNIZED_OPTION = 66061;
   static final int U_BRK_MALFORMED_RULE_TAG = 66062;
   static final int U_BRK_MALFORMED_SET = 66063;
   static final int U_BRK_ERROR_LIMIT = 66064;

   RBBIRuleBuilder(String rules) {
      this.fRules = rules;
      this.fUSetNodes = new ArrayList();
      this.fRuleStatusVals = new ArrayList();
      this.fScanner = new RBBIRuleScanner(this);
      this.fSetBuilder = new RBBISetBuilder(this);
   }

   static final int align8(int i) {
      return i + 7 & -8;
   }

   void flattenData(OutputStream os) throws IOException {
      DataOutputStream dos = new DataOutputStream(os);
      String strippedRules = RBBIRuleScanner.stripRules(this.fRules);
      int headerSize = 96;
      int forwardTableSize = align8(this.fForwardTables.getTableSize());
      int reverseTableSize = align8(this.fReverseTables.getTableSize());
      int safeFwdTableSize = align8(this.fSafeFwdTables.getTableSize());
      int safeRevTableSize = align8(this.fSafeRevTables.getTableSize());
      int trieSize = align8(this.fSetBuilder.getTrieSize());
      int statusTableSize = align8(this.fRuleStatusVals.size() * 4);
      int rulesSize = align8(strippedRules.length() * 2);
      int totalSize = headerSize + forwardTableSize + reverseTableSize + safeFwdTableSize + safeRevTableSize + statusTableSize + trieSize + rulesSize;
      int outputPos = 0;
      byte[] ICUDataHeader = new byte[128];
      dos.write(ICUDataHeader);
      int[] header = new int[24];
      header[0] = '놠';
      header[1] = 50397184;
      header[2] = totalSize;
      header[3] = this.fSetBuilder.getNumCharCategories();
      header[4] = headerSize;
      header[5] = forwardTableSize;
      header[6] = header[4] + forwardTableSize;
      header[7] = reverseTableSize;
      header[8] = header[6] + reverseTableSize;
      header[9] = safeFwdTableSize;
      header[10] = header[8] + safeFwdTableSize;
      header[11] = safeRevTableSize;
      header[12] = header[10] + safeRevTableSize;
      header[13] = this.fSetBuilder.getTrieSize();
      header[16] = header[12] + header[13];
      header[17] = statusTableSize;
      header[14] = header[16] + statusTableSize;
      header[15] = strippedRules.length() * 2;

      for(int i = 0; i < header.length; ++i) {
         dos.writeInt(header[i]);
         outputPos += 4;
      }

      short[] tableData = this.fForwardTables.exportTable();
      Assert.assrt(outputPos == header[4]);

      for(int var20 = 0; var20 < tableData.length; ++var20) {
         dos.writeShort(tableData[var20]);
         outputPos += 2;
      }

      tableData = this.fReverseTables.exportTable();
      Assert.assrt(outputPos == header[6]);

      for(int var21 = 0; var21 < tableData.length; ++var21) {
         dos.writeShort(tableData[var21]);
         outputPos += 2;
      }

      Assert.assrt(outputPos == header[8]);
      tableData = this.fSafeFwdTables.exportTable();

      for(int var22 = 0; var22 < tableData.length; ++var22) {
         dos.writeShort(tableData[var22]);
         outputPos += 2;
      }

      Assert.assrt(outputPos == header[10]);
      tableData = this.fSafeRevTables.exportTable();

      for(int var23 = 0; var23 < tableData.length; ++var23) {
         dos.writeShort(tableData[var23]);
         outputPos += 2;
      }

      Assert.assrt(outputPos == header[12]);
      this.fSetBuilder.serializeTrie(os);

      for(outputPos = outputPos + header[13]; outputPos % 8 != 0; ++outputPos) {
         dos.write(0);
      }

      Assert.assrt(outputPos == header[16]);

      for(Integer val : this.fRuleStatusVals) {
         dos.writeInt(val.intValue());
         outputPos += 4;
      }

      while(outputPos % 8 != 0) {
         dos.write(0);
         ++outputPos;
      }

      Assert.assrt(outputPos == header[14]);
      dos.writeChars(strippedRules);

      for(outputPos = outputPos + strippedRules.length() * 2; outputPos % 8 != 0; ++outputPos) {
         dos.write(0);
      }

   }

   static void compileRules(String rules, OutputStream os) throws IOException {
      RBBIRuleBuilder builder = new RBBIRuleBuilder(rules);
      builder.fScanner.parse();
      builder.fSetBuilder.build();
      builder.fForwardTables = new RBBITableBuilder(builder, 0);
      builder.fReverseTables = new RBBITableBuilder(builder, 1);
      builder.fSafeFwdTables = new RBBITableBuilder(builder, 2);
      builder.fSafeRevTables = new RBBITableBuilder(builder, 3);
      builder.fForwardTables.build();
      builder.fReverseTables.build();
      builder.fSafeFwdTables.build();
      builder.fSafeRevTables.build();
      if(builder.fDebugEnv != null && builder.fDebugEnv.indexOf("states") >= 0) {
         builder.fForwardTables.printRuleStatusTable();
      }

      builder.flattenData(os);
   }
}
