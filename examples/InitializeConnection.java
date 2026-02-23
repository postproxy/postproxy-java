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
    }
}
