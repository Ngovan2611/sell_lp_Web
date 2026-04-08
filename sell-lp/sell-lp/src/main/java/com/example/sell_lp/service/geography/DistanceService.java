package com.example.sell_lp.service.geography;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistanceService {

    private static final double STORE_LAT = 21.0278;
    private static final double STORE_LON = 105.8342;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DistanceService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    public double calculateDistanceFromStore(String userAddress) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search"
                + "?q=" + java.net.URLEncoder.encode(userAddress, "UTF-8")
                + "&format=json&limit=1";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("User-Agent", "MyApp/1.0 (nvanvo261105@gmail.com)");
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        org.springframework.http.ResponseEntity<String> response =
                restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        if (root.isEmpty()) {
            throw new RuntimeException("Không tìm thấy địa chỉ trên bản đồ");
        }
        double lat = root.get(0).get("lat").asDouble();
        double lon = root.get(0).get("lon").asDouble();

        // 3️⃣ Tính khoảng cách bằng Haversine
        return haversine(STORE_LAT, STORE_LON, lat, lon);
    }

    // Hàm Haversine
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính Trái đất, km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}