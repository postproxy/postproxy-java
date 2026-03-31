# PostProxy Java SDK

Java client for the [PostProxy API](https://postproxy.dev). Uses Java records for models, `java.net.http.HttpClient` for HTTP, and Jackson for JSON. No external HTTP dependencies.

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.postproxy:postproxy-java:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'dev.postproxy:postproxy-java:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>dev.postproxy</groupId>
    <artifactId>postproxy-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

Requires Java 17+.

## Quick start

```java
import dev.postproxy.sdk.PostProxy;
import dev.postproxy.sdk.param.CreatePostParams;
import java.util.List;

var client = PostProxy.builder("your-api-key")
    .profileGroupId("pg-abc")
    .build();

// List profiles
var profiles = client.profiles().list();

// Create a post
var post = client.posts().create(CreatePostParams.builder()
    .body("Hello from PostProxy!")
    .profiles(List.of(profiles.data().get(0).id()))
    .build());
System.out.println(post.id() + " " + post.status());
```

## Usage

### Client

```java
import dev.postproxy.sdk.PostProxy;

// Basic
var client = PostProxy.builder("your-api-key").build();

// With a default profile group (applied to all requests)
var client = PostProxy.builder("your-api-key")
    .profileGroupId("pg-abc")
    .build();

// With a custom base URL
var client = PostProxy.builder("your-api-key")
    .baseUrl("https://custom.postproxy.dev")
    .build();
```

### Posts

```java
import dev.postproxy.sdk.param.*;
import dev.postproxy.sdk.model.*;
import java.util.List;
import java.nio.file.Path;

// List posts (paginated)
var page = client.posts().list(ListPostsParams.builder()
    .page(0).perPage(10).status(PostStatus.DRAFT).build());
System.out.println(page.total() + " " + page.data());

// Filter by platform and schedule
var page = client.posts().list(ListPostsParams.builder()
    .platforms(List.of(Platform.INSTAGRAM, Platform.TIKTOK))
    .scheduledAfter("2025-06-01T00:00:00Z")
    .build());

// Get a single post
var post = client.posts().get("post-id");

// Create a post
var post = client.posts().create(CreatePostParams.builder()
    .body("Check out our new product!")
    .profiles(List.of("profile-id-1", "profile-id-2"))
    .build());

// Create a draft
var post = client.posts().create(CreatePostParams.builder()
    .body("Draft content")
    .profiles(List.of("profile-id"))
    .draft(true)
    .build());

// Create with media URLs
var post = client.posts().create(CreatePostParams.builder()
    .body("Photo post")
    .profiles(List.of("profile-id"))
    .media(List.of("https://example.com/image.jpg"))
    .build());

// Create with local file uploads
var post = client.posts().create(CreatePostParams.builder()
    .body("Posted with a local file!")
    .profiles(List.of("profile-id"))
    .mediaFiles(List.of(Path.of("./photo.jpg"), Path.of("./video.mp4")))
    .build());

// Create with platform-specific params
var post = client.posts().create(CreatePostParams.builder()
    .body("Cross-platform post")
    .profiles(List.of("ig-profile", "tt-profile"))
    .platforms(PlatformParams.builder()
        .instagram(InstagramParams.builder()
            .format(InstagramFormat.REEL)
            .collaborators(List.of("@friend"))
            .build())
        .tiktok(TikTokParams.builder()
            .privacyStatus(TikTokPrivacy.PUBLIC_TO_EVERYONE)
            .build())
        .build())
    .build());

// Schedule a post
var post = client.posts().create(CreatePostParams.builder()
    .body("Scheduled post")
    .profiles(List.of("profile-id"))
    .scheduledAt("2025-12-25T09:00:00Z")
    .build());

// Create a thread post
import dev.postproxy.sdk.param.ThreadChildInput;

var post = client.posts().create(CreatePostParams.builder()
    .body("Thread starts here")
    .profiles(List.of("profile-id"))
    .thread(List.of(
        ThreadChildInput.builder().body("Second post in the thread").build(),
        ThreadChildInput.builder().body("Third with media")
            .media(List.of("https://example.com/img.jpg")).build()
    ))
    .build());
for (var child : post.thread()) {
    System.out.println(child.id() + ": " + child.body());
}

// Update a post (only drafts or scheduled posts)
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .body("Updated content!")
    .build());

// Update platform params only
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .platforms(PlatformParams.builder()
        .youtube(YouTubeParams.builder().privacyStatus("unlisted").build())
        .build())
    .build());

// Replace profiles and media
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .profiles(List.of("twitter", "threads"))
    .media(List.of("https://example.com/new-image.jpg"))
    .build());

// Replace thread children
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .thread(List.of(
        ThreadChildInput.builder().body("Updated first reply").build(),
        ThreadChildInput.builder().body("Updated second reply")
            .media(List.of("https://example.com/img.jpg")).build()
    ))
    .build());

// Remove all media
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .media(List.of())
    .build());

// Publish a draft
var post = client.posts().publishDraft("post-id");

// Delete a post
var result = client.posts().delete("post-id");
System.out.println(result.deleted()); // true
```

### Threads

```java
import dev.postproxy.sdk.param.ThreadChildInput;

var post = client.posts().create(CreatePostParams.builder()
    .body("Thread starts here")
    .profiles(List.of("profile-id"))
    .thread(List.of(
        ThreadChildInput.builder().body("Second post in the thread").build(),
        ThreadChildInput.builder().body("Third with media")
            .media(List.of("https://example.com/img.jpg")).build()
    ))
    .build());
for (var child : post.thread()) {
    System.out.println(child.id() + ": " + child.body());
}
```

### Queues

```java
import dev.postproxy.sdk.param.*;
import dev.postproxy.sdk.model.*;
import java.util.List;
import java.util.Map;

// List all queues
var queues = client.queues().list();

// Get a queue
var queue = client.queues().get("queue-id");

// Get next available slot
var nextSlot = client.queues().nextSlot("queue-id");
System.out.println(nextSlot.nextSlot());

// Create a queue with timeslots
var queue = client.queues().create(CreateQueueParams.builder()
    .name("Morning Posts")
    .profileGroupId("pg-abc")
    .description("Weekday morning content")
    .timezone("America/New_York")
    .jitter(10)
    .timeslots(List.of(
        Map.of("day", 1, "time", "09:00"),
        Map.of("day", 2, "time", "09:00"),
        Map.of("day", 3, "time", "09:00")
    ))
    .build());

// Update a queue
var queue = client.queues().update("queue-id", UpdateQueueParams.builder()
    .jitter(15)
    .timeslots(List.of(
        Map.of("day", 6, "time", "10:00"),           // add new timeslot
        Map.of("id", 1, "_destroy", true)              // remove existing timeslot
    ))
    .build());

// Pause/unpause a queue
client.queues().update("queue-id", UpdateQueueParams.builder()
    .enabled(false).build());

// Delete a queue
var result = client.queues().delete("queue-id");

// Add a post to a queue
var post = client.posts().create(CreatePostParams.builder()
    .body("This post will be scheduled by the queue")
    .profiles(List.of("profile-id"))
    .queueId("queue-id")
    .queuePriority("high")
    .build());
```

### Webhooks

```java
import dev.postproxy.sdk.param.*;
import dev.postproxy.sdk.model.Webhook;

// List webhooks
var webhooks = client.webhooks().list();

// Get a webhook
var webhook = client.webhooks().get("wh-id");

// Create a webhook
var webhook = client.webhooks().create(CreateWebhookParams.builder()
    .url("https://example.com/webhook")
    .events(List.of("post.published", "post.failed"))
    .description("My webhook")
    .build());
System.out.println(webhook.secret());

// Update a webhook
var webhook = client.webhooks().update("wh-id", UpdateWebhookParams.builder()
    .events(List.of("post.published"))
    .enabled(false)
    .build());

// Delete a webhook
client.webhooks().delete("wh-id");

// List deliveries
var deliveries = client.webhooks().deliveries("wh-id", 0, 10);
for (var d : deliveries.data()) {
    System.out.println(d.eventType() + ": " + d.success());
}
```

#### Signature verification

Verify incoming webhook signatures using HMAC-SHA256:

```java
import dev.postproxy.sdk.WebhookSignature;

boolean isValid = WebhookSignature.verify(
    requestBody,                              // raw request body string
    request.getHeader("X-PostProxy-Signature"),  // "t=...,v1=..."
    "whsec_..."                               // webhook secret
);
```

### Comments

```java
import dev.postproxy.sdk.model.Comment;

// List comments on a post (paginated)
var comments = client.comments().list("post-id", "profile-id");
for (var comment : comments.data()) {
    System.out.println(comment.authorUsername() + ": " + comment.body());
    for (var reply : comment.replies()) {
        System.out.println("  " + reply.authorUsername() + ": " + reply.body());
    }
}

// List with pagination
var comments = client.comments().list("post-id", "profile-id", 2, 10);

// Get a single comment
var comment = client.comments().get("post-id", "comment-id", "profile-id");

// Create a comment
var comment = client.comments().create("post-id", "profile-id", "Great post!");

// Reply to a comment
var reply = client.comments().create("post-id", "profile-id", "Thanks!", "comment-id");

// Delete a comment
var result = client.comments().delete("post-id", "comment-id", "profile-id");
System.out.println(result.accepted()); // true

// Hide / unhide a comment
client.comments().hide("post-id", "comment-id", "profile-id");
client.comments().unhide("post-id", "comment-id", "profile-id");

// Like / unlike a comment
client.comments().like("post-id", "comment-id", "profile-id");
client.comments().unlike("post-id", "comment-id", "profile-id");
```

### Post Stats

Retrieve stats snapshots for one or more posts. Returns all matching snapshots so you can see trends over time.

```java
import dev.postproxy.sdk.param.GetStatsParams;
import java.util.List;

// Get stats for posts
var stats = client.posts().stats(GetStatsParams.builder()
    .postIds(List.of("post-id-1", "post-id-2"))
    .build());

// Iterate results
stats.data().forEach((postId, postStats) -> {
    for (var platform : postStats.platforms()) {
        System.out.println(platform.platform() + " " + platform.profileId());
        for (var record : platform.records()) {
            System.out.println(record.recordedAt() + ": " + record.stats());
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
```

The `profiles` parameter accepts network names (`instagram`, `twitter`, etc.) or profile hashids, or a mix. The `stats` map in each record contains platform-specific metrics:

| Platform | Metrics |
|---|---|
| Instagram | impressions, likes, comments, saved, profile_visits, follows |
| Facebook | impressions, clicks, likes |
| Threads | impressions, likes, replies, reposts, quotes, shares |
| Twitter | impressions, likes, retweets, comments, quotes, saved |
| YouTube | impressions, likes, comments, saved |
| LinkedIn | impressions |
| TikTok | impressions, likes, comments, shares |
| Pinterest | impressions, likes, comments, saved, outbound_clicks |

### Profiles

```java
// List all profiles
var profiles = client.profiles().list();

// List profiles in a specific group (overrides client default)
var profiles = client.profiles().list("pg-other");

// Get a single profile
var profile = client.profiles().get("profile-id");
System.out.println(profile.name() + " " + profile.platform() + " " + profile.status());

// Get available placements for a profile
var placements = client.profiles().placements("profile-id");
for (var p : placements.data()) {
    System.out.println(p.id() + " " + p.name());
}

// Delete a profile
var result = client.profiles().delete("profile-id");
System.out.println(result.success()); // true
```

### Profile Groups

```java
import dev.postproxy.sdk.model.Platform;

// List all groups
var groups = client.profileGroups().list();

// Get a single group
var group = client.profileGroups().get("pg-id");
System.out.println(group.name() + " " + group.profilesCount());

// Create a group
var group = client.profileGroups().create("My New Group");

// Delete a group (must have no profiles)
var result = client.profileGroups().delete("pg-id");
System.out.println(result.deleted()); // true

// Initialize a social platform connection
var conn = client.profileGroups().initializeConnection(
    "pg-id",
    Platform.INSTAGRAM,
    "https://yourapp.com/callback"
);
System.out.println(conn.url()); // Redirect the user to this URL
```

## Error handling

All errors extend `PostProxyException`, which includes the HTTP status code and raw response body:

```java
import dev.postproxy.sdk.exception.*;

try {
    client.posts().get("nonexistent");
} catch (NotFoundException e) {
    System.out.println(e.getStatusCode());  // 404
    System.out.println(e.getResponse());    // {error=Not found}
} catch (PostProxyException e) {
    System.out.println("API error " + e.getStatusCode() + ": " + e.getMessage());
}
```

Exception hierarchy:

| Exception | HTTP Status |
|---|---|
| `PostProxyException` | Base class |
| `AuthenticationException` | 401 |
| `BadRequestException` | 400 |
| `NotFoundException` | 404 |
| `ValidationException` | 422 |

## Types

All list methods return a response object with a `data()` list:

```java
var profiles = client.profiles().list().data();
var posts = client.posts().list().data();  // PaginatedResponse also has total(), page(), perPage()
```

Key types:

| Type | Fields |
|---|---|
| `Post` | id, body, status, scheduledAt, createdAt, media, thread, platforms, queueId, queuePriority |
| `Profile` | id, name, status, platform, profileGroupId, expiresAt, postCount |
| `ProfileGroup` | id, name, profilesCount |
| `Media` | id, type, url, status |
| `ThreadChild` | id, body, media |
| `ThreadChildInput` | body, media |
| `Webhook` | id, url, events, secret, enabled, description, createdAt |
| `WebhookDelivery` | id, eventId, eventType, responseStatus, attemptNumber, success, attemptedAt, createdAt |
| `PlatformResult` | platform, status, params, error, attemptedAt, insights |
| `ListResponse<T>` | data |
| `Comment` | id, externalId, body, status, authorUsername, authorAvatarUrl, authorExternalId, parentExternalId, likeCount, isHidden, permalink, platformData, postedAt, createdAt, replies |
| `AcceptedResponse` | accepted |
| `PaginatedResponse<T>` | data, total, page, perPage |
| `StatsResponse` | data (Map&lt;String, PostStats&gt;) |
| `PostStats` | platforms |
| `PlatformStats` | profileId, platform, records |
| `StatsRecord` | stats (Map), recordedAt |
| `Queue` | id, name, description, timezone, enabled, jitter, profileGroupId, timeslots, postsCount |
| `Timeslot` | id, day, time |
| `NextSlotResponse` | nextSlot |

### Platform parameter types

| Type | Platform |
|---|---|
| `FacebookParams` | format (`post`, `story`), firstComment, pageId |
| `InstagramParams` | format (`post`, `reel`, `story`), firstComment, collaborators, coverUrl, audioName, trialStrategy, thumbOffset |
| `TikTokParams` | format (`video`, `image`), privacyStatus, photoCoverIndex, autoAddMusic, madeWithAi, disableComment, disableDuet, disableStitch, brandContentToggle, brandOrganicToggle |
| `LinkedInParams` | format (`post`), organizationId |
| `YouTubeParams` | format (`post`), title, privacyStatus, coverUrl |
| `PinterestParams` | format (`pin`), title, boardId, destinationLink, coverUrl, thumbOffset |
| `ThreadsParams` | format (`post`) |
| `TwitterParams` | format (`post`) |

Wrap them in `PlatformParams` when passing to `posts().create()`.

## Development

```bash
./gradlew build
./gradlew test
```

## License

MIT
