package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NextSlotResponse(
        @JsonProperty("next_slot") String nextSlot
) {}
