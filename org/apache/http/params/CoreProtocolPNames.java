package org.apache.http.params;

/** @deprecated */
@Deprecated
public interface CoreProtocolPNames {
   String PROTOCOL_VERSION = "http.protocol.version";
   String HTTP_ELEMENT_CHARSET = "http.protocol.element-charset";
   String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";
   String USER_AGENT = "http.useragent";
   String ORIGIN_SERVER = "http.origin-server";
   String STRICT_TRANSFER_ENCODING = "http.protocol.strict-transfer-encoding";
   String USE_EXPECT_CONTINUE = "http.protocol.expect-continue";
   String WAIT_FOR_CONTINUE = "http.protocol.wait-for-continue";
   String HTTP_MALFORMED_INPUT_ACTION = "http.malformed.input.action";
   String HTTP_UNMAPPABLE_INPUT_ACTION = "http.unmappable.input.action";
}
