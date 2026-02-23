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
}
