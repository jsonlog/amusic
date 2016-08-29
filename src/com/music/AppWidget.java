/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.music;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.os.Bundle;
import android.net.Uri;
import android.widget.Toast;
import android.util.Log;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.music.util.Constant;
//import com.play.AudioPlayerHolder;
//import com.play.MainActivity;
//import com.play.service.ApolloService;
/**
 * Simple widget to show currently playing album art along with play/pause and
 * next track buttons.
 */
public class AppWidget extends AppWidgetProvider {
	private static String WIDGET_STYLE = "";
    private static final String TAG = "ExampleAppWidgetProvider";

    private boolean DEBUG = false; 
    // ����ExampleAppWidgetService�����Ӧ��action
    ///private final Intent EXAMPLE_SERVICE_INTENT = 
            //new Intent("com.playerm.util.PlayerService");
//	    // ���� widget �Ĺ㲥��Ӧ��action
//	    private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
    // ���� widget ��id��HashSet��ÿ�½�һ�� widget ����Ϊ�� widget ����һ�� id��
    private static Set idsSet = new HashSet();
    // ��ť��Ϣ
//	    private static final int BUTTON_REPEAT= 1;
//	    private static final int BUTTON_SHUFFLE=2;

    // onUpdate() �ڸ��� widget ʱ����ִ�У�
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	final RemoteViews views = new RemoteViews(context.getPackageName(),
                (WIDGET_STYLE.equals(context.getResources().getString(R.string.widget_style_light))?
                		R.layout.fourbytwo_app_widget:R.layout.fourbytwo_app_widget_dark));
    	
        Log.d(TAG, "onUpdate(): appWidgetIds.length="+appWidgetIds.length);

