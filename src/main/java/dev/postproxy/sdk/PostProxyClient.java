package dev.postproxy.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.postproxy.sdk.exception.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PostProxyClient {

    private final String apiKey;
    private final String baseUrl;
    private final String defaultProfileGroupId;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    PostProxyClient(String apiKey, String baseUrl, String defaultProfileGroupId) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.defaultProfileGroupId = defaultProfileGroupId;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getDefaultProfileGroupId() {
        return defaultProfileGroupId;
    }

    public <T> T get(String path, Map<String, String> queryParams, TypeReference<T> type) {
        return request("GET", path, queryParams, null, type);
    }

    public <T> T post(String path, Map<String, String> queryParams, Object body, TypeReference<T> type) {
        return request("POST", path, queryParams, body, type);
    }

    public <T> T patch(String path, Map<String, String> queryParams, Object body, TypeReference<T> type) {
        return request("PATCH", path, queryParams, body, type);
    }

    public <T> T delete(String path, Map<String, String> queryParams, TypeReference<T> type) {
        return request("DELETE", path, queryParams, null, type);
    }

    public <T> T postMultipart(String path, Map<String, String> queryParams,
                                Map<String, Object> fields, Map<String, List<Path>> fileGroups, TypeReference<T> type) {
        try {
            String boundary = "----PostProxy" + UUID.randomUUID().toString().replace("-", "");
            byte[] body = buildMultipartBody(boundary, fields, fileGroups);

            URI uri = buildUri(path, queryParams);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, type);
        } catch (PostProxyException e) {
            throw e;
        } catch (Exception e) {
            throw new PostProxyException("Request failed: " + e.getMessage(), 0, null);
        }
    }

    private <T> T request(String method, String path, Map<String, String> queryParams,
                           Object body, TypeReference<T> type) {
        try {
            URI uri = buildUri(path, queryParams);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json");

            if (body != null) {
                String json = objectMapper.writeValueAsString(body);
                builder.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(json));
            } else if ("POST".equals(method)) {
                builder.header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{}"));
            } else if ("DELETE".equals(method)) {
                builder.DELETE();
            } else {
                builder.GET();
            }

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, type);
        } catch (PostProxyException e) {
            throw e;
        } catch (Exception e) {
            throw new PostProxyException("Request failed: " + e.getMessage(), 0, null);
        }
    }

    private URI buildUri(String path, Map<String, String> queryParams) {
        StringBuilder sb = new StringBuilder(baseUrl).append(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            sb.append("?");
            StringJoiner joiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            sb.append(joiner);
        }
        return URI.create(sb.toString());
    }

    @SuppressWarnings("unchecked")
    private <T> T handleResponse(HttpResponse<String> response, TypeReference<T> type) {
        int status = response.statusCode();
        String responseBody = response.body();

        if (status == 204) {
            return null;
        }

        Map<String, Object> parsed = null;
        if (responseBody != null && !responseBody.isBlank()) {
            try {
                parsed = objectMapper.readValue(responseBody, new TypeReference<>() {});
            } catch (Exception ignored) {
            }
        }

        if (status >= 200 && status < 300) {
            if (responseBody == null || responseBody.isBlank()) {
                return null;
            }
            try {
                return objectMapper.readValue(responseBody, type);
            } catch (Exception e) {
                throw new PostProxyException("Failed to parse response: " + e.getMessage(), status, parsed);
            }
        }

        String message = "HTTP " + status;
        if (parsed != null) {
            if (parsed.containsKey("error")) {
                message = String.valueOf(parsed.get("error"));
            } else if (parsed.containsKey("message")) {
                message = String.valueOf(parsed.get("message"));
            } else {
                message = "HTTP " + status + ": " + responseBody;
            }
        } else if (responseBody != null && !responseBody.isBlank()) {
            message = "HTTP " + status + ": " + responseBody;
        }

        throw switch (status) {
            case 400 -> new BadRequestException(message, parsed);
            case 401 -> new AuthenticationException(message, parsed);
            case 404 -> new NotFoundException(message, parsed);
            case 422 -> new ValidationException(message, parsed);
            default -> new PostProxyException(message, status, parsed);
        };
    }

    private byte[] buildMultipartBody(String boundary, Map<String, Object> fields, Map<String, List<Path>> fileGroups)
            throws IOException {
        var parts = new ArrayList<byte[]>();
        String crlf = "\r\n";

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?> list) {
                for (Object item : list) {
                    parts.add(("--" + boundary + crlf).getBytes(StandardCharsets.UTF_8));
                    parts.add(("Content-Disposition: form-data; name=\"" + name + "\"" + crlf + crlf).getBytes(StandardCharsets.UTF_8));
                    parts.add((String.valueOf(item) + crlf).getBytes(StandardCharsets.UTF_8));
                }
            } else if (value != null) {
                parts.add(("--" + boundary + crlf).getBytes(StandardCharsets.UTF_8));
                parts.add(("Content-Disposition: form-data; name=\"" + name + "\"" + crlf + crlf).getBytes(StandardCharsets.UTF_8));
                parts.add((String.valueOf(value) + crlf).getBytes(StandardCharsets.UTF_8));
            }
        }

        if (fileGroups != null) {
            for (Map.Entry<String, List<Path>> group : fileGroups.entrySet()) {
                String fieldName = group.getKey();
                for (Path file : group.getValue()) {
                    String filename = file.getFileName().toString();
                    String contentType = guessMimeType(filename);
                    byte[] fileBytes = Files.readAllBytes(file);

                    parts.add(("--" + boundary + crlf).getBytes(StandardCharsets.UTF_8));
                    parts.add(("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"" + crlf).getBytes(StandardCharsets.UTF_8));
                    parts.add(("Content-Type: " + contentType + crlf + crlf).getBytes(StandardCharsets.UTF_8));
                    parts.add(fileBytes);
                    parts.add(crlf.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        parts.add(("--" + boundary + "--" + crlf).getBytes(StandardCharsets.UTF_8));

        int totalSize = parts.stream().mapToInt(b -> b.length).sum();
        byte[] result = new byte[totalSize];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }

    private static String guessMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".mov")) return "video/quicktime";
        if (lower.endsWith(".avi")) return "video/x-msvideo";
        if (lower.endsWith(".webm")) return "video/webm";
        return "application/octet-stream";
    }
}
