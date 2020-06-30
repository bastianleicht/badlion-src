package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.Utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class URLHandler {
   public static final String PROPNAME = "urlhandler.props";
   private static final Map handlers;
   private static final boolean DEBUG = ICUDebug.enabled("URLHandler");

   public static URLHandler get(URL url) {
      if(url == null) {
         return null;
      } else {
         String protocol = url.getProtocol();
         if(handlers != null) {
            Method m = (Method)handlers.get(protocol);
            if(m != null) {
               try {
                  URLHandler handler = (URLHandler)m.invoke((Object)null, new Object[]{url});
                  if(handler != null) {
                     return handler;
                  }
               } catch (IllegalAccessException var4) {
                  if(DEBUG) {
                     System.err.println(var4);
                  }
               } catch (IllegalArgumentException var5) {
                  if(DEBUG) {
                     System.err.println(var5);
                  }
               } catch (InvocationTargetException var6) {
                  if(DEBUG) {
                     System.err.println(var6);
                  }
               }
            }
         }

         return getDefault(url);
      }
   }

   protected static URLHandler getDefault(URL url) {
      URLHandler handler = null;
      String protocol = url.getProtocol();

      try {
         if(protocol.equals("file")) {
            handler = new URLHandler.FileURLHandler(url);
         } else if(protocol.equals("jar") || protocol.equals("wsjar")) {
            handler = new URLHandler.JarURLHandler(url);
         }
      } catch (Exception var4) {
         ;
      }

      return handler;
   }

   public void guide(URLHandler.URLVisitor visitor, boolean recurse) {
      this.guide(visitor, recurse, true);
   }

   public abstract void guide(URLHandler.URLVisitor var1, boolean var2, boolean var3);

   static {
      Map<String, Method> h = null;

      try {
         InputStream is = URLHandler.class.getResourceAsStream("urlhandler.props");
         if(is == null) {
            ClassLoader loader = Utility.getFallbackClassLoader();
            is = loader.getResourceAsStream("urlhandler.props");
         }

         if(is != null) {
            Class<?>[] params = new Class[]{URL.class};
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            for(String line = br.readLine(); line != null; line = br.readLine()) {
               line = line.trim();
               if(line.length() != 0 && line.charAt(0) != 35) {
                  int ix = line.indexOf(61);
                  if(ix == -1) {
                     if(DEBUG) {
                        System.err.println("bad urlhandler line: \'" + line + "\'");
                     }
                     break;
                  }

                  String key = line.substring(0, ix).trim();
                  String value = line.substring(ix + 1).trim();

                  try {
                     Class<?> cl = Class.forName(value);
                     Method m = cl.getDeclaredMethod("get", params);
                     if(h == null) {
                        h = new HashMap();
                     }

                     h.put(key, m);
                  } catch (ClassNotFoundException var10) {
                     if(DEBUG) {
                        System.err.println(var10);
                     }
                  } catch (NoSuchMethodException var11) {
                     if(DEBUG) {
                        System.err.println(var11);
                     }
                  } catch (SecurityException var12) {
                     if(DEBUG) {
                        System.err.println(var12);
                     }
                  }
               }
            }

            br.close();
         }
      } catch (Throwable var13) {
         if(DEBUG) {
            System.err.println(var13);
         }
      }

      handlers = h;
   }

   private static class FileURLHandler extends URLHandler {
      File file;

      FileURLHandler(URL url) {
         try {
            this.file = new File(url.toURI());
         } catch (URISyntaxException var3) {
            ;
         }

         if(this.file == null || !this.file.exists()) {
            if(URLHandler.DEBUG) {
               System.err.println("file does not exist - " + url.toString());
            }

            throw new IllegalArgumentException();
         }
      }

      public void guide(URLHandler.URLVisitor v, boolean recurse, boolean strip) {
         if(this.file.isDirectory()) {
            this.process(v, recurse, strip, "/", this.file.listFiles());
         } else {
            v.visit(this.file.getName());
         }

      }

      private void process(URLHandler.URLVisitor v, boolean recurse, boolean strip, String path, File[] files) {
         for(int i = 0; i < files.length; ++i) {
            File f = files[i];
            if(f.isDirectory()) {
               if(recurse) {
                  this.process(v, recurse, strip, path + f.getName() + '/', f.listFiles());
               }
            } else {
               v.visit(strip?f.getName():path + f.getName());
            }
         }

      }
   }

   private static class JarURLHandler extends URLHandler {
      JarFile jarFile;
      String prefix;

      JarURLHandler(URL url) {
         try {
            this.prefix = url.getPath();
            int ix = this.prefix.lastIndexOf("!/");
            if(ix >= 0) {
               this.prefix = this.prefix.substring(ix + 2);
            }

            String protocol = url.getProtocol();
            if(!protocol.equals("jar")) {
               String urlStr = url.toString();
               int idx = urlStr.indexOf(":");
               if(idx != -1) {
                  url = new URL("jar" + urlStr.substring(idx));
               }
            }

            JarURLConnection conn = (JarURLConnection)url.openConnection();
            this.jarFile = conn.getJarFile();
         } catch (Exception var6) {
            if(URLHandler.DEBUG) {
               System.err.println("icurb jar error: " + var6);
            }

            throw new IllegalArgumentException("jar error: " + var6.getMessage());
         }
      }

      public void guide(URLHandler.URLVisitor v, boolean recurse, boolean strip) {
         try {
            Enumeration<JarEntry> entries = this.jarFile.entries();

            while(true) {
               String var9;
               while(true) {
                  if(!entries.hasMoreElements()) {
                     return;
                  }

                  JarEntry entry = (JarEntry)entries.nextElement();
                  if(!entry.isDirectory()) {
                     name = entry.getName();
                     if(var9.startsWith(this.prefix)) {
                        var9 = var9.substring(this.prefix.length());
                        int ix = var9.lastIndexOf(47);
                        if(ix == -1) {
                           break;
                        }

                        if(recurse) {
                           if(strip) {
                              var9 = var9.substring(ix + 1);
                           }
                           break;
                        }
                     }
                  }
               }

               v.visit(var9);
            }
         } catch (Exception var8) {
            if(URLHandler.DEBUG) {
               System.err.println("icurb jar error: " + var8);
            }
         }

      }
   }

   public interface URLVisitor {
      void visit(String var1);
   }
}
