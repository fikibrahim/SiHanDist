package com.haerul.sihandist.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haerul.sihandist.R;
import com.haerul.sihandist.data.db.MasterDatabase;
import com.haerul.sihandist.data.db.repository.MasterRepository;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.ui.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.VISIBILITY_PRIVATE;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class AppService extends Service {

    private Notification notification;
    private NotificationManager notificationManager;

    private String CHANNEL_ID = "my_channel_01";// The id of the channel.
    private CharSequence name = "SHD";// The user-visible name of the channel.
    private CharSequence name2 = "SiHanDist";// The user-visible name of the channel.
    private int importance = NotificationManager.IMPORTANCE_NONE;
    private int importance_high = NotificationManager.IMPORTANCE_HIGH;
    private NotificationChannel mChannel = null;
    private int notifId = 101;

    private Handler handler ;
    private Runnable runnable;
    private int delayMillis = 60000;
    private MasterRepository repository;
    private String status_diterima;

    private static boolean isRunning;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            Log.w("getAction", intent.getAction());
            if (intent.getAction().equals(MainActivity.STARTFOREGROUND_ACTION)) {
                startServiceOreoCondition();
            } else if (intent.getAction().equals(MainActivity.STOPFOREGROUND_ACTION)) {
                Log.w("Stop", "stop");
                stopForeground(true);
                this.stopSelf();
            }
        }
        handler.postDelayed(runnable, 6000);
        return Service.START_STICKY; //super.onStartCommand(intent, flags, startId);/;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.d("SERVICE" , "onCreate : " + Util.getTimestampNow());
        repository = new MasterRepository(MasterDatabase.getDatabase(getBaseContext()));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        status_diterima = repository.getRefByValue(Constants.STATUS_GANGGUAN, 1).ref_sid;
        handler = new Handler();
        runnable = () -> {
            try {
                if (Util.getBooleanPreference(getBaseContext(), Constants.IS_LOGIN)) {
                    if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == 7) {
                        getInspeksi();
                    }
                    else if (repository.getRefBySID(Util.getStringPreference(this, Constants.USER_ROLE_SID)).ref_value == 6) {
                        getGangguan();
                    }
                    handler.postDelayed(runnable, delayMillis);
                } else {
                    if (AppService.isRunning()) {
                        Intent serviceIntent = new Intent(this, AppService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.w("stop", "foreground");
                            serviceIntent.setAction(MainActivity.STOPFOREGROUND_ACTION);
                            stopService(serviceIntent);
                        } else {
                            stopService(serviceIntent);
                            Log.w("stop", "background");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (AppService.isRunning()) {
                    Intent serviceIntent = new Intent(this, AppService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.w("stop", "foreground");
                        serviceIntent.setAction(MainActivity.STOPFOREGROUND_ACTION);
                        stopService(serviceIntent);
                    } else {
                        stopService(serviceIntent);
                        Log.w("stop", "background");
                    }
                }
            }
        };
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private Notification createNotification(String message) {
        Intent intent = null;
        PendingIntent pendingIntent = null;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationn_icon)
                .setContentTitle("⚡ SiHanDist")
                .setContentText(message)
                .setContentIntent(pendingIntent);

        return notificationBuilder.setChannelId(CHANNEL_ID).build();
    }

    private Notification createNotificationNew(String message) {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Intent intent = null;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tag", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        v.vibrate(1000);
        v.vibrate(500);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationn_icon)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_icon))
                .setContentTitle("⚠ SiHanDist")
                .setContentText(message)
                /*.addExtras(addData)*/
                .setContentIntent(pendingIntent)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500});
        notificationBuilder.setSound(defaultRingtoneUri);

        Notification notification = notificationBuilder.setChannelId(CHANNEL_ID).build();
        notification.ledARGB = 0xFFff0000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 100;
        notification.ledOffMS = 100;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults|= Notification.DEFAULT_SOUND;
        notification.defaults|= Notification.DEFAULT_LIGHTS;
        notification.defaults|= Notification.DEFAULT_VIBRATE;
        return notification;
    }

    private void launchNotification(String message, int notifId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
            notification = createNotification(message);
            notificationManager.notify(notifId, notification);
        } else {
            notification = createNotification(message);
            notificationManager.notify(notifId, notification);
        }
    }

    private void launchNotificationNew(String channel, String message, int notifId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(channel, name2, importance_high);
            notificationManager.createNotificationChannel(mChannel);
            notification = createNotificationNew(message);
            notificationManager.notify(notifId, notification);
        } else {
            notification = createNotificationNew(message);
            notificationManager.notify(notifId, notification);
        }
    }

    @Override
    public void onDestroy() {
        Log.w("TAG", "SERVICE REMOVED");
        isRunning = false;
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("onTaskRemoved called");
        Log.w(String.valueOf(this), "Service stop, called broadcast receiver");
        super.onTaskRemoved(rootIntent);
        
        /*Intent intent = new Intent(AppService.this, AppBroadcastReceiver.class);
        sendBroadcast(intent);*/
    }

    private void startServiceOreoCondition() {
        if (Build.VERSION.SDK_INT >= 26) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setLockscreenVisibility(VISIBILITY_PRIVATE);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCategory(Notification.CATEGORY_SYSTEM)
                    .setSmallIcon(R.drawable.notificationn_icon)
                    .setPriority(PRIORITY_MIN)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("SHD")
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .build();

            startForeground(notifId, notification);
            launchNotification("Tap to open the application.", notifId);
        }
    }
    
    
    //===============================================

    public void getInspeksi() {
        Log.w("-->", "sync " + UUID.randomUUID().toString().toUpperCase() + " time -> " + Util.getTimestampNow());
        if (repository.getRefBySID(Util.getStringPreference(AppService.this, Constants.USER_ROLE_SID)).ref_value == 7) {
            Log.w("-->", "user role " + "7" + " time -> " + Util.getTimestampNow());
            Inspeksi inspeksi = repository.getLastInspeksi();
            String lastDate;
            if (inspeksi == null) {
                lastDate = "";
                Log.w("-->", "insp null");
            } else {
                lastDate = inspeksi.wo_date.replace(" ", "T");
                Log.w("-->", "insp " + inspeksi.inspeksi_sid + " wodate -> " + inspeksi.wo_date);
            }
            String token = Util.getStringPreference(this, Constants.TOKEN_AUTH);
            Util.getApi().getInspeksi(token, 
                    Util.getStringPreference(this, Constants.USER_UNIT),
                    lastDate)
                .enqueue(new Callback<List<Inspeksi>>() {
                    @Override
                    public void onResponse(Call<List<Inspeksi>> call, Response<List<Inspeksi>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().size() > 0) {
                                int i = 0;
                                for (Inspeksi inspeksi: response.body()) {
                                    launchNotificationNew(
                                            inspeksi.inspeksi_sid,
                                            "Notifikasi Inspeksi! — " +
                                                    inspeksi.inspeksi_uid + "\n(" +
                                                    repository.getRefBySID(inspeksi.jenis_temuan_sid).ref_name + ")",
                                            notifId + 100 + i++);
                                    Log.w("Elemant ", inspeksi.inspeksi_uid);
                                    inspeksi.post_status = true;
                                    inspeksi.is_receive = true;
                                    Util.getApi().putInspeksi(token, new JsonParser().parse(new Gson().toJson(inspeksi)).getAsJsonObject()).enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            Log.w("TAG", "response_receive" + response.body().toString());
                                            repository.insertInspeksi(inspeksi);
                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            Log.w("TAG", "response_receive" + t.getLocalizedMessage());
                                            inspeksi.is_receive = false;
                                            repository.insertInspeksi(inspeksi);
                                        }
                                    });
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Inspeksi>> call, Throwable t) {}
                });
        }
    }

    public void getGangguan() {
        Log.w("-->", "sync " + UUID.randomUUID().toString().toUpperCase() + " time -> " + Util.getTimestampNow());
        Gangguan gangguan = repository.getLastGangguan();
        String lastDate;
        if (gangguan == null) {
            lastDate = "";
        } else {
            lastDate = gangguan.date_created.replace(" ", "T");
            Log.w("-->", "insp " + gangguan.g_sid + " date -> " + gangguan.date_created);
        }
        String token = Util.getStringPreference(this, Constants.TOKEN_AUTH);
        Util.getApi().getGangguan(token, Util.getStringPreference(this, Constants.USER_UNIT), lastDate)
                .enqueue(new Callback<List<Gangguan>>() {
                    @Override
                    public void onResponse(Call<List<Gangguan>> call, Response<List<Gangguan>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int i = 0;
                            if (response.body().size() > 0) {
                                for (Gangguan ggn : response.body()) {
                                    launchNotificationNew(
                                            ggn.g_sid,
                                            "Notifikasi Gangguan! — " + 
                                                    ggn.g_uid + "\n(" + 
                                                    repository.getRefBySID(ggn.g_indikasi).ref_name + ")", 
                                            notifId + 200 + i++);
                                    Log.w("Elemant ", ggn.g_sid);
                                    ggn.g_status = status_diterima;
                                    ggn.post_status = true;
                                    Util.getApi().putGangguan(token, new JsonParser().parse(new Gson().toJson(ggn)).getAsJsonObject()).enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            Log.w("TAG", "response_receive" + response.body().toString());
                                            repository.insertGangguan(ggn);
                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            Log.w("TAG", "response_receive" + t.getLocalizedMessage());
                                            repository.insertGangguan(ggn);
                                        }
                                    });
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Gangguan>> call, Throwable t) {}
                });
    }
}
