package org.apache.http.impl.auth;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;
import org.apache.http.util.EncodingUtils;

@NotThreadSafe
final class NTLMEngineImpl implements NTLMEngine {
   protected static final int FLAG_REQUEST_UNICODE_ENCODING = 1;
   protected static final int FLAG_REQUEST_TARGET = 4;
   protected static final int FLAG_REQUEST_SIGN = 16;
   protected static final int FLAG_REQUEST_SEAL = 32;
   protected static final int FLAG_REQUEST_LAN_MANAGER_KEY = 128;
   protected static final int FLAG_REQUEST_NTLMv1 = 512;
   protected static final int FLAG_DOMAIN_PRESENT = 4096;
   protected static final int FLAG_WORKSTATION_PRESENT = 8192;
   protected static final int FLAG_REQUEST_ALWAYS_SIGN = 32768;
   protected static final int FLAG_REQUEST_NTLM2_SESSION = 524288;
   protected static final int FLAG_REQUEST_VERSION = 33554432;
   protected static final int FLAG_TARGETINFO_PRESENT = 8388608;
   protected static final int FLAG_REQUEST_128BIT_KEY_EXCH = 536870912;
   protected static final int FLAG_REQUEST_EXPLICIT_KEY_EXCH = 1073741824;
   protected static final int FLAG_REQUEST_56BIT_ENCRYPTION = Integer.MIN_VALUE;
   private static final SecureRandom RND_GEN;
   static final String DEFAULT_CHARSET = "ASCII";
   private String credentialCharset = "ASCII";
   private static final byte[] SIGNATURE;

