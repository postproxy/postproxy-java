import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.model.YouTubePrivacy;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.PlatformParams;
import dev.postproxy.sdk.param.YouTubeParams;

import java.util.List;

public class YouTubePlaylists {

    public static void main(String[] args) {
        var client = PostProxy.builder(System.getenv("POSTPROXY_API_KEY")).build();
        var profileId = "your-youtube-profile-id";

        // List the channel's playlists, page by page
        String pageToken = null;
        do {
            var page = client.profiles().youtubePlaylists(profileId, pageToken);
            for (var p : page.data()) {
                System.out.println(p.id() + ": " + p.title() + " (" + p.privacyStatus().getValue() + ", "
                        + p.itemCount() + " videos)");
            }
            pageToken = page.nextPageToken();
        } while (pageToken != null);

        // Create a playlist and publish a video into it
        var playlist = client.profiles().createYouTubePlaylist(profileId, "Tutorials",
                "Step-by-step guides", YouTubePrivacy.PUBLIC);

        var post = client.posts().create(CreatePostParams.builder()
                .body("How to set up your account")
                .profiles(List.of(profileId))
                .media(List.of("https://example.com/tutorial.mp4"))
                .platforms(PlatformParams.builder()
                        .youtube(YouTubeParams.builder()
                                .title("How to set up your account")
                                .playlistId(playlist.id())
                                .build())
                        .build())
                .build());
        System.out.println("Post: " + post.id());

        // Rename it later; null arguments keep their value
        client.profiles().updateYouTubePlaylist(profileId, playlist.id(), "Getting started", null, null);

        // Deleting a playlist keeps its videos
        // client.profiles().deleteYouTubePlaylist(profileId, playlist.id());
    }
}
