package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface FileRegion extends ReferenceCounted {
   long position();

   long transfered();

   long count();

   long transferTo(WritableByteChannel var1, long var2) throws IOException;
}
