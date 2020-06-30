package tv.twitch.chat;

import tv.twitch.chat.ChatMessageToken;
import tv.twitch.chat.ChatMessageTokenType;

public class ChatTextureImageMessageToken extends ChatMessageToken {
   public int sheetIndex = -1;
   public short x1;
   public short y1;
   public short x2;
   public short y2;

   public ChatTextureImageMessageToken() {
      this.type = ChatMessageTokenType.TTV_CHAT_MSGTOKEN_TEXTURE_IMAGE;
   }
}
