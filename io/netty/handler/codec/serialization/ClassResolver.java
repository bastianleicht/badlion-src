package io.netty.handler.codec.serialization;

public interface ClassResolver {
   Class resolve(String var1) throws ClassNotFoundException;
}
