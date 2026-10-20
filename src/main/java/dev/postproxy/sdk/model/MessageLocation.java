package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A WhatsApp location pin. {@code name} and {@code address} are optional. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageLocation(
        @JsonProperty("latitude") double latitude,
        @JsonProperty("longitude") double longitude,
        @JsonProperty("name") String name,
        @JsonProperty("address") String address
) {
    public MessageLocation(double latitude, double longitude) {
        this(latitude, longitude, null, null);
    }
}
