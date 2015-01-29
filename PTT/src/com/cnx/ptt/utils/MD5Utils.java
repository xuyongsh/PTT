package com.cnx.ptt.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * MD5 �����㷨
	 * 
	 * @param String
	 *            str
	 * @return String md5(str)
	 */
	public static String md5(String ss) {
		// �õ�һ����ϢժҪ��
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");

			byte[] result = digest.digest(ss.getBytes());
			StringBuffer buffer = new StringBuffer();
			// ��ÿһ��byte ��һ��������
			for (byte b : result) {
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
