package com.cnx.ptt.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cnx.ptt.chat.app.XXApp;
import com.cnx.ptt.chat.exception.XmppAdressMalformedException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;


public class XMPPHelper {
	private static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

	public static void verifyJabberID(String jid)
			throws XmppAdressMalformedException {
		if (jid != null) {
			Pattern p = Pattern
					.compile("(?i)[a-z0-9\\-_\\.]++@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");
			Matcher m = p.matcher(jid);

			if (!m.matches()) {
				throw new XmppAdressMalformedException(
						"Configured Jabber-ID is incorrect!");
			}
		} else {
			throw new XmppAdressMalformedException("Jabber-ID wasn't set!");
		}
	}

	public static void verifyJabberID(Editable jid)
			throws XmppAdressMalformedException {
		verifyJabberID(jid.toString());
	}

	public static int tryToParseInt(String value, int defVal) {
		int ret;
		try {
			ret = Integer.parseInt(value);
		} catch (NumberFormatException ne) {
			ret = defVal;
		}
		return ret;
	}

	public static int getEditTextColor(Context ctx) {
		TypedValue tv = new TypedValue();
		boolean found = ctx.getTheme().resolveAttribute(
				android.R.attr.editTextColor, tv, true);
		if (found) {
			// SDK 11+
			return ctx.getResources().getColor(tv.resourceId);
		} else {
			// SDK < 11
			return ctx.getResources().getColor(
					android.R.color.primary_text_light);
		}
	}

	public static String splitJidAndServer(String account) {
		if (!account.contains("@"))
			return account;
		String[] res = account.split("@");
		String userName = res[0];
		return userName;
	}

	/**
	 * �����ַ����еı���
	 * 
	 * @param context
	 * @param message
	 *            �������Ҫ�����String
	 * @param small
	 *            �Ƿ���ҪСͼƬ
	 * @return
	 */
	public static CharSequence convertNormalStringToSpannableString(
			Context context, String message, boolean small) {
		String hackTxt;
		if (message.startsWith("[") && message.endsWith("]")) {
			hackTxt = message + " ";
		} else {
			hackTxt = message;
		}
		SpannableString value = SpannableString.valueOf(hackTxt);

		Matcher localMatcher = EMOTION_URL.matcher(value);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
			int k = localMatcher.start();
			int m = localMatcher.end();
			if (m - k < 8) {
				if (XXApp.getInstance().getFaceMap().containsKey(str2)) {
					int face = XXApp.getInstance().getFaceMap().get(str2);
					Bitmap bitmap = BitmapFactory.decodeResource(
							context.getResources(), face);
					if (bitmap != null) {
						if (small) {
							int rawHeigh = bitmap.getHeight();
							int rawWidth = bitmap.getHeight();
							int newHeight = 30;
							int newWidth = 30;
							// ������������
							float heightScale = ((float) newHeight) / rawHeigh;
							float widthScale = ((float) newWidth) / rawWidth;
							// �½�������
							Matrix matrix = new Matrix();
							matrix.postScale(heightScale, widthScale);
							// ����ͼƬ����ת�Ƕ�
							// matrix.postRotate(-30);
							// ����ͼƬ����б
							// matrix.postSkew(0.1f, 0.1f);
							// ��ͼƬ��Сѹ��
							// ѹ����ͼƬ�Ŀ�͸��Լ�kB��С����仯
							bitmap = Bitmap.createBitmap(bitmap, 0, 0,
									rawWidth, rawHeigh, matrix, true);
						}
						ImageSpan localImageSpan = new ImageSpan(context,
								bitmap, ImageSpan.ALIGN_BASELINE);
						value.setSpan(localImageSpan, k, m,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return value;
	}

}
