package com.boot.mvc.encrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * PasswordEncoder Utility Class
 * 
 * This is a utility class to generate BCrypt encoded passwords
 * We use this to convert plain text passwords into secure BCrypt hashes
 * These hashes are then used in SecurityConfig for user authentication
 */
public class PasswordEncoder {

	public static void main(String[] args) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();   //creating BCrypt encoder object to encode passwords
		
		String pwd1 = encoder.encode("gupta");   //encoding plain password "gupta" for user "som", this will generate a BCrypt hash
		String pwd2 = encoder.encode("hyd");     //encoding plain password "hyd" for user "akash", this will generate a BCrypt hash
		String pwd3 = encoder.encode("ald");	 //encoding plain password "ald" for user "ajay", this will generate a BCrypt hash
		
		System.out.println(pwd1);   //printing encoded password for "gupta" - copy this hash and use in SecurityConfig
		System.out.println(pwd2);   //printing encoded password for "hyd" - copy this hash and use in SecurityConfig
		System.out.println(pwd3);   //printing encoded password for "ald" - copy this hash and use in SecurityConfig

	}
}

/*
 * 						How to Use This Class
 * 
 * Step 1: Run this class as Java application
 * Step 2: Copy the generated BCrypt hashes from console
 * Step 3: Paste these hashes in SecurityConfig.java
 * 
 * Example Output:
 * $2a$10$HdiYik9N/S.GsTOZnlaAVelq8BRfMsteMzp3Clf4EVYMGu8eMbbgO  (for "gupta")
 * $2a$10$XCnGIGDSdnDLZNUv6SYH/OAnS0of7mcm2JYZp0O0vCmRV1WV1OWU6  (for "hyd")
 * $2a$10$aj1Bi.ldWLdV2VXABbvouuWxXzzSy6BXt91mzQUTGWcUPQq4FMeqq  (for "ald")
 * 
 * 						Why BCrypt?
 * 
 * - BCrypt is a secure password hashing algorithm
 * - It cannot be reversed to get original password (one-way encryption)
 * - Same password generates different hash each time (because of salt)
 * - Industry standard for password security
 * - Recommended by Spring Security
 * 
 * 
 * 						How BCrypt Works
 * 
 * Plain Password: "gupta"
 *     ↓
 * BCrypt adds random salt
 *     ↓
 * Generates hash: $2a$10$HdiYik9N...
 *     ↓
 * Hash stored in database/config
 * 
 * When user logs in:
 * User enters: "gupta"
 *     ↓
 * Spring Security encodes it
 *     ↓
 * Compares with stored hash
 *     ↓
 * If match → Login success
 * If no match → Login failed
 * 
 * 
 * 						BCrypt Hash Structure
 * 
 * $2a$10$HdiYik9N/S.GsTOZnlaAVelq8BRfMsteMzp3Clf4EVYMGu8eMbbgO
 * 
 * $2a  → BCrypt algorithm version
 * $10  → Cost factor (number of rounds, higher = more secure but slower)
 * $HdiYik9N/S.GsTOZnlaAVe  → Salt (random data added to password)
 * lq8BRfMsteMzp3Clf4EVYMGu8eMbbgO  → Actual hash
 * 
 * 
 * 						Important Notes
 * 
 * 1. Each time you run this class, you get DIFFERENT hashes for same password
 *    Why? Because BCrypt uses random salt each time
 * 
 * 2. Users still type plain password ("gupta") to login
 *    Spring Security handles the encoding and comparison
 * 
 * 3. Never store plain passwords in production
 *    Always use BCrypt or similar encoding
 * 
 * 4. Cost factor 10 is good balance between security and performance
 *    Higher cost = more secure but slower login
 * 
 * 5. These hashes are used in SecurityConfig like this:
 *    .withUser("som").password("$2a$10$HdiYik9N...").roles("USER")
 * 
 */