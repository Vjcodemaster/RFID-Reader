package com.autochip.rfidreader;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app_utility.NetworkState;
import app_utility.PowerReceiver;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static app_utility.StaticReferenceClass.sINVENTORYURL;
import static app_utility.StaticReferenceClass.sRFIDURL;

public class RFIDReadService extends Service {

    String channelId = "app_utility.TrackingService";
    String channelName = "tracking";
    public static int stockFlag = 0;

    public static boolean isAlreadyInProgress = false;

    static RFIDReadService refOfService;
    NetworkState networkState;
    //IntentFilter intentFilterData;
    //BroadcastReceiver broadcastReceiverData;

    String sPreviousRFID = "";
    NotificationManager notifyMgr;
    NotificationCompat.Builder nBuilder;
    NotificationCompat.InboxStyle inboxStyle;

    public RFIDReadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refOfService = this;
        networkState = new NetworkState();
        /*intentFilterData = new IntentFilter("com.scanner.broadcast");
        broadcastReceiverData = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String s = intent.getExtras().getString("data");
                Toast.makeText(getApplicationContext(), "" + alData.size(), Toast.LENGTH_SHORT).show();
                //testWithAlDataBase(alBeaconInfo);
            }
        };
        getApplicationContext().registerReceiver(broadcastReceiverData, intentFilterData);*/
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        //intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new PowerReceiver();
        registerReceiver(mReceiver, intentFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("RFIDReadService", "onStartCommand called");
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener( new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() {
                ClipData clip = clipboard.getPrimaryClip();
                CharSequence a;
                String sMsg;
                String URL;
                if(stockFlag==0) {
                    sMsg = "false";
                    URL = sRFIDURL;
                }
                else {
                    sMsg = "true";
                    URL = sINVENTORYURL;
                }

                if (networkState.isOnline() && networkState.isNetworkAvailable(getApplicationContext())) {
                    if (clip != null && clip.getItemCount() > 0 && !isAlreadyInProgress) {
                        isAlreadyInProgress = true;
                        a = clip.getItemAt(0).coerceToText(getApplicationContext());
                    /*if(clip.getItemCount()>1){
                        Toast.makeText(getApplicationContext(), ""+ clip.getItemCount(), Toast.LENGTH_SHORT).show();
                    }*/
                        //if(!sPreviousRFID.equals(a.toString())) {
                        HashMap<String, String> params = new HashMap<>();
                        /*ArrayList<String> alTmp = new ArrayList<>();
                        alTmp.add(a.toString());*/
                        params.put("db", "Trufrost-Live"); //Trufrost-Testing
                        params.put("user", "admin");
                        params.put("password", "autochip@505");
                        //params.put("rfids", String.valueOf(alTmp));
                        String text = a.toString();
                        String sRFID = text.substring(0, a.length() - 2);
                        params.put("rfids", sRFID);
                        params.put("start_inventory", sMsg);
                        VolleyTask volleyTask = new VolleyTask(getApplicationContext(), params, "PUSH_RFID", stockFlag, URL);
                        sPreviousRFID = a.toString();
                        notifyUser(""+a);
                        //}
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                    if(notifyMgr!=null){
                        notifyMgr.cancel(515);
                    }
                }
                /*if(onServiceInterface!=null){
                    onServiceInterface.onServiceCall("RFID", a.toString());
                } else {
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                    onServiceInterface.onServiceCall("RFID", a.toString());
                }*/
                //Toast.makeText(getBaseContext(),"Copy:\n"+a,Toast.LENGTH_LONG).show();

            }
        });
        return START_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), createNotificationChannel() );
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent in = new Intent(getApplicationContext(), RFIDReadService.class);
        startService(in);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private void notifyUser(String sRFID) {
        inboxStyle = new NotificationCompat.InboxStyle();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nBuilder = new NotificationCompat.Builder(RFIDReadService.
                this, channelId)
                .setSmallIcon(R.drawable.rfid_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("RFID Number")
                .setSubText(sRFID)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        // Allows notification to be cancelled when user clicks it
        nBuilder.setAutoCancel(true);
        nBuilder.setStyle(inboxStyle);
        int notificationId = 515;
        notifyMgr.notify(notificationId, nBuilder.build());
    }


}
