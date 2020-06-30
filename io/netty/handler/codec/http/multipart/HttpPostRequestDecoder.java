package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostRequestDecoder {
   private static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private boolean bodyToDecode;
   private boolean isLastChunk;
   private final List bodyListHttpData;
   private final Map bodyMapHttpData;
   private ByteBuf undecodedChunk;
   private boolean isMultipart;
   private int bodyListHttpDataRank;
   private String multipartDataBoundary;
   private String multipartMixedBoundary;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus;
   private Map currentFieldAttributes;
   private FileUpload currentFileUpload;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold;

   public HttpPostRequestDecoder(HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
      this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
      this(factory, request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException, HttpPostRequestDecoder.IncompatibleDataDecoderException {
      this.bodyListHttpData = new ArrayList();
      this.bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
      this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
      this.discardThreshold = 10485760;
      if(factory == null) {
         throw new NullPointerException("factory");
      } else if(request == null) {
         throw new NullPointerException("request");
      } else if(charset == null) {
         throw new NullPointerException("charset");
      } else {
         this.request = request;
         HttpMethod method = request.getMethod();
         if(method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
            this.bodyToDecode = true;
         }

         this.charset = charset;
         this.factory = factory;
         String contentType = this.request.headers().get("Content-Type");
         if(contentType != null) {
            this.checkMultipart(contentType);
         } else {
            this.isMultipart = false;
         }

         if(!this.bodyToDecode) {
            throw new HttpPostRequestDecoder.IncompatibleDataDecoderException("No Body to decode");
         } else {
            if(request instanceof HttpContent) {
               this.offer((HttpContent)request);
            } else {
               this.undecodedChunk = Unpooled.buffer();
               this.parseBody();
            }

         }
      }
   }

   private void checkMultipart(String contentType) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      String[] headerContentType = splitHeaderContentType(contentType);
      if(headerContentType[0].toLowerCase().startsWith("multipart/form-data") && headerContentType[1].toLowerCase().startsWith("boundary")) {
         String[] boundary = StringUtil.split(headerContentType[1], '=');
         if(boundary.length != 2) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Needs a boundary value");
         }

         if(boundary[1].charAt(0) == 34) {
            String bound = boundary[1].trim();
            int index = bound.length() - 1;
            if(bound.charAt(index) == 34) {
               boundary[1] = bound.substring(1, index);
            }
         }

         this.multipartDataBoundary = "--" + boundary[1];
         this.isMultipart = true;
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
      } else {
         this.isMultipart = false;
      }

   }

   private void checkDestroyed() {
      if(this.destroyed) {
         throw new IllegalStateException(HttpPostRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   public boolean isMultipart() {
      this.checkDestroyed();
      return this.isMultipart;
   }

   public void setDiscardThreshold(int discardThreshold) {
      if(discardThreshold < 0) {
         throw new IllegalArgumentException("discardThreshold must be >= 0");
      } else {
         this.discardThreshold = discardThreshold;
      }
   }

   public int getDiscardThreshold() {
      return this.discardThreshold;
   }

   public List getBodyHttpDatas() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      this.checkDestroyed();
      if(!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyListHttpData;
      }
   }

   public List getBodyHttpDatas(String name) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      this.checkDestroyed();
      if(!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return (List)this.bodyMapHttpData.get(name);
      }
   }

   public InterfaceHttpData getBodyHttpData(String name) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      this.checkDestroyed();
      if(!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         List<InterfaceHttpData> list = (List)this.bodyMapHttpData.get(name);
         return list != null?(InterfaceHttpData)list.get(0):null;
      }
   }

   public HttpPostRequestDecoder offer(HttpContent content) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      this.checkDestroyed();
      ByteBuf buf = content.content();
      if(this.undecodedChunk == null) {
         this.undecodedChunk = buf.copy();
      } else {
         this.undecodedChunk.writeBytes(buf);
      }

      if(content instanceof LastHttpContent) {
         this.isLastChunk = true;
      }

      this.parseBody();
      if(this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
         this.undecodedChunk.discardReadBytes();
      }

      return this;
   }

   public boolean hasNext() throws HttpPostRequestDecoder.EndOfDataDecoderException {
      this.checkDestroyed();
      if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
      } else {
         return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
      }
   }

   public InterfaceHttpData next() throws HttpPostRequestDecoder.EndOfDataDecoderException {
      this.checkDestroyed();
      return this.hasNext()?(InterfaceHttpData)this.bodyListHttpData.get(this.bodyListHttpDataRank++):null;
   }

   private void parseBody() throws HttpPostRequestDecoder.ErrorDataDecoderException {
      if(this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         if(this.isMultipart) {
            this.parseBodyMultipart();
         } else {
            this.parseBodyAttributes();
         }

      } else {
         if(this.isLastChunk) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         }

      }
   }

   protected void addHttpData(InterfaceHttpData data) {
      if(data != null) {
         List<InterfaceHttpData> datas = (List)this.bodyMapHttpData.get(data.getName());
         if(datas == null) {
            datas = new ArrayList(1);
            this.bodyMapHttpData.put(data.getName(), datas);
         }

         datas.add(data);
         this.bodyListHttpData.add(data);
      }
   }

   private void parseBodyAttributesStandard() throws HttpPostRequestDecoder.ErrorDataDecoderException {
      int firstpos = this.undecodedChunk.readerIndex();
      int currentpos = firstpos;
      if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
      }

      boolean contRead = true;

      try {
         while(this.undecodedChunk.isReadable() && contRead) {
            char read = (char)this.undecodedChunk.readUnsignedByte();
            ++currentpos;
            switch(this.currentStatus) {
            case DISPOSITION:
               if(read == 61) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                  int equalpos = currentpos - 1;
                  String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, key);
                  firstpos = currentpos;
               } else if(read == 38) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  int ampersandpos = currentpos - 1;
                  String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, key);
                  this.currentAttribute.setValue("");
                  this.addHttpData(this.currentAttribute);
                  this.currentAttribute = null;
                  firstpos = currentpos;
                  contRead = true;
               }
               break;
            case FIELD:
               if(read == 38) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  int ampersandpos = currentpos - 1;
                  this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                  firstpos = currentpos;
                  contRead = true;
               } else if(read == 13) {
                  if(this.undecodedChunk.isReadable()) {
                     read = (char)this.undecodedChunk.readUnsignedByte();
                     ++currentpos;
                     if(read != 10) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                     }

                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                     int ampersandpos = currentpos - 2;
                     this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                     firstpos = currentpos;
                     contRead = false;
                  } else {
                     --currentpos;
                  }
               } else if(read == 10) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                  int ampersandpos = currentpos - 1;
                  this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                  firstpos = currentpos;
                  contRead = false;
               }
               break;
            default:
               contRead = false;
            }
         }

         if(this.isLastChunk && this.currentAttribute != null) {
            if(currentpos > firstpos) {
               this.setFinalBuffer(this.undecodedChunk.copy(firstpos, currentpos - firstpos));
            } else if(!this.currentAttribute.isCompleted()) {
               this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
            }

            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            this.undecodedChunk.readerIndex(currentpos);
         } else {
            if(contRead && this.currentAttribute != null) {
               if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                  this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                  firstpos = currentpos;
               }

               this.undecodedChunk.readerIndex(firstpos);
            } else {
               this.undecodedChunk.readerIndex(firstpos);
            }

         }
      } catch (HttpPostRequestDecoder.ErrorDataDecoderException var8) {
         this.undecodedChunk.readerIndex(firstpos);
         throw var8;
      } catch (IOException var9) {
         this.undecodedChunk.readerIndex(firstpos);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
      }
   }

   private void parseBodyAttributes() throws HttpPostRequestDecoder.ErrorDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var9) {
         this.parseBodyAttributesStandard();
         return;
      }

      int firstpos = this.undecodedChunk.readerIndex();
      int currentpos = firstpos;
      if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
      }

      boolean contRead = true;

      try {
         label62:
         while(sao.pos < sao.limit) {
            char read = (char)(sao.bytes[sao.pos++] & 255);
            ++currentpos;
            switch(this.currentStatus) {
            case DISPOSITION:
               if(read == 61) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                  int equalpos = currentpos - 1;
                  String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, key);
                  firstpos = currentpos;
               } else if(read == 38) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  int ampersandpos = currentpos - 1;
                  String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, key);
                  this.currentAttribute.setValue("");
                  this.addHttpData(this.currentAttribute);
                  this.currentAttribute = null;
                  firstpos = currentpos;
                  contRead = true;
               }
               break;
            case FIELD:
               if(read == 38) {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  int ampersandpos = currentpos - 1;
                  this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                  firstpos = currentpos;
                  contRead = true;
               } else if(read == 13) {
                  if(sao.pos < sao.limit) {
                     read = (char)(sao.bytes[sao.pos++] & 255);
                     ++currentpos;
                     if(read != 10) {
                        sao.setReadPosition(0);
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                     }

                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                     int ampersandpos = currentpos - 2;
                     sao.setReadPosition(0);
                     this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                     firstpos = currentpos;
                     contRead = false;
                     break label62;
                  }

                  if(sao.limit > 0) {
                     --currentpos;
                  }
               } else {
                  if(read != 10) {
                     continue;
                  }

                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                  int ampersandpos = currentpos - 1;
                  sao.setReadPosition(0);
                  this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                  firstpos = currentpos;
                  contRead = false;
                  break label62;
               }
               break;
            default:
               sao.setReadPosition(0);
               contRead = false;
               break label62;
            }
         }

         if(this.isLastChunk && this.currentAttribute != null) {
            if(currentpos > firstpos) {
               this.setFinalBuffer(this.undecodedChunk.copy(firstpos, currentpos - firstpos));
            } else if(!this.currentAttribute.isCompleted()) {
               this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
            }

            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            this.undecodedChunk.readerIndex(currentpos);
         } else {
            if(contRead && this.currentAttribute != null) {
               if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                  this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                  firstpos = currentpos;
               }

               this.undecodedChunk.readerIndex(firstpos);
            } else {
               this.undecodedChunk.readerIndex(firstpos);
            }

         }
      } catch (HttpPostRequestDecoder.ErrorDataDecoderException var10) {
         this.undecodedChunk.readerIndex(firstpos);
         throw var10;
      } catch (IOException var11) {
         this.undecodedChunk.readerIndex(firstpos);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
      }
   }

   private void setFinalBuffer(ByteBuf buffer) throws HttpPostRequestDecoder.ErrorDataDecoderException, IOException {
      this.currentAttribute.addContent(buffer, true);
      String value = decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);
      this.currentAttribute.setValue(value);
      this.addHttpData(this.currentAttribute);
      this.currentAttribute = null;
   }

   private static String decodeAttribute(String s, Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      try {
         return QueryStringDecoder.decodeComponent(s, charset);
      } catch (IllegalArgumentException var3) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: \'" + s + '\'', var3);
      }
   }

   private void parseBodyMultipart() throws HttpPostRequestDecoder.ErrorDataDecoderException {
      if(this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
         for(InterfaceHttpData data = this.decodeMultipart(this.currentStatus); data != null; data = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(data);
            if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
               break;
            }
         }

      }
   }

   private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      switch(state) {
      case DISPOSITION:
         return this.findMultipartDisposition();
      case FIELD:
         Charset localCharset = null;
         Attribute charsetAttribute = (Attribute)this.currentFieldAttributes.get("charset");
         if(charsetAttribute != null) {
            try {
               localCharset = Charset.forName(charsetAttribute.getValue());
            } catch (IOException var10) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
            }
         }

         Attribute nameAttribute = (Attribute)this.currentFieldAttributes.get("name");
         if(this.currentAttribute == null) {
            try {
               this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()));
            } catch (NullPointerException var7) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var7);
            } catch (IllegalArgumentException var8) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var8);
            } catch (IOException var9) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
            }

            if(localCharset != null) {
               this.currentAttribute.setCharset(localCharset);
            }
         }

         try {
            this.loadFieldMultipart(this.multipartDataBoundary);
         } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var6) {
            return null;
         }

         Attribute finalAttribute = this.currentAttribute;
         this.currentAttribute = null;
         this.currentFieldAttributes = null;
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
         return finalAttribute;
      case NOTSTARTED:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
      case PREAMBLE:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
      case HEADERDELIMITER:
         return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
      case FILEUPLOAD:
         return this.getFileUpload(this.multipartDataBoundary);
      case MIXEDDELIMITER:
         return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
      case MIXEDDISPOSITION:
         return this.findMultipartDisposition();
      case MIXEDFILEUPLOAD:
         return this.getFileUpload(this.multipartMixedBoundary);
      case PREEPILOGUE:
         return null;
      case EPILOGUE:
         return null;
      default:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn\'t reach here.");
      }
   }

   void skipControlCharacters() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var5) {
         try {
            this.skipControlCharactersStandard();
            return;
         } catch (IndexOutOfBoundsException var4) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var4);
         }
      }

      while(sao.pos < sao.limit) {
         char c = (char)(sao.bytes[sao.pos++] & 255);
         if(!Character.isISOControl(c) && !Character.isWhitespace(c)) {
            sao.setReadPosition(1);
            return;
         }
      }

      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
   }

   void skipControlCharactersStandard() {
      while(true) {
         char c = (char)this.undecodedChunk.readUnsignedByte();
         if(!Character.isISOControl(c) && !Character.isWhitespace(c)) {
            break;
         }
      }

      this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
   }

   private InterfaceHttpData findMultipartDelimiter(String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         this.skipControlCharacters();
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var8) {
         this.undecodedChunk.readerIndex(readerIndex);
         return null;
      }

      this.skipOneLine();

      String newline;
      try {
         newline = this.readDelimiter(delimiter);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var7) {
         this.undecodedChunk.readerIndex(readerIndex);
         return null;
      }

      if(newline.equals(delimiter)) {
         this.currentStatus = dispositionStatus;
         return this.decodeMultipart(dispositionStatus);
      } else if(newline.equals(delimiter + "--")) {
         this.currentStatus = closeDelimiterStatus;
         if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
         } else {
            return null;
         }
      } else {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
      }
   }

   private InterfaceHttpData findMultipartDisposition() throws HttpPostRequestDecoder.ErrorDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();
      if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         this.currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
      }

      while(!this.skipOneLine()) {
         String newline;
         try {
            this.skipControlCharacters();
            newline = this.readLine();
         } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var20) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
         }

         String[] contents = splitMultipartHeader(newline);
         if(!contents[0].equalsIgnoreCase("Content-Disposition")) {
            if(contents[0].equalsIgnoreCase("Content-Transfer-Encoding")) {
               Attribute attribute;
               try {
                  attribute = this.factory.createAttribute(this.request, "Content-Transfer-Encoding", cleanString(contents[1]));
               } catch (NullPointerException var16) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
               } catch (IllegalArgumentException var17) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var17);
               }

               this.currentFieldAttributes.put("Content-Transfer-Encoding", attribute);
            } else if(contents[0].equalsIgnoreCase("Content-Length")) {
               Attribute attribute;
               try {
                  attribute = this.factory.createAttribute(this.request, "Content-Length", cleanString(contents[1]));
               } catch (NullPointerException var14) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
               } catch (IllegalArgumentException var15) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
               }

               this.currentFieldAttributes.put("Content-Length", attribute);
            } else {
               if(!contents[0].equalsIgnoreCase("Content-Type")) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + newline);
               }

               if(contents[1].equalsIgnoreCase("multipart/mixed")) {
                  if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                     String[] values = StringUtil.split(contents[2], '=');
                     this.multipartMixedBoundary = "--" + values[1];
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                     return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                  }

                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
               }

               for(int i = 1; i < contents.length; ++i) {
                  if(contents[i].toLowerCase().startsWith("charset")) {
                     String[] values = StringUtil.split(contents[i], '=');

                     Attribute attribute;
                     try {
                        attribute = this.factory.createAttribute(this.request, "charset", cleanString(values[1]));
                     } catch (NullPointerException var12) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
                     } catch (IllegalArgumentException var13) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
                     }

                     this.currentFieldAttributes.put("charset", attribute);
                  } else {
                     Attribute attribute;
                     try {
                        attribute = this.factory.createAttribute(this.request, cleanString(contents[0]), contents[i]);
                     } catch (NullPointerException var10) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
                     } catch (IllegalArgumentException var11) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                     }

                     this.currentFieldAttributes.put(attribute.getName(), attribute);
                  }
               }
            }
         } else {
            boolean checkSecondArg;
            if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
               checkSecondArg = contents[1].equalsIgnoreCase("form-data");
            } else {
               checkSecondArg = contents[1].equalsIgnoreCase("attachment") || contents[1].equalsIgnoreCase("file");
            }

            if(checkSecondArg) {
               for(int i = 2; i < contents.length; ++i) {
                  String[] values = StringUtil.split(contents[i], '=');

                  Attribute attribute;
                  try {
                     String name = cleanString(values[0]);
                     String value = values[1];
                     if("filename".equals(name)) {
                        value = value.substring(1, value.length() - 1);
                     } else {
                        value = cleanString(value);
                     }

                     attribute = this.factory.createAttribute(this.request, name, value);
                  } catch (NullPointerException var18) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var18);
                  } catch (IllegalArgumentException var19) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var19);
                  }

                  this.currentFieldAttributes.put(attribute.getName(), attribute);
               }
            }
         }
      }

      Attribute filenameAttribute = (Attribute)this.currentFieldAttributes.get("filename");
      if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         if(filenameAttribute != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
         }
      } else if(filenameAttribute != null) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
         return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
      } else {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
      }
   }

   protected InterfaceHttpData getFileUpload(String delimiter) throws HttpPostRequestDecoder.ErrorDataDecoderException {
      Attribute encoding = (Attribute)this.currentFieldAttributes.get("Content-Transfer-Encoding");
      Charset localCharset = this.charset;
      HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
      if(encoding != null) {
         String code;
         try {
            code = encoding.getValue().toLowerCase();
         } catch (IOException var20) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var20);
         }

         if(code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
            localCharset = HttpPostBodyUtil.US_ASCII;
         } else if(code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
            localCharset = HttpPostBodyUtil.ISO_8859_1;
            mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
         } else {
            if(!code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
            }

            mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
         }
      }

      Attribute charsetAttribute = (Attribute)this.currentFieldAttributes.get("charset");
      if(charsetAttribute != null) {
         try {
            localCharset = Charset.forName(charsetAttribute.getValue());
         } catch (IOException var19) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var19);
         }
      }

      if(this.currentFileUpload == null) {
         Attribute filenameAttribute = (Attribute)this.currentFieldAttributes.get("filename");
         Attribute nameAttribute = (Attribute)this.currentFieldAttributes.get("name");
         Attribute contentTypeAttribute = (Attribute)this.currentFieldAttributes.get("Content-Type");
         if(contentTypeAttribute == null) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Content-Type is absent but required");
         }

         Attribute lengthAttribute = (Attribute)this.currentFieldAttributes.get("Content-Length");

         long size;
         try {
            size = lengthAttribute != null?Long.parseLong(lengthAttribute.getValue()):0L;
         } catch (IOException var17) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var17);
         } catch (NumberFormatException var18) {
            size = 0L;
         }

         try {
            this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentTypeAttribute.getValue(), mechanism.value(), localCharset, size);
         } catch (NullPointerException var14) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
         } catch (IllegalArgumentException var15) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
         } catch (IOException var16) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
         }
      }

      try {
         this.readFileUploadByteMultipart(delimiter);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var13) {
         return null;
      }

      if(this.currentFileUpload.isCompleted()) {
         if(this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            this.currentFieldAttributes = null;
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            this.cleanMixedAttributes();
         }

         FileUpload fileUpload = this.currentFileUpload;
         this.currentFileUpload = null;
         return fileUpload;
      } else {
         return null;
      }
   }

   public void destroy() {
      this.checkDestroyed();
      this.cleanFiles();
      this.destroyed = true;
      if(this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
         this.undecodedChunk.release();
         this.undecodedChunk = null;
      }

      for(int i = this.bodyListHttpDataRank; i < this.bodyListHttpData.size(); ++i) {
         ((InterfaceHttpData)this.bodyListHttpData.get(i)).release();
      }

   }

   public void cleanFiles() {
      this.checkDestroyed();
      this.factory.cleanRequestHttpDatas(this.request);
   }

   public void removeHttpDataFromClean(InterfaceHttpData data) {
      this.checkDestroyed();
      this.factory.removeHttpDataFromClean(this.request, data);
   }

   private void cleanMixedAttributes() {
      this.currentFieldAttributes.remove("charset");
      this.currentFieldAttributes.remove("Content-Length");
      this.currentFieldAttributes.remove("Content-Transfer-Encoding");
      this.currentFieldAttributes.remove("Content-Type");
      this.currentFieldAttributes.remove("filename");
   }

   private String readLineStandard() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         ByteBuf line = Unpooled.buffer(64);

         while(this.undecodedChunk.isReadable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if(nextByte == 13) {
               nextByte = this.undecodedChunk.getByte(this.undecodedChunk.readerIndex());
               if(nextByte == 10) {
                  this.undecodedChunk.skipBytes(1);
                  return line.toString(this.charset);
               }

               line.writeByte(13);
            } else {
               if(nextByte == 10) {
                  return line.toString(this.charset);
               }

               line.writeByte(nextByte);
            }
         }
      } catch (IndexOutOfBoundsException var4) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var4);
      }

      this.undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private String readLine() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var5) {
         return this.readLineStandard();
      }

      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         ByteBuf line = Unpooled.buffer(64);

         while(sao.pos < sao.limit) {
            byte nextByte = sao.bytes[sao.pos++];
            if(nextByte == 13) {
               if(sao.pos < sao.limit) {
                  nextByte = sao.bytes[sao.pos++];
                  if(nextByte == 10) {
                     sao.setReadPosition(0);
                     return line.toString(this.charset);
                  }

                  --sao.pos;
                  line.writeByte(13);
               } else {
                  line.writeByte(nextByte);
               }
            } else {
               if(nextByte == 10) {
                  sao.setReadPosition(0);
                  return line.toString(this.charset);
               }

               line.writeByte(nextByte);
            }
         }
      } catch (IndexOutOfBoundsException var6) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var6);
      }

      this.undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private String readDelimiterStandard(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         StringBuilder sb = new StringBuilder(64);
         int delimiterPos = 0;
         int len = delimiter.length();

         while(this.undecodedChunk.isReadable() && delimiterPos < len) {
            byte nextByte = this.undecodedChunk.readByte();
            if(nextByte != delimiter.charAt(delimiterPos)) {
               this.undecodedChunk.readerIndex(readerIndex);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            ++delimiterPos;
            sb.append((char)nextByte);
         }

         if(this.undecodedChunk.isReadable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if(nextByte == 13) {
               nextByte = this.undecodedChunk.readByte();
               if(nextByte == 10) {
                  return sb.toString();
               }

               this.undecodedChunk.readerIndex(readerIndex);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            if(nextByte == 10) {
               return sb.toString();
            }

            if(nextByte == 45) {
               sb.append('-');
               nextByte = this.undecodedChunk.readByte();
               if(nextByte == 45) {
                  sb.append('-');
                  if(this.undecodedChunk.isReadable()) {
                     nextByte = this.undecodedChunk.readByte();
                     if(nextByte == 13) {
                        nextByte = this.undecodedChunk.readByte();
                        if(nextByte == 10) {
                           return sb.toString();
                        }

                        this.undecodedChunk.readerIndex(readerIndex);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                     }

                     if(nextByte == 10) {
                        return sb.toString();
                     }

                     this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                     return sb.toString();
                  }

                  return sb.toString();
               }
            }
         }
      } catch (IndexOutOfBoundsException var7) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var7);
      }

      this.undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private String readDelimiter(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var8) {
         return this.readDelimiterStandard(delimiter);
      }

      int readerIndex = this.undecodedChunk.readerIndex();
      int delimiterPos = 0;
      int len = delimiter.length();

      try {
         StringBuilder sb = new StringBuilder(64);

         while(sao.pos < sao.limit && delimiterPos < len) {
            byte nextByte = sao.bytes[sao.pos++];
            if(nextByte != delimiter.charAt(delimiterPos)) {
               this.undecodedChunk.readerIndex(readerIndex);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            ++delimiterPos;
            sb.append((char)nextByte);
         }

         if(sao.pos < sao.limit) {
            byte nextByte = sao.bytes[sao.pos++];
            if(nextByte == 13) {
               if(sao.pos < sao.limit) {
                  nextByte = sao.bytes[sao.pos++];
                  if(nextByte == 10) {
                     sao.setReadPosition(0);
                     return sb.toString();
                  }

                  this.undecodedChunk.readerIndex(readerIndex);
                  throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
               }

               this.undecodedChunk.readerIndex(readerIndex);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            if(nextByte == 10) {
               sao.setReadPosition(0);
               return sb.toString();
            }

            if(nextByte == 45) {
               sb.append('-');
               if(sao.pos < sao.limit) {
                  nextByte = sao.bytes[sao.pos++];
                  if(nextByte == 45) {
                     sb.append('-');
                     if(sao.pos < sao.limit) {
                        nextByte = sao.bytes[sao.pos++];
                        if(nextByte == 13) {
                           if(sao.pos < sao.limit) {
                              nextByte = sao.bytes[sao.pos++];
                              if(nextByte == 10) {
                                 sao.setReadPosition(0);
                                 return sb.toString();
                              }

                              this.undecodedChunk.readerIndex(readerIndex);
                              throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                           }

                           this.undecodedChunk.readerIndex(readerIndex);
                           throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                        }

                        if(nextByte == 10) {
                           sao.setReadPosition(0);
                           return sb.toString();
                        }

                        sao.setReadPosition(1);
                        return sb.toString();
                     }

                     sao.setReadPosition(0);
                     return sb.toString();
                  }
               }
            }
         }
      } catch (IndexOutOfBoundsException var9) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var9);
      }

      this.undecodedChunk.readerIndex(readerIndex);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private void readFileUploadByteMultipartStandard(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();
      boolean newLine = true;
      int index = 0;
      int lastPosition = this.undecodedChunk.readerIndex();
      boolean found = false;

      while(this.undecodedChunk.isReadable()) {
         byte nextByte = this.undecodedChunk.readByte();
         if(newLine) {
            if(nextByte == delimiter.codePointAt(index)) {
               ++index;
               if(delimiter.length() == index) {
                  found = true;
                  break;
               }
            } else {
               newLine = false;
               index = 0;
               if(nextByte == 13) {
                  if(this.undecodedChunk.isReadable()) {
                     nextByte = this.undecodedChunk.readByte();
                     if(nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 2;
                     } else {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        this.undecodedChunk.readerIndex(lastPosition);
                     }
                  }
               } else if(nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastPosition = this.undecodedChunk.readerIndex() - 1;
               } else {
                  lastPosition = this.undecodedChunk.readerIndex();
               }
            }
         } else if(nextByte == 13) {
            if(this.undecodedChunk.isReadable()) {
               nextByte = this.undecodedChunk.readByte();
               if(nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastPosition = this.undecodedChunk.readerIndex() - 2;
               } else {
                  lastPosition = this.undecodedChunk.readerIndex() - 1;
                  this.undecodedChunk.readerIndex(lastPosition);
               }
            }
         } else if(nextByte == 10) {
            newLine = true;
            index = 0;
            lastPosition = this.undecodedChunk.readerIndex() - 1;
         } else {
            lastPosition = this.undecodedChunk.readerIndex();
         }
      }

      ByteBuf buffer = this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
      if(found) {
         try {
            this.currentFileUpload.addContent(buffer, true);
            this.undecodedChunk.readerIndex(lastPosition);
         } catch (IOException var9) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
         }
      } else {
         try {
            this.currentFileUpload.addContent(buffer, false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         } catch (IOException var10) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
         }
      }
   }

   private void readFileUploadByteMultipart(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var13) {
         this.readFileUploadByteMultipartStandard(delimiter);
         return;
      }

      int readerIndex = this.undecodedChunk.readerIndex();
      boolean newLine = true;
      int index = 0;
      int lastrealpos = sao.pos;
      boolean found = false;

      while(sao.pos < sao.limit) {
         byte nextByte = sao.bytes[sao.pos++];
         if(newLine) {
            if(nextByte == delimiter.codePointAt(index)) {
               ++index;
               if(delimiter.length() == index) {
                  found = true;
                  break;
               }
            } else {
               newLine = false;
               index = 0;
               if(nextByte == 13) {
                  if(sao.pos < sao.limit) {
                     nextByte = sao.bytes[sao.pos++];
                     if(nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 2;
                     } else {
                        --sao.pos;
                        lastrealpos = sao.pos;
                     }
                  }
               } else if(nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastrealpos = sao.pos - 1;
               } else {
                  lastrealpos = sao.pos;
               }
            }
         } else if(nextByte == 13) {
            if(sao.pos < sao.limit) {
               nextByte = sao.bytes[sao.pos++];
               if(nextByte == 10) {
                  newLine = true;
                  index = 0;
                  lastrealpos = sao.pos - 2;
               } else {
                  --sao.pos;
                  lastrealpos = sao.pos;
               }
            }
         } else if(nextByte == 10) {
            newLine = true;
            index = 0;
            lastrealpos = sao.pos - 1;
         } else {
            lastrealpos = sao.pos;
         }
      }

      int lastPosition = sao.getReadPosition(lastrealpos);
      ByteBuf buffer = this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
      if(found) {
         try {
            this.currentFileUpload.addContent(buffer, true);
            this.undecodedChunk.readerIndex(lastPosition);
         } catch (IOException var11) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
         }
      } else {
         try {
            this.currentFileUpload.addContent(buffer, false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         } catch (IOException var12) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
         }
      }
   }

   private void loadFieldMultipartStandard(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         boolean newLine = true;
         int index = 0;
         int lastPosition = this.undecodedChunk.readerIndex();
         boolean found = false;

         while(this.undecodedChunk.isReadable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if(newLine) {
               if(nextByte == delimiter.codePointAt(index)) {
                  ++index;
                  if(delimiter.length() == index) {
                     found = true;
                     break;
                  }
               } else {
                  newLine = false;
                  index = 0;
                  if(nextByte == 13) {
                     if(this.undecodedChunk.isReadable()) {
                        nextByte = this.undecodedChunk.readByte();
                        if(nextByte == 10) {
                           newLine = true;
                           index = 0;
                           lastPosition = this.undecodedChunk.readerIndex() - 2;
                        } else {
                           lastPosition = this.undecodedChunk.readerIndex() - 1;
                           this.undecodedChunk.readerIndex(lastPosition);
                        }
                     } else {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                     }
                  } else if(nextByte == 10) {
                     newLine = true;
                     index = 0;
                     lastPosition = this.undecodedChunk.readerIndex() - 1;
                  } else {
                     lastPosition = this.undecodedChunk.readerIndex();
                  }
               }
            } else if(nextByte == 13) {
               if(this.undecodedChunk.isReadable()) {
                  nextByte = this.undecodedChunk.readByte();
                  if(nextByte == 10) {
                     newLine = true;
                     index = 0;
                     lastPosition = this.undecodedChunk.readerIndex() - 2;
                  } else {
                     lastPosition = this.undecodedChunk.readerIndex() - 1;
                     this.undecodedChunk.readerIndex(lastPosition);
                  }
               } else {
                  lastPosition = this.undecodedChunk.readerIndex() - 1;
               }
            } else if(nextByte == 10) {
               newLine = true;
               index = 0;
               lastPosition = this.undecodedChunk.readerIndex() - 1;
            } else {
               lastPosition = this.undecodedChunk.readerIndex();
            }
         }

         if(found) {
            try {
               this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
            } catch (IOException var8) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var8);
            }

            this.undecodedChunk.readerIndex(lastPosition);
         } else {
            try {
               this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
            } catch (IOException var9) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
            }

            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         }
      } catch (IndexOutOfBoundsException var10) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var10);
      }
   }

   private void loadFieldMultipart(String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
      HttpPostBodyUtil.SeekAheadOptimize sao;
      try {
         sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
      } catch (HttpPostBodyUtil.SeekAheadNoBackArrayException var12) {
         this.loadFieldMultipartStandard(delimiter);
         return;
      }

      int readerIndex = this.undecodedChunk.readerIndex();

      try {
         boolean newLine = true;
         int index = 0;
         int lastrealpos = sao.pos;
         boolean found = false;

         while(sao.pos < sao.limit) {
            byte nextByte = sao.bytes[sao.pos++];
            if(newLine) {
               if(nextByte == delimiter.codePointAt(index)) {
                  ++index;
                  if(delimiter.length() == index) {
                     found = true;
                     break;
                  }
               } else {
                  newLine = false;
                  index = 0;
                  if(nextByte == 13) {
                     if(sao.pos < sao.limit) {
                        nextByte = sao.bytes[sao.pos++];
                        if(nextByte == 10) {
                           newLine = true;
                           index = 0;
                           lastrealpos = sao.pos - 2;
                        } else {
                           --sao.pos;
                           lastrealpos = sao.pos;
                        }
                     }
                  } else if(nextByte == 10) {
                     newLine = true;
                     index = 0;
                     lastrealpos = sao.pos - 1;
                  } else {
                     lastrealpos = sao.pos;
                  }
               }
            } else if(nextByte == 13) {
               if(sao.pos < sao.limit) {
                  nextByte = sao.bytes[sao.pos++];
                  if(nextByte == 10) {
                     newLine = true;
                     index = 0;
                     lastrealpos = sao.pos - 2;
                  } else {
                     --sao.pos;
                     lastrealpos = sao.pos;
                  }
               }
            } else if(nextByte == 10) {
               newLine = true;
               index = 0;
               lastrealpos = sao.pos - 1;
            } else {
               lastrealpos = sao.pos;
            }
         }

         int lastPosition = sao.getReadPosition(lastrealpos);
         if(found) {
            try {
               this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
            } catch (IOException var10) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
            }

            this.undecodedChunk.readerIndex(lastPosition);
         } else {
            try {
               this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
            } catch (IOException var11) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
            }

            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         }
      } catch (IndexOutOfBoundsException var13) {
         this.undecodedChunk.readerIndex(readerIndex);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var13);
      }
   }

   private static String cleanString(String field) {
      StringBuilder sb = new StringBuilder(field.length());

      for(int i = 0; i < field.length(); ++i) {
         char nextChar = field.charAt(i);
         if(nextChar == 58) {
            sb.append(32);
         } else if(nextChar == 44) {
            sb.append(32);
         } else if(nextChar == 61) {
            sb.append(32);
         } else if(nextChar == 59) {
            sb.append(32);
         } else if(nextChar == 9) {
            sb.append(32);
         } else if(nextChar != 34) {
            sb.append(nextChar);
         }
      }

      return sb.toString().trim();
   }

   private boolean skipOneLine() {
      if(!this.undecodedChunk.isReadable()) {
         return false;
      } else {
         byte nextByte = this.undecodedChunk.readByte();
         if(nextByte == 13) {
            if(!this.undecodedChunk.isReadable()) {
               this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
               return false;
            } else {
               nextByte = this.undecodedChunk.readByte();
               if(nextByte == 10) {
                  return true;
               } else {
                  this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                  return false;
               }
            }
         } else if(nextByte == 10) {
            return true;
         } else {
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
         }
      }
   }

   private static String[] splitHeaderContentType(String sb) {
      int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
      int aEnd = sb.indexOf(59);
      if(aEnd == -1) {
         return new String[]{sb, ""};
      } else {
         if(sb.charAt(aEnd - 1) == 32) {
            --aEnd;
         }

         int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
         int bEnd = HttpPostBodyUtil.findEndOfString(sb);
         return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd)};
      }
   }

   private static String[] splitMultipartHeader(String sb) {
      ArrayList<String> headers = new ArrayList(1);
      int nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);

      int nameEnd;
      for(nameEnd = nameStart; nameEnd < sb.length(); ++nameEnd) {
         char ch = sb.charAt(nameEnd);
         if(ch == 58 || Character.isWhitespace(ch)) {
            break;
         }
      }

      int colonEnd;
      for(colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
         if(sb.charAt(colonEnd) == 58) {
            ++colonEnd;
            break;
         }
      }

      int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
      int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
      headers.add(sb.substring(nameStart, nameEnd));
      String svalue = sb.substring(valueStart, valueEnd);
      String[] values;
      if(svalue.indexOf(59) >= 0) {
         values = StringUtil.split(svalue, ';');
      } else {
         values = StringUtil.split(svalue, ',');
      }

      for(String value : values) {
         headers.add(value.trim());
      }

      String[] array = new String[headers.size()];

      for(int i = 0; i < headers.size(); ++i) {
         array[i] = (String)headers.get(i);
      }

      return array;
   }

   public static class EndOfDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 1336267941020800769L;
   }

   public static class ErrorDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataDecoderException() {
      }

      public ErrorDataDecoderException(String msg) {
         super(msg);
      }

      public ErrorDataDecoderException(Throwable cause) {
         super(cause);
      }

      public ErrorDataDecoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }

   public static class IncompatibleDataDecoderException extends DecoderException {
      private static final long serialVersionUID = -953268047926250267L;

      public IncompatibleDataDecoderException() {
      }

      public IncompatibleDataDecoderException(String msg) {
         super(msg);
      }

      public IncompatibleDataDecoderException(Throwable cause) {
         super(cause);
      }

      public IncompatibleDataDecoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }

   private static enum MultiPartStatus {
      NOTSTARTED,
      PREAMBLE,
      HEADERDELIMITER,
      DISPOSITION,
      FIELD,
      FILEUPLOAD,
      MIXEDPREAMBLE,
      MIXEDDELIMITER,
      MIXEDDISPOSITION,
      MIXEDFILEUPLOAD,
      MIXEDCLOSEDELIMITER,
      CLOSEDELIMITER,
      PREEPILOGUE,
      EPILOGUE;
   }

   public static class NotEnoughDataDecoderException extends DecoderException {
      private static final long serialVersionUID = -7846841864603865638L;

      public NotEnoughDataDecoderException() {
      }

      public NotEnoughDataDecoderException(String msg) {
         super(msg);
      }

      public NotEnoughDataDecoderException(Throwable cause) {
         super(cause);
      }

      public NotEnoughDataDecoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }
}
