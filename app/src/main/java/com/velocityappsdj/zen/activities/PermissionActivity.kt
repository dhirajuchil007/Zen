package com.velocityappsdj.zen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.velocityappsdj.zen.R
import com.velocityappsdj.zen.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {
    private val TAG = "PermissionActivity"
    lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)
        if (Settings.Secure.getString(
                this.getContentResolver(),
                "enabled_notification_listeners"
            ).contains(getApplicationContext().getPackageName())
        ) {
            startActivity(Intent(this, MainActivity::class.java))

        }
        binding.btnPermission.setOnClickListener {
            if (Settings.Secure.getString(
                    this.getContentResolver(),
                    "enabled_notification_listeners"
                ).contains(getApplicationContext().getPackageName())
            ) {
                //service is enabled do something

            } else {

                AlertDialog.Builder(this)
                    .setMessage("On the next screen select Zen Notification manager and tap on allow")
                    .setPositiveButton("Okay") { dialog, _ ->
                        this.startActivity(
                            Intent(
                                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                            )
                        )
                    }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Settings.Secure.getString(
                this.getContentResolver(),
                "enabled_notification_listeners"
            ).contains(getApplicationContext().getPackageName())
        ) {
            startActivity(Intent(this, MainActivity::class.java))

        }

    }
}