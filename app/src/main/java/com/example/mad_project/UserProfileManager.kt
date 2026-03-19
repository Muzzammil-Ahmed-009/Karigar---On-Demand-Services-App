package com.example.mad_project

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton helper to save & load user profile data using SharedPreferences.
 * Data persists between app sessions.
 */
object UserProfileManager {

    private const val PREFS_NAME  = "karigar_user_profile"
    private const val KEY_NAME    = "profile_name"
    private const val KEY_EMAIL   = "profile_email"
    private const val KEY_CONTACT = "profile_contact"
    private const val KEY_CITY    = "profile_city"
    private const val KEY_GENDER  = "profile_gender"
    private const val KEY_PHOTO_URI = "profile_photo_uri"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveName(context: Context, name: String)    = prefs(context).edit().putString(KEY_NAME,    name).apply()
    fun saveEmail(context: Context, email: String)  = prefs(context).edit().putString(KEY_EMAIL,   email).apply()
    fun saveContact(context: Context, c: String)    = prefs(context).edit().putString(KEY_CONTACT, c).apply()
    fun saveCity(context: Context, city: String)    = prefs(context).edit().putString(KEY_CITY,    city).apply()
    fun saveGender(context: Context, gender: String)= prefs(context).edit().putString(KEY_GENDER,  gender).apply()
    fun savePhotoUri(context: Context, uri: String) = prefs(context).edit().putString(KEY_PHOTO_URI, uri).apply()

    fun getName(context: Context)     = prefs(context).getString(KEY_NAME,    "") ?: ""
    fun getEmail(context: Context)    = prefs(context).getString(KEY_EMAIL,   "") ?: ""
    fun getContact(context: Context)  = prefs(context).getString(KEY_CONTACT, "") ?: ""
    fun getCity(context: Context)     = prefs(context).getString(KEY_CITY,    "") ?: ""
    fun getGender(context: Context)   = prefs(context).getString(KEY_GENDER,  "Male") ?: "Male"
    fun getPhotoUri(context: Context) = prefs(context).getString(KEY_PHOTO_URI, null)

    /** Save all fields at once */
    fun saveAll(context: Context, name: String, email: String, contact: String,
                city: String, gender: String) {
        prefs(context).edit()
            .putString(KEY_NAME,    name)
            .putString(KEY_EMAIL,   email)
            .putString(KEY_CONTACT, contact)
            .putString(KEY_CITY,    city)
            .putString(KEY_GENDER,  gender)
            .apply()
    }
}
