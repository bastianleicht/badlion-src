package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUResourceBundleReader;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import com.ibm.icu.util.UResourceTypeMismatchException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

class ICUResourceBundleImpl extends ICUResourceBundle {
   protected ICUResourceBundleImpl(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
      super(reader, key, resPath, resource, container);
   }

   protected final ICUResourceBundle createBundleObject(String _key, int _resource, HashMap table, UResourceBundle requested, boolean[] isAlias) {
      if(isAlias != null) {
         isAlias[0] = false;
      }

      String _resPath = this.resPath + "/" + _key;
      switch(ICUResourceBundleReader.RES_GET_TYPE(_resource)) {
      case 0:
      case 6:
         return new ICUResourceBundleImpl.ResourceString(this.reader, _key, _resPath, _resource, this);
      case 1:
         return new ICUResourceBundleImpl.ResourceBinary(this.reader, _key, _resPath, _resource, this);
      case 2:
      case 4:
      case 5:
         return new ICUResourceBundleImpl.ResourceTable(this.reader, _key, _resPath, _resource, this);
      case 3:
         if(isAlias != null) {
            isAlias[0] = true;
         }

         return this.findResource(_key, _resPath, _resource, table, requested);
      case 7:
         return new ICUResourceBundleImpl.ResourceInt(this.reader, _key, _resPath, _resource, this);
      case 8:
      case 9:
         return new ICUResourceBundleImpl.ResourceArray(this.reader, _key, _resPath, _resource, this);
      case 10:
      case 11:
      case 12:
      case 13:
      default:
         throw new IllegalStateException("The resource type is unknown");
      case 14:
         return new ICUResourceBundleImpl.ResourceIntVector(this.reader, _key, _resPath, _resource, this);
      }
   }

   private static class ResourceArray extends ICUResourceBundleImpl.ResourceContainer {
      protected String[] handleGetStringArray() {
         String[] strings = new String[this.value.getSize()];
         UResourceBundleIterator iter = this.getIterator();

         for(int i = 0; iter.hasNext(); strings[i++] = iter.next().getString()) {
            ;
         }

         return strings;
      }

      public String[] getStringArray() {
         return this.handleGetStringArray();
      }

      protected UResourceBundle handleGetImpl(String indexStr, HashMap table, UResourceBundle requested, int[] index, boolean[] isAlias) {
         int i = indexStr.length() > 0?Integer.valueOf(indexStr).intValue():-1;
         if(index != null) {
            index[0] = i;
         }

         if(i < 0) {
            throw new UResourceTypeMismatchException("Could not get the correct value for index: " + indexStr);
         } else {
            return this.createBundleObject(i, indexStr, table, requested, isAlias);
         }
      }

      protected UResourceBundle handleGetImpl(int index, HashMap table, UResourceBundle requested, boolean[] isAlias) {
         return this.createBundleObject(index, Integer.toString(index), table, requested, isAlias);
      }

      ResourceArray(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
         this.value = reader.getArray(resource);
         this.createLookupCache();
      }
   }

   private static final class ResourceBinary extends ICUResourceBundleImpl {
      public ByteBuffer getBinary() {
         return this.reader.getBinary(this.resource);
      }

      public byte[] getBinary(byte[] ba) {
         return this.reader.getBinary(this.resource, ba);
      }

      ResourceBinary(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
      }
   }

   private static class ResourceContainer extends ICUResourceBundleImpl {
      protected ICUResourceBundleReader.Container value;

      public int getSize() {
         return this.value.getSize();
      }

      protected int getContainerResource(int index) {
         return this.value.getContainerResource(index);
      }

      protected UResourceBundle createBundleObject(int index, String resKey, HashMap table, UResourceBundle requested, boolean[] isAlias) {
         int item = this.getContainerResource(index);
         if(item == -1) {
            throw new IndexOutOfBoundsException();
         } else {
            return this.createBundleObject(resKey, item, table, requested, isAlias);
         }
      }

      ResourceContainer(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
      }
   }

   private static final class ResourceInt extends ICUResourceBundleImpl {
      public int getInt() {
         return ICUResourceBundleReader.RES_GET_INT(this.resource);
      }

      public int getUInt() {
         return ICUResourceBundleReader.RES_GET_UINT(this.resource);
      }

      ResourceInt(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
      }
   }

   private static final class ResourceIntVector extends ICUResourceBundleImpl {
      private int[] value;

      public int[] getIntVector() {
         return this.value;
      }

      ResourceIntVector(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
         this.value = reader.getIntVector(resource);
      }
   }

   private static final class ResourceString extends ICUResourceBundleImpl {
      private String value;

      public String getString() {
         return this.value;
      }

      ResourceString(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
         this.value = reader.getString(resource);
      }
   }

   static class ResourceTable extends ICUResourceBundleImpl.ResourceContainer {
      protected String getKey(int index) {
         return ((ICUResourceBundleReader.Table)this.value).getKey(index);
      }

      protected Set handleKeySet() {
         TreeSet<String> keySet = new TreeSet();
         ICUResourceBundleReader.Table table = (ICUResourceBundleReader.Table)this.value;

         for(int i = 0; i < table.getSize(); ++i) {
            keySet.add(table.getKey(i));
         }

         return keySet;
      }

      protected int getTableResource(String resKey) {
         return ((ICUResourceBundleReader.Table)this.value).getTableResource(resKey);
      }

      protected int getTableResource(int index) {
         return this.getContainerResource(index);
      }

      protected UResourceBundle handleGetImpl(String resKey, HashMap table, UResourceBundle requested, int[] index, boolean[] isAlias) {
         int i = ((ICUResourceBundleReader.Table)this.value).findTableItem(resKey);
         if(index != null) {
            index[0] = i;
         }

         return i < 0?null:this.createBundleObject(i, resKey, table, requested, isAlias);
      }

      protected UResourceBundle handleGetImpl(int index, HashMap table, UResourceBundle requested, boolean[] isAlias) {
         String itemKey = ((ICUResourceBundleReader.Table)this.value).getKey(index);
         if(itemKey == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return this.createBundleObject(index, itemKey, table, requested, isAlias);
         }
      }

      ResourceTable(ICUResourceBundleReader reader, String key, String resPath, int resource, ICUResourceBundleImpl container) {
         super(reader, key, resPath, resource, container);
         this.value = reader.getTable(resource);
         this.createLookupCache();
      }
   }
}
