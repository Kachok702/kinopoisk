package org.example.VKR.config.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateService {

    private final RestTemplate restTemplate;

    @Autowired
    public RestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> getResponse(String url, Class<T> response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "14088d89-fa7f-4114-acc2-8a1fe9f0baa2");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                response
        );
    }

}
