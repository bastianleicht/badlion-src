package org.apache.logging.log4j.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.BaseConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.FileConfigurationMonitor;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.config.plugins.ResolverUtil;
import org.apache.logging.log4j.core.helpers.FileUtils;
import org.apache.logging.log4j.status.StatusConsoleListener;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLConfiguration extends BaseConfiguration implements Reconfigurable {
   private static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
   private static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private static final String LOG4J_XSD = "Log4j-config.xsd";
   private static final int BUF_SIZE = 16384;
   private final List status = new ArrayList();
   private Element rootElement;
   private boolean strict;
   private String schema;
   private final File configFile;

   static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      enableXInclude(factory);
      return factory.newDocumentBuilder();
   }

   private static void enableXInclude(DocumentBuilderFactory factory) {
      try {
         factory.setXIncludeAware(true);
      } catch (UnsupportedOperationException var6) {
         LOGGER.warn((String)("The DocumentBuilderFactory does not support XInclude: " + factory), (Throwable)var6);
      } catch (AbstractMethodError var7) {
         LOGGER.warn("The DocumentBuilderFactory is out of date and does not support XInclude: " + factory);
      }

      try {
         factory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
      } catch (ParserConfigurationException var4) {
         LOGGER.warn((String)("The DocumentBuilderFactory [" + factory + "] does not support the feature [" + "http://apache.org/xml/features/xinclude/fixup-base-uris" + "]"), (Throwable)var4);
      } catch (AbstractMethodError var5) {
         LOGGER.warn("The DocumentBuilderFactory is out of date and does not support setFeature: " + factory);
      }

      try {
         factory.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
      } catch (ParserConfigurationException var2) {
         LOGGER.warn((String)("The DocumentBuilderFactory [" + factory + "] does not support the feature [" + "http://apache.org/xml/features/xinclude/fixup-language" + "]"), (Throwable)var2);
      } catch (AbstractMethodError var3) {
         LOGGER.warn("The DocumentBuilderFactory is out of date and does not support setFeature: " + factory);
      }

   }

   public XMLConfiguration(ConfigurationFactory.ConfigurationSource configSource) {
      this.configFile = configSource.getFile();
      byte[] buffer = null;

      try {
         List<String> messages = new ArrayList();
         InputStream configStream = configSource.getInputStream();
         buffer = this.toByteArray(configStream);
         configStream.close();
         InputSource source = new InputSource(new ByteArrayInputStream(buffer));
         Document document = newDocumentBuilder().parse(source);
         this.rootElement = document.getDocumentElement();
         Map<String, String> attrs = this.processAttributes(this.rootNode, this.rootElement);
         Level status = this.getDefaultStatus();
         boolean verbose = false;
         PrintStream stream = System.out;

         for(Entry<String, String> entry : attrs.entrySet()) {
            if("status".equalsIgnoreCase((String)entry.getKey())) {
               Level stat = Level.toLevel(this.getStrSubstitutor().replace((String)entry.getValue()), (Level)null);
               if(stat != null) {
                  status = stat;
               } else {
                  messages.add("Invalid status specified: " + (String)entry.getValue() + ". Defaulting to " + status);
               }
            } else if("dest".equalsIgnoreCase((String)entry.getKey())) {
               String dest = this.getStrSubstitutor().replace((String)entry.getValue());
               if(dest != null) {
                  if(dest.equalsIgnoreCase("err")) {
                     stream = System.err;
                  } else {
                     try {
                        File destFile = FileUtils.fileFromURI(new URI(dest));
                        String enc = Charset.defaultCharset().name();
                        stream = new PrintStream(new FileOutputStream(destFile), true, enc);
                     } catch (URISyntaxException var22) {
                        System.err.println("Unable to write to " + dest + ". Writing to stdout");
                     }
                  }
               }
            } else if("shutdownHook".equalsIgnoreCase((String)entry.getKey())) {
               String hook = this.getStrSubstitutor().replace((String)entry.getValue());
               this.isShutdownHookEnabled = !hook.equalsIgnoreCase("disable");
            } else if("verbose".equalsIgnoreCase((String)entry.getKey())) {
               verbose = Boolean.parseBoolean(this.getStrSubstitutor().replace((String)entry.getValue()));
            } else if("packages".equalsIgnoreCase(this.getStrSubstitutor().replace((String)entry.getKey()))) {
               String[] packages = ((String)entry.getValue()).split(",");

               for(String p : packages) {
                  PluginManager.addPackage(p);
               }
            } else if("name".equalsIgnoreCase((String)entry.getKey())) {
               this.setName(this.getStrSubstitutor().replace((String)entry.getValue()));
            } else if("strict".equalsIgnoreCase((String)entry.getKey())) {
               this.strict = Boolean.parseBoolean(this.getStrSubstitutor().replace((String)entry.getValue()));
            } else if("schema".equalsIgnoreCase((String)entry.getKey())) {
               this.schema = this.getStrSubstitutor().replace((String)entry.getValue());
            } else if("monitorInterval".equalsIgnoreCase((String)entry.getKey())) {
               int interval = Integer.parseInt(this.getStrSubstitutor().replace((String)entry.getValue()));
               if(interval > 0 && this.configFile != null) {
                  this.monitor = new FileConfigurationMonitor(this, this.configFile, this.listeners, interval);
               }
            } else if("advertiser".equalsIgnoreCase((String)entry.getKey())) {
               this.createAdvertiser(this.getStrSubstitutor().replace((String)entry.getValue()), configSource, buffer, "text/xml");
            }
         }

         Iterator<StatusListener> iter = ((StatusLogger)LOGGER).getListeners();
         boolean found = false;

         while(iter.hasNext()) {
            StatusListener listener = (StatusListener)iter.next();
            if(listener instanceof StatusConsoleListener) {
               found = true;
               ((StatusConsoleListener)listener).setLevel(status);
               if(!verbose) {
                  ((StatusConsoleListener)listener).setFilters(VERBOSE_CLASSES);
               }
            }
         }

         if(!found && status != Level.OFF) {
            StatusConsoleListener listener = new StatusConsoleListener(status, stream);
            if(!verbose) {
               listener.setFilters(VERBOSE_CLASSES);
            }

            ((StatusLogger)LOGGER).registerListener(listener);

            for(String msg : messages) {
               LOGGER.error(msg);
            }
         }
      } catch (SAXException var23) {
         LOGGER.error((String)("Error parsing " + configSource.getLocation()), (Throwable)var23);
      } catch (IOException var24) {
         LOGGER.error((String)("Error parsing " + configSource.getLocation()), (Throwable)var24);
      } catch (ParserConfigurationException var25) {
         LOGGER.error((String)("Error parsing " + configSource.getLocation()), (Throwable)var25);
      }

      if(this.strict && this.schema != null && buffer != null) {
         InputStream is = null;

         try {
            is = this.getClass().getClassLoader().getResourceAsStream(this.schema);
         } catch (Exception var21) {
            LOGGER.error("Unable to access schema " + this.schema);
         }

         if(is != null) {
            Source src = new StreamSource(is, "Log4j-config.xsd");
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = null;

            try {
               schema = factory.newSchema(src);
            } catch (SAXException var20) {
               LOGGER.error((String)"Error parsing Log4j schema", (Throwable)var20);
            }

            if(schema != null) {
               Validator validator = schema.newValidator();

               try {
                  validator.validate(new StreamSource(new ByteArrayInputStream(buffer)));
               } catch (IOException var18) {
                  LOGGER.error((String)"Error reading configuration for validation", (Throwable)var18);
               } catch (SAXException var19) {
                  LOGGER.error((String)"Error validating configuration", (Throwable)var19);
               }
            }
         }
      }

      if(this.getName() == null) {
         this.setName(configSource.getLocation());
      }

   }

   public void setup() {
      if(this.rootElement == null) {
         LOGGER.error("No logging configuration");
      } else {
         this.constructHierarchy(this.rootNode, this.rootElement);
         if(this.status.size() <= 0) {
            this.rootElement = null;
         } else {
            for(XMLConfiguration.Status s : this.status) {
               LOGGER.error("Error processing element " + s.name + ": " + s.errorType);
            }

         }
      }
   }

   public Configuration reconfigure() {
      if(this.configFile != null) {
         try {
            ConfigurationFactory.ConfigurationSource source = new ConfigurationFactory.ConfigurationSource(new FileInputStream(this.configFile), this.configFile);
            return new XMLConfiguration(source);
         } catch (FileNotFoundException var2) {
            LOGGER.error((String)("Cannot locate file " + this.configFile), (Throwable)var2);
         }
      }

      return null;
   }

   private void constructHierarchy(Node node, Element element) {
      this.processAttributes(node, element);
      StringBuilder buffer = new StringBuilder();
      NodeList list = element.getChildNodes();
      List<Node> children = node.getChildren();

      for(int i = 0; i < list.getLength(); ++i) {
         org.w3c.dom.Node w3cNode = list.item(i);
         if(w3cNode instanceof Element) {
            Element child = (Element)w3cNode;
            String name = this.getType(child);
            PluginType<?> type = this.pluginManager.getPluginType(name);
            Node childNode = new Node(node, name, type);
            this.constructHierarchy(childNode, child);
            if(type == null) {
               String value = childNode.getValue();
               if(!childNode.hasChildren() && value != null) {
                  node.getAttributes().put(name, value);
               } else {
                  this.status.add(new XMLConfiguration.Status(name, element, XMLConfiguration.ErrorType.CLASS_NOT_FOUND));
               }
            } else {
               children.add(childNode);
            }
         } else if(w3cNode instanceof Text) {
            Text data = (Text)w3cNode;
            buffer.append(data.getData());
         }
      }

      String text = buffer.toString().trim();
      if(text.length() > 0 || !node.hasChildren() && !node.isRoot()) {
         node.setValue(text);
      }

   }

   private String getType(Element element) {
      if(this.strict) {
         NamedNodeMap attrs = element.getAttributes();

         for(int i = 0; i < attrs.getLength(); ++i) {
            org.w3c.dom.Node w3cNode = attrs.item(i);
            if(w3cNode instanceof Attr) {
               Attr attr = (Attr)w3cNode;
               if(attr.getName().equalsIgnoreCase("type")) {
                  String type = attr.getValue();
                  attrs.removeNamedItem(attr.getName());
                  return type;
               }
            }
         }
      }

      return element.getTagName();
   }

   private byte[] toByteArray(InputStream is) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[16384];

      int nRead;
      while((nRead = is.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, nRead);
      }

      return buffer.toByteArray();
   }

   private Map processAttributes(Node node, Element element) {
      NamedNodeMap attrs = element.getAttributes();
      Map<String, String> attributes = node.getAttributes();

      for(int i = 0; i < attrs.getLength(); ++i) {
         org.w3c.dom.Node w3cNode = attrs.item(i);
         if(w3cNode instanceof Attr) {
            Attr attr = (Attr)w3cNode;
            if(!attr.getName().equals("xml:base")) {
               attributes.put(attr.getName(), attr.getValue());
            }
         }
      }

      return attributes;
   }

   private static enum ErrorType {
      CLASS_NOT_FOUND;
   }

   private class Status {
      private final Element element;
      private final String name;
      private final XMLConfiguration.ErrorType errorType;

      public Status(String name, Element element, XMLConfiguration.ErrorType errorType) {
         this.name = name;
         this.element = element;
         this.errorType = errorType;
      }
   }
}
