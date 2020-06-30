package com.ibm.icu.impl;

import com.ibm.icu.impl.CacheBase;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUConfig;
import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundleImpl;
import com.ibm.icu.impl.ICUResourceBundleReader;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.impl.URLHandler;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

public class ICUResourceBundle extends UResourceBundle {
   protected static final String ICU_DATA_PATH = "com/ibm/icu/impl/";
   public static final String ICU_BUNDLE = "data/icudt51b";
   public static final String ICU_BASE_NAME = "com/ibm/icu/impl/data/icudt51b";
   public static final String ICU_COLLATION_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/coll";
   public static final String ICU_BRKITR_NAME = "/brkitr";
   public static final String ICU_BRKITR_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/brkitr";
   public static final String ICU_RBNF_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/rbnf";
   public static final String ICU_TRANSLIT_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/translit";
   public static final String ICU_LANG_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/lang";
   public static final String ICU_CURR_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/curr";
   public static final String ICU_REGION_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/region";
   public static final String ICU_ZONE_BASE_NAME = "com/ibm/icu/impl/data/icudt51b/zone";
   private static final String NO_INHERITANCE_MARKER = "∅∅∅";
   protected String resPath;
   public static final ClassLoader ICU_DATA_CLASS_LOADER;
   protected static final String INSTALLED_LOCALES = "InstalledLocales";
   public static final int FROM_FALLBACK = 1;
   public static final int FROM_ROOT = 2;
   public static final int FROM_DEFAULT = 3;
   public static final int FROM_LOCALE = 4;
   private int loadingStatus = -1;
   private static final String ICU_RESOURCE_INDEX = "res_index";
   private static final String DEFAULT_TAG = "default";
   private static final String FULL_LOCALE_NAMES_LIST = "fullLocaleNames.lst";
   private static final boolean DEBUG;
   private static CacheBase GET_AVAILABLE_CACHE;
   protected String localeID;
   protected String baseName;
   protected ULocale ulocale;
   protected ClassLoader loader;
   protected ICUResourceBundleReader reader;
   protected String key;
   protected int resource;
   public static final int RES_BOGUS = -1;
   public static final int ALIAS = 3;
   public static final int TABLE32 = 4;
   public static final int TABLE16 = 5;
   public static final int STRING_V2 = 6;
   public static final int ARRAY16 = 9;
   private static final int[] gPublicTypes;
   private static final char RES_PATH_SEP_CHAR = '/';
   private static final String RES_PATH_SEP_STR = "/";
   private static final String ICUDATA = "ICUDATA";
   private static final char HYPHEN = '-';
   private static final String LOCALE = "LOCALE";
   protected ICUCache lookup;
   private static final int MAX_INITIAL_LOOKUP_SIZE = 64;

   public void setLoadingStatus(int newStatus) {
      this.loadingStatus = newStatus;
   }

   public int getLoadingStatus() {
      return this.loadingStatus;
   }

   public void setLoadingStatus(String requestedLocale) {
      String locale = this.getLocaleID();
      if(locale.equals("root")) {
         this.setLoadingStatus(2);
      } else if(locale.equals(requestedLocale)) {
         this.setLoadingStatus(4);
      } else {
         this.setLoadingStatus(1);
      }

   }

   public String getResPath() {
      return this.resPath;
   }

