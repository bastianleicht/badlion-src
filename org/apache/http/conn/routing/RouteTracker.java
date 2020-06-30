package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

@NotThreadSafe
public final class RouteTracker implements RouteInfo, Cloneable {
   private final HttpHost targetHost;
   private final InetAddress localAddress;
   private boolean connected;
   private HttpHost[] proxyChain;
   private RouteInfo.TunnelType tunnelled;
   private RouteInfo.LayerType layered;
   private boolean secure;

   public RouteTracker(HttpHost target, InetAddress local) {
      Args.notNull(target, "Target host");
      this.targetHost = target;
      this.localAddress = local;
      this.tunnelled = RouteInfo.TunnelType.PLAIN;
      this.layered = RouteInfo.LayerType.PLAIN;
   }

   public void reset() {
      this.connected = false;
      this.proxyChain = null;
      this.tunnelled = RouteInfo.TunnelType.PLAIN;
      this.layered = RouteInfo.LayerType.PLAIN;
      this.secure = false;
   }

   public RouteTracker(HttpRoute route) {
      this(route.getTargetHost(), route.getLocalAddress());
   }

   public final void connectTarget(boolean secure) {
      Asserts.check(!this.connected, "Already connected");
      this.connected = true;
      this.secure = secure;
   }

   public final void connectProxy(HttpHost proxy, boolean secure) {
      Args.notNull(proxy, "Proxy host");
      Asserts.check(!this.connected, "Already connected");
      this.connected = true;
      this.proxyChain = new HttpHost[]{proxy};
      this.secure = secure;
   }

   public final void tunnelTarget(boolean secure) {
      Asserts.check(this.connected, "No tunnel unless connected");
      Asserts.notNull(this.proxyChain, "No tunnel without proxy");
      this.tunnelled = RouteInfo.TunnelType.TUNNELLED;
      this.secure = secure;
   }

   public final void tunnelProxy(HttpHost proxy, boolean secure) {
      Args.notNull(proxy, "Proxy host");
      Asserts.check(this.connected, "No tunnel unless connected");
      Asserts.notNull(this.proxyChain, "No tunnel without proxy");
      HttpHost[] proxies = new HttpHost[this.proxyChain.length + 1];
      System.arraycopy(this.proxyChain, 0, proxies, 0, this.proxyChain.length);
      proxies[proxies.length - 1] = proxy;
      this.proxyChain = proxies;
      this.secure = secure;
   }

   public final void layerProtocol(boolean secure) {
      Asserts.check(this.connected, "No layered protocol unless connected");
      this.layered = RouteInfo.LayerType.LAYERED;
      this.secure = secure;
   }

   public final HttpHost getTargetHost() {
      return this.targetHost;
   }

   public final InetAddress getLocalAddress() {
      return this.localAddress;
   }

   public final int getHopCount() {
      int hops = 0;
      if(this.connected) {
         if(this.proxyChain == null) {
            hops = 1;
         } else {
            hops = this.proxyChain.length + 1;
         }
      }

      return hops;
   }

   public final HttpHost getHopTarget(int hop) {
      Args.notNegative(hop, "Hop index");
      int hopcount = this.getHopCount();
      Args.check(hop < hopcount, "Hop index exceeds tracked route length");
      HttpHost result = null;
      if(hop < hopcount - 1) {
         result = this.proxyChain[hop];
      } else {
         result = this.targetHost;
      }

      return result;
   }

   public final HttpHost getProxyHost() {
      return this.proxyChain == null?null:this.proxyChain[0];
   }

   public final boolean isConnected() {
      return this.connected;
   }

   public final RouteInfo.TunnelType getTunnelType() {
      return this.tunnelled;
   }

   public final boolean isTunnelled() {
      return this.tunnelled == RouteInfo.TunnelType.TUNNELLED;
   }

   public final RouteInfo.LayerType getLayerType() {
      return this.layered;
   }

   public final boolean isLayered() {
      return this.layered == RouteInfo.LayerType.LAYERED;
   }

   public final boolean isSecure() {
      return this.secure;
   }

   public final HttpRoute toRoute() {
      return !this.connected?null:new HttpRoute(this.targetHost, this.localAddress, this.proxyChain, this.secure, this.tunnelled, this.layered);
   }

   public final boolean equals(Object o) {
      if(o == this) {
         return true;
      } else if(!(o instanceof RouteTracker)) {
         return false;
      } else {
         RouteTracker that = (RouteTracker)o;
         return this.connected == that.connected && this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals((Object)this.targetHost, (Object)that.targetHost) && LangUtils.equals((Object)this.localAddress, (Object)that.localAddress) && LangUtils.equals((Object[])this.proxyChain, (Object[])that.proxyChain);
      }
   }

   public final int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.targetHost);
      hash = LangUtils.hashCode(hash, this.localAddress);
      if(this.proxyChain != null) {
         for(HttpHost element : this.proxyChain) {
            hash = LangUtils.hashCode(hash, element);
         }
      }

      hash = LangUtils.hashCode(hash, this.connected);
      hash = LangUtils.hashCode(hash, this.secure);
      hash = LangUtils.hashCode(hash, this.tunnelled);
      hash = LangUtils.hashCode(hash, this.layered);
      return hash;
   }

   public final String toString() {
      StringBuilder cab = new StringBuilder(50 + this.getHopCount() * 30);
      cab.append("RouteTracker[");
      if(this.localAddress != null) {
         cab.append(this.localAddress);
         cab.append("->");
      }

      cab.append('{');
      if(this.connected) {
         cab.append('c');
      }

      if(this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
         cab.append('t');
      }

      if(this.layered == RouteInfo.LayerType.LAYERED) {
         cab.append('l');
      }

      if(this.secure) {
         cab.append('s');
      }

      cab.append("}->");
      if(this.proxyChain != null) {
         for(HttpHost element : this.proxyChain) {
            cab.append(element);
            cab.append("->");
         }
      }

      cab.append(this.targetHost);
      cab.append(']');
      return cab.toString();
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
