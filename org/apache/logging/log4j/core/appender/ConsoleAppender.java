package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(
   name = "Console",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class ConsoleAppender extends AbstractOutputStreamAppender {
   private static final String JANSI_CLASS = "org.fusesource.jansi.WindowsAnsiOutputStream";
   private static ConsoleAppender.ConsoleManagerFactory factory = new ConsoleAppender.ConsoleManagerFactory();

   private ConsoleAppender(String name, Layout layout, Filter filter, OutputStreamManager manager, boolean ignoreExceptions) {
      super(name, layout, filter, ignoreExceptions, true, manager);
   }

   @PluginFactory
   public static ConsoleAppender createAppender(@PluginElement("Layout") Layout layout, @PluginElement("Filters") Filter filter, @PluginAttribute("target") String t, @PluginAttribute("name") String name, @PluginAttribute("follow") String follow, @PluginAttribute("ignoreExceptions") String ignore) {
      if(name == null) {
         LOGGER.error("No name provided for ConsoleAppender");
         return null;
      } else {
         if(layout == null) {
            layout = PatternLayout.createLayout((String)null, (Configuration)null, (RegexReplacement)null, (String)null, (String)null);
         }

         boolean isFollow = Boolean.parseBoolean(follow);
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
         ConsoleAppender.Target target = t == null?ConsoleAppender.Target.SYSTEM_OUT:ConsoleAppender.Target.valueOf(t);
         return new ConsoleAppender(name, (Layout)layout, filter, getManager(isFollow, target, (Layout)layout), ignoreExceptions);
      }
   }

   private static OutputStreamManager getManager(boolean follow, ConsoleAppender.Target target, Layout layout) {
      String type = target.name();
      OutputStream os = getOutputStream(follow, target);
      return OutputStreamManager.getManager(target.name() + "." + follow, new ConsoleAppender.FactoryData(os, type, layout), factory);
   }

   private static OutputStream getOutputStream(boolean follow, ConsoleAppender.Target target) {
      String enc = Charset.defaultCharset().name();
      PrintStream printStream = null;

      try {
         printStream = target == ConsoleAppender.Target.SYSTEM_OUT?(follow?new PrintStream(new ConsoleAppender.SystemOutStream(), true, enc):System.out):(follow?new PrintStream(new ConsoleAppender.SystemErrStream(), true, enc):System.err);
      } catch (UnsupportedEncodingException var11) {
         throw new IllegalStateException("Unsupported default encoding " + enc, var11);
      }

      PropertiesUtil propsUtil = PropertiesUtil.getProperties();
      if(propsUtil.getStringProperty("os.name").startsWith("Windows") && !propsUtil.getBooleanProperty("log4j.skipJansi")) {
         try {
            ClassLoader loader = Loader.getClassLoader();
            Class<?> clazz = loader.loadClass("org.fusesource.jansi.WindowsAnsiOutputStream");
            Constructor<?> constructor = clazz.getConstructor(new Class[]{OutputStream.class});
            return (OutputStream)constructor.newInstance(new Object[]{printStream});
         } catch (ClassNotFoundException var8) {
            LOGGER.debug("Jansi is not installed, cannot find {}", new Object[]{"org.fusesource.jansi.WindowsAnsiOutputStream"});
         } catch (NoSuchMethodException var9) {
            LOGGER.warn("{} is missing the proper constructor", new Object[]{"org.fusesource.jansi.WindowsAnsiOutputStream"});
         } catch (Exception var10) {
            LOGGER.warn("Unable to instantiate {}", new Object[]{"org.fusesource.jansi.WindowsAnsiOutputStream"});
         }

         return printStream;
      } else {
         return printStream;
      }
   }

   private static class ConsoleManagerFactory implements ManagerFactory {
      private ConsoleManagerFactory() {
      }

      public OutputStreamManager createManager(String name, ConsoleAppender.FactoryData data) {
         return new OutputStreamManager(data.os, data.type, data.layout);
      }
   }

   private static class FactoryData {
      private final OutputStream os;
      private final String type;
      private final Layout layout;

      public FactoryData(OutputStream os, String type, Layout layout) {
         this.os = os;
         this.type = type;
         this.layout = layout;
      }
   }

   private static class SystemErrStream extends OutputStream {
      public void close() {
      }

      public void flush() {
         System.err.flush();
      }

      public void write(byte[] b) throws IOException {
         System.err.write(b);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         System.err.write(b, off, len);
      }

      public void write(int b) {
         System.err.write(b);
      }
   }

   private static class SystemOutStream extends OutputStream {
      public void close() {
      }

      public void flush() {
         System.out.flush();
      }

      public void write(byte[] b) throws IOException {
         System.out.write(b);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         System.out.write(b, off, len);
      }

      public void write(int b) throws IOException {
         System.out.write(b);
      }
   }

   public static enum Target {
      SYSTEM_OUT,
      SYSTEM_ERR;
   }
}
