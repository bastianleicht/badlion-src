package org.apache.http.impl.auth;

import org.apache.http.impl.auth.NTLMEngineException;

public interface NTLMEngine {
   String generateType1Msg(String var1, String var2) throws NTLMEngineException;

   String generateType3Msg(String var1, String var2, String var3, String var4, String var5) throws NTLMEngineException;
}
