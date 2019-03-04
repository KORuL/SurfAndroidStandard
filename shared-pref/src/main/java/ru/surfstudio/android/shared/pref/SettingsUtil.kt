/*
  Copyright (c) 2018-present, SurfStudio LLC.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package ru.surfstudio.android.shared.pref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

/**
 * хелпер для работы с SharedPref
 */
const val NO_BACKUP_SHARED_PREF = "NO_BACKUP_SHARED_PREF"
const val BACKUP_SHARED_PREF = "BACKUP_SHARED_PREF"

object SettingsUtil {
    val EMPTY_STRING_SETTING = ""
    val EMPTY_INT_SETTING = -1
    val EMPTY_LONG_SETTING = -1L

    fun getString(context: Context, key: String): String {
        return getString(getDefaultSharedPreferences(context), key)
    }

    fun putString(context: Context, key: String, value: String) {
        putString(getDefaultSharedPreferences(context), key, value)
    }

    fun putInt(context: Context, key: String, value: Int) {
        putInt(getDefaultSharedPreferences(context), key, value)
    }

    fun putLong(context: Context, key: String, value: Long) {
        putLong(getDefaultSharedPreferences(context), key, value)
    }

    fun getInt(context: Context, key: String): Int {
        return getInt(getDefaultSharedPreferences(context), key)
    }

    fun getLong(context: Context, key: String): Long {
        return getLong(getDefaultSharedPreferences(context), key)
    }

    fun putBoolean(context: Context, key: String, value: Boolean) {
        putBoolean(getDefaultSharedPreferences(context), key, value)
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        return getBoolean(getDefaultSharedPreferences(context), key, defaultValue)
    }

    fun getString(sp: SharedPreferences, key: String): String {
        return sp.getString(key, EMPTY_STRING_SETTING)
    }

    fun getStringSet(sp: SharedPreferences, key: String): Set<String> {
        return sp.getStringSet(key, HashSet())
    }

    fun putStringSet(sp: SharedPreferences, key: String, value: Set<String>) {
        val editor = sp.edit()
        editor.putStringSet(key, value)
        saveChanges(editor)
    }

    fun putString(sp: SharedPreferences, key: String, value: String) {
        val editor = sp.edit()
        editor.putString(key, value)
        saveChanges(editor)
    }

    fun putInt(sp: SharedPreferences, key: String, value: Int) {
        val editor = sp.edit()
        editor.putInt(key, value)
        saveChanges(editor)
    }

    fun putLong(sp: SharedPreferences, key: String, value: Long) {
        val editor = sp.edit()
        editor.putLong(key, value)
        saveChanges(editor)
    }

    fun getInt(sp: SharedPreferences, key: String): Int {
        return sp.getInt(key, EMPTY_INT_SETTING)
    }

    fun getLong(sp: SharedPreferences, key: String): Long {
        return sp.getLong(key, EMPTY_LONG_SETTING)
    }

    fun getLong(sp: SharedPreferences, key: String, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    fun putBoolean(sp: SharedPreferences, key: String, value: Boolean) {
        val editor = sp.edit()
        editor.putBoolean(key, value)
        saveChanges(editor)
    }

    fun getBoolean(sp: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    private fun saveChanges(editor: SharedPreferences.Editor) {
        editor.apply()
    }

    private fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
