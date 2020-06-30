package com.google.common.io;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface OutputSupplier {
   Object getOutput() throws IOException;
}
