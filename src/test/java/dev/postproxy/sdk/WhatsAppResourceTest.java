package dev.postproxy.sdk;

import dev.postproxy.sdk.param.WhatsAppTemplateInput;
import dev.postproxy.sdk.resource.WhatsAppResource;
import dev.postproxy.sdk.resource.WhatsAppTemplatesResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WhatsAppResourceTest {

    private static final String BASE = "/api/profiles/prof_wa/whatsapp";

    private static final Map<String, Object> MOCK_TEMPLATE = Map.ofEntries(
            Map.entry("id", "tpl_1"),
            Map.entry("external_id", "998877"),
            Map.entry("profile_id", "prof_wa"),
            Map.entry("name", "order_update"),
            Map.entry("language", "en_US"),
            Map.entry("category", "UTILITY"),
            Map.entry("status", "APPROVED"),
            Map.entry("parameter_format", "POSITIONAL"),
            Map.entry("components", List.of(Map.of("type", "BODY", "text", "Order {{1}} shipped"))),
            Map.entry("variable_count", 1),
            Map.entry("created_at", "2026-09-28T00:00:00Z"),
            Map.entry("updated_at", "2026-09-28T00:00:00Z")
    );

    // --- Templates ---

    @Test
    void listsTemplatesWithFilters() {
        var mock = new MockPostProxyClient(Map.of("data", List.of(MOCK_TEMPLATE)), 200, null);
        var templates = new WhatsAppTemplatesResource(mock);

        var result = templates.list("prof_wa", null, null, "APPROVED", true, null);
        assertEquals("order_update", result.data().get(0).name());
        assertEquals(1, result.data().get(0).variableCount());
        assertEquals("https://mock.postproxy.dev/api/profiles/prof_wa/templates?status=APPROVED&refresh=true",
                mock.getRequests().get(0).url());
    }

    @Test
    void getsTemplateByNameAndLanguage() {
        var mock = new MockPostProxyClient(MOCK_TEMPLATE, 200, null);
        var templates = new WhatsAppTemplatesResource(mock);

        templates.get("prof_wa", "order update", "en_US", null);
        assertEquals("https://mock.postproxy.dev/api/profiles/prof_wa/templates/order%20update?language=en_US",
                mock.getRequests().get(0).url());
    }

    @Test
    void createsUpdatesAndDeletesTemplate() {
        var mock = new MockPostProxyClient(MOCK_TEMPLATE, 200, null);
        var templates = new WhatsAppTemplatesResource(mock);

        templates.create("prof_wa", WhatsAppTemplateInput.builder("order_update", "UTILITY", "en_US")
                .components(List.of(Map.of("type", "BODY", "text", "Order {{1}} shipped")))
                .build());
        templates.update("prof_wa", "tpl_1", null, 3600);
        templates.delete("prof_wa", "order_update");

        var reqs = mock.getRequests();
        assertEquals("POST", reqs.get(0).method());
        assertEquals("UTILITY", ((WhatsAppTemplateInput) reqs.get(0).body()).category());
        assertEquals("PATCH", reqs.get(1).method());
        assertTrue(reqs.get(1).url().contains("/templates/tpl_1"));
        assertEquals(Map.of("message_send_ttl_seconds", 3600), reqs.get(1).body());
        assertEquals("DELETE", reqs.get(2).method());
        assertFalse(reqs.get(2).url().contains("language"));
    }

    @Test
    void readsTemplateLibrary() {
        var mock = new MockPostProxyClient(Map.of("data", List.of()), 200, null);
        var templates = new WhatsAppTemplatesResource(mock);

        templates.library("prof_wa", "delivery", null, null);
        assertEquals("https://mock.postproxy.dev/api/profiles/prof_wa/templates/library?name=delivery",
                mock.getRequests().get(0).url());
    }

    // --- Number administration ---

    @Test
    void passesPhoneNumberIdAsQueryParamOnReads() {
        var mock = new MockPostProxyClient(Map.of("verified_name", "Acme"), 200, null);
        var wa = new WhatsAppResource(mock);

        var info = wa.numberInfo("prof_wa", "pn_1");
        wa.blockedUsers("prof_wa", "pn_1", 10, null, null);

        assertEquals("Acme", info.get("verified_name"));
        assertEquals("https://mock.postproxy.dev" + BASE + "/number_info?phone_number_id=pn_1",
                mock.getRequests().get(0).url());
        assertEquals("https://mock.postproxy.dev" + BASE + "/blocked_users?phone_number_id=pn_1&limit=10",
                mock.getRequests().get(1).url());
    }

    @Test
    void sendsWritesAsJsonBodies() {
        var mock = new MockPostProxyClient(Map.of("success", true), 200, null);
        var wa = new WhatsAppResource(mock);

        wa.registerPhoneNumber("prof_wa", "pn_1", "123456");
        wa.requestVerificationCode("prof_wa", "pn_1", "SMS", "en", null, null);
        wa.verifyPhoneNumber("prof_wa", "pn_1", "000111");
        wa.updateBusinessProfile("prof_wa", "pn_1", Map.of("about", "Hi", "websites", List.of("https://acme.test")));
        wa.updateBusinessProfilePhoto("prof_wa", "pn_1", "https://img.test/a.png");
        wa.requestDisplayNameChange("prof_wa", "pn_1", "Acme Co");
        wa.setUsername("prof_wa", "pn_1", "acme", "force_transfer", null, null);
        wa.blockUsers("prof_wa", "pn_1", List.of("15551234567"));

        var reqs = mock.getRequests();
        assertEquals(List.of("POST", "POST", "POST", "PATCH", "POST", "POST", "PUT", "POST"),
                reqs.stream().map(MockPostProxyClient.RecordedRequest::method).toList());
        assertEquals(Map.of("phone_number_id", "pn_1", "pin", "123456"), reqs.get(0).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "method", "SMS", "language", "en"), reqs.get(1).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "about", "Hi", "websites", List.of("https://acme.test")),
                reqs.get(3).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "url", "https://img.test/a.png"), reqs.get(4).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "display_name", "Acme Co"), reqs.get(5).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "username", "acme", "transfer_action", "force_transfer"),
                reqs.get(6).body());
        assertEquals(Map.of("phone_number_id", "pn_1", "users", List.of("15551234567")), reqs.get(7).body());
    }

    @Test
    void deletesUsernameViaQueryParams() {
        var mock = new MockPostProxyClient(Map.of("success", true), 200, null);
        var wa = new WhatsAppResource(mock);

        wa.deleteUsername("prof_wa", "pn_1");

        var req = mock.getRequests().get(0);
        assertEquals("DELETE", req.method());
        assertNull(req.body());
        assertTrue(req.url().contains("phone_number_id=pn_1"));
    }

    @Test
    void managesGroups() {
        var mock = new MockPostProxyClient(Map.of(), 200, null);
        var wa = new WhatsAppResource(mock);

        wa.createGroup("prof_wa", "pn_1", "Customers", null, "approval_required", null, null);
        wa.group("prof_wa", "pn_1", "g_1");
        wa.updateGroup("prof_wa", "pn_1", "g_1", "VIPs", null, null);
        wa.addGroupParticipants("prof_wa", "pn_1", "g_1", List.of("15551234567"));
        wa.createGroupInviteLink("prof_wa", "pn_1", "g_1");
        wa.approveGroupJoinRequests("prof_wa", "pn_1", "g_1", List.of("15557654321"));
        wa.deleteGroup("prof_wa", "pn_1", "g_1");

        var reqs = mock.getRequests();
        assertEquals(Map.of("phone_number_id", "pn_1", "subject", "Customers", "join_approval_mode", "approval_required"),
                reqs.get(0).body());
        assertTrue(reqs.get(1).url().contains("group_id=g_1"));
        assertEquals(Map.of("phone_number_id", "pn_1", "group_id", "g_1", "subject", "VIPs"), reqs.get(2).body());
        assertTrue(reqs.get(3).url().contains(BASE + "/add_group_participants"));
        assertEquals(Map.of("phone_number_id", "pn_1", "group_id", "g_1", "phone_numbers", List.of("15551234567")),
                reqs.get(3).body());
        assertTrue(reqs.get(4).url().contains("/create_group_invite_link"));
        assertTrue(reqs.get(5).url().contains("/approve_group_join_requests"));
        assertEquals("DELETE", reqs.get(6).method());
        assertTrue(reqs.get(6).url().contains("group_id=g_1"));
    }

    @Test
    void sendsConversionEventsWithoutPhoneNumberId() {
        var mock = new MockPostProxyClient(Map.of("events_received", 1), 200, null);
        var wa = new WhatsAppResource(mock);

        wa.dataset("prof_wa");
        wa.sendConversionEvent("prof_wa", Map.of("event_name", "Purchase", "event_id", "order-42",
                "chat_id", "chat_1", "currency", "USD", "value", 19.99));

        var reqs = mock.getRequests();
        assertFalse(reqs.get(0).url().contains("phone_number_id"));
        assertTrue(reqs.get(1).url().contains(BASE + "/send_conversion_event"));
        assertEquals(Map.of("event_name", "Purchase", "event_id", "order-42", "chat_id", "chat_1",
                "currency", "USD", "value", 19.99), reqs.get(1).body());
    }
}
