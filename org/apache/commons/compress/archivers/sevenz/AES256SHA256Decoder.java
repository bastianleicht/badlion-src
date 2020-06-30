package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;

class AES256SHA256Decoder extends CoderBase {
   AES256SHA256Decoder() {
      super(new Class[0]);
   }

   InputStream decode(final InputStream in, final Coder coder, final byte[] passwordBytes) throws IOException {
      return new InputStream() {
         private boolean isInitialized = false;
         private CipherInputStream cipherInputStream = null;

         private CipherInputStream init() throws IOException {
            if(this.isInitialized) {
               return this.cipherInputStream;
            } else {
               int byte0 = 255 & coder.properties[0];
               int numCyclesPower = byte0 & 63;
               int byte1 = 255 & coder.properties[1];
               int ivSize = (byte0 >> 6 & 1) + (byte1 & 15);
               int saltSize = (byte0 >> 7 & 1) + (byte1 >> 4);
               if(2 + saltSize + ivSize > coder.properties.length) {
                  throw new IOException("Salt size + IV size too long");
               } else {
                  byte[] salt = new byte[saltSize];
                  System.arraycopy(coder.properties, 2, salt, 0, saltSize);
                  byte[] iv = new byte[16];
                  System.arraycopy(coder.properties, 2 + saltSize, iv, 0, ivSize);
                  if(passwordBytes == null) {
                     throw new IOException("Cannot read encrypted files without a password");
                  } else {
                     byte[] aesKeyBytes;
                     if(numCyclesPower == 63) {
                        aesKeyBytes = new byte[32];
                        System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
                        System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
                     } else {
                        MessageDigest digest;
                        try {
                           digest = MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException var15) {
                           IOException ioe = new IOException("SHA-256 is unsupported by your Java implementation");
                           ioe.initCause(var15);
                           throw ioe;
                        }

                        byte[] extra = new byte[8];

                        for(long j = 0L; j < 1L << numCyclesPower; ++j) {
                           digest.update(salt);
                           digest.update(passwordBytes);
                           digest.update(extra);

                           for(int k = 0; k < extra.length; ++k) {
                              ++extra[k];
                              if(extra[k] != 0) {
                                 break;
                              }
                           }
                        }

                        aesKeyBytes = digest.digest();
                     }

                     SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                     try {
                        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                        cipher.init(2, aesKey, new IvParameterSpec(iv));
                        this.cipherInputStream = new CipherInputStream(in, cipher);
                        this.isInitialized = true;
                        return this.cipherInputStream;
                     } catch (GeneralSecurityException var14) {
                        IOException ioe = new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)");
                        ioe.initCause(var14);
                        throw ioe;
                     }
                  }
               }
            }
         }

         public int read() throws IOException {
            return this.init().read();
         }

         public int read(byte[] b, int off, int len) throws IOException {
            return this.init().read(b, off, len);
         }

         public void close() {
         }
      };
   }
}