   final String getResponseFor(String message, String username, String password, String host, String domain) throws NTLMEngineException {
      String response;
      if(message != null && !message.trim().equals("")) {
         NTLMEngineImpl.Type2Message t2m = new NTLMEngineImpl.Type2Message(message);
         response = this.getType3Message(username, password, host, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
      } else {
         response = this.getType1Message(host, domain);
      }

      return response;
   }

   String getType1Message(String host, String domain) throws NTLMEngineException {
      return (new NTLMEngineImpl.Type1Message(domain, host)).getResponse();
   }

   String getType3Message(String user, String password, String host, String domain, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
      return (new NTLMEngineImpl.Type3Message(domain, host, user, password, nonce, type2Flags, target, targetInformation)).getResponse();
   }

   String getCredentialCharset() {
      return this.credentialCharset;
   }

   void setCredentialCharset(String credentialCharset) {
      this.credentialCharset = credentialCharset;
   }

   private static String stripDotSuffix(String value) {
      if(value == null) {
         return null;
      } else {
         int index = value.indexOf(".");
         return index != -1?value.substring(0, index):value;
      }
   }

   private static String convertHost(String host) {
      return stripDotSuffix(host);
   }

   private static String convertDomain(String domain) {
      return stripDotSuffix(domain);
   }

   private static int readULong(byte[] src, int index) throws NTLMEngineException {
      if(src.length < index + 4) {
         throw new NTLMEngineException("NTLM authentication - buffer too small for DWORD");
      } else {
         return src[index] & 255 | (src[index + 1] & 255) << 8 | (src[index + 2] & 255) << 16 | (src[index + 3] & 255) << 24;
      }
   }

   private static int readUShort(byte[] src, int index) throws NTLMEngineException {
      if(src.length < index + 2) {
         throw new NTLMEngineException("NTLM authentication - buffer too small for WORD");
      } else {
         return src[index] & 255 | (src[index + 1] & 255) << 8;
      }
   }

   private static byte[] readSecurityBuffer(byte[] src, int index) throws NTLMEngineException {
      int length = readUShort(src, index);
      int offset = readULong(src, index + 4);
      if(src.length < offset + length) {
         throw new NTLMEngineException("NTLM authentication - buffer too small for data item");
      } else {
         byte[] buffer = new byte[length];
         System.arraycopy(src, offset, buffer, 0, length);
         return buffer;
      }
   }

   private static byte[] makeRandomChallenge() throws NTLMEngineException {
      if(RND_GEN == null) {
         throw new NTLMEngineException("Random generator not available");
      } else {
         byte[] rval = new byte[8];
         synchronized(RND_GEN) {
            RND_GEN.nextBytes(rval);
            return rval;
         }
      }
   }

   private static byte[] makeSecondaryKey() throws NTLMEngineException {
      if(RND_GEN == null) {
         throw new NTLMEngineException("Random generator not available");
      } else {
         byte[] rval = new byte[16];
         synchronized(RND_GEN) {
            RND_GEN.nextBytes(rval);
            return rval;
         }
      }
   }

   static byte[] hmacMD5(byte[] value, byte[] key) throws NTLMEngineException {
      NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(key);
      hmacMD5.update(value);
      return hmacMD5.getOutput();
   }

   static byte[] RC4(byte[] value, byte[] key) throws NTLMEngineException {
      try {
         Cipher rc4 = Cipher.getInstance("RC4");
         rc4.init(1, new SecretKeySpec(key, "RC4"));
         return rc4.doFinal(value);
      } catch (Exception var3) {
         throw new NTLMEngineException(var3.getMessage(), var3);
      }
   }

   static byte[] ntlm2SessionResponse(byte[] ntlmHash, byte[] challenge, byte[] clientChallenge) throws NTLMEngineException {
      try {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         md5.update(challenge);
         md5.update(clientChallenge);
         byte[] digest = md5.digest();
         byte[] sessionHash = new byte[8];
         System.arraycopy(digest, 0, sessionHash, 0, 8);
         return lmResponse(ntlmHash, sessionHash);
      } catch (Exception var6) {
         if(var6 instanceof NTLMEngineException) {
            throw (NTLMEngineException)var6;
         } else {
            throw new NTLMEngineException(var6.getMessage(), var6);
         }
      }
   }

   private static byte[] lmHash(String password) throws NTLMEngineException {
      try {
         byte[] oemPassword = password.toUpperCase(Locale.US).getBytes("US-ASCII");
         int length = Math.min(oemPassword.length, 14);
         byte[] keyBytes = new byte[14];
         System.arraycopy(oemPassword, 0, keyBytes, 0, length);
         Key lowKey = createDESKey(keyBytes, 0);
         Key highKey = createDESKey(keyBytes, 7);
         byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
         Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
         des.init(1, lowKey);
         byte[] lowHash = des.doFinal(magicConstant);
         des.init(1, highKey);
         byte[] highHash = des.doFinal(magicConstant);
         byte[] lmHash = new byte[16];
         System.arraycopy(lowHash, 0, lmHash, 0, 8);
         System.arraycopy(highHash, 0, lmHash, 8, 8);
         return lmHash;
      } catch (Exception var11) {
         throw new NTLMEngineException(var11.getMessage(), var11);
      }
   }

   private static byte[] ntlmHash(String password) throws NTLMEngineException {
      try {
         byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
         NTLMEngineImpl.MD4 md4 = new NTLMEngineImpl.MD4();
         md4.update(unicodePassword);
         return md4.getOutput();
      } catch (UnsupportedEncodingException var3) {
         throw new NTLMEngineException("Unicode not supported: " + var3.getMessage(), var3);
      }
   }

   private static byte[] lmv2Hash(String domain, String user, byte[] ntlmHash) throws NTLMEngineException {
      try {
         NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(ntlmHash);
         hmacMD5.update(user.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked"));
         if(domain != null) {
            hmacMD5.update(domain.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked"));
         }

         return hmacMD5.getOutput();
      } catch (UnsupportedEncodingException var4) {
         throw new NTLMEngineException("Unicode not supported! " + var4.getMessage(), var4);
      }
   }

   private static byte[] ntlmv2Hash(String domain, String user, byte[] ntlmHash) throws NTLMEngineException {
      try {
         NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(ntlmHash);
         hmacMD5.update(user.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked"));
         if(domain != null) {
            hmacMD5.update(domain.getBytes("UnicodeLittleUnmarked"));
         }

         return hmacMD5.getOutput();
      } catch (UnsupportedEncodingException var4) {
         throw new NTLMEngineException("Unicode not supported! " + var4.getMessage(), var4);
      }
   }

   private static byte[] lmResponse(byte[] hash, byte[] challenge) throws NTLMEngineException {
      try {
         byte[] keyBytes = new byte[21];
         System.arraycopy(hash, 0, keyBytes, 0, 16);
         Key lowKey = createDESKey(keyBytes, 0);
         Key middleKey = createDESKey(keyBytes, 7);
         Key highKey = createDESKey(keyBytes, 14);
         Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
         des.init(1, lowKey);
         byte[] lowResponse = des.doFinal(challenge);
         des.init(1, middleKey);
         byte[] middleResponse = des.doFinal(challenge);
         des.init(1, highKey);
         byte[] highResponse = des.doFinal(challenge);
         byte[] lmResponse = new byte[24];
         System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
         System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
         System.arraycopy(highResponse, 0, lmResponse, 16, 8);
         return lmResponse;
      } catch (Exception var11) {
         throw new NTLMEngineException(var11.getMessage(), var11);
      }
   }

   private static byte[] lmv2Response(byte[] hash, byte[] challenge, byte[] clientData) throws NTLMEngineException {
      NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(hash);
      hmacMD5.update(challenge);
      hmacMD5.update(clientData);
      byte[] mac = hmacMD5.getOutput();
      byte[] lmv2Response = new byte[mac.length + clientData.length];
      System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
      System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
      return lmv2Response;
   }

   private static byte[] createBlob(byte[] clientChallenge, byte[] targetInformation, byte[] timestamp) {
      byte[] blobSignature = new byte[]{(byte)1, (byte)1, (byte)0, (byte)0};
      byte[] reserved = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0};
      byte[] unknown1 = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0};
      byte[] unknown2 = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0};
      byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + 8 + unknown1.length + targetInformation.length + unknown2.length];
      int offset = 0;
      System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
      offset = offset + blobSignature.length;
      System.arraycopy(reserved, 0, blob, offset, reserved.length);
      offset = offset + reserved.length;
      System.arraycopy(timestamp, 0, blob, offset, timestamp.length);
      offset = offset + timestamp.length;
      System.arraycopy(clientChallenge, 0, blob, offset, 8);
      offset = offset + 8;
      System.arraycopy(unknown1, 0, blob, offset, unknown1.length);
      offset = offset + unknown1.length;
      System.arraycopy(targetInformation, 0, blob, offset, targetInformation.length);
      offset = offset + targetInformation.length;
      System.arraycopy(unknown2, 0, blob, offset, unknown2.length);
      int var10000 = offset + unknown2.length;
      return blob;
   }

