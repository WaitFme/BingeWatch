package com.anpe.bingewatch.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {
    suspend fun editPreference(key: String, value: String) {
        val stringKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[stringKey] = value
        }
    }

    fun readPreference(key: String): Flow<String> {
        val stringKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[stringKey] ?: ""
        }
    }

    suspend fun editIntPreference(key: String, value: Int) {
        val intKey = intPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[intKey] = value
        }
    }

    fun readIntPreference(key: String): Flow<Int> {
        val intKey = intPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[intKey] ?: 0
        }
    }
}
