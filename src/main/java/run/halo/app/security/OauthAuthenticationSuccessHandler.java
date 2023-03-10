package run.halo.app.security;

import org.pf4j.ExtensionPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import reactor.core.publisher.Mono;

/**
 * Handles authentication success for oauth.
 *
 * @author guqing
 * @since 2.0.0
 */
public interface OauthAuthenticationSuccessHandler extends ExtensionPoint {

    /**
     * Invoked when the application authenticates successfully.
     *
     * @param webFilterExchange the exchange
     * @param authentication    the {@link Authentication}
     * @return a completion notification (success or error)
     */
    Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                       Authentication authentication);
}
