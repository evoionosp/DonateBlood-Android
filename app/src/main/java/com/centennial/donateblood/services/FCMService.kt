
package com.google.firebase.quickstart.fcm.kotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.centennial.donateblood.R
import com.centennial.donateblood.activities.MainActivity
import com.centennial.donateblood.utils.MyWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Kotlin sample functional, you must remove the following from the Java messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
class FCMService : FirebaseMessagingService() {


        override fun onMessageReceived(remoteMessage: RemoteMessage?) {


                // TODO(developer): Handle FCM messages here.
                // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
                Log.d(TAG, "From: ${remoteMessage?.from}")

                // Check if message contains a data payload.
                remoteMessage?.data?.isNotEmpty()?.let {
                        Log.d(TAG, "Message data payload: " + remoteMessage.data)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                sendNotification(remoteMessage.data.getOrDefault("blood_group", "Donate Blood"), "Emergency: Blood required at "+remoteMessage.data.getOrDefault("hospital_name", "Hospital"))
                        } else {

                                if (remoteMessage.data.get("blood_group") != null && remoteMessage.data.get("hospital_name") != null){

                                        sendNotification(remoteMessage.data.get("blood_group").toString(), "Emergency: Blood required at "+remoteMessage.data.get("hospital_name"))
                                } else {
                                        sendNotification("DonateBlood", "You have notification")
                                }
                        }
                }

                // Check if message contains a notification payload.
                remoteMessage?.notification?.let {
                        Log.d(TAG, "Message Notification Body: ${it.body}")
                }

                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.
        }
        // [END receive_message]

        // [START on_new_token]
        /**
         * Called if InstanceID token is updated. This may occur if the security of
         * the previous token had been compromised. Note that this is called when the InstanceID token
         * is initially generated so this is where you would retrieve the token.
         */
        override fun onNewToken(token: String?) {
                Log.d(TAG, "Refreshed token: $token")

        }

        private fun scheduleJob() {
                // [START dispatch_job]
                val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
                WorkManager.getInstance().beginWith(work).enqueue()
                // [END dispatch_job]
        }


        private fun sendNotification(title: String, messageBody: String) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(
                        this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT
                )

                val channelId = getString(R.string.default_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_bloodgroup)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(false)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                                channelId,
                                "Donate Blood Notifications",
                                NotificationManager.IMPORTANCE_HIGH
                        )
                        notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify( NOTIFICATION_ID, notificationBuilder.build())
        }

        companion object {

                private const val TAG = "FCMService"
                private const val NOTIFICATION_ID = 153
        }
}