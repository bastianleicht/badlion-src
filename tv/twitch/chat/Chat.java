package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.ErrorCode;
import tv.twitch.chat.ChatAPI;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;

public class Chat {
   private static Chat s_Instance = null;
   private ChatAPI m_ChatAPI = null;

   public static Chat getInstance() {
      return s_Instance;
   }

   public Chat(ChatAPI var1) {
      this.m_ChatAPI = var1;
      if(s_Instance == null) {
         s_Instance = this;
      }

   }

   public ErrorCode initialize(HashSet var1, IChatAPIListener var2) {
      return this.m_ChatAPI.initialize(var1, var2);
   }

   public ErrorCode shutdown() {
      return this.m_ChatAPI.shutdown();
   }

   public ErrorCode connect(String var1, String var2, String var3, IChatChannelListener var4) {
      return this.m_ChatAPI.connect(var1, var2, var3, var4);
   }

   public ErrorCode connectAnonymous(String var1, IChatChannelListener var2) {
      return this.m_ChatAPI.connectAnonymous(var1, var2);
   }

   public ErrorCode disconnect(String var1) {
      return this.m_ChatAPI.disconnect(var1);
   }

   public ErrorCode sendMessage(String var1, String var2) {
      return this.m_ChatAPI.sendMessage(var1, var2);
   }

   public ErrorCode flushEvents() {
      return this.m_ChatAPI.flushEvents();
   }

   public ErrorCode downloadEmoticonData() {
      return this.m_ChatAPI.downloadEmoticonData();
   }

   public ErrorCode getEmoticonData(ChatEmoticonData var1) {
      return this.m_ChatAPI.getEmoticonData(var1);
   }

   public ErrorCode clearEmoticonData() {
      return this.m_ChatAPI.clearEmoticonData();
   }

   public ErrorCode downloadBadgeData(String var1) {
      return this.m_ChatAPI.downloadBadgeData(var1);
   }

   public ErrorCode getBadgeData(String var1, ChatBadgeData var2) {
      return this.m_ChatAPI.getBadgeData(var1, var2);
   }

   public ErrorCode clearBadgeData(String var1) {
      return this.m_ChatAPI.clearBadgeData(var1);
   }

   public int getMessageFlushInterval() {
      return this.m_ChatAPI.getMessageFlushInterval();
   }

   public ErrorCode setMessageFlushInterval(int var1) {
      return this.m_ChatAPI.setMessageFlushInterval(var1);
   }

   public int getUserChangeEventInterval() {
      return this.m_ChatAPI.getUserChangeEventInterval();
   }

   public ErrorCode setUserChangeEventInterval(int var1) {
      return this.m_ChatAPI.setUserChangeEventInterval(var1);
   }
}
