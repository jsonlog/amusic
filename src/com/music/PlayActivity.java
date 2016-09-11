package com.music;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.music.R;
import com.music.util.Constant;
import com.music.util.Constant.SerializableList;
import com.music.util.LrcContent;
import com.music.util.LrcView;
import com.music.util.Mp3Info;
import com.music.util.Constant.SerializableMaplist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AdapterView.OnItemClickListener;

public class PlayActivity extends Activity {
	private TextView titleView;// ��������
	private TextView progressView; // ��������
	private TextView artistView;
	private TextView durationView;
	private Button previousBtn; // ��һ��
	private Button repeatBtn; // �ظ�������ѭ����ȫ��ѭ����
	private Button playBtn; // ���ţ����š���ͣ��
	private Button shuffleBtn; // �������
	private Button nextBtn; // ��һ��
	private Button queueBtn;
	private Receiver receiver;
	
	RelativeLayout ll_player_voice;	//����������岼��
	ImageButton ibtn_player_voice;	//��ʾ�����������İ�ť
	SeekBar sb_player_voice;		//����������С
	int currentVolume;				//��ǰ����
	int maxVolume;					//�������
	private SeekBar music_progressBar;  //��������  
	private AudioManager am;
	public static LrcView lrcView;
	private List<LrcContent> lrcList; //��Ÿ���б���� = new ArrayList<LrcContent>()
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity_layout);
		
		findViewById();
	    ViewOnClickListener viewOnClickListener = new ViewOnClickListener();  
	    setViewOnClickListener(viewOnClickListener);
		music_progressBar = (SeekBar) findViewById(R.id.audioTrack);  
		ibtn_player_voice = (ImageButton) findViewById(R.id.ibtn_player_voice);
		ll_player_voice = (RelativeLayout) findViewById(R.id.ll_player_voice);
		sb_player_voice = (SeekBar) findViewById(R.id.sb_player_voice);	
		queueBtn = (Button) findViewById(R.id.play_queue);
		ibtn_player_voice.setOnClickListener(viewOnClickListener);	    
		queueBtn.setOnClickListener(viewOnClickListener);  
		lrcView = (LrcView) findViewById(R.id.lrcShowView);
//	    sendIntent(Constant.playMSG.PLAY_MSG);	  
		receiver = new Receiver();
		// ����IntentFilter
		IntentFilter filter = new IntentFilter();
		// ָ��BroadcastReceiver������Action
		filter.addAction(Constant.CTL_ACTION);
		filter.addAction(Constant.LRC_ACTION);
		// ע��BroadcastReceiver
		registerReceiver(receiver, filter);
	    
        music_progressBar.setOnSeekBarChangeListener(new SeekBarChangeListener());  
		sb_player_voice.setOnSeekBarChangeListener(new SeekBarChangeListener());
		//���ϵͳ��Ƶ����������
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	sb_player_voice.setProgress(currentVolume);
    	
    	showNotify(Constant.playMSG.PLAY_MSG,"������","�ܽ���",0,0);
	}
//	@Override
//	public void onResume(){
//		titleView.setText(titleView.getText());
//		artistView.setText(artistView.getText());
//		lrcView.setmLrcList(lrcList);
//		lrcView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.alpha_z));
//		super.onResume();		
//		Log.v("PLAYING","resume"+titleView.getText());
//	}
	public void showNotify(int MSG,String title,String artist,long Id,long AlbumId){
		NotificationCompat.Builder mBuilder = new Builder(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.view_custom_button);
		if(Id == 0){
			views.setImageViewResource(R.id.custom_song_icon, R.drawable.icon);
		}else{
			Bitmap bm = Constant.getArtwork(this, Id, AlbumId, true, true);
			views.setImageViewBitmap(R.id.custom_song_icon, bm);
		}
		//views.setImageViewBitmap(viewId, bitmap);
		//API3.0 ���ϵ�ʱ����ʾ��ť��������ʧ
		views.setTextViewText(R.id.tv_custom_song_name, title);
		views.setTextViewText(R.id.tv_custom_song_singer, artist);
		//����汾�ŵ��ڣ�3��0������ô����ʾ��ť
		if(android.os.Build.VERSION.SDK_INT <= 9){
			views.setViewVisibility(R.id.ll_custom_button, View.GONE);
		}else{
			views.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
			//
			if(MSG == Constant.playMSG.PAUSE_MSG){
				views.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_play);
			}else{
				views.setImageViewResource(R.id.btn_custom_play, R.drawable.btn_pause);
			}
		}
		//������¼�����
		Intent buttonIntent = new Intent(Constant.BUTTON_ACTION);
		/* ��һ�װ�ť */
		buttonIntent.putExtra("MSG", Constant.playMSG.PREVIOUS_MSG);
		//������˹㲥������INTENT�ı�����getBroadcast����
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		/* ����/��ͣ  ��ť */
		buttonIntent.putExtra("MSG", Constant.playMSG.PLAY_MSG);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		/* ��һ�� ��ť  */
		buttonIntent.putExtra("MSG", Constant.playMSG.NEXT_MSG);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), Notification.FLAG_ONGOING_EVENT);
		mBuilder.setContent(views)
				.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
				.setTicker("���ڲ���")
				.setPriority(Notification.PRIORITY_DEFAULT)// ���ø�֪ͨ���ȼ�
				.setOngoing(true)
				.setSmallIcon(R.drawable.icon);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		//�ᱨ�������ҽ��˼·
