package com.example.cloud;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.concurrent.TimeUnit;

public class BackgroundService extends IntentService {
    private static final String TAG = "BackgroundService";
    private static final String EXTRA_TODAY_WEATHER = "EXTRA_TODAY_WEATHER";
    private static final long SERVICE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, BackgroundService.class);
        return intent;
    }

    public BackgroundService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");

        Intent i = MainActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        DayWeather dw = WeatherList.get(getBaseContext()).getTodayWeather();

        Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, 0);

         NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(getBaseContext().getApplicationInfo().icon)
                .setShowWhen(true).setAutoCancel(true).setContentIntent(resultPendingIntent);

        String daytext = dw.getTextDay();
        String w = "";
        int intday = Integer.parseInt(daytext);
        if (intday == 100 || intday == 102 || intday == 103) {
            w = "Sunny";
            builder.setContentTitle("Sunny");
            builder.setLargeIcon(getBitmapFromVectorDrawable(getBaseContext(), R.drawable.sunny));
        } else if (intday >= 300 && intday <= 500) {
            w = "Rainy";
            builder.setContentTitle("Rainy");
            builder.setLargeIcon(getBitmapFromVectorDrawable(getBaseContext(), R.drawable.rainy));
        } else {
            w = "Cloudy";
            builder.setContentTitle("Cloudy");
            builder.setLargeIcon(getBitmapFromVectorDrawable(getBaseContext(), R.drawable.other));
        }
        String content = "Temperature: " + dw.getTemperatureMin() + "° ~ " + dw.getTemperatureMax() + "°";
        builder.setContentText(content);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = BackgroundService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i ,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), SERVICE_INTERVAL_MS, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = BackgroundService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        @SuppressLint("RestrictedApi")
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
