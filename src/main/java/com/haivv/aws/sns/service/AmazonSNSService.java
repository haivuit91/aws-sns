package com.haivv.aws.sns.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AmazonSNSService {

    private final AmazonSNS amazonSNS;
    private final Topic topic;

    public AmazonSNSService(AmazonSNS amazonSNS,
                            Topic topic) {
        this.amazonSNS = amazonSNS;
        this.topic = topic;
    }

    /**
     * Publish message
     *
     * @param message message
     */
    public void publish(Object message) {
        PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topic.getTopicArn())
                .withMessage(parseDataJson(message));
        amazonSNS.publish(publishRequest);
    }

    // Parse data object to string json
    private String parseDataJson(Object message) {
        try {
            return new ObjectMapper().writeValueAsString(message);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return "";
    }
}
