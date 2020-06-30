package com.google.common.eventbus;

import com.google.common.collect.Multimap;

interface SubscriberFindingStrategy {
   Multimap findAllSubscribers(Object var1);
}
