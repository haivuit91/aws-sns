package com.haivv.aws.sns.helper;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.haivv.aws.sns.config.AmazonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CognitoHelper {

    private final AmazonProperties amazonProperties;

    @Autowired
    public CognitoHelper(AmazonProperties amazonProperties) {
        this.amazonProperties = amazonProperties;
    }

    /**
     * Helper method to validate the user
     *
     * @param username represents the username in the cognito user pool
     * @param password represents the password in the cognito user pool
     * @return returns the JWT token after the validation
     */
    public String validateUser(String username, String password) {
        AuthenticationHelper helper = new AuthenticationHelper(amazonProperties.getCognito().getPoolId(),
                amazonProperties.getCognito().getClientAppId(), amazonProperties.getRegion());
        return helper.performSRPAuthentication(username, password);
    }

    /**
     * Returns the AWS credentials
     *
     * @param idprovider the IDP provider for the login map
     * @param id         the username for the login map.
     * @return returns the credentials based on the access token returned from the user pool.
     */
    public Credentials getCredentials(String idprovider, String id) {
        AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();
        AmazonCognitoIdentity provider = AmazonCognitoIdentityClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.fromName(amazonProperties.getRegion()))
                .build();

        GetIdRequest idrequest = new GetIdRequest();
        idrequest.setIdentityPoolId(amazonProperties.getCognito().getFedPoolId());
        idrequest.addLoginsEntry(idprovider, id);
        GetIdResult idResult = provider.getId(idrequest);

        GetCredentialsForIdentityRequest request = new GetCredentialsForIdentityRequest();
        request.setIdentityId(idResult.getIdentityId());
        request.addLoginsEntry(idprovider, id);

        GetCredentialsForIdentityResult result = provider.getCredentialsForIdentity(request);
        return result.getCredentials();
    }
}
