import dev.postproxy.sdk.PostProxy;
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
