package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField;
import org.apache.commons.compress.archivers.zip.ZipShort;

public class UnicodePathExtraField extends AbstractUnicodeExtraField {
   public static final ZipShort UPATH_ID = new ZipShort(28789);

   public UnicodePathExtraField() {
   }

   public UnicodePathExtraField(String text, byte[] bytes, int off, int len) {
      super(text, bytes, off, len);
   }

   public UnicodePathExtraField(String name, byte[] bytes) {
      super(name, bytes);
   }

   public ZipShort getHeaderId() {
      return UPATH_ID;
   }
}
