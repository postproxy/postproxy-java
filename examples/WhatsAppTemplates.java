import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.MessageLocation;
import dev.postproxy.sdk.model.MessageTemplateInput;
import dev.postproxy.sdk.param.CreateChatParams;
import dev.postproxy.sdk.param.SendMessageParams;
import dev.postproxy.sdk.param.WhatsAppTemplateInput;

import java.util.List;
import java.util.Map;

public class WhatsAppTemplates {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY").build();
        var profileId = "your-whatsapp-profile-id";

        // Create a template — Meta reviews it before it can be sent
        var template = client.whatsappTemplates().create(profileId,
                WhatsAppTemplateInput.builder("order_shipped", "UTILITY", "en_US")
                        .components(List.of(Map.of("type", "BODY", "text", "Hi {{1}}, your order {{2}} has shipped.")))
                        .build());
        System.out.println(template.name() + " " + template.status()); // "order_shipped PENDING"

        // Check approval status (or subscribe to message_template.status_updated)
        var approved = client.whatsappTemplates().list(profileId, null, null, "APPROVED", true, null);
        approved.data().forEach(t -> System.out.println("Approved: " + t.name()));

        // Send it to a customer. The phone number id is a placement of the profile.
        var numbers = client.profiles().placements(profileId);
        var chat = client.chats().create(profileId, CreateChatParams.builder("15551234567")
                .placementId(numbers.data().get(0).id())
                .build());

        client.messages().send(chat.id(), SendMessageParams.builder()
                .template(MessageTemplateInput.builder()
                        .name("order_shipped")
                        .language("en_US")
                        .variables(List.of("Jane", "#1234"))
                        .build())
                .build());

        // Inside the 24h window, free-form messages work too
        var fresh = client.chats().get(chat.id());
        if (Boolean.TRUE.equals(fresh.withinMessagingWindow())) {
            client.messages().send(chat.id(), SendMessageParams.builder()
                    .location(new MessageLocation(52.52, 13.405, "Pickup point", null))
                    .build());
        }
    }
}
