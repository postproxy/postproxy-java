package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.LinkedInFormat;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LinkedInParams(
        @JsonProperty("format") LinkedInFormat format,
        @JsonProperty("organization_id") String organizationId,
        // URN of a post to reshare, e.g. "urn:li:share:123".
        @JsonProperty("reshare_urn") String reshareUrn,
        // A String, or a List<String> with one entry per media item.
        @JsonProperty("alt_text") Object altText,
        // Organization pages only; up to 25 ISO 3166-1 alpha-2 codes.
        @JsonProperty("countries") List<String> countries,
        // Required when format is POLL: 140 characters or fewer.
        @JsonProperty("poll_question") String pollQuestion,
        // Required when format is POLL: 2-4 options, max 30 characters each.
        @JsonProperty("poll_options") List<String> pollOptions,
        // 1, 3, 7 (default) or 14.
        @JsonProperty("poll_duration_days") Integer pollDurationDays,
        // Title shown above an attached document. Defaults to the file name.
        @JsonProperty("document_title") String documentTitle,
        // Posted right after publishing, as the same author (1,250 characters or fewer).
        // Not on polls; profiles connected before 2026-10-28 must reconnect.
        @JsonProperty("first_comment") String firstComment
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LinkedInFormat format;
        private String organizationId;
        private String reshareUrn;
        private Object altText;
        private List<String> countries;
        private String pollQuestion;
        private List<String> pollOptions;
        private Integer pollDurationDays;
        private String documentTitle;
        private String firstComment;

        public Builder format(LinkedInFormat format) { this.format = format; return this; }
        public Builder organizationId(String organizationId) { this.organizationId = organizationId; return this; }
        public Builder reshareUrn(String reshareUrn) { this.reshareUrn = reshareUrn; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder altText(List<String> altText) { this.altText = altText; return this; }
        public Builder countries(List<String> countries) { this.countries = countries; return this; }
        public Builder pollQuestion(String pollQuestion) { this.pollQuestion = pollQuestion; return this; }
        public Builder pollOptions(List<String> pollOptions) { this.pollOptions = pollOptions; return this; }
        public Builder pollDurationDays(Integer pollDurationDays) { this.pollDurationDays = pollDurationDays; return this; }
        public Builder documentTitle(String documentTitle) { this.documentTitle = documentTitle; return this; }
        public Builder firstComment(String firstComment) { this.firstComment = firstComment; return this; }

        public LinkedInParams build() {
            return new LinkedInParams(format, organizationId, reshareUrn, altText, countries,
                    pollQuestion, pollOptions, pollDurationDays, documentTitle, firstComment);
        }
    }
}
