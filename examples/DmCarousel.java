import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.MessageButton;
import dev.postproxy.sdk.model.MessageElement;
import dev.postproxy.sdk.param.ListChatsParams;
import dev.postproxy.sdk.param.SendMessageParams;

import java.util.List;

public class DmCarousel {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY").build();
        var profileId = "your-instagram-profile-id";

        var chats = client.chats().list(profileId, ListChatsParams.builder().perPage(1).build());
        var chat = chats.data().get(0);

        // Show the participant we're on it, then send a product carousel
        client.chats().markRead(chat.id());
        client.chats().startTyping(chat.id());

        client.messages().send(chat.id(), SendMessageParams.builder()
                .elements(List.of(
                        new MessageElement("Red sneaker", "$89 — sizes 38-46", "https://cdn.example.com/red.png",
                                List.of(MessageButton.webUrl("Buy", "https://shop.example.com/red"))),
                        new MessageElement("Blue sneaker", "$92 — back in stock soon", "https://cdn.example.com/blue.png",
                                List.of(MessageButton.postback("Notify me", "NOTIFY:blue")))))
                .build());

        client.chats().stopTyping(chat.id());
    }
}
