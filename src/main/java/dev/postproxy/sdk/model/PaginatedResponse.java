package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PaginatedResponse<T>(
        @JsonProperty("data") List<T> data,
        @JsonProperty("total") int total,
        @JsonProperty("page") int page,
        @JsonProperty("per_page") int perPage
) {}
