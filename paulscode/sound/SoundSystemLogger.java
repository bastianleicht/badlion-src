package paulscode.sound;

public class SoundSystemLogger {
   public void message(String message, int indent) {
      String spacer = "";

      for(int x = 0; x < indent; ++x) {
         spacer = spacer + "    ";
      }

      String messageText = spacer + message;
      System.out.println(messageText);
   }

   public void importantMessage(String message, int indent) {
      String spacer = "";

      for(int x = 0; x < indent; ++x) {
         spacer = spacer + "    ";
      }

      String messageText = spacer + message;
      System.out.println(messageText);
   }

   public boolean errorCheck(boolean error, String classname, String message, int indent) {
      if(error) {
         this.errorMessage(classname, message, indent);
      }

      return error;
   }

   public void errorMessage(String classname, String message, int indent) {
      String spacer = "";

      for(int x = 0; x < indent; ++x) {
         spacer = spacer + "    ";
      }

      String headerLine = spacer + "Error in class \'" + classname + "\'";
      String messageText = "    " + spacer + message;
      System.out.println(headerLine);
      System.out.println(messageText);
   }

   public void printStackTrace(Exception e, int indent) {
      this.printExceptionMessage(e, indent);
      this.importantMessage("STACK TRACE:", indent);
      if(e != null) {
         StackTraceElement[] stack = e.getStackTrace();
         if(stack != null) {
            for(int x = 0; x < stack.length; ++x) {
               StackTraceElement line = stack[x];
               if(line != null) {
                  this.message(line.toString(), indent + 1);
               }
            }

         }
      }
   }

   public void printExceptionMessage(Exception e, int indent) {
      this.importantMessage("ERROR MESSAGE:", indent);
      if(e.getMessage() == null) {
         this.message("(none)", indent + 1);
      } else {
         this.message(e.getMessage(), indent + 1);
      }

   }
}
