package org.apache.logging.log4j.message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.logging.log4j.message.Message;

public class ParameterizedMessage implements Message {
   public static final String RECURSION_PREFIX = "[...";
   public static final String RECURSION_SUFFIX = "...]";
   public static final String ERROR_PREFIX = "[!!!";
   public static final String ERROR_SEPARATOR = "=>";
   public static final String ERROR_MSG_SEPARATOR = ":";
   public static final String ERROR_SUFFIX = "!!!]";
   private static final long serialVersionUID = -665975803997290697L;
   private static final int HASHVAL = 31;
   private static final char DELIM_START = '{';
   private static final char DELIM_STOP = '}';
   private static final char ESCAPE_CHAR = '\\';
   private final String messagePattern;
   private final String[] stringArgs;
   private transient Object[] argArray;
   private transient String formattedMessage;
   private transient Throwable throwable;

   public ParameterizedMessage(String messagePattern, String[] stringArgs, Throwable throwable) {
      this.messagePattern = messagePattern;
      this.stringArgs = stringArgs;
      this.throwable = throwable;
   }

   public ParameterizedMessage(String messagePattern, Object[] objectArgs, Throwable throwable) {
      this.messagePattern = messagePattern;
      this.throwable = throwable;
      this.stringArgs = this.parseArguments(objectArgs);
   }

   public ParameterizedMessage(String messagePattern, Object[] arguments) {
      this.messagePattern = messagePattern;
      this.stringArgs = this.parseArguments(arguments);
   }

   public ParameterizedMessage(String messagePattern, Object arg) {
      this(messagePattern, new Object[]{arg});
   }

   public ParameterizedMessage(String messagePattern, Object arg1, Object arg2) {
      this(messagePattern, new Object[]{arg1, arg2});
   }

   private String[] parseArguments(Object[] arguments) {
      if(arguments == null) {
         return null;
      } else {
         int argsCount = countArgumentPlaceholders(this.messagePattern);
         int resultArgCount = arguments.length;
         if(argsCount < arguments.length && this.throwable == null && arguments[arguments.length - 1] instanceof Throwable) {
            this.throwable = (Throwable)arguments[arguments.length - 1];
            --resultArgCount;
         }

         this.argArray = new Object[resultArgCount];

         for(int i = 0; i < resultArgCount; ++i) {
            this.argArray[i] = arguments[i];
         }

         String[] strArgs;
         if(argsCount == 1 && this.throwable == null && arguments.length > 1) {
            strArgs = new String[]{deepToString(arguments)};
         } else {
            strArgs = new String[resultArgCount];

            for(int i = 0; i < strArgs.length; ++i) {
               strArgs[i] = deepToString(arguments[i]);
            }
         }

         return strArgs;
      }
   }

