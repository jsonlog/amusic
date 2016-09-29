
package com.music;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.music.R;
import com.music.util.Constant;
import com.music.util.Constant.SerializableMaplist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import android.app.Activity;

public class SingActivity extends Activity {

	ListView listView;
	SimpleAdapter simpleAdapter;
	List<Map<String,Object>>  datalists;

	List<List<Map<String, Object>>> datalist = new ArrayList<List<Map<String, Object>>>();
	List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
	Map<String, Object> map =  new HashMap<String, Object>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_1);
		listView = (ListView) findViewById(R.id.singer_info);
		datalist = Constant.getSingerInfo(this);
		listView.setAdapter(new MyAdapter1(this));
//		Toast.makeText(this, "歌手",Toast.LENGTH_SHORT).show();
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				maps = datalist.get(position);//更新maps
				if(maps.size() == 1){
					sendIntent(Constant.playMSG.SINGER_MSG);
					Intent intent = new Intent(SingActivity.this,PlayActivity.class);
					startActivity(intent);
				}
				else{
					Intent intent = new Intent(SingActivity.this,SongActivity.class);

					//传递数据
					final Constant.SerializableMaplist myMap = new Constant.SerializableMaplist();
					myMap.setList(maps);//将map数据添加封装的myMap中
					Bundle bundle=new Bundle();
					bundle.putSerializable("map", myMap);
					intent.putExtras(bundle);
//					startActivity(intent);


					Window w = SecondGroupTab.group.getLocalActivityManager()
							.startActivity("SongActivity",intent);
					View views = w.getDecorView();
					//设置要跳转的Activity显示为本ActivityGroup的内容
					SecondGroupTab.group.setContentView(views);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    Holder.linearLayout.removeAllViews();
//                    View view = getLocalActivityManager().startActivity(
//                            PollingManagementActivitySub.class.getSimpleName(), intent)
//                            .getDecorView();
//                    Holder.linearLayout.addView(view);
				}
			}
		});

	}
	//	Map<String, String[]> map;
//	Bundle bundle = new Bundle();
//	Set<String> keySet = map.keySet();
//	Iterator<String> iter = keySet.iterator();
//	while(iter.hasNext())
//	{
//	    String key = iter.next();
//	    bundle.putStringArray(key, map.get(key));
//	}
//	intent.putExtra("map", bundle);
//
//
//	获取的方法如下：
//	[java] view plain copy
//	Map<String, String[]> map;
//	Bundle bundle = intent.getBundleExtra("map");
//	Set<String> keySet = bundle.keySet();   // 得到bundle中所有的key
//	Iterator<String> iter = keySet.iterator();  
//	while(iter.hasNext())  
//	{  
//	    String key = iter.next();  
//	    map.put(key, bundle.getStringArray(key));  
//	}
	public void sendIntent(int MSG){
		Intent intent = new Intent(this,PlayService.class);
		intent.setPackage(getPackageName());
		intent.putExtra("url", maps.get(0).get("url").toString());
		Log.v("uuuuuu",maps.get(0).get("url").toString());
		intent.setAction(Constant.SERVICE_ACTION);
		intent.putExtra("MSG", MSG);
		startService(intent);
	}
	private class MyAdapter1 extends BaseAdapter {
		LayoutInflater inflater;
		Context context;
		public MyAdapter1(Context c) {
			this.inflater = LayoutInflater.from(c);
			this.context = c;
		}

		public int getCount() {
			return datalist.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView,
							ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.song_1_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imageview = (ImageView) convertView
						.findViewById(R.id.singer_info_image);
				viewHolder.siner_name = (TextView) convertView
						.findViewById(R.id.singer_info_name);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			map = datalist.get(position).get(0);
//			for(Map<String, Object> m : maps){
//				if(!map.get("title").toString().contains(m.get("title").toString()))
//				map.put("title",map.get("title").toString()+"--"+m.get("title").toString());
//			}
			viewHolder.siner_name.setText(map.get("artist").toString());
			String id = map.get("id").toString();
			String albumId = map.get("albumId").toString();
			long Id = Long.valueOf(id);
			long AlbumId = Long.valueOf(albumId);
			Bitmap bm = Constant.getArtwork(this.context, Id, AlbumId, true, true);
			viewHolder.imageview.setImageBitmap(bm);

			return convertView;
		}

		class ViewHolder {
			ImageView imageview;
			TextView siner_name;

		}
	}
}
