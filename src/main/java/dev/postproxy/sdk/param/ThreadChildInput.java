package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ThreadChildInput(
        @JsonProperty("body") String body,
        @JsonProperty("media") List<String> media,
        @JsonIgnore List<Path> mediaFiles
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private List<String> media;
        private List<Path> mediaFiles;

        public Builder body(String body) { this.body = body; return this; }
        public Builder media(List<String> media) { this.media = media; return this; }
        public Builder mediaFiles(List<Path> mediaFiles) { this.mediaFiles = mediaFiles; return this; }

        public ThreadChildInput build() {
            if (body == null) throw new IllegalArgumentException("body is required");
            return new ThreadChildInput(body, media, mediaFiles);
        }
    }
}
