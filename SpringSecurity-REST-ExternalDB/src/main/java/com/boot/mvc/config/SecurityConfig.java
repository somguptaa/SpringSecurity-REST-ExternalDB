package com.boot.mvc.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration Class for Bank Operations REST API
 * 
 * This class configures Spring Security for the bank application with the following features:
 * - Form-based authentication with JSON responses
 * - Role-based access control (USER, MANAGER)
 * - Database authentication using JDBC
 * - BCrypt password encryption
 * - Custom JSON error handling
 * 
 */
@Configuration              // Marks this class as a source of bean definitions
@EnableWebSecurity          // Enables Spring Security's web security support
@EnableMethodSecurity(prePostEnabled = true)  // Enables method-level security annotations like @PreAuthorize
public class SecurityConfig {
    
    /**
     * Constructor - Prints confirmation message when Spring loads this configuration.
     * Helps verify that the security configuration is being properly initialized.
     */
    public SecurityConfig() {
        System.out.println("================================");
        System.out.println("SecurityConfig is being loaded!");
        System.out.println("================================");
    }
    
    /**
     * Configures the Security Filter Chain for HTTP requests.
     * This is the main security configuration method that defines:
     * - Which URLs are public vs protected
     * - What roles can access which endpoints
     * - How authentication works (form login)
     * - How to handle errors (JSON responses)
     * 
     * @param http HttpSecurity object to configure security rules
     * @return SecurityFilterChain - the built security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        System.out.println("Configuring Security Filter Chain for REST API with Form Login...");
        
        http
            // CSRF Protection Configuration
            // Disabled for REST API to make testing easier
            // In production, you may want to enable CSRF with proper token handling
            .csrf(csrf -> csrf.disable())
            
            // Authorization Rules - Define which URLs require what permissions
            .authorizeHttpRequests(authorize -> {
                authorize
                    // Public Endpoints - No authentication required
                    .requestMatchers("/bank/home").permitAll()
                    
                    // Authenticated Endpoints - Any logged-in user can access
                    .requestMatchers("/bank/offers").authenticated()
                    
                    // Role-Based Endpoints - Requires specific roles
                    // USER or MANAGER can check balance
                    .requestMatchers("/bank/checkBalance").hasAnyRole("USER", "MANAGER")
                    
                    // Only MANAGER can approve loans
                    .requestMatchers("/bank/approveloan").hasRole("MANAGER")
                    
                    // Access denied page - Public so error can be shown
                    .requestMatchers("/bank/denied").permitAll()
                    
                    // All other requests require authentication
                    .anyRequest().authenticated();
            })
            
            // Form Login Configuration
            // Enables traditional username/password login with custom JSON responses
            .formLogin(form -> form
                .permitAll()  // Everyone can access the login page
                
                // Success Handler - Executes when login is successful
                // Returns JSON response instead of redirecting to a page
                .successHandler((request, response, authentication) -> {
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"message\":\"Login successful\"," +
                        "\"status\":\"success\"," +
                        "\"user\":\"" + authentication.getName() + "\"}"
                    );
                })
                
                // Failure Handler - Executes when login fails
                // Returns JSON error message instead of redirecting to error page
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"message\":\"Login failed: " + exception.getMessage() + "\"," +
                        "\"status\":\"error\"}"
                    );
                })
            )
            
            // Logout Configuration
            // Handles user logout with custom JSON response
            .logout(logout -> logout
                .permitAll()  // Everyone can logout
                
                // Logout Success Handler - Returns JSON confirmation
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"message\":\"Logout successful\"," +
                        "\"status\":\"success\"}"
                    );
                })
            )
            
            // Exception Handling Configuration
            // Handles security-related errors
            .exceptionHandling(exception -> exception
                // Access Denied Handler - Executes when user lacks required role
                // Example: USER trying to access MANAGER-only endpoint
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"message\":\"Access Denied! You don't have permission to access this resource\"," +
                        "\"status\":\"error\"}"
                    );
                })
            );
        
        System.out.println("Security Filter Chain configured successfully for REST API!");
        
        return http.build();  // Build and return the configured security filter chain
    }
    
    /**
     * Creates and configures the UserDetailsManager bean.
     * This manages user authentication by loading user details from the database.
     * 
     * Uses JDBC to connect to database and retrieve:
     * - Username
     * - Password (encrypted)
     * - Roles/Authorities
     * 
     * Expected database tables:
     * - users (username, password, enabled)
     * - authorities (username, authority/role)
     * 
     * @param dataSource DataSource bean (auto-configured by Spring Boot)
     * @return UserDetailsManager instance for database authentication
     */
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        System.out.println("Creating UserDetailsManager with DataSource...");
        