        // ÿ�� widget ������ʱ����Ӧ�Ľ�widget��id���ӵ�set��
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
            Log.d(TAG, "onUpdate(): appWidgetIds="+appWidgetId);
        }
        prtSet();
        
        views.setTextViewText(R.id.four_by_two_albumname, "title");
		views.setTextViewText(R.id.four_by_two_artistname, "arts");
    }
    
    // �� widget ���������� ���� �� widget �Ĵ�С���ı�ʱ�������� 
    @Override  
    public void onAppWidgetOptionsChanged(Context context,  
            AppWidgetManager appWidgetManager, int appWidgetId,  
            Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId ,  
                newOptions);  
    }  
    
    // widget��ɾ��ʱ����  
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
        Log.d(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);

        // �� widget ��ɾ��ʱ����Ӧ��ɾ��set�б����widget��id
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        prtSet();
        
        super.onDeleted(context, appWidgetIds);  
    }

    // ��һ��widget������ʱ����  
    @Override  
    public void onEnabled(Context context) {  
 	    WIDGET_STYLE = context.getResources().getString(R.string.widget_style_light);
        Log.d(TAG, "onEnabled");
        // �ڵ�һ�� widget ������ʱ����������
      //  context.startService(EXAMPLE_SERVICE_INTENT);
        
        super.onEnabled(context);  
    }  
    
    // ���һ��widget��ɾ��ʱ����  
    @Override  
    public void onDisabled(Context context) {  
        Log.d(TAG, "onDisabled");

        // �����һ�� widget ��ɾ��ʱ����ֹ����
       // context.stopService(EXAMPLE_SERVICE_INTENT);

        super.onDisabled(context);  
    }
    // �����ã�����set
    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator it = idsSet.iterator();
            Log.d(TAG, "total:"+size);
            while (it.hasNext()) {
                Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
            }
        }
    }
    private int listPosition = 0;
    
    // ���չ㲥�Ļص�����
    @Override  
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
		if(action.equals(Constant.CTL_ACTION)){
			final RemoteViews views = new RemoteViews(context.getPackageName(),
	                (WIDGET_STYLE.equals(context.getResources().getString(R.string.widget_style_dark))?
	                		R.layout.fourbytwo_app_widget_dark:R.layout.fourbytwo_app_widget));
	    	Intent buttonIntent = new Intent(Constant.BUTTON_ACTION);
	    	buttonIntent.putExtra("MSG", Constant.playMSG.PREVIOUS_MSG);
			PendingIntent intent_prev = PendingIntent.getBroadcast(context, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.four_by_two_control_prev, intent_prev);
	    	
			buttonIntent.putExtra("MSG", Constant.playMSG.PLAY_MSG);
			PendingIntent intent_paly = PendingIntent.getBroadcast(context, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.four_by_two_control_play, intent_paly);
			
			buttonIntent.putExtra("MSG", Constant.playMSG.NEXT_MSG);
			PendingIntent intent_next = PendingIntent.getBroadcast(context, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.four_by_two_control_next, intent_next);

			buttonIntent.putExtra("MSG", Constant.playMSG.REPEAT_MSG);
			PendingIntent intent_repeat= PendingIntent.getBroadcast(context, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.four_by_two_control_repeat, intent_repeat);
			
			buttonIntent.putExtra("MSG", Constant.playMSG.SHUFFLE_MSG);
			PendingIntent intent_shuffle = PendingIntent.getBroadcast(context, 5, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.four_by_two_control_shuffle, intent_shuffle);

			switch(intent.getIntExtra("MSG", 0)){
			case Constant.playMSG.SHUFFLE_MSG:
				switch(intent.getIntExtra("repeat", 0)){
				case Constant.repeatState.isShuffle:
					Toast.makeText(context, "��������Ѵ�", Toast.LENGTH_SHORT).show();
					views.setImageViewResource(R.id.four_by_two_control_shuffle,R.drawable.apollo_holo_light_shuffle_on);
					break;
				case Constant.repeatState.isOrder:
					Toast.makeText(context, "��������ѹر�", Toast.LENGTH_SHORT).show();
					views.setImageViewResource(R.id.four_by_two_control_shuffle,R.drawable.apollo_holo_light_shuffle_normal);
					break;
				}
				views.setImageViewResource(R.id.four_by_two_control_repeat,R.drawable.apollo_holo_light_repeat_normal);
				break;
			case Constant.playMSG.REPEAT_MSG:
				switch(intent.getIntExtra("repeat", 0)){
				case Constant.repeatState.isCurrentRepeat:
					Toast.makeText(context, "�ظ������Ѵ�", Toast.LENGTH_SHORT).show();
					views.setImageViewResource(R.id.four_by_two_control_repeat,R.drawable.apollo_holo_light_repeat_one);
					break;
				case Constant.repeatState.isAllRepeat:
					Toast.makeText(context, "ѭ�������Ѵ�", Toast.LENGTH_SHORT).show();
					views.setImageViewResource(R.id.four_by_two_control_repeat,R.drawable.apollo_holo_light_repeat_all);
					break;
				case Constant.repeatState.isOrder:
					Toast.makeText(context, "ѭ�������ѹر�", Toast.LENGTH_SHORT).show();
					views.setImageViewResource(R.id.four_by_two_control_repeat,R.drawable.apollo_holo_light_repeat_normal);
					break;
				}
				views.setImageViewResource(R.id.four_by_two_control_shuffle,R.drawable.apollo_holo_light_shuffle_normal);
				break;
			case Constant.playMSG.PLAY_MSG:
			case Constant.playMSG.PAUSE_MSG:
			case Constant.playMSG.PREVIOUS_MSG:
			case Constant.playMSG.NEXT_MSG:
			case Constant.playMSG.LOCATION_MSG:
				String title = intent.getExtras().getString("title");
				String artist = intent.getExtras().getString("artist");
				String album = intent.getExtras().getString("album");
				if(intent.getIntExtra("MSG", 0) == Constant.playMSG.PAUSE_MSG){
					views.setImageViewResource(R.id.four_by_two_control_play, R.drawable.apollo_holo_light_play);
				}else{
					views.setImageViewResource(R.id.four_by_two_control_play, R.drawable.apollo_holo_light_pause);
				}
				views.setTextViewText(R.id.four_by_two_artistname, artist);
				views.setTextViewText(R.id.four_by_two_albumname, album);
				views.setTextViewText(R.id.four_by_two_trackname, title);
				Bitmap bm = Constant.getArtwork(context, intent.getLongExtra("Id", 0),intent.getLongExtra("AlbumId", 0), true, false);
				views.setImageViewBitmap(R.id.four_by_two_albumart, bm);
				break;
			}
			Iterator<Integer> it = idsSet.iterator();int appID = 0;
			while(it.hasNext()){
				appID = ((Integer)it.next()).intValue();
			}
			AppWidgetManager.getInstance(context).updateAppWidget(appID, views);  
		}
        super.onReceive(context, intent);  
    }  
}