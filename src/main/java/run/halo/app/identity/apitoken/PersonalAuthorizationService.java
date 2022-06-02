package run.halo.app.identity.apitoken;

import org.springframework.lang.Nullable;

/**
 * Implementations of this interface are responsible for the management
 * of {@link PersonalAuthorization Personal Authorization(s)}.
 *
 * @author guqing
 * @since 2.0.0
 */
public interface PersonalAuthorizationService {

    /**
     * Returns the {@link PersonalAuthorization} containing the provided {@code token},
     * or {@code null} if not found.
     *
     * @param type Subclass of {@link PersonalAuthorization}
     * @param tokenValue the token credential
     * @return the {@link PersonalAuthorization} if found, otherwise {@code null}
     */
    @Nullable
    <T extends PersonalAuthorization> T findByToken(Class<T> type, String tokenValue);
}