        // JdbcUserDetailsManager uses standard SQL queries to load users from database
        return new JdbcUserDetailsManager(dataSource);
    }
    
    /**
     * Creates the Password Encoder bean.
     * BCrypt is a strong hashing algorithm that:
     * - Generates different hashes for the same password (uses salt)
     * - Is computationally expensive to slow down brute-force attacks
     * - Is one-way (cannot decrypt the hash back to original password)
     * 
     * When users register: plain password → BCrypt → encrypted hash stored in DB
     * When users login: entered password → BCrypt → compared with DB hash
     * 
     * @return PasswordEncoder instance using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Creating BCryptPasswordEncoder...");
        
        // BCryptPasswordEncoder provides industry-standard password encryption
        return new BCryptPasswordEncoder();
    }
}




/**
 * SECURITY FLOW EXPLANATION:
 * 
 * 1. USER TRIES TO ACCESS PROTECTED ENDPOINT
 *    Example: GET /bank/checkBalance
 *    
 * 2. SPRING SECURITY INTERCEPTS REQUEST
 *    - Checks if user is authenticated
 *    - If not authenticated → redirects to login page
 *    
 * 3. USER SUBMITS LOGIN FORM
 *    - Username and password sent to /login (default Spring Security endpoint)
 *    - UserDetailsManager loads user from database
 *    - PasswordEncoder compares entered password with DB hash
 *    
 * 4. AUTHENTICATION SUCCESS/FAILURE
 *    Success: successHandler returns JSON with user info
 *    Failure: failureHandler returns JSON with error message
 *    
 * 5. AUTHORIZATION CHECK (for authenticated users)
 *    - SecurityFilterChain checks user's roles
 *    - @PreAuthorize annotations in controller also checked
 *    - If user has required role → allow access
 *    - If user lacks required role → accessDeniedHandler returns JSON error
 *    
 * 6. REQUEST PROCESSED
 *    - Controller method executes
 *    - Returns JSON response to client
 */

/**
 * ROLE HIERARCHY:
 * 
 * MANAGER (highest privilege)
 *   ✓ Can approve loans (/bank/approveloan)
 *   ✓ Can check balance (/bank/checkBalance)
 *   ✓ Can view offers (/bank/offers)
 *   ✓ Can access home (/bank/home)
 * 
 * USER (standard user)
 *   ✗ Cannot approve loans
 *   ✓ Can check balance (/bank/checkBalance)
 *   ✓ Can view offers (/bank/offers)
 *   ✓ Can access home (/bank/home)
 * 
 * ANONYMOUS (not logged in)
 *   ✗ Cannot approve loans
 *   ✗ Cannot check balance
 *   ✗ Cannot view offers
 *   ✓ Can access home (/bank/home)
 */

/**
 * DATABASE SCHEMA REQUIRED:
 * 
 * Table: users
 * +----------+--------------+----------+
 * | username | password     | enabled  |
 * +----------+--------------+----------+
 * | som      | $2a$10$...   | true     |
 * | akash    | $2a$10$...   | true     |
 * | ajay     | $2a$10$...   | true     |
 * +----------+--------------+----------+
 * 
 * Table: authorities
 * +----------+----------------------------+
 * | username | authority                  |
 * +----------+----------------------------+
 * | som      | ROLE_USER, ROLE_MANAGER    |
 * | akash    | ROLE_MANAGER               |
 * | akash    | ROLE_VISTOR                |
 * +----------+----------------------------+
 * 
 * Note: Passwords are BCrypt hashed, not plain text
 * Note: Roles must be prefixed with "ROLE_" in database
 */