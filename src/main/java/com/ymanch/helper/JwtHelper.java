package com.ymanch.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ymanch.exception.AuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtHelper {
	// private String SECRET_KEY = "my-very-secret-and-long-key-which-is-32-bytes";
	@Value("${jwt.privateKey.path}")
	private String privateKeyPath; // RSA private key from application.properties

	@Value("${jwt.publicKey.path}")
	private String publicKeyPath; // RSA public key from application.properties

	// Updated private key method that properly reads and decodes the private key
	/*
	 * private RSAPrivateKey getPrivateKey() { try { // Read the PEM file content
	 * and remove the BEGIN/END markers StringBuilder pemContent = new
	 * StringBuilder(); try (BufferedReader reader = new BufferedReader(new
	 * FileReader(privateKeyPath))) { String line; while ((line = reader.readLine())
	 * != null) { if (!line.startsWith("-----")) { // Skip BEGIN/END lines
	 * pemContent.append(line); } } }
	 * 
	 * // Decode the Base64 content of the private key byte[] privateKeyBytes =
	 * java.util.Base64.getDecoder().decode(pemContent.toString());
	 * 
	 * // Create the private key from PKCS#8 format PKCS8EncodedKeySpec keySpec =
	 * new PKCS8EncodedKeySpec(privateKeyBytes); KeyFactory keyFactory =
	 * KeyFactory.getInstance("RSA"); return (RSAPrivateKey)
	 * keyFactory.generatePrivate(keySpec); } catch (Exception e) { throw new
	 * AuthenticationException("Error loading private key: " + e.getMessage()); } }
	 */
	private RSAPrivateKey getPrivateKey() throws AuthenticationException {
	    try {
	        // Read the entire PEM file content
	        String pemContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
	        
	        // Extract the Base64 content between markers
	        String privateKeyPEM = pemContent
	            .replace("-----BEGIN PRIVATE KEY-----", "")
	            .replace("-----END PRIVATE KEY-----", "")
	            .replaceAll("\\s", "");  // Remove all whitespace, including newlines

	        // Decode the Base64 content
	        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

	        // Create the private key from PKCS#8 format
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	    } catch (Exception e) {
	        throw new AuthenticationException("Error loading private key: " + e.getMessage(), e);
	    }
	}


	// Updated public key method that properly reads and decodes the public key
	private RSAPublicKey getPublicKey() {
		 try {
		        // Read the public key file content and remove the BEGIN/END markers
		        File publicKeyFile = new File(publicKeyPath);
		        if (!publicKeyFile.exists()) {
		            throw new AuthenticationException("Public key file not found at path: " + publicKeyPath);
		        }

		        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

		        // Clean the public key content (remove BEGIN/END lines)
		        String keyContent = new String(publicKeyBytes).replaceAll("\\n", "").replaceAll("-----\\w+ PUBLIC KEY-----", "");

		        // Decode the Base64 content
		        byte[] decodedKey = java.util.Base64.getDecoder().decode(keyContent);

		        // Create the RSA public key from the decoded bytes (X.509 format)
		        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
		        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		    } catch (Exception e) {
		        throw new AuthenticationException("Error loading public key: " + e.getMessage());
		    }
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		return extractClaim(token, claims -> Long.valueOf(claims.get("userId").toString()));
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(getPublicKey()) // Use public key for verification
					.build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			throw new AuthenticationException("Invalid JWT token: " + e.getMessage());
		}
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails, Long userId, String userStatus) {

		Map<String, Object> claims = new HashMap<>();
		if ("IN-ACTIVE".equalsIgnoreCase(userStatus)) {
			throw new AuthenticationException("User account is IN-ACTIVE. Token cannot be generated.");
		}
		claims.put("userId", userId);
		if (userDetails.getUsername() == null || userDetails.getUsername().isBlank()
				|| userDetails.getUsername().isEmpty()) {
			return createToken(claims, userDetails.getPassword());
		}
		return createToken(claims, userDetails.getUsername());
	}

	// Create JWT token using RS256 and private key for signing
	private String createToken(Map<String, Object> claims, String subject) {
		System.out.println("Creating token with claims: " + claims);
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 15)) // 15 days expiration
				.signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
