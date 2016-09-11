package com.music.util;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.music.util.Mp3Info;
import com.music.R;

public class Constant {
	private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
//	public static final String UPDATE_ACTION = "com.music.action.UPDATE_ACTION";  //更新动作  
	public static final String CTL_ACTION = "com.music.action.CTL_ACTION";        //控制动作,service发出  
	public static final String LRC_ACTION = "com.music.action.LRC_ACTION";        //播放进度控制动作,service发出  
//	public static final String MUSIC_CURRENT = "com.music.action.MUSIC_CURRENT";  //音乐当前时间改变动作  
//	public static final String MUSIC_DURATION = "com.playerm.action.MUSIC_DURATION";//音乐播放长度改变动作  
//	/** 通知栏按钮点击事件对应的ACTION */
	public final static String BUTTON_ACTION = "com.music.action.BUTTON_ACTION"; //通知栏按钮点击事件对应的ACTION,service响应
	public static final String SERVICE_ACTION = "com.music.media.MUSIC_SERVICE"; //后台启动service对应的ACTION,service响应
	public class playMSG{ 	
		public static final int PLAY_MSG = 0;		//播放
		public static final int PAUSE_MSG = 1;		//暂停
		public static final int PREVIOUS_MSG = 2;	//上一首
		public static final int NEXT_MSG = 3;		//下一首
		public static final int REPEAT_MSG = 4;		//
		public static final int SHUFFLE_MSG = 5;	//
		public static final int LOCATION_MSG=6;
		public static final int PROGRESS_MSG = 7;//进度改变
		public static final int SINGER_MSG = 8;//进度改变
	}
	public class repeatState{
		public static final int isOrder = 0; // 无重复播放
		public static final int isCurrentRepeat = 1; // 单曲循环
		public static final int isAllRepeat = 2; // 全部循环
		public static final int isShuffle = 3;// 随机播放
	}
	public static class SerializableMaplist implements Serializable {
		  
	  private List<Map<String,Object>> list;

	  public List<Map<String, Object>> getList() {
	    return list;
	  }

	  public void setList(List<Map<String, Object>> list) {
	    this.list = list;
	  }

	}
	public static class SerializableList implements Serializable {
		
		private List<LrcContent> list;

		  public List<LrcContent> getList() {
		    return list;
		  }

		  public void setList(List<LrcContent> list) {
		    this.list = list;
		  }

		}
//	public class SerializableMap implements Serializable {
//
//	    private Map<String,Object> map;
//
//	    public Map<String, Object> getMap() {
//	        return map;
//	    }
//
//	    public void setMap(Map<String, Object> map) {
//	        this.map = map;
//	    }
//	}
	/**
	 * 解析歌词时间
	 * 歌词内容格式如下：
	 * [00:02.32]陈奕迅
	 * [00:03.43]好久不见
	 * [00:05.22]歌词制作  王涛
	 * @param timeStr
	 * @return
	 */
	public static int time2Str(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");
		
		String timeData[] = timeStr.split("@");	//将时间分隔成字符串数组
		
		//分离出分、秒并转换为整型
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);
		
