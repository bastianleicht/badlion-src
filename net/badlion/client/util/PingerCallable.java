package net.badlion.client.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import net.minecraft.client.multiplayer.ServerAddress;

public class PingerCallable {
   private SocketAddress address;
   private String rawAddress;

   public PingerCallable(String address) {
      ServerAddress serveraddress = ServerAddress.func_78860_a(address);
      this.address = new InetSocketAddress(serveraddress.getIP(), serveraddress.getPort());
      this.rawAddress = address;
   }

   public String getRawAddress() {
      return this.rawAddress;
   }

   public SocketAddress getAddress() {
      return this.address;
   }

   public Long getPing() {
      Socket socket = new Socket();

      try {
         long i = System.currentTimeMillis();
         socket.connect(this.address, 1000);
         socket.close();
         return Long.valueOf(System.currentTimeMillis() - i);
      } catch (IOException var5) {
         if(!socket.isClosed()) {
            try {
               socket.close();
            } catch (IOException var4) {
               ;
            }
         }

         if(var5 instanceof SocketTimeoutException) {
            ;
         }

         return Long.valueOf(-1L);
      }
   }
}
