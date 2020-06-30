package org.apache.commons.lang3.builder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;

public abstract class ToStringStyle implements Serializable {
   private static final long serialVersionUID = -2587890625525655916L;
   public static final ToStringStyle DEFAULT_STYLE = new ToStringStyle.DefaultToStringStyle();
   public static final ToStringStyle MULTI_LINE_STYLE = new ToStringStyle.MultiLineToStringStyle();
   public static final ToStringStyle NO_FIELD_NAMES_STYLE = new ToStringStyle.NoFieldNameToStringStyle();
   public static final ToStringStyle SHORT_PREFIX_STYLE = new ToStringStyle.ShortPrefixToStringStyle();
   public static final ToStringStyle SIMPLE_STYLE = new ToStringStyle.SimpleToStringStyle();
   private static final ThreadLocal REGISTRY = new ThreadLocal();
   private boolean useFieldNames = true;
   private boolean useClassName = true;
   private boolean useShortClassName = false;
   private boolean useIdentityHashCode = true;
   private String contentStart = "[";
   private String contentEnd = "]";
   private String fieldNameValueSeparator = "=";
   private boolean fieldSeparatorAtStart = false;
   private boolean fieldSeparatorAtEnd = false;
   private String fieldSeparator = ",";
   private String arrayStart = "{";
   private String arraySeparator = ",";
   private boolean arrayContentDetail = true;
   private String arrayEnd = "}";
   private boolean defaultFullDetail = true;
   private String nullText = "<null>";
   private String sizeStartText = "<size=";
   private String sizeEndText = ">";
   private String summaryObjectStartText = "<";
   private String summaryObjectEndText = ">";

   static Map getRegistry() {
      return (Map)REGISTRY.get();
   }

   static boolean isRegistered(Object value) {
      Map<Object, Object> m = getRegistry();
      return m != null && m.containsKey(value);
   }

   static void register(Object value) {
      if(value != null) {
         Map<Object, Object> m = getRegistry();
         if(m == null) {
            REGISTRY.set(new WeakHashMap());
         }

         getRegistry().put(value, (Object)null);
      }

   }

   static void unregister(Object value) {
      if(value != null) {
         Map<Object, Object> m = getRegistry();
         if(m != null) {
            m.remove(value);
            if(m.isEmpty()) {
               REGISTRY.remove();
            }
         }
      }

   }

   public void appendSuper(StringBuffer buffer, String superToString) {
      this.appendToString(buffer, superToString);
   }

   public void appendToString(StringBuffer buffer, String toString) {
      if(toString != null) {
         int pos1 = toString.indexOf(this.contentStart) + this.contentStart.length();
         int pos2 = toString.lastIndexOf(this.contentEnd);
         if(pos1 != pos2 && pos1 >= 0 && pos2 >= 0) {
            String data = toString.substring(pos1, pos2);
            if(this.fieldSeparatorAtStart) {
               this.removeLastFieldSeparator(buffer);
            }

            buffer.append(data);
            this.appendFieldSeparator(buffer);
         }
      }

   }

   public void appendStart(StringBuffer buffer, Object object) {
      if(object != null) {
         this.appendClassName(buffer, object);
         this.appendIdentityHashCode(buffer, object);
         this.appendContentStart(buffer);
         if(this.fieldSeparatorAtStart) {
            this.appendFieldSeparator(buffer);
         }
      }

   }

   public void appendEnd(StringBuffer buffer, Object object) {
      if(!this.fieldSeparatorAtEnd) {
         this.removeLastFieldSeparator(buffer);
      }

      this.appendContentEnd(buffer);
      unregister(object);
   }

   protected void removeLastFieldSeparator(StringBuffer buffer) {
      int len = buffer.length();
      int sepLen = this.fieldSeparator.length();
      if(len > 0 && sepLen > 0 && len >= sepLen) {
         boolean match = true;

         for(int i = 0; i < sepLen; ++i) {
            if(buffer.charAt(len - 1 - i) != this.fieldSeparator.charAt(sepLen - 1 - i)) {
               match = false;
               break;
            }
         }

         if(match) {
            buffer.setLength(len - sepLen);
         }
      }

   }

