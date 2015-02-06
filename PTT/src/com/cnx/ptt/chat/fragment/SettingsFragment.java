package com.cnx.ptt.chat.fragment;

import com.cnx.ptt.R;
import com.cnx.ptt.activity.LoginActivity;
import com.cnx.ptt.activity.MessageTabActivity;
import com.cnx.ptt.chat.FragmentCallBack;
import com.cnx.ptt.chat.service.XmppService;
import com.cnx.ptt.chat.switcher.Switch;
import com.cnx.ptt.chat.utils.PreferenceConstants;
import com.cnx.ptt.chat.utils.PreferenceUtils;
import com.cnx.ptt.chat.view.CustomDialog;
import com.cnx.ptt.utils.XMPPHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;


public class SettingsFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {
	private TextView mTitleNameView;
	private View mAccountSettingView;
	private ImageView mHeadIcon;
	private ImageView mStatusIcon;
	private TextView mStatusView;
	private TextView mNickView;
	private Switch mShowOfflineRosterSwitch;
	private Switch mNotifyRunBackgroundSwitch;
	private Switch mNewMsgSoundSwitch;
	private Switch mNewMsgVibratorSwitch;
	private Switch mNewMsgLedSwitch;
	private Switch mVisiableNewMsgSwitch;
	private Switch mShowHeadSwitch;
	private Switch mConnectionAutoSwitch;
	private Switch mPoweronReceiverMsgSwitch;
	private Switch mSendCrashSwitch;
	private View mFeedBackView;
	private View mAboutView;
	private Button mExitBtn;
	private View mExitMenuView;
	private Button mExitCancleBtn;
	private Button mExitConfirmBtn;
	private FragmentCallBack mFragmentCallBack;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mFragmentCallBack = (FragmentCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.c_main_settings_fragment, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mExitMenuView = LayoutInflater.from(getActivity()).inflate(
				R.layout.c_common_menu_dialog_2btn_layout, null);
		mExitCancleBtn = (Button) mExitMenuView.findViewById(R.id.btnCancel);
		mExitConfirmBtn = (Button) mExitMenuView
				.findViewById(R.id.btn_exit_comfirm);
		mExitConfirmBtn.setText(R.string.exit);
		mExitCancleBtn.setOnClickListener(this);
		mExitConfirmBtn.setOnClickListener(this);
		//David remove
//		mTitleNameView = (TextView) view.findViewById(R.id.ivTitleName);
		mTitleNameView.setText(R.string.settings_fragment_title);
		mAccountSettingView = view.findViewById(R.id.accountSetting);
		mAccountSettingView.setOnClickListener(this);
		mHeadIcon = (ImageView) view.findViewById(R.id.face);
		mStatusIcon = (ImageView) view.findViewById(R.id.statusIcon);
		mStatusView = (TextView) view.findViewById(R.id.status);
		mNickView = (TextView) view.findViewById(R.id.nick);
		mShowOfflineRosterSwitch = (Switch) view
				.findViewById(R.id.show_offline_roster_switch);
		mShowOfflineRosterSwitch.setOnCheckedChangeListener(this);
		mNotifyRunBackgroundSwitch = (Switch) view
				.findViewById(R.id.notify_run_background_switch);
		mNotifyRunBackgroundSwitch.setOnCheckedChangeListener(this);
		mNewMsgSoundSwitch = (Switch) view
				.findViewById(R.id.new_msg_sound_switch);
		mNewMsgSoundSwitch.setOnCheckedChangeListener(this);
		mNewMsgVibratorSwitch = (Switch) view
				.findViewById(R.id.new_msg_vibrator_switch);
		mNewMsgSoundSwitch.setOnCheckedChangeListener(this);
		mNewMsgLedSwitch = (Switch) view.findViewById(R.id.new_msg_led_switch);
		mNewMsgLedSwitch.setOnCheckedChangeListener(this);
		mVisiableNewMsgSwitch = (Switch) view
				.findViewById(R.id.visiable_new_msg_switch);
		mVisiableNewMsgSwitch.setOnCheckedChangeListener(this);
		mShowHeadSwitch = (Switch) view.findViewById(R.id.show_head_switch);
		mShowHeadSwitch.setOnCheckedChangeListener(this);
		mConnectionAutoSwitch = (Switch) view
				.findViewById(R.id.connection_auto_switch);
		mConnectionAutoSwitch.setOnCheckedChangeListener(this);
		mPoweronReceiverMsgSwitch = (Switch) view
				.findViewById(R.id.poweron_receiver_msg_switch);
		mPoweronReceiverMsgSwitch.setOnCheckedChangeListener(this);
		mSendCrashSwitch = (Switch) view.findViewById(R.id.send_crash_switch);
		mSendCrashSwitch.setOnCheckedChangeListener(this);
		mFeedBackView = view.findViewById(R.id.set_feedback);
		mAboutView = view.findViewById(R.id.set_about);
		mExitBtn = (Button) view.findViewById(R.id.exit_app);
		mFeedBackView.setOnClickListener(this);
		mAboutView.setOnClickListener(this);
		mExitBtn.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		readData();
	}

