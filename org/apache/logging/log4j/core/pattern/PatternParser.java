package org.apache.logging.log4j.core.pattern;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.LiteralPatternConverter;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.status.StatusLogger;

public final class PatternParser {
   private static final char ESCAPE_CHAR = '%';
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int BUF_SIZE = 32;
   private static final int DECIMAL = 10;
   private final Configuration config;
   private final Map converterRules;

   public PatternParser(String converterKey) {
      this((Configuration)null, converterKey, (Class)null, (Class)null);
   }

   public PatternParser(Configuration config, String converterKey, Class expected) {
      this(config, converterKey, expected, (Class)null);
   }

   public PatternParser(Configuration config, String converterKey, Class expectedClass, Class filterClass) {
      this.config = config;
      PluginManager manager = new PluginManager(converterKey, expectedClass);
      manager.collectPlugins();
      Map<String, PluginType<?>> plugins = manager.getPlugins();
      Map<String, Class<PatternConverter>> converters = new HashMap();

      for(PluginType<?> type : plugins.values()) {
         try {
            Class<PatternConverter> clazz = type.getPluginClass();
            if(filterClass == null || filterClass.isAssignableFrom(clazz)) {
               ConverterKeys keys = (ConverterKeys)clazz.getAnnotation(ConverterKeys.class);
               if(keys != null) {
                  for(String key : keys.value()) {
                     converters.put(key, clazz);
                  }
               }
            }
         } catch (Exception var16) {
            LOGGER.error((String)("Error processing plugin " + type.getElementName()), (Throwable)var16);
         }
      }

      this.converterRules = converters;
   }

   public List parse(String pattern) {
      return this.parse(pattern, false);
   }

   public List parse(String pattern, boolean alwaysWriteExceptions) {
      List<PatternFormatter> list = new ArrayList();
      List<PatternConverter> converters = new ArrayList();
      List<FormattingInfo> fields = new ArrayList();
      this.parse(pattern, converters, fields);
      Iterator<FormattingInfo> fieldIter = fields.iterator();
      boolean handlesThrowable = false;

      for(PatternConverter converter : converters) {
         LogEventPatternConverter pc;
         if(converter instanceof LogEventPatternConverter) {
            pc = (LogEventPatternConverter)converter;
            handlesThrowable |= pc.handlesThrowable();
         } else {
            pc = new LiteralPatternConverter(this.config, "");
         }

         FormattingInfo field;
         if(fieldIter.hasNext()) {
            field = (FormattingInfo)fieldIter.next();
         } else {
            field = FormattingInfo.getDefault();
         }

         list.add(new PatternFormatter(pc, field));
      }

      if(alwaysWriteExceptions && !handlesThrowable) {
         LogEventPatternConverter pc = ExtendedThrowablePatternConverter.newInstance((String[])null);
         list.add(new PatternFormatter(pc, FormattingInfo.getDefault()));
      }

      return list;
   }

   private static int extractConverter(char lastChar, String pattern, int i, StringBuilder convBuf, StringBuilder currentLiteral) {
      convBuf.setLength(0);
      if(!Character.isUnicodeIdentifierStart(lastChar)) {
         return i;
      } else {
         convBuf.append(lastChar);

         while(i < pattern.length() && Character.isUnicodeIdentifierPart(pattern.charAt(i))) {
            convBuf.append(pattern.charAt(i));
            currentLiteral.append(pattern.charAt(i));
            ++i;
         }

         return i;
      }
   }

   private static int extractOptions(String pattern, int i, List options) {
      while(true) {
         if(i < pattern.length() && pattern.charAt(i) == 123) {
            int begin = i++;
            int depth = 0;

            int end;
            while(true) {
               end = pattern.indexOf(125, i);
               if(end != -1) {
                  int next = pattern.indexOf("{", i);
                  if(next != -1 && next < end) {
                     i = end + 1;
                     ++depth;
                  } else if(depth > 0) {
                     --depth;
                  }
               }

               if(depth <= 0) {
                  break;
               }
            }

            if(end != -1) {
               String r = pattern.substring(begin + 1, end);
               options.add(r);
               i = end + 1;
               continue;
            }
         }

         return i;
      }
   }

