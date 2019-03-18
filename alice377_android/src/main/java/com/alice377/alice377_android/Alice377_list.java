package com.alice377.alice377_android;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alice377.alice377_android.providers.AppLogDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.alice377.alice377_android.Alice377_android.appname;
import static com.alice377.alice377_android.Alice377_android.catchdb;
import static com.alice377.alice377_android.Alice377_android.langnum;
import static com.alice377.alice377_android.Alice377_android.mobiletoday;
import static com.alice377.alice377_android.Alice377_android.uselog;
import static com.alice377.alice377_android.providers.AppLogDb.delete;
import static com.alice377.alice377_android.providers.AppLogDb.insert;
import static com.alice377.alice377_android.providers.AppLogDb.rawquery;

/**
 * Created by alice377 on 2019/3/18.
 */

public class Alice377_list extends AppCompatActivity {

    public static String myselecion = "";
    public static String myargs[] = new String[]{};
    public static String myorder = "id DESC"; //排序欄位
    //    private Uri uri = alice377_list_ContentProvider.CONTENT_URI; //user_action
    private final Context context = Alice377_list.this;
    private RelativeLayout m_alice377_list_R_logo, m_alice377_list_R_rel, m_alice377_list_R_query;
    private TextView m_alice377_list_T_gettoday, m_alice377_list_T_msg, m_alice377_list_T_loginmsg,
            m_alice377_list_T_msg2;
    private Spinner m_table_S_db, m_table_S_table;
    private EditText m_sql_E_write, m_table_E_sql_where, m_table_E_sql_order, m_alice377_list_E_sql_where,
            m_alice377_list_E_sql_order;
    private ListView m_alice377_list_L_menu, m_alice377_list_L_menu2;
    private String today = ""; //記錄今天的日期
    private View alice377view; //宣告自定義dialog
    private Alice377AlertDialog alice377AlertDialog, alice377AlertDialog2; //宣告dialog
    private EditText m_alice377_list_login_E_account, m_alice377_list_login_E_password;
    private long exitTime = 0;
    private SpannableString msgcolor; //宣告字串顏色方法變數
    //連線及SQLite相關宣告----------------------------------------------------------------------------
    private ContentResolver mContRes;
    private String[] USERCOLUMN = new String[]{"id", "app_name", "action_view", "action_action",
            "action_date", "status", "insert_date"};
    private String[] alice377DATACOLUMN = new String[]{"id", "so_date", "so_nbr", "so_nbr_step"};
    private String[] tablecolumn = USERCOLUMN; //預設為user_action
    private ArrayList<String> recSet = new ArrayList<String>(); //暫存arraylist的值
    private ArrayList<String> reclist = new ArrayList<String>(); //暫存arraylist的值
    private ArrayList<Map<String, Object>> mList = new ArrayList<Map<String, Object>>(); //儲存arraylist的值
    private Executor singleThreadExecutor = Executors.newSingleThreadExecutor(); //創建單一執行緒緒程池
    private int data = 0; //儲存SQLite撈到的總資料筆數
    private int data_show = 0; //儲存SQLite顯示的資料筆數(最多100筆)
    private String TAG = "alice377=>";
    private String user_write = ""; //儲存使用者輸入的SQLite語法
    //----------------------------------------------------------------------------------------------

    private ArrayAdapter<String> db_spinner = null; //存放資料庫陣列
    private ArrayAdapter<String> table_spinner = null;  //存放資料表陣列
    private String[] db_name = {"打包區", "電鍍委外", "維修通報", "維修助手"}; //資料庫中文清單
    private String[] db_id = {"packing_scan.db", "Elec_com.db", "main_bull.db", "main_at.db"}; //資料庫id清單
    private String[] table_id = {"packing_scan", "electroplating_commission", "maintenance_bulletin",
            "maintenance_assistant"}; //資料表id清單
    private int table_num = 0; //預設打包區
    private String[] packing_scan = {"台北廠", "雲科廠", "南俊商貿"}; //打包區資料表中文清單
    private String[] packing_scan_id = {"packing_scan_NJ", "packing_scan_RP", "packing_scan_SZ"}; //打包區db資料表清單
    private String[] electroplating_commission = {"台北廠", "雲科廠"}; //電鍍委外資料表中文清單
    private String[] electroplating_commission_id = {"Elec_com_NJ", "Elec_com_RP"}; //電鍍委外db資料表清單
    private String[] maintenance_bulletin = {"報修主表", "報修子表", "teamplus訊息表"}; //維修通報資料表中文清單
    private String[] maintenance_bulletin_id = {"main_bull_sentdata_m", "main_bull_sentdata_d",
            "main_bull_sentteamplus"}; //維修通報db資料表清單
    private String[] maintenance_assistant = {"報修主表", "報修子表", "報修teamplus訊息表", "維修主表",
            "維修子表", "維修teamplus訊息表"}; //維修助手資料表中文清單
    private String[] maintenance_assistant_id = {"main_at_sentdata_m", "main_at_sentdata_d",
            "main_at_sentteamplus", "main_at_main_data_m",
            "main_at_main_data_d", "main_at_main_data_teamplus"}; //維修助手db資料表清單
    private int subtable_num = 0; //預設台北
    private String sql_where = ""; //儲存使用者輸入的sqlite_where語法
    private String sql_order = ""; //儲存使用者輸入的sql_order語法
    private int table_page = 0; //記錄工程模式-資料表查詢頁面：1=使用中
    private int first_create = 0; //記錄SQLite_table查詢初次使用情形：0=初次使用
    //login提示訊息監聽
    private TextView.OnClickListener showlogin = new TextView.OnClickListener() {

        @Override
        public void onClick(View v) {
            alice377dialog(); //開啟工程模式登入視窗
            alice377log("Alice377_list login_layout start.", 1); //寫log
        }
    };
    //資料表選項監聽
    private Spinner.OnItemSelectedListener tablechoice = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            subtable_num = position; //記錄選擇第幾項
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    //資料庫db選項監聽
    private Spinner.OnItemSelectedListener dbchoice = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            table_num = position; //記錄選擇第幾項

