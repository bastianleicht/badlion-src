package tv.twitch.chat;

import tv.twitch.chat.ChatMessageToken;
import tv.twitch.chat.ChatMessageTokenType;

public class ChatTextMessageToken extends ChatMessageToken {
   public String text = null;

   public ChatTextMessageToken() {
      this.type = ChatMessageTokenType.TTV_CHAT_MSGTOKEN_TEXT;
   }
}
