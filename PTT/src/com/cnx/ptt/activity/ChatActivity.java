package com.cnx.ptt.activity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.cnx.ptt.Constants;
import com.cnx.ptt.R;
import com.cnx.ptt.adapter.ChatAdapter;
import com.cnx.ptt.adapter.FaceAdapter;
import com.cnx.ptt.adapter.FacePageAdeapter;
import com.cnx.ptt.chat.app.XXApp;
import com.cnx.ptt.chat.db.ChatProvider;
import com.cnx.ptt.chat.db.ChatProvider.ChatConstants;
import com.cnx.ptt.chat.db.RosterProvider;
import com.cnx.ptt.chat.service.IConnectionStatusCallback;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.swipeback.SwipeBackActivity;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
//import com.cnx.ptt.chat.view.CirclePageIndicator;
import com.cnx.ptt.chat.xlistview.MsgListView;
import com.cnx.ptt.chat.xlistview.MsgListView.IXListViewListener;
import com.cnx.ptt.utils.BroadcastHelper;
import com.cnx.ptt.utils.L;
import com.cnx.ptt.utils.T;
import com.cnx.ptt.utils.XMPPHelper;
import com.cnx.ptt.utils.StatusMode;

import android.R.layout;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;


public class ChatActivity extends SwipeBackActivity implements OnTouchListener,
		OnClickListener, IXListViewListener, IConnectionStatusCallback {
	public static final String INTENT_EXTRA_USERNAME = ChatActivity.class
			.getName() + ".username";// �ǳƶ�Ӧ��key
	private MsgListView mMsgListView;// �Ի�ListView
	private ViewPager mFaceViewPager;// ����ѡ��ViewPager
	private int mCurrentPage = 0;// ��ǰ����ҳ
	private boolean mIsFaceShow = false;// �Ƿ���ʾ����
	private Button mSendMsgBtn;// ������Ϣbutton
	private ImageButton mFaceSwitchBtn;// �л����̺ͱ����button
//	private TextView mTitleNameView;// ������
//	private ImageView mTitleStatusView;
	private EditText mChatEditText;// ��Ϣ�����
	private LinearLayout mFaceRoot;// ���鸸����
	private WindowManager.LayoutParams mWindowManagerParams;
	private InputMethodManager mInputMethodManager;
	private List<String> mFaceMapKeys;// �����Ӧ���ַ�������
	private String mWithJabberID = null;// ��ǰ�����û���ID

	private static final String[] PROJECTION_FROM = new String[] {
			ChatProvider.ChatConstants._ID, ChatProvider.ChatConstants.DATE,
			ChatProvider.ChatConstants.DIRECTION,
			ChatProvider.ChatConstants.JID, ChatProvider.ChatConstants.MESSAGE,
			ChatProvider.ChatConstants.DELIVERY_STATUS };// ��ѯ�ֶ�

	private ContentObserver mContactObserver = new ContactObserver();// ��ϵ�����ݼ�������Ҫ�Ǽ����Է�����״̬
	private XmppService mXxService;// Main����
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXxService = ((XmppService.XXBinder) service).getService();
			mXxService.registerConnectionStatusCallback(ChatActivity.this);
			// ���û�������ϣ�����������xmpp������
			if (!mXxService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(ChatActivity.this,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						ChatActivity.this, PreferenceConstants.PASSWORD, "");
				mXxService.Login(usr, password);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXxService.unRegisterConnectionStatusCallback();
			mXxService = null;
		}

	};

	/**
	 * ������
	 */
	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
		} catch (IllegalArgumentException e) {
			L.e("Service wasn't bound!");
		}
	}

	/**
	 * �󶨷���
	 */
	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, XmppService.class);
		Uri chatURI = Uri.parse(mWithJabberID);
		mServiceIntent.setData(chatURI);
		bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_chat);
		initData();// ��ʼ������
		initView();// ��ʼ��view
		initFacePage();// ��ʼ������
		setChatWindowAdapter();// ��ʼ���Ի�����
		getContentResolver().registerContentObserver(RosterProvider.CONTENT_URI, true, mContactObserver);// ��ʼ������ϵ�����ݿ�
	}
	@Override
	protected void onResume() {
		super.onResume();
		updateContactStatus();// ������ϵ��״̬
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// ��ѯ��ϵ�����ݿ��ֶ�
	private static final String[] STATUS_QUERY = new String[] {
			RosterProvider.RosterConstants.STATUS_MODE,
			RosterProvider.RosterConstants.STATUS_MESSAGE, };

	private void updateContactStatus() {
		Cursor cursor = getContentResolver().query(RosterProvider.CONTENT_URI,
				STATUS_QUERY, RosterProvider.RosterConstants.JID + " = ?",
				new String[] { mWithJabberID }, null);
		int MODE_IDX = cursor
				.getColumnIndex(RosterProvider.RosterConstants.STATUS_MODE);
		int MSG_IDX = cursor
				.getColumnIndex(RosterProvider.RosterConstants.STATUS_MESSAGE);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			
			int status_mode = cursor.getInt(MODE_IDX);
			String status_message = cursor.getString(MSG_IDX);
			
			L.d("contact status changed: " + status_mode + " " + status_message);
//			mTitleNameView.setText(XMPPHelper.splitJidAndServer(getIntent()
//					.getStringExtra(INTENT_EXTRA_USERNAME)));
			/*int statusId = StatusMode.values()[status_mode].getDrawableId();
			
			if (statusId != -1) {// �����Ӧ����״̬
				// Drawable icon = getResources().getDrawable(statusId);
				// mTitleNameView.setCompoundDrawablesWithIntrinsicBounds(icon,
				// null,
				// null, null);
				mTitleStatusView.setImageResource(statusId);
				mTitleStatusView.setVisibility(View.VISIBLE);
			} else {
				mTitleStatusView.setVisibility(View.GONE);
			}*/
		}
		cursor.close();
	}

	/**
	 * ��ϵ�����ݿ�仯����
	 * 
	 */
	private class ContactObserver extends ContentObserver {
		public ContactObserver() {
			super(new Handler());
		}

		public void onChange(boolean selfChange) {
			L.d("ContactObserver.onChange: " + selfChange);
			updateContactStatus();// ��ϵ��״̬�仯ʱ��ˢ�½���
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (hasWindowFocus())
			unbindXMPPService();// ������
		getContentResolver().unregisterContentObserver(mContactObserver);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// ���ڻ�ȡ������ʱ�󶨷���ʧȥ���㽫���
		if (hasFocus)
			bindXMPPService();
		else
			unbindXMPPService();
	}

	private void initData() {
		mWithJabberID = getIntent().getDataString().toLowerCase();// ��ȡ��������id
		// ������map��key������������
		Set<String> keySet = XXApp.getInstance().getFaceMap().keySet();
		mFaceMapKeys = new ArrayList<String>();
		mFaceMapKeys.addAll(keySet);
	}

	/**
	 * ���������Adapter
	 */
	private void setChatWindowAdapter() {
		String selection = ChatConstants.JID + "='" + mWithJabberID + "'";
		// �첽��ѯ���ݿ�
		new AsyncQueryHandler(getContentResolver()) {

			@Override
			protected void onQueryComplete(int token, Object cookie,
					Cursor cursor) {
				// ListAdapter adapter = new ChatWindowAdapter(cursor,
				// PROJECTION_FROM, PROJECTION_TO, mWithJabberID);
				ListAdapter adapter = new ChatAdapter(ChatActivity.this,
						cursor, PROJECTION_FROM);
				mMsgListView.setAdapter(adapter);
				mMsgListView.setSelection(adapter.getCount() - 1);
			}

		}.startQuery(0, null, ChatProvider.CONTENT_URI, PROJECTION_FROM,
				selection, null, null);
		// ͬ����ѯ���ݿ⣬����ֹͣʹ��,��������Ӵ�ʱ�����½���ʧȥ��Ӧ
		// Cursor cursor = managedQuery(ChatProvider.CONTENT_URI,
		// PROJECTION_FROM,
		// selection, null, null);
		// ListAdapter adapter = new ChatWindowAdapter(cursor, PROJECTION_FROM,
		// PROJECTION_TO, mWithJabberID);
		// mMsgListView.setAdapter(adapter);
		// mMsgListView.setSelection(adapter.getCount() - 1);
	}

	private void initView() {
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowManagerParams = getWindow().getAttributes();
		
		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		// ����ListView���ر�������뷨
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mSendMsgBtn = (Button) findViewById(R.id.send);
		mFaceSwitchBtn = (ImageButton) findViewById(R.id.face_switch_btn);
		mChatEditText = (EditText) findViewById(R.id.input);
		mFaceRoot = (LinearLayout) findViewById(R.id.face_ll);//ʵ����װ�ر��������� ���Բ���
		mFaceViewPager = (ViewPager) findViewById(R.id.face_pager);//ʵ�������ر���������� viewpager
		mChatEditText.setOnTouchListener(this);
//		mTitleNameView = (TextView) findViewById(R.id.ivTitleName);
//		mTitleStatusView = (ImageView) findViewById(R.id.ivTitleStatus);
		mChatEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowManagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
							|| mIsFaceShow) {
						mFaceRoot.setVisibility(View.GONE);
						mIsFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		mChatEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					mSendMsgBtn.setEnabled(true);
				} else {
					mSendMsgBtn.setEnabled(false);
				}
			}
		});
		mFaceSwitchBtn.setOnClickListener(this);
		mSendMsgBtn.setOnClickListener(this);
	}

	@Override
	public void onRefresh() {
		mMsgListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// do nothing
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.face_switch_btn:
			if (!mIsFaceShow) {
				mInputMethodManager.hideSoftInputFromWindow(mChatEditText.getWindowToken(), 0);
				try {
					Thread.sleep(80);// �����ʱ���һ����Ļ������
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mFaceRoot.setVisibility(View.VISIBLE);
				mFaceSwitchBtn.setImageResource(R.drawable.aio_keyboard);
				mIsFaceShow = true;
			} else {
				mFaceRoot.setVisibility(View.GONE);
				mInputMethodManager.showSoftInput(mChatEditText, 0);
				mFaceSwitchBtn.setImageResource(R.drawable.qzone_edit_face_drawable);
				mIsFaceShow = false;
			}
			break;
		case R.id.send:// ������Ϣ
			sendMessageIfNotNull();
			break;
		default:
			break;
		}
	}

	private void sendMessageIfNotNull() {
		if (mChatEditText.getText().length() >= 1) {
			if (mXxService != null) {
				mXxService.sendMessage(mWithJabberID, mChatEditText.getText()
						.toString());
				if (!mXxService.isAuthenticated())
					T.showShort(this, "��Ϣ�Ѿ����������");
			}
			mChatEditText.setText(null);
			mSendMsgBtn.setEnabled(false);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.msg_listView:
			mInputMethodManager.hideSoftInputFromWindow(
					mChatEditText.getWindowToken(), 0);
			mFaceSwitchBtn
					.setImageResource(R.drawable.qzone_edit_face_drawable);
			mFaceRoot.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;
		case R.id.input:
			mInputMethodManager.showSoftInput(mChatEditText, 0);
			mFaceSwitchBtn
					.setImageResource(R.drawable.qzone_edit_face_drawable);
			mFaceRoot.setVisibility(View.GONE);
			mIsFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}
	/**
	 * ��ʼ������ҳ��
	 */
	private void initFacePage() {
		List<View> lv = new ArrayList<View>();//����һ�����ÿҳfacepage ��list, ���list��������м���ҳ�棬 ÿ��ҳ���ŵ���4��7�еı���
		//for (int i = 0; i < XXApp.NUM_PAGE; ++i)
		lv.add(getGridView(0));//����һҳ����ŵ�list � ��ǰֻ������һҳ
		FacePageAdeapter adapter = new FacePageAdeapter(lv);//
		mFaceViewPager.setAdapter(adapter);
		mFaceViewPager.setCurrentItem(mCurrentPage);
//		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
//		indicator.setViewPager(mFaceViewPager);
		adapter.notifyDataSetChanged();
		mFaceRoot.setVisibility(View.GONE);

		/*indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mCurrentPage = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int position) {
				// do nothing
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// do nothing
			}
		});*/

	}
	/**
	 * ��������ҳ��Ĺؼ�����
	 * �õ�һ��4��7�е�һ������ҳ��
	 * 
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		
		GridView gv = new GridView(this);
		gv.setNumColumns(7);//����7��
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// ����GridViewĬ�ϵ��Ч��
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(this, i));
		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> layout, View view, int position,
					long rowid ) {
				
				if (position == XXApp.NUM) {// ɾ������λ��
					int selection = mChatEditText.getSelectionStart();//�õ�����λ��
					String text = mChatEditText.getText().toString();//�õ������������
					if (selection > 0) {
						String text2 = text.substring(selection - 1);//�õ�������һ���ַ�
						if ("]".equals(text2)) {//�ж����һ���ַ����Ƿ���], �����] ������ж�
							int start = text.lastIndexOf("[");
							int end = selection;
							mChatEditText.getText().delete(start, end);
							return;
						}
						mChatEditText.getText()
								.delete(selection - 1, selection);
					}
				} else {
					int count = mCurrentPage * XXApp.NUM + position;
					// ע�͵Ĳ��֣���EditText����ʾ�ַ���
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// �����ⲿ�֣���EditText����ʾ����
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), (Integer) XXApp.getInstance().getFaceMap().values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 40;
						int newWidth = 40;
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
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(ChatActivity.this, newBitmap);
						//emojistr = "[angry]"
						String emojiStr = mFaceMapKeys.get(count);//��ȡ����Ӧgrid�б����key�ַ���
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan,
								emojiStr.indexOf('['),
								emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//��imageSpan ���ָ��������
						mChatEditText.append(spannableString);
					} else {
						String ori = mChatEditText.getText().toString();
						int index = mChatEditText.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, mFaceMapKeys.get(count));
						mChatEditText.setText(stringBuilder.toString());
						mChatEditText.setSelection(index + mFaceMapKeys.get(count).length());
					}
				}
			}
		});
		return gv;
	}

	// ��ֹ��pageview�ҹ���
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		// TODO Auto-generated method stub

	}

}
