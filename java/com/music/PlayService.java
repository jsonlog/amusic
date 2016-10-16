package com.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.music.util.Constant;
import com.music.util.LrcContent;
import com.music.util.Mp3Info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PlayService extends Service {
    private MediaPlayer mediaPlayer = getMediaPlayer(this);//= new MediaPlayer(); /* 媒体播放器对象*/
    public static List<Mp3Info> mp3Infos;	/*存放Mp3Info对象的集合*/
    private Mp3Info mp3Info;
    private int location = 0;
    private boolean play = true;
    private int repeat = Constant.repeatState.isOrder;
    private int currentTime = 0;
    private int duration = 0;
    private Receiver receiver;
    private boolean firstTime;
    LrcContent mLrcContent = new LrcContent();
    int count = 1;
    private List<LrcContent> lrcList = new ArrayList<>(); /*存放歌词列表对象 **/
    /*handler用来接收消息，来发送广播更新播放时间*/
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) { /*			if(msg.getData().getInt
        ("duration", 0) > 0){*/
            if (msg.what == 1) {
                currentTime = mediaPlayer.getCurrentPosition(); /* 获取当前音乐播放的位置*/
                Intent sendIntent = new Intent();
                sendIntent.putExtra("currentTime", currentTime);
                sendIntent.putExtra("lrcIndex", lrcIndex());
                sendIntent.setPackage(getPackageName());
                sendIntent.setAction(Constant.LRC_ACTION);
                if (firstTime) {
                    final Constant.SerializableList list = new Constant.SerializableList();
                    list.setList(lrcList);/*将map数据添加封装的myMap中*/
                    Bundle extras = new Bundle();
                    extras.putSerializable("list", list);
                    sendIntent.putExtras(extras);
                    sendIntent.putExtra("firstTime", firstTime);
                    Log.v("playing", firstTime + "");
                    firstTime = false;
                    sendIntent.putExtra("title", mp3Info.getTitle());
                    sendIntent.putExtra("artist", mp3Info.getArtist());
                    sendIntent.putExtra("duration", mp3Info.getDuration());
                }
                sendBroadcast(sendIntent);
                if(currentTime < duration) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
            Log.v("playing", mediaPlayer.isPlaying() + "++" + currentTime);
        }
    };

    public void broadcast(Intent intent , int playMSG) {
        intent.setPackage(getPackageName());
        intent.setAction(Constant.CTL_ACTION);
        intent.putExtra("MSG", playMSG);
        sendBroadcast(intent);
        Log.v("MSG", playMSG + "");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp3Infos = Constant.getMp3Infos(this); /*		mp3Info = mp3Infos.get(location);*/
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BUTTON_ACTION); /* 注册BroadcastReceiver*/
        registerReceiver(receiver, filter);
        //media not release
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (repeat) {
                    case Constant.repeatState.isOrder:
                        next();
                        break;
                    case Constant.repeatState.isCurrentRepeat:
                        break;
                    case Constant.repeatState.isAllRepeat:
                        location++;
                        if (location > mp3Infos.size() - 1)

                            location = 0;
                        break;
                    case Constant.repeatState.isShuffle:
                        location = (int) (Math.random() * (mp3Infos.size() - 1));
                        break;
                }
                currentTime = 0;
                Intent sendIntent = new Intent();
                sendIntent.putExtra("currentTime", currentTime);
                sendIntent.putExtra("title", mp3Info.getTitle());
                sendIntent.putExtra("artist", mp3Info.getArtist());
                sendIntent.putExtra("duration", mp3Info.getDuration());
                broadcast(sendIntent,Constant.playMSG.NEXT_MSG); // 给PlayerActivity发送广播
                play();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent sendIntent = new Intent();
        if (intent != null) {
            int flag = 0;
            int playState = intent.getIntExtra("MSG", 0);
            switch (playState) {
                case Constant.playMSG.PLAY_MSG:
                    flag = 1;
                    if (!play) {
                        play();
                        break;
                    }
                    play = false;
                    playState = Constant.playMSG.PAUSE_MSG;//接着pause
//                case Constant.playMSG.PAUSE_MSG:
                    mediaPlayer.pause();
                    break;
                case Constant.playMSG.PREVIOUS_MSG:
                    previous();
                    flag = 1;
                    break;
                case Constant.playMSG.NEXT_MSG:
                    next();
                    flag = 1;
                    break;
                case Constant.playMSG.LOCATION_MSG:
                    location(intent.getIntExtra("location", 0));
                    flag = 1;
                    break;
                case Constant.playMSG.PROGRESS_MSG:
                    if (!play) break;
                    currentTime = intent.getIntExtra("currentTime", 0);
                    sendIntent.putExtra("currentTime", currentTime);
                    mediaPlayer.seekTo(currentTime);
                    break;//TODO
                case Constant.playMSG.REPEAT_MSG:
                    repeat();
                    sendIntent.putExtra("repeat", repeat);
                    break;
                case Constant.playMSG.SHUFFLE_MSG:
                    shuffle();
                    sendIntent.putExtra("repeat", repeat);
                    break;
                case Constant.playMSG.SINGER_MSG:
                    int i = 0;
                    for (Mp3Info m : mp3Infos) {
                        if (m.getUrl().equals(intent.getExtras().getString("url"))) {
                            location(i);
                            break;
                        }
                        i++;
                    }
                    flag = 1;
                    break;

                case Constant.playMSG.ALBUM_MSG:
                    int j = 0;
                    for (Mp3Info m : mp3Infos) {
                        if (m.getUrl().equals(intent.getExtras().getString("url"))) {
                            location(j);
                            break;
                        }
                        j++;
                    }
                    flag = 1;
                    break;
            }
            if (flag == 1) {
                sendIntent.putExtra("currentTime", currentTime);
                sendIntent.putExtra("title", mp3Info.getTitle());
                sendIntent.putExtra("artist", mp3Info.getArtist());
                sendIntent.putExtra("duration", mp3Info.getDuration());
                sendIntent.putExtra("Id", mp3Info.getId());
                sendIntent.putExtra("AlbumId", mp3Info.getAlbumId());
                sendIntent.putExtra("album", mp3Info.getAlbum());
            }
            broadcast(sendIntent,playState);
        }
        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY | Service.START_FLAG_RETRY, startId);
        //Error:(199, 39) Must be one or more of: Service.START_FLAG_REDELIVERY, Service
        // .START_FLAG_RETRY
        //Service.START_REDELIVER_INTENT
    }

    public void play() {
        try {
            play = true;
            if (currentTime > 0) {
                mediaPlayer.start(); // 开始播放
                mediaPlayer.seekTo(currentTime);
                return;
            }
            mediaPlayer.reset();
            mp3Info = mp3Infos.get(location);
            duration = mp3Info.getDuration();
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			MediaPlayer mp = MediaPlayer.create(MainActivity.this,R.raw.baobao);
            //mediaPlayer.prepare();
//			SubtitleController sc = new SubtitleController(context, null, null);
//			sc.mHandler = new Handler();
//			mediaplayer.setSubtitleAnchor(sc, null)
            mediaPlayer.setDataSource(mp3Info.getUrl());
            mediaPlayer.prepareAsync();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start(); // 开始播放
                }
            });
            initLrc();
            Log.v("initlrc", location + "_" + mp3Info.getTitle() + "--" + mp3Info.getUrl());//TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static MediaPlayer getMediaPlayer(Context context) {

        MediaPlayer mediaplayer = new MediaPlayer();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media" +
                    ".SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media" +
                    ".SubtitleController$Listener");

            Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context
                    .class, cMediaTimeProvider, iSubtitleControllerListener});

            Object subtitleInstance = constructor.newInstance(context, null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
            //Log.e("", "subtitle is setted :p");
        } catch (Exception e) {
        }

        return mediaplayer;
    }

    public void previous() {
        location--;
        if (location == -1) location = mp3Infos.size() - 1;
        currentTime = 0;
        play();
    }

    public void next() {
        location++;
        if (location == mp3Infos.size()) location = 0;
        currentTime = 0;
        play();
    }

    public void shuffle() {
        if (repeat == Constant.repeatState.isShuffle) {
            repeat = Constant.repeatState.isOrder;
        } else {
            repeat = Constant.repeatState.isShuffle;
        }
    }

    public void repeat() {
        switch (repeat) {
            case Constant.repeatState.isShuffle:
            case Constant.repeatState.isOrder:
                repeat = Constant.repeatState.isCurrentRepeat;
                break;
            case Constant.repeatState.isCurrentRepeat:
                repeat = Constant.repeatState.isAllRepeat;
                break;
            case Constant.repeatState.isAllRepeat:
                repeat = Constant.repeatState.isOrder;
                break;
        }
    }

    public void location(int position) {
        location = position;
        currentTime = 0;
        play();
    }


    public void initLrc() {
        firstTime = true;
        lrcList = new ArrayList<>();

        //定义一个StringBuilder对象，用来存放歌词内容
        StringBuilder stringBuilder = new StringBuilder();

        try {
            File f = new File(mp3Info.getUrl().replace(".mp3", ".lrc"));
            //创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s;
            while ((s = br.readLine()) != null) {
                //替换字符
                s = s.replace("[", "");
                s = s.replace("]", "@");

                //分离“@”字符
                String splitLrcData[] = s.split("@");
                if (splitLrcData.length > 1) {
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
//		return stringBuilder.toString();
//		PlayActivity.lrcView.setmLrcList(lrcList);
//		PlayActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(PlayService.this,R.anim
// .alpha_z));
        handler.sendEmptyMessage(1);
        handler.postDelayed(mRunnable, 8000);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(mRunnable, 100);//100
        }
    };

    /**
     * 根据时间获取歌词显示的索引值
     *
     */
    public int lrcIndex() {
//		int duration = 1000;
        int index = 0;
//		if(mediaPlayer.isPlaying()) {
        currentTime = mediaPlayer.getCurrentPosition();
//		}
        if (currentTime < duration) {
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

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BUTTON_ACTION)) {
                onStartCommand(intent, 0, 0);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
