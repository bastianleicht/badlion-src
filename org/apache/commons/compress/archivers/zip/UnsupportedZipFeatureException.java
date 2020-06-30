package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipMethod;

public class UnsupportedZipFeatureException extends ZipException {
   private final UnsupportedZipFeatureException.Feature reason;
   private final ZipArchiveEntry entry;
   private static final long serialVersionUID = 20130101L;

   public UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature reason, ZipArchiveEntry entry) {
      super("unsupported feature " + reason + " used in entry " + entry.getName());
      this.reason = reason;
      this.entry = entry;
   }

   public UnsupportedZipFeatureException(ZipMethod method, ZipArchiveEntry entry) {
      super("unsupported feature method \'" + method.name() + "\' used in entry " + entry.getName());
      this.reason = UnsupportedZipFeatureException.Feature.METHOD;
      this.entry = entry;
   }

   public UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature reason) {
      super("unsupported feature " + reason + " used in archive.");
      this.reason = reason;
      this.entry = null;
   }

   public UnsupportedZipFeatureException.Feature getFeature() {
      return this.reason;
   }

   public ZipArchiveEntry getEntry() {
      return this.entry;
   }

   public static class Feature {
      public static final UnsupportedZipFeatureException.Feature ENCRYPTION = new UnsupportedZipFeatureException.Feature("encryption");
      public static final UnsupportedZipFeatureException.Feature METHOD = new UnsupportedZipFeatureException.Feature("compression method");
      public static final UnsupportedZipFeatureException.Feature DATA_DESCRIPTOR = new UnsupportedZipFeatureException.Feature("data descriptor");
      public static final UnsupportedZipFeatureException.Feature SPLITTING = new UnsupportedZipFeatureException.Feature("splitting");
      private final String name;

      private Feature(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }
}
