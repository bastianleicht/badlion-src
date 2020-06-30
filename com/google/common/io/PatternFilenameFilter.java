package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@Beta
public final class PatternFilenameFilter implements FilenameFilter {
   private final Pattern pattern;

   public PatternFilenameFilter(String patternStr) {
      this(Pattern.compile(patternStr));
   }

   public PatternFilenameFilter(Pattern pattern) {
      this.pattern = (Pattern)Preconditions.checkNotNull(pattern);
   }

   public boolean accept(@Nullable File dir, String fileName) {
      return this.pattern.matcher(fileName).matches();
   }
}
