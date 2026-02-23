package dev.postproxy.sdk;

import dev.postproxy.sdk.model.Platform;
import dev.postproxy.sdk.param.CreatePostParams;
import dev.postproxy.sdk.param.InstagramParams;
import dev.postproxy.sdk.param.ListPostsParams;
import dev.postproxy.sdk.param.PlatformParams;
import dev.postproxy.sdk.param.TikTokParams;
import dev.postproxy.sdk.model.InstagramFormat;
import dev.postproxy.sdk.model.PostStatus;
import dev.postproxy.sdk.model.TikTokPrivacy;
import dev.postproxy.sdk.resource.PostsResource;
import org.junit.jupiter.api.Test;

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
}
