package com.cnx.ptt.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * MD5 加密算法
	 * 
	 * @param String
	 *            str
	 * @return String md5(str)
	 */
	public static String md5(String ss) {
		// 得到一个信息摘要器
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");

			byte[] result = digest.digest(ss.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 把每一个byte 做一个与运算
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
