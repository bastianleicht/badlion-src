package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.builder.ToStringStyle;

public class StandardToStringStyle extends ToStringStyle {
   private static final long serialVersionUID = 1L;

   public boolean isUseClassName() {
      return super.isUseClassName();
   }

   public void setUseClassName(boolean useClassName) {
      super.setUseClassName(useClassName);
   }

   public boolean isUseShortClassName() {
      return super.isUseShortClassName();
   }

   public void setUseShortClassName(boolean useShortClassName) {
      super.setUseShortClassName(useShortClassName);
   }

   public boolean isUseIdentityHashCode() {
      return super.isUseIdentityHashCode();
   }

   public void setUseIdentityHashCode(boolean useIdentityHashCode) {
      super.setUseIdentityHashCode(useIdentityHashCode);
   }

   public boolean isUseFieldNames() {
      return super.isUseFieldNames();
   }

   public void setUseFieldNames(boolean useFieldNames) {
      super.setUseFieldNames(useFieldNames);
   }

   public boolean isDefaultFullDetail() {
      return super.isDefaultFullDetail();
   }

   public void setDefaultFullDetail(boolean defaultFullDetail) {
      super.setDefaultFullDetail(defaultFullDetail);
   }

   public boolean isArrayContentDetail() {
      return super.isArrayContentDetail();
   }

   public void setArrayContentDetail(boolean arrayContentDetail) {
      super.setArrayContentDetail(arrayContentDetail);
   }

   public String getArrayStart() {
      return super.getArrayStart();
   }

   public void setArrayStart(String arrayStart) {
      super.setArrayStart(arrayStart);
   }

   public String getArrayEnd() {
      return super.getArrayEnd();
   }

   public void setArrayEnd(String arrayEnd) {
      super.setArrayEnd(arrayEnd);
   }

   public String getArraySeparator() {
      return super.getArraySeparator();
   }

   public void setArraySeparator(String arraySeparator) {
      super.setArraySeparator(arraySeparator);
   }

   public String getContentStart() {
      return super.getContentStart();
   }

   public void setContentStart(String contentStart) {
      super.setContentStart(contentStart);
   }

   public String getContentEnd() {
      return super.getContentEnd();
   }

   public void setContentEnd(String contentEnd) {
      super.setContentEnd(contentEnd);
   }

   public String getFieldNameValueSeparator() {
      return super.getFieldNameValueSeparator();
   }

   public void setFieldNameValueSeparator(String fieldNameValueSeparator) {
      super.setFieldNameValueSeparator(fieldNameValueSeparator);
   }

   public String getFieldSeparator() {
      return super.getFieldSeparator();
   }

   public void setFieldSeparator(String fieldSeparator) {
      super.setFieldSeparator(fieldSeparator);
   }

   public boolean isFieldSeparatorAtStart() {
      return super.isFieldSeparatorAtStart();
   }

   public void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart) {
      super.setFieldSeparatorAtStart(fieldSeparatorAtStart);
   }

   public boolean isFieldSeparatorAtEnd() {
      return super.isFieldSeparatorAtEnd();
   }

   public void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd) {
      super.setFieldSeparatorAtEnd(fieldSeparatorAtEnd);
   }

   public String getNullText() {
      return super.getNullText();
   }

   public void setNullText(String nullText) {
      super.setNullText(nullText);
   }

   public String getSizeStartText() {
      return super.getSizeStartText();
   }

   public void setSizeStartText(String sizeStartText) {
      super.setSizeStartText(sizeStartText);
   }

   public String getSizeEndText() {
      return super.getSizeEndText();
   }

   public void setSizeEndText(String sizeEndText) {
      super.setSizeEndText(sizeEndText);
   }

   public String getSummaryObjectStartText() {
      return super.getSummaryObjectStartText();
   }

   public void setSummaryObjectStartText(String summaryObjectStartText) {
      super.setSummaryObjectStartText(summaryObjectStartText);
   }

   public String getSummaryObjectEndText() {
      return super.getSummaryObjectEndText();
   }

   public void setSummaryObjectEndText(String summaryObjectEndText) {
      super.setSummaryObjectEndText(summaryObjectEndText);
   }
}
