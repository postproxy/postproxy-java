package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;
import dev.postproxy.sdk.model.MediaDownload;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WhatsApp number administration: registration, business profile, display name, username,
 * blocked users, groups and conversion events.
 *
 * <p>{@code phoneNumberId} is the placement id from {@code profiles().placements(profileId)}.
 * Dataset and conversion calls are account-level and don't take one.
 */
public class WhatsAppResource {

    private final PostProxyClient client;

    public WhatsAppResource(PostProxyClient client) {
        this.client = client;
    }

    // --- Phone number ---

    public Map<String, Object> numberInfo(String profileId, String phoneNumberId) {
        return numberInfo(profileId, phoneNumberId, null);
    }

    public Map<String, Object> numberInfo(String profileId, String phoneNumberId, String profileGroupId) {
        return call("GET", profileId, "number_info", data(phoneNumberId), profileGroupId, null);
    }

    /** {@code pin} is the 6-digit two-step verification PIN. */
    public Map<String, Object> registerPhoneNumber(String profileId, String phoneNumberId, String pin) {
        return registerPhoneNumber(profileId, phoneNumberId, pin, null, null);
    }

    public Map<String, Object> registerPhoneNumber(String profileId, String phoneNumberId, String pin,
                                                   String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("pin", pin);
        return call("POST", profileId, "register_phone_number", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> requestVerificationCode(String profileId, String phoneNumberId) {
        return requestVerificationCode(profileId, phoneNumberId, null, null, null, null);
    }

    /** {@code method} is {@code SMS} or {@code VOICE}. */
    public Map<String, Object> requestVerificationCode(String profileId, String phoneNumberId, String method,
                                                       String language, String profileGroupId,
                                                       String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("method", method);
        data.put("language", language);
        return call("POST", profileId, "request_verification_code", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> verifyPhoneNumber(String profileId, String phoneNumberId, String code) {
        return verifyPhoneNumber(profileId, phoneNumberId, code, null, null);
    }

    public Map<String, Object> verifyPhoneNumber(String profileId, String phoneNumberId, String code,
                                                 String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("code", code);
        return call("POST", profileId, "verify_phone_number", data, profileGroupId, idempotencyKey);
    }

    // --- Business profile ---

    public Map<String, Object> businessProfile(String profileId, String phoneNumberId) {
        return businessProfile(profileId, phoneNumberId, null);
    }

    public Map<String, Object> businessProfile(String profileId, String phoneNumberId, String profileGroupId) {
        return call("GET", profileId, "business_profile", data(phoneNumberId), profileGroupId, null);
    }

    /**
     * {@code fields} takes {@code about}, {@code address}, {@code description}, {@code email},
     * {@code websites} (a list) and {@code vertical}.
     */
    public Map<String, Object> updateBusinessProfile(String profileId, String phoneNumberId,
                                                     Map<String, Object> fields) {
        return updateBusinessProfile(profileId, phoneNumberId, fields, null, null);
    }

    public Map<String, Object> updateBusinessProfile(String profileId, String phoneNumberId,
                                                     Map<String, Object> fields, String profileGroupId,
                                                     String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.putAll(fields);
        return call("PATCH", profileId, "update_business_profile", data, profileGroupId, idempotencyKey);
    }

    /** Sets the profile photo from a public {@code url}. */
    public Map<String, Object> updateBusinessProfilePhoto(String profileId, String phoneNumberId, String url) {
        return updateBusinessProfilePhoto(profileId, phoneNumberId, url, null, null, null, null);
    }

    /**
     * Pass a public {@code url}, or base64 {@code data} with its {@code contentType}
     * (image/jpeg or image/png).
     */
    public Map<String, Object> updateBusinessProfilePhoto(String profileId, String phoneNumberId, String url,
                                                          String data, String contentType, String profileGroupId,
                                                          String idempotencyKey) {
        Map<String, Object> body = data(phoneNumberId);
        body.put("url", url);
        body.put("data", data);
        body.put("content_type", contentType);
        return call("POST", profileId, "update_business_profile_photo", body, profileGroupId, idempotencyKey);
    }

    // --- Display name ---

    public Map<String, Object> displayName(String profileId, String phoneNumberId) {
        return displayName(profileId, phoneNumberId, null);
    }

    public Map<String, Object> displayName(String profileId, String phoneNumberId, String profileGroupId) {
        return call("GET", profileId, "display_name", data(phoneNumberId), profileGroupId, null);
    }

    /**
     * Meta reviews the new name; the outcome arrives as the {@code placement.name_status_updated}
     * webhook.
     */
    public Map<String, Object> requestDisplayNameChange(String profileId, String phoneNumberId, String displayName) {
        return requestDisplayNameChange(profileId, phoneNumberId, displayName, null, null);
    }

    public Map<String, Object> requestDisplayNameChange(String profileId, String phoneNumberId, String displayName,
                                                        String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("display_name", displayName);
        return call("POST", profileId, "request_display_name_change", data, profileGroupId, idempotencyKey);
    }

    // --- Username ---

    public Map<String, Object> username(String profileId, String phoneNumberId) {
        return username(profileId, phoneNumberId, null);
    }

    public Map<String, Object> username(String profileId, String phoneNumberId, String profileGroupId) {
        return call("GET", profileId, "username", data(phoneNumberId), profileGroupId, null);
    }

    public Map<String, Object> setUsername(String profileId, String phoneNumberId, String username) {
        return setUsername(profileId, phoneNumberId, username, null, null, null);
    }

    /** {@code transferAction} is {@code none} or {@code force_transfer}. */
    public Map<String, Object> setUsername(String profileId, String phoneNumberId, String username,
                                           String transferAction, String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("username", username);
        data.put("transfer_action", transferAction);
        return call("PUT", profileId, "set_username", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> deleteUsername(String profileId, String phoneNumberId) {
        return deleteUsername(profileId, phoneNumberId, null, null);
    }

    public Map<String, Object> deleteUsername(String profileId, String phoneNumberId, String profileGroupId,
                                              String idempotencyKey) {
        return call("DELETE", profileId, "delete_username", data(phoneNumberId), profileGroupId, idempotencyKey);
    }

    public Map<String, Object> usernameSuggestions(String profileId, String phoneNumberId) {
        return usernameSuggestions(profileId, phoneNumberId, null);
    }

    public Map<String, Object> usernameSuggestions(String profileId, String phoneNumberId, String profileGroupId) {
        return call("GET", profileId, "username_suggestions", data(phoneNumberId), profileGroupId, null);
    }

    // --- Blocked users ---

    public Map<String, Object> blockedUsers(String profileId, String phoneNumberId) {
        return blockedUsers(profileId, phoneNumberId, null, null, null);
    }

    public Map<String, Object> blockedUsers(String profileId, String phoneNumberId, Integer limit, String after,
                                            String profileGroupId) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("limit", limit);
        data.put("after", after);
        return call("GET", profileId, "blocked_users", data, profileGroupId, null);
    }

    public Map<String, Object> blockedUserStatus(String profileId, String phoneNumberId, String user) {
        return blockedUserStatus(profileId, phoneNumberId, user, null);
    }

    public Map<String, Object> blockedUserStatus(String profileId, String phoneNumberId, String user,
                                                 String profileGroupId) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("user", user);
        return call("GET", profileId, "blocked_user_status", data, profileGroupId, null);
    }

    /** {@code users} are phone numbers or WhatsApp ids. */
    public Map<String, Object> blockUsers(String profileId, String phoneNumberId, List<String> users) {
        return blockUsers(profileId, phoneNumberId, users, null, null);
    }

    public Map<String, Object> blockUsers(String profileId, String phoneNumberId, List<String> users,
                                          String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("users", users);
        return call("POST", profileId, "block_users", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> unblockUsers(String profileId, String phoneNumberId, List<String> users) {
        return unblockUsers(profileId, phoneNumberId, users, null, null);
    }

    public Map<String, Object> unblockUsers(String profileId, String phoneNumberId, List<String> users,
                                            String profileGroupId, String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("users", users);
        return call("POST", profileId, "unblock_users", data, profileGroupId, idempotencyKey);
    }

    // --- Groups ---

    public Map<String, Object> groups(String profileId, String phoneNumberId) {
        return groups(profileId, phoneNumberId, null, null, null);
    }

    public Map<String, Object> groups(String profileId, String phoneNumberId, Integer limit, String after,
                                      String profileGroupId) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("limit", limit);
        data.put("after", after);
        return call("GET", profileId, "groups", data, profileGroupId, null);
    }

    public Map<String, Object> createGroup(String profileId, String phoneNumberId, String subject) {
        return createGroup(profileId, phoneNumberId, subject, null, null, null, null);
    }

    /** {@code joinApprovalMode} is {@code approval_required} or {@code auto_approve}. */
    public Map<String, Object> createGroup(String profileId, String phoneNumberId, String subject,
                                           String description, String joinApprovalMode, String profileGroupId,
                                           String idempotencyKey) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("subject", subject);
        data.put("description", description);
        data.put("join_approval_mode", joinApprovalMode);
        return call("POST", profileId, "create_group", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> group(String profileId, String phoneNumberId, String groupId) {
        return group(profileId, phoneNumberId, groupId, null);
    }

    public Map<String, Object> group(String profileId, String phoneNumberId, String groupId, String profileGroupId) {
        return call("GET", profileId, "group", groupData(phoneNumberId, groupId), profileGroupId, null);
    }

    /** Null {@code subject} / {@code description} / {@code joinApprovalMode} are left unchanged. */
    public Map<String, Object> updateGroup(String profileId, String phoneNumberId, String groupId, String subject,
                                           String description, String joinApprovalMode) {
        return updateGroup(profileId, phoneNumberId, groupId, subject, description, joinApprovalMode, null, null);
    }

    public Map<String, Object> updateGroup(String profileId, String phoneNumberId, String groupId, String subject,
                                           String description, String joinApprovalMode, String profileGroupId,
                                           String idempotencyKey) {
        Map<String, Object> data = groupData(phoneNumberId, groupId);
        data.put("subject", subject);
        data.put("description", description);
        data.put("join_approval_mode", joinApprovalMode);
        return call("PATCH", profileId, "update_group", data, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> deleteGroup(String profileId, String phoneNumberId, String groupId) {
        return deleteGroup(profileId, phoneNumberId, groupId, null, null);
    }

    public Map<String, Object> deleteGroup(String profileId, String phoneNumberId, String groupId,
                                           String profileGroupId, String idempotencyKey) {
        return call("DELETE", profileId, "delete_group", groupData(phoneNumberId, groupId),
                profileGroupId, idempotencyKey);
    }

    public Map<String, Object> addGroupParticipants(String profileId, String phoneNumberId, String groupId,
                                                    List<String> phoneNumbers) {
        return addGroupParticipants(profileId, phoneNumberId, groupId, phoneNumbers, null, null);
    }

    public Map<String, Object> addGroupParticipants(String profileId, String phoneNumberId, String groupId,
                                                    List<String> phoneNumbers, String profileGroupId,
                                                    String idempotencyKey) {
        return groupMembers(profileId, phoneNumberId, groupId, "add_group_participants", phoneNumbers,
                profileGroupId, idempotencyKey);
    }

    public Map<String, Object> removeGroupParticipants(String profileId, String phoneNumberId, String groupId,
                                                       List<String> phoneNumbers) {
        return removeGroupParticipants(profileId, phoneNumberId, groupId, phoneNumbers, null, null);
    }

    public Map<String, Object> removeGroupParticipants(String profileId, String phoneNumberId, String groupId,
                                                       List<String> phoneNumbers, String profileGroupId,
                                                       String idempotencyKey) {
        return groupMembers(profileId, phoneNumberId, groupId, "remove_group_participants", phoneNumbers,
                profileGroupId, idempotencyKey);
    }

    public Map<String, Object> createGroupInviteLink(String profileId, String phoneNumberId, String groupId) {
        return createGroupInviteLink(profileId, phoneNumberId, groupId, null, null);
    }

    public Map<String, Object> createGroupInviteLink(String profileId, String phoneNumberId, String groupId,
                                                     String profileGroupId, String idempotencyKey) {
        return call("POST", profileId, "create_group_invite_link", groupData(phoneNumberId, groupId),
                profileGroupId, idempotencyKey);
    }

    public Map<String, Object> groupJoinRequests(String profileId, String phoneNumberId, String groupId) {
        return groupJoinRequests(profileId, phoneNumberId, groupId, null);
    }

    public Map<String, Object> groupJoinRequests(String profileId, String phoneNumberId, String groupId,
                                                 String profileGroupId) {
        return call("GET", profileId, "group_join_requests", groupData(phoneNumberId, groupId), profileGroupId, null);
    }

    public Map<String, Object> approveGroupJoinRequests(String profileId, String phoneNumberId, String groupId,
                                                        List<String> phoneNumbers) {
        return approveGroupJoinRequests(profileId, phoneNumberId, groupId, phoneNumbers, null, null);
    }

    public Map<String, Object> approveGroupJoinRequests(String profileId, String phoneNumberId, String groupId,
                                                        List<String> phoneNumbers, String profileGroupId,
                                                        String idempotencyKey) {
        return groupMembers(profileId, phoneNumberId, groupId, "approve_group_join_requests", phoneNumbers,
                profileGroupId, idempotencyKey);
    }

    public Map<String, Object> rejectGroupJoinRequests(String profileId, String phoneNumberId, String groupId,
                                                       List<String> phoneNumbers) {
        return rejectGroupJoinRequests(profileId, phoneNumberId, groupId, phoneNumbers, null, null);
    }

    public Map<String, Object> rejectGroupJoinRequests(String profileId, String phoneNumberId, String groupId,
                                                       List<String> phoneNumbers, String profileGroupId,
                                                       String idempotencyKey) {
        return groupMembers(profileId, phoneNumberId, groupId, "reject_group_join_requests", phoneNumbers,
                profileGroupId, idempotencyKey);
    }

    // --- Conversions (account-level) ---

    public Map<String, Object> dataset(String profileId) {
        return dataset(profileId, null);
    }

    public Map<String, Object> dataset(String profileId, String profileGroupId) {
        return call("GET", profileId, "dataset", new LinkedHashMap<>(), profileGroupId, null);
    }

    public Map<String, Object> createDataset(String profileId) {
        return createDataset(profileId, null, null);
    }

    public Map<String, Object> createDataset(String profileId, String profileGroupId, String idempotencyKey) {
        return call("POST", profileId, "create_dataset", new LinkedHashMap<>(), profileGroupId, idempotencyKey);
    }

    /**
     * Reports a conversion (e.g. Purchase, LeadSubmitted) back to Meta for a click-to-WhatsApp ad.
     * {@code event} takes {@code event_name} and {@code event_id} (required), identifies the person
     * by {@code chat_id} or {@code phone}, and optionally {@code dataset_id}, {@code event_time},
     * {@code email}, {@code external_id}, {@code currency}, {@code value}, {@code content_ids} and
     * {@code test_code}.
     */
    public Map<String, Object> sendConversionEvent(String profileId, Map<String, Object> event) {
        return sendConversionEvent(profileId, event, null, null);
    }

    public Map<String, Object> sendConversionEvent(String profileId, Map<String, Object> event,
                                                   String profileGroupId, String idempotencyKey) {
        return call("POST", profileId, "send_conversion_event", new LinkedHashMap<>(event),
                profileGroupId, idempotencyKey);
    }

    // --- Media ---

    /**
     * Downloads an inbound media file (e.g. from a message attachment's {@code externalId}) as raw
     * bytes with its content type.
     */
    public MediaDownload media(String profileId, String phoneNumberId, String mediaId) {
        return media(profileId, phoneNumberId, mediaId, null);
    }

    public MediaDownload media(String profileId, String phoneNumberId, String mediaId, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("phone_number_id", phoneNumberId);
        query.put("media_id", mediaId);
        putProfileGroup(query, profileGroupId);

        return client.getRaw("/api/profiles/" + profileId + "/whatsapp/media", query);
    }

    private static Map<String, Object> data(String phoneNumberId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("phone_number_id", phoneNumberId);
        return data;
    }

    private static Map<String, Object> groupData(String phoneNumberId, String groupId) {
        Map<String, Object> data = data(phoneNumberId);
        data.put("group_id", groupId);
        return data;
    }

    private Map<String, Object> groupMembers(String profileId, String phoneNumberId, String groupId, String action,
                                             List<String> phoneNumbers, String profileGroupId,
                                             String idempotencyKey) {
        Map<String, Object> data = groupData(phoneNumberId, groupId);
        data.put("phone_numbers", phoneNumbers);
        return call("POST", profileId, action, data, profileGroupId, idempotencyKey);
    }

    // GET and DELETE send the data as query params, the rest as a JSON body.
    private Map<String, Object> call(String method, String profileId, String action, Map<String, Object> data,
                                     String profileGroupId, String idempotencyKey) {
        String path = "/api/profiles/" + profileId + "/whatsapp/" + action;
        Map<String, Object> compact = new LinkedHashMap<>();
        data.forEach((key, value) -> {
            if (value != null) compact.put(key, value);
        });

        Map<String, String> query = new LinkedHashMap<>();
        if ("GET".equals(method) || "DELETE".equals(method)) {
            compact.forEach((key, value) -> query.put(key, String.valueOf(value)));
        }
        putProfileGroup(query, profileGroupId);

        TypeReference<Map<String, Object>> type = new TypeReference<>() {};
        return switch (method) {
            case "GET" -> client.get(path, query, type);
            case "DELETE" -> client.delete(path, query, type, idempotencyKey);
            case "POST" -> client.post(path, query, compact, type, idempotencyKey);
            case "PUT" -> client.put(path, query, compact, type, idempotencyKey);
            default -> client.patch(path, query, compact, type, idempotencyKey);
        };
    }

    private void putProfileGroup(Map<String, String> query, String profileGroupId) {
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
    }
}
