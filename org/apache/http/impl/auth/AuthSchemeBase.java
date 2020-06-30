package org.apache.http.impl.auth;

import java.util.Locale;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public abstract class AuthSchemeBase implements ContextAwareAuthScheme {
   private ChallengeState challengeState;

   /** @deprecated */
   @Deprecated
   public AuthSchemeBase(ChallengeState challengeState) {
      this.challengeState = challengeState;
   }

   public AuthSchemeBase() {
   }

   public void processChallenge(Header header) throws MalformedChallengeException {
      Args.notNull(header, "Header");
      String authheader = header.getName();
      if(authheader.equalsIgnoreCase("WWW-Authenticate")) {
         this.challengeState = ChallengeState.TARGET;
      } else {
         if(!authheader.equalsIgnoreCase("Proxy-Authenticate")) {
            throw new MalformedChallengeException("Unexpected header name: " + authheader);
         }

         this.challengeState = ChallengeState.PROXY;
      }

      CharArrayBuffer buffer;
      int pos;
      if(header instanceof FormattedHeader) {
         buffer = ((FormattedHeader)header).getBuffer();
         pos = ((FormattedHeader)header).getValuePos();
      } else {
         String s = header.getValue();
         if(s == null) {
            throw new MalformedChallengeException("Header value is null");
         }

         buffer = new CharArrayBuffer(s.length());
         buffer.append(s);
         pos = 0;
      }

      while(pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
         ++pos;
      }

      int beginIndex;
      for(beginIndex = pos; pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos)); ++pos) {
         ;
      }

      String s = buffer.substring(beginIndex, pos);
      if(!s.equalsIgnoreCase(this.getSchemeName())) {
         throw new MalformedChallengeException("Invalid scheme identifier: " + s);
      } else {
         this.parseChallenge(buffer, pos, buffer.length());
      }
   }

   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
      return this.authenticate(credentials, request);
   }

   protected abstract void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException;

   public boolean isProxy() {
      return this.challengeState != null && this.challengeState == ChallengeState.PROXY;
   }

   public ChallengeState getChallengeState() {
      return this.challengeState;
   }

   public String toString() {
      String name = this.getSchemeName();
      return name != null?name.toUpperCase(Locale.US):super.toString();
   }
}
