package api.kun.uz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SpringConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationFilter jwtTokenFilter;

    public static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/api/v1/attach/download/**",
            "/api/v1/attach/open/**",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    public static final String[] AUTH_MODERATOR = {
            "/api/v1/article/private-moderator/filter",
            "/api/v1/article/create",
            "/api/v1/article/update/*",
            "/api/v1/article/delete/*",
    };

    public static final String[] AUTH_PUBLISHER = {
            "api/v1/article/update/status/*",
            "/api/v1/article/private-publisher/filter",

    };
    public static final String[] AUTH_ADMIN = {
            "/api/v1/attach/pagination",
            "/api/v1/attach//delete/*",
            "/api/v1/category/**",
            "/api/v1/comment/",
            "/api/v1/comment/filter",
            "/api/v1/email-history/admin",
            "/api/v1/profile/admin/**",
            "/api/v1/region/**",
            "/api/v1/section/**",
            "/api/v1/sms-history/admin",
            "/api/v1/tag/all",

    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // authentication (login,password)
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // authorization
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .requestMatchers(AUTH_PUBLISHER).hasRole("PUBLISHER")
                    .requestMatchers(AUTH_MODERATOR).hasRole("MODERATOR")
                    .requestMatchers(AUTH_ADMIN).hasRole("ADMIN")
                    .anyRequest()
                    .authenticated();
        }).addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        ;

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
