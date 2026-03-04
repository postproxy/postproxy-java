package dev.postproxy.sdk;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public final class WebhookSignature {

    private WebhookSignature() {}

    public static boolean verify(String payload, String signatureHeader, String secret) {
        try {
            String timestamp = null;
            String expected = null;

            for (String part : signatureHeader.split(",")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2) {
                    if ("t".equals(kv[0])) timestamp = kv[1];
                    else if ("v1".equals(kv[0])) expected = kv[1];
                }
            }

            if (timestamp == null || expected == null) {
                return false;
            }

            String signedPayload = timestamp + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] hash = mac.doFinal(signedPayload.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String computed = sb.toString();

            return MessageDigest.isEqual(computed.getBytes(), expected.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}
