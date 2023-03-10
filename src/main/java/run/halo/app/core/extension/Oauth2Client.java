package run.halo.app.core.extension;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * Oauth2 client extension for {@link ClientRegistration}.
 *
 * @author guqing
 * @see ClientRegistration
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@GVK(group = "rbac.authorization.halo.run", version = "v1alpha1", kind = "Oauth2Client",
    singular = "oauth2client", plural = "oauth2clients")
public class Oauth2Client extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private Oauth2ClientSpec spec;

    @Data
    public static class Oauth2ClientSpec {

        @Schema(requiredMode = REQUIRED, minLength = 1)
        private String clientId;

        private String clientSecret;

        @Schema(requiredMode = REQUIRED, minLength = 1)
        private String tokenUri;

        /**
         * <p>The client authentication method of the client application.</p>
         *
         * @see ClientAuthenticationMethod
         */
        private String clientAuthenticationMethod;

        /**
         * <p>The grant type of the client application.</p>
         */
        @Schema(allowableValues = {"authorization_code", "refresh_token", "client_credentials",
            "password"})
        private String authorizationGrantType;

        /**
         * <p>The redirect URI of the client application.</p>
         * if {@link #authorizationGrantType) is AUTHORIZATION_CODE, this field cannot be empty.
         */
        private String redirectUri;

        private Set<String> scopes;

        /**
         * <p>The URI of the authorization server's authorization endpoint.</p>
         * if {@link #authorizationGrantType) is AUTHORIZATION_CODE, this field cannot be empty.
         */
        private String authorizationUri;

        private String userInfoUri;

        /**
         * <p>The method used for authenticating the client with the authorization server.</p>
         *
         * @see AuthenticationMethod
         */
        private String userInfoAuthenticationMethod;

        private String userNameAttributeName;

        private String jwkSetUri;

        private String issuerUri;

        private Map<String, Object> configurationMetadata;

        private String clientName;
    }
}
