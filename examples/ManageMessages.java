import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.MessageDirection;
import dev.postproxy.sdk.param.CreateChatParams;
import dev.postproxy.sdk.param.EditMessageParams;
import dev.postproxy.sdk.param.ListMessagesParams;
import dev.postproxy.sdk.param.ReactParams;
import dev.postproxy.sdk.param.SendMessageParams;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ManageMessages {

    public static void main(String[] args) {
        var client = PostProxy.builder(System.getenv("POSTPROXY_API_KEY"))
                .profileGroupId(System.getenv("POSTPROXY_PROFILE_GROUP_ID"))
                .build();

        var profileId = "your-profile-id";

        // Create or find a chat with a participant
        var chat = client.chats().create(profileId, CreateChatParams.builder("igsid_8675309")
                .participantUsername("jane_doe")
                .participantName("Jane Doe")
                .build());
        System.out.println("Chat: " + chat.id() + " with " + chat.participantUsername());

        // List chats for the profile
        var chats = client.chats().list(profileId);
        System.out.println("Total chats: " + chats.total());
        for (var c : chats.data()) {
            System.out.println("  " + c.id() + " -> " + c.participantExternalId());
        }

        // List messages in a chat (inbound only)
        var messages = client.messages().list(chat.id(), ListMessagesParams.builder()
                .direction(MessageDirection.INBOUND)
                .build());
        for (var msg : messages.data()) {
            System.out.println("  [" + msg.direction().getValue() + "] " + msg.body());
            for (var att : msg.attachments()) {
                System.out.println("    attachment " + att.type() + ": " + att.url());
            }
            for (var r : msg.reactions()) {
                System.out.println("    reaction " + r.reaction() + " from " + r.senderExternalId());
            }
        }

        // Send a text message
        var sent = client.messages().send(chat.id(), SendMessageParams.builder()
                .body("Yes, we ship worldwide!")
                .build());
        System.out.println("Sent: " + sent.id() + " (status: " + sent.status().getValue() + ")");

        // Send a message outside the 24h window with a tag
        client.messages().send(chat.id(), SendMessageParams.builder()
                .body("Following up on your order.")
                .tag("HUMAN_AGENT")
                .build());

        // Send media (local file)
        client.messages().send(chat.id(), SendMessageParams.builder()
                .mediaFiles(List.of(Path.of("./photo.png")))
                .build());

        // Send media (hosted URL)
        client.messages().send(chat.id(), SendMessageParams.builder()
                .media(List.of("https://cdn.example.com/photo.png"))
                .build());

        // React / unreact (Facebook & Instagram)
        client.messages().react(sent.id(), ReactParams.builder().reaction("love").build());
        client.messages().unreact(sent.id());

        // Edit a message (Telegram only) — including an inline keyboard
        client.messages().edit(sent.id(), EditMessageParams.builder()
                .body("Updated answer: we ship to 90+ countries.")
                .replyMarkup(Map.of("inline_keyboard", List.of(List.of(
                        Map.of("text", "Track order", "url", "https://shop.example.com/orders/123")))))
                .build());

        // Archive / unarchive (Bluesky only)
        var archived = client.chats().archive(chat.id());
        System.out.println("Archived: " + archived.archived());
        client.chats().unarchive(chat.id());

        // Private reply to a comment -> returns a Message
        var reply = client.comments().privateReply(
                "your-post-id", "comment-id", profileId,
                "Thanks for your comment — DM-ing you the details.");
        System.out.println("Private reply message: " + reply.id() + " in chat " + reply.chatId());
    }
}
