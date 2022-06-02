package run.halo.app.identity.apitoken;

import org.apache.commons.lang3.StringUtils;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.identity.authentication.OAuth2TokenType;

/**
 * A default implementation for {@link PersonalAuthorization}.
 *
 * @author guqing
 * @since 2.0.0
 */
public class DefaultPersonalAuthorizationService implements PersonalAuthorizationService {

    private final ExtensionClient extensionClient;

    public DefaultPersonalAuthorizationService(ExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }

    @Override
    public <T extends PersonalAuthorization> T findByToken(Class<T> type, String tokenValue) {
        return extensionClient.list(type, authorization ->
                    StringUtils.equals(authorization.getTokenValue(), tokenValue)
                        && OAuth2TokenType.ACCESS_TOKEN.equals(authorization.getTokenType()),
                null)
            .stream().findFirst()
            .orElse(null);
    }
}
