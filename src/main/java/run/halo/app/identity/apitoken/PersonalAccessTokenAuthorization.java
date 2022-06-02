package run.halo.app.identity.apitoken;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.GVK;
import run.halo.app.extension.Metadata;
import run.halo.app.identity.authentication.OAuth2TokenType;

/**
 * @author guqing
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "", version = "v1alpha1", kind = "PersonalAccessToken", plural =
    "personalaccesstokens", singular = "personalaccesstoken")
public class PersonalAccessTokenAuthorization extends PersonalAuthorization {

    public PersonalAccessTokenAuthorization() {
        this.tokenType = OAuth2TokenType.ACCESS_TOKEN;
    }

    public PersonalAccessTokenAuthorization(String apiVersion,
        String kind,
        Metadata metadata,
        String tokenValue,
        Instant issuedAt,
        String owner,
        Instant expiresAt,
        OAuth2TokenType tokenType,
        String description,
        Set<String> scopes,
        Map<String, Object> claims) {
        super(apiVersion, kind, metadata, tokenValue, issuedAt, owner, expiresAt, tokenType,
            description, scopes, claims);
    }

    public static class Builder extends AbstractBuilder<PersonalAccessTokenAuthorization, Builder> {
        @Override
        public PersonalAccessTokenAuthorization build() {
            return new PersonalAccessTokenAuthorization(apiVersion, kind, metadata, tokenValue,
                issuedAt, owner, expiresAt, OAuth2TokenType.ACCESS_TOKEN,
                description, scopes, claims);
        }
    }
}
