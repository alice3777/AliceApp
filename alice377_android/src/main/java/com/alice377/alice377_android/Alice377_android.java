package com.alice377.alice377_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alice377.alice377_android.providers.AppLogDb;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by alice377 on 2019/3/18.
 */

public class Alice377_android {

    private static final String TAG = "alice377=>";
    public static boolean uselog = false; //是否啟用log
    //*app應用類----------------------------------------------------------------------------------*//
    public static int langnum = 0; //標記使用的語言：0=預設繁體,1=簡體,2=英文
    public static Locale locale_TW = Locale.TRADITIONAL_CHINESE; //多國語言：繁體
    public static Locale locale_CN = Locale.SIMPLIFIED_CHINESE; //多國語言：簡體
    public static Locale locale_EN = Locale.ENGLISH; //多國語言：英文
    //*網路連線類---------------------------------------------------------------------------------*//
    public static int wifilink = 0; //記錄使用wifi連線:1=使用wifi
    //*apk文件訊息處理類---------------------------------------------------------------------------*//
    public static String packagename = ""; //記錄包名
    public static String appname = ""; //記錄app名稱
    public static String versionname = ""; //記錄版本名稱
    public static int versioncode = 0; //記錄版本號碼
    private static String msg;
    private static String locale_save = null; //儲存value語系
    private Alice377_android context = Alice377_android.this;
    private String msg2; //toast訊息用

    //起始語言配置
    public static void langstart(Context context) {

        //儲存語言設定值在app裡
        SharedPreferences textsetdata = context.getSharedPreferences("TEXT_SET", 0);
        locale_save = textsetdata.getString("locale_set", locale_TW.toString());

        //變更設定檔
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        //locale_save為簡體時設定簡體中文以此類推，預設為繁體中文
        if (locale_save.equals(locale_CN.toString())) {
            config.locale = locale_CN; //設定為簡體中文
            resources.updateConfiguration(config, dm); //改變配置訊息即時套用
            langnum = 1; //設為使用簡體

        } else if (locale_save.equals(locale_EN.toString())) {
            config.locale = locale_EN; //設定為英文
            resources.updateConfiguration(config, dm); //改變配置訊息即時套用
            langnum = 2; //設為使用英文

        } else {
            textsetdata.edit().putString("locale_set", locale_save).apply(); //記錄語言設定值
            langnum = 0; //設為預設繁體
        }

    }


    //*------------------------------------------------------------------------------------------*//

