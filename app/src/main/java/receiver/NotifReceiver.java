package receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.gpstracker.MapsActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.test.gpstracker.MapsActivity.NOTIF_DISMISS;
import static com.test.gpstracker.MapsActivity.NOTIF_ID;
import static com.test.gpstracker.MapsActivity.NOTIF_INTENT_KEY;
import static com.test.gpstracker.MapsActivity.NOTIF_OPEN;

/**
 * Created by salimi on 4/9/2017.
 */

public class NotifReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().getString(NOTIF_INTENT_KEY)!=null && intent.getExtras().getString(NOTIF_INTENT_KEY).equals(NOTIF_DISMISS)) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIF_ID);
        } else if (intent.getExtras().getString(NOTIF_INTENT_KEY)!=null && intent.getExtras().getString(NOTIF_INTENT_KEY).equals(NOTIF_OPEN)) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIF_ID);
            Intent activityIntent=new Intent(context, MapsActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService (Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
//            boolean is_pageExist=false;
//            for (int i1 = 0; i1 < services.size(); i1++) {
//                String st=services.get(i1).baseActivity.getClassName();
//                if (!st.equals("com.test.gpstracker.MapsActivity")) {
//                    is_pageExist=true;
//                }
//            }
//            if (!is_pageExist) {
                context.startActivity(activityIntent);
//            }
        }
    }
}
