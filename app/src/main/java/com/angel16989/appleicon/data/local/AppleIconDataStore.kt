package com.angel16989.appleicon.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.appleIconDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AirPodsPreferencesRepository.DATASTORE_NAME,
)
