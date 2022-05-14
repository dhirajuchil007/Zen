package com.velocityappsdj.zen

import android.content.Context
import android.content.SharedPreferences

class SharedPrefUtil(var context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("name", Context.MODE_PRIVATE)

    fun setZenMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("ZEN_MODE", enabled).apply()
    }

    fun isZenModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("ZEN_MODE", false);
    }

    fun setDefaultSetupDone(done: Boolean) {
        sharedPreferences.edit().putBoolean("DEFAULT_SETUP", done).apply();
    }

    fun isDefaultSetupDone(): Boolean {
        return sharedPreferences.getBoolean("DEFAULT_SETUP", false);
    }

    fun setCurrentBatchId(id: Int) {
        sharedPreferences.edit().putInt("BATCH_ID", id).apply();
    }

    fun getCurrentBatchId(): Int {
        return sharedPreferences.getInt("BATCH_ID", 0);
    }

    fun setCurrentBatchPrimaryKey(key: String) {
        sharedPreferences.edit().putString("BATCH_KEY", key).apply()
    }

    fun getCurrentBatchPrimaryKey(): String? {
        return sharedPreferences.getString("BATCH_KEY", "")
    }


}