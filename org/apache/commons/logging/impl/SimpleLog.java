package org.apache.commons.logging.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

public class SimpleLog implements Log, Serializable {
   private static final long serialVersionUID = 136942970684951178L;
   protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
   protected static final Properties simpleLogProps = new Properties();
   protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
   protected static volatile boolean showLogName = false;
   protected static volatile boolean showShortName = true;
   protected static volatile boolean showDateTime = false;
   protected static volatile String dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
   protected static DateFormat dateFormatter = null;
   public static final int LOG_LEVEL_TRACE = 1;
   public static final int LOG_LEVEL_DEBUG = 2;
   public static final int LOG_LEVEL_INFO = 3;
   public static final int LOG_LEVEL_WARN = 4;
   public static final int LOG_LEVEL_ERROR = 5;
   public static final int LOG_LEVEL_FATAL = 6;
   public static final int LOG_LEVEL_ALL = 0;
   public static final int LOG_LEVEL_OFF = 7;
   protected volatile String logName = null;
   protected volatile int currentLogLevel;
   private volatile String shortLogName = null;
   // $FF: synthetic field
   static Class class$java$lang$Thread;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$impl$SimpleLog;

   private static String getStringProperty(String name) {
      String prop = null;

      try {
         prop = System.getProperty(name);
      } catch (SecurityException var3) {
         ;
      }

      return prop == null?simpleLogProps.getProperty(name):prop;
   }

   private static String getStringProperty(String name, String dephault) {
      String prop = getStringProperty(name);
      return prop == null?dephault:prop;
   }

   private static boolean getBooleanProperty(String name, boolean dephault) {
      String prop = getStringProperty(name);
      return prop == null?dephault:"true".equalsIgnoreCase(prop);
   }

   public SimpleLog(String name) {
      this.logName = name;
      this.setLevel(3);
      String lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);

