package run.halo.app.security;

import org.pf4j.ExtensionPoint;
import org.springframework.core.Ordered;
import org.springframework.web.server.WebFilter;

/**
 * @author guqing
 * @since 2.0.0
 */
public interface AdditionalWebFilter extends WebFilter, ExtensionPoint, Ordered {

    /**
     * Gets the order value of the object.
     *
     * @return the order value
     */
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
