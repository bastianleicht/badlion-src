package com.google.gson;

import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;

public enum FieldNamingPolicy implements FieldNamingStrategy {
   IDENTITY {
      public String translateName(Field f) {
         return f.getName();
      }
   },
   UPPER_CAMEL_CASE {
      public String translateName(Field f) {
         return FieldNamingPolicy.upperCaseFirstLetter(f.getName());
      }
   },
   UPPER_CAMEL_CASE_WITH_SPACES {
      public String translateName(Field f) {
         return FieldNamingPolicy.upperCaseFirstLetter(FieldNamingPolicy.separateCamelCase(f.getName(), " "));
      }
   },
   LOWER_CASE_WITH_UNDERSCORES {
      public String translateName(Field f) {
         return FieldNamingPolicy.separateCamelCase(f.getName(), "_").toLowerCase();
      }
   },
   LOWER_CASE_WITH_DASHES {
      public String translateName(Field f) {
         return FieldNamingPolicy.separateCamelCase(f.getName(), "-").toLowerCase();
      }
   };

   private FieldNamingPolicy() {
   }

   private static String separateCamelCase(String name, String separator) {
      StringBuilder translation = new StringBuilder();

      for(int i = 0; i < name.length(); ++i) {
         char character = name.charAt(i);
         if(Character.isUpperCase(character) && translation.length() != 0) {
            translation.append(separator);
         }

         translation.append(character);
      }

      return translation.toString();
   }

   private static String upperCaseFirstLetter(String name) {
      StringBuilder fieldNameBuilder = new StringBuilder();
      int index = 0;

      char firstCharacter;
      for(firstCharacter = name.charAt(index); index < name.length() - 1 && !Character.isLetter(firstCharacter); firstCharacter = name.charAt(index)) {
         fieldNameBuilder.append(firstCharacter);
         ++index;
      }

      if(index == name.length()) {
         return fieldNameBuilder.toString();
      } else if(!Character.isUpperCase(firstCharacter)) {
         char var10000 = Character.toUpperCase(firstCharacter);
         ++index;
         String modifiedTarget = modifyString(var10000, name, index);
         return fieldNameBuilder.append(modifiedTarget).toString();
      } else {
         return name;
      }
   }

   private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
      return indexOfSubstring < srcString.length()?firstCharacter + srcString.substring(indexOfSubstring):String.valueOf(firstCharacter);
   }
}
