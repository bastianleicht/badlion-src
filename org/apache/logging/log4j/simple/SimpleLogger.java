package org.apache.logging.log4j.simple;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SimpleLogger extends AbstractLogger {
   private static final char SPACE = ' ';
   private DateFormat dateFormatter;
   private Level level;
   private final boolean showDateTime;
   private final boolean showContextMap;
   private PrintStream stream;
   private final String logName;

   public SimpleLogger(String name, Level defaultLevel, boolean showLogName, boolean showShortLogName, boolean showDateTime, boolean showContextMap, String dateTimeFormat, MessageFactory messageFactory, PropertiesUtil props, PrintStream stream) {
      super(name, messageFactory);
      String lvl = props.getStringProperty("org.apache.logging.log4j.simplelog." + name + ".level");
      this.level = Level.toLevel(lvl, defaultLevel);
      if(showShortLogName) {
         int index = name.lastIndexOf(".");
         if(index > 0 && index < name.length()) {
            this.logName = name.substring(index + 1);
         } else {
            this.logName = name;
         }
      } else if(showLogName) {
         this.logName = name;
      } else {
         this.logName = null;
      }

      this.showDateTime = showDateTime;
      this.showContextMap = showContextMap;
      this.stream = stream;
      if(showDateTime) {
         try {
            this.dateFormatter = new SimpleDateFormat(dateTimeFormat);
         } catch (IllegalArgumentException var13) {
            this.dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
         }
      }

   }

   public void setStream(PrintStream stream) {
      this.stream = stream;
   }

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level level) {
      if(level != null) {
         this.level = level;
      }

   }

   public void log(Marker marker, String fqcn, Level level, Message msg, Throwable throwable) {
      StringBuilder sb = new StringBuilder();
      if(this.showDateTime) {
         Date now = new Date();
         String dateText;
         synchronized(this.dateFormatter) {
            dateText = this.dateFormatter.format(now);
         }

         sb.append(dateText);
         sb.append(' ');
      }

      sb.append(level.toString());
      sb.append(' ');
      if(this.logName != null && this.logName.length() > 0) {
         sb.append(this.logName);
         sb.append(' ');
      }

      sb.append(msg.getFormattedMessage());
      if(this.showContextMap) {
         Map<String, String> mdc = ThreadContext.getContext();
         if(mdc.size() > 0) {
            sb.append(' ');
            sb.append(mdc.toString());
            sb.append(' ');
         }
      }

      Object[] params = msg.getParameters();
      Throwable t;
      if(throwable == null && params != null && params[params.length - 1] instanceof Throwable) {
         t = (Throwable)params[params.length - 1];
      } else {
         t = throwable;
      }

      if(t != null) {
         sb.append(' ');
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         t.printStackTrace(new PrintStream(baos));
         sb.append(baos.toString());
      }

      this.stream.println(sb.toString());
   }

   protected boolean isEnabled(Level level, Marker marker, String msg) {
      return this.level.intLevel() >= level.intLevel();
   }

   protected boolean isEnabled(Level level, Marker marker, String msg, Throwable t) {
      return this.level.intLevel() >= level.intLevel();
   }

   protected boolean isEnabled(Level level, Marker marker, String msg, Object... p1) {
      return this.level.intLevel() >= level.intLevel();
   }

   protected boolean isEnabled(Level level, Marker marker, Object msg, Throwable t) {
      return this.level.intLevel() >= level.intLevel();
   }

   protected boolean isEnabled(Level level, Marker marker, Message msg, Throwable t) {
      return this.level.intLevel() >= level.intLevel();
   }
}
