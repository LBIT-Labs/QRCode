package workshop.lbit.qrcode.fcm_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import workshop.lbit.qrcode.R
import workshop.lbit.qrcode.ui.Splashscreen
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {


    private val TAG = "MyFirebaseToken"
    private lateinit var notificationManager: NotificationManager
    private val ADMIN_CHANNEL_ID = "LetzBank"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.let { message ->

            val params = remoteMessage.data
            //            val `object` = JSONObject(params)
            //            Log.e("JSON_OBJECT", `object`.toString())


            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["body"]
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Setting up Notification channels for android O and above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupNotificationChannels()
            }
            val notificationId = Random().nextInt(60000)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_qrcode)  //a resource for your custom small icon
                .setContentTitle(remoteMessage.notification?.title) //the "title" value you sent in your notification
                .setContentText(remoteMessage.notification?.body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build())

            sendNotification(title,message)

        }


    }

    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")

    }

    private fun sendNotification(title: String?, message: String?) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val CHANNEL_ID = resources.getString(R.string.default_notification_channel_id)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notification_id = (Math.random() * 100).toInt() + System.currentTimeMillis().toInt()


        val intent = Intent(this, Splashscreen::class.java)
        intent.data = Uri.Builder().scheme(title).build()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("tagFrom", title)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mChannel = NotificationChannel(CHANNEL_ID, resources.getString(R.string.app_name), importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val mBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon_qrcode)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon_qrcode))
            .setContentTitle(title)
            .setContentText(message)
        mBuilder.setContentIntent(contentIntent)
        mBuilder.setAutoCancel(true)
        mBuilder.setSound(uri)
        mBuilder.setChannelId(CHANNEL_ID)
        notificationManager.notify(notification_id, mBuilder.build())


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}