   public void parse(String pattern, List patternConverters, List formattingInfos) {
      if(pattern == null) {
         throw new NullPointerException("pattern");
      } else {
         StringBuilder currentLiteral = new StringBuilder(32);
         int patternLength = pattern.length();
         PatternParser.ParserState state = PatternParser.ParserState.LITERAL_STATE;
         int i = 0;
         FormattingInfo formattingInfo = FormattingInfo.getDefault();

         while(i < patternLength) {
            char c = pattern.charAt(i++);
            switch(state) {
            case LITERAL_STATE:
               if(i == patternLength) {
                  currentLiteral.append(c);
               } else if(c == 37) {
                  switch(pattern.charAt(i)) {
                  case '%':
                     currentLiteral.append(c);
                     ++i;
                     break;
                  default:
                     if(currentLiteral.length() != 0) {
                        patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
                        formattingInfos.add(FormattingInfo.getDefault());
                     }

                     currentLiteral.setLength(0);
                     currentLiteral.append(c);
                     state = PatternParser.ParserState.CONVERTER_STATE;
                     formattingInfo = FormattingInfo.getDefault();
                  }
               } else {
                  currentLiteral.append(c);
               }
               break;
            case CONVERTER_STATE:
               currentLiteral.append(c);
               switch(c) {
               case '-':
                  formattingInfo = new FormattingInfo(true, formattingInfo.getMinLength(), formattingInfo.getMaxLength());
                  continue;
               case '.':
                  state = PatternParser.ParserState.DOT_STATE;
                  continue;
               default:
                  if(c >= 48 && c <= 57) {
                     formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), c - 48, formattingInfo.getMaxLength());
                     state = PatternParser.ParserState.MIN_STATE;
                     continue;
                  }

                  i = this.finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                  state = PatternParser.ParserState.LITERAL_STATE;
                  formattingInfo = FormattingInfo.getDefault();
                  currentLiteral.setLength(0);
                  continue;
               }
            case MIN_STATE:
               currentLiteral.append(c);
               if(c >= 48 && c <= 57) {
                  formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength() * 10 + c - 48, formattingInfo.getMaxLength());
               } else if(c == 46) {
                  state = PatternParser.ParserState.DOT_STATE;
               } else {
                  i = this.finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                  state = PatternParser.ParserState.LITERAL_STATE;
                  formattingInfo = FormattingInfo.getDefault();
                  currentLiteral.setLength(0);
               }
               break;
            case DOT_STATE:
               currentLiteral.append(c);
               if(c >= 48 && c <= 57) {
                  formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), c - 48);
                  state = PatternParser.ParserState.MAX_STATE;
                  break;
               }

               LOGGER.error("Error occurred in position " + i + ".\n Was expecting digit, instead got char \"" + c + "\".");
               state = PatternParser.ParserState.LITERAL_STATE;
               break;
            case MAX_STATE:
               currentLiteral.append(c);
               if(c >= 48 && c <= 57) {
                  formattingInfo = new FormattingInfo(formattingInfo.isLeftAligned(), formattingInfo.getMinLength(), formattingInfo.getMaxLength() * 10 + c - 48);
               } else {
                  i = this.finalizeConverter(c, pattern, i, currentLiteral, formattingInfo, this.converterRules, patternConverters, formattingInfos);
                  state = PatternParser.ParserState.LITERAL_STATE;
                  formattingInfo = FormattingInfo.getDefault();
                  currentLiteral.setLength(0);
               }
            }
         }

         if(currentLiteral.length() != 0) {
            patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
            formattingInfos.add(FormattingInfo.getDefault());
         }

      }
   }

   private PatternConverter createConverter(String converterId, StringBuilder currentLiteral, Map rules, List options) {
      String converterName = converterId;
      Class<PatternConverter> converterClass = null;

      for(int i = converterId.length(); i > 0 && converterClass == null; --i) {
         converterName = converterName.substring(0, i);
         if(converterClass == null && rules != null) {
            converterClass = (Class)rules.get(converterName);
         }
      }

      if(converterClass == null) {
         LOGGER.error("Unrecognized format specifier [" + converterId + "]");
         return null;
      } else {
         Method[] methods = converterClass.getDeclaredMethods();
         Method newInstanceMethod = null;

         for(Method method : methods) {
            if(Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(converterClass) && method.getName().equals("newInstance")) {
               if(newInstanceMethod == null) {
                  newInstanceMethod = method;
               } else if(method.getReturnType().equals(newInstanceMethod.getReturnType())) {
                  LOGGER.error("Class " + converterClass + " cannot contain multiple static newInstance methods");
                  return null;
               }
            }
         }

         if(newInstanceMethod == null) {
            LOGGER.error("Class " + converterClass + " does not contain a static newInstance method");
            return null;
         } else {
            Class<?>[] parmTypes = newInstanceMethod.getParameterTypes();
            Object[] parms = parmTypes.length > 0?new Object[parmTypes.length]:null;
            if(parms != null) {
               int i = 0;
               boolean errors = false;

               for(Class<?> clazz : parmTypes) {
                  if(clazz.isArray() && clazz.getName().equals("[Ljava.lang.String;")) {
                     String[] optionsArray = (String[])options.toArray(new String[options.size()]);
                     parms[i] = optionsArray;
                  } else if(clazz.isAssignableFrom(Configuration.class)) {
                     parms[i] = this.config;
                  } else {
                     LOGGER.error("Unknown parameter type " + clazz.getName() + " for static newInstance method of " + converterClass.getName());
                     errors = true;
                  }

                  ++i;
               }

               if(errors) {
                  return null;
               }
            }

            try {
               Object newObj = newInstanceMethod.invoke((Object)null, parms);
               if(newObj instanceof PatternConverter) {
                  currentLiteral.delete(0, currentLiteral.length() - (converterId.length() - converterName.length()));
                  return (PatternConverter)newObj;
               }

               LOGGER.warn("Class " + converterClass.getName() + " does not extend PatternConverter.");
            } catch (Exception var18) {
               LOGGER.error((String)("Error creating converter for " + converterId), (Throwable)var18);
            }

            return null;
         }
      }
   }

   private int finalizeConverter(char c, String pattern, int i, StringBuilder currentLiteral, FormattingInfo formattingInfo, Map rules, List patternConverters, List formattingInfos) {
      StringBuilder convBuf = new StringBuilder();
      i = extractConverter(c, pattern, i, convBuf, currentLiteral);
      String converterId = convBuf.toString();
      List<String> options = new ArrayList();
      i = extractOptions(pattern, i, options);
      PatternConverter pc = this.createConverter(converterId, currentLiteral, rules, options);
      if(pc == null) {
         StringBuilder msg;
         if(Strings.isEmpty(converterId)) {
            msg = new StringBuilder("Empty conversion specifier starting at position ");
         } else {
            msg = new StringBuilder("Unrecognized conversion specifier [");
            msg.append(converterId);
            msg.append("] starting at position ");
         }

         msg.append(Integer.toString(i));
         msg.append(" in conversion pattern.");
         LOGGER.error(msg.toString());
         patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
         formattingInfos.add(FormattingInfo.getDefault());
      } else {
         patternConverters.add(pc);
         formattingInfos.add(formattingInfo);
         if(currentLiteral.length() > 0) {
            patternConverters.add(new LiteralPatternConverter(this.config, currentLiteral.toString()));
            formattingInfos.add(FormattingInfo.getDefault());
         }
      }

      currentLiteral.setLength(0);
      return i;
   }

   private static enum ParserState {
      LITERAL_STATE,
      CONVERTER_STATE,
      DOT_STATE,
      MIN_STATE,
      MAX_STATE;
   }
}