//		notify.contentView = views;
//		notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(200, notify);
	}	
	public static void collapseStatusBar(Context context) { 
		try { 
			Object statusBarManager = context.getSystemService("statusbar"); 
			Method collapse; 
			if (Build.VERSION.SDK_INT <= 16) { 
				collapse = statusBarManager.getClass().getMethod("collapse"); 
			} else { 
				collapse = statusBarManager.getClass().getMethod("collapsePanels"); 
			}
			collapse.invoke(statusBarManager);
		} 
		catch (Exception localException) {
			localException.printStackTrace(); 
		} 
	}
	//������ʾ�����������Ķ���
	public void voicePanelAnimation() {
		if(ll_player_voice.getVisibility() == View.GONE) {
			ll_player_voice.startAnimation(AnimationUtils.loadAnimation(PlayActivity.this, R.anim.push_up_in));
			ll_player_voice.setVisibility(View.VISIBLE);
		}
		else{
			ll_player_voice.startAnimation(AnimationUtils.loadAnimation(PlayActivity.this, R.anim.push_up_out));
			ll_player_voice.setVisibility(View.GONE);
		}
	}
	/** 
	 * ʵ�ּ���Seekbar���� 
	 * @author playerm 
	 * 
	 */  
	private class SeekBarChangeListener implements OnSeekBarChangeListener {  
		@Override  
		public void onProgressChanged(SeekBar seekBar, int progress,  
				boolean fromUser) {  
			switch(seekBar.getId()) {
			case R.id.audioTrack:
				if (fromUser) {
					sendIntent(Constant.playMSG.PROGRESS_MSG,progress);
				}
				break;
			case R.id.sb_player_voice:
				// ��������
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				ll_player_voice.startAnimation(AnimationUtils.loadAnimation(PlayActivity.this, R.anim.push_up_out));
				ll_player_voice.setVisibility(View.GONE);
				break;
			}
		}  
		@Override  
		public void onStartTrackingTouch(SeekBar seekBar) {  
			  
		}  
		@Override  
		public void onStopTrackingTouch(SeekBar seekBar) {  
		        
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
	 * ��ÿһ����ť���ü�����
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
		if(MSG == Constant.playMSG.PROGRESS_MSG){
			intent.putExtra("currentTime", position);
		}else{
			intent.putExtra("location", position);
		}
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
			case R.id.ibtn_player_voice: voicePanelAnimation();break;
			case R.id.play_queue: showPlayQueue();break;
			}
			sendIntent(MSG);
		}
	}
	private void showPlayQueue(){
		LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View playQueueLayout = layoutInflater.inflate(R.layout.play_queue_layout, (ViewGroup)findViewById(R.id.play_queue_layout));
		ListView queuelist = (ListView) playQueueLayout.findViewById(R.id.lv_play_queue);
		
		List<Mp3Info> mp3Infos = Constant.getMp3Infos(this);
		List<HashMap<String, String>> queues = Constant.getMusicMaps(mp3Infos);
		SimpleAdapter adapter = new SimpleAdapter(this, queues, R.layout.play_queue_item_layout, new String[]{"title",
				"Artist", "duration"}, new int[]{R.id.music_title, R.id.music_Artist, R.id.music_duration});
		queuelist.setAdapter(adapter);
		AlertDialog.Builder builder;
		final AlertDialog dialog;
		builder = new AlertDialog.Builder(this);
		dialog = builder.create();
		dialog.setView(playQueueLayout);
		dialog.show();
		queuelist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sendIntent(Constant.playMSG.LOCATION_MSG,position);
				dialog.dismiss();
			}
		});
	}
	/** 
	 * * ��ע��㲥 
	*/  
	@Override  
	protected void onStop() {  
		unregisterReceiver(receiver);  
//		Intent MyIntent = new Intent(Intent.ACTION_MAIN);
//	    MyIntent.addCategory(Intent.CATEGORY_HOME);
//	    startActivity(MyIntent);
//	    finish();
		super.onStop();  
	}  
	
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Constant.LRC_ACTION)){
				int currentTime = intent.getIntExtra("currentTime", 0);
				if(intent.getBooleanExtra("firstTime", false)){//lrcList == null
//					new Handler().postDelayed(new Runnable(){   
//					    public void run() {   
//					    }   
//					 }, 8000);   
					String title = intent.getExtras().getString("title");
					String artist = intent.getExtras().getString("artist");
					int duration = intent.getExtras().getInt("duration");
					titleView.setText(title);
					artistView.setText(artist);
					durationView.setText(Constant.formatTime(duration));
					playBtn.setBackgroundResource(R.drawable.pause_selector);
					
					Bundle bundle = intent.getExtras();
			        SerializableList list = (SerializableList) bundle.get("list");
//			    	lrcList = new ArrayList<LrcContent>();
		        	lrcList = list.getList();
					lrcView.setmLrcList(lrcList);
					lrcView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.alpha_z));
		        }
				lrcView.setIndex(intent.getIntExtra("lrcIndex", 0));
				lrcView.invalidate();
				progressView.setText(Constant.formatTime(currentTime));
				music_progressBar.setProgress(currentTime);
			}else if(action.equals(Constant.CTL_ACTION)){
				switch(intent.getIntExtra("MSG", 0)){
				case Constant.playMSG.PAUSE_MSG:
					playBtn.setBackgroundResource(R.drawable.play_selector);break;
				case Constant.playMSG.PLAY_MSG:
				case Constant.playMSG.PREVIOUS_MSG:
				case Constant.playMSG.NEXT_MSG:
				case Constant.playMSG.LOCATION_MSG:
					int current = intent.getExtras().getInt("currentTime");
					int duration = intent.getExtras().getInt("duration");
					int msg = intent.getIntExtra("MSG", 0);
					String title = intent.getExtras().getString("title");
					String artist = intent.getExtras().getString("artist");
					Log.v("msg",title + artist+msg);
					titleView.setText(title);
					artistView.setText(artist);
					durationView.setText(Constant.formatTime(duration));
					progressView.setText(Constant.formatTime(current));
					music_progressBar.setProgress(current);
					music_progressBar.setMax((int)duration);
					showNotify(intent.getIntExtra("MSG", 0),title,artist,intent.getLongExtra("Id", 0),intent.getLongExtra("AlbumId", 0));
					collapseStatusBar(context);
//					if(msg != Constant.playMSG.PAUSE_MSG){
						playBtn.setBackgroundResource(R.drawable.pause_selector);
					break;
				case Constant.playMSG.SHUFFLE_MSG:
					switch(intent.getIntExtra("repeat", 0)){
					case Constant.repeatState.isShuffle:
						shuffleBtn.setBackgroundResource(R.drawable.shuffle_selector);
						Toast.makeText(context, "��������Ѵ�", Toast.LENGTH_SHORT).show();
						break;
					case Constant.repeatState.isOrder:
						shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
						Toast.makeText(context, "��������ѹر�", Toast.LENGTH_SHORT).show();
						break;
					}
					repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
					break;
				case Constant.playMSG.REPEAT_MSG:
					switch(intent.getIntExtra("repeat",0)){
					case Constant.repeatState.isOrder:
						repeatBtn.setBackgroundResource(R.drawable.repeat_none_selector);
						Toast.makeText(context, "ѭ�������ѹر�", Toast.LENGTH_SHORT).show();
						break;
					case Constant.repeatState.isCurrentRepeat:
						Toast.makeText(context, "�ظ������Ѵ�", Toast.LENGTH_SHORT).show();
						repeatBtn.setBackgroundResource(R.drawable.repeat_current_selector);
						break;
					case Constant.repeatState.isAllRepeat:
						repeatBtn.setBackgroundResource(R.drawable.repeat_all_selector);
						Toast.makeText(context, "ѭ�������Ѵ�", Toast.LENGTH_SHORT).show();
						break;
					}
					shuffleBtn.setBackgroundResource(R.drawable.shuffle_none_selector);
					break;
				}
			}
		}
	}	
}
