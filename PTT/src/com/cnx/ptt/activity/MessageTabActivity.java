package com.cnx.ptt.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cnx.ptt.R;
import com.cnx.ptt.adapter.RosterAdapter;
import com.cnx.ptt.chat.FragmentCallBack;
import com.cnx.ptt.chat.app.XmppBroadcastReceiver;
import com.cnx.ptt.chat.app.XmppBroadcastReceiver.EventHandler;
import com.cnx.ptt.chat.db.ChatProvider;
import com.cnx.ptt.chat.db.RosterProvider;
import com.cnx.ptt.chat.db.RosterProvider.RosterConstants;
import com.cnx.ptt.chat.iphonetreeview.IphoneTreeView;
import com.cnx.ptt.chat.pulltorefresh.PullToRefreshBase;
import com.cnx.ptt.chat.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cnx.ptt.chat.pulltorefresh.PullToRefreshScrollView;
import com.cnx.ptt.chat.quickaction.ActionItem;
import com.cnx.ptt.chat.quickaction.QuickAction;
import com.cnx.ptt.chat.quickaction.QuickAction.OnActionItemClickListener;
import com.cnx.ptt.chat.service.IConnectionStatusCallback;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.slidingmenu.BaseSlidingFragmentActivity;
import com.cnx.ptt.chat.slidingmenu.SlidingMenu;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.chat.view.AddRosterItemDialog;
import com.cnx.ptt.chat.view.CustomDialog;
import com.cnx.ptt.chat.view.GroupNameView;
import com.cnx.ptt.utils.L;
import com.cnx.ptt.utils.NetUtil;
import com.cnx.ptt.utils.StatusMode;
import com.cnx.ptt.utils.T;

