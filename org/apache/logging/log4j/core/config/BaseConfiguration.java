package org.apache.logging.log4j.core.config;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.ConfigurationMonitor;
import org.apache.logging.log4j.core.config.DefaultAdvertiser;
import org.apache.logging.log4j.core.config.DefaultConfigurationMonitor;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Loggers;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginNode;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.helpers.NameUtil;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.MapLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class BaseConfiguration extends AbstractFilterable implements Configuration {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected Node rootNode;
   protected final List listeners = new CopyOnWriteArrayList();
   protected ConfigurationMonitor monitor = new DefaultConfigurationMonitor();
   private Advertiser advertiser = new DefaultAdvertiser();
   protected Map advertisedConfiguration;
   private Node advertiserNode = null;
   private Object advertisement;
   protected boolean isShutdownHookEnabled = true;
   private String name;
   private ConcurrentMap appenders = new ConcurrentHashMap();
   private ConcurrentMap loggers = new ConcurrentHashMap();
   private final StrLookup tempLookup = new Interpolator();
   private final StrSubstitutor subst;
   private LoggerConfig root;
   private final boolean started;
   private final ConcurrentMap componentMap;
   protected PluginManager pluginManager;

   protected BaseConfiguration() {
      this.subst = new StrSubstitutor(this.tempLookup);
      this.root = new LoggerConfig();
      this.started = false;
      this.componentMap = new ConcurrentHashMap();
      this.pluginManager = new PluginManager("Core");
      this.rootNode = new Node();
   }

   public Map getProperties() {
      return (Map)this.componentMap.get("ContextProperties");
   }

   public void start() {
      this.pluginManager.collectPlugins();
      this.setup();
      this.setupAdvertisement();
      this.doConfigure();

      for(LoggerConfig logger : this.loggers.values()) {
         logger.startFilter();
      }

      for(Appender appender : this.appenders.values()) {
         appender.start();
      }

      this.root.startFilter();
      this.startFilter();
   }

   public void stop() {
      Appender[] array = (Appender[])this.appenders.values().toArray(new Appender[this.appenders.size()]);

      for(int i = array.length - 1; i >= 0; --i) {
         array[i].stop();
      }

      for(LoggerConfig logger : this.loggers.values()) {
         logger.clearAppenders();
         logger.stopFilter();
      }

      this.root.stopFilter();
      this.stopFilter();
      if(this.advertiser != null && this.advertisement != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

   }

   public boolean isShutdownHookEnabled() {
      return this.isShutdownHookEnabled;
   }

   protected void setup() {
   }

   protected Level getDefaultStatus() {
      String statusLevel = PropertiesUtil.getProperties().getStringProperty("Log4jDefaultStatusLevel", Level.ERROR.name());

      try {
         return Level.toLevel(statusLevel);
      } catch (Exception var3) {
         return Level.ERROR;
      }
   }

   protected void createAdvertiser(String advertiserString, ConfigurationFactory.ConfigurationSource configSource, byte[] buffer, String contentType) {
      if(advertiserString != null) {
         Node node = new Node((Node)null, advertiserString, (PluginType)null);
         Map<String, String> attributes = node.getAttributes();
         attributes.put("content", new String(buffer));
         attributes.put("contentType", contentType);
         attributes.put("name", "configuration");
         if(configSource.getLocation() != null) {
            attributes.put("location", configSource.getLocation());
         }

         this.advertiserNode = node;
      }

   }

   private void setupAdvertisement() {
      if(this.advertiserNode != null) {
         String name = this.advertiserNode.getName();
         PluginType<Advertiser> type = this.pluginManager.getPluginType(name);
         if(type != null) {
            Class<Advertiser> clazz = type.getPluginClass();

            try {
               this.advertiser = (Advertiser)clazz.newInstance();
               this.advertisement = this.advertiser.advertise(this.advertiserNode.getAttributes());
            } catch (InstantiationException var5) {
               System.err.println("InstantiationException attempting to instantiate advertiser: " + name);
            } catch (IllegalAccessException var6) {
               System.err.println("IllegalAccessException attempting to instantiate advertiser: " + name);
            }
         }
      }

   }

   public Object getComponent(String name) {
      return this.componentMap.get(name);
   }

   public void addComponent(String name, Object obj) {
      this.componentMap.putIfAbsent(name, obj);
   }

   protected void doConfigure() {
      boolean setRoot = false;
      boolean setLoggers = false;

      for(Node child : this.rootNode.getChildren()) {
         this.createConfiguration(child, (LogEvent)null);
         if(child.getObject() != null) {
            if(child.getName().equalsIgnoreCase("Properties")) {
               if(this.tempLookup == this.subst.getVariableResolver()) {
                  this.subst.setVariableResolver((StrLookup)child.getObject());
               } else {
                  LOGGER.error("Properties declaration must be the first element in the configuration");
               }
            } else {
               if(this.tempLookup == this.subst.getVariableResolver()) {
                  Map<String, String> map = (Map)this.componentMap.get("ContextProperties");
                  StrLookup lookup = map == null?null:new MapLookup(map);
                  this.subst.setVariableResolver(new Interpolator(lookup));
               }

               if(child.getName().equalsIgnoreCase("Appenders")) {
                  this.appenders = (ConcurrentMap)child.getObject();
               } else if(child.getObject() instanceof Filter) {
                  this.addFilter((Filter)child.getObject());
               } else if(child.getName().equalsIgnoreCase("Loggers")) {
                  Loggers l = (Loggers)child.getObject();
                  this.loggers = l.getMap();
                  setLoggers = true;
                  if(l.getRoot() != null) {
                     this.root = l.getRoot();
                     setRoot = true;
                  }
               } else {
                  LOGGER.error("Unknown object \"" + child.getName() + "\" of type " + child.getObject().getClass().getName() + " is ignored");
               }
            }
         }
      }

      if(!setLoggers) {
         LOGGER.warn("No Loggers were configured, using default. Is the Loggers element missing?");
         this.setToDefault();
      } else {
         if(!setRoot) {
            LOGGER.warn("No Root logger was configured, creating default ERROR-level Root logger with Console appender");
            this.setToDefault();
         }

         for(Entry<String, LoggerConfig> entry : this.loggers.entrySet()) {
            LoggerConfig l = (LoggerConfig)entry.getValue();

            for(AppenderRef ref : l.getAppenderRefs()) {
               Appender app = (Appender)this.appenders.get(ref.getRef());
               if(app != null) {
                  l.addAppender(app, ref.getLevel(), ref.getFilter());
               } else {
                  LOGGER.error("Unable to locate appender " + ref.getRef() + " for logger " + l.getName());
               }
            }
         }

         this.setParents();
      }
   }

   private void setToDefault() {
      this.setName("Default");
      Layout<? extends Serializable> layout = PatternLayout.createLayout("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n", (Configuration)null, (RegexReplacement)null, (String)null, (String)null);
      Appender appender = ConsoleAppender.createAppender(layout, (Filter)null, "SYSTEM_OUT", "Console", "false", "true");
      appender.start();
      this.addAppender(appender);
      LoggerConfig root = this.getRootLogger();
      root.addAppender(appender, (Level)null, (Filter)null);
      String levelName = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.level");
      Level level = levelName != null && Level.valueOf(levelName) != null?Level.valueOf(levelName):Level.ERROR;
      root.setLevel(level);
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void addListener(ConfigurationListener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(ConfigurationListener listener) {
      this.listeners.remove(listener);
   }

   public Appender getAppender(String name) {
      return (Appender)this.appenders.get(name);
   }

   public Map getAppenders() {
      return this.appenders;
   }

   public void addAppender(Appender appender) {
      this.appenders.put(appender.getName(), appender);
   }

   public StrSubstitutor getStrSubstitutor() {
      return this.subst;
   }

   public void setConfigurationMonitor(ConfigurationMonitor monitor) {
      this.monitor = monitor;
   }

   public ConfigurationMonitor getConfigurationMonitor() {
      return this.monitor;
   }

   public void setAdvertiser(Advertiser advertiser) {
      this.advertiser = advertiser;
   }

   public Advertiser getAdvertiser() {
      return this.advertiser;
   }

   public synchronized void addLoggerAppender(org.apache.logging.log4j.core.Logger logger, Appender appender) {
      String name = logger.getName();
      this.appenders.putIfAbsent(appender.getName(), appender);
      LoggerConfig lc = this.getLoggerConfig(name);
      if(lc.getName().equals(name)) {
         lc.addAppender(appender, (Level)null, (Filter)null);
      } else {
         LoggerConfig nlc = new LoggerConfig(name, lc.getLevel(), lc.isAdditive());
         nlc.addAppender(appender, (Level)null, (Filter)null);
         nlc.setParent(lc);
         this.loggers.putIfAbsent(name, nlc);
         this.setParents();
         logger.getContext().updateLoggers();
      }

   }

   public synchronized void addLoggerFilter(org.apache.logging.log4j.core.Logger logger, Filter filter) {
      String name = logger.getName();
      LoggerConfig lc = this.getLoggerConfig(name);
      if(lc.getName().equals(name)) {
         lc.addFilter(filter);
      } else {
         LoggerConfig nlc = new LoggerConfig(name, lc.getLevel(), lc.isAdditive());
         nlc.addFilter(filter);
         nlc.setParent(lc);
         this.loggers.putIfAbsent(name, nlc);
         this.setParents();
         logger.getContext().updateLoggers();
      }

   }

   public synchronized void setLoggerAdditive(org.apache.logging.log4j.core.Logger logger, boolean additive) {
      String name = logger.getName();
      LoggerConfig lc = this.getLoggerConfig(name);
      if(lc.getName().equals(name)) {
         lc.setAdditive(additive);
      } else {
         LoggerConfig nlc = new LoggerConfig(name, lc.getLevel(), additive);
         nlc.setParent(lc);
         this.loggers.putIfAbsent(name, nlc);
         this.setParents();
         logger.getContext().updateLoggers();
      }

   }

   public synchronized void removeAppender(String name) {
      for(LoggerConfig logger : this.loggers.values()) {
         logger.removeAppender(name);
      }

      Appender app = (Appender)this.appenders.remove(name);
      if(app != null) {
         app.stop();
      }

   }

   public LoggerConfig getLoggerConfig(String name) {
      if(this.loggers.containsKey(name)) {
         return (LoggerConfig)this.loggers.get(name);
      } else {
         String substr = name;

         while((substr = NameUtil.getSubName(substr)) != null) {
            if(this.loggers.containsKey(substr)) {
               return (LoggerConfig)this.loggers.get(substr);
            }
         }

         return this.root;
      }
   }

   public LoggerConfig getRootLogger() {
      return this.root;
   }

   public Map getLoggers() {
      return Collections.unmodifiableMap(this.loggers);
   }

   public LoggerConfig getLogger(String name) {
      return (LoggerConfig)this.loggers.get(name);
   }

   public void addLogger(String name, LoggerConfig loggerConfig) {
      this.loggers.put(name, loggerConfig);
      this.setParents();
   }

   public void removeLogger(String name) {
      this.loggers.remove(name);
      this.setParents();
   }

   public void createConfiguration(Node node, LogEvent event) {
      PluginType<?> type = node.getType();
      if(type != null && type.isDeferChildren()) {
         node.setObject(this.createPluginObject(type, node, event));
      } else {
         for(Node child : node.getChildren()) {
            this.createConfiguration(child, event);
         }

         if(type == null) {
            if(node.getParent() != null) {
               LOGGER.error("Unable to locate plugin for " + node.getName());
            }
         } else {
            node.setObject(this.createPluginObject(type, node, event));
         }
      }

   }

   private Object createPluginObject(PluginType type, Node node, LogEvent event) {
      Class<T> clazz = type.getPluginClass();
      if(Map.class.isAssignableFrom(clazz)) {
         try {
            Map<String, Object> map = (Map)clazz.newInstance();

            for(Node child : node.getChildren()) {
               map.put(child.getName(), child.getObject());
            }

            return map;
         } catch (Exception var34) {
            LOGGER.warn("Unable to create Map for " + type.getElementName() + " of class " + clazz);
         }
      }

      if(List.class.isAssignableFrom(clazz)) {
         try {
            List<Object> list = (List)clazz.newInstance();

            for(Node child : node.getChildren()) {
               list.add(child.getObject());
            }

            return list;
         } catch (Exception var33) {
            LOGGER.warn("Unable to create List for " + type.getElementName() + " of class " + clazz);
         }
      }

      Method factoryMethod = null;

      for(Method method : clazz.getMethods()) {
         if(method.isAnnotationPresent(PluginFactory.class)) {
            factoryMethod = method;
            break;
         }
      }

      if(factoryMethod == null) {
         return null;
      } else {
         Annotation[][] parmArray = factoryMethod.getParameterAnnotations();
         Class<?>[] parmClasses = factoryMethod.getParameterTypes();
         if(parmArray.length != parmClasses.length) {
            LOGGER.error("Number of parameter annotations does not equal the number of paramters");
         }

         Object[] parms = new Object[parmClasses.length];
         int index = 0;
         Map<String, String> attrs = node.getAttributes();
         List<Node> children = node.getChildren();
         StringBuilder sb = new StringBuilder();
         List<Node> used = new ArrayList();

         for(Annotation[] parmTypes : parmArray) {
            String[] aliases = null;

            for(Annotation a : parmTypes) {
               if(a instanceof PluginAliases) {
                  aliases = ((PluginAliases)a).value();
               }
            }

            for(Annotation a : parmTypes) {
               if(!(a instanceof PluginAliases)) {
                  if(sb.length() == 0) {
                     sb.append(" with params(");
                  } else {
                     sb.append(", ");
                  }

                  if(a instanceof PluginNode) {
                     parms[index] = node;
                     sb.append("Node=").append(node.getName());
                  } else if(a instanceof PluginConfiguration) {
                     parms[index] = this;
                     if(this.name != null) {
                        sb.append("Configuration(").append(this.name).append(")");
                     } else {
                        sb.append("Configuration");
                     }
                  } else if(a instanceof PluginValue) {
                     String name = ((PluginValue)a).value();
                     String v = node.getValue();
                     if(v == null) {
                        v = this.getAttrValue("value", (String[])null, attrs);
                     }

                     String value = this.subst.replace(event, v);
                     sb.append(name).append("=\"").append(value).append("\"");
                     parms[index] = value;
                  } else if(a instanceof PluginAttribute) {
                     PluginAttribute attr = (PluginAttribute)a;
                     String name = attr.value();
                     String value = this.subst.replace(event, this.getAttrValue(name, aliases, attrs));
                     sb.append(name).append("=\"").append(value).append("\"");
                     parms[index] = value;
                  } else if(a instanceof PluginElement) {
                     PluginElement elem = (PluginElement)a;
                     String name = elem.value();
                     if(parmClasses[index].isArray()) {
                        Class<?> parmClass = parmClasses[index].getComponentType();
                        List<Object> list = new ArrayList();
                        sb.append(name).append("={");
                        boolean first = true;

                        for(Node child : children) {
                           PluginType<?> childType = child.getType();
                           if(elem.value().equalsIgnoreCase(childType.getElementName()) || parmClass.isAssignableFrom(childType.getPluginClass())) {
                              used.add(child);
                              if(!first) {
                                 sb.append(", ");
                              }

                              first = false;
                              Object obj = child.getObject();
                              if(obj == null) {
                                 LOGGER.error("Null object returned for " + child.getName() + " in " + node.getName());
                              } else {
                                 if(obj.getClass().isArray()) {
                                    this.printArray(sb, (Object[])((Object[])obj));
                                    parms[index] = obj;
                                    break;
                                 }

                                 sb.append(child.toString());
                                 list.add(obj);
                              }
                           }
                        }

                        sb.append("}");
                        if(parms[index] != null) {
                           break;
                        }

                        if(list.size() > 0 && !parmClass.isAssignableFrom(list.get(0).getClass())) {
                           LOGGER.error("Attempted to assign List containing class " + list.get(0).getClass().getName() + " to array of type " + parmClass + " for attribute " + name);
                           break;
                        }

                        Object[] array = (Object[])((Object[])Array.newInstance(parmClass, list.size()));
                        int i = 0;

                        for(Object obj : list) {
                           array[i] = obj;
                           ++i;
                        }

                        parms[index] = array;
                     } else {
                        Class<?> parmClass = parmClasses[index];
                        boolean present = false;

                        for(Node child : children) {
                           PluginType<?> childType = child.getType();
                           if(elem.value().equals(childType.getElementName()) || parmClass.isAssignableFrom(childType.getPluginClass())) {
                              sb.append(child.getName()).append("(").append(child.toString()).append(")");
                              present = true;
                              used.add(child);
                              parms[index] = child.getObject();
                              break;
                           }
                        }

                        if(!present) {
                           sb.append("null");
                        }
                     }
                  }
               }
            }

            ++index;
         }

         if(sb.length() > 0) {
            sb.append(")");
         }

         if(attrs.size() > 0) {
            StringBuilder eb = new StringBuilder();

            for(String key : attrs.keySet()) {
               if(eb.length() == 0) {
                  eb.append(node.getName());
                  eb.append(" contains ");
                  if(attrs.size() == 1) {
                     eb.append("an invalid element or attribute ");
                  } else {
                     eb.append("invalid attributes ");
                  }
               } else {
                  eb.append(", ");
               }

               eb.append("\"");
               eb.append(key);
               eb.append("\"");
            }

            LOGGER.error(eb.toString());
         }

         if(!type.isDeferChildren() && used.size() != children.size()) {
            for(Node child : children) {
               if(!used.contains(child)) {
                  String nodeType = node.getType().getElementName();
                  String start = nodeType.equals(node.getName())?node.getName():nodeType + " " + node.getName();
                  LOGGER.error(start + " has no parameter that matches element " + child.getName());
               }
            }
         }

         try {
            int mod = factoryMethod.getModifiers();
            if(!Modifier.isStatic(mod)) {
               LOGGER.error(factoryMethod.getName() + " method is not static on class " + clazz.getName() + " for element " + node.getName());
               return null;
            } else {
               LOGGER.debug("Calling {} on class {} for element {}{}", new Object[]{factoryMethod.getName(), clazz.getName(), node.getName(), sb.toString()});
               return factoryMethod.invoke((Object)null, parms);
            }
         } catch (Exception var32) {
            LOGGER.error((String)("Unable to invoke method " + factoryMethod.getName() + " in class " + clazz.getName() + " for element " + node.getName()), (Throwable)var32);
            return null;
         }
      }
   }

   private void printArray(StringBuilder sb, Object... array) {
      boolean first = true;

      for(Object obj : array) {
         if(!first) {
            sb.append(", ");
         }

         sb.append(obj.toString());
         first = false;
      }

   }

   private String getAttrValue(String name, String[] aliases, Map attrs) {
      for(String key : attrs.keySet()) {
         if(key.equalsIgnoreCase(name)) {
            String attr = (String)attrs.get(key);
            attrs.remove(key);
            return attr;
         }

         if(aliases != null) {
            for(String alias : aliases) {
               if(key.equalsIgnoreCase(alias)) {
                  String attr = (String)attrs.get(key);
                  attrs.remove(key);
                  return attr;
               }
            }
         }
      }

      return null;
   }

   private void setParents() {
      for(Entry<String, LoggerConfig> entry : this.loggers.entrySet()) {
         LoggerConfig logger = (LoggerConfig)entry.getValue();
         String name = (String)entry.getKey();
         if(!name.equals("")) {
            int i = name.lastIndexOf(46);
            if(i > 0) {
               name = name.substring(0, i);
               LoggerConfig parent = this.getLoggerConfig(name);
               if(parent == null) {
                  parent = this.root;
               }

               logger.setParent(parent);
            } else {
               logger.setParent(this.root);
            }
         }
      }

   }
}
