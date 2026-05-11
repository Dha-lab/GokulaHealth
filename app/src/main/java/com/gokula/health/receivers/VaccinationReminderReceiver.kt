package com.gokula.health.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gokula.health.R
import com.gokula.health.ui.main.MainActivity

class VaccinationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val vaccineName = intent.getStringExtra("vaccineName") ?: "Vaccine"
        val earTag = intent.getStringExtra("earTag") ?: ""
        val id = intent.getLongExtra("id", 0L).toInt()

        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "vaccination_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💉 Vaccination Due Today!")
            .setContentText("$vaccineName for cattle $earTag is scheduled today")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$vaccineName for cattle tag $earTag is due for vaccination today. Please complete this vaccination as soon as possible."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, notification)
    }
}