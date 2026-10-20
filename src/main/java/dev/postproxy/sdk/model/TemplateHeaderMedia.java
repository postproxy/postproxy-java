package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The image / video / document for a WhatsApp template header: a public {@code link} or an uploaded media {@code id}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TemplateHeaderMedia(
        @JsonProperty("link") String link,
        @JsonProperty("id") String id
) {
    public static TemplateHeaderMedia link(String link) {
        return new TemplateHeaderMedia(link, null);
    }

    public static TemplateHeaderMedia id(String id) {
        return new TemplateHeaderMedia(null, id);
    }
}
