package com.haivv.aws.sns.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PlatformApplication;
import com.amazonaws.services.sns.model.Topic;
import com.haivv.aws.sns.helper.CognitoHelper;
import com.haivv.aws.sns.helper.CognitoJWTParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonClientConfig {

    @Autowired
    private AmazonProperties amazonProperties;

    @Bean
    public Topic topic() {
        return new Topic().withTopicArn(amazonProperties.getSns().getTopic().getArn());
    }

    @Bean
    public PlatformApplication platformApplication() {
        return new PlatformApplication().withPlatformApplicationArn(amazonProperties.getSns().getPlatformApplication().getArn());
    }

    @Bean
    public AmazonSNS amazonSNS(CognitoHelper helper) {
        String result = helper.validateUser(amazonProperties.getCognito().getUserName(), amazonProperties.getCognito().getUserPass());

        JSONObject payload = CognitoJWTParser.getPayload(result);
        String provider = payload.get("iss").toString().replace("https://", "");

        Credentials credentials = helper.getCredentials(provider, result);

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(amazonProperties.getAccessKey(), amazonProperties.getSecretKey());
//        BasicSessionCredentials awsCreds = new BasicSessionCredentials(credentials.getAccessKeyId(), credentials.getSecretKey(), credentials.getSessionToken());

        return AmazonSNSClientBuilder.standard()
                .withRegion(Regions.fromName(amazonProperties.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
