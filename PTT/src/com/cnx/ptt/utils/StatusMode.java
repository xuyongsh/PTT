package com.cnx.ptt.utils;

import com.cnx.ptt.R;


public enum StatusMode {
	offline(R.string.status_offline, -1), // ����״̬��û��ͼ��
	dnd(R.string.status_dnd, R.drawable.status_shield), // �������
	xa(R.string.status_xa, R.drawable.status_invisible), // ����
	away(R.string.status_away, R.drawable.status_leave), // �뿪
	available(R.string.status_online, R.drawable.status_online), // ����
	chat(R.string.status_chat, R.drawable.status_qme);// Q�Ұ�

	private final int textId;
	private final int drawableId;

	StatusMode(int textId, int drawableId) {
		this.textId = textId;
		this.drawableId = drawableId;
	}

	public int getTextId() {
		return textId;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public String toString() {
		return name();
	}

	public static StatusMode fromString(String status) {
		return StatusMode.valueOf(status);
	}

}
