package dev.postproxy.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Instagram only: whether a DM participant is verified and follows you. Cached briefly server-side. */
public record ParticipantStatus(
        @JsonProperty("is_verified_user") Boolean isVerifiedUser,
        @JsonProperty("is_user_follow_business") Boolean isUserFollowBusiness,
        @JsonProperty("is_business_follow_user") Boolean isBusinessFollowUser,
        @JsonProperty("fetched_at") String fetchedAt
) {}