   public static final ULocale getFunctionalEquivalent(String baseName, ClassLoader loader, String resName, String keyword, ULocale locID, boolean[] isAvailable, boolean omitDefault) {
      String kwVal = locID.getKeywordValue(keyword);
      String baseLoc = locID.getBaseName();
      String defStr = null;
      ULocale parent = new ULocale(baseLoc);
      ULocale defLoc = null;
      boolean lookForDefault = false;
      ULocale fullBase = null;
      int defDepth = 0;
      int resDepth = 0;
      if(kwVal == null || kwVal.length() == 0 || kwVal.equals("default")) {
         kwVal = "";
         lookForDefault = true;
      }

      ICUResourceBundle r = null;
      r = (ICUResourceBundle)UResourceBundle.getBundleInstance(baseName, parent);
      if(isAvailable != null) {
         isAvailable[0] = false;
         ULocale[] availableULocales = getAvailEntry(baseName, loader).getULocaleList();

         for(int i = 0; i < availableULocales.length; ++i) {
            if(parent.equals(availableULocales[i])) {
               isAvailable[0] = true;
               break;
            }
         }
      }

      while(true) {
         try {
            ICUResourceBundle irb = (ICUResourceBundle)r.get(resName);
            defStr = irb.getString("default");
            if(lookForDefault) {
               kwVal = defStr;
               lookForDefault = false;
            }

            defLoc = r.getULocale();
         } catch (MissingResourceException var21) {
            ;
         }

         if(defLoc == null) {
            r = (ICUResourceBundle)r.getParent();
            ++defDepth;
         }

         if(r == null || defLoc != null) {
            break;
         }
      }

      parent = new ULocale(baseLoc);
      r = (ICUResourceBundle)UResourceBundle.getBundleInstance(baseName, parent);

      while(true) {
         try {
            ICUResourceBundle irb = (ICUResourceBundle)r.get(resName);
            irb.get(kwVal);
            fullBase = irb.getULocale();
            if(fullBase != null && resDepth > defDepth) {
               defStr = irb.getString("default");
               defLoc = r.getULocale();
               defDepth = resDepth;
            }
         } catch (MissingResourceException var20) {
            ;
         }

         if(fullBase == null) {
            r = (ICUResourceBundle)r.getParent();
            ++resDepth;
         }

         if(r == null || fullBase != null) {
            break;
         }
      }

      if(fullBase == null && defStr != null && !defStr.equals(kwVal)) {
         kwVal = defStr;
         parent = new ULocale(baseLoc);
         r = (ICUResourceBundle)UResourceBundle.getBundleInstance(baseName, parent);
         resDepth = 0;

         while(true) {
            try {
               ICUResourceBundle irb = (ICUResourceBundle)r.get(resName);
               UResourceBundle urb = irb.get(kwVal);
               fullBase = r.getULocale();
               if(!fullBase.toString().equals(urb.getLocale().toString())) {
                  fullBase = null;
               }

               if(fullBase != null && resDepth > defDepth) {
                  defStr = irb.getString("default");
                  defLoc = r.getULocale();
                  defDepth = resDepth;
               }
            } catch (MissingResourceException var19) {
               ;
            }

            if(fullBase == null) {
               r = (ICUResourceBundle)r.getParent();
               ++resDepth;
            }

            if(r == null || fullBase != null) {
               break;
            }
         }
      }

      if(fullBase == null) {
         throw new MissingResourceException("Could not find locale containing requested or default keyword.", baseName, keyword + "=" + kwVal);
      } else {
         return omitDefault && defStr.equals(kwVal) && resDepth <= defDepth?fullBase:new ULocale(fullBase.toString() + "@" + keyword + "=" + kwVal);
      }
   }

   public static final String[] getKeywordValues(String baseName, String keyword) {
      Set<String> keywords = new HashSet();
      ULocale[] locales = createULocaleList(baseName, ICU_DATA_CLASS_LOADER);

      for(int i = 0; i < locales.length; ++i) {
         try {
            UResourceBundle b = UResourceBundle.getBundleInstance(baseName, locales[i]);
            ICUResourceBundle irb = (ICUResourceBundle)((ICUResourceBundle)b.getObject(keyword));
            Enumeration<String> e = irb.getKeys();

            while(e.hasMoreElements()) {
               String s = (String)e.nextElement();
               if(!"default".equals(s)) {
                  keywords.add(s);
               }
            }
         } catch (Throwable var9) {
            ;
         }
      }

      return (String[])keywords.toArray(new String[0]);
   }

   public ICUResourceBundle getWithFallback(String path) throws MissingResourceException {
      ICUResourceBundle result = null;
      result = findResourceWithFallback(path, this, (UResourceBundle)null);
      if(result == null) {
         throw new MissingResourceException("Can\'t find resource for bundle " + this.getClass().getName() + ", key " + this.getType(), path, this.getKey());
      } else if(result.getType() == 0 && result.getString().equals("∅∅∅")) {
         throw new MissingResourceException("Encountered NO_INHERITANCE_MARKER", path, this.getKey());
      } else {
         return result;
      }
   }

