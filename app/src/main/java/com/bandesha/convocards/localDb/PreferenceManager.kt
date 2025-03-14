package com.bandesha.convocards.localDb

import android.content.Context


const val PREFERENCE_NAME = "SharedPreferenceExample"
const val PREFERENCE_LANGUAGE = "Language"
const val INFORMATION = "new"

class PreferenceManager (context : Context)
{

    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLoginCount() : String{
        return preference.getString(PREFERENCE_LANGUAGE,"en").toString()
    }

    fun setLoginCount(Language:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE,Language)
        editor.apply()
    }



    fun getInformation() : String{
        return preference.getString(INFORMATION,"new").toString()
    }
    fun setInformation(Language:String){
        val editor = preference.edit()
        editor.putString(INFORMATION,Language)
        editor.apply()
    }

}