package com.ibm.icu.text;

import java.text.CharacterIterator;
import java.util.Stack;

interface LanguageBreakEngine {
   boolean handles(int var1, int var2);

   int findBreaks(CharacterIterator var1, int var2, int var3, boolean var4, int var5, Stack var6);
}
