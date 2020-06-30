package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.ErrorCode;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;

public abstract class ChatAPI {
   public abstract ErrorCode initialize(HashSet var1, IChatAPIListener var2);

   public abstract ErrorCode shutdown();

   public abstract ErrorCode connect(String var1, String var2, String var3, IChatChannelListener var4);

   public abstract ErrorCode connectAnonymous(String var1, IChatChannelListener var2);

   public abstract ErrorCode disconnect(String var1);

   public abstract ErrorCode sendMessage(String var1, String var2);

   public abstract ErrorCode flushEvents();

   public abstract ErrorCode downloadEmoticonData();

   public abstract ErrorCode getEmoticonData(ChatEmoticonData var1);

   public abstract ErrorCode clearEmoticonData();

   public abstract ErrorCode downloadBadgeData(String var1);

   public abstract ErrorCode getBadgeData(String var1, ChatBadgeData var2);

   public abstract ErrorCode clearBadgeData(String var1);

   public abstract int getMessageFlushInterval();

   public abstract ErrorCode setMessageFlushInterval(int var1);

   public abstract int getUserChangeEventInterval();

   public abstract ErrorCode setUserChangeEventInterval(int var1);
}
