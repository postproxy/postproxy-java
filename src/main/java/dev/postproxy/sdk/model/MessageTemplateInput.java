package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A WhatsApp template send. Address the template by {@code id} (PostProxy or Meta id) or by
 * {@code name} (plus {@code language} when it exists in several).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageTemplateInput(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("language") String language,
        /** Positional values: header, then body, then dynamic URL buttons. */
        @JsonProperty("variables") List<String> variables,
        @JsonProperty("button_params") List<TemplateButtonParam> buttonParams,
        @JsonProperty("header_media") TemplateHeaderMedia headerMedia,
        @JsonProperty("header_location") MessageLocation headerLocation
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String language;
        private List<String> variables;
        private List<TemplateButtonParam> buttonParams;
        private TemplateHeaderMedia headerMedia;
        private MessageLocation headerLocation;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder language(String language) { this.language = language; return this; }
        public Builder variables(List<String> variables) { this.variables = variables; return this; }
        public Builder buttonParams(List<TemplateButtonParam> buttonParams) { this.buttonParams = buttonParams; return this; }
        public Builder headerMedia(TemplateHeaderMedia headerMedia) { this.headerMedia = headerMedia; return this; }
        public Builder headerLocation(MessageLocation headerLocation) { this.headerLocation = headerLocation; return this; }

        public MessageTemplateInput build() {
            return new MessageTemplateInput(id, name, language, variables, buttonParams, headerMedia, headerLocation);
        }
    }
}
