package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField;
import org.apache.commons.compress.archivers.zip.ZipShort;

public class UnicodeCommentExtraField extends AbstractUnicodeExtraField {
   public static final ZipShort UCOM_ID = new ZipShort(25461);

   public UnicodeCommentExtraField() {
   }

   public UnicodeCommentExtraField(String text, byte[] bytes, int off, int len) {
      super(text, bytes, off, len);
   }

   public UnicodeCommentExtraField(String comment, byte[] bytes) {
      super(comment, bytes);
   }

   public ZipShort getHeaderId() {
      return UCOM_ID;
   }
}