      for(int i = String.valueOf(name).lastIndexOf("."); null == lvl && i > -1; i = String.valueOf(name).lastIndexOf(".")) {
         name = name.substring(0, i);
         lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + name);
      }

      if(null == lvl) {
         lvl = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
      }

      if("all".equalsIgnoreCase(lvl)) {
         this.setLevel(0);
      } else if("trace".equalsIgnoreCase(lvl)) {
         this.setLevel(1);
      } else if("debug".equalsIgnoreCase(lvl)) {
         this.setLevel(2);
      } else if("info".equalsIgnoreCase(lvl)) {
         this.setLevel(3);
      } else if("warn".equalsIgnoreCase(lvl)) {
         this.setLevel(4);
      } else if("error".equalsIgnoreCase(lvl)) {
         this.setLevel(5);
      } else if("fatal".equalsIgnoreCase(lvl)) {
         this.setLevel(6);
      } else if("off".equalsIgnoreCase(lvl)) {
         this.setLevel(7);
      }

   }

   public void setLevel(int currentLogLevel) {
      this.currentLogLevel = currentLogLevel;
   }

   public int getLevel() {
      return this.currentLogLevel;
   }

   protected void log(int type, Object message, Throwable t) {
      StringBuffer buf = new StringBuffer();
      if(showDateTime) {
         Date now = new Date();
         String dateText;
         synchronized(dateFormatter) {
            dateText = dateFormatter.format(now);
         }

         buf.append(dateText);
         buf.append(" ");
      }

      switch(type) {
      case 1:
         buf.append("[TRACE] ");
         break;
      case 2:
         buf.append("[DEBUG] ");
         break;
      case 3:
         buf.append("[INFO] ");
         break;
      case 4:
         buf.append("[WARN] ");
         break;
      case 5:
         buf.append("[ERROR] ");
         break;
      case 6:
         buf.append("[FATAL] ");
      }

      if(showShortName) {
         if(this.shortLogName == null) {
            String slName = this.logName.substring(this.logName.lastIndexOf(".") + 1);
            this.shortLogName = slName.substring(slName.lastIndexOf("/") + 1);
         }

         buf.append(String.valueOf(this.shortLogName)).append(" - ");
      } else if(showLogName) {
         buf.append(String.valueOf(this.logName)).append(" - ");
      }

      buf.append(String.valueOf(message));
      if(t != null) {
         buf.append(" <");
         buf.append(t.toString());
         buf.append(">");
         StringWriter sw = new StringWriter(1024);
         PrintWriter pw = new PrintWriter(sw);
         t.printStackTrace(pw);
         pw.close();
         buf.append(sw.toString());
      }

      this.write(buf);
   }

   protected void write(StringBuffer buffer) {
      System.err.println(buffer.toString());
   }

   protected boolean isLevelEnabled(int logLevel) {
      return logLevel >= this.currentLogLevel;
   }

   public final void debug(Object message) {
      if(this.isLevelEnabled(2)) {
         this.log(2, message, (Throwable)null);
      }

   }

   public final void debug(Object message, Throwable t) {
      if(this.isLevelEnabled(2)) {
         this.log(2, message, t);
      }

   }

   public final void trace(Object message) {
      if(this.isLevelEnabled(1)) {
         this.log(1, message, (Throwable)null);
      }

   }

   public final void trace(Object message, Throwable t) {
      if(this.isLevelEnabled(1)) {
         this.log(1, message, t);
      }

   }

   public final void info(Object message) {
      if(this.isLevelEnabled(3)) {
         this.log(3, message, (Throwable)null);
      }

   }

   public final void info(Object message, Throwable t) {
      if(this.isLevelEnabled(3)) {
         this.log(3, message, t);
      }

   }

   public final void warn(Object message) {
      if(this.isLevelEnabled(4)) {
         this.log(4, message, (Throwable)null);
      }

   }

   public final void warn(Object message, Throwable t) {
      if(this.isLevelEnabled(4)) {
         this.log(4, message, t);
      }

   }

   public final void error(Object message) {
      if(this.isLevelEnabled(5)) {
         this.log(5, message, (Throwable)null);
      }

   }

   public final void error(Object message, Throwable t) {
      if(this.isLevelEnabled(5)) {
         this.log(5, message, t);
      }

   }

   public final void fatal(Object message) {
      if(this.isLevelEnabled(6)) {
         this.log(6, message, (Throwable)null);
      }

   }

   public final void fatal(Object message, Throwable t) {
      if(this.isLevelEnabled(6)) {
         this.log(6, message, t);
      }

   }

   public final boolean isDebugEnabled() {
      return this.isLevelEnabled(2);
   }

   public final boolean isErrorEnabled() {
      return this.isLevelEnabled(5);
   }

   public final boolean isFatalEnabled() {
      return this.isLevelEnabled(6);
   }

   public final boolean isInfoEnabled() {
      return this.isLevelEnabled(3);
   }

   public final boolean isTraceEnabled() {
      return this.isLevelEnabled(1);
   }

   public final boolean isWarnEnabled() {
      return this.isLevelEnabled(4);
   }

   private static ClassLoader getContextClassLoader() {
      ClassLoader classLoader = null;

      try {
         Method method = (class$java$lang$Thread == null?(class$java$lang$Thread = class$("java.lang.Thread")):class$java$lang$Thread).getMethod("getContextClassLoader", (Class[])null);

         try {
            classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Class[])null);
         } catch (IllegalAccessException var3) {
            ;
         } catch (InvocationTargetException var4) {
            if(!(var4.getTargetException() instanceof SecurityException)) {
               throw new LogConfigurationException("Unexpected InvocationTargetException", var4.getTargetException());
            }
         }
      } catch (NoSuchMethodException var5) {
         ;
      }

      if(classLoader == null) {
         classLoader = (class$org$apache$commons$logging$impl$SimpleLog == null?(class$org$apache$commons$logging$impl$SimpleLog = class$("org.apache.commons.logging.impl.SimpleLog")):class$org$apache$commons$logging$impl$SimpleLog).getClassLoader();
      }

      return classLoader;
   }

   private static InputStream getResourceAsStream(final String name) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader threadCL = SimpleLog.getContextClassLoader();
            return threadCL != null?threadCL.getResourceAsStream(name):ClassLoader.getSystemResourceAsStream(name);
         }
      });
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   static {
      InputStream in = getResourceAsStream("simplelog.properties");
      if(null != in) {
         try {
            simpleLogProps.load(in);
            in.close();
         } catch (IOException var3) {
            ;
         }
      }

      showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
      showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", showShortName);
      showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", showDateTime);
      if(showDateTime) {
         dateTimeFormat = getStringProperty("org.apache.commons.logging.simplelog.dateTimeFormat", dateTimeFormat);

         try {
            dateFormatter = new SimpleDateFormat(dateTimeFormat);
         } catch (IllegalArgumentException var2) {
            dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
            dateFormatter = new SimpleDateFormat(dateTimeFormat);
         }
      }

   }
}
