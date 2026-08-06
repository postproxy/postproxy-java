import dev.postproxy.sdk.PostProxy;

public class ManageComments {

    public static void main(String[] args) {
        var client = PostProxy.builder(System.getenv("POSTPROXY_API_KEY"))
                .profileGroupId(System.getenv("POSTPROXY_PROFILE_GROUP_ID"))
                .build();

        var postId = "your-post-id";
        var profileId = "your-profile-id";

        // List comments on a post
        var comments = client.comments().list(postId, profileId);
        System.out.println("Total comments: " + comments.total());
        for (var comment : comments.data()) {
            System.out.println("  " + comment.authorUsername() + ": " + comment.body());
            if (comment.metadata() != null) {
                System.out.println("    metadata: " + comment.metadata());
            }
            if (comment.attachments() != null) {
                for (var att : comment.attachments()) {
                    System.out.println("    attachment " + att.type() + ": " + att.url());
                }
            }
            for (var reply : comment.replies()) {
                System.out.println("    " + reply.authorUsername() + ": " + reply.body());
            }
        }

        // Filter the per-post list by when PostProxy received the comment
        var recent = client.comments().list(postId, profileId, null, null, "2026-03-25", "2026-03-26");
        System.out.println("Comments received 2026-03-25..26: " + recent.total());

        // List comments across every post in the profile group. Flat: replies
        // come back as their own entries linked by parentExternalId.
        var across = client.comments().listAll(
                null, java.util.List.of("instagram"), "2026-03-25", null, null, 50, null);
        System.out.println("Comments across posts: " + across.total());
        for (var c : across.data()) {
            var kind = c.parentExternalId() == null ? "comment" : "reply";
            System.out.println("  [" + c.platform().getValue() + "] " + kind + " on post " + c.postId()
                    + " — " + c.authorUsername() + ": " + c.body());
        }

        // Create a comment. An idempotency key makes the write safe to retry
        // after a dropped connection — the retry replays the original response
        // instead of posting a second comment.
        var newComment = client.comments().create(
                postId, profileId, "Thanks for the feedback!", null, java.util.UUID.randomUUID().toString());
        System.out.println("Created: " + newComment.id() + " (status: " + newComment.status() + ")");

        // Reply to a comment
        var reply = client.comments().create(postId, profileId, "Glad you liked it!", newComment.id());
        System.out.println("Reply: " + reply.id());

        // Hide / unhide
        client.comments().hide(postId, newComment.id(), profileId);
        System.out.println("Comment hidden");

        client.comments().unhide(postId, newComment.id(), profileId);
        System.out.println("Comment unhidden");

        // Like / unlike
        client.comments().like(postId, newComment.id(), profileId);
        System.out.println("Comment liked");

        client.comments().unlike(postId, newComment.id(), profileId);
        System.out.println("Comment unliked");

        // Send a DM in private reply to a comment (returns a Message)
        var privateReply = client.comments().privateReply(
                postId, newComment.id(), profileId,
                "Thanks for your comment — DM-ing you the details.");
        System.out.println("Private reply message: " + privateReply.id());

        // Delete
        client.comments().delete(postId, newComment.id(), profileId);
        System.out.println("Comment deleted");
    }
}
