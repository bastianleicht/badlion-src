package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Beta
public final class Resources {
   /** @deprecated */
   @Deprecated
   public static InputSupplier newInputStreamSupplier(URL url) {
      return ByteStreams.asInputSupplier(asByteSource(url));
   }

   public static ByteSource asByteSource(URL url) {
      return new Resources.UrlByteSource(url);
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier newReaderSupplier(URL url, Charset charset) {
      return CharStreams.asInputSupplier(asCharSource(url, charset));
   }

   public static CharSource asCharSource(URL url, Charset charset) {
      return asByteSource(url).asCharSource(charset);
   }

   public static byte[] toByteArray(URL url) throws IOException {
      return asByteSource(url).read();
   }

   public static String toString(URL url, Charset charset) throws IOException {
      return asCharSource(url, charset).read();
   }

   public static Object readLines(URL url, Charset charset, LineProcessor callback) throws IOException {
      return CharStreams.readLines(newReaderSupplier(url, charset), callback);
   }

   public static List readLines(URL url, Charset charset) throws IOException {
      return (List)readLines(url, charset, new LineProcessor() {
         final List result = Lists.newArrayList();

         public boolean processLine(String line) {
            this.result.add(line);
            return true;
         }

         public List getResult() {
            return this.result;
         }
      });
   }

   public static void copy(URL from, OutputStream to) throws IOException {
      asByteSource(from).copyTo(to);
   }

   public static URL getResource(String resourceName) {
      ClassLoader loader = (ClassLoader)Objects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
      URL url = loader.getResource(resourceName);
      Preconditions.checkArgument(url != null, "resource %s not found.", new Object[]{resourceName});
      return url;
   }

   public static URL getResource(Class contextClass, String resourceName) {
      URL url = contextClass.getResource(resourceName);
      Preconditions.checkArgument(url != null, "resource %s relative to %s not found.", new Object[]{resourceName, contextClass.getName()});
      return url;
   }

   private static final class UrlByteSource extends ByteSource {
      private final URL url;

      private UrlByteSource(URL url) {
         this.url = (URL)Preconditions.checkNotNull(url);
      }

      public InputStream openStream() throws IOException {
         return this.url.openStream();
      }

      public String toString() {
         return "Resources.asByteSource(" + this.url + ")";
      }
   }
}
