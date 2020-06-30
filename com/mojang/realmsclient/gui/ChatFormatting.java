package com.mojang.realmsclient.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatFormatting {
   BLACK('0'),
   DARK_BLUE('1'),
   DARK_GREEN('2'),
   DARK_AQUA('3'),
   DARK_RED('4'),
   DARK_PURPLE('5'),
   GOLD('6'),
   GRAY('7'),
   DARK_GRAY('8'),
   BLUE('9'),
   GREEN('a'),
   AQUA('b'),
   RED('c'),
   LIGHT_PURPLE('d'),
   YELLOW('e'),
   WHITE('f'),
   OBFUSCATED('k', true),
   BOLD('l', true),
   STRIKETHROUGH('m', true),
   UNDERLINE('n', true),
   ITALIC('o', true),
   RESET('r');

   public static final char PREFIX_CODE = '§';
   private static final Map FORMATTING_BY_CHAR = new HashMap();
   private static final Map FORMATTING_BY_NAME = new HashMap();
   private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final char code;
   private final boolean isFormat;
   private final String toString;

   private ChatFormatting(char code) {
      this(code, false);
   }

   private ChatFormatting(char code, boolean isFormat) {
      this.code = code;
      this.isFormat = isFormat;
      this.toString = "§" + code;
   }

   public char getChar() {
      return this.code;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   public String getName() {
      return this.name().toLowerCase();
   }

   public String toString() {
      return this.toString;
   }

   public static String stripFormatting(String input) {
      return input == null?null:STRIP_FORMATTING_PATTERN.matcher(input).replaceAll("");
   }

   public static ChatFormatting getByChar(char code) {
      return (ChatFormatting)FORMATTING_BY_CHAR.get(Character.valueOf(code));
   }

   public static ChatFormatting getByName(String name) {
      return name == null?null:(ChatFormatting)FORMATTING_BY_NAME.get(name.toLowerCase());
   }

   public static Collection getNames(boolean getColors, boolean getFormats) {
      List<String> result = new ArrayList();

      for(ChatFormatting format : values()) {
         if((!format.isColor() || getColors) && (!format.isFormat() || getFormats)) {
            result.add(format.getName());
         }
      }

      return result;
   }

   static {
      for(ChatFormatting format : values()) {
         FORMATTING_BY_CHAR.put(Character.valueOf(format.getChar()), format);
         FORMATTING_BY_NAME.put(format.getName(), format);
      }

   }
}
