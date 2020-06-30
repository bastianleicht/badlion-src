package org.apache.logging.log4j.core.lookup;

import javax.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(
   name = "web",
   category = "Lookup"
)
public class WebLookup implements StrLookup {
   private static final String ATTR_PREFIX = "attr.";
   private static final String INIT_PARAM_PREFIX = "initParam.";

   protected ServletContext getServletContext() {
      LoggerContext lc = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
      if(lc == null) {
         lc = (LoggerContext)LogManager.getContext(false);
      }

      if(lc == null) {
         return null;
      } else {
         Object obj = lc.getExternalContext();
         return obj != null && obj instanceof ServletContext?(ServletContext)obj:null;
      }
   }

   public String lookup(String key) {
      ServletContext ctx = this.getServletContext();
      if(ctx == null) {
         return null;
      } else if(key.startsWith("attr.")) {
         String attrName = key.substring("attr.".length());
         Object attrValue = ctx.getAttribute(attrName);
         return attrValue == null?null:attrValue.toString();
      } else if(key.startsWith("initParam.")) {
         String paramName = key.substring("initParam.".length());
         return ctx.getInitParameter(paramName);
      } else if("rootDir".equals(key)) {
         String root = ctx.getRealPath("/");
         if(root == null) {
            String msg = "failed to resolve web:rootDir -- servlet container unable to translate virtual path  to real path (probably not deployed as exploded";
            throw new RuntimeException(msg);
         } else {
            return root;
         }
      } else if("contextPath".equals(key)) {
         return ctx.getContextPath();
      } else if("servletContextName".equals(key)) {
         return ctx.getServletContextName();
      } else if("serverInfo".equals(key)) {
         return ctx.getServerInfo();
      } else if("effectiveMajorVersion".equals(key)) {
         return String.valueOf(ctx.getEffectiveMajorVersion());
      } else if("effectiveMinorVersion".equals(key)) {
         return String.valueOf(ctx.getEffectiveMinorVersion());
      } else if("majorVersion".equals(key)) {
         return String.valueOf(ctx.getMajorVersion());
      } else if("minorVersion".equals(key)) {
         return String.valueOf(ctx.getMinorVersion());
      } else if(ctx.getAttribute(key) != null) {
         return ctx.getAttribute(key).toString();
      } else if(ctx.getInitParameter(key) != null) {
         return ctx.getInitParameter(key);
      } else {
         ctx.log(this.getClass().getName() + " unable to resolve key \'" + key + "\'");
         return null;
      }
   }

   public String lookup(LogEvent event, String key) {
      return this.lookup(key);
   }
}
