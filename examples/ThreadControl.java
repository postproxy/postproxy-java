import dev.postproxy.sdk.PostProxy;

public class ThreadControl {

    // Meta's own inbox apps
    private static final String PAGE_INBOX_APP_ID = "263902037430900";
    private static final String INSTAGRAM_INBOX_APP_ID = "1217981644879628";

    public static void main(String[] args) {
        var client = PostProxy.builder(System.getenv("POSTPROXY_API_KEY")).build();

        // A Facebook or Instagram chat (for Instagram, pass INSTAGRAM_INBOX_APP_ID below)
        var chatId = "your-chat-id";

        // Who controls the conversation right now? Both fields are null when idle.
        var owner = client.chats().threadOwner(chatId);
        System.out.println("Owner app: " + owner.appId() + " until " + owner.expiresAt());

        // Escalate to a human in Meta's inbox
        var chat = client.chats().passThreadControl(chatId, PAGE_INBOX_APP_ID, "escalated to support");
        System.out.println("Thread control: " + chat.metadata().get("thread_control"));

        // Later: take it back (needs Conversation Routing on the Page) ...
        client.chats().takeThreadControl(chatId);
        // ... or, without Conversation Routing, ask the owner to pass it back and
        // wait for the chat.thread_control_changed webhook.
        // client.chats().requestThreadControl(chatId);

        // Done: let the conversation go idle
        client.chats().releaseThreadControl(chatId);
    }
}
