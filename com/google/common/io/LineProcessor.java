package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public interface LineProcessor {
   boolean processLine(String var1) throws IOException;

   Object getResult();
}
