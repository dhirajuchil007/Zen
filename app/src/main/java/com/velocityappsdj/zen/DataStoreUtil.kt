package com.velocityappsdj.zen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.concurrent.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "batch"
)
var prefKey = stringPreferencesKey("batch_id")

class DataStoreUtil(var context: Context) {
    suspend fun saveBatch(batch: String) {
        context.dataStore.edit {
            it[prefKey] = batch
        }
    }

    fun getBatch() = context.dataStore.data


}