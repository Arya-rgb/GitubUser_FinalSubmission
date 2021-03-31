package com.thorin.dicoding.broadcastreceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.thorin.dicoding.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotifyReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "channel_github_reminder"
        private const val NOTIFICATION_ID = 1
        private const val TIME_FORMAT = "HH:mm"
        private const val ID_REPEATING = 101
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_TYPE = "extra_type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        sendNotification(context)

    }

    private fun sendNotification(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage("com.thorin.dicoding")
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText(context.resources.getString(R.string.notification_content))
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun setRepeatingAlarm(context: Context, type: String, time: String, message: String) {
        if (TIME_FORMAT.isDateInvalid(time)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotifyReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        Toast.makeText(
            context,
            context.resources.getString(R.string.notification_toast),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun String.isDateInvalid(time: String): Boolean {
        return try {

            val df = SimpleDateFormat(this, Locale.getDefault())
            df.isLenient = false
            df.parse(time)
            false

        } catch (
            e: ParseException
        ) {
            true
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotifyReceiver::class.java)
        val requestNotifyCode = ID_REPEATING
        val pendingIntent = PendingIntent.getBroadcast(context, requestNotifyCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(
            context,
            context.resources.getString(R.string.notification_toast_cancel),
            Toast.LENGTH_SHORT
        ).show()
    }

}