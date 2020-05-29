package com.haivv.aws.sns.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AmazonProperties {

    private String accessKey;
    private String secretKey;
    private String region;
    private SNS sns;
    private Cognito cognito;

    @Data
    public static class SNS {
        private String name;
        private Topic topic;
        private PlatformApplication platformApplication;
    }

    @Data
    public static class Topic {
        private String arn;
    }

    @Data
    public static class PlatformApplication {
        private String arn;
    }

    @Data
    public static class Cognito {
        private String poolId;
        private String clientAppId;
        private String fedPoolId;
        private String userName;
        private String userPass;
    }
}
