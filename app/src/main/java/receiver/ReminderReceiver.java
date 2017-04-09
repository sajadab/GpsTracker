package receiver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.test.gpstracker.R;

import model.HeartBeatRequestBody;
import model.HeartBeatResponse;
import restOptions.ServiceHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.test.gpstracker.MapsActivity.NOTIF_DISMISS;
import static com.test.gpstracker.MapsActivity.NOTIF_ID;
import static com.test.gpstracker.MapsActivity.NOTIF_INTENT_KEY;
import static com.test.gpstracker.MapsActivity.NOTIF_OPEN;

/**
 * Created by SAJJAD on 4/7/2017.
 */
public class ReminderReceiver extends BroadcastReceiver {
    ServiceHelper serviceHelper = new ServiceHelper();


    @Override
    public void onReceive(final Context context, Intent intent) {
        final Location location = getMyLocation(context.getApplicationContext());
        serviceHelper.createAdapter();
        final HeartBeatRequestBody heartBeatRequestBody = new HeartBeatRequestBody();
        heartBeatRequestBody.setLat(location.getLatitude());
        heartBeatRequestBody.setLng(location.getLongitude());
        serviceHelper.setHeartBeat(heartBeatRequestBody, new Callback<HeartBeatResponse>() {
            @Override
            public void success(HeartBeatResponse heartBeatResponse, Response response) {
//                Toast.makeText(context, heartBeatRequestBody.getLat() + " " + heartBeatRequestBody.getLng()
//                        , Toast.LENGTH_LONG).show();
                makeNotif(context, location);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void makeNotif(Context context, Location location) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        remoteViews.setTextViewText(R.id.notifLat, "lat= " + location.getLatitude());
        remoteViews.setTextViewText(R.id.notifLng, "lng= " + location.getLongitude());

        Intent intent = new Intent(context, NotifReceiver.class);
        intent.putExtra(NOTIF_INTENT_KEY, NOTIF_OPEN);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                20, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.open, pendingIntent);

        Intent intentm = new Intent(context, NotifReceiver.class);
        intentm.putExtra(NOTIF_INTENT_KEY, NOTIF_DISMISS);
        PendingIntent pendingIntentm = PendingIntent.getBroadcast(context,
                22, intentm, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dismiss, pendingIntentm);

        Notification notification = new NotificationCompat.Builder(context)
                .setContent(remoteViews)
                .setCustomBigContentView(remoteViews)
//                        .setContentIntent(pendingIntent)
                .setCustomHeadsUpContentView(remoteViews)
//                        .setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(Notification.PRIORITY_MAX)
//                        .addAction(R.id.open,"op",pendingIntent)
//                        .addAction(R.id.close,"cl",pendingIntentm)
                .setFullScreenIntent(pendingIntentm, true)
                .setOngoing(false)
                .setAutoCancel(false)
                .setTicker("Location changed")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID, notification);
    }


    private Location getMyLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = lm.getBestProvider(criteria, true);
        myLocation = lm.getLastKnownLocation(provider);
        return myLocation;
    }
}
