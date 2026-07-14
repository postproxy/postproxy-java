# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

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
