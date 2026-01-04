package gov.nj.dhs.his.common.security;

/**
 * Shared constants for security configurations across all microservices.
 */
public final class SecurityConstants {

    /**
     * A whitelist of paths that should be publicly accessible without authentication.
     * This is primarily used for Swagger UI, API documentation, and the H2 database console.
     */
    public static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/h2-console/**",
            "/fallback/**" // Also include the gateway's fallback path
    };

    // Private constructor to prevent instantiation
    private SecurityConstants() {}
}
