package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ScheduledFuture;

@Beta
public interface ListenableScheduledFuture extends ScheduledFuture, ListenableFuture {
}
