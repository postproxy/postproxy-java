package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Searches that showed a Google Business location, month by month. */
public record SearchKeywordsResponse(
        @JsonProperty("months") List<SearchKeywordsMonth> months
) {}
