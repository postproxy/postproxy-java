import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.CreateQueueParams;
import dev.postproxy.sdk.param.UpdateQueueParams;

import java.util.List;
import java.util.Map;

public class ManageQueues {

        public static void main(String[] args) {
                var client = PostProxy.builder("POSTPROXY_API_KEY")
                                .profileGroupId("your-profile-group-id")
                                .build();

                // List all queues
                var queues = client.queues().list();
                System.out.println("Queues: " + queues.data().size());

                // Create a queue with timeslots
                var queue = client.queues().create(CreateQueueParams.builder()
                                .name("Morning Posts")
                                .profileGroupId("your-profile-group-id")
                                .description("Weekday morning content")
                                .timezone("America/New_York")
                                .jitter(10)
                                .timeslots(List.of(
                                                Map.of("day", 1, "time", "09:00"),
                                                Map.of("day", 2, "time", "09:00"),
                                                Map.of("day", 3, "time", "09:00")))
                                .build());
                System.out.println("Created queue: " + queue.id() + " " + queue.name());
                System.out.println("Timeslots: " + queue.timeslots().size());

                // Get a queue
                var fetched = client.queues().get(queue.id());
                System.out.println("Fetched: " + fetched.name() + " enabled=" + fetched.enabled());

                // Get next available slot
                var nextSlot = client.queues().nextSlot(queue.id());
                System.out.println("Next slot: " + nextSlot.nextSlot());

                // Update the queue — add a timeslot and change jitter
                var updated = client.queues().update(queue.id(), UpdateQueueParams.builder()
                                .jitter(15)
                                .timeslots(List.of(
                                                Map.of("day", 4, "time", "09:00")))
                                .build());
                System.out.println("Updated jitter: " + updated.jitter());

                // Pause the queue
                var paused = client.queues().update(queue.id(), UpdateQueueParams.builder()
                                .enabled(false)
                                .build());
                System.out.println("Paused: enabled=" + paused.enabled());

                // Add a post to the queue
                var profiles = client.profiles().list();
                var profileId = profiles.data().get(0).id();

                var post = client.posts().create(CreatePostParams.builder()
                                .body("This post will be scheduled by the queue")
                                .profiles(List.of(profileId))
                                .queueId(queue.id())
                                .queuePriority("high")
                                .build());
                System.out.println("Queued post: " + post.id());

                // Delete the queue
                var deleted = client.queues().delete(queue.id());
                System.out.println("Deleted: " + deleted.deleted());
        }
}
