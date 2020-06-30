package org.apache.logging.log4j.core.jmx;

public interface ContextSelectorAdminMBean {
   String NAME = "org.apache.logging.log4j2:type=ContextSelector";

   String getImplementationClassName();
}
