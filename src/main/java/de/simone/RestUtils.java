/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.simone.backend.ScheduledTasksExecutor;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static FreeIpApiResponse getIpLocation(String ip4) {
        FreeIpApiResponse ipLocation = new FreeIpApiResponse();

        try (HttpClient client = HttpClient.newHttpClient()) {
            String url = "https://free.freeipapi.com/api/json/" + ip4;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean isOk = ScheduledTasksExecutor.logApiError(response, url);
            if (!isOk)
                return ipLocation;

            ipLocation = objectMapper.readValue(response.body(), FreeIpApiResponse.class);

        } catch (Exception e) {
            Log.error("", e);
        }
        return ipLocation;
    }

    public static class FreeIpApiResponse {
        public int ipVersion;
        public String ipAddress;
        public double latitude;
        public double longitude;
        public String countryName;
        public String countryCode;
        public String capital;
        public List<Integer> phoneCodes;
        public List<String> timeZones;
        public String zipCode;
        public String cityName;
        public String regionName;
        public Object regionCode;
        public String continent;
        public String continentCode;
        public List<String> currencies;
        public List<String> languages;
        public String asn;
        public String asnOrganization;
        public boolean isProxy;
    }
}
