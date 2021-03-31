package com.thorin.dicoding.sharedpref

import android.content.Context
import com.thorin.dicoding.model.NotifyReminder

class NotifyReminderPref(context: Context) {
    companion object {
        const val PREFS_NOTIFY_NAME = "reminder_pref"
        private const val REMINDER = "isRemind"
    }

    private val preference = context.getSharedPreferences(PREFS_NOTIFY_NAME, Context.MODE_PRIVATE)

    fun setReminder(value: NotifyReminder) {

        val editor = preference.edit()
        editor.putBoolean(REMINDER, value.isReminder)
        editor.apply()

    }

    fun getReminder(): NotifyReminder {
        val model = NotifyReminder()
        model.isReminder = preference.getBoolean(REMINDER, false)
        return model
    }

}