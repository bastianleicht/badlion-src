package com.mojang.authlib.properties;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.apache.commons.codec.binary.Base64;

public class Property {
   private final String name;
   private final String value;
   private final String signature;

   public Property(String value, String name) {
      this(value, name, (String)null);
   }

   public Property(String name, String value, String signature) {
      this.name = name;
      this.value = value;
      this.signature = signature;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String getSignature() {
      return this.signature;
   }

   public boolean hasSignature() {
      return this.signature != null;
   }

   public boolean isSignatureValid(PublicKey publicKey) {
      try {
         Signature signature = Signature.getInstance("SHA1withRSA");
         signature.initVerify(publicKey);
         signature.update(this.value.getBytes());
         return signature.verify(Base64.decodeBase64(this.signature));
      } catch (NoSuchAlgorithmException var3) {
         var3.printStackTrace();
      } catch (InvalidKeyException var4) {
         var4.printStackTrace();
      } catch (SignatureException var5) {
         var5.printStackTrace();
      }

      return false;
   }
}
