package com.holytrinity.util

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {

    private const val PREFS_NAME = "UserPrefs"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Retrieve user ID
    fun getUserId(context: Context): Int {
        return getSharedPreferences(context).getInt("user_id", -1)  // Default value -1 if not found
    }

    // Retrieve user role ID
    fun getRoleId(context: Context): Int {
        return getSharedPreferences(context).getInt("role_id", -1)  // Default value -1 if not found
    }

    // Retrieve username
    fun getUsername(context: Context): String? {
        return getSharedPreferences(context).getString("username", null)
    }

    // Retrieve user name
    fun getName(context: Context): String? {
        return getSharedPreferences(context).getString("name", null)
    }

    // Retrieve user email
    fun getEmail(context: Context): String? {
        return getSharedPreferences(context).getString("email", null)
    }

    // Check if user is logged in
    fun isLoggedIn(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean("is_logged_in", false)
    }

    // Optionally, you can add methods to clear the preferences or log the user out
    fun logout(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.clear()  // Clear all preferences
        editor.apply()
    }


    //get data or use
//    val username = UserPreferences.getUsername(requireContext())
//    val roleId = UserPreferences.getRoleId(requireContext())
//    val isLoggedIn = UserPreferences.isLoggedIn(requireContext())
}
