package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdStatus;
import io.netty.handler.codec.socks.SocksResponse;
import io.netty.handler.codec.socks.SocksResponseType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;

public final class SocksCmdResponse extends SocksResponse {
   private final SocksCmdStatus cmdStatus;
   private final SocksAddressType addressType;
   private final String host;
   private final int port;
   private static final byte[] DOMAIN_ZEROED = new byte[]{(byte)0};
   private static final byte[] IPv4_HOSTNAME_ZEROED = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0};
   private static final byte[] IPv6_HOSTNAME_ZEROED = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};

   public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType) {
      this(cmdStatus, addressType, (String)null, 0);
   }

   public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType, String host, int port) {
      super(SocksResponseType.CMD);
      if(cmdStatus == null) {
         throw new NullPointerException("cmdStatus");
      } else if(addressType == null) {
         throw new NullPointerException("addressType");
      } else {
         if(host != null) {
            switch(addressType) {
            case IPv4:
               if(!NetUtil.isValidIpV4Address(host)) {
                  throw new IllegalArgumentException(host + " is not a valid IPv4 address");
               }
               break;
            case DOMAIN:
               if(IDN.toASCII(host).length() > 255) {
                  throw new IllegalArgumentException(host + " IDN: " + IDN.toASCII(host) + " exceeds 255 char limit");
               }
               break;
            case IPv6:
               if(!NetUtil.isValidIpV6Address(host)) {
                  throw new IllegalArgumentException(host + " is not a valid IPv6 address");
               }
            case UNKNOWN:
            }

            host = IDN.toASCII(host);
         }

         if(port >= 0 && port <= '\uffff') {
            this.cmdStatus = cmdStatus;
            this.addressType = addressType;
            this.host = host;
            this.port = port;
         } else {
            throw new IllegalArgumentException(port + " is not in bounds 0 <= x <= 65535");
         }
      }
   }

   public SocksCmdStatus cmdStatus() {
      return this.cmdStatus;
   }

   public SocksAddressType addressType() {
      return this.addressType;
   }

   public String host() {
      return this.host != null?IDN.toUnicode(this.host):null;
   }

   public int port() {
      return this.port;
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(this.protocolVersion().byteValue());
      byteBuf.writeByte(this.cmdStatus.byteValue());
      byteBuf.writeByte(0);
      byteBuf.writeByte(this.addressType.byteValue());
      switch(this.addressType) {
      case IPv4:
         byte[] hostContent = this.host == null?IPv4_HOSTNAME_ZEROED:NetUtil.createByteArrayFromIpAddressString(this.host);
         byteBuf.writeBytes(hostContent);
         byteBuf.writeShort(this.port);
         break;
      case DOMAIN:
         byte[] hostContent = this.host == null?DOMAIN_ZEROED:this.host.getBytes(CharsetUtil.US_ASCII);
         byteBuf.writeByte(hostContent.length);
         byteBuf.writeBytes(hostContent);
         byteBuf.writeShort(this.port);
         break;
      case IPv6:
         byte[] hostContent = this.host == null?IPv6_HOSTNAME_ZEROED:NetUtil.createByteArrayFromIpAddressString(this.host);
         byteBuf.writeBytes(hostContent);
         byteBuf.writeShort(this.port);
      }

   }
}
