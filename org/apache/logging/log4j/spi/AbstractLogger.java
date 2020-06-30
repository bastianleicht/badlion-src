package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractLogger implements Logger {
   public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
   public static final Marker ENTRY_MARKER = MarkerManager.getMarker("ENTRY", FLOW_MARKER);
   public static final Marker EXIT_MARKER = MarkerManager.getMarker("EXIT", FLOW_MARKER);
   public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
   public static final Marker THROWING_MARKER = MarkerManager.getMarker("THROWING", EXCEPTION_MARKER);
   public static final Marker CATCHING_MARKER = MarkerManager.getMarker("CATCHING", EXCEPTION_MARKER);
   public static final Class DEFAULT_MESSAGE_FACTORY_CLASS = ParameterizedMessageFactory.class;
   private static final String FQCN = AbstractLogger.class.getName();
   private static final String THROWING = "throwing";
   private static final String CATCHING = "catching";
   private final String name;
   private final MessageFactory messageFactory;

   public AbstractLogger() {
      this.name = this.getClass().getName();
      this.messageFactory = this.createDefaultMessageFactory();
   }

   public AbstractLogger(String name) {
      this.name = name;
      this.messageFactory = this.createDefaultMessageFactory();
   }

   public AbstractLogger(String name, MessageFactory messageFactory) {
      this.name = name;
      this.messageFactory = messageFactory == null?this.createDefaultMessageFactory():messageFactory;
   }

   public static void checkMessageFactory(Logger logger, MessageFactory messageFactory) {
      String name = logger.getName();
      MessageFactory loggerMessageFactory = logger.getMessageFactory();
      if(messageFactory != null && !loggerMessageFactory.equals(messageFactory)) {
         StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with the message factory {}, which may create log events with unexpected formatting.", new Object[]{name, loggerMessageFactory, messageFactory});
      } else if(messageFactory == null && !loggerMessageFactory.getClass().equals(DEFAULT_MESSAGE_FACTORY_CLASS)) {
         StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with a null message factory (defaults to {}), which may create log events with unexpected formatting.", new Object[]{name, loggerMessageFactory, DEFAULT_MESSAGE_FACTORY_CLASS.getName()});
      }

   }

   public void catching(Level level, Throwable t) {
      this.catching(FQCN, level, t);
   }

   public void catching(Throwable t) {
      this.catching(FQCN, Level.ERROR, t);
   }

   protected void catching(String fqcn, Level level, Throwable t) {
      if(this.isEnabled(level, CATCHING_MARKER, (Object)((Object)null), (Throwable)null)) {
         this.log(CATCHING_MARKER, fqcn, level, this.messageFactory.newMessage("catching"), t);
      }

   }

   private MessageFactory createDefaultMessageFactory() {
      try {
         return (MessageFactory)DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      } catch (IllegalAccessException var3) {
         throw new IllegalStateException(var3);
      }
   }

   public void debug(Marker marker, Message msg) {
      if(this.isEnabled(Level.DEBUG, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.DEBUG, msg, (Throwable)null);
      }

   }

   public void debug(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.DEBUG, marker, msg, t)) {
         this.log(marker, FQCN, Level.DEBUG, msg, t);
      }

   }

   public void debug(Marker marker, Object message) {
      if(this.isEnabled(Level.DEBUG, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void debug(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.DEBUG, marker, message, t)) {
         this.log(marker, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), t);
      }

   }

   public void debug(Marker marker, String message) {
      if(this.isEnabled(Level.DEBUG, marker, message)) {
         this.log(marker, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void debug(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.DEBUG, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.DEBUG, msg, msg.getThrowable());
      }

   }

   public void debug(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.DEBUG, marker, message, t)) {
         this.log(marker, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), t);
      }

   }

   public void debug(Message msg) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.DEBUG, msg, (Throwable)null);
      }

   }

   public void debug(Message msg, Throwable t) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.DEBUG, msg, t);
      }

   }

   public void debug(Object message) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void debug(Object message, Throwable t) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), t);
      }

   }

   public void debug(String message) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void debug(String message, Object... params) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.DEBUG, msg, msg.getThrowable());
      }

   }

   public void debug(String message, Throwable t) {
      if(this.isEnabled(Level.DEBUG, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.DEBUG, this.messageFactory.newMessage(message), t);
      }

   }

   public void entry() {
      this.entry(FQCN, new Object[0]);
   }

   public void entry(Object... params) {
      this.entry(FQCN, params);
   }

   protected void entry(String fqcn, Object... params) {
      if(this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)((Object)null), (Throwable)null)) {
         this.log(ENTRY_MARKER, fqcn, Level.TRACE, this.entryMsg(params.length, params), (Throwable)null);
      }

   }

   private Message entryMsg(int count, Object... params) {
      if(count == 0) {
         return this.messageFactory.newMessage("entry");
      } else {
         StringBuilder sb = new StringBuilder("entry params(");
         int i = 0;

         for(Object parm : params) {
            if(parm != null) {
               sb.append(parm.toString());
            } else {
               sb.append("null");
            }

            ++i;
            if(i < params.length) {
               sb.append(", ");
            }
         }

         sb.append(")");
         return this.messageFactory.newMessage(sb.toString());
      }
   }

   public void error(Marker marker, Message msg) {
      if(this.isEnabled(Level.ERROR, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.ERROR, msg, (Throwable)null);
      }

   }

   public void error(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.ERROR, marker, msg, t)) {
         this.log(marker, FQCN, Level.ERROR, msg, t);
      }

   }

   public void error(Marker marker, Object message) {
      if(this.isEnabled(Level.ERROR, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.ERROR, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void error(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.ERROR, marker, message, t)) {
         this.log(marker, FQCN, Level.ERROR, this.messageFactory.newMessage(message), t);
      }

   }

   public void error(Marker marker, String message) {
      if(this.isEnabled(Level.ERROR, marker, message)) {
         this.log(marker, FQCN, Level.ERROR, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void error(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.ERROR, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.ERROR, msg, msg.getThrowable());
      }

   }

   public void error(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.ERROR, marker, message, t)) {
         this.log(marker, FQCN, Level.ERROR, this.messageFactory.newMessage(message), t);
      }

   }

   public void error(Message msg) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.ERROR, msg, (Throwable)null);
      }

   }

   public void error(Message msg, Throwable t) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.ERROR, msg, t);
      }

   }

   public void error(Object message) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.ERROR, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void error(Object message, Throwable t) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.ERROR, this.messageFactory.newMessage(message), t);
      }

   }

   public void error(String message) {
      if(this.isEnabled(Level.ERROR, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.ERROR, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void error(String message, Object... params) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.ERROR, msg, msg.getThrowable());
      }

   }

   public void error(String message, Throwable t) {
      if(this.isEnabled(Level.ERROR, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.ERROR, this.messageFactory.newMessage(message), t);
      }

   }

   public void exit() {
      this.exit(FQCN, (Object)null);
   }

   public Object exit(Object result) {
      return this.exit(FQCN, result);
   }

   protected Object exit(String fqcn, Object result) {
      if(this.isEnabled(Level.TRACE, EXIT_MARKER, (Object)((Object)null), (Throwable)null)) {
         this.log(EXIT_MARKER, fqcn, Level.TRACE, this.toExitMsg(result), (Throwable)null);
      }

      return result;
   }

   public void fatal(Marker marker, Message msg) {
      if(this.isEnabled(Level.FATAL, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.FATAL, msg, (Throwable)null);
      }

   }

   public void fatal(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.FATAL, marker, msg, t)) {
         this.log(marker, FQCN, Level.FATAL, msg, t);
      }

   }

   public void fatal(Marker marker, Object message) {
      if(this.isEnabled(Level.FATAL, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.FATAL, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void fatal(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.FATAL, marker, message, t)) {
         this.log(marker, FQCN, Level.FATAL, this.messageFactory.newMessage(message), t);
      }

   }

   public void fatal(Marker marker, String message) {
      if(this.isEnabled(Level.FATAL, marker, message)) {
         this.log(marker, FQCN, Level.FATAL, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void fatal(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.FATAL, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.FATAL, msg, msg.getThrowable());
      }

   }

   public void fatal(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.FATAL, marker, message, t)) {
         this.log(marker, FQCN, Level.FATAL, this.messageFactory.newMessage(message), t);
      }

   }

   public void fatal(Message msg) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.FATAL, msg, (Throwable)null);
      }

   }

   public void fatal(Message msg, Throwable t) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.FATAL, msg, t);
      }

   }

   public void fatal(Object message) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.FATAL, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void fatal(Object message, Throwable t) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.FATAL, this.messageFactory.newMessage(message), t);
      }

   }

   public void fatal(String message) {
      if(this.isEnabled(Level.FATAL, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.FATAL, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void fatal(String message, Object... params) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.FATAL, msg, msg.getThrowable());
      }

   }

   public void fatal(String message, Throwable t) {
      if(this.isEnabled(Level.FATAL, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.FATAL, this.messageFactory.newMessage(message), t);
      }

   }

   public MessageFactory getMessageFactory() {
      return this.messageFactory;
   }

   public String getName() {
      return this.name;
   }

   public void info(Marker marker, Message msg) {
      if(this.isEnabled(Level.INFO, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.INFO, msg, (Throwable)null);
      }

   }

   public void info(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.INFO, marker, msg, t)) {
         this.log(marker, FQCN, Level.INFO, msg, t);
      }

   }

   public void info(Marker marker, Object message) {
      if(this.isEnabled(Level.INFO, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.INFO, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void info(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.INFO, marker, message, t)) {
         this.log(marker, FQCN, Level.INFO, this.messageFactory.newMessage(message), t);
      }

   }

   public void info(Marker marker, String message) {
      if(this.isEnabled(Level.INFO, marker, message)) {
         this.log(marker, FQCN, Level.INFO, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void info(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.INFO, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.INFO, msg, msg.getThrowable());
      }

   }

   public void info(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.INFO, marker, message, t)) {
         this.log(marker, FQCN, Level.INFO, this.messageFactory.newMessage(message), t);
      }

   }

   public void info(Message msg) {
      if(this.isEnabled(Level.INFO, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.INFO, msg, (Throwable)null);
      }

   }

   public void info(Message msg, Throwable t) {
      if(this.isEnabled(Level.INFO, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.INFO, msg, t);
      }

   }

   public void info(Object message) {
      if(this.isEnabled(Level.INFO, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.INFO, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void info(Object message, Throwable t) {
      if(this.isEnabled(Level.INFO, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.INFO, this.messageFactory.newMessage(message), t);
      }

   }

   public void info(String message) {
      if(this.isEnabled(Level.INFO, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.INFO, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void info(String message, Object... params) {
      if(this.isEnabled(Level.INFO, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.INFO, msg, msg.getThrowable());
      }

   }

   public void info(String message, Throwable t) {
      if(this.isEnabled(Level.INFO, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.INFO, this.messageFactory.newMessage(message), t);
      }

   }

   public boolean isDebugEnabled() {
      return this.isEnabled(Level.DEBUG, (Marker)null, (String)null);
   }

   public boolean isDebugEnabled(Marker marker) {
      return this.isEnabled(Level.DEBUG, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isEnabled(Level level) {
      return this.isEnabled(level, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   protected abstract boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4);

   protected abstract boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4);

   protected abstract boolean isEnabled(Level var1, Marker var2, String var3);

   protected abstract boolean isEnabled(Level var1, Marker var2, String var3, Object... var4);

   protected abstract boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4);

   public boolean isErrorEnabled() {
      return this.isEnabled(Level.ERROR, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   public boolean isErrorEnabled(Marker marker) {
      return this.isEnabled(Level.ERROR, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isFatalEnabled() {
      return this.isEnabled(Level.FATAL, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   public boolean isFatalEnabled(Marker marker) {
      return this.isEnabled(Level.FATAL, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isInfoEnabled() {
      return this.isEnabled(Level.INFO, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   public boolean isInfoEnabled(Marker marker) {
      return this.isEnabled(Level.INFO, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isTraceEnabled() {
      return this.isEnabled(Level.TRACE, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   public boolean isTraceEnabled(Marker marker) {
      return this.isEnabled(Level.TRACE, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isWarnEnabled() {
      return this.isEnabled(Level.WARN, (Marker)null, (Object)((Object)null), (Throwable)null);
   }

   public boolean isWarnEnabled(Marker marker) {
      return this.isEnabled(Level.WARN, marker, (Object)((Object)null), (Throwable)null);
   }

   public boolean isEnabled(Level level, Marker marker) {
      return this.isEnabled(level, marker, (Object)((Object)null), (Throwable)null);
   }

   public void log(Level level, Marker marker, Message msg) {
      if(this.isEnabled(level, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, level, msg, (Throwable)null);
      }

   }

   public void log(Level level, Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(level, marker, msg, t)) {
         this.log(marker, FQCN, level, msg, t);
      }

   }

   public void log(Level level, Marker marker, Object message) {
      if(this.isEnabled(level, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, level, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void log(Level level, Marker marker, Object message, Throwable t) {
      if(this.isEnabled(level, marker, message, t)) {
         this.log(marker, FQCN, level, this.messageFactory.newMessage(message), t);
      }

   }

   public void log(Level level, Marker marker, String message) {
      if(this.isEnabled(level, marker, message)) {
         this.log(marker, FQCN, level, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void log(Level level, Marker marker, String message, Object... params) {
      if(this.isEnabled(level, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, level, msg, msg.getThrowable());
      }

   }

   public void log(Level level, Marker marker, String message, Throwable t) {
      if(this.isEnabled(level, marker, message, t)) {
         this.log(marker, FQCN, level, this.messageFactory.newMessage(message), t);
      }

   }

   public void log(Level level, Message msg) {
      if(this.isEnabled(level, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, level, msg, (Throwable)null);
      }

   }

   public void log(Level level, Message msg, Throwable t) {
      if(this.isEnabled(level, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, level, msg, t);
      }

   }

   public void log(Level level, Object message) {
      if(this.isEnabled(level, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, level, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void log(Level level, Object message, Throwable t) {
      if(this.isEnabled(level, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, level, this.messageFactory.newMessage(message), t);
      }

   }

   public void log(Level level, String message) {
      if(this.isEnabled(level, (Marker)null, message)) {
         this.log((Marker)null, FQCN, level, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void log(Level level, String message, Object... params) {
      if(this.isEnabled(level, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, level, msg, msg.getThrowable());
      }

   }

   public void log(Level level, String message, Throwable t) {
      if(this.isEnabled(level, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, level, this.messageFactory.newMessage(message), t);
      }

   }

   public void printf(Level level, String format, Object... params) {
      if(this.isEnabled(level, (Marker)null, (String)format, (Object[])params)) {
         Message msg = new StringFormattedMessage(format, params);
         this.log((Marker)null, FQCN, level, msg, msg.getThrowable());
      }

   }

   public void printf(Level level, Marker marker, String format, Object... params) {
      if(this.isEnabled(level, marker, format, params)) {
         Message msg = new StringFormattedMessage(format, params);
         this.log(marker, FQCN, level, msg, msg.getThrowable());
      }

   }

   public abstract void log(Marker var1, String var2, Level var3, Message var4, Throwable var5);

   public Throwable throwing(Level level, Throwable t) {
      return this.throwing(FQCN, level, t);
   }

   public Throwable throwing(Throwable t) {
      return this.throwing(FQCN, Level.ERROR, t);
   }

   protected Throwable throwing(String fqcn, Level level, Throwable t) {
      if(this.isEnabled(level, THROWING_MARKER, (Object)((Object)null), (Throwable)null)) {
         this.log(THROWING_MARKER, fqcn, level, this.messageFactory.newMessage("throwing"), t);
      }

      return t;
   }

   private Message toExitMsg(Object result) {
      return result == null?this.messageFactory.newMessage("exit"):this.messageFactory.newMessage("exit with(" + result + ")");
   }

   public String toString() {
      return this.name;
   }

   public void trace(Marker marker, Message msg) {
      if(this.isEnabled(Level.TRACE, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.TRACE, msg, (Throwable)null);
      }

   }

   public void trace(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.TRACE, marker, msg, t)) {
         this.log(marker, FQCN, Level.TRACE, msg, t);
      }

   }

   public void trace(Marker marker, Object message) {
      if(this.isEnabled(Level.TRACE, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.TRACE, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void trace(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.TRACE, marker, message, t)) {
         this.log(marker, FQCN, Level.TRACE, this.messageFactory.newMessage(message), t);
      }

   }

   public void trace(Marker marker, String message) {
      if(this.isEnabled(Level.TRACE, marker, message)) {
         this.log(marker, FQCN, Level.TRACE, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void trace(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.TRACE, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.TRACE, msg, msg.getThrowable());
      }

   }

   public void trace(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.TRACE, marker, message, t)) {
         this.log(marker, FQCN, Level.TRACE, this.messageFactory.newMessage(message), t);
      }

   }

   public void trace(Message msg) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.TRACE, msg, (Throwable)null);
      }

   }

   public void trace(Message msg, Throwable t) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.TRACE, msg, t);
      }

   }

   public void trace(Object message) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.TRACE, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void trace(Object message, Throwable t) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.TRACE, this.messageFactory.newMessage(message), t);
      }

   }

   public void trace(String message) {
      if(this.isEnabled(Level.TRACE, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.TRACE, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void trace(String message, Object... params) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.TRACE, msg, msg.getThrowable());
      }

   }

   public void trace(String message, Throwable t) {
      if(this.isEnabled(Level.TRACE, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.TRACE, this.messageFactory.newMessage(message), t);
      }

   }

   public void warn(Marker marker, Message msg) {
      if(this.isEnabled(Level.WARN, marker, (Message)msg, (Throwable)null)) {
         this.log(marker, FQCN, Level.WARN, msg, (Throwable)null);
      }

   }

   public void warn(Marker marker, Message msg, Throwable t) {
      if(this.isEnabled(Level.WARN, marker, msg, t)) {
         this.log(marker, FQCN, Level.WARN, msg, t);
      }

   }

   public void warn(Marker marker, Object message) {
      if(this.isEnabled(Level.WARN, marker, (Object)message, (Throwable)null)) {
         this.log(marker, FQCN, Level.WARN, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void warn(Marker marker, Object message, Throwable t) {
      if(this.isEnabled(Level.WARN, marker, message, t)) {
         this.log(marker, FQCN, Level.WARN, this.messageFactory.newMessage(message), t);
      }

   }

   public void warn(Marker marker, String message) {
      if(this.isEnabled(Level.WARN, marker, message)) {
         this.log(marker, FQCN, Level.WARN, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void warn(Marker marker, String message, Object... params) {
      if(this.isEnabled(Level.WARN, marker, message, params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log(marker, FQCN, Level.WARN, msg, msg.getThrowable());
      }

   }

   public void warn(Marker marker, String message, Throwable t) {
      if(this.isEnabled(Level.WARN, marker, message, t)) {
         this.log(marker, FQCN, Level.WARN, this.messageFactory.newMessage(message), t);
      }

   }

   public void warn(Message msg) {
      if(this.isEnabled(Level.WARN, (Marker)null, (Message)msg, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.WARN, msg, (Throwable)null);
      }

   }

   public void warn(Message msg, Throwable t) {
      if(this.isEnabled(Level.WARN, (Marker)null, (Message)msg, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.WARN, msg, t);
      }

   }

   public void warn(Object message) {
      if(this.isEnabled(Level.WARN, (Marker)null, (Object)message, (Throwable)null)) {
         this.log((Marker)null, FQCN, Level.WARN, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void warn(Object message, Throwable t) {
      if(this.isEnabled(Level.WARN, (Marker)null, (Object)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.WARN, this.messageFactory.newMessage(message), t);
      }

   }

   public void warn(String message) {
      if(this.isEnabled(Level.WARN, (Marker)null, message)) {
         this.log((Marker)null, FQCN, Level.WARN, this.messageFactory.newMessage(message), (Throwable)null);
      }

   }

   public void warn(String message, Object... params) {
      if(this.isEnabled(Level.WARN, (Marker)null, (String)message, (Object[])params)) {
         Message msg = this.messageFactory.newMessage(message, params);
         this.log((Marker)null, FQCN, Level.WARN, msg, msg.getThrowable());
      }

   }

   public void warn(String message, Throwable t) {
      if(this.isEnabled(Level.WARN, (Marker)null, (String)message, (Throwable)t)) {
         this.log((Marker)null, FQCN, Level.WARN, this.messageFactory.newMessage(message), t);
      }

   }
}
