package com.music;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

//第一个标签页显示的Activity，继承ActivityGroup，管理所有子Activity
public class ThirdGroupTab extends ActivityGroup {
    //用于管理本Group中的所有Activity
    public static ActivityGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = this;
    }

    @Override
    public void onBackPressed() {
        //把后退事件交给子Activity处理
        group.getLocalActivityManager().getCurrentActivity().onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //要跳转的Activity
        Intent intent = new Intent(this, AlbumActivity.class);
        // 把Activity转换成一个Window，然后转换成View
        Window w = group.getLocalActivityManager().startActivity(
                "AlbumActivity", intent);
        View view = w.getDecorView();
        //设置要跳转的Activity显示为本ActivityGroup的内容
        group.setContentView(view);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();
        }
        return false;
    }
    protected void dialog() {
        new AlertDialog.Builder(ThirdGroupTab.this).
                setTitle(R.string.info).
                setMessage(R.string.dialog_messenge).
                setNegativeButton(R.string.confrim, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        Intent intent = new Intent(ThirdGroupTab.this, PlayService.class);
                        stopService(intent);
                        finish();
                    }
                }).setPositiveButton(R.string.cancel, null).show();
    }
}
