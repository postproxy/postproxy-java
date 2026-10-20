import dev.postproxy.sdk.PostProxy;

import java.util.List;
import java.util.Map;

public class WhatsAppGroups {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY").build();
        var profileId = "your-whatsapp-profile-id";
        var phoneNumberId = "your-phone-number-id";
        var wa = client.whatsapp();

        // Business profile
        wa.updateBusinessProfile(profileId, phoneNumberId, Map.of(
                "about", "Fresh coffee, fast replies",
                "websites", List.of("https://example.com")));

        // Create a group and share its invite link
        var group = wa.createGroup(profileId, phoneNumberId, "VIP customers",
                "Early access to new roasts", "approval_required", null, null);
        var groupId = String.valueOf(group.get("id"));
        var invite = wa.createGroupInviteLink(profileId, phoneNumberId, groupId);
        System.out.println("Invite link: " + invite);

        // Approve everyone waiting to join
        var requests = wa.groupJoinRequests(profileId, phoneNumberId, groupId);
        System.out.println("Join requests: " + requests);
        wa.approveGroupJoinRequests(profileId, phoneNumberId, groupId, List.of("15551234567"));

        // Block a spammer
        wa.blockUsers(profileId, phoneNumberId, List.of("15557654321"));

        // Report a purchase back to Meta for a click-to-WhatsApp ad
        wa.sendConversionEvent(profileId, Map.of(
                "event_name", "Purchase",
                "event_id", "order-1234",
                "phone", "15551234567",
                "currency", "USD",
                "value", 24.5));
    }
}
