package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.PostsResource;
import dev.postproxy.sdk.resource.ProfileGroupsResource;
import dev.postproxy.sdk.resource.ProfilesResource;

public class PostProxy {

    private static final String DEFAULT_BASE_URL = "https://api.postproxy.dev";

    private final PostsResource posts;
    private final ProfilesResource profiles;
    private final ProfileGroupsResource profileGroups;

    private PostProxy(String apiKey, String baseUrl, String profileGroupId) {
        PostProxyClient client = new PostProxyClient(apiKey, baseUrl, profileGroupId);
        this.posts = new PostsResource(client);
        this.profiles = new ProfilesResource(client);
        this.profileGroups = new ProfileGroupsResource(client);
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    public PostsResource posts() {
        return posts;
    }

    public ProfilesResource profiles() {
        return profiles;
    }

    public ProfileGroupsResource profileGroups() {
        return profileGroups;
    }

    public static class Builder {
        private final String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private String profileGroupId;

        private Builder(String apiKey) {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("API key is required");
            }
            this.apiKey = apiKey;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder profileGroupId(String profileGroupId) {
            this.profileGroupId = profileGroupId;
            return this;
        }

        public PostProxy build() {
            return new PostProxy(apiKey, baseUrl, profileGroupId);
        }
    }
}
