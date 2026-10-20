package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.PostsResource;
import dev.postproxy.sdk.resource.ProfileGroupsResource;
import dev.postproxy.sdk.resource.ProfilesResource;
import dev.postproxy.sdk.resource.WebhooksResource;
import dev.postproxy.sdk.resource.QueuesResource;
import dev.postproxy.sdk.resource.CommentsResource;
import dev.postproxy.sdk.resource.ProfileCommentsResource;
import dev.postproxy.sdk.resource.ChatsResource;
import dev.postproxy.sdk.resource.MessagesResource;
import dev.postproxy.sdk.resource.PlacementsResource;
import dev.postproxy.sdk.resource.ApiKeysResource;
import dev.postproxy.sdk.resource.GoogleBusinessResource;
import dev.postproxy.sdk.resource.WhatsAppTemplatesResource;
import dev.postproxy.sdk.resource.WhatsAppResource;

public class PostProxy {

    private static final String DEFAULT_BASE_URL = "https://api.postproxy.dev";

    private final PostsResource posts;
    private final ProfilesResource profiles;
    private final ProfileGroupsResource profileGroups;
    private final WebhooksResource webhooks;
    private final QueuesResource queues;
    private final CommentsResource comments;
    private final ProfileCommentsResource profileComments;
    private final ChatsResource chats;
    private final MessagesResource messages;
    private final PlacementsResource placements;
    private final ApiKeysResource apiKeys;
    private final GoogleBusinessResource googleBusiness;
    private final WhatsAppTemplatesResource whatsappTemplates;
    private final WhatsAppResource whatsapp;

    private PostProxy(String apiKey, String baseUrl, String profileGroupId) {
        PostProxyClient client = new PostProxyClient(apiKey, baseUrl, profileGroupId);
        this.posts = new PostsResource(client);
        this.profiles = new ProfilesResource(client);
        this.profileGroups = new ProfileGroupsResource(client);
        this.webhooks = new WebhooksResource(client);
        this.queues = new QueuesResource(client);
        this.comments = new CommentsResource(client);
        this.profileComments = new ProfileCommentsResource(client);
        this.chats = new ChatsResource(client);
        this.messages = new MessagesResource(client);
        this.placements = new PlacementsResource(client);
        this.apiKeys = new ApiKeysResource(client);
        this.googleBusiness = new GoogleBusinessResource(client);
        this.whatsappTemplates = new WhatsAppTemplatesResource(client);
        this.whatsapp = new WhatsAppResource(client);
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

    public WebhooksResource webhooks() {
        return webhooks;
    }

    public QueuesResource queues() {
        return queues;
    }

    public CommentsResource comments() {
        return comments;
    }

    public ProfileCommentsResource profileComments() {
        return profileComments;
    }

    public ChatsResource chats() {
        return chats;
    }

    public MessagesResource messages() {
        return messages;
    }

    public PlacementsResource placements() {
        return placements;
    }

    public ApiKeysResource apiKeys() {
        return apiKeys;
    }

    public GoogleBusinessResource googleBusiness() {
        return googleBusiness;
    }

    public WhatsAppTemplatesResource whatsappTemplates() {
        return whatsappTemplates;
    }

    public WhatsAppResource whatsapp() {
        return whatsapp;
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
