package joptsimple;

public interface ValueConverter {
   Object convert(String var1);

   Class valueType();

   String valuePattern();
}