   private static Key createDESKey(byte[] bytes, int offset) {
      byte[] keyBytes = new byte[7];
      System.arraycopy(bytes, offset, keyBytes, 0, 7);
      byte[] material = new byte[]{keyBytes[0], (byte)(keyBytes[0] << 7 | (keyBytes[1] & 255) >>> 1), (byte)(keyBytes[1] << 6 | (keyBytes[2] & 255) >>> 2), (byte)(keyBytes[2] << 5 | (keyBytes[3] & 255) >>> 3), (byte)(keyBytes[3] << 4 | (keyBytes[4] & 255) >>> 4), (byte)(keyBytes[4] << 3 | (keyBytes[5] & 255) >>> 5), (byte)(keyBytes[5] << 2 | (keyBytes[6] & 255) >>> 6), (byte)(keyBytes[6] << 1)};
      oddParity(material);
      return new SecretKeySpec(material, "DES");
   }

   private static void oddParity(byte[] bytes) {
      for(int i = 0; i < bytes.length; ++i) {
         byte b = bytes[i];
         boolean needsParity = ((b >>> 7 ^ b >>> 6 ^ b >>> 5 ^ b >>> 4 ^ b >>> 3 ^ b >>> 2 ^ b >>> 1) & 1) == 0;
         if(needsParity) {
            bytes[i] = (byte)(bytes[i] | 1);
         } else {
            bytes[i] &= -2;
         }
      }

   }

   static void writeULong(byte[] buffer, int value, int offset) {
      buffer[offset] = (byte)(value & 255);
      buffer[offset + 1] = (byte)(value >> 8 & 255);
      buffer[offset + 2] = (byte)(value >> 16 & 255);
      buffer[offset + 3] = (byte)(value >> 24 & 255);
   }

   static int F(int x, int y, int z) {
      return x & y | ~x & z;
   }

   static int G(int x, int y, int z) {
      return x & y | x & z | y & z;
   }

   static int H(int x, int y, int z) {
      return x ^ y ^ z;
   }

   static int rotintlft(int val, int numbits) {
      return val << numbits | val >>> 32 - numbits;
   }

   public String generateType1Msg(String domain, String workstation) throws NTLMEngineException {
      return this.getType1Message(workstation, domain);
   }