            switch (table_id[table_num]) {
                case "packing_scan": //打包區
                    table_spinner = new ArrayAdapter<String>(context,
                            R.layout.table_simple_spinner_item, packing_scan);

                    if (subtable_num > 2)
                        subtable_num = 0; //打包區只有三個選項所以回到預設值

                    break;

                case "electroplating_commission": //電鍍委外
                    table_spinner = new ArrayAdapter<String>(context,
                            R.layout.table_simple_spinner_item, electroplating_commission);

                    if (subtable_num == 2)
                        subtable_num = 0; //電鍍委外只有兩個選項所以回到預設值

                    break;

                case "maintenance_bulletin": //維修通報
                    table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                            maintenance_bulletin);

                    if (subtable_num > 2)
                        subtable_num = 0; //維修通報只有三個選項所以回到預設值

                    break;

                case "maintenance_assistant": //維修助手
                    table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                            maintenance_assistant);
                    break;
            }

//            if (table_id[table_num].equals("packing_scan")){ //打包區
//                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item, packing_scan);
//
//            }else if (table_id[table_num].equals("electroplating_commission")){ //電鍍委外
//                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item, electroplating_commission);
//
//                if (subtable_num == 2)
//                    subtable_num = 0; //電鍍委外只有兩個選項所以回到預設值
//
//            }else { //維修通報
//                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item, maintenance_bulletin);
//            }

            //重新載入使用者選擇的資料表spinner
            table_spinner.setDropDownViewResource(R.layout.table_dropdown_spinner_item);
            table_spinner.notifyDataSetChanged(); //綁定更新
            m_table_S_table.setAdapter(table_spinner);
            m_table_S_table.setSelection(subtable_num, true); //預設為台北
            m_table_S_table.setOnItemSelectedListener(tablechoice);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alice377_list);

        catchdb(); //抓資料庫執行緒
        setupViewComponent(); //自定義method
    }

    //自定義method
    private void setupViewComponent() {
        m_alice377_list_R_logo = (RelativeLayout) findViewById(R.id.alice377_list_R_logo); //alice377_Logo畫面
        m_alice377_list_R_rel = (RelativeLayout) findViewById(R.id.alice377_list_R_rel); //工程模式畫面
        m_alice377_list_R_query = (RelativeLayout) findViewById(R.id.alice377_list_R_query); //工程模式-資料表查詢畫面
        m_alice377_list_T_loginmsg = (TextView) findViewById(R.id.alice377_list_T_loginmsg); //alice377_Logo畫面的提示訊息
        m_alice377_list_T_gettoday = (TextView) findViewById(R.id.alice377_list_T_gettoday); //今日日期
        m_alice377_list_T_msg = (TextView) findViewById(R.id.alice377_list_T_msg); //訊息框
        m_alice377_list_L_menu = (ListView) findViewById(R.id.alice377_list_L_menu); //資料列表清單
        m_alice377_list_E_sql_where = (EditText) findViewById(R.id.alice377_list_E_sql_where); //顯示使用者輸入的sql_where語法
        m_alice377_list_E_sql_order = (EditText) findViewById(R.id.alice377_list_E_sql_order); //顯示使用者輸入的sql_order語法
        m_alice377_list_T_msg2 = (TextView) findViewById(R.id.alice377_list_T_msg2); //資料表查詢的訊息框
        m_alice377_list_L_menu2 = (ListView) findViewById(R.id.alice377_list_L_menu2); //資料表查詢的列表清單

        today = mobiletoday("yyyy/M/d"); //取得手機今天日期
        m_alice377_list_T_gettoday.setText(today);
        m_alice377_list_T_gettoday.setTextColor(getResources().getColor(R.color.blue));

        layoutstart(); //layout初始化設定
        alice377dialog(); //初始化工程模式登入視窗
        cleardata(); //SQLite僅保留七天資料，其餘清掉 ※注意：執行後資料排序會改變，注意撈資料庫行為要在此method執行完再做

        //加入文字並給予顏色--------------------------------------------------------------------------
        String newmsg = "\n《點一下輸入帳號密碼！》";

        if (langnum == 1) { //簡體
            newmsg = "\n《点一下输入帐号密码！》";
        }

        String msg = getResources().getString(R.string.alice377_list_t_loginmsg) + newmsg;
        int redstart = getResources().getString(R.string.alice377_list_t_loginmsg).length();
        int redend = msg.length();

        msgcolor = new SpannableString(msg);
        msgcolor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), redstart,
                redend, 0); //紅色提醒
        m_alice377_list_T_loginmsg.setText(msgcolor, TextView.BufferType.SPANNABLE);
        //------------------------------------------------------------------------------------------

        m_alice377_list_T_loginmsg.setOnClickListener(showlogin); //login提示訊息監聽
    }

    //layout初始化設定
    private void layoutstart() {
        m_alice377_list_R_logo.setVisibility(View.VISIBLE); //顯示Logo頁面
        m_alice377_list_R_rel.setVisibility(View.GONE); //殺掉工程模式頁面
        m_alice377_list_R_query.setVisibility(View.GONE); //殺掉工程模式-資料表查詢頁面
    }

    //初始化工程模式登入視窗
    @SuppressLint("InflateParams")
    private void alice377dialog() {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_login, null); //自定義Layout:dialog

        //設定選單選擇視窗
        alice377AlertDialog = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        m_alice377_list_login_E_account = (EditText) alice377view.findViewById(R.id.alice377_list_login_E_account); //帳號
        m_alice377_list_login_E_password = (EditText) alice377view.findViewById(R.id.alice377_list_login_E_password); //密碼
        alice377AlertDialog.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog.getWindow().setAttributes(params); //dialog參數綁定

        alice377log("Alice377_list login_layout start.", 1); //寫log
    }

    //寫log
    private void alice377log(String getaction, int getstatus) {
        AppLogDb.action_view = "Alice377_list";
        AppLogDb.action_action = getaction;
        AppLogDb.action_date = mobiletoday("yyyy/M/d HH:mm:ss");
        AppLogDb.status = getstatus; //0=失敗,1=成功
        AppLogDb.insert_date = mobiletoday("yyyy/M/d");
        insert(context); //寫入資料
    }

