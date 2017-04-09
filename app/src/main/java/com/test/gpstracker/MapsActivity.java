package com.test.gpstracker;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Button;

import model.HeartBeatRequestBody;
import model.HeartBeatResponse;
import receiver.NotifReceiver;
import receiver.ReminderReceiver;
import restOptions.ServiceHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utils.GeneralHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private static final int RECEIVER_REQUEST_CODE = 123;
    public static final String NOTIF_INTENT_KEY = "notif_intent";
    public static final String NOTIF_OPEN = "open";
    public static final String NOTIF_DISMISS = "dismiss";
    public static final int NOTIF_ID = 102;
    private static final String RECEIVER_ACTION = "action";

    private GoogleMap mMap;
    private AlarmManager am;
    private Button logOut;
    private GoogleApiClient googleApiClient;

    private double longitude;
    private double latitude;

    private Runnable runnable;
    private Handler handler = new Handler();
    private ServiceHelper serviceHelper = new ServiceHelper();


    private int oneSecond = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GeneralHelper.setStatusBarColor(this, R.color.status_color);
        checkGpsConnection();
        initialParams();
        itemClickListener();

    }

    private void checkGpsConnection() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            getCurrentLocation();
                            sendDataToServerInApp();

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MapsActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private void itemClickListener() {
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel service
                am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent amintent = new Intent();
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), RECEIVER_REQUEST_CODE, amintent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pi);
                //save login situation
                SharedPreferences.Editor editor = getSharedPreferences(LoginActivity.LOGIN_STATUS, 0).edit();
                editor.putBoolean(LoginActivity.LOGIN_KEY, false);
                editor.apply();

                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }


    private void initialParams() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        logOut = (Button) findViewById(R.id.logOut);
        runnable = new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
                serviceHelper.createAdapter();
                final HeartBeatRequestBody heartBeatRequestBody = new HeartBeatRequestBody();
                heartBeatRequestBody.setLat(latitude);
                heartBeatRequestBody.setLng(longitude);
                serviceHelper.setHeartBeat(heartBeatRequestBody, new Callback<HeartBeatResponse>() {
                    @Override
                    public void success(HeartBeatResponse heartBeatResponse, Response response) {
//                        Toast.makeText(MapsActivity.this, heartBeatResponse.getStatus()
//                                        + " " + heartBeatRequestBody.getLat() + " " + heartBeatRequestBody.getLng()
//                                , Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                sendDataToServerInApp();
            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    Toast.makeText(MapsActivity.this,"lat= "+ latitude
                                        + "lng= " + longitude
                                , Toast.LENGTH_LONG).show();
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    moveMap();
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void sendDataToServerInApp() {
        handler.postDelayed(runnable, 10000);
    }

    private void sendLocationToServer() {
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MapsActivity.this, ReminderReceiver.class);
        intent.setAction(RECEIVER_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), RECEIVER_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
        if (Build.VERSION.SDK_INT >= 19) {
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 10 * oneSecond, pi);
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 10 * oneSecond, pi);
        }
    }

    private void getCurrentLocation() {
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //moving the map to location
            moveMap();
        }
    }
    private void moveMap() {
        LatLng latLng = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true)
//                .title("Marker in your position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void makeNotif(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        remoteViews.setTextViewText(R.id.notifLat, "lat= " + latitude);
        remoteViews.setTextViewText(R.id.notifLng, "lng= " + longitude);

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


    @Override
    protected void onPause() {
        super.onPause();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            sendLocationToServer();
        }
        Log.d("sajad","pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
//            sendLocationToServer();
        }
        Log.d("sajad","stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), RECEIVER_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
//        sendDataToServerInApp();
        Log.d("sajad","resume");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
