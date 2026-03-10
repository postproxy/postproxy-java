package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.GetStatsParams;
import dev.postproxy.sdk.param.InstagramParams;
import dev.postproxy.sdk.param.ListPostsParams;
import dev.postproxy.sdk.param.PlatformParams;
import dev.postproxy.sdk.param.ThreadChildInput;
import dev.postproxy.sdk.param.TikTokParams;
import dev.postproxy.sdk.model.InstagramFormat;
import dev.postproxy.sdk.model.PostStatus;
import dev.postproxy.sdk.model.TikTokPrivacy;
import dev.postproxy.sdk.resource.PostsResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostsResourceTest {

    private static final Map<String, Object> MOCK_POST = Map.of(
            "id", "post-1",
            "body", "Hello world",
            "status", "pending",
            "created_at", "2025-01-01T00:00:00Z",
            "platforms", List.of()
    );

    private static final Map<String, Object> MOCK_PAGINATED = Map.of(
            "total", 1,
            "page", 0,
            "per_page", 10,
            "data", List.of(MOCK_POST)
    );

    @Test
    void listsPostsWithPagination() {
        var mock = new MockPostProxyClient(MOCK_PAGINATED, 200, null);
        var posts = new PostsResource(mock);

        var result = posts.list(ListPostsParams.builder().page(0).perPage(10).build());
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals("post-1", result.data().get(0).id());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("page=0"));
        assertTrue(url.contains("per_page=10"));
    }

    @Test
    void listsPostsFilteredByStatus() {
        var mock = new MockPostProxyClient(MOCK_PAGINATED, 200, null);
        var posts = new PostsResource(mock);
        posts.list(ListPostsParams.builder().status(PostStatus.DRAFT).build());
        assertTrue(mock.getRequests().get(0).url().contains("status=draft"));
    }

    @Test
    void listsPostsFilteredByPlatforms() {
        var mock = new MockPostProxyClient(MOCK_PAGINATED, 200, null);
        var posts = new PostsResource(mock);
        posts.list(ListPostsParams.builder()
                .platforms(List.of(Platform.INSTAGRAM, Platform.TIKTOK))
                .build());
        assertTrue(mock.getRequests().get(0).url().contains("platforms=instagram,tiktok")
                || mock.getRequests().get(0).url().contains("platforms=instagram%2Ctiktok"));
    }

    @Test
    void getsPostById() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        var post = posts.get("post-1");
        assertEquals("post-1", post.id());
        assertEquals("Hello world", post.body());
        assertTrue(mock.getRequests().get(0).url().contains("/posts/post-1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsPostWithJsonPayload() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        var post = posts.create(CreatePostParams.builder()
                .body("Hello world")
                .profiles(List.of("profile-1"))
                .media(List.of("https://example.com/image.jpg"))
                .build());

        assertEquals("post-1", post.id());
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        var postMap = (Map<String, Object>) body.get("post");
        assertEquals("Hello world", postMap.get("body"));
        assertEquals(List.of("profile-1"), body.get("profiles"));
        assertEquals(List.of("https://example.com/image.jpg"), body.get("media"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsPostWithPlatformParams() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        posts.create(CreatePostParams.builder()
                .body("Hello")
                .profiles(List.of("profile-1"))
                .platforms(PlatformParams.builder()
                        .instagram(InstagramParams.builder()
                                .format(InstagramFormat.REEL)
                                .collaborators(List.of("user1"))
                                .build())
                        .tiktok(TikTokParams.builder()
                                .privacyStatus(TikTokPrivacy.PUBLIC_TO_EVERYONE)
                                .build())
                        .build())
                .build());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertNotNull(body.get("platforms"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsScheduledPost() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        posts.create(CreatePostParams.builder()
                .body("Hello")
                .profiles(List.of("profile-1"))
                .scheduledAt("2025-12-25T09:00:00Z")
                .build());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        var postMap = (Map<String, Object>) body.get("post");
        assertEquals("2025-12-25T09:00:00Z", postMap.get("scheduled_at"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsDraftPost() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        posts.create(CreatePostParams.builder()
                .body("Hello")
                .profiles(List.of("profile-1"))
                .draft(true)
                .build());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        var postMap = (Map<String, Object>) body.get("post");
        assertEquals(true, postMap.get("draft"));
    }

    @Test
    void publishesDraft() {
        var mock = new MockPostProxyClient(MOCK_POST, 200, null);
        var posts = new PostsResource(mock);
        var post = posts.publishDraft("post-1");
        assertEquals("pending", post.status().getValue());
        assertEquals("POST", mock.getRequests().get(0).method());
        assertTrue(mock.getRequests().get(0).url().contains("/posts/post-1/publish"));
    }

    @Test
    void deletesPost() {
        var mock = new MockPostProxyClient(Map.of("deleted", true), 200, null);
        var posts = new PostsResource(mock);
        var result = posts.delete("post-1");
        assertTrue(result.deleted());
        assertEquals("DELETE", mock.getRequests().get(0).method());
    }

    private static final Map<String, Object> MOCK_STATS = Map.of(
            "data", Map.of(
                    "post-1", Map.of(
                            "platforms", List.of(Map.of(
                                    "profile_id", "prof-1",
                                    "platform", "instagram",
                                    "records", List.of(
                                            Map.of(
                                                    "stats", Map.of("impressions", 1200, "likes", 85),
                                                    "recorded_at", "2026-02-20T12:00:00Z"
                                            ),
                                            Map.of(
                                                    "stats", Map.of("impressions", 1523, "likes", 102),
                                                    "recorded_at", "2026-02-21T04:00:00Z"
                                            )
                                    )
                            ))
                    ),
                    "post-2", Map.of(
                            "platforms", List.of(Map.of(
                                    "profile_id", "prof-2",
                                    "platform", "twitter",
                                    "records", List.of(
                                            Map.of(
                                                    "stats", Map.of("impressions", 430, "likes", 22, "retweets", 5),
                                                    "recorded_at", "2026-02-20T12:00:00Z"
                                            )
                                    )
                            ))
                    )
            )
    );

    @Test
    void getsStatsForPosts() {
        var mock = new MockPostProxyClient(MOCK_STATS, 200, null);
        var posts = new PostsResource(mock);

        var result = posts.stats(GetStatsParams.builder()
                .postIds(List.of("post-1", "post-2"))
                .build());

        assertEquals(2, result.data().size());

        var post1 = result.data().get("post-1");
        assertEquals(1, post1.platforms().size());
        assertEquals("prof-1", post1.platforms().get(0).profileId());
        assertEquals("instagram", post1.platforms().get(0).platform());
        assertEquals(2, post1.platforms().get(0).records().size());
        assertEquals(1200, post1.platforms().get(0).records().get(0).stats().get("impressions"));
        assertEquals("2026-02-20T12:00:00Z", post1.platforms().get(0).records().get(0).recordedAt());

        var post2 = result.data().get("post-2");
        assertEquals("twitter", post2.platforms().get(0).platform());
        assertEquals(5, post2.platforms().get(0).records().get(0).stats().get("retweets"));

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("post_ids=post-1,post-2"));
    }

    @Test
    void getsStatsWithFilters() {
        var mock = new MockPostProxyClient(MOCK_STATS, 200, null);
        var posts = new PostsResource(mock);

        posts.stats(GetStatsParams.builder()
                .postIds(List.of("post-1"))
                .profiles(List.of("instagram", "prof-1"))
                .from("2026-02-01T00:00:00Z")
                .to("2026-02-24T00:00:00Z")
                .build());

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("post_ids=post-1"));
        assertTrue(url.contains("profiles=instagram,prof-1"));
        assertTrue(url.contains("from=2026-02-01T00:00:00Z"));
        assertTrue(url.contains("to=2026-02-24T00:00:00Z"));
    }

    @Test
    void statsRequiresPostIds() {
        assertThrows(IllegalArgumentException.class, () ->
                GetStatsParams.builder().build());
        assertThrows(IllegalArgumentException.class, () ->
                GetStatsParams.builder().postIds(List.of()).build());
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsPostWithThread() {
        var mockResponse = Map.of(
                "id", "post-thread",
                "body", "Main post",
                "status", "pending",
                "created_at", "2025-01-01T00:00:00Z",
                "platforms", List.of(),
                "thread", List.of(
                        Map.of("id", "t-1", "body", "Reply 1", "media", List.of()),
                        Map.of("id", "t-2", "body", "Reply 2", "media", List.of())
                )
        );
        var mock = new MockPostProxyClient(mockResponse, 200, null);
        var posts = new PostsResource(mock);

        var post = posts.create(CreatePostParams.builder()
                .body("Main post")
                .profiles(List.of("profile-1"))
                .thread(List.of(
                        ThreadChildInput.builder().body("Reply 1").build(),
                        ThreadChildInput.builder().body("Reply 2")
                                .media(List.of("https://example.com/img.jpg")).build()
                ))
                .build());

        assertEquals("post-thread", post.id());
        assertEquals(2, post.thread().size());
        assertEquals("Reply 1", post.thread().get(0).body());

        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        var thread = (List<ThreadChildInput>) body.get("thread");
        assertEquals(2, thread.size());
        assertEquals("Reply 1", thread.get(0).body());
    }

    @SuppressWarnings("unchecked")
    @Test
    void createsThreadWithMediaFiles(@TempDir Path tempDir) throws IOException {
        var img1 = tempDir.resolve("thread1.jpg");
        var img2 = tempDir.resolve("thread2.png");
        Files.write(img1, new byte[]{1, 2, 3});
        Files.write(img2, new byte[]{4, 5, 6});

        var mockResponse = Map.of(
                "id", "post-thread-files",
                "body", "Main post",
                "status", "pending",
                "created_at", "2025-01-01T00:00:00Z",
                "platforms", List.of(),
                "thread", List.of(
                        Map.of("id", "t-1", "body", "Reply 1", "media", List.of()),
                        Map.of("id", "t-2", "body", "Reply 2", "media", List.of())
                )
        );
        var mock = new MockPostProxyClient(mockResponse, 200, null);
        var posts = new PostsResource(mock);

        var post = posts.create(CreatePostParams.builder()
                .body("Main post")
                .profiles(List.of("profile-1"))
                .thread(List.of(
                        ThreadChildInput.builder().body("Reply 1")
                                .mediaFiles(List.of(img1)).build(),
                        ThreadChildInput.builder().body("Reply 2")
                                .media(List.of("https://example.com/img.jpg"))
                                .mediaFiles(List.of(img2)).build()
                ))
                .build());

        assertEquals("post-thread-files", post.id());

        // Should use multipart path
        var body = (Map<String, Object>) mock.getRequests().get(0).body();
        assertEquals("Main post", body.get("post[body]"));
        assertEquals("Reply 1", body.get("thread[0][body]"));
        assertEquals("Reply 2", body.get("thread[1][body]"));
        assertEquals(List.of("https://example.com/img.jpg"), body.get("thread[1][media][]"));

        var fileGroups = (Map<String, List<Path>>) body.get("__fileGroups");
        assertEquals(List.of(img1), fileGroups.get("thread[0][media][]"));
        assertEquals(List.of(img2), fileGroups.get("thread[1][media][]"));
        assertNull(fileGroups.get("media[]")); // no parent media files
    }

    @Test
    void getsPostWithMediaAndThread() {
        var mockResponse = Map.of(
                "id", "post-1",
                "body", "Hello",
                "status", "media_processing_failed",
                "created_at", "2025-01-01T00:00:00Z",
                "platforms", List.of(),
                "media", List.of(
                        Map.of("id", "m-1", "status", "processed", "content_type", "image/jpeg", "url", "https://cdn.example.com/img.jpg")
                ),
                "thread", List.of(
                        Map.of("id", "t-1", "body", "Reply", "media", List.of())
                )
        );
        var mock = new MockPostProxyClient(mockResponse, 200, null);
        var posts = new PostsResource(mock);

        var post = posts.get("post-1");
        assertEquals("media_processing_failed", post.status().getValue());
        assertEquals(1, post.media().size());
        assertEquals("processed", post.media().get(0).status().getValue());
        assertEquals(1, post.thread().size());
        assertEquals("Reply", post.thread().get(0).body());
    }
}
