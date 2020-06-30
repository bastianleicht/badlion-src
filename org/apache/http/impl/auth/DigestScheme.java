package org.apache.http.impl.auth;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.impl.auth.HttpEntityDigester;
import org.apache.http.impl.auth.RFC2617Scheme;
import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;

@NotThreadSafe
public class DigestScheme extends RFC2617Scheme {
   private static final char[] HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private boolean complete;
   private static final int QOP_UNKNOWN = -1;
   private static final int QOP_MISSING = 0;
   private static final int QOP_AUTH_INT = 1;
   private static final int QOP_AUTH = 2;
   private String lastNonce;
   private long nounceCount;
   private String cnonce;
   private String a1;
   private String a2;

   public DigestScheme(Charset credentialsCharset) {
      super(credentialsCharset);
      this.complete = false;
   }

   /** @deprecated */
   @Deprecated
   public DigestScheme(ChallengeState challengeState) {
      super(challengeState);
   }

   public DigestScheme() {
      this(Consts.ASCII);
   }

   public void processChallenge(Header header) throws MalformedChallengeException {
      super.processChallenge(header);
      this.complete = true;
   }

   public boolean isComplete() {
      String s = this.getParameter("stale");
      return "true".equalsIgnoreCase(s)?false:this.complete;
   }

   public String getSchemeName() {
      return "digest";
   }

   public boolean isConnectionBased() {
      return false;
   }

   public void overrideParamter(String name, String value) {
      this.getParameters().put(name, value);
   }

