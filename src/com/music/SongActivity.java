
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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

public class SongActivity extends Activity {

	ListView listView;
	SimpleAdapter simpleAdapter;
    List<Map<String,Object>>  datalists;

//    List<List<Map<String, Object>>> datalist = new ArrayList<List<Map<String, Object>>>();
	List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
	Map<String, Object> map =  new HashMap<String, Object>();
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_2);
		listView = (ListView) findViewById(R.id.song_info);
//		datalist = Constant.getSingerInfo(this);
		listView.setAdapter(new MyAdapter1(this));
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sendIntent(Constant.playMSG.SINGER_MSG,position);
				Intent intent = new Intent(SongActivity.this,PlayActivity.class);
				startActivity(intent);
			}
		});
		onResume();
	}
	@Override
	public void onResume(){
		super.onResume();
		Bundle bundle = getIntent().getExtras();
        SerializableMaplist serializableMap = (SerializableMaplist) bundle.get("map");
        maps = serializableMap.getList();
	}
	public void sendIntent(int MSG,int position){
		Intent intent = new Intent(this,PlayService.class);
		intent.setPackage(getPackageName());
		intent.putExtra("url", maps.get(position).get("url").toString());
		Log.v("uuuuuu",maps.get(position).get("url").toString());
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
			return maps.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.song_2_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imageview = (ImageView) convertView
						.findViewById(R.id.song_info_image);
				viewHolder.siner_name = (TextView) convertView
						.findViewById(R.id.song_info_name);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
//			viewHolder.imageview.setBackgroundResource(R.drawable.default_mini_singer);
			
			map = maps.get(position);
			viewHolder.siner_name.setText(map.get("title").toString());
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
