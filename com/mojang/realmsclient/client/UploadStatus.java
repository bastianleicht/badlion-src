package com.mojang.realmsclient.client;

public class UploadStatus {
   public volatile Long bytesWritten = Long.valueOf(0L);
   public volatile Long totalBytes = Long.valueOf(0L);
}
