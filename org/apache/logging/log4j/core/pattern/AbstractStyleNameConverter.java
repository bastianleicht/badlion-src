package org.apache.logging.log4j.core.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.AnsiEscape;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

public abstract class AbstractStyleNameConverter extends LogEventPatternConverter {
   private final List formatters;
   private final String style;

   protected AbstractStyleNameConverter(String name, List formatters, String styling) {
      super(name, "style");
      this.formatters = formatters;
      this.style = styling;
   }

   protected static AbstractStyleNameConverter newInstance(Class asnConverterClass, String name, Configuration config, String[] options) {
      List<PatternFormatter> formatters = toPatternFormatterList(config, options);
      if(formatters == null) {
         return null;
      } else {
         try {
            Constructor<T> constructor = asnConverterClass.getConstructor(new Class[]{List.class, String.class});
            return (AbstractStyleNameConverter)constructor.newInstance(new Object[]{formatters, AnsiEscape.createSequence(new String[]{name})});
         } catch (SecurityException var6) {
            LOGGER.error((String)var6.toString(), (Throwable)var6);
         } catch (NoSuchMethodException var7) {
            LOGGER.error((String)var7.toString(), (Throwable)var7);
         } catch (IllegalArgumentException var8) {
            LOGGER.error((String)var8.toString(), (Throwable)var8);
         } catch (InstantiationException var9) {
            LOGGER.error((String)var9.toString(), (Throwable)var9);
         } catch (IllegalAccessException var10) {
            LOGGER.error((String)var10.toString(), (Throwable)var10);
         } catch (InvocationTargetException var11) {
            LOGGER.error((String)var11.toString(), (Throwable)var11);
         }

         return null;
      }
   }

   private static List toPatternFormatterList(Configuration config, String[] options) {
      if(options.length != 0 && options[0] != null) {
         PatternParser parser = PatternLayout.createPatternParser(config);
         if(parser == null) {
            LOGGER.error("No PatternParser created for config=" + config + ", options=" + Arrays.toString(options));
            return null;
         } else {
            return parser.parse(options[0]);
         }
      } else {
         LOGGER.error("No pattern supplied on style for config=" + config);
         return null;
      }
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      StringBuilder buf = new StringBuilder();

      for(PatternFormatter formatter : this.formatters) {
         formatter.format(event, buf);
      }

      if(buf.length() > 0) {
         toAppendTo.append(this.style).append(buf.toString()).append(AnsiEscape.getDefaultStyle());
      }

   }

   @Plugin(
      name = "black",
      category = "Converter"
   )
   @ConverterKeys({"black"})
   public static final class Black extends AbstractStyleNameConverter {
      protected static final String NAME = "black";

      public Black(List formatters, String styling) {
         super("black", formatters, styling);
      }

      public static AbstractStyleNameConverter.Black newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Black)newInstance(AbstractStyleNameConverter.Black.class, "black", config, options);
      }
   }

   @Plugin(
      name = "blue",
      category = "Converter"
   )
   @ConverterKeys({"blue"})
   public static final class Blue extends AbstractStyleNameConverter {
      protected static final String NAME = "blue";

      public Blue(List formatters, String styling) {
         super("blue", formatters, styling);
      }

      public static AbstractStyleNameConverter.Blue newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Blue)newInstance(AbstractStyleNameConverter.Blue.class, "blue", config, options);
      }
   }

   @Plugin(
      name = "cyan",
      category = "Converter"
   )
   @ConverterKeys({"cyan"})
   public static final class Cyan extends AbstractStyleNameConverter {
      protected static final String NAME = "cyan";

      public Cyan(List formatters, String styling) {
         super("cyan", formatters, styling);
      }

      public static AbstractStyleNameConverter.Cyan newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Cyan)newInstance(AbstractStyleNameConverter.Cyan.class, "cyan", config, options);
      }
   }

   @Plugin(
      name = "green",
      category = "Converter"
   )
   @ConverterKeys({"green"})
   public static final class Green extends AbstractStyleNameConverter {
      protected static final String NAME = "green";

      public Green(List formatters, String styling) {
         super("green", formatters, styling);
      }

      public static AbstractStyleNameConverter.Green newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Green)newInstance(AbstractStyleNameConverter.Green.class, "green", config, options);
      }
   }

   @Plugin(
      name = "magenta",
      category = "Converter"
   )
   @ConverterKeys({"magenta"})
   public static final class Magenta extends AbstractStyleNameConverter {
      protected static final String NAME = "magenta";

      public Magenta(List formatters, String styling) {
         super("magenta", formatters, styling);
      }

      public static AbstractStyleNameConverter.Magenta newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Magenta)newInstance(AbstractStyleNameConverter.Magenta.class, "magenta", config, options);
      }
   }

   @Plugin(
      name = "red",
      category = "Converter"
   )
   @ConverterKeys({"red"})
   public static final class Red extends AbstractStyleNameConverter {
      protected static final String NAME = "red";

      public Red(List formatters, String styling) {
         super("red", formatters, styling);
      }

      public static AbstractStyleNameConverter.Red newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Red)newInstance(AbstractStyleNameConverter.Red.class, "red", config, options);
      }
   }

   @Plugin(
      name = "white",
      category = "Converter"
   )
   @ConverterKeys({"white"})
   public static final class White extends AbstractStyleNameConverter {
      protected static final String NAME = "white";

      public White(List formatters, String styling) {
         super("white", formatters, styling);
      }

      public static AbstractStyleNameConverter.White newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.White)newInstance(AbstractStyleNameConverter.White.class, "white", config, options);
      }
   }

   @Plugin(
      name = "yellow",
      category = "Converter"
   )
   @ConverterKeys({"yellow"})
   public static final class Yellow extends AbstractStyleNameConverter {
      protected static final String NAME = "yellow";

      public Yellow(List formatters, String styling) {
         super("yellow", formatters, styling);
      }

      public static AbstractStyleNameConverter.Yellow newInstance(Configuration config, String[] options) {
         return (AbstractStyleNameConverter.Yellow)newInstance(AbstractStyleNameConverter.Yellow.class, "yellow", config, options);
      }
   }
}
