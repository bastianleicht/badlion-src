package tv.twitch.chat;

import java.util.HashSet;

public class ChatUserInfo {
   public String displayName = null;
   public HashSet modes = new HashSet();
   public HashSet subscriptions = new HashSet();
   public int nameColorARGB = 0;
   public boolean ignore = false;
}
