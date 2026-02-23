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

// Publish a draft
var post = client.posts().publishDraft("post-id");

// Delete a post
var result = client.posts().delete("post-id");
System.out.println(result.deleted()); // true
```

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
| `Post` | id, body, status, scheduledAt, createdAt, platforms |
| `Profile` | id, name, status, platform, profileGroupId, expiresAt, postCount |
| `ProfileGroup` | id, name, profilesCount |
| `PlatformResult` | platform, status, params, error, attemptedAt, insights |
| `ListResponse<T>` | data |
| `PaginatedResponse<T>` | data, total, page, perPage |

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
