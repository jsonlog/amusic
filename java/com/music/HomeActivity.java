package com.music;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.music.util.Constant;
import com.music.util.Mp3Info;
import com.music.util.MusicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
	private ListView listView;
	private TextView titleView;// 歌曲标题
	private TextView progressView; // 歌曲进度
	private TextView artistView;
	private TextView durationView;
	private Button previousBtn; // 上一首
	private Button repeatBtn; // 重复（单曲循环、全部循环）
	private Button playBtn; // 播放（播放、暂停）
	private Button shuffleBtn; // 随机播放
	private Button nextBtn; // 下一首
	private Receiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

			List<String> permissions = new ArrayList<String>();
			if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
				permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			} else {
//                preferencesUtility.setString("storage", "true");
				SharedPreferences sp = getSharedPreferences("自定义保存的文件名", Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = sp.edit();
				// 和Map<key, value>一样保存数据，取数据也是一样简单
				edit.putString("storage", "storage");
				edit.commit();
			}

			if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
				permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

			} else {
//                preferencesUtility.setString("storage", "true");
				SharedPreferences sp = this.getSharedPreferences("自定义保存的文件名", Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = sp.edit();
				// 和Map<key, value>一样保存数据，取数据也是一样简单
				edit.putString("storage", "storage");
				edit.commit();
			}

			if (!permissions.isEmpty()) {
				requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
			}
		}

		setContentView(R.layout.home_activity_layout);

		findViewById();
		ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
		setViewOnClickListener(viewOnClickListener);
		listView = (ListView) findViewById(R.id.music_list);

		sendIntent(Constant.playMSG.LOCATION_MSG,0);
		receiver = new Receiver();
		// 创建IntentFilter
		IntentFilter filter = new IntentFilter();
		// 指定BroadcastReceiver监听的Action
		filter.addAction(Constant.CTL_ACTION);
		filter.addAction(Constant.LRC_ACTION);
		// 注册BroadcastReceiver
		registerReceiver(receiver, filter);

		List<Mp3Info> mp3Infos = Constant.getMp3Infos(this);//playservice.mp3Infos;
		MusicListAdapter listAdapter = new MusicListAdapter(this, mp3Infos);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				sendIntent(Constant.playMSG.LOCATION_MSG,position);
				Intent intent = new Intent(HomeActivity.this,PlayActivity.class);
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
										   long id){
				Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
				vibrator.vibrate(50); // 长按振动
				// TODO
				return true;
			}
		});
	}
	public static final int  REQUEST_CODE_SOME_FEATURES_PERMISSIONS=1;
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
//            case 1:{
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
					} else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
					}
				}
			}
			break;
			default: {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			}
		}
	}
	private void findViewById(){
		titleView = (TextView) findViewById(R.id.music_title);
		artistView = (TextView)findViewById(R.id.music_artist);
		progressView = (TextView)findViewById(R.id.current_progress);
		durationView = (TextView) findViewById(R.id.final_progress);
		previousBtn = (Button) findViewById(R.id.previous_music);
		repeatBtn = (Button) findViewById(R.id.repeat_music);
		playBtn = (Button) findViewById(R.id.play_music);
		shuffleBtn = (Button) findViewById(R.id.shuffle_music);
		nextBtn = (Button) findViewById(R.id.next_music);
	}
	/**
	 * 给每一个按钮设置监听器
	 */
	private void setViewOnClickListener(ViewOnClickListener viewOnClickListener) {
		previousBtn.setOnClickListener(viewOnClickListener);
		repeatBtn.setOnClickListener(viewOnClickListener);
		playBtn.setOnClickListener(viewOnClickListener);
		shuffleBtn.setOnClickListener(viewOnClickListener);
		nextBtn.setOnClickListener(viewOnClickListener);
	}
	public void sendIntent(int MSG){
		Intent intent = new Intent(this,PlayService.class);
		intent.setPackage(getPackageName());
		intent.setAction(Constant.SERVICE_ACTION);
		intent.putExtra("MSG", MSG);
		startService(intent);
	}
	public void sendIntent(int MSG,int position){
		Intent intent = new Intent(this,PlayService.class);
		intent.setPackage(getPackageName());
		intent.putExtra("location", position);
		intent.setAction(Constant.SERVICE_ACTION);
		intent.putExtra("MSG", MSG);
		startService(intent);
	}
	private class ViewOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v){
			int MSG = Constant.playMSG.PLAY_MSG;
			switch(v.getId()){
				case R.id.previous_music: MSG = Constant.playMSG.PREVIOUS_MSG;break;
				case R.id.next_music: MSG = Constant.playMSG.NEXT_MSG;break;
				case R.id.shuffle_music: MSG = Constant.playMSG.SHUFFLE_MSG;break;
				case R.id.repeat_music: MSG = Constant.playMSG.REPEAT_MSG;break;
				case R.id.play_music: MSG = Constant.playMSG.PLAY_MSG;break;
			}
			sendIntent(MSG);
		}
	}
	/**
	 * * 反注册广播
	 */
	@Override
	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Constant.LRC_ACTION)){
				progressView.setText(Constant.formatTime(intent.getIntExtra("currentTime", 0)));
			}else if(action.equals(Constant.CTL_ACTION)){
				switch(intent.getIntExtra("MSG", 0)){
					case Constant.playMSG.PREVIOUS_MSG:
					case Constant.playMSG.NEXT_MSG:
					case Constant.playMSG.LOCATION_MSG:
						progressView.setText(Constant.formatTime(intent.getExtras().getInt("currentTime")));
						titleView.setText(intent.getExtras().getString("title"));
						artistView.setText(intent.getExtras().getString("artist"));
						durationView.setText(Constant.formatTime(intent.getExtras().getInt("duration")));
						playBtn.setBackgroundResource(R.drawable.pause_selector);
						break;
					case Constant.playMSG.PAUSE_MSG:
						playBtn.setBackgroundResource(R.drawable.play_selector);break;
					case Constant.playMSG.PLAY_MSG:
						playBtn.setBackgroundResource(R.drawable.pause_selector);break;
					case Constant.playMSG.REPEAT_MSG:
						switch(intent.getIntExtra("repeat",0)){
							case Constant.repeatState.isOrder:
								//Toast.makeText(context, "循环播放已关闭", Toast.LENGTH_SHORT).show();
								repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
								break;
							case Constant.repeatState.isCurrentRepeat:
								//Toast.makeText(context, "重复播放已打开", Toast.LENGTH_SHORT).show();
								repeatBtn.setBackgroundResource(R.drawable.repeat_current_selector);
								break;
							case Constant.repeatState.isAllRepeat:
								//Toast.makeText(context, "循环播放已打开", Toast.LENGTH_SHORT).show();
								repeatBtn.setBackgroundResource(R.drawable.repeat_all_selector);
								break;
						}
						shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
						break;
					case Constant.playMSG.SHUFFLE_MSG:
						switch(intent.getIntExtra("repeat", 0)){
							case Constant.repeatState.isShuffle:
								//Toast.makeText(context, "随机播放已打开", Toast.LENGTH_SHORT).show();
								shuffleBtn.setBackgroundResource(R.drawable.shuffle_selector);
								break;
							case Constant.repeatState.isOrder:
								//Toast.makeText(context, "随机播放已关闭", Toast.LENGTH_SHORT).show();
								shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
								break;
						}
						repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
						break;
				}
			}
		}
	}
}
