package dev.postproxy.sdk.param;

public record ListChatsParams(
        Integer page,
        Integer perPage,
        String before,
        String after,
        String profileGroupId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page;
        private Integer perPage;
        private String before;
        private String after;
        private String profileGroupId;

        public Builder page(Integer page) { this.page = page; return this; }
        public Builder perPage(Integer perPage) { this.perPage = perPage; return this; }
        public Builder before(String before) { this.before = before; return this; }
        public Builder after(String after) { this.after = after; return this; }
        public Builder profileGroupId(String profileGroupId) { this.profileGroupId = profileGroupId; return this; }

        public ListChatsParams build() {
            return new ListChatsParams(page, perPage, before, after, profileGroupId);
        }
    }
}