   public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(value == null) {
         this.appendNullText(buffer, fieldName);
      } else {
         this.appendInternal(buffer, fieldName, value, this.isFullDetail(fullDetail));
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendInternal(StringBuffer buffer, String fieldName, Object value, boolean detail) {
      if(isRegistered(value) && !(value instanceof Number) && !(value instanceof Boolean) && !(value instanceof Character)) {
         this.appendCyclicObject(buffer, fieldName, value);
      } else {
         register(value);

         try {
            if(value instanceof Collection) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (Collection)value);
               } else {
                  this.appendSummarySize(buffer, fieldName, ((Collection)value).size());
               }
            } else if(value instanceof Map) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (Map)value);
               } else {
                  this.appendSummarySize(buffer, fieldName, ((Map)value).size());
               }
            } else if(value instanceof long[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (long[])((long[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (long[])((long[])value));
               }
            } else if(value instanceof int[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (int[])((int[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (int[])((int[])value));
               }
            } else if(value instanceof short[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (short[])((short[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (short[])((short[])value));
               }
            } else if(value instanceof byte[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (byte[])((byte[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (byte[])((byte[])value));
               }
            } else if(value instanceof char[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (char[])((char[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (char[])((char[])value));
               }
            } else if(value instanceof double[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (double[])((double[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (double[])((double[])value));
               }
            } else if(value instanceof float[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (float[])((float[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (float[])((float[])value));
               }
            } else if(value instanceof boolean[]) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (boolean[])((boolean[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (boolean[])((boolean[])value));
               }
            } else if(value.getClass().isArray()) {
               if(detail) {
                  this.appendDetail(buffer, fieldName, (Object[])((Object[])value));
               } else {
                  this.appendSummary(buffer, fieldName, (Object[])((Object[])value));
               }
            } else if(detail) {
               this.appendDetail(buffer, fieldName, value);
            } else {
               this.appendSummary(buffer, fieldName, value);
            }
         } finally {
            unregister(value);
         }

      }
   }

   protected void appendCyclicObject(StringBuffer buffer, String fieldName, Object value) {
      ObjectUtils.identityToString(buffer, value);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
      buffer.append(value);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
      buffer.append(coll);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Map map) {
      buffer.append(map);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, Object value) {
      buffer.append(this.summaryObjectStartText);
      buffer.append(this.getShortClassName(value.getClass()));
      buffer.append(this.summaryObjectEndText);
   }

   public void append(StringBuffer buffer, String fieldName, long value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, long value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, int value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, int value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, short value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, short value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, byte value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, byte value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, char value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, char value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, double value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, double value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, float value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, float value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, boolean value) {
      this.appendFieldStart(buffer, fieldName);
      this.appendDetail(buffer, fieldName, value);
      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, boolean value) {
      buffer.append(value);
   }

   public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         Object item = array[i];
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         if(item == null) {
            this.appendNullText(buffer, fieldName);
         } else {
            this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
         }
      }

      buffer.append(this.arrayEnd);
   }

   protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
      buffer.append(this.arrayStart);
      int length = Array.getLength(array);

      for(int i = 0; i < length; ++i) {
         Object item = Array.get(array, i);
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         if(item == null) {
            this.appendNullText(buffer, fieldName);
         } else {
            this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
         }
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, Object[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, long[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, int[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, short[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, byte[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, char[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, double[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, float[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {
      this.appendFieldStart(buffer, fieldName);
      if(array == null) {
         this.appendNullText(buffer, fieldName);
      } else if(this.isFullDetail(fullDetail)) {
         this.appendDetail(buffer, fieldName, array);
      } else {
         this.appendSummary(buffer, fieldName, array);
      }

      this.appendFieldEnd(buffer, fieldName);
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
      buffer.append(this.arrayStart);

      for(int i = 0; i < array.length; ++i) {
         if(i > 0) {
            buffer.append(this.arraySeparator);
         }

         this.appendDetail(buffer, fieldName, array[i]);
      }

      buffer.append(this.arrayEnd);
   }

   protected void appendSummary(StringBuffer buffer, String fieldName, boolean[] array) {
      this.appendSummarySize(buffer, fieldName, array.length);
   }

   protected void appendClassName(StringBuffer buffer, Object object) {
      if(this.useClassName && object != null) {
         register(object);
         if(this.useShortClassName) {
            buffer.append(this.getShortClassName(object.getClass()));
         } else {
            buffer.append(object.getClass().getName());
         }
      }

   }

   protected void appendIdentityHashCode(StringBuffer buffer, Object object) {
      if(this.isUseIdentityHashCode() && object != null) {
         register(object);
         buffer.append('@');
         buffer.append(Integer.toHexString(System.identityHashCode(object)));
      }

   }

   protected void appendContentStart(StringBuffer buffer) {
      buffer.append(this.contentStart);
   }

   protected void appendContentEnd(StringBuffer buffer) {
      buffer.append(this.contentEnd);
   }

   protected void appendNullText(StringBuffer buffer, String fieldName) {
      buffer.append(this.nullText);
   }

   protected void appendFieldSeparator(StringBuffer buffer) {
      buffer.append(this.fieldSeparator);
   }

   protected void appendFieldStart(StringBuffer buffer, String fieldName) {
      if(this.useFieldNames && fieldName != null) {
         buffer.append(fieldName);
         buffer.append(this.fieldNameValueSeparator);
      }

   }

   protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
      this.appendFieldSeparator(buffer);
   }

   protected void appendSummarySize(StringBuffer buffer, String fieldName, int size) {
      buffer.append(this.sizeStartText);
      buffer.append(size);
      buffer.append(this.sizeEndText);
   }

   protected boolean isFullDetail(Boolean fullDetailRequest) {
      return fullDetailRequest == null?this.defaultFullDetail:fullDetailRequest.booleanValue();
   }

   protected String getShortClassName(Class cls) {
      return ClassUtils.getShortClassName(cls);
   }

   protected boolean isUseClassName() {
      return this.useClassName;
   }

   protected void setUseClassName(boolean useClassName) {
      this.useClassName = useClassName;
   }

   protected boolean isUseShortClassName() {
      return this.useShortClassName;
   }

   protected void setUseShortClassName(boolean useShortClassName) {
      this.useShortClassName = useShortClassName;
   }

   protected boolean isUseIdentityHashCode() {
      return this.useIdentityHashCode;
   }

   protected void setUseIdentityHashCode(boolean useIdentityHashCode) {
      this.useIdentityHashCode = useIdentityHashCode;
   }

   protected boolean isUseFieldNames() {
      return this.useFieldNames;
   }

   protected void setUseFieldNames(boolean useFieldNames) {
      this.useFieldNames = useFieldNames;
   }

   protected boolean isDefaultFullDetail() {
      return this.defaultFullDetail;
   }

   protected void setDefaultFullDetail(boolean defaultFullDetail) {
      this.defaultFullDetail = defaultFullDetail;
   }

   protected boolean isArrayContentDetail() {
      return this.arrayContentDetail;
   }

   protected void setArrayContentDetail(boolean arrayContentDetail) {
      this.arrayContentDetail = arrayContentDetail;
   }

   protected String getArrayStart() {
      return this.arrayStart;
   }

   protected void setArrayStart(String arrayStart) {
      if(arrayStart == null) {
         arrayStart = "";
      }

      this.arrayStart = arrayStart;
   }

   protected String getArrayEnd() {
      return this.arrayEnd;
   }

   protected void setArrayEnd(String arrayEnd) {
      if(arrayEnd == null) {
         arrayEnd = "";
      }

      this.arrayEnd = arrayEnd;
   }

   protected String getArraySeparator() {
      return this.arraySeparator;
   }

   protected void setArraySeparator(String arraySeparator) {
      if(arraySeparator == null) {
         arraySeparator = "";
      }

      this.arraySeparator = arraySeparator;
   }

   protected String getContentStart() {
      return this.contentStart;
   }

   protected void setContentStart(String contentStart) {
      if(contentStart == null) {
         contentStart = "";
      }

      this.contentStart = contentStart;
   }

   protected String getContentEnd() {
      return this.contentEnd;
   }

   protected void setContentEnd(String contentEnd) {
      if(contentEnd == null) {
         contentEnd = "";
      }

      this.contentEnd = contentEnd;
   }

   protected String getFieldNameValueSeparator() {
      return this.fieldNameValueSeparator;
   }

   protected void setFieldNameValueSeparator(String fieldNameValueSeparator) {
      if(fieldNameValueSeparator == null) {
         fieldNameValueSeparator = "";
      }

      this.fieldNameValueSeparator = fieldNameValueSeparator;
   }

   protected String getFieldSeparator() {
      return this.fieldSeparator;
   }

   protected void setFieldSeparator(String fieldSeparator) {
      if(fieldSeparator == null) {
         fieldSeparator = "";
      }

      this.fieldSeparator = fieldSeparator;
   }

   protected boolean isFieldSeparatorAtStart() {
      return this.fieldSeparatorAtStart;
   }

   protected void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart) {
      this.fieldSeparatorAtStart = fieldSeparatorAtStart;
   }

   protected boolean isFieldSeparatorAtEnd() {
      return this.fieldSeparatorAtEnd;
   }

   protected void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd) {
      this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
   }

   protected String getNullText() {
      return this.nullText;
   }

   protected void setNullText(String nullText) {
      if(nullText == null) {
         nullText = "";
      }

      this.nullText = nullText;
   }

   protected String getSizeStartText() {
      return this.sizeStartText;
   }

   protected void setSizeStartText(String sizeStartText) {
      if(sizeStartText == null) {
         sizeStartText = "";
      }

      this.sizeStartText = sizeStartText;
   }

   protected String getSizeEndText() {
      return this.sizeEndText;
   }

   protected void setSizeEndText(String sizeEndText) {
      if(sizeEndText == null) {
         sizeEndText = "";
      }

      this.sizeEndText = sizeEndText;
   }

   protected String getSummaryObjectStartText() {
      return this.summaryObjectStartText;
   }

   protected void setSummaryObjectStartText(String summaryObjectStartText) {
      if(summaryObjectStartText == null) {
         summaryObjectStartText = "";
      }

      this.summaryObjectStartText = summaryObjectStartText;
   }

   protected String getSummaryObjectEndText() {
      return this.summaryObjectEndText;
   }

   protected void setSummaryObjectEndText(String summaryObjectEndText) {
      if(summaryObjectEndText == null) {
         summaryObjectEndText = "";
      }

      this.summaryObjectEndText = summaryObjectEndText;
   }

   private static final class DefaultToStringStyle extends ToStringStyle {
      private static final long serialVersionUID = 1L;

      private Object readResolve() {
         return ToStringStyle.DEFAULT_STYLE;
      }
   }

   private static final class MultiLineToStringStyle extends ToStringStyle {
      private static final long serialVersionUID = 1L;

      MultiLineToStringStyle() {
         this.setContentStart("[");
         this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
         this.setFieldSeparatorAtStart(true);
         this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
      }

      private Object readResolve() {
         return ToStringStyle.MULTI_LINE_STYLE;
      }
   }

   private static final class NoFieldNameToStringStyle extends ToStringStyle {
      private static final long serialVersionUID = 1L;

      NoFieldNameToStringStyle() {
         this.setUseFieldNames(false);
      }

      private Object readResolve() {
         return ToStringStyle.NO_FIELD_NAMES_STYLE;
      }
   }

   private static final class ShortPrefixToStringStyle extends ToStringStyle {
      private static final long serialVersionUID = 1L;

      ShortPrefixToStringStyle() {
         this.setUseShortClassName(true);
         this.setUseIdentityHashCode(false);
      }

      private Object readResolve() {
         return ToStringStyle.SHORT_PREFIX_STYLE;
      }
   }

   private static final class SimpleToStringStyle extends ToStringStyle {
      private static final long serialVersionUID = 1L;

      SimpleToStringStyle() {
         this.setUseClassName(false);
         this.setUseIdentityHashCode(false);
         this.setUseFieldNames(false);
         this.setContentStart("");
         this.setContentEnd("");
      }

      private Object readResolve() {
         return ToStringStyle.SIMPLE_STYLE;
      }
   }
}
