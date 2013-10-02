package com.uslunchbox.restaurant.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DataEncryption {
	
	private static final String encryptionstring = "abcdefgh";
	private static final String ivparameter = "abcdefgh";
	private static final String des = "DES/CBC/PKCS5Padding";
	
	private static byte[] encryptByteData(byte[] SourceData) throws Exception {
		byte[] retByte = null;
		
		// Create SecretKey object
		byte[] encryptionByte = encryptionstring.getBytes();
		DESKeySpec dks = new DESKeySpec(encryptionByte);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		//Create IvParameterSpec object with initialization vector
		IvParameterSpec spec = new IvParameterSpec(ivparameter.getBytes());

		//Create Cipter object
		Cipher cipher = Cipher.getInstance(des);

		//Initialize Cipher object
		cipher.init(Cipher.ENCRYPT_MODE, securekey, spec);

		//Encrypting data
		retByte = cipher.doFinal(SourceData);
		return retByte;
	}

	private static byte[] decryptByteData(byte[] SourceData) throws Exception   {
		byte[] retByte = null;
		
		//Create SecretKey object
		byte[] encryptionByte = encryptionstring.getBytes();
		DESKeySpec dks = new DESKeySpec(encryptionByte);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		
		// Create IvParameterSpec object with initialization vector
		IvParameterSpec spec=new IvParameterSpec(ivparameter.getBytes());
		
		//Create Cipter object
		Cipher cipher = Cipher.getInstance(des);
		
		//Initialize Cipher object
		cipher.init(Cipher.DECRYPT_MODE, securekey, spec);
		
		//Decrypting data
		retByte = cipher.doFinal(SourceData);
		return retByte;
	}

	public static String encryptStringData(String SourceData) throws Exception {
		String retStr = null;
		byte[] retByte = null;
		//Transform SourceData to byte array
		byte[] sorData = SourceData.getBytes();
		//Encrypte data
		retByte = encryptByteData(sorData);
		//Encode encryption data
		BASE64Encoder be = new BASE64Encoder();
		retStr = be.encode(retByte);
		
		return retStr;
	}

	public static String decryptStringData(String SourceData) throws Exception {
		String retStr = null;
		byte[] retByte = null;
		//Decode encryption data
		BASE64Decoder bd = new BASE64Decoder();
		byte[] sorData = bd.decodeBuffer(SourceData);
		//Decrypting data
		retByte = decryptByteData(sorData);
		retStr = new String(retByte);
		
		return retStr;
	}

}
