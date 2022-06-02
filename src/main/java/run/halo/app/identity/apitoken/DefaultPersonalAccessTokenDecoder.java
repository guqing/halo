package run.halo.app.identity.apitoken;

import java.util.Collection;
import javax.crypto.SecretKey;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A default implementation of personal access token authentication.
 *
 * @author guqing
 * @since 2.0.0
 */
public class DefaultPersonalAccessTokenDecoder implements PersonalAccessTokenDecoder {

    private static final String DECODING_ERROR_MESSAGE_TEMPLATE =
        "An error occurred while attempting to decode the personal access token: %s";

    private OAuth2TokenValidator<PersonalAccessToken> personalAccessTokenValidator =
        createDefault();

    private final PersonalAuthorizationService personalAuthorizationService;

    private final SecretKey secretKey;

    public DefaultPersonalAccessTokenDecoder(
        PersonalAuthorizationService personalAuthorizationService, SecretKey secretKey) {
        this.personalAuthorizationService = personalAuthorizationService;
        this.secretKey = secretKey;
    }

    /**
     * Use this {@link PersonalAccessToken} Validator.
     *
     * @param personalAccessTokenValidator - the PersonalAccessToken Validator to use
     */
    public void setTokenValidator(
        OAuth2TokenValidator<PersonalAccessToken> personalAccessTokenValidator) {
        Assert.notNull(personalAccessTokenValidator, "personalAccessTokenValidator cannot be null");
        this.personalAccessTokenValidator = personalAccessTokenValidator;
    }

    @Override
    public PersonalAccessToken decode(String token) throws PersonalAccessTokenException {
        preValidate(token);
        PersonalAccessToken personalAccessToken = createPersonalAccessToken(token);
        return validate(personalAccessToken);
    }

    private void preValidate(String token) {
        if (secretKey == null) {
            return;
        }
        boolean matches = PersonalAccessTokenUtils.verifyChecksum(token, secretKey);
        if (matches) {
            return;
        }
        throw new PersonalAccessTokenException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE,
            "Failed to verify the personal access token"));
    }

    private PersonalAccessToken createPersonalAccessToken(String token) {
        PersonalAuthorization personalAuthorization =
            personalAuthorizationService.findByToken(PersonalAccessTokenAuthorization.class, token);
        if (personalAuthorization == null) {
            throw new PersonalAccessTokenException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE,
                "Failed to retrieve personal access token"));
        }
        return createPersonalAccessToken(personalAuthorization);
    }

    private PersonalAccessToken createPersonalAccessToken(PersonalAuthorization authorization) {
        return PersonalAccessToken.builder()
            .principalName(authorization.getOwner())
            .tokenValue(authorization.getTokenValue())
            .issuedAt(authorization.getIssuedAt())
            .expiresAt(authorization.getExpiresAt())
            .scopes(authorization.getScopes())
            .attributes(authorization.getClaims())
            .build();
    }

    private PersonalAccessToken validate(PersonalAccessToken token) {
        OAuth2TokenValidatorResult result = this.personalAccessTokenValidator.validate(token);
        if (result.hasErrors()) {
            Collection<OAuth2Error> errors = result.getErrors();
            String validationErrorString = getValidationExceptionMessage(errors);
            throw new JwtValidationException(validationErrorString, errors);
        }
        return token;
    }

    private String getValidationExceptionMessage(Collection<OAuth2Error> errors) {
        for (OAuth2Error oauth2Error : errors) {
            if (!StringUtils.hasText(oauth2Error.getDescription())) {
                return String.format(DECODING_ERROR_MESSAGE_TEMPLATE, oauth2Error.getDescription());
            }
        }
        return "Unable to validate personal access token";
    }

    public static OAuth2TokenValidator<PersonalAccessToken> createDefault() {
        return new DelegatingOAuth2TokenValidator<>(new PersonalAccessTokenTimestampValidator());
    }
}