   /** @deprecated */
   @Deprecated
   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
      return this.authenticate(credentials, request, new BasicHttpContext());
   }

   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
      Args.notNull(credentials, "Credentials");
      Args.notNull(request, "HTTP request");
      if(this.getParameter("realm") == null) {
         throw new AuthenticationException("missing realm in challenge");
      } else if(this.getParameter("nonce") == null) {
         throw new AuthenticationException("missing nonce in challenge");
      } else {
         this.getParameters().put("methodname", request.getRequestLine().getMethod());
         this.getParameters().put("uri", request.getRequestLine().getUri());
         String charset = this.getParameter("charset");
         if(charset == null) {
            this.getParameters().put("charset", this.getCredentialsCharset(request));
         }

         return this.createDigestHeader(credentials, request);
      }
   }

   private static MessageDigest createMessageDigest(String digAlg) throws UnsupportedDigestAlgorithmException {
      try {
         return MessageDigest.getInstance(digAlg);
      } catch (Exception var2) {
         throw new UnsupportedDigestAlgorithmException("Unsupported algorithm in HTTP Digest authentication: " + digAlg);
      }
   }

   private Header createDigestHeader(Credentials credentials, HttpRequest request) throws AuthenticationException {
      String uri = this.getParameter("uri");
      String realm = this.getParameter("realm");
      String nonce = this.getParameter("nonce");
      String opaque = this.getParameter("opaque");
      String method = this.getParameter("methodname");
      String algorithm = this.getParameter("algorithm");
      if(algorithm == null) {
         algorithm = "MD5";
      }

      Set<String> qopset = new HashSet(8);
      int qop = -1;
      String qoplist = this.getParameter("qop");
      if(qoplist != null) {
         StringTokenizer tok = new StringTokenizer(qoplist, ",");

         while(tok.hasMoreTokens()) {
            String variant = tok.nextToken().trim();
            qopset.add(variant.toLowerCase(Locale.US));
         }

         if(request instanceof HttpEntityEnclosingRequest && qopset.contains("auth-int")) {
            qop = 1;
         } else if(qopset.contains("auth")) {
            qop = 2;
         }
      } else {
         qop = 0;
      }

      if(qop == -1) {
         throw new AuthenticationException("None of the qop methods is supported: " + qoplist);
      } else {
         String charset = this.getParameter("charset");
         if(charset == null) {
            charset = "ISO-8859-1";
         }

         String digAlg = algorithm;
         if(algorithm.equalsIgnoreCase("MD5-sess")) {
            digAlg = "MD5";
         }

         MessageDigest digester;
         try {
            digester = createMessageDigest(digAlg);
         } catch (UnsupportedDigestAlgorithmException var30) {
            throw new AuthenticationException("Unsuppported digest algorithm: " + digAlg);
         }

         String uname = credentials.getUserPrincipal().getName();
         String pwd = credentials.getPassword();
         if(nonce.equals(this.lastNonce)) {
            ++this.nounceCount;
         } else {
            this.nounceCount = 1L;
            this.cnonce = null;
            this.lastNonce = nonce;
         }

         StringBuilder sb = new StringBuilder(256);
         Formatter formatter = new Formatter(sb, Locale.US);
         formatter.format("%08x", new Object[]{Long.valueOf(this.nounceCount)});
         formatter.close();
         String nc = sb.toString();
         if(this.cnonce == null) {
            this.cnonce = createCnonce();
         }

         this.a1 = null;
         this.a2 = null;
         if(algorithm.equalsIgnoreCase("MD5-sess")) {
            sb.setLength(0);
            sb.append(uname).append(':').append(realm).append(':').append(pwd);
            String checksum = encode(digester.digest(EncodingUtils.getBytes(sb.toString(), charset)));
            sb.setLength(0);
            sb.append(checksum).append(':').append(nonce).append(':').append(this.cnonce);
            this.a1 = sb.toString();
         } else {
            sb.setLength(0);
            sb.append(uname).append(':').append(realm).append(':').append(pwd);
            this.a1 = sb.toString();
         }

         String hasha1 = encode(digester.digest(EncodingUtils.getBytes(this.a1, charset)));
         if(qop == 2) {
            this.a2 = method + ':' + uri;
         } else if(qop == 1) {
            HttpEntity entity = null;
            if(request instanceof HttpEntityEnclosingRequest) {
               entity = ((HttpEntityEnclosingRequest)request).getEntity();
            }

            if(entity != null && !entity.isRepeatable()) {
               if(!qopset.contains("auth")) {
                  throw new AuthenticationException("Qop auth-int cannot be used with a non-repeatable entity");
               }

               qop = 2;
               this.a2 = method + ':' + uri;
            } else {
               HttpEntityDigester entityDigester = new HttpEntityDigester(digester);

               try {
                  if(entity != null) {
                     entity.writeTo(entityDigester);
                  }

                  entityDigester.close();
               } catch (IOException var31) {
                  throw new AuthenticationException("I/O error reading entity content", var31);
               }

               this.a2 = method + ':' + uri + ':' + encode(entityDigester.getDigest());
            }
         } else {
            this.a2 = method + ':' + uri;
         }

         String hasha2 = encode(digester.digest(EncodingUtils.getBytes(this.a2, charset)));
         String digestValue;
         if(qop == 0) {
            sb.setLength(0);
            sb.append(hasha1).append(':').append(nonce).append(':').append(hasha2);
            digestValue = sb.toString();
         } else {
            sb.setLength(0);
            sb.append(hasha1).append(':').append(nonce).append(':').append(nc).append(':').append(this.cnonce).append(':').append(qop == 1?"auth-int":"auth").append(':').append(hasha2);
            digestValue = sb.toString();
         }

         String digest = encode(digester.digest(EncodingUtils.getAsciiBytes(digestValue)));
         CharArrayBuffer buffer = new CharArrayBuffer(128);
         if(this.isProxy()) {
            buffer.append("Proxy-Authorization");
         } else {
            buffer.append("Authorization");
         }

         buffer.append(": Digest ");
         List<BasicNameValuePair> params = new ArrayList(20);
         params.add(new BasicNameValuePair("username", uname));
         params.add(new BasicNameValuePair("realm", realm));
         params.add(new BasicNameValuePair("nonce", nonce));
         params.add(new BasicNameValuePair("uri", uri));
         params.add(new BasicNameValuePair("response", digest));
         if(qop != 0) {
            params.add(new BasicNameValuePair("qop", qop == 1?"auth-int":"auth"));
            params.add(new BasicNameValuePair("nc", nc));
            params.add(new BasicNameValuePair("cnonce", this.cnonce));
         }

         params.add(new BasicNameValuePair("algorithm", algorithm));
         if(opaque != null) {
            params.add(new BasicNameValuePair("opaque", opaque));
         }

         for(int i = 0; i < ((List)params).size(); ++i) {
            BasicNameValuePair param = (BasicNameValuePair)params.get(i);
            if(i > 0) {
               buffer.append(", ");
            }

            String name = param.getName();
            boolean noQuotes = "nc".equals(name) || "qop".equals(name) || "algorithm".equals(name);
            BasicHeaderValueFormatter.INSTANCE.formatNameValuePair(buffer, param, !noQuotes);
         }

         return new BufferedHeader(buffer);
      }
   }

   String getCnonce() {
      return this.cnonce;
   }

   String getA1() {
      return this.a1;
   }

   String getA2() {
      return this.a2;
   }

   static String encode(byte[] binaryData) {
      int n = binaryData.length;
      char[] buffer = new char[n * 2];

      for(int i = 0; i < n; ++i) {
         int low = binaryData[i] & 15;
         int high = (binaryData[i] & 240) >> 4;
         buffer[i * 2] = HEXADECIMAL[high];
         buffer[i * 2 + 1] = HEXADECIMAL[low];
      }

      return new String(buffer);
   }

   public static String createCnonce() {
      SecureRandom rnd = new SecureRandom();
      byte[] tmp = new byte[8];
      rnd.nextBytes(tmp);
      return encode(tmp);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("DIGEST [complete=").append(this.complete).append(", nonce=").append(this.lastNonce).append(", nc=").append(this.nounceCount).append("]");
      return builder.toString();
   }
}
