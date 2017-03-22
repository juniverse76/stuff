package xyz.juniverse.stuff.crypt;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES128 암호화/복호화
 * @author Administrator
 *
 */
public class Aes128Crypt extends Crypt {
	
	private final String characterEncoding = "UTF-8";
	private final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final String aesEncryptionAlgorithm = "AES";

	public Aes128Crypt(String encKey, String decKey) {
		super(encKey, decKey);
	}

	private byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		
		return cipherText;
	}

	private byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		
		return plainText;
	}

	private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[16];
		byte[] parameterKeyBytes = key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
				Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	/**
	 * 암호화
	 * @param plainText
	 * @param key
	 * @return
	 */
	@Override
	public String encrypt(String plainText, String key){
		String result = null;
		try {
			byte[] plainTextbytes = plainText.getBytes(characterEncoding);
			byte[] keyBytes = getKeyBytes(key);
			result = Base64.encodeBase64String(encrypt(plainTextbytes,
					keyBytes, keyBytes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 복호화
	 * @param encryptedText
	 * @param key
	 * @return
	 */
	@Override
	public String decrypt(String encryptedText, String key){
		String result = null;
		try {
			byte[] cipheredBytes = Base64.decodeBase64(encryptedText);
			byte[] keyBytes = getKeyBytes(key);
			result = new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
