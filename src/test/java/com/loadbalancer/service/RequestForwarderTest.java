package com.loadbalancer.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class RequestForwarderTest {

    @Mock
    private LoadBalancer loadBalancer;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RequestForwarder requestForwarder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testForwardRequest_Success() {
        String path = "data";
        String backendUrl = "http://localhost:8081/";
        when(loadBalancer.getNextServer()).thenReturn(backendUrl);
        when(restTemplate.getForEntity(backendUrl + path, String.class)).thenReturn(ResponseEntity.ok("Success Response"));

        ResponseEntity<String> response = requestForwarder.forwardRequest(path);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success Response", response.getBody());
    }

    @Test
    void testForwardRequest_ClientError() {
        String path = "invalid";
        String backendUrl = "http://localhost:8081/";
        when(loadBalancer.getNextServer()).thenReturn(backendUrl);
        when(restTemplate.getForEntity(backendUrl + path, String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        ResponseEntity<String> response = requestForwarder.forwardRequest(path);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Client error"));
    }

    @Test
    void testForwardRequest_ServerUnreachable() {
        String path = "data";
        String backendUrl = "http://localhost:8081/";
        when(loadBalancer.getNextServer()).thenReturn(backendUrl);
        when(restTemplate.getForEntity(backendUrl + path, String.class))
                .thenThrow(new ResourceAccessException("Connection refused"));

        ResponseEntity<String> response = requestForwarder.forwardRequest(path);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Backend server unreachable.", response.getBody());
    }

    @Test
    void testForwardRequest_UnexpectedError() {
        String path = "data";
        String backendUrl = "http://localhost:8081/";
        when(loadBalancer.getNextServer()).thenReturn(backendUrl);
        when(restTemplate.getForEntity(backendUrl + path, String.class))
                .thenThrow(new RuntimeException("Unexpected exception"));

        ResponseEntity<String> response = requestForwarder.forwardRequest(path);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to forward request.", response.getBody());
    }
}

