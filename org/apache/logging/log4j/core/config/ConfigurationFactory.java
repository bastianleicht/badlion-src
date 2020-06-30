package org.apache.logging.log4j.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.helpers.FileUtils;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public abstract class ConfigurationFactory {
   public static final String CONFIGURATION_FACTORY_PROPERTY = "log4j.configurationFactory";
   public static final String CONFIGURATION_FILE_PROPERTY = "log4j.configurationFile";
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected static final String TEST_PREFIX = "log4j2-test";
   protected static final String DEFAULT_PREFIX = "log4j2";
   private static final String CLASS_LOADER_SCHEME = "classloader";
   private static final int CLASS_LOADER_SCHEME_LENGTH = "classloader".length() + 1;
   private static final String CLASS_PATH_SCHEME = "classpath";
   private static final int CLASS_PATH_SCHEME_LENGTH = "classpath".length() + 1;
   private static volatile List factories = null;
   private static ConfigurationFactory configFactory = new ConfigurationFactory.Factory();
   protected final StrSubstitutor substitutor = new StrSubstitutor(new Interpolator());

   public static ConfigurationFactory getInstance() {
      if(factories == null) {
         synchronized("log4j2-test") {
            if(factories == null) {
               List<ConfigurationFactory> list = new ArrayList();
               String factoryClass = PropertiesUtil.getProperties().getStringProperty("log4j.configurationFactory");
               if(factoryClass != null) {
                  addFactory(list, factoryClass);
               }

               PluginManager manager = new PluginManager("ConfigurationFactory");
               manager.collectPlugins();
               Map<String, PluginType<?>> plugins = manager.getPlugins();
               Set<ConfigurationFactory.WeightedFactory> ordered = new TreeSet();

               for(PluginType<?> type : plugins.values()) {
                  try {
                     Class<ConfigurationFactory> clazz = type.getPluginClass();
                     Order order = (Order)clazz.getAnnotation(Order.class);
                     if(order != null) {
                        int weight = order.value();
                        ordered.add(new ConfigurationFactory.WeightedFactory(weight, clazz));
                     }
                  } catch (Exception var12) {
                     LOGGER.warn("Unable to add class " + type.getPluginClass());
                  }
               }

               for(ConfigurationFactory.WeightedFactory wf : ordered) {
                  addFactory(list, wf.factoryClass);
               }

               factories = Collections.unmodifiableList(list);
            }
         }
      }

      return configFactory;
   }

   private static void addFactory(List list, String factoryClass) {
      try {
         addFactory(list, Class.forName(factoryClass));
      } catch (ClassNotFoundException var3) {
         LOGGER.error((String)("Unable to load class " + factoryClass), (Throwable)var3);
      } catch (Exception var4) {
         LOGGER.error((String)("Unable to load class " + factoryClass), (Throwable)var4);
      }

   }

   private static void addFactory(List list, Class factoryClass) {
      try {
         list.add(factoryClass.newInstance());
      } catch (Exception var3) {
         LOGGER.error((String)("Unable to create instance of " + factoryClass.getName()), (Throwable)var3);
      }

   }

   public static void setConfigurationFactory(ConfigurationFactory factory) {
      configFactory = factory;
   }

   public static void resetConfigurationFactory() {
      configFactory = new ConfigurationFactory.Factory();
   }

   public static void removeConfigurationFactory(ConfigurationFactory factory) {
      if(configFactory == factory) {
         configFactory = new ConfigurationFactory.Factory();
      }

   }

   protected abstract String[] getSupportedTypes();

   protected boolean isActive() {
      return true;
   }

   public abstract Configuration getConfiguration(ConfigurationFactory.ConfigurationSource var1);

   public Configuration getConfiguration(String name, URI configLocation) {
      if(!this.isActive()) {
         return null;
      } else {
         if(configLocation != null) {
            ConfigurationFactory.ConfigurationSource source = this.getInputFromURI(configLocation);
            if(source != null) {
               return this.getConfiguration(source);
            }
         }

         return null;
      }
   }

   protected ConfigurationFactory.ConfigurationSource getInputFromURI(URI configLocation) {
      File configFile = FileUtils.fileFromURI(configLocation);
      if(configFile != null && configFile.exists() && configFile.canRead()) {
         try {
            return new ConfigurationFactory.ConfigurationSource(new FileInputStream(configFile), configFile);
         } catch (FileNotFoundException var12) {
            LOGGER.error((String)("Cannot locate file " + configLocation.getPath()), (Throwable)var12);
         }
      }

      String scheme = configLocation.getScheme();
      boolean isClassLoaderScheme = scheme != null && scheme.equals("classloader");
      boolean isClassPathScheme = scheme != null && !isClassLoaderScheme && scheme.equals("classpath");
      if(scheme == null || isClassLoaderScheme || isClassPathScheme) {
         ClassLoader loader = this.getClass().getClassLoader();
         String path;
         if(isClassLoaderScheme) {
            path = configLocation.toString().substring(CLASS_LOADER_SCHEME_LENGTH);
         } else if(isClassPathScheme) {
            path = configLocation.toString().substring(CLASS_PATH_SCHEME_LENGTH);
         } else {
            path = configLocation.getPath();
         }

         ConfigurationFactory.ConfigurationSource source = this.getInputFromResource(path, loader);
         if(source != null) {
            return source;
         }
      }

      try {
         return new ConfigurationFactory.ConfigurationSource(configLocation.toURL().openStream(), configLocation.getPath());
      } catch (MalformedURLException var9) {
         LOGGER.error((String)("Invalid URL " + configLocation.toString()), (Throwable)var9);
      } catch (IOException var10) {
         LOGGER.error((String)("Unable to access " + configLocation.toString()), (Throwable)var10);
      } catch (Exception var11) {
         LOGGER.error((String)("Unable to access " + configLocation.toString()), (Throwable)var11);
      }

      return null;
   }

   protected ConfigurationFactory.ConfigurationSource getInputFromString(String config, ClassLoader loader) {
      try {
         URL url = new URL(config);
         return new ConfigurationFactory.ConfigurationSource(url.openStream(), FileUtils.fileFromURI(url.toURI()));
      } catch (Exception var7) {
         ConfigurationFactory.ConfigurationSource source = this.getInputFromResource(config, loader);
         if(source == null) {
            try {
               File file = new File(config);
               return new ConfigurationFactory.ConfigurationSource(new FileInputStream(file), file);
            } catch (FileNotFoundException var6) {
               ;
            }
         }

         return source;
      }
   }

   protected ConfigurationFactory.ConfigurationSource getInputFromResource(String resource, ClassLoader loader) {
      URL url = Loader.getResource(resource, loader);
      if(url == null) {
         return null;
      } else {
         InputStream is = null;

         try {
            is = url.openStream();
         } catch (IOException var6) {
            return null;
         }

         if(is == null) {
            return null;
         } else {
            if(FileUtils.isFile(url)) {
               try {
                  return new ConfigurationFactory.ConfigurationSource(is, FileUtils.fileFromURI(url.toURI()));
               } catch (URISyntaxException var7) {
                  ;
               }
            }

            return new ConfigurationFactory.ConfigurationSource(is, resource);
         }
      }
   }

   public static class ConfigurationSource {
      private File file;
      private String location;
      private InputStream stream;

      public ConfigurationSource() {
      }

      public ConfigurationSource(InputStream stream) {
         this.stream = stream;
         this.file = null;
         this.location = null;
      }

      public ConfigurationSource(InputStream stream, File file) {
         this.stream = stream;
         this.file = file;
         this.location = file.getAbsolutePath();
      }

      public ConfigurationSource(InputStream stream, String location) {
         this.stream = stream;
         this.location = location;
         this.file = null;
      }

      public File getFile() {
         return this.file;
      }

      public void setFile(File file) {
         this.file = file;
      }

      public String getLocation() {
         return this.location;
      }

      public void setLocation(String location) {
         this.location = location;
      }

      public InputStream getInputStream() {
         return this.stream;
      }

      public void setInputStream(InputStream stream) {
         this.stream = stream;
      }
   }

   private static class Factory extends ConfigurationFactory {
      private Factory() {
      }

      public Configuration getConfiguration(String name, URI configLocation) {
         if(configLocation == null) {
            String config = this.substitutor.replace(PropertiesUtil.getProperties().getStringProperty("log4j.configurationFile"));
            if(config != null) {
               ConfigurationFactory.ConfigurationSource source = null;

               try {
                  source = this.getInputFromURI(new URI(config));
               } catch (Exception var13) {
                  ;
               }

               if(source == null) {
                  ClassLoader loader = this.getClass().getClassLoader();
                  source = this.getInputFromString(config, loader);
               }

               if(source != null) {
                  for(ConfigurationFactory factory : ConfigurationFactory.factories) {
                     String[] types = factory.getSupportedTypes();
                     if(types != null) {
                        for(String type : types) {
                           if(type.equals("*") || config.endsWith(type)) {
                              Configuration c = factory.getConfiguration(source);
                              if(c != null) {
                                 return c;
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            for(ConfigurationFactory factory : ConfigurationFactory.factories) {
               String[] types = factory.getSupportedTypes();
               if(types != null) {
                  for(String type : types) {
                     if(type.equals("*") || configLocation.toString().endsWith(type)) {
                        Configuration config = factory.getConfiguration(name, configLocation);
                        if(config != null) {
                           return config;
                        }
                     }
                  }
               }
            }
         }

         Configuration config = this.getConfiguration(true, name);
         if(config == null) {
            config = this.getConfiguration(true, (String)null);
            if(config == null) {
               config = this.getConfiguration(false, name);
               if(config == null) {
                  config = this.getConfiguration(false, (String)null);
               }
            }
         }

         return (Configuration)(config != null?config:new DefaultConfiguration());
      }

      private Configuration getConfiguration(boolean isTest, String name) {
         boolean named = name != null && name.length() > 0;
         ClassLoader loader = this.getClass().getClassLoader();

         for(ConfigurationFactory factory : ConfigurationFactory.factories) {
            String prefix = isTest?"log4j2-test":"log4j2";
            String[] types = factory.getSupportedTypes();
            if(types != null) {
               for(String suffix : types) {
                  if(!suffix.equals("*")) {
                     String configName = named?prefix + name + suffix:prefix + suffix;
                     ConfigurationFactory.ConfigurationSource source = this.getInputFromResource(configName, loader);
                     if(source != null) {
                        return factory.getConfiguration(source);
                     }
                  }
               }
            }
         }

         return null;
      }

      public String[] getSupportedTypes() {
         return null;
      }

      public Configuration getConfiguration(ConfigurationFactory.ConfigurationSource source) {
         if(source != null) {
            String config = source.getLocation();

            for(ConfigurationFactory factory : ConfigurationFactory.factories) {
               String[] types = factory.getSupportedTypes();
               if(types != null) {
                  for(String type : types) {
                     if(type.equals("*") || config != null && config.endsWith(type)) {
                        Configuration c = factory.getConfiguration(source);
                        if(c != null) {
                           return c;
                        }

                        LOGGER.error("Cannot determine the ConfigurationFactory to use for {}", new Object[]{config});
                        return null;
                     }
                  }
               }
            }
         }

         LOGGER.error("Cannot process configuration, input source is null");
         return null;
      }
   }

   private static class WeightedFactory implements Comparable {
      private final int weight;
      private final Class factoryClass;

      public WeightedFactory(int weight, Class clazz) {
         this.weight = weight;
         this.factoryClass = clazz;
      }

      public int compareTo(ConfigurationFactory.WeightedFactory wf) {
         int w = wf.weight;
         return this.weight == w?0:(this.weight > w?-1:1);
      }
   }
}
