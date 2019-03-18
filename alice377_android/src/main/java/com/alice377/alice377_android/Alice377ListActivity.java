package com.alice377.alice377_android;

import android.annotation.SuppressLint;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by T170337 on 2018/3/23.
 */

public class Alice377ListActivity extends ExpandableListActivity {

    private View alice377view;
    private Alice377AlertDialog alice377AlertDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();

        setupViewComponent(); //自定義
    }

    //自定義
    @SuppressLint("InflateParams")
    private void setupViewComponent() {
        alice377view = LayoutInflater.from(Alice377ListActivity.this)
                .inflate(R.layout.alice377_list_menu_table, null); //自定義Layout:dialog

        alice377AlertDialog2 = new Alice377AlertDialog(Alice377ListActivity.this);
        alice377AlertDialog2.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
//        m_table_T_show = reponview.findViewById(R.id.table_T_show); //顯示使用者選擇的table
//        creat_double_listview(); //二階展開式項目生成
        alice377AlertDialog2.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog2.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog2.getWindow().setAttributes(params); //dialog參數綁定

//        reponAlertDialog.dismiss(); //關閉dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
