package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * A WhatsApp message template to create. Pass Meta's {@code components} (HEADER, BODY, FOOTER,
 * BUTTONS), or create from Meta's template library with {@code libraryTemplateName}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WhatsAppTemplateInput(
        @JsonProperty("name") String name,
        // MARKETING, UTILITY or AUTHENTICATION.
        @JsonProperty("category") String category,
        @JsonProperty("language") String language,
        // POSITIONAL or NAMED.
        @JsonProperty("parameter_format") String parameterFormat,
        @JsonProperty("components") List<Map<String, Object>> components,
        @JsonProperty("library_template_name") String libraryTemplateName,
        @JsonProperty("library_template_body_inputs") Map<String, Object> libraryTemplateBodyInputs,
        @JsonProperty("library_template_button_inputs") List<Map<String, Object>> libraryTemplateButtonInputs,
        @JsonProperty("message_send_ttl_seconds") Integer messageSendTtlSeconds
) {
    public static Builder builder(String name, String category, String language) {
        return new Builder(name, category, language);
    }

    public static class Builder {
        private final String name;
        private final String category;
        private final String language;
        private String parameterFormat;
        private List<Map<String, Object>> components;
        private String libraryTemplateName;
        private Map<String, Object> libraryTemplateBodyInputs;
        private List<Map<String, Object>> libraryTemplateButtonInputs;
        private Integer messageSendTtlSeconds;

        private Builder(String name, String category, String language) {
            this.name = name;
            this.category = category;
            this.language = language;
        }

        public Builder parameterFormat(String parameterFormat) { this.parameterFormat = parameterFormat; return this; }
        public Builder components(List<Map<String, Object>> components) { this.components = components; return this; }
        public Builder libraryTemplateName(String libraryTemplateName) { this.libraryTemplateName = libraryTemplateName; return this; }
        public Builder libraryTemplateBodyInputs(Map<String, Object> libraryTemplateBodyInputs) { this.libraryTemplateBodyInputs = libraryTemplateBodyInputs; return this; }
        public Builder libraryTemplateButtonInputs(List<Map<String, Object>> libraryTemplateButtonInputs) { this.libraryTemplateButtonInputs = libraryTemplateButtonInputs; return this; }
        public Builder messageSendTtlSeconds(Integer messageSendTtlSeconds) { this.messageSendTtlSeconds = messageSendTtlSeconds; return this; }

        public WhatsAppTemplateInput build() {
            return new WhatsAppTemplateInput(name, category, language, parameterFormat, components,
                    libraryTemplateName, libraryTemplateBodyInputs, libraryTemplateButtonInputs, messageSendTtlSeconds);
        }
    }
}
