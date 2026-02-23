package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.LinkedInFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LinkedInParams(
        @JsonProperty("format") LinkedInFormat format,
        @JsonProperty("organization_id") String organizationId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LinkedInFormat format;
        private String organizationId;

        public Builder format(LinkedInFormat format) { this.format = format; return this; }
        public Builder organizationId(String organizationId) { this.organizationId = organizationId; return this; }

        public LinkedInParams build() {
            return new LinkedInParams(format, organizationId);
        }
    }
}
