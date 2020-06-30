package com.ibm.icu.text;

public enum DisplayContext {
   STANDARD_NAMES(DisplayContext.Type.DIALECT_HANDLING, 0),
   DIALECT_NAMES(DisplayContext.Type.DIALECT_HANDLING, 1),
   CAPITALIZATION_NONE(DisplayContext.Type.CAPITALIZATION, 0),
   CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE(DisplayContext.Type.CAPITALIZATION, 1),
   CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE(DisplayContext.Type.CAPITALIZATION, 2),
   CAPITALIZATION_FOR_UI_LIST_OR_MENU(DisplayContext.Type.CAPITALIZATION, 3),
   CAPITALIZATION_FOR_STANDALONE(DisplayContext.Type.CAPITALIZATION, 4);

   private final DisplayContext.Type type;
   private final int value;

   private DisplayContext(DisplayContext.Type type, int value) {
      this.type = type;
      this.value = value;
   }

   public DisplayContext.Type type() {
      return this.type;
   }

   public int value() {
      return this.value;
   }

   public static enum Type {
      DIALECT_HANDLING,
      CAPITALIZATION;
   }
}