//    //儲存使用者操作資料進SQLite
//    public static void db_log_insert(ContentResolver ConRes, Uri uri, Context context) {
//        ContentValues newRow = new ContentValues();
//        newRow.put("app_name", appname);
//        newRow.put("action_view", action_view);
//        newRow.put("action_action", action_action);
//        newRow.put("action_date", action_date);
//        newRow.put("status", status);
//        newRow.put("insert_date", insert_date);
//        Uri uri1 = ConRes.insert(uri, newRow);
//
//        if (uri1 == null) {
//            Toast.makeText(context, "log write eroor.", Toast.LENGTH_SHORT).show();
//            ContentValues errorRow = new ContentValues();
//            errorRow.put("app_name", appname);
//            errorRow.put("action_view", action_view);
//            errorRow.put("action_action", "log write eroor.");
//            errorRow.put("action_date", action_date);
//            errorRow.put("status", 0);
//            errorRow.put("insert_date", insert_date);
//            ConRes.insert(uri, errorRow);
//        }
//
//    }

    //SQLite掃描資料只保留最近七天的資料：避免log資料過於龐大
    private void cleardata() {

        //開單一執行緒清資料
        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                String sqlite = "select insert_date from alice377_user_action order by id desc";
//                Cursor cur = rawquery(context, uri, db_alice377_list, sqlite, myargs);
                Cursor cur = rawquery(context, sqlite, myargs);

                try {
                    cur.moveToFirst();
                    int data = cur.getCount();

                    if (data > 0) {
                        //設定暫存Array內容
                        reclist = new ArrayList<String>();
                        String str = ""; //記錄上一筆string

                        while (!cur.isAfterLast()) {
                            String fldSet = cur.getString(0);

                            if (!str.equals(fldSet)) { //insert日期不同時再紀錄
                                reclist.add(fldSet); //存放到arraylist中
                                str = fldSet;
                            }

                            cur.moveToNext();
                        }

                        int j = reclist.size();

                        if (j > 7) {

                            for (int i = 7; i < j; i++) {
                                myselecion = "insert_date = '" + reclist.get(i) + "'";
                                int k = delete(context, myselecion);

                                if (k == 0) //出現錯誤就停止
                                    break;
                            }
                        }
                    }

                    cur.close();

                } catch (Exception e) {
                    if (uselog) Log.d(TAG, "error" + e.toString());

                } finally {
                    if (cur != null) cur.close();
                }
            }
        });

    }

    //alice377AlertDialog上的login按鈕
    public void login(View view) {
        String account = m_alice377_list_login_E_account.getText().toString().trim();
        String password = m_alice377_list_login_E_password.getText().toString().trim();

        if (!account.equals("") && !password.equals("") && Alice377_android.alice377_acget(account) &&
                Alice377_android.alice377_pwget(password)) { //登入帳密
            Toast.makeText(this, "登入成功！", Toast.LENGTH_SHORT).show();
            alice377log("Accout:******** login success.", 1); //寫log
            actionshow(); //顯示使用者活動列表
            m_alice377_list_R_logo.setVisibility(View.GONE); //殺掉Logo頁面
            m_alice377_list_R_rel.setVisibility(View.VISIBLE); //顯示工程模式頁面
            alice377AlertDialog.dismiss(); //關閉dialog

        } else {

            if (langnum == 1) { //簡體
                Toast.makeText(this, "帐号密码输入错误！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "帳號密碼輸入錯誤！", Toast.LENGTH_SHORT).show();
            }

            m_alice377_list_login_E_password.setText(""); //清空輸入的密碼
            alice377log("Accout:******** login failure.", 0); //寫log
        }

    }

    //顯示使用者活動列表
    private void actionshow() {

        //開工作執行緒撈SQLite掃描資料
        singleThreadExecutor.execute(new Runnable() {

            String Col_name = "";
            int error = 0; //

            @Override
            public void run() {

                //查詢總資料筆數
//                mContRes = getContentResolver();
//                myselecion = "app_name LIKE '" + appname + "'";
//                myargs = new String[]{}; //指定要撈某個欄位時:"%" + fix_num + "%"
//                myorder = "action_date DESC"; //排序規則:掃描時間降冪排序
//                Cursor c = mContRes.query(uri, tablecolumn, myselecion, myargs, myorder);

                String sql = "select * from alice377_user_action where 1=1 and app_name = '" +
                        appname + "' order by action_date desc"; //最多顯示100筆
                Cursor c = rawquery(context, sql, myargs);
                c.moveToFirst();
                data = c.getCount(); //記錄總資料筆數
                c.close(); //用完關閉

                //使用SQLite語法撈資料:context=這支內容,uri=哪張table
                sql = "select * from alice377_user_action where 1=1 and app_name = '" + appname +
                        "' order by id desc limit 100"; //最多顯示100筆

                if (!user_write.equals("")) {
                    sql = user_write;
                } else {
                    user_write = sql;
                }

                try {
                    Cursor cur = rawquery(context, sql, myargs);
                    data_show = cur.getCount();

                    if (data_show > 0) { //有資料,設定筆數
                        String[] ColName = cur.getColumnNames();
                        cur.moveToFirst();

                        //設定listview內容
                        recSet = new ArrayList<>(); //重設recSet的值為空
                        int columnCount = cur.getColumnCount();
                        mList = new ArrayList<>(); //重設mList的值為空

                        while (!cur.isAfterLast()) {
                            StringBuilder fldSet = new StringBuilder();

                            for (int ii = 0; ii < columnCount; ii++) {
                                fldSet.append(cur.getString(ii));

                                if (ii < columnCount - 1)
                                    fldSet.append("#");
                            }

                            recSet.add(fldSet.toString()); //存放到arraylist中
                            cur.moveToNext();
                        }

                        cur.close(); //用完關掉

                        for (int i = 0; i < recSet.size(); i++) {
                            Map<String, Object> item = new HashMap<>();
                            String[] fld = recSet.get(i).split("#");
                            StringBuilder str = new StringBuilder();
                            int j = fld.length;

                            for (int k = 1; k <= j; k++) {

                                if (k == 1) {
                                    changename(ColName[k - 1]);
                                    str.append(Col_name).append(fld[k - 1]);

                                } else if (k == 2 || k == 3 || k == 4 || k == 5 || k == 7) {
                                    changename(ColName[k - 1]);
                                    str.append("\n").append(Col_name).append(fld[k - 1]);

                                } else if (k == 6) {
                                    String status_CH = "成功";

                                    if (fld[5].equals("0"))
                                        status_CH = "失敗";

                                    changename(ColName[k - 1]);
                                    str.append("\n").append(Col_name).append(status_CH);
                                }

                            }

                            item.put("textview", str.toString()); //"APP名稱：" + fld[1] + "\n執行介面：" + fld[2] + "\n執行動作：" + fld[3] + "\n執行時間：" + fld[4] + "\n執行結果：" + status_CH
                            mList.add(item);
                        }

                    } else {
                        cur.close(); //用完關掉
                        mList = new ArrayList<>(); //重設mList的值為空
                    }

                } catch (Exception e) {
                    if (uselog) Log.d(TAG, "error=" + e.toString());
                    error = 1;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //===========設定Listview==========//
                        SimpleAdapter adapter = new SimpleAdapter(
                                Alice377_list.this,
                                mList,
                                R.layout.alice377_list_item,
                                new String[]{"textview"},
                                new int[]{R.id.alice377_list_item_T_data}
                        );
                        //--------------------
                        adapter.notifyDataSetChanged(); //通知UI更新數據
                        m_alice377_list_L_menu.setAdapter(adapter);
//                        m_elec_com_main_L_menu.setSelection(last_item); //移到最新掃描的資料
                        m_alice377_list_L_menu.setEnabled(true);
//                        m_alice377_list_L_menu.setOnItemClickListener(menudetail);

                        if (error == 1) {
                            Toast.makeText(context, "語法輸入錯誤", Toast.LENGTH_SHORT).show();
                        } else {
                            textmsg(); //設定訊息框
                        }

                    }
                });

            }

            //欄位名稱轉換為中文
            private void changename(String s) {

                switch (s) {
                    case "id":
                        Col_name = "id：";
                        break;

                    case "action_view":
                        Col_name = "APP名稱：";
                        break;

                    case "app_name":
                        Col_name = "執行介面：";
                        break;

                    case "action_action":
                        Col_name = "執行動作：";
                        break;

                    case "action_date":
                        Col_name = "執行時間：";
                        break;

                    case "status":
                        Col_name = "執行結果：";
                        break;

                    case "insert_date":
                        Col_name = "儲存日期：";
                        break;
                }
            }

        });

    }

    //設定訊息框
    private void textmsg() {
        String msg = "目前共有" + data + "筆資料，顯示最新" + data_show + "筆";
        String msgbluestart = "目前共有";
        String msg2 = "資料已更新";

        if (langnum == 1) { //簡體
            msg = "目前共有" + data + "笔资料，显示最新" + data_show + "笔";
            msg2 = "资料已更新";
        }

        int bluestart = msgbluestart.length();
        int blueend = Integer.toString(data).length();
        int redstart = ("目前共有" + data + "筆資料，顯示最新").length();
        int redend = Integer.toString(data_show).length();

        msgcolor = new SpannableString(msg);
        msgcolor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), bluestart, bluestart + blueend, 0); //藍色表示
        msgcolor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), redstart, redstart + redend, 0); //紅色表示

        m_alice377_list_T_msg.setText(msgcolor, TextView.BufferType.SPANNABLE);
        Toast.makeText(this, msg2, Toast.LENGTH_SHORT).show();
    }

    //alice377AlertDialog上的yes按鈕
    public void yesbtn(View view) {
        alice377log("User click yesbtn to finish this.", 1); //寫log
        alice377AlertDialog.dismiss(); //關閉dialog視窗
        Toast.makeText(this, "已離開工程模式", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    //alice377AlertDialog上的calcel按鈕
    public void cancelbtn(View view) {
        alice377AlertDialog.dismiss(); //關閉dialog視窗
    }

    //工程模式title右邊的image
    public void I_setting(View view) {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_menu, null); //自定義Layout:dialog

        alice377AlertDialog = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        alice377AlertDialog.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog.getWindow().setAttributes(params); //dialog參數綁定
    }

    //工程模式選單：SQL查詢
    public void sqlbtn(View view) {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_menu_sql, null); //自定義Layout:dialog

        alice377AlertDialog2 = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog2.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        m_sql_E_write = alice377view.findViewById(R.id.sql_E_write); //SQLite_where以後語法輸入框
        int begin = "select * from alice377_user_action ".length();

        //讀取資料----------------------------------------------------------------------------
        SharedPreferences textsetdata = getSharedPreferences("TEXT_SET", 0);
        String str = textsetdata.getString("SQLite_set", "");

        if (!str.equals("")) { //儲存值為空
            user_write = str.substring(begin); //載入預設值
        } else {
            user_write = user_write.substring(begin);
        }
        //-----------------------------------------------------------------------------------

        m_sql_E_write.setText(user_write); //顯示儲存的SQLite語法
        alice377AlertDialog2.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog2.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog2.getWindow().setAttributes(params); //dialog參數綁定

        alice377AlertDialog.dismiss(); //關閉dialog
    }

    //SQL查詢視窗：查詢紐
    public void querybtn(View view) {
        String str = m_sql_E_write.getText().toString(); //重新取得值

        if (str.substring(0, 1).equals("w") || str.substring(0, 1).equals("W") ||
                str.substring(0, 1).equals("o") || str.substring(0, 1).equals("O")) {
            user_write = "select * from alice377_user_action " + str;
            actionshow(); //依使用者輸入的語法重新查詢資料

            //儲存SQLite語法資料------------------------------------------------------------------
            SharedPreferences textsetdata = getSharedPreferences("TEXT_SET", 0);
            textsetdata.edit().putString("SQLite_set", user_write).apply();
            //-----------------------------------------------------------------------------------
        } else {
            String msg = "發生錯誤，請輸入where條件或order by語法";

            if (langnum == 1) { //簡體
                msg = "发生错误，请输入where条件或order by语法";
            }

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        table_page = 0; //工程模式-資料表查詢頁面關閉
        m_alice377_list_R_rel.setVisibility(View.VISIBLE); //顯示工程模式頁面
        m_alice377_list_R_query.setVisibility(View.GONE); //殺掉工程模式-資料表查詢頁面
        alice377AlertDialog2.dismiss(); //關閉dialog
    }

    //SQL查詢視窗：欄位說明
    public void supportbtn(View view) {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_sql_support, null); //自定義Layout:dialog

        alice377AlertDialog = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        TextView m_support_T_detail = (TextView) alice377view.findViewById(R.id.support_T_detail); //LOG欄位說明視窗
        String msg = getString(R.string.support_t_detail) + "\n";

        if (langnum == 1) { //簡體
            msg += "id：id\naction_view：APP名称\napp_name：执行介面\naction_action：执行动作\n" +
                    "action_date：执行时间\nstatus：执行结果\ninsert_date：储存日期";
        } else {
            msg += "id：id\naction_view：APP名稱\napp_name：執行介面\naction_action：執行動作\n" +
                    "action_date：執行時間\nstatus：執行結果\ninsert_date：儲存日期";
        }

        m_support_T_detail.setText(msg);
        alice377AlertDialog.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog.getWindow().setAttributes(params); //dialog參數綁定
    }

    //工程模式選單：選擇資料表查詢
    public void tablebtn(View view) {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_menu_table, null); //自定義Layout:dialog

        alice377AlertDialog2 = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog2.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        m_table_S_db = (Spinner) alice377view.findViewById(R.id.table_S_db); //資料庫選單
        m_table_S_table = (Spinner) alice377view.findViewById(R.id.table_S_table); //資料表選單
        m_table_E_sql_where = (EditText) alice377view.findViewById(R.id.table_E_sql_where); //SQLite_where語法
        m_table_E_sql_order = (EditText) alice377view.findViewById(R.id.table_E_sql_order); //SQLite_order語法
        db_spinner(); //資料庫項目生成

        if (!sql_where.equals(""))
            m_table_E_sql_where.setText(sql_where);

        if (!sql_order.equals(""))
            m_table_E_sql_order.setText(sql_order);

        alice377AlertDialog2.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog2.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog2.getWindow().setAttributes(params); //dialog參數綁定

        alice377AlertDialog.dismiss(); //關閉dialog
    }

    //spinner項目生成
    private void db_spinner() {
        if (first_create == 0) { //初次顯示項目

            if (!appname.equals("")) { //不為空值時

                if (appname.substring(0, 1).equals("電"))
                    table_num = 1;

                if (appname.substring(0, 1).equals("維") && appname.contains("通報"))
                    table_num = 2;

                if (appname.substring(0, 1).equals("維") && appname.contains("助手"))
                    table_num = 3;
            }

            first_create = 1;
        }

        //資料庫spinner
        db_spinner = new ArrayAdapter<String>(this, R.layout.table_simple_spinner_item, db_name);
        db_spinner.setDropDownViewResource(R.layout.table_dropdown_spinner_item);
        db_spinner.notifyDataSetChanged(); //綁定更新
        m_table_S_db.setAdapter(db_spinner);
        m_table_S_db.setSelection(table_num, true); //預設為打包區db
        m_table_S_db.setOnItemSelectedListener(dbchoice);

        //資料表spinner
        switch (table_num) {
            case 0: //打包區掃描
                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                        packing_scan);

                if (subtable_num > 2)
                    subtable_num = 0; //打包區只有三個選項所以回到預設值
                break;

            case 1: //電鍍委外
                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                        electroplating_commission);

                if (subtable_num > 1)
                    subtable_num = 0; //電鍍委外只有兩個選項所以回到預設值
                break;

            case 2: //維修通報
                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                        maintenance_bulletin);

                if (subtable_num > 2)
                    subtable_num = 0; //維修通報只有三個選項所以回到預設值
                break;

            case 3: //維修助手
                table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
                        maintenance_assistant);
                break;
        }

