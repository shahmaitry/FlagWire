package com.example.flagwire.others

import android.content.Context
import android.content.SharedPreferences
import java.util.HashMap

class SessionManagement {

    internal  var sharedPreferences: SharedPreferences
    internal  var editor: SharedPreferences.Editor
    internal  var context: Context
    internal var PRIVATE_MODE = 0
    private val PREF_NAME = "FlagWire"
    private val IS_LOGIN = "is_logged_in"

    constructor(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    fun Save_UserDetails(userid: String,username:String) {

        editor.putBoolean(IS_LOGIN, true)
        editor.putString(Companion.KEY_ID, userid)
        editor.putString(Companion.KEY_USER_NAME, username)
        editor.commit()

    }



    fun GetUserDetails(): HashMap<String, String> {

        val user = HashMap<String, String>()
        user[Companion.KEY_ID] = sharedPreferences.getString(Companion.KEY_ID, null).toString()
        user[Companion.KEY_USER_NAME] = sharedPreferences.getString(Companion.KEY_USER_NAME, null).toString()
        return user
    }

    fun LogoutUser() {
        editor.clear()
        editor.commit()
    }

    fun isLoggedIn(): Boolean {

        return sharedPreferences.getBoolean(IS_LOGIN, false)
    }

    companion object {
        const  val KEY_ID = "user_id"
        const  val KEY_USER_NAME = "key_USER_NAME"

    }
}