   public String getFormattedMessage() {
      if(this.formattedMessage == null) {
         this.formattedMessage = this.formatMessage(this.messagePattern, this.stringArgs);
      }

      return this.formattedMessage;
   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return (Object[])(this.argArray != null?this.argArray:this.stringArgs);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   protected String formatMessage(String msgPattern, String[] sArgs) {
      return format(msgPattern, sArgs);
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         ParameterizedMessage that = (ParameterizedMessage)o;
         if(this.messagePattern != null) {
            if(!this.messagePattern.equals(that.messagePattern)) {
               return false;
            }
         } else if(that.messagePattern != null) {
            return false;
         }

         if(!Arrays.equals(this.stringArgs, that.stringArgs)) {
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.messagePattern != null?this.messagePattern.hashCode():0;
      result = 31 * result + (this.stringArgs != null?Arrays.hashCode(this.stringArgs):0);
      return result;
   }

   public static String format(String messagePattern, Object[] arguments) {
      if(messagePattern != null && arguments != null && arguments.length != 0) {
         StringBuilder result = new StringBuilder();
         int escapeCounter = 0;
         int currentArgument = 0;

         for(int i = 0; i < messagePattern.length(); ++i) {
            char curChar = messagePattern.charAt(i);
            if(curChar == 92) {
               ++escapeCounter;
            } else if(curChar == 123 && i < messagePattern.length() - 1 && messagePattern.charAt(i + 1) == 125) {
               int escapedEscapes = escapeCounter / 2;

               for(int j = 0; j < escapedEscapes; ++j) {
                  result.append('\\');
               }

               if(escapeCounter % 2 == 1) {
                  result.append('{');
                  result.append('}');
               } else {
                  if(currentArgument < arguments.length) {
                     result.append(arguments[currentArgument]);
                  } else {
                     result.append('{').append('}');
                  }

                  ++currentArgument;
               }

               ++i;
               escapeCounter = 0;
            } else {
               if(escapeCounter > 0) {
                  for(int j = 0; j < escapeCounter; ++j) {
                     result.append('\\');
                  }

                  escapeCounter = 0;
               }

               result.append(curChar);
            }
         }

         return result.toString();
      } else {
         return messagePattern;
      }
   }

   public static int countArgumentPlaceholders(String messagePattern) {
      if(messagePattern == null) {
         return 0;
      } else {
         int delim = messagePattern.indexOf(123);
         if(delim == -1) {
            return 0;
         } else {
            int result = 0;
            boolean isEscaped = false;

            for(int i = 0; i < messagePattern.length(); ++i) {
               char curChar = messagePattern.charAt(i);
               if(curChar == 92) {
                  isEscaped = !isEscaped;
               } else if(curChar == 123) {
                  if(!isEscaped && i < messagePattern.length() - 1 && messagePattern.charAt(i + 1) == 125) {
                     ++result;
                     ++i;
                  }

                  isEscaped = false;
               } else {
                  isEscaped = false;
               }
            }

            return result;
         }
      }
   }

   public static String deepToString(Object o) {
      if(o == null) {
         return null;
      } else if(o instanceof String) {
         return (String)o;
      } else {
         StringBuilder str = new StringBuilder();
         Set<String> dejaVu = new HashSet();
         recursiveDeepToString(o, str, dejaVu);
         return str.toString();
      }
   }

   private static void recursiveDeepToString(Object o, StringBuilder str, Set dejaVu) {
      if(o == null) {
         str.append("null");
      } else if(o instanceof String) {
         str.append(o);
      } else {
         Class<?> oClass = o.getClass();
         if(oClass.isArray()) {
            if(oClass == byte[].class) {
               str.append(Arrays.toString((byte[])((byte[])o)));
            } else if(oClass == short[].class) {
               str.append(Arrays.toString((short[])((short[])o)));
            } else if(oClass == int[].class) {
               str.append(Arrays.toString((int[])((int[])o)));
            } else if(oClass == long[].class) {
               str.append(Arrays.toString((long[])((long[])o)));
            } else if(oClass == float[].class) {
               str.append(Arrays.toString((float[])((float[])o)));
            } else if(oClass == double[].class) {
               str.append(Arrays.toString((double[])((double[])o)));
            } else if(oClass == boolean[].class) {
               str.append(Arrays.toString((boolean[])((boolean[])o)));
            } else if(oClass == char[].class) {
               str.append(Arrays.toString((char[])((char[])o)));
            } else {
               String id = identityToString(o);
               if(dejaVu.contains(id)) {
                  str.append("[...").append(id).append("...]");
               } else {
                  dejaVu.add(id);
                  Object[] oArray = (Object[])((Object[])o);
                  str.append("[");
                  boolean first = true;

                  for(Object current : oArray) {
                     if(first) {
                        first = false;
                     } else {
                        str.append(", ");
                     }

                     recursiveDeepToString(current, str, new HashSet(dejaVu));
                  }

                  str.append("]");
               }
            }
         } else if(o instanceof Map) {
            String id = identityToString(o);
            if(dejaVu.contains(id)) {
               str.append("[...").append(id).append("...]");
            } else {
               dejaVu.add(id);
               Map<?, ?> oMap = (Map)o;
               str.append("{");
               boolean isFirst = true;

               for(Object o1 : oMap.entrySet()) {
                  Entry<?, ?> current = (Entry)o1;
                  if(isFirst) {
                     isFirst = false;
                  } else {
                     str.append(", ");
                  }

                  Object key = current.getKey();
                  Object value = current.getValue();
                  recursiveDeepToString(key, str, new HashSet(dejaVu));
                  str.append("=");
                  recursiveDeepToString(value, str, new HashSet(dejaVu));
               }

               str.append("}");
            }
         } else if(o instanceof Collection) {
            String id = identityToString(o);
            if(dejaVu.contains(id)) {
               str.append("[...").append(id).append("...]");
            } else {
               dejaVu.add(id);
               Collection<?> oCol = (Collection)o;
               str.append("[");
               boolean isFirst = true;

               for(Object anOCol : oCol) {
                  if(isFirst) {
                     isFirst = false;
                  } else {
                     str.append(", ");
                  }

                  recursiveDeepToString(anOCol, str, new HashSet(dejaVu));
               }

               str.append("]");
            }
         } else if(o instanceof Date) {
            Date date = (Date)o;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ");
            str.append(format.format(date));
         } else {
            try {
               str.append(o.toString());
            } catch (Throwable var12) {
               str.append("[!!!");
               str.append(identityToString(o));
               str.append("=>");
               String msg = var12.getMessage();
               String className = var12.getClass().getName();
               str.append(className);
               if(!className.equals(msg)) {
                  str.append(":");
                  str.append(msg);
               }

               str.append("!!!]");
            }
         }

      }
   }

   public static String identityToString(Object obj) {
      return obj == null?null:obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
   }

   public String toString() {
      return "ParameterizedMessage[messagePattern=" + this.messagePattern + ", stringArgs=" + Arrays.toString(this.stringArgs) + ", throwable=" + this.throwable + "]";
   }
}
