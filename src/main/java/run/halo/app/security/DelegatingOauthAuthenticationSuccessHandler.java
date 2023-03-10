package run.halo.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * Delegates to a collection of  {@link OauthAuthenticationSuccessHandler} implementations.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@RequiredArgsConstructor
public class DelegatingOauthAuthenticationSuccessHandler
    implements OauthAuthenticationSuccessHandler, ServerAuthenticationSuccessHandler {

    private final ExtensionGetter extensionGetter;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange,
                                              Authentication authentication) {
        return delegates()
            .concatMap((delegate) -> delegate.onAuthenticationSuccess(exchange, authentication))
            .then();
    }

    private Flux<OauthAuthenticationSuccessHandler> delegates() {
        return extensionGetter.getEnabledExtensionByDefinition(
                OauthAuthenticationSuccessHandler.class)
            .filter(result -> result != this);
    }
}
