package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField;
import org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodePathExtraField;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipMethod;

public abstract class ZipUtil {
   private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448L);

   public static ZipLong toDosTime(Date time) {
      return new ZipLong(toDosTime(time.getTime()));
   }

   public static byte[] toDosTime(long t) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(t);
      int year = c.get(1);
      if(year < 1980) {
         return copy(DOS_TIME_MIN);
      } else {
         int month = c.get(2) + 1;
         long value = (long)(year - 1980 << 25 | month << 21 | c.get(5) << 16 | c.get(11) << 11 | c.get(12) << 5 | c.get(13) >> 1);
         return ZipLong.getBytes(value);
      }
   }

   public static long adjustToLong(int i) {
      return i < 0?4294967296L + (long)i:(long)i;
   }

   public static byte[] reverse(byte[] array) {
      int z = array.length - 1;

      for(int i = 0; i < array.length / 2; ++i) {
         byte x = array[i];
         array[i] = array[z - i];
         array[z - i] = x;
      }

      return array;
   }

   static long bigToLong(BigInteger big) {
      if(big.bitLength() <= 63) {
         return big.longValue();
      } else {
         throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
      }
   }

   static BigInteger longToBig(long l) {
      if(l < -2147483648L) {
         throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
      } else {
         if(l < 0L && l >= -2147483648L) {
            l = adjustToLong((int)l);
         }

         return BigInteger.valueOf(l);
      }
   }

   public static int signedByteToUnsignedInt(byte b) {
      return b >= 0?b:256 + b;
   }

   public static byte unsignedIntToSignedByte(int i) {
      if(i <= 255 && i >= 0) {
         return i < 128?(byte)i:(byte)(i - 256);
      } else {
         throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
      }
   }

   public static Date fromDosTime(ZipLong zipDosTime) {
      long dosTime = zipDosTime.getValue();
      return new Date(dosToJavaTime(dosTime));
   }

   public static long dosToJavaTime(long dosTime) {
      Calendar cal = Calendar.getInstance();
      cal.set(1, (int)(dosTime >> 25 & 127L) + 1980);
      cal.set(2, (int)(dosTime >> 21 & 15L) - 1);
      cal.set(5, (int)(dosTime >> 16) & 31);
      cal.set(11, (int)(dosTime >> 11) & 31);
      cal.set(12, (int)(dosTime >> 5) & 63);
      cal.set(13, (int)(dosTime << 1) & 62);
      cal.set(14, 0);
      return cal.getTime().getTime();
   }

   static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze, byte[] originalNameBytes, byte[] commentBytes) {
      UnicodePathExtraField name = (UnicodePathExtraField)ze.getExtraField(UnicodePathExtraField.UPATH_ID);
      String originalName = ze.getName();
      String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
      if(newName != null && !originalName.equals(newName)) {
         ze.setName(newName);
      }

      if(commentBytes != null && commentBytes.length > 0) {
         UnicodeCommentExtraField cmt = (UnicodeCommentExtraField)ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
         String newComment = getUnicodeStringIfOriginalMatches(cmt, commentBytes);
         if(newComment != null) {
            ze.setComment(newComment);
         }
      }

   }

   private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig) {
      if(f != null) {
         CRC32 crc32 = new CRC32();
         crc32.update(orig);
         long origCRC32 = crc32.getValue();
         if(origCRC32 == f.getNameCRC32()) {
            try {
               return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
            } catch (IOException var6) {
               return null;
            }
         }
      }

      return null;
   }

   static byte[] copy(byte[] from) {
      if(from != null) {
         byte[] to = new byte[from.length];
         System.arraycopy(from, 0, to, 0, to.length);
         return to;
      } else {
         return null;
      }
   }

   static boolean canHandleEntryData(ZipArchiveEntry entry) {
      return supportsEncryptionOf(entry) && supportsMethodOf(entry);
   }

   private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
      return !entry.getGeneralPurposeBit().usesEncryption();
   }

   private static boolean supportsMethodOf(ZipArchiveEntry entry) {
      return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8;
   }

   static void checkRequestedFeatures(ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
      if(!supportsEncryptionOf(ze)) {
         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
      } else if(!supportsMethodOf(ze)) {
         ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
         if(m == null) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
         } else {
            throw new UnsupportedZipFeatureException(m, ze);
         }
      }
   }
}