   public String generateType3Msg(String username, String password, String domain, String workstation, String challenge) throws NTLMEngineException {
      NTLMEngineImpl.Type2Message t2m = new NTLMEngineImpl.Type2Message(challenge);
      return this.getType3Message(username, password, workstation, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
   }

   static {
      SecureRandom rnd = null;

      try {
         rnd = SecureRandom.getInstance("SHA1PRNG");
      } catch (Exception var2) {
         ;
      }

      RND_GEN = rnd;
      byte[] bytesWithoutNull = EncodingUtils.getBytes("NTLMSSP", "ASCII");
      SIGNATURE = new byte[bytesWithoutNull.length + 1];
      System.arraycopy(bytesWithoutNull, 0, SIGNATURE, 0, bytesWithoutNull.length);
      SIGNATURE[bytesWithoutNull.length] = 0;
   }

   protected static class CipherGen {
      protected final String domain;
      protected final String user;
      protected final String password;
      protected final byte[] challenge;
      protected final String target;
      protected final byte[] targetInformation;
      protected byte[] clientChallenge;
      protected byte[] clientChallenge2;
      protected byte[] secondaryKey;
      protected byte[] timestamp;
      protected byte[] lmHash;
      protected byte[] lmResponse;
      protected byte[] ntlmHash;
      protected byte[] ntlmResponse;
      protected byte[] ntlmv2Hash;
      protected byte[] lmv2Hash;
      protected byte[] lmv2Response;
      protected byte[] ntlmv2Blob;
      protected byte[] ntlmv2Response;
      protected byte[] ntlm2SessionResponse;
      protected byte[] lm2SessionResponse;
      protected byte[] lmUserSessionKey;
      protected byte[] ntlmUserSessionKey;
      protected byte[] ntlmv2UserSessionKey;
      protected byte[] ntlm2SessionResponseUserSessionKey;
      protected byte[] lanManagerSessionKey;

      public CipherGen(String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation, byte[] clientChallenge, byte[] clientChallenge2, byte[] secondaryKey, byte[] timestamp) {
         this.lmHash = null;
         this.lmResponse = null;
         this.ntlmHash = null;
         this.ntlmResponse = null;
         this.ntlmv2Hash = null;
         this.lmv2Hash = null;
         this.lmv2Response = null;
         this.ntlmv2Blob = null;
         this.ntlmv2Response = null;
         this.ntlm2SessionResponse = null;
         this.lm2SessionResponse = null;
         this.lmUserSessionKey = null;
         this.ntlmUserSessionKey = null;
         this.ntlmv2UserSessionKey = null;
         this.ntlm2SessionResponseUserSessionKey = null;
         this.lanManagerSessionKey = null;
         this.domain = domain;
         this.target = target;
         this.user = user;
         this.password = password;
         this.challenge = challenge;
         this.targetInformation = targetInformation;
         this.clientChallenge = clientChallenge;
         this.clientChallenge2 = clientChallenge2;
         this.secondaryKey = secondaryKey;
         this.timestamp = timestamp;
      }

      public CipherGen(String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation) {
         this(domain, user, password, challenge, target, targetInformation, (byte[])null, (byte[])null, (byte[])null, (byte[])null);
      }

      public byte[] getClientChallenge() throws NTLMEngineException {
         if(this.clientChallenge == null) {
            this.clientChallenge = NTLMEngineImpl.makeRandomChallenge();
         }

         return this.clientChallenge;
      }

      public byte[] getClientChallenge2() throws NTLMEngineException {
         if(this.clientChallenge2 == null) {
            this.clientChallenge2 = NTLMEngineImpl.makeRandomChallenge();
         }

         return this.clientChallenge2;
      }

      public byte[] getSecondaryKey() throws NTLMEngineException {
         if(this.secondaryKey == null) {
            this.secondaryKey = NTLMEngineImpl.makeSecondaryKey();
         }

         return this.secondaryKey;
      }

      public byte[] getLMHash() throws NTLMEngineException {
         if(this.lmHash == null) {
            this.lmHash = NTLMEngineImpl.lmHash(this.password);
         }

         return this.lmHash;
      }

      public byte[] getLMResponse() throws NTLMEngineException {
         if(this.lmResponse == null) {
            this.lmResponse = NTLMEngineImpl.lmResponse(this.getLMHash(), this.challenge);
         }

         return this.lmResponse;
      }

      public byte[] getNTLMHash() throws NTLMEngineException {
         if(this.ntlmHash == null) {
            this.ntlmHash = NTLMEngineImpl.ntlmHash(this.password);
         }

         return this.ntlmHash;
      }

      public byte[] getNTLMResponse() throws NTLMEngineException {
         if(this.ntlmResponse == null) {
            this.ntlmResponse = NTLMEngineImpl.lmResponse(this.getNTLMHash(), this.challenge);
         }

         return this.ntlmResponse;
      }

      public byte[] getLMv2Hash() throws NTLMEngineException {
         if(this.lmv2Hash == null) {
            this.lmv2Hash = NTLMEngineImpl.lmv2Hash(this.domain, this.user, this.getNTLMHash());
         }

         return this.lmv2Hash;
      }

      public byte[] getNTLMv2Hash() throws NTLMEngineException {
         if(this.ntlmv2Hash == null) {
            this.ntlmv2Hash = NTLMEngineImpl.ntlmv2Hash(this.domain, this.user, this.getNTLMHash());
         }

         return this.ntlmv2Hash;
      }

      public byte[] getTimestamp() {
         // $FF: Couldn't be decompiled
      }

      public byte[] getNTLMv2Blob() throws NTLMEngineException {
         if(this.ntlmv2Blob == null) {
            this.ntlmv2Blob = NTLMEngineImpl.createBlob(this.getClientChallenge2(), this.targetInformation, this.getTimestamp());
         }

         return this.ntlmv2Blob;
      }

      public byte[] getNTLMv2Response() throws NTLMEngineException {
         if(this.ntlmv2Response == null) {
            this.ntlmv2Response = NTLMEngineImpl.lmv2Response(this.getNTLMv2Hash(), this.challenge, this.getNTLMv2Blob());
         }

         return this.ntlmv2Response;
      }

      public byte[] getLMv2Response() throws NTLMEngineException {
         if(this.lmv2Response == null) {
            this.lmv2Response = NTLMEngineImpl.lmv2Response(this.getLMv2Hash(), this.challenge, this.getClientChallenge());
         }

         return this.lmv2Response;
      }

      public byte[] getNTLM2SessionResponse() throws NTLMEngineException {
         if(this.ntlm2SessionResponse == null) {
            this.ntlm2SessionResponse = NTLMEngineImpl.ntlm2SessionResponse(this.getNTLMHash(), this.challenge, this.getClientChallenge());
         }

         return this.ntlm2SessionResponse;
      }

      public byte[] getLM2SessionResponse() throws NTLMEngineException {
         if(this.lm2SessionResponse == null) {
            byte[] clientChallenge = this.getClientChallenge();
            this.lm2SessionResponse = new byte[24];
            System.arraycopy(clientChallenge, 0, this.lm2SessionResponse, 0, clientChallenge.length);
            Arrays.fill(this.lm2SessionResponse, clientChallenge.length, this.lm2SessionResponse.length, (byte)0);
         }

         return this.lm2SessionResponse;
      }

      public byte[] getLMUserSessionKey() throws NTLMEngineException {
         if(this.lmUserSessionKey == null) {
            byte[] lmHash = this.getLMHash();
            this.lmUserSessionKey = new byte[16];
            System.arraycopy(lmHash, 0, this.lmUserSessionKey, 0, 8);
            Arrays.fill(this.lmUserSessionKey, 8, 16, (byte)0);
         }

         return this.lmUserSessionKey;
      }

      public byte[] getNTLMUserSessionKey() throws NTLMEngineException {
         if(this.ntlmUserSessionKey == null) {
            byte[] ntlmHash = this.getNTLMHash();
            NTLMEngineImpl.MD4 md4 = new NTLMEngineImpl.MD4();
            md4.update(ntlmHash);
            this.ntlmUserSessionKey = md4.getOutput();
         }

         return this.ntlmUserSessionKey;
      }

      public byte[] getNTLMv2UserSessionKey() throws NTLMEngineException {
         if(this.ntlmv2UserSessionKey == null) {
            byte[] ntlmv2hash = this.getNTLMv2Hash();
            byte[] truncatedResponse = new byte[16];
            System.arraycopy(this.getNTLMv2Response(), 0, truncatedResponse, 0, 16);
            this.ntlmv2UserSessionKey = NTLMEngineImpl.hmacMD5(truncatedResponse, ntlmv2hash);
         }

         return this.ntlmv2UserSessionKey;
      }

      public byte[] getNTLM2SessionResponseUserSessionKey() throws NTLMEngineException {
         if(this.ntlm2SessionResponseUserSessionKey == null) {
            byte[] ntlmUserSessionKey = this.getNTLMUserSessionKey();
            byte[] ntlm2SessionResponseNonce = this.getLM2SessionResponse();
            byte[] sessionNonce = new byte[this.challenge.length + ntlm2SessionResponseNonce.length];
            System.arraycopy(this.challenge, 0, sessionNonce, 0, this.challenge.length);
            System.arraycopy(ntlm2SessionResponseNonce, 0, sessionNonce, this.challenge.length, ntlm2SessionResponseNonce.length);
            this.ntlm2SessionResponseUserSessionKey = NTLMEngineImpl.hmacMD5(sessionNonce, ntlmUserSessionKey);
         }

         return this.ntlm2SessionResponseUserSessionKey;
      }

      public byte[] getLanManagerSessionKey() throws NTLMEngineException {
         if(this.lanManagerSessionKey == null) {
            byte[] lmHash = this.getLMHash();
            byte[] lmResponse = this.getLMResponse();

            try {
               byte[] keyBytes = new byte[14];
               System.arraycopy(lmHash, 0, keyBytes, 0, 8);
               Arrays.fill(keyBytes, 8, keyBytes.length, (byte)-67);
               Key lowKey = NTLMEngineImpl.createDESKey(keyBytes, 0);
               Key highKey = NTLMEngineImpl.createDESKey(keyBytes, 7);
               byte[] truncatedResponse = new byte[8];
               System.arraycopy(lmResponse, 0, truncatedResponse, 0, truncatedResponse.length);
               Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
               des.init(1, lowKey);
               byte[] lowPart = des.doFinal(truncatedResponse);
               des = Cipher.getInstance("DES/ECB/NoPadding");
               des.init(1, highKey);
               byte[] highPart = des.doFinal(truncatedResponse);
               this.lanManagerSessionKey = new byte[16];
               System.arraycopy(lowPart, 0, this.lanManagerSessionKey, 0, lowPart.length);
               System.arraycopy(highPart, 0, this.lanManagerSessionKey, lowPart.length, highPart.length);
            } catch (Exception var10) {
               throw new NTLMEngineException(var10.getMessage(), var10);
            }
         }

         return this.lanManagerSessionKey;
      }
   }

   static class HMACMD5 {
      protected byte[] ipad;
      protected byte[] opad;
      protected MessageDigest md5;

      HMACMD5(byte[] input) throws NTLMEngineException {
         byte[] key = input;

         try {
            this.md5 = MessageDigest.getInstance("MD5");
         } catch (Exception var5) {
            throw new NTLMEngineException("Error getting md5 message digest implementation: " + var5.getMessage(), var5);
         }

         this.ipad = new byte[64];
         this.opad = new byte[64];
         int keyLength = input.length;
         if(keyLength > 64) {
            this.md5.update(input);
            key = this.md5.digest();
            keyLength = key.length;
         }

         int i;
         for(i = 0; i < keyLength; ++i) {
            this.ipad[i] = (byte)(key[i] ^ 54);
            this.opad[i] = (byte)(key[i] ^ 92);
         }

         while(i < 64) {
            this.ipad[i] = 54;
            this.opad[i] = 92;
            ++i;
         }

         this.md5.reset();
         this.md5.update(this.ipad);
      }

      byte[] getOutput() {
         byte[] digest = this.md5.digest();
         this.md5.update(this.opad);
         return this.md5.digest(digest);
      }

      void update(byte[] input) {
         this.md5.update(input);
      }

      void update(byte[] input, int offset, int length) {
         this.md5.update(input, offset, length);
      }
   }

   static class MD4 {
      protected int A = 1732584193;
      protected int B = -271733879;
      protected int C = -1732584194;
      protected int D = 271733878;
      protected long count = 0L;
      protected byte[] dataBuffer = new byte[64];

      void update(byte[] input) {
         int curBufferPos = (int)(this.count & 63L);
         int inputIndex = 0;

         while(input.length - inputIndex + curBufferPos >= this.dataBuffer.length) {
            int transferAmt = this.dataBuffer.length - curBufferPos;
            System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
            this.count += (long)transferAmt;
            curBufferPos = 0;
            inputIndex += transferAmt;
            this.processBuffer();
         }

         if(inputIndex < input.length) {
            int transferAmt = input.length - inputIndex;
            System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
            this.count += (long)transferAmt;
            int var10000 = curBufferPos + transferAmt;
         }

      }

      byte[] getOutput() {
         int bufferIndex = (int)(this.count & 63L);
         int padLen = bufferIndex < 56?56 - bufferIndex:120 - bufferIndex;
         byte[] postBytes = new byte[padLen + 8];
         postBytes[0] = -128;

         for(int i = 0; i < 8; ++i) {
            postBytes[padLen + i] = (byte)((int)(this.count * 8L >>> 8 * i));
         }

         this.update(postBytes);
         byte[] result = new byte[16];
         NTLMEngineImpl.writeULong(result, this.A, 0);
         NTLMEngineImpl.writeULong(result, this.B, 4);
         NTLMEngineImpl.writeULong(result, this.C, 8);
         NTLMEngineImpl.writeULong(result, this.D, 12);
         return result;
      }

      protected void processBuffer() {
         int[] d = new int[16];

         for(int i = 0; i < 16; ++i) {
            d[i] = (this.dataBuffer[i * 4] & 255) + ((this.dataBuffer[i * 4 + 1] & 255) << 8) + ((this.dataBuffer[i * 4 + 2] & 255) << 16) + ((this.dataBuffer[i * 4 + 3] & 255) << 24);
         }

         int AA = this.A;
         int BB = this.B;
         int CC = this.C;
         int DD = this.D;
         this.round1(d);
         this.round2(d);
         this.round3(d);
         this.A += AA;
         this.B += BB;
         this.C += CC;
         this.D += DD;
      }

      protected void round1(int[] d) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[0], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[1], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[2], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[3], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[4], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[5], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[6], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[7], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[8], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[9], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[10], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[11], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[12], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[13], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[14], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[15], 19);
      }

