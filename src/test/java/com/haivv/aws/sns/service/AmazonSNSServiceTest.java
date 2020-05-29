package com.haivv.aws.sns.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AmazonSNSServiceTest {

    @Autowired
    private AmazonSNSService service;

    @Test
    public void publish() {
        service.publish("Test publish notice");
    }

    @Test
    public void createEndpoint() {
        String endpointArn = service.createEndpoint("5ecdc78325c3fd00282d640a");
        service.subscribeTopic(endpointArn);
    }

    @Test
    public void publishToTargetArn() {
        service.publishToTargetArn("", "Test publish to target ARN");
    }

    @Test
    public void getEndpointArn() {
        service.getEndpointArn();
    }
}
