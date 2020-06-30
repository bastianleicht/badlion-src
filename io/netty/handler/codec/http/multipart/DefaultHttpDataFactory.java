package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultHttpDataFactory implements HttpDataFactory {
   public static final long MINSIZE = 16384L;
   private final boolean useDisk;
   private final boolean checkSize;
   private long minSize;
   private final Map requestFileDeleteMap = PlatformDependent.newConcurrentHashMap();

   public DefaultHttpDataFactory() {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = 16384L;
   }

   public DefaultHttpDataFactory(boolean useDisk) {
      this.useDisk = useDisk;
      this.checkSize = false;
   }

   public DefaultHttpDataFactory(long minSize) {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = minSize;
   }

   private List getList(HttpRequest request) {
      List<HttpData> list = (List)this.requestFileDeleteMap.get(request);
      if(list == null) {
         list = new ArrayList();
         this.requestFileDeleteMap.put(request, list);
      }

      return list;
   }

   public Attribute createAttribute(HttpRequest request, String name) {
      if(this.useDisk) {
         Attribute attribute = new DiskAttribute(name);
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(attribute);
         return attribute;
      } else if(this.checkSize) {
         Attribute attribute = new MixedAttribute(name, this.minSize);
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(attribute);
         return attribute;
      } else {
         return new MemoryAttribute(name);
      }
   }

   public Attribute createAttribute(HttpRequest request, String name, String value) {
      if(this.useDisk) {
         Attribute attribute;
         try {
            attribute = new DiskAttribute(name, value);
         } catch (IOException var6) {
            attribute = new MixedAttribute(name, value, this.minSize);
         }

         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(attribute);
         return attribute;
      } else if(this.checkSize) {
         Attribute attribute = new MixedAttribute(name, value, this.minSize);
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(attribute);
         return attribute;
      } else {
         try {
            return new MemoryAttribute(name, value);
         } catch (IOException var7) {
            throw new IllegalArgumentException(var7);
         }
      }
   }

   public FileUpload createFileUpload(HttpRequest request, String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
      if(this.useDisk) {
         FileUpload fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(fileUpload);
         return fileUpload;
      } else if(this.checkSize) {
         FileUpload fileUpload = new MixedFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.minSize);
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.add(fileUpload);
         return fileUpload;
      } else {
         return new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
      }
   }

   public void removeHttpDataFromClean(HttpRequest request, InterfaceHttpData data) {
      if(data instanceof HttpData) {
         List<HttpData> fileToDelete = this.getList(request);
         fileToDelete.remove(data);
      }

   }

   public void cleanRequestHttpDatas(HttpRequest request) {
      List<HttpData> fileToDelete = (List)this.requestFileDeleteMap.remove(request);
      if(fileToDelete != null) {
         for(HttpData data : fileToDelete) {
            data.delete();
         }

         fileToDelete.clear();
      }

   }

   public void cleanAllHttpDatas() {
      Iterator<Entry<HttpRequest, List<HttpData>>> i = this.requestFileDeleteMap.entrySet().iterator();

      while(i.hasNext()) {
         Entry<HttpRequest, List<HttpData>> e = (Entry)i.next();
         i.remove();
         List<HttpData> fileToDelete = (List)e.getValue();
         if(fileToDelete != null) {
            for(HttpData data : fileToDelete) {
               data.delete();
            }

            fileToDelete.clear();
         }
      }

   }
}