		//计算上一行与下一行的时间转换为毫秒数
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}
	/**
	 * 往List集合中添加Map对象数据，每一个Map对象存放一首音乐的所有属性
	 * @param mp3Infos
	 * @return
	 */
	public static List<HashMap<String, String>> getMusicMaps(
			List<Mp3Info> mp3Infos) {
		List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
		for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", mp3Info.getTitle());
			map.put("Artist", mp3Info.getArtist());
			map.put("album", mp3Info.getAlbum());
			map.put("displayName", mp3Info.getDisplayName());
			map.put("albumId", String.valueOf(mp3Info.getAlbumId()));
			map.put("duration", formatTime(mp3Info.getDuration()));
			map.put("size", String.valueOf(mp3Info.getSize()));
			map.put("url", mp3Info.getUrl());
			mp3list.add(map);
		}
		return mp3list;
	}
	/**
	 * 格式化时间，将毫秒转换为分:秒格式
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60)) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}
	/**
	 * 获取专辑封面位图对象
	 * @param context
	 * @param song_id
	 * @param album_id
	 * @param allowdefalut
	 * @return
	 */
	public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small){
		if(album_id < 0) {
			if(song_id < 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if(bm != null) {
					return bm;
				}
			}
			if(allowdefalut) {
				return getDefaultArtwork(context, small);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
		if(uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				//先制定原始大小
				options.inSampleSize = 1;
				//只进行大小判断
				options.inJustDecodeBounds = true;
				//调用此方法得到options得到图片的大小
				BitmapFactory.decodeStream(in, null, options);
				/** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
				/** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
				if(small){
					options.inSampleSize = computeSampleSize(options, 40);
				} else{
					options.inSampleSize = computeSampleSize(options, 600);
				}
				// 我们得到了缩放比例，现在开始正式读入Bitmap数据
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException e) {
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if(bm != null) {
					if(bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if(bm == null && allowdefalut) {
							return getDefaultArtwork(context, small);
						}
					}
				} else if(allowdefalut) {
					bm = getDefaultArtwork(context, small);
				}
				return bm;
			} finally {
				try {
					if(in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * 获取默认专辑图片
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultArtwork(Context context,boolean small) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if(small){	//返回小图片
			return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.music5), null, opts);
		}
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
	}
	/**
	 * 对图片进行合适的缩放
	 * @param options
	 * @param target
	 * @return
	 */
	public static int computeSampleSize(Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if(candidate == 0) {
			return 1;
		}
		if(candidate > 1) {
			if((w > target) && (w / candidate) < target) {
				candidate -= 1;
			}
		}
		if(candidate > 1) {
			if((h > target) && (h / candidate) < target) {
				candidate -= 1;
			}
		}
		return candidate;
	}
	/**
	 * 从文件当中获取专辑封面位图
	 * @param context
	 * @param songid
	 * @param albumid
	 * @return
	 */
	private static Bitmap getArtworkFromFile(Context context, long songid, long albumid){
		Bitmap bm = null;
		if(albumid < 0 && songid < 0) {
			throw new IllegalArgumentException("Must specify an album or a song id");
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			FileDescriptor fd = null;
			if(albumid < 0){
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			//根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bm;
	}
	/**
	 * 用于从数据库中查询歌曲的信息，保存在List当中
	 * 
	 * @return
	 */
	public static List<Mp3Info> getMp3Infos(Context context) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));	//音乐id
			String title = cursor.getString((cursor	
					.getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));	//专辑
			String displayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
			if (isMusic != 0) { // 只把音乐添加到集合当中
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setArtist(artist);
				mp3Info.setAlbum(album);
				mp3Info.setDisplayName(displayName);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUrl(url);
				mp3Infos.add(mp3Info);
			}
		}
		return mp3Infos;
	}
	public static List<List<Map<String, Object>>> getSingerInfo(Context c) {
		List<Map<String, Object>> musicdata = new ArrayList<Map<String, Object>>();
		List<List<Map<String, Object>>> musicdatas = new ArrayList<List<Map<String, Object>>>();
//		Set<List<Map<String, Object>>> set = new LinkedHashSet<List<Map<String, Object>>>();
//		Set<String> set = new HashSet<String>();  
//		List<Map<String, Object>> musicdatas = new ArrayList<Map<String, Object>>();
//		Map<String, Object> maps = new HashMap<String, Object>();
		ContentResolver cr = c.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.ARTIST_ID);
		int count = 1;
		boolean isfirstTime = true;
		String artist = "";
		if (cursor != null && cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {

				String url = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

				if (url.endsWith(".mp3") || url.endsWith(".MP3")) {
					long id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
					String title = cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
	
					int time = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
	
					String name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
	
					String suffix = name
							.substring(name.length() - 4, name.length());
	
					String album = cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
					long albumid = cursor
							.getLong(cursor
									.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
	
					long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
	
					String singer = cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					if(singer.equals(artist)){
						count++;
					}else{
//						maps.put("count", count);
						artist = singer;
						if(isfirstTime){
							isfirstTime = !isfirstTime;
						}else{
							musicdatas.add(musicdata);
							count = 1;
							musicdata = new ArrayList<Map<String, Object>>();
						}
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", title);
					map.put("artist", singer);
					map.put("duration", time);
					map.put("id", id);
					map.put("displayName", name);
					map.put("url", url);
					map.put("albumId", albumid);
					map.put("album", album);
					map.put("size", size);
//					map.put("SUFFIX", suffix);
					musicdata.add(map);
				}
			}
//			if(count != 1){
				musicdatas.add(musicdata);
//			}
		}
//		Iterator<List<Map<String, Object>>> it = set.iterator();  
//		while (it.hasNext()) {  
//			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
//			maps = it.next(); 
//			Map<String, Object> mapp =  new HashMap<String, Object>();
//			mapp = maps.get(0);
//			Log.v("mmmmmmmmmmm",mapp.get("title").toString());
//		}  
		return musicdatas;

	}

}
