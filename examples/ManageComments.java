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

        // Create a comment
        var newComment = client.comments().create(postId, profileId, "Thanks for the feedback!");
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
