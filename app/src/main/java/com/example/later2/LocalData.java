package com.example.later2;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LocalData {

    protected void onCreate(Bundle savedInstanceState) {

    }

    String getData(String outer, String inner, Context application){
        SharedPreferences settings = application.getSharedPreferences(outer, 0);
        String data = settings.getString(inner, "");
        return (data);
    }

    boolean getDataBool(String outer, String inner, Context application){
        SharedPreferences settings = application.getSharedPreferences(outer, 0);
        boolean data = settings.getBoolean(inner, false);
        return (data);
    }
    void setDataBool(String outer, String inner, Boolean toSet,Context application){
        SharedPreferences settings = application.getSharedPreferences(outer, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(inner, toSet);//main
        editor.apply();
    }

    void setData(String outer, String inner, String toSet,Context application){
        SharedPreferences settings = application.getSharedPreferences(outer, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(inner, toSet);//main
        editor.apply();
    }

}
