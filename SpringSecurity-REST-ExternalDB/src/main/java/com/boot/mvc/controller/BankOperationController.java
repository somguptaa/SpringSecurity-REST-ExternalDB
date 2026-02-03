package com.boot.mvc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Bank Operations REST API Controller
 * 
 * This controller handles all bank-related operations and provides REST API endpoints
 * with different security levels:
 * - Public endpoints (accessible to everyone)
 * - Authenticated endpoints (requires login)
 * - Role-based endpoints (requires specific roles: USER or MANAGER)
 * 
 * All endpoints return JSON responses instead of HTML pages.
 * 
 * Security is enforced at two levels:
 * 1. SecurityConfig class (URL-based security)
 * 2. @PreAuthorize annotations (method-level security)
 * 
 * Base URL: /bank
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@RestController                    // Marks this class as a REST controller (returns JSON, not views)
@RequestMapping("/bank")           // Base URL path for all endpoints in this controller
public class BankOperationController {
	
    /**
     * Home Endpoint - Public Access
     * 
     * This is a public endpoint accessible to everyone (authenticated or not).
     * Typically used for landing page or welcome message.
     * 
     * Security Level: PUBLIC (No authentication required)
     * 
     * URL: GET /bank/home
     * 
     * Success Response:
     * {
     *   "message": "Welcome to the Bank!",
     *   "status": "success"
     * }
     * 
     * @return ResponseEntity containing welcome message in JSON format
     */
    @GetMapping("/home")
    public ResponseEntity<Map<String, String>> showHome() {
        // Create a map to hold the JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to the Bank!");
        response.put("status", "success");
        
        // Return HTTP 200 OK with JSON body
        return ResponseEntity.ok(response);
    }
    
