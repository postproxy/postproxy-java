package dev.postproxy.sdk.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.postproxy.sdk.model.Message;

public record MessageEventData(
        @JsonProperty("message") Message message
) {}
