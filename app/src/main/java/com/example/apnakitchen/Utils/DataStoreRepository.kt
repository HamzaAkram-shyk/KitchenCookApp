package com.example.apnakitchen.Utils

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.first

import java.util.prefs.Preferences

class DataStoreRepository(private var context: Context) {
    private val id = "userid"
    private val userType = "usertype"
    private val status = "status"
    private val locationAlert = "alert"
    private val firstVisitCook = "firstVisit"
    private val imageUrl = "url"
    private val userName = "userName"
    private val cookAddress = "address"
    private val cookIntro = "Intro"
    private val orderKey = "orderKey"
    private val storeName = "DataStore"
    private val dataStore = context.createDataStore(name = storeName)
    private val userIdKey = preferencesKey<String>(id)
    private val userTypeKey = preferencesKey<String>(userType)
    private val userStatusKey = preferencesKey<Boolean>(status)
    private val userImageKey = preferencesKey<String>(imageUrl)
    private val userNameKey = preferencesKey<String>(userName)
    private val cookIntroKey = preferencesKey<String>(cookIntro)
    private val cookAddressKey = preferencesKey<String>(cookAddress)
    private val cookVisitKey = preferencesKey<Boolean>(firstVisitCook)
    private val orderQueue = preferencesKey<Boolean>(orderKey)

    companion object {
        private var instance: DataStoreRepository? = null

        fun getInstance(context: Context): DataStoreRepository {
            return instance ?: synchronized(this) {
                instance ?: DataStoreRepository(context).also { instance = it }
            }
        }


    }

    suspend fun setUserId(uid: String) {
        dataStore.edit { settings ->
            settings[userIdKey] = uid
        }
    }

    suspend fun getUserId(): String? {
        val pref = dataStore.data.first()
        return pref[userIdKey]
    }


    suspend fun setUserName(name: String) {
        dataStore.edit { settings ->
            settings[userNameKey] = name
        }
    }

    suspend fun getUserName(): String? {
        val pref = dataStore.data.first()
        return pref[userNameKey]
    }

    suspend fun setUserImage(image: String) {
        dataStore.edit { settings ->
            settings[userImageKey] = image
        }
    }

    suspend fun getUserImage(): String? {
        val pref = dataStore.data.first()
        return pref[userImageKey]
    }


    suspend fun setCookAddress(address: String) {
        dataStore.edit { settings ->
            settings[cookAddressKey] = address
        }
    }

    suspend fun getAddress(): String? {
        val pref = dataStore.data.first()
        return pref[cookAddressKey]
    }


    suspend fun setCookAbout(intro: String) {
        dataStore.edit { settings ->
            settings[cookIntroKey] = intro
        }
    }

    suspend fun getCookAbout(): String? {
        val pref = dataStore.data.first()
        return pref[cookIntroKey]
    }


    suspend fun setFirstVisit(visit: Boolean) {
        dataStore.edit { settings ->
            settings[cookVisitKey] = visit
        }
    }

    suspend fun getFirstVisit(): Boolean? {
        val pref = dataStore.data.first()
        return pref[cookVisitKey] ?: true
    }


    suspend fun setOrderQueue(value: Boolean) {
        dataStore.edit { settings ->
            settings[orderQueue] = value
        }
    }

    suspend fun getOrderQueue(): Boolean? {
        val pref = dataStore.data.first()
        return pref[orderQueue] ?: false
    }

    suspend fun setUserLogin(status: Boolean) {
        dataStore.edit { settings ->
            settings[userStatusKey] = status
        }
    }

    suspend fun isUserLogin(): Boolean? {
        val pref = dataStore.data.first()
        return pref[userStatusKey] ?: false
    }

    suspend fun setUserType(value: String) {
        dataStore.edit { settings ->
            settings[userTypeKey] = value
        }
    }

    suspend fun getUserType(): String? {
        val pref = dataStore.data.first()
        return pref[userTypeKey] ?: "null"
    }


}


