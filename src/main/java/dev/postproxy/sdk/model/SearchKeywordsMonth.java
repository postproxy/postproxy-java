package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SearchKeywordsMonth(
        // YYYY-MM
        @JsonProperty("month") String month,
        @JsonProperty("keywords") List<SearchKeyword> keywords
) {}