    //軟體鍵盤顯示或隱藏
    public static void showKeyboard(View view) { //軟體鍵盤顯示
        InputMethodManager imm = (InputMethodManager) view.getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(View view) { //軟體鍵盤隱藏
        InputMethodManager imm = (InputMethodManager) view.getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //*------------------------------------------------------------------------------------------*//


    //*資料庫處理類-------------------------------------------------------------------------------*//

    public static void toggleSoftInput(View view) { //檢查軟體鍵盤使用狀態
        InputMethodManager imm = (InputMethodManager) view.getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }

    //toast長訊息
    public static void longtoast(Context getcontext, String getmsg) {
        Toast.makeText(getcontext, getmsg, Toast.LENGTH_LONG).show();
    }
    //*------------------------------------------------------------------------------------------*//

    //toast短訊息
    public static void shorttoast(Context getcontext, String getmsg) {
        Toast.makeText(getcontext, getmsg, Toast.LENGTH_SHORT).show();
    }

    //監測網路連線
    public static void checklink(String view, Context mContext) {
        Context context = mContext.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //獲取NetcorkInfo對象
        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        wifilink = 0; //偵測前先重置

        //判斷當前網路狀態是否為連接狀態且連接哪種網路
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            msg = "已偵測到網路連線：WIFI網路已連接";

            if (langnum == 1) { //簡體
                msg = "已侦测到网路连线：WIFI网路已连接";
            } else if (langnum == 2) { //英文
                msg = "Internet connection detected: WIFI network connected";
            }

//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            wifilink = 1; //記錄使用wifi

        } else if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype(); //獲得mobile網路的類型
            if (uselog) Log.d(TAG, "nSubType=" + nSubType);
            String netmsg = null;
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //行動網路類型
            if (telephonyManager != null) {
                if (nSubType == TelephonyManager.NETWORK_TYPE_LTE && !telephonyManager.isNetworkRoaming()) {
                    netmsg = "4G行動網路";

                    if (langnum == 1) { //簡體
                        netmsg = "4G行动网路";
                    } else if (langnum == 2) { //英文
                        netmsg = "4G mobile network";
                    }

                } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                        || nSubType == TelephonyManager.NETWORK_TYPE_EHRPD
                        || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || nSubType == TelephonyManager.NETWORK_TYPE_HSPA
                        || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_B
                        || nSubType == TelephonyManager.NETWORK_TYPE_HSPAP //台灣之星3G
                        && !telephonyManager.isNetworkRoaming()) {
                    netmsg = "3G行動網路";

                    if (langnum == 1) { //簡體
                        netmsg = "3G行动网路";
                    } else if (langnum == 2) { //英文
                        netmsg = "3G mobile network";
                    }

                } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                        || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                        || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                        || nSubType == TelephonyManager.NETWORK_TYPE_IDEN
                        || nSubType == TelephonyManager.NETWORK_TYPE_1xRTT) {
                    netmsg = "2G行動網路";

                    if (langnum == 1) { //簡體
                        netmsg = "2G行动网路";
                    } else if (langnum == 2) { //英文
                        netmsg = "2G mobile network";
                    }

                }

            }

