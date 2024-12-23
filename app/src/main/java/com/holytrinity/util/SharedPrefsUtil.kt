package com.holytrinity.util

import android.content.Context
import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.holytrinity.model.Enrollment_Period
import com.holytrinity.model.EnrollmentPeriod as EnrollmentPeriod1

object SharedPrefsUtil {
    private const val PREF_NAME = "enrollment_prefs"
    private const val KEY_SELECTED_PERIOD = "selected_period"

    fun saveSelectedPeriod(context: Context, period: Enrollment_Period) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(period)
        editor.putString(KEY_SELECTED_PERIOD, json)
        editor.apply()
    }

    fun getSelectedPeriod(context: Context): Enrollment_Period? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SELECTED_PERIOD, null) ?: return null
        return Gson().fromJson(json, object : TypeToken<Enrollment_Period>() {}.type)
    }
}