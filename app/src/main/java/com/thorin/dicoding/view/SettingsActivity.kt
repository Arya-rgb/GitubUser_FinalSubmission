package com.thorin.dicoding.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.thorin.dicoding.R
import com.thorin.dicoding.broadcastreceiver.NotifyReceiver
import com.thorin.dicoding.databinding.ActivitySettingsBinding
import com.thorin.dicoding.model.NotifyReminder
import com.thorin.dicoding.sharedpref.NotifyReminderPref
import com.thorin.dicoding.sharedpref.SharedPrefNightMode

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPrefNightMode
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var reminder: NotifyReminder
    private lateinit var notifyReceiver: NotifyReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val remaindered = NotifyReminderPref(this)
        binding.switchReminder.isChecked = remaindered.getReminder().isReminder

        notifyReceiver = NotifyReceiver()

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveReminder(true)
                notifyReceiver.setRepeatingAlarm(this, "RepeatingAlarm", "09:00", "Github Reminder")
            } else {
                saveReminder(false)
                notifyReceiver.cancelAlarm(this)
            }
        }

        supportActionBar?.title = resources.getString(R.string.Setting)

        sharedPref = SharedPrefNightMode(this)
        if (sharedPref.loadNightModeState()) {
            binding.darkMode.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding.darkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPref.setNightModeState(true)
            } else {
                sharedPref.setNightModeState(false)
            }
            this.recreate()
        }
        binding.btnLanguage.setOnClickListener {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
    }

    private fun saveReminder(state: Boolean) {
        val reminderPref = NotifyReminderPref(this)
        reminder = NotifyReminder()

        reminder.isReminder = state
        reminderPref.setReminder(reminder)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}