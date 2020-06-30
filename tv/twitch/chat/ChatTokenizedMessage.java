package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.chat.ChatMessageToken;

public class ChatTokenizedMessage {
   public String displayName;
   public HashSet modes = new HashSet();
   public HashSet subscriptions = new HashSet();
   public int nameColorARGB;
   public ChatMessageToken[] tokenList;
   public boolean action;
}
