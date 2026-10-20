package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A parameter for one button of a WhatsApp template. {@code subType} is {@code url},
 * {@code copy_code}, {@code flow}, {@code quick_reply} or {@code voice_call}; the remaining
 * keys (e.g. {@code text}, {@code payload}) are sent as given in {@code params}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TemplateButtonParam(
        @JsonProperty("index") int index,
        @JsonProperty("sub_type") String subType,
        @JsonAnyGetter Map<String, Object> params
) {
    public static TemplateButtonParam of(int index, String subType, Map<String, Object> params) {
        return new TemplateButtonParam(index, subType, params);
    }
}
