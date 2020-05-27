package com.haivv.aws.sns.helper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.Base64;

/**
 * Utility class for all operations on JWT.
 */
public class CognitoJWTParser {

    private static final int PAYLOAD = 1;
    private static final int JWT_PARTS = 3;

    /**
     * Returns payload of a JWT as a JSON object.
     *
     * @param jwt REQUIRED: valid JSON Web Token as String.
     * @return payload as a JSONObject.
     */
    public static JSONObject getPayload(String jwt) {
        try {
            validateJWT(jwt);
            Base64.Decoder dec = Base64.getDecoder();
            final String payload = jwt.split("\\.")[PAYLOAD];
            final byte[] sectionDecoded = dec.decode(payload);
            final String jwtSection = new String(sectionDecoded, "UTF-8");
            return new JSONObject(jwtSection);
        } catch (final UnsupportedEncodingException e) {
            throw new InvalidParameterException(e.getMessage());
        } catch (final Exception e) {
            throw new InvalidParameterException("error in parsing JSON");
        }
    }

    /**
     * Checks if {@code JWT} is a valid JSON Web Token.
     *
     * @param jwt REQUIRED: The JWT as a {@link String}.
     */
    private static void validateJWT(String jwt) {
        // Check if the the JWT has the three parts
        final String[] jwtParts = jwt.split("\\.");
        if (jwtParts.length != JWT_PARTS) {
            throw new InvalidParameterException("not a JSON Web Token");
        }
    }
}
