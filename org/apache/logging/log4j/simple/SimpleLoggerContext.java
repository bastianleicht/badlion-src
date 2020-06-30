package org.apache.logging.log4j.simple;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SimpleLoggerContext implements LoggerContext {
   protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
   protected static final String SYSTEM_PREFIX = "org.apache.logging.log4j.simplelog.";
   private final Properties simpleLogProps = new Properties();
   private final PropertiesUtil props = new PropertiesUtil("log4j2.simplelog.properties");
   private final boolean showLogName;
   private final boolean showShortName;
   private final boolean showDateTime;
   private final boolean showContextMap;
   private final String dateTimeFormat;
   private final Level defaultLevel;
   private final PrintStream stream;
   private final ConcurrentMap loggers = new ConcurrentHashMap();

   public SimpleLoggerContext() {
      this.showContextMap = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showContextMap", false);
      this.showLogName = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showlogname", false);
      this.showShortName = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showShortLogname", true);
      this.showDateTime = this.props.getBooleanProperty("org.apache.logging.log4j.simplelog.showdatetime", false);
      String lvl = this.props.getStringProperty("org.apache.logging.log4j.simplelog.level");
      this.defaultLevel = Level.toLevel(lvl, Level.ERROR);
      this.dateTimeFormat = this.showDateTime?this.props.getStringProperty("org.apache.logging.log4j.simplelog.dateTimeFormat", "yyyy/MM/dd HH:mm:ss:SSS zzz"):null;
      String fileName = this.props.getStringProperty("org.apache.logging.log4j.simplelog.logFile", "system.err");
      PrintStream ps;
      if("system.err".equalsIgnoreCase(fileName)) {
         ps = System.err;
      } else if("system.out".equalsIgnoreCase(fileName)) {
         ps = System.out;
      } else {
         try {
            FileOutputStream os = new FileOutputStream(fileName);
            ps = new PrintStream(os);
         } catch (FileNotFoundException var5) {
            ps = System.err;
         }
      }

      this.stream = ps;
   }

   public Logger getLogger(String name) {
      return this.getLogger(name, (MessageFactory)null);
   }

   public Logger getLogger(String name, MessageFactory messageFactory) {
      if(this.loggers.containsKey(name)) {
         Logger logger = (Logger)this.loggers.get(name);
         AbstractLogger.checkMessageFactory(logger, messageFactory);
         return logger;
      } else {
         this.loggers.putIfAbsent(name, new SimpleLogger(name, this.defaultLevel, this.showLogName, this.showShortName, this.showDateTime, this.showContextMap, this.dateTimeFormat, messageFactory, this.props, this.stream));
         return (Logger)this.loggers.get(name);
      }
   }

   public boolean hasLogger(String name) {
      return false;
   }

   public Object getExternalContext() {
      return null;
   }
}
