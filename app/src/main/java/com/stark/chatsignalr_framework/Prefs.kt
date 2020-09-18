package com.stark.chatsignalr_framework

import android.content.Context

class Prefs {
    companion object {
        private const val PREF_ID = "chathub_pref"
        fun setSuporte(context: Context, checked: Boolean) {
            val editor = context.getSharedPreferences(PREF_ID, 0).edit()
            editor.putBoolean("Supervisor", checked)
            editor.apply()
        }

        fun getSuporte(context: Context):Boolean{
            return  context.getSharedPreferences(PREF_ID,0).getBoolean("Supervisor", false)
        }
    }
}