            if (netmsg == null) {
                msg = "行動網路訊號不佳，請使用WIFI網路確保資料正常下載";

                if (langnum == 1) { //簡體
                    msg = "行动网路讯号不佳，请使用WIFI网路确保资料正常下载";
                } else if (langnum == 2) { //英文
                    msg = "Mobile network signal is not good, please use WIFI network to ensure" +
                            " the normal download of data";
                }

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

            } else {
                msg = "已偵測使用" + netmsg + "，請使用WIFI網路確保資料正常下載";

                if (langnum == 1) { //簡體
                    msg = "已侦测使用" + netmsg + "，请使用WIFI网路确保资料正常下载";
                } else if (langnum == 2) { //英文
                    msg = "Detected use" + netmsg + ", please use WIFI network to ensure normal" +
                            " download of data";
                }

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }

        } else {
            wifilink = 0; //記錄斷網
            msg = "尚未連接網路，請確認手機已連接WIFI網路";

            if (langnum == 1) { //簡體
                msg = "尚未连接网路，请确认手机已连接WIFI网路";
            } else if (langnum == 2) { //英文
                msg = "Not connected to the Internet yet, please make sure the phone is connected" +
                        " to WIFI network";
            }

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            alice377log(view, "[自動]未連接網路", 0, mContext); //寫lag
        }

    }

    //抓資料庫執行緒的資料萬用法：寫在Create裡
    public static void catchdb() {
        StrictMode.setThreadPolicy(
                new
                        StrictMode.
                                ThreadPolicy.Builder().
                        detectDiskReads().
                        detectDiskWrites().
                        detectNetwork().
                        penaltyLog().
                        build());
        StrictMode.setVmPolicy(
                new
                        StrictMode.
                                VmPolicy.
                                Builder().
                        detectLeakedSqlLiteObjects().
                        penaltyLog().
                        penaltyDeath().
                        build());
    }

    //app寫log
    public static void alice377log(String view, String action, int status, Context context) {
        AppLogDb.action_view = view;
        AppLogDb.action_action = action;
        AppLogDb.action_date = mobiletoday("yyyy/MM/dd HH:mm:ss");
        AppLogDb.status = status; //0=失敗,1=成功
        AppLogDb.insert_date = mobiletoday("yyyy/MM/dd");
        AppLogDb.insert(context); //寫log
    }

    //傳遞apk版本名稱及版號：用於私有雲apk更新或工程模式
    public static int apkname(Context context) {
        int apkinfo = 0; //記錄有無回傳訊息

        //取得目前的apk文件訊息
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo ai = pi.applicationInfo;

            packagename = pi.packageName; //取得app包名
            appname = pm.getApplicationLabel(ai).toString(); //取得app名稱
            versionname = pi.versionName; //取得app版本名稱
            versioncode = pi.versionCode; //取得app版本號碼
            Log.d(TAG, "APP包名:" + packagename + "APP名稱:" + appname + "版本名稱:" +
                    versionname + "版本號碼:" + versioncode);

            if (!packagename.equals("") && !appname.equals("") && !versionname.equals("")
                    && versioncode != 0) {
                apkinfo = 1; //表示都有值
            }

        } catch (PackageManager.NameNotFoundException e) {
            if (uselog) Log.d(TAG, "錯誤=" + e.toString());
        }

        return apkinfo;
    }
    //*------------------------------------------------------------------------------------------*//


    //*時間處理類---------------------------------------------------------------------------------*//
    /*傳入時間格式抓取手機時間：傳入的簡易字串格式 yyyy/M/d HH:mm:ss
      SimpleDateFormat函数语法：
      G 年代标志符
      y 年
      M 月
      d 日
      h 时 在上午或下午 (1~12)
      H 时 在一天中 (0~23)
      m 分
      s 秒
      S 毫秒
      E 星期
      D 一年中的第几天
      F 一月中第几个星期几
      w 一年中第几个星期
      W 一月中第几个星期
      a 上午 / 下午 标记符
      k 时 在一天中 (1~24)
      K 时 在上午或下午 (0~11)
      z 时区
    */
    public static String mobiletoday(String str) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(str);
        String today = formatter.format(new Date());
        return today;
    }

    //取得現在時間，此寫法網路傳遞無須轉碼
    public static String nowtime(String time_format) { //傳入時間格式,ex."M/d HH:mm"
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat(time_format);
        date.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return date.format(currentLocalTime);
    }

    //*圖片處理類---------------------------------------------------------------------------------*//
    //處理drawable圖片生成bitmap，解決設定圖片時產生的Out of Memory(OOM)
    public static Bitmap readBitMap(Context context, int resId, int size) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true; //java系統記憶體不時先行回收部分的記憶體
        opt.inInputShareable = true;
        opt.inSampleSize = size; //原始圖片幾分之一的大小