   public ICUResourceBundle at(int index) {
      return (ICUResourceBundle)this.handleGet(index, (HashMap)null, this);
   }

   public ICUResourceBundle at(String key) {
      return this instanceof ICUResourceBundleImpl.ResourceTable?(ICUResourceBundle)this.handleGet(key, (HashMap)null, this):null;
   }

   public ICUResourceBundle findTopLevel(int index) {
      return (ICUResourceBundle)super.findTopLevel(index);
   }

   public ICUResourceBundle findTopLevel(String aKey) {
      return (ICUResourceBundle)super.findTopLevel(aKey);
   }

   public ICUResourceBundle findWithFallback(String path) {
      return findResourceWithFallback(path, this, (UResourceBundle)null);
   }

   public String getStringWithFallback(String path) throws MissingResourceException {
      return this.getWithFallback(path).getString();
   }

   public static Set getAvailableLocaleNameSet(String bundlePrefix, ClassLoader loader) {
      return getAvailEntry(bundlePrefix, loader).getLocaleNameSet();
   }

   public static Set getFullLocaleNameSet() {
      return getFullLocaleNameSet("com/ibm/icu/impl/data/icudt51b", ICU_DATA_CLASS_LOADER);
   }

   public static Set getFullLocaleNameSet(String bundlePrefix, ClassLoader loader) {
      return getAvailEntry(bundlePrefix, loader).getFullLocaleNameSet();
   }

   public static Set getAvailableLocaleNameSet() {
      return getAvailableLocaleNameSet("com/ibm/icu/impl/data/icudt51b", ICU_DATA_CLASS_LOADER);
   }

   public static final ULocale[] getAvailableULocales(String baseName, ClassLoader loader) {
      return getAvailEntry(baseName, loader).getULocaleList();
   }

   public static final ULocale[] getAvailableULocales() {
      return getAvailableULocales("com/ibm/icu/impl/data/icudt51b", ICU_DATA_CLASS_LOADER);
   }

   public static final Locale[] getAvailableLocales(String baseName, ClassLoader loader) {
      return getAvailEntry(baseName, loader).getLocaleList();
   }

   public static final Locale[] getAvailableLocales() {
      return getAvailEntry("com/ibm/icu/impl/data/icudt51b", ICU_DATA_CLASS_LOADER).getLocaleList();
   }

   public static final Locale[] getLocaleList(ULocale[] ulocales) {
      ArrayList<Locale> list = new ArrayList(ulocales.length);
      HashSet<Locale> uniqueSet = new HashSet();

      for(int i = 0; i < ulocales.length; ++i) {
         Locale loc = ulocales[i].toLocale();
         if(!uniqueSet.contains(loc)) {
            list.add(loc);
            uniqueSet.add(loc);
         }
      }

      return (Locale[])list.toArray(new Locale[list.size()]);
   }

   public Locale getLocale() {
      return this.getULocale().toLocale();
   }

   private static final ULocale[] createULocaleList(String baseName, ClassLoader root) {
      ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.instantiateBundle(baseName, "res_index", root, true);
      bundle = (ICUResourceBundle)bundle.get("InstalledLocales");
      int length = bundle.getSize();
      int i = 0;
      ULocale[] locales = new ULocale[length];
      UResourceBundleIterator iter = bundle.getIterator();
      iter.reset();

      while(iter.hasNext()) {
         String locstr = iter.next().getKey();
         if(locstr.equals("root")) {
            locales[i++] = ULocale.ROOT;
         } else {
            locales[i++] = new ULocale(locstr);
         }
      }

      bundle = null;
      return locales;
   }

   private static final Locale[] createLocaleList(String baseName, ClassLoader loader) {
      ULocale[] ulocales = getAvailEntry(baseName, loader).getULocaleList();
      return getLocaleList(ulocales);
   }

   private static final String[] createLocaleNameArray(String baseName, ClassLoader root) {
      ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.instantiateBundle(baseName, "res_index", root, true);
      bundle = (ICUResourceBundle)bundle.get("InstalledLocales");
      int length = bundle.getSize();
      int i = 0;
      String[] locales = new String[length];
      UResourceBundleIterator iter = bundle.getIterator();
      iter.reset();

      while(iter.hasNext()) {
         String locstr = iter.next().getKey();
         if(locstr.equals("root")) {
            locales[i++] = ULocale.ROOT.toString();
         } else {
            locales[i++] = locstr;
         }
      }

      bundle = null;
      return locales;
   }

