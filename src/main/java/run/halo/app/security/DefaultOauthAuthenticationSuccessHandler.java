package run.halo.app.security;

import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Performs a redirect on authentication success.
 * The default is to redirect to a saved request if present and otherwise "/console".
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
public class DefaultOauthAuthenticationSuccessHandler extends
    RedirectServerAuthenticationSuccessHandler implements OauthAuthenticationSuccessHandler {

    public DefaultOauthAuthenticationSuccessHandler() {
        super("/console");
    }
}
