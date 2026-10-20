import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.ConnectionOnboarding;
import dev.postproxy.sdk.model.Platform;

public class InitializeConnection {

    public static void main(String[] args) {
        var client = PostProxy.builder("your-api-key")
                .profileGroupId("your-profile-group-id")
                .build();

        // List profile groups
        var groups = client.profileGroups().list();
        System.out.println("Profile Groups: " + groups.data());

        // Initialize a connection
        var connection = client.profileGroups().initializeConnection(
                "your-profile-group-id",
                Platform.INSTAGRAM,
                "https://your-app.com/callback"
        );
        System.out.println("Connection URL: " + connection.url());

        // Instagram via Facebook login (needed to attach music to reels)
        var igViaFacebook = client.profileGroups().initializeConnection(
                "your-profile-group-id",
                Platform.INSTAGRAM,
                "https://your-app.com/callback",
                ConnectionOnboarding.FACEBOOK,
                null
        );
        System.out.println("Instagram (Facebook login) URL: " + igViaFacebook.url());

        // WhatsApp through the WhatsApp Business app (API for the Cloud API)
        var whatsapp = client.profileGroups().initializeConnection(
                "your-profile-group-id",
                Platform.WHATSAPP,
                "https://your-app.com/callback",
                ConnectionOnboarding.BUSINESS_APP,
                null
        );
        System.out.println("WhatsApp URL: " + whatsapp.url());

        // After connecting, list a profile's placements (Pages, channels, locations)
        var placements = client.profiles().placements("profile-id");
        placements.data().forEach(p -> System.out.println("Placement: " + p.name() + " (" + p.id() + ")"));

        // Move one placement to a different profile group
        if (!placements.data().isEmpty()) {
            client.profiles().assignPlacementToGroup(
                    "profile-id", placements.data().get(0).id(), "other-group-id");
        }
    }
}