   private static final List createFullLocaleNameArray(final String baseName, final ClassLoader root) {
      List<String> list = (List)AccessController.doPrivileged(new PrivilegedAction() {
         public List run() {
            String bn = baseName.endsWith("/")?baseName:baseName + "/";
            List<String> resList = null;
            String skipScan = ICUConfig.get("com.ibm.icu.impl.ICUResourceBundle.skipRuntimeLocaleResourceScan", "false");
            if(!skipScan.equalsIgnoreCase("true")) {
               try {
                  Enumeration<URL> urls = root.getResources(bn);

                  while(urls.hasMoreElements()) {
                     URL url = (URL)urls.nextElement();
                     URLHandler handler = URLHandler.get(url);
                     if(handler != null) {
                        final List<String> lst = new ArrayList();
                        URLHandler.URLVisitor v = new URLHandler.URLVisitor() {
                           public void visit(String s) {
                              if(s.endsWith(".res")) {
                                 String locstr = s.substring(0, s.length() - 4);
                                 if(locstr.contains("_") && !locstr.equals("res_index")) {
                                    lst.add(locstr);
                                 } else if(locstr.length() != 2 && locstr.length() != 3) {
                                    if(locstr.equalsIgnoreCase("root")) {
                                       lst.add(ULocale.ROOT.toString());
                                    }
                                 } else {
                                    lst.add(locstr);
                                 }
                              }

                           }
                        };
                        handler.guide(v, false);
                        if(resList == null) {
                           resList = new ArrayList(lst);
                        } else {
                           resList.addAll(lst);
                        }
                     } else if(ICUResourceBundle.DEBUG) {
                        System.out.println("handler for " + url + " is null");
                     }
                  }
               } catch (IOException var10) {
                  if(ICUResourceBundle.DEBUG) {
                     System.out.println("ouch: " + var10.getMessage());
                  }

                  resList = null;
               }
            }

            if(resList == null) {
               try {
                  InputStream s = root.getResourceAsStream(bn + "fullLocaleNames.lst");
                  if(s != null) {
                     resList = new ArrayList();
                     BufferedReader br = new BufferedReader(new InputStreamReader(s, "ASCII"));

                     String line;
                     while((line = br.readLine()) != null) {
                        if(line.length() != 0 && !line.startsWith("#")) {
                           if(line.equalsIgnoreCase("root")) {
                              resList.add(ULocale.ROOT.toString());
                           } else {
                              resList.add(line);
                           }
                        }
                     }

                     br.close();
                  }
               } catch (IOException var9) {
                  ;
               }
            }

            return resList;
         }
      });
      return list;
   }

   private static Set createFullLocaleNameSet(String baseName, ClassLoader loader) {
      List<String> list = createFullLocaleNameArray(baseName, loader);
      if(list == null) {
         if(DEBUG) {
            System.out.println("createFullLocaleNameArray returned null");
         }

         Set<String> locNameSet = createLocaleNameSet(baseName, loader);
         String rootLocaleID = ULocale.ROOT.toString();
         if(!locNameSet.contains(rootLocaleID)) {
            Set<String> tmp = new HashSet(locNameSet);
            tmp.add(rootLocaleID);
            locNameSet = Collections.unmodifiableSet(tmp);
         }

         return locNameSet;
      } else {
         Set<String> fullLocNameSet = new HashSet();
         fullLocNameSet.addAll(list);
         return Collections.unmodifiableSet(fullLocNameSet);
      }
   }

   private static Set createLocaleNameSet(String baseName, ClassLoader loader) {
      try {
         String[] locales = createLocaleNameArray(baseName, loader);
         HashSet<String> set = new HashSet();
         set.addAll(Arrays.asList(locales));
         return Collections.unmodifiableSet(set);
      } catch (MissingResourceException var4) {
         if(DEBUG) {
            System.out.println("couldn\'t find index for bundleName: " + baseName);
            Thread.dumpStack();
         }

         return Collections.emptySet();
      }
   }

