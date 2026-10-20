package dev.postproxy.sdk.param;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/** A media item given as {@code {"url": ...}}; {@code media} lists also take plain URL strings. */
public record MediaInput(
        @JsonProperty("url") String url
) {
    public static MediaInput of(String url) {
        return new MediaInput(url);
    }

    /** The URL of a media list item (a String or a MediaInput). */
    public static String urlOf(Object item) {
        return item instanceof MediaInput input ? input.url() : (String) item;
    }

    static List<Object> normalize(List<?> media) {
        if (media == null) return null;
        List<Object> items = new ArrayList<>(media.size());
        for (Object item : media) {
            if (!(item instanceof String) && !(item instanceof MediaInput)) {
                throw new IllegalArgumentException("media items must be URL strings or MediaInput");
            }
            items.add(item);
        }
        return items;
    }
}
