package run.halo.app.security;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.Oauth2Client;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.store.ExtensionStore;

/**
 * A reactive repository for OAuth 2.0 / OpenID Connect 1.0 ClientRegistration(s) that stores
 * {@link ClientRegistration}(s) in the {@link ExtensionStore}.
 *
 * @author guqing
 * @see Oauth2Client
 * @since 2.0.0
 */
@RequiredArgsConstructor
public class OauthClientRegistrationRepository implements ReactiveClientRegistrationRepository {

    private final ReactiveExtensionClient client;

    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
        return client.fetch(Oauth2Client.class, registrationId)
            .map(oauth2Client -> {
                Oauth2Client.Oauth2ClientSpec spec = oauth2Client.getSpec();
                return ClientRegistration.withRegistrationId(registrationId)
                    .clientId(spec.getClientId())
                    .clientSecret(spec.getClientSecret())
                    .clientAuthenticationMethod(
                        toClientAuthenticationMethod(spec.getClientAuthenticationMethod()))
                    .authorizationGrantType(
                        toAuthorizationGrantType(spec.getAuthorizationGrantType()))
                    .redirectUri(spec.getRedirectUri())
                    .authorizationUri(spec.getAuthorizationUri())
                    .tokenUri(spec.getTokenUri())
                    .userInfoUri(spec.getUserInfoUri())
                    .userInfoAuthenticationMethod(
                        toAuthenticationMethod(spec.getUserInfoAuthenticationMethod())
                    )
                    .userNameAttributeName(spec.getUserNameAttributeName())
                    .jwkSetUri(spec.getJwkSetUri())
                    .clientName(spec.getClientName())
                    .providerConfigurationMetadata(defaultIfNull(spec.getConfigurationMetadata(),
                        Map.of())
                    )
                    .issuerUri(spec.getIssuerUri())
                    .scope(spec.getScopes())
                    .build();
            });
    }

    ClientAuthenticationMethod toClientAuthenticationMethod(String value) {
        return new ClientAuthenticationMethod(value);
    }

    AuthorizationGrantType toAuthorizationGrantType(String value) {
        return new AuthorizationGrantType(value);
    }

    AuthenticationMethod toAuthenticationMethod(String value) {
        return StringUtils.isBlank(value) ? AuthenticationMethod.HEADER
            : new AuthenticationMethod(value);
    }
}