   private static ICUResourceBundle.AvailEntry getAvailEntry(String key, ClassLoader loader) {
      return (ICUResourceBundle.AvailEntry)GET_AVAILABLE_CACHE.getInstance(key, loader);
   }

   protected static final ICUResourceBundle findResourceWithFallback(String path, UResourceBundle actualBundle, UResourceBundle requested) {
      ICUResourceBundle sub = null;
      if(requested == null) {
         requested = actualBundle;
      }

      ICUResourceBundle base = (ICUResourceBundle)actualBundle;

      for(String basePath = ((ICUResourceBundle)actualBundle).resPath.length() > 0?((ICUResourceBundle)actualBundle).resPath:""; base != null; basePath = "") {
         if(path.indexOf(47) == -1) {
            sub = (ICUResourceBundle)base.handleGet(path, (HashMap)null, requested);
            if(sub != null) {
               break;
            }
         } else {
            ICUResourceBundle currentBase = base;

            for(StringTokenizer st = new StringTokenizer(path, "/"); st.hasMoreTokens(); currentBase = sub) {
               String subKey = st.nextToken();
               sub = findResourceWithFallback(subKey, currentBase, requested);
               if(sub == null) {
                  break;
               }
            }

            if(sub != null) {
               break;
            }
         }

         base = (ICUResourceBundle)base.getParent();
         path = basePath.length() > 0?basePath + "/" + path:path;
      }

      if(sub != null) {
         sub.setLoadingStatus(((ICUResourceBundle)requested).getLocaleID());
      }

      return sub;
   }

