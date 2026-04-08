package com.example.sell_lp.service.geography;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class GeoCodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getCoordinatesFromAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl("https://nominatim.openstreetmap.org/search")
                .queryParam("q", address)
                .queryParam("format", "json")
                .queryParam("addressdetails", 1)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "MyApp/1.0 (nvanvo261105@gmail.com)"); // bắt buộc
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);

        Map[] results = response.getBody();
        if (results != null && results.length > 0) {
            return results[0];
        }
        return null;
    }
}