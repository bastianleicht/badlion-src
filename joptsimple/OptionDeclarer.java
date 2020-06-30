package joptsimple;

import java.util.Collection;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionSpecBuilder;

public interface OptionDeclarer {
   OptionSpecBuilder accepts(String var1);

   OptionSpecBuilder accepts(String var1, String var2);

   OptionSpecBuilder acceptsAll(Collection var1);

   OptionSpecBuilder acceptsAll(Collection var1, String var2);

   NonOptionArgumentSpec nonOptions();

   NonOptionArgumentSpec nonOptions(String var1);

   void posixlyCorrect(boolean var1);

   void allowsUnrecognizedOptions();

   void recognizeAlternativeLongOptions(boolean var1);
}
