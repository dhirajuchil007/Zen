package com.velocityappsdj.zen.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import com.velocityappsdj.zen.R

class BatteryPermission : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName: String = getPackageName()
            val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                startActivity(Intent(this, MainActivity::class.java))
            }
            findViewById<Button>(R.id.btn_battery_action).setOnClickListener {
                openBatterySettings()
            }


        }
    }

    private fun openBatterySettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(intent)
    }
}