	public void readData() {
		mHeadIcon.setImageResource(R.drawable.login_default_avatar);
		mStatusIcon.setImageResource(MessageTabActivity.mStatusMap
				.get(PreferenceUtils.getPrefString(getActivity(),
						PreferenceConstants.STATUS_MODE,
						PreferenceConstants.AVAILABLE)));
		mStatusView.setText(PreferenceUtils.getPrefString(getActivity(),
				PreferenceConstants.STATUS_MESSAGE,
				getActivity().getString(R.string.status_available)));
		mNickView
				.setText(XMPPHelper.splitJidAndServer(PreferenceUtils
						.getPrefString(getActivity(),
								PreferenceConstants.ACCOUNT, "")));
		mShowOfflineRosterSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.SHOW_OFFLINE, true));

		mNotifyRunBackgroundSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.FOREGROUND, true));
		mNewMsgSoundSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.SCLIENTNOTIFY, false));
		mNewMsgVibratorSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.VIBRATIONNOTIFY, true));
		mNewMsgLedSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.LEDNOTIFY, true));
		mVisiableNewMsgSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.TICKER, true));
		mShowHeadSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.SHOW_MY_HEAD, true));
		mConnectionAutoSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.AUTO_RECONNECT, true));
		mPoweronReceiverMsgSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.AUTO_START, true));
		mSendCrashSwitch.setChecked(PreferenceUtils.getPrefBoolean(
				getActivity(), PreferenceConstants.REPORT_CRASH, true));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.show_offline_roster_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.SHOW_OFFLINE, isChecked);
			mFragmentCallBack.getMessageActivity().updateRoster();
			break;
		case R.id.notify_run_background_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.FOREGROUND, isChecked);
			break;
		case R.id.new_msg_sound_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.SCLIENTNOTIFY, isChecked);
			break;
		case R.id.new_msg_vibrator_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.VIBRATIONNOTIFY, isChecked);
			break;
		case R.id.new_msg_led_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.LEDNOTIFY, isChecked);
			break;
		case R.id.visiable_new_msg_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.TICKER, isChecked);
			break;
		case R.id.show_head_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.SHOW_MY_HEAD, isChecked);
			break;
		case R.id.connection_auto_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.AUTO_RECONNECT, isChecked);
			break;
		case R.id.poweron_receiver_msg_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.AUTO_START, isChecked);
			break;
		case R.id.send_crash_switch:
			PreferenceUtils.setPrefBoolean(getActivity(),
					PreferenceConstants.REPORT_CRASH, isChecked);

			break;
		default:
			break;
		}
	}

	private Dialog mExitDialog;

	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
		case R.id.set_feedback:
			startActivity(new Intent(getActivity(), FeedBackActivity.class));
			break;
		case R.id.set_about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			break;
		case R.id.exit_app:
			if (mExitDialog == null)
				mExitDialog = DialogUtil.getMenuDialog(getActivity(),
						mExitMenuView);
			mExitDialog.show();
			break;
		case R.id.btnCancel:
			if (mExitDialog != null && mExitDialog.isShowing())
				mExitDialog.dismiss();
			break;
		case R.id.btn_exit_comfirm:
			XXService service = mFragmentCallBack.getService();
			if (service != null) {
				service.logout();// ע��
				service.stopSelf();// ֹͣ����
			}
			if(mExitDialog.isShowing()){
				mExitDialog.cancel();
			}
			getActivity().finish();
			break;
		case R.id.accountSetting:
			logoutDialog();
			break;
		default:
			break;
		}*/
	}

	public void logoutDialog() {
		new CustomDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.open_switch_account))
				.setMessage(
						getActivity().getString(
								R.string.open_switch_account_msg))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								XmppService service = mFragmentCallBack
										.getService();
								if (service != null) {
									service.logout();// ע��
								}
								dialog.dismiss();
								startActivity(new Intent(getActivity(),
										LoginActivity.class));
								getActivity().finish();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}
}
