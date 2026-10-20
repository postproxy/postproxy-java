import dev.postproxy.sdk.PostProxy;

import java.util.List;
import java.util.Map;

public class GoogleBusiness {

    public static void main(String[] args) {
        var client = PostProxy.builder("POSTPROXY_API_KEY").build();
        var profileId = "your-google-business-profile-id";
        var gbp = client.googleBusiness();

        // locationId is the placement id of the location
        var placements = client.profiles().placements(profileId);
        var locationId = placements.data().get(0).id();

        // Read and update the location
        var location = gbp.location(profileId, locationId, "title,phoneNumbers,websiteUri,regularHours", null);
        System.out.println("Location: " + location);

        gbp.updateLocation(profileId, locationId, List.of("websiteUri"),
                Map.of("websiteUri", "https://example.com"));

        // Opening hours: Mon-Fri 9-17
        var weekdays = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
        var periods = weekdays.stream().map(day -> Map.of(
                "openDay", day,
                "openTime", Map.of("hours", 9),
                "closeDay", day,
                "closeTime", Map.of("hours", 17))).toList();
        gbp.updateHours(profileId, locationId, List.of("regularHours"),
                Map.of("regularHours", Map.of("periods", periods)));

        // Booking link
        var types = gbp.availablePlaceActionTypes(profileId, locationId);
        System.out.println("Available action types: " + types);
        gbp.createPlaceActionLink(profileId, locationId, "https://book.example.com", "APPOINTMENT",
                true, null, null);

        // Add a storefront photo
        gbp.createMedia(profileId, locationId, "https://example.com/storefront.jpg",
                "EXTERIOR", "PHOTO", null, null, null);
        System.out.println("Media: " + gbp.media(profileId, locationId));
    }
}
