package dev.postproxy.sdk.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.postproxy.sdk.PostProxyClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages a Google Business Profile location's own data — hours, attributes, services, menus,
 * action links and photos — separate from posting to it.
 *
 * <p>{@code locationId} is the placement id from {@code profiles().placements(profileId)}.
 * Google's payloads are camelCased and passed through untouched, so you can PATCH back exactly
 * what you GET. Updates take a {@code fields} list naming which top-level fields to change
 * (Google's update mask).
 */
public class GoogleBusinessResource {

    private final PostProxyClient client;

    public GoogleBusinessResource(PostProxyClient client) {
        this.client = client;
    }

    // --- Location ---

    public Map<String, Object> location(String profileId, String locationId) {
        return location(profileId, locationId, null, null);
    }

    public Map<String, Object> location(String profileId, String locationId, String readMask, String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("read_mask", readMask);
        return read(profileId, "location", locationId, query, profileGroupId);
    }

    /**
     * {@code location} holds the Google fields to change (e.g. {@code title},
     * {@code phoneNumbers}, {@code websiteUri}); {@code fields} names them.
     */
    public Map<String, Object> updateLocation(String profileId, String locationId, List<String> fields,
                                              Map<String, Object> location) {
        return updateLocation(profileId, locationId, fields, location, null, null);
    }

    public Map<String, Object> updateLocation(String profileId, String locationId, List<String> fields,
                                              Map<String, Object> location, String profileGroupId,
                                              String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>(location);
        body.put("fields", fields);
        return write("PATCH", profileId, "update_location", locationId, body, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> availableLocationCategories(String profileId, String locationId, String regionCode) {
        return availableLocationCategories(profileId, locationId, regionCode, null, null, null, null, null, null);
    }

    public Map<String, Object> availableLocationCategories(String profileId, String locationId, String regionCode,
                                                           String languageCode, String filter, String view,
                                                           Integer pageSize, String pageToken,
                                                           String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("region_code", regionCode);
        query.put("language_code", languageCode);
        query.put("filter", filter);
        query.put("view", view);
        query.put("page_size", pageSize);
        query.put("page_token", pageToken);
        return read(profileId, "available_location_categories", locationId, query, profileGroupId);
    }

    /**
     * {@code hours} holds Google's {@code regularHours}, {@code specialHours} and/or
     * {@code moreHours}; {@code fields} names the ones to change.
     */
    public Map<String, Object> updateHours(String profileId, String locationId, List<String> fields,
                                           Map<String, Object> hours) {
        return updateHours(profileId, locationId, fields, hours, null, null);
    }

    public Map<String, Object> updateHours(String profileId, String locationId, List<String> fields,
                                           Map<String, Object> hours, String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fields", fields);
        body.putAll(hours);
        return write("PATCH", profileId, "update_hours", locationId, body, profileGroupId, idempotencyKey);
    }

    // --- Attributes ---

    public Map<String, Object> attributes(String profileId, String locationId) {
        return attributes(profileId, locationId, null);
    }

    public Map<String, Object> attributes(String profileId, String locationId, String profileGroupId) {
        return read(profileId, "attributes", locationId, Map.of(), profileGroupId);
    }

    public Map<String, Object> availableAttributes(String profileId, String locationId) {
        return availableAttributes(profileId, locationId, null, null, null, null, null, null, null);
    }

    public Map<String, Object> availableAttributes(String profileId, String locationId, String categoryName,
                                                   String regionCode, String languageCode, Boolean showAll,
                                                   Integer pageSize, String pageToken, String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("category_name", categoryName);
        query.put("region_code", regionCode);
        query.put("language_code", languageCode);
        query.put("show_all", showAll);
        query.put("page_size", pageSize);
        query.put("page_token", pageToken);
        return read(profileId, "available_attributes", locationId, query, profileGroupId);
    }

    public Map<String, Object> updateAttributes(String profileId, String locationId,
                                                List<Map<String, Object>> attributes) {
        return updateAttributes(profileId, locationId, attributes, null, null, null);
    }

    public Map<String, Object> updateAttributes(String profileId, String locationId,
                                                List<Map<String, Object>> attributes, List<String> attributeMask,
                                                String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("attributes", attributes);
        body.put("attribute_mask", attributeMask);
        return write("PATCH", profileId, "update_attributes", locationId, body, profileGroupId, idempotencyKey);
    }

    // --- Services & food menus ---

    public Map<String, Object> serviceList(String profileId, String locationId) {
        return serviceList(profileId, locationId, null);
    }

    public Map<String, Object> serviceList(String profileId, String locationId, String profileGroupId) {
        return read(profileId, "service_list", locationId, Map.of(), profileGroupId);
    }

    public Map<String, Object> updateServiceList(String profileId, String locationId,
                                                 List<Map<String, Object>> serviceItems) {
        return updateServiceList(profileId, locationId, serviceItems, null, null, null);
    }

    public Map<String, Object> updateServiceList(String profileId, String locationId,
                                                 List<Map<String, Object>> serviceItems, List<String> fields,
                                                 String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("serviceItems", serviceItems);
        body.put("fields", fields);
        return write("PATCH", profileId, "update_service_list", locationId, body, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> foodMenus(String profileId, String locationId) {
        return foodMenus(profileId, locationId, null, null);
    }

    public Map<String, Object> foodMenus(String profileId, String locationId, List<String> fields,
                                         String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("fields", fields != null ? String.join(",", fields) : null);
        return read(profileId, "food_menus", locationId, query, profileGroupId);
    }

    public Map<String, Object> updateFoodMenus(String profileId, String locationId, List<Map<String, Object>> menus) {
        return updateFoodMenus(profileId, locationId, menus, null, null, null);
    }

    public Map<String, Object> updateFoodMenus(String profileId, String locationId, List<Map<String, Object>> menus,
                                               List<String> fields, String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("menus", menus);
        body.put("fields", fields);
        return write("PATCH", profileId, "update_food_menus", locationId, body, profileGroupId, idempotencyKey);
    }

    // --- Place action links (booking, ordering, ...) ---

    public Map<String, Object> placeActionLinks(String profileId, String locationId) {
        return placeActionLinks(profileId, locationId, null, null, null);
    }

    public Map<String, Object> placeActionLinks(String profileId, String locationId, Integer pageSize,
                                                String pageToken, String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page_size", pageSize);
        query.put("page_token", pageToken);
        return read(profileId, "place_action_links", locationId, query, profileGroupId);
    }

    public Map<String, Object> availablePlaceActionTypes(String profileId, String locationId) {
        return availablePlaceActionTypes(profileId, locationId, null, null, null, null);
    }

    public Map<String, Object> availablePlaceActionTypes(String profileId, String locationId, String languageCode,
                                                         Integer pageSize, String pageToken, String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("language_code", languageCode);
        query.put("page_size", pageSize);
        query.put("page_token", pageToken);
        return read(profileId, "available_place_action_types", locationId, query, profileGroupId);
    }

    /**
     * {@code placeActionType} is e.g. APPOINTMENT, ONLINE_APPOINTMENT, DINING_RESERVATION,
     * FOOD_ORDERING, FOOD_DELIVERY, FOOD_TAKEOUT, SHOP_ONLINE.
     */
    public Map<String, Object> createPlaceActionLink(String profileId, String locationId, String uri,
                                                     String placeActionType) {
        return createPlaceActionLink(profileId, locationId, uri, placeActionType, null, null, null);
    }

    public Map<String, Object> createPlaceActionLink(String profileId, String locationId, String uri,
                                                     String placeActionType, Boolean isPreferred,
                                                     String profileGroupId, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("uri", uri);
        body.put("place_action_type", placeActionType);
        body.put("is_preferred", isPreferred);
        return write("POST", profileId, "create_place_action_link", locationId, body, profileGroupId, idempotencyKey);
    }

    /**
     * {@code name} is the link's resource name from {@link #placeActionLinks(String, String)};
     * {@code fields} names what changes. Null {@code uri} / {@code placeActionType} /
     * {@code isPreferred} are left out.
     */
    public Map<String, Object> updatePlaceActionLink(String profileId, String locationId, String name,
                                                     List<String> fields, String uri, String placeActionType,
                                                     Boolean isPreferred) {
        return updatePlaceActionLink(profileId, locationId, name, fields, uri, placeActionType, isPreferred, null, null);
    }

    public Map<String, Object> updatePlaceActionLink(String profileId, String locationId, String name,
                                                     List<String> fields, String uri, String placeActionType,
                                                     Boolean isPreferred, String profileGroupId,
                                                     String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("fields", fields);
        body.put("uri", uri);
        body.put("place_action_type", placeActionType);
        body.put("is_preferred", isPreferred);
        return write("PATCH", profileId, "update_place_action_link", locationId, body, profileGroupId, idempotencyKey);
    }

    public Map<String, Object> deletePlaceActionLink(String profileId, String locationId, String name) {
        return deletePlaceActionLink(profileId, locationId, name, null, null);
    }

    public Map<String, Object> deletePlaceActionLink(String profileId, String locationId, String name,
                                                     String profileGroupId, String idempotencyKey) {
        return remove(profileId, "delete_place_action_link", locationId, Map.of("name", name),
                profileGroupId, idempotencyKey);
    }

    // --- Media ---

    public Map<String, Object> media(String profileId, String locationId) {
        return media(profileId, locationId, null, null, null);
    }

    public Map<String, Object> media(String profileId, String locationId, Integer pageSize, String pageToken,
                                     String profileGroupId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page_size", pageSize);
        query.put("page_token", pageToken);
        return read(profileId, "media", locationId, query, profileGroupId);
    }

    public Map<String, Object> createMedia(String profileId, String locationId, String mediaUrl) {
        return createMedia(profileId, locationId, mediaUrl, null, null, null, null, null);
    }

    /**
     * {@code category} is e.g. COVER, PROFILE, LOGO, EXTERIOR, INTERIOR, PRODUCT, ADDITIONAL;
     * {@code mediaFormat} is PHOTO or VIDEO.
     */
    public Map<String, Object> createMedia(String profileId, String locationId, String mediaUrl, String category,
                                           String mediaFormat, String description, String profileGroupId,
                                           String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("media_url", mediaUrl);
        body.put("category", category);
        body.put("media_format", mediaFormat);
        body.put("description", description);
        return write("POST", profileId, "create_media", locationId, body, profileGroupId, idempotencyKey);
    }

    /** {@code mediaName} is the item's resource name from {@link #media(String, String)}. */
    public Map<String, Object> deleteMedia(String profileId, String locationId, String mediaName) {
        return deleteMedia(profileId, locationId, mediaName, null, null);
    }

    public Map<String, Object> deleteMedia(String profileId, String locationId, String mediaName,
                                           String profileGroupId, String idempotencyKey) {
        return remove(profileId, "delete_media", locationId, Map.of("media_name", mediaName),
                profileGroupId, idempotencyKey);
    }

    private static String path(String profileId, String action) {
        return "/api/profiles/" + profileId + "/google_business/" + action;
    }

    private Map<String, Object> read(String profileId, String action, String locationId,
                                     Map<String, Object> params, String profileGroupId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("location_id", locationId);
        params.forEach((key, value) -> {
            if (value != null) query.put(key, String.valueOf(value));
        });
        putProfileGroup(query, profileGroupId);

        return client.get(path(profileId, action), query, new TypeReference<>() {});
    }

    private Map<String, Object> write(String method, String profileId, String action, String locationId,
                                      Map<String, Object> params, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        putProfileGroup(query, profileGroupId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("location_id", locationId);
        params.forEach((key, value) -> {
            if (value != null) body.put(key, value);
        });

        if ("POST".equals(method)) {
            return client.post(path(profileId, action), query, body, new TypeReference<>() {}, idempotencyKey);
        }
        return client.patch(path(profileId, action), query, body, new TypeReference<>() {}, idempotencyKey);
    }

    private Map<String, Object> remove(String profileId, String action, String locationId,
                                       Map<String, String> params, String profileGroupId, String idempotencyKey) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("location_id", locationId);
        query.putAll(params);
        putProfileGroup(query, profileGroupId);

        return client.delete(path(profileId, action), query, new TypeReference<>() {}, idempotencyKey);
    }

    private void putProfileGroup(Map<String, String> query, String profileGroupId) {
        String pgId = profileGroupId != null ? profileGroupId : client.getDefaultProfileGroupId();
        if (pgId != null) query.put("profile_group_id", pgId);
    }
}
