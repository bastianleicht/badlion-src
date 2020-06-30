package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class DoubleMetaphone implements StringEncoder {
   private static final String VOWELS = "AEIOUY";
   private static final String[] SILENT_START = new String[]{"GN", "KN", "PN", "WR", "PS"};
   private static final String[] L_R_N_M_B_H_F_V_W_SPACE = new String[]{"L", "R", "N", "M", "B", "H", "F", "V", "W", " "};
   private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = new String[]{"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER"};
   private static final String[] L_T_K_S_N_M_B_Z = new String[]{"L", "T", "K", "S", "N", "M", "B", "Z"};
   private int maxCodeLen = 4;

   public String doubleMetaphone(String value) {
      return this.doubleMetaphone(value, false);
   }

   public String doubleMetaphone(String value, boolean alternate) {
      value = this.cleanInput(value);
      if(value == null) {
         return null;
      } else {
         boolean slavoGermanic = this.isSlavoGermanic(value);
         int index = this.isSilentStart(value)?1:0;
         DoubleMetaphone.DoubleMetaphoneResult result = new DoubleMetaphone.DoubleMetaphoneResult(this.getMaxCodeLen());

         while(!result.isComplete() && index <= value.length() - 1) {
            switch(value.charAt(index)) {
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'Y':
               index = this.handleAEIOUY(result, index);
               break;
            case 'B':
               result.append('P');
               index = this.charAt(value, index + 1) == 66?index + 2:index + 1;
               break;
            case 'C':
               index = this.handleC(value, result, index);
               break;
            case 'D':
               index = this.handleD(value, result, index);
               break;
            case 'F':
               result.append('F');
               index = this.charAt(value, index + 1) == 70?index + 2:index + 1;
               break;
            case 'G':
               index = this.handleG(value, result, index, slavoGermanic);
               break;
            case 'H':
               index = this.handleH(value, result, index);
               break;
            case 'J':
               index = this.handleJ(value, result, index, slavoGermanic);
               break;
            case 'K':
               result.append('K');
               index = this.charAt(value, index + 1) == 75?index + 2:index + 1;
               break;
            case 'L':
               index = this.handleL(value, result, index);
               break;
            case 'M':
               result.append('M');
               index = this.conditionM0(value, index)?index + 2:index + 1;
               break;
            case 'N':
               result.append('N');
               index = this.charAt(value, index + 1) == 78?index + 2:index + 1;
               break;
            case 'P':
               index = this.handleP(value, result, index);
               break;
            case 'Q':
               result.append('K');
               index = this.charAt(value, index + 1) == 81?index + 2:index + 1;
               break;
            case 'R':
               index = this.handleR(value, result, index, slavoGermanic);
               break;
            case 'S':
               index = this.handleS(value, result, index, slavoGermanic);
               break;
            case 'T':
               index = this.handleT(value, result, index);
               break;
            case 'V':
               result.append('F');
               index = this.charAt(value, index + 1) == 86?index + 2:index + 1;
               break;
            case 'W':
               index = this.handleW(value, result, index);
               break;
            case 'X':
               index = this.handleX(value, result, index);
               break;
            case 'Z':
               index = this.handleZ(value, result, index, slavoGermanic);
               break;
            case 'Ç':
               result.append('S');
               ++index;
               break;
            case 'Ñ':
               result.append('N');
               ++index;
               break;
            default:
               ++index;
            }
         }

         return alternate?result.getAlternate():result.getPrimary();
      }
   }

   public Object encode(Object obj) throws EncoderException {
      if(!(obj instanceof String)) {
         throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
      } else {
         return this.doubleMetaphone((String)obj);
      }
   }

   public String encode(String value) {
      return this.doubleMetaphone(value);
   }

   public boolean isDoubleMetaphoneEqual(String value1, String value2) {
      return this.isDoubleMetaphoneEqual(value1, value2, false);
   }

   public boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate) {
      return this.doubleMetaphone(value1, alternate).equals(this.doubleMetaphone(value2, alternate));
   }

   public int getMaxCodeLen() {
      return this.maxCodeLen;
   }

   public void setMaxCodeLen(int maxCodeLen) {
      this.maxCodeLen = maxCodeLen;
   }

   private int handleAEIOUY(DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(index == 0) {
         result.append('A');
      }

      return index + 1;
   }

   private int handleC(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(this.conditionC0(value, index)) {
         result.append('K');
         index += 2;
      } else if(index == 0 && contains(value, index, 6, new String[]{"CAESAR"})) {
         result.append('S');
         index += 2;
      } else if(contains(value, index, 2, new String[]{"CH"})) {
         index = this.handleCH(value, result, index);
      } else if(contains(value, index, 2, new String[]{"CZ"}) && !contains(value, index - 2, 4, new String[]{"WICZ"})) {
         result.append('S', 'X');
         index += 2;
      } else if(contains(value, index + 1, 3, new String[]{"CIA"})) {
         result.append('X');
         index += 3;
      } else {
         if(contains(value, index, 2, new String[]{"CC"}) && (index != 1 || this.charAt(value, 0) != 77)) {
            return this.handleCC(value, result, index);
         }

         if(contains(value, index, 2, new String[]{"CK", "CG", "CQ"})) {
            result.append('K');
            index += 2;
         } else if(contains(value, index, 2, new String[]{"CI", "CE", "CY"})) {
            if(contains(value, index, 3, new String[]{"CIO", "CIE", "CIA"})) {
               result.append('S', 'X');
            } else {
               result.append('S');
            }

            index += 2;
         } else {
            result.append('K');
            if(contains(value, index + 1, 2, new String[]{" C", " Q", " G"})) {
               index += 3;
            } else if(contains(value, index + 1, 1, new String[]{"C", "K", "Q"}) && !contains(value, index + 1, 2, new String[]{"CE", "CI"})) {
               index += 2;
            } else {
               ++index;
            }
         }
      }

      return index;
   }

   private int handleCC(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(contains(value, index + 2, 1, new String[]{"I", "E", "H"}) && !contains(value, index + 2, 2, new String[]{"HU"})) {
         if((index != 1 || this.charAt(value, index - 1) != 65) && !contains(value, index - 1, 5, new String[]{"UCCEE", "UCCES"})) {
            result.append('X');
         } else {
            result.append("KS");
         }

         index = index + 3;
      } else {
         result.append('K');
         index = index + 2;
      }

      return index;
   }

   private int handleCH(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(index > 0 && contains(value, index, 4, new String[]{"CHAE"})) {
         result.append('K', 'X');
         return index + 2;
      } else if(this.conditionCH0(value, index)) {
         result.append('K');
         return index + 2;
      } else if(this.conditionCH1(value, index)) {
         result.append('K');
         return index + 2;
      } else {
         if(index > 0) {
            if(contains(value, 0, 2, new String[]{"MC"})) {
               result.append('K');
            } else {
               result.append('X', 'K');
            }
         } else {
            result.append('X');
         }

         return index + 2;
      }
   }

   private int handleD(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(contains(value, index, 2, new String[]{"DG"})) {
         if(contains(value, index + 2, 1, new String[]{"I", "E", "Y"})) {
            result.append('J');
            index += 3;
         } else {
            result.append("TK");
            index += 2;
         }
      } else if(contains(value, index, 2, new String[]{"DT", "DD"})) {
         result.append('T');
         index += 2;
      } else {
         result.append('T');
         ++index;
      }

      return index;
   }

   private int handleG(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
      if(this.charAt(value, index + 1) == 72) {
         index = this.handleGH(value, result, index);
      } else if(this.charAt(value, index + 1) == 78) {
         if(index == 1 && this.isVowel(this.charAt(value, 0)) && !slavoGermanic) {
            result.append("KN", "N");
         } else if(!contains(value, index + 2, 2, new String[]{"EY"}) && this.charAt(value, index + 1) != 89 && !slavoGermanic) {
            result.append("N", "KN");
         } else {
            result.append("KN");
         }

         index += 2;
      } else if(contains(value, index + 1, 2, new String[]{"LI"}) && !slavoGermanic) {
         result.append("KL", "L");
         index += 2;
      } else if(index != 0 || this.charAt(value, index + 1) != 89 && !contains(value, index + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER)) {
         if((contains(value, index + 1, 2, new String[]{"ER"}) || this.charAt(value, index + 1) == 89) && !contains(value, 0, 6, new String[]{"DANGER", "RANGER", "MANGER"}) && !contains(value, index - 1, 1, new String[]{"E", "I"}) && !contains(value, index - 1, 3, new String[]{"RGY", "OGY"})) {
            result.append('K', 'J');
            index += 2;
         } else if(!contains(value, index + 1, 1, new String[]{"E", "I", "Y"}) && !contains(value, index - 1, 4, new String[]{"AGGI", "OGGI"})) {
            if(this.charAt(value, index + 1) == 71) {
               index += 2;
               result.append('K');
            } else {
               ++index;
               result.append('K');
            }
         } else {
            if(!contains(value, 0, 4, new String[]{"VAN ", "VON "}) && !contains(value, 0, 3, new String[]{"SCH"}) && !contains(value, index + 1, 2, new String[]{"ET"})) {
               if(contains(value, index + 1, 3, new String[]{"IER"})) {
                  result.append('J');
               } else {
                  result.append('J', 'K');
               }
            } else {
               result.append('K');
            }

            index += 2;
         }
      } else {
         result.append('K', 'J');
         index += 2;
      }

      return index;
   }

   private int handleGH(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(index > 0 && !this.isVowel(this.charAt(value, index - 1))) {
         result.append('K');
         index = index + 2;
      } else if(index == 0) {
         if(this.charAt(value, index + 2) == 73) {
            result.append('J');
         } else {
            result.append('K');
         }

         index = index + 2;
      } else if((index <= 1 || !contains(value, index - 2, 1, new String[]{"B", "H", "D"})) && (index <= 2 || !contains(value, index - 3, 1, new String[]{"B", "H", "D"})) && (index <= 3 || !contains(value, index - 4, 1, new String[]{"B", "H"}))) {
         if(index > 2 && this.charAt(value, index - 1) == 85 && contains(value, index - 3, 1, new String[]{"C", "G", "L", "R", "T"})) {
            result.append('F');
         } else if(index > 0 && this.charAt(value, index - 1) != 73) {
            result.append('K');
         }

         index = index + 2;
      } else {
         index = index + 2;
      }

      return index;
   }

   private int handleH(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if((index == 0 || this.isVowel(this.charAt(value, index - 1))) && this.isVowel(this.charAt(value, index + 1))) {
         result.append('H');
         index += 2;
      } else {
         ++index;
      }

      return index;
   }

   private int handleJ(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
      if(!contains(value, index, 4, new String[]{"JOSE"}) && !contains(value, 0, 4, new String[]{"SAN "})) {
         if(index == 0 && !contains(value, index, 4, new String[]{"JOSE"})) {
            result.append('J', 'A');
         } else if(!this.isVowel(this.charAt(value, index - 1)) || slavoGermanic || this.charAt(value, index + 1) != 65 && this.charAt(value, index + 1) != 79) {
            if(index == value.length() - 1) {
               result.append('J', ' ');
            } else if(!contains(value, index + 1, 1, L_T_K_S_N_M_B_Z) && !contains(value, index - 1, 1, new String[]{"S", "K", "L"})) {
               result.append('J');
            }
         } else {
            result.append('J', 'H');
         }

         if(this.charAt(value, index + 1) == 74) {
            index += 2;
         } else {
            ++index;
         }
      } else {
         if((index != 0 || this.charAt(value, index + 4) != 32) && value.length() != 4 && !contains(value, 0, 4, new String[]{"SAN "})) {
            result.append('J', 'H');
         } else {
            result.append('H');
         }

         ++index;
      }

      return index;
   }

   private int handleL(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(this.charAt(value, index + 1) == 76) {
         if(this.conditionL0(value, index)) {
            result.appendPrimary('L');
         } else {
            result.append('L');
         }

         index += 2;
      } else {
         ++index;
         result.append('L');
      }

      return index;
   }

   private int handleP(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(this.charAt(value, index + 1) == 72) {
         result.append('F');
         index = index + 2;
      } else {
         result.append('P');
         index = contains(value, index + 1, 1, new String[]{"P", "B"})?index + 2:index + 1;
      }

      return index;
   }

   private int handleR(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
      if(index == value.length() - 1 && !slavoGermanic && contains(value, index - 2, 2, new String[]{"IE"}) && !contains(value, index - 4, 2, new String[]{"ME", "MA"})) {
         result.appendAlternate('R');
      } else {
         result.append('R');
      }

      return this.charAt(value, index + 1) == 82?index + 2:index + 1;
   }

   private int handleS(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
      if(contains(value, index - 1, 3, new String[]{"ISL", "YSL"})) {
         ++index;
      } else if(index == 0 && contains(value, index, 5, new String[]{"SUGAR"})) {
         result.append('X', 'S');
         ++index;
      } else if(contains(value, index, 2, new String[]{"SH"})) {
         if(contains(value, index + 1, 4, new String[]{"HEIM", "HOEK", "HOLM", "HOLZ"})) {
            result.append('S');
         } else {
            result.append('X');
         }

         index += 2;
      } else if(!contains(value, index, 3, new String[]{"SIO", "SIA"}) && !contains(value, index, 4, new String[]{"SIAN"})) {
         if((index != 0 || !contains(value, index + 1, 1, new String[]{"M", "N", "L", "W"})) && !contains(value, index + 1, 1, new String[]{"Z"})) {
            if(contains(value, index, 2, new String[]{"SC"})) {
               index = this.handleSC(value, result, index);
            } else {
               if(index == value.length() - 1 && contains(value, index - 2, 2, new String[]{"AI", "OI"})) {
                  result.appendAlternate('S');
               } else {
                  result.append('S');
               }

               index = contains(value, index + 1, 1, new String[]{"S", "Z"})?index + 2:index + 1;
            }
         } else {
            result.append('S', 'X');
            index = contains(value, index + 1, 1, new String[]{"Z"})?index + 2:index + 1;
         }
      } else {
         if(slavoGermanic) {
            result.append('S');
         } else {
            result.append('S', 'X');
         }

         index += 3;
      }

      return index;
   }

   private int handleSC(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(this.charAt(value, index + 2) == 72) {
         if(contains(value, index + 3, 2, new String[]{"OO", "ER", "EN", "UY", "ED", "EM"})) {
            if(contains(value, index + 3, 2, new String[]{"ER", "EN"})) {
               result.append("X", "SK");
            } else {
               result.append("SK");
            }
         } else if(index == 0 && !this.isVowel(this.charAt(value, 3)) && this.charAt(value, 3) != 87) {
            result.append('X', 'S');
         } else {
            result.append('X');
         }
      } else if(contains(value, index + 2, 1, new String[]{"I", "E", "Y"})) {
         result.append('S');
      } else {
         result.append("SK");
      }

      return index + 3;
   }

   private int handleT(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(contains(value, index, 4, new String[]{"TION"})) {
         result.append('X');
         index = index + 3;
      } else if(contains(value, index, 3, new String[]{"TIA", "TCH"})) {
         result.append('X');
         index = index + 3;
      } else if(!contains(value, index, 2, new String[]{"TH"}) && !contains(value, index, 3, new String[]{"TTH"})) {
         result.append('T');
         index = contains(value, index + 1, 1, new String[]{"T", "D"})?index + 2:index + 1;
      } else {
         if(!contains(value, index + 2, 2, new String[]{"OM", "AM"}) && !contains(value, 0, 4, new String[]{"VAN ", "VON "}) && !contains(value, 0, 3, new String[]{"SCH"})) {
            result.append('0', 'T');
         } else {
            result.append('T');
         }

         index = index + 2;
      }

      return index;
   }

   private int handleW(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(contains(value, index, 2, new String[]{"WR"})) {
         result.append('R');
         index += 2;
      } else if(index != 0 || !this.isVowel(this.charAt(value, index + 1)) && !contains(value, index, 2, new String[]{"WH"})) {
         if((index != value.length() - 1 || !this.isVowel(this.charAt(value, index - 1))) && !contains(value, index - 1, 5, new String[]{"EWSKI", "EWSKY", "OWSKI", "OWSKY"}) && !contains(value, 0, 3, new String[]{"SCH"})) {
            if(contains(value, index, 4, new String[]{"WICZ", "WITZ"})) {
               result.append("TS", "FX");
               index += 4;
            } else {
               ++index;
            }
         } else {
            result.appendAlternate('F');
            ++index;
         }
      } else {
         if(this.isVowel(this.charAt(value, index + 1))) {
            result.append('A', 'F');
         } else {
            result.append('A');
         }

         ++index;
      }

      return index;
   }

   private int handleX(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index) {
      if(index == 0) {
         result.append('S');
         ++index;
      } else {
         if(index != value.length() - 1 || !contains(value, index - 3, 3, new String[]{"IAU", "EAU"}) && !contains(value, index - 2, 2, new String[]{"AU", "OU"})) {
            result.append("KS");
         }

         index = contains(value, index + 1, 1, new String[]{"C", "X"})?index + 2:index + 1;
      }

      return index;
   }

   private int handleZ(String value, DoubleMetaphone.DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
      if(this.charAt(value, index + 1) == 72) {
         result.append('J');
         index = index + 2;
      } else {
         if(!contains(value, index + 1, 2, new String[]{"ZO", "ZI", "ZA"}) && (!slavoGermanic || index <= 0 || this.charAt(value, index - 1) == 84)) {
            result.append('S');
         } else {
            result.append("S", "TS");
         }

         index = this.charAt(value, index + 1) == 90?index + 2:index + 1;
      }

      return index;
   }

   private boolean conditionC0(String value, int index) {
      if(contains(value, index, 4, new String[]{"CHIA"})) {
         return true;
      } else if(index <= 1) {
         return false;
      } else if(this.isVowel(this.charAt(value, index - 2))) {
         return false;
      } else if(!contains(value, index - 1, 3, new String[]{"ACH"})) {
         return false;
      } else {
         char c = this.charAt(value, index + 2);
         return c != 73 && c != 69 || contains(value, index - 2, 6, new String[]{"BACHER", "MACHER"});
      }
   }

   private boolean conditionCH0(String value, int index) {
      return index != 0?false:(!contains(value, index + 1, 5, new String[]{"HARAC", "HARIS"}) && !contains(value, index + 1, 3, new String[]{"HOR", "HYM", "HIA", "HEM"})?false:!contains(value, 0, 5, new String[]{"CHORE"}));
   }

   private boolean conditionCH1(String value, int index) {
      return contains(value, 0, 4, new String[]{"VAN ", "VON "}) || contains(value, 0, 3, new String[]{"SCH"}) || contains(value, index - 2, 6, new String[]{"ORCHES", "ARCHIT", "ORCHID"}) || contains(value, index + 2, 1, new String[]{"T", "S"}) || (contains(value, index - 1, 1, new String[]{"A", "O", "U", "E"}) || index == 0) && (contains(value, index + 2, 1, L_R_N_M_B_H_F_V_W_SPACE) || index + 1 == value.length() - 1);
   }

   private boolean conditionL0(String value, int index) {
      return index == value.length() - 3 && contains(value, index - 1, 4, new String[]{"ILLO", "ILLA", "ALLE"})?true:(contains(value, value.length() - 2, 2, new String[]{"AS", "OS"}) || contains(value, value.length() - 1, 1, new String[]{"A", "O"})) && contains(value, index - 1, 4, new String[]{"ALLE"});
   }

   private boolean conditionM0(String value, int index) {
      return this.charAt(value, index + 1) == 77?true:contains(value, index - 1, 3, new String[]{"UMB"}) && (index + 1 == value.length() - 1 || contains(value, index + 2, 2, new String[]{"ER"}));
   }

   private boolean isSlavoGermanic(String value) {
      return value.indexOf(87) > -1 || value.indexOf(75) > -1 || value.indexOf("CZ") > -1 || value.indexOf("WITZ") > -1;
   }

   private boolean isVowel(char ch) {
      return "AEIOUY".indexOf(ch) != -1;
   }

   private boolean isSilentStart(String value) {
      boolean result = false;

      for(String element : SILENT_START) {
         if(value.startsWith(element)) {
            result = true;
            break;
         }
      }

      return result;
   }

   private String cleanInput(String input) {
      if(input == null) {
         return null;
      } else {
         input = input.trim();
         return input.length() == 0?null:input.toUpperCase(Locale.ENGLISH);
      }
   }

   protected char charAt(String value, int index) {
      return index >= 0 && index < value.length()?value.charAt(index):'\u0000';
   }

   protected static boolean contains(String value, int start, int length, String... criteria) {
      boolean result = false;
      if(start >= 0 && start + length <= value.length()) {
         String target = value.substring(start, start + length);

         for(String element : criteria) {
            if(target.equals(element)) {
               result = true;
               break;
            }
         }
      }

      return result;
   }

   public class DoubleMetaphoneResult {
      private final StringBuilder primary = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
      private final StringBuilder alternate = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
      private final int maxLength;

      public DoubleMetaphoneResult(int maxLength) {
         this.maxLength = maxLength;
      }

      public void append(char value) {
         this.appendPrimary(value);
         this.appendAlternate(value);
      }

      public void append(char primary, char alternate) {
         this.appendPrimary(primary);
         this.appendAlternate(alternate);
      }

      public void appendPrimary(char value) {
         if(this.primary.length() < this.maxLength) {
            this.primary.append(value);
         }

      }

      public void appendAlternate(char value) {
         if(this.alternate.length() < this.maxLength) {
            this.alternate.append(value);
         }

      }

      public void append(String value) {
         this.appendPrimary(value);
         this.appendAlternate(value);
      }

      public void append(String primary, String alternate) {
         this.appendPrimary(primary);
         this.appendAlternate(alternate);
      }

      public void appendPrimary(String value) {
         int addChars = this.maxLength - this.primary.length();
         if(value.length() <= addChars) {
            this.primary.append(value);
         } else {
            this.primary.append(value.substring(0, addChars));
         }

      }

      public void appendAlternate(String value) {
         int addChars = this.maxLength - this.alternate.length();
         if(value.length() <= addChars) {
            this.alternate.append(value);
         } else {
            this.alternate.append(value.substring(0, addChars));
         }

      }

      public String getPrimary() {
         return this.primary.toString();
      }

      public String getAlternate() {
         return this.alternate.toString();
      }

      public boolean isComplete() {
         return this.primary.length() >= this.maxLength && this.alternate.length() >= this.maxLength;
      }
   }
}
