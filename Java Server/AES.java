// Della Matera Lorenzo 5E

import java.security.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;

public class AES {
	
	public static String encrypt(String input, SecretKey key, IvParameterSpec iv)
	    	throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
	    
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
	    byte[] cipherText = cipher.doFinal(input.getBytes());
	    return Base64.getEncoder().encodeToString(cipherText);
	}
	
	public static String decrypt(String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
	    
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, key, iv);
	    byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
	    return new String(plainText);
	}

	public static byte[] getIV(){
		final byte[] iv = new byte[16];
		final SecureRandom rnd = new SecureRandom();
		rnd.nextBytes(iv);
		return iv;
	}

}
