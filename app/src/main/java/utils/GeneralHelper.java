package utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.test.gpstracker.MainApp;


public class GeneralHelper {

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }

//    public static void analyticLog(Context context, String category, String action, String label, Long value) {
//        EasyTracker.getInstance(context).send(MapBuilder.createEvent(category, action, label, value).build());
//    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void setStatusBarColor(Context context,int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(color));
        }
    }

    public static void setDrawBack(Context context, View view, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(context.getDrawable(drawable));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(drawable));
        }
    }
    public static void setColorBack(Context context, View view, int color) {
            view.setBackgroundColor(context.getResources().getColor(color));
    }

    public static float dipToPixels(float dipValue) {
        float scale = MainApp.getAppContext().getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (dipValue * scale + 0.5f);
        return padding_in_px;
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String setChangeTimeToString(long t) {
        int h = (int) (t / 3600000);
        int m = (int) (t - h * 3600000) / 60000;
        int s = (int) (t - h * 3600000 - m * 60000) / 1000;
        String hh = h < 10 ? "" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        String total=hh + ":" + mm + ":" + ss;
        return total;
    }

    public static String setChangeNumToPersian(String text){
        String t = text;
        t = t.replaceAll("0", "۰");
        t = t.replaceAll("1", "۱");
        t = t.replaceAll("2", "۲");
        t = t.replaceAll("3", "۳");
        t = t.replaceAll("4", "۴");
        t = t.replaceAll("5", "۵");
        t = t.replaceAll("6", "۶");
        t = t.replaceAll("7", "۷");
        t = t.replaceAll("8", "۸");
        t = t.replaceAll("9", "۹");
        return t;
    }

}
