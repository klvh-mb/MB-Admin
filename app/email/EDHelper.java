package email;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class EDHelper {
	
	private static String secret_key = "zYzZvDROfx2Rpe2CaMCpew==";
	
	public static String doEncryption(String data) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException{
		
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] secretKeyAsBytes = base64Decoder.decodeBuffer(secret_key);
		SecretKey _secretKey = new SecretKeySpec(secretKeyAsBytes, 0, secretKeyAsBytes.length, "AES");
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE,_secretKey);

		byte[] byteDataToEncrypt = base64Decoder.decodeBuffer(data);
		byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
		
		return new BASE64Encoder().encode(byteCipherText);
	}
	
	public static String doDecryption(String data) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] secretKeyAsBytes = base64Decoder.decodeBuffer(secret_key);
		SecretKey _secretKey = new SecretKeySpec(secretKeyAsBytes, 0, secretKeyAsBytes.length, "AES");
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE,_secretKey);
		
		byte[] byteDecryptedText = aesCipher.doFinal(base64Decoder.decodeBuffer(data));
		return new BASE64Encoder().encode(byteDecryptedText);
	}
}
