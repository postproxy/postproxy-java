import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.exception.ConflictException;
import dev.postproxy.sdk.model.PostSync;
import dev.postproxy.sdk.model.PostSyncStatus;

/** Backfill a profile's older posts and follow the sync run to completion. */
public class BackfillPosts {

    public static void main(String[] args) throws InterruptedException {
        var client = PostProxy.builder(System.getenv("POSTPROXY_API_KEY"))
                .profileGroupId(System.getenv("POSTPROXY_PROFILE_GROUP_ID"))
                .build();

        var profileId = "your-profile-id";

        // Start a backfill. It walks the profile's feed backwards from the
        // newest post in batches of 25 and stops at `from` — or earlier, if the
        // platform stops returning history. Runs in the background.
        PostSync sync;
        try {
            sync = client.profiles().backfillPosts(profileId, "2025-01-01");
        } catch (ConflictException e) {
            // Only one backfill runs per profile at a time; the running one
            // already covers any window a second request could ask for.
            var runningId = String.valueOf(e.getResponse().get("profile_sync_id"));
            System.out.println("Backfill already running: " + runningId);
            sync = client.profiles().postSync(profileId, runningId);
        }

        System.out.println("Backfill " + sync.id() + " — status: " + sync.status().getValue());

        // Poll until it finishes.
        while (sync.status() == PostSyncStatus.PENDING || sync.status() == PostSyncStatus.RUNNING) {
            Thread.sleep(5000);
            sync = client.profiles().postSync(profileId, sync.id());
            System.out.println("  " + sync.status().getValue() + ": " + sync.postsImported()
                    + " imported of " + sync.postsSeen() + " seen, reached back to " + sync.oldestPostedAt());
        }

        if (sync.status() == PostSyncStatus.FAILED) {
            System.out.println("Backfill failed: " + sync.error());
        } else {
            System.out.println("Done. Imported " + sync.postsImported() + " posts");
        }

        // Every pull is recorded — the sync fired on connect, the recurring
        // poll, and each backfill. Runs are kept for 30 days.
        var runs = client.profiles().postSyncs(profileId, null, null, null, 10, null);
        System.out.println("\nRecent post syncs (" + runs.total() + "):");
        for (var run : runs.data()) {
            System.out.println("  " + run.createdAt() + " " + run.trigger().getValue()
                    + " → " + run.status().getValue()
                    + " (" + run.postsImported() + "/" + run.postsSeen() + " new)");
        }
    }
}
