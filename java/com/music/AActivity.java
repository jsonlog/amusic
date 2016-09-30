package com.music;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
public class AActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_aaitivity);
        TextView tv = new TextView(this);
        tv.setText("This is A Activity!");
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
    }
}