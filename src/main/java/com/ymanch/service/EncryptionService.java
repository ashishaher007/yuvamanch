package com.ymanch.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class EncryptionService {

	private final Map<String, SecretKey> keyStore = new HashMap<>();
	private static final String ALGORITHM = "AES";

	// Generate and encode a new AES key
	public SecretKey generateKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		keyGen.init(128); // AES-128
		return keyGen.generateKey();
	}

	// Encode the key to Base64
	public String encodeKeyToBase64(SecretKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	// Decode Base64 encoded key to SecretKey
	public SecretKey decodeKeyFromBase64(String base64Key) {
		byte[] keyBytes = Base64.getDecoder().decode(base64Key);
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	// Encrypt data using the provided key
	public String encrypt(String valueToEnc, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedValue = cipher.doFinal(valueToEnc.getBytes());
		return Base64.getEncoder().encodeToString(encryptedValue);
	}

	// Decrypt data using the provided key

	public String decrypt(String encryptedValue, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
		byte[] decryptedValue = cipher.doFinal(decodedValue);
		return new String(decryptedValue);
	}

	// Store a key

	public void storeKey(String id, SecretKey key) {
		keyStore.put(id, key);
	}

	// Retrieve a key
	public SecretKey getKey(String id) {
		return keyStore.get(id);
	}
}
