package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record Message(
        @JsonProperty("id") String id,
        @JsonProperty("chat_id") String chatId,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("direction") MessageDirection direction,
        @JsonProperty("body") String body,
        @JsonProperty("status") MessageStatus status,
        @JsonProperty("tag") String tag,
        @JsonProperty("external_comment_id") String externalCommentId,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("error_details") ErrorDetails errorDetails,
        @JsonProperty("platform_data") Map<String, Object> platformData,
        @JsonProperty("external_posted_at") String externalPostedAt,
        @JsonProperty("external_delivered_at") String externalDeliveredAt,
        @JsonProperty("external_read_at") String externalReadAt,
        @JsonProperty("external_edited_at") String externalEditedAt,
        @JsonProperty("reply_to_external_id") String replyToExternalId,
        @JsonProperty("reply_markup") Map<String, Object> replyMarkup,
        @JsonProperty("quick_replies") List<QuickReply> quickReplies,
        @JsonProperty("buttons") List<MessageButton> buttons,
        @JsonProperty("card") MessageCard card,
        @JsonProperty("elements") List<MessageElement> elements,
        @JsonProperty("tapped_action") TappedAction tappedAction,
        @JsonProperty("story_reply") Map<String, Object> storyReply,
        /** WhatsApp only. */
        @JsonProperty("interactive") Map<String, Object> interactive,
        /** WhatsApp only. */
        @JsonProperty("location") MessageLocation location,
        /** WhatsApp only. */
        @JsonProperty("contacts") List<Map<String, Object>> contacts,
        /** WhatsApp only: the template this message was sent from. */
        @JsonProperty("template") MessageTemplateRef template,
        @JsonProperty("external_deleted_at") String externalDeletedAt,
        @JsonProperty("reactions") List<Reaction> reactions,
        @JsonProperty("attachments") List<Attachment> attachments,
        @JsonProperty("is_unsupported") boolean isUnsupported,
        @JsonProperty("created_at") String createdAt
) {}
