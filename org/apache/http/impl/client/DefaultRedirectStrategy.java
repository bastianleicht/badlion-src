package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.TextUtils;

@Immutable
public class DefaultRedirectStrategy implements RedirectStrategy {
   private final Log log = LogFactory.getLog(this.getClass());
   /** @deprecated */
   @Deprecated
   public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
   public static final DefaultRedirectStrategy INSTANCE = new DefaultRedirectStrategy();
   private static final String[] REDIRECT_METHODS = new String[]{"GET", "HEAD"};

   public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
      Args.notNull(request, "HTTP request");
      Args.notNull(response, "HTTP response");
      int statusCode = response.getStatusLine().getStatusCode();
      String method = request.getRequestLine().getMethod();
      Header locationHeader = response.getFirstHeader("location");
      switch(statusCode) {
      case 301:
      case 307:
         return this.isRedirectable(method);
      case 302:
         return this.isRedirectable(method) && locationHeader != null;
      case 303:
         return true;
      case 304:
      case 305:
      case 306:
      default:
         return false;
      }
   }

   public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
      Args.notNull(request, "HTTP request");
      Args.notNull(response, "HTTP response");
      Args.notNull(context, "HTTP context");
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      Header locationHeader = response.getFirstHeader("location");
      if(locationHeader == null) {
         throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
      } else {
         String location = locationHeader.getValue();
         if(this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location \'" + location + "\'");
         }

         RequestConfig config = clientContext.getRequestConfig();
         URI uri = this.createLocationURI(location);

         try {
            if(!uri.isAbsolute()) {
               if(!config.isRelativeRedirectsAllowed()) {
                  throw new ProtocolException("Relative redirect location \'" + uri + "\' not allowed");
               }

               HttpHost target = clientContext.getTargetHost();
               Asserts.notNull(target, "Target host");
               URI requestURI = new URI(request.getRequestLine().getUri());
               URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, false);
               uri = URIUtils.resolve(absoluteRequestURI, uri);
            }
         } catch (URISyntaxException var12) {
            throw new ProtocolException(var12.getMessage(), var12);
         }

         RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
         if(redirectLocations == null) {
            redirectLocations = new RedirectLocations();
            context.setAttribute("http.protocol.redirect-locations", redirectLocations);
         }

         if(!config.isCircularRedirectsAllowed() && redirectLocations.contains(uri)) {
            throw new CircularRedirectException("Circular redirect to \'" + uri + "\'");
         } else {
            redirectLocations.add(uri);
            return uri;
         }
      }
   }

   protected URI createLocationURI(String location) throws ProtocolException {
      try {
         URIBuilder b = new URIBuilder((new URI(location)).normalize());
         String host = b.getHost();
         if(host != null) {
            b.setHost(host.toLowerCase(Locale.US));
         }

         String path = b.getPath();
         if(TextUtils.isEmpty(path)) {
            b.setPath("/");
         }

         return b.build();
      } catch (URISyntaxException var5) {
         throw new ProtocolException("Invalid redirect URI: " + location, var5);
      }
   }

   protected boolean isRedirectable(String method) {
      for(String m : REDIRECT_METHODS) {
         if(m.equalsIgnoreCase(method)) {
            return true;
         }
      }

      return false;
   }

   public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
      URI uri = this.getLocationURI(request, response, context);
      String method = request.getRequestLine().getMethod();
      if(method.equalsIgnoreCase("HEAD")) {
         return new HttpHead(uri);
      } else if(method.equalsIgnoreCase("GET")) {
         return new HttpGet(uri);
      } else {
         int status = response.getStatusLine().getStatusCode();
         return (HttpUriRequest)(status == 307?RequestBuilder.copy(request).setUri(uri).build():new HttpGet(uri));
      }
   }
}
