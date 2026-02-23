package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.TikTokFormat;
import dev.postproxy.sdk.model.TikTokPrivacy;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TikTokParams(
        @JsonProperty("format") TikTokFormat format,
        @JsonProperty("privacy_status") TikTokPrivacy privacyStatus,
        @JsonProperty("photo_cover_index") Integer photoCoverIndex,
        @JsonProperty("auto_add_music") Boolean autoAddMusic,
        @JsonProperty("made_with_ai") Boolean madeWithAi,
        @JsonProperty("disable_comment") Boolean disableComment,
        @JsonProperty("disable_duet") Boolean disableDuet,
        @JsonProperty("disable_stitch") Boolean disableStitch,
        @JsonProperty("brand_content_toggle") Boolean brandContentToggle,
        @JsonProperty("brand_organic_toggle") Boolean brandOrganicToggle
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TikTokFormat format;
        private TikTokPrivacy privacyStatus;
        private Integer photoCoverIndex;
        private Boolean autoAddMusic;
        private Boolean madeWithAi;
        private Boolean disableComment;
        private Boolean disableDuet;
        private Boolean disableStitch;
        private Boolean brandContentToggle;
        private Boolean brandOrganicToggle;

        public Builder format(TikTokFormat format) { this.format = format; return this; }
        public Builder privacyStatus(TikTokPrivacy privacyStatus) { this.privacyStatus = privacyStatus; return this; }
        public Builder photoCoverIndex(Integer photoCoverIndex) { this.photoCoverIndex = photoCoverIndex; return this; }
        public Builder autoAddMusic(Boolean autoAddMusic) { this.autoAddMusic = autoAddMusic; return this; }
        public Builder madeWithAi(Boolean madeWithAi) { this.madeWithAi = madeWithAi; return this; }
        public Builder disableComment(Boolean disableComment) { this.disableComment = disableComment; return this; }
        public Builder disableDuet(Boolean disableDuet) { this.disableDuet = disableDuet; return this; }
        public Builder disableStitch(Boolean disableStitch) { this.disableStitch = disableStitch; return this; }
        public Builder brandContentToggle(Boolean brandContentToggle) { this.brandContentToggle = brandContentToggle; return this; }
        public Builder brandOrganicToggle(Boolean brandOrganicToggle) { this.brandOrganicToggle = brandOrganicToggle; return this; }

        public TikTokParams build() {
            return new TikTokParams(format, privacyStatus, photoCoverIndex, autoAddMusic, madeWithAi,
                    disableComment, disableDuet, disableStitch, brandContentToggle, brandOrganicToggle);
        }
    }
}
