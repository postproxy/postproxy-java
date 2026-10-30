# Postproxy Java SDK

Java client for the [Postproxy API](https://postproxy.dev). Uses Java records for models, `java.net.http.HttpClient` for HTTP, and Jackson for JSON. No external HTTP dependencies.

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.postproxy:postproxy-java:1.15.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'dev.postproxy:postproxy-java:1.15.0'
}
```

### Maven

```xml
<dependency>
    <groupId>dev.postproxy</groupId>
    <artifactId>postproxy-java</artifactId>
    <version>1.15.0</version>
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

#### Idempotency

Every write (`POST`/`PUT`/`PATCH`/`DELETE`) sends an `Idempotency-Key` header when you give
it one. If the connection drops before you see the response, retry with the same key and
you get the original response back instead of a second post:

```java
var key = UUID.randomUUID().toString();

var post = client.posts().create(CreatePostParams.builder()
    .body("Hello")
    .profiles(List.of("profile-id"))
    .idempotencyKey(key)
    .build());

// Retrying the same call with the same key replays the original response.
```

Methods that take a params record expose `idempotencyKey(...)` on the builder; the rest
take it as a trailing argument:

```java
client.comments().create("post-id", "profile-id", "Nice!", null, key);
client.profiles().backfillPosts("profile-id", "2025-01-01", null, key);
```

Generate a fresh key per logical operation — a UUID is ideal. Keys are scoped to your
account and may be up to 255 characters. The SDK never generates keys or retries for you.

| Situation | Result |
|---|---|
| First request with the key | Runs normally |
| Retry after a success | Original status and body replayed |
| Retry while the first is still running | `ConflictException` (409) — wait and retry |
| Same key, different request body | `ValidationException` (422) |
| Retry after an error response | Runs normally — errors are not replayed |

Only successful (`2xx`) responses are stored, so a request that failed validation or hit a
quota leaves the key free — fix the payload and retry with the same key. Stored responses
are kept for **24 hours**. Requests without a key are unaffected.

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

// Media items can be URL strings or MediaInput ({"url": ...}) objects
var post = client.posts().create(CreatePostParams.builder()
    .body("Photos")
    .profiles(List.of("profile-id"))
    .media(List.of("https://example.com/1.jpg", MediaInput.of("https://example.com/2.jpg")))
    .build());

// Alt text (one string, or one per media item), AI disclosure, and country targeting
client.posts().create(CreatePostParams.builder()
    .body("Launch day")
    .profiles(List.of("ig-profile", "li-profile", "fb-profile"))
    .media(List.of("https://example.com/1.jpg", "https://example.com/2.jpg"))
    .platforms(PlatformParams.builder()
        .instagram(InstagramParams.builder()
            .altText(List.of("Our team", "The product")).madeWithAi(false).build())
        .linkedin(LinkedInParams.builder()
            .altText("Our team and the product").countries(List.of("US", "CA")).organizationId("123").build())
        .facebook(FacebookParams.builder().countries(List.of("US")).build())
        .build())
    .build());

// Facebook link post (text-only — renders the link preview card)
client.posts().create(CreatePostParams.builder()
    .body("Read the announcement")
    .profiles(List.of("fb-profile"))
    .platforms(PlatformParams.builder()
        .facebook(FacebookParams.builder().link("https://example.com/blog/launch").build())
        .build())
    .build());

// LinkedIn poll
client.posts().create(CreatePostParams.builder()
    .body("Quick question for you all")
    .profiles(List.of("li-profile"))
    .platforms(PlatformParams.builder()
        .linkedin(LinkedInParams.builder()
            .format(LinkedInFormat.POLL)
            .organizationId("123")
            .pollQuestion("Tabs or spaces?")
            .pollOptions(List.of("Tabs", "Spaces"))
            .pollDurationDays(7) // 1, 3, 7 or 14
            .build())
        .build())
    .build());

// Threads topic tag, TikTok draft with a custom cover
client.posts().create(CreatePostParams.builder()
    .body("New drop")
    .profiles(List.of("threads-profile", "tt-profile"))
    .media(List.of("https://example.com/clip.mp4"))
    .platforms(PlatformParams.builder()
        .threads(ThreadsParams.builder().topicTag("Sneakers").build())
        .tiktok(TikTokParams.builder()
            .format(TikTokFormat.VIDEO).uploadAsDraft(true).coverUrl("https://example.com/cover.jpg").build())
        .build())
    .build());

// Custom cover image (Instagram, TikTok, YouTube, Pinterest): a hosted URL…
client.posts().create(CreatePostParams.builder()
    .body("New video")
    .profiles(List.of("yt-profile"))
    .media(List.of("https://example.com/video.mp4"))
    .platforms(PlatformParams.builder()
        .youtube(YouTubeParams.builder().title("Launch").coverUrl("https://example.com/cover.jpg").build())
        .build())
    .build());
// …or a local file, uploaded with the post (switches the request to multipart)…
client.posts().create(CreatePostParams.builder()
    .body("New video")
    .profiles(List.of("tt-profile"))
    .media(List.of("https://example.com/video.mp4"))
    .platforms(PlatformParams.builder()
        .tiktok(TikTokParams.builder().format(TikTokFormat.VIDEO).coverFile("./cover.jpg").build())
        .build())
    .build());
// …or a base64 data URL
client.posts().create(CreatePostParams.builder()
    .body("New pin")
    .profiles(List.of("pin-profile"))
    .media(List.of("https://example.com/video.mp4"))
    .platforms(PlatformParams.builder()
        .pinterest(PinterestParams.builder()
            .boardId("board-id").coverFile("data:image/jpeg;base64,/9j/4AAQ...").build())
        .build())
    .build());

// Instagram reel with licensed music (profiles connected via Facebook login).
// Find an audio id with client.profiles().instagramAvailableAudio(...).
client.posts().create(CreatePostParams.builder()
    .body("Summer vibes")
    .profiles(List.of("ig-profile"))
    .media(List.of("https://example.com/reel.mp4"))
    .platforms(PlatformParams.builder()
        .instagram(InstagramParams.builder()
            .format(InstagramFormat.REEL).audioId("1234567890").audioVolume(60).videoVolume(30).build())
        .build())
    .build());

// Skip the duplicate check (otherwise an identical post submitted moments
// ago throws ConflictException)
client.posts().create(CreatePostParams.builder()
    .body("Same text again")
    .profiles(List.of("profile-id"))
    .skipDuplicateCheck(true)
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
    // Each thread item is delivered on its own; its platforms() shows whether it went out
    child.platforms().forEach(p -> System.out.println("  " + p.platform() + ": " + p.status()));
}

// Update a post (only drafts or scheduled posts)
var post = client.posts().update("post-id", UpdatePostParams.builder()
    .body("Updated content!")
    .build());

