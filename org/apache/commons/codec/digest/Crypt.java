package org.apache.commons.codec.digest;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.commons.codec.digest.UnixCrypt;

public class Crypt {
   public static String crypt(byte[] keyBytes) {
      return crypt((byte[])keyBytes, (String)null);
   }

   public static String crypt(byte[] keyBytes, String salt) {
      return salt == null?Sha2Crypt.sha512Crypt(keyBytes):(salt.startsWith("$6$")?Sha2Crypt.sha512Crypt(keyBytes, salt):(salt.startsWith("$5$")?Sha2Crypt.sha256Crypt(keyBytes, salt):(salt.startsWith("$1$")?Md5Crypt.md5Crypt(keyBytes, salt):UnixCrypt.crypt(keyBytes, salt))));
   }

   public static String crypt(String key) {
      return crypt((String)key, (String)null);
   }

   public static String crypt(String key, String salt) {
      return crypt(key.getBytes(Charsets.UTF_8), salt);
   }
}
