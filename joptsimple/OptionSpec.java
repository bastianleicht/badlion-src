package joptsimple;

import java.util.Collection;
import java.util.List;
import joptsimple.OptionSet;

public interface OptionSpec {
   List values(OptionSet var1);

   Object value(OptionSet var1);

   Collection options();

   boolean isForHelp();
}