//        //隱藏版變數，直接把不使用的記憶體算到VM裡，降低OOM發生的機率
//        try {
//            BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(opt,true);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        //獲取資源圖片
        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is, null, opt);
    }
    //*------------------------------------------------------------------------------------------*//

    //*工程模式登入驗證----------------------------------------------------------------------------*//
    //account驗證
    static boolean alice377_acget(String str) {
        char[] getabc = str.toCharArray(); //字串轉字元陣列
        int[] getabc_int = new int[getabc.length]; //設定數字陣列長度
        StringBuilder getabc_Str = new StringBuilder(); //設定存放英文字元
        String[] abc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z"};
        int[] check = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; //數字陣列
        boolean ischeck = false; //記錄是否通過驗證

        //使用者輸入的字串處理------------------------------------------------------------------------
        for (int i = 0; i < str.length(); i++) { //字串轉換分別存入數字及英文陣列

            if (getabc[i] > 47 && getabc[i] < 58) { //數字0~9

                for (int j = 0; j <= i; j++) {

                    if (j == i)
                        getabc_int[j] = Integer.parseInt(String.valueOf(getabc[i]));

                }
                if (uselog) Log.d(TAG, "getabc_int=" + getabc_int);

            } else if (getabc[i] > 96 && getabc[i] < 123) { //英文小寫字母

                for (int k = 0; k <= i; k++) {

                    if (k == i) {
                        String save_Str = getabc_Str.substring(0, k);
                        getabc_Str = new StringBuilder(save_Str + String.valueOf(getabc[i]));

                    } else {
                        getabc_Str.append("z");
                    }

                }
                if (uselog) Log.d(TAG, "getabc_Str=" + getabc_Str);
            }

        }

        if (uselog) Log.d(TAG, "getabc_int=" + getabc_int + "getabc_Str=" + getabc_Str);
        //------------------------------------------------------------------------------------------
        try {
            int char0 = getabc[0] - 97; //ASCII第一位計算
            int char1 = getabc[1] - 97; //ASCII第二位計算
            String sc0 = Integer.toString(char0).substring(0, 1);
            String sc1 = Integer.toString(char0).substring(1, 2);
            int isc0 = Integer.parseInt(sc0);
            int isc1 = Integer.parseInt(sc1);

            //驗證公式：先字母後數字
            if (getabc_Str.substring(char1, (char1 + 1)).equals(abc[char0]) &&
                    getabc_Str.substring((char0 * char1 + 1), (char0 * char1 + 2)).equals(abc[char1])) {

                if ((isc0 * isc1 - (isc1 - isc0)) - isc0 == char1)
                    ischeck = true;
            }

        } catch (Exception e) {
            if (uselog) Log.d(TAG, "error=" + e.toString());
        }

        return ischeck;
    }
    //*------------------------------------------------------------------------------------------*//

    //password驗證
    static boolean alice377_pwget(String str) {
        char[] getabc = str.toCharArray(); //字串轉字元陣列
        int[] getabc_int = new int[getabc.length]; //設定數字陣列長度
        StringBuilder getabc_Str = new StringBuilder(); //設定存放英文字元
        char[] abc = "abcdefghijklmnopqrstuvwxyz".toCharArray(); //26個字母字元陣列
        boolean ischeck = false; //記錄是否通過驗證

        //使用者輸入的字串處理------------------------------------------------------------------------
        for (int i = 0; i < str.length(); i++) { //字串轉換分別存入數字及英文陣列

            if (getabc[i] > 47 && getabc[i] < 58) { //數字0~9

                for (int j = 0; j <= i; j++) {

                    if (j == i)
                        getabc_int[j] = Integer.parseInt(String.valueOf(getabc[i]));

                }
                if (uselog) Log.d(TAG, "getabc_int=" + getabc_int);

            } else if (getabc[i] > 96 && getabc[i] < 123) { //英文小寫字母

                for (int k = 0; k <= i; k++) {

                    if (k == i) {
                        String save_Str = getabc_Str.substring(0, k);
                        getabc_Str = new StringBuilder(save_Str + String.valueOf(getabc[i]));

                    } else {
                        getabc_Str.append("z");
                    }

                }
                if (uselog) Log.d(TAG, "getabc_Str=" + getabc_Str);
            }

        }

        if (uselog) Log.d(TAG, "getabc_int=" + getabc_int + "getabc_Str=" + getabc_Str);
        //------------------------------------------------------------------------------------------
        try {
            int char0 = getabc[0] - 97; //ASCII第一位計算
            int char1 = getabc[3] - getabc[2]; //ASCII第三位與第二位的差
            int char2 = getabc[4] - getabc[5]; //ASCII第四位與第五位的差
            int char3 = getabc[6] - getabc[7]; //ASCII第六位與第七位的差

            //驗證公式:先字母後數字
            if (str.substring(0, 1).equals(String.valueOf(abc[char0]))
                    && str.substring(1, 2).equals(String.valueOf(abc[char1]))) {

                if (char3 - char2 - 6 == char1 && char0 - 3 * 5 == 0) //數字驗證
                    ischeck = true; //驗證通過
            }

        } catch (Exception e) {
            if (uselog) Log.d(TAG, "error=" + e.toString());
        }

        return ischeck;
    }

    //日期計算：今天日期回推45天的日期
    public String calculateday(String today) {
        int yearsp = 0; //記錄是否為閏年
        int[] bmonth = {1, 3, 5, 7, 8, 10, 12}; //大月31天
        int big_month = 0; //記錄是否為大月(三月不標記)
        int num = 0; //記錄跨月的天數

        String year = today.substring(0, 4); //提取字元範圍:substring(start,end)
        String month = today.substring(5, 7);
        String day = today.substring(8); //end沒寫就是執行到最後一位
        String start_day = ""; //記錄計算後的start_day

        //先判斷是否為閏年:規則= 1.可被4整除但不被100整除 2.可被400整除
        int iyear = Integer.parseInt(year); //轉成整數

        if (iyear % 4 == 0 && iyear % 100 != 0 || iyear % 400 == 0) {
//            Log.(TAG, iyear + "年是閏年!");
            yearsp = 1;
        }

        //判斷是否為大月
        int imonth = Integer.parseInt(month);
        int iday = Integer.parseInt(day);
//        Log.d(TAG, "bmonth.length=" + bmonth.length);

        for (int m = 0; m < bmonth.length; m++) {

            if (bmonth[m] == imonth) {
                big_month = 1; //標記大月

                if (imonth == 1 || imonth == 8) {

                    if (iday < 14) {
                        num = 14 - iday; //跨上上個月幾天

                        if (imonth == 1) {
                            start_day = (iyear - 1) + "/11/" + (30 - num + 1);

                        } else {
                            start_day = (iyear - 1) + "/" + (imonth - 2) + "/" + (30 - num + 1);
                        }

                    } else {
                        num = iday - 14;

                        if (imonth == 1) {
                            start_day = (iyear - 1) + "/12/" + (1 + num);

                        } else {
                            start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                        }

                    }

                } else if (imonth == 3) {

                    if (yearsp == 0) { //不是閏年

                        if (iday < 17) {
                            num = 17 - iday; //跨上上個月幾天
                            start_day = year + "/" + (imonth - 2) + "/" + (31 - num + 1);

                        } else {
                            num = iday - 17;
                            start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                        }

                    } else {

                        if (iday < 16) {
                            num = 16 - iday; //跨上上個月幾天
                            start_day = year + "/" + (imonth - 2) + "/" + (31 - num + 1);

                        } else {
                            num = iday - 16;
                            start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                        }

                    }

                } else {

                    if (iday < 15) {
                        num = 15 - iday; //跨上上個月幾天
                        start_day = year + "/" + (imonth - 2) + "/" + (31 - num + 1);

                    } else {
                        num = iday - 15;
                        start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                    }

                }

                break;
            }

        }

        //判斷是否為小月
        if (big_month == 0) {

            if (imonth == 2 || imonth == 9) { //2月或9月

                if (iday < 14) {
                    num = 14 - iday; //跨上上個月幾天

                    if (imonth == 2) {
                        start_day = (iyear - 1) + "/12/" + (31 - num + 1);

                    } else {
                        start_day = year + "/" + (imonth - 2) + "/" + (31 - num + 1);
                    }

                } else {
                    num = iday - 14;
                    start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                }

            } else if (imonth == 4) { //4月

                if (iday < 14) {
                    num = 14 - iday; //跨上上個月幾天
                    start_day = year + "/" + (imonth - 2) + "/";

                    if (yearsp == 0) { //不是閏年
                        start_day += (28 - num + 1);

                    } else {
                        start_day += (29 - num + 1);
                    }

                } else {
                    num = iday - 14;
                    start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                }

            } else {

                if (iday < 14) {
                    num = 14 - iday; //跨上上個月幾天
                    start_day = year + "/" + (imonth - 2) + "/" + (31 - num + 1);

                } else {
                    num = iday - 14;
                    start_day = year + "/" + (imonth - 1) + "/" + (1 + num);
                }

            }

        }

//        Log.d(TAG,"start_date=" + start_day);
        return start_day;
    }
    //*------------------------------------------------------------------------------------------*//

//    void testNewUserPush() {
//        Log.d(TAG, "測試上傳github");
//    }

}
