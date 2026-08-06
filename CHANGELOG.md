# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.12.0] - 2026-08-06

### Added

- **Post syncs & backfill.** `profiles().backfillPosts(id, from)` walks a profile's feed backwards from the newest post and imports the history behind it; `profiles().postSyncs(id, ...)` and `profiles().postSync(id, postSyncId)` expose every post pull — the one fired on connect, the recurring poll, and backfills — as the new `PostSync` record, with the `PostSyncTrigger` and `PostSyncStatus` enums.
- **`comments().listAll(postIds, profiles, from, to, page, perPage, profileGroupId)`** — comments across every post in the profile group in one request. Flat: replies are their own entries linked by `parentExternalId()`, typed as the new `BulkComment` record (adds `postId()`, `profileId()`, `platform()`).
- `from` and `to` arguments on `comments().list(...)`, filtering on when PostProxy received the comment.
- **Idempotency.** Every write sends an `Idempotency-Key` header when given one: params records gain `idempotencyKey(...)` on their builders, and the remaining write methods take it as a trailing argument (added as overloads, so existing calls still compile).
- `ConflictException` (409), thrown for a duplicate submission (`getResponse().get("duplicate_post_id")`), a backfill already running (`getResponse().get("profile_sync_id")`), or an in-flight idempotency key. Previously these surfaced as a bare `PostProxyException`.
- **Instagram user tags.** `InstagramParams.userTags` with the new `InstagramUserTag` record (`username`, `x`, `y`, `mediaIndex`) and `of(...)` factories — tag accounts on feed posts, reels, and stories.
- `StatsRecord.rawStats` — every metric under its original platform name, alongside the normalized `stats`.
- `examples/BackfillPosts.java`, and cross-post comment listing in `examples/ManageComments.java`.

### Changed

- LinkedIn post stats now normalize `likes`, `comments`, `shares`, and `clicks` alongside `impressions` (server-side; `stats` was already an open map).
- `HUMAN_AGENT` is now approved on **both** Facebook and Instagram and extends the reply window to 7 days. `SendMessageParams.tag` is unchanged — see the README for Meta's policy limits.

## [1.11.0] - 2026-07-14

### Added

- `profiles().iceBreakers(id)`, `profiles().setIceBreakers(id, iceBreakers)`, and `profiles().deleteIceBreakers(id)` for managing Instagram DM ice breakers, with `IceBreaker` and `IceBreakersResponse` models.
- `profiles().assignPlacementToGroup(id, placementId, targetProfileGroupId)` to move a placement (Facebook Page, Telegram channel, GBP location) to another profile group; returns the new `AssignedPlacement` model.
- `Placement.metadata` field.
- Twitter polls: `TwitterFormat.POLL`, and `TwitterParams` gains `pollOptions` (2-4 choices, max 25 chars each) and `pollDurationMinutes` (5-10080).

### Fixed

- `gradle.properties` version aligned with the SDK version (was stuck at 1.7.0).

## [1.10.0] - 2026-06-03

### Added

- **Direct Messages API.** New `Chat` and `Message` models plus `Attachment` and `Reaction`.
  - `postproxy.chats()`: `list`, `create`, `get`, `archive`, `unarchive`.
  - `postproxy.messages()`: `list`, `send` (text, media URLs, or local media files via multipart), `get`, `edit`, `react`, `unreact`.
  - Param classes: `ListChatsParams`, `CreateChatParams`, `ListMessagesParams`, `SendMessageParams`, `EditMessageParams`, `ReactParams`.
  - `MessageDirection` and `MessageStatus` enums.
- `CommentsResource.privateReply(...)` — send a DM in reply to a comment; returns a `Message`.
- `Comment.attachments` (`List<Attachment>`) and `Comment.metadata` (author signals).
- New webhook event types: `profile_comment.created`, the eight `message.*` events, and `reaction.received`.
- New typed webhook payloads with decoders on `WebhookEvents`: `asMessageEvent` (`MessageEventData`), `asReactionEvent` (`ReactionEventData`), and `asProfileCommentCreated` (`ProfileCommentCreatedData`).

### Changed

- Version reconciled to `1.10.0` to match the other PostProxy SDKs.

## [1.8.0] - 2026-05-15

### Added

- `Platform.GOOGLE_BUSINESS` (serialized as `google_business`) for posts and profiles.
- `ProfileCommentsResource`: `list`, `get`, `create`, `delete` for review replies via `/api/profiles/:profile_id/comments`. Accessed via `postproxy.profileComments()`.
- `MediaPlatformError` model and `Media.platforms` field for per-media platform error reporting.
