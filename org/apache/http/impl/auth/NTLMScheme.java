package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineImpl;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class NTLMScheme extends AuthSchemeBase {
   private final NTLMEngine engine;
   private NTLMScheme.State state;
   private String challenge;

   public NTLMScheme(NTLMEngine engine) {
      Args.notNull(engine, "NTLM engine");
      this.engine = engine;
      this.state = NTLMScheme.State.UNINITIATED;
      this.challenge = null;
   }

   public NTLMScheme() {
      this(new NTLMEngineImpl());
   }

   public String getSchemeName() {
      return "ntlm";
   }

   public String getParameter(String name) {
      return null;
   }

   public String getRealm() {
      return null;
   }

   public boolean isConnectionBased() {
      return true;
   }

   protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
      this.challenge = buffer.substringTrimmed(beginIndex, endIndex);
      if(this.challenge.length() == 0) {
         if(this.state == NTLMScheme.State.UNINITIATED) {
            this.state = NTLMScheme.State.CHALLENGE_RECEIVED;
         } else {
            this.state = NTLMScheme.State.FAILED;
         }
      } else {
         if(this.state.compareTo(NTLMScheme.State.MSG_TYPE1_GENERATED) < 0) {
            this.state = NTLMScheme.State.FAILED;
            throw new MalformedChallengeException("Out of sequence NTLM response message");
         }

         if(this.state == NTLMScheme.State.MSG_TYPE1_GENERATED) {
            this.state = NTLMScheme.State.MSG_TYPE2_RECEVIED;
         }
      }

   }

   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
      NTCredentials ntcredentials = null;

      try {
         ntcredentials = (NTCredentials)credentials;
      } catch (ClassCastException var6) {
         throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
      }

      String response = null;
      if(this.state == NTLMScheme.State.FAILED) {
         throw new AuthenticationException("NTLM authentication failed");
      } else {
         if(this.state == NTLMScheme.State.CHALLENGE_RECEIVED) {
            response = this.engine.generateType1Msg(ntcredentials.getDomain(), ntcredentials.getWorkstation());
            this.state = NTLMScheme.State.MSG_TYPE1_GENERATED;
         } else {
            if(this.state != NTLMScheme.State.MSG_TYPE2_RECEVIED) {
               throw new AuthenticationException("Unexpected state: " + this.state);
            }

            response = this.engine.generateType3Msg(ntcredentials.getUserName(), ntcredentials.getPassword(), ntcredentials.getDomain(), ntcredentials.getWorkstation(), this.challenge);
            this.state = NTLMScheme.State.MSG_TYPE3_GENERATED;
         }

         CharArrayBuffer buffer = new CharArrayBuffer(32);
         if(this.isProxy()) {
            buffer.append("Proxy-Authorization");
         } else {
            buffer.append("Authorization");
         }

         buffer.append(": NTLM ");
         buffer.append(response);
         return new BufferedHeader(buffer);
      }
   }

   public boolean isComplete() {
      return this.state == NTLMScheme.State.MSG_TYPE3_GENERATED || this.state == NTLMScheme.State.FAILED;
   }

   static enum State {
      UNINITIATED,
      CHALLENGE_RECEIVED,
      MSG_TYPE1_GENERATED,
      MSG_TYPE2_RECEVIED,
      MSG_TYPE3_GENERATED,
      FAILED;
   }
}
