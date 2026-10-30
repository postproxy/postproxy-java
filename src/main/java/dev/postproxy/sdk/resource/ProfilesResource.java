package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfilesResource {

    private final PostProxyClient client;

    public ProfilesResource(PostProxyClient client) {
        this.client = client;
    }

    public ListResponse<Profile> list() {
        return list(null);
    }

    public ListResponse<Profile> list(String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles", query, new TypeReference<>() {});
    }

    public Profile get(String id) {
        return get(id, null);
    }

    public Profile get(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id, query, new TypeReference<>() {});
    }

    public ListResponse<Placement> placements(String id) {
        return placements(id, null);
    }

    public ListResponse<Placement> placements(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/placements", query, new TypeReference<>() {});
    }

    /**
     * Fetch the profile stats timeseries. {@code placementId} is required for
     * facebook, linkedin, and telegram profiles.
     */
    public ProfileStatsResponse getProfileStats(String id, String placementId) {
        return getProfileStats(id, placementId, null, null, null);
    }

    public ProfileStatsResponse getProfileStats(String id, String placementId, String from, String to) {
        return getProfileStats(id, placementId, from, to, null);
    }

    public ProfileStatsResponse getProfileStats(String id, String placementId, String from, String to, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (placementId != null) query.put("placement_id", placementId);
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/stats", query, new TypeReference<>() {});
    }

    /**
     * Per-day series. {@link DailyStatsAttribution#PUBLISH} attributes post metrics to the day the
     * post was published instead of the day they were received.
     */
    public DailyStatsResponse dailyStats(String id) {
        return dailyStats(id, null, null, null, null, null);
    }

    public DailyStatsResponse dailyStats(String id, String placementId, String from, String to,
                                         DailyStatsAttribution attribution, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (placementId != null) query.put("placement_id", placementId);
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);
        if (attribution != null) query.put("attribution", attribution.getValue());
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/daily_stats", query, new TypeReference<>() {});
    }

    public FollowerHistoryResponse followerHistory(String id) {
        return followerHistory(id, null, null, null, null);
    }

    public FollowerHistoryResponse followerHistory(String id, String placementId, String from, String to,
                                                   String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (placementId != null) query.put("placement_id", placementId);
        if (from != null) query.put("from", from);
        if (to != null) query.put("to", to);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/follower_history", query, new TypeReference<>() {});
    }

    /** Instagram only: whether a DM participant is verified and follows you. */
    public ParticipantStatus participantStatus(String id, String userId) {
        return participantStatus(id, userId, null);
    }

    public ParticipantStatus participantStatus(String id, String userId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/participants/" + encodePath(userId), query,
                new TypeReference<>() {});
    }

    /**
     * LinkedIn only: resolves a company page URL (or vanity name) to the URN used to @-mention it
     * in a post.
     */
    public ResolvedMention resolveMention(String id, String url) {
        return resolveMention(id, url, null);
    }

    public ResolvedMention resolveMention(String id, String url, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("url", url);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/resolve_mention", query, new TypeReference<>() {});
    }

    /**
     * Music and original sounds an Instagram reel can attach via {@code InstagramParams.audioId}.
     * Profiles connected via Facebook login only.
     */
    public ListResponse<Map<String, Object>> instagramAvailableAudio(String id) {
        return instagramAvailableAudio(id, null, null, null);
    }

    public ListResponse<Map<String, Object>> instagramAvailableAudio(String id, InstagramAudioType audioType,
                                                                     String searchQuery, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (audioType != null) query.put("audio_type", audioType.getValue());
        if (searchQuery != null) query.put("search_query", searchQuery);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/instagram/available_audio", query, new TypeReference<>() {});
    }

    /**
     * Posts from other accounts that invited this Instagram profile as a collaborator. Profiles
     * connected via Facebook login only. Pass {@code nextCursor()} as {@code after} for the next page.
     */
    public InstagramCollaborationInvitesResponse instagramCollaborationInvites(String id) {
        return instagramCollaborationInvites(id, null, null);
    }

    public InstagramCollaborationInvitesResponse instagramCollaborationInvites(String id, String after) {
        return instagramCollaborationInvites(id, after, null);
    }

    public InstagramCollaborationInvitesResponse instagramCollaborationInvites(String id, String after,
                                                                               String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (after != null) query.put("after", after);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/instagram/collaboration_invites", query,
                new TypeReference<>() {});
    }

    public InstagramCollaborationInviteResult acceptInstagramCollaborationInvite(String id, String mediaId) {
        return acceptInstagramCollaborationInvite(id, mediaId, null, null);
    }

    public InstagramCollaborationInviteResult acceptInstagramCollaborationInvite(String id, String mediaId,
                                                                                 String profileGroupId,
                                                                                 String idempotencyKey) {
        return collaborationInvite(id, "accept_collaboration_invite", mediaId, profileGroupId, idempotencyKey);
    }

    public InstagramCollaborationInviteResult declineInstagramCollaborationInvite(String id, String mediaId) {
        return declineInstagramCollaborationInvite(id, mediaId, null, null);
    }

    public InstagramCollaborationInviteResult declineInstagramCollaborationInvite(String id, String mediaId,
                                                                                  String profileGroupId,
                                                                                  String idempotencyKey) {
        return collaborationInvite(id, "decline_collaboration_invite", mediaId, profileGroupId, idempotencyKey);
    }

    private InstagramCollaborationInviteResult collaborationInvite(String id, String action, String mediaId,
                                                                   String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.post("/api/profiles/" + id + "/instagram/" + action, query,
                Map.of("media_id", mediaId), new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Pinterest only. Boards are the profile's placements; the new board's {@code id()} is the
     * {@code boardId} you publish to.
     */
    public Placement createPinterestBoard(String id, String name) {
        return createPinterestBoard(id, name, null, null);
    }

    public Placement createPinterestBoard(String id, String name, String description, PinterestBoardPrivacy privacy) {
        return createPinterestBoard(id, name, description, privacy, null, null);
    }

    public Placement createPinterestBoard(String id, String name, String description, PinterestBoardPrivacy privacy,
                                          String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        if (description != null) body.put("description", description);
        if (privacy != null) body.put("privacy", privacy.getValue());

        return client.post("/api/profiles/" + id + "/pinterest/create_board", query, body,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Also deletes every Pin on the board, on Pinterest. Cannot be undone. */
    public PinterestBoardDeleted deletePinterestBoard(String id, String boardId) {
        return deletePinterestBoard(id, boardId, null, null);
    }

    public PinterestBoardDeleted deletePinterestBoard(String id, String boardId, String profileGroupId,
                                                      String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("board_id", boardId);
        putProfileGroup(query, profileGroupId);

        return client.delete("/api/profiles/" + id + "/pinterest/delete_board", query,
                new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * YouTube only. Up to 50 playlists per page; pass {@code nextPageToken()} as
     * {@code pageToken} for the next one.
     */
    public YouTubePlaylistsResponse youtubePlaylists(String id) {
        return youtubePlaylists(id, null, null);
    }

    public YouTubePlaylistsResponse youtubePlaylists(String id, String pageToken) {
        return youtubePlaylists(id, pageToken, null);
    }

    public YouTubePlaylistsResponse youtubePlaylists(String id, String pageToken, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (pageToken != null) query.put("page_token", pageToken);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/youtube/playlists", query, new TypeReference<>() {});
    }

    public YouTubePlaylist createYouTubePlaylist(String id, String title) {
        return createYouTubePlaylist(id, title, null, null);
    }

    public YouTubePlaylist createYouTubePlaylist(String id, String title, String description,
                                                 YouTubePrivacy privacyStatus) {
        return createYouTubePlaylist(id, title, description, privacyStatus, null, null);
    }

    public YouTubePlaylist createYouTubePlaylist(String id, String title, String description,
                                                 YouTubePrivacy privacyStatus, String profileGroupId,
                                                 String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        if (description != null) body.put("description", description);
        if (privacyStatus != null) body.put("privacy_status", privacyStatus.getValue());

        return client.post("/api/profiles/" + id + "/youtube/create_playlist", query, body,
                new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Updates a playlist. {@code null} arguments keep their current value; an empty
     * {@code description} clears it.
     */
    public YouTubePlaylist updateYouTubePlaylist(String id, String playlistId, String title, String description,
                                                 YouTubePrivacy privacyStatus) {
        return updateYouTubePlaylist(id, playlistId, title, description, privacyStatus, null, null);
    }

    public YouTubePlaylist updateYouTubePlaylist(String id, String playlistId, String title, String description,
                                                 YouTubePrivacy privacyStatus, String profileGroupId,
                                                 String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("playlist_id", playlistId);
        if (title != null) body.put("title", title);
        if (description != null) body.put("description", description);
        if (privacyStatus != null) body.put("privacy_status", privacyStatus.getValue());

        return client.patch("/api/profiles/" + id + "/youtube/update_playlist", query, body,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Deletes the playlist. The videos in it are not deleted. */
    public YouTubePlaylistDeleted deleteYouTubePlaylist(String id, String playlistId) {
        return deleteYouTubePlaylist(id, playlistId, null, null);
    }

    public YouTubePlaylistDeleted deleteYouTubePlaylist(String id, String playlistId, String profileGroupId,
                                                        String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("playlist_id", playlistId);
        putProfileGroup(query, profileGroupId);

        return client.delete("/api/profiles/" + id + "/youtube/delete_playlist", query,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Facebook only. {@code pageId} is the placement id from {@link #placements(String)}. */
    public Map<String, Object> facebookPage(String id, String pageId) {
        return facebookPage(id, pageId, null, null);
    }

    public Map<String, Object> facebookPage(String id, String pageId, String fields, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("page_id", pageId);
        if (fields != null) query.put("fields", fields);
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/facebook/page", query, new TypeReference<>() {});
    }

    /**
     * Updates the Page's about / contact fields, e.g. {@code about} (255 characters or fewer),
     * {@code description}, {@code website}, {@code phone}, {@code emails}.
     */
    public Map<String, Object> updateFacebookPage(String id, String pageId, Map<String, Object> fields) {
        return updateFacebookPage(id, pageId, fields, null, null);
    }

    public Map<String, Object> updateFacebookPage(String id, String pageId, Map<String, Object> fields,
                                                  String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        Map<String, Object> body = new LinkedHashMap<>(fields);
        body.put("page_id", pageId);

        return client.patch("/api/profiles/" + id + "/facebook/update_page", query, body,
                new TypeReference<>() {}, idempotencyKey);
    }

    /** Moves the profile (with its placements) to another profile group. */
    public Profile assignToGroup(String id, String targetProfileGroupId) {
        return assignToGroup(id, targetProfileGroupId, null, null);
    }

    public Profile assignToGroup(String id, String targetProfileGroupId, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.patch("/api/profiles/" + id + "/assign_to_group", query,
                Map.of("target_profile_group_id", targetProfileGroupId), new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Moves a placement (e.g. a Facebook Page or Telegram channel) to another
     * profile group. {@code placementId} is the placement's external ID as
     * returned by {@link #placements(String)}.
     */
    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId) {
        return assignPlacementToGroup(id, placementId, targetProfileGroupId, null);
    }

    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId, String profileGroupId) {
        return assignPlacementToGroup(id, placementId, targetProfileGroupId, profileGroupId, null);
    }

    public AssignedPlacement assignPlacementToGroup(String id, String placementId, String targetProfileGroupId,
                                                    String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("placement_id", placementId);
        body.put("target_profile_group_id", targetProfileGroupId);

        return client.patch("/api/profiles/" + id + "/assign_placement_to_group", query, body, new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Imports older posts from the platform.
     *
     * <p>Walks the profile's feed backwards from the newest post until it
     * reaches {@code from} or the platform stops returning posts. Runs in the
     * background — poll {@link #postSync(String, String)} with the returned id
     * for progress. Only one backfill runs per profile; starting a second
     * throws {@link dev.postproxy.sdk.exception.ConflictException} carrying the
     * running one's {@code profile_sync_id}.
     */
    public PostSync backfillPosts(String id, String from) {
        return backfillPosts(id, from, null, null);
    }

    public PostSync backfillPosts(String id, String from, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/profiles/" + id + "/backfill_posts", query,
                Map.of("from", from), new TypeReference<>() {}, idempotencyKey);
    }

    /**
     * Lists post sync runs for a profile, newest first. Runs are kept for 30 days.
     */
    public PaginatedResponse<PostSync> postSyncs(String id) {
        return postSyncs(id, null, null, null, null, null);
    }

    public PaginatedResponse<PostSync> postSyncs(String id, PostSyncTrigger trigger, PostSyncStatus status,
                                                 Integer page, Integer perPage, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (trigger != null) query.put("trigger", trigger.getValue());
        if (status != null) query.put("status", status.getValue());
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/post_syncs", query, new TypeReference<>() {});
    }

    /**
     * Fetches a single run. Poll this to follow a backfill to completion — the
     * run is finished when its status is COMPLETED or FAILED.
     */
    public PostSync postSync(String id, String postSyncId) {
        return postSync(id, postSyncId, null);
    }

    public PostSync postSync(String id, String postSyncId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/post_syncs/" + postSyncId, query, new TypeReference<>() {});
    }

    /**
     * Imports DM history back to {@code from}. Like {@link #backfillPosts(String, String)}, runs in
     * the background and only one runs per profile.
     */
    public ChatSync backfillChats(String id, String from) {
        return backfillChats(id, from, null, null);
    }

    public ChatSync backfillChats(String id, String from, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.post("/api/profiles/" + id + "/backfill_chats", query,
                Map.of("from", from), new TypeReference<>() {}, idempotencyKey);
    }

    public PaginatedResponse<ChatSync> chatSyncs(String id) {
        return chatSyncs(id, null, null, null, null, null);
    }

    public PaginatedResponse<ChatSync> chatSyncs(String id, PostSyncTrigger trigger, PostSyncStatus status,
                                                 Integer page, Integer perPage, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        if (trigger != null) query.put("trigger", trigger.getValue());
        if (status != null) query.put("status", status.getValue());
        if (page != null) query.put("page", page.toString());
        if (perPage != null) query.put("per_page", perPage.toString());
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/chat_syncs", query, new TypeReference<>() {});
    }

    public ChatSync chatSync(String id, String chatSyncId) {
        return chatSync(id, chatSyncId, null);
    }

    public ChatSync chatSync(String id, String chatSyncId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        return client.get("/api/profiles/" + id + "/chat_syncs/" + chatSyncId, query, new TypeReference<>() {});
    }

    /**
     * Lists DM ice breakers. Supported for Instagram profiles only.
     */
    public IceBreakersResponse iceBreakers(String id) {
        return iceBreakers(id, null);
    }

    public IceBreakersResponse iceBreakers(String id, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.get("/api/profiles/" + id + "/ice_breakers", query, new TypeReference<>() {});
    }

    /**
     * Replaces the DM ice breakers for a profile (1-4 items).
     */
    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers) {
        return setIceBreakers(id, iceBreakers, null);
    }

    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers, String profileGroupId) {
        return setIceBreakers(id, iceBreakers, profileGroupId, null);
    }

    public SuccessResponse setIceBreakers(String id, List<IceBreaker> iceBreakers, String profileGroupId,
                                          String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.post("/api/profiles/" + id + "/ice_breakers", query,
                Map.of("ice_breakers", iceBreakers), new TypeReference<>() {}, idempotencyKey);
    }

    public SuccessResponse deleteIceBreakers(String id) {
        return deleteIceBreakers(id, null);
    }

    public SuccessResponse deleteIceBreakers(String id, String profileGroupId) {
        return deleteIceBreakers(id, profileGroupId, null);
    }

    public SuccessResponse deleteIceBreakers(String id, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id + "/ice_breakers", query, new TypeReference<>() {}, idempotencyKey);
    }

    public SuccessResponse delete(String id) {
        return delete(id, null);
    }

    public SuccessResponse delete(String id, String profileGroupId) {
        return delete(id, profileGroupId, null);
    }

    public SuccessResponse delete(String id, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);

        return client.delete("/api/profiles/" + id, query, new TypeReference<>() {}, idempotencyKey);
    }

    private void putProfileGroup(Map<String, String> query, String profileGroupId) {
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
    }

    private static String encodePath(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