    /**
     * Offers Endpoint - Authenticated Users Only
     * 
     * This endpoint shows current bank offers to authenticated users.
     * Any logged-in user (regardless of role) can access this.
     * 
     * Security Level: AUTHENTICATED (Requires login, any role)
     * 
     * URL: GET /bank/offers
     * 
     * Success Response (HTTP 200):
     * {
     *   "message": "Current offers available for authenticated users",
     *   "status": "success"
     * }
     * 
     * Error Response (HTTP 401 if not logged in):
     * {
     *   "message": "Login failed: ...",
     *   "status": "error"
     * }
     * 
     * @return ResponseEntity containing offers message in JSON format
     */
    @GetMapping("/offers")
    public ResponseEntity<Map<String, String>> showOffers() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Current offers available for authenticated users");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check Balance Endpoint - USER or MANAGER Role Required
     * 
     * This endpoint allows users to check their account balance.
     * Only users with USER or MANAGER role can access this endpoint.
     * 
     * Security Level: ROLE-BASED (Requires ROLE_USER or ROLE_MANAGER)
     * 
     * The @PreAuthorize annotation provides method-level security.
     * It checks if the authenticated user has the required role before executing the method.
     * 
     * URL: GET /bank/checkBalance
     * 
     * Success Response (HTTP 200):
     * {
     *   "message": "Your balance information",
     *   "balance": 50000.00,
     *   "accountNumber": "XXXX1234",
     *   "status": "success"
     * }
     * 
     * Error Response (HTTP 403 if user lacks required role):
     * {
     *   "message": "Access Denied! You don't have permission to access this resource",
     *   "status": "error"
     * }
     * 
     * @return ResponseEntity containing balance information in JSON format
     */
    @GetMapping("/checkBalance")
    @PreAuthorize("hasAnyRole('USER','MANAGER')")  // Method-level security check
    public ResponseEntity<Map<String, Object>> showBalance() {
        // Create response with multiple data types (String and Number)
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Your balance information");
        response.put("balance", 50000.00);                    // Sample balance
        response.put("accountNumber", "XXXX1234");            // Masked account number
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Loan Approval Endpoint - MANAGER Role Only
     * 
     * This endpoint allows managers to approve loan applications.
     * Only users with MANAGER role can access this endpoint.
     * Regular users (ROLE_USER) will get access denied error.
     * 
     * Security Level: ROLE-BASED (Requires ROLE_MANAGER only)
     * 
     * The @PreAuthorize annotation ensures only MANAGER can execute this method.
     * This is typically used for administrative or privileged operations.
     * 
     * URL: GET /bank/approveloan
     * 
     * Success Response (HTTP 200):
     * {
     *   "message": "Loan approval page - Manager access only",
     *   "status": "success"
     * }
     * 
     * Error Response (HTTP 403 if user is not MANAGER):
     * {
     *   "message": "Access Denied! You don't have permission to access this resource",
     *   "status": "error"
     * }
     * 
     * @return ResponseEntity containing loan approval message in JSON format
     */
    @GetMapping("/approveloan")
    @PreAuthorize("hasRole('MANAGER')")  // Strict role check - only MANAGER allowed
    public ResponseEntity<Map<String, String>> loanApprove() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Loan approval page - Manager access only");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Access Denied Endpoint
     * 
     * This endpoint is called when a user tries to access a resource
     * they don't have permission for. Returns a proper error message.
     * 
     * Security Level: PUBLIC (So the error can be displayed to anyone)
     * 
     * URL: GET /bank/denied
     * 
     * Response (HTTP 403 Forbidden):
     * {
     *   "message": "Access Denied! You don't have permission to access this resource",
     *   "status": "error"
     * }
     * 
     * Note: This endpoint is also triggered by the accessDeniedHandler
     * in SecurityConfig when authorization fails.
     * 
     * @return ResponseEntity containing access denied message with HTTP 403 status
     */
    @GetMapping("/denied")
    public ResponseEntity<Map<String, String>> accessDenied() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Access Denied! You don't have permission to access this resource");
        response.put("status", "error");
        
        // Return HTTP 403 FORBIDDEN status with error message
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}



/**
 * ============================================================================
 * ENDPOINT ACCESS MATRIX
 * ============================================================================
 * 
 * Endpoint             | Anonymous | USER  | MANAGER | HTTP Method
 * ---------------------|-----------|-------|-------- |-------------
 * /bank/home           |    ✓      |   ✓   |    ✓    |    GET
 * /bank/offers         |    ✗      |   ✓   |    ✓    |    GET
 * /bank/checkBalance   |    ✗      |   ✓   |    ✓    |    GET
 * /bank/approveloan    |    ✗      |   ✗   |    ✓    |    GET
 * /bank/denied         |    ✓      |   ✓   |    ✓    |    GET
 * 
 * Legend:
 * ✓ = Access Allowed
 * ✗ = Access Denied (will return 401 or 403 error)
 * 
 * ============================================================================
 */

/**
 * ============================================================================
 * SECURITY ANNOTATIONS EXPLAINED
 * ============================================================================
 * 
 * @RestController:
 * - Combines @Controller and @ResponseBody
 * - All methods automatically return JSON (not HTML views)
 * - Spring converts return values to JSON using Jackson
 * 
 * @RequestMapping("/bank"):
 * - Sets base path for all endpoints in this controller
 * - All URLs will start with /bank
 * 
 * @GetMapping:
 * - Maps HTTP GET requests to handler methods
 * - Shortcut for @RequestMapping(method = RequestMethod.GET)
 * 
 * @PreAuthorize:
 * - Method-level security annotation
 * - Checked BEFORE method execution
 * - Uses Spring Expression Language (SpEL)
 * - Common expressions:
 *   - hasRole('ROLE_NAME') - checks for single role
 *   - hasAnyRole('ROLE1','ROLE2') - checks for any of the roles
 *   - isAuthenticated() - checks if user is logged in
 *   - permitAll() - allows everyone
 * 
 * ============================================================================
 */

/**
 * ============================================================================
 * RESPONSE ENTITY EXPLAINED
 * ============================================================================
 * 
 * ResponseEntity is a wrapper that allows you to:
 * 1. Set HTTP status code (200, 403, 404, 500, etc.)
 * 2. Set response headers
 * 3. Set response body (the actual data)
 * 
 * Common Response Patterns:
 * 
 * 1. Success Response:
 *    return ResponseEntity.ok(data);
 *    // Returns HTTP 200 with data
 * 
 * 2. Created Response:
 *    return ResponseEntity.status(HttpStatus.CREATED).body(data);
 *    // Returns HTTP 201 with data
 * 
 * 3. Error Response:
 *    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
 *    // Returns HTTP 403 with error message
 * 
 * 4. No Content Response:
 *    return ResponseEntity.noContent().build();
 *    // Returns HTTP 204 with no body
 * 
 * ============================================================================
 */

/**
 * ============================================================================
 * TESTING THE API
 * ============================================================================
 * 
 * Using cURL:
 * 
 * 1. Test public endpoint (no login):
 *    curl http://localhost:8080/bank/home
 * 
 * 2. Login first:
 *    curl -X POST http://localhost:8080/login \
 *         -d "username=john&password=password123" \
 *         -c cookies.txt
 * 
 * 3. Test authenticated endpoint:
 *    curl http://localhost:8080/bank/offers -b cookies.txt
 * 
 * 4. Test role-based endpoint (as USER):
 *    curl http://localhost:8080/bank/checkBalance -b cookies.txt
 * 
 * 5. Test manager-only endpoint (will fail if logged in as USER):
 *    curl http://localhost:8080/bank/approveloan -b cookies.txt
 * 
 * Using Postman:
 * 1. POST to http://localhost:8080/login with form data
 * 2. Postman will save cookies automatically
 * 3. Make GET requests to protected endpoints
 * 
 * Using Browser:
 * 1. Navigate to http://localhost:8080/bank/home (works without login)
 * 2. Navigate to http://localhost:8080/bank/offers (redirects to login)
 * 3. Login and access protected resources
 * 
 * ============================================================================
 */

/**
 * ============================================================================
 * HTTP STATUS CODES USED
 * ============================================================================
 * 
 * 200 OK - Request successful, data returned
 *          Used by: home, offers, checkBalance, approveloan
 * 
 * 401 UNAUTHORIZED - Authentication required or failed
 *                    Returned when: user not logged in
 * 
 * 403 FORBIDDEN - Authenticated but lacks permission
 *                 Returned when: logged in but wrong role
 *                 Used by: denied endpoint
 * 
 * 404 NOT FOUND - Endpoint doesn't exist
 *                 Returned when: wrong URL
 * 
 * 500 INTERNAL SERVER ERROR - Server-side error
 *                             Returned when: exception in code
 * 
 * ============================================================================
 */

/**
 * ============================================================================
 * BEST PRACTICES IMPLEMENTED
 * ============================================================================
 * 
 * 1. ✓ Consistent JSON Response Structure
 *    - Every response has "message" and "status" fields
 *    - Makes client-side parsing easier
 * 
 * 2. ✓ Proper HTTP Status Codes
 *    - 200 for success
 *    - 403 for access denied
 *    - Clear indication of request outcome
 * 
 * 3. ✓ Descriptive Endpoint Names
 *    - /home, /offers, /checkBalance, /approveloan
 *    - Self-explanatory URLs
 * 
 * 4. ✓ RESTful Design
 *    - Uses HTTP methods correctly (GET for retrieval)
 *    - Stateless communication
 *    - Resource-based URLs
 * 
 * 5. ✓ Security by Design
 *    - Multiple layers of security
 *    - Principle of least privilege
 *    - Clear role separation
 * 
 * 6. ✓ Error Handling
 *    - Dedicated error endpoint
 *    - Clear error messages
 *    - Proper status codes
 * 
 * ============================================================================
 */