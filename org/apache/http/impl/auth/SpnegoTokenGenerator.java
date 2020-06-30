package org.apache.http.impl.auth;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface SpnegoTokenGenerator {
   byte[] generateSpnegoDERObject(byte[] var1) throws IOException;
}