//        table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item, packing_scan);
//
//        if (table_num == 1){ //電鍍委外
//            table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
//                    electroplating_commission);
//
//            if (subtable_num == 2)
//                subtable_num = 0; //電鍍委外只有兩個選項所以回到預設值
//
//        }else if (table_num == 2){ //維修通報
//            table_spinner = new ArrayAdapter<String>(context, R.layout.table_simple_spinner_item,
//                    maintenance_bulletin);
//        }

        table_spinner.setDropDownViewResource(R.layout.table_dropdown_spinner_item);
        table_spinner.notifyDataSetChanged(); //綁定更新
        m_table_S_table.setAdapter(table_spinner);
        m_table_S_table.setSelection(subtable_num, true); //預設為台北
        m_table_S_table.setOnItemSelectedListener(tablechoice);
    }

    //選擇資料表查詢裡的確定按鈕
    public void tablecheckbtn(View view) {

        //開單一執行緒撈資料
        singleThreadExecutor.execute(new Runnable() {

            int error = 0; //記錄錯誤

            @Override
            public void run() {
                if (!sql_where.equals("")) {
                    sql_where = m_table_E_sql_where.getText().toString(); //儲存SQLite_where語法
                }

                if (!sql_order.equals(""))
                    sql_order = m_table_E_sql_order.getText().toString(); //儲存SQLite_order語法

                String sql = "select * from "; //預設查詢全部資料
                String[] cp = {"Packing_scanContentProvider", "Elec_comContentProvider",
                        "Main_bullContentProvider", "Main_atContentProvider"}; //內容提供者名稱
                String authority = "com.alice377group." + table_id[table_num] + ".providers." +
                        cp[table_num]; //內容提供者路徑
                Uri quri = null;

                switch (table_num) {
                    case 0: //打包區
                        quri = Uri.parse("content://" + authority + "/" +
                                packing_scan_id[subtable_num]); //預設打包區
                        sql += packing_scan_id[subtable_num] + " where " + sql_where + " order by "
                                + sql_order; //組合整段SQLite語法
                        break;

                    case 1: //電鍍委外
                        quri = Uri.parse("content://" + authority + "/" +
                                electroplating_commission_id[subtable_num]);
                        sql += electroplating_commission_id[subtable_num] + " where " + sql_where +
                                " order by " + sql_order; //組合整段SQLite語法
                        break;

                    case 2: //維修通報
                        quri = Uri.parse("content://" + authority + "/" +
                                maintenance_bulletin_id[subtable_num]);
                        sql += maintenance_bulletin_id[subtable_num] + " where " + sql_where +
                                " order by " + sql_order; //組合整段SQLite語法
                        break;

                    case 3: //維修助手
                        quri = Uri.parse("content://" + authority + "/" +
                                maintenance_assistant_id[subtable_num]);
                        sql += maintenance_assistant_id[subtable_num] + " where " + sql_where +
                                " order by " + sql_order; //組合整段SQLite語法
                        break;
                }

//                AppLogDb appLogDb = new AppLogDb(context, DB_NAME, null, 1);
//                SQLiteDatabase mSQLDatabase = mDbOpenHelper.getReadableDatabase(); //只讀取

                mContRes = getContentResolver();
                myselecion = sql_where; //where條件
                myorder = sql_order; //排序規則

                try {
                    Cursor cur = mContRes.query(quri, null, myselecion, null,
                            myorder);
                    data = cur != null ? cur.getCount() : 0;

                    if (data > 0) { //有資料,設定筆數
                        String[] ColName = cur.getColumnNames();
                        cur.moveToFirst();

                        //設定listview內容
                        recSet = new ArrayList<>(); //重設recSet的值為空
                        int columnCount = cur.getColumnCount();
                        mList = new ArrayList<>(); //重設mList的值為空

                        while (!cur.isAfterLast()) {
                            StringBuilder fldSet = new StringBuilder();

                            for (int ii = 0; ii < columnCount; ii++) {
                                fldSet.append(cur.getString(ii));

                                if (ii < columnCount - 1)
                                    fldSet.append("#");
                            }

                            recSet.add(fldSet.toString()); //存放到arraylist中
                            cur.moveToNext();
                        }

                        cur.close(); //用完關掉

                        for (int i = 0; i < recSet.size(); i++) {
                            Map<String, Object> item = new HashMap<>();
                            String[] fld = recSet.get(i).split("#");
                            StringBuilder str = new StringBuilder();
                            int j = fld.length;

                            for (int k = 1; k <= j; k++) {
                                str.append(ColName[k - 1]).append("：").append(fld[k - 1]);

                                if (k < j)
                                    str.append("\n");
                            }

                            item.put("textview", str.toString());
                            mList.add(item);
                        }

                    } else {
                        if (cur != null) cur.close(); //用完關掉
                        mList = new ArrayList<>(); //重設mList的值為空
                    }

                } catch (Exception e) {
                    if (uselog) Log.d(TAG, "error=" + e.toString());
                    error = 1; //發生錯誤
                }

                //更新UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //===========設定Listview==========//
                        SimpleAdapter adapter = new SimpleAdapter(
                                Alice377_list.this,
                                mList,
                                R.layout.alice377_list_item,
                                new String[]{"textview"},
                                new int[]{R.id.alice377_list_item_T_data}
                        );
                        //----------------------------------
                        adapter.notifyDataSetChanged(); //通知UI更新數據
                        m_alice377_list_L_menu2.setAdapter(adapter);
                        m_alice377_list_L_menu2.setEnabled(true);

                        if (error == 1) {
                            String msg = "語法輸入錯誤或無法查詢此條件資料";

                            if (langnum == 1) //簡體
                                msg = "语法输入错误或无法查询此条件资料";

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            table_page = 1; //記錄顯示工程模式-資料表查詢頁面
                            m_alice377_list_E_sql_where.setText(sql_where); //顯示使用者輸入的SQL_where語法
                            m_alice377_list_E_sql_order.setText(sql_order); //顯示使用者輸入的SQL_order語法
                            m_alice377_list_R_rel.setVisibility(View.GONE); //殺掉工程模式頁面
                            m_alice377_list_R_query.setVisibility(View.VISIBLE); //顯示工程模式-資料表查詢頁面
                            textmsg2(); //設定訊息框
                            alice377AlertDialog2.dismiss(); //關閉dialog
                        }
                    }
                });

            }
        });

    }

    //設定資料表查詢訊息框
    private void textmsg2() {
        String msg = "查詢到" + data + "筆資料";
        String msgbluestart = "查詢到";
        String msg2 = "資料已更新";

        if (langnum == 1) { //簡體
            msg = "查询到" + data + "笔资料";
            msg2 = "资料已更新";
        }

        int bluestart = msgbluestart.length();
        int blueend = Integer.toString(data).length();

        msgcolor = new SpannableString(msg);
        msgcolor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), bluestart,
                bluestart + blueend, 0); //藍色表示

        m_alice377_list_T_msg2.setText(msgcolor, TextView.BufferType.SPANNABLE);
        Toast.makeText(this, msg2, Toast.LENGTH_SHORT).show();
    }

    //工程模式-資料表查詢裡的查詢按鈕
    public void requerybtn(View view) {
        sql_where = m_alice377_list_E_sql_where.getText().toString();
        sql_order = m_alice377_list_E_sql_order.getText().toString();
        m_table_E_sql_where.setText(sql_where); //設定使用者輸入的SQLite_where語法
        m_table_E_sql_order.setText(sql_order); //設定使用者輸入的SQLite_order語法
        tablecheckbtn(view); //重新查詢
    }

    //SQLite_log資料清除
    @SuppressLint("InflateParams")
    public void logdelbtn(View view) {
        alice377view = LayoutInflater.from(Alice377_list.this)
                .inflate(R.layout.alice377_list_delcheck, null); //自定義Layout:dialog

        alice377AlertDialog2 = new Alice377AlertDialog(Alice377_list.this);
        alice377AlertDialog2.setView(alice377view, 0, 0, 0,
                0); //設定自定義layout
        alice377AlertDialog2.show(); //務必先show出來才能設定參數

        //自定義Dialog視窗參數
        WindowManager.LayoutParams params = alice377AlertDialog2.getWindow().getAttributes(); //取得dialog參數對象
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
        params.gravity = Gravity.CENTER; //設置dialog重心
        alice377AlertDialog2.getWindow().setAttributes(params); //dialog參數綁定

        alice377AlertDialog.dismiss(); //關閉上一個dialog
    }

    //SQLite_log資料清除確認視窗：確定
    public void delyesbtn(View view) {
        String msg, msg2; //訊息
        int i = delete(context, null);

        if (langnum == 1) { //簡體
            msg = "LOG资料删除成功";
            msg2 = "删除失败";

        } else {
            msg = "LOG資料刪除成功";
            msg2 = "刪除失敗";
        }

        if (i > 0) {
            actionshow(); //重載清單
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, msg2, Toast.LENGTH_SHORT).show();
        }

        alice377AlertDialog2.dismiss(); //執行後關閉
    }

    //SQLite_log資料清除確認視窗：取消
    public void delcancelbtn(View view) {
        String msg = "取消清除LOG資料";

        if (langnum == 1) //簡體
            msg = "取消清除LOG资料";

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        alice377AlertDialog2.dismiss(); //關閉dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    //監聽手機返回鍵
    @SuppressLint("InflateParams")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (System.currentTimeMillis() - exitTime > 2000) {
                exitTime = System.currentTimeMillis();
                String msg, msg2;

                if (langnum == 1) { //簡體
                    msg = "再按一次离开工程模式";
                    msg2 = "再按一次离开资料表查询";

                } else {
                    msg = "再按一次離開工程模式";
                    msg2 = "再按一次離開資料表查詢";
                }

                if (m_alice377_list_R_query.getVisibility() == View.VISIBLE) { //工程模式-資料表查詢頁面開啟
                    Toast.makeText(this, msg2, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }

            } else {

                if (m_alice377_list_R_rel.getVisibility() == View.VISIBLE) { //工程模式開啟
                    alice377view = LayoutInflater.from(Alice377_list.this)
                            .inflate(R.layout.alice377_list_quit, null); //自定義Layout:dialog

                    //設定選單選擇視窗
                    alice377AlertDialog = new Alice377AlertDialog(Alice377_list.this);
                    alice377AlertDialog.setView(alice377view, 0, 0, 0,
                            0); //設定自定義layout
                    alice377AlertDialog.setCancelable(false); //dialog顯示時不能用其他方式關掉
                    alice377AlertDialog.show(); //務必先show出來才能設定參數

                    //自定義Dialog視窗參數
                    WindowManager.LayoutParams params = alice377AlertDialog.getWindow().getAttributes(); //取得dialog參數對象
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog寬度包裹內容
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT; //設置dialog高度包裹內容
                    params.gravity = Gravity.CENTER; //設置dialog重心
                    alice377AlertDialog.getWindow().setAttributes(params); //dialog參數綁定

                } else if (m_alice377_list_R_query.getVisibility() == View.VISIBLE) { //工程模式-資料表查詢頁面開啟
                    m_alice377_list_R_query.setVisibility(View.GONE); //殺掉資料表查詢頁面
                    m_alice377_list_R_rel.setVisibility(View.VISIBLE); //顯示工程模式

                } else {
                    alice377log("User click mobile_backbtn second to close this.", 1); //寫log

                    if (langnum == 1) { //簡體
                        Toast.makeText(this, "已离开工程模式", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "已離開工程模式", Toast.LENGTH_SHORT).show();
                    }

                    this.finish();
                }

            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
