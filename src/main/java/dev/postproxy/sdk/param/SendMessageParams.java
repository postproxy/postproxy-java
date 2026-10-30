package dev.postproxy.sdk.param;

import dev.postproxy.sdk.model.MessageButton;
import dev.postproxy.sdk.model.MessageCard;
import dev.postproxy.sdk.model.MessageElement;
import dev.postproxy.sdk.model.MessageLocation;
import dev.postproxy.sdk.model.MessageTemplateInput;
import dev.postproxy.sdk.model.QuickReply;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Parameters for sending a direct message.
 *
 * <p>{@code quickReplies}, {@code buttons}, {@code card} and {@code elements} are Facebook and
 * Instagram only — they return 422 on Telegram and Bluesky, where {@code replyMarkup} is the
 * equivalent. They are sent on the JSON path only, so pass {@code media} as hosted URLs rather than
 * {@code mediaFiles} when combining with an attachment.
 *
 * <p>WhatsApp: send exactly one of {@code body}, {@code media}, {@code template},
 * {@code interactive}, {@code location} or {@code contacts}. Outside the 24-hour window only a
 * {@code template} gets through.
 */
public record SendMessageParams(
        String body,
        List<String> media,
        List<Path> mediaFiles,
        String tag,
        // Telegram, WhatsApp and Instagram: the external_id of the message to quote.
        String replyToExternalId,
        Map<String, Object> replyMarkup,
        List<QuickReply> quickReplies,
        List<MessageButton> buttons,
        /** Requires {@code buttons}. */
        MessageCard card,
        /** Carousel of up to 10 cards. Can't be combined with {@code body}, {@code buttons} or {@code card}. */
        List<MessageElement> elements,
        MessageTemplateInput template,
        Map<String, Object> interactive,
        MessageLocation location,
        List<Map<String, Object>> contacts,
        /** {@code "utility"} — send a text message under the utility category. */
        String category,
        Boolean linkPreview,
        /** Deliver an audio attachment as a voice note. */
        Boolean voiceNote,
        /** File name shown for a document attachment. */
        String filename,
        String profileGroupId,
        /** Optional Idempotency-Key: retrying with the same key replays the original response. */
        @com.fasterxml.jackson.annotation.JsonIgnore String idempotencyKey
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private List<String> media;
        private List<Path> mediaFiles;
        private String tag;
        private String replyToExternalId;
        private Map<String, Object> replyMarkup;
        private List<QuickReply> quickReplies;
        private List<MessageButton> buttons;
        private MessageCard card;
        private List<MessageElement> elements;
        private MessageTemplateInput template;
        private Map<String, Object> interactive;
        private MessageLocation location;
        private List<Map<String, Object>> contacts;
        private String category;
        private Boolean linkPreview;
        private Boolean voiceNote;
        private String filename;
        private String profileGroupId;
        private String idempotencyKey;

        public Builder body(String body) { this.body = body; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder tag(String tag) { this.tag = tag; return this; }
        public Builder replyToExternalId(String replyToExternalId) { this.replyToExternalId = replyToExternalId; return this; }
        public Builder replyMarkup(Map<String, Object> replyMarkup) { this.replyMarkup = replyMarkup; return this; }
        public Builder quickReplies(List<QuickReply> quickReplies) { this.quickReplies = quickReplies; return this; }
        public Builder buttons(List<MessageButton> buttons) { this.buttons = buttons; return this; }
        public Builder card(MessageCard card) { this.card = card; return this; }
        public Builder elements(List<MessageElement> elements) { this.elements = elements; return this; }
        public Builder template(MessageTemplateInput template) { this.template = template; return this; }
        public Builder interactive(Map<String, Object> interactive) { this.interactive = interactive; return this; }
        public Builder location(MessageLocation location) { this.location = location; return this; }
        public Builder contacts(List<Map<String, Object>> contacts) { this.contacts = contacts; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder linkPreview(Boolean linkPreview) { this.linkPreview = linkPreview; return this; }
        public Builder voiceNote(Boolean voiceNote) { this.voiceNote = voiceNote; return this; }
        public Builder filename(String filename) { this.filename = filename; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }

        public SendMessageParams build() {
            return new SendMessageParams(body, media, mediaFiles, tag, replyToExternalId, replyMarkup,
                    quickReplies, buttons, card, elements, template, interactive, location, contacts,
                    category, linkPreview, voiceNote, filename, profileGroupId, idempotencyKey);
        }
    }
}
