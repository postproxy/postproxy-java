package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlatformParams(
        @JsonProperty("facebook") FacebookParams facebook,
        @JsonProperty("instagram") InstagramParams instagram,
        @JsonProperty("tiktok") TikTokParams tiktok,
        @JsonProperty("linkedin") LinkedInParams linkedin,
        @JsonProperty("youtube") YouTubeParams youtube,
        @JsonProperty("pinterest") PinterestParams pinterest,
        @JsonProperty("threads") ThreadsParams threads,
        @JsonProperty("twitter") TwitterParams twitter
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FacebookParams facebook;
        private InstagramParams instagram;
        private TikTokParams tiktok;
        private LinkedInParams linkedin;
        private YouTubeParams youtube;
        private PinterestParams pinterest;
        private ThreadsParams threads;
        private TwitterParams twitter;

        public Builder facebook(FacebookParams facebook) { this.facebook = facebook; return this; }
        public Builder instagram(InstagramParams instagram) { this.instagram = instagram; return this; }
        public Builder tiktok(TikTokParams tiktok) { this.tiktok = tiktok; return this; }
        public Builder linkedin(LinkedInParams linkedin) { this.linkedin = linkedin; return this; }
        public Builder youtube(YouTubeParams youtube) { this.youtube = youtube; return this; }
        public Builder pinterest(PinterestParams pinterest) { this.pinterest = pinterest; return this; }
        public Builder threads(ThreadsParams threads) { this.threads = threads; return this; }
        public Builder twitter(TwitterParams twitter) { this.twitter = twitter; return this; }

        public PlatformParams build() {
            return new PlatformParams(facebook, instagram, tiktok, linkedin, youtube, pinterest, threads, twitter);
        }
    }
}
