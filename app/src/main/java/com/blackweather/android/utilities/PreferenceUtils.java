package com.blackweather.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;

import com.blackweather.android.BlackApplication;
import com.blackweather.android.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public final class PreferenceUtils {

//    private static SharedPreferences prefs;
//    private static SharedPreferences.Editor editor;
//
//    /**
//     * 静态内部类单例模式
//     */
//    private static class SingletonHolder {
//        private static final SharedPreferenceUtils sInstance = new SharedPreferenceUtils();
//    }
//
//    public static SharedPreferenceUtils getInstance() {
//        return SingletonHolder.sInstance;
//    }

//    private SharedPreferenceUtils() {
//        prefs = PreferenceManager.getDefaultSharedPreferences(BlackApplication.getContext());
//        editor = prefs.edit();
//    }

    public static final String PAGES_WEATHER_ID_KEY = "pages_weather_id";

    private PreferenceUtils() {
    }

    public static void removeKeyValuePair(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key).apply();
    }

    /**
     * 保存list
     */
    public static <E> void savedPagesWeatherId(Context context, String key, List<E> datas) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        if (datas == null || datas.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(datas);
        editor.remove(key);
        editor.putString(key, jsonStr);
        editor.apply();
    }

    /**
     * 读取list
     */
    public static <E> List<E> fetchPagesWeatherId(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        List<E> datas = new ArrayList<>();
        String jsonStr = sp.getString(key, null);
        if (jsonStr == null) {
            return datas;
        }
        Gson gson = new Gson();
        datas = gson.fromJson(jsonStr, new TypeToken<List<E>>() {
        }.getType());
        return datas;
    }

    /**
     * 存放实体类以及任意类型
     */
    public static void putObj(Context context, String key, Object obj) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }

    public static Object getObj(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Object obj = null;
        try {
            String base64 = sp.getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static boolean isMetric(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String str = sp.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric));
        return (str.equals(context.getString(R.string.pref_units_metric)));
    }

    public static boolean isAllowedAutpUpdate(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bool = sp.getBoolean(context.getString(R.string.pref_auto_update_key),
                true);
        return bool;
    }

}
