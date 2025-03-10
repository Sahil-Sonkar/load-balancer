package com.loadbalancer.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.loadbalancer.service.RequestForwarder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LoadBalancerControllerTest {

    @Mock
    private RequestForwarder requestForwarder;

    @InjectMocks
    private LoadBalancerController loadBalancerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testForwardRequest_Success() {
        String path = "/data";
        when(requestForwarder.forwardRequest(path)).thenReturn(ResponseEntity.ok("Success"));

        ResponseEntity<String> response = loadBalancerController.forwardRequest(path);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
    }

    @Test
    void testForwardRequest_EmptyPath() {
        ResponseEntity<String> response = loadBalancerController.forwardRequest("");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Path cannot be empty", response.getBody());
    }
}

