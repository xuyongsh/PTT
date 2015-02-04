/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.cnx.ptt.chat.xlistview;

import com.cnx.ptt.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MsgHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public MsgHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MsgHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// ��ʼ�������������ˢ��view�߶�Ϊ0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.c_message_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);

	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // ��ʾ����
			mProgressBar.setVisibility(View.VISIBLE);
		} else { // ��ʾ��ͷͼƬ
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
			}
			if (mState == STATE_REFRESHING) {
			}
			mHintTextView.setVisibility(View.VISIBLE);
			mHintTextView.setText("��ʾ������Ϣ");
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mHintTextView.setVisibility(View.VISIBLE);
				mHintTextView.setText("�ͷż�����ʾ");
			}
			break;
		case STATE_REFRESHING:
			// mHintTextView.setText(R.string.xlistview_header_hint_loading);
			mHintTextView.setVisibility(View.GONE);
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
