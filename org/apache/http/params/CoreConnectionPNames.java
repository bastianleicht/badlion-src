package org.apache.http.params;

/** @deprecated */
@Deprecated
public interface CoreConnectionPNames {
   String SO_TIMEOUT = "http.socket.timeout";
   String TCP_NODELAY = "http.tcp.nodelay";
   String SOCKET_BUFFER_SIZE = "http.socket.buffer-size";
   String SO_LINGER = "http.socket.linger";
   String SO_REUSEADDR = "http.socket.reuseaddr";
   String CONNECTION_TIMEOUT = "http.connection.timeout";
   String STALE_CONNECTION_CHECK = "http.connection.stalecheck";
   String MAX_LINE_LENGTH = "http.connection.max-line-length";
   String MAX_HEADER_COUNT = "http.connection.max-header-count";
   String MIN_CHUNK_LIMIT = "http.connection.min-chunk-limit";
   String SO_KEEPALIVE = "http.socket.keepalive";
}
