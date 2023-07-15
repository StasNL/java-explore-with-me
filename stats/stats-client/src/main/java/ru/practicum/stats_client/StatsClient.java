package ru.practicum.stats_client;

import ru.practicum.dto.HitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private static final String GET_STATS_PATH = "/stats";
    private static final String POST_STATS_PATH = "/hit";
    private final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String statsServerUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {

        StringBuilder path = new StringBuilder(GET_STATS_PATH + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                path.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            path.append("&unique=").append(unique);
        }



        return makeAndSendRequest(HttpMethod.GET, path.toString(), parameters, null);
    }

    public ResponseEntity<Object> saveStats(HitDto body) {
        return makeAndSendRequest(HttpMethod.POST, POST_STATS_PATH, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            @Nullable T body
    ) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}