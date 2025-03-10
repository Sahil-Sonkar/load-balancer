package com.loadbalancer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoadBalancerApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> LoadBalancerApplication.main(new String[] {}));
    }
}
