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
//	public static final String UPDATE_ACTION = "com.music.action.UPDATE_ACTION";  //���¶���  
	public static final String CTL_ACTION = "com.music.action.CTL_ACTION";        //���ƶ���,service����  
	public static final String LRC_ACTION = "com.music.action.LRC_ACTION";        //���Ž��ȿ��ƶ���,service����  
//	public static final String MUSIC_CURRENT = "com.music.action.MUSIC_CURRENT";  //���ֵ�ǰʱ��ı䶯��  
//	public static final String MUSIC_DURATION = "com.playerm.action.MUSIC_DURATION";//���ֲ��ų��ȸı䶯��  
//	/** ֪ͨ����ť����¼���Ӧ��ACTION */
	public final static String BUTTON_ACTION = "com.music.action.BUTTON_ACTION"; //֪ͨ����ť����¼���Ӧ��ACTION,service��Ӧ
	public static final String SERVICE_ACTION = "com.music.media.MUSIC_SERVICE"; //��̨����service��Ӧ��ACTION,service��Ӧ
	public class playMSG{ 	
		public static final int PLAY_MSG = 0;		//����
		public static final int PAUSE_MSG = 1;		//��ͣ
		public static final int PREVIOUS_MSG = 2;	//��һ��
		public static final int NEXT_MSG = 3;		//��һ��
		public static final int REPEAT_MSG = 4;		//
		public static final int SHUFFLE_MSG = 5;	//
		public static final int LOCATION_MSG=6;
		public static final int PROGRESS_MSG = 7;//���ȸı�
		public static final int SINGER_MSG = 8;//���ȸı�
	}
	public class repeatState{
		public static final int isOrder = 0; // ���ظ�����
		public static final int isCurrentRepeat = 1; // ����ѭ��
		public static final int isAllRepeat = 2; // ȫ��ѭ��
		public static final int isShuffle = 3;// �������
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
	 * �������ʱ��
	 * ������ݸ�ʽ���£�
	 * [00:02.32]����Ѹ
	 * [00:03.43]�þò���
	 * [00:05.22]�������  ����
	 * @param timeStr
	 * @return
	 */
	public static int time2Str(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");
		
		String timeData[] = timeStr.split("@");	//��ʱ��ָ����ַ�������
		
		//������֡��벢ת��Ϊ����
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);
		
		//������һ������һ�е�ʱ��ת��Ϊ������
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}
	/**
	 * ��List���������Map�������ݣ�ÿһ��Map������һ�����ֵ���������
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
	 * ��ʽ��ʱ�䣬������ת��Ϊ��:���ʽ
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
	 * ��ȡר������λͼ����
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
				//���ƶ�ԭʼ��С
				options.inSampleSize = 1;
				//ֻ���д�С�ж�
				options.inJustDecodeBounds = true;
				//���ô˷����õ�options�õ�ͼƬ�Ĵ�С
				BitmapFactory.decodeStream(in, null, options);
				/** ���ǵ�Ŀ��������N pixel�Ļ�������ʾ�� ������Ҫ����computeSampleSize�õ�ͼƬ���ŵı��� **/
				/** �����targetΪ800�Ǹ���Ĭ��ר��ͼƬ��С�����ģ�800ֻ�ǲ������ֵ���������������Ľ�� **/
				if(small){
					options.inSampleSize = computeSampleSize(options, 40);
				} else{
					options.inSampleSize = computeSampleSize(options, 600);
				}
				// ���ǵõ������ű��������ڿ�ʼ��ʽ����Bitmap����
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
	 * ��ȡĬ��ר��ͼƬ
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultArtwork(Context context,boolean small) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		if(small){	//����СͼƬ
			return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.music5), null, opts);
		}
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
	}
	/**
	 * ��ͼƬ���к��ʵ�����
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
	 * ���ļ����л�ȡר������λͼ
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
			// ֻ���д�С�ж�
			options.inJustDecodeBounds = true;
			// ���ô˷����õ�options�õ�ͼƬ��С
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// ���ǵ�Ŀ������800pixel�Ļ�������ʾ
			// ������Ҫ����computeSampleSize�õ�ͼƬ���ŵı���
			options.inSampleSize = 100;
			// ���ǵõ������ŵı��������ڿ�ʼ��ʽ����Bitmap����
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			//����options��������������Ҫ���ڴ�
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bm;
	}
	/**
	 * ���ڴ����ݿ��в�ѯ��������Ϣ��������List����
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
					.getColumnIndex(MediaStore.Audio.Media._ID));	//����id
			String title = cursor.getString((cursor	
					.getColumnIndex(MediaStore.Audio.Media.TITLE))); // ���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // ������
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));	//ר��
			String displayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION)); // ʱ��
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // �ļ�·��
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // �Ƿ�Ϊ����
			if (isMusic != 0) { // ֻ��������ӵ����ϵ���
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
							.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
	
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