// Edit a post that's already published. Only the body, the YouTube title and
// the Pinterest title / destinationLink can change; anything else is a 422.
// The edit is pushed to the platforms in the background — watch for the
// platform_post.edited / platform_post.edit_failed webhooks.
client.posts().update("published-post-id", UpdatePostParams.builder()
    .body("Fixed a typo")
    .platforms(PlatformParams.builder()
        .youtube(YouTubeParams.builder().title("Better title").build())
        .build())
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

// Delete a post and also remove it from social platforms
var result2 = client.posts().delete("post-id", true, null);

// Delete from platforms only (keeps DB record). Defaults to all platforms.
var r1 = client.posts().deleteOnPlatform("post-id");
// Target a single network
var r2 = client.posts().deleteOnPlatform(
        "post-id", DeleteOnPlatformParams.builder().network("twitter").build());
// Target a specific profile
var r3 = client.posts().deleteOnPlatform(
        "post-id", DeleteOnPlatformParams.builder().profileId("prof-abc").build());
// Target a specific post profile (covers entire thread for that profile)
var r4 = client.posts().deleteOnPlatform(
        "post-id", DeleteOnPlatformParams.builder().postProfileId("pp-abc").build());
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

#### Parsing events and typed payloads

`WebhookEvents.parse(body)` validates the envelope and returns a `WebhookEvent`
(whose `data` is a `JsonNode`). Decode `data` into a typed payload with one of the
`as*` helpers based on `event.type()`:

```java
import dev.postproxy.sdk.webhook.WebhookEvent;
import dev.postproxy.sdk.webhook.WebhookEventType;
import dev.postproxy.sdk.webhook.WebhookEvents;

WebhookEvent event = WebhookEvents.parse(requestBody);

switch (event.type()) {
    case COMMENT_CREATED -> {
        var data = WebhookEvents.asCommentCreated(event);
        System.out.println(data.body());
    }
    case PROFILE_COMMENT_CREATED -> {
        var data = WebhookEvents.asProfileCommentCreated(event);
        System.out.println(data.authorUsername());
    }
    case MESSAGE_RECEIVED, MESSAGE_SENT, MESSAGE_DELIVERED, MESSAGE_READ,
         MESSAGE_EDITED, MESSAGE_DELETED, MESSAGE_FAILED_WAITING_FOR_RETRY,
         MESSAGE_FAILED -> {
        var data = WebhookEvents.asMessageEvent(event);
        System.out.println(data.message().body());
    }
    case REACTION_RECEIVED -> {
        var data = WebhookEvents.asReactionEvent(event);
        System.out.println(data.action() + " " + data.reaction());
    }
    case PLATFORM_POST_EDITED, PLATFORM_POST_EDIT_FAILED -> {
        var data = WebhookEvents.asPlatformPost(event);
        System.out.println("Edit on " + data.platform() + ": " + data.status());
    }
    case CHAT_THREAD_CONTROL_CHANGED -> {
        var data = WebhookEvents.asThreadControlChanged(event);
        System.out.println("Chat " + data.chat().id() + " now owned by " + data.newOwnerAppId());
    }
    case MESSAGE_TEMPLATE_STATUS_UPDATED -> {
        var data = WebhookEvents.asMessageTemplateEvent(event);
        System.out.println(data.name() + ": " + data.status());
    }
    default -> { /* ... */ }
}
```

Event types:

- **Posts:** `post.processed`, `post.imported`, `platform_post.published`, `platform_post.failed`, `platform_post.failed_waiting_for_retry`, `platform_post.edited`, `platform_post.edit_failed`, `platform_post.insights`, `media.failed`
- **Profiles:** `profile.connected`, `profile.disconnected`, `profile.stats`, `profile.placements_synced`, `placement.orphaned`, `placement.restored`
- **Comments:** `comment.created`, `comment.failed`, `comment.failed_waiting_for_retry`, `comment.delivery_unconfirmed`, `comment.edited`, `comment.edit_failed`, `profile_comment.created`, `profile_comment.failed`, `profile_comment.failed_waiting_for_retry`, `profile_comment.edited`, `profile_comment.deleted`
- **Messages:** `message.received`, `message.sent`, `message.delivered`, `message.read`, `message.edited`, `message.deleted`, `message.failed_waiting_for_retry`, `message.failed`, `reaction.received`, `referral.received`, `chat.thread_control_changed`, `chat.thread_control_requested`
- **WhatsApp:** `message_template.status_updated`, `message_template.category_updated`, `placement.name_status_updated`, `placement.quality_updated`, `whatsapp.automatic_event`

Typed payloads (decoders on `WebhookEvents`):

| Payload | Events | Decoder |
| --- | --- | --- |
| `PlatformPostData` | `platform_post.*` (except `insights`) | `asPlatformPost` |
| `ProfileEventData` | `profile.connected`, `profile.disconnected` (with `error` / `errorDetails` when PostProxy disconnected it) | `asProfileEvent` |
| `PlacementsSyncedData` | `profile.placements_synced` | `asPlacementsSynced` |
| `PlacementEventData` | `placement.orphaned`, `placement.restored`, `placement.name_status_updated`, `placement.quality_updated` | `asPlacementEvent` |
| `CommentCreatedData` | the six `comment.*` events | `asCommentCreated` |
| `ProfileCommentCreatedData` | the five `profile_comment.*` events | `asProfileCommentCreated` |
| `MessageEventData` | the eight `message.*` events | `asMessageEvent` |
| `ReactionEventData` | `reaction.received` | `asReactionEvent` |
| `ReferralEventData` | `referral.received` | `asReferral` |
| `ThreadControlChangedEventData` | `chat.thread_control_changed` | `asThreadControlChanged` |
| `ThreadControlRequestedEventData` | `chat.thread_control_requested` | `asThreadControlRequested` |
| `MessageTemplateEventData` | `message_template.status_updated`, `message_template.category_updated` | `asMessageTemplateEvent` |
| `WhatsAppAutomaticEventData` | `whatsapp.automatic_event` | `asWhatsAppAutomaticEvent` |

### Comments

```java
import dev.postproxy.sdk.model.Comment;

// List comments on a post (paginated)
var comments = client.comments().list("post-id", "profile-id");
for (var comment : comments.data()) {
    System.out.println(comment.authorUsername() + ": " + comment.body());
    // Media attached to the comment (images, video, etc.)
    for (var att : comment.attachments()) {
        System.out.println("  attachment " + att.type() + ": " + att.url());
    }
    // Author signals (platform-specific), e.g. is_verified_user / follower_count
    if (comment.metadata() != null) {
        System.out.println("  metadata: " + comment.metadata());
    }
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

// Edit a comment you published (Facebook, YouTube). Runs in the background —
// the outcome arrives as comment.edited / comment.edit_failed.
client.comments().edit("post-id", "comment-id", "profile-id", "Fixed a typo");

// Pin / unpin a comment (TikTok)
client.comments().pin("post-id", "comment-id", "profile-id");
client.comments().unpin("post-id", "comment-id", "profile-id");

// Private reply: send a DM in reply to a comment (Instagram & Facebook). Returns a Message.
var message = client.comments().privateReply(
    "post-id", "comment-id", "profile-id",
    "Thanks for your comment — DM-ing you the details.");
System.out.println(message.id() + " in chat " + message.chatId());

// Private replies take quick replies, buttons, a card or a carousel too
// (one private reply per comment, within 7 days of it)
client.comments().privateReply("post-id", "comment-id", "profile-id", "Pick one:",
    List.of(new QuickReply("Pricing", "PRICING"), new QuickReply("Talk to us", "HUMAN")),
    null, null, null, null);

// Filter by when PostProxy received the comment (created_at, not posted_at).
// A bare date means that date's start of day. Applies to top-level comments —
// one in range brings its full replies list with it.
var recent = client.comments().list("post-id", "profile-id", null, null,
    "2026-03-25", "2026-03-26T12:00:00Z");
```

#### Comments across posts

`comments().listAll(...)` returns comments spanning every post in the profile group in one
request — the comments counterpart to `posts().stats(...)`. Every filter is optional.

**This list is flat.** Unlike the per-post list, replies are not nested: every comment,
top-level or reply, is its own entry linked to its parent by `parentExternalId()`, so
`total()` counts every comment and paging is exact. Entries are `BulkComment`, which adds
`postId()`, `profileId()`, and `platform()`.

```java
import dev.postproxy.sdk.model.BulkComment;

var all = client.comments().listAll(
    List.of("post-1", "post-2"),           // omit (null) for every post in scope
    List.of("instagram", "prof-abc"),      // profile IDs or network names, mixed
    "2026-03-25",                          // from
    null,                                  // to
    null,                                  // page
    50,                                    // perPage (max 100)
    null);                                 // profileGroupId

for (var c : all.data()) {
    // Each entry says where it came from, so you can act on it with the
    // post-scoped methods above.
    System.out.println(c.platform().getValue() + " " + c.postId() + " " + c.profileId() + ": " + c.body());

    if (c.parentExternalId() != null) {
        System.out.println("  ↳ reply to " + c.parentExternalId());
    }
}

// Reply to one of them
var first = all.data().get(0);
client.comments().create(first.postId(), first.profileId(), "Thanks!", first.id());
```

Unknown or out-of-scope IDs in the post and profile lists are ignored rather than erroring.
Results are ordered newest first by receipt time.

### Direct Messages

Read and send 1:1 messages on profiles that support DMs (Facebook Messenger,
Instagram, WhatsApp, Telegram, Bluesky). Outbound sends are processed asynchronously and
start with `status: pending`.

```java
import dev.postproxy.sdk.model.MessageDirection;
import dev.postproxy.sdk.param.CreateChatParams;
import dev.postproxy.sdk.param.EditMessageParams;
import dev.postproxy.sdk.param.ListChatsParams;
import dev.postproxy.sdk.param.ListMessagesParams;
import dev.postproxy.sdk.param.ReactParams;
import dev.postproxy.sdk.param.SendMessageParams;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

// List chats for a profile (paginated)
var chats = client.chats().list("profile-id");
var chatsFiltered = client.chats().list("profile-id", ListChatsParams.builder()
    .perPage(20)
    .build());

// Create or find a chat by participant (idempotent)
var chat = client.chats().create("profile-id", CreateChatParams.builder("igsid_8675309")
    .participantUsername("jane_doe")
    .participantName("Jane Doe")
    .build());

// Get / archive / unarchive a chat (archive is Bluesky-only)
var fetched = client.chats().get(chat.id());
client.chats().archive(chat.id());
client.chats().unarchive(chat.id());

// Read receipt and typing indicator (typing: Facebook, Instagram, WhatsApp)
client.chats().markRead(chat.id());
client.chats().startTyping(chat.id());
client.chats().stopTyping(chat.id());

// Thread control (Facebook & Instagram): move the conversation between Meta apps sharing
// the inbox. Each returns the chat; metadata().get("thread_control") holds the owner.
client.chats().passThreadControl(chat.id(), "263902037430900", "escalated to support");
client.chats().takeThreadControl(chat.id());
client.chats().releaseThreadControl(chat.id());
client.chats().requestThreadControl(chat.id());
var owner = client.chats().threadOwner(chat.id()); // appId(), expiresAt()

// List messages in a chat
var messages = client.messages().list(chat.id(), ListMessagesParams.builder()
    .direction(MessageDirection.INBOUND)
    .build());
for (var msg : messages.data()) {
    System.out.println(msg.direction().getValue() + ": " + msg.body());
    for (var att : msg.attachments()) System.out.println("  " + att.type() + ": " + att.url());
    for (var r : msg.reactions()) System.out.println("  " + r.reaction());
}

// Send a text message
var sent = client.messages().send(chat.id(), SendMessageParams.builder()
    .body("Yes, we ship worldwide!")
    .build());

// Send outside the 24h window with a tag (Facebook/Instagram)
client.messages().send(chat.id(), SendMessageParams.builder()
    .body("Following up on your order.")
    .tag("HUMAN_AGENT")
    .build());

// Send media — by hosted URL or by uploading a local file
client.messages().send(chat.id(), SendMessageParams.builder()
    .media(List.of("https://cdn.example.com/photo.png"))
    .build());
client.messages().send(chat.id(), SendMessageParams.builder()
    .mediaFiles(List.of(Path.of("./photo.png")))
    .build());

// Get a single message
var message = client.messages().get(sent.id());

// Edit a message (Telegram only) — body and/or reply_markup
client.messages().edit(sent.id(), EditMessageParams.builder()
    .body("Updated answer: we ship to 90+ countries.")
    .replyMarkup(Map.of("inline_keyboard", List.of(List.of(
        Map.of("text", "Track order", "url", "https://shop.example.com/orders/123")))))
    .build());

// React / unreact (Facebook & Instagram)
client.messages().react(sent.id(), ReactParams.builder().reaction("love").build());
client.messages().unreact(sent.id());
```

#### Quick replies and buttons (Facebook & Instagram)

Meta's two interactive primitives. **Quick replies** are chips above the participant's
composer that disappear once tapped; **buttons** are attached to the message and stay in
the thread. Telegram's equivalent is `replyMarkup` — passing `quickReplies` or `buttons` on
a Telegram or Bluesky chat returns `422`.

```java
// Quick replies — up to 13. title ≤ 20 chars, payload ≤ 1000.
client.messages().send(chat.id(), SendMessageParams.builder()
    .body("What can I help with?")
    .quickReplies(List.of(
        new QuickReply("Track order", "TRACK"),
        new QuickReply("Talk to support", "HELP")))
    .build());

// Buttons — up to 3, each either web_url or postback. card is optional and
// requires buttons.
client.messages().send(chat.id(), SendMessageParams.builder()
    .body("Your order shipped")
    .buttons(List.of(
        MessageButton.webUrl("Track", "https://shop.example.com/o/123"),
        MessageButton.postback("Cancel", "CANCEL:123")))
    .card(new MessageCard(
        "Arriving Friday",
        "https://cdn.example.com/shoe.png",
        CardDefaultAction.webUrl("https://shop.example.com/o/123")))
    .build());
```

Buttons are delivered as a Meta generic template and your `body` becomes the template's
element title — so **`body` is capped at 80 characters when buttons are present**. That is
Meta's limit, not Postproxy's, and a longer body is rejected with a `422` naming the
length. Buttons cannot be combined with media. Instagram is stricter than Messenger: it
delivers quick replies only on a plain-text message, so `quickReplies` with media or with
`buttons` returns `422` on Instagram while both are accepted on Facebook.

Validation happens server-side and names the offending index — `buttons[1].url must be an
https:// URL` — surfacing as the SDK's usual exception for a `422`.

> The new params are sent on the JSON path only. To combine quick replies with an
> attachment, pass `media` as a hosted URL rather than uploading via `mediaFiles`.

A tap comes back as an **inbound message** carrying `tappedAction`:

```java
var inbound = client.messages().list(chat.id(), ListMessagesParams.builder()
    .direction(MessageDirection.INBOUND).build());
for (var msg : inbound.data()) {
    if (msg.tappedAction() != null) {
        // TappedAction.KIND_QUICK_REPLY / KIND_POSTBACK / KIND_CALLBACK_QUERY
        System.out.println(msg.tappedAction().kind() + ": " + msg.tappedAction().payload());
    }
}
```

Subscribe to `message.received` to react to taps as they happen — the same field is on the
webhook payload. `tappedAction` is derived rather than stored, so it also resolves for taps
recorded before Postproxy exposed it, including Instagram ice-breaker taps and Telegram
callback queries (`KIND_CALLBACK_QUERY`). A tap also opens the 24h window.

#### Carousels (Facebook & Instagram)

`elements` sends a horizontally scrolling carousel of up to 10 cards. Each `MessageElement`
needs a `title` (≤ 80 chars) plus at least one of `subtitle`, `imageUrl`, `defaultAction` or
`buttons` (≤ 3). A carousel can't be combined with `body`, `buttons` or `card`.

```java
import dev.postproxy.sdk.model.MessageElement;

client.messages().send(chat.id(), SendMessageParams.builder()
    .elements(List.of(
        new MessageElement("Red sneaker", "$89", "https://cdn.example.com/red.png",
            List.of(MessageButton.webUrl("Buy", "https://shop.example.com/red"))),
        new MessageElement("Blue sneaker", "$92", "https://cdn.example.com/blue.png",
            List.of(MessageButton.postback("Notify me", "NOTIFY:blue")))))
    .build());
```

#### WhatsApp

WhatsApp chats run through a phone number, so creating one takes the `placementId` (the
phone number id from `profiles().placements(...)`). Send exactly one of `body`, `media`,
`template`, `interactive`, `location` or `contacts` per message. Outside the 24-hour
customer service window (`chat.withinMessagingWindow()` is false) only an approved template
gets through.

```java
import dev.postproxy.sdk.model.MessageLocation;
import dev.postproxy.sdk.model.MessageTemplateInput;
import dev.postproxy.sdk.model.TemplateButtonParam;

var chat = client.chats().create("wa-profile-id", CreateChatParams.builder("15551234567")
    .placementId("phone-number-id")
    .build());

// Template — address it by name (+ language) or id. Variables are positional:
// header, then body, then dynamic URL buttons.
client.messages().send(chat.id(), SendMessageParams.builder()
    .template(MessageTemplateInput.builder()
        .name("order_update")
        .language("en_US")
        .variables(List.of("Jane", "#1234"))
        .buttonParams(List.of(TemplateButtonParam.of(0, "url", Map.of("text", "1234"))))
        .build())
    .build());

// Location, contacts, and Meta's interactive messages (buttons, lists, ...)
client.messages().send(chat.id(), SendMessageParams.builder()
    .location(new MessageLocation(52.52, 13.405, "Our store", "Main St 1"))
    .build());
client.messages().send(chat.id(), SendMessageParams.builder()
    .contacts(List.of(Map.of(
        "name", Map.of("formatted_name", "Support"),
        "phones", List.of(Map.of("phone", "+15550000000")))))
    .build());
client.messages().send(chat.id(), SendMessageParams.builder()
    .interactive(Map.of(
        "type", "button",
        "body", Map.of("text", "Did that help?"),
        "action", Map.of("buttons", List.of(Map.of("type", "reply", "reply", Map.of("id", "yes", "title", "Yes"))))))
    .build());

// Send options
client.messages().send(chat.id(), SendMessageParams.builder()
    .body("https://example.com").linkPreview(true).build());
client.messages().send(chat.id(), SendMessageParams.builder()
    .media(List.of("https://example.com/invoice.pdf")).filename("invoice.pdf").build());
client.messages().send(chat.id(), SendMessageParams.builder()
    .media(List.of("https://example.com/note.ogg")).voiceNote(true).build());
```

Templates, number registration, the business profile, groups and more are managed through
`client.whatsappTemplates()` and `client.whatsapp()` — see [WhatsApp management](#whatsapp-management).

### Profile comments (Google Business reviews)

Profile-level comments expose Google Business reviews and replies. Reviews are user-generated — the SDK lets you list/get them and reply to or delete your own replies. Reviews sync twice daily.

```java
// List reviews for a profile (paginated)
var reviews = client.profileComments().list("profile-id");
for (var review : reviews.data()) {
    System.out.println(review.authorUsername() + ": " + review.body());
    for (var reply : review.replies()) {
        System.out.println("  reply: " + reply.body());
    }
}

// Filter by placement (location)
var reviews = client.profileComments().list("profile-id", "accounts/123/locations/456", null, null);

// Get a single review
var review = client.profileComments().get("profile-id", "review-id");

// Reply to a review (parentId is the review id)
var reply = client.profileComments().create("profile-id", "review-id", "Thanks for visiting!");

// Delete your reply
client.profileComments().delete("profile-id", "reply-id");
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

// Move a placement (e.g. a Facebook Page or Telegram channel) to another group
var placement = client.profiles().assignPlacementToGroup(
        "profile-id", "placement-external-id", "pg-other");
System.out.println(placement.profileGroupId()); // "pg-other"

// Move the whole profile (with its placements) to another group
client.profiles().assignToGroup("profile-id", "pg-other");

// Daily stats and follower history
var daily = client.profiles().dailyStats("profile-id", null, "2026-09-01", "2026-09-30",
        DailyStatsAttribution.PUBLISH, null); // or RECEIVED (default)
for (var day : daily.data().records()) {
    System.out.println(day.date() + " " + day.metric("impressions"));
}

var followers = client.profiles().followerHistory("profile-id", null, "2026-09-01", null, null);
for (var day : followers.data().records()) {
    System.out.println(day.date() + " " + day.followersTotal());
}

// Instagram: whether a DM participant is verified / follows you
var status = client.profiles().participantStatus("ig-profile-id", "igsid-123");

// LinkedIn: resolve a company page to the URN used to @-mention it
var mention = client.profiles().resolveMention("li-profile-id", "https://www.linkedin.com/company/acme");
System.out.println(mention.urn()); // "urn:li:organization:123"

// Instagram (Facebook login): music for reels — pass the id as InstagramParams.audioId
var audio = client.profiles().instagramAvailableAudio("ig-profile-id",
        InstagramAudioType.MUSIC, "summer", null); // or ORIGINAL_SOUND

// Instagram (Facebook login): incoming collaboration invites
var invites = client.profiles().instagramCollaborationInvites("ig-profile-id"); // or (id, after)
client.profiles().acceptInstagramCollaborationInvite("ig-profile-id",
        invites.collaborationInvites().get(0).mediaId());
client.profiles().declineInstagramCollaborationInvite("ig-profile-id", "17890000000000001");

// Pinterest boards — a board is a placement; its id is the boardId you publish to
var board = client.profiles().createPinterestBoard("pin-profile-id", "Summer Recipes",
        "Light dinners for hot days", PinterestBoardPrivacy.PUBLIC); // or PROTECTED
client.profiles().deletePinterestBoard("pin-profile-id", board.id()); // also deletes its Pins

// YouTube playlists — publish into one with YouTubeParams.playlistId
var playlists = client.profiles().youtubePlaylists("yt-profile-id"); // data(), nextPageToken()
var playlist = client.profiles().createYouTubePlaylist("yt-profile-id", "Tutorials",
        "Step-by-step guides", YouTubePrivacy.PUBLIC);
client.profiles().updateYouTubePlaylist("yt-profile-id", playlist.id(), null, null, YouTubePrivacy.UNLISTED);
client.profiles().deleteYouTubePlaylist("yt-profile-id", playlist.id());

// Facebook Page about / contact info
var page = client.profiles().facebookPage("fb-profile-id", "page-id");
client.profiles().updateFacebookPage("fb-profile-id", "page-id", Map.of(
        "about", "Coffee roasters since 2010",
        "website", "https://example.com"));

// Ice breakers (Instagram DMs): FAQ prompts shown when a user opens a chat
var iceBreakers = client.profiles().iceBreakers("profile-id");
iceBreakers.iceBreakers().forEach(ib -> System.out.println(ib.question()));

client.profiles().setIceBreakers("profile-id", List.of(
        new IceBreaker("What services do you offer?", "services"),
        new IceBreaker("What are your hours?", "hours")
)); // 1-4 items

client.profiles().deleteIceBreakers("profile-id");

// Delete a profile
var result = client.profiles().delete("profile-id");
System.out.println(result.success()); // true

// Profile stats timeseries — placementId required for facebook, linkedin, telegram
var stats = client.profiles().getProfileStats("prof_li_001", "108520199", "2026-04-01T00:00:00Z", null);
for (var r : stats.data().records()) {
    System.out.println(r.recordedAt() + ": " + r.stats().get("followerCount"));
}

// Bluesky — no placements
var bsky = client.profiles().getProfileStats("prof_bsky_001", null);
var last = bsky.data().records().get(bsky.data().records().size() - 1);
System.out.println(last.stats().get("followersCount"));
```

Every stats record (post stats and profile stats alike) carries `rawStats()` alongside the
normalized `stats()`, exposing each metric under its **original platform name**:

```java
var stats = client.posts().stats(GetStatsParams.builder().postIds(List.of("post-id")).build());
var record = stats.data().get("post-id").platforms().get(0).records().get(0);

System.out.println(record.stats().get("impressions"));          // normalized
System.out.println(record.rawStats().get("views"));             // Instagram's own name
System.out.println(record.rawStats().get("impression_count"));  // Twitter/X's own name
```

LinkedIn post stats now normalize `likes`, `comments`, `shares`, and `clicks` alongside
`impressions` — previously only `impressions` was normalized.

#### Post syncs & backfill

Postproxy mirrors posts published natively on a platform into your account. Every one of
those pulls is recorded as a **post sync**: the one fired when the profile connects, the
recurring poll, and any backfill you start.

```java
import dev.postproxy.sdk.model.PostSyncStatus;
import dev.postproxy.sdk.model.PostSyncTrigger;

// Start a backfill — walks the feed backwards from the newest post in batches
// of 25 until it reaches `from` or the platform stops returning posts.
var sync = client.profiles().backfillPosts("profile-id", "2025-01-01");
System.out.println(sync.id() + " " + sync.status().getValue()); // "sync456def pending"

// Poll it to completion — finished when the status is COMPLETED or FAILED
var run = client.profiles().postSync("profile-id", sync.id());
System.out.println(run.postsImported() + " of " + run.postsSeen() + ", back to " + run.oldestPostedAt());

// List recent runs (kept for 30 days), newest first
var runs = client.profiles().postSyncs("profile-id",
    PostSyncTrigger.BACKFILL,     // CONNECT | SCHEDULED | BACKFILL
    PostSyncStatus.COMPLETED,     // PENDING | RUNNING | COMPLETED | FAILED
    null, 25, null);
```

| `PostSync` accessor | Description |
|---|---|
| `id()` | Sync identifier |
| `profileId()` | Profile this run belongs to |
| `kind()` | Always `posts` today |
| `trigger()` | `CONNECT`, `SCHEDULED`, or `BACKFILL` |
| `status()` | `PENDING`, `RUNNING`, `COMPLETED`, or `FAILED` |
| `startedAt()` / `completedAt()` | ISO 8601 timestamps, null until set |
| `postsSeen()` | Posts the platform returned across the run |
| `postsImported()` | Posts that were **new** and got created |
| `backfillFrom()` | The date floor requested; null for `CONNECT`/`SCHEDULED` |
| `oldestPostedAt()` | Publish date of the oldest post the run reached |
| `error()` | Platform error message when `status()` is `FAILED` |
| `createdAt()` | ISO 8601 timestamp |

**How far back a backfill reaches depends on the platform's API**, not on Postproxy: where
history is pageable we follow it, otherwise the run ends early with whatever it got and
still reports `COMPLETED`.

Only one backfill runs per profile at a time — starting a second throws `ConflictException`
carrying the running one's id:

```java
try {
    client.profiles().backfillPosts("profile-id", "2025-01-01");
} catch (ConflictException e) {
    var runningId = e.getResponse().get("profile_sync_id");
    // Poll the run that's already going.
}
```

Posts you already have are skipped, so overlapping backfills are safe. Imported posts
behave exactly like ones the poll picks up (`source: "imported"`, `post.imported` webhook),
but a backfill's follow-up work is queued at a lower priority so a deep run can't slow down
publishing.

#### Chat backfill

`backfillChats(...)` imports DM history the same way `backfillPosts(...)` imports posts. Runs
are `ChatSync` records (`chatsSeen()`, `messagesImported()`, `oldestUpdatedAt()`).

```java
var run = client.profiles().backfillChats("profile-id", "2026-01-01");
var latest = client.profiles().chatSync("profile-id", run.id());
var runs = client.profiles().chatSyncs("profile-id", null, PostSyncStatus.COMPLETED, null, null, null);
```

### Profile Groups

```java
import dev.postproxy.sdk.model.ConnectionOnboarding;
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

// Initialize a social platform OAuth connection
var conn = client.profileGroups().initializeConnection(
    "pg-id",
    Platform.INSTAGRAM,
    "https://yourapp.com/callback"
);
System.out.println(conn.url()); // Redirect the user to this URL

// Pick the onboarding flow: Instagram via Facebook login (needed for reel music),
// WhatsApp via the Cloud API (API) or the WhatsApp Business app (BUSINESS_APP).
client.profileGroups().initializeConnection("pg-id", Platform.INSTAGRAM,
    "https://yourapp.com/callback", ConnectionOnboarding.FACEBOOK, null);
client.profileGroups().initializeConnection("pg-id", Platform.WHATSAPP,
    "https://yourapp.com/callback", ConnectionOnboarding.BUSINESS_APP, null);

// Re-authorize an existing connection
client.profileGroups().initializeConnection("pg-id", Platform.LINKEDIN,
    "https://yourapp.com/callback", null, true);

// BlueSky — app password (synchronous, no OAuth)
var bsky = client.profileGroups().connectBluesky(
    "pg-id", "yourname.bsky.social", "xxxx-xxxx-xxxx-xxxx"
);
System.out.println(bsky.profile().id());

// Telegram — bring-your-own-bot. Channels populate asynchronously; poll
// placements until non-empty.
var tg = client.profileGroups().connectTelegram(
    "pg-id", "123456789:ABCdef-GhIJklMnOpQrStUvWxYz"
);
System.out.println(tg.nextStep());

while (client.profiles().placements(tg.profile().id()).data().isEmpty()) {
    Thread.sleep(3000);
}
```

### Placements & API keys

Both require an **account-wide** API key.

```java
// Every placement (Facebook Page, LinkedIn org, WhatsApp number, ...) across groups
var placements = client.placements().list(null, 50);
for (var p : placements.data()) {
    System.out.println(p.platform() + " " + p.name() + " via " + p.activeProfile().name());
}

// When several connected profiles reach the same placement, pick the one to use
client.placements().setActiveProfile("placement-id", "profile-id");

// Group-scoped API keys
var key = client.apiKeys().create("CI pipeline", "pg-id");
System.out.println(key.token()); // shown only once
var keys = client.apiKeys().list();
client.apiKeys().delete(key.id());
```

### Google Business management

Manage a Google Business Profile location's own data — separate from posting to it.
`locationId` is the placement id from `profiles().placements(...)`. Google's camelCased
payloads pass through untouched as `Map<String, Object>`, so you can PATCH back what you GET;
updates take a `fields` list naming what to change. Every method has a short form and a
long form taking the optional arguments (null to omit) plus `profileGroupId` and, on writes,
`idempotencyKey`.

```java
var gbp = client.googleBusiness();

var location = gbp.location("gbp-profile-id", "location-id");
gbp.updateLocation("gbp-profile-id", "location-id", List.of("websiteUri"),
    Map.of("websiteUri", "https://example.com"));
gbp.updateHours("gbp-profile-id", "location-id", List.of("regularHours"), Map.of(
    "regularHours", Map.of("periods", List.of(Map.of(
        "openDay", "MONDAY", "openTime", Map.of("hours", 9),
        "closeDay", "MONDAY", "closeTime", Map.of("hours", 17))))));

// Categories and attributes
gbp.availableLocationCategories("gbp-profile-id", "location-id", "US", null, "coffee", null, null, null, null);
var attrs = gbp.attributes("gbp-profile-id", "location-id");
gbp.availableAttributes("gbp-profile-id", "location-id");
gbp.updateAttributes("gbp-profile-id", "location-id",
    List.of(Map.of("name", "attributes/has_wi_fi", "values", List.of(true))),
    List.of("attributes/has_wi_fi"), null, null);

// Services and food menus
gbp.serviceList("gbp-profile-id", "location-id");
gbp.updateServiceList("gbp-profile-id", "location-id", List.of(/* serviceItems */));
gbp.foodMenus("gbp-profile-id", "location-id");
gbp.updateFoodMenus("gbp-profile-id", "location-id", List.of(/* menus */));

// Place action links (booking, ordering, ...)
gbp.availablePlaceActionTypes("gbp-profile-id", "location-id");
gbp.createPlaceActionLink("gbp-profile-id", "location-id", "https://book.example.com", "APPOINTMENT",
    true, null, null);
var links = gbp.placeActionLinks("gbp-profile-id", "location-id");
gbp.updatePlaceActionLink("gbp-profile-id", "location-id", "locations/1/placeActionLinks/2",
    List.of("uri"), "https://book.example.com/new", null, null);
gbp.deletePlaceActionLink("gbp-profile-id", "location-id", "locations/1/placeActionLinks/2");

// Photos and videos
gbp.createMedia("gbp-profile-id", "location-id", "https://example.com/storefront.jpg",
    "EXTERIOR", "PHOTO", null, null, null);
var media = gbp.media("gbp-profile-id", "location-id");
gbp.deleteMedia("gbp-profile-id", "location-id", "accounts/1/locations/1/media/9");

// Search keywords that showed the location, month by month (max 12 months).
// Unlike the calls above, this returns a typed SearchKeywordsResponse.
var keywords = gbp.searchKeywords("gbp-profile-id", "location-id", "2026-07", "2026-09");
for (var month : keywords.months()) {
    for (var k : month.keywords()) {
        // Low-volume keywords have impressions() == null and an impressionsThreshold() bound
        System.out.println(month.month() + " " + k.keyword() + " "
            + (k.impressions() != null ? k.impressions() : "<" + k.impressionsThreshold()));
    }
}
```

### WhatsApp management

`phoneNumberId` is the placement id from `profiles().placements(...)`. Responses are Meta's
payloads as `Map<String, Object>`.

```java
import dev.postproxy.sdk.param.WhatsAppTemplateInput;

// Message templates (submitted to Meta for review)
var tpl = client.whatsappTemplates().create("wa-profile-id",
    WhatsAppTemplateInput.builder("order_update", "UTILITY", "en_US")
        .components(List.of(Map.of("type", "BODY", "text", "Hi {{1}}, order {{2}} has shipped.")))
        .build());
var templates = client.whatsappTemplates().list("wa-profile-id", null, null, "APPROVED", null, null);
client.whatsappTemplates().get("wa-profile-id", "order_update", "en_US", null);
client.whatsappTemplates().update("wa-profile-id", tpl.id(), null, 3600);
client.whatsappTemplates().delete("wa-profile-id", "order_update"); // every language
client.whatsappTemplates().library("wa-profile-id", "delivery", null, null);

var wa = client.whatsapp();

// Number registration
wa.numberInfo("wa-profile-id", "phone-number-id");
wa.requestVerificationCode("wa-profile-id", "phone-number-id", "SMS", "en", null, null);
wa.verifyPhoneNumber("wa-profile-id", "phone-number-id", "123456");
wa.registerPhoneNumber("wa-profile-id", "phone-number-id", "000000"); // two-step PIN

// Business profile, display name, username
wa.updateBusinessProfile("wa-profile-id", "phone-number-id",
    Map.of("about", "We reply fast", "websites", List.of("https://example.com")));
wa.updateBusinessProfilePhoto("wa-profile-id", "phone-number-id", "https://example.com/logo.png");
wa.requestDisplayNameChange("wa-profile-id", "phone-number-id", "Acme Coffee");
wa.usernameSuggestions("wa-profile-id", "phone-number-id");
wa.setUsername("wa-profile-id", "phone-number-id", "acmecoffee");

// Blocked users
wa.blockUsers("wa-profile-id", "phone-number-id", List.of("15551234567"));
wa.blockedUsers("wa-profile-id", "phone-number-id");

// Groups
var group = wa.createGroup("wa-profile-id", "phone-number-id", "VIP customers",
    null, "approval_required", null, null);
wa.createGroupInviteLink("wa-profile-id", "phone-number-id", "group-id");
wa.groupJoinRequests("wa-profile-id", "phone-number-id", "group-id");
wa.approveGroupJoinRequests("wa-profile-id", "phone-number-id", "group-id", List.of("15551234567"));

// Conversions for click-to-WhatsApp ads (account-level, no phone number id)
wa.createDataset("wa-profile-id");
wa.sendConversionEvent("wa-profile-id", Map.of(
    "event_name", "Purchase",
    "event_id", "order-1234",
    "chat_id", "chat-id",
    "currency", "USD",
    "value", 49.0));

// Download inbound media
var file = wa.media("wa-profile-id", "phone-number-id", "media-id");
System.out.println(file.contentType() + " " + file.data().length);
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
| `ForbiddenException` | 403 — account canceled or past due, or the platform refused the action |
| `NotFoundException` | 404 |
| `ConflictException` | 409 — duplicate submission (`duplicate_post_id`), a backfill already running (`profile_sync_id`), or an in-flight `Idempotency-Key` |
| `ValidationException` | 422 |
| `RateLimitException` | 429 — posting rate limit reached, or the platform is rate-limiting; `getRetryAfter()` holds the `Retry-After` seconds (or null) |

```java
try {
    client.posts().create(params);
} catch (RateLimitException e) {
    Integer wait = e.getRetryAfter(); // seconds, or null
}
```

## Types

All list methods return a response object with a `data()` list:

```java
var profiles = client.profiles().list().data();
var posts = client.posts().list().data();  // PaginatedResponse also has total(), page(), perPage()
```

Key types:

| Type | Fields |
|---|---|
| `Post` | id, body, status, scheduledAt, createdAt, draft, source, media, thread, platforms, queueId, queuePriority |
| `Profile` | id, name, username, status, inactiveReason, platform, platformId, profileGroupId, expiresAt, postCount, avatarUrl, platformUrl, placementsSyncStatus |
| `Placement` | id, name, avatarUrl, metadata |
| `AccountPlacement` | id, platform, externalId, name, avatarUrl, status, activeProfile, linkedProfiles — returned by `placements().list(...)` |
| `ApiKey` | id, name, profileGroupId, sandbox, token (only on create), createdAt |
| `ProfileGroup` | id, name, profilesCount |
| `Media` | id, type, url, status |
| `ThreadChild` | id, body, media, platforms |
| `ThreadChildInput` | body, media |
| `Webhook` | id, url, events, secret, enabled, description, createdAt |
| `WebhookDelivery` | id, eventId, eventType, responseStatus, attemptNumber, success, attemptedAt, createdAt |
| `PlatformResult` | platform, profileId, profileName, status, permalink, params, error, errorDetails, attemptedAt, insights, attachments |
| `PlatformAttachment` | id, type, status, contentType, url |
| `ListResponse<T>` | data |
| `Comment` | id, externalId, body, status, authorUsername, authorAvatarUrl, authorExternalId, metadata, parentExternalId, likeCount, isHidden, permalink, platformData, attachments, externalEditedAt, postedAt, createdAt, replies |
| `BulkComment` | Every `Comment` accessor except `replies`, plus postId, profileId, platform — returned by `comments().listAll(...)` |
| `PostSync` | id, profileId, placementId, kind, trigger, status, startedAt, completedAt, postsSeen, postsImported, backfillFrom, oldestPostedAt, error, createdAt |
| `ChatSync` | id, profileId, placementId, kind, trigger, status, startedAt, completedAt, chatsSeen, messagesImported, backfillFrom, oldestUpdatedAt, error, createdAt |
| `DailyStatsResponse` / `DailyStats` | data → profileId, platform, placementId, attribution, from, to, dataTimezone, records (`DailyStatsRecord`: date, platformStats, metrics / `metric(name)`), metrics |
| `FollowerHistoryResponse` / `FollowerHistory` | data → profileId, platform, placementId, from, to, records (`FollowerHistoryRecord`: date, followersGained, followersLost, followersChange, followersTotal) |
| `ParticipantStatus` | isVerifiedUser, isUserFollowBusiness, isBusinessFollowUser, fetchedAt |
| `ResolvedMention` | urn, type, name, vanityName |
| `ThreadOwner` | appId, expiresAt — both null when the thread is idle |
| `InstagramCollaborationInvitesResponse` / `InstagramCollaborationInvite` | collaborationInvites → mediaId, mediaOwnerUsername, caption, mediaUrl; nextCursor |
| `InstagramCollaborationInviteResult` | mediaId, accepted, declined |
| `PinterestBoardDeleted` | deleted, boardId |
| `YouTubePlaylistsResponse` / `YouTubePlaylist` | data → id, title, description, privacyStatus, itemCount, thumbnailUrl, publishedAt; nextPageToken |
| `YouTubePlaylistDeleted` | deleted, playlistId |
| `SearchKeywordsResponse` / `SearchKeywordsMonth` / `SearchKeyword` | months → month, keywords → keyword, impressions, impressionsThreshold |
| `Chat` | id, profileId, platform, externalPlacementId, group, participantExternalId, participantUsername, participantName, participantAvatarUrl, externalConversationId, lastInboundAt, lastOutboundAt, lastMessageAt, metadata, archived, withinMessagingWindow, createdAt |
| `Message` | id, chatId, externalId, direction, body, status, tag, externalCommentId, errorMessage, errorDetails, platformData, externalPostedAt, externalDeliveredAt, externalReadAt, externalEditedAt, replyToExternalId, replyMarkup, quickReplies, buttons, card, elements, tappedAction, storyReply, interactive, location, contacts, template, externalDeletedAt, reactions, attachments, isUnsupported, createdAt |
| `MessageElement` | title, subtitle, imageUrl, defaultAction, buttons |
| `MessageLocation` | latitude, longitude, name, address |
| `MessageTemplateInput` | id, name, language, variables, buttonParams (`TemplateButtonParam`), headerMedia (`TemplateHeaderMedia`), headerLocation |
| `MessageTemplateRef` | id, externalId, name, language, variables |
| `WhatsAppTemplate` | id, externalId, profileId, name, language, category, status, parameterFormat, components, messageSendTtlSeconds, rejectedReason, qualityScore, variableCount, createdAt, updatedAt |
| `MediaDownload` | data (byte[]), contentType |
| `QuickReply` | contentType, title, payload |
| `MessageButton` | type, title, url, payload — with `webUrl(title, url)` / `postback(title, payload)` factories |
| `MessageCard` | subtitle, imageUrl, defaultAction |
| `CardDefaultAction` | type, url — with a `webUrl(url)` factory |
| `TappedAction` | kind, payload, title |
| `Attachment` | id, type, url, status, externalId |
| `Reaction` | senderExternalId, emoji, reaction, at |
| `AcceptedResponse` | accepted |
| `PaginatedResponse<T>` | data, total, page, perPage |
| `StatsResponse` | data (Map&lt;String, PostStats&gt;) |
| `PostStats` | platforms |
| `PlatformStats` | profileId, platform, records |
| `StatsRecord` | stats (Map), rawStats (Map, metrics under their platform-native names), recordedAt |
| `Queue` | id, name, description, timezone, enabled, jitter, profileGroupId, timeslots, postsCount |
| `Timeslot` | id, day, time |
| `NextSlotResponse` | nextSlot |

### Platform parameter types

| Type | Platform |
|---|---|
| `FacebookParams` | format (`post`, `story`, `reel`), title, firstComment, pageId, link, altText, madeWithAi, countries |
| `InstagramParams` | format (`post`, `reel`, `story`), firstComment, collaborators, coverUrl, coverFile, audioName, audioId, audioVolume, videoVolume, stripAudio, trialStrategy (`MANUAL`, `SS_PERFORMANCE`), thumbOffset, userTags, altText, madeWithAi, disableComment, locationId, shareToFeed (reels) |
| `InstagramUserTag` | username, x, y, mediaIndex |
| `TikTokParams` | format (`video`, `image`), privacyStatus, photoCoverIndex, autoAddMusic, madeWithAi, disableComment, disableDuet, disableStitch, brandContentToggle, brandOrganicToggle, uploadAsDraft, coverUrl, coverFile, thumbOffset |
| `LinkedInParams` | format (`post`, `poll`), organizationId, reshareUrn, altText, countries, pollQuestion, pollOptions (2-4), pollDurationDays (1, 3, 7, 14), documentTitle, firstComment |
| `YouTubeParams` | format (`post`), title, privacyStatus, coverUrl, coverFile, madeForKids, tags, categoryId, containsSyntheticMedia, playlistId |
| `PinterestParams` | format (`pin`), title, boardId, destinationLink, coverUrl, coverFile, thumbOffset, altText |
| `ThreadsParams` | format (`post`), altText, topicTag |
| `TwitterParams` | format (`post`, `poll`), altText, pollOptions (2-4 choices, max 25 chars each; required for `poll`), pollDurationMinutes (5-10080; required for `poll`) |
| `BlueskyParams` | format (`post`), altText |
| `TelegramParams` | format (`post`), chatId (required), parseMode (`HTML`, `MarkdownV2`), disableLinkPreview, disableNotification |

#### Instagram user tags

Tag public Instagram accounts in a post — feed post, reel, or story:

```java
import dev.postproxy.sdk.param.InstagramUserTag;

var params = PlatformParams.builder()
    .instagram(InstagramParams.builder()
        .format(InstagramFormat.POST)
        .userTags(List.of(
            InstagramUserTag.of("natgeo", 0.5, 0.4),      // slide 0
            InstagramUserTag.of("nasa", 0.2, 0.8, 1),     // slide 1
            InstagramUserTag.of("spacex", 2)))            // video slide — username only
        .build())
    .build();
```

- **Images require `x` and `y`** — floats `0.0`–`1.0` measured from the top-left corner.
- **Reels and video slides** are tagged by username only; coordinates are ignored and dropped.
- **Stories** accept coordinates but don't need them.
- `mediaIndex` picks the carousel slide (0-based, defaults to `0`, video slides included).
- A leading `@` on a username is stripped for you.

Coordinates outside `0.0`–`1.0`, a `mediaIndex` past the last media item, or an image tag
missing `x`/`y` are rejected with a `ValidationException` naming the offending entry.
Accounts that are private or have tagging turned off are silently skipped by Instagram at
publish time.

Wrap them in `PlatformParams` when passing to `posts().create()`. Telegram requires a `chatId` per post — list channels with `client.profiles().placements(profileId)`.

`altText` takes a single `String` or a `List<String>` with one entry per media item.
`coverFile` takes a local path — uploaded with the post, switching the request to multipart
even without `mediaFiles` — or a base64 `data:` URL, sent as-is.

Supported platforms: facebook, instagram, tiktok, linkedin, youtube, twitter, threads, pinterest, bluesky, telegram, google_business, whatsapp.

#### Google Business

Google Business posts use the `googleBusiness` builder method on `PlatformParams`, taking a plain `Map<String, Object>`. The `location_id` is the location resource path returned by `client.profiles().placements()`. Supported formats: `standard`, `event`, `offer`. CTA actions: `LEARN_MORE`, `BOOK`, `ORDER`, `SHOP`, `SIGN_UP`, `CALL`. Media is limited to one image (≤5 MB).

```java
import java.util.Map;

var params = PlatformParams.builder()
    .googleBusiness(Map.of(
        "format", "standard",
        "location_id", "accounts/123/locations/456",
        "cta_action_type", "LEARN_MORE",
        "cta_url", "https://example.com"
    ))
    .build();
```

## Development

```bash
./gradlew build
./gradlew test
```

## License

MIT
