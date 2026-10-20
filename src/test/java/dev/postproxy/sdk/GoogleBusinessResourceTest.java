package dev.postproxy.sdk;

import dev.postproxy.sdk.resource.GoogleBusinessResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GoogleBusinessResourceTest {

    private static final String BASE = "/api/profiles/prof_gbp/google_business";

    @Test
    void readsLocationWithReadMask() {
        var mock = new MockPostProxyClient(Map.of("title", "Cafe"), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        var result = gbp.location("prof_gbp", "loc_1", "title,phoneNumbers", null);
        assertEquals("Cafe", result.get("title"));
        assertEquals("https://mock.postproxy.dev" + BASE + "/location?location_id=loc_1&read_mask=title,phoneNumbers",
                mock.getRequests().get(0).url());
    }

    @Test
    void updatesLocationWithFieldMask() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        gbp.updateLocation("prof_gbp", "loc_1", List.of("websiteUri"), Map.of("websiteUri", "https://acme.test"));

        var req = mock.getRequests().get(0);
        assertEquals("PATCH", req.method());
        assertTrue(req.url().contains(BASE + "/update_location"));
        assertEquals(Map.of("location_id", "loc_1", "websiteUri", "https://acme.test", "fields", List.of("websiteUri")),
                req.body());
    }

    @Test
    void listsAvailableCategoriesWithRegionCode() {
        var mock = new MockPostProxyClient(Map.of("categories", List.of()), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        gbp.availableLocationCategories("prof_gbp", "loc_1", "US", null, null, null, 50, null, null);

        var url = mock.getRequests().get(0).url();
        assertTrue(url.contains("region_code=US"));
        assertTrue(url.contains("page_size=50"));
        assertFalse(url.contains("filter="));
    }

    @Test
    void updatesHoursPassingGoogleShape() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var gbp = new GoogleBusinessResource(mock);
        var regularHours = Map.of("periods", List.of(Map.of("openDay", "MONDAY", "openTime", Map.of("hours", 9),
                "closeDay", "MONDAY", "closeTime", Map.of("hours", 17))));

        gbp.updateHours("prof_gbp", "loc_1", List.of("regularHours"), Map.of("regularHours", regularHours));

        assertEquals(Map.of("location_id", "loc_1", "fields", List.of("regularHours"), "regularHours", regularHours),
                mock.getRequests().get(0).body());
    }

    @Test
    void updatesAttributesServiceListAndFoodMenus() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        gbp.updateAttributes("prof_gbp", "loc_1",
                List.of(Map.of("name", "attributes/has_wifi", "values", List.of(true))),
                List.of("attributes/has_wifi"), null, null);
        gbp.updateServiceList("prof_gbp", "loc_1", List.of(Map.of("freeFormServiceItem", Map.of())));
        gbp.updateFoodMenus("prof_gbp", "loc_1", List.of(Map.of("labels", List.of())), List.of("menus"), null, null);

        var reqs = mock.getRequests();
        assertTrue(reqs.get(0).url().contains("/update_attributes"));
        assertEquals(Map.of("location_id", "loc_1",
                "attributes", List.of(Map.of("name", "attributes/has_wifi", "values", List.of(true))),
                "attribute_mask", List.of("attributes/has_wifi")), reqs.get(0).body());
        assertEquals(Map.of("location_id", "loc_1", "serviceItems", List.of(Map.of("freeFormServiceItem", Map.of()))),
                reqs.get(1).body());
        assertEquals(Map.of("location_id", "loc_1", "menus", List.of(Map.of("labels", List.of())),
                "fields", List.of("menus")), reqs.get(2).body());
    }

    @Test
    void readsAttributesServicesAndMenus() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        gbp.attributes("prof_gbp", "loc_1");
        gbp.availableAttributes("prof_gbp", "loc_1", null, null, null, true, null, null, null);
        gbp.serviceList("prof_gbp", "loc_1");
        gbp.foodMenus("prof_gbp", "loc_1", List.of("menus", "name"), null);

        var reqs = mock.getRequests();
        assertTrue(reqs.get(0).url().contains(BASE + "/attributes?"));
        assertTrue(reqs.get(1).url().contains(BASE + "/available_attributes?"));
        assertTrue(reqs.get(1).url().contains("show_all=true"));
        assertTrue(reqs.get(2).url().contains(BASE + "/service_list?"));
        assertTrue(reqs.get(3).url().contains(BASE + "/food_menus?"));
        assertTrue(reqs.get(3).url().contains("fields=menus,name"));
        assertTrue(reqs.stream().allMatch(r -> r.method().equals("GET")));
    }

    @Test
    void managesPlaceActionLinks() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var gbp = new GoogleBusinessResource(mock);

        gbp.placeActionLinks("prof_gbp", "loc_1");
        gbp.availablePlaceActionTypes("prof_gbp", "loc_1", "en", null, null, null);
        gbp.createPlaceActionLink("prof_gbp", "loc_1", "https://book.test", "APPOINTMENT", true, null, null);
        gbp.updatePlaceActionLink("prof_gbp", "loc_1", "locations/1/placeActionLinks/2", List.of("uri"),
                "https://book2.test", null, null);
        gbp.deletePlaceActionLink("prof_gbp", "loc_1", "locations/1/placeActionLinks/2");

        var reqs = mock.getRequests();
        assertEquals("GET", reqs.get(0).method());
        assertTrue(reqs.get(1).url().contains("language_code=en"));
        assertEquals("POST", reqs.get(2).method());
        assertEquals(Map.of("location_id", "loc_1", "uri", "https://book.test", "place_action_type", "APPOINTMENT",
                "is_preferred", true), reqs.get(2).body());
        assertEquals("PATCH", reqs.get(3).method());
        assertEquals(Map.of("location_id", "loc_1", "name", "locations/1/placeActionLinks/2", "fields", List.of("uri"),
                "uri", "https://book2.test"), reqs.get(3).body());
        assertEquals("DELETE", reqs.get(4).method());
        assertEquals("https://mock.postproxy.dev" + BASE
                        + "/delete_place_action_link?location_id=loc_1&name=locations/1/placeActionLinks/2",
                reqs.get(4).url());
    }

    @Test
    void managesMedia() {
        var mock = new MockPostProxyClient(Map.of(), 200, "pg_1");
        var gbp = new GoogleBusinessResource(mock);

        gbp.media("prof_gbp", "loc_1", 10, null, null);
        gbp.createMedia("prof_gbp", "loc_1", "https://img.test/a.jpg", "COVER", "PHOTO", null, null, "idem");
        gbp.deleteMedia("prof_gbp", "loc_1", "accounts/1/locations/1/media/9");

        var reqs = mock.getRequests();
        assertTrue(reqs.get(0).url().contains("page_size=10"));
        assertTrue(reqs.get(0).url().contains("profile_group_id=pg_1"));
        assertEquals(Map.of("location_id", "loc_1", "media_url", "https://img.test/a.jpg", "category", "COVER",
                "media_format", "PHOTO"), reqs.get(1).body());
        assertEquals("idem", reqs.get(1).idempotencyKey());
        assertEquals("DELETE", reqs.get(2).method());
        assertTrue(reqs.get(2).url().contains("media_name=accounts/1/locations/1/media/9"));
    }
}
