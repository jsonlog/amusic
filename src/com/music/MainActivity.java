package com.music;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

import com.music.R;

public class MainActivity extends TabActivity implements OnCheckedChangeListener{
	private static final int SWIPE_MIN_DISTANCE = 120;  
    private static final int SWIPE_MAX_OFF_PATH = 250;  
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;  
    private GestureDetector gestureDetector;  
    View.OnTouchListener gestureListener;  
       
    private static int maxTabIndex = 2;  
	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintabs);
        
        this.mAIntent = new Intent(this,HomeActivity.class);
        this.mBIntent = new Intent(this,PlayActivity.class);
//        this.mAIntent = new Intent(this,PlayActivity.class);
//        this.mBIntent = new Intent(this,HomeActivity.class);
        this.mCIntent = new Intent(this,AActivity.class);
        
		((RadioButton) findViewById(R.id.radio_button0))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button1))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button2))
		.setOnCheckedChangeListener(this);
        
        setupIntent();

    	//�����¼�  
        gestureDetector = new GestureDetector(new TabGestureDetector());    
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.mTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_button1:
				this.mTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_button2:
				this.mTabHost.setCurrentTabByTag("C_TAB");
				break;
			}
		}
		
	}
	
	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_home,
				R.drawable.icon_1_n, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_news,
				R.drawable.icon_1_n, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB",
				R.string.main_manage_date, R.drawable.icon_1_n,
				this.mCIntent));


        mTabHost.setCurrentTab(0);
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
    // ���һ����պ�ҳ��Ҳ�л���Ч��    
    private class TabGestureDetector extends SimpleOnGestureListener {    
        @Override    
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,    
                float velocityY) {    
             
            try {    
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)    
                    return false;    
                // right to left swipe    
                int currentView = mTabHost.getCurrentTab();  
                  
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE    
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {    
                    Log.i("test", "right" +currentView);    
                    if (currentView<maxTabIndex) {    
                        currentView++;  
                        mTabHost.setCurrentTab(currentView);    
                    }  
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE    
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {    
                    Log.i("test", "left" +currentView);    
                    if (currentView > 0) {    
                         currentView--;    
                         mTabHost.setCurrentTab(currentView);    
                    }  
                      
                }    
            } catch (Exception e) {    
                e.printStackTrace();  
            }    
            return false;    
        }    
    }    

    @Override    
    public boolean dispatchTouchEvent(MotionEvent event) {    
        if (gestureDetector.onTouchEvent(event)) {    
            Log.i("test", "---------------dispatch ... ");    
            event.setAction(MotionEvent.ACTION_CANCEL);    
        }    
        return super.dispatchTouchEvent(event);    
    }    
}
