package com.gokula.health.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.gokula.health.models.Vaccination
import com.gokula.health.receivers.VaccinationReminderReceiver

object AlarmHelper {
    fun scheduleReminder(context: Context, vaccination: Vaccination) {
        val intent = Intent(context, VaccinationReminderReceiver::class.java).apply {
            putExtra("vaccineName", vaccination.vaccineName)
            putExtra("earTag", vaccination.cattleEarTagId)
            putExtra("id", vaccination.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            vaccination.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    vaccination.scheduledDate,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    vaccination.scheduledDate,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                vaccination.scheduledDate,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, vaccinationId: Long) {
        val intent = Intent(context, VaccinationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            vaccinationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}