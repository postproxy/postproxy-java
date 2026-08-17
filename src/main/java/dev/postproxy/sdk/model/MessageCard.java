package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Extra fields for the generic-template element that carries {@code buttons}, for a richer
 * product-style card.
 *
 * <p>Requires {@code buttons}. {@code subtitle} is capped at 80 characters, and both {@code
 * imageUrl} and the default action's URL must be https.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageCard(
        @JsonProperty("subtitle") String subtitle,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("default_action") CardDefaultAction defaultAction
) {}
