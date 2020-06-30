package com.google.common.collect;

import com.google.common.collect.Multiset;
import java.util.SortedSet;

interface SortedMultisetBridge extends Multiset {
   SortedSet elementSet();
}
