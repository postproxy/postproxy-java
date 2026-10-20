import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.param.PinterestParams;
import dev.postproxy.sdk.param.PlatformParams;
import dev.postproxy.sdk.param.UpdatePostParams;
import dev.postproxy.sdk.param.YouTubeParams;
import dev.postproxy.sdk.webhook.WebhookEvents;

public class EditPublishedPost {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY").build();

        // Only the body, the YouTube title and the Pinterest title / destinationLink
        // can change once a post is published.
        var post = client.posts().update("published-post-id", UpdatePostParams.builder()
                .body("Launch day! (now with the right link)")
                .platforms(PlatformParams.builder()
                        .youtube(YouTubeParams.builder().title("Launch day — full walkthrough").build())
                        .pinterest(PinterestParams.builder().destinationLink("https://example.com/launch").build())
                        .build())
                .build());
        post.platforms().forEach(p -> System.out.println(p.platform().getValue() + ": " + p.permalink()));
    }

    // The edit is pushed to each platform in the background. Your webhook handler
    // learns how it went:
    public static void handleWebhook(String rawBody) {
        var event = WebhookEvents.parse(rawBody);
        switch (event.type()) {
            case PLATFORM_POST_EDITED -> {
                var data = WebhookEvents.asPlatformPost(event);
                System.out.println("Edited on " + data.platform().getValue());
            }
            case PLATFORM_POST_EDIT_FAILED -> {
                var data = WebhookEvents.asPlatformPost(event);
                System.out.println("Edit failed on " + data.platform().getValue() + ": " + data.error());
            }
            default -> { }
        }
    }
}
