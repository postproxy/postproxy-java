package dev.postproxy.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.postproxy.sdk.exception.*;

import java.nio.file.Path;
import java.util.*;

public class MockPostProxyClient extends PostProxyClient {

    private final Object responseBody;
    private final int responseStatus;
    private final ObjectMapper mapper;
    private final List<RecordedRequest> requests = new ArrayList<>();

    public record RecordedRequest(String method, String url, Object body) {}

    public MockPostProxyClient(Object responseBody, int responseStatus, String profileGroupId) {
        super("test-api-key", "https://mock.postproxy.dev", profileGroupId);
        this.responseBody = responseBody;
        this.responseStatus = responseStatus;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<RecordedRequest> getRequests() {
        return requests;
    }

    @Override
    public <T> T get(String path, Map<String, String> queryParams, TypeReference<T> type) {
        return handle("GET", path, queryParams, null, type);
    }

    @Override
    public <T> T post(String path, Map<String, String> queryParams, Object body, TypeReference<T> type) {
        return handle("POST", path, queryParams, body, type);
    }

    @Override
    public <T> T delete(String path, Map<String, String> queryParams, TypeReference<T> type) {
        return handle("DELETE", path, queryParams, null, type);
    }

    @Override
    public <T> T postMultipart(String path, Map<String, String> queryParams,
                                Map<String, Object> fields, List<Path> files, TypeReference<T> type) {
        return handle("POST", path, queryParams, fields, type);
    }

    @SuppressWarnings("unchecked")
    private <T> T handle(String method, String path, Map<String, String> queryParams,
                          Object body, TypeReference<T> type) {
        StringBuilder url = new StringBuilder("https://mock.postproxy.dev").append(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            StringJoiner joiner = new StringJoiner("&");
            for (Map.Entry<String, String> e : queryParams.entrySet()) {
                joiner.add(e.getKey() + "=" + e.getValue());
            }
            url.append(joiner);
        }
        requests.add(new RecordedRequest(method, url.toString(), body));

        if (responseStatus == 204) return null;

        if (responseStatus >= 400) {
            Map<String, Object> parsed = mapper.convertValue(responseBody, new TypeReference<>() {});
            String msg = parsed != null && parsed.containsKey("error")
                    ? String.valueOf(parsed.get("error")) : "Error";
            throw switch (responseStatus) {
                case 400 -> new BadRequestException(msg, parsed);
                case 401 -> new AuthenticationException(msg, parsed);
                case 404 -> new NotFoundException(msg, parsed);
                case 422 -> new ValidationException(msg, parsed);
                default -> new PostProxyException(msg, responseStatus, parsed);
            };
        }

        String json;
        try {
            json = mapper.writeValueAsString(responseBody);
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
