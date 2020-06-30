package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.ErrorCode;
import tv.twitch.chat.ChatAPI;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;

public class StandardChatAPI extends ChatAPI {
   protected void finalize() {
   }

   private static native ErrorCode TTV_Java_Chat_Initialize(int var0, IChatAPIListener var1);

   private static native ErrorCode TTV_Java_Chat_Shutdown();

   private static native ErrorCode TTV_Java_Chat_Connect(String var0, String var1, String var2, IChatChannelListener var3);

   private static native ErrorCode TTV_Java_Chat_ConnectAnonymous(String var0, IChatChannelListener var1);

   private static native ErrorCode TTV_Java_Chat_Disconnect(String var0);

   private static native ErrorCode TTV_Java_Chat_SendMessage(String var0, String var1);

   private static native ErrorCode TTV_Java_Chat_FlushEvents();

   private static native ErrorCode TTV_Java_Chat_DownloadEmoticonData();

   private static native ErrorCode TTV_Java_Chat_GetEmoticonData(ChatEmoticonData var0);

   private static native ErrorCode TTV_Java_Chat_ClearEmoticonData();

   private static native ErrorCode TTV_Java_Chat_DownloadBadgeData(String var0);

   private static native ErrorCode TTV_Java_Chat_GetBadgeData(String var0, ChatBadgeData var1);

   private static native ErrorCode TTV_Java_Chat_ClearBadgeData(String var0);

   private static native long TTV_Java_Chat_GetMessageFlushInterval();

   private static native ErrorCode TTV_Java_Chat_SetMessageFlushInterval(long var0);

   private static native long TTV_Java_Chat_GetUserChangeEventInterval();

   private static native ErrorCode TTV_Java_Chat_SetUserChangeEventInterval(long var0);

   public ErrorCode initialize(HashSet var1, IChatAPIListener var2) {
      int var3 = ChatTokenizationOption.getNativeValue(var1);
      return TTV_Java_Chat_Initialize(var3, var2);
   }

   public ErrorCode shutdown() {
      return TTV_Java_Chat_Shutdown();
   }

   public ErrorCode connect(String var1, String var2, String var3, IChatChannelListener var4) {
      return TTV_Java_Chat_Connect(var1, var2, var3, var4);
   }

   public ErrorCode connectAnonymous(String var1, IChatChannelListener var2) {
      return TTV_Java_Chat_ConnectAnonymous(var1, var2);
   }

   public ErrorCode disconnect(String var1) {
      return TTV_Java_Chat_Disconnect(var1);
   }

   public ErrorCode sendMessage(String var1, String var2) {
      return TTV_Java_Chat_SendMessage(var1, var2);
   }

   public ErrorCode flushEvents() {
      return TTV_Java_Chat_FlushEvents();
   }

   public ErrorCode downloadEmoticonData() {
      return TTV_Java_Chat_DownloadEmoticonData();
   }

   public ErrorCode getEmoticonData(ChatEmoticonData var1) {
      return TTV_Java_Chat_GetEmoticonData(var1);
   }

   public ErrorCode clearEmoticonData() {
      return TTV_Java_Chat_ClearEmoticonData();
   }

   public ErrorCode downloadBadgeData(String var1) {
      return TTV_Java_Chat_DownloadBadgeData(var1);
   }

   public ErrorCode getBadgeData(String var1, ChatBadgeData var2) {
      return TTV_Java_Chat_GetBadgeData(var1, var2);
   }

   public ErrorCode clearBadgeData(String var1) {
      return TTV_Java_Chat_ClearBadgeData(var1);
   }

   public int getMessageFlushInterval() {
      return (int)TTV_Java_Chat_GetMessageFlushInterval();
   }

   public ErrorCode setMessageFlushInterval(int var1) {
      return TTV_Java_Chat_SetMessageFlushInterval((long)var1);
   }

   public int getUserChangeEventInterval() {
      return (int)TTV_Java_Chat_GetUserChangeEventInterval();
   }

   public ErrorCode setUserChangeEventInterval(int var1) {
      return TTV_Java_Chat_SetUserChangeEventInterval((long)var1);
   }
}
