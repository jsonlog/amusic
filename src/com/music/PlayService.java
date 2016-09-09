package com.music;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.music.PlayActivity;
import com.music.util.LrcContent;
import com.music.util.Constant;
import com.music.util.Mp3Info;
import com.music.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.animation.AnimationUtils;

public class PlayService extends Service {
	private MediaPlayer mediaPlayer = new MediaPlayer(); // 媒体播放器对象
	public static List<Mp3Info> mp3Infos;	//存放Mp3Info对象的集合
	private Mp3Info mp3Info;
	private int location = 0;
	private int play = Constant.playMSG.PLAY_MSG;
	private int repeat = Constant.repeatState.isOrder;
	private int currentTime = 0;
	private Receiver receiver;
	private boolean firstTime = true;
	LrcContent mLrcContent = new LrcContent();
	private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
	/**
	 * handler用来接收消息，来发送广播更新播放时间
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if(mediaPlayer != null && mediaPlayer.isPlaying()) {
					currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
					Intent sendIntent = new Intent();
					sendIntent.putExtra("currentTime", currentTime);
					play = Constant.playMSG.PROGRESS_MSG;
					broadcast(sendIntent); // 给PlayerActivity发送广播
					handler.sendEmptyMessageDelayed(1, 1000);
				}
			}
		};
	};	
	public void initLrc(){
		lrcList = new ArrayList<LrcContent>();
		readLRC(mp3Info.getUrl());
		Log.v("iiiiiii",currentTime+"");
		PlayActivity.lrcView.setmLrcList(lrcList);
		//切换带动画显示歌词
		PlayActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(PlayService.this,R.anim.alpha_z));
		handler.post(mRunnable);
	}

	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			PlayActivity.lrcView.setIndex(lrcIndex());
			PlayActivity.lrcView.invalidate();
			handler.postDelayed(mRunnable, 100);
		}
	};
	/**
	 * 根据时间获取歌词显示的索引值
	 * @return
	 */
	public int lrcIndex() {
//		int duration = 1000;
		int index = 0;
//		if(mediaPlayer.isPlaying()) {
			currentTime = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();
//		}
		if(currentTime < duration) {
			for (int i = 0; i < lrcList.size(); i++) {
				if (i < lrcList.size() - 1) {
					if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
						index = i;
					}
					if (currentTime > lrcList.get(i).getLrcTime()
							&& currentTime < lrcList.get(i + 1).getLrcTime()) {
						index = i;
					}
				}
				if (i == lrcList.size() - 1
						&& currentTime > lrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}
	/**
	 * 读取歌词
	 * @param path
	 * @return
	 */
	public String readLRC(String path) {
		//定义一个StringBuilder对象，用来存放歌词内容
		StringBuilder stringBuilder = new StringBuilder();
		File f = new File(path.replace(".mp3", ".lrc"));
		
		try {
			//创建一个文件输入流对象
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while((s = br.readLine()) != null) {
				//替换字符
				s = s.replace("[", "");
				s = s.replace("]", "@");
				
				//分离“@”字符
				String splitLrcData[] = s.split("@");
				if(splitLrcData.length > 1) {
					mLrcContent.setLrcStr(splitLrcData[1]);
					
					//处理歌词取得歌曲的时间
					int lrcTime = Constant.time2Str(splitLrcData[0]);
					
					mLrcContent.setLrcTime(lrcTime);
					
					//添加进列表数组
					lrcList.add(mLrcContent);
					
					//新创建歌词内容对象
					mLrcContent = new LrcContent();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			stringBuilder.append("木有下载歌词文件！...");
		} catch (IOException e) {
			e.printStackTrace();
			stringBuilder.append("木有读取到歌词哦！");
		}
		return stringBuilder.toString();
	}
	public void broadcast(Intent intent){
		intent.setPackage(getPackageName());
		intent.setAction(Constant.CTL_ACTION);
		intent.putExtra("MSG", play);
		sendBroadcast(intent);
	}
	@Override
	public void onCreate(){
		super.onCreate();
		mp3Infos = Constant.getMp3Infos(this);
		mp3Info = mp3Infos.get(location);
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.BUTTON_ACTION);
		// 注册BroadcastReceiver
		registerReceiver(receiver, filter);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				switch(repeat){
				case Constant.repeatState.isOrder:
					next();break;
				case Constant.repeatState.isCurrentRepeat:
					break;
				case Constant.repeatState.isAllRepeat:
					location++;
					if(location > mp3Infos.size() - 1){
						location = 0;
					}
					break;
				case Constant.repeatState.isShuffle:
					location = (int) (Math.random() * (mp3Infos.size() - 1));break;
				}
				currentTime = 0;
				Intent sendIntent = new Intent();
				sendIntent.putExtra("currentTime", currentTime);
				sendIntent.putExtra("title", mp3Info.getTitle());
				sendIntent.putExtra("artist", mp3Info.getArtist());
				sendIntent.putExtra("duration", mp3Info.getDuration());
				play = Constant.playMSG.NEXT_MSG;
				broadcast(sendIntent); // 给PlayerActivity发送广播
				play();
			}
		});
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		Intent sendIntent = new Intent();	
		if(intent!=null){
			int flag = 0;int playState = intent.getIntExtra("MSG", 0);
			switch(playState){
			case Constant.playMSG.PLAY_MSG:
				if(mediaPlayer.isPlaying()){
					playState = Constant.playMSG.PAUSE_MSG;//接着pause
				}else{
					play();flag = 1;break;
				}
			case Constant.playMSG.PAUSE_MSG:
				mediaPlayer.pause();flag = 1;break;
			case Constant.playMSG.PREVIOUS_MSG:
				previous();flag = 1;break;
			case Constant.playMSG.NEXT_MSG:
				next();flag = 1;break;
			case Constant.playMSG.LOCATION_MSG:
				location(intent.getIntExtra("location", 0));flag = 1;break;
			case Constant.playMSG.PROGRESS_MSG:
				if(!mediaPlayer.isPlaying())break;
				currentTime = intent.getIntExtra("currentTime", 0);
				sendIntent.putExtra("currentTime", currentTime);
				mediaPlayer.seekTo(currentTime);
				break;
			case Constant.playMSG.REPEAT_MSG:
				repeat();
				sendIntent.putExtra("repeat", repeat);break;
			case Constant.playMSG.SHUFFLE_MSG:
				shuffle();
				sendIntent.putExtra("repeat", repeat);break;
			case Constant.playMSG.SINGER_MSG:
				int i = 0;
				for(Mp3Info m : mp3Infos){
					if(m.getUrl().equals(intent.getExtras().getString("url"))){
						location(i);break;
					}
					i++;
				}
				flag = 1;break;
			}
			if(flag == 1){
				sendIntent.putExtra("currentTime", currentTime);
				sendIntent.putExtra("title", mp3Info.getTitle());
				sendIntent.putExtra("artist", mp3Info.getArtist());
				sendIntent.putExtra("duration", mp3Info.getDuration());
				sendIntent.putExtra("Id",mp3Info.getId());
				sendIntent.putExtra("AlbumId",mp3Info.getAlbumId());
				sendIntent.putExtra("album", mp3Info.getAlbum());
			}
			play = playState;
			broadcast(sendIntent);
		}
		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
	}
	public void play(){
		try{
			if(currentTime > 0){
				mediaPlayer.start(); // 开始播放	
				mediaPlayer.seekTo(currentTime);return;
			}
			mediaPlayer.reset();
			mp3Info = mp3Infos.get(location);//TODO  更新mp3
			Log.v("playplay",location+"_"+mp3Info.getTitle()+"--"+mp3Info.getUrl());
			if(firstTime){
				firstTime = false;
			}else{
				initLrc();
			}		
			mediaPlayer.setDataSource(mp3Info.getUrl());
			mediaPlayer.prepare(); // 进行缓冲
			mediaPlayer.setOnPreparedListener(new OnPreparedListener(){
				@Override
				public void onPrepared(MediaPlayer mp){
					mediaPlayer.start(); // 开始播放	
				}
			});
			handler.sendEmptyMessage(1);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
//	public void singer(){
//		
//		play();
//	}
	public void previous(){
		location--;
		if(location == -1) location = mp3Infos.size()-1;
		currentTime = 0;
		play();
	}
	public void next(){
		location++;
		if(location == mp3Infos.size()) location = 0;
		currentTime = 0;
		play();
	}
	public void shuffle(){
		if(repeat == Constant.repeatState.isShuffle){
			repeat = Constant.repeatState.isOrder;
		}else{
			repeat = Constant.repeatState.isShuffle;
		}
	}
	public void repeat(){
		switch(repeat){
		case Constant.repeatState.isShuffle:
		case Constant.repeatState.isOrder:
			repeat = Constant.repeatState.isCurrentRepeat;break;
		case Constant.repeatState.isCurrentRepeat:
			repeat = Constant.repeatState.isAllRepeat;break;
		case Constant.repeatState.isAllRepeat:
			repeat = Constant.repeatState.isOrder;break;
		}
	}
	public void location(int position){
		location = position;
		currentTime = 0;
		play(); 
	}
	@Override
	public void onDestroy(){
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == Constant.BUTTON_ACTION){
				onStartCommand(intent,0,0);
			}
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
