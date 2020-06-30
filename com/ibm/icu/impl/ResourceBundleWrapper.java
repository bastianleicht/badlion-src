package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ResourceBundleWrapper extends UResourceBundle {
   private ResourceBundle bundle = null;
   private String localeID = null;
   private String baseName = null;
   private List keys = null;
   private static final boolean DEBUG = ICUDebug.enabled("resourceBundleWrapper");

   private ResourceBundleWrapper(ResourceBundle bundle) {
      this.bundle = bundle;
   }

   protected void setLoadingStatus(int newStatus) {
   }

   protected Object handleGetObject(String aKey) {
      ResourceBundleWrapper current = this;

      Object obj;
      for(obj = null; current != null; current = (ResourceBundleWrapper)current.getParent()) {
         try {
            obj = current.bundle.getObject(aKey);
            break;
         }
      }

      if(obj == null) {
         throw new MissingResourceException("Can\'t find resource for bundle " + this.baseName + ", key " + aKey, this.getClass().getName(), aKey);
      } else {
         return obj;
      }
   }

   public Enumeration getKeys() {
      return Collections.enumeration(this.keys);
   }

   private void initKeysVector() {
      ResourceBundleWrapper current = this;

      for(this.keys = new ArrayList(); current != null; current = (ResourceBundleWrapper)current.getParent()) {
         Enumeration<String> e = current.bundle.getKeys();

         while(e.hasMoreElements()) {
            String elem = (String)e.nextElement();
            if(!this.keys.contains(elem)) {
               this.keys.add(elem);
            }
         }
      }

   }

   protected String getLocaleID() {
      return this.localeID;
   }

   protected String getBaseName() {
      return this.bundle.getClass().getName().replace('.', '/');
   }

   public ULocale getULocale() {
      return new ULocale(this.localeID);
   }

   public UResourceBundle getParent() {
      return (UResourceBundle)this.parent;
   }

   public static UResourceBundle getBundleInstance(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
      UResourceBundle b = instantiateBundle(baseName, localeID, root, disableFallback);
      if(b == null) {
         String separator = "_";
         if(baseName.indexOf(47) >= 0) {
            separator = "/";
         }

         throw new MissingResourceException("Could not find the bundle " + baseName + separator + localeID, "", "");
      } else {
         return b;
      }
   }

   protected static synchronized UResourceBundle instantiateBundle(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
      if(root == null) {
         root = Utility.getFallbackClassLoader();
      }

      final ClassLoader cl = root;
      String name = baseName;
      ULocale defaultLocale = ULocale.getDefault();
      if(localeID.length() != 0) {
         name = baseName + "_" + localeID;
      }

      ResourceBundleWrapper b = (ResourceBundleWrapper)loadFromCache(root, name, defaultLocale);
      if(b == null) {
         ResourceBundleWrapper parent = null;
         int i = localeID.lastIndexOf(95);
         boolean loadFromProperties = false;
         if(i != -1) {
            String locName = localeID.substring(0, i);
            parent = (ResourceBundleWrapper)loadFromCache(root, baseName + "_" + locName, defaultLocale);
            if(parent == null) {
               parent = (ResourceBundleWrapper)instantiateBundle(baseName, locName, root, disableFallback);
            }
         } else if(localeID.length() > 0) {
            parent = (ResourceBundleWrapper)loadFromCache(root, baseName, defaultLocale);
            if(parent == null) {
               parent = (ResourceBundleWrapper)instantiateBundle(baseName, "", root, disableFallback);
            }
         }

         try {
            Class<? extends ResourceBundle> cls = cl.loadClass(name).asSubclass(ResourceBundle.class);
            ResourceBundle bx = (ResourceBundle)cls.newInstance();
            b = new ResourceBundleWrapper(bx);
            if(parent != null) {
               b.setParent(parent);
            }

            b.baseName = baseName;
            b.localeID = localeID;
         } catch (ClassNotFoundException var29) {
            loadFromProperties = true;
         } catch (NoClassDefFoundError var30) {
            loadFromProperties = true;
         } catch (Exception var31) {
            if(DEBUG) {
               System.out.println("failure");
            }

            if(DEBUG) {
               System.out.println(var31);
            }
         }

         if(loadFromProperties) {
            try {
               final String resName = name.replace('.', '/') + ".properties";
               InputStream stream = (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
                  public InputStream run() {
                     return cl != null?cl.getResourceAsStream(resName):ClassLoader.getSystemResourceAsStream(resName);
                  }
               });
               if(stream != null) {
                  BufferedInputStream var35 = new BufferedInputStream(stream);

                  try {
                     b = new ResourceBundleWrapper(new PropertyResourceBundle(var35));
                     if(parent != null) {
                        b.setParent(parent);
                     }

                     b.baseName = baseName;
                     b.localeID = localeID;
                  } catch (Exception var26) {
                     ;
                  } finally {
                     try {
                        var35.close();
                     } catch (Exception var25) {
                        ;
                     }

                  }
               }

               if(b == null) {
                  String defaultName = defaultLocale.toString();
                  if(localeID.length() > 0 && localeID.indexOf(95) < 0 && defaultName.indexOf(localeID) == -1) {
                     b = (ResourceBundleWrapper)loadFromCache(cl, baseName + "_" + defaultName, defaultLocale);
                     if(b == null) {
                        b = (ResourceBundleWrapper)instantiateBundle(baseName, defaultName, cl, disableFallback);
                     }
                  }
               }

               if(b == null) {
                  b = parent;
               }
            } catch (Exception var28) {
               if(DEBUG) {
                  System.out.println("failure");
               }

               if(DEBUG) {
                  System.out.println(var28);
               }
            }
         }

         b = (ResourceBundleWrapper)addToCache(root, name, defaultLocale, b);
      }

      if(b != null) {
         b.initKeysVector();
      } else if(DEBUG) {
         System.out.println("Returning null for " + baseName + "_" + localeID);
      }

      return b;
   }
}
