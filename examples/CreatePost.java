import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.InstagramFormat;
import dev.postproxy.sdk.model.TikTokPrivacy;
import dev.postproxy.sdk.param.*;
import dev.postproxy.sdk.param.ThreadChildInput;

import java.util.List;

public class CreatePost {

        public static void main(String[] args) {
                var client = PostProxy.builder("POSTPROXY_API_KEY")
                                .profileGroupId("your-profile-group-id")
                                .build();

                // List profiles
                var profiles = client.profiles().list();
                System.out.println("Profiles: " + profiles.data());

                var profileId = profiles.data().get(0).id();

                // Create a simple draft
                var post = client.posts().create(CreatePostParams.builder()
                                .body("Hello from PostProxy SDK!")
                                .profiles(List.of(profileId))
                                .draft(true)
                                .build());
                System.out.println("Created post: " + post);

                // Create a post with media URLs
                var postWithMedia = client.posts().create(CreatePostParams.builder()
                                .body("Check out this image!")
                                .profiles(List.of(profileId))
                                .media(List.of("https://example.com/image.jpg"))
                                .draft(true)
                                .build());
                System.out.println("Post with media: " + postWithMedia);

                // Create a post with platform-specific params
                var postWithParams = client.posts().create(CreatePostParams.builder()
                                .body("Cross-platform post!")
                                .profiles(List.of(profileId))
                                .platforms(PlatformParams.builder()
                                                .instagram(InstagramParams.builder()
                                                                .format(InstagramFormat.REEL)
                                                                .collaborators(List.of("friend_username"))
                                                                .build())
                                                .tiktok(TikTokParams.builder()
                                                                .privacyStatus(TikTokPrivacy.PUBLIC_TO_EVERYONE)
                                                                .madeWithAi(true)
                                                                .build())
                                                .build())
                                .build());
                System.out.println("Post with platform params: " + postWithParams);

                // Create a scheduled post
                var scheduledPost = client.posts().create(CreatePostParams.builder()
                                .body("Scheduled post")
                                .profiles(List.of(profileId))
                                .scheduledAt("2025-12-25T09:00:00Z")
                                .build());
                System.out.println("Scheduled post: " + scheduledPost);

                // Publish a draft
                var published = client.posts().publishDraft(post.id());
                System.out.println("Published: " + published);

                // List posts with filters
                var postList = client.posts().list(ListPostsParams.builder()
                                .page(0)
                                .perPage(10)
                                .build());
                System.out.println("Posts: " + postList);

                // Create a thread post
                var threadPost = client.posts().create(CreatePostParams.builder()
                                .body("Here's a thread about PostProxy \uD83E\uDDF5")
                                .profiles(List.of(profileId))
                                .thread(List.of(
                                                ThreadChildInput.builder().body("First, connect your social accounts.")
                                                                .build(),
                                                ThreadChildInput.builder().body("Then, create posts with media!")
                                                                .media(List.of("https://example.com/demo.jpg")).build(),
                                                ThreadChildInput.builder()
                                                                .body("Finally, schedule or publish instantly.")
                                                                .build()))
                                .build());
                System.out.println(
                                "Thread post: " + threadPost.id() + " (" + threadPost.thread().size() + " children)");

                // Delete a post
                var deleted = client.posts().delete(post.id());
                System.out.println("Deleted: " + deleted);
        }
}
