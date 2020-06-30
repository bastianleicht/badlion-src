package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;

public abstract class HttpObjectDecoder extends ReplayingDecoder {
   private final int maxInitialLineLength;
   private final int maxHeaderSize;
   private final int maxChunkSize;
   private final boolean chunkedSupported;
   protected final boolean validateHeaders;
   private final AppendableCharSequence seq;
   private final HttpObjectDecoder.HeaderParser headerParser;
   private final HttpObjectDecoder.LineParser lineParser;
   private HttpMessage message;
   private long chunkSize;
   private int headerSize;
   private long contentLength;

   protected HttpObjectDecoder() {
      this(4096, 8192, 8192, true);
   }

   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
      this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
   }

   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
      super(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
      this.seq = new AppendableCharSequence(128);
      this.headerParser = new HttpObjectDecoder.HeaderParser(this.seq);
      this.lineParser = new HttpObjectDecoder.LineParser(this.seq);
      this.contentLength = Long.MIN_VALUE;
      if(maxInitialLineLength <= 0) {
         throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + maxInitialLineLength);
      } else if(maxHeaderSize <= 0) {
         throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + maxHeaderSize);
      } else if(maxChunkSize <= 0) {
         throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
      } else {
         this.maxInitialLineLength = maxInitialLineLength;
         this.maxHeaderSize = maxHeaderSize;
         this.maxChunkSize = maxChunkSize;
         this.chunkedSupported = chunkedSupported;
         this.validateHeaders = validateHeaders;
      }
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List out) throws Exception {
      switch((HttpObjectDecoder.State)this.state()) {
      case SKIP_CONTROL_CHARS:
         try {
            skipControlCharacters(buffer);
            this.checkpoint(HttpObjectDecoder.State.READ_INITIAL);
         } finally {
            this.checkpoint();
         }
      case READ_INITIAL:
         try {
            String[] initialLine = splitInitialLine(this.lineParser.parse(buffer));
            if(initialLine.length < 3) {
               this.checkpoint(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
               return;
            }

            this.message = this.createMessage(initialLine);
            this.checkpoint(HttpObjectDecoder.State.READ_HEADER);
         } catch (Exception var14) {
            out.add(this.invalidMessage(var14));
            return;
         }
      case READ_HEADER:
         try {
            HttpObjectDecoder.State nextState = this.readHeaders(buffer);
            this.checkpoint(nextState);
            if(nextState == HttpObjectDecoder.State.READ_CHUNK_SIZE) {
               if(!this.chunkedSupported) {
                  throw new IllegalArgumentException("Chunked messages not supported");
               }

               out.add(this.message);
               return;
            }

            if(nextState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS) {
               out.add(this.message);
               out.add(LastHttpContent.EMPTY_LAST_CONTENT);
               this.reset();
               return;
            }

            long contentLength = this.contentLength();
            if(contentLength != 0L && (contentLength != -1L || !this.isDecodingRequest())) {
               assert nextState == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT || nextState == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;

               out.add(this.message);
               if(nextState == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT) {
                  this.chunkSize = contentLength;
               }

               return;
            }

            out.add(this.message);
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            this.reset();
            return;
         } catch (Exception var16) {
            out.add(this.invalidMessage(var16));
            return;
         }
      case READ_VARIABLE_LENGTH_CONTENT:
         int toRead = Math.min(this.actualReadableBytes(), this.maxChunkSize);
         if(toRead > 0) {
            ByteBuf content = ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead);
            if(buffer.isReadable()) {
               out.add(new DefaultHttpContent(content));
            } else {
               out.add(new DefaultLastHttpContent(content, this.validateHeaders));
               this.reset();
            }
         } else if(!buffer.isReadable()) {
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            this.reset();
         }

         return;
      case READ_FIXED_LENGTH_CONTENT:
         int readLimit = this.actualReadableBytes();
         if(readLimit == 0) {
            return;
         }

         int toRead = Math.min(readLimit, this.maxChunkSize);
         if((long)toRead > this.chunkSize) {
            toRead = (int)this.chunkSize;
         }

         ByteBuf content = ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead);
         this.chunkSize -= (long)toRead;
         if(this.chunkSize == 0L) {
            out.add(new DefaultLastHttpContent(content, this.validateHeaders));
            this.reset();
         } else {
            out.add(new DefaultHttpContent(content));
         }

         return;
      case READ_CHUNK_SIZE:
         try {
            AppendableCharSequence line = this.lineParser.parse(buffer);
            int chunkSize = getChunkSize(line.toString());
            this.chunkSize = (long)chunkSize;
            if(chunkSize == 0) {
               this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_FOOTER);
               return;
            }

            this.checkpoint(HttpObjectDecoder.State.READ_CHUNKED_CONTENT);
         } catch (Exception var13) {
            out.add(this.invalidChunk(var13));
            return;
         }
      case READ_CHUNKED_CONTENT:
         assert this.chunkSize <= 2147483647L;

         int toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
         HttpContent chunk = new DefaultHttpContent(ByteBufUtil.readBytes(ctx.alloc(), buffer, toRead));
         this.chunkSize -= (long)toRead;
         out.add(chunk);
         if(this.chunkSize != 0L) {
            return;
         }

         this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_DELIMITER);
      case READ_CHUNK_DELIMITER:
         while(true) {
            byte next = buffer.readByte();
            if(next == 13) {
               if(buffer.readByte() == 10) {
                  break;
               }
            } else {
               if(next == 10) {
                  this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_SIZE);
                  return;
               }

               this.checkpoint();
            }
         }

         this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_SIZE);
         return;
      case READ_CHUNK_FOOTER:
         try {
            LastHttpContent trailer = this.readTrailingHeaders(buffer);
            out.add(trailer);
            this.reset();
            return;
         } catch (Exception var12) {
            out.add(this.invalidChunk(var12));
            return;
         }
      case BAD_MESSAGE:
         buffer.skipBytes(this.actualReadableBytes());
         break;
      case UPGRADED:
         int readableBytes = this.actualReadableBytes();
         if(readableBytes > 0) {
            out.add(buffer.readBytes(this.actualReadableBytes()));
         }
      }

   }

   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
      this.decode(ctx, in, out);
      if(this.message != null) {
         boolean prematureClosure;
         if(this.isDecodingRequest()) {
            prematureClosure = true;
         } else {
            prematureClosure = this.contentLength() > 0L;
         }

         this.reset();
         if(!prematureClosure) {
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
         }
      }

   }

   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      if(msg instanceof HttpResponse) {
         HttpResponse res = (HttpResponse)msg;
         int code = res.getStatus().code();
         if(code >= 100 && code < 200) {
            return code != 101 || res.headers().contains("Sec-WebSocket-Accept");
         }

         switch(code) {
         case 204:
         case 205:
         case 304:
            return true;
         }
      }

      return false;
   }

   private void reset() {
      HttpMessage message = this.message;
      this.message = null;
      this.contentLength = Long.MIN_VALUE;
      if(!this.isDecodingRequest()) {
         HttpResponse res = (HttpResponse)message;
         if(res != null && res.getStatus().code() == 101) {
            this.checkpoint(HttpObjectDecoder.State.UPGRADED);
            return;
         }
      }

      this.checkpoint(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
   }

   private HttpMessage invalidMessage(Exception cause) {
      this.checkpoint(HttpObjectDecoder.State.BAD_MESSAGE);
      if(this.message != null) {
         this.message.setDecoderResult(DecoderResult.failure(cause));
      } else {
         this.message = this.createInvalidMessage();
         this.message.setDecoderResult(DecoderResult.failure(cause));
      }

      HttpMessage ret = this.message;
      this.message = null;
      return ret;
   }

   private HttpContent invalidChunk(Exception cause) {
      this.checkpoint(HttpObjectDecoder.State.BAD_MESSAGE);
      HttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      chunk.setDecoderResult(DecoderResult.failure(cause));
      this.message = null;
      return chunk;
   }

   private static void skipControlCharacters(ByteBuf buffer) {
      while(true) {
         char c = (char)buffer.readUnsignedByte();
         if(!Character.isISOControl(c) && !Character.isWhitespace(c)) {
            break;
         }
      }

      buffer.readerIndex(buffer.readerIndex() - 1);
   }

   private HttpObjectDecoder.State readHeaders(ByteBuf buffer) {
      this.headerSize = 0;
      HttpMessage message = this.message;
      HttpHeaders headers = message.headers();
      AppendableCharSequence line = this.headerParser.parse(buffer);
      String name = null;
      String value = null;
      if(line.length() > 0) {
         headers.clear();

         while(true) {
            char firstChar = line.charAt(0);
            if(name == null || firstChar != 32 && firstChar != 9) {
               if(name != null) {
                  headers.add((String)name, (Object)value);
               }

               String[] header = splitHeader(line);
               name = header[0];
               value = header[1];
            } else {
               value = value + ' ' + line.toString().trim();
            }

            line = this.headerParser.parse(buffer);
            if(line.length() <= 0) {
               break;
            }
         }

         if(name != null) {
            headers.add((String)name, (Object)value);
         }
      }

      HttpObjectDecoder.State nextState;
      if(this.isContentAlwaysEmpty(message)) {
         HttpHeaders.removeTransferEncodingChunked(message);
         nextState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
      } else if(HttpHeaders.isTransferEncodingChunked(message)) {
         nextState = HttpObjectDecoder.State.READ_CHUNK_SIZE;
      } else if(this.contentLength() >= 0L) {
         nextState = HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT;
      } else {
         nextState = HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;
      }

      return nextState;
   }

   private long contentLength() {
      if(this.contentLength == Long.MIN_VALUE) {
         this.contentLength = HttpHeaders.getContentLength(this.message, -1L);
      }

      return this.contentLength;
   }

   private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
      this.headerSize = 0;
      AppendableCharSequence line = this.headerParser.parse(buffer);
      String lastHeader = null;
      if(line.length() <= 0) {
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         LastHttpContent trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);

         while(true) {
            char firstChar = line.charAt(0);
            if(lastHeader != null && (firstChar == 32 || firstChar == 9)) {
               List<String> current = trailer.trailingHeaders().getAll(lastHeader);
               if(!current.isEmpty()) {
                  int lastPos = current.size() - 1;
                  String newString = (String)current.get(lastPos) + line.toString().trim();
                  current.set(lastPos, newString);
               }
            } else {
               String[] header = splitHeader(line);
               String name = header[0];
               if(!HttpHeaders.equalsIgnoreCase(name, "Content-Length") && !HttpHeaders.equalsIgnoreCase(name, "Transfer-Encoding") && !HttpHeaders.equalsIgnoreCase(name, "Trailer")) {
                  trailer.trailingHeaders().add((String)name, (Object)header[1]);
               }

               lastHeader = name;
            }

            line = this.headerParser.parse(buffer);
            if(line.length() <= 0) {
               break;
            }
         }

         return trailer;
      }
   }

   protected abstract boolean isDecodingRequest();

   protected abstract HttpMessage createMessage(String[] var1) throws Exception;

   protected abstract HttpMessage createInvalidMessage();

   private static int getChunkSize(String hex) {
      hex = hex.trim();

      for(int i = 0; i < hex.length(); ++i) {
         char c = hex.charAt(i);
         if(c == 59 || Character.isWhitespace(c) || Character.isISOControl(c)) {
            hex = hex.substring(0, i);
            break;
         }
      }

      return Integer.parseInt(hex, 16);
   }

   private static String[] splitInitialLine(AppendableCharSequence sb) {
      int aStart = findNonWhitespace(sb, 0);
      int aEnd = findWhitespace(sb, aStart);
      int bStart = findNonWhitespace(sb, aEnd);
      int bEnd = findWhitespace(sb, bStart);
      int cStart = findNonWhitespace(sb, bEnd);
      int cEnd = findEndOfString(sb);
      return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), cStart < cEnd?sb.substring(cStart, cEnd):""};
   }

   private static String[] splitHeader(AppendableCharSequence sb) {
      int length = sb.length();
      int nameStart = findNonWhitespace(sb, 0);

      int nameEnd;
      for(nameEnd = nameStart; nameEnd < length; ++nameEnd) {
         char ch = sb.charAt(nameEnd);
         if(ch == 58 || Character.isWhitespace(ch)) {
            break;
         }
      }

      int colonEnd;
      for(colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
         if(sb.charAt(colonEnd) == 58) {
            ++colonEnd;
            break;
         }
      }

      int valueStart = findNonWhitespace(sb, colonEnd);
      if(valueStart == length) {
         return new String[]{sb.substring(nameStart, nameEnd), ""};
      } else {
         int valueEnd = findEndOfString(sb);
         return new String[]{sb.substring(nameStart, nameEnd), sb.substring(valueStart, valueEnd)};
      }
   }

   private static int findNonWhitespace(CharSequence sb, int offset) {
      int result;
      for(result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {
         ;
      }

      return result;
   }

   private static int findWhitespace(CharSequence sb, int offset) {
      int result;
      for(result = offset; result < sb.length() && !Character.isWhitespace(sb.charAt(result)); ++result) {
         ;
      }

      return result;
   }

   private static int findEndOfString(CharSequence sb) {
      int result;
      for(result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {
         ;
      }

      return result;
   }

   private final class HeaderParser implements ByteBufProcessor {
      private final AppendableCharSequence seq;

      HeaderParser(AppendableCharSequence seq) {
         this.seq = seq;
      }

      public AppendableCharSequence parse(ByteBuf buffer) {
         this.seq.reset();
         HttpObjectDecoder.this.headerSize = 0;
         int i = buffer.forEachByte(this);
         buffer.readerIndex(i + 1);
         return this.seq;
      }

      public boolean process(byte value) throws Exception {
         char nextByte = (char)value;
         HttpObjectDecoder.this.headerSize++;
         if(nextByte == 13) {
            return true;
         } else if(nextByte == 10) {
            return false;
         } else if(HttpObjectDecoder.this.headerSize >= HttpObjectDecoder.this.maxHeaderSize) {
            throw new TooLongFrameException("HTTP header is larger than " + HttpObjectDecoder.this.maxHeaderSize + " bytes.");
         } else {
            this.seq.append(nextByte);
            return true;
         }
      }
   }

   private final class LineParser implements ByteBufProcessor {
      private final AppendableCharSequence seq;
      private int size;

      LineParser(AppendableCharSequence seq) {
         this.seq = seq;
      }

      public AppendableCharSequence parse(ByteBuf buffer) {
         this.seq.reset();
         this.size = 0;
         int i = buffer.forEachByte(this);
         buffer.readerIndex(i + 1);
         return this.seq;
      }

      public boolean process(byte value) throws Exception {
         char nextByte = (char)value;
         if(nextByte == 13) {
            return true;
         } else if(nextByte == 10) {
            return false;
         } else if(this.size >= HttpObjectDecoder.this.maxInitialLineLength) {
            throw new TooLongFrameException("An HTTP line is larger than " + HttpObjectDecoder.this.maxInitialLineLength + " bytes.");
         } else {
            ++this.size;
            this.seq.append(nextByte);
            return true;
         }
      }
   }

   static enum State {
      SKIP_CONTROL_CHARS,
      READ_INITIAL,
      READ_HEADER,
      READ_VARIABLE_LENGTH_CONTENT,
      READ_FIXED_LENGTH_CONTENT,
      READ_CHUNK_SIZE,
      READ_CHUNKED_CONTENT,
      READ_CHUNK_DELIMITER,
      READ_CHUNK_FOOTER,
      BAD_MESSAGE,
      UPGRADED;
   }
}
