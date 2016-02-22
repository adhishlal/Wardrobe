package adhish.crowdfire.com.wardrobe;

/**
 * Created by Adhish on 22/02/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task
        showNotification(context);
    }

    public void showNotification(Context ctx) {
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(ctx)
                .setTicker("Crowdfire's Wardrobe")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Wardrobe")
                .setContentText("Hey! Check out what to wear today.")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
