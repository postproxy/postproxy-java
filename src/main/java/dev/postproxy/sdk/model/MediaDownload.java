package dev.postproxy.sdk.model;

/** Raw bytes downloaded from the API, with the response's {@code Content-Type} (or null). */
public record MediaDownload(
        byte[] data,
        String contentType
) {}
