package com.loadbalancer.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.loadbalancer.service.LoadBalancer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class ServerControllerTest {

    @Mock
    private LoadBalancer loadBalancer;

    @InjectMocks
    private ServerController serverController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterServer_Success() {
        String serverUrl = "http://localhost:8081";
        doNothing().when(loadBalancer).addServer(serverUrl);

        ResponseEntity<String> response = serverController.registerServer(Map.of("serverUrl", serverUrl));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Server Registered: " + serverUrl, response.getBody());
    }

    @Test
    void testRegisterServer_EmptyUrl() {
        ResponseEntity<String> response = serverController.registerServer(Map.of("serverUrl", ""));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Server URL cannot be empty", response.getBody());
    }

    @Test
    void testRemoveServer_Success() {
        String serverUrl = "http://localhost:8081";
        when(loadBalancer.getBackendServers()).thenReturn(List.of(serverUrl));
        doNothing().when(loadBalancer).removeServer(serverUrl);

        ResponseEntity<String> response = serverController.removeServer(Map.of("serverUrl", serverUrl));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Server Removed: " + serverUrl, response.getBody());
    }

    @Test
    void testRemoveServer_NotFound() {
        String serverUrl = "http://localhost:8081";
        when(loadBalancer.getBackendServers()).thenReturn(List.of());

        ResponseEntity<String> response = serverController.removeServer(Map.of("serverUrl", serverUrl));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Server not found: " + serverUrl, response.getBody());
    }

    @Test
    void testListServers_WithServers() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082");
        when(loadBalancer.getBackendServers()).thenReturn(servers);

        ResponseEntity<List<String>> response = serverController.listServers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(servers, response.getBody());
    }

    @Test
    void testListServers_NoServers() {
        when(loadBalancer.getBackendServers()).thenReturn(List.of());

        ResponseEntity<List<String>> response = serverController.listServers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of("No servers registered"), response.getBody());
    }
}

