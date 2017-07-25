package com.bolo1.mobilesafe1.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

public class Md5Util {
public  static String encoder(String psd) {
	  try {
		  psd=psd+"mobilesafe";
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] bs =digest.digest(psd.getBytes());
		StringBuffer stringbuffer = new StringBuffer();
		for(byte b :bs){
			int i= b&0xff;
		String hexstring=Integer.toHexString(i);
		if(hexstring.length()<2){
			hexstring="0"+hexstring;
		}
			stringbuffer.append(hexstring);
		}
		return stringbuffer.toString();
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	return "";
}
}
