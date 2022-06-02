package run.halo.app.identity.apitoken;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.Assert;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.Metadata;
import run.halo.app.identity.authentication.OAuth2TokenType;

/**
 * @author guqing
 * @since 2.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class PersonalAuthorization extends AbstractExtension implements Serializable {

    @Schema(required = true)
    protected String tokenValue;

    @Schema(required = true)
    protected Instant issuedAt;

    @Schema(required = true)
    protected String owner;

    protected Instant expiresAt;

    protected OAuth2TokenType tokenType;

    protected Set<String> scopes;

    protected String description;

    protected Map<String, Object> claims;

    public PersonalAuthorization() {
    }

    /**
     * Constructs a {@link PersonalAuthorization} using the provided parameters.
     */
    public PersonalAuthorization(String apiVersion, String kind, Metadata metadata,
        String tokenValue,
        Instant issuedAt, String owner, Instant expiresAt, OAuth2TokenType tokenType,
        String description, Set<String> scopes, Map<String, Object> claims) {
        setApiVersion(apiVersion);
        setKind(kind);
        setMetadata(metadata);
        this.tokenValue = tokenValue;
        this.issuedAt = issuedAt;
        this.owner = owner;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
        this.description = description;
        this.scopes = scopes;
        this.claims = claims;
    }

    protected abstract static class AbstractBuilder<T extends PersonalAuthorization,
        B extends AbstractBuilder<T, B>> {

        protected String apiVersion;

        protected String kind;

        protected Metadata metadata;

        protected String tokenValue;

        protected Instant issuedAt;

        protected String owner;

        protected Instant expiresAt;

        protected OAuth2TokenType tokenType;

        protected String description;

        protected Set<String> scopes = new HashSet<>();

        protected Map<String, Object> claims = new HashMap<>();

        protected AbstractBuilder() {
        }

        public B apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return getThis();
        }

        public B kind(String kind) {
            this.kind = kind;
            return getThis();
        }

        public B metadata(Metadata metadata) {
            this.metadata = metadata;
            return getThis();
        }

        public B claim(String key, String value) {
            claims.put(key, value);
            return getThis();
        }

        public B issuedAt(Instant issuedAt) {
            this.issuedAt = issuedAt;
            return getThis();
        }

        public B tokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
            return getThis();
        }

        public B expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return getThis();
        }

        public B owner(String owner) {
            this.owner = owner;
            return getThis();
        }

        public B scopes(Set<String> scopes) {
            Assert.notNull(scopes, "The scopes must not be null.");
            this.scopes = scopes;
            return getThis();
        }

        public B scope(String scope) {
            scopes.add(scope);
            return getThis();
        }

        public B description(String description) {
            this.description = description;
            return getThis();
        }

        @SuppressWarnings("unchecked")
        protected final B getThis() {
            return (B) this;
        }

        public abstract T build();
    }
}