   public boolean equals(Object other) {
      if(this == other) {
         return true;
      } else {
         if(other instanceof ICUResourceBundle) {
            ICUResourceBundle o = (ICUResourceBundle)other;
            if(this.getBaseName().equals(o.getBaseName()) && this.getLocaleID().equals(o.getLocaleID())) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   public static UResourceBundle getBundleInstance(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
      UResourceBundle b = instantiateBundle(baseName, localeID, root, disableFallback);
      if(b == null) {
         throw new MissingResourceException("Could not find the bundle " + baseName + "/" + localeID + ".res", "", "");
      } else {
         return b;
      }
   }

   protected static synchronized UResourceBundle instantiateBundle(String baseName, String localeID, ClassLoader root, boolean disableFallback) {
      ULocale defaultLocale = ULocale.getDefault();
      String localeName = localeID;
      if(localeID.indexOf(64) >= 0) {
         localeName = ULocale.getBaseName(localeID);
      }

      String fullName = ICUResourceBundleReader.getFullName(baseName, localeName);
      ICUResourceBundle b = (ICUResourceBundle)loadFromCache(root, fullName, defaultLocale);
      String rootLocale = baseName.indexOf(46) == -1?"root":"";
      String defaultID = defaultLocale.getBaseName();
      if(localeName.equals("")) {
         localeName = rootLocale;
      }

      if(DEBUG) {
         System.out.println("Creating " + fullName + " currently b is " + b);
      }

      if(b == null) {
         b = createBundle(baseName, localeName, root);
         if(DEBUG) {
            System.out.println("The bundle created is: " + b + " and disableFallback=" + disableFallback + " and bundle.getNoFallback=" + (b != null && b.getNoFallback()));
         }

         if(disableFallback || b != null && b.getNoFallback()) {
            return addToCache(root, fullName, defaultLocale, b);
         }

         if(b == null) {
            int i = localeName.lastIndexOf(95);
            if(i != -1) {
               String temp = localeName.substring(0, i);
               b = (ICUResourceBundle)instantiateBundle(baseName, temp, root, disableFallback);
               if(b != null && b.getULocale().getName().equals(temp)) {
                  b.setLoadingStatus(1);
               }
            } else if(defaultID.indexOf(localeName) == -1) {
               b = (ICUResourceBundle)instantiateBundle(baseName, defaultID, root, disableFallback);
               if(b != null) {
                  b.setLoadingStatus(3);
               }
            } else if(rootLocale.length() != 0) {
               b = createBundle(baseName, rootLocale, root);
               if(b != null) {
                  b.setLoadingStatus(2);
               }
            }
         } else {
            UResourceBundle parent = null;
            localeName = b.getLocaleID();
            int i = localeName.lastIndexOf(95);
            b = (ICUResourceBundle)addToCache(root, fullName, defaultLocale, b);
            if(b.getTableResource("%%Parent") != -1) {
               String parentLocaleName = b.getString("%%Parent");
               parent = instantiateBundle(baseName, parentLocaleName, root, disableFallback);
            } else if(i != -1) {
               parent = instantiateBundle(baseName, localeName.substring(0, i), root, disableFallback);
            } else if(!localeName.equals(rootLocale)) {
               parent = instantiateBundle(baseName, rootLocale, root, true);
            }

            if(!b.equals(parent)) {
               b.setParent(parent);
            }
         }
      }

      return b;
   }

   UResourceBundle get(String aKey, HashMap table, UResourceBundle requested) {
      ICUResourceBundle obj = (ICUResourceBundle)this.handleGet(aKey, table, requested);
      if(obj == null) {
         obj = (ICUResourceBundle)this.getParent();
         if(obj != null) {
            obj = (ICUResourceBundle)obj.get(aKey, table, requested);
         }

         if(obj == null) {
            String fullName = ICUResourceBundleReader.getFullName(this.getBaseName(), this.getLocaleID());
            throw new MissingResourceException("Can\'t find resource for bundle " + fullName + ", key " + aKey, this.getClass().getName(), aKey);
         }
      }

      obj.setLoadingStatus(((ICUResourceBundle)requested).getLocaleID());
      return obj;
   }

   public static ICUResourceBundle createBundle(String baseName, String localeID, ClassLoader root) {
      ICUResourceBundleReader reader = ICUResourceBundleReader.getReader(baseName, localeID, root);
      return reader == null?null:getBundle(reader, baseName, localeID, root);
   }

   protected String getLocaleID() {
      return this.localeID;
   }

   protected String getBaseName() {
      return this.baseName;
   }

   public ULocale getULocale() {
      return this.ulocale;
   }

   public UResourceBundle getParent() {
      return (UResourceBundle)this.parent;
   }

   protected void setParent(ResourceBundle parent) {
      this.parent = parent;
   }

   public String getKey() {
      return this.key;
   }

   public int getType() {
      return gPublicTypes[ICUResourceBundleReader.RES_GET_TYPE(this.resource)];
   }

   private boolean getNoFallback() {
      return this.reader.getNoFallback();
   }

   private static ICUResourceBundle getBundle(ICUResourceBundleReader reader, String baseName, String localeID, ClassLoader loader) {
      int rootRes = reader.getRootResource();
      if(gPublicTypes[ICUResourceBundleReader.RES_GET_TYPE(rootRes)] == 2) {
         ICUResourceBundleImpl bundle = new ICUResourceBundleImpl.ResourceTable(reader, (String)null, "", rootRes, (ICUResourceBundleImpl)null);
         bundle.baseName = baseName;
         bundle.localeID = localeID;
         bundle.ulocale = new ULocale(localeID);
         bundle.loader = loader;
         UResourceBundle alias = bundle.handleGetImpl("%%ALIAS", (HashMap)null, bundle, (int[])null, (boolean[])null);
         return (ICUResourceBundle)(alias != null?(ICUResourceBundle)UResourceBundle.getBundleInstance(baseName, alias.getString()):bundle);
      } else {
         throw new IllegalStateException("Invalid format error");
      }
   }

   protected ICUResourceBundle(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundle container) {
      this.reader = reader;
      this.key = key;
      this.resPath = resPath;
      this.resource = resource;
      if(container != null) {
         this.baseName = container.baseName;
         this.localeID = container.localeID;
         this.ulocale = container.ulocale;
         this.loader = container.loader;
         this.parent = container.parent;
      }

   }

   private String getAliasValue(int res) {
      String result = this.reader.getAlias(res);
      return result != null?result:"";
   }

   protected ICUResourceBundle findResource(String key, String resPath, int _resource, HashMap table, UResourceBundle requested) {
      ClassLoader loaderToUse = this.loader;
      String locale = null;
      String keyPath = null;
      String rpath = this.getAliasValue(_resource);
      if(table == null) {
         table = new HashMap();
      }

      if(table.get(rpath) != null) {
         throw new IllegalArgumentException("Circular references in the resource bundles");
      } else {
         table.put(rpath, "");
         String bundleName;
         if(rpath.indexOf(47) == 0) {
            int i = rpath.indexOf(47, 1);
            int j = rpath.indexOf(47, i + 1);
            bundleName = rpath.substring(1, i);
            if(j < 0) {
               locale = rpath.substring(i + 1);
               keyPath = resPath;
            } else {
               locale = rpath.substring(i + 1, j);
               keyPath = rpath.substring(j + 1, rpath.length());
            }

            if(bundleName.equals("ICUDATA")) {
               bundleName = "com/ibm/icu/impl/data/icudt51b";
               loaderToUse = ICU_DATA_CLASS_LOADER;
            } else if(bundleName.indexOf("ICUDATA") > -1) {
               int idx = bundleName.indexOf(45);
               if(idx > -1) {
                  bundleName = "com/ibm/icu/impl/data/icudt51b/" + bundleName.substring(idx + 1, bundleName.length());
                  loaderToUse = ICU_DATA_CLASS_LOADER;
               }
            }
         } else {
            int i = rpath.indexOf(47);
            if(i != -1) {
               locale = rpath.substring(0, i);
               keyPath = rpath.substring(i + 1);
            } else {
               locale = rpath;
               keyPath = resPath;
            }

            bundleName = this.baseName;
         }

         ICUResourceBundle bundle = null;
         ICUResourceBundle sub = null;
         if(bundleName.equals("LOCALE")) {
            bundleName = this.baseName;
            keyPath = rpath.substring("LOCALE".length() + 2, rpath.length());
            locale = ((ICUResourceBundle)requested).getLocaleID();
            bundle = (ICUResourceBundle)getBundleInstance(bundleName, locale, loaderToUse, false);
            if(bundle != null) {
               sub = findResourceWithFallback(keyPath, bundle, (UResourceBundle)null);
            }
         } else {
            if(locale == null) {
               bundle = (ICUResourceBundle)getBundleInstance(bundleName, "", loaderToUse, false);
            } else {
               bundle = (ICUResourceBundle)getBundleInstance(bundleName, locale, loaderToUse, false);
            }

            StringTokenizer st = new StringTokenizer(keyPath, "/");

            for(ICUResourceBundle current = bundle; st.hasMoreTokens(); current = sub) {
               String subKey = st.nextToken();
               sub = (ICUResourceBundle)current.get(subKey, table, requested);
               if(sub == null) {
                  break;
               }
            }
         }

         if(sub == null) {
            throw new MissingResourceException(this.localeID, this.baseName, key);
         } else {
            return sub;
         }
      }
   }

   protected void createLookupCache() {
      this.lookup = new SimpleCache(1, Math.max(this.getSize() * 2, 64));
   }

   protected UResourceBundle handleGet(String resKey, HashMap table, UResourceBundle requested) {
      UResourceBundle res = null;
      if(this.lookup != null) {
         res = (UResourceBundle)this.lookup.get(resKey);
      }

      if(res == null) {
         int[] index = new int[1];
         boolean[] alias = new boolean[1];
         res = this.handleGetImpl(resKey, table, requested, index, alias);
         if(res != null && this.lookup != null && !alias[0]) {
            this.lookup.put(resKey, res);
            this.lookup.put(Integer.valueOf(index[0]), res);
         }
      }

      return res;
   }

   protected UResourceBundle handleGet(int index, HashMap table, UResourceBundle requested) {
      UResourceBundle res = null;
      Integer indexKey = null;
      if(this.lookup != null) {
         indexKey = Integer.valueOf(index);
         res = (UResourceBundle)this.lookup.get(indexKey);
      }

      if(res == null) {
         boolean[] alias = new boolean[1];
         res = this.handleGetImpl(index, table, requested, alias);
         if(res != null && this.lookup != null && !alias[0]) {
            this.lookup.put(res.getKey(), res);
            this.lookup.put(indexKey, res);
         }
      }

      return res;
   }

   protected UResourceBundle handleGetImpl(String resKey, HashMap table, UResourceBundle requested, int[] index, boolean[] isAlias) {
      return null;
   }

   protected UResourceBundle handleGetImpl(int index, HashMap table, UResourceBundle requested, boolean[] isAlias) {
      return null;
   }

   /** @deprecated */
   protected int getTableResource(String resKey) {
      return -1;
   }

   protected int getTableResource(int index) {
      return -1;
   }

   /** @deprecated */
   public boolean isAlias(int index) {
      return ICUResourceBundleReader.RES_GET_TYPE(this.getTableResource(index)) == 3;
   }

   /** @deprecated */
   public boolean isAlias() {
      return ICUResourceBundleReader.RES_GET_TYPE(this.resource) == 3;
   }

   /** @deprecated */
   public boolean isAlias(String k) {
      return ICUResourceBundleReader.RES_GET_TYPE(this.getTableResource(k)) == 3;
   }

   /** @deprecated */
   public String getAliasPath(int index) {
      return this.getAliasValue(this.getTableResource(index));
   }

   /** @deprecated */
   public String getAliasPath() {
      return this.getAliasValue(this.resource);
   }

   /** @deprecated */
   public String getAliasPath(String k) {
      return this.getAliasValue(this.getTableResource(k));
   }

   protected String getKey(int index) {
      return null;
   }

   /** @deprecated */
   public Enumeration getKeysSafe() {
      if(!ICUResourceBundleReader.URES_IS_TABLE(this.resource)) {
         return this.getKeys();
      } else {
         List<String> v = new ArrayList();
         int size = this.getSize();

         for(int index = 0; index < size; ++index) {
            String curKey = this.getKey(index);
            v.add(curKey);
         }

         return Collections.enumeration(v);
      }
   }

   protected Enumeration handleGetKeys() {
      return Collections.enumeration(this.handleKeySet());
   }

   protected boolean isTopLevelResource() {
      return this.resPath.length() == 0;
   }

   static {
      ClassLoader loader = ICUData.class.getClassLoader();
      if(loader == null) {
         loader = Utility.getFallbackClassLoader();
      }

      ICU_DATA_CLASS_LOADER = loader;
      DEBUG = ICUDebug.enabled("localedata");
      GET_AVAILABLE_CACHE = new SoftCache() {
         protected ICUResourceBundle.AvailEntry createInstance(String key, ClassLoader loader) {
            return new ICUResourceBundle.AvailEntry(key, loader);
         }
      };
      gPublicTypes = new int[]{0, 1, 2, 3, 2, 2, 0, 7, 8, 8, -1, -1, -1, -1, 14, -1};
   }

   private static final class AvailEntry {
      private String prefix;
      private ClassLoader loader;
      private volatile ULocale[] ulocales;
      private volatile Locale[] locales;
      private volatile Set nameSet;
      private volatile Set fullNameSet;

      AvailEntry(String prefix, ClassLoader loader) {
         this.prefix = prefix;
         this.loader = loader;
      }

      ULocale[] getULocaleList() {
         if(this.ulocales == null) {
            synchronized(this) {
               if(this.ulocales == null) {
                  this.ulocales = ICUResourceBundle.createULocaleList(this.prefix, this.loader);
               }
            }
         }

         return this.ulocales;
      }

      Locale[] getLocaleList() {
         if(this.locales == null) {
            synchronized(this) {
               if(this.locales == null) {
                  this.locales = ICUResourceBundle.createLocaleList(this.prefix, this.loader);
               }
            }
         }

         return this.locales;
      }

      Set getLocaleNameSet() {
         if(this.nameSet == null) {
            synchronized(this) {
               if(this.nameSet == null) {
                  this.nameSet = ICUResourceBundle.createLocaleNameSet(this.prefix, this.loader);
               }
            }
         }

         return this.nameSet;
      }

      Set getFullLocaleNameSet() {
         if(this.fullNameSet == null) {
            synchronized(this) {
               if(this.fullNameSet == null) {
                  this.fullNameSet = ICUResourceBundle.createFullLocaleNameSet(this.prefix, this.loader);
               }
            }
         }

         return this.fullNameSet;
      }
   }
}
