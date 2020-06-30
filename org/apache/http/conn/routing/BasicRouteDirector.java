package org.apache.http.conn.routing;

import org.apache.http.annotation.Immutable;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;

@Immutable
public class BasicRouteDirector implements HttpRouteDirector {
   public int nextStep(RouteInfo plan, RouteInfo fact) {
      Args.notNull(plan, "Planned route");
      int step = -1;
      if(fact != null && fact.getHopCount() >= 1) {
         if(plan.getHopCount() > 1) {
            step = this.proxiedStep(plan, fact);
         } else {
            step = this.directStep(plan, fact);
         }
      } else {
         step = this.firstStep(plan);
      }

      return step;
   }

   protected int firstStep(RouteInfo plan) {
      return plan.getHopCount() > 1?2:1;
   }

   protected int directStep(RouteInfo plan, RouteInfo fact) {
      return fact.getHopCount() > 1?-1:(!plan.getTargetHost().equals(fact.getTargetHost())?-1:(plan.isSecure() != fact.isSecure()?-1:(plan.getLocalAddress() != null && !plan.getLocalAddress().equals(fact.getLocalAddress())?-1:0)));
   }

   protected int proxiedStep(RouteInfo plan, RouteInfo fact) {
      if(fact.getHopCount() <= 1) {
         return -1;
      } else if(!plan.getTargetHost().equals(fact.getTargetHost())) {
         return -1;
      } else {
         int phc = plan.getHopCount();
         int fhc = fact.getHopCount();
         if(phc < fhc) {
            return -1;
         } else {
            for(int i = 0; i < fhc - 1; ++i) {
               if(!plan.getHopTarget(i).equals(fact.getHopTarget(i))) {
                  return -1;
               }
            }

            return phc > fhc?4:((!fact.isTunnelled() || plan.isTunnelled()) && (!fact.isLayered() || plan.isLayered())?(plan.isTunnelled() && !fact.isTunnelled()?3:(plan.isLayered() && !fact.isLayered()?5:(plan.isSecure() != fact.isSecure()?-1:0))):-1);
         }
      }
   }
}
