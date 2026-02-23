package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ListResponse<T>(
        @JsonProperty("data") List<T> data
) {}
