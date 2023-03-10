package run.halo.app.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;
import static org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;
import run.halo.app.core.extension.service.RoleService;
import run.halo.app.core.extension.service.UserService;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.AnonymousUserConst;
import run.halo.app.infra.properties.HaloProperties;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;
import run.halo.app.security.DefaultUserDetailService;
import run.halo.app.security.DelegatingOauthAuthenticationSuccessHandler;
import run.halo.app.security.DynamicMatcherSecurityWebFilterChain;
import run.halo.app.security.OauthClientRegistrationRepository;
import run.halo.app.security.SuperAdminInitializer;
import run.halo.app.security.authentication.SecurityConfigurer;
import run.halo.app.security.authorization.RequestInfoAuthorizationManager;

/**
 * Security configuration for WebFlux.
 *
 * @author johnniang
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebServerSecurityConfig {

    @Bean(name = "apiSecurityFilterChain")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityWebFilterChain apiFilterChain(ServerHttpSecurity http,
        RoleService roleService,
        ObjectProvider<SecurityConfigurer> securityConfigurers,
        ServerSecurityContextRepository securityContextRepository,
        DelegatingOauthAuthenticationSuccessHandler delegatingOauthAuthenticationSuccessHandler,
        ExtensionGetter extensionGetter) {

        http.securityMatcher(
                pathMatchers("/api/**", "/apis/**", "/login", "/login/**", "/oauth2/**", "/logout",
                    "/actuator/**"))
            .authorizeExchange().anyExchange()
            .access(new RequestInfoAuthorizationManager(roleService)).and()
            .anonymous(spec -> {
                spec.authorities(AnonymousUserConst.Role);
                spec.principal(AnonymousUserConst.PRINCIPAL);
            })
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(delegatingOauthAuthenticationSuccessHandler)
            )
            .securityContextRepository(securityContextRepository)
            .formLogin(withDefaults())
            .logout(withDefaults())
            .httpBasic(withDefaults());

        // Integrate with other configurers separately
        securityConfigurers.orderedStream()
            .forEach(securityConfigurer -> securityConfigurer.configure(http));
        return new DynamicMatcherSecurityWebFilterChain(extensionGetter, http.build());
    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository(
        ReactiveExtensionClient client) {
        return new OauthClientRegistrationRepository(client);
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(
        ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(
        ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(
            authorizedClientService);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    SecurityWebFilterChain portalFilterChain(ServerHttpSecurity http,
        ServerSecurityContextRepository securityContextRepository) {
        var pathMatcher = pathMatchers(HttpMethod.GET, "/**");
        var mediaTypeMatcher = new MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML);
        mediaTypeMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
        http.securityMatcher(new AndServerWebExchangeMatcher(pathMatcher, mediaTypeMatcher))
            .authorizeExchange().anyExchange().permitAll().and()
            .securityContextRepository(securityContextRepository)
            .headers()
            .frameOptions().mode(SAMEORIGIN)
            .referrerPolicy().policy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN).and()
            .cache().disable().and()
            .anonymous(spec -> spec.authenticationFilter(
                new HaloAnonymousAuthenticationWebFilter("portal", AnonymousUserConst.PRINCIPAL,
                    AuthorityUtils.createAuthorityList(AnonymousUserConst.Role),
                    securityContextRepository)));
        return http.build();
    }

    @Bean
    ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    ReactiveUserDetailsService userDetailsService(UserService userService,
        RoleService roleService) {
        return new DefaultUserDetailService(userService, roleService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(name = "halo.security.initializer.disabled",
        havingValue = "false",
        matchIfMissing = true)
    SuperAdminInitializer superAdminInitializer(ReactiveExtensionClient client,
        HaloProperties halo) {
        return new SuperAdminInitializer(client, passwordEncoder(),
            halo.getSecurity().getInitializer());
    }
}
