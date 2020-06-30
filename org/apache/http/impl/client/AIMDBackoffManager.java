package org.apache.http.impl.client;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.BackoffManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.Clock;
import org.apache.http.impl.client.SystemClock;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.util.Args;

public class AIMDBackoffManager implements BackoffManager {
   private final ConnPoolControl connPerRoute;
   private final Clock clock;
   private final Map lastRouteProbes;
   private final Map lastRouteBackoffs;
   private long coolDown;
   private double backoffFactor;
   private int cap;

   public AIMDBackoffManager(ConnPoolControl connPerRoute) {
      this(connPerRoute, new SystemClock());
   }

   AIMDBackoffManager(ConnPoolControl connPerRoute, Clock clock) {
      this.coolDown = 5000L;
      this.backoffFactor = 0.5D;
      this.cap = 2;
      this.clock = clock;
      this.connPerRoute = connPerRoute;
      this.lastRouteProbes = new HashMap();
      this.lastRouteBackoffs = new HashMap();
   }

   public void backOff(HttpRoute route) {
      synchronized(this.connPerRoute) {
         int curr = this.connPerRoute.getMaxPerRoute(route);
         Long lastUpdate = this.getLastUpdate(this.lastRouteBackoffs, route);
         long now = this.clock.getCurrentTime();
         if(now - lastUpdate.longValue() >= this.coolDown) {
            this.connPerRoute.setMaxPerRoute(route, this.getBackedOffPoolSize(curr));
            this.lastRouteBackoffs.put(route, Long.valueOf(now));
         }
      }
   }

   private int getBackedOffPoolSize(int curr) {
      return curr <= 1?1:(int)Math.floor(this.backoffFactor * (double)curr);
   }

   public void probe(HttpRoute route) {
      synchronized(this.connPerRoute) {
         int curr = this.connPerRoute.getMaxPerRoute(route);
         int max = curr >= this.cap?this.cap:curr + 1;
         Long lastProbe = this.getLastUpdate(this.lastRouteProbes, route);
         Long lastBackoff = this.getLastUpdate(this.lastRouteBackoffs, route);
         long now = this.clock.getCurrentTime();
         if(now - lastProbe.longValue() >= this.coolDown && now - lastBackoff.longValue() >= this.coolDown) {
            this.connPerRoute.setMaxPerRoute(route, max);
            this.lastRouteProbes.put(route, Long.valueOf(now));
         }
      }
   }

   private Long getLastUpdate(Map updates, HttpRoute route) {
      Long lastUpdate = (Long)updates.get(route);
      if(lastUpdate == null) {
         lastUpdate = Long.valueOf(0L);
      }

      return lastUpdate;
   }

   public void setBackoffFactor(double d) {
      Args.check(d > 0.0D && d < 1.0D, "Backoff factor must be 0.0 < f < 1.0");
      this.backoffFactor = d;
   }

   public void setCooldownMillis(long l) {
      Args.positive(this.coolDown, "Cool down");
      this.coolDown = l;
   }

   public void setPerHostConnectionCap(int cap) {
      Args.positive(cap, "Per host connection cap");
      this.cap = cap;
   }
}
