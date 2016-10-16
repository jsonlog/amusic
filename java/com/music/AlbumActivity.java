
package com.music;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.music.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumActivity extends Activity {

    ListView listView;
    SimpleAdapter simpleAdapter;
    List<Map<String, Object>> datalists;

    List<List<Map<String, Object>>> datalist = new ArrayList<>();
    List<Map<String, Object>> maps = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.music_1);
        listView = (ListView) findViewById(R.id.album_info);
        datalist = Constant.getAlbumInfo(this);
        listView.setAdapter(new MyAdapter1(this));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                maps = datalist.get(position);//更新maps
                if (maps.size() == 1) {
                    sendIntent(Constant.playMSG.ALBUM_MSG);
                    Intent intent = new Intent(AlbumActivity.this, PlayActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AlbumActivity.this, MusicActivity.class);

                    //传递数据
                    final Constant.SerializableMaplist myMap = new Constant.SerializableMaplist();
                    myMap.setList(maps);//将map数据添加封装的myMap中
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("map", myMap);
                    intent.putExtras(bundle);
                    Window w = ThirdGroupTab.group.getLocalActivityManager()
                            .startActivity("MusicActivity", intent);
                    View views = w.getDecorView();
                    //设置要跳转的Activity显示为本ActivityGroup的内容
                    ThirdGroupTab.group.setContentView(views);
                }
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();
        }
        return false;
    }

    protected void dialog() {
        new AlertDialog.Builder(AlbumActivity.this).
                setTitle(R.string.info).
                setMessage(R.string.dialog_messenge).
                setNegativeButton(R.string.confrim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AlbumActivity.this, PlayService.class);
                        stopService(intent);
                        finish();
                    }
                }).setPositiveButton(R.string.cancel, null).show();
    }

    public void sendIntent(int MSG) {
        Intent intent = new Intent(this, PlayService.class);
        intent.setPackage(getPackageName());
        intent.putExtra("url", maps.get(0).get("url").toString());
        Log.v("uuuuuu", maps.get(0).get("url").toString());
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
            viewHolder.siner_name.setText(map.get("album").toString());
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
