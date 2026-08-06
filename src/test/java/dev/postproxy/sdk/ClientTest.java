package dev.postproxy.sdk;

import dev.postproxy.sdk.exception.*;
import dev.postproxy.sdk.resource.PostsResource;
import dev.postproxy.sdk.resource.ProfilesResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void sendsAuthorizationHeader() {
        var mock = new MockPostProxyClient(Map.of("data", List.of()), 200, null);
        var profiles = new ProfilesResource(mock);
        profiles.list();
        assertEquals(1, mock.getRequests().size());
        assertTrue(mock.getRequests().get(0).url().contains("/api/profiles"));
    }

    @Test
    void includesDefaultProfileGroupId() {
        var mock = new MockPostProxyClient(Map.of("data", List.of()), 200, "pg-123");
        var profiles = new ProfilesResource(mock);
        profiles.list();
        assertTrue(mock.getRequests().get(0).url().contains("profile_group_id=pg-123"));
    }

    @Test
    void allowsOverridingProfileGroupIdPerRequest() {
        var mock = new MockPostProxyClient(Map.of("data", List.of()), 200, "pg-default");
        var profiles = new ProfilesResource(mock);
        profiles.list("pg-override");
        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("profile_group_id=pg-override"));
        assertFalse(url.contains("pg-default"));
    }

    @Test
    void throwsAuthenticationExceptionOn401() {
        var mock = new MockPostProxyClient(Map.of("error", "Invalid API key"), 401, null);
        var profiles = new ProfilesResource(mock);
        assertThrows(AuthenticationException.class, profiles::list);
    }

    @Test
    void throwsNotFoundExceptionOn404() {
        var mock = new MockPostProxyClient(Map.of("error", "Not found"), 404, null);
        var profiles = new ProfilesResource(mock);
        assertThrows(NotFoundException.class, () -> profiles.get("bad-id"));
    }

    @Test
    void throwsValidationExceptionOn422() {
        var mock = new MockPostProxyClient(Map.of("error", "Validation failed"), 422, null);
        var posts = new PostsResource(mock);
        var params = dev.postproxy.sdk.param.CreatePostParams.builder()
                .body("test").profiles(List.of("profile-1")).build();
        assertThrows(ValidationException.class, () -> posts.create(params));
    }

    @Test
    void throwsBadRequestExceptionOn400() {
        var mock = new MockPostProxyClient(Map.of("error", "Bad request"), 400, null);
        var posts = new PostsResource(mock);
        var params = dev.postproxy.sdk.param.CreatePostParams.builder()
                .body("test").profiles(List.of("profile-1")).build();
        assertThrows(BadRequestException.class, () -> posts.create(params));
    }

    @Test
    void throwsConflictExceptionOn409() {
        var mock = new MockPostProxyClient(
                Map.of("error", "Duplicate post", "duplicate_post_id", "post-1"), 409, null);
        var posts = new dev.postproxy.sdk.resource.PostsResource(mock);

        var e = assertThrows(dev.postproxy.sdk.exception.ConflictException.class,
                () -> posts.create(dev.postproxy.sdk.param.CreatePostParams.builder()
                        .body("hello")
                        .profiles(java.util.List.of("prof-1"))
                        .build()));

        assertEquals(409, e.getStatusCode());
        assertEquals("post-1", e.getResponse().get("duplicate_post_id"));
    }

    @Test
    void sendsIdempotencyKeyFromParams() {
        var mock = new MockPostProxyClient(
                Map.of("id", "post-1", "body", "hello", "created_at", "2026-08-06T00:00:00Z"), 200, null);
        var posts = new dev.postproxy.sdk.resource.PostsResource(mock);

        posts.create(dev.postproxy.sdk.param.CreatePostParams.builder()
                .body("hello")
                .profiles(java.util.List.of("prof-1"))
                .idempotencyKey("3f8b1c94-6a2d-4f0e-9d31-7c5e2a8b4f10")
                .build());

        assertEquals("3f8b1c94-6a2d-4f0e-9d31-7c5e2a8b4f10", mock.getRequests().get(0).idempotencyKey());
    }

    @Test
    void omitsIdempotencyKeyByDefault() {
        var mock = new MockPostProxyClient(
                Map.of("id", "post-1", "body", "hello", "created_at", "2026-08-06T00:00:00Z"), 200, null);
        var posts = new dev.postproxy.sdk.resource.PostsResource(mock);

        posts.create(dev.postproxy.sdk.param.CreatePostParams.builder()
                .body("hello")
                .profiles(java.util.List.of("prof-1"))
                .build());

        assertNull(mock.getRequests().get(0).idempotencyKey());
    }
}