public class MessageTabActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, IConnectionStatusCallback, EventHandler,
		FragmentCallBack {
	
	private static final int ID_CHAT = 0;
	private static final int ID_AVAILABLE = 1;
	private static final int ID_AWAY = 2;
	private static final int ID_XA = 3;
	private static final int ID_DND = 4;
	public static HashMap<String, Integer> mStatusMap;
	static {
		mStatusMap = new HashMap<String, Integer>();
		mStatusMap.put(PreferenceConstants.OFFLINE, -1);
		mStatusMap.put(PreferenceConstants.DND, R.drawable.status_shield);
		mStatusMap.put(PreferenceConstants.XA, R.drawable.status_invisible);
		mStatusMap.put(PreferenceConstants.AWAY, R.drawable.status_leave);
		mStatusMap.put(PreferenceConstants.AVAILABLE, R.drawable.status_online);
		mStatusMap.put(PreferenceConstants.CHAT, R.drawable.status_qme);
	}
	private Handler mainHandler = new Handler();
	private XmppService mXxService;
	private SlidingMenu mSlidingMenu;
	private View mNetErrorView;
//	private TextView mTitleNameView;
//	private ImageView mTitleStatusView;
//	private ProgressBar mTitleProgressBar;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private IphoneTreeView mIphoneTreeView;
	private RosterAdapter mRosterAdapter;
	private ContentObserver mRosterObserver = new RosterObserver();
	private int mLongPressGroupId, mLongPressChildId;

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXxService = ((XmppService.XXBinder) service).getService();
			mXxService.registerConnectionStatusCallback(MessageTabActivity.this);
			// ��ʼ����xmpp������
			if (!mXxService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(MessageTabActivity.this,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						MessageTabActivity.this, PreferenceConstants.PASSWORD, "");
				mXxService.Login(usr, password);
//				 mTitleNameView.setText(R.string.login_prompt_msg);
//				 setStatusImage(false);
//				 mTitleProgressBar.setVisibility(View.VISIBLE);
			} else {
//				mTitleNameView.setText(XMPPHelper
//						.splitJidAndServer(PreferenceUtils.getPrefString(
//								MessageTabActivity.this, PreferenceConstants.ACCOUNT,
//								"")));
//				setStatusImage(true);
//				mTitleProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXxService.unRegisterConnectionStatusCallback();
			mXxService = null;
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(MessageTabActivity.this, XmppService.class));
		
//		initSlidingMenu();
		setContentView(R.layout.c_main_center_layout);
		initViews();
		registerListAdapter();
	}

	/**
	 * ���������η��ؼ����˳�
	 */
	private long firstTime;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - firstTime < 3000) {
			finish();
		} else {
			firstTime = System.currentTimeMillis();
			T.showShort(this, R.string.press_again_backrun);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindXMPPService();
		//���������ṩ��(���ݿ�)�仯
		getContentResolver().registerContentObserver(RosterProvider.CONTENT_URI, true, mRosterObserver);
		//����״̬
//		setStatusImage(isConnected());
		mRosterAdapter.requery();
		XmppBroadcastReceiver.mListeners.add(this);
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
			mNetErrorView.setVisibility(View.VISIBLE);
		else
			mNetErrorView.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(mRosterObserver);
		unbindXMPPService();
		XmppBroadcastReceiver.mListeners.remove(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private void registerListAdapter() {
		mRosterAdapter = new RosterAdapter(this, mIphoneTreeView, mPullRefreshScrollView);
		mIphoneTreeView.setAdapter(mRosterAdapter);
		mRosterAdapter.requery();
	}

	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
			L.i(LoginActivity.class, "[SERVICE] Unbind");
		} catch (IllegalArgumentException e) {
			L.e(LoginActivity.class, "Service wasn't bound!");
		}
	}

	private void bindXMPPService() {
		L.i(LoginActivity.class, "[SERVICE] bind");
		bindService(new Intent(MessageTabActivity.this, XmppService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	private void initViews() {
		mNetErrorView = findViewById(R.id.net_status_bar_top);
//		mSlidingMenu.setSecondaryMenu(R.layout.c_main_right_layout);
//		FragmentTransaction mFragementTransaction = getSupportFragmentManager()
//				.beginTransaction();
//		Fragment mFrag = new SettingsFragment();
//		mFragementTransaction.replace(R.id.main_right_fragment, mFrag);
//		mFragementTransaction.commit();

//		ImageButton mLeftBtn = ((ImageButton) findViewById(R.id.show_left_fragment_btn));
//		mLeftBtn.setVisibility(View.VISIBLE);
//		mLeftBtn.setOnClickListener(this);
//		ImageButton mRightBtn = ((ImageButton) findViewById(R.id.show_right_fragment_btn));
//		mRightBtn.setVisibility(View.VISIBLE);
//		mRightBtn.setOnClickListener(this);
		//David remove
//		mTitleNameView = (TextView) findViewById(R.id.ivTitleName);
//		mTitleProgressBar = (ProgressBar) findViewById(R.id.ivTitleProgress);
//		mTitleStatusView = (ImageView) findViewById(R.id.ivTitleStatus);
//		mTitleNameView.setText(XMPPHelper.splitJidAndServer(PreferenceUtils
//				.getPrefString(this, PreferenceConstants.ACCOUNT, "")));
//		mTitleNameView.setOnClickListener(this);
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		// mPullRefreshScrollView.setMode(Mode.DISABLED);
		// mPullRefreshScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(
		// "������£��ո�");
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetDataTask().execute();
					}
				});
		mIphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
		mIphoneTreeView.setHeaderView(getLayoutInflater().inflate(R.layout.c_contact_buddy_list_group, mIphoneTreeView, false));
		mIphoneTreeView.setEmptyView(findViewById(R.id.empty));
		
		mIphoneTreeView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						int groupPos = (Integer) view.getTag(R.id.xxx01); // ����ֵ����setTagʱʹ�õĶ�Ӧ��Դid��
						int childPos = (Integer) view.getTag(R.id.xxx02);
						mLongPressGroupId = groupPos;
						mLongPressChildId = childPos;
						if (childPos == -1) {// �������Ǹ���
							// ����groupPos�ж��㳤�������ĸ��������Ӧ��������ȣ�
							showGroupQuickActionBar(view.findViewById(R.id.group_name));
							// T.showShort(MessageTabActivity.this,
							// "LongPress group position = " + groupPos);
						} else {
							// ����groupPos��childPos�ж��㳤�������ĸ������µ��ĸ����Ȼ������Ӧ����
							// T.showShort(MessageTabActivity.this,
							// "onClick child position = " + groupPos
							// + ":" + childPos);
							showChildQuickActionBar(view
									.findViewById(R.id.icon));

						}
						return false;
					}
				});
		mIphoneTreeView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String userJid = mRosterAdapter.getChild(groupPosition,
						childPosition).getJid();
				String userName = mRosterAdapter.getChild(groupPosition,
						childPosition).getAlias();
				startChatActivity(userJid, userName);
				return false;
			}
		});
	}

	private void startChatActivity(String userJid, String userName) {
		Intent chatIntent = new Intent(MessageTabActivity.this, ChatActivity.class);
		Uri userNameUri = Uri.parse(userJid);
		chatIntent.setData(userNameUri);
		chatIntent.putExtra(ChatActivity.INTENT_EXTRA_USERNAME, userName);
		startActivity(chatIntent);
	}

	private boolean isConnected() {
		return mXxService != null && mXxService.isAuthenticated();
	}

	private void showGroupQuickActionBar(View view) {
		QuickAction quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
		quickAction
				.addActionItem(new ActionItem(0, getString(R.string.rename)));
		quickAction.addActionItem(new ActionItem(1,
				getString(R.string.add_friend)));
		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// ���û������ֱ�ӷ���
						if (!isConnected()) {
							T.showShort(MessageTabActivity.this,
									R.string.conversation_net_error_label);
							return;
						}
						switch (actionId) {
						case 0:
							String groupName = mRosterAdapter.getGroup(
									mLongPressGroupId).getGroupName();
							if (TextUtils.isEmpty(groupName)) {// ϵͳĬ�Ϸ��鲻����������
								T.showShort(MessageTabActivity.this,
										R.string.roster_group_rename_failed);
								return;
							}
							renameRosterGroupDialog(mRosterAdapter.getGroup(
									mLongPressGroupId).getGroupName());
							break;
						case 1:

							new AddRosterItemDialog(MessageTabActivity.this,
									mXxService).show();// �����ϵ��
							break;
						default:
							break;
						}
					}
				});
		quickAction.show(view);
		quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

	private void showChildQuickActionBar(View view) {
		QuickAction quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
		quickAction.addActionItem(new ActionItem(0, getString(R.string.open)));
		quickAction
				.addActionItem(new ActionItem(1, getString(R.string.rename)));
		quickAction.addActionItem(new ActionItem(2, getString(R.string.move)));
		quickAction
				.addActionItem(new ActionItem(3, getString(R.string.delete)));
		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						String userJid = mRosterAdapter.getChild(
								mLongPressGroupId, mLongPressChildId).getJid();
						String userName = mRosterAdapter.getChild(
								mLongPressGroupId, mLongPressChildId)
								.getAlias();
						switch (actionId) {
						case 0:
							startChatActivity(userJid, userName);
							break;
						case 1:
							if (!isConnected()) {
								T.showShort(MessageTabActivity.this,
										R.string.conversation_net_error_label);
								break;
							}
							renameRosterItemDialog(userJid, userName);
							break;
						case 2:
							if (!isConnected()) {
								T.showShort(MessageTabActivity.this,
										R.string.conversation_net_error_label);
								break;
							}
							moveRosterItemToGroupDialog(userJid);
							break;
						case 3:
							if (!isConnected()) {
								T.showShort(MessageTabActivity.this,
										R.string.conversation_net_error_label);
								break;
							}
							removeRosterItemDialog(userJid, userName);
							break;
						default:
							break;
						}
					}
				});
		quickAction.show(view);
	}

	private void initSlidingMenu() {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int mScreenWidth = dm.widthPixels;// ��ȡ��Ļ�ֱ��ʿ��
//		setBehindContentView(R.layout.c_main_left_layout);// ������˵�
//		FragmentTransaction mFragementTransaction = getSupportFragmentManager()
//				.beginTransaction();
//		Fragment mFrag = new RecentChatFragment();
//		mFragementTransaction.replace(R.id.main_left_fragment, mFrag);
//		mFragementTransaction.commit();
		// customize the SlidingMenu
//		mSlidingMenu = getSlidingMenu();
//		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// �������󻬻����һ����������Ҷ����Ի������������Ҷ����Ի�
//		mSlidingMenu.setShadowWidth(mScreenWidth / 40);// ������Ӱ���
//		mSlidingMenu.setBehindOffset(mScreenWidth / 8);// ���ò˵����
//		mSlidingMenu.setFadeDegree(0.35f);// ���õ��뵭���ı���
//		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//		mSlidingMenu.setShadowDrawable(R.drawable.shadow_left);// ������˵���ӰͼƬ
//		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);// �����Ҳ˵���ӰͼƬ
//		mSlidingMenu.setFadeEnabled(true);// ���û���ʱ�˵����Ƿ��뵭��
//		mSlidingMenu.setBehindScrollScale(0.333f);// ���û���ʱ��קЧ��
	}

	private static final String[] GROUPS_QUERY = new String[] {
			RosterConstants._ID, RosterConstants.GROUP, };
	private static final String[] ROSTER_QUERY = new String[] {
			RosterConstants._ID, RosterConstants.JID, RosterConstants.ALIAS,
			RosterConstants.STATUS_MODE, RosterConstants.STATUS_MESSAGE, };

	public List<String> getRosterGroups() {
		// we want all, online and offline
		List<String> list = new ArrayList<String>();
		Cursor cursor = getContentResolver().query(RosterProvider.GROUPS_URI,
				GROUPS_QUERY, null, null, RosterConstants.GROUP);
		int idx = cursor.getColumnIndex(RosterConstants.GROUP);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(cursor.getString(idx));
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	public List<String[]> getRosterContacts() {
		// we want all, online and offline
		List<String[]> list = new ArrayList<String[]>();
		Cursor cursor = getContentResolver().query(RosterProvider.CONTENT_URI,
				ROSTER_QUERY, null, null, RosterConstants.ALIAS);
		int JIDIdx = cursor.getColumnIndex(RosterConstants.JID);
		int aliasIdx = cursor.getColumnIndex(RosterConstants.ALIAS);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String jid = cursor.getString(JIDIdx);
			String alias = cursor.getString(aliasIdx);
			if ((alias == null) || (alias.length() == 0))
				alias = jid;
			list.add(new String[] { jid, alias });
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	protected void setViewImage(ImageView v, String value) {
		/*int presenceMode = Integer.parseInt(value);
		int statusDrawable = getIconForPresenceMode(presenceMode);
		v.setImageResource(statusDrawable);
		if (statusDrawable == R.drawable.status_busy)
			v.setVisibility(View.INVISIBLE);*/

	}

	private int getIconForPresenceMode(int presenceMode) {
		return StatusMode.values()[presenceMode].getDrawableId();
	}

	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
		case R.id.show_left_fragment_btn:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.show_right_fragment_btn:
			mSlidingMenu.showSecondaryMenu(true);
			break;
		//David remove
		case R.id.ivTitleName:
			if (isConnected())
				showStatusQuickAction(v);
			break;
		default:
			break;
		}*/
	}

	private void showStatusQuickAction(View v) {
		QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);
		quickAction.addActionItem(new ActionItem(ID_CHAT,
				getString(R.string.status_chat), getResources().getDrawable(
						R.drawable.status_qme)));
		quickAction.addActionItem(new ActionItem(ID_AVAILABLE,
				getString(R.string.status_available), getResources()
						.getDrawable(R.drawable.status_online)));
		quickAction.addActionItem(new ActionItem(ID_AWAY,
				getString(R.string.status_away), getResources().getDrawable(
						R.drawable.status_leave)));
		quickAction.addActionItem(new ActionItem(ID_XA,
				getString(R.string.status_xa), getResources().getDrawable(
						R.drawable.status_invisible)));
		quickAction.addActionItem(new ActionItem(ID_DND,
				getString(R.string.status_dnd), getResources().getDrawable(
						R.drawable.status_shield)));
		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// TODO Auto-generated method stub
						if (!isConnected()) {
							T.showShort(MessageTabActivity.this,
									R.string.conversation_net_error_label);
							return;
						}
						switch (actionId) {
						case ID_CHAT:
//							mTitleStatusView
//									.setImageResource(R.drawable.status_qme);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MODE,
									PreferenceConstants.CHAT);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MESSAGE,
									getString(R.string.status_chat));
							break;
						case ID_AVAILABLE:
