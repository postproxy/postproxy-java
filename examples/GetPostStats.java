import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.param.GetStatsParams;

import java.util.List;

public class GetPostStats {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY")
                .profileGroupId("your-profile-group-id")
                .build();

        // Get stats for multiple posts
        var stats = client.posts().stats(GetStatsParams.builder()
                .postIds(List.of("post-id-1", "post-id-2"))
                .build());

        // Iterate over each post's stats
        stats.data().forEach((postId, postStats) -> {
            System.out.println("Post: " + postId);
            for (var platform : postStats.platforms()) {
                System.out.println("  Platform: " + platform.platform() + " (profile: " + platform.profileId() + ")");
                for (var record : platform.records()) {
                    System.out.println("    " + record.recordedAt() + ": " + record.stats());
                }
            }
        });

        // Filter by profiles/networks and time range
        var filtered = client.posts().stats(GetStatsParams.builder()
                .postIds(List.of("post-id-1"))
                .profiles(List.of("instagram", "twitter"))
                .from("2026-02-01T00:00:00Z")
                .to("2026-02-24T00:00:00Z")
                .build());

        System.out.println("Filtered stats: " + filtered.data());
    }
}
