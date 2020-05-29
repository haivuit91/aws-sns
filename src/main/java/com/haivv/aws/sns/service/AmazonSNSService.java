package com.haivv.aws.sns.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.GetTopicAttributesRequest;
import com.amazonaws.services.sns.model.GetTopicAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.PlatformApplication;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class AmazonSNSService {

    private final AmazonSNS snsClient;
    private final Topic topic;
    private final PlatformApplication platformApplication;

    public AmazonSNSService(AmazonSNS snsClient,
                            Topic topic,
                            PlatformApplication platformApplication) {
        this.snsClient = snsClient;
        this.topic = topic;
        this.platformApplication = platformApplication;
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

        snsClient.publish(publishRequest);
    }

    /**
     * Publish message
     *
     * @param endpointArn endpointArn
     * @param message message
     */
    public void publishToTargetArn(String endpointArn, Object message) {
        PublishRequest publishRequest = new PublishRequest()
                .withTargetArn(endpointArn)
                .withMessage(parseDataJson(message));

        snsClient.publish(publishRequest);
    }

    /**
     * Subscribe topic SNS
     *
     * @param endpointArn endpointArn
     */
    public void subscribeTopic(String endpointArn) {
        SubscribeRequest subscribeRequest = new SubscribeRequest()
                .withTopicArn(topic.getTopicArn())
                .withProtocol("application")
                .withEndpoint(endpointArn);

        snsClient.subscribe(subscribeRequest);
    }

    /**
     * Subscribe topic SNS
     */
    public void getEndpointArn() {
        ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest().withPlatformApplicationArn(platformApplication.getPlatformApplicationArn());

        ListEndpointsByPlatformApplicationResult result = snsClient.listEndpointsByPlatformApplication(request);

        List<Endpoint> endpoints = result.getEndpoints();

        log.info(endpoints);
    }

    /**
     * Create Platform endpoint
     *
     * @param token token
     * @return endpointArn
     */
    public String createEndpoint(String token) {
        String endpointArn = null;
        try {
            log.info("Creating platform endpoint with token: {}", token);
            CreatePlatformEndpointRequest cpeReq = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn(platformApplication.getPlatformApplicationArn())
                    .withToken(token);

            CreatePlatformEndpointResult cpeRes = snsClient.createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();
        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            log.error("Exception message: {}", message);

            Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists with the same [Tt]oken.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                endpointArn = m.group(1);
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }
        return endpointArn;
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