//							mTitleStatusView
//									.setImageResource(R.drawable.status_online);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MODE,
									PreferenceConstants.AVAILABLE);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MESSAGE,
									getString(R.string.status_available));
							break;
						case ID_AWAY:
//							mTitleStatusView
//									.setImageResource(R.drawable.status_leave);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MODE,
									PreferenceConstants.AWAY);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MESSAGE,
									getString(R.string.status_away));
							break;
						case ID_XA:
//							mTitleStatusView
//									.setImageResource(R.drawable.status_invisible);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MODE,
									PreferenceConstants.XA);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MESSAGE,
									getString(R.string.status_xa));
							break;
						case ID_DND:
//							mTitleStatusView
//									.setImageResource(R.drawable.status_shield);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MODE,
									PreferenceConstants.DND);
							PreferenceUtils.setPrefString(MessageTabActivity.this,
									PreferenceConstants.STATUS_MESSAGE,
									getString(R.string.status_dnd));
							break;
						default:
							break;
						}
						mXxService.setStatusFromConfig();
//						SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager()
//								.findFragmentById(R.id.main_right_fragment);
//						fragment.readData();
					}
				});
		quickAction.show(v);
	}

	public abstract class EditOk {
		abstract public void ok(String result);
	}

	void removeChatHistory(final String JID) {
		getContentResolver().delete(ChatProvider.CONTENT_URI,
				ChatProvider.ChatConstants.JID + " = ?", new String[] { JID });
	}

	void removeRosterItemDialog(final String JID, final String userName) {
		new CustomDialog.Builder(this)
				.setTitle(R.string.deleteRosterItem_title)
				.setMessage(
						getString(R.string.deleteRosterItem_text, userName, JID))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mXxService.removeRosterItem(JID);
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

	private void editTextDialog(int titleId, CharSequence message, String text,
			final EditOk ok) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.c_edittext_dialog, null);

		TextView messageView = (TextView) layout.findViewById(R.id.text);
		messageView.setText(message);
		final EditText input = (EditText) layout.findViewById(R.id.editText);
		input.setTransformationMethod(android.text.method.SingleLineTransformationMethod
				.getInstance());
		input.setText(text);
		new CustomDialog.Builder(this)
				.setTitle(titleId)
				.setView(layout)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String newName = input.getText().toString();
								if (newName.length() != 0)
									ok.ok(newName);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

	void renameRosterItemDialog(final String JID, final String userName) {
		editTextDialog(R.string.RenameEntry_title,
				getString(R.string.RenameEntry_summ, userName, JID), userName,
				new EditOk() {
					public void ok(String result) {
						if (mXxService != null)
							mXxService.renameRosterItem(JID, result);
					}
				});
	}

	void renameRosterGroupDialog(final String groupName) {
		editTextDialog(R.string.RenameGroup_title,
				getString(R.string.RenameGroup_summ, groupName), groupName,
				new EditOk() {
					public void ok(String result) {
						if (mXxService != null)
							mXxService.renameRosterGroup(groupName, result);
					}
				});
	}

	void moveRosterItemToGroupDialog(final String jabberID) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View group = inflater
				.inflate(R.layout.c_moverosterentrytogroupview, null);
		final GroupNameView gv = (GroupNameView) group
				.findViewById(R.id.moverosterentrytogroupview_gv);
		gv.setGroupList(getRosterGroups());
		new CustomDialog.Builder(this)
				.setTitle(R.string.MoveRosterEntryToGroupDialog_title)
				.setView(group)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								L.d("new group: " + gv.getGroupName());
								if (isConnected())
									mXxService.moveRosterItemToGroup(jabberID,
											gv.getGroupName());
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

	@Override
	public void onNetChange() {
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
			T.showShort(this, R.string.net_error_tip);
			mNetErrorView.setVisibility(View.VISIBLE);
		} else {
			mNetErrorView.setVisibility(View.GONE);
		}
	}

	private void setStatusImage(boolean isConnected) {
		/*if (!isConnected) {
			mTitleStatusView.setVisibility(View.GONE);
			return;
		}
		String statusMode = PreferenceUtils.getPrefString(this,
				PreferenceConstants.STATUS_MODE, PreferenceConstants.AVAILABLE);
		int statusId = mStatusMap.get(statusMode);
		if (statusId == -1) {
			mTitleStatusView.setVisibility(View.GONE);
		} else {
			mTitleStatusView.setVisibility(View.VISIBLE);
			mTitleStatusView.setImageResource(statusId);
		}*/
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		/*switch (connectedState) {
		case XmppService.CONNECTED:
			mTitleNameView.setText(XMPPHelper.splitJidAndServer(PreferenceUtils
					.getPrefString(MessageTabActivity.this,
							PreferenceConstants.ACCOUNT, "")));
			mTitleProgressBar.setVisibility(View.GONE);
			// mTitleStatusView.setVisibility(View.GONE);
			setStatusImage(true);
			break;
		case XmppService.CONNECTING:
			mTitleNameView.setText(R.string.login_prompt_msg);
			mTitleProgressBar.setVisibility(View.VISIBLE);
			mTitleStatusView.setVisibility(View.GONE);
			break;
		case XmppService.DISCONNECTED:
			mTitleNameView.setText(R.string.login_prompt_no);
			mTitleProgressBar.setVisibility(View.GONE);
			mTitleStatusView.setVisibility(View.GONE);
			T.showLong(this, reason);
			break;

		default:
			break;
		}*/
	}

	@Override
	public XmppService getService() {
		return mXxService;
	}

	@Override
	public MessageTabActivity getMessageActivity() {
		return this;
	}

	public void updateRoster() {
		mRosterAdapter.requery();
	}

	private class RosterObserver extends ContentObserver {
		public RosterObserver() {
			super(mainHandler);
		}

		public void onChange(boolean selfChange) {
			L.d(MainActivity.class, "RosterObserver.onChange: " + selfChange);
			if (mRosterAdapter != null)
				mainHandler.postDelayed(new Runnable() {
					public void run() {
						updateRoster();
					}
				}, 100);
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// if (mPullRefreshScrollView.getState() != State.REFRESHING)
			// mPullRefreshScrollView.setState(State.REFRESHING, true);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			/*if (!isConnected()) {// ���û��������������
				String usr = PreferenceUtils.getPrefString(MessageTabActivity.this,
						PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(
						MessageTabActivity.this, PreferenceConstants.PASSWORD, "");
				mXxService.Login(usr, password);
			}*/
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			mRosterAdapter.requery();// ���²�ѯһ�����ݿ�
			mPullRefreshScrollView.onRefreshComplete();
			// mPullRefreshScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(
			// "������£��ո�");
			T.showShort(MessageTabActivity.this, "Refresh successful");
			super.onPostExecute(result);
		}
	}
}
