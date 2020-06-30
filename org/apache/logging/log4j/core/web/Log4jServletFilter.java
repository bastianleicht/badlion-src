package org.apache.logging.log4j.core.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.logging.log4j.core.web.Log4jWebInitializer;
import org.apache.logging.log4j.core.web.Log4jWebInitializerImpl;

public class Log4jServletFilter implements Filter {
   static final String ALREADY_FILTERED_ATTRIBUTE = Log4jServletFilter.class.getName() + ".FILTERED";
   private ServletContext servletContext;
   private Log4jWebInitializer initializer;

   public void init(FilterConfig filterConfig) throws ServletException {
      this.servletContext = filterConfig.getServletContext();
      this.servletContext.log("Log4jServletFilter initialized.");
      this.initializer = Log4jWebInitializerImpl.getLog4jWebInitializer(this.servletContext);
      this.initializer.clearLoggerContext();
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      if(request.getAttribute(ALREADY_FILTERED_ATTRIBUTE) != null) {
         chain.doFilter(request, response);
      } else {
         request.setAttribute(ALREADY_FILTERED_ATTRIBUTE, Boolean.valueOf(true));

         try {
            this.initializer.setLoggerContext();
            chain.doFilter(request, response);
         } finally {
            this.initializer.clearLoggerContext();
         }
      }

   }

   public void destroy() {
      if(this.servletContext != null && this.initializer != null) {
         this.servletContext.log("Log4jServletFilter destroyed.");
         this.initializer.setLoggerContext();
      } else {
         throw new IllegalStateException("Filter destroyed before it was initialized.");
      }
   }
}