      protected void round2(int[] d) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[0] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[4] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[8] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[12] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[1] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[5] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[9] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[13] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[2] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[6] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[10] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[14] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[3] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[7] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[11] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[15] + 1518500249, 13);
      }

      protected void round3(int[] d) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[0] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[8] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[4] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[12] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[2] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[10] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[6] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[14] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[1] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[9] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[5] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[13] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[3] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[11] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[7] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[15] + 1859775393, 15);
      }
   }

   static class NTLMMessage {
      private byte[] messageContents = null;
      private int currentOutputPosition = 0;

      NTLMMessage() {
      }

      NTLMMessage(String messageBody, int expectedType) throws NTLMEngineException {
         this.messageContents = Base64.decodeBase64(EncodingUtils.getBytes(messageBody, "ASCII"));
         if(this.messageContents.length < NTLMEngineImpl.SIGNATURE.length) {
            throw new NTLMEngineException("NTLM message decoding error - packet too short");
         } else {
            for(int i = 0; i < NTLMEngineImpl.SIGNATURE.length; ++i) {
               if(this.messageContents[i] != NTLMEngineImpl.SIGNATURE[i]) {
                  throw new NTLMEngineException("NTLM message expected - instead got unrecognized bytes");
               }
            }

            int type = this.readULong(NTLMEngineImpl.SIGNATURE.length);
            if(type != expectedType) {
               throw new NTLMEngineException("NTLM type " + Integer.toString(expectedType) + " message expected - instead got type " + Integer.toString(type));
            } else {
               this.currentOutputPosition = this.messageContents.length;
            }
         }
      }

      protected int getPreambleLength() {
         return NTLMEngineImpl.SIGNATURE.length + 4;
      }

      protected int getMessageLength() {
         return this.currentOutputPosition;
      }

      protected byte readByte(int position) throws NTLMEngineException {
         if(this.messageContents.length < position + 1) {
            throw new NTLMEngineException("NTLM: Message too short");
         } else {
            return this.messageContents[position];
         }
      }

      protected void readBytes(byte[] buffer, int position) throws NTLMEngineException {
         if(this.messageContents.length < position + buffer.length) {
            throw new NTLMEngineException("NTLM: Message too short");
         } else {
            System.arraycopy(this.messageContents, position, buffer, 0, buffer.length);
         }
      }

      protected int readUShort(int position) throws NTLMEngineException {
         return NTLMEngineImpl.readUShort(this.messageContents, position);
      }

      protected int readULong(int position) throws NTLMEngineException {
         return NTLMEngineImpl.readULong(this.messageContents, position);
      }

      protected byte[] readSecurityBuffer(int position) throws NTLMEngineException {
         return NTLMEngineImpl.readSecurityBuffer(this.messageContents, position);
      }

      protected void prepareResponse(int maxlength, int messageType) {
         this.messageContents = new byte[maxlength];
         this.currentOutputPosition = 0;
         this.addBytes(NTLMEngineImpl.SIGNATURE);
         this.addULong(messageType);
      }

      protected void addByte(byte b) {
         this.messageContents[this.currentOutputPosition] = b;
         ++this.currentOutputPosition;
      }

      protected void addBytes(byte[] bytes) {
         if(bytes != null) {
            for(byte b : bytes) {
               this.messageContents[this.currentOutputPosition] = b;
               ++this.currentOutputPosition;
            }

         }
      }

      protected void addUShort(int value) {
         this.addByte((byte)(value & 255));
         this.addByte((byte)(value >> 8 & 255));
      }

      protected void addULong(int value) {
         this.addByte((byte)(value & 255));
         this.addByte((byte)(value >> 8 & 255));
         this.addByte((byte)(value >> 16 & 255));
         this.addByte((byte)(value >> 24 & 255));
      }

      String getResponse() {
         byte[] resp;
         if(this.messageContents.length > this.currentOutputPosition) {
            byte[] tmp = new byte[this.currentOutputPosition];
            System.arraycopy(this.messageContents, 0, tmp, 0, this.currentOutputPosition);
            resp = tmp;
         } else {
            resp = this.messageContents;
         }

         return EncodingUtils.getAsciiString(Base64.encodeBase64(resp));
      }
   }

   static class Type1Message extends NTLMEngineImpl.NTLMMessage {
      protected byte[] hostBytes;
      protected byte[] domainBytes;

      Type1Message(String domain, String host) throws NTLMEngineException {
         try {
            String unqualifiedHost = NTLMEngineImpl.stripDotSuffix(host);
            String unqualifiedDomain = NTLMEngineImpl.stripDotSuffix(domain);
            this.hostBytes = unqualifiedHost != null?unqualifiedHost.getBytes("ASCII"):null;
            this.domainBytes = unqualifiedDomain != null?unqualifiedDomain.toUpperCase(Locale.US).getBytes("ASCII"):null;
         } catch (UnsupportedEncodingException var5) {
            throw new NTLMEngineException("Unicode unsupported: " + var5.getMessage(), var5);
         }
      }

      String getResponse() {
         int finalLength = 40;
         this.prepareResponse(40, 1);
         this.addULong(-1576500735);
         this.addUShort(0);
         this.addUShort(0);
         this.addULong(40);
         this.addUShort(0);
         this.addUShort(0);
         this.addULong(40);
         this.addUShort(261);
         this.addULong(2600);
         this.addUShort(3840);
         return super.getResponse();
      }
   }

   static class Type2Message extends NTLMEngineImpl.NTLMMessage {
      protected byte[] challenge = new byte[8];
      protected String target;
      protected byte[] targetInfo;
      protected int flags;

      Type2Message(String message) throws NTLMEngineException {
         super(message, 2);
         this.readBytes(this.challenge, 24);
         this.flags = this.readULong(20);
         if((this.flags & 1) == 0) {
            throw new NTLMEngineException("NTLM type 2 message has flags that make no sense: " + Integer.toString(this.flags));
         } else {
            this.target = null;
            if(this.getMessageLength() >= 20) {
               byte[] bytes = this.readSecurityBuffer(12);
               if(bytes.length != 0) {
                  try {
                     this.target = new String(bytes, "UnicodeLittleUnmarked");
                  } catch (UnsupportedEncodingException var4) {
                     throw new NTLMEngineException(var4.getMessage(), var4);
                  }
               }
            }

            this.targetInfo = null;
            if(this.getMessageLength() >= 48) {
               byte[] bytes = this.readSecurityBuffer(40);
               if(bytes.length != 0) {
                  this.targetInfo = bytes;
               }
            }

         }
      }

      byte[] getChallenge() {
         return this.challenge;
      }

      String getTarget() {
         return this.target;
      }

      byte[] getTargetInfo() {
         return this.targetInfo;
      }

      int getFlags() {
         return this.flags;
      }
   }

   static class Type3Message extends NTLMEngineImpl.NTLMMessage {
      protected int type2Flags;
      protected byte[] domainBytes;
      protected byte[] hostBytes;
      protected byte[] userBytes;
      protected byte[] lmResp;
      protected byte[] ntResp;
      protected byte[] sessionKey;

      Type3Message(String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
         this.type2Flags = type2Flags;
         String unqualifiedHost = NTLMEngineImpl.stripDotSuffix(host);
         String unqualifiedDomain = NTLMEngineImpl.stripDotSuffix(domain);
         NTLMEngineImpl.CipherGen gen = new NTLMEngineImpl.CipherGen(unqualifiedDomain, user, password, nonce, target, targetInformation);

         byte[] userSessionKey;
         try {
            if((type2Flags & 8388608) != 0 && targetInformation != null && target != null) {
               this.ntResp = gen.getNTLMv2Response();
               this.lmResp = gen.getLMv2Response();
               if((type2Flags & 128) != 0) {
                  userSessionKey = gen.getLanManagerSessionKey();
               } else {
                  userSessionKey = gen.getNTLMv2UserSessionKey();
               }
            } else if((type2Flags & 524288) != 0) {
               this.ntResp = gen.getNTLM2SessionResponse();
               this.lmResp = gen.getLM2SessionResponse();
               if((type2Flags & 128) != 0) {
                  userSessionKey = gen.getLanManagerSessionKey();
               } else {
                  userSessionKey = gen.getNTLM2SessionResponseUserSessionKey();
               }
            } else {
               this.ntResp = gen.getNTLMResponse();
               this.lmResp = gen.getLMResponse();
               if((type2Flags & 128) != 0) {
                  userSessionKey = gen.getLanManagerSessionKey();
               } else {
                  userSessionKey = gen.getNTLMUserSessionKey();
               }
            }
         } catch (NTLMEngineException var15) {
            this.ntResp = new byte[0];
            this.lmResp = gen.getLMResponse();
            if((type2Flags & 128) != 0) {
               userSessionKey = gen.getLanManagerSessionKey();
            } else {
               userSessionKey = gen.getLMUserSessionKey();
            }
         }

         if((type2Flags & 16) != 0) {
            if((type2Flags & 1073741824) != 0) {
               this.sessionKey = NTLMEngineImpl.RC4(gen.getSecondaryKey(), userSessionKey);
            } else {
               this.sessionKey = userSessionKey;
            }
         } else {
            this.sessionKey = null;
         }

         try {
            this.hostBytes = unqualifiedHost != null?unqualifiedHost.getBytes("UnicodeLittleUnmarked"):null;
            this.domainBytes = unqualifiedDomain != null?unqualifiedDomain.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked"):null;
            this.userBytes = user.getBytes("UnicodeLittleUnmarked");
         } catch (UnsupportedEncodingException var14) {
            throw new NTLMEngineException("Unicode not supported: " + var14.getMessage(), var14);
         }
      }

      String getResponse() {
         int ntRespLen = this.ntResp.length;
         int lmRespLen = this.lmResp.length;
         int domainLen = this.domainBytes != null?this.domainBytes.length:0;
         int hostLen = this.hostBytes != null?this.hostBytes.length:0;
         int userLen = this.userBytes.length;
         int sessionKeyLen;
         if(this.sessionKey != null) {
            sessionKeyLen = this.sessionKey.length;
         } else {
            sessionKeyLen = 0;
         }

         int lmRespOffset = 72;
         int ntRespOffset = 72 + lmRespLen;
         int domainOffset = ntRespOffset + ntRespLen;
         int userOffset = domainOffset + domainLen;
         int hostOffset = userOffset + userLen;
         int sessionKeyOffset = hostOffset + hostLen;
         int finalLength = sessionKeyOffset + sessionKeyLen;
         this.prepareResponse(finalLength, 3);
         this.addUShort(lmRespLen);
         this.addUShort(lmRespLen);
         this.addULong(72);
         this.addUShort(ntRespLen);
         this.addUShort(ntRespLen);
         this.addULong(ntRespOffset);
         this.addUShort(domainLen);
         this.addUShort(domainLen);
         this.addULong(domainOffset);
         this.addUShort(userLen);
         this.addUShort(userLen);
         this.addULong(userOffset);
         this.addUShort(hostLen);
         this.addUShort(hostLen);
         this.addULong(hostOffset);
         this.addUShort(sessionKeyLen);
         this.addUShort(sessionKeyLen);
         this.addULong(sessionKeyOffset);
         this.addULong(this.type2Flags & 128 | this.type2Flags & 512 | this.type2Flags & 524288 | 33554432 | this.type2Flags & '耀' | this.type2Flags & 32 | this.type2Flags & 16 | this.type2Flags & 536870912 | this.type2Flags & Integer.MIN_VALUE | this.type2Flags & 1073741824 | this.type2Flags & 8388608 | this.type2Flags & 1 | this.type2Flags & 4);
         this.addUShort(261);
         this.addULong(2600);
         this.addUShort(3840);
         this.addBytes(this.lmResp);
         this.addBytes(this.ntResp);
         this.addBytes(this.domainBytes);
         this.addBytes(this.userBytes);
         this.addBytes(this.hostBytes);
         if(this.sessionKey != null) {
            this.addBytes(this.sessionKey);
         }

         return super.getResponse();
      }
   }
}
