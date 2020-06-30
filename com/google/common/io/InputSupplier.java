package com.google.common.io;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface InputSupplier {
   Object getInput() throws IOException